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

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.ui.dialogs.StatusInfo;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;

import es.bsc.servicess.ide.Logger;
import es.bsc.servicess.ide.ProjectMetadata;
import es.bsc.servicess.ide.model.OrchestrationClass;
import es.bsc.servicess.ide.model.ServiceElement;
import es.bsc.servicess.ide.wizards.coretypes.BinaryMethodSpecificTreatment;
import es.bsc.servicess.ide.wizards.coretypes.ExistingMethodSpecificTreatment;
import es.bsc.servicess.ide.wizards.coretypes.NewMethodSpecificTreatment;
import es.bsc.servicess.ide.wizards.coretypes.ServiceWSDLSpecificTreatment;
import es.bsc.servicess.ide.wizards.coretypes.ServiceWarSpecificTreatment;

/** 
 * Wizard page for selecting the way to create the new core element.
 * @author Jorge Ejarque (Barcelona Supercomputing Center)
 */
@SuppressWarnings("restriction")
public final class ServiceSsCoreElementSecondPage extends
		ServiceSsCommonWizardPage {

	

	private int type = -1;
	private TypeSpecificTreatment specificTreat = null;
	private StatusInfo fCEStatus;
	private boolean pageCreated;
	private Composite composite;
	private OrchestrationClass orchestrationClass = null;
	
	private static Logger log = Logger.getLogger();
	
	public final static int METHOD_NEW = 0;
	public final static int METHOD_EXISTS = 1;
	public final static int SERVICE_WSDL = 2;
	public final static int METHOD_BIN = 3;
	public static final int SERVICE_WAR = 4;

	/**
	 * Constructor.
	 */
	@SuppressWarnings("restriction")
	public ServiceSsCoreElementSecondPage() {
		super("selectCE", "Create Core Element",
				"Create a Core Element with the selected procedure");
		super.setClassLabel("CE Interface", false);
		fCEStatus = new StatusInfo();
		
		pageCreated = false;

		doStatusUpdate();
	}
	
	public OrchestrationClass getOrchestrationClass() {
		return orchestrationClass;
	}

	public void setOrchestrationClass(OrchestrationClass orchClass) {
		this.orchestrationClass = orchClass;
	}
	
	@Override
	protected void createExtraControls(Group group) {
		log.debug("Creating Extra controls");
		//composite = new Composite(group, SWT.NONE);
		if (specificTreat!= null){
			log.debug("Specific treatment exists. Updating Page");
			composite = specificTreat.updateSecondPageGroupControls(group, composite);
			
		}
		pageCreated = true;
		
	}
	
	@Override
	public void addExtraListeners() {
		if (specificTreat!= null){
			specificTreat.updateControlsListeners();
		}
	}
	
	/** Generate the core element according to the selected way
	 * @return Generated Core element
	 * @throws JavaModelException
	 */
	private ServiceElement generateCoreElement() throws JavaModelException {
		ServiceElement el = null;
		el = specificTreat.generateCoreElement();
		return el;
	}	
		
	/* (non-Javadoc)
	 * @see es.bsc.servicess.ide.wizards.ServiceSsCommonWizardPage#init(org.eclipse.jface.viewers.IStructuredSelection)
	 */
	public void init(IStructuredSelection selection) {
		super.init(selection);
	}

	/**
	 * Set the type of core element generation
	 * @param type
	 */
	public void setType(int type) {
		if (this.type != type){
			this.type = type;
			switch (this.type){
				case(METHOD_NEW):	
					specificTreat = new NewMethodSpecificTreatment(this, getShell());
					break;
				case(METHOD_EXISTS):
					specificTreat = new ExistingMethodSpecificTreatment(this, getShell());
					break;
				case(METHOD_BIN):
					specificTreat = new BinaryMethodSpecificTreatment(this, getShell());
					break;
				case(SERVICE_WSDL):
					specificTreat = new ServiceWSDLSpecificTreatment(this, getShell());
					break;
				case(SERVICE_WAR):
					specificTreat = new ServiceWarSpecificTreatment(this, getShell());
					break;
				default:
					specificTreat = new NewMethodSpecificTreatment(this, getShell());
					break;
			}
			ServiceSsCoreElementWizardPage p = (ServiceSsCoreElementWizardPage) getNextPage();
			if (p!=null)
				p.setType(type);
			if (pageCreated) {
				log.debug("Updating composite");
				composite = specificTreat.updateSecondPageGroupControls(group, composite);
				composite.layout(true);
				composite.redraw();
				group.layout(true);
				group.redraw();
				this.getRootComposite().layout(true);
				this.getRootComposite().redraw();
				addExtraListeners();
			}
		}
	}


	/**
	 * Check if the mandatory field of the second page are filled.
	 * @return True if complete, otherwise false.
	 */
	public boolean isComplete() {
		return specificTreat.isPageComplete();
	}
	
	/* (non-Javadoc)
	 * @see es.bsc.servicess.ide.wizards.ServiceSsCommonWizardPage#doStatusUpdate()
	 */
	public void doStatusUpdate() {
		// status of all used components
		IStatus[] status = new IStatus[] { fContainerStatus, fPackageStatus,
				fClassStatus, fCEStatus };

		// the mode severe status will be displayed and the OK button
		// enabled/disabled.
		updateStatus(status);
	}

	/**
	 * Get the default package of the core elements 
	 * @return Default core elements package name
	 */
	public String getCoresPackage() {
		try {
			IFile m_file = getJavaProject().getProject()
					.getFolder(ProjectMetadata.METADATA_FOLDER)
					.getFile(ProjectMetadata.METADATA_FILENAME);
			ProjectMetadata pr_meta = new ProjectMetadata(m_file
					.getRawLocation().toFile());
			return pr_meta.getCoreElementsPackageName();
		} catch (Exception e) {
			log.error("Error adding dependency to element");
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Get the Core element status
	 * @return
	 */
	public StatusInfo getCEStatus() {
		return fCEStatus;
	}
	
	/**
	 * Execute the methods to perform the cancellation of the 
	 * wizard according to the core element generation type
	 */
	public void performCancel() {
		specificTreat.performCancel();
		
	}
	
	/**
	 * Execute the methods to perform the creation core element according 
	 * to generation type when the wizard is finalized
	 * @param monitor Progress monitor
	 * @throws CoreException
	 */
	public void performFinish(IProgressMonitor monitor) throws CoreException{
		specificTreat.performFinish(monitor);
	}

}
