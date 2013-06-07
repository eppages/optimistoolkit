/*
 *  Copyright 2011-2013 Barcelona Supercomputing Center (www.bsc.es)
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

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

import es.bsc.servicess.ide.model.Parameter;
import es.bsc.servicess.ide.model.ServiceElement;

public abstract class SpecificComposite {
	protected ServiceSsCoreElementWizardPage thirdPage;
	protected Shell shell;
	
	public SpecificComposite(ServiceSsCoreElementWizardPage page, Shell shell){
		this.thirdPage= page;
		this.shell=shell;
	}

	public abstract Composite createComposite(Composite ceBar);
	
	public abstract boolean isCompositeCompleated();

	public abstract ServiceElement generateElement(String name, int modifier,
			String returnType);

	public abstract void addListeners();

	public abstract void printElement(ServiceElement element);

	public abstract String getParameterDirection(Parameter p);

	public abstract void performFinish(IProgressMonitor monitor) throws JavaModelException;
	
	public abstract void performCancel();

	public abstract void addSpecificParams(ServiceElement el);


}
