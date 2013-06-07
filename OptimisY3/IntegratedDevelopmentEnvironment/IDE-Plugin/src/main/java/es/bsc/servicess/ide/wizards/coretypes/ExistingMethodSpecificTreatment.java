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

import java.util.ArrayList;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.Flags;
import org.eclipse.jdt.core.IClassFile;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IImportDeclaration;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.Signature;
import org.eclipse.jdt.core.search.IJavaSearchConstants;
import org.eclipse.jdt.core.search.IJavaSearchScope;
import org.eclipse.jdt.core.search.SearchEngine;
import org.eclipse.jdt.internal.core.JavaModelManager;
import org.eclipse.jdt.internal.ui.dialogs.FilteredTypesSelectionDialog;
import org.eclipse.jdt.ui.JavaElementLabelProvider;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.ElementListSelectionDialog;

import es.bsc.servicess.ide.Logger;
import es.bsc.servicess.ide.ProjectMetadata;
import es.bsc.servicess.ide.dialogs.JarFileDialog;
import es.bsc.servicess.ide.model.CoreElementParameter;
import es.bsc.servicess.ide.model.MethodCoreElement;
import es.bsc.servicess.ide.model.ServiceElement;
import es.bsc.servicess.ide.wizards.ServiceSsCoreElementSecondPage;
import es.bsc.servicess.ide.wizards.ServiceSsCoreElementWizardPage;
import es.bsc.servicess.ide.wizards.TypeSpecificTreatment;

public class ExistingMethodSpecificTreatment extends
		TypeSpecificTreatment {

	private MethodCoreElement element;
	private IType ceDCType;
	private IMethod ceMethod;
	private Text libraryLocation;
	private Button librarySelectButton;
	private Text declaringClass;
	private Button selectClassButton;
	private Text methodNameText;
	private Button selectMethodButton;
	private boolean libraryAdded;
	private String prev_jarLoc;
	private Logger log = Logger.getLogger(ExistingMethodSpecificTreatment.class);
	
	public ExistingMethodSpecificTreatment(
			ServiceSsCoreElementSecondPage secondPage, Shell shell) {
		super(secondPage, shell);
	}

	@Override
	public void updateControlsListeners() {
		librarySelectButton.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {
				newLibraryLocation();
			}

			@Override
			public void widgetSelected(SelectionEvent arg0) {
				newLibraryLocation();
			}
		});
		
		selectClassButton.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {
					selectCEClass();
			}

			@Override
			public void widgetSelected(SelectionEvent arg0) {
					selectCEClass();
			}
		});
		
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
		comp.setLayout(new GridLayout(2, false));
		Label libraryLabel = new Label(comp, SWT.NONE);
		libraryLabel.setText("Library location");
		Composite wsdlc = new Composite(comp, SWT.NONE);
		rd = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		rd.grabExcessHorizontalSpace = true;
		rd.horizontalIndent = 0;
		rd.horizontalSpan = 0;
		wsdlc.setLayoutData(rd);
		GridLayout oeReturnLayout = new GridLayout();
		oeReturnLayout.numColumns = 2;
		oeReturnLayout.marginLeft = 0;
		oeReturnLayout.marginRight = 0;
		wsdlc.setLayout(oeReturnLayout);
		libraryLocation = new Text(wsdlc, SWT.SINGLE | SWT.BORDER);
		rd = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		rd.grabExcessHorizontalSpace = true;
		libraryLocation.setLayoutData(rd);
		librarySelectButton = new Button(wsdlc, SWT.NONE);
		librarySelectButton.setText("Select...");

		// Declaring/class
		Label declaringClassLabel = new Label(comp, SWT.NONE);
		declaringClassLabel.setText("Declaring Class");
		Composite dc = new Composite(comp, SWT.NONE);
		rd = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		rd.grabExcessHorizontalSpace = true;
		rd.horizontalIndent = 0;
		rd.horizontalSpan = 0;
		dc.setLayoutData(rd);
		oeReturnLayout = new GridLayout();
		oeReturnLayout.numColumns = 2;
		oeReturnLayout.marginLeft = 0;
		oeReturnLayout.marginRight = 0;
		dc.setLayout(oeReturnLayout);
		declaringClass = new Text(dc, SWT.SINGLE | SWT.BORDER);
		rd = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		rd.grabExcessHorizontalSpace = true;
		declaringClass.setLayoutData(rd);
		selectClassButton = new Button(dc, SWT.NONE);
		selectClassButton.setText("Select...");
		
		Label methodLabel = new Label(comp, SWT.NONE);
		methodLabel.setText("Method");
		Composite method = new Composite(comp, SWT.NONE);
		rd = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		rd.grabExcessHorizontalSpace = true;
		rd.horizontalIndent = 0;
		rd.horizontalSpan = 0;
		method.setLayoutData(rd);
		oeReturnLayout = new GridLayout();
		oeReturnLayout.numColumns = 2;
		oeReturnLayout.marginLeft = 0;
		oeReturnLayout.marginRight = 0;
		method.setLayout(oeReturnLayout);
		methodNameText = new Text(method, SWT.SINGLE | SWT.BORDER);
		rd = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		rd.grabExcessHorizontalSpace = true;
		methodNameText.setLayoutData(rd);
		selectMethodButton = new Button(method, SWT.NONE);
		selectMethodButton.setText("Select...");
		return comp;
	}
	
	private void selectCEMethod() {
		if (ceDCType == null) {

			MessageDialog.openError(shell, "Error", "Getting the Class");
			return;
		}

		try {
			IMethod[] elements = ceDCType.getMethods();
			IJavaSearchScope scope = SearchEngine
					.createJavaSearchScope(elements);
			ElementListSelectionDialog dialog = new ElementListSelectionDialog(
					shell, new JavaElementLabelProvider(
							JavaElementLabelProvider.SHOW_DEFAULT));
			dialog.setIgnoreCase(false);
			dialog.setTitle("Choose Method");
			dialog.setMessage("Choose Method from the selected class");
			dialog.setEmptyListMessage("Empty");
			dialog.setElements(elements);
			dialog.setHelpAvailable(false);

			if (dialog.open() == Window.OK) {
				ceMethod = (IMethod) dialog.getFirstResult();
				if (!methodNameText.getText().trim()
						.equals(ceMethod.getElementName())) {
					methodNameText.setText(ceMethod.getElementName());
					updateMethodName();
				}
			} else {
				MessageDialog.openError(shell, "Error",
						"Getting the Class");

			}
		} catch (JavaModelException e) {
			MessageDialog.openError(shell, "Error", e.getMessage());
		}

	}
	
	
	
	protected void updateMethodName() {
		System.out.println("Updating Method Name");
		IWizardPage p = secondPage.getNextPage();
		if (element != null) {
			element.setMethodName(methodNameText.getText());
			((ServiceSsCoreElementWizardPage) p).setType(ServiceSsCoreElementSecondPage.METHOD_EXISTS);
			try {
				element = setCEMethodParameters(element);
				((ServiceSsCoreElementWizardPage) p).updateElement(element);
				secondPage.getCEStatus().setOK();
				secondPage.doStatusUpdate();

			} catch (JavaModelException e) {
				MessageDialog.openError(shell, "Error", e.getMessage());
			}
		} else if (isPageComplete()) {
			((ServiceSsCoreElementWizardPage) p).setType(ServiceSsCoreElementSecondPage.METHOD_EXISTS);
			
				try {
					element = (MethodCoreElement)generateCoreElement();
					element = setCEMethodParameters(element);
					((ServiceSsCoreElementWizardPage) p).updateElement(element);
					secondPage.getCEStatus().setOK();
					secondPage.doStatusUpdate();
				} catch (JavaModelException e) {
					MessageDialog
							.openError(shell, "Error", e.getMessage());
				}

		} else {
			secondPage.getCEStatus()
					.setError("There are missing parameters to complete the Element information");
			secondPage.doStatusUpdate();
		}

	}

	private MethodCoreElement setCEMethodParameters(MethodCoreElement el)
			throws JavaModelException {
		String[] paramNames = ceMethod.getParameterNames();
		String[] paramTypes = ceMethod.getParameterTypes();
		for (int i = 0; i < ceMethod.getNumberOfParameters(); i++) {
			// TODO Change to include the direction ( IN by default)
			CoreElementParameter p = new CoreElementParameter(
					getQualifiedName(paramTypes[i]), paramNames[i], "IN");
			el.getParameters().add(p);

		}
		return el;
	}

	private String getQualifiedName(String string) throws JavaModelException {
		if (string.equals(Signature.SIG_INT)
				|| string.equals(Signature.SIG_BOOLEAN)
				|| string.equals(Signature.SIG_BYTE)
				|| string.equals(Signature.SIG_CHAR)
				|| string.equals(Signature.SIG_DOUBLE)
				|| string.equals(Signature.SIG_LONG)
				|| string.equals(Signature.SIG_FLOAT)
				|| string.equals(Signature.SIG_VOID)
				|| string.equals(Signature.SIG_SHORT)) {
			return Signature.toString(string);
		} else {
			String qualifier = Signature.getQualifier(string);
			if (qualifier == null || (qualifier.length() == 0)) {
				log.debug("qualifier is null searching in imports."
						+ Signature.toString(string));
				return seachQualifierForClass(Signature.toString(string));

			} else
				return Signature.toString(string);
		}

	}

	private String seachQualifierForClass(String string)
			throws JavaModelException {
		IImportDeclaration[] imports = ceDCType.getCompilationUnit()
				.getImports();
		for (IImportDeclaration imp : imports) {
			if (imp.getElementName().endsWith("." + string)) {
				return imp.getElementName();
			}
		}
		// Check if it from the same class path, same package or java.lang
		IType t = ceDCType.getCompilationUnit().getType(string);
		if (t != null && t.exists()) {
			return t.getFullyQualifiedName();
		}

		IClassFile cf = ceDCType.getPackageFragment().getClassFile(
				string + ".class");
		if (cf != null && cf.exists()) {
			return cf.getType().getFullyQualifiedName();
		}
		for (ICompilationUnit cu : ceDCType.getPackageFragment()
				.getCompilationUnits()) {
			IType tt = cu.getType(string);
			if (tt != null && tt.exists()) {
				return tt.getFullyQualifiedName();
			}
		}
		return "java.lang." + string;
	}

	private void selectCEClass() {
		IJavaElement[] elements;
		final IJavaProject project = secondPage.getJavaProject();
		if (project == null) {
			MessageDialog.openError(shell, "Error",
					"Project is not selected");
			return;
		}
		IJavaElement el;

		if (libraryLocation == null || libraryLocation.getText().length() <= 0) {
			if (secondPage.getOrchestrationClass()!= null){
				try {
					elements = findElementsToSerach(project, secondPage.getOrchestrationClass().getLibraryLocation());
				} catch (JavaModelException e) {
					e.printStackTrace();
					MessageDialog.openError(shell, "Error",
							"Error opening jar location");
					return;

				} catch (CoreException e) {
					e.printStackTrace();
					MessageDialog.openError(shell, "Error",
							"Error opening jar location");
					return;
				}
			}else{
				IPackageFragmentRoot root = secondPage.getPackageFragmentRoot();
				if (root == null) {
					el = project;
				} else
					el = root;
				elements = new IJavaElement[] { el };
			}
		} else {
			try {
				elements = findElementsToSerach(project, libraryLocation.getText());
			} catch (JavaModelException e) {
				e.printStackTrace();
				MessageDialog.openError(shell, "Error",
						"Error opening jar location");
				return;

			} catch (CoreException e) {
				e.printStackTrace();
				MessageDialog.openError(shell, "Error",
						"Error opening jar location");
				return;
			}
		}
		if (elements == null || elements.length <= 0) {
			MessageDialog.openError(shell, "Error",
					"Getting java elements");
			return;
		}
		IJavaSearchScope scope = SearchEngine.createJavaSearchScope(elements);
		FilteredTypesSelectionDialog dialog = new FilteredTypesSelectionDialog(
				shell, false, secondPage.getWizard().getContainer(), scope,
				IJavaSearchConstants.CLASS);
		dialog.setTitle("Class Selection");
		dialog.setMessage("Select a Class");
		dialog.setInitialPattern(declaringClass.getText().trim());
		if (declaringClass.getText().trim().length() > 0) {
			dialog.setInitialPattern(declaringClass.getText().trim());
		} else
			dialog.setInitialPattern("?");
		if (dialog.open() == Window.OK) {
			ceDCType = (IType) dialog.getFirstResult();
			if (!declaringClass.getText().trim()
					.equals(ceDCType.getElementName())) {
				declaringClass.setText(ceDCType.getFullyQualifiedName());
				methodNameText.setText("");
				element = null;
				updateDeclaringClass();
			}
		} else {
			/*
			 * TODO if (libraryAdded){ cont.removeEntry(entry); libraryAdded =
			 * false; cont = null; entry = null; }
			 */
		}

	}
	
	private IJavaElement[] findElementsToSerach(IJavaProject project, String libraryLoc) throws CoreException{

		project.getProject().refreshLocal(IResource.DEPTH_INFINITE,
				null);
		ArrayList<IPackageFragmentRoot> pfrs = new ArrayList<IPackageFragmentRoot>();
		for (IPackageFragmentRoot r : project
				.getAllPackageFragmentRoots()) {
			log.debug("PFR: " + r.getElementName()+ " entry: "
					+ r.getResolvedClasspathEntry().getPath());
			if (r.getResolvedClasspathEntry().getPath().toOSString()
					.trim().equals(libraryLoc.trim())) {
				pfrs.add(r);
			}
		}
		return pfrs.toArray(new IPackageFragmentRoot[pfrs.size()]);
	}

	protected void updateDeclaringClass() {
		if (element != null) {
			element.setDeclaringClass(methodNameText.getText());
			IWizardPage p = secondPage.getNextPage();
			((ServiceSsCoreElementWizardPage) p).updateElement(element);
		} else if (isPageComplete()) {
			IWizardPage p = secondPage.getNextPage();
			((ServiceSsCoreElementWizardPage) p).setType(ServiceSsCoreElementSecondPage.METHOD_EXISTS);
				try {
					element = (MethodCoreElement) generateCoreElement();
					((ServiceSsCoreElementWizardPage) p).updateElement(element);
					secondPage.getCEStatus().setOK();
					secondPage.doStatusUpdate();
				} catch (JavaModelException e) {
					MessageDialog
							.openError(shell, "Error", e.getMessage());
				}

		} else {
			secondPage.getCEStatus()
					.setError("There are missing parameters to complete the Element information");
			secondPage.doStatusUpdate();
		}

	}
	protected void newLibraryLocation() {
		// Con JarFile Dialog
		String defaultPath=null;
		if (secondPage.getOrchestrationClass()!=null){
			 defaultPath = secondPage.getOrchestrationClass().getLibraryLocation();
			 String workspaceLocation = secondPage.getJavaProject().getProject().getWorkspace().
					 getRoot().getRawLocation().toOSString();
			 defaultPath = workspaceLocation.concat(defaultPath);
			 log.debug("Workspace Location: " + workspaceLocation);
			 log.debug("Path to open library editor is " +defaultPath);
		}else
			log.debug("Orchestration class is null");
		JarFileDialog dialog = new JarFileDialog(shell,
				secondPage.getJavaProject(), libraryAdded, prev_jarLoc,
				new String[]{ProjectMetadata.CLASS_FOLDER_DEP_TYPE, 
						ProjectMetadata.JAR_DEP_TYPE}, defaultPath, false);
		if (dialog.open() == Window.OK) {
			libraryLocation.setText(dialog.getPath());
			libraryAdded = dialog.isLibraryAdded();
			prev_jarLoc = dialog.getPath();
		}
	}
	
	public void updateDependency(String dir) {
		try {
			IClasspathEntry ent = JavaCore.newLibraryEntry(new Path(dir), null,
					null, true);
			if (ent != null) {
				IFile m_file = secondPage.getJavaProject().getProject()
						.getFolder(ProjectMetadata.METADATA_FOLDER)
						.getFile(ProjectMetadata.METADATA_FILENAME);
				ProjectMetadata pr_meta = new ProjectMetadata(m_file
						.getRawLocation().toFile());
				if (libraryAdded) {
					log.debug("Library " + prev_jarLoc + " removed");
					pr_meta.removeDependency(prev_jarLoc);
				}
				if (!pr_meta
						.existsDependency(dir, ProjectMetadata.JAR_DEP_TYPE)) {
					pr_meta.addDependency(dir, ProjectMetadata.JAR_DEP_TYPE);
					libraryAdded = true;
					log.debug("Library " + dir + " Added");
				} else {
					log.debug("Library " + dir + " already exists");
					libraryAdded = false;
				}
				pr_meta.toFile(m_file.getRawLocation().toFile());
				m_file.refreshLocal(1, null);
				JavaModelManager manager = JavaModelManager
						.getJavaModelManager();
				manager.loadVariablesAndContainers();
				log.debug(" Dependency loaded");
				libraryLocation.setText(dir);
				prev_jarLoc = dir;
			} else {
				MessageDialog.openError(shell, "Error updating libraries",
						"We can not create a libray entry for " + dir);
			}

		} catch (Exception e) {
			MessageDialog.openError(shell, "Error updatin libraries",
					e.getMessage());
		}
	}
	
	@Override
	public ServiceElement generateCoreElement() throws JavaModelException {
		MethodCoreElement el = new MethodCoreElement(methodNameText.getText().trim(),
				Flags.AccPublic,
				getQualifiedName(ceMethod.getReturnType()), ceMethod,
				declaringClass.getText().trim(), true, false);
		return el;
	}

	@Override
	public boolean isPageComplete() {
		if (methodNameText.getText().trim().length() > 0
				&& declaringClass.getText().trim().length() > 0) {
			return true;
		}
		return false;
	}
	
	public boolean islibraryAdded() {
		return libraryAdded;
	}

	public void removeAddedLibrary() {

		if (libraryAdded) {
			try {
				IFile m_file = secondPage.getJavaProject().getProject()
						.getFolder(ProjectMetadata.METADATA_FOLDER)
						.getFile(ProjectMetadata.METADATA_FILENAME);
				ProjectMetadata pr_meta = new ProjectMetadata(m_file
						.getRawLocation().toFile());
				pr_meta.removeDependency(prev_jarLoc);
				pr_meta.toFile(m_file.getRawLocation().toFile());
				log.debug("Library " + prev_jarLoc + " removed");
				libraryAdded = false;
			} catch (Exception e) {
				log.error("Error updating project");
				e.printStackTrace();
			}
		} else {
			log.error("There are not library to remove");
		}

	}
	
	public void addElementToDependency() {
		if (prev_jarLoc != null && prev_jarLoc.trim().length() > 0) {
			try {
				IFile m_file = secondPage.getJavaProject().getProject()
						.getFolder(ProjectMetadata.METADATA_FOLDER)
						.getFile(ProjectMetadata.METADATA_FILENAME);
				ProjectMetadata pr_meta = new ProjectMetadata(m_file
						.getRawLocation().toFile());
				pr_meta.addElementToDependency(prev_jarLoc.trim(),
						ProjectMetadata.JAR_DEP_TYPE, element.getLabel());
				pr_meta.toFile(m_file.getRawLocation().toFile());
			} catch (Exception e) {
				log.error("Error adding dependency to element");
				e.printStackTrace();
			}
		}
	}

	@Override
	public void performCancel() {
		if (islibraryAdded()) {
			removeAddedLibrary();
		}
	}

	@Override
	public void performFinish(IProgressMonitor monitor) {
		addElementToDependency();
		
	}

}
