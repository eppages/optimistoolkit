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

package es.bsc.servicess.ide.wizards.coretypes;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.Signature;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.internal.core.JavaModelStatus;
import org.eclipse.jdt.internal.corext.codemanipulation.StubUtility;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.Section;

import es.bsc.servicess.ide.Checker;
import es.bsc.servicess.ide.ConstraintsUtils;
import es.bsc.servicess.ide.KeyValueTableComposite;
import es.bsc.servicess.ide.Logger;
import es.bsc.servicess.ide.ProjectMetadata;
import es.bsc.servicess.ide.TitlesAndConstants;
import es.bsc.servicess.ide.dialogs.ModifyArgumentDialog;
import es.bsc.servicess.ide.dialogs.ModifyConstraintsDialog;
import es.bsc.servicess.ide.model.MethodCoreElement;
import es.bsc.servicess.ide.model.ServiceElement;
import es.bsc.servicess.ide.wizards.ServiceSsCoreElementSecondPage;
import es.bsc.servicess.ide.wizards.ServiceSsCoreElementWizardPage;
import es.bsc.servicess.ide.wizards.TypeSpecificTreatment;

/**Class for implementing the Specific treatment for generating method core elements from a binary
 * @author Jorge Ejarque (Barcelona Supercomputing Center)
 *
 */
public class BinaryMethodSpecificTreatment extends TypeSpecificTreatment {

	private Text declaringClassText;
	private String declaringClass = "";
	//private Button classServiceButton;
	private Text methodNameText;
	private String methodName = "";
	private Table argsTable;
	private List<String> args = new ArrayList<String>();
	private Button ceAddParButton;
	private Button ceModifyParButton;
	private Button ceDeleteParButton;
	private EnvVariablesTableComposite envVariablesTableComp;
	private Text execText;
	private String exec = "";
	private Text stdinText;
	private String stdin = "";
	private Text stdoutText;
	private String stdout = "";
	private Text stderrText;
	private String stderr = "";
	
	private static Logger log = Logger.getLogger(BinaryMethodSpecificTreatment.class);

	/** Constructor
	 * @param secondPage Second page of the core element creation wizard
	 * @param shell Parent's shell
	 */
	public BinaryMethodSpecificTreatment(
			ServiceSsCoreElementSecondPage secondPage, Shell shell) {
		super(secondPage, shell);
	}

	@Override
	public void updateControlsListeners() {
		declaringClassText.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent arg0) {
				if (Checker.validateClassName(declaringClassText.getText().trim()).isOK()){
					declaringClass = declaringClassText.getText().trim();
					updateInfo();
				}else{
					secondPage.getCEStatus().setError("Incorrect class Name");
					secondPage.doStatusUpdate();
				}
			}
		});
		methodNameText.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent arg0) {
				if (Checker.validateMethodName(methodNameText.getText().trim()).isOK()){
					methodName = methodNameText.getText().trim();
					updateInfo();
				}else{
					secondPage.getCEStatus().setError("Incorrect method Name");
					secondPage.doStatusUpdate();
				}
			}
		});
		execText.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent arg0) {
				if (BinaryWrapper.checkString(execText.getText().trim())){
					exec = execText.getText().trim();
					updateInfo();
				}else{
					secondPage.getCEStatus().setError("Incorrect text for executable (check if variable definition follow $var_name$ format");
					secondPage.doStatusUpdate();
				}
				
			}
		});
		stdinText.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent arg0) {
				if (BinaryWrapper.checkString(stdinText.getText().trim())){
					stdin = stdinText.getText().trim();
					updateInfo();
				}else{
					secondPage.getCEStatus().setError("Incorrect text for standard input (check if variable definition follow $var_name$ format");
					secondPage.doStatusUpdate();
				}
			}
		});
		stdoutText.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent arg0) {
				if (BinaryWrapper.checkOutputs(stdoutText.getText().trim())){
					stdout = stdoutText.getText().trim();
					updateInfo();
				}else{
					secondPage.getCEStatus().setError("Incorrect text for standard output. Only one variable definition is allow ($var_name$ format");
					secondPage.doStatusUpdate();
				}
			}
		});
		
		stderrText.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent arg0) {
				if (BinaryWrapper.checkOutputs(stderrText.getText().trim())){
					stderr = stderrText.getText().trim();
					updateInfo();
				}else{
					secondPage.getCEStatus().setError("Incorrect text for standard error. Only one variable definition is allow ($var_name$ format");
					secondPage.doStatusUpdate();
				}
			}
		});
		
		argsTable.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {
				if (argsTable.getSelectionIndex() < 0) {
					ceDeleteParButton.setEnabled(false);
					ceModifyParButton.setEnabled(false);
				} else {
					ceDeleteParButton.setEnabled(true);
					ceModifyParButton.setEnabled(true);
				}
			}

			@Override
			public void widgetSelected(SelectionEvent arg0) {
				if (argsTable.getSelectionIndex() < 0) {
					ceDeleteParButton.setEnabled(false);
					ceModifyParButton.setEnabled(false);
				} else {
					ceDeleteParButton.setEnabled(true);
					ceModifyParButton.setEnabled(true);
				}

			}
		});
		ceAddParButton.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {
				modifyArgsTableItem(-1);
			}


			@Override
			public void widgetSelected(SelectionEvent arg0) {
				modifyArgsTableItem(-1);
			}
		});
		ceModifyParButton.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {
				modifyArgsTableItem(argsTable.getSelectionIndex());
			}

			@Override
			public void widgetSelected(SelectionEvent arg0) {
				modifyArgsTableItem(argsTable.getSelectionIndex());
			}
		});
		ceDeleteParButton.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {
				removeArgument(argsTable.getSelectionIndex());
			}

			@Override
			public void widgetSelected(SelectionEvent arg0) {
				removeArgument(argsTable.getSelectionIndex());
			}
		});
		
		
	}
	
	/**
	 * Modify the binaries argument table 
	 * @param selection Table row index for the selected argument
	 */
	protected void modifyArgsTableItem(int selection) {
		String p = null;
		if (selection >= 0) {
			p =	argsTable.getItem(selection).getText();
		}
		ModifyArgumentDialog dialog = new ModifyArgumentDialog(	shell, p);

		if (dialog.open() == Window.OK) {
			p = dialog.getArgument();
			if (p != null) {
				TableItem it;
				if (BinaryWrapper.checkString(p)){
					if (selection < 0) {
						it = new TableItem(argsTable, SWT.NONE);
						it.setText(p);
						args.add(p);
					} else {
						it = argsTable.getItem(selection);
						it.setText(p);
						args.set(selection, p);
						
					}
					updateInfo();
				}else{
					secondPage.getCEStatus().setError("Incorrect text for argument (check if variable definition follow $var_name$ format");
					secondPage.doStatusUpdate();	
				}	
			}
		}
	}

	/**
	 * Remove an argument
	 * @param selectionIndex
	 */
	protected void removeArgument(int selectionIndex) {
		//TODO remove old parameters
		argsTable.remove(selectionIndex);
		args.remove(selectionIndex);

	}

	/**
	 * Updade the core element info
	 */
	protected void updateInfo(){
			if (isPageComplete()) {
				log.debug("Updating Element info");
				IWizardPage p = secondPage.getNextPage();
				List<String> params = new ArrayList<String>();
				String decClass;
				try{
					decClass = getDeclaringClassFQDN(declaringClassText.getText().trim());
				}catch (Exception e){
					log.error(e.getMessage());
					e.printStackTrace();
					decClass = declaringClassText.getText().trim();
				}
				((SpecificMethodComposite)((ServiceSsCoreElementWizardPage) p).getSpecificComposite()).updateCoreElementInfo(
						decClass, methodNameText.getText().trim());
				if (execText.getText().trim().length() >0){
					params = BinaryWrapper.getParameters(execText.getText().trim(), params);
				}
				if (stdinText.getText().trim().length() >0){
					params = BinaryWrapper.getParameters(stdinText.getText().trim(), params);
				}
				if (stdoutText.getText().trim().length() >0){
					params = BinaryWrapper.getParameters(stdoutText.getText().trim(), params);
				}
				if (stderrText.getText().trim().length() >0){
					params = BinaryWrapper.getParameters(stderrText.getText().trim(), params);

				}
				for(TableItem it:argsTable.getItems()){
					String arg = it.getText().trim();
					params = BinaryWrapper.getParameters(arg, params);
				}
				for(Entry<String, String> e:envVariablesTableComp.getKeyValueMap().entrySet()){
					String arg = e.getKey();
					params = BinaryWrapper.getParameters(arg, params);
					arg = e.getValue();
					params = BinaryWrapper.getParameters(arg, params);
					
				}
				log.debug("Parameters to update: " + params);
				if (params!=null && params.size()>0)
					((ServiceSsCoreElementWizardPage) p).updateMethodParemeters(params);
				secondPage.getCEStatus().setOK();
				secondPage.doStatusUpdate();

			} else {
				secondPage.getCEStatus()
				.setError("There are missing parameters to complete the Element information");
				secondPage.doStatusUpdate();
			}
	}
	

	@Override
	public Composite updateSecondPageGroupControls(Group group, Composite cp) {
				if (cp != null)
					cp.dispose();
				Composite comp = new Composite(group, SWT.NONE);
				GridData rd = new GridData(GridData.FILL_HORIZONTAL
						| GridData.VERTICAL_ALIGN_BEGINNING);
				// rd.grabExcessHorizontalSpace = true;
				comp.setLayoutData(rd);
				comp.setLayout(new GridLayout(1, false));
				
				createBasicPart(comp);
				createArgumentsPart(comp);
				createStandardStreamsPart(comp);
				createEnvVariablesPart(comp);
				return comp;

	}
	
	/** Create the binary core element definition basic part in a composite
	 * @param cp Parent composite
	 */
	private void createBasicPart(Composite cp) {

		Composite comp = new Composite(cp, SWT.NONE);
		GridData rd = new GridData(GridData.FILL_HORIZONTAL
				| GridData.VERTICAL_ALIGN_BEGINNING);
		// rd.grabExcessHorizontalSpace = true;
		comp.setLayoutData(rd);
		comp.setLayout(new GridLayout(2, false));
		//Class Name
		Label serviceClassLabel = new Label(comp, SWT.NONE);
		serviceClassLabel.setText("Declaring Class");
		Composite dc = new Composite(comp, SWT.NONE);
		rd = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		rd.grabExcessHorizontalSpace = true;
		rd.horizontalIndent = 0;
		rd.horizontalSpan = 0;
		dc.setLayoutData(rd);
		GridLayout oeReturnLayout = new GridLayout();
		oeReturnLayout.numColumns = 1;
		oeReturnLayout.marginLeft = 0;
		oeReturnLayout.marginRight = 0;
		dc.setLayout(oeReturnLayout);
		declaringClassText = new Text(dc, SWT.SINGLE | SWT.BORDER);
		rd = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		rd.grabExcessHorizontalSpace = true;
		declaringClassText.setLayoutData(rd);
		//classServiceButton = new Button(dc, SWT.NONE);
		//classServiceButton.setText("Select...");

		Label methodLabel = new Label(comp, SWT.NONE);
		methodLabel.setText("Method");
		Composite method = new Composite(comp, SWT.NONE);
		rd = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		rd.grabExcessHorizontalSpace = true;
		rd.horizontalIndent = 0;
		rd.horizontalSpan = 0;
		method.setLayoutData(rd);
		oeReturnLayout = new GridLayout();
		oeReturnLayout.numColumns = 1;
		oeReturnLayout.marginLeft = 0;
		oeReturnLayout.marginRight = 0;
		method.setLayout(oeReturnLayout);
		methodNameText = new Text(method, SWT.SINGLE | SWT.BORDER);
		rd = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		rd.grabExcessHorizontalSpace = true;
		methodNameText.setLayoutData(rd);

		createExecutablePart(comp);
	}

	/** Create the Environment variables part of the core element composita
	 * @param cp Composite
	 */
	private void createEnvVariablesPart(Composite cp) {
		Section section = new Section(cp,
				Section.TWISTIE | SWT.BORDER);
		section.setText("Environment Variables");
		GridData rd = new GridData(GridData.HORIZONTAL_ALIGN_FILL
				| GridData.VERTICAL_ALIGN_BEGINNING);
		rd.grabExcessHorizontalSpace = true;
		section.setLayoutData(rd);
		section.setLayout(new GridLayout(1, true));
		Composite comp = new Composite(section, SWT.NONE);
		rd = new GridData(GridData.FILL_HORIZONTAL
				| GridData.VERTICAL_ALIGN_BEGINNING);
		rd.grabExcessHorizontalSpace = true;
		comp.setLayout(new GridLayout(2, false));
		comp.setLayoutData(rd);
		Label envsLabel = new Label(comp, SWT.NONE);
		envsLabel.setText("Env. Variables");
		envVariablesTableComp = new EnvVariablesTableComposite(shell, null, "Variable Name", "Value", true, true, this);
		envVariablesTableComp.createComposite(comp);
		section.setClient(comp);
		section.setExpanded(true);
		section.setExpanded(false);
	}

	/**
	 * Create the Arguments type of the binary method core element specification
	 * @param cp Composite
	 */
	private void createArgumentsPart(Composite cp) {
		Section section = new Section(cp,
				Section.TWISTIE | SWT.BORDER);
		section.setText("Executable Arguments");
		GridData rd = new GridData(GridData.HORIZONTAL_ALIGN_FILL
				| GridData.VERTICAL_ALIGN_BEGINNING);
		rd.grabExcessHorizontalSpace = true;
		section.setLayoutData(rd);
		section.setLayout(new GridLayout(1, true));
		Composite comp = new Composite(section, SWT.BORDER);
		rd = new GridData(GridData.FILL_HORIZONTAL
				| GridData.VERTICAL_ALIGN_BEGINNING);
		rd.grabExcessHorizontalSpace = true;
		comp.setLayout(new GridLayout(2, false));
		comp.setLayoutData(rd);
		
		Label argsLabel = new Label(comp, SWT.NONE);
		argsLabel.setText("Arguments");
		Composite argsComp = new Composite(comp, SWT.NONE);
		rd = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		rd.grabExcessHorizontalSpace = true;
		rd.horizontalIndent = 0;
		argsComp.setLayoutData(rd);
		GridLayout tableLayout = new GridLayout();
		tableLayout.numColumns = 2;
		tableLayout.marginLeft = 0;
		tableLayout.marginRight = 0;
		tableLayout.marginWidth = 0;
		argsComp.setLayout(tableLayout);
		argsTable = new Table(argsComp, SWT.SINGLE | SWT.BORDER
				| SWT.FULL_SELECTION | SWT.FILL | SWT.V_SCROLL | SWT.H_SCROLL);
		argsTable.setHeaderVisible(true);
		argsTable.setLinesVisible(true);
		
		TableColumn ceParamValue = new TableColumn(argsTable, SWT.FILL);
		ceParamValue.setText("Text");
		ceParamValue.setAlignment(SWT.FILL);
		ceParamValue.pack();
		
		rd = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		rd.grabExcessHorizontalSpace = true;
		rd.grabExcessVerticalSpace = true;
		rd.minimumHeight = 90;
		argsTable.setLayoutData(rd);
		Composite parButtons = new Composite(argsComp, SWT.NONE);
		tableLayout = new GridLayout();
		tableLayout.numColumns = 1;
		tableLayout.marginLeft = 0;
		tableLayout.marginRight = 0;
		parButtons.setLayout(tableLayout);
		ceAddParButton = new Button(parButtons,	SWT.NONE);
		ceAddParButton.setText("Add...");
		rd = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		rd.grabExcessHorizontalSpace = true;
		ceAddParButton.setLayoutData(rd);
		ceModifyParButton = new Button(parButtons,	SWT.NONE);
		ceModifyParButton.setText("Modify...");
		ceModifyParButton.setEnabled(false);
		rd = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		rd.grabExcessHorizontalSpace = true;
		ceModifyParButton.setLayoutData(rd);
		ceDeleteParButton = new Button(parButtons , SWT.NONE); 
		ceDeleteParButton.setText("Delete");
		ceDeleteParButton.setEnabled(false); 
		rd = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		rd.grabExcessHorizontalSpace = true;
		ceDeleteParButton.setLayoutData(rd);
		
		section.setClient(comp);
		section.setExpanded(true);
		section.setExpanded(false);
		
	}

	/**
	 * Create the part for defining an executable in a composite
	 * @param comp composite
	 */
	private void createExecutablePart(Composite comp){
		Label execLabel = new Label(comp, SWT.NONE);
		execLabel.setText("Executable");
		Composite execComp = new Composite(comp, SWT.NONE);
		GridData rd = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		rd.grabExcessHorizontalSpace = true;
		rd.horizontalIndent = 0;
		rd.horizontalSpan = 0;
		execComp.setLayoutData(rd);
		GridLayout ceReturnLayout = new GridLayout();
		ceReturnLayout.numColumns = 1;
		ceReturnLayout.marginLeft = 0;
		ceReturnLayout.marginRight = 0;
		execComp.setLayout(ceReturnLayout);
		execText = new Text(execComp, SWT.SINGLE | SWT.BORDER);
		rd = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		rd.grabExcessHorizontalSpace = true;
		execText.setLayoutData(rd);
	}
	
	/** Create the part to specify the standard stream specification of the binary in the method core element composite 
	 * @param cp Composite
	 */
	private void createStandardStreamsPart(Composite cp){
		Section section = new Section(cp,
				Section.TWISTIE | SWT.BORDER);
		section.setText("Standard Streams");
		GridData rd = new GridData(GridData.HORIZONTAL_ALIGN_FILL
				| GridData.VERTICAL_ALIGN_BEGINNING);
		rd.grabExcessHorizontalSpace = true;
		section.setLayoutData(rd);
		section.setLayout(new GridLayout(1, true));
		Composite comp = new Composite(section, SWT.BORDER);
		rd = new GridData(GridData.FILL_HORIZONTAL
				| GridData.VERTICAL_ALIGN_BEGINNING);
		rd.grabExcessHorizontalSpace = true;
		comp.setLayout(new GridLayout(2, false));
		comp.setLayoutData(rd);
		createStdInPart(comp);
		createStdOutPart(comp);
		createStdErrPart(comp);
		section.setClient(comp);
		section.setExpanded(true);
		section.setExpanded(false);
	}
	
	/**
	 * Create the standard input stream part
	 * @param comp
	 */
	private void createStdInPart(Composite comp){
		Label stdinLabel = new Label(comp, SWT.NONE);
		stdinLabel.setText("Standard Input");
		Composite stdinComp = new Composite(comp, SWT.NONE);
		GridData rd = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		rd.grabExcessHorizontalSpace = true;
		rd.horizontalIndent = 0;
		rd.horizontalSpan = 0;
		stdinComp.setLayoutData(rd);
		GridLayout ceReturnLayout = new GridLayout();
		ceReturnLayout.numColumns = 1;
		ceReturnLayout.marginLeft = 0;
		ceReturnLayout.marginRight = 0;
		stdinComp.setLayout(ceReturnLayout);
		stdinText = new Text(stdinComp, SWT.SINGLE | SWT.BORDER);
		rd = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		rd.grabExcessHorizontalSpace = true;
		stdinText.setLayoutData(rd);
		
	}
	
	/**
	 * Create the standard output stream part
	 * @param comp Composite
	 */
	private void createStdOutPart(Composite comp){
		Label stdoutLabel = new Label(comp, SWT.NONE);
		stdoutLabel.setText("Standard Output");
		Composite stdoutComp = new Composite(comp, SWT.NONE);
		GridData rd = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		rd.grabExcessHorizontalSpace = true;
		rd.horizontalIndent = 0;
		rd.horizontalSpan = 0;
		stdoutComp.setLayoutData(rd);
		GridLayout ceReturnLayout = new GridLayout();
		ceReturnLayout.numColumns = 1;
		ceReturnLayout.marginLeft = 0;
		ceReturnLayout.marginRight = 0;
		stdoutComp.setLayout(ceReturnLayout);
		stdoutText = new Text(stdoutComp, SWT.SINGLE | SWT.BORDER);
		rd = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		rd.grabExcessHorizontalSpace = true;
		stdoutText.setLayoutData(rd);
		
	}
	
	private void createStdErrPart(Composite comp){
		Label stderrLabel = new Label(comp, SWT.NONE);
		stderrLabel.setText("Standard Error");
		Composite stderrComp = new Composite(comp, SWT.NONE);
		GridData rd = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		rd.grabExcessHorizontalSpace = true;
		rd.horizontalIndent = 0;
		rd.horizontalSpan = 0;
		stderrComp.setLayoutData(rd);
		GridLayout ceReturnLayout = new GridLayout();
		ceReturnLayout.numColumns = 1;
		ceReturnLayout.marginLeft = 0;
		ceReturnLayout.marginRight = 0;
		stderrComp.setLayout(ceReturnLayout);
		stderrText = new Text(stderrComp, SWT.SINGLE | SWT.BORDER);
		rd = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		rd.grabExcessHorizontalSpace = true;
		stderrText.setLayoutData(rd);
	}
	
	
	@Override
	public ServiceElement generateCoreElement() throws JavaModelException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isPageComplete() {
		return declaringClassText.getText().trim().length()>0 &&
				methodNameText.getText().trim().length()>0 &&
				execText.getText().trim().length()>0;
	}

	@Override
	public void performCancel() {
		// TODO Auto-generated method stub
		
	}

	public void performFinish(IProgressMonitor monitor) throws JavaModelException {
			IType newClass = createDeclaringClassType(monitor);
			MethodCoreElement ce = (MethodCoreElement)((ServiceSsCoreElementWizardPage)secondPage.getNextPage()).getElement();
			if (ce != null)	
				ce.generateSimpleMethodInType(newClass, false, generateMethodBody(ce), monitor);
			else
				throw (new JavaModelException(new Exception("Error: Core element is null "),JavaModelStatus.ERROR));
		
	}
	
	/** Generate the code of the core element
	 * @param ce Method Core element description
	 * @return code of the method core element
	 * @throws JavaModelException
	 */
	private String generateMethodBody(MethodCoreElement ce) throws JavaModelException {
	
		String methodBody = new String ();
		//Add binary
		if (ce.getReturnType()!=null && !ce.getReturnTypeAsSignature().startsWith("V"))
				methodBody = methodBody.concat("\t "+ce.getReturnType() +" "+ TitlesAndConstants.RETURNTYPE+" = null;\n");
		methodBody = methodBody.concat(BinaryWrapper.createCommandCode(exec, args, "\t")+"\n");
		/*methodBody = methodBody.concat(BinaryWrapper.createBinaryString(exec, "\t ")+"\n");
		for (String arg:args){
			methodBody = methodBody.concat("\t cmd = cmd.concat(\" \");\n");
			methodBody = methodBody.concat(BinaryWrapper.createArgumentString(arg, "\t ")+"\n");
		}*/
		//Add Process to execute
		methodBody = methodBody.concat("\t Process execProc = null;\n");
		methodBody = methodBody.concat("\t ProcessBuilder pb = new ProcessBuilder(cmd);\n");
		methodBody = methodBody.concat(BinaryWrapper.createEnvironmentVarsCode(envVariablesTableComp.getKeyValueMap(),"\t"));
		
		methodBody = methodBody.concat("\t try {\n");
		methodBody = methodBody.concat("\t\t int exitValue = 0;\n");
		methodBody = methodBody.concat("\t\t for (int i = 0; i < 10; i++) {\n");
		methodBody = methodBody.concat("\t\t\t System.out.println(\"Attempt \" + i + \" out of \" + 3);\n");
		//methodBody = methodBody.concat("\t\t\t execProc = Runtime.getRuntime().exec(cmd);\n");
		methodBody = methodBody.concat("\t\t\t execProc = pb.start();\n");
		//Add standard input treatment 
		methodBody = methodBody.concat(BinaryWrapper.createStandardInputString(stdin, ce, "\t\t\t ")+"\n");
		//Add standard err treatment
		methodBody = methodBody.concat(BinaryWrapper.createStandardStreamsRedirectionString(stderr, "stderr_is", "execProc.getErrorStream()", ce, "\t\t\t ")+"\n");
		
		//Add standard output treatment
		methodBody = methodBody.concat(BinaryWrapper.createStandardStreamsRedirectionString(stdout, "stdout_is", "execProc.getInputStream()", ce ,"\t\t\t ")+"\n");
		
		methodBody = methodBody.concat("\t\t\t exitValue = execProc.waitFor();\n");
		methodBody = methodBody.concat("\t\t\t System.out.println(exitValue);\n");
		methodBody = methodBody.concat("\t\t\t if (exitValue == 0) {\n");
		methodBody = methodBody.concat("\t\t\t\t break;\n");
		methodBody = methodBody.concat("\t\t\t }\n");
		methodBody = methodBody.concat("\t\t }\n");
        
		methodBody = methodBody.concat("\t\t if (exitValue != 0) {\n");
		methodBody = methodBody.concat("\t\t\t throw new Exception(\"Exit value is \" + exitValue);\n");
		methodBody = methodBody.concat("\t\t }\n");
		
		methodBody = methodBody.concat("\t} catch (Exception e) {\n");
		methodBody = methodBody.concat("\t\t e.printStackTrace();\n");
		methodBody = methodBody.concat("\t\t System.exit(1);\n");
		methodBody = methodBody.concat("\t}\n");
		if (ce.getReturnType()!=null && !ce.getReturnTypeAsSignature().startsWith("V"))
			methodBody = methodBody.concat("\t return "+ TitlesAndConstants.RETURNTYPE+";\n");
		return methodBody;
	}

	/** Get the fully qualified domain name of the declaring class from the introduced class name
	 * @param decClass Introduced declaring class name 
	 * @return Fully qualified domain name
	 * @throws JavaModelException
	 */
	private String getDeclaringClassFQDN(String decClass)
			throws JavaModelException {
		IPackageFragment pf;
		String st = Signature.getQualifier(declaringClass);
		if (st != null && st.trim().length() > 0) {
			return decClass;
		} else {
			IFile metadataFile = secondPage.getJavaProject().getProject()
					.getFolder(ProjectMetadata.METADATA_FOLDER)
					.getFile(ProjectMetadata.METADATA_FILENAME);
			if (metadataFile != null) {
				try {
					ProjectMetadata pr_meta = new ProjectMetadata(new File(
							metadataFile.getRawLocation().toOSString()));
					return pr_meta.getMainPackageName() + ".coreelements." + decClass;
				} catch (Exception e) {
					throw new JavaModelException(e, JavaModelStatus.ERROR);
				}
			} else
				throw new JavaModelException(new Exception(
						"Unable to find project metadata file"),
						JavaModelStatus.ERROR);
		}
	}
	
	/** Create the method core element declaring class
	 * @param m Progress monitor
	 * @return  Eclipse JDT IType object representing the core element declaring class
	 * @throws JavaModelException
	 */
	private IType createDeclaringClassType(IProgressMonitor m)
			throws JavaModelException {
		IPackageFragmentRoot pfr = secondPage.getPackageFragmentRoot();
		String lineDelimiter = StubUtility
				.getLineDelimiterUsed(secondPage.getJavaProject());
		IPackageFragment pf;
		String st = Signature.getQualifier(declaringClass);
		if (st != null && st.trim().length() > 0) {
			pf = pfr.getPackageFragment(st);
			if (pf != null) {
				if (!pf.exists()) {
					pf = pfr.createPackageFragment(st, true, m);
				}
			} else {
				pf = pfr.createPackageFragment(st, true, m);
			}
		} else {
			IFile metadataFile = secondPage.getJavaProject().getProject()
					.getFolder(ProjectMetadata.METADATA_FOLDER)
					.getFile(ProjectMetadata.METADATA_FILENAME);
			if (metadataFile != null) {
				try {
					ProjectMetadata pr_meta = new ProjectMetadata(new File(
							metadataFile.getRawLocation().toOSString()));
					pfr = secondPage.getJavaProject().findPackageFragmentRoot(
							secondPage.getJavaProject().getPath().append(
									pr_meta.getSourceDir()));
					if (pfr != null) {
						pf = pfr.getPackageFragment(pr_meta
								.getMainPackageName() + ".coreelements");
						if (pf.exists()) {
							st = pf.getElementName();
						} else
							throw new JavaModelException(new Exception(
									"Unable to find core elemet package"),
									JavaModelStatus.ERROR);
					} else {
						throw new JavaModelException(new Exception(
								"Unable to find core elemet package root"),
								JavaModelStatus.ERROR);
					}
				} catch (Exception e) {
					throw new JavaModelException(e, JavaModelStatus.ERROR);
				}
			} else
				throw new JavaModelException(new Exception(
						"Unable to find project metadata file"),
						JavaModelStatus.ERROR);
		}
		String name = Signature.getSimpleName(declaringClass);
		log.debug("Package: " + st + " Class name: " + name);
		ICompilationUnit cu = pf.getCompilationUnit(name + ".java");
		if (cu == null || !cu.exists()){
			cu = pf.createCompilationUnit(name + ".java", "",false, m);
			cu.createPackageDeclaration(st, m);
			cu.createType(constructCEClassStub(name, lineDelimiter), null,false, m);
		}
		return cu.getType(name);
	}
	
	/** Build the core element class code
	 * @param interfaceName Name of the class
	 * @param lineDelimiter Line delimiter
	 * @return Code of the core element class
	 */
	private String constructCEClassStub(String interfaceName,
			String lineDelimiter) {
		StringBuffer buf = new StringBuffer("public class "); //$NON-NLS-1$
		buf.append(interfaceName);
		buf.append("{");
		buf.append(lineDelimiter);
		buf.append("}");
		return buf.toString();
	}
	
}
