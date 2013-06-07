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

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.eclipse.jdt.ui.IPackagesViewPart;
import org.eclipse.jdt.ui.wizards.NewJavaProjectWizardPageOne;
import org.eclipse.jdt.ui.wizards.NewJavaProjectWizardPageTwo;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.*;
import org.eclipse.ui.*;
import org.eclipse.ui.wizards.newresource.BasicNewProjectResourceWizard;
import org.eclipse.jdt.core.IClasspathContainer;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.core.JavaModelManager;
import org.eclipse.jdt.internal.ui.packageview.PackageExplorerPart;
import org.eclipse.jdt.internal.ui.util.ExceptionHandler;
import org.eclipse.jdt.internal.ui.wizards.NewElementWizard;
import org.eclipse.jdt.internal.ui.wizards.NewWizardMessages;
import org.eclipse.jface.dialogs.MessageDialog;

import es.bsc.servicess.ide.DependenciesClasspathContainer;
import es.bsc.servicess.ide.ProjectMetadata;
import es.bsc.servicess.ide.TitlesAndConstants;

/**This wizard allows the user to create a new service project.
 * 
 * @author Jorge Ejarque (Barcelona Supercomputing Center)
 */
public class ServiceSsNewProjectWizard extends NewElementWizard implements
		IExecutableExtension {

	private ServiceSsNewProjectWizardPage page0;
	private NewJavaProjectWizardPageOne page1;
	private NewJavaProjectWizardPageTwo page2;
	private IConfigurationElement fConfigElement;

	/**
	 * Constructor for the Service Project wizard.
	 */
	public ServiceSsNewProjectWizard() {

		setWindowTitle(TitlesAndConstants.getNewProjectWizardTitle());
	}

	/**
	 * Adding the pages to the wizard.
	 */
	public void addPages() {
		page0 = new ServiceSsNewProjectWizardPage();
		page1 = new NewJavaProjectWizardPageOne();
		page2 = new NewJavaProjectWizardPageTwo(page1);
		addPage(page0);
		addPage(page1);
		addPage(page2);
	}

	/**
	 * This method is called when 'Finish' button is pressed in the wizard. We
	 * will create an operation and run it using wizard as execution context.
	 */
	public boolean performFinish() {
		boolean res = super.performFinish();
		if (res) {
			final IJavaElement newElement = getCreatedElement();

			IWorkingSet[] workingSets = page1.getWorkingSets();
			if (workingSets.length > 0) {
				PlatformUI.getWorkbench().getWorkingSetManager()
						.addToWorkingSets(newElement, workingSets);
			}

			BasicNewProjectResourceWizard.updatePerspective(fConfigElement);
			selectAndReveal(page2.getJavaProject().getProject());

			Display.getDefault().asyncExec(new Runnable() {
				public void run() {
					IWorkbenchPart activePart = getActivePart();
					if (activePart instanceof IPackagesViewPart) {
						PackageExplorerPart view = PackageExplorerPart
								.openInActivePerspective();
						view.tryToReveal(newElement);
					}
				}
			});
			IFile f = page2.getJavaProject().getProject()
					.getFolder(ProjectMetadata.METADATA_FOLDER)
					.getFile(ProjectMetadata.METADATA_FILENAME);
			if (f != null && f.exists()) {
				openResource(f);
			}
		}
		return res;

	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.wizard.Wizard#performCancel()
	 */
	public boolean performCancel() {
		page2.performCancel();
		return super.performCancel();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.IExecutableExtension#setInitializationData(org.eclipse.core.runtime.IConfigurationElement, java.lang.String, java.lang.Object)
	 */
	@Override
	public void setInitializationData(IConfigurationElement arg0, String arg1,
			Object arg2) throws CoreException {
		fConfigElement = arg0;

	}

	/* (non-Javadoc)
	 * @see org.eclipse.jdt.internal.ui.wizards.NewElementWizard#finishPage(org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	protected void finishPage(IProgressMonitor arg0) {
		IJavaProject project;
		try {
			page2.performFinish(arg0);
			project = page2.getJavaProject();
			IPackageFragmentRoot[] pfr = project.getPackageFragmentRoots();

			if (pfr.length > 0) {
				// TODO: Try another way to get the main package fragment root, currently prf[0]
				IPackageFragment frag = pfr[0].createPackageFragment(
						page0.getPackageName(), true, arg0);
				IPackageFragment ce_frag = pfr[0].createPackageFragment(
						page0.getPackageName() + ".coreelements", true, arg0);
				System.out.println("Created packages: " + frag.getElementName()
						+ ", " + ce_frag.getElementName());
				IFolder out_folder = project.getProject().getFolder(
						ProjectMetadata.OUTPUT_FOLDER);
				out_folder.create(true, true, arg0);
				System.out.println("Folder created: "
						+ out_folder.getFullPath().toOSString());
				IFolder classes_folder = out_folder
						.getFolder(ProjectMetadata.CLASSES_FOLDER);
				classes_folder.create(true, true, arg0);
				System.out.println("Folder created: "
						+ classes_folder.getFullPath().toOSString());
				project.setOutputLocation(classes_folder.getFullPath(), arg0);
				System.out.println("OutpuLocation: "
						+ project.getOutputLocation().toOSString());
				IFolder folder = project.getProject().getFolder(
						ProjectMetadata.METADATA_FOLDER);
				folder.create(true, true, arg0);

				IFile meta = folder.getFile(ProjectMetadata.METADATA_FILENAME);
				ProjectMetadata pr_meta;
				pr_meta = new ProjectMetadata(page1.getProjectName());
				pr_meta.setRuntimeLocation(page0.getRuntimeLocation());
				pr_meta.setSourceDir(pfr[0].getElementName());
				pr_meta.setMainPackageName(page0.getPackageName());
				pr_meta.addDependency(page0.getRuntimeLocation()
						+ ProjectMetadata.ITJAR_EXT,
						ProjectMetadata.JAR_DEP_TYPE);
				System.out.println(pr_meta.getString());
				meta.create(new ByteArrayInputStream(pr_meta.getString()
						.getBytes()), true, arg0);
				createClasspathEntries(project, arg0);
				IFile projectFile = project.getProject().getFile(
						frag.getPath()
								.makeRelativeTo(
										project.getProject().getFullPath())
								.append("project.xml"));
				projectFile.create(initialProjectStream(), true, arg0);
				IFile resourcesFile = project.getProject().getFile(
						frag.getPath()
								.makeRelativeTo(
										project.getProject().getFullPath())
								.append("resources.xml"));
				resourcesFile.create(initialResourcesStream(), true, arg0);

			} else {
				MessageDialog.openError(getShell(),
						"Error creating metadata file ",
						"There are not enough fragment roots for the project");
				page2.performCancel();
			}
			
		} catch (InterruptedException e) {
			MessageDialog.openError(getShell(), "Error creating project ",
					e.getMessage());
			page2.performCancel();
			// project.getProject().delete(true, arg0);
			e.printStackTrace();
		} catch (CoreException e) {
			MessageDialog.openError(getShell(),
					"Error creating project elements ", e.getMessage());
			page2.performCancel();
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			MessageDialog.openError(getShell(),
					"Error creating metadata file ", e.getMessage());
			page2.performCancel();
			e.printStackTrace();
		} catch (TransformerException e) {
			MessageDialog.openError(getShell(),
					"Error creating metadata file ", e.getMessage());
			page2.performCancel();
			e.printStackTrace();
		}

	}

	/** Get the content of a empty Project file
	 * @return Input Stream with the content of an empty project file.
	 */
	private InputStream initialProjectStream() {
		String contents = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
				+ "<Project>\n" + "</Project>";
		return new ByteArrayInputStream(contents.getBytes());
	}

	/**Get the content of a empty Resources file.
	 * @return Input Stream with the content of an empty Resources file.
	 */
	private InputStream initialResourcesStream() {
		String contents = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
				+ "<ResourceList>\n" + "</ResourceList>";
		return new ByteArrayInputStream(contents.getBytes());
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jdt.internal.ui.wizards.NewElementWizard#getCreatedElement()
	 */
	@Override
	public IJavaElement getCreatedElement() {
		return page2.getJavaProject();
	}

	/**Get the part of the eclipse workbench which is currently active.
	 * @return part of the eclipse workbench which is currently active
	 */
	private IWorkbenchPart getActivePart() {
		IWorkbenchWindow activeWindow = getWorkbench()
				.getActiveWorkbenchWindow();
		if (activeWindow != null) {
			IWorkbenchPage activePage = activeWindow.getActivePage();
			if (activePage != null) {
				return activePage.getActivePart();
			}
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jdt.internal.ui.wizards.NewElementWizard#handleFinishException(org.eclipse.swt.widgets.Shell, java.lang.reflect.InvocationTargetException)
	 */
	@Override
	protected void handleFinishException(Shell shell,
			InvocationTargetException e) {
		String title = NewWizardMessages.JavaProjectWizard_op_error_title;
		String message = NewWizardMessages.JavaProjectWizard_op_error_create_message;
		ExceptionHandler.handle(e, getShell(), title, message);
	}

	/**Create class path entries for the new created project.
	 * @param project Project created
	 * @param monitor Eclipse progress monitor
	 * @throws CoreException
	 */
	protected void createClasspathEntries(IJavaProject project,
			IProgressMonitor monitor) throws CoreException {
		IClasspathEntry[] entries = project.getRawClasspath();
		IClasspathEntry[] new_entries = new IClasspathEntry[entries.length + 1];
		for (int i = 0; i < entries.length; i++) {
			new_entries[i] = entries[i];
		}
		new_entries[entries.length] = JavaCore.newContainerEntry(new Path(
				ProjectMetadata.DEPENDENCY_ENTRYPATH));
		project.setRawClasspath(new_entries, monitor);
		JavaCore.setClasspathContainer(new Path(
				ProjectMetadata.DEPENDENCY_ENTRYPATH),
				new IJavaProject[] { project }, // value for 'myProject'
				new IClasspathContainer[] { new DependenciesClasspathContainer(
						project) }, monitor);
		project.getProject().refreshLocal(IResource.DEPTH_INFINITE, monitor);
	}

}