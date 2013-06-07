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

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.search.IJavaSearchConstants;
import org.eclipse.jdt.core.search.IJavaSearchScope;
import org.eclipse.jdt.core.search.SearchEngine;
import org.eclipse.jdt.internal.core.JavaModelManager;
import org.eclipse.jdt.internal.ui.dialogs.FilteredTypesSelectionDialog;
import org.eclipse.jdt.ui.JavaElementLabelProvider;
import org.eclipse.jdt.ui.wizards.NewElementWizardPage;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.ElementListSelectionDialog;

import es.bsc.servicess.ide.Activator;
import es.bsc.servicess.ide.Logger;
import es.bsc.servicess.ide.PackagingUtils;
import es.bsc.servicess.ide.ProjectMetadata;
import es.bsc.servicess.ide.TitlesAndConstants;
import es.bsc.servicess.ide.dialogs.JarFileDialog;
import es.bsc.servicess.ide.model.MethodCoreElement;

public class ServiceSsImportOrchestrationClassPage extends WizardPage{

	private Logger log = Logger.getLogger(ServiceSsImportOrchestrationClassPage.class);
	private final static String PAGE_NAME = "ImportOrchestrationClassWizardPage";
	private Text warLocation;
	private Button warSelectButton;
	private Text declaringClass;
	private Button selectClassButton;

	private Text libraryLocation;
	private Button librarySelectButton;
	private IJavaProject project;
	private String warPath;
	private String libraryPath;
	private IFolder packageExtractFolder;
	private IType oeClass;
	private boolean libraryAdded;
		
	protected ServiceSsImportOrchestrationClassPage(IJavaProject project) {
		super(PAGE_NAME);
		this.project = project;
		setTitle(TitlesAndConstants.getImportOrchestrationClassPageWizardTitle());
		setDescription(TitlesAndConstants.getImportOrchestrationClassPageWizardDescription());
	}

	@Override
	public void createControl(Composite group) {
		Composite comp = new Composite(group, SWT.NONE);
		GridData rd = new GridData(GridData.FILL_HORIZONTAL
				| GridData.VERTICAL_ALIGN_BEGINNING);
		// rd.grabExcessHorizontalSpace = true;
		comp.setLayoutData(rd);
		comp.setLayout(new GridLayout(2, false));
		Label serviceWAR = new Label(comp, SWT.NONE);
		serviceWAR.setText("Package location");
		Composite war = new Composite(comp, SWT.NONE);
		rd = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		rd.grabExcessHorizontalSpace = true;
		rd.horizontalIndent = 0;
		rd.horizontalSpan = 0;
		war.setLayoutData(rd);
		GridLayout oeReturnLayout = new GridLayout();
		oeReturnLayout.numColumns = 2;
		oeReturnLayout.marginLeft = 0;
		oeReturnLayout.marginRight = 0;
		war.setLayout(oeReturnLayout);
		warLocation = new Text(war, SWT.SINGLE | SWT.BORDER);
		rd = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		rd.grabExcessHorizontalSpace = true;
		warLocation.setLayoutData(rd);
		warLocation.setEditable(false);
		warSelectButton = new Button(war, SWT.NONE);
		warSelectButton.setText("Select...");
		Label libraryLabel = new Label(comp, SWT.NONE);
		libraryLabel.setText("Sub-package(if any jar)");
		Composite wsdlc = new Composite(comp, SWT.NONE);
		rd = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		rd.grabExcessHorizontalSpace = true;
		rd.horizontalIndent = 0;
		rd.horizontalSpan = 0;
		wsdlc.setLayoutData(rd);
		oeReturnLayout = new GridLayout();
		oeReturnLayout.numColumns = 2;
		oeReturnLayout.marginLeft = 0;
		oeReturnLayout.marginRight = 0;
		wsdlc.setLayout(oeReturnLayout);
		libraryLocation = new Text(wsdlc, SWT.SINGLE | SWT.BORDER);
		rd = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		rd.grabExcessHorizontalSpace = true;
		libraryLocation.setLayoutData(rd);
		librarySelectButton = new Button(wsdlc, SWT.NONE);
		librarySelectButton.setText("Select...");

		// Declaring/class
		Label declaringClassLabel = new Label(comp, SWT.NONE);
		declaringClassLabel.setText("Declaring Class");
		Composite dc = new Composite(comp, SWT.NONE);
		rd = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		rd.grabExcessHorizontalSpace = true;
		rd.horizontalIndent = 0;
		rd.horizontalSpan = 0;
		dc.setLayoutData(rd);
		oeReturnLayout = new GridLayout();
		oeReturnLayout.numColumns = 2;
		oeReturnLayout.marginLeft = 0;
		oeReturnLayout.marginRight = 0;
		dc.setLayout(oeReturnLayout);
		declaringClass = new Text(dc, SWT.SINGLE | SWT.BORDER);
		rd = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		rd.grabExcessHorizontalSpace = true;
		declaringClass.setLayoutData(rd);
		selectClassButton = new Button(dc, SWT.NONE);
		selectClassButton.setText("Select...");
		addListeners();
		setControl(comp);
	}

	private void addListeners() {
		warSelectButton.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {
				newWarLocation();
			}

			@Override
			public void widgetSelected(SelectionEvent arg0) {
				newWarLocation();
			}
		});
		librarySelectButton.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {
				newLibraryLocation();
			}

			@Override
			public void widgetSelected(SelectionEvent arg0) {
				newLibraryLocation();
			}
		});
		
		selectClassButton.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {
					selectOEClass();
			}

			@Override
			public void widgetSelected(SelectionEvent arg0) {
					selectOEClass();
			}
		});
		
		
		
	}
	
	protected void newWarLocation() {
		// Con JarFile Dialog
		boolean packageAdded = false;
		if (warLocation.getText().trim()!=null && warLocation.getText().trim().length()>0)
			packageAdded = true;
		JarFileDialog dialog = new JarFileDialog(this.getShell(),
			this.project, libraryAdded, libraryPath, 
			new String[]{ProjectMetadata.WAR_DEP_TYPE, 	ProjectMetadata.JAR_DEP_TYPE, 
			ProjectMetadata.ZIP_DEP_TYPE},null, true);
		if (dialog.open() == Window.OK) {
			warLocation.setText(dialog.getPath());
			warPath = dialog.getPath();
			libraryLocation.setText("");
			librarySelectButton.setEnabled(true);
			declaringClass.setText("");
			oeClass = null;
		}
		
		/*final FileDialog dialog = new FileDialog(this.getShell());
		dialog.setText("Select WAR File");
	
		String[] filterExt = { "*.war" };
		dialog.setFilterExtensions(filterExt);
		String directoryName = warLocation.getText().trim();
		if (directoryName.length() > 0) {
			File fpath = new File(directoryName);
			if (fpath.exists())
				dialog.setFilterPath(directoryName);
		}
		final String selectedDirectory = dialog.open();
		if (selectedDirectory != null && selectedDirectory.length()>0) {
			IFolder importFolder = project.getProject()
					.getFolder(ProjectMetadata.IMPORT_FOLDER);
				
			if (loadWarFile(selectedDirectory, importFolder)){
				warLocation.setText(selectedDirectory);
				warPath = selectedDirectory;
				libraryLocation.setText("");
				libraryPath = null;
				librarySelectButton.setEnabled(true);
				declaringClass.setText("");
				oeClass = null;
			}
		}*/ 
	}

	private boolean loadWarFile(final String selectedDirectory, final IFolder importFolder) {
		
		final ProgressMonitorDialog dialog = new ProgressMonitorDialog(this.getShell());
		try {
			log.debug("Selected dir: " + selectedDirectory);
			
			final File warFile = new File(selectedDirectory);	
			if (warFile.exists()) {
				dialog.run(false, true, new IRunnableWithProgress() {
					public void run(IProgressMonitor monitor)
							throws InvocationTargetException {
						try {
							if (!importFolder.exists())
								importFolder.create(true, true, monitor);
							String name = PackagingUtils.getPackageName(warFile);
							log.debug("Package name is " + name);
							IFolder extractFolder = importFolder.getFolder(name);
							if (!extractFolder.exists()){
								extractWar(warFile, extractFolder, monitor);
								updateDeps(warPath, selectedDirectory,ProjectMetadata.WAR_DEP_TYPE,monitor);
							}else
								log.info("Package already exists. Not extracting");
							
						} catch (Exception e) {
							throw (new InvocationTargetException(e));
						}
					}
				});
				return true;
			}else
				throw(new InvocationTargetException(
						new Exception("The selected file doesn't exists")));
		} catch (InvocationTargetException e) {
			log.error("Error loading package");
			ErrorDialog.openError(dialog.getShell(),
					"Error loading new package file", "Exception when loading package", new Status(IStatus.ERROR, Activator.PLUGIN_ID, e.getMessage(),e));
			return false;
		} catch (InterruptedException e) {
			log.error("Error loading package");
			ErrorDialog.openError(dialog.getShell(), "Package load interrumped",
					"Exception when loading package",new Status(IStatus.ERROR, Activator.PLUGIN_ID, e.getMessage(),e));
			return false;
		}	
	}
	
	private void extractWar(File warFile, IFolder extractFolder, IProgressMonitor progressMonitor) throws IOException, CoreException, InterruptedException {
		if (extractFolder!= null && extractFolder.exists()){
			log.debug("Removing " + extractFolder.getRawLocation().toOSString());
			extractFolder.delete(true, progressMonitor);
		}
		extractFolder.create(true, false, progressMonitor);
		log.debug("Created " + extractFolder.getRawLocation().toOSString());
		PackagingUtils.extractZip(warFile, extractFolder.getRawLocation().toOSString(), extractFolder, progressMonitor);
		extractFolder.refreshLocal(1, progressMonitor);
	}
	
	/** Method to update a dependency in the project
	 * @param monitor Monitor to report the progress
	 */
	public void updateDeps(String prev_jarLoc, String path, String sType, IProgressMonitor monitor) {
		try {
				IFile m_file = project.getProject()
						.getFolder(ProjectMetadata.METADATA_FOLDER)
						.getFile(ProjectMetadata.METADATA_FILENAME);

				ProjectMetadata pr_meta = new ProjectMetadata(m_file
						.getRawLocation().toFile());
				if (libraryAdded) {
					log.debug("Library " + prev_jarLoc + " removed");
					pr_meta.removeDependency(prev_jarLoc);
				}
				if (!pr_meta.existsDependency(path, sType)) {
					pr_meta.addDependency(path, sType);
					libraryAdded = true;
					log.debug("Library " + path + " Added");
				} else {
					log.debug("Library " + path + " already exists");
					libraryAdded = false;
				}
				pr_meta.toFile(m_file.getRawLocation().toFile());
				m_file.refreshLocal(1, monitor);
				JavaCore.getClasspathContainerInitializer("Dependencies")
						.initialize(
								new Path(ProjectMetadata.DEPENDENCY_ENTRYPATH),
								project);

				JavaModelManager manager = JavaModelManager
						.getJavaModelManager();
				manager.loadVariablesAndContainers();
				log.debug(" Updating ");
				project.getProject().refreshLocal(IResource.DEPTH_INFINITE,
						monitor);
				log.debug(" Dependency loaded");

		} catch (Exception e) {
			log.error("Exception found", e);
			ErrorDialog.openError(getShell(), "Error", e.getMessage(),
					new Status(IStatus.ERROR, Activator.PLUGIN_ID,
							"Exception when updating a dependency" ));
		}
	}
	
	private void selectOEClass() {
		IJavaElement[] elements;
		if (project == null) {
			MessageDialog.openError(this.getShell(), "Error",
					"Project is not selected");
			return;
		}
			try {
				//
				project.getProject().refreshLocal(IResource.DEPTH_INFINITE,
						null);
				ArrayList<IPackageFragmentRoot> pfrs = new ArrayList<IPackageFragmentRoot>();
				String packLoc;
				if (libraryLocation != null && libraryLocation.getText().trim().length() > 0) {
					packLoc = libraryLocation.getText().trim();
				}else if (warPath.endsWith(".jar")){
					packLoc = warPath.trim();
				}else{
					packLoc = project.getProject().getFolder(ProjectMetadata.IMPORT_FOLDER)
							.getFolder(PackagingUtils.getPackageName(warPath)).getFolder("WEB-INF")
							.getFolder("classes").getFullPath().toOSString().trim();
				}		
				for (IPackageFragmentRoot r : project.getAllPackageFragmentRoots()) {
					log.debug("PFR: " + r.getElementName()
							+ " entry: "
							+ r.getResolvedClasspathEntry().getPath().toOSString());
					
						log.debug("Looking for "+ packLoc);
						
						if (r.getResolvedClasspathEntry().getPath().toOSString()
							.trim().equals(packLoc)) {
							pfrs.add(r);
						}
				}
				elements = pfrs.toArray(new IPackageFragmentRoot[pfrs.size()]);
			} catch (Exception e) {
				e.printStackTrace();
				ErrorDialog.openError(this.getShell(), "Error",
						"Error opening package location", new Status(IStatus.ERROR, 
								Activator.PLUGIN_ID, e.getMessage(),e) );
				return;

			} 
		if (elements == null || elements.length <= 0) {
			ErrorDialog.openError(this.getShell(), "Error",
					"Getting java elements", new Status(IStatus.ERROR, Activator.PLUGIN_ID, 
							"Java elements not found"));
			return;
		}
		IJavaSearchScope scope = SearchEngine.createJavaSearchScope(elements);
		FilteredTypesSelectionDialog dialog = new FilteredTypesSelectionDialog(
				this.getShell(), false, this.getWizard().getContainer(), scope,
				IJavaSearchConstants.CLASS);
		dialog.setTitle("Class Selection");
		dialog.setMessage("Select a Class");
		dialog.setInitialPattern(declaringClass.getText().trim());
		if (declaringClass.getText().trim().length() > 0) {
			dialog.setInitialPattern(declaringClass.getText().trim());
		} else
			dialog.setInitialPattern("?");
		if (dialog.open() == Window.OK) {
			oeClass = (IType) dialog.getFirstResult();
			if (!declaringClass.getText().trim()
					.equals(oeClass.getElementName())) {
				declaringClass.setText(oeClass.getFullyQualifiedName());
			}
		} else {
			/*
			 * TODO if (libraryAdded){ cont.removeEntry(entry); libraryAdded =
			 * false; cont = null; entry = null; }
			 */
		}

	}
	
	
	protected void newLibraryLocation() {
		// Con JarFile Dialog
		String defaultPath = this.getImportedLocation();
		JarFileDialog dialog = new JarFileDialog(this.getShell(),
				this.project, libraryAdded, libraryPath, 
				new String[]{ProjectMetadata.CLASS_FOLDER_DEP_TYPE, 
								ProjectMetadata.JAR_DEP_TYPE}, defaultPath, true);
		if (dialog.open() == Window.OK) {
			libraryLocation.setText(dialog.getPath());
			libraryAdded = dialog.isLibraryAdded();
			libraryPath = dialog.getPath();
		}
	}
	

	public String getTypeName() {
		return oeClass.getFullyQualifiedName();
	}
	
	public IType getType(){
		 return oeClass;
	}
	
	public String getTypePackageName(){
		return oeClass.getPackageFragment().getElementName();
	}

	public String getExternalLocation() {
		return warPath;
	}
	
	public String getIntraPackageLocation() throws Exception{
		if (libraryPath!=null && libraryPath.trim().length()>0){
			return libraryPath;
		}else if (warPath.endsWith(".jar")){
			return warPath.trim();
		}else{
			return project.getProject().getFolder(ProjectMetadata.IMPORT_FOLDER)
				.getFolder(PackagingUtils.getPackageName(warPath)).getFolder("WEB-INF")
				.getFolder("classes").getFullPath().toOSString().trim();
		}
	}
	
	public String getImportedLocation() {
		
		if (warPath.endsWith(".jar")){
			return null;
		}else{
				try {
					return project.getProject().getFolder(ProjectMetadata.IMPORT_FOLDER)
					.getFolder(PackagingUtils.getPackageName(warPath)).getFullPath().toOSString().trim();
				} catch (Exception e) {
					log.error("Error getting package name", e);
					e.printStackTrace();
					return null;
				}
		}
	}
	
	

	public void removeDependencies() {
		
		try {
			IFile m_file = project.getProject()
				.getFolder(ProjectMetadata.METADATA_FOLDER)
				.getFile(ProjectMetadata.METADATA_FILENAME);

			ProjectMetadata pr_meta = new ProjectMetadata(m_file
				.getRawLocation().toFile());
			if(warPath!=null){
				log.debug("Library " + warPath + " removed");
				pr_meta.removeDependency(warPath);
			}	
			if (libraryAdded) {
				log.debug("Library " + libraryPath + " removed");
				pr_meta.removeDependency(libraryPath);
			}
			pr_meta.toFile(m_file.getRawLocation().toFile());
			m_file.refreshLocal(1, new NullProgressMonitor());
		} catch (Exception e) {
			e.printStackTrace();
			ErrorDialog.openError(this.getShell(), "Error",
					"Error opening package location", new Status(IStatus.ERROR, 
							Activator.PLUGIN_ID, e.getMessage(),e) );
		} 
	}
	
	public void removeFolders() {
		try {
			project.getProject().getFolder(ProjectMetadata.IMPORT_FOLDER)
				.getFolder(PackagingUtils.getPackageName(warPath)).delete(true, new NullProgressMonitor());
		} catch (Exception e) {
			e.printStackTrace();
			ErrorDialog.openError(this.getShell(), "Error",
					"Error opening package location", new Status(IStatus.ERROR, 
							Activator.PLUGIN_ID, e.getMessage(),e) );
		} 
	}
	
	
}
