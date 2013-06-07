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
import org.eclipse.jdt.core.IJavaProject;
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

public class SpecificNewMethodComposite extends SpecificMethodComposite{
	
		public SpecificNewMethodComposite(ServiceSsCoreElementWizardPage page,
			Shell shell) {
			super(page, shell);
		}
		
		

		@Override
		public void performFinish(IProgressMonitor monitor) throws JavaModelException {
			IType newClass = createDeclaringClassType(monitor);
			MethodCoreElement ce = (MethodCoreElement)thirdPage.getElement();
			ce.setMethodModifier(ce.getMethodModifier()|modifier);
			ce.generateSimpleMethodInType(newClass, false, ce.generateReturnCode(), monitor);
			
		}
		
		
		private IType createDeclaringClassType(IProgressMonitor m)
				throws JavaModelException {
			IPackageFragmentRoot pfr = thirdPage.getPackageFragmentRoot();
			String lineDelimiter = StubUtility
					.getLineDelimiterUsed(thirdPage.getJavaProject());
			IPackageFragment pf;
			String st = Signature.getQualifier(declaringClass);
			if (st != null && st.trim().length() > 0) {
				pf = pfr.getPackageFragment(st);
				if (pf != null) {
					if (!pf.exists()) {
						pf = pfr.createPackageFragment(st, true, m);
					}
				} else {
					pf = pfr.createPackageFragment(st, true, m);
				}
			} else {
				IFile metadataFile = thirdPage.getJavaProject().getProject()
						.getFolder(ProjectMetadata.METADATA_FOLDER)
						.getFile(ProjectMetadata.METADATA_FILENAME);
				if (metadataFile != null) {
					try {
						ProjectMetadata pr_meta = new ProjectMetadata(new File(
								metadataFile.getRawLocation().toOSString()));
						pfr = thirdPage.getJavaProject().findPackageFragmentRoot(
								thirdPage.getJavaProject().getPath().append(
										pr_meta.getSourceDir()));
						if (pfr != null) {
							pf = pfr.getPackageFragment(pr_meta
									.getMainPackageName() + ".coreelements");
							if (pf.exists()) {
								st = pf.getElementName();
							} else
								throw new JavaModelException(new Exception(
										"Unable to find core elemet package"),
										JavaModelStatus.ERROR);
						} else {
							throw new JavaModelException(new Exception(
									"Unable to find core elemet package root"),
									JavaModelStatus.ERROR);
						}
					} catch (Exception e) {
						throw new JavaModelException(e, JavaModelStatus.ERROR);
					}
				} else
					throw new JavaModelException(new Exception(
							"Unable to find project metadata file"),
							JavaModelStatus.ERROR);
			}
			String name = Signature.getSimpleName(declaringClass);
			System.out.println("Package: " + st + " Class name: " + name);
			ICompilationUnit cu = pf.getCompilationUnit(name + ".java");
			if (cu == null || !cu.exists()){
				cu = pf.createCompilationUnit(name + ".java", "",false, m);
				cu.createPackageDeclaration(st, m);
				cu.createType(constructCEClassStub(name, lineDelimiter), null,false, m);
			}
			return cu.getType(name);
		}
		
		private String constructCEClassStub(String interfaceName,
				String lineDelimiter) {
			StringBuffer buf = new StringBuffer("public class "); //$NON-NLS-1$
			buf.append(interfaceName);
			buf.append("{");
			buf.append(lineDelimiter);
			buf.append("}");
			return buf.toString();
		}
		
		@Override
		public void performCancel() {
			// TODO Auto-generated method stub
			
		}
}
