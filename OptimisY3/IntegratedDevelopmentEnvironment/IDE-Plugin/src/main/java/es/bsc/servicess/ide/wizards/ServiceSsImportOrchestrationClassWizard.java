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

import org.eclipse.core.internal.resources.Resource;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExecutableExtension;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.jdt.core.Flags;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.compiler.ast.Wildcard;
import org.eclipse.jdt.internal.core.JavaModelStatus;
import org.eclipse.jdt.internal.corext.codemanipulation.StubUtility;
import org.eclipse.jdt.internal.ui.wizards.NewElementWizard;
import org.eclipse.jface.wizard.Wizard;

import es.bsc.servicess.ide.ProjectMetadata;
import es.bsc.servicess.ide.TitlesAndConstants;

public class ServiceSsImportOrchestrationClassWizard extends NewElementWizard implements
	IExecutableExtension, IServiceSsOrchestrationClassWizard {


	private ServiceSsImportOrchestrationClassPage fPage;
	private boolean doOpen;
	private ICompilationUnit interfaceCU;
	private IJavaProject project;
	
	public ServiceSsImportOrchestrationClassWizard(IJavaProject project) {
		this(true, project);
	}
	
	/**
	 * Wizard constructor
	 */
	public ServiceSsImportOrchestrationClassWizard(boolean doOpen, IJavaProject project) {
		setWindowTitle(TitlesAndConstants.getImportOrchestrationWizardTitle());
		this.doOpen = doOpen;
		this.project = project;
	}
	
	/**Generate an interface definition
	 * @param interfaceName Name of the interface
	 * @param lineDelimiter Format for line delimiters
	 * @return Content of the class definition
	 */
	private String constructInterfaceStub(String interfaceName,
			String lineDelimiter) {
		StringBuffer buf = new StringBuffer("public interface "); //$NON-NLS-1$
		buf.append(interfaceName);
		buf.append("{");
		buf.append(lineDelimiter);
		buf.append("}");
		return buf.toString();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jdt.internal.ui.wizards.NewElementWizard#performFinish()
	 */
	@Override
	public boolean performFinish() {
		warnAboutTypeCommentDeprecation();
		boolean res = super.performFinish();
		if (doOpen) {
			if (res) {
				IResource resource = interfaceCU.getResource();
				if (resource != null) {
					selectAndReveal(resource);
					openResource((IFile) resource);
				}
			}
		}
		return res;

	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.wizard.Wizard#performCancel()
	 */
	public boolean performCancel() {
		fPage.removeDependencies();
		fPage.removeFolders();
		return super.performCancel();
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jdt.internal.ui.wizards.NewElementWizard#getCreatedElement()
	 */
	@Override
	public IJavaElement getCreatedElement() {
		return interfaceCU;
	}
	
	@Override
	public void setInitializationData(IConfigurationElement arg0, String arg1,
			Object arg2) throws CoreException {
		// TODO Auto-generated method stub
		
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.wizard.Wizard#addPages()
	 */
	@Override
	public void addPages() {
		fPage = new ServiceSsImportOrchestrationClassPage(project);
		addPage(fPage);
	}
	/* (non-Javadoc)
	 * @see org.eclipse.jdt.internal.ui.wizards.NewElementWizard#finishPage(org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	protected void finishPage(IProgressMonitor arg0)
			throws InterruptedException, CoreException {
		
		String lineDelimiter = StubUtility.getLineDelimiterUsed(project);
		
		
		
		IFile file = project.getProject()
				.getFolder(ProjectMetadata.METADATA_FOLDER)
				.getFile(ProjectMetadata.METADATA_FILENAME);
		try {
			ProjectMetadata pr_meta = new ProjectMetadata(file.getRawLocation().toFile());
			IPackageFragmentRoot root = pr_meta.getPackageFragmentRoot(project);
			if (root== null || !root.exists())
				throw (new Exception("Main package fragment root not found"));
			IPackageFragment frag = root.getPackageFragment(fPage.getTypePackageName());
			if (frag==null|| !frag.exists())
				root.createPackageFragment(fPage.getTypePackageName(), true, arg0);
			String className = fPage.getType().getElementName().replaceAll(" ", "") + "Itf";
			interfaceCU = frag.getCompilationUnit(className+".java");
			if (interfaceCU== null || !interfaceCU.exists()){
				interfaceCU = frag.createCompilationUnit(className+".java", "", false,
						new SubProgressMonitor(arg0, 2));
				interfaceCU.createPackageDeclaration(frag.getElementName(), arg0);
				String typeStub = constructInterfaceStub(className, lineDelimiter);
				interfaceCU.createType(typeStub, null, false, arg0);
			}
			pr_meta.addExternalOrchestrationClass(fPage.getTypeName(), fPage.getExternalLocation(), fPage.getIntraPackageLocation());
			pr_meta.toFile(file.getRawLocation().toFile());
			project.getProject().refreshLocal(Resource.DEPTH_INFINITE, arg0);
		} catch (Exception e) {
			throw new JavaModelException(e, JavaModelStatus.ERROR);
		}

	}

	@Override
	public String getFullyQualifiedDomainName() {
		return fPage.getTypeName();
	}
	
	
}
