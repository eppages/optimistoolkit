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

import es.bsc.servicess.ide.model.OrchestrationElement;

@SuppressWarnings("restriction")
public class ServiceSsNewOrchestrationElementWizard extends NewElementWizard {

	private ServiceSsOrchestrationElementWizardPage fPage;

	private IMethod method;

	public ServiceSsNewOrchestrationElementWizard() {
		super();
		setWindowTitle("New Orchestration Element");
	}

	public void addPages() {
		super.addPages();
		if (fPage == null) {
			fPage = new ServiceSsOrchestrationElementWizardPage();
			fPage.init(getSelection());
		}
		addPage(fPage);
	}

	@Override
	protected void finishPage(IProgressMonitor arg0)
			throws InterruptedException, CoreException {
		IType t = fPage.getTypeClass();
		OrchestrationElement e = fPage.getOrchestrationElement();
		method = e.generateElementMethodInType(t, false, arg0);

	}

	@Override
	public IJavaElement getCreatedElement() {
		return method;
	}

}
