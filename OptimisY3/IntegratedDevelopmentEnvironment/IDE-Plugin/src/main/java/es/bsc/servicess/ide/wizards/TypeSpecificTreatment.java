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

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;

import es.bsc.servicess.ide.model.ServiceElement;

/**
 * Abstract class for general treatment of core element creation. 
 * A Core element type specific class must extend this class implementing the abstract methods 
 * @author Jorge Ejarque (Barcelona Supercomputing Center) 
 *
 */
public abstract class TypeSpecificTreatment {

	protected ServiceSsCoreElementSecondPage secondPage;
	protected Shell shell;
	
	/**Constructor
	 * @param secondPage Core element wizard second page
	 * @param shell Parent's shell
	 */
	public TypeSpecificTreatment(
			ServiceSsCoreElementSecondPage secondPage, Shell shell) {
		this.secondPage = secondPage;
		this.shell = shell;
	}
	
	/**
	 * Update listeners of the specific core element widgets
	 */
	public abstract void updateControlsListeners();


	/**
	 * Update the general core element widgets the specific widgets 
	 * @param group
	 * @param composite
	 * @return
	 */
	public abstract Composite updateSecondPageGroupControls(Group group, Composite composite);


	/**
	 * Generate the type specific core element
	 * @return Core element representation
	 * @throws JavaModelException
	 */
	public abstract ServiceElement generateCoreElement() throws JavaModelException;


	/**
	 * Check if the specific core element information is completed
	 * @return true if yes, false if not
	 */
	public abstract boolean isPageComplete();

	/**
	 * Undo the changes performed by the specific core element creation in the second page of the wizard.
	 */
	public abstract void performCancel();

	/**
	 * Perform final changes for specific core element once the wizard has finished
	 * @param monitor
	 * @throws CoreException
	 */
	public abstract void performFinish(IProgressMonitor monitor) throws CoreException;

}
