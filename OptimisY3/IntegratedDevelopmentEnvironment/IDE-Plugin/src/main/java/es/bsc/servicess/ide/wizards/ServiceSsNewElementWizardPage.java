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

import java.net.URI;
import java.util.ArrayList;
import java.util.Map.Entry;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.Flags;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaConventions;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.Signature;
import org.eclipse.jdt.core.compiler.IProblem;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.search.IJavaSearchConstants;
import org.eclipse.jdt.core.search.IJavaSearchScope;
import org.eclipse.jdt.core.search.SearchEngine;
import org.eclipse.jdt.internal.core.JavaElement;
import org.eclipse.jdt.internal.corext.util.JavaConventionsUtil;
import org.eclipse.jdt.internal.corext.util.JavaModelUtil;
import org.eclipse.jdt.internal.corext.util.Messages;
import org.eclipse.jdt.internal.corext.util.Resources;
import org.eclipse.jdt.internal.ui.JavaPlugin;
import org.eclipse.jdt.internal.ui.dialogs.FilteredTypesSelectionDialog;
import org.eclipse.jdt.internal.ui.dialogs.StatusInfo;
import org.eclipse.jdt.internal.ui.dialogs.TextFieldNavigationHandler;
import org.eclipse.jdt.internal.ui.preferences.CodeTemplatePreferencePage;
import org.eclipse.jdt.internal.ui.refactoring.contentassist.ControlContentAssistHelper;
import org.eclipse.jdt.internal.ui.refactoring.contentassist.JavaPackageCompletionProcessor;
import org.eclipse.jdt.internal.ui.viewsupport.BasicElementLabels;
import org.eclipse.jdt.internal.ui.viewsupport.IViewPartInputProvider;
import org.eclipse.jdt.internal.ui.wizards.NewWizardMessages;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.DialogField;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.IDialogFieldListener;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.IListAdapter;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.IStringButtonAdapter;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.LayoutUtil;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.ListDialogField;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.StringButtonStatusDialogField;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.StringDialogField;
import org.eclipse.jdt.ui.JavaElementLabelProvider;
import org.eclipse.jdt.ui.wizards.NewContainerWizardPage;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
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
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.dialogs.ElementListSelectionDialog;
import org.eclipse.ui.dialogs.PreferencesUtil;
import org.eclipse.ui.views.contentoutline.ContentOutline;

import es.bsc.servicess.ide.Checker;
import es.bsc.servicess.ide.ConstraintsUtils;
import es.bsc.servicess.ide.dialogs.ModifyConstraintsDialog;
import es.bsc.servicess.ide.dialogs.ModifyParameterDialog;
import es.bsc.servicess.ide.model.OrchestrationElement;
import es.bsc.servicess.ide.model.Parameter;
import es.bsc.servicess.ide.model.ServiceElement;

@SuppressWarnings("restriction")
public abstract class ServiceSsNewElementWizardPage extends
		NewContainerWizardPage {

	private boolean pageCreated;
	private StringButtonStatusDialogField fPackageDialogField;
	private StringButtonStatusDialogField fClassDialogField;
	protected StatusInfo fPackageStatus;
	protected StatusInfo fClassStatus;
	private boolean fCanModifyPackage;
	private boolean fCanModifyClass;
	protected Text oeNameText;
	protected Text oeReturnTypeText;
	protected Table oeParametersTable;
	protected Table oeConstraintsTable;
	private IPackageFragment fCurrPackage;
	protected StatusInfo fOEStatus;
	private JavaPackageCompletionProcessor fCurrPackageCompletionProcessor;
	private IType fCurrClass;
	protected ServiceElement element = null;
	private Button selectReturnButton;
	private Button oeAddParamButton;
	private Button oeAddConButton;
	private Button oeModifyConButton;
	private Button oeDeleteConButton;
	private Button oeModifyParamButton;
	private Button oeDeleteParamButton;
	private String elementName;
	private boolean isClass = true;
	protected static final int NAME = 0;
	protected static final int MODIFIER = 1;
	protected static final int RETURN_TYPE = 2;
	protected static final int CONSTRAINTS = 3;
	protected static final int PARAMETERS = 4;
	protected static final int IS_WEB = 5;

	public ServiceSsNewElementWizardPage(String name, String title,
			String description) {
		super(name);

		setTitle(title);
		setDescription(description);

		OEFieldsAdapter adapter = new OEFieldsAdapter();
		fPackageDialogField = new StringButtonStatusDialogField(adapter);
		fPackageDialogField.setDialogFieldListener(adapter);
		fPackageDialogField
				.setLabelText(NewWizardMessages.NewTypeWizardPage_package_label);
		fPackageDialogField
				.setButtonLabel(NewWizardMessages.NewTypeWizardPage_package_button);
		fPackageDialogField
				.setStatusWidthHint(NewWizardMessages.NewTypeWizardPage_default);

		fClassDialogField = new StringButtonStatusDialogField(adapter);
		fClassDialogField.setDialogFieldListener(adapter);
		fClassDialogField.setLabelText("Service Class");
		fClassDialogField
				.setButtonLabel(NewWizardMessages.NewTypeWizardPage_package_button);
		fClassDialogField
				.setStatusWidthHint(NewWizardMessages.NewTypeWizardPage_default);
		fCurrPackageCompletionProcessor = new JavaPackageCompletionProcessor();

		fPackageStatus = new StatusInfo();
		fClassStatus = new StatusInfo();
		fOEStatus = new StatusInfo();
		fOEStatus
				.setError("Orchestration Element Description is not completed");
		fCanModifyPackage = true;
		fCanModifyClass = true;
		pageCreated = false;
		updateEnableState();
		setPageComplete(false);
	}

	public void setClassLabel(String label, boolean b) {
		this.isClass = b;
		fClassDialogField.setLabelText(label);
	}

	public void createControl(Composite parent) {
		initializeDialogUnits(parent);

		Composite root = new Composite(parent, SWT.NONE);
		root.setFont(parent.getFont());
		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		root.setLayout(layout);
		int nColumnsLocation = 4;

		Group location = new Group(root, SWT.NONE);
		GridLayout layoutLocation = new GridLayout();
		layoutLocation.numColumns = nColumnsLocation;
		location.setLayout(layoutLocation);
		location.setText("Element Location");

		Group group = new Group(root, SWT.NONE);
		group.setLayout(new GridLayout(2, false));
		group.setText("Element description");
		GridData rd = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		rd.grabExcessHorizontalSpace = true;
		group.setLayoutData(rd);

		// pick & choose the wanted UI components

		createContainerControls(location, nColumnsLocation);
		createPackageControls(location, nColumnsLocation);
		createClassControls(location, nColumnsLocation);
		createCommonControls(group);
		createElementTypeSpecificControls(group);
		if (element != null) {
			setPrintElementData();
		} else if (elementName != null) {
			oeNameText.setText(elementName);
		}
		pageCreated = true;
		addListeners();
		setControl(root);
		Dialog.applyDialogFont(root);

	}

	private void setPrintElementData() {
		oeNameText.setText(element.getMethodName());
		oeReturnTypeText.setText(element.getReturnType());
		// printModifiers();
		printParameters();
		printConstraints();
		printTypeSpecific();

	}

	private void printConstraints() {
		oeConstraintsTable.removeAll();
		for (Entry<String, String> e : element.getConstraints().entrySet()) {
			TableItem it = new TableItem(oeConstraintsTable, SWT.NONE);
			it.setText(new String[] { e.getKey(), e.getValue() });
		}

	}

	protected abstract void printTypeSpecific();

	protected abstract void printParameters();

	// protected abstract void printModifiers();

	protected abstract void createElementTypeSpecificControls(Group group);

	protected void createPackageControls(Composite composite, int nColumns) {
		fPackageDialogField.doFillIntoGrid(composite, nColumns);
		Text text = fPackageDialogField.getTextControl(null);
		LayoutUtil.setWidthHint(text, getMaxFieldWidth());
		LayoutUtil.setHorizontalGrabbing(text);
		ControlContentAssistHelper.createTextContentAssistant(text,
				fCurrPackageCompletionProcessor);
		TextFieldNavigationHandler.install(text);
	}

	protected void createClassControls(Composite composite, int nColumns) {
		fClassDialogField.doFillIntoGrid(composite, nColumns);
		Text text = fClassDialogField.getTextControl(null);
		LayoutUtil.setWidthHint(text, getMaxFieldWidth());
		LayoutUtil.setHorizontalGrabbing(text);
		// ControlContentAssistHelper.createTextContentAssistant(text,
		// fCurrPackageCompletionProcessor);
		TextFieldNavigationHandler.install(text);
	}

	protected void createCommonControls(Composite oeDesc) {

		Label oeNameLabel = new Label(oeDesc, SWT.NONE);
		oeNameLabel.setText("Name");
		oeNameText = new Text(oeDesc, SWT.SINGLE | SWT.BORDER);
		GridData rd = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		rd.grabExcessHorizontalSpace = true;
		oeNameText.setLayoutData(rd);

		// Modifiers
		/*
		 * Label oeModifierLabel = new Label(oeDesc, SWT.NONE);
		 * oeModifierLabel.setText("Modifier"); Composite oeModifiers = new
		 * Composite (oeDesc, SWT.NONE); GridLayout oeModifiersLayout = new
		 * GridLayout (); oeModifiersLayout.numColumns=2;
		 * oeModifiers.setLayout(oeModifiersLayout);
		 * createSpecificModifiers(oeModifiers); rd = new
		 * GridData(GridData.HORIZONTAL_ALIGN_FILL);
		 * rd.grabExcessHorizontalSpace = true; oeModifiers.setLayoutData(rd);
		 */
		// ReturnType
		Label oeReturnTypeLabel = new Label(oeDesc, SWT.NONE);
		oeReturnTypeLabel.setText("Return Type");
		Composite oeReturn = new Composite(oeDesc, SWT.NONE);
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
		oeReturnTypeText = new Text(oeReturn, SWT.SINGLE | SWT.BORDER);
		rd = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		rd.grabExcessHorizontalSpace = true;
		oeReturnTypeText.setLayoutData(rd);
		selectReturnButton = new Button(oeReturn, SWT.NONE);
		selectReturnButton.setText("Select...");

		// Params Table
		Label oeParametersLabel = new Label(oeDesc, SWT.NONE);
		oeParametersLabel.setText("Parameters");
		Composite oeParams = new Composite(oeDesc, SWT.NONE);
		rd = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		rd.grabExcessHorizontalSpace = true;
		rd.horizontalIndent = 0;
		rd.horizontalSpan = 0;
		oeParams.setLayoutData(rd);
		GridLayout oeParamsLayout = new GridLayout();
		oeParamsLayout.numColumns = 2;
		oeParamsLayout.marginLeft = 0;
		oeParamsLayout.marginRight = 0;
		oeParams.setLayout(oeParamsLayout);
		oeParametersTable = new Table(oeParams, SWT.SINGLE | SWT.BORDER
				| SWT.FULL_SELECTION | SWT.FILL | SWT.V_SCROLL | SWT.H_SCROLL);
		oeParametersTable.setHeaderVisible(true);
		oeParametersTable.setLinesVisible(true);
		configureSpecificParameterTableColumns();
		rd = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		rd.grabExcessHorizontalSpace = true;
		rd.grabExcessVerticalSpace = true;
		rd.minimumHeight = 90;
		oeParametersTable.setLayoutData(rd);
		Composite oeParamsButtons = new Composite(oeParams, SWT.NONE);
		oeParamsLayout = new GridLayout();
		oeParamsLayout.numColumns = 1;
		oeParamsLayout.marginLeft = 0;
		oeParamsLayout.marginRight = 0;
		oeParamsButtons.setLayout(oeParamsLayout);
		oeAddParamButton = new Button(oeParamsButtons, SWT.NONE);
		oeAddParamButton.setText("Add...");
		rd = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		rd.grabExcessHorizontalSpace = true;
		oeAddParamButton.setLayoutData(rd);
		oeModifyParamButton = new Button(oeParamsButtons, SWT.NONE);
		oeModifyParamButton.setText("Modify...");
		oeModifyParamButton.setEnabled(false);
		rd = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		rd.grabExcessHorizontalSpace = true;
		oeModifyParamButton.setLayoutData(rd);
		oeDeleteParamButton = new Button(oeParamsButtons, SWT.NONE);
		oeDeleteParamButton.setText("Delete");
		oeDeleteParamButton.setEnabled(false);
		rd = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		rd.grabExcessHorizontalSpace = true;
		oeDeleteParamButton.setLayoutData(rd);

		// Constraints table
		Label oeConstLabel = new Label(oeDesc, SWT.NONE);
		oeConstLabel.setText("Constraints");
		Composite oeCons = new Composite(oeDesc, SWT.NONE);
		rd = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		rd.grabExcessHorizontalSpace = true;
		rd.horizontalIndent = 0;
		oeCons.setLayoutData(rd);
		oeParamsLayout = new GridLayout();
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
		oeParamsButtons = new Composite(oeCons, SWT.NONE);
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

	}

	protected abstract void configureSpecificParameterTableColumns();

	// protected abstract void createSpecificModifiers(Composite oeModifiers);

	public void init(IStructuredSelection selection) {
		IJavaElement jelem = getInitialJavaElement(selection);
		initContainerPage(jelem);
		initPage(jelem);
		doStatusUpdate();
	}

	protected void initPage(IJavaElement elem) {

		IJavaProject project = null;
		IPackageFragment pack = null;
		IType typeName = null;
		if (elem != null) {
			// evaluate the enclosing type
			project = elem.getJavaProject();
			pack = (IPackageFragment) elem
					.getAncestor(IJavaElement.PACKAGE_FRAGMENT);
			System.out.println("Element name:" + elem.getElementName());
			if (elem.getElementType() == JavaElement.COMPILATION_UNIT) {
				typeName = ((ICompilationUnit) elem).findPrimaryType();
			} else if (elem.getElementType() == JavaElement.TYPE) {
				typeName = (IType) elem;
			}
		}

		setPackageFragment(pack, true);

		setClassName(typeName, true);

	}

	public void setPackageFragment(IPackageFragment pack, boolean canBeModified) {
		fCurrPackage = pack;
		fCanModifyPackage = canBeModified;
		String str = (pack == null) ? "" : pack.getElementName(); //$NON-NLS-1$
		fPackageDialogField.setText(str);
		updateEnableState();
	}

	public void setClassName(IType type, boolean canBeModified) {
		// TODO validate if its a Service Class
		fCurrClass = type;
		fCanModifyClass = canBeModified;
		String str = (type == null) ? "" : type.getElementName();
		fClassDialogField.setText(str);
		fClassDialogField.setEnabled(canBeModified);
		updateEnableState();

	}

	private void updateEnableState() {
		fPackageDialogField.setEnabled(fCanModifyPackage);
		fClassDialogField.setEnabled(fCanModifyClass);
	}

	private static IStatus validateJavaTypeName(String text,
			IJavaProject project) {

		if (project == null || !project.exists()) {
			return JavaConventions.validateJavaTypeName(text,
					JavaCore.VERSION_1_3, JavaCore.VERSION_1_3);
		}
		return JavaConventionsUtil.validateJavaTypeName(text, project);
	}

	private static IStatus validatePackageName(String text, IJavaProject project) {
		if (project == null || !project.exists()) {
			return JavaConventions.validatePackageName(text,
					JavaCore.VERSION_1_3, JavaCore.VERSION_1_3);
		}
		return JavaConventionsUtil.validatePackageName(text, project);
	}

	// ------ validation --------
	private void doStatusUpdate() {
		// status of all used components
		IStatus[] status = new IStatus[] { fContainerStatus, fPackageStatus,
				fClassStatus, fOEStatus };

		// the mode severe status will be displayed and the OK button
		// enabled/disabled.
		updateStatus(status);
	}

	private class OEFieldsAdapter implements IStringButtonAdapter,
			IDialogFieldListener, IListAdapter, SelectionListener {

		// -------- IStringButtonAdapter
		public void changeControlPressed(DialogField field) {
			typePageChangeControlPressed(field);
		}

		// -------- IListAdapter
		public void customButtonPressed(ListDialogField field, int index) {
			typePageCustomButtonPressed(field, index);
		}

		public void selectionChanged(ListDialogField field) {
		}

		// -------- IDialogFieldListener
		public void dialogFieldChanged(DialogField field) {
			typePageDialogFieldChanged(field);
		}

		public void doubleClicked(ListDialogField field) {
		}

		public void widgetSelected(SelectionEvent e) {
			typePageLinkActivated();
		}

		public void widgetDefaultSelected(SelectionEvent e) {
			typePageLinkActivated();
		}
	}

	private void typePageLinkActivated() {
		IJavaProject project = getJavaProject();
		if (project != null) {
			PreferenceDialog dialog = PreferencesUtil.createPropertyDialogOn(
					getShell(), project.getProject(),
					CodeTemplatePreferencePage.PROP_ID, null, null);
			dialog.open();
		} else {
			String title = NewWizardMessages.NewTypeWizardPage_configure_templates_title;
			String message = NewWizardMessages.NewTypeWizardPage_configure_templates_message;
			MessageDialog.openInformation(getShell(), title, message);
		}
	}

	private void typePageChangeControlPressed(DialogField field) {
		if (field == fPackageDialogField) {
			IPackageFragment pack = choosePackage();
			if (pack != null) {
				fPackageDialogField.setText(pack.getElementName());
			}
		} else if (field == fClassDialogField) {
			IType type = chooseClass();
			setClassName(type, fCanModifyClass);
		}
	}

	protected IType chooseClass() {
		IJavaProject project = getJavaProject();
		if (project == null) {
			return null;
		}
		IJavaElement el;
		IPackageFragment pack = getPackageFragment();
		if (pack == null) {
			IPackageFragmentRoot root = getPackageFragmentRoot();
			if (root == null) {
				el = project;
			} else
				el = root;
		} else {
			el = pack;
		}
		IJavaElement[] elements = new IJavaElement[] { el };
		IJavaSearchScope scope = SearchEngine.createJavaSearchScope(elements);
		int filter;
		if (isClass) {
			filter = IJavaSearchConstants.CLASS;
		} else
			filter = IJavaSearchConstants.INTERFACE;
		FilteredTypesSelectionDialog dialog = new FilteredTypesSelectionDialog(
				getShell(), false, getWizard().getContainer(), scope, filter);
		dialog.setTitle("Class Selection");
		dialog.setMessage("Select a Class");
		dialog.setInitialPattern(getClassName());

		if (dialog.open() == Window.OK) {
			return (IType) dialog.getFirstResult();
		}
		return null;
	}

	protected IPackageFragment choosePackage() {
		IPackageFragmentRoot froot = getPackageFragmentRoot();
		IJavaElement[] packages = null;
		try {
			if (froot != null && froot.exists()) {
				packages = froot.getChildren();
			}
		} catch (JavaModelException e) {
			JavaPlugin.log(e);
		}
		if (packages == null) {
			packages = new IJavaElement[0];
		}

		ElementListSelectionDialog dialog = new ElementListSelectionDialog(
				getShell(), new JavaElementLabelProvider(
						JavaElementLabelProvider.SHOW_DEFAULT));
		dialog.setIgnoreCase(false);
		dialog.setTitle(NewWizardMessages.NewTypeWizardPage_ChoosePackageDialog_title);
		dialog.setMessage(NewWizardMessages.NewTypeWizardPage_ChoosePackageDialog_description);
		dialog.setEmptyListMessage(NewWizardMessages.NewTypeWizardPage_ChoosePackageDialog_empty);
		dialog.setElements(packages);
		dialog.setHelpAvailable(false);

		IPackageFragment pack = getPackageFragment();
		if (pack != null) {
			dialog.setInitialSelections(new Object[] { pack });
		}

		if (dialog.open() == Window.OK) {
			return (IPackageFragment) dialog.getFirstResult();
		}
		return null;
	}

	public String getPackageText() {
		return fPackageDialogField.getText();
	}

	public IPackageFragment getPackageFragment() {
		return fCurrPackage;
	}

	public IType getTypeClass() {
		return fCurrClass;
	}

	private void typePageCustomButtonPressed(DialogField field, int index) {

	}

	private void typePageDialogFieldChanged(DialogField field) {
		String fieldName = null;
		if (field == fPackageDialogField) {
			fPackageStatus = (StatusInfo) packageChanged();
			updatePackageStatusLabel();
			fClassStatus = (StatusInfo) classChanged();
			fieldName = "PACKAGE";
		} else {
			fClassStatus = (StatusInfo) classChanged();
			fieldName = "TYPENAME";
		}
		// tell all others
		handleFieldChanged(fieldName);
	}

	private void updatePackageStatusLabel() {
		String packName = getPackageText();

		if (packName.length() == 0) {
			fPackageDialogField
					.setStatus(NewWizardMessages.NewTypeWizardPage_default);
		} else {
			fPackageDialogField.setStatus(""); //$NON-NLS-1$
		}
	}

	protected IStatus containerChanged() {
		IStatus status = super.containerChanged();
		IPackageFragmentRoot root = getPackageFragmentRoot();
		fCurrPackageCompletionProcessor.setPackageFragmentRoot(root);
		fPackageDialogField.enableButton(root != null);
		return status;
	}

	/**
	 * A hook method that gets called when the package field has changed. The
	 * method validates the package name and returns the status of the
	 * validation. The validation also updates the package fragment model.
	 * <p>
	 * Subclasses may extend this method to perform their own validation.
	 * </p>
	 * 
	 * @return the status of the validation
	 */
	protected IStatus packageChanged() {
		StatusInfo status = new StatusInfo();
		IPackageFragmentRoot root = getPackageFragmentRoot();
		fPackageDialogField.enableButton(root != null);

		IJavaProject project = root != null ? root.getJavaProject() : null;

		String packName = getPackageText();
		if (packName.length() > 0) {
			IStatus val = validatePackageName(packName, project);
			if (val.getSeverity() == IStatus.ERROR) {
				status.setError(Messages
						.format(NewWizardMessages.NewTypeWizardPage_error_InvalidPackageName,
								val.getMessage()));
				return status;
			} else if (val.getSeverity() == IStatus.WARNING) {
				status.setWarning(Messages
						.format(NewWizardMessages.NewTypeWizardPage_warning_DiscouragedPackageName,
								val.getMessage()));
				// continue
			}
		} else {
			status.setWarning(NewWizardMessages.NewTypeWizardPage_warning_DefaultPackageDiscouraged);
		}

		if (project != null) {
			if (project.exists() && packName.length() > 0) {
				try {
					IPath rootPath = root.getPath();
					IPath outputPath = project.getOutputLocation();
					if (rootPath.isPrefixOf(outputPath)
							&& !rootPath.equals(outputPath)) {
						// if the bin folder is inside of our root, don't allow
						// to name a package
						// like the bin folder
						IPath packagePath = rootPath.append(packName.replace(
								'.', '/'));
						if (outputPath.isPrefixOf(packagePath)) {
							status.setError(NewWizardMessages.NewTypeWizardPage_error_ClashOutputLocation);
							return status;
						}
					}
				} catch (JavaModelException e) {
					JavaPlugin.log(e);
					// let pass
				}
			}

			fCurrPackage = root.getPackageFragment(packName);
		} else {
			status.setError(""); //$NON-NLS-1$
		}
		return status;
	}

	protected IStatus classChanged() {
		StatusInfo status = new StatusInfo();
		fCurrClass = null;
		String typeNameWithParameters = getClassName();
		// must not be empty
		if (typeNameWithParameters.length() == 0) {
			status.setError(NewWizardMessages.NewTypeWizardPage_error_EnterTypeName);
			return status;
		}

		String typeName = getClassNameWithoutParameters();
		if (typeName.indexOf('.') != -1) {
			status.setError(NewWizardMessages.NewTypeWizardPage_error_QualifiedName);
			return status;
		}

		IJavaProject project = getJavaProject();

		IStatus val = validateJavaTypeName(typeName, project);
		if (val.getSeverity() == IStatus.ERROR) {
			status.setError(Messages.format(
					NewWizardMessages.NewTypeWizardPage_error_InvalidTypeName,
					val.getMessage()));
			return status;
		} else if (val.getSeverity() == IStatus.WARNING) {
			status.setWarning(Messages
					.format(NewWizardMessages.NewTypeWizardPage_warning_TypeNameDiscouraged,
							val.getMessage()));
			// continue checking
		}

		

		IPackageFragment pack = getPackageFragment();
		if (pack != null) {
			ICompilationUnit cu = pack
					.getCompilationUnit(getCompilationUnitName(typeName));
			fCurrClass = cu.getType(typeName);
			IResource resource = cu.getResource();

			if (!resource.exists()) {
				status.setError("Class does not exist");
				return status;
			}
			URI location = resource.getLocationURI();
			if (location != null) {
				try {
					IFileStore store = EFS.getStore(location);
					if (!store.fetchInfo().exists()) {
						status.setError("Class does not exist");
						return status;
					}
				} catch (CoreException e) {
					status.setError(Messages
							.format(NewWizardMessages.NewTypeWizardPage_error_uri_location_unkown,
									BasicElementLabels.getURLPart(Resources
											.getLocationString(resource))));
				}
			}
			IStatus classSt = validateCorrectElementClass(fCurrPackage.getElementName()+"."+fCurrClass.getElementName(), project.getProject());
			//IStatus classSt = Checker.validateOrchestrationClass(fCurrPackage.getElementName()+"."+fCurrClass, project.getProject());
			if (!classSt.isOK()){
				status.setError(classSt.getMessage());
				return status;
			}
		}
		
		return status;
	}

	protected abstract IStatus validateCorrectElementClass(String string, IProject project);

	public String getClassName() {
		return fClassDialogField.getText();
	}

	private String getClassNameWithoutParameters() {
		String typeNameWithParameters = getClassName();
		int angleBracketOffset = typeNameWithParameters.indexOf('<');
		if (angleBracketOffset == -1) {
			return typeNameWithParameters;
		} else {
			return typeNameWithParameters.substring(0, angleBracketOffset);
		}
	}

	protected String getCompilationUnitName(String typeName) {
		return typeName + JavaModelUtil.DEFAULT_CU_SUFFIX;
	}

	/**
	 * The wizard owning this page is responsible for calling this method with
	 * the current selection. The selection is used to initialize the fields of
	 * the wizard page.
	 * 
	 * @param selection
	 *            used to initialize the fields
	 */

	public abstract ServiceElement generateElement();

	public abstract boolean isElementCompleted();

	public void addListeners() {
		oeNameText.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent arg0) {
				updateElementName();
			}
		});
		oeReturnTypeText.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent arg0) {
				updateReturnType();
			}
		});
		selectReturnButton.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {
				selectType();
			}

			@Override
			public void widgetSelected(SelectionEvent arg0) {
				selectType();
			}
		});
		// addModifiersListeners();
		addParametersListeners();
		addConstraintsListeners();
		addSpecificListeners();

	}

	protected abstract void addSpecificListeners();

	protected void addParametersListeners() {
		oeParametersTable.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {
				if (oeParametersTable.getSelectionIndex() < 0) {
					oeDeleteParamButton.setEnabled(false);
					oeModifyParamButton.setEnabled(false);
				} else {
					oeDeleteParamButton.setEnabled(true);
					oeModifyParamButton.setEnabled(true);
				}
			}

			@Override
			public void widgetSelected(SelectionEvent arg0) {
				if (oeParametersTable.getSelectionIndex() < 0) {
					oeDeleteParamButton.setEnabled(false);
					oeModifyParamButton.setEnabled(false);
				} else {
					oeDeleteParamButton.setEnabled(true);
					oeModifyParamButton.setEnabled(true);
				}

			}
		});
		oeAddParamButton.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {
				modifyParamTableItem(-1);
			}

			@Override
			public void widgetSelected(SelectionEvent arg0) {
				modifyParamTableItem(-1);
			}
		});
		oeModifyParamButton.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {
				modifyParamTableItem(oeParametersTable.getSelectionIndex());
			}

			@Override
			public void widgetSelected(SelectionEvent arg0) {
				modifyParamTableItem(oeParametersTable.getSelectionIndex());
			}
		});
		oeDeleteParamButton.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {
				removeParameter(oeParametersTable.getSelectionIndex());
			}

			@Override
			public void widgetSelected(SelectionEvent arg0) {
				removeParameter(oeParametersTable.getSelectionIndex());
			}
		});

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
					if (element != null) {
						element.getConstraints().remove(it.getText(0));
					}
				}
				it.setText(p);
				if (element != null) {
					element.getConstraints().put(p[0], p[1]);
				}
			}
		}
	}

	protected void removeConstraint(int selectionIndex) {
		if (element != null) {
			TableItem it = oeConstraintsTable.getItem(selectionIndex);
			element.getConstraints().remove(it.getText(0));
		}
		oeConstraintsTable.remove(selectionIndex);

	}

	protected void removeParameter(int i) {
		if (element != null) {
			TableItem it = oeParametersTable.getItem(i);
			element.getParameters().remove(it.getText(0));
		}
		oeParametersTable.remove(i);

	}

	protected abstract void modifyParamTableItem(int selection);

	// public abstract void addModifiersListeners();

	public void updateReturnType() {
		System.out.println("Updating return type");
		if (element != null) {
			element.setReturnType(oeReturnTypeText.getText());
		} else if (isElementCompleted()) {
			element = generateElement();
			fOEStatus.setOK();
			doStatusUpdate();
		} else {
			fOEStatus
					.setError("There are missing parameter to complete the Element information");
			doStatusUpdate();
		}
	}

	public void updateElementName() {
		System.out.println("Updating element name");
		if (element != null) {
			element.setMethodName(oeNameText.getText());
		} else if (isElementCompleted()) {
			element = generateElement();
			fOEStatus.setOK();
			doStatusUpdate();
		} else {
			fOEStatus
					.setError("There are missing parameter to complete the Element information");
			doStatusUpdate();
		}
	}

	protected void selectType() {
		if (getJavaProject() == null) {
			MessageDialog.openError(getShell(), "Error",
					"There is no java project selected");
		}

		IJavaElement[] elements = new IJavaElement[] { getJavaProject() };
		IJavaSearchScope scope = SearchEngine.createJavaSearchScope(elements);

		FilteredTypesSelectionDialog dialog = new FilteredTypesSelectionDialog(
				getShell(), false, getContainer(), scope,
				IJavaSearchConstants.CLASS);
		dialog.setTitle("Type Selection");
		dialog.setMessage("Select the class type");
		// dialog.setInitialPattern(getSuperClass());

		if (dialog.open() == Window.OK) {
			try {
				oeReturnTypeText.setText(((IType) (dialog.getFirstResult()))
						.getFullyQualifiedParameterizedName());
			} catch (JavaModelException e) {
				MessageDialog.openError(getShell(), "Error", e.getMessage());
			}
			// updateType();
		}

	}

	public ServiceElement getElement() {
		return element;
	}

	public void updateElement(ServiceElement el) {
		System.out.println("Updating element");
		this.element = el;
		if (pageCreated) {
			setPrintElementData();
		}

	}

	public void setInitialMethodName(String name) {
		this.elementName = name;
		if (pageCreated) {
			oeNameText.setText(name);
			;
		}

	}

}
