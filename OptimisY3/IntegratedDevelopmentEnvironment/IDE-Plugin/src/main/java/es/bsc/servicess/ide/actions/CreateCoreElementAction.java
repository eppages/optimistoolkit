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

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

import es.bsc.servicess.ide.Logger;
import es.bsc.servicess.ide.ResourcesFile;
import es.bsc.servicess.ide.wizards.ServiceSsNewCoreElementWizard;

/**Action for creating a Core Element in a service
 * @author jorgee
 *
 */
public class CreateCoreElementAction implements IWorkbenchWindowActionDelegate {
	private IStructuredSelection selection;
	private IWorkbenchWindow window;
	
	private static Logger log = Logger.getLogger(CreateCoreElementAction.class);

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IActionDelegate#run(org.eclipse.jface.action.IAction)
	 */
	@SuppressWarnings("restriction")
	@Override
	public void run(IAction arg0) {
		ServiceSsNewCoreElementWizard wizard = new ServiceSsNewCoreElementWizard();

		// ISelection selection =this.window.getActivePage().getSelection();
		wizard.init(window.getWorkbench(), selection);
		WizardDialog dialog = new WizardDialog(window.getShell(), wizard);
		dialog.open();
		/*
		 * }else MessageDialog.openError( window.getShell(), "Error",
		 * "Error window is null");
		 */
	}

	@Override
	public void selectionChanged(IAction arg0, ISelection arg1) {
		if (arg1 instanceof IStructuredSelection) {
			selection = (IStructuredSelection) arg1;
			// System.out.println("Selection Changed: Setting Selection");
		} else
			log.warn("Selection not valid");

	}

	@Override
	public void dispose() {

	}

	@Override
	public void init(IWorkbenchWindow window) {
		this.window = window;
		/*
		 * ISelection arg1 =this.window.getActivePage().getSelection(); if (arg1
		 * instanceof IStructuredSelection ){ selection =
		 * (IStructuredSelection)arg1;
		 * System.out.println("Init Setting Selection"); }else
		 * System.out.println("Selection not valid"); }
		 */
	}

}
