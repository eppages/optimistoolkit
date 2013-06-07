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

package es.bsc.servicess.ide.actions;

import java.io.File;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.ui.JavaPlugin;
import org.eclipse.jdt.internal.ui.dialogs.StatusInfo;
import org.eclipse.jdt.internal.ui.viewsupport.IViewPartInputProvider;
import org.eclipse.jdt.ui.JavaElementLabelProvider;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.dialogs.ElementListSelectionDialog;
import org.eclipse.ui.views.contentoutline.ContentOutline;
import es.bsc.servicess.ide.PackagingUtils;
import es.bsc.servicess.ide.ProjectMetadata;


/** Action to build the deployed service
 * @author jorgee
 *
 */
public class BuildServiceAction implements IWorkbenchWindowActionDelegate {
	private IStructuredSelection selection;
	private IWorkbenchWindow window;

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IActionDelegate#run(org.eclipse.jface.action.IAction)
	 */
	@Override
	public void run(IAction arg0) {
		IJavaProject project = null;
		if (selection != null) {
			IJavaElement el = getInitialJavaElement(selection);
			if (el != null) {
				project = el.getJavaProject();
			}
		}
		if (project == null) {
			IJavaProject[] projects;
			try {
				projects = JavaCore.create(
						ResourcesPlugin.getWorkspace().getRoot())
						.getJavaProjects();

				ElementListSelectionDialog dialog = new ElementListSelectionDialog(
						window.getShell(), new JavaElementLabelProvider(
								JavaElementLabelProvider.SHOW_DEFAULT));
				dialog.setIgnoreCase(false);
				dialog.setTitle("Choose Project");
				dialog.setMessage("Choose Project to build");
				dialog.setEmptyListMessage("Empty");
				dialog.setElements(projects);
				dialog.setHelpAvailable(false);
				if (dialog.open() == Window.OK) {
					project = (IJavaProject) dialog.getFirstResult();
				} else {
					ErrorDialog.openError(window.getShell(), "Error",
							"Getting the Project", new StatusInfo(IStatus.ERROR, "Error getting the project"));
				}
			} catch (JavaModelException e) {
				ErrorDialog.openError(window.getShell(), "Error",
						e.getMessage(), new StatusInfo(IStatus.ERROR, "Ezception during service building"));
			}
		}
		if (project != null) {
			IProgressMonitor myProgressMonitor = new NullProgressMonitor();
			try {
				//TODO Y3: Add the automatic package grouping.
				IFile file = project.getProject().getFolder(ProjectMetadata.METADATA_FOLDER)
				.getFile(ProjectMetadata.METADATA_FILENAME);
				
				ProjectMetadata pr_meta = new ProjectMetadata(new File(file.getRawLocation().toOSString()));
				//TODO Y3: Add progress monitor
				PackagingUtils.buildPackages(project, pr_meta, myProgressMonitor);

			} catch (Exception e) {
				ErrorDialog.openError(window.getShell(), "Error Building Service",
						e.getMessage(),new StatusInfo(IStatus.ERROR, "Exception during service buildinge"));
			} 

		}

	}


	/* (non-Javadoc)
	 * @see org.eclipse.ui.IActionDelegate#selectionChanged(org.eclipse.jface.action.IAction, org.eclipse.jface.viewers.ISelection)
	 */
	@Override
	public void selectionChanged(IAction arg0, ISelection arg1) {
		if (arg1 instanceof IStructuredSelection) {
			selection = (IStructuredSelection) arg1;
		}

	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IWorkbenchWindowActionDelegate#dispose()
	 */
	@Override
	public void dispose() {
		
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IWorkbenchWindowActionDelegate#init(org.eclipse.ui.IWorkbenchWindow)
	 */
	@Override
	public void init(IWorkbenchWindow arg0) {
		this.window = arg0;

	}

	/** Get a Java Element from a selection
	 * @param selection Selected item
	 * @return Java Element
	 */
	private IJavaElement getInitialJavaElement(IStructuredSelection selection) {
		IJavaElement jelem = null;
		if (selection != null && !selection.isEmpty()) {
			Object selectedElement = selection.getFirstElement();
			if (selectedElement instanceof IAdaptable) {
				IAdaptable adaptable = (IAdaptable) selectedElement;

				jelem = (IJavaElement) adaptable.getAdapter(IJavaElement.class);
				if (jelem == null || !jelem.exists()) {
					jelem = null;
					IResource resource = (IResource) adaptable
							.getAdapter(IResource.class);
					if (resource != null
							&& resource.getType() != IResource.ROOT) {
						while (jelem == null
								&& resource.getType() != IResource.PROJECT) {
							resource = resource.getParent();
							jelem = (IJavaElement) resource
									.getAdapter(IJavaElement.class);
						}
						if (jelem == null) {
							jelem = JavaCore.create(resource); // java project
						}
					}
				}
			}
		}
		if (jelem == null) {
			IWorkbenchPart part = JavaPlugin.getActivePage().getActivePart();
			if (part instanceof ContentOutline) {
				part = JavaPlugin.getActivePage().getActiveEditor();
			}

			if (part instanceof IViewPartInputProvider) {
				Object elem = ((IViewPartInputProvider) part)
						.getViewPartInput();
				if (elem instanceof IJavaElement) {
					jelem = (IJavaElement) elem;
				}
			}
		}
		return jelem;
	}
}
