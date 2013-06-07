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

package es.bsc.servicess.ide.wizards.coretypes;

import java.io.File;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.Signature;
import org.eclipse.jdt.internal.core.JavaModelStatus;
import org.eclipse.jdt.internal.corext.codemanipulation.StubUtility;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import es.bsc.servicess.ide.ProjectMetadata;
import es.bsc.servicess.ide.model.MethodCoreElement;
import es.bsc.servicess.ide.model.ServiceCoreElement;
import es.bsc.servicess.ide.model.ServiceElement;
import es.bsc.servicess.ide.wizards.ServiceSsCoreElementSecondPage;
import es.bsc.servicess.ide.wizards.ServiceSsCoreElementWizardPage;
import es.bsc.servicess.ide.wizards.TypeSpecificTreatment;

public class NewMethodSpecificTreatment extends TypeSpecificTreatment {

	private Text declaringClass;
	private Text methodNameText;
	//private Button classServiceButton;

	public NewMethodSpecificTreatment(
			ServiceSsCoreElementSecondPage secondPage, Shell shell) {
		super(secondPage,shell);
	}

	@Override
	public void updateControlsListeners() {
		declaringClass.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent arg0) {
				updateInfo();
			}
		});
		methodNameText.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent arg0) {
				updateInfo();
			}
		});
		
	}

	@Override
	public Composite updateSecondPageGroupControls(Group group, Composite cp) {
		//Class Name
		if (cp != null)
			cp.dispose();
		Composite comp = new Composite(group, SWT.NONE);
		GridData rd = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		rd.grabExcessHorizontalSpace = true;
		comp.setLayoutData(rd);
		comp.setLayout(new GridLayout(2, false));
		Label serviceClassLabel = new Label(comp, SWT.NONE);
		serviceClassLabel.setText("Declaring Class");
		Composite dc = new Composite(comp, SWT.NONE);
		rd = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		rd.grabExcessHorizontalSpace = true;
		rd.horizontalIndent = 0;
		rd.horizontalSpan = 0;
		dc.setLayoutData(rd);
		GridLayout oeReturnLayout = new GridLayout();
		oeReturnLayout.numColumns = 2;
		oeReturnLayout.marginLeft = 0;
		oeReturnLayout.marginRight = 0;
		dc.setLayout(oeReturnLayout);
		declaringClass = new Text(dc, SWT.SINGLE | SWT.BORDER);
		rd = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		rd.grabExcessHorizontalSpace = true;
		declaringClass.setLayoutData(rd);
//		classServiceButton = new Button(dc, SWT.NONE);
//		classServiceButton.setText("Select...");
		
		Label methodLabel = new Label(comp, SWT.NONE);
		methodLabel.setText("Method");
		Composite method = new Composite(comp, SWT.NONE);
		rd = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		rd.grabExcessHorizontalSpace = true;
		rd.horizontalIndent = 0;
		rd.horizontalSpan = 0;
		method.setLayoutData(rd);
		oeReturnLayout = new GridLayout();
		oeReturnLayout.numColumns = 1;
		oeReturnLayout.marginLeft = 0;
		oeReturnLayout.marginRight = 0;
		method.setLayout(oeReturnLayout);
		methodNameText = new Text(method, SWT.SINGLE | SWT.BORDER);
		rd = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		rd.grabExcessHorizontalSpace = true;
		methodNameText.setLayoutData(rd);	
		
		return comp;
	}

	@Override
	public ServiceElement generateCoreElement() throws JavaModelException {
		throw new JavaModelException(new Exception("Can not generate an element in this step"), IStatus.ERROR);
	}

	@Override
	public boolean isPageComplete() {
		if (methodNameText.getText().trim().length() > 0
				&& declaringClass.getText().trim().length() > 0) {
			return true;
		}else
			return false;
	}
	
	protected void updateInfo() {
		if (isPageComplete()) {
			System.out.println("Updating Element info");
			IWizardPage p = secondPage.getNextPage();
			((SpecificMethodComposite)((ServiceSsCoreElementWizardPage) p).getSpecificComposite()).updateCoreElementInfo(
			declaringClass.getText().trim(), methodNameText.getText().trim());
				secondPage.getCEStatus().setOK();
				secondPage.doStatusUpdate();

		} else {
			secondPage.getCEStatus()
					.setError("There are missing parameters to complete the Element information");
			secondPage.doStatusUpdate();
		}

	}

	@Override
	public void performCancel() {
		
	}

	@Override
	public void performFinish(IProgressMonitor monitor) {
		
	}

}
