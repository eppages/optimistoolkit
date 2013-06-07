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

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.swt.widgets.Group;

public class ServiceSsSelectServiceFromWSDLPage extends
		ServiceSsCommonWizardPage {

	private IWizardPage op_a_page;

	public ServiceSsSelectServiceFromWSDLPage() {
		super("newServiceCE", "Create New Service Core Element",
				"Creates a new service core element from the service wsdl");

	}

	@Override
	protected void createExtraControls(Group group) {
		// TODO Auto-generated method stub

	}

	@Override
	public void addExtraListeners() {
		// TODO Auto-generated method stub

	}

	public void init(IStructuredSelection selection, IWizardPage op_a_page) {
		super.init(selection);
		this.op_a_page = op_a_page;
		ServiceSsNewCoreElementWizard w = (ServiceSsNewCoreElementWizard) getWizard();
		w.addPage(this.op_a_page);
		setPageComplete(true);

	}

}
