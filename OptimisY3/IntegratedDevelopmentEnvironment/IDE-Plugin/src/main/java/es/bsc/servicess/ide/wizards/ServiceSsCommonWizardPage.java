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
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jdt.core.Flags;
import org.eclipse.jdt.core.ICompilationUnit;
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

import es.bsc.servicess.ide.dialogs.ModifyConstraintsDialog;
import es.bsc.servicess.ide.dialogs.ModifyParameterDialog;
import es.bsc.servicess.ide.model.OrchestrationElement;
import es.bsc.servicess.ide.model.Parameter;
import es.bsc.servicess.ide.model.ServiceElement;

@SuppressWarnings("restriction")
public abstract class ServiceSsCommonWizardPage extends NewContainerWizardPage {
	private boolean isClass = true;
	private StringButtonStatusDialogField fPackageDialogField;
	private StringButtonStatusDialogField fClassDialogField;
	protected StatusInfo fPackageStatus;
	protected StatusInfo fClassStatus;
	private boolean fCanModifyPackage;
	private boolean fCanModifyClass;
	private IPackageFragment fCurrPackage;
	private JavaPackageCompletionProcessor fCurrPackageCompletionProcessor;
	private IType fCurrClass;
	protected ServiceElement element = null;
	protected Group group;
	private Composite root;

	public ServiceSsCommonWizardPage(String name, String title,
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
		fCanModifyPackage = true;
		fCanModifyClass = true;
		updateEnableState();
		// setPageComplete(false);
	}

	public void createControl(Composite parent) {
		initializeDialogUnits(parent);

		root = new Composite(parent, SWT.NONE);
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

		group = new Group(root, SWT.NONE);
		group.setLayout(new GridLayout(2, false));
		group.setText("Element description");
		GridData rd = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		rd.grabExcessHorizontalSpace = true;
		group.setLayoutData(rd);

		// pick & choose the wanted UI components

		createContainerControls(location, nColumnsLocation);
		createPackageControls(location, nColumnsLocation);
		createClassControls(location, nColumnsLocation);
		createExtraControls(group);
		addExtraListeners();
		setControl(root);
		Dialog.applyDialogFont(root);

	}

	protected abstract void createExtraControls(Group group);

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
			System.out.println("Element name:" + elem.getElementName()
					+ "type: " + elem.getElementType());
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
	protected void doStatusUpdate() {
		// status of all used components
		IStatus[] status = new IStatus[] { fContainerStatus, fPackageStatus,
				fClassStatus };

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
			IType type = chooseType();
			setClassName(type, fCanModifyClass);
		}
	}

	protected IType chooseType() {
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

		// TODO must exist and validate it is Service Class

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
		}
		/*
		 * TODO check it if (!typeNameWithParameters.equals(typeName) && project
		 * != null) { if (!JavaModelUtil.is50OrHigher(project)) {
		 * status.setError
		 * (NewWizardMessages.NewTypeWizardPage_error_TypeParameters); return
		 * status; } String typeDeclaration= "class " + typeNameWithParameters +
		 * " {}"; //$NON-NLS-1$//$NON-NLS-2$ ASTParser parser=
		 * ASTParser.newParser(AST.JLS3);
		 * parser.setSource(typeDeclaration.toCharArray());
		 * parser.setProject(project); CompilationUnit compilationUnit=
		 * (CompilationUnit) parser.createAST(null); IProblem[] problems=
		 * compilationUnit.getProblems(); if (problems.length > 0) {
		 * status.setError(Messages.format(NewWizardMessages.
		 * NewTypeWizardPage_error_InvalidTypeName, problems[0].getMessage()));
		 * return status; } }
		 */
		return status;
	}

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

	public abstract void addExtraListeners();

	public void setClassLabel(String string, boolean isClass) {
		this.isClass = isClass;
		fClassDialogField.setLabelText(string);
	}
	
	public Composite getRootComposite(){
		return this.root;
	}

}
