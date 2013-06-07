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
import org.eclipse.jdt.core.Flags;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.Signature;
import org.eclipse.jdt.internal.core.JavaModelStatus;
import org.eclipse.jdt.internal.corext.codemanipulation.StubUtility;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import es.bsc.servicess.ide.ProjectMetadata;
import es.bsc.servicess.ide.model.CoreElementParameter;
import es.bsc.servicess.ide.model.MethodCoreElement;
import es.bsc.servicess.ide.model.Parameter;
import es.bsc.servicess.ide.model.ServiceElement;
import es.bsc.servicess.ide.wizards.ServiceSsCoreElementWizardPage;
import es.bsc.servicess.ide.wizards.SpecificComposite;

public class SpecificMethodComposite extends SpecificComposite{
		protected Composite meDesc;
		protected Text ceDCText;
		protected Button ceIsInit;
		protected Button ceIsModifier;
		protected String declaringClass;
		protected int modifier = Flags.AccPublic;
		protected Button ceStatic;
		protected Button ceFinal;
		
		public SpecificMethodComposite(ServiceSsCoreElementWizardPage page,
				Shell shell) {
			super(page, shell);
		}
		public Composite createComposite(Composite parent){
			meDesc = new Composite(parent, SWT.NONE);
			meDesc.setLayout(new GridLayout(2, false));
			Label ceDeclaringClassLabel = new Label(meDesc, SWT.NONE);
			ceDeclaringClassLabel.setText("Declaring Class");
			ceDCText = new Text(meDesc, SWT.SINGLE | SWT.BORDER);
			GridData rd = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
			rd.grabExcessHorizontalSpace = true;
			ceDCText.setLayoutData(rd);
			Label ceOptions = new Label(meDesc, SWT.NONE);
			ceOptions.setText("Options");
			Composite compOptions = new Composite(meDesc, SWT.NONE);
			compOptions.setLayout(new GridLayout(2, false));
			ceIsInit = new Button(compOptions, SWT.CHECK);
			ceIsInit.setText("isInit");
			rd = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
			rd.grabExcessHorizontalSpace = true;
			ceIsInit.setLayoutData(rd);
			ceIsModifier = new Button(compOptions, SWT.CHECK);
			ceIsModifier.setText("isModifier");
			rd = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
			rd.grabExcessHorizontalSpace = true;
			ceIsModifier.setLayoutData(rd);
			ceIsModifier.setSelection(true);
			createModifiers();
			return meDesc;
		}
		
		@Override
		public boolean isCompositeCompleated() {
			if (ceDCText.getText().trim().length() > 0)
				return true;
			else
				return false;
		}

		@Override
		public ServiceElement generateElement(String name, int modifier,
				String returnType) {
			return new MethodCoreElement(name, modifier,
					returnType.trim(), null,ceDCText.getText().trim(), 
					ceIsModifier.getSelection(), ceIsInit.getSelection());
		}

		@Override
		public void addListeners() {
			
			ceDCText.addModifyListener(new ModifyListener() {

				@Override
				public void modifyText(ModifyEvent arg0) {
					updateDC();
				}
			});

			ceIsInit.addSelectionListener(new SelectionListener() {
				@Override
				public void widgetDefaultSelected(SelectionEvent arg0) {
					updateIsInit();
				}

				@Override
				public void widgetSelected(SelectionEvent arg0) {
					updateIsInit();

				}

			});
			ceIsModifier.addSelectionListener(new SelectionListener() {
				@Override
				public void widgetDefaultSelected(SelectionEvent arg0) {
					updateIsModifier();
				}

				@Override
				public void widgetSelected(SelectionEvent arg0) {
					updateIsModifier();

				}

			});
			
			addModifiersListemers();
		}
		
		private void updateDC() {
			if (thirdPage.getElement() != null) {
					((MethodCoreElement) thirdPage.getElement()).setDeclaringClass(ceDCText
							.getText().trim());

			} else if (thirdPage.isElementCompleted()) {
				thirdPage.updateElement(thirdPage.generateElement());
				thirdPage.getCEStatus().setOK();
				thirdPage.doStatusUpdate();
			}
			declaringClass = ceDCText.getText().trim();

		}
		
		protected void updateIsModifier() {
			if (thirdPage.getElement() != null) {
				((MethodCoreElement) thirdPage.getElement()).setModifier(ceIsModifier
						.getSelection());
			}
			

		}

		protected void updateIsInit() {
			if (thirdPage.getElement() != null) {
				((MethodCoreElement) thirdPage.getElement()).setModifier(ceIsInit.getSelection());
			}

		}

		public void updateCoreElementInfo(String declaringClass, String methodName) {
			System.out.println("Updating declaring Class: " + declaringClass
					+ " methodName: " + methodName);
			thirdPage.setInitialMethodName(methodName);
			if (thirdPage.isPageCreated()) {
				ceDCText.setText(declaringClass);
			}
			this.declaringClass = declaringClass;
		}
		
		@Override
		public void printElement(ServiceElement element) {
			if (element!=null) {
				ceDCText.setText(((MethodCoreElement) element).getDeclaringClass());
				ceIsInit.setSelection(((MethodCoreElement) element).isInit());
				ceIsModifier.setSelection(((MethodCoreElement) element)
						.isModifier());
				ceDCText.setEnabled(false);
				thirdPage.getNameText().setEnabled(false);
				thirdPage.getReturnTypeText().setEnabled(false);
				thirdPage.getCEStatus().setOK();
				thirdPage.doStatusUpdate();
			} else {
				if (declaringClass != null) {
					ceDCText.setText(declaringClass);
					thirdPage.getCEStatus().setOK();
					thirdPage.doStatusUpdate();
				}
				ceDCText.setEnabled(true);
				thirdPage.getNameText().setEnabled(true);
				thirdPage.getReturnTypeText().setEnabled(true);
				printModifiers((MethodCoreElement)element);
			}
		}
		
		@Override
		public String getParameterDirection(Parameter p) {
			return ((CoreElementParameter) p).getDirection();
		}
		@Override
		public void performFinish(IProgressMonitor monitor) throws JavaModelException {
			//Nothing to do
		}
		
		@Override
		public void performCancel() {
			// Nothing to do	
		}
		@Override
		public void addSpecificParams(ServiceElement el) {
			((MethodCoreElement) el).setModifier(ceIsModifier
					.getSelection());
			((MethodCoreElement) el).setInit(ceIsInit
					.getSelection());
			addModifiers((MethodCoreElement) el);
			
		}
		protected void createModifiers() {
			Label ceOptions = new Label(meDesc, SWT.NONE);
			ceOptions.setText("Method Modifiers");
			Composite compOptions = new Composite(meDesc, SWT.NONE);
			compOptions.setLayout(new GridLayout(2, false));
			ceStatic = new Button(compOptions, SWT.CHECK);
			ceStatic.setText("static");
			
			GridData rd = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
			rd.grabExcessHorizontalSpace = true;
			ceStatic.setLayoutData(rd);
			ceFinal = new Button(compOptions, SWT.CHECK);
			ceFinal.setText("final");
			rd = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
			rd.grabExcessHorizontalSpace = true;
			ceFinal.setLayoutData(rd);
			ceStatic.setSelection(true);
			updateModifiers();
			
		}
		
		private void addModifiers(MethodCoreElement el) {
			el.setMethodModifier(modifier);
			
		}
		
		private void printModifiers(MethodCoreElement element) {
			ceStatic.setSelection(Flags.isStatic(element.getMethodModifier()));
			ceFinal.setSelection(Flags.isFinal(element.getMethodModifier()));
			
		}
		
		private void addModifiersListemers() {
			ceStatic.addSelectionListener(new SelectionListener() {
				@Override
				public void widgetDefaultSelected(SelectionEvent arg0) {
					updateModifiers();
				}

				@Override
				public void widgetSelected(SelectionEvent arg0) {
					updateModifiers();

				}

			});
			ceFinal.addSelectionListener(new SelectionListener() {
				@Override
				public void widgetDefaultSelected(SelectionEvent arg0) {
					updateModifiers();
				}

				@Override
				public void widgetSelected(SelectionEvent arg0) {
					updateModifiers();

				}

			});
		}
		
		public void updateModifiers(){ 
			modifier = Flags.AccPublic;
			if (ceStatic.getSelection()) 
				 modifier = modifier | Flags.AccStatic; 
			 if (ceFinal.getSelection()) 
				 modifier = modifier | Flags.AccFinal; 
		}
}
