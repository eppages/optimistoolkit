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

import java.io.ByteArrayInputStream;
import java.io.File;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExecutableExtension;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.jdt.core.Flags;
import org.eclipse.jdt.core.IBuffer;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.core.JavaModelStatus;
import org.eclipse.jdt.internal.corext.codemanipulation.StubUtility;
import org.eclipse.jdt.internal.ui.wizards.NewElementWizard;

import es.bsc.servicess.ide.ProjectMetadata;
import es.bsc.servicess.ide.TitlesAndConstants;

/**Wizard for creating an new Service Class.
 * @author Jorge Ejarque (Barcelona Supercomputing Center)
 *
 */
@SuppressWarnings("restriction")
public class ServiceSsNewServiceClassWizard extends NewElementWizard implements
		IExecutableExtension, IServiceSsOrchestrationClassWizard {

	private ServiceSsNewServiceClassPage fPage;
	private ICompilationUnit cu_class;
	private boolean doOpen;

	/**
	 * Wizard constructor
	 */
	public ServiceSsNewServiceClassWizard() {
		this(true);
		this.doOpen = true;
	}
	/**
	 * Wizard constructor
	 */
	public ServiceSsNewServiceClassWizard(boolean doOpen) {
		super();

		setWindowTitle(TitlesAndConstants.getNewClassWizardTitle());
		this.doOpen = doOpen;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.wizard.Wizard#addPages()
	 */
	public void addPages() {
		super.addPages();
		if (fPage == null) {
			fPage = new ServiceSsNewServiceClassPage();
			fPage.init(getSelection());
		}
		addPage(fPage);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jdt.internal.ui.wizards.NewElementWizard#finishPage(org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	protected void finishPage(IProgressMonitor arg0)
			throws InterruptedException, CoreException {
		IPackageFragment frag = fPage.getPackageFragment();
		String lineDelimiter = StubUtility.getLineDelimiterUsed(frag
				.getJavaProject());
		cu_class = frag.createCompilationUnit(fPage.getTypeName() + ".java",
				"", false, new SubProgressMonitor(arg0, 2));
		cu_class.createPackageDeclaration(frag.getElementName(), arg0);
		cu_class.createImport("javax.jws.WebService", null, Flags.AccDefault,
				arg0);
		String classStub = constructClassStub(fPage.getTypeName(),
				lineDelimiter);

		IType type = cu_class.createType(classStub, null, false, arg0);
		ICompilationUnit cu = frag.createCompilationUnit(fPage.getTypeName()
				.replaceAll(" ", "") + "Itf.java", "", false,
				new SubProgressMonitor(arg0, 2));
		cu.createPackageDeclaration(frag.getElementName(), arg0);
		String typeStub = constructInterfaceStub(fPage.getTypeName()
				.replaceAll(" ", "") + "Itf", lineDelimiter);
		cu.createType(typeStub, null, false, arg0);
		IFile file = fPage.getJavaProject().getProject()
				.getFolder(ProjectMetadata.METADATA_FOLDER)
				.getFile(ProjectMetadata.METADATA_FILENAME);
		try {
			ProjectMetadata pr_meta = new ProjectMetadata(file.getRawLocation()
					.toFile());
			pr_meta.addOrchestrationClass(frag.getElementName() + "."
					+ fPage.getTypeName(), fPage.getClassType());
			pr_meta.toFile(file.getRawLocation().toFile());
		} catch (Exception e) {
			throw new JavaModelException(e, JavaModelStatus.ERROR);
		}

	}

	/** Generate the content of a new the service class
	 * @param pack Java Package
	 * @param typeStub Content stub with the class definition
	 * @param lineDelimiter Format for line delimiters
	 * @return String with the content
	 */
	private String constructCUContent(IPackageFragment pack, String typeStub,
			String lineDelimiter) {
		StringBuffer buf = new StringBuffer();
		if (!pack.isDefaultPackage()) {
			buf.append("package ").append(pack.getElementName()).append(';'); //$NON-NLS-1$
		}
		buf.append(lineDelimiter).append(lineDelimiter);
		buf.append(typeStub);
		return buf.toString();

	}

	/**Generate the class definition
	 * @param className Name of the class 
	 * @param lineDelimiter Format for line delimiters
	 * @return Content of the class definition
	 */
	private String constructClassStub(String className, String lineDelimiter) {
		StringBuffer buf = new StringBuffer("@WebService\npublic class "); //$NON-NLS-1$
		buf.append(className);
		buf.append("{");
		buf.append(lineDelimiter);
		buf.append("}");
		return buf.toString();
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
				IResource resource = cu_class.getResource();
				if (resource != null) {
					selectAndReveal(resource);
					openResource((IFile) resource);
				}
			}
		}
		return res;

	}

	/* (non-Javadoc)
	 * @see org.eclipse.jdt.internal.ui.wizards.NewElementWizard#getCreatedElement()
	 */
	@Override
	public IJavaElement getCreatedElement() {
		return cu_class.getType(fPage.getTypeName());
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.IExecutableExtension#setInitializationData(org.eclipse.core.runtime.IConfigurationElement, java.lang.String, java.lang.Object)
	 */
	@Override
	public void setInitializationData(IConfigurationElement arg0, String arg1,
			Object arg2) throws CoreException {
		

	}
	@Override
	public String getFullyQualifiedDomainName() {
		return cu_class.getType(fPage.getTypeName()).getFullyQualifiedName();
		
	}

}
