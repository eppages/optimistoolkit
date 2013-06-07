/*
 *  Copyright 2011-2013 Barcelona Supercomputing Center (www.bsc.es)
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

package es.bsc.servicess.ide.dialogs;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.Signature;
import org.eclipse.jdt.core.search.IJavaSearchScope;
import org.eclipse.jdt.core.search.SearchEngine;
import org.eclipse.jdt.ui.JavaElementLabelProvider;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.ElementListSelectionDialog;

import es.bsc.servicess.ide.Activator;
import es.bsc.servicess.ide.ConstraintsUtils;
import es.bsc.servicess.ide.Logger;
import es.bsc.servicess.ide.ProjectMetadata;
import es.bsc.servicess.ide.model.CoreElementParameter;
import es.bsc.servicess.ide.model.Dependency;
import es.bsc.servicess.ide.model.OrchestrationElement;
import es.bsc.servicess.ide.model.Parameter;

public class SelectMethodFromExternalClassDialog extends Dialog {

	private Text methodNameText;
	private Button selectMethodButton;
	private IType oeType;
	private IMethod oeMethod;
	private Table oeConstraintsTable;
	private Button oeAddConButton;
	private Button oeModifyConButton;
	private Button oeDeleteConButton;
	private Map<String,String> constraints;
	private OrchestrationElement oe;
	private Logger log= Logger.getLogger(SelectMethodFromExternalClassDialog.class);

	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.Dialog#createDialogArea(org.eclipse.swt.widgets.Composite)
	 */
	public Composite createDialogArea(Composite parent) {
		Composite comp = new Composite(parent, SWT.NONE);
		GridData rd = new GridData(GridData.FILL_HORIZONTAL
				| GridData.VERTICAL_ALIGN_BEGINNING);
		rd.grabExcessHorizontalSpace = true;
		comp.setLayoutData(rd);
		comp.setLayout(new GridLayout(2, false));
		
		Label methodLabel = new Label(comp, SWT.NONE);
		methodLabel.setText("Method");
		Composite method = new Composite(comp, SWT.NONE);
		rd = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		rd.grabExcessHorizontalSpace = true;
		rd.horizontalIndent = 0;
		rd.horizontalSpan = 0;
		method.setLayoutData(rd);
		GridLayout oeReturnLayout = new GridLayout();
		oeReturnLayout.numColumns = 2;
		oeReturnLayout.marginLeft = 0;
		oeReturnLayout.marginRight = 0;
		method.setLayout(oeReturnLayout);
		methodNameText = new Text(method, SWT.SINGLE | SWT.BORDER);
		rd = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		rd.grabExcessHorizontalSpace = true;
		rd.minimumWidth = 300;
		methodNameText.setLayoutData(rd);
		selectMethodButton = new Button(method, SWT.NONE);
		selectMethodButton.setText("Select...");
		selectMethodButton.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {
					selectCEMethod();
			}

			@Override
			public void widgetSelected(SelectionEvent arg0) {
					selectCEMethod();
			}
		});
		createContraintsPart(comp);
		
		return comp;
	}

	private void createContraintsPart(Composite comp) {
		// Constraints table
		Label oeConstLabel = new Label(comp, SWT.NONE);
		oeConstLabel.setText("Constraints");
		Composite oeCons = new Composite(comp, SWT.NONE);
		GridData rd = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		rd.grabExcessHorizontalSpace = true;
		rd.horizontalIndent = 0;
		oeCons.setLayoutData(rd);
		GridLayout oeParamsLayout = new GridLayout();
		oeParamsLayout.numColumns = 2;
		oeParamsLayout.marginLeft = 0;
		oeParamsLayout.marginRight = 0;
		oeCons.setLayout(oeParamsLayout);
		oeConstraintsTable = new Table(oeCons, SWT.SINGLE | SWT.BORDER
				| SWT.FULL_SELECTION);
		oeConstraintsTable.setHeaderVisible(true);
		oeConstraintsTable.setLinesVisible(true);
		TableColumn oeConstName = new TableColumn(oeConstraintsTable, SWT.NULL);
		oeConstName.setText("Name");
		oeConstName.pack();
		TableColumn oeConstValue = new TableColumn(oeConstraintsTable, SWT.NULL);
		oeConstValue.setText("Value");
		oeConstValue.pack();
		rd = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		rd.grabExcessHorizontalSpace = true;
		rd.grabExcessVerticalSpace = true;
		rd.minimumHeight = 90;
		oeConstraintsTable.setLayoutData(rd);
		Composite oeParamsButtons = new Composite(oeCons, SWT.NONE);
		oeParamsLayout = new GridLayout();
		oeParamsLayout.numColumns = 1;
		oeParamsLayout.marginLeft = 0;
		oeParamsLayout.marginRight = 0;
		oeParamsButtons.setLayout(oeParamsLayout);
		oeAddConButton = new Button(oeParamsButtons, SWT.NONE);
		oeAddConButton.setText("Add...");
		rd = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		rd.grabExcessHorizontalSpace = true;
		oeAddConButton.setLayoutData(rd);
		oeModifyConButton = new Button(oeParamsButtons, SWT.NONE);
		oeModifyConButton.setText("Modify...");
		oeModifyConButton.setEnabled(false);
		rd = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		rd.grabExcessHorizontalSpace = true;
		oeModifyConButton.setLayoutData(rd);
		oeDeleteConButton = new Button(oeParamsButtons, SWT.NONE);
		oeDeleteConButton.setText("Delete");
		oeDeleteConButton.setEnabled(false);
		rd = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		rd.grabExcessHorizontalSpace = true;
		oeDeleteConButton.setLayoutData(rd);
		addConstraintsListeners();
	}

	protected void addConstraintsListeners() {
		oeConstraintsTable.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {
				if (oeConstraintsTable.getSelectionIndex() < 0) {
					oeDeleteConButton.setEnabled(false);
					oeModifyConButton.setEnabled(false);
				} else {
					oeDeleteConButton.setEnabled(true);
					oeModifyConButton.setEnabled(true);
				}
			}

			@Override
			public void widgetSelected(SelectionEvent arg0) {
				if (oeConstraintsTable.getSelectionIndex() < 0) {
					oeDeleteConButton.setEnabled(false);
					oeModifyConButton.setEnabled(false);
				} else {
					oeDeleteConButton.setEnabled(true);
					oeModifyConButton.setEnabled(true);
				}

			}
		});
		oeAddConButton.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {
				modifyConTableItem(-1);
			}

			@Override
			public void widgetSelected(SelectionEvent arg0) {
				modifyConTableItem(-1);
			}
		});
		oeModifyConButton.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {
				modifyConTableItem(oeConstraintsTable.getSelectionIndex());
			}

			@Override
			public void widgetSelected(SelectionEvent arg0) {
				modifyConTableItem(oeConstraintsTable.getSelectionIndex());
			}
		});
		oeDeleteConButton.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {
				removeConstraint(oeConstraintsTable.getSelectionIndex());
			}

			@Override
			public void widgetSelected(SelectionEvent arg0) {
				removeConstraint(oeConstraintsTable.getSelectionIndex());
			}
		});

	}
	
	protected void modifyConTableItem(int selection) {
		String[] p = null;
		boolean modified = false;
		if (selection >= 0) {
			p = new String[] {
					oeConstraintsTable.getItem(selection).getText(0).trim(),
					oeConstraintsTable.getItem(selection).getText(1).trim() };
			modified = true;
		}
		ModifyConstraintsDialog dialog = new ModifyConstraintsDialog(
				this.getShell(), p,
				ConstraintsUtils.getSupportedConstraintNames(), modified);
		if (dialog.open() == Window.OK) {
			p = dialog.getConstraint();
			if (p != null) {
				TableItem it;
				if (selection < 0) {
					it = new TableItem(oeConstraintsTable, SWT.NONE);
				} else {
					it = oeConstraintsTable.getItem(selection);
					constraints.remove(it.getText(0));
				}
				it.setText(p);
				constraints.put(p[0], p[1]);
			}
		}
	}

	protected void removeConstraint(int selectionIndex) {
		TableItem it = oeConstraintsTable.getItem(selectionIndex);
		constraints.remove(it.getText(0));
		oeConstraintsTable.remove(selectionIndex);
		
	}

	private void selectCEMethod() {
		if (oeType == null) {
			MessageDialog.openError(super.getShell(), "Error", "Getting the Class");
			return;
		}
		try {
			IMethod[] elements = oeType.getMethods();
			IJavaSearchScope scope = SearchEngine
					.createJavaSearchScope(elements);
			ElementListSelectionDialog dialog = new ElementListSelectionDialog(
					this.getShell(), new JavaElementLabelProvider(
							JavaElementLabelProvider.SHOW_DEFAULT));
			dialog.setIgnoreCase(false);
			dialog.setTitle("Choose Method");
			dialog.setMessage("Choose Method from the selected class");
			dialog.setEmptyListMessage("Empty");
			dialog.setElements(elements);
			dialog.setHelpAvailable(false);

			if (dialog.open() == Window.OK) {
				oeMethod = (IMethod) dialog.getFirstResult();
				if (!methodNameText.getText().trim()
						.equals(oeMethod.getElementName())) {
					methodNameText.setText(oeMethod.getElementName());
				}
			} else {
				MessageDialog.openError(this.getShell(), "Error",
						"Getting the Class");

			}
		} catch (JavaModelException e) {
			MessageDialog.openError(this.getShell(), "Error", e.getMessage());
		}

	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.Dialog#okPressed()
	 */
	public void okPressed() {
		if (oeMethod != null ) {
			try{
				log.debug("Creating oe for "+ oeMethod.getElementName());
				oe = new OrchestrationElement(oeMethod.getElementName(), oeMethod.getFlags(),
						Signature.toString(oeMethod.getReturnType()),oeMethod, 
						oeType.getFullyQualifiedName());
				String[] paramNames = oeMethod.getParameterNames();
				String[] paramTypes = oeMethod.getParameterTypes();
				log.debug(" Adding "+ oeMethod.getNumberOfParameters() + " parameters");
				log.debug(" Param names: "+ paramTypes);
				log.debug(" Param types: "+ paramTypes);
				for (int i = 0; i < oeMethod.getNumberOfParameters(); i++) {
					Parameter p = new Parameter(
							Signature.toString(paramTypes[i]), paramNames[i]);
					//TODO
					oe.getParameters().add(p);
				}
				oe.setConstraints(constraints);
				IFile file = oeType.getJavaProject().getProject()
						.getFolder(ProjectMetadata.METADATA_FOLDER)
						.getFile(ProjectMetadata.METADATA_FILENAME);
				ProjectMetadata prMeta = new ProjectMetadata(file.getRawLocation().toFile());
				Dependency dep = prMeta.getExternalOrchestrationClassDependency(oeType);
				prMeta.addElementToDependency(dep.getLocation(), dep.getType(), oe.getLabel());
				prMeta.toFile(file.getRawLocation().toFile());
				super.okPressed();
			}catch (Exception e){
				log.error("Exception generating OE",e);
				ErrorDialog.openError(getShell(), "Error", e.getMessage(), 
						new Status(IStatus.ERROR, Activator.PLUGIN_ID, e.getMessage()));
			}
		} else {
			ErrorDialog.openError(getShell(), "Error", "Method missing", 
					new Status(IStatus.ERROR, Activator.PLUGIN_ID, "Selected Method is null"));
		}

	}

	public SelectMethodFromExternalClassDialog(Shell parentShell, IType oeType) {
		super(parentShell);
		this.oeType=oeType;
		this.oeMethod=null;
		this.constraints = new HashMap<String,String>();
	}
	
	public IMethod getMethod(){
		return oeMethod;
	}
	
	public Map<String,String> getConstraints(){
		return constraints;
	}
	
	public OrchestrationElement getOrchestrationElement(){
		return oe;
	}
	
	


	
}
