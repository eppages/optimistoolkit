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

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaConventions;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.compiler.IProblem;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.internal.corext.util.JavaConventionsUtil;
import org.eclipse.jdt.internal.corext.util.JavaModelUtil;
import org.eclipse.jdt.internal.corext.util.Messages;
import org.eclipse.jdt.internal.corext.util.Resources;
import org.eclipse.jdt.internal.ui.JavaPlugin;
import org.eclipse.jdt.internal.ui.dialogs.StatusInfo;
import org.eclipse.jdt.internal.ui.dialogs.TextFieldNavigationHandler;
import org.eclipse.jdt.internal.ui.preferences.CodeTemplatePreferencePage;
import org.eclipse.jdt.internal.ui.refactoring.contentassist.ControlContentAssistHelper;
import org.eclipse.jdt.internal.ui.refactoring.contentassist.JavaPackageCompletionProcessor;
import org.eclipse.jdt.internal.ui.refactoring.contentassist.JavaTypeCompletionProcessor;
import org.eclipse.jdt.internal.ui.util.SWTUtil;
import org.eclipse.jdt.internal.ui.viewsupport.BasicElementLabels;
import org.eclipse.jdt.internal.ui.wizards.NewWizardMessages;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.DialogField;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.IDialogFieldListener;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.IListAdapter;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.IStringButtonAdapter;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.ListDialogField;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.SelectionButtonDialogField;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.Separator;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.StringButtonDialogField;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.StringButtonStatusDialogField;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.StringDialogField;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.LayoutUtil;
import org.eclipse.jdt.ui.JavaElementLabelProvider;
import org.eclipse.jdt.ui.wizards.NewContainerWizardPage;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.IStructuredSelection;
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
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.ElementListSelectionDialog;
import org.eclipse.ui.dialogs.PreferencesUtil;

import es.bsc.servicess.ide.Checker;
import es.bsc.servicess.ide.TitlesAndConstants;

/**
 * 
 * @author Jorge Ejarque (Barcelona supercomputing Center
 *
 */
@SuppressWarnings("restriction")
public class ServiceSsNewServiceClassPage extends NewContainerWizardPage {
	
	private final static String PAGE_NAME = "NewServiceClassWizardPage";
	private StringButtonStatusDialogField fPackageDialogField;
	private boolean fCanModifyPackage;
	private IPackageFragment fCurrPackage;
	private IType fCurrType;
	private StringDialogField fTypeNameDialogField;
	private JavaPackageCompletionProcessor fCurrPackageCompletionProcessor;
	protected IStatus fPackageStatus;
	protected IStatus fTypeNameStatus;
	private Combo classType;
	private String classTypeString;

	protected final static String PACKAGE = PAGE_NAME + ".package"; //$NON-NLS-1$
	/** Field ID of the type name input field. */
	protected final static String TYPENAME = PAGE_NAME + ".typename"; //$NON-NLS-1$
	private static final String NORMAL_CLASS = "Standard Class";
	private static final String WS_CLASS = "Web Service Interface Class";
	//private static final String[] CLASS_TYPES = new String[]{NORMAL_CLASS, WS_CLASS};

	/**
	 * Constructor
	 */
	public ServiceSsNewServiceClassPage() {
		super(PAGE_NAME);
		setTitle(TitlesAndConstants.getNewClassPageWizardTitle());
		setDescription(TitlesAndConstants.getNewClassPageWizardDescription());

		TypeFieldsAdapter adapter = new TypeFieldsAdapter();
		fPackageDialogField = new StringButtonStatusDialogField(adapter);
		fPackageDialogField.setDialogFieldListener(adapter);
		fPackageDialogField
				.setLabelText(NewWizardMessages.NewTypeWizardPage_package_label);
		fPackageDialogField
				.setButtonLabel(NewWizardMessages.NewTypeWizardPage_package_button);
		fPackageDialogField
				.setStatusWidthHint(NewWizardMessages.NewTypeWizardPage_default);

		fTypeNameDialogField = new StringDialogField();
		fTypeNameDialogField.setDialogFieldListener(adapter);
		fTypeNameDialogField
				.setLabelText(NewWizardMessages.NewTypeWizardPage_typename_label);

		fCurrPackageCompletionProcessor = new JavaPackageCompletionProcessor();

		fPackageStatus = new StatusInfo();

		fTypeNameStatus = new StatusInfo();
		fCanModifyPackage = true;
		updateEnableState();

	}

	/**
	 * The wizard owning this page is responsible for calling this method with
	 * the current selection. The selection is used to initialize the fields of
	 * the wizard page.
	 * 
	 * @param selection
	 *            used to initialize the fields
	 */
	public void init(IStructuredSelection selection) {
		IJavaElement jelem = getInitialJavaElement(selection);
		initContainerPage(jelem);
		initTypePage(jelem);
		doStatusUpdate();
	}

	/**
	 * Initialize the class part in the wizard page.
	 * @param elem Selected java element
	 */
	protected void initTypePage(IJavaElement elem) {
		IJavaProject project = null;
		IPackageFragment pack = null;
		if (elem != null) {
			// evaluate the enclosing type
			project = elem.getJavaProject();
			IStatus status = Checker.validateServiceSsProject(project.getProject());
			if (!status.isOK()){
				setPackageFragment(null, false);
				setTypeName("", false);
				fContainerStatus = status;
			}
			pack = (IPackageFragment) elem
					.getAncestor(IJavaElement.PACKAGE_FRAGMENT);
		}
		String typeName = ""; //$NON-NLS-1$
		ITextSelection selection = getCurrentTextSelection();
		if (selection != null) {
			String text = selection.getText();
			if (text != null && validateJavaTypeName(text, project).isOK()) {
				typeName = text;
			}
		}
		setPackageFragment(pack, true);
		setTypeName(typeName, true);
	}

	public void setPackageFragment(IPackageFragment pack, boolean canBeModified) {
		fCurrPackage = pack;
		fCanModifyPackage = canBeModified;
		String str = (pack == null) ? "" : pack.getElementName(); //$NON-NLS-1$
		fPackageDialogField.setText(str);
		updateEnableState();
	}

	public void setTypeName(String name, boolean canBeModified) {
		fTypeNameDialogField.setText(name);
		fTypeNameDialogField.setEnabled(canBeModified);
	}

	private void updateEnableState() {
		fPackageDialogField.setEnabled(fCanModifyPackage);
	}

	/**
	 * Validate a java class name.
	 * @param text class name.
	 * @param project Java Project.
	 * @return Status.OK if correct java class name, Status.ERROR if incorrect java class name.
	 */
	private static IStatus validateJavaTypeName(String text,
			IJavaProject project) {
		if (project == null || !project.exists()) {
			return JavaConventions.validateJavaTypeName(text,
					JavaCore.VERSION_1_3, JavaCore.VERSION_1_3);
		}
		return JavaConventionsUtil.validateJavaTypeName(text, project);
	}

	/**
	 * Validate the package name
	 * @param text Package name
	 * @param project Java Project
	 * @return Status.OK if correct package name, Status.ERROR if incorrect package name.
	 */
	private static IStatus validatePackageName(String text, IJavaProject project) {
		if (project == null || !project.exists()) {
			return JavaConventions.validatePackageName(text,
					JavaCore.VERSION_1_3, JavaCore.VERSION_1_3);
		}
		return JavaConventionsUtil.validatePackageName(text, project);
	}

	/**
	 * Do the page status update
	 */
	private void doStatusUpdate() {
		// status of all used components
		IStatus[] status = new IStatus[] { fContainerStatus, fPackageStatus,
				fTypeNameStatus, };

		// the mode severe status will be displayed and the OK button
		// enabled/disabled.
		updateStatus(status);
	}

	/*
	 * @see NewContainerWizardPage#handleFieldChanged
	 */
	protected void handleFieldChanged(String fieldName) {
		super.handleFieldChanged(fieldName);
		if (fieldName == CONTAINER) {
			fPackageStatus = packageChanged();

			fTypeNameStatus = typeNameChanged();

		}
		doStatusUpdate();
	}

	/**
	 * Creates a separator line. Expects a <code>GridLayout</code> with at least
	 * 1 column.
	 * 
	 * @param composite
	 *            the parent composite
	 * @param nColumns
	 *            number of columns to span
	 */
	protected void createSeparator(Composite composite, int nColumns) {
		(new Separator(SWT.SEPARATOR | SWT.HORIZONTAL)).doFillIntoGrid(
				composite, nColumns, convertHeightInCharsToPixels(1));
	}

	/**
	 * Creates the controls for the package name field. Expects a
	 * <code>GridLayout</code> with at least 4 columns.
	 * 
	 * @param composite
	 *            the parent composite
	 * @param nColumns
	 *            number of columns to span
	 */
	protected void createPackageControls(Composite composite, int nColumns) {
		fPackageDialogField.doFillIntoGrid(composite, nColumns);
		Text text = fPackageDialogField.getTextControl(null);
		LayoutUtil.setWidthHint(text, getMaxFieldWidth());
		LayoutUtil.setHorizontalGrabbing(text);
		ControlContentAssistHelper.createTextContentAssistant(text,
				fCurrPackageCompletionProcessor);
		TextFieldNavigationHandler.install(text);
	}

	/**
	 * Creates the controls for the type name field. Expects a
	 * <code>GridLayout</code> with at least 2 columns.
	 * 
	 * @param composite
	 *            the parent composite
	 * @param nColumns
	 *            number of columns to span
	 */
	protected void createTypeNameControls(Composite composite, int nColumns) {
		fTypeNameDialogField.doFillIntoGrid(composite, nColumns - 1);
		DialogField.createEmptySpace(composite);

		Text text = fTypeNameDialogField.getTextControl(null);
		LayoutUtil.setWidthHint(text, getMaxFieldWidth());
		TextFieldNavigationHandler.install(text);
		Label l = new Label(composite, SWT.NONE);
		l.setText("Type");
		classType = new Combo(composite, SWT.CHECK);
		classType.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent event){ 
				classTypeString = classType.getItem(classType.getSelectionIndex()); 
			} 
		});
		classType.setItems(TitlesAndConstants.getOrchestrationClassTypes());
		classType.select(0);
		classTypeString = classType.getItem(classType.getSelectionIndex());
	}

	public void createControl(Composite parent) {
		initializeDialogUnits(parent);

		Composite composite = new Composite(parent, SWT.NONE);
		composite.setFont(parent.getFont());

		int nColumns = 4;

		GridLayout layout = new GridLayout();
		layout.numColumns = nColumns;
		composite.setLayout(layout);

		// pick & choose the wanted UI components

		createContainerControls(composite, nColumns);
		createPackageControls(composite, nColumns);
		createTypeNameControls(composite, nColumns);

		setControl(composite);
		Dialog.applyDialogFont(composite);

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

	public String getPackageText() {
		return fPackageDialogField.getText();
	}

	public IPackageFragment getPackageFragment() {
		return fCurrPackage;
	}

	protected IStatus typeNameChanged() {
		StatusInfo status = new StatusInfo();
		fCurrType = null;
		String typeNameWithParameters = getTypeName();
		// must not be empty
		if (typeNameWithParameters.length() == 0) {
			status.setError(NewWizardMessages.NewTypeWizardPage_error_EnterTypeName);
			return status;
		}

		String typeName = getTypeNameWithoutParameters();
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

		// must not exist

		IPackageFragment pack = getPackageFragment();
		if (pack != null) {
			ICompilationUnit cu = pack
					.getCompilationUnit(getCompilationUnitName(typeName));
			fCurrType = cu.getType(typeName);
			IResource resource = cu.getResource();

			if (resource.exists()) {
				status.setError(NewWizardMessages.NewTypeWizardPage_error_TypeNameExists);
				return status;
			}
			URI location = resource.getLocationURI();
			if (location != null) {
				try {
					IFileStore store = EFS.getStore(location);
					if (store.fetchInfo().exists()) {
						status.setError(NewWizardMessages.NewTypeWizardPage_error_TypeNameExistsDifferentCase);
						return status;
					}
				} catch (CoreException e) {
					status.setError(Messages
							.format(NewWizardMessages.NewTypeWizardPage_error_uri_location_unkown,
									BasicElementLabels.getURLPart(Resources
											.getLocationString(resource))));
				}
			}
		}

		if (!typeNameWithParameters.equals(typeName) && project != null) {
			if (!JavaModelUtil.is50OrHigher(project)) {
				status.setError(NewWizardMessages.NewTypeWizardPage_error_TypeParameters);
				return status;
			}
			String typeDeclaration = "class " + typeNameWithParameters + " {}"; //$NON-NLS-1$//$NON-NLS-2$
			ASTParser parser = ASTParser.newParser(AST.JLS3);
			parser.setSource(typeDeclaration.toCharArray());
			parser.setProject(project);
			CompilationUnit compilationUnit = (CompilationUnit) parser
					.createAST(null);
			IProblem[] problems = compilationUnit.getProblems();
			if (problems.length > 0) {
				status.setError(Messages
						.format(NewWizardMessages.NewTypeWizardPage_error_InvalidTypeName,
								problems[0].getMessage()));
				return status;
			}
		}
		return status;
	}

	public String getTypeName() {
		return fTypeNameDialogField.getText();
	}

	private String getTypeNameWithoutParameters() {
		String typeNameWithParameters = getTypeName();
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

	private class TypeFieldsAdapter implements IStringButtonAdapter,
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

	/**
	 * @param field
	 */
	private void typePageChangeControlPressed(DialogField field) {
		if (field == fPackageDialogField) {
			IPackageFragment pack = choosePackage();
			if (pack != null) {
				fPackageDialogField.setText(pack.getElementName());
			}
		}
	}

	/**
	 * Open the dialog for changing the package
	 * @return Selected package
	 */
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

	/**
	 * Perform the action required when a class change button is pressed
	 * @param field
	 * @param index
	 */
	private void typePageCustomButtonPressed(DialogField field, int index) {
		//TODO provide type button action pressed
	}

	/**
	 * A field on the type has changed. The fields' status and all dependent
	 * status are updated.
	 * @param field Changed field
	 */
	private void typePageDialogFieldChanged(DialogField field) {
		String fieldName = null;
		if (field == fPackageDialogField) {
			fPackageStatus = packageChanged();
			updatePackageStatusLabel();
			fTypeNameStatus = typeNameChanged();
			fieldName = PACKAGE;
		} else {
			fTypeNameStatus = typeNameChanged();
			fieldName = TYPENAME;
		}
		// tell all others
		handleFieldChanged(fieldName);
	}

	/**
	 * Update the package status
	 */
	private void updatePackageStatusLabel() {
		String packName = getPackageText();

		if (packName.length() == 0) {
			fPackageDialogField
					.setStatus(NewWizardMessages.NewTypeWizardPage_default);
		} else {
			fPackageDialogField.setStatus(""); //$NON-NLS-1$
		}
	}

	public String getClassType() {
		return classTypeString;
	}
}
