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

import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;

/**
 * Abstract class which perform the service deployment. It has to be extended for the different deployment infrastructures
 * @author Jorge Ejarque (Barcelona Supercomputing Center)
 *
 */
public abstract class Deployer {
	protected Composite composite;
	protected IWorkbenchWindow window;
	protected ServiceFormEditor editor;
	protected BuildingDeploymentFormPage page;

	/**
	 * Constructor
	 * @param editor Parent's service editor
	 * @param window Parent's workbench window
	 * @param page Parent's editor page
	 */
	public Deployer(ServiceFormEditor editor, IWorkbenchWindow window,
			BuildingDeploymentFormPage page) {
		this.editor = editor;
		this.window = window;
		this.page = page;
	}

	
	/**
	 * Create the widgets for the deployment option
	 * @param toolkit Parent's toolkit
	 * @param options Parents option composite
	 * @param old_options Old deployment option 
	 * @return New deployment option composite
	 */
	public abstract Composite createComposite(FormToolkit toolkit,
			Composite options, Composite old_options);
	
	/**
	 * Method to initialize the deployment option widgets. It is executed when the deployment option is loaded
	 */
	public abstract void initiate();
	
	/**
	 * Method to deploy a service. This will be run when the deploy button of the deployment page
	 */
	public abstract void deploy();

	/**
	 * Method to be executed when the composite 
	 */
	public abstract void diposeComposite();

	/**
	 * Get the java project to be deployed
	 * @return
	 */
	public IJavaProject getProject() {
		return editor.getProject();
	}
	
	/**
	 * Get the Service editor reference
	 * @return Service editor reference
	 */
	public ServiceFormEditor getEditor() {
		return editor;
	}

	/**
	 * Get the parent's shell 
	 * @return Parent's shell reference
	 */
	public Shell getShell() {
		return window.getShell();
	}

	/**
	 * Get the building & deployment form page
	 * @return Building and deployment page
	 */
	public BuildingDeploymentFormPage getPage() {
		return page;
	}

	/**
	 * Get the Workbench window reference
	 * @return Workbech window
	 */
	public IWorkbenchPage getWorkbenchPage() {
		return window.getActivePage();
	}
}