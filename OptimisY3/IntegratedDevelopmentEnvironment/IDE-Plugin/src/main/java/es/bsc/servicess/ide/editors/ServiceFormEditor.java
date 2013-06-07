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

import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.core.internal.resources.Resource;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.*;
import org.eclipse.ui.editors.text.TextEditor;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.ide.IDE;
import org.eclipse.wst.sse.ui.StructuredTextEditor;
import org.xml.sax.SAXException;

import es.bsc.servicess.ide.Activator;
import es.bsc.servicess.ide.Logger;
import es.bsc.servicess.ide.ProjectMetadata;


public class ServiceFormEditor extends FormEditor implements IResourceChangeListener {

	private TextEditor metadataEditor;

	private StructuredTextEditor projectEditor;

	private StructuredTextEditor resourcesEditor;

	/*private IFile metadataFile;*/

	private IFile projectFile;

	private IFile resourcesFile;

	private IJavaProject project;

	private IPackageFragmentRoot frag_root;

	private IPackageFragment frag;

	private ImplementationFormPage formPage;

	private BuildingDeploymentFormPage deploymentPage;
	
	private static Logger log = Logger.getLogger(ServiceFormEditor.class);
	

	/**
	 * Creates the service editor.
	 */
	public ServiceFormEditor() {
		super();
		ResourcesPlugin.getWorkspace().addResourceChangeListener(this, IResourceChangeEvent.POST_CHANGE);
	}

	/**
	 * Creates the metadata page
	 */
	void createPageMetadata() {
		try {
			metadataEditor = new TextEditor();
			int index = addPage(metadataEditor, new FileEditorInput(
					getMetadataFile()));
			setPageText(index, getMetadataFile().getName());
		} catch (PartInitException e) {
			ErrorDialog.openError(getSite().getShell(),
					"Error creating nested text editor", e.getMessage(), e.getStatus());
		}
	}

	/**
	 * Create the project page
	 */
	void createPageProjects() {
		try {
			checkFile(projectFile);
			projectEditor = new StructuredTextEditor();
			int index = addPage(projectEditor, new FileEditorInput(projectFile));
			setPageText(index, projectFile.getName());
		} catch (PartInitException e) {
			ErrorDialog.openError(getSite().getShell(),
					"Error creating nested structured text editor", e.getMessage(),
					e.getStatus());
		}
	}

	/**
	 * Create the resource page
	 */
	void createPageResources() {
		try {
			checkFile(resourcesFile);
			resourcesEditor = new StructuredTextEditor();
			int index = addPage(resourcesEditor, new FileEditorInput(
					resourcesFile));
			setPageText(index, resourcesFile.getName());
		} catch (PartInitException e) {
			ErrorDialog.openError(getSite().getShell(),
					"Error creating Resources editor", e.getMessage(),
					e.getStatus());
		}
	}

	/** Check if file exists
	 * @param resourcesFile2
	 * @throws PartInitException
	 */
	private void checkFile(IFile resourcesFile2) throws PartInitException {
		if (resourcesFile2 == null)
			throw new PartInitException("File not found");

	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.forms.editor.FormEditor#addPages()
	 */
	protected void addPages() {

		formPage = new ImplementationFormPage(this);
		deploymentPage = new BuildingDeploymentFormPage(this);
		try {
			addPage(formPage);
			addPage(deploymentPage);
			createPageMetadata();
			createPageProjects();
			createPageResources();
		} catch (PartInitException e) {
			ErrorDialog.openError(getSite().getShell(),
					"Error creating overview page", e.getMessage(),
					e.getStatus());
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.forms.editor.FormEditor#dispose()
	 */
	public void dispose() {
		super.dispose();
		ResourcesPlugin.getWorkspace().removeResourceChangeListener(this);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.EditorPart#doSave(org.eclipse.core.runtime.IProgressMonitor)
	 */
	public void doSave(IProgressMonitor monitor) {
		getEditor(1).doSave(monitor);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.EditorPart#doSaveAs()
	 */
	public void doSaveAs() {
		setInput(getEditor(1).getEditorInput());
	}

	/*
	 * (non-Javadoc) Method declared on IEditorPart
	 */
	public void gotoMarker(IMarker marker) {
		setActivePage(0);
		IDE.gotoMarker(getEditor(0), marker);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.forms.editor.FormEditor#init(org.eclipse.ui.IEditorSite, org.eclipse.ui.IEditorInput)
	 */
	public void init(IEditorSite site, IEditorInput editorInput)
			throws PartInitException {
		if (!(editorInput instanceof IFileEditorInput)) {
			throw new PartInitException(
					"Invalid Input: Must be IFileEditorInput");
		}
		project = JavaCore.create(((IFileEditorInput) editorInput).getFile()
				.getProject());

		IFile metadataFile = getMetadataFile();
		if (metadataFile != null) {
			try {
				ProjectMetadata prMetadata = getProjectMetadata();
				
				frag_root = prMetadata.getPackageFragmentRoot(project);
				if (frag_root != null) {

					frag = prMetadata.getMainPackageFragment(project);
					if (frag.exists()) {
						projectFile = prMetadata.getProjectFile(project);
						resourcesFile = prMetadata.getResourcesFile(project);
					} else {
						//log.warn("package does not exists");
					}
				} else {
					//log.warn("package root does not exists");
				}
			} catch (Exception e) {
				e.printStackTrace();
				throw new PartInitException(metadataFile.getFullPath().toFile()
						.getAbsolutePath()
						+ " cannot be read");
			}
		} else {
			throw new PartInitException("Metadata file not found ");
		}

		super.init(site, editorInput);
	}

	/*
	 * (non-Javadoc) Method declared on IEditorPart.
	 */
	public boolean isSaveAsAllowed() {
		return true;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.forms.editor.FormEditor#pageChange(int)
	 */
	protected void pageChange(int newPageIndex) {
		if (getActiveEditor() != null) {
			if (getActiveEditor().isDirty()) {
				if (MessageDialog
						.openQuestion(this.getSite().getShell(), "Save page",
								"Changes could be lost. Do you want to save changes before changing the page?")) {
					getActiveEditor().doSave(null);
				}
			}
			IEditorPart ed = getEditor(newPageIndex);
			try {

				if (ed.equals(metadataEditor)) {
					getMetadataFile().refreshLocal(Resource.DEPTH_INFINITE, null);
					metadataEditor.updatePartControl(new FileEditorInput(
							getMetadataFile()));
				} else if (ed.equals(projectEditor)) {
					projectFile.refreshLocal(Resource.DEPTH_INFINITE, null);
					projectEditor.updatePartControl(new FileEditorInput(
							projectFile));
				} else if (ed.equals(resourcesEditor)) {
					resourcesFile.refreshLocal(Resource.DEPTH_INFINITE, null);
					resourcesEditor.updatePartControl(new FileEditorInput(
							resourcesFile));
				} else if (ed.equals(formPage)) {
					formPage.initData();
				} else if (ed.equals(deploymentPage)) {
					deploymentPage.initData();
				}

			} catch (CoreException e) {
				ErrorDialog.openError(getSite().getShell(),
						"Error updating active page", e.getMessage(), 
						new Status(IStatus.ERROR, Activator.PLUGIN_ID, e.getMessage(),e));
				e.printStackTrace();
			}
		}
		super.pageChange(newPageIndex);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.resources.IResourceChangeListener#resourceChanged(org.eclipse.core.resources.IResourceChangeEvent)
	 */
	public void resourceChanged(IResourceChangeEvent event) {
		//log.debug("Element changed " + event.getDelta().getResource()+" from project " + event.getDelta().getResource().getProject());
		if (!deploymentPage.isBlocking()){
		Display.getDefault().asyncExec(new Runnable() {
			@Override
			public void run() {
				try{
					getMetadataFile().refreshLocal(Resource.DEPTH_INFINITE, null);
					metadataEditor.updatePartControl(new FileEditorInput(
							getMetadataFile()));
					projectFile.refreshLocal(Resource.DEPTH_INFINITE, null);
					projectEditor.updatePartControl(new FileEditorInput(
							projectFile));
					resourcesFile.refreshLocal(Resource.DEPTH_INFINITE, null);
					resourcesEditor.updatePartControl(new FileEditorInput(
							resourcesFile));
					log.debug("Element changed refreshing data");
					formPage.refreshData();
					deploymentPage.refreshData();
				}catch (CoreException e) {
					ErrorDialog.openError(getSite().getShell(),
							"Error updating active page", e.getMessage(), e.getStatus());
					e.printStackTrace();
				}
			}
		});
		}
	}

	/**
	 * Get the main service package
	 * @return
	 */
	public IPackageFragment getMainPackage() {
		return frag;
	}

	/**
	 * Get the main service package root 
	 * @return
	 */
	public IPackageFragmentRoot getMainFragmentRoot() {
		return frag_root;
	}

	/**
	 * Get the service implementation project
	 * @return
	 */
	public IJavaProject getProject() {
		return project;
	}

	/**
	 * Get the project metadata manager
	 * @return Project Metadata manager object
	 * @throws SAXException
	 * @throws IOException
	 * @throws ParserConfigurationException
	 */
	public ProjectMetadata getProjectMetadata() throws SAXException, 
	IOException, ParserConfigurationException{
		return new ProjectMetadata(new File(
				getMetadataFile().getRawLocation().toOSString()));
	}
	
	/**
	 * Get the project metadata file
	 * @return Project metadata file
	 */
	public IFile getMetadataFile() {
		return project.getProject().getFolder(ProjectMetadata.METADATA_FOLDER)
				.getFile(ProjectMetadata.METADATA_FILENAME);
		//return metadataFile;
	}

	/**
	 * Get the runtime's project configuration file
	 * @return Runtime's project configuration file
	 */
	public IFile getProjectFile() {
		return projectFile;
	}

	/**
	 * Get the runtime's service resources file
	 * @return Runtime's resources file
	 */
	public IFile getResourcesFile() {
		return resourcesFile;
	}
}
