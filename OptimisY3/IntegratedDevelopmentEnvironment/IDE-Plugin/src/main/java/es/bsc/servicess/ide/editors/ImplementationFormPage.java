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

package es.bsc.servicess.ide.editors;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.LinkedList;
import java.util.Map.Entry;

import javax.xml.parsers.ParserConfigurationException;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.eclipse.core.internal.resources.Resource;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.Signature;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.AbstractTypeDeclaration;
import org.eclipse.jdt.core.dom.Annotation;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.IExtendedModifier;
import org.eclipse.jdt.core.dom.ImportDeclaration;
import org.eclipse.jdt.core.dom.MemberValuePair;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.NormalAnnotation;
import org.eclipse.jdt.core.dom.QualifiedName;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.internal.ui.wizards.NewElementWizard;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;
import org.eclipse.text.edits.MalformedTreeException;
import org.eclipse.text.edits.TextEdit;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.*;
import org.eclipse.ui.forms.editor.*;
import org.eclipse.ui.forms.events.*;
import org.eclipse.ui.forms.widgets.*;
import org.xml.sax.SAXException;

import es.bsc.servicess.ide.Activator;
import es.bsc.servicess.ide.ConstraintsUtils;
import es.bsc.servicess.ide.Logger;
import es.bsc.servicess.ide.ProjectMetadata;
import es.bsc.servicess.ide.TitlesAndConstants;
import es.bsc.servicess.ide.dialogs.AddServiceLocationDialog;
import es.bsc.servicess.ide.dialogs.ModifyConstraintsDialog;
import es.bsc.servicess.ide.dialogs.ModifyCoreElementParameterDialog;
import es.bsc.servicess.ide.dialogs.SelectMethodFromExternalClassDialog;
import es.bsc.servicess.ide.model.CoreElementParameter;
import es.bsc.servicess.ide.model.Dependency;
import es.bsc.servicess.ide.model.MethodCoreElement;
import es.bsc.servicess.ide.model.OrchestrationClass;
import es.bsc.servicess.ide.model.OrchestrationElement;
import es.bsc.servicess.ide.model.Parameter;
import es.bsc.servicess.ide.model.ServiceCoreElement;
import es.bsc.servicess.ide.model.ServiceElement;
import es.bsc.servicess.ide.wizards.IServiceSsOrchestrationClassWizard;
import es.bsc.servicess.ide.wizards.ServiceSsImportCoreElementWizard;
import es.bsc.servicess.ide.wizards.ServiceSsImportOrchestrationClassWizard;
import es.bsc.servicess.ide.wizards.ServiceSsNewCoreElementWizard;
import es.bsc.servicess.ide.wizards.ServiceSsNewOrchestrationElementWizard;
import es.bsc.servicess.ide.wizards.ServiceSsNewServiceClassWizard;

/**
 * Editor page for implementing the Service Implementation tab
 * @author Jorge Ejarque (Barcelona Supercomputing Center)
 *
 */
@SuppressWarnings("restriction")
public class ImplementationFormPage extends CommonFormPage {

	protected static final int MIN = 0;
	protected static final int MAX = 1;
	protected static final int INI = 2;
	private Combo serviceClassList;
	private int selectedClass;
	private List oeList;
	private int selectedOE;
	private LinkedList<ServiceElement> selectedOEList;
	private List ceList;
	private int selectedCE;
	private LinkedList<ServiceElement> selectedCEList;
	private Text runtimetext;
	private Text oeNameText;
	private Text ceNameText;
	// private Combo ceModifierCombo;
	private Text oeReturnTypeText;
	private Text ceReturnTypeText;
	private Table oeParametersTable;
	private Table ceParametersTable;
	private Table oeConstraintsTable;
	private Table ceConstraintsTable;
	private Button isPartOfServiceItfButton;
	// private Button oePrivate;
	// private Button oePublic;
	// private Button oeStatic;
	// private Button oeFinal;
	// private Button ceStatic;
	// private Button ceFinal;
	private Section oeDescSection;
	private Section ceDescSection;
	private Button newCEButton;
	private Button newOEButton;
	private Text ceDCText;
	private Button ceIsInit;
	private Button ceIsModifier;
	private Text ceNamespaceText;
	private Text ceServiceText;
	private Text cePortText;
	private Button ceAddConButton;
	private Button ceModifyConButton;
	private Button ceDeleteConButton;
	private Button oeAddConButton;
	private Button oeModifyConButton;
	private Button oeDeleteConButton;
	// private Button ceAddParButton;
	private Button ceModifyParButton;
	// private Button ceDeleteParButton;
	private List wsdlList;
	private Button addWsdlButton;
	private Button modWsdlButton;
	private Button deleteWsdlButton;
	private ScrolledForm form;
	private String previousRuntimeLocation;
	private Button deleteServiceButton;
	private Composite ceSpecificComposite;
	private FormToolkit toolkit;
	private Section ceSpecificSection;
	private Composite ceDesc;
	private Text ceMinimumText;
	private Text ceMaximumText;
	private Text oeMinimumText;
	private Text oeMaximumText;
	// private Text initialText;
	private Section ceElas_section;
	private Section oeElas_section;
	private DependencySection ceDep_section;
	private DependencySection oeDep_section;
	private boolean isExternalClass;
	
	private static Logger log = Logger.getLogger(ImplementationFormPage.class);

	/**
	 * Constructor
	 * @param editor Service editor
	 */
	public ImplementationFormPage(FormEditor editor) {
		super(editor, "implementation", "Implementation");
		selectedClass = -1;
		selectedOE = -1;
		selectedCE = -1;
		previousRuntimeLocation = null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.forms.editor.FormPage#createFormContent(org.eclipse.ui.forms.IManagedForm)
	 */
	protected void createFormContent(IManagedForm managedForm) {
		form = managedForm.getForm();
		toolkit = managedForm.getToolkit();
		form.setText("Implementation Overview");
		GridLayout layout = new GridLayout(2, false);
		//layout.verticalSpacing = 3;
		layout.makeColumnsEqualWidth = false;
		form.getBody().setLayout(layout);
		GridData rd = new GridData(GridData.HORIZONTAL_ALIGN_FILL
				| GridData.VERTICAL_ALIGN_BEGINNING);
		rd.grabExcessHorizontalSpace = true;
		form.getBody().setLayoutData(rd);
		
		createServiceClassSection();
		createRuntimeSection();
		createOESection();
		createCESection();
		createOEDescriptionSection();
		createCEDescriptionSection();

		try {
			initData();
			addListeners();
		} catch (PartInitException e) {
			ErrorDialog.openError(getSite().getShell(),
					"Error creating nested structured text editor",
					e.getMessage(), e.getStatus());
		}
	}

	/**
	 * Add widget listeners
	 */
	private void addListeners() {
		// new Service class button

		// serviceClass listener
		serviceClassList.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				serviceClassSelected();
			}
		});
		oeList.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				oeSelected();
			}
		});
		ceList.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				ceSelected();
			}
		});
		ceDescSection.addExpansionListener(new ExpansionAdapter() {
			public void expansionStateChanged(ExpansionEvent e) {
				// form.reflow(true);
			}
		});
		ceParametersTable.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				ceModifyParButton.setEnabled(true);
			}
		});
		ceModifyParButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				modifyParameterDirection();
			}
		});
		ceConstraintsTable.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				ceModifyConButton.setEnabled(true);
				ceDeleteConButton.setEnabled(true);
			}
		});
		ceModifyConButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				modifyCEConstraints(ceConstraintsTable.getSelectionIndex());
			}
		});
		oeModifyConButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				modifyOEConstraints(oeConstraintsTable.getSelectionIndex());
			}
		});
		ceAddConButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				modifyCEConstraints(-1);
			}
		});
		oeAddConButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				modifyOEConstraints(-1);
			}
		});
		ceDeleteConButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				deleteCEConstraints(ceConstraintsTable.getSelectionIndex());
			}

		});
		oeDeleteConButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				deleteOEConstraints(oeConstraintsTable.getSelectionIndex());
			}
		});
		ceMinimumText.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent arg0) {
				updateCEElasticity(MIN, ceMinimumText.getText().trim());
			}
		});
		ceMaximumText.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent arg0) {
				updateCEElasticity(MAX, ceMaximumText.getText().trim());
			}
		});
		oeMinimumText.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent arg0) {
				updateOEElasticity(MIN, oeMinimumText.getText().trim());
			}
		});
		oeMaximumText.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent arg0) {
				updateOEElasticity(MAX, oeMaximumText.getText().trim());
			}
		});
	}

	/**
	 * Update elasticity of the selected core element
	 * @param type MIN or MAX
	 * @param trim
	 */
	protected void updateCEElasticity(int type, String trim) {
		//TODO Extend also for orchestration elements
		//log.debug("Updating Elasticity (" + type + ") to " + trim);
		try {
			if (trim.length() > 0) {
				ServiceElement ele = (ServiceElement) selectedCEList
						.get(ceList.getSelectionIndex());
				ProjectMetadata pr_meta = new ProjectMetadata(
						((ServiceFormEditor) getEditor()).getMetadataFile()
								.getRawLocation().toFile());
				switch (type) {
				case MIN:
					pr_meta.setMinElasticity(ele.getLabel(),
							Integer.parseInt(trim));
					break;
				case MAX:
					pr_meta.setMaxElasticity(ele.getLabel(),
							Integer.parseInt(trim));
					break;
				default:
					break;
				}
				pr_meta.toFile(((ServiceFormEditor) getEditor())
						.getMetadataFile().getRawLocation().toFile());
			}
		} catch (Exception e) {
			log.error("Error updating elasticity");
			e.printStackTrace();
		}
	}
	
	/**
	 * Update elasticity of the selected orchestration element
	 * @param type MIN or MAX
	 * @param trim
	 */
	protected void updateOEElasticity(int type, String trim) {
		//log.debug("Updating Elasticity (" + type + ") to " + trim);
		try {
			if (trim.length() > 0) {
				ServiceElement ele = (ServiceElement) selectedOEList
						.get(oeList.getSelectionIndex());
				ProjectMetadata pr_meta = new ProjectMetadata(
						((ServiceFormEditor) getEditor()).getMetadataFile()
								.getRawLocation().toFile());
				switch (type) {
				case MIN:
					pr_meta.setMinElasticity(ele.getLabel(),
							Integer.parseInt(trim));
					break;
				case MAX:
					pr_meta.setMaxElasticity(ele.getLabel(),
							Integer.parseInt(trim));
					break;
				default:
					break;
				}
				pr_meta.toFile(((ServiceFormEditor) getEditor())
						.getMetadataFile().getRawLocation().toFile());
			}
		} catch (Exception e) {
			log.error("Error updating elasticity");
			e.printStackTrace();
		}
	}

	/**
	 * Delete core element constraints
	 * @param selection Selected constraint
	 */
	private void deleteCEConstraints(int selection) {
		if (selection >= 0) {
			String[] p = new String[] {
					ceConstraintsTable.getItem(selection).getText(0).trim(),
					ceConstraintsTable.getItem(selection).getText(1).trim() };
			try {
				deleteConstraint(serviceClassList.getItem(selectedClass),ceNameText.getText().trim(),
						ceList.getItem(ceList.getSelectionIndex()).trim(), p[0], p[1], true);
				ceConstraintsTable.remove(selection);
			} catch (Exception e) {
				log.error("Error deleting contraint");
				e.printStackTrace();
			}
		}
	}

	/**
	 * Delete orchestration element constraint
	 * @param selection Selected constraint
	 */
	private void deleteOEConstraints(int selection) {
		if (selection >= 0) {
			String[] p = new String[] {
					oeConstraintsTable.getItem(selection).getText(0).trim(),
					oeConstraintsTable.getItem(selection).getText(1).trim() };
			try {
				deleteConstraint(serviceClassList.getItem(selectedClass),oeNameText.getText().trim(),
						oeList.getItem(oeList.getSelectionIndex()).trim(), p[0], p[1], false);
				oeConstraintsTable.remove(selection);
			} catch (Exception e) {
				log.error("Error deleting contraint");
				e.printStackTrace();
			}
		}
	}

	/**
	 * Modify orchestration element constraints
	 * @param selection Selected constraint
	 */
	protected void modifyOEConstraints(int selection) {
		String[] p = null;
		boolean modified = false;
		if (selection >= 0) {
			p = new String[] {
					oeConstraintsTable.getItem(selection).getText(0).trim(),
					oeConstraintsTable.getItem(selection).getText(1).trim() };
			modified = true;
		}
		try {
			ModifyConstraintsDialog dialog = new ModifyConstraintsDialog(this
					.getEditorSite().getShell(), p,
					ConstraintsUtils.getSupportedConstraintNames(), modified);
			if (dialog.open() == Window.OK) {
				p = dialog.getConstraint();
				if (p != null) {
					modifyConstraint(serviceClassList.getItem(selectedClass),
							oeNameText.getText().trim(),oeList.getItem(oeList.getSelectionIndex()).trim(),
							p[0], p[1], false);
					TableItem it;
					if (selection < 0) {
						it = new TableItem(oeConstraintsTable, SWT.NONE);
					} else {
						it = oeConstraintsTable.getItem(selection);
					}
					it.setText(p);
				}
			}
		} catch (Exception e) {
			log.error("Error Modifying constraint");
			e.printStackTrace();
			ErrorDialog.openError(getSite().getShell(),
				"Error Modifying constraint", e.getMessage(),
				new Status(IStatus.ERROR, Activator.PLUGIN_ID,
					"Error Modifying constraint"));
		}
	}

	/**
	 * Modify core element constraint
	 * @param selection Selected constraint
	 */
	protected void modifyCEConstraints(int selection) {
		String[] p = null;
		boolean modified = false;
		if (selection >= 0) {
			p = new String[] {
					ceConstraintsTable.getItem(selection).getText(0).trim(),
					ceConstraintsTable.getItem(selection).getText(1).trim() };
			modified = true;
		}
		try {
			ModifyConstraintsDialog dialog = new ModifyConstraintsDialog(this
					.getEditorSite().getShell(), p,
					ConstraintsUtils.getSupportedConstraintNames(), modified);
			if (dialog.open() == Window.OK) {
				p = dialog.getConstraint();
				if (p != null) {
					modifyConstraint(serviceClassList.getItem(selectedClass),ceNameText.getText().trim(),
							ceList.getItem(ceList.getSelectionIndex()).trim(),p[0], p[1], true);
					TableItem it;
					if (selection < 0) {
						it = new TableItem(ceConstraintsTable, SWT.NONE);
					} else {
						it = ceConstraintsTable.getItem(selection);
					}
					it.setText(p);
				}
			}
		} catch (Exception e) {
			log.error("Error Modifying constraint");
			e.printStackTrace();
			ErrorDialog.openError(getSite().getShell(),
				"Error Modifying constraint", e.getMessage(),
				new Status(IStatus.ERROR, Activator.PLUGIN_ID, "Error Modifying constraint"));
		}
	}

	/**
	 * Modify the selected core element parameter direction
	 */
	private void modifyParameterDirection() {
		int selection = ceParametersTable.getSelectionIndex();
		CoreElementParameter p = null;
		if (selection >= 0) {
			p = new CoreElementParameter(ceParametersTable.getItem(selection)
					.getText(0).trim(), ceParametersTable.getItem(selection)
					.getText(1).trim(), ceParametersTable.getItem(selection)
					.getText(2).trim());
			ModifyCoreElementParameterDialog dialog = new ModifyCoreElementParameterDialog(
					this.getEditorSite().getWorkbenchWindow(), this
							.getEditorSite().getShell(), p,
					((ServiceFormEditor) this.getEditor()).getProject(), false);
			if (dialog.open() == Window.OK) {
				try {
					p = (CoreElementParameter) dialog.getParameter();
					TableItem it = ceParametersTable.getItem(selection);
					modifyDirection(serviceClassList.getItem(selectedClass),
							ceNameText.getText().trim(), p);
					it.setText(new String[] { p.getType(), p.getName(),
							p.getDirection() });
				} catch (Exception e) {
					log.error("Error Modifying direction");
					e.printStackTrace();
				}
			}
		} else {
			log.error("Selected parameter is unknown");
		}
	}

	/**
	 * Load widgets when an orchestration class is selected
	 */
	protected void serviceClassSelected() {
		try {
			
			if ((selectedClass == -1)
					|| (selectedClass != serviceClassList.getSelectionIndex())) {
				isExternalClass =((ServiceFormEditor)getEditor()).getProjectMetadata()
				 .isExternalOrchestrationClass(serviceClassList.getText());
				reloadClassElements();
			}
		} catch (PartInitException e) {
			ErrorDialog.openError(getSite().getShell(),
					"Getting elements from " + serviceClassList.getText(),
					e.getMessage(), e.getStatus());
		} catch (JavaModelException e) {
			ErrorDialog.openError(getSite().getShell(),
					"Getting elements from " + serviceClassList.getText(),
					e.getMessage(), e.getStatus());
		} catch (ParserConfigurationException e) {
			ErrorDialog.openError(getSite().getShell(),
					"Getting elements from " + serviceClassList.getText(),
					e.getMessage(), new Status(IStatus.ERROR, Activator.PLUGIN_ID,e.getMessage(), e));
		} catch (SAXException e) {
			ErrorDialog.openError(getSite().getShell(),
					"Getting elements from " + serviceClassList.getText(),
					e.getMessage(), new Status(IStatus.ERROR, Activator.PLUGIN_ID,e.getMessage(), e));
		} catch (IOException e) {
			ErrorDialog.openError(getSite().getShell(),
					"Getting elements from " + serviceClassList.getText(),
					e.getMessage(), new Status(IStatus.ERROR, Activator.PLUGIN_ID,e.getMessage(), e));
		} catch (Exception e) {
			ErrorDialog.openError(getSite().getShell(),
					"Getting elements from " + serviceClassList.getText(),
					e.getMessage(), new Status(IStatus.ERROR, Activator.PLUGIN_ID,e.getMessage(), e));
			
		}
	}
	
	private void reloadClassElements() throws Exception{
		selectedCEList = getCoreElements(serviceClassList.getText(), 
				((ServiceFormEditor)getEditor()).getProject(), 
				((ServiceFormEditor)getEditor()).getProjectMetadata());
		//log.debug("**** CE found: " + selectedCEList.size());
		ceList.setItems(getElementsNames(selectedCEList));
		selectedOEList = getOrchestrationElements(serviceClassList.getText(),
				((ServiceFormEditor)getEditor()).getProject(), 
				((ServiceFormEditor)getEditor()).getProjectMetadata());
		//log.debug("**** OE found: " + selectedOEList.size());
		oeList.setItems(getElementsNames(selectedOEList));
		selectedClass = serviceClassList.getSelectionIndex();
		selectedCE=-1;
		selectedOE=-1;
		resetCEDescription();
		resetOEDescription();
		deleteServiceButton.setEnabled(true);
		newCEButton.setEnabled(true);
		newOEButton.setEnabled(true);
	}

	private void resetOEDescription() {
		resetGeneralOEDescription();
		resetSpecificOEDescription();
		resetElasticityOEDescription();
		oeDep_section.reset();
		oeDescSection.setExpanded(false);
	}


	private void resetElasticityOEDescription() {
		oeMinimumText.setText("");
		oeMaximumText.setText("");
		oeElas_section.setExpanded(false);
		
	}

	private void resetGeneralOEDescription() {
		oeNameText.setText("");
		oeReturnTypeText.setText("");
		oeParametersTable.removeAll();
		oeConstraintsTable.removeAll();
	}
	
	private void resetSpecificOEDescription() {
		isPartOfServiceItfButton.setEnabled(false);
		
	}
	private void resetCEDescription() {
		resetGeneralCEDescription();
		resetSpecificCEDescription();
		resetElasticityCEDescription();
		ceDep_section.reset();
		ceDescSection.setExpanded(false);
	}
	
	private void resetElasticityCEDescription() {
		ceMinimumText.setText("");
		ceMaximumText.setText("");
		ceElas_section.setExpanded(false);
		
	}
	
	private void resetGeneralCEDescription() {
		ceNameText.setText("");
		ceReturnTypeText.setText("");
		ceParametersTable.removeAll();
		ceConstraintsTable.removeAll();
	}
	
	private void resetSpecificCEDescription() {
		if (ceSpecificComposite!=null)
			ceSpecificComposite.dispose();
		ceSpecificComposite = toolkit.createComposite(ceSpecificSection,
				SWT.NONE);
		ceSpecificSection.setClient(ceSpecificComposite);
		ceSpecificSection.setExpanded(true);
		ceSpecificSection.setExpanded(false);
		
	}

	/**
	 * Load widgets when an core element is selected
	 */
	protected void ceSelected() {
		if (selectedCE != ceList.getSelectionIndex()) {
			loadSelectedCEDesc();
		}
	}
	
	private void loadSelectedCEDesc(){
		printElementDescription(selectedCEList.get(ceList
				.getSelectionIndex()));
		selectedCE = ceList.getSelectionIndex();
		ceDescSection.setEnabled(true);
		ceDescSection.setExpanded(true);
	}
	
	/**
	 * Load widgets when an orchestration element is selected
	 */
	protected void oeSelected() {
		if (selectedOE != oeList.getSelectionIndex()) {
			loadSelectedOEDesc();
		}
	}
	
	private void loadSelectedOEDesc(){
		printElementDescription(selectedOEList.get(oeList
				.getSelectionIndex()));
		selectedOE = oeList.getSelectionIndex();
		oeDescSection.setEnabled(true);
		oeDescSection.setExpanded(true);
	}

	/**
	 * Print the selected element description
	 * @param serviceElement Selected service element
	 */
	protected void printElementDescription(ServiceElement serviceElement) {
		try {
			ProjectMetadata pr_meta = new ProjectMetadata(
					((ServiceFormEditor) getEditor()).getMetadataFile()
					.getRawLocation().toFile());
			if (serviceElement instanceof OrchestrationElement) {
				log.debug("Printing: " + serviceElement.getMethodName());
				oeNameText.setText(serviceElement.getMethodName());
				oeReturnTypeText.setText(serviceElement.getReturnType());
				// Checkflags
				/*
				 * oePublic.setSelection(Flags.isPublic(serviceElement.getMethodModifier
				 * ())); oePrivate.setSelection(Flags.isPrivate(serviceElement.
				 * getMethodModifier()));
				 * oeStatic.setSelection(Flags.isStatic(serviceElement
				 * .getMethodModifier()));
				 * oeFinal.setSelection(Flags.isFinal(serviceElement
				 * .getMethodModifier()));
				 */

				// Set parameters
				oeParametersTable.removeAll();
				for (Parameter p : serviceElement.getParameters()) {
					String[] row = new String[2];
					row[0] = p.getType();
					row[1] = p.getName();
					TableItem ti = new TableItem(oeParametersTable, SWT.NONE);
					ti.setText(row);
				}
				// oeParametersTable.pack();
				// Set Constraints
				oeConstraintsTable.removeAll();
				for (Entry<String, String> e : serviceElement.getConstraints()
						.entrySet()) {
					String[] row = new String[2];
					row[0] = e.getKey();
					row[1] = e.getValue();
					TableItem ti = new TableItem(oeConstraintsTable, SWT.NONE);
					ti.setText(row);
				}
				// oeConstraintsTable.pack();
				isPartOfServiceItfButton
				.setSelection(((OrchestrationElement) serviceElement)
						.isPartOfServiceItf());
				oeDep_section.setCurrentElement(serviceElement);
				oeDep_section.setEnabled(true);
				// Check if elasticity should be enabled for OEs
				if (pr_meta.getOrchestrationClass(serviceClassList.getItem(
						serviceClassList.getSelectionIndex())).getType().
						equals(TitlesAndConstants.WS_CLASS)){
					oeElas_section.setExpanded(true);
					oeElas_section.setExpanded(false);
					oeElas_section.setEnabled(true);

					oeMinimumText.setText(Integer.toString(pr_meta
							.getMinElasticity(serviceElement.getLabel())));
					oeMaximumText.setText(Integer.toString(pr_meta
							.getMaxElasticity(serviceElement.getLabel())));
				}else{
					oeElas_section.setExpanded(false);
					oeElas_section.setEnabled(true);

					oeMinimumText.setText("1");
					oeMaximumText.setText("1");
				}
				
			} else if ((serviceElement instanceof MethodCoreElement)
					|| (serviceElement instanceof ServiceCoreElement)) {
				log.debug("Printing: " + serviceElement.getMethodName());
				ceNameText.setText(serviceElement.getMethodName());
				ceReturnTypeText.setText(serviceElement.getReturnType());
				// Checkflags
				// ceStatic.setSelection(Flags.isStatic(serviceElement.getMethodModifier()));
				// ceFinal.setSelection(Flags.isFinal(serviceElement.getMethodModifier()));
				// Set parameters
				ceParametersTable.removeAll();
				for (Parameter p : serviceElement.getParameters()) {
					String[] row = new String[3];
					row[0] = p.getType();
					row[1] = p.getName();
					row[2] = ((CoreElementParameter) p).getDirection();
					TableItem ti = new TableItem(ceParametersTable, SWT.NONE);
					ti.setText(row);
				}
				// ceParametersTable.pack();
				// Set Constraints
				ceConstraintsTable.removeAll();
				for (Entry<String, String> e : serviceElement.getConstraints()
						.entrySet()) {
					String[] row = new String[2];
					row[0] = e.getKey();
					row[1] = e.getValue();
					TableItem ti = new TableItem(ceConstraintsTable, SWT.NONE);
					ti.setText(row);
				}
				// ceConstraintsTable.pack();
				


				if (serviceElement instanceof MethodCoreElement) {
					ceSpecificComposite = createMethodSpecificDesc(ceSpecificComposite);
					updateSpecific();
					ceDCText.setText(((MethodCoreElement) serviceElement)
							.getDeclaringClass());
					ceIsInit.setSelection(((MethodCoreElement) serviceElement)
							.isInit());
					ceIsModifier.setSelection(((MethodCoreElement) serviceElement)
							.isModifier());
					ceElas_section.setExpanded(true);
					ceElas_section.setExpanded(false);
					ceElas_section.setEnabled(true);

					ceMinimumText.setText(Integer.toString(pr_meta
							.getMinElasticity(serviceElement.getLabel())));
					ceMaximumText.setText(Integer.toString(pr_meta
							.getMaxElasticity(serviceElement.getLabel())));
					// initialText.setText(Integer.toString(pr_meta.getInitialElasticity(serviceElement.getLabel())));

					ceDep_section.setCurrentElement(serviceElement);
					ceDep_section.setEnabled(true);
				} else if (serviceElement instanceof ServiceCoreElement) {
					/*
					 * meComp..setExpanded(false); meComp.setEnabled(false);
					 * seComp.setExpanded(true); seComp.setEnabled(true);
					 */
					ceSpecificComposite = createServiceSpecificDesc(ceSpecificComposite);
					updateSpecific();
					ceServiceText.setText(((ServiceCoreElement) serviceElement)
							.getServiceName());
					ceNamespaceText.setText(((ServiceCoreElement) serviceElement)
							.getNamespace());
					cePortText.setText(((ServiceCoreElement) serviceElement)
							.getPort());

					wsdlList.removeAll();
					for (String s : ((ServiceCoreElement) serviceElement)
							.getWsdlURIs()) {
						wsdlList.add(s);
					}
					//If internal service (war dependency)
					java.util.List<Dependency> deps = pr_meta.getDependencies(new String[]{serviceElement.getLabel()});
					log.debug("Getting dependencies for: " +serviceElement.getLabel());
					log.debug("Deps ("+deps.size()+"): "+ deps);
					if (deps.size()>0){
						log.debug("Internal service element");
						ceElas_section.setExpanded(true);
						ceElas_section.setExpanded(false);
						ceElas_section.setEnabled(true);

						ceMinimumText.setText(Integer.toString(pr_meta
								.getMinElasticity(serviceElement.getLabel())));
						ceMaximumText.setText(Integer.toString(pr_meta
								.getMaxElasticity(serviceElement.getLabel())));
						// initialText.setText(Integer.toString(pr_meta.getInitialElasticity(serviceElement.getLabel())));

						ceDep_section.setCurrentElement(serviceElement);
						ceDep_section.setEnabled(true);
					}else{
						log.debug("External service element");
						ceElas_section.setExpanded(false);
						ceElas_section.setEnabled(false);
						ceDep_section.setEnabled(false);
					}
				}
			}
		} catch (Exception e) {
			log.error("Error Modifying direction");
			e.printStackTrace();
		}
		

	}

	/**
	 * Create the service core element description composite
	 * @param specificComposite specific core element composite
	 * @return Service core element description composite
	 */
	private Composite createServiceSpecificDesc(Composite specificComposite) {
		if (specificComposite != null) {
			specificComposite.dispose();
		}
		Composite seDesc = toolkit.createComposite(ceSpecificSection, SWT.NONE);
		seDesc.setLayout(new GridLayout(2, false));
		toolkit.createLabel(seDesc, "Namespace", SWT.NONE);
		ceNamespaceText = toolkit.createText(seDesc, "", SWT.SINGLE
				| SWT.BORDER);
		GridData rd = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		rd.grabExcessHorizontalSpace = true;
		ceNamespaceText.setLayoutData(rd);
		toolkit.createLabel(seDesc, "Service", SWT.NONE);
		ceServiceText = toolkit.createText(seDesc, "", SWT.SINGLE | SWT.BORDER);
		rd = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		rd.grabExcessHorizontalSpace = true;
		ceServiceText.setLayoutData(rd);
		toolkit.createLabel(seDesc, "Port", SWT.NONE);
		cePortText = toolkit.createText(seDesc, "", SWT.SINGLE | SWT.BORDER);
		rd = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		rd.grabExcessHorizontalSpace = true;
		cePortText.setLayoutData(rd);
		toolkit.createLabel(seDesc, "Locations", SWT.NONE);
		Composite wsdl = toolkit.createComposite(seDesc, SWT.NONE);
		GridLayout wsdlLayout = new GridLayout();
		wsdlLayout.numColumns = 2;
		wsdlLayout.marginLeft = 0;
		wsdlLayout.marginRight = 0;
		wsdl.setLayout(wsdlLayout);
		rd = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		rd.grabExcessHorizontalSpace = true;
		rd.horizontalIndent = 0;
		rd.horizontalSpan = 0;
		wsdl.setLayoutData(rd);
		wsdlList = new List(wsdl, SWT.BORDER);
		rd = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		rd.grabExcessHorizontalSpace = true;
		rd.grabExcessVerticalSpace = true;
		rd.minimumHeight = 90;
		wsdlList.setLayoutData(rd);
		// TODO:AddListeners
		Composite wsdlButtons = toolkit.createComposite(wsdl, SWT.NONE);
		wsdlButtons.setLayout(new GridLayout(1, false));
		addWsdlButton = toolkit.createButton(wsdlButtons, "Add...", SWT.NONE);
		rd = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		rd.grabExcessHorizontalSpace = true;
		addWsdlButton.setLayoutData(rd);
		modWsdlButton = toolkit
				.createButton(wsdlButtons, "Modify...", SWT.NONE);
		rd = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		rd.grabExcessHorizontalSpace = true;
		modWsdlButton.setLayoutData(rd);
		deleteWsdlButton = toolkit
				.createButton(wsdlButtons, "Delete", SWT.NONE);
		rd = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		rd.grabExcessHorizontalSpace = true;
		deleteWsdlButton.setLayoutData(rd);
		wsdlList.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {
				modWsdlButton.setEnabled(true);
			}

			@Override
			public void widgetSelected(SelectionEvent arg0) {
				modWsdlButton.setEnabled(true);

			}
		});
		modWsdlButton.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {
				addLocation(wsdlList.getSelectionIndex());
			}

			@Override
			public void widgetSelected(SelectionEvent arg0) {
				addLocation(wsdlList.getSelectionIndex());

			}

		});
		addWsdlButton.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {
				addLocation(-1);
			}

			@Override
			public void widgetSelected(SelectionEvent arg0) {
				addLocation(-1);

			}

		});
		
		return seDesc;
		
	}
	protected void addLocation(int selection) {
		String s = null;
		if (selection >= 0) {
			s = wsdlList.getItem(selection);
		}
		AddServiceLocationDialog dialog = new AddServiceLocationDialog(
				getSite().getShell(), s);
		if (dialog.open() == Window.OK) {
			s = dialog.getLocation();
			if (selection < 0) {
				wsdlList.add(s);
				//TODO
				/*if (thirdPage.getElement() != null) {
					((ServiceCoreElement) thirdPage.getElement()).getWsdlURIs().add(s);
				}*/
			} else {
				wsdlList.setItem(selection, s);
				/*if (thirdPage.getElement() != null) {
					((ServiceCoreElement) thirdPage.getElement()).getWsdlURIs().set(selection,
							s);
				}*/
			}
		}

	}
	
	/**
	 * Update service description data
	 */
	private void updateSpecific() {
		ceSpecificComposite.layout(true);
		ceSpecificComposite.redraw();
		ceSpecificSection.setClient(ceSpecificComposite);
		ceSpecificSection.layout(true);
		ceSpecificSection.redraw();
		ceDesc.layout(true);
		ceDesc.redraw();
		form.reflow(true);
	}

	/**
	 * Create the method core element description
	 * @param specificComposite core element specific composite
	 * @return Method core element description composite
	 */
	private Composite createMethodSpecificDesc(Composite specificComposite) {
		if (specificComposite != null) {
			specificComposite.dispose();
		}
		Composite meDesc = toolkit.createComposite(ceSpecificSection, SWT.NONE);
		meDesc.setLayout(new GridLayout(2, false));
		toolkit.createLabel(meDesc, "Class", SWT.NONE);
		ceDCText = toolkit.createText(meDesc, "", SWT.SINGLE | SWT.BORDER);
		GridData rd = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		rd.grabExcessHorizontalSpace = true;
		ceDCText.setLayoutData(rd);
		ceDCText.setEditable(false);
		ceIsInit = toolkit.createButton(meDesc, "isInit", SWT.CHECK);
		rd = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		ceIsInit.setLayoutData(rd);
		ceIsModifier = toolkit.createButton(meDesc, "isModifier", SWT.CHECK);
		rd = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		ceIsModifier.setLayoutData(rd);
		return meDesc;
	}

	/**
	 * Get the label of a list of service elements
	 * @param selectedList selected service element
	 * @return Array of element labels
	 */
	protected String[] getElementsNames(LinkedList<ServiceElement> selectedList) {
		String[] elements = new String[selectedList.size()];
		for (int i = 0; i < selectedList.size(); i++) {
			elements[i] = selectedList.get(i).getLabel();
		}
		return elements;
	}

	/**
	 * Create the core element description section
	 */
	private void createCEDescriptionSection() {
		ceDescSection = toolkit.createSection(form.getBody(),
				Section.DESCRIPTION | Section.TITLE_BAR | Section.TWISTIE
						| Section.EXPANDED);
		ceDescSection.setText("Core Element Description"); //$NON-NLS-1$
		ceDescSection
				.setDescription("This section provides the interface to manage the selected core element ");
		GridData rd = new GridData(GridData.HORIZONTAL_ALIGN_FILL
				| GridData.VERTICAL_ALIGN_BEGINNING);
		rd.grabExcessHorizontalSpace = true;
		rd.minimumWidth = 300;
		rd.widthHint = (form.getSize().y) / 2;
		ceDescSection.setLayoutData(rd);
		ceDesc = toolkit.createComposite(ceDescSection, SWT.NONE);
		GridLayout ceDescLayout = new GridLayout();
		ceDescLayout.numColumns = 1;
		ceDesc.setLayout(ceDescLayout);
		createCEGeneralDescription(ceDesc);
		createCESpecificDescription(ceDesc);
		createCEElasticityBounds(ceDesc);
		createCEDependenciesSection(ceDesc);
		ceDescSection.setClient(ceDesc);
		ceDescSection.setExpanded(false);
		ceDescSection.setEnabled(false);

	}

	/**
	 * Create the core element dependency section
	 * @param composite Core element description section
	 */
	private void createCEDependenciesSection(Composite composite) {
		ceDep_section = new DependencySection(form, toolkit, this.getEditor()
				.getSite().getShell(), (ServiceFormEditor) getEditor());
		ceDep_section.createComposite(composite);
	}

	/**
	 * Create the orchestration element dependency section
	 * @param composite Orchestraion element description section
	 */
	private void createOEDependenciesSection(Composite composite) {
		oeDep_section = new DependencySection(form, toolkit, this.getEditor()
				.getSite().getShell(), (ServiceFormEditor) getEditor());
		oeDep_section.createComposite(composite);
	}

	/**
	 * Create core element elasticity section
	 * @param composite Core element description composite
	 */
	private void createCEElasticityBounds(Composite composite) {
		ceElas_section = toolkit.createSection(composite, Section.TWISTIE);
		ceElas_section.setText("Elasticity Description");
		GridData rd = new GridData(GridData.HORIZONTAL_ALIGN_FILL
				| GridData.VERTICAL_ALIGN_BEGINNING);
		rd.grabExcessHorizontalSpace = true;
		ceElas_section.setLayoutData(rd);
		ceElas_section.setLayout(new GridLayout(1, true));
		Composite com = toolkit.createComposite(ceElas_section, SWT.NONE);
		GridLayout ceDescLayout = new GridLayout(4, false);
		com.setLayout(ceDescLayout);
		toolkit.createLabel(com, "Minimum", SWT.NONE);
		ceMinimumText = toolkit.createText(com, "", SWT.SINGLE | SWT.BORDER);
		toolkit.createLabel(com, "Maximum", SWT.NONE);
		ceMaximumText = toolkit.createText(com, "", SWT.SINGLE | SWT.BORDER);
		ceElas_section.setClient(com);
		ceElas_section.setExpanded(true);
		ceElas_section.setExpanded(false);
	}
	
	/**
	 * Create core element elasticity section
	 * @param composite Core element description composite
	 */
	private void createOEElasticityBounds(Composite composite) {
		oeElas_section = toolkit.createSection(composite, Section.TWISTIE);
		oeElas_section.setText("Elasticity Description");
		GridData rd = new GridData(GridData.HORIZONTAL_ALIGN_FILL
				| GridData.VERTICAL_ALIGN_BEGINNING);
		rd.grabExcessHorizontalSpace = true;
		oeElas_section.setLayoutData(rd);
		oeElas_section.setLayout(new GridLayout(1, true));
		Composite com = toolkit.createComposite(oeElas_section, SWT.NONE);
		GridLayout ceDescLayout = new GridLayout(4, false);
		com.setLayout(ceDescLayout);
		toolkit.createLabel(com, "Minimum", SWT.NONE);
		oeMinimumText = toolkit.createText(com, "", SWT.SINGLE | SWT.BORDER);
		toolkit.createLabel(com, "Maximum", SWT.NONE);
		oeMaximumText = toolkit.createText(com, "", SWT.SINGLE | SWT.BORDER);
		oeElas_section.setClient(com);
		oeElas_section.setExpanded(true);
		oeElas_section.setExpanded(false);
	}

	/**
	 * Create the Method/Service specific section of a core element
	 * @param composite Core element description composite
	 */
	private void createCESpecificDescription(Composite composite) {
		ceSpecificSection = toolkit.createSection(composite, Section.TWISTIE);
		ceSpecificSection.setText("Specific Description");
		GridData rd = new GridData(GridData.HORIZONTAL_ALIGN_FILL
				| GridData.VERTICAL_ALIGN_BEGINNING);
		rd.grabExcessHorizontalSpace = true;
		ceSpecificSection.setLayoutData(rd);
		ceSpecificSection.setLayout(new GridLayout(1, true));
		ceSpecificComposite = toolkit.createComposite(ceSpecificSection,
				SWT.NONE);
		ceSpecificSection.setClient(ceSpecificComposite);
		ceSpecificSection.setExpanded(true);
		ceSpecificSection.setExpanded(false);
	}

	/**
	 * Create the general section of a core element
	 * @param composite Core element description composite
	 */
	private void createCEGeneralDescription(Composite composite) {
		Section section = toolkit.createSection(composite, Section.TWISTIE);
		section.setText("General Description");
		section.setDescription("Define the general parameters of a Core Element");
		GridData rd = new GridData(GridData.HORIZONTAL_ALIGN_FILL
				| GridData.VERTICAL_ALIGN_BEGINNING);
		rd.grabExcessHorizontalSpace = true;
		section.setLayoutData(rd);
		section.setLayout(new GridLayout(1, true));
		Composite ceDesc = toolkit.createComposite(section, SWT.NONE);
		GridLayout ceDescLayout = new GridLayout();
		ceDescLayout.numColumns = 2;
		ceDesc.setLayout(ceDescLayout);
		toolkit.createLabel(ceDesc, "Name", SWT.NONE);
		ceNameText = toolkit.createText(ceDesc, "", SWT.SINGLE | SWT.BORDER);
		rd = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		rd.grabExcessHorizontalSpace = true;
		ceNameText.setLayoutData(rd);
		ceNameText.setEditable(false);
		/*
		 * Label ceModifierLabel = toolkit.createLabel(ceDesc, "Modifier",
		 * SWT.NONE); Composite ceModifiers = toolkit.createComposite (ceDesc,
		 * SWT.NONE); GridLayout oeModifiersLayout = new GridLayout ();
		 * oeModifiersLayout.numColumns=2;
		 * ceModifiers.setLayout(oeModifiersLayout); ceStatic =
		 * toolkit.createButton(ceModifiers, "static", SWT.CHECK); rd = new
		 * GridData(GridData.HORIZONTAL_ALIGN_FILL);
		 * rd.grabExcessHorizontalSpace = true; ceStatic.setLayoutData(rd);
		 * ceFinal = toolkit.createButton(ceModifiers, "final", SWT.CHECK); rd =
		 * new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		 * rd.grabExcessHorizontalSpace = true; ceFinal.setLayoutData(rd); rd =
		 * new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		 * rd.grabExcessHorizontalSpace = true; ceModifiers.setLayoutData(rd);
		 */
		toolkit.createLabel(ceDesc, "Return Type", SWT.NONE);
		Composite ceReturn = toolkit.createComposite(ceDesc);
		rd = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		rd.grabExcessHorizontalSpace = true;
		rd.horizontalIndent = 0;
		rd.horizontalSpan = 0;
		ceReturn.setLayoutData(rd);
		GridLayout ceReturnLayout = new GridLayout();
		ceReturnLayout.numColumns = 2;
		ceReturnLayout.marginLeft = 0;
		ceReturnLayout.marginRight = 0;
		ceReturn.setLayout(ceReturnLayout);
		ceReturnTypeText = toolkit.createText(ceReturn, "", SWT.SINGLE
				| SWT.BORDER);
		rd = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		rd.grabExcessHorizontalSpace = true;
		ceReturnTypeText.setLayoutData(rd);
		ceReturnTypeText.setEditable(false);
		Button selectReturnButton = toolkit.createButton(ceReturn, "Select...",
				SWT.NONE);
		selectReturnButton.setEnabled(false);
		toolkit.createLabel(ceDesc, "Parameters", SWT.NONE);
		Composite cePar = toolkit.createComposite(ceDesc, SWT.NONE);
		rd = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		rd.grabExcessHorizontalSpace = true;
		rd.horizontalIndent = 0;
		cePar.setLayoutData(rd);
		GridLayout tableLayout = new GridLayout();
		tableLayout.numColumns = 2;
		tableLayout.marginLeft = 0;
		tableLayout.marginRight = 0;
		tableLayout.marginWidth = 0;
		cePar.setLayout(tableLayout);
		ceParametersTable = toolkit.createTable(cePar, SWT.SINGLE | SWT.BORDER
				| SWT.FULL_SELECTION | SWT.FILL | SWT.V_SCROLL | SWT.H_SCROLL);
		ceParametersTable.setHeaderVisible(true);
		ceParametersTable.setLinesVisible(true);
		TableColumn ceParamType = new TableColumn(ceParametersTable, SWT.FILL);
		ceParamType.setText("Type");
		ceParamType.setAlignment(SWT.FILL);
		ceParamType.pack();
		TableColumn ceParamName = new TableColumn(ceParametersTable, SWT.FILL);
		ceParamName.setText("Name");
		ceParamName.setAlignment(SWT.FILL);
		ceParamName.pack();
		TableColumn ceDirectionType = new TableColumn(ceParametersTable,
				SWT.FILL);
		ceDirectionType.setText("Direction");
		ceDirectionType.setAlignment(SWT.FILL);
		ceDirectionType.pack();
		rd = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		rd.grabExcessHorizontalSpace = true;
		rd.grabExcessVerticalSpace = true;
		rd.minimumHeight = 90;
		ceParametersTable.setLayoutData(rd);
		Composite parButtons = toolkit.createComposite(cePar, SWT.NONE);
		tableLayout = new GridLayout();
		tableLayout.numColumns = 1;
		tableLayout.marginLeft = 0;
		tableLayout.marginRight = 0;
		parButtons.setLayout(tableLayout);
		/*
		 * ceAddParButton = toolkit.createButton(parButtons ,"Add...",
		 * SWT.NONE); rd = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		 * rd.grabExcessHorizontalSpace = true;
		 * ceAddParButton.setLayoutData(rd);
		 */
		ceModifyParButton = toolkit.createButton(parButtons, "Modify...",
				SWT.NONE);
		ceModifyParButton.setEnabled(false);
		rd = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		rd.grabExcessHorizontalSpace = true;
		ceModifyParButton.setLayoutData(rd);
		/*
		 * ceDeleteParButton = toolkit.createButton(parButtons ,"Delete",
		 * SWT.NONE); ceDeleteParButton.setEnabled(false); rd = new
		 * GridData(GridData.HORIZONTAL_ALIGN_FILL);
		 * rd.grabExcessHorizontalSpace = true;
		 * ceDeleteParButton.setLayoutData(rd);
		 */
		toolkit.createLabel(ceDesc, "Constraints", SWT.NONE);
		Composite oeCons = toolkit.createComposite(ceDesc, SWT.NONE);
		rd = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		rd.grabExcessHorizontalSpace = true;
		rd.horizontalIndent = 0;
		oeCons.setLayoutData(rd);

		tableLayout = new GridLayout();
		tableLayout.numColumns = 2;
		tableLayout.marginLeft = 0;
		tableLayout.marginRight = 0;
		tableLayout.marginWidth = 0;
		oeCons.setLayout(tableLayout);
		ceConstraintsTable = toolkit.createTable(oeCons, SWT.SINGLE
				| SWT.BORDER | SWT.FULL_SELECTION);
		ceConstraintsTable.setHeaderVisible(true);
		ceConstraintsTable.setLinesVisible(true);
		TableColumn ceConstName = new TableColumn(ceConstraintsTable, SWT.NULL);
		ceConstName.setText("Name");
		ceConstName.pack();
		TableColumn ceConstValue = new TableColumn(ceConstraintsTable, SWT.NULL);
		ceConstValue.setText("Value");
		ceConstValue.pack();
		rd = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		rd.grabExcessHorizontalSpace = true;
		rd.grabExcessVerticalSpace = true;
		rd.minimumHeight = 90;
		ceConstraintsTable.setLayoutData(rd);
		Composite consButtons = toolkit.createComposite(oeCons, SWT.NONE);
		tableLayout = new GridLayout();
		tableLayout.numColumns = 1;
		tableLayout.marginLeft = 0;
		tableLayout.marginRight = 0;
		consButtons.setLayout(tableLayout);
		ceAddConButton = toolkit.createButton(consButtons, "Add...", SWT.NONE);
		rd = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		rd.grabExcessHorizontalSpace = true;
		ceAddConButton.setLayoutData(rd);
		ceModifyConButton = toolkit.createButton(consButtons, "Modify...",
				SWT.NONE);
		ceModifyConButton.setEnabled(false);
		rd = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		rd.grabExcessHorizontalSpace = true;
		ceModifyConButton.setLayoutData(rd);
		ceDeleteConButton = toolkit.createButton(consButtons, "Delete",
				SWT.NONE);
		ceDeleteConButton.setEnabled(false);
		rd = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		rd.grabExcessHorizontalSpace = true;
		ceDeleteConButton.setLayoutData(rd);

		section.setClient(ceDesc);
		section.setExpanded(true);
	}

	/**
	 * Create the orchestration element description section
	 */
	private void createOEDescriptionSection() {
		oeDescSection = toolkit.createSection(form.getBody(),
				Section.DESCRIPTION | Section.TITLE_BAR | Section.TWISTIE
						| Section.EXPANDED);
		oeDescSection.setText("Orchestration Element Description"); //$NON-NLS-1$
		oeDescSection
				.setDescription("This section provides the interface to manage the selected orchestration element ");
		GridData rd = new GridData(GridData.HORIZONTAL_ALIGN_FILL
				| GridData.VERTICAL_ALIGN_BEGINNING);
		rd.grabExcessHorizontalSpace = true;
		rd.minimumWidth = 300;
		rd.widthHint = (form.getSize().y) / 2;
		oeDescSection.setLayoutData(rd);
		Composite oeDesc = toolkit.createComposite(oeDescSection, SWT.NONE);
		GridLayout oeDescLayout = new GridLayout();
		oeDescLayout.numColumns = 1;
		oeDesc.setLayout(oeDescLayout);
		createOEGeneralDescription(oeDesc);
		createOESpecificDescription(oeDesc);
		createOEElasticityBounds(oeDesc);
		createOEDependenciesSection(oeDesc);

		oeDescSection.setClient(oeDesc);
		oeDescSection.setExpanded(false);
		oeDescSection.setEnabled(false);

	}

	/**
	 * Create the orchestration element specific description section
	 * @param composite Orchestration element composite
	 */
	private void createOESpecificDescription(Composite composite) {

		Section section = toolkit.createSection(composite, Section.TWISTIE);
		section.setText("Specific Description");
		section.setDescription("Define the specific parameters of an Orchestration Element");
		GridData rd = new GridData(GridData.HORIZONTAL_ALIGN_FILL
				| GridData.VERTICAL_ALIGN_BEGINNING);
		rd.grabExcessHorizontalSpace = true;
		section.setLayoutData(rd);
		section.setLayout(new GridLayout(1, true));
		Composite oeDesc = toolkit.createComposite(section, SWT.NONE);
		rd = new GridData(GridData.HORIZONTAL_ALIGN_FILL
				| GridData.VERTICAL_ALIGN_BEGINNING);
		rd.grabExcessHorizontalSpace = true;
		oeDesc.setLayoutData(rd);
		oeDesc.setLayout(new GridLayout(1, true));
		isPartOfServiceItfButton = toolkit.createButton(oeDesc,
				"Deployed in the Service Interface", SWT.CHECK);
		isPartOfServiceItfButton.setEnabled(false);

		section.setClient(oeDesc);
		section.setExpanded(true);
		section.setExpanded(false);
	}

	/**
	 * Create the general description section of an orchestration element
	 * @param composite Orchestration element description composite 
	 */
	private void createOEGeneralDescription(Composite composite) {

		Section section = toolkit.createSection(composite, Section.TWISTIE);
		section.setText("General Description");
		section.setDescription("Define the general parameters of an Orchestration Element");
		GridData rd = new GridData(GridData.HORIZONTAL_ALIGN_FILL
				| GridData.VERTICAL_ALIGN_BEGINNING);
		rd.grabExcessHorizontalSpace = true;
		section.setLayoutData(rd);
		section.setLayout(new GridLayout(1, true));
		Composite oeDesc = toolkit.createComposite(section, SWT.NONE);
		rd = new GridData(GridData.HORIZONTAL_ALIGN_FILL
				| GridData.VERTICAL_ALIGN_BEGINNING);
		rd.grabExcessHorizontalSpace = true;
		oeDesc.setLayoutData(rd);
		oeDesc.setLayout(new GridLayout(2, false));
		toolkit.createLabel(oeDesc, "Name", SWT.NONE);
		oeNameText = toolkit.createText(oeDesc, "", SWT.SINGLE | SWT.BORDER);
		rd = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		rd.grabExcessHorizontalSpace = true;
		oeNameText.setLayoutData(rd);
		oeNameText.setEditable(false);

		/*
		 * Label oeModifierLabel = toolkit.createLabel(oeDesc, "Modifier",
		 * SWT.NONE); Composite oeModifiers = toolkit.createComposite (oeDesc,
		 * SWT.NONE); GridLayout oeModifiersLayout = new GridLayout ();
		 * oeModifiersLayout.numColumns=2;
		 * oeModifiers.setLayout(oeModifiersLayout); oePrivate =
		 * toolkit.createButton(oeModifiers, "private", SWT.CHECK); rd = new
		 * GridData(GridData.HORIZONTAL_ALIGN_FILL);
		 * rd.grabExcessHorizontalSpace = true; oePrivate.setLayoutData(rd);
		 * //TODO:Change set enabled and add listener
		 * oePrivate.setEnabled(false);
		 * 
		 * oePublic = toolkit.createButton(oeModifiers, "public", SWT.CHECK); rd
		 * = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		 * rd.grabExcessHorizontalSpace = true; oePublic.setLayoutData(rd);
		 * //TODO:Change set enabled and add listener
		 * oePublic.setEnabled(false);
		 * 
		 * oeStatic = toolkit.createButton(oeModifiers, "static", SWT.CHECK); rd
		 * = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		 * rd.grabExcessHorizontalSpace = true; oeStatic.setLayoutData(rd);
		 * //TODO:Change set enabled and add listener
		 * oeStatic.setEnabled(false);
		 * 
		 * oeFinal = toolkit.createButton(oeModifiers, "final", SWT.CHECK); rd =
		 * new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		 * rd.grabExcessHorizontalSpace = true; oeFinal.setLayoutData(rd);
		 * //TODO:Change set enabled and add listener oeFinal.setEnabled(false);
		 * 
		 * rd = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		 * rd.grabExcessHorizontalSpace = true; oeModifiers.setLayoutData(rd);
		 */

		toolkit.createLabel(oeDesc, "Return Type", SWT.NONE);
		Composite oeReturn = toolkit.createComposite(oeDesc);
		rd = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		rd.grabExcessHorizontalSpace = true;
		rd.horizontalIndent = 0;
		rd.horizontalSpan = 0;
		oeReturn.setLayoutData(rd);
		GridLayout oeReturnLayout = new GridLayout();
		oeReturnLayout.numColumns = 2;
		oeReturnLayout.marginLeft = 0;
		oeReturnLayout.marginRight = 0;
		oeReturn.setLayout(oeReturnLayout);
		oeReturnTypeText = toolkit.createText(oeReturn, "", SWT.SINGLE
				| SWT.BORDER);
		rd = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		rd.grabExcessHorizontalSpace = true;
		oeReturnTypeText.setLayoutData(rd);
		oeReturnTypeText.setEditable(false);
		Button selectReturnButton = toolkit.createButton(oeReturn, "Select...",
				SWT.NONE);
		selectReturnButton.setEnabled(false);

		toolkit.createLabel(oeDesc, "Parameters", SWT.NONE);
		oeParametersTable = toolkit.createTable(oeDesc, SWT.SINGLE | SWT.BORDER
				| SWT.FULL_SELECTION | SWT.FILL | SWT.V_SCROLL | SWT.H_SCROLL);
		oeParametersTable.setHeaderVisible(true);
		oeParametersTable.setLinesVisible(true);
		TableColumn oeParamType = new TableColumn(oeParametersTable, SWT.FILL);
		oeParamType.setText("Type");
		oeParamType.setAlignment(SWT.FILL);
		oeParamType.pack();
		TableColumn oeParamName = new TableColumn(oeParametersTable, SWT.FILL);
		oeParamName.setText("Name");
		oeParamName.setAlignment(SWT.FILL);
		oeParamName.pack();
		rd = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		rd.grabExcessHorizontalSpace = true;
		rd.grabExcessVerticalSpace = true;
		rd.minimumHeight = 70;
		oeParametersTable.setLayoutData(rd);

		toolkit.createLabel(oeDesc, "Constraints", SWT.NONE);
		Composite oeCons = toolkit.createComposite(oeDesc, SWT.NONE);
		rd = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		rd.grabExcessHorizontalSpace = true;
		rd.horizontalIndent = 0;
		oeCons.setLayoutData(rd);

		GridLayout tableLayout = new GridLayout();
		tableLayout.numColumns = 2;
		tableLayout.marginLeft = 0;
		tableLayout.marginRight = 0;
		tableLayout.marginWidth = 0;
		oeCons.setLayout(tableLayout);
		oeConstraintsTable = toolkit.createTable(oeCons, SWT.SINGLE
				| SWT.BORDER | SWT.FULL_SELECTION);
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
		rd.minimumHeight = 70;
		oeConstraintsTable.setLayoutData(rd);
		Composite consButtons = toolkit.createComposite(oeCons, SWT.NONE);
		tableLayout = new GridLayout();
		tableLayout.numColumns = 1;
		tableLayout.marginLeft = 0;
		tableLayout.marginRight = 0;
		consButtons.setLayout(tableLayout);
		oeAddConButton = toolkit.createButton(consButtons, "Add...", SWT.NONE);
		rd = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		rd.grabExcessHorizontalSpace = true;
		oeAddConButton.setLayoutData(rd);
		oeModifyConButton = toolkit.createButton(consButtons, "Modify...",
				SWT.NONE);
		oeModifyConButton.setEnabled(false);
		rd = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		rd.grabExcessHorizontalSpace = true;
		oeModifyConButton.setLayoutData(rd);
		oeDeleteConButton = toolkit.createButton(consButtons, "Delete",
				SWT.NONE);
		oeDeleteConButton.setEnabled(false);
		rd = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		rd.grabExcessHorizontalSpace = true;
		oeDeleteConButton.setLayoutData(rd);

		section.setClient(oeDesc);
		section.setExpanded(true);
	}

	/**
	 * Create the section to display the core element of an orchestration class
	 */
	private void createCESection() {
		Section ceSection = toolkit.createSection(form.getBody(),
				Section.DESCRIPTION | Section.TITLE_BAR | Section.TWISTIE
						| Section.EXPANDED);
		ceSection.setText("Core Elements");
		ceSection
				.setDescription("This section provides the interface to manage the core elements of the selected class ");
		GridData rd = new GridData(GridData.HORIZONTAL_ALIGN_FILL
				| GridData.VERTICAL_ALIGN_BEGINNING );
		rd.grabExcessHorizontalSpace = true;
		rd.minimumWidth = 300;
		rd.widthHint = 500;
		rd.heightHint = 200;
		ceSection.setLayoutData(rd);
		Composite ceComp = toolkit.createComposite(ceSection, SWT.NONE);
		GridLayout ceLayout = new GridLayout(2, false);
		ceComp.setLayout(ceLayout);
		ceList = new List(ceComp, SWT.READ_ONLY | SWT.MULTI | SWT.V_SCROLL
				| SWT.H_SCROLL | SWT.BORDER);
		rd = new GridData(GridData.HORIZONTAL_ALIGN_FILL
				| GridData.VERTICAL_ALIGN_BEGINNING);
		rd.grabExcessHorizontalSpace = true;
		rd.heightHint = 120;
		rd.minimumWidth = 300;
		rd.widthHint = 430;
		ceList.setLayoutData(rd);
		newCEButton = toolkit.createButton(ceComp, "New...", SWT.NONE);
		rd = new GridData(GridData.HORIZONTAL_ALIGN_FILL
				| GridData.VERTICAL_ALIGN_BEGINNING);
		newCEButton.setLayoutData(rd);
		newCEButton.setEnabled(false);
		newCEButton.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {
				addCE();
			}

			@Override
			public void widgetSelected(SelectionEvent arg0) {
				addCE();
			}
		});
		ceSection.setClient(ceComp);

	}

	/**
	 * Open the wizard to add a new core element
	 */
	protected void addCE() {
		
		try {
			ProjectMetadata prMetadata = ((ServiceFormEditor)getEditor()).getProjectMetadata();
			int index = serviceClassList.getSelectionIndex();
			ICompilationUnit cu_class = CommonFormPage.getCEInterface(serviceClassList.getItem(index),
				((ServiceFormEditor) getEditor()).getProject(), prMetadata);
			NewElementWizard wizard= null;
			if (isExternalClass){
				wizard = new ServiceSsImportCoreElementWizard();
				wizard.init(getEditor().getSite().getWorkbenchWindow().getWorkbench(),
						new StructuredSelection(cu_class));
				OrchestrationClass oc = prMetadata.getOrchestrationClass(serviceClassList.getItem(index));
				if (oc!= null){
					((ServiceSsImportCoreElementWizard)wizard).setExternalClass(oc);
				}else{
					throw (new Exception("External Class is null"));
				}
			}else{
				wizard = new ServiceSsNewCoreElementWizard();
				wizard.init(getEditor().getSite().getWorkbenchWindow().getWorkbench(),
						new StructuredSelection(cu_class));
			}
			WizardDialog dialog = new WizardDialog(this.getEditor().getSite()
				.getShell(), (IWizard)wizard);
			if (dialog.open() == WizardDialog.OK) {
			
				initData();
				serviceClassList.select(index);
				serviceClassSelected();
				String[] classes = ceList.getItems();
				for (int i = 0; i < classes.length; i++) {
					if (classes[i].equals(wizard.getCreatedElement()
							.getElementName())) {
						ceList.select(i);
						ceSelected();
					}
				}
			}
			} catch (PartInitException e) {
				log.error("Error adding Core Element", e);
				ErrorDialog.openError(this.getEditor().getSite().getShell(),
						"Error updating editor data", e.getMessage(), e.getStatus());
			} catch (JavaModelException e) {
				log.error("Error adding Core Element", e);
				ErrorDialog.openError(this.getEditor().getSite().getShell(),
						"Error getting Core Element interface", e.getMessage(), e.getStatus());
			} catch (SAXException e) {
				log.error("Error adding Core Element", e);
				ErrorDialog.openError(this.getEditor().getSite().getShell(),
						"Error getting Core Element interface", e.getMessage(), 
						new Status(IStatus.ERROR,Activator.PLUGIN_ID, e.getMessage(),e));
			} catch (IOException e) {
				log.error("Error adding Core Element", e);
				ErrorDialog.openError(this.getEditor().getSite().getShell(),
						"Error getting Core Element interface", e.getMessage(),
						new Status(IStatus.ERROR,Activator.PLUGIN_ID, e.getMessage(),e));
			} catch (ParserConfigurationException e) {
				log.error("Error adding Core Element", e);
				ErrorDialog.openError(this.getEditor().getSite().getShell(),
						"Error getting Core Element interface", e.getMessage(), 
						new Status(IStatus.ERROR,Activator.PLUGIN_ID, e.getMessage(),e));
			} catch (Exception e) {
				log.error("Error adding Core Element", e);
				ErrorDialog.openError(this.getEditor().getSite().getShell(),
						"Error getting Core Element interface", e.getMessage(), 
						new Status(IStatus.ERROR,Activator.PLUGIN_ID, e.getMessage(),e));
			}

	}

	/**
	 * Create the section to visualize the orchestration elements bound to an orchestration class
	 */
	private void createOESection() {
		Section oeSection = toolkit.createSection(form.getBody(),
				Section.DESCRIPTION | Section.TITLE_BAR | Section.TWISTIE
						| Section.EXPANDED);
		oeSection.setText("Orchestration Elements");
		oeSection
				.setDescription("This section provides the interface to manage the orchestration elements of the selected class ");
		GridData rd = new GridData(GridData.HORIZONTAL_ALIGN_FILL
				| GridData.VERTICAL_ALIGN_BEGINNING);
		rd.grabExcessHorizontalSpace = true;
		rd.minimumWidth = 300;
		rd.widthHint = 500;
		rd.heightHint = 200;
		oeSection.setLayoutData(rd);
		Composite oeComp = toolkit.createComposite(oeSection, SWT.NORMAL);
		GridLayout oeLayout = new GridLayout(2, false);
		oeComp.setLayout(oeLayout);
		oeList = new List(oeComp, SWT.READ_ONLY | SWT.MULTI | SWT.V_SCROLL
				| SWT.H_SCROLL | SWT.BORDER);
		rd = new GridData(GridData.HORIZONTAL_ALIGN_FILL
				| GridData.VERTICAL_ALIGN_BEGINNING);
		rd.grabExcessHorizontalSpace = true;
		rd.heightHint = 120;
		rd.minimumWidth = 300;
		rd.widthHint = 430;
		oeList.setLayoutData(rd);
		newOEButton = toolkit.createButton(oeComp, "New...", SWT.NONE);
		rd = new GridData(GridData.HORIZONTAL_ALIGN_FILL
				| GridData.VERTICAL_ALIGN_BEGINNING);
		newOEButton.setLayoutData(rd);
		newOEButton.setEnabled(false);
		newOEButton.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {
				addOE();
			}

			@Override
			public void widgetSelected(SelectionEvent arg0) {
				addOE();
			}
		});
		oeSection.setClient(oeComp);

	}

	/**
	 * Open the wizard to create a new Orchestration Element 
	 */
	protected void addOE() {
		
		if (isExternalClass){
			int index = serviceClassList.getSelectionIndex();
			try {
				
				IType type = getExternalOrchestrationClass(serviceClassList.getItem(index), 
						((ServiceFormEditor) getEditor()).getProject(),
						((ServiceFormEditor) getEditor()).getProjectMetadata());
				if(type!= null){
					SelectMethodFromExternalClassDialog dialog = new SelectMethodFromExternalClassDialog(
							this.getEditor().getSite().getShell(), type);
					if (dialog.open() == Window.OK ) {
						generateExternalOrchestrationElement(dialog,((ServiceFormEditor) getEditor()).getProjectMetadata());
						updateOrchestrationClassPart(dialog.getMethod().getElementName(), index);			
					
					}
				}
					
				
			} catch (Exception e) {
				log.error("Error updating external orchestration class", e);
				ErrorDialog.openError(this.getEditor().getSite().getShell(),
					"Error updating external orchestration class", e.getMessage(), 
					new Status(IStatus.ERROR,Activator.PLUGIN_ID, e.getMessage(),e));
				
			}
		}else{
			try {
				ServiceSsNewOrchestrationElementWizard wizard = new ServiceSsNewOrchestrationElementWizard();
				int index = serviceClassList.getSelectionIndex();
			
				IPackageFragment frag = ((ServiceFormEditor) getEditor())
						.getMainPackage();
				ICompilationUnit cu_class = getOrchestrationClass(serviceClassList.getItem(index),
					((ServiceFormEditor) getEditor()).getProject(),
					((ServiceFormEditor) getEditor()).getProjectMetadata()); 
				wizard.init(getEditor().getSite().getWorkbenchWindow().getWorkbench(),
						new StructuredSelection(cu_class));

				WizardDialog dialog = new WizardDialog(this.getEditor().getSite()
						.getShell(), wizard);
				if (dialog.open() == WizardDialog.OK) {
					updateOrchestrationClassPart(wizard.getCreatedElement()
							.getElementName(), index);			
				
				}
			} catch (Exception e) {
				log.error("Error updating orchestration class", e);
				ErrorDialog.openError(this.getEditor().getSite().getShell(),
					"Error updating orchestration class", e.getMessage(), new Status(IStatus.ERROR, 
							Activator.PLUGIN_ID, e.getMessage(),e));
				
			} 
		}
	}
	

	private void generateExternalOrchestrationElement(
			SelectMethodFromExternalClassDialog dialog,
			ProjectMetadata projectMetadata) throws Exception {
		OrchestrationElement oe = dialog.getOrchestrationElement();
		projectMetadata.addOEtoOrchestrationClass(oe.getServiceClass(), oe);
		projectMetadata.toFile(((ServiceFormEditor)this.getEditor()).getMetadataFile().getRawLocation().toFile());
		
	}

	private void updateOrchestrationClassPart(String elementName, int index) throws PartInitException {
		initData();
		serviceClassList.select(index);
		serviceClassSelected();
		String[] classes = oeList.getItems();
		for (int i = 0; i < classes.length; i++) {
			if (classes[i].equals(elementName)) {
					oeList.select(i);
					oeSelected();
			}
		}
	}

	/**
	 * Create the section to manage the runtime installation
	 */
	private void createRuntimeSection() {
		Section runtimeSection = toolkit.createSection(form.getBody(),
				Section.DESCRIPTION | Section.TITLE_BAR);
		runtimeSection.setText("Runtime"); //$NON-NLS-1$
		runtimeSection
				.setDescription("This section provides the interface to manage the Runtime location ");
		GridData rd = new GridData(GridData.HORIZONTAL_ALIGN_FILL
				| GridData.VERTICAL_ALIGN_BEGINNING);
		rd.grabExcessHorizontalSpace = true;
		rd.minimumWidth = 300;
		rd.widthHint = 500;
		rd.heightHint = 80;
		runtimeSection.setLayoutData(rd);
		Composite firstRow2 = toolkit.createComposite(runtimeSection,
				SWT.NO_REDRAW_RESIZE);
		GridLayout firstRow2Layout = new GridLayout();
		firstRow2Layout.numColumns = 3;
		firstRow2.setLayout(firstRow2Layout);
		firstRow2.setLayoutData(rd);
		toolkit.createLabel(firstRow2, "Location", SWT.NONE);
		runtimetext = toolkit
				.createText(firstRow2, "", SWT.SINGLE | SWT.BORDER);
		runtimetext.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent arg0) {
				updateRuntimeLocation();
			}
		});
		rd = new GridData(GridData.VERTICAL_ALIGN_BEGINNING);
		rd.grabExcessHorizontalSpace = true;
		rd.minimumWidth = 300;
		rd.widthHint = 430;
		runtimetext.setLayoutData(rd);
		Button changeLocationButton = toolkit.createButton(firstRow2,
				"Change...", SWT.NORMAL);
		changeLocationButton.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {
				changeRuntimeLocation();
			}

			@Override
			public void widgetSelected(SelectionEvent arg0) {
				changeRuntimeLocation();
			}
		});
		runtimeSection.setClient(firstRow2);
	}

	/**
	 * Update the runtime location modifiying the dependency to the runtime libraries
	 */
	protected void updateRuntimeLocation() {
		if (previousRuntimeLocation != null
				&& !previousRuntimeLocation
						.equals(runtimetext.getText().trim())) {
			IFile f = ((ServiceFormEditor) getEditor()).getMetadataFile();
			try {
				ProjectMetadata pr_meta = new ProjectMetadata(f
						.getRawLocation().toFile());
				pr_meta.removeDependency(previousRuntimeLocation
						+ ProjectMetadata.ITJAR_EXT);
				pr_meta.setRuntimeLocation(runtimetext.getText().trim());
				pr_meta.addDependency(runtimetext.getText().trim()
						+ ProjectMetadata.ITJAR_EXT,
						ProjectMetadata.JAR_DEP_TYPE);
				pr_meta.toFile(f.getRawLocation().toFile());
				final IJavaProject project = ((ServiceFormEditor) getEditor())
						.getProject();

				final ProgressMonitorDialog mon = new ProgressMonitorDialog(
						getEditor().getSite().getShell());
				mon.run(false, false, new IRunnableWithProgress() {
					public void run(IProgressMonitor monitor) {
						try {
							project.getProject().refreshLocal(
									Resource.DEPTH_INFINITE, monitor);
						} catch (CoreException e) {
							ErrorDialog.openError(mon.getShell(),
									"Error refreshing the classpath",
									e.getMessage(), e.getStatus());
							e.printStackTrace();
						}
					}
				});

			} catch (Exception e) {
				ErrorDialog.openError(this.getEditor().getSite().getShell(),
							"Error adding runtime to the classpath",
							e.getMessage(), new Status(IStatus.ERROR, 
									Activator.PLUGIN_ID, e.getMessage(),e));
				e.printStackTrace();
			}
		}
		previousRuntimeLocation = runtimetext.getText().trim();
	}

	/**
	 * Open the dialog to change the runtime installation location
	 */
	protected void changeRuntimeLocation() {
		final DirectoryDialog dialog = new DirectoryDialog(this.getEditor()
				.getSite().getShell());
		dialog.setMessage("Select Runtime Folder");
		String directoryName = runtimetext.getText().trim();
		if (directoryName.length() > 0) {
			final File path = new File(directoryName);
			if (path.exists())
				dialog.setFilterPath(directoryName);
		}
		final String selectedDirectory = dialog.open();
		if (selectedDirectory != null) {
			runtimetext.setText(selectedDirectory);
		}

	}

	/**
	 * Create the section to visualize the orchestration classes of a service
	 */
	private void createServiceClassSection() {
		Section serviceClassSection = toolkit.createSection(form.getBody(),
				Section.DESCRIPTION | Section.TITLE_BAR);
		serviceClassSection.setText("Service Classes"); //$NON-NLS-1$
		serviceClassSection
				.setDescription("This section provides the interface to manage Service Classes ");
		GridData rd = new GridData(GridData.HORIZONTAL_ALIGN_FILL
				| GridData.VERTICAL_ALIGN_BEGINNING);
		rd.grabExcessHorizontalSpace = true;
		rd.minimumWidth = 300;
		rd.widthHint = (form.getSize().x) / 2;
		rd.heightHint = 90;
		serviceClassSection.setLayoutData(rd);
		Composite firstRow1 = toolkit.createComposite(serviceClassSection,
				SWT.NONE);
		GridLayout firstRow1Layout = new GridLayout();
		firstRow1Layout.numColumns = 5;
		firstRow1.setLayout(firstRow1Layout);
		rd = new GridData();
		firstRow1.setLayoutData(rd);
		Label l = toolkit
				.createLabel(firstRow1, "Service Class", SWT.BEGINNING);
		rd = new GridData(GridData.VERTICAL_ALIGN_CENTER);
		l.setLayoutData(rd);
		serviceClassList = new Combo(firstRow1, SWT.READ_ONLY | SWT.BORDER
				| SWT.DEFAULT);
		rd = new GridData(GridData.FILL_HORIZONTAL
				| GridData.VERTICAL_ALIGN_BEGINNING);
		rd.grabExcessHorizontalSpace = true;
		rd.minimumWidth = 300;
		rd.widthHint = 370;
		serviceClassList.setLayoutData(rd);
		// Composite buttonRow = toolkit.createComposite(firstRow1, SWT.NONE);
		// buttonRow.setLayout(new GridLayout(2, true));
		Button newServiceButton = toolkit.createButton(firstRow1, "New...",
				SWT.NONE);
		rd = new GridData(GridData.VERTICAL_ALIGN_BEGINNING);
		newServiceButton.setLayoutData(rd);
		newServiceButton.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {
				createServiceClass();
			}

			@Override
			public void widgetSelected(SelectionEvent arg0) {
				createServiceClass();
			}
		});
		Button importServiceButton = toolkit.createButton(firstRow1, "Import...",
				SWT.NONE);
		rd = new GridData(GridData.VERTICAL_ALIGN_BEGINNING);
		importServiceButton.setLayoutData(rd);
		importServiceButton.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {
				importServiceClass();
			}

			@Override
			public void widgetSelected(SelectionEvent arg0) {
				importServiceClass();
			}
		});
		deleteServiceButton = toolkit.createButton(firstRow1, "Delete",
				SWT.NONE);
		deleteServiceButton.setEnabled(false);
		rd = new GridData(GridData.VERTICAL_ALIGN_BEGINNING);
		deleteServiceButton.setLayoutData(rd);
		deleteServiceButton.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {
				deleteServiceClass(serviceClassList.getSelectionIndex());
			}

			@Override
			public void widgetSelected(SelectionEvent arg0) {
				deleteServiceClass(serviceClassList.getSelectionIndex());
			}
		});

		serviceClassSection.setClient(firstRow1);

	}

	protected void importServiceClass() {
		ServiceSsImportOrchestrationClassWizard wizard = new ServiceSsImportOrchestrationClassWizard(
				false, ((ServiceFormEditor) getEditor()).getProject());
		wizard.init(
				getEditor().getSite().getWorkbenchWindow().getWorkbench(),
				new StructuredSelection(((ServiceFormEditor) getEditor())
						.getMainPackage()));
		WizardDialog dialog = new WizardDialog(this.getEditor().getSite()
				.getShell(), wizard);

		if (dialog.open() == WizardDialog.OK) {
			try {
				updateEditorData(wizard);
				
			} catch (PartInitException e) {
				log.error("Error updating editor data after importing class", e);
				ErrorDialog.openError(this.getEditor().getSite().getShell(),
						"Error updating editor data after importing class", e.getMessage(), e.getStatus());
			}
		}
		
	}

	/**
	 * Delete an orchestration class
	 * @param index Index of the selected Orchestration class 
	 */
	protected void deleteServiceClass(int index) {

		if (MessageDialog.openQuestion(this.getEditor().getSite().getShell(),
				"Service Class Delete", " Delete service class "
						+ serviceClassList.getItem(index) + ".\nAre you sure?")) {
			IPackageFragment frag = ((ServiceFormEditor) getEditor())
					.getMainPackage();
			final ICompilationUnit cu_class = frag.getCompilationUnit(Signature
					.getSimpleName(serviceClassList.getItem(index)) + ".java");
			final ICompilationUnit cu_itf = frag.getCompilationUnit(Signature
					.getSimpleName(serviceClassList.getItem(index))
					+ "Itf.java");
			final ProgressMonitorDialog mon = new ProgressMonitorDialog(
					getEditor().getSite().getShell());
			try {
				deleteClassFromMetadataFile(serviceClassList.getItem(index));
				mon.run(false, false, new IRunnableWithProgress() {
					public void run(IProgressMonitor monitor) {
						try {
							cu_class.delete(true, monitor);
							cu_itf.delete(true, monitor);

						} catch (JavaModelException e) {
							log.error("Error deleting service class", e);
							ErrorDialog.openError(mon.getShell(),
									"Error deleting service class",
									e.getMessage(), e.getStatus());
						}
					}
				});
				initData();
			} catch (InvocationTargetException e) {
				log.error("Error deleting service class", e);
				ErrorDialog.openError(mon.getShell(),
						"Error deleting service class", e.getMessage(),
						new Status(IStatus.ERROR, Activator.PLUGIN_ID, e.getMessage(),e));
			} catch (InterruptedException e) {
				log.error("Error deleting service class", e);
				ErrorDialog.openError(mon.getShell(),
						"Error deleting service class", e.getMessage(),
						new Status(IStatus.ERROR, Activator.PLUGIN_ID, e.getMessage(),e));
			} catch (ConfigurationException e) {
				log.error("Error deleting service class", e);
				ErrorDialog.openError(mon.getShell(),
						"Error deleting service class", e.getMessage(),
						new Status(IStatus.ERROR, Activator.PLUGIN_ID, e.getMessage(),e));
			} catch (PartInitException e) {
				log.error("Error deleting service class", e);
				ErrorDialog.openError(mon.getShell(),
						"Error deleting service class", e.getMessage(), e.getStatus());
			}
		}

	}

	/**
	 * Delete a orchestration class from the project metadata 
	 * @param classToDelete Orchestration class to be deleted
	 * @throws ConfigurationException
	 */
	protected void deleteClassFromMetadataFile(String classToDelete)
			throws ConfigurationException {
		IFile f = ((ServiceFormEditor) getEditor()).getMetadataFile();
		PropertiesConfiguration config = new PropertiesConfiguration(f
				.getRawLocation().toOSString());
		String[] classes = config.getStringArray("service.class");
		config.clearProperty("service.class");
		for (String c : classes) {
			if (!c.equals(classToDelete)) {
				config.addProperty("service.class", c);
			}
		}
		config.save();
	}

	/**
	 * Open the wizard to create a new orchestration class
	 */
	protected void createServiceClass() {
		ServiceSsNewServiceClassWizard wizard = new ServiceSsNewServiceClassWizard(
				false);
		wizard.init(
				getEditor().getSite().getWorkbenchWindow().getWorkbench(),
				new StructuredSelection(((ServiceFormEditor) getEditor())
						.getMainPackage()));

		WizardDialog dialog = new WizardDialog(this.getEditor().getSite()
				.getShell(), wizard);
		if (dialog.open() == WizardDialog.OK) {
			try {
				updateEditorData(wizard);
				
			} catch (PartInitException e) {
				log.error("Error updating editor data", e);
				ErrorDialog.openError(this.getEditor().getSite().getShell(),
						"Error updating editor data", e.getMessage(), e.getStatus());
			}
		}
	}

	private void updateEditorData(IServiceSsOrchestrationClassWizard wizard) throws PartInitException {
		initData();
		String[] classes = serviceClassList.getItems();
		log.info("Generated Class: "+ wizard.getFullyQualifiedDomainName());
		for (int i = 0; i < classes.length; i++) {
			log.debug("Current class: " + classes[i]);
			if (classes[i].equals(wizard.getFullyQualifiedDomainName())){
				log.debug("They are the same class");
				serviceClassList.select(i);
				serviceClassSelected();
			}
		}
	}

	/**
	 * Initialize the information of the implementation page 
	 * @throws PartInitException
	 */
	public void initData() throws PartInitException {
		IFile f = ((ServiceFormEditor) getEditor()).getMetadataFile();
		try {
			ProjectMetadata pr_meta = new ProjectMetadata(f.getRawLocation()
					.toFile());
			if (pr_meta != null) {
				String[] classes = pr_meta.getAllOrchestrationClasses();
				if (classes != null) {
					selectedClass = -1;
					selectedOE = -1;
					selectedCE = -1;
					serviceClassList.setItems(classes);

					String location = pr_meta.getRuntimeLocation();
					if (location != null) {
						runtimetext.setText(location);
						previousRuntimeLocation = location;
					} else
						throw (new PartInitException(
								"runtime location not found"));
					// Init OESection
					oeList.removeAll();
					newOEButton.setEnabled(false);
					// Init CESection
					ceList.removeAll();
					newCEButton.setEnabled(false);
					removeOEDesc();
					removeCEDesc();
				} else
					throw (new PartInitException(
							"Service classes not defined in metadata class"));

			} else
				throw (new PartInitException(
						"Configuration Properties manager is null"));
		} catch (Exception e) {
			throw (new PartInitException(
					"Error loading configuration properties", e));
		}
	}
	
	/**
	 * Refresh the information of the implementation page 
	 * @throws PartInitException
	 */
	/**
	 * @throws PartInitException
	 */
	public void refreshData() throws PartInitException {
		IFile f = ((ServiceFormEditor) getEditor()).getMetadataFile();
		String currentClass = null;
		String currentCE = null;
		String currentOE = null;
		String[] currentCEList = ceList.getItems();
		String[] currentOEList = oeList.getItems();
		if (selectedClass >= 0)
			currentClass = serviceClassList.getItem(selectedClass);
		if (selectedCE >= 0)
			currentCE = ceList.getItem(selectedCE);
		if (selectedOE >= 0)
			currentOE = oeList.getItem(selectedOE);
		try {
			ProjectMetadata pr_meta = new ProjectMetadata(f.getRawLocation()
					.toFile());

			if (pr_meta != null) {
				String location = pr_meta.getRuntimeLocation();
				if (location != null) {
					runtimetext.setText(location);
					previousRuntimeLocation = location;
				} else
					throw (new PartInitException(
								"runtime location not found"));
				String[] classes = pr_meta.getAllOrchestrationClasses();
				if (classes != null) {
					if (currentClass != null){
						serviceClassList.setItems(classes);
						int item = getItemFromArray(currentClass, classes);
						if (item >=0) {
							serviceClassList.select(item);
							selectedClass=item;
							reloadClassElements();
							if (hasElementInterfaceChanges(currentOEList,oeList.getItems())){
								MessageDialog.openWarning(this.getSite().getShell(),
										"Change in Orchestration elements definition" ,
										"There has been an update in a Orchetration element definition. " +
										"If you have modified an element declaration, " +
										"dependencies of the modified elements have been removed");
							}else
								log.debug(" No Orchestration Element interface changes");
							if (hasElementInterfaceChanges(currentCEList,ceList.getItems())){
								MessageDialog.openWarning(this.getSite().getShell(),
										"Change in Core Element Interface definition" ,
										"There has been an update in a Core Element interfaces definition. " +
										"If you have modified an element declaration, " +
										"dependencies and elasticity section of the mofified elements have been removed");
							}else
								log.debug(" No Core Element interface changes");
							if (currentCE != null){
								//Treat CEs
								int itemCE = getItemFromArray(currentCE,ceList.getItems());
								if (itemCE >= 0){
									ceList.select(itemCE);
									selectedCE = itemCE;
									loadSelectedCEDesc();
								}else{
									selectedCE = -1;
									removeCEDesc();
								}
							}else{
								selectedCE = -1;
								removeCEDesc();
							}
							
							if (currentOE != null){
								//Treat OEs
								int itemOE = getItemFromArray(currentOE,oeList.getItems());
								if (itemOE>=0){
									oeList.select(itemOE);
									selectedOE = itemOE;
									loadSelectedOEDesc();
								}else{
									selectedOE = -1;
									removeOEDesc();
								}
							}else{
								selectedOE = -1;
								removeOEDesc();
							}
							
						}else{
							log.debug("Old selected service class not found");
							MessageDialog.openWarning(this.getSite().getShell(),
									"Class not found" ,
									"The class selected by the editor has been removed.");
							serviceClassList.setText("");
							selectedClass=-1;
							oeList.removeAll();
							newOEButton.setEnabled(false);
							// Init CESection
							ceList.removeAll();
							newCEButton.setEnabled(false);
							removeOEDesc();
							removeCEDesc();
						}
					}else{
						log.debug("No service class was selected");
						// Init OESection
						oeList.removeAll();
						newOEButton.setEnabled(false);
						removeOEDesc();
						// Init CESection
						ceList.removeAll();
						newCEButton.setEnabled(false);
						removeCEDesc();
					}
				} else
					throw (new PartInitException(
							"Service classes not defined in metadata class"));

			} else
				throw (new PartInitException(
						"Configuration Properties manager is null"));
		} catch (Exception e) {
			throw (new PartInitException(
					"Error refreshing data", e));
		}
	}


	/** Get the item number of the string in an array of string
	 * @param current Current text to search
	 * @param array Group of string
	 * @return Item number
	 */
	private int getItemFromArray(String current, String[] array) {
		for (int i=0; i<array.length; i++)
			if (current.equals(array[i])){
				return i;
			}
		return -1;
	}

	/**
	 * Remove the core element description
	 */
	private void removeCEDesc() {
		//TODO remove the core element from the CEI
		ceDescSection.setExpanded(false);
		ceDescSection.setEnabled(false);
	}

	/**
	 * Remove an orchestration element
	 */
	private void removeOEDesc() {
		//TODO remove the @orchestration tag in the Orchestration class
		oeDescSection.setExpanded(false);
		oeDescSection.setEnabled(false);
	}

	/**
	 * Delete an element constraint
	 * @param serviceClass Orchestration class name
	 * @param methodName Element method name
	 * @param constraintName Element constraint name
	 * @param constraintValue Element constraint value
	 * @param isCE flag to indicate is the lement is a core element
	 * @throws Exception 
	 */
	private void deleteConstraint(String serviceClass, String methodName, String label,
			String constraintName, String constraintValue, boolean isCE) throws Exception {
		ICompilationUnit cu;
		if (isCE) {
			cu = getCEInterface(serviceClass, ((ServiceFormEditor)getEditor()).getProject(), ((ServiceFormEditor)getEditor()).getProjectMetadata());
			deleteConstraintInCompilationUnit(cu, methodName, constraintName, constraintValue, 
					Signature.getSimpleName(serviceClass)+ "Itf");
		} else {
			if (isExternalClass){
				ProjectMetadata prMeta = ((ServiceFormEditor)getEditor()).getProjectMetadata();
				prMeta.deleteConstraint(serviceClass, label, constraintName);
				prMeta.toFile(((ServiceFormEditor)getEditor()).getMetadataFile().getRawLocation().toFile());
				
			}else{
				cu = getOrchestrationClass(serviceClass,((ServiceFormEditor)getEditor()).getProject(), 
					((ServiceFormEditor)getEditor()).getProjectMetadata());
				deleteConstraintInCompilationUnit(cu, methodName, constraintName, constraintValue, 
						Signature.getSimpleName(serviceClass));
				
			}
		}
	}
	private void deleteConstraintInCompilationUnit(ICompilationUnit cu,
			String methodName, String constraintName, String constraintValue, 
			String localtypeName) throws JavaModelException, MalformedTreeException, BadLocationException {
		if (cu != null) {
			Document document = new Document(cu.getSource());
			log.debug(document.get());
			ASTParser parser = ASTParser.newParser(AST.JLS3);
			parser.setSource(cu);
			CompilationUnit result = (CompilationUnit) parser.createAST(null);
			if (result != null) {
				result.recordModifications();
				log.debug(result.toString());
				AST ast = result.getAST();
				java.util.List<AbstractTypeDeclaration> types = result.types();
				log.debug("pack: " + result.getPackage().toString()
						+ " types: " + result.types().size());
				if (result.types().size() > 0) {
					boolean find = false;
					for (AbstractTypeDeclaration type : types) {
						log.debug("Type: " + type.getName().getIdentifier() + "("
								+ type.getName().getFullyQualifiedName() + ")");
						if (type.getName().getIdentifier().equals(localtypeName)) {
							MethodDeclaration[] methods = ((TypeDeclaration) type)
									.getMethods();
							for (MethodDeclaration m : methods) {
								log.debug("method FQDN: "+ m.getName().getFullyQualifiedName()
										+ " identifier: "+ m.getName().getIdentifier());
								if (m.getName().getIdentifier()	.equals(methodName)) {
									java.util.List<IExtendedModifier> mods = m
											.modifiers();
									for (IExtendedModifier mod : mods) {
										log.debug("modifier: "+ mod.getClass().toString());
										if (mod.isAnnotation()) {
											if (((Annotation) mod)
													.isNormalAnnotation()) {
												log.debug("annotation: "
														+ ((NormalAnnotation) mod)
															.getTypeName().toString());
												if (((NormalAnnotation) mod).getTypeName()
														.toString().equals("Constraints")) {
													java.util.List<MemberValuePair> vals = ((NormalAnnotation) mod)
															.values();
													MemberValuePair value = null;
													for (MemberValuePair v : vals) {
														log.debug("member: "+ v.getName()
																.getIdentifier());
														if (v.getName().getIdentifier()
																.equals(constraintName)) {
															vals.remove(v);
															find = true;
															break;
														}
													}
													if (find)
														break;
												}
											}
										}
									}
									if (find)
										break;
								}
							}
							if (find)
								break;
						}
					}
					if (find) {
						TextEdit edits = result.rewrite(document, cu.getJavaProject().getOptions(true));
						edits.apply(document);
						String newSource = document.get();
						cu.getBuffer().setContents(newSource);
						cu.save(null, true);
						log.debug("writting modifications "	+ newSource);
					} else {
						log.warn("Constraint annotation not found");
					}

				} else {
					log.warn("No types found in the Compilation unit from AST");
				}

			} else {
				log.error("Error parsing Compilation unit with AST");
			}
		} else {
			log.error("Error getting Interface Compilation Unit");
		}
	}

	/**
	 * Modify a element constraint
	 * @param serviceClass Orchestration element class
	 * @param methodName Service element method name
	 * @param constraintName  Element constraint name
	 * @param constraintValue Element constraint value
	 * @param isCE Flag to indicati if element is a core element
	 * @throws Exception 
	 */
	private void modifyConstraint(String serviceClass, String methodName, String label,
			String constraintName, String constraintValue, boolean isCE) throws Exception {
		ICompilationUnit cu;
		if (isCE) {
			cu = getCEInterface(serviceClass,((ServiceFormEditor)getEditor()).getProject(),
					((ServiceFormEditor)getEditor()).getProjectMetadata());
			modifyConstraintInCompilationUnit(cu, methodName, constraintName, constraintValue, 
					Signature.getSimpleName(serviceClass)+ "Itf");
		} else {
			if (isExternalClass){
				ProjectMetadata prMeta = ((ServiceFormEditor)getEditor()).getProjectMetadata();
				prMeta.modifyConstraint(serviceClass, label, constraintName, constraintValue);
				prMeta.toFile(((ServiceFormEditor)getEditor()).getMetadataFile().getRawLocation().toFile());
				
			}else{
				cu = getOrchestrationClass(serviceClass, ((ServiceFormEditor)getEditor()).getProject(),
					((ServiceFormEditor)getEditor()).getProjectMetadata());
				modifyConstraintInCompilationUnit(cu, methodName, constraintName, constraintValue, 
						Signature.getSimpleName(serviceClass));
			}
		}
		
		
		
	}

	private void modifyConstraintInCompilationUnit(ICompilationUnit cu,
			String methodName, String constraintName, String constraintValue, 
			String localtypeName) throws JavaModelException, MalformedTreeException, BadLocationException {
		if (cu != null) {
			Document document = new Document(cu.getSource());
			// IDocument document = textFileBuffer.getDocument();
			log.debug("Loading document for modify constraint "
					+ constraintName + " in method " + methodName);
			ASTParser parser = ASTParser.newParser(AST.JLS3); // handles JDK
																// 1.0, 1.1,
																// 1.2, 1.3,
																// 1.4, 1.5, 1.6
			parser.setSource(cu);
			// In order to parse 1.5 code, some compiler options need to be set
			// to 1.5
			// Map options = JavaCore.getOptions();
			// JavaCore.setComplianceOptions(JavaCore.VERSION_1_6, options);
			// parser.setCompilerOptions(options);
			CompilationUnit result = (CompilationUnit) parser.createAST(null);
			if (result != null) {
				result.recordModifications();
				AST ast = result.getAST();
				java.util.List<AbstractTypeDeclaration> types = result.types();
				log.debug("pack: " + result.getPackage().toString()
						+ " types: " + result.types().size());
				if (result.types().size() > 0) {
					boolean find = false;
					for (AbstractTypeDeclaration type : types) {
						log.debug("Type: " + type.getName().getIdentifier() + "("
								+ type.getName().getFullyQualifiedName() + ")");
						if (type.getName().getIdentifier()
								.equals(localtypeName)) {
							MethodDeclaration[] methods = ((TypeDeclaration) type)
									.getMethods();
							for (MethodDeclaration m : methods) {
								log.debug("method FQDN: " + m.getName().getFullyQualifiedName()
										+ " identifier: " + m.getName().getIdentifier());
								if (m.getName().getIdentifier().equals(methodName)) {
									java.util.List<IExtendedModifier> mods = m.modifiers();
									for (IExtendedModifier mod : mods) {
										log.debug("modifier: "	+ mod.getClass().toString());
										if (mod.isAnnotation()) {
											if (((Annotation) mod).isNormalAnnotation()) {
												log.debug("annotation: " + ((NormalAnnotation) mod)
																.getTypeName().toString());
												if (((NormalAnnotation) mod).getTypeName()
														.toString().equals("Constraints")) {
													java.util.List<MemberValuePair> vals = ((NormalAnnotation) mod)
															.values();
													MemberValuePair value = null;
													for (MemberValuePair v : vals) {
														log.debug("member: "+ v.getName().getIdentifier());
														if (v.getName().getIdentifier()
																.equals(constraintName)) {
															value = v;
															break;
														}
													}
													Expression qn = ConstraintsUtils
															.convertValueToExpression(constraintName,
																	constraintValue, ast);
													if (value == null) {
														value = ast.newMemberValuePair();
														value.setName(ast.newSimpleName(constraintName));
														value.setValue(qn);
														log.debug("Adding property to annotation: "
																		+ value.toString());
														vals.add(value);
													} else {
														value.setValue(qn);
														log.debug("Changing direction: " + value.toString());
													}
													find = true;
													break;
												}
											}
										}
									}
									if (find)
										break;
									else {
										ImportDeclaration imp = ast.newImportDeclaration();
										imp.setName(ast.newName("integratedtoolkit.types.annotations.Constraints"));
										if (!result.imports().contains(imp))
											result.imports().add(imp);
										NormalAnnotation annotation = ast.newNormalAnnotation();
										annotation.setTypeName(ast.newSimpleName("Constraints"));
										java.util.List<MemberValuePair> vals = annotation.values();
										MemberValuePair value = ast.newMemberValuePair();
										value.setName(ast.newSimpleName(constraintName));
										value.setValue(ConstraintsUtils.convertValueToExpression(constraintName,
														constraintValue, ast));
										log.debug("Adding property to annotation: "	+ value.toString());
										vals.add(value);
										m.modifiers().add(0, annotation);
										find = true;
									}
								}
							}
							if (find)
								break;
						}

					}
					if (find) {

						TextEdit edits = result.rewrite(document, cu
								.getJavaProject().getOptions(true));
						edits.apply(document);
						String newSource = document.get();
						cu.getBuffer().setContents(newSource);
						cu.save(null, true);
						log.debug("writting modifications "	+ newSource);

					} else {
						log.warn("Varaible and annotation not found");
					}

				} else {
					log.warn("No types found in the Compilation unit from AST");
				}

			} else {
				log.error("Error parsing Compilation unit with AST");
			}
		} else {
			log.error("Error getting Interface Compilation Unit");
		}		
	}

	/**
	 * Modify parameter element
	 * @param serviceClass Orchestration class name
	 * @param methodName Element method name
	 * @param p Parameter
	 * @throws PartInitException
	 * @throws JavaModelException
	 * @throws MalformedTreeException
	 * @throws BadLocationException
	 * @throws SAXException
	 * @throws IOException
	 * @throws ParserConfigurationException
	 */
	private void modifyDirection(String serviceClass, String methodName,
			CoreElementParameter p) throws PartInitException,
			JavaModelException, MalformedTreeException, BadLocationException, 
			SAXException, IOException, ParserConfigurationException {
		log.debug("Modifying direction for core element"
				+ serviceClass + "." + methodName + " parameter " + p.getName()
				+ "(" + p.getDirection() + ")");
		ICompilationUnit cu = getCEInterface(serviceClass,((ServiceFormEditor)getEditor())
				.getProject(), ((ServiceFormEditor)getEditor()).getProjectMetadata());
		if (cu != null) {
			Document document = new Document(cu.getSource());
			log.debug(document.get());
			String localtypeName = serviceClass.subSequence(
					serviceClass.lastIndexOf(".") + 1, serviceClass.length())
					+ "Itf";
			ASTParser parser = ASTParser.newParser(AST.JLS3); // handles JDK
																// 1.0, 1.1,
																// 1.2, 1.3,
																// 1.4, 1.5, 1.6
			parser.setSource(cu);
			CompilationUnit result = (CompilationUnit) parser.createAST(null);
			if (result != null) {
				result.recordModifications();
				log.debug(result.toString());
				AST ast = result.getAST();
				java.util.List<AbstractTypeDeclaration> types = result.types();
				log.debug("pack: " + result.getPackage().toString()
						+ " types: " + result.types().size());
				if (result.types().size() > 0) {
					boolean find = false;
					for (AbstractTypeDeclaration type : types) {
						log.debug("Type: " + type.getName().getIdentifier() + "("
								+ type.getName().getFullyQualifiedName() + ")");
						if (type.getName().getIdentifier().equals(localtypeName)) {
							MethodDeclaration[] methods = ((TypeDeclaration) type)
									.getMethods();
							for (MethodDeclaration m : methods) {
								log.debug("method FQDN: " + m.getName().getFullyQualifiedName()
										+ " identifier: " + m.getName().getIdentifier());
								if (m.getName().getIdentifier().equals(methodName)) {
									java.util.List<SingleVariableDeclaration> pars = m
											.parameters();
									for (SingleVariableDeclaration var : pars) {
										log.debug("var FQDN: "	+ var.getName().getFullyQualifiedName()
												+ " identifier: " + var.getName().getIdentifier());
										if (var.getName().getIdentifier().equals(p.getName())) {
											java.util.List<IExtendedModifier> mods = var.modifiers();
											for (IExtendedModifier mod : mods) {
												log.debug("modifier: "	+ mod.getClass().toString());
												if (mod.isAnnotation()) {
													if (((Annotation) mod).isNormalAnnotation()) {
														log.debug("annotation: "+ ((NormalAnnotation) mod)
																	.getTypeName().toString());
														if (((NormalAnnotation) mod).getTypeName()
																.toString().equals("Parameter")) {
															java.util.List<MemberValuePair> vals = ((NormalAnnotation) mod)
																	.values();
															MemberValuePair dir_value = null;
															for (MemberValuePair v : vals) {
																log.debug("member: "+ v.getName()
																			.getIdentifier());
																if (v.getName().getIdentifier()
																		.equals("direction")) {
																	dir_value = v;
																	break;
																}
															}

															if (dir_value == null) {
																dir_value = ast.newMemberValuePair();
																dir_value.setName(ast
																			.newSimpleName("direction"));
																QualifiedName qn = ast.newQualifiedName(
																		ast.newSimpleName("Direction"),
																		ast.newSimpleName(p.getDirection()));
																dir_value.setValue(qn);
																log.debug("Adding property to annotation: "
																				+ dir_value.toString());
																vals.add(dir_value);
															} else {
																QualifiedName ex = (QualifiedName) dir_value
																		.getValue();
																log.debug("ValueClass: "+ ex.getClass());
																ex.setName(ast.newSimpleName(p
																			.getDirection()));
																log.debug("Changing direction: "
																			+ dir_value.toString());
															}
															find = true;
															break;
														}
													}
												}
											}
											if (find)
												break;
										}
									}
									if (find)
										break;
								}
							}
							if (find)
								break;
						}
					}
					if (find) {
						TextEdit edits = result.rewrite(document, cu.getJavaProject().getOptions(true));
						edits.apply(document);
						String newSource = document.get();
						cu.getBuffer().setContents(newSource);
						cu.save(null, true);
						log.debug("writting modifications "	+ newSource);
					} else {
						log.warn("Varaible and annotation not found");
					}

				} else {
					log.warn("No types found in the Compilation unit from AST");
				}
			} else {
				log.error("Error parsing Compilation unit with AST");
			}
		} else {
			log.error("Error getting Interface Compilation Unit");
		}
	}
}