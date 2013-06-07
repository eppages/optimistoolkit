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
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.internal.ui.wizards.NewElementWizard;
import es.bsc.servicess.ide.model.OrchestrationClass;
import es.bsc.servicess.ide.model.ServiceElement;

@SuppressWarnings("restriction")
public class ServiceSsImportCoreElementWizard extends NewElementWizard {

	private ServiceSsCoreElementWizardPage lastPage;
	private ServiceSsCoreElementSecondPage secondPage;
	private IMethod method;
	private OrchestrationClass orchestrationClass;
	
	public ServiceSsImportCoreElementWizard() {
		super();
		setWindowTitle("New Core Element ");
	}

	public void addPages() {
		super.addPages();
		if (secondPage == null) {
			secondPage = new ServiceSsCoreElementSecondPage();
			secondPage.init(getSelection());
			secondPage.setType(ServiceSsCoreElementSecondPage.METHOD_EXISTS);
			secondPage.setOrchestrationClass(orchestrationClass);
		}
		if (lastPage == null) {
			lastPage = new ServiceSsCoreElementWizardPage();
			((ServiceSsCoreElementWizardPage) lastPage).init(getSelection());
			lastPage.setType(ServiceSsCoreElementSecondPage.METHOD_EXISTS);
		}
		addPage(secondPage);
		addPage(lastPage);
	}

	@Override
	protected void finishPage(IProgressMonitor monitor)
			throws InterruptedException, CoreException {
		int type = lastPage.getType();
		IType t = lastPage.getTypeClass();
		ServiceElement e = lastPage.getElement();
		method = e.generateElementMethodInType(t, true, monitor);
		secondPage.performFinish(monitor);
		lastPage.performFinish(monitor);
		
		/*if (type == ServiceSsCoreElementSecondPage.METHOD_NEW) {
			IType newClass = lastPage.createDeclaringClassType(arg0);
			e.generateSimpleMethodInType(newClass, false, arg0);
		} else if (type == ServiceSsCoreElementSecondPage.SERVICE_WSDL) {
			secondPage.generateServiceCode(arg0);
			try {
				lastPage.writeServiceLocations();
			} catch (Exception ex) {
				// MessageDialog.openError(getShell(),"Error updating active page",ex.getMessage());
				ex.printStackTrace();
			}

		} else if (type == ServiceSsCoreElementSecondPage.METHOD_EXISTS) {
			secondPage.addElementToDependency();
		}
		 */
	}

	@Override
	public IJavaElement getCreatedElement() {
		return method;
	}

	@Override
	public boolean performCancel() {
		secondPage.performCancel();
		lastPage.performCancel();
		return super.performCancel();
	}

	public void setExternalClass(OrchestrationClass orchestrationClass) {
		this.orchestrationClass = orchestrationClass;
		if (secondPage != null){
			secondPage.setOrchestrationClass(orchestrationClass);
		}
		
	}

}
