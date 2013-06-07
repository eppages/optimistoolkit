/*
 *  Copyright 2011-2012 Barcelona Supercomputing Center (www.bsc.es)
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package es.bsc.servicess.ide.wizards;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jdt.core.Flags;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.Signature;
import org.eclipse.jdt.internal.core.JavaModelStatus;
import org.eclipse.jdt.internal.core.search.matching.SecondaryTypeDeclarationPattern;
import org.eclipse.jdt.internal.corext.codemanipulation.StubUtility;
import org.eclipse.jdt.internal.ui.dialogs.StatusInfo;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ExpandEvent;
import org.eclipse.swt.events.ExpandListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.ExpandBar;
import org.eclipse.swt.widgets.ExpandItem;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.xml.sax.SAXException;

import es.bsc.servicess.ide.Checker;
import es.bsc.servicess.ide.ProjectMetadata;
import es.bsc.servicess.ide.TitlesAndConstants;
import es.bsc.servicess.ide.dialogs.AddServiceLocationDialog;
import es.bsc.servicess.ide.dialogs.ModifyCoreElementParameterDialog;
import es.bsc.servicess.ide.dialogs.ModifyParameterDialog;
import es.bsc.servicess.ide.model.CoreElementParameter;
import es.bsc.servicess.ide.model.MethodCoreElement;
import es.bsc.servicess.ide.model.OrchestrationElement;
import es.bsc.servicess.ide.model.Parameter;
import es.bsc.servicess.ide.model.ServiceCoreElement;
import es.bsc.servicess.ide.model.ServiceElement;
import es.bsc.servicess.ide.wizards.coretypes.SpecificMethodComposite;
import es.bsc.servicess.ide.wizards.coretypes.SpecificNewMethodComposite;
import es.bsc.servicess.ide.wizards.coretypes.SpecificServiceComposite;

public class ServiceSsCoreElementWizardPage extends
		ServiceSsNewElementWizardPage {

	private SpecificComposite specComposite;
	
	private boolean pageCreated;
	private StatusInfo fCEStatus;
	private int type = -1;
	private ExpandItem specificItem;
	private Composite currentComposite;
	private ExpandBar ceBar;
	

	public ServiceSsCoreElementWizardPage() {
		super("newCE", " New Core Element",
				"Creates a new core element");
		super.setClassLabel("CE Interface", false);
		fCEStatus = new StatusInfo();
		doStatusUpdate();
		pageCreated = false;
	}

	public Text getNameText() {
		return super.oeNameText;
	}
	
	public Text getReturnTypeText(){
		return super.oeReturnTypeText;
	}

	public StatusInfo getCEStatus() {
		return fCEStatus;
	}

	public void setType(int type) {
		System.out.println("Updating CE creating page type to " + type);
		this.type = type;
		if (type == ServiceSsCoreElementSecondPage.METHOD_NEW){
			specComposite = new SpecificNewMethodComposite(this, getShell());
		}else if (type == ServiceSsCoreElementSecondPage.METHOD_EXISTS){
			specComposite = new SpecificMethodComposite(this, getShell());
		}else if (type == ServiceSsCoreElementSecondPage.METHOD_BIN){
			specComposite = new SpecificMethodComposite(this, getShell());
		}else if (type == ServiceSsCoreElementSecondPage.SERVICE_WSDL){
			specComposite = new SpecificServiceComposite(this, getShell());
		}else if (type == ServiceSsCoreElementSecondPage.SERVICE_WAR){
			specComposite = new SpecificServiceComposite(this, getShell());
		}
		if (pageCreated && type >= 0) {
			updateComposite();
			addSpecificListeners();
		}
	}

	public void doStatusUpdate() {
		// status of all used components
		IStatus[] status = new IStatus[] { fContainerStatus, fPackageStatus,
				fClassStatus, fCEStatus };

		// the mode severe status will be displayed and the OK button
		// enabled/disabled.
		updateStatus(status);
	}

	private void updateComposite(){
		if (currentComposite!= null){
			currentComposite.dispose();
		}
		currentComposite = specComposite.createComposite(ceBar);
		currentComposite.layout(true);
		specificItem.setHeight(currentComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT).y);
		specificItem.setControl(currentComposite);
		specificItem.setExpanded(true);
	}
	
	@Override
	protected void createElementTypeSpecificControls(Group group) {
		if (specComposite != null){
			System.out.println("Creating specific control in CE creation page ");
			ceBar = new ExpandBar(group, SWT.V_SCROLL);
			ceBar.setEnabled(true);
			GridData rd = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
			rd.grabExcessHorizontalSpace = true;
			rd.horizontalSpan = 2;
			ceBar.setLayoutData(rd);	
			specificItem = new ExpandItem(ceBar, SWT.NONE, 0);
			specificItem.setText("Specific Core Element Description");
			updateComposite();
		}
		pageCreated = true;
	}

	@Override
	public ServiceElement generateElement() {
		ServiceElement el;
		int modifier = Flags.AccPublic;
		/*
		 * if (oeStatic.getSelection()) modifier = modifier | Flags.AccStatic;
		 * if (oeFinal.getSelection()) modifier = modifier | Flags.AccFinal;
		 */
		el = specComposite.generateElement(oeNameText.getText().trim(), modifier,
					oeReturnTypeText.getText().trim());
		TableItem[] items = oeParametersTable.getItems();
		ArrayList<Parameter> params = new ArrayList<Parameter>();
		for (TableItem it : items) {
			CoreElementParameter p = new CoreElementParameter(it.getText(0),
					it.getText(1), it.getText(2));
			params.add(p);
		}
		System.out.println("Setting parameters "+ params);
		el.setParameters(params);
		items = oeConstraintsTable.getItems();
		HashMap<String, String> cons = new HashMap<String, String>();
		for (TableItem it : items) {
			cons.put(it.getText(0), it.getText(1));
		}
		el.setConstraints(cons);
		specComposite.addSpecificParams(el);
		return el;
	}

	public boolean isElementCompleted() {
		if (oeReturnTypeText.getText().trim().length() > 0
				&& oeNameText.getText().trim().length() > 0) {
			return specComposite.isCompositeCompleated();
		}
		return false;
	}
	

	/*
	 * public void updateModifiers(){ if (element != null){ int modifier =
	 * Flags.AccPublic; if (oeStatic.getSelection()) modifier = modifier |
	 * Flags.AccStatic; if (oeFinal.getSelection()) modifier = modifier |
	 * Flags.AccFinal; element.setMethodModifier(modifier); } }
	 */

	@Override
	protected void configureSpecificParameterTableColumns() {
		TableColumn oeParamType = new TableColumn(oeParametersTable, SWT.FILL);
		oeParamType.setText("Type");
		oeParamType.setAlignment(SWT.FILL);
		oeParamType.pack();
		TableColumn oeParamName = new TableColumn(oeParametersTable, SWT.FILL);
		oeParamName.setText("Name");
		oeParamName.setAlignment(SWT.FILL);
		oeParamName.pack();
		TableColumn oeParamDirec = new TableColumn(oeParametersTable, SWT.FILL);
		oeParamDirec.setText("Direction");
		oeParamDirec.setAlignment(SWT.FILL);
		oeParamDirec.pack();

	}

	/*
	 * @Override protected void createSpecificModifiers(Composite oeModifiers) {
	 * oeStatic = new Button(oeModifiers, SWT.CHECK);
	 * oeStatic.setText("static"); GridData rd = new
	 * GridData(GridData.HORIZONTAL_ALIGN_FILL); rd.grabExcessHorizontalSpace =
	 * true; oeStatic.setLayoutData(rd); oeFinal = new Button(oeModifiers,
	 * SWT.CHECK); oeFinal.setText("final"); rd = new
	 * GridData(GridData.HORIZONTAL_ALIGN_FILL); rd.grabExcessHorizontalSpace =
	 * true; oeFinal.setLayoutData(rd);
	 * 
	 * }
	 */

	@Override
	protected void addSpecificListeners() {
		if (specComposite != null)
			specComposite.addListeners();
	}
	
	//TODO Modify specModify
	@Override
	protected void modifyParamTableItem(int selection) {
		if (type == ServiceSsCoreElementSecondPage.SERVICE_WSDL) {
			modifyNormalParamTableItem(selection);
		} else
			modifyCoreElementParamTableItem(selection);

	}
	//TODO Modify DRY unify this and next method
	private void modifyNormalParamTableItem(int selection) {
		Parameter p = null;
		if (selection >= 0) {
			p = new Parameter(oeParametersTable.getItem(selection).getText(0)
					.trim(), oeParametersTable.getItem(selection).getText(1)
					.trim());
		}
		ModifyParameterDialog dialog = new ModifyParameterDialog(
				getContainer(), getShell(), p, getJavaProject(), true);
		if (dialog.open() == Window.OK) {
			p = dialog.getParameter();
			TableItem it;
			if (selection < 0) {
				it = new TableItem(oeParametersTable, SWT.NONE);
				if (element != null) {

					element.getParameters().add(
							new CoreElementParameter(p.getType(), p.getName(),
									"IN"));
				}
			} else {
				it = oeParametersTable.getItem(selection);
				if (element != null) {
					element.getParameters().set(
							selection,
							new CoreElementParameter(p.getType(), p.getName(),
									"IN"));
				}
			}

			it.setText(new String[] { p.getType(), p.getName(), "IN" });

		}
	}

	private void modifyCoreElementParamTableItem(int selection) {
		CoreElementParameter p = null;
		if (selection >= 0) {
			p = new CoreElementParameter(oeParametersTable.getItem(selection)
					.getText(0).trim(), oeParametersTable.getItem(selection)
					.getText(1).trim(), oeParametersTable.getItem(selection)
					.getText(2).trim());
		}
		ModifyCoreElementParameterDialog dialog = new ModifyCoreElementParameterDialog(
				getContainer(), getShell(), p, getJavaProject(), true);
		if (dialog.open() == Window.OK) {
			p = (CoreElementParameter) dialog.getParameter();
			TableItem it;
			if (selection < 0) {
				it = new TableItem(oeParametersTable, SWT.NONE);
				if (element != null) {
					element.getParameters().add(p);
				}
			} else {
				it = oeParametersTable.getItem(selection);
				if (element != null) {
					element.getParameters().set(selection, p);
				}
			}
			it.setText(new String[] { p.getType(), p.getName(),
					p.getDirection() });
		}
	}

	@Override
	protected void printTypeSpecific() {
		specComposite.printElement(element);

	}

	@Override
	protected void printParameters() {
		System.out.println("Removing Elements in parameters table");
		oeParametersTable.removeAll();
		System.out.println("Parameters in table: "
				+ oeParametersTable.getItemCount());
		if (element != null) {
				for (Parameter p : element.getParameters()) {
					TableItem it = new TableItem(oeParametersTable, SWT.NONE);
					it.setText(new String[] { p.getType(), p.getName(),specComposite.getParameterDirection(p) });
				}
		}
	}

	/*
	 * @Override protected void printModifiers() {
	 * oeStatic.setSelection(Flags.isStatic(element.getMethodModifier()));
	 * oeFinal.setSelection(Flags.isFinal(element.getMethodModifier())); }
	 */

	public int getType() {
		return this.type;
	}
	
	public boolean isPageCreated(){
		return this.pageCreated;
	}

	@Override
	protected IStatus validateCorrectElementClass(String interfaceName,
			IProject project) {
		return Checker.validateCoreElementInterface(interfaceName, project);
	}

	public void performFinish(IProgressMonitor monitor) throws JavaModelException {
		specComposite.performFinish(monitor);
	}

	public SpecificComposite getSpecificComposite() {
		return specComposite;
	}

	public void performCancel() {
		specComposite.performCancel();
	}

	public void updateMethodParemeters(java.util.List<String> params) {
		System.out.println("Updating parameters " + params);
		if (element!=null){
			System.out.println("Element exists");
			for (String param:params){
				System.out.println("updating param "+ param);
				if (param.equals(TitlesAndConstants.RETURNTYPE)){
					if (oeReturnTypeText.getText().trim().length()<=0)
						oeReturnTypeText.setText(TitlesAndConstants.JAVA_LANG+"."+TitlesAndConstants.STRING);
				}else{
					boolean found = false;
					for (Parameter p:element.getParameters()){
						System.out.println("Comparing: "+ p.getName() +" with "+ param);
						if (p.getName().equals(param)){
							System.out.println(param + " found");
							found = true;
							break;
						}
					}
					if (!found)
						element.getParameters().add(new CoreElementParameter(TitlesAndConstants.JAVA_LANG+"."+TitlesAndConstants.STRING, param, "IN"));
				}
			}
			printParameters();
		}else{
			System.out.println("Element doesn't exists");
			for (String param:params){
				System.out.println("updating param "+ param);
				TableItem[] items = oeParametersTable.getItems();
				if (param.equals(TitlesAndConstants.RETURNTYPE)){
					if (oeReturnTypeText.getText().trim().length()<=0)
						oeReturnTypeText.setText(TitlesAndConstants.JAVA_LANG+"."+TitlesAndConstants.STRING);
				}else{
					boolean found = false;
					for (TableItem it:items){
						System.out.println("Comparing: "+ it.getText(1)+" with "+ param);
						if (it.getText(1).equals(param)){
							System.out.println(param + " found");
							found = true;
							break;
						}
					}
					if (!found){
						TableItem it = new TableItem(oeParametersTable, SWT.NONE);
						it.setText(new String[]{TitlesAndConstants.JAVA_LANG+"."+TitlesAndConstants.STRING, param, "IN"});
					}
				}
			}
		}
	}

}
