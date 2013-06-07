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

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.swing.SpringLayout.Constraints;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.xml.sax.SAXException;

import es.bsc.servicess.ide.Activator;
import es.bsc.servicess.ide.ConstraintsUtils;
import es.bsc.servicess.ide.Logger;
import es.bsc.servicess.ide.PackagingUtils;
import es.bsc.servicess.ide.ProjectMetadata;
import es.bsc.servicess.ide.dialogs.ModifyParameterDialog;
import es.bsc.servicess.ide.dialogs.PackageDialog;
import es.bsc.servicess.ide.editors.deployers.GridDeployer;
import es.bsc.servicess.ide.editors.deployers.LocalhostDeployer;
import es.bsc.servicess.ide.editors.deployers.OptimisDeployer;
import es.bsc.servicess.ide.model.ServiceElement;

/**
 * Editor page for building and deploying the service
 * @author Jorge Ejarque (Barcelona Supercomputing Center)
 *
 */
public class BuildingDeploymentFormPage extends CommonFormPage {

	private ScrolledForm form;
	private FormToolkit toolkit;
	private Button manualButton;
	private Button automaticButton;
	private Combo servicePackagesList;
	private List elList;
	private List compList;
	private HashMap<String, Deployer> deploymentCases;
	private Combo deploymentTypeList;
	private String[] deploymentTypesNames;
	private Deployer selectedDeploymentOption = null;
	private String previous_type;
	private ExpandableComposite deploymentOptions;
	private Composite deploymentComposite;
	private Composite options;
	private ExpandableComposite ec;
	private Button removeButton;
	private Button addButton;
	private HashMap<String, ServiceElement> constraintsElements;
	private Text typeList;
	private Button deleteButton;
	private Composite packageComposite;
	private boolean redoingPackages= false;
	private static Logger log = Logger.getLogger(BuildingDeploymentFormPage.class);
	
	/** Constructor
	 * @param editor Service Editor
	 */
	public BuildingDeploymentFormPage(FormEditor editor) {
		super(editor, "build.deploy", "Build and Deploy");

	}

	/**
	 * Initialize page data
	 */
	public void initData() {
		initDeployement();
		if (initManual(null)) {
			expandManual();
		}
	}
	
	/**
	 * Update data
	 * @throws PartInitException
	 */
	public void refreshData() throws PartInitException {
		if (constraintsElements != null) {
			try{
				ProjectMetadata pr_meta = ((ServiceFormEditor) getEditor()).getProjectMetadata();
				
				IJavaProject project = ((ServiceFormEditor) getEditor()).getProject();
				
				String[] orch_cls = pr_meta.getAllOrchestrationClasses();
				constraintsElements = getElements(orch_cls,
						ProjectMetadata.BOTH_TYPE, project, pr_meta);
				String[] packs = pr_meta.getPackages();
				if (packs!= null && packs.length>0){
					if (hasPackagedElementsChanged(pr_meta)){
						if (!redoingPackages){
							if (!MessageDialog.openQuestion(getSite().getShell(), "Changes in Core Element interfaces", 
									"An update in the Core Element interface has been produced which could " +
									"invalidate current packages. Do you want to keep current packages?")){
								pr_meta.removeAllPackages();
							}
						}
					}
				}
			}catch (Exception e){
				throw (new PartInitException(
							"Error loading configuration properties", e));
			}
		}
	}

	/**
	 * Checks if a elements of the packages are in a list of core elements
	 * @param pr_meta Project metadata for extracting the packages and elements per package
	 * @return True if there are elements which does not exists
	 */
	private boolean hasPackagedElementsChanged(ProjectMetadata pr_meta) {
		String[] packs = pr_meta.getPackages();
		if (packs!= null && packs.length>0){
			for (String p:packs){
				String[] els = pr_meta.getElementsInPackage(p);
				for(String e:els){
					if (!constraintsElements.containsKey(e)){
						log.debug("Element " + e + "in package "+ p +" not found.");
						return true;
					}
				}
			}
		}
		return false;
	}

	/**
	 * Initialize deployment types
	 */
	public void initDeployement() {
		// TODO: Get Deployment Cases from preferences pages
		deploymentCases = new HashMap<String, Deployer>();
		deploymentCases.put("Localhost", (Deployer) new LocalhostDeployer(
				(ServiceFormEditor) getEditor(), this.getEditorSite()
						.getWorkbenchWindow(), this));
		deploymentCases.put("Optimis Cloud", (Deployer) new OptimisDeployer(
				(ServiceFormEditor) getEditor(), this.getEditorSite()
						.getWorkbenchWindow(), this));
		deploymentCases.put("Grid", (Deployer) new GridDeployer(
				(ServiceFormEditor) getEditor(), this.getEditorSite()
						.getWorkbenchWindow(), this));
		deploymentTypesNames = deploymentCases.keySet().toArray(
				new String[deploymentCases.keySet().size()]);
		deploymentTypeList.setItems(deploymentTypesNames);

	}

	/**
	 * Initialize manual mode of packaging
	 * @param selectedpack selected package
	 * @return True is no error, false if error
	 */
	public boolean initManual(String selectedpack) {
		try {
			ProjectMetadata pr_meta = new ProjectMetadata(
					((ServiceFormEditor) getEditor()).getMetadataFile()
							.getRawLocation().toFile());
			String[] packs = pr_meta.getPackages();
			if (packs != null && packs.length > 0) {
				initManual(pr_meta, packs, selectedpack);
				return true;
			} else
				return false;
		} catch (Exception e) {
			log.error("Error updating manual composite");
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * Initialize the manual mode
	 * 
	 * @param pr_meta Project metadata
	 * @param packs Existing packages
	 * @param selectedpack Selected packages
	 * @throws TransformerException
	 * @throws IOException
	 */
	public void initManual(ProjectMetadata pr_meta, String[] packs,
			String selectedpack) throws TransformerException, IOException {
		servicePackagesList.setItems(packs);
		if (selectedpack != null && selectedpack.length() > 0) {
			servicePackagesList.setText(selectedpack);
			updatePackage(selectedpack, pr_meta);
		} else {
			setNoPackage();
		}
	}

	/**
	 * Update package
	 * 
	 * @param selectedpack Selected package name
	 * @param pr_meta Project Metadata
	 * @throws TransformerException
	 * @throws IOException
	 */
	private void updatePackage(String selectedpack, ProjectMetadata pr_meta)
			throws TransformerException, IOException {
		if (pr_meta.existsPackage(selectedpack)) {
			deleteButton.setEnabled(true);
			String type = pr_meta.getPackageType(selectedpack);
			typeList.setText(type);
			if (type.equals(ProjectMetadata.SER_CORE_PACK_TYPE)){
				type=ProjectMetadata.SERVICE_TYPE;
			}else if (type.equals(ProjectMetadata.METH_CORE_PACK_TYPE)){
				type=ProjectMetadata.METHOD_TYPE;
			}else if (type.equals(ProjectMetadata.ORCH_PACK_TYPE)){
				type=ProjectMetadata.ORCH_TYPE;
			}else if (type.equals(ProjectMetadata.ALL_PACK_TYPE)){
				type=ProjectMetadata.BOTH_TYPE;
			}else
				type=ProjectMetadata.BOTH_TYPE;
			//TODO change constraints element have to set all elements
			HashMap<String, ServiceElement> constElements = CommonFormPage.getElements(
				pr_meta.getAllOrchestrationClasses(),type ,((ServiceFormEditor) getEditor()).getProject(), pr_meta);
			if (constElements != null)
				updateLists(pr_meta, selectedpack, constElements);
		} else {
			setNoPackage();
		}
	}

	/**
	 * Unselect packages
	 */
	private void setNoPackage() {
		servicePackagesList.setText("");
		elList.setItems(new String[0]);
		compList.setItems(new String[0]);
		addButton.setEnabled(false);
		removeButton.setEnabled(false);
		deleteButton.setEnabled(false);
		typeList.setText("");
		typeList.setEnabled(false);
	}

	/** 
	 * Update element lists
	 * @param pr_meta Project Metadata
	 * @param selectedpack Selected Package
	 * @param constElements 
	 */
	private void updateLists(ProjectMetadata pr_meta, String selectedpack, HashMap<String, ServiceElement> constElements) {
		String[] elementsInPackage = pr_meta.getElementsInPackage(selectedpack);
		compList.setItems(elementsInPackage);
		elList.setItems(getMissingElements(constElements, elementsInPackage));
	}

	/** 
	 * Get missing elements 
	 * @param constElements 
	 * @param elementsInPackage Current elements in package
	 * @return packages not selected
	 */
	private String[] getMissingElements(HashMap<String, ServiceElement> constElements, String[] elementsInPackage) {
		if (constElements != null && !constElements.isEmpty()) {
			Set<String> str = new HashSet<String>();
			str.addAll(constElements.keySet());
			for (String s : elementsInPackage) {
				str.remove(s);
			}
			return str.toArray(new String[str.size()]);
		} else {
			log.error("There are no elements to show");
			return new String[0];
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.forms.editor.FormPage#createFormContent(org.eclipse.ui.forms.IManagedForm)
	 */
	protected void createFormContent(IManagedForm managedForm) {
		form = managedForm.getForm();
		toolkit = managedForm.getToolkit();
		form.setText("Implementation Overview");
		GridLayout layout = new GridLayout(1, true);
		form.getBody().setLayout(layout);
		createElementsGroupingSection();
		createDeploymentSection();
		initData();

	}

	/**
	 * Create the section to group the service elements
	 */
	private void createElementsGroupingSection() {
		Section servicePackageSection = toolkit.createSection(form.getBody(),
				Section.DESCRIPTION | Section.TITLE_BAR);
		servicePackageSection.setText("Service Packages"); //$NON-NLS-1$
		servicePackageSection
				.setDescription("This section provides the interface to group Service Elements to build the service packages  ");
		GridData rd = new GridData(GridData.HORIZONTAL_ALIGN_FILL
				| GridData.VERTICAL_ALIGN_BEGINNING);
		rd.grabExcessHorizontalSpace = true;
		rd.minimumWidth = 500;
		rd.heightHint = 260;
		servicePackageSection.setLayoutData(rd);
		packageComposite = toolkit.createComposite(servicePackageSection,
				SWT.NONE);
		GridLayout firstRow1Layout = new GridLayout();
		firstRow1Layout.numColumns = 1;
		packageComposite.setLayout(firstRow1Layout);
		Composite firstRow = toolkit
				.createComposite(packageComposite, SWT.NONE);
		firstRow1Layout = new GridLayout();
		firstRow1Layout.numColumns = 3;
		firstRow.setLayout(firstRow1Layout);
		Label packageLabel = toolkit.createLabel(firstRow, "Packaging Mode",
				SWT.BEGINNING);
		manualButton = toolkit
				.createButton(firstRow, "Manual Mode.", SWT.CHECK);
		manualButton.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {
				initManual(servicePackagesList.getText());
				expandManual();
			}

			@Override
			public void widgetSelected(SelectionEvent arg0) {
				initManual(servicePackagesList.getText());
				expandManual();
			}
		});
		automaticButton = toolkit.createButton(firstRow, "Automatic Mode.",
				SWT.CHECK);
		// TODO TO be removed, capability is not currently supported
		automaticButton.setEnabled(true);
		automaticButton.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {
				manualButton.setSelection(false);
				automaticButton.setSelection(true);
				ec.setExpanded(false);
				ec.setEnabled(false);
				packageComposite.redraw();
				// form.reflow(true);
			}

			@Override
			public void widgetSelected(SelectionEvent arg0) {
				manualButton.setSelection(false);
				automaticButton.setSelection(true);
				ec.setExpanded(false);
				ec.setEnabled(false);
				packageComposite.redraw();
				// form.reflow(true);
			}
		});
		ec = toolkit.createExpandableComposite(packageComposite,
				ExpandableComposite.TREE_NODE
						| ExpandableComposite.CLIENT_INDENT);
		ec.setText("Manual Package creation");
		firstRow1Layout = new GridLayout();
		firstRow1Layout.numColumns = 1;
		ec.setLayout(firstRow1Layout);
		rd = new GridData(GridData.FILL_HORIZONTAL
				| GridData.VERTICAL_ALIGN_BEGINNING);
		rd.grabExcessHorizontalSpace = true;
		ec.setLayoutData(rd);
		Composite manual = toolkit.createComposite(ec, SWT.NONE);
		rd = new GridData(GridData.FILL_HORIZONTAL
				| GridData.VERTICAL_ALIGN_BEGINNING);
		rd.grabExcessHorizontalSpace = true;
		manual.setLayoutData(rd);
		firstRow1Layout = new GridLayout();
		firstRow1Layout.numColumns = 1;
		manual.setLayout(firstRow1Layout);
		Composite buttonRow = toolkit.createComposite(manual, SWT.NONE);
		rd = new GridData(GridData.VERTICAL_ALIGN_BEGINNING);
		// rd.grabExcessHorizontalSpace = true;
		buttonRow.setLayoutData(rd);
		firstRow1Layout = new GridLayout();
		firstRow1Layout.numColumns = 6;
		buttonRow.setLayout(firstRow1Layout);
		Label packLabel = toolkit.createLabel(buttonRow, "Package",
				SWT.BEGINNING);
		servicePackagesList = new Combo(buttonRow, SWT.READ_ONLY | SWT.BORDER
				| SWT.DEFAULT);
		rd = new GridData(GridData.VERTICAL_ALIGN_BEGINNING);
		rd.grabExcessHorizontalSpace = true;
		rd.minimumWidth = 350;
		servicePackagesList.setLayoutData(rd);
		servicePackagesList.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				selectPackage();
			}
		});
		Button newPackageButton = toolkit.createButton(buttonRow, "New...",
				SWT.NONE);
		newPackageButton.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {
				createPackage();
			}

			@Override
			public void widgetSelected(SelectionEvent arg0) {
				createPackage();
			}
		});
		deleteButton = toolkit.createButton(buttonRow, "Delete", SWT.NONE);
		deleteButton.setEnabled(false);
		deleteButton.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {
				deletePackage();
			}

			@Override
			public void widgetSelected(SelectionEvent arg0) {
				deletePackage();
			}
		});
		Label typeLabel = toolkit.createLabel(buttonRow,"Elements Type",SWT.BEGINNING);
		
		typeList = new Text (buttonRow, SWT.READ_ONLY | SWT.BORDER |SWT.NONE); 
		typeList.setText("");
		Composite listsRow = toolkit.createComposite(manual, SWT.NONE);
		rd = new GridData(GridData.FILL_HORIZONTAL
				| GridData.VERTICAL_ALIGN_BEGINNING);
		rd.grabExcessHorizontalSpace = true;
		listsRow.setLayoutData(rd);
		firstRow1Layout = new GridLayout();
		firstRow1Layout.numColumns = 4;
		listsRow.setLayout(firstRow1Layout);
		elList = new List(listsRow, SWT.READ_ONLY | SWT.MULTI | SWT.V_SCROLL
				| SWT.H_SCROLL | SWT.BORDER);
		elList.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				addButton.setEnabled(true);
			}
		});
		rd = new GridData(GridData.HORIZONTAL_ALIGN_FILL
				| GridData.VERTICAL_ALIGN_CENTER);
		rd.grabExcessHorizontalSpace = true;
		rd.grabExcessVerticalSpace = true;
		rd.heightHint = 120;
		elList.setLayoutData(rd);
		removeButton = toolkit.createButton(listsRow, "<", SWT.NONE);
		rd = new GridData(GridData.VERTICAL_ALIGN_CENTER);
		removeButton.setLayoutData(rd);
		removeButton.setEnabled(false);
		removeButton.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {
				removeElementFromPackage();
			}

			@Override
			public void widgetSelected(SelectionEvent arg0) {
				removeElementFromPackage();
			}
		});
		addButton = toolkit.createButton(listsRow, ">", SWT.NONE);
		rd = new GridData(GridData.VERTICAL_ALIGN_CENTER);
		addButton.setLayoutData(rd);
		addButton.setEnabled(false);
		addButton.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {
				addElementToPackage();
			}

			@Override
			public void widgetSelected(SelectionEvent arg0) {
				addElementToPackage();
			}
		});
		compList = new List(listsRow, SWT.READ_ONLY | SWT.MULTI | SWT.V_SCROLL
				| SWT.H_SCROLL | SWT.BORDER);
		compList.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				removeButton.setEnabled(true);
			}
		});
		rd = new GridData(GridData.HORIZONTAL_ALIGN_FILL
				| GridData.VERTICAL_ALIGN_CENTER);
		rd.grabExcessHorizontalSpace = true;
		rd.grabExcessVerticalSpace = true;
		rd.heightHint = 120;
		compList.setLayoutData(rd);
		ec.setClient(manual);
		ec.setExpanded(true);
		ec.setExpanded(false);
		Button generateButton = toolkit.createButton(packageComposite,
				"Generate", SWT.NONE);
		rd = new GridData(GridData.VERTICAL_ALIGN_CENTER);
		generateButton.setLayoutData(rd);
		generateButton.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {
				generate();
			}

			@Override
			public void widgetSelected(SelectionEvent arg0) {
				generate();
			}
		});
		servicePackageSection.setExpanded(true);
		servicePackageSection.setClient(packageComposite);

	}

	/**
	 * Expand the manual part
	 */
	protected void expandManual() {
		manualButton.setSelection(true);
		automaticButton.setSelection(false);
		ec.setExpanded(true);
		ec.setEnabled(true);
		form.reflow(true);
	}

	/**
	 * Actions when selecting a package
	 */
	protected void selectPackage() {
		try {
			ProjectMetadata pr_meta = new ProjectMetadata(
					((ServiceFormEditor) getEditor()).getMetadataFile()
							.getRawLocation().toFile());
			String selectedpack = servicePackagesList.getText().trim();
			updatePackage(selectedpack, pr_meta);
		} catch (Exception e) {
			ErrorDialog.openError(this.getEditor().getSite().getShell(),
					"Error selecting package", e.getMessage(), new Status(IStatus.ERROR, Activator.PLUGIN_ID, e.getMessage(),e));
			e.printStackTrace();
		}
	}

	/**
	 * Add an element in a package
	 */
	protected void addElementToPackage() {
		try {
			String pName = servicePackagesList.getItem(servicePackagesList
					.getSelectionIndex());
			String[] compNames = elList.getSelection();
			ProjectMetadata pr_meta = new ProjectMetadata(
					((ServiceFormEditor) getEditor()).getMetadataFile()
							.getRawLocation().toFile());
			for (String compName : compNames) {
				pr_meta.addElementToPackage(pName, compName);
			}
			pr_meta.toFile(((ServiceFormEditor) getEditor()).getMetadataFile()
					.getRawLocation().toFile());
			String type = typeList.getText();
			if (type.equals(ProjectMetadata.SER_CORE_PACK_TYPE)){
				type=ProjectMetadata.SERVICE_TYPE;
			}else if (type.equals(ProjectMetadata.METH_CORE_PACK_TYPE)){
				type=ProjectMetadata.METHOD_TYPE;
			}else if (type.equals(ProjectMetadata.ORCH_PACK_TYPE)){
				type=ProjectMetadata.ORCH_TYPE;
			}else if (type.equals(ProjectMetadata.ALL_PACK_TYPE)){
				type=ProjectMetadata.BOTH_TYPE;
			}else
				type=ProjectMetadata.BOTH_TYPE;
			//TODO Check if all Orchestration or only internal
			HashMap<String, ServiceElement> constElements = CommonFormPage.getElements(
				pr_meta.getAllOrchestrationClasses(),type ,((ServiceFormEditor) getEditor()).getProject(), pr_meta);
			if (constElements != null)
				updateLists(pr_meta, pName, constElements);
			else
				throw(new Exception("No elements for this type"));
		} catch (Exception e) {
			ErrorDialog.openError(this.getEditor().getSite().getShell(),
					"Error adding element to package", e.getMessage(), new Status(IStatus.ERROR, Activator.PLUGIN_ID, e.getMessage(),e));
			e.printStackTrace();
		}

	}

	/**
	 * Remove element in a package
	 */
	protected void removeElementFromPackage() {
		try {
			String pName = servicePackagesList.getItem(servicePackagesList
					.getSelectionIndex());
			String[] compNames = compList.getSelection();
			ProjectMetadata pr_meta = new ProjectMetadata(
					((ServiceFormEditor) getEditor()).getMetadataFile()
							.getRawLocation().toFile());
			for (String compName : compNames) {
				pr_meta.removeElementFromPackage(pName, compName);
			}
			pr_meta.toFile(((ServiceFormEditor) getEditor()).getMetadataFile()
					.getRawLocation().toFile());
			String type = typeList.getText();
			if (type.equals(ProjectMetadata.SER_CORE_PACK_TYPE)){
				type=ProjectMetadata.SERVICE_TYPE;
			}else if (type.equals(ProjectMetadata.METH_CORE_PACK_TYPE)){
				type=ProjectMetadata.METHOD_TYPE;
			}else if (type.equals(ProjectMetadata.ORCH_PACK_TYPE)){
				type=ProjectMetadata.ORCH_TYPE;
			}else if (type.equals(ProjectMetadata.ALL_PACK_TYPE)){
				type=ProjectMetadata.BOTH_TYPE;
			}else
				type=ProjectMetadata.BOTH_TYPE;
			//TODO Check if all orchestration or only internal
			HashMap<String, ServiceElement> constElements = CommonFormPage.getElements(
				pr_meta.getAllOrchestrationClasses(),type ,((ServiceFormEditor) getEditor()).getProject(), pr_meta);
			if (constElements != null)
				updateLists(pr_meta, pName, constElements);
			else
				throw(new Exception("No elements for this type"));
		} catch (Exception e) {
			ErrorDialog.openError(this.getEditor().getSite().getShell(),
					"Error deleting element from package", e.getMessage(), new Status(IStatus.ERROR, Activator.PLUGIN_ID, e.getMessage(),e));
			e.printStackTrace();
		}
	}

	/**
	 * Delete a package
	 */
	protected void deletePackage() {
		try {
			String pName = servicePackagesList.getItem(servicePackagesList
					.getSelectionIndex());
			ProjectMetadata pr_meta = new ProjectMetadata(
					((ServiceFormEditor) getEditor()).getMetadataFile()
							.getRawLocation().toFile());
			pr_meta.removePackage(pName);
			pr_meta.toFile(((ServiceFormEditor) getEditor()).getMetadataFile()
					.getRawLocation().toFile());
			servicePackagesList.setText("");
			initManual(pr_meta, pr_meta.getPackages(), null);
		} catch (Exception e) {
			ErrorDialog.openError(this.getEditor().getSite().getShell(),
					"Error deleting package", e.getMessage(), new Status(IStatus.ERROR, Activator.PLUGIN_ID, e.getMessage(),e));
			e.printStackTrace();
		}

	}

	/**
	 * Create a package
	 */
	protected void createPackage() {
		String pName;
		try {
			
			PackageDialog dialog = new PackageDialog(this.getEditorSite()
					.getShell(), ProjectMetadata.SUPPORTED_PACK_TYPES);
			if (dialog.open() == Window.OK) {
				pName = dialog.getPackageName();
				ProjectMetadata pr_meta = new ProjectMetadata(
						((ServiceFormEditor) getEditor()).getMetadataFile()
								.getRawLocation().toFile());
				pr_meta.addPackage(pName, dialog.getType());
				pr_meta.toFile(((ServiceFormEditor) getEditor())
						.getMetadataFile().getRawLocation().toFile());
				servicePackagesList.setText(pName);
				typeList.setText(dialog.getType());
				initManual(pr_meta, pr_meta.getPackages(), pName);
			}
		} catch (Exception e) {
			ErrorDialog.openError(this.getEditor().getSite().getShell(),
					"Error creating package", e.getMessage(),new Status(IStatus.ERROR, Activator.PLUGIN_ID, e.getMessage(),e));
			e.printStackTrace();
		}
	}

	/**
	 * Create Deployment section
	 */
	private void createDeploymentSection() {
		Section servicePackageSection = toolkit.createSection(form.getBody(),
				Section.DESCRIPTION | Section.TITLE_BAR);
		servicePackageSection.setText("Deployment");
		servicePackageSection
				.setDescription("This section provides the interface to deploy the service");
		GridData rd = new GridData(GridData.HORIZONTAL_ALIGN_FILL
				| GridData.VERTICAL_ALIGN_BEGINNING);
		rd.grabExcessHorizontalSpace = true;
		rd.minimumWidth = 500;
		rd.widthHint = (form.getSize().x);
		servicePackageSection.setLayoutData(rd);
		deploymentComposite = toolkit.createComposite(servicePackageSection,
				SWT.NONE);
		GridLayout firstRow1Layout = new GridLayout();
		firstRow1Layout.numColumns = 1;
		deploymentComposite.setLayout(firstRow1Layout);
		Composite buttonRow = toolkit.createComposite(deploymentComposite,
				SWT.NONE);
		rd = new GridData(GridData.VERTICAL_ALIGN_BEGINNING);
		// rd.grabExcessHorizontalSpace = true;
		buttonRow.setLayoutData(rd);
		firstRow1Layout = new GridLayout();
		firstRow1Layout.numColumns = 2;
		buttonRow.setLayout(firstRow1Layout);
		Label packLabel = toolkit.createLabel(buttonRow, "Deployment Type",
				SWT.BEGINNING);
		deploymentTypeList = new Combo(buttonRow, SWT.READ_ONLY | SWT.BORDER
				| SWT.DEFAULT);
		rd = new GridData(GridData.VERTICAL_ALIGN_BEGINNING);
		rd.grabExcessHorizontalSpace = true;
		rd.minimumWidth = 350;
		deploymentTypeList.setLayoutData(rd);
		deploymentTypeList.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {
				updateDeploymentOptions();
			}

			@Override
			public void widgetSelected(SelectionEvent arg0) {
				updateDeploymentOptions();
			}
		});
		/*
		 * Button newPackageButton = toolkit.createButton(buttonRow,"",
		 * SWT.NONE); newPackageButton.addSelectionListener(new
		 * SelectionListener(){
		 * 
		 * @Override public void widgetDefaultSelected(SelectionEvent arg0) {
		 * createPackage(); }
		 * 
		 * @Override public void widgetSelected(SelectionEvent arg0) {
		 * createPackage(); } });
		 */

		deploymentOptions = toolkit.createExpandableComposite(
				deploymentComposite, ExpandableComposite.TREE_NODE
						| ExpandableComposite.CLIENT_INDENT);
		deploymentOptions.setText("Deployment Options");
		firstRow1Layout = new GridLayout();
		firstRow1Layout.numColumns = 1;
		deploymentOptions.setLayout(firstRow1Layout);
		/*
		 * rd = new
		 * GridData(GridData.FILL_HORIZONTAL|GridData.VERTICAL_ALIGN_BEGINNING);
		 * rd.grabExcessHorizontalSpace = true;
		 * deploymentOptions.setLayoutData(rd);
		 */
		// options = toolkit.createComposite(deploymentComposite, SWT.NONE);
		// deploymentOptions.setClient(options);
		// deploymentOptions.setExpanded(true);
		Composite buttonRow2 = toolkit.createComposite(deploymentComposite,
				SWT.NONE);
		firstRow1Layout = new GridLayout();
		firstRow1Layout.numColumns = 1;
		buttonRow2.setLayout(firstRow1Layout);
		rd = new GridData(GridData.VERTICAL_ALIGN_BEGINNING);
		rd.grabExcessHorizontalSpace = true;
		buttonRow2.setLayoutData(rd);
		Button deploymentButton = toolkit.createButton(buttonRow2, "Deploy",
				SWT.NONE);
		deploymentButton.setLayoutData(rd);
		deploymentButton.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {
				deploy();
			}

			@Override
			public void widgetSelected(SelectionEvent arg0) {
				deploy();
			}
		});
		servicePackageSection.setExpanded(true);
		servicePackageSection.setClient(deploymentComposite);
	}

	/**
	 * Generate the service package
	 */
	protected void generate() {
		if (automaticButton.getSelection()) {
			redoingPackages= true;
			definePackagesAutomatically();
		}
		buildPackages();
		redoingPackages=false;

	}

	/**
	 * Build the service packages
	 */
	private void buildPackages() {
		final ProgressMonitorDialog dialog = new ProgressMonitorDialog(
				getSite().getShell());
		try {
			dialog.run(true, true, new IRunnableWithProgress() {
				/* (non-Javadoc)
				 * @see org.eclipse.jface.operation.IRunnableWithProgress#run(org.eclipse.core.runtime.IProgressMonitor)
				 */
				public void run(IProgressMonitor monitor)
						throws InvocationTargetException {
					IJavaProject project = ((ServiceFormEditor) getEditor())
								.getProject();
					try {
						ProjectMetadata pr_meta = ((ServiceFormEditor) getEditor()).getProjectMetadata();
						PackagingUtils.buildPackages(project, pr_meta, monitor);
						/*
						monitor.beginTask("Building project classes...", 100);
						
						IFolder output = project.getProject().getFolder(
								ProjectMetadata.OUTPUT_FOLDER);
						if (output == null || !output.exists()) {
							output.create(true, true, monitor);
						}

						project.getProject().build(
								IncrementalProjectBuilder.INCREMENTAL_BUILD,
								monitor);
						// monitor.beginTask("Instrumenting Orchestrations...",
						// 100);
						// PackagingUtils.instrumentOrchestrations(pr_meta.getRuntimeLocation(),pr_meta.getOrchestrationClasses(),
						// output, monitor);
						monitor.beginTask("Creating packages...", 100);
						IFolder packages = output
								.getFolder(ProjectMetadata.PACKAGES_FOLDER);
						if (packages != null && packages.exists()) {
							packages.delete(true, monitor);
						}
						packages.create(true, true, monitor);
						// PackagingUtils.copyConfigFiles((ServiceFormEditor)getEditor());
						log.debug("Getting packages...");
						String[] packs = pr_meta.getPackages();
						String runtime = pr_meta.getRuntimeLocation();
						String[] orch_cls = pr_meta.getOrchestrationClasses();
						IFolder src_fld = project.getProject().getFolder(
								pr_meta.getSourceDir());
						IPackageFragmentRoot source = null;
						if (src_fld != null)
							source = project.findPackageFragmentRoot(src_fld
									.getFullPath());
						IFolder gen_fld = project.getProject().getFolder(
								"generated");
						IPackageFragmentRoot generated = null;
						if (gen_fld != null)
							generated = project.findPackageFragmentRoot(project
									.getProject().getFolder("generated")
									.getFullPath());
						if (source != null && source.exists()) {
							if (constraintsElements == null) {
								constraintsElements = getElements(orch_cls,
										ProjectMetadata.CORE_TYPE, project, pr_meta);
							}
							if (packs != null && packs.length > 0) {
								log.debug("Building core element packages");
								for (String p : packs) {
									String[] elements = pr_meta
											.getElementsInPackage(p);
									if (elements != null && elements.length > 0) {
										PackagingUtils.createCorePackage(
												runtime,
												p,
												elements,
												pr_meta.getDependencies(elements),
												constraintsElements, source,
												output, packages, monitor);
									}
								}
							} else {
								log.warn("Warning: No core element packages built");
								monitor.setCanceled(true);
								throw (new InvocationTargetException(
										new Exception("No core element packages built")));
							}
							if (orch_cls!= null && orch_cls.length>0){
								log.debug("Generating Orchestration");
								PackagingUtils.createServiceOrchestrationPackage(
									runtime, PackagingUtils.getClasspath(project),
									project.getProject().getName(), orch_cls,
									pr_meta.getDependencies(getOrchestrationElementsLabels(
											orch_cls, project, pr_meta)),
									source, generated, output, packages, monitor, 
									shouldBeWarFile(pr_meta.getOrchestrationClassesTypes()));
								monitor.done();
							}else{
								log.warn("Warning: No orchestration element packages built");
								monitor.setCanceled(true);
								throw (new InvocationTargetException(
										new Exception("No orchestration packages built")));
							}
							
						} else {
							log.error("Source dir not found");
							monitor.setCanceled(true);
							throw (new InvocationTargetException(new Exception(
									"Source dir " + src_fld.getFullPath()
											+ " not found")));
						}*/
					} catch (Exception e) {
						throw (new InvocationTargetException(e));
					}
				}
				
			});
		} catch (InvocationTargetException e) {
			ErrorDialog.openError(dialog.getShell(),
					"Error creating packages", e.getMessage(), new Status(IStatus.ERROR, Activator.PLUGIN_ID, e.getMessage(),e));
			e.printStackTrace();
		} catch (InterruptedException e) {
			ErrorDialog.openError(dialog.getShell(), "Building interrumped",
					e.getMessage(),new Status(IStatus.ERROR, Activator.PLUGIN_ID, e.getMessage(),e));
			e.printStackTrace();
		}
	}
	
	
	/**
	 * Define packages automatically
	 */
	private void definePackagesAutomatically() {
		try {
			ProjectMetadata pr_meta = new ProjectMetadata(
					((ServiceFormEditor) getEditor()).getMetadataFile()
					.getRawLocation().toFile());
			pr_meta.removeAllPackages();
			servicePackagesList.removeAll();
			boolean packCreated = false;
			//Orchestration Types
			//TODO check 
			HashMap<String, ServiceElement> oeEls = CommonFormPage.getElements(
					pr_meta.getAllOrchestrationClasses(), ProjectMetadata.ORCH_TYPE,
					((ServiceFormEditor) getEditor()).getProject(), pr_meta);
			if (oeEls != null && oeEls.size()>0){
				Map<String, ArrayList<String>> oePacks = automaticPackagesCalculation(pr_meta, oeEls, 
						((ServiceFormEditor) getEditor()).getProject().getProject().getName());
				if (oePacks != null && oePacks.size()>0){
				
				for (Entry<String, ArrayList<String>> e: oePacks.entrySet()){
					log.debug("Creating package " + e.getKey());
					pr_meta.addPackage(e.getKey(), ProjectMetadata.ORCH_PACK_TYPE);
					servicePackagesList.add(e.getKey());
					packCreated = true;
					for (String compName: e.getValue()){
						log.debug("Adding element " + compName);
						pr_meta.addElementToPackage(e.getKey(), compName);
					}	
				}	
				}else{
					ErrorDialog.openError(this.getEditor().getSite()
							.getShell(),
							"Error creating packages","No orchestration packages created and there are method elements", 
							new Status(IStatus.ERROR, Activator.PLUGIN_ID, "Review service core elements constraints inconsitencies"));
					log.warn(" No orchestration packages created");
				}
			}else
				log.debug(" No method elements to do");
			
			//Method Core Types	
			//TODO check if all orchestration or only internal
			HashMap<String, ServiceElement> meEls = CommonFormPage.getElements(
					pr_meta.getAllOrchestrationClasses(), ProjectMetadata.METHOD_TYPE,
					((ServiceFormEditor) getEditor()).getProject(), pr_meta);
			if (meEls != null && meEls.size()>0){
				Map<String, ArrayList<String>> mePacks = automaticPackagesCalculation(pr_meta, meEls, "autoMethod");
				if (mePacks != null && mePacks.size()>0){
				
				for (Entry<String, ArrayList<String>> e: mePacks.entrySet()){
					log.debug("Creating package " + e.getKey());
					pr_meta.addPackage(e.getKey(), ProjectMetadata.METH_CORE_PACK_TYPE);
					servicePackagesList.add(e.getKey());
					packCreated = true;
					for (String compName: e.getValue()){
						log.debug("Adding element " + compName);
						pr_meta.addElementToPackage(e.getKey(), compName);
					}	
				}	
				}else{
					ErrorDialog.openError(this.getEditor().getSite()
							.getShell(),
							"Error creating packages","No method packages created and there are method elements", 
							new Status(IStatus.ERROR, Activator.PLUGIN_ID, "Review service core elements constraints inconsitencies"));
					log.warn(" No method package created");
				}
			}else
				log.debug(" No method elements to do");
			//Services
			//TODO Check if all orchestration classes or only internal
			HashMap<String, ServiceElement> seEls = CommonFormPage.getElements(
					pr_meta.getAllOrchestrationClasses(), ProjectMetadata.SERVICE_TYPE,
					((ServiceFormEditor) getEditor()).getProject(), pr_meta);
			if (seEls!= null && seEls.size()>0){
				Map<String, ArrayList<String>> sePacks = automaticPackagesCalculation(pr_meta, seEls, "autoService");
				if (sePacks != null && sePacks.size()>0){
					for (Entry<String, ArrayList<String>> e: sePacks.entrySet()){
						pr_meta.addPackage(e.getKey(), ProjectMetadata.SER_CORE_PACK_TYPE);
						servicePackagesList.add(e.getKey());
						packCreated = true;
						for (String compName: e.getValue()){
							pr_meta.addElementToPackage(e.getKey(), compName);
						}
					}
				}else{
					ErrorDialog.openError(this.getEditor().getSite()
							.getShell(),
							"Error creating packages","No service packages created and there are service elements", 
							new Status(IStatus.ERROR, Activator.PLUGIN_ID, "Review service core elements constraints inconsitencies"));
					log.warn(" No service package created");
				}
			}else
				log.debug(" No service elements");
			pr_meta.toFile(((ServiceFormEditor) getEditor())
					.getMetadataFile().getRawLocation().toFile());
			if ((packCreated)){
				initManual(servicePackagesList.getItem(0));
				expandManual();
			}/*else{
				ErrorDialog.openError(this.getEditor().getSite()
						.getShell(),
						"Error creating packages","No packages created", 
						new Status(IStatus.ERROR, Activator.PLUGIN_ID, "Review core element constraints inconsitencies"));
			}*/
		} catch (Exception e) {
			ErrorDialog.openError(this.getEditor().getSite()
					.getShell(),
					"Error creating packages","No packages created", 
					new Status(IStatus.ERROR, Activator.PLUGIN_ID, e.getMessage()));
			e.printStackTrace();
		}
	}

	/** 
	 * Calculate service package automatically
	 * 
	 * @param pr_meta Project Metadata
	 * @param type Package type
	 * @param prefix 
	 * @return Map of packages and their elements
	 */
	private Map<String, ArrayList<String>> automaticPackagesCalculation(ProjectMetadata pr_meta, HashMap<String, ServiceElement> elements, String prefix) {
		HashMap<String, ArrayList<String>> map = new HashMap<String, ArrayList<String>>();
		HashMap<String, HashMap<String, String>> typeConsMap = new HashMap<String, HashMap<String, String>>();
		 
		//Create first type;
		int nextTypeNum = 0;
		map.put(prefix, new ArrayList<String>());
		typeConsMap.put(prefix, new HashMap<String,String>());
		nextTypeNum++;
		//Group elements by OS and Architecture type
		for (String el:elements.keySet()){
			Map<String, String> constraints = elements.get(el).getConstraints();
			boolean packFound = false;
			for (String pack:map.keySet()){
				Map<String, String> packConstraints = typeConsMap.get(pack);
				Set<Map<String, String>> set= new HashSet<Map<String, String>>();
				set.add(constraints);
				set.add(packConstraints);
				if (ConstraintsUtils.checkConsistency(set)){//{constraints, packConstraints}))){
					map.get(pack).add(el);
					ConstraintsUtils.updateCompatibleConstraints(typeConsMap, pack,constraints);
					packFound = true;
					break;
				}
			}
			if (!packFound){
				String newName = prefix+"_"+nextTypeNum;
				map.put(newName, new ArrayList<String>());
				typeConsMap.put(newName, new HashMap<String,String>());
				map.get(newName).add(el);
				ConstraintsUtils.updateCompatibleConstraints(typeConsMap, newName, constraints);
			}			
		}
		return map;
	}

	/**
	 * Deploy a service
	 */
	protected void deploy() {
		if (selectedDeploymentOption != null) {
			selectedDeploymentOption.deploy();
		} else {
			ErrorDialog.openError(this.getEditor().getSite().getShell(),
					"Error deploying service", " Deployer object is null", new Status(IStatus.ERROR, Activator.PLUGIN_ID,"Deployer object is null"));
		}
	}

	/**
	 * Update infrastructure deployment options
	 */
	protected void updateDeploymentOptions() {
		log.debug("Updating options");
		String new_type = deploymentTypeList.getItem(deploymentTypeList
				.getSelectionIndex());
		log.debug("Selected item is " + new_type);
		if (new_type != null) {
			if (previous_type != null) {
				if (!new_type.equals(previous_type)) {
					if (selectedDeploymentOption != null) {
						Deployer new_deployer = deploymentCases.get(new_type);
						if (new_deployer != null) {
							// selectedDeploymentOption.diposeComposite();
							options = new_deployer.createComposite(toolkit,
									deploymentOptions, options);
							options.layout(true);
							options.redraw();
							deploymentOptions.setClient(options);
							deploymentOptions.setExpanded(true);
							deploymentOptions.layout(true);
							deploymentOptions.redraw();
							deploymentComposite.layout(true);
							deploymentComposite.redraw();
							// form.reflow(true);
							new_deployer.initiate();
							form.reflow(true);
							selectedDeploymentOption = new_deployer;
							previous_type = new_type;
							log.debug("deployment options updated");
						} else {
							ErrorDialog.openError(this.getEditor().getSite()
									.getShell(),
									"Error loading deployment options",
									" Deployer object is null", new Status(IStatus.ERROR, Activator.PLUGIN_ID, "Deployer object is null" ));
						}

					} else {
						ErrorDialog.openError(this.getEditor().getSite()
								.getShell(),
								"Error loading deployment options",
								" selected deployer object is null", new Status(IStatus.ERROR, Activator.PLUGIN_ID, "Selected Deployer object is null" ));
					}
				}// else nothing to do
			} else {
				Deployer new_deployer = deploymentCases.get(new_type);
				if (new_deployer != null) {
					options = new_deployer.createComposite(toolkit,
							deploymentOptions, options);
					deploymentOptions.setClient(options);
					deploymentOptions.setExpanded(true);
					// options.layout(true);
					deploymentOptions.redraw();
					deploymentComposite.redraw();
					form.reflow(true);
					new_deployer.initiate();
					form.reflow(true);
					selectedDeploymentOption = new_deployer;
					previous_type = new_type;
					log.debug("deployment options updated");
				} else {
					ErrorDialog.openError(this.getEditor().getSite()
							.getShell(), "Error loading deployment options",
							" Deployer object is null", new Status(IStatus.ERROR, Activator.PLUGIN_ID, "Deployer object is null" ));
				}
			}
		} else {
			ErrorDialog.openError(this.getEditor().getSite().getShell(),
					"Error loading deployment options", "New type is null", new Status(IStatus.ERROR, Activator.PLUGIN_ID, "New type is null" ));
		}
	}

	/**
	 * Get the constraint of the selected elements.
	 * @param elements Selected elements.
	 * @param constraintsElements Constraint elements.
	 * @param minNumber Minimum number of element instances.
	 * @param maxDefault Maximum values for resources.
	 * @param newConstraints New package constraints.
	 * @return Minimum number of Instances
	 */
	public static Map<String, Integer> getConstraintsElements(String[] elements,
			HashMap<String, ServiceElement> constraintsElements,
			Map<String, Integer> minNumber, Map<String, String> maxDefault, 
			Map<String, String> newConstraints) {
		Map<String, Integer> newMinNumbers = new HashMap<String, Integer>();
		Set<Map<String, String>> set = new HashSet<Map<String, String>>();
		if (elements != null && elements.length > 0) {
			for (String e : elements) {
				ServiceElement se = constraintsElements.get(e);
				if (se != null) {
					int newMinNumber = ConstraintsUtils.checkMaxResourceProperties(
						se.getConstraints(), maxDefault, minNumber.get(se.getLabel()).intValue());
					Map<String, String> cons = ConstraintsUtils.getMinConstraints(
						se.getConstraints(), newMinNumber);
					if (cons != null && cons.size() > 0){
						newMinNumbers.put(se.getLabel(), newMinNumber);
						set.add(cons);
					}else{
						newMinNumbers.put(se.getLabel(), newMinNumber);
						log.debug("Element " + e + " has no constraints.");
					}
				}else{
					log.debug(" Service Element " + e + " not found.");
				}
			}
			newConstraints.putAll(ConstraintsUtils.getMaxConstraints(set));
			return newMinNumbers;
		}else{
			log.debug("No elements for package.");
			return minNumber;
		}
		
	}

	/**
	 * Get the editor form.
	 * @return Editor form.
	 */
	public ScrolledForm getForm() {
		return form;
	}

	/**
	 * Get the editors toolkit
	 * @return editors toolkit
	 */
	public FormToolkit getToolkit() {
		return toolkit;
	}
	
	/**
	 * Get the project metadata
	 * @return Project metadata
	 */
	public ProjectMetadata getProjectMetadata() {
		try{
			return new ProjectMetadata(((ServiceFormEditor) getEditor()).getMetadataFile()
						.getRawLocation().toFile());
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}		
	}

	public boolean isBlocking() {
		return redoingPackages;
	}
	
}
