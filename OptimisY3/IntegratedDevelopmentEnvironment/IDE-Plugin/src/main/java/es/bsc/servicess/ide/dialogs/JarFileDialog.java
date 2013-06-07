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

package es.bsc.servicess.ide.dialogs;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.LinkedList;

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
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.internal.core.JavaModelManager;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import es.bsc.servicess.ide.Activator;
import es.bsc.servicess.ide.Logger;
import es.bsc.servicess.ide.PackagingUtils;
import es.bsc.servicess.ide.ProjectMetadata;

/** Dialog for selecting a Jar file
 * @author Jorge Ejarque(Barcelona Supercomputing Center)
 *
 */
public class JarFileDialog extends Dialog {
	private IJavaProject project;
	private Text libraryText;
	private String path;
	private String sType;
	/*private IClasspathEntry prev_entry;
	private boolean prev_libraryAdded;
	private IClasspathEntry entry = null;*/
	private boolean libraryAdded = false;
	private String prev_jarLoc;
	private String[] types;
	private boolean imported;
	private String defaultPath;
	private Combo type;
	private static Logger log = Logger.getLogger(JarFileDialog.class);

	/** Constructor
	 * @param parent shell of the parent window
	 * @param project Java project of the implemented service
	 * @param libraryAdded Boolean to check if a library has been added previously
	 * @param prev_jarLoc Location of the library added previously
	 */
	public JarFileDialog(Shell parent, IJavaProject project,
			boolean libraryAdded, String prev_jarLoc, String[] types, String defaultPath, boolean imported) {
		super(parent);
		this.project = project;
		this.prev_jarLoc = prev_jarLoc;
		this.libraryAdded = libraryAdded;
		this.types = types;
		this.imported = imported;
		this.defaultPath = defaultPath;
		log.debug("Imported is set to " + this.imported +" and default path set to " +defaultPath);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.Dialog#createDialogArea(org.eclipse.swt.widgets.Composite)
	 */
	public Composite createDialogArea(Composite parent) {
		Composite comp = (Composite) super.createDialogArea(parent);
		comp.setLayout(new GridLayout(1, false));
		GridData rd = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		rd.grabExcessHorizontalSpace = true;
		comp.setLayoutData(rd);
		Composite type_composite = new Composite(comp, SWT.NONE);
		type_composite.setLayout(new GridLayout(2, false));
		rd = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		rd.grabExcessHorizontalSpace = true;
		type_composite.setLayoutData(rd);
		Label typeLabel = new Label(type_composite, SWT.NONE);
		typeLabel.setText("Library Type");
		type = new Combo(type_composite, SWT.SINGLE | SWT.BORDER);
		type.setItems(types);
		type.select(0);
		updateType();
		rd = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		rd.grabExcessHorizontalSpace = true;
		type.setLayoutData(rd);
		type.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent arg0) {
				updateType();
			}
		});
		Composite composite = new Composite(comp, SWT.NONE);
		composite.setLayout(new GridLayout(3, false));
		rd = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		rd.grabExcessHorizontalSpace = true;
		composite.setLayoutData(rd);
		Label serverLabel = new Label(composite, SWT.NONE);
		serverLabel.setText("Library");
		libraryText = new Text(composite, SWT.SINGLE | SWT.BORDER);
		rd = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		rd.grabExcessHorizontalSpace = true;
		rd.minimumWidth = 300;
		libraryText.setLayoutData(rd);
		Button serverButton = new Button(composite, SWT.NORMAL);
		serverButton.setText("Select");
		serverButton.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {
				updatePath();
			}

			@Override
			public void widgetSelected(SelectionEvent arg0) {
				updatePath();
			}
		});
		return comp;
	}
	/**Update the dependency type
	 * 
	 */
	protected void updateType() {
		sType = type.getItem(type.getSelectionIndex());

	}

	/**Update the dependency path
	 * 
	 */

	private void updatePath() {
		if (sType.equalsIgnoreCase(ProjectMetadata.CLASS_FOLDER_DEP_TYPE)) {
			selectFolderLocation();
		} else
			selectJarLocation();

	}

	/**Open a dialog to select a folder
	 * 
	 */
	protected void selectFolderLocation() {
		final DirectoryDialog dialog = new DirectoryDialog(this.getShell());
		dialog.setText("Select Class Folder");
		String directoryName = libraryText.getText().trim();
		if (directoryName.length() > 0) {
			final File path = new File(directoryName);
			if (path.exists())
				dialog.setFilterPath(directoryName);
			else
				log.debug("Path " + directoryName + " does not exist");
		}else if (defaultPath !=null && defaultPath.length()>0){
			final File path = new File(defaultPath);
			if (path.exists())
				dialog.setFilterPath(defaultPath);
			else
				log.debug("Path " + defaultPath + " does not exist");
		}else
			log.debug("No path selected");
		final String selectedDirectory = dialog.open();
		if (selectedDirectory != null) {
			libraryText.setText(selectedDirectory);
			path = selectedDirectory;
		}

	}

	/**Open a dialog to select the location of the jar file
	 * 
	 */
	protected void selectJarLocation() {
		final FileDialog dialog = new FileDialog(this.getShell());
		dialog.setText("Select Package");
		LinkedList<String> filterExt = new LinkedList<String>();
		if (sType.equals(ProjectMetadata.JAR_DEP_TYPE)){
			filterExt.add("*.jar");
		}else if (sType.equals(ProjectMetadata.WAR_DEP_TYPE)){
			filterExt.add("*.war");
		}else if (sType.equals(ProjectMetadata.ZIP_DEP_TYPE)){
			filterExt.add("*.zip");
		}
		dialog.setFilterExtensions(filterExt.toArray(new String[filterExt.size()]));
		String directoryName = libraryText.getText().trim();
		if (directoryName.length() > 0) {
			final File path = new File(directoryName);
			if (path.exists())
				dialog.setFilterPath(directoryName);
			else
				log.debug("Path " + directoryName + " does not exist");
		}else if (defaultPath !=null && defaultPath.length()>0){
			final File path = new File(defaultPath);
			if (path.exists())
				dialog.setFilterPath(defaultPath);
			else
				log.debug("Path " + defaultPath + " does not exist");
		}else
			log.debug("No path selected");
		final String selectedDirectory = dialog.open();
		if (selectedDirectory != null) {

			libraryText.setText(selectedDirectory);
			path = selectedDirectory;
		}

	}

	/** Runnable to update a dependency in the jar
	 * 
	 */
	public void updateDependency() {
		Display.getDefault().syncExec(new Runnable() {
			@Override
			public void run() {
				updateDeps(new NullProgressMonitor());
			}
		});

	}

	/** Method to update a dependency in the project
	 * @param monitor Monitor to report the progress
	 */
	public void updateDeps(IProgressMonitor monitor) {
		try {
			/*
			IClasspathEntry ent = JavaCore.newLibraryEntry(new Path(path),
					null, null, true);
			if (ent != null) {*/
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
					pr_meta.addDependency(path, sType, imported);
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
			/*} else {
				ErrorDialog.openError(getShell(), "Error", "Updating libraries",
						new Status(IStatus.ERROR, Activator.PLUGIN_ID,
								"We can not create a library entry for " + path));
				path = null;
			}*/

		} catch (Exception e) {
			log.error("Exception found", e);
			ErrorDialog.openError(getShell(), "Error", e.getMessage(),
					new Status(IStatus.ERROR, Activator.PLUGIN_ID,
							"Exception when updating a dependency" ));
			path = null;
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.Dialog#okPressed()
	 */
	protected void okPressed() {

		if (sType != null && path != null && sType.length() > 0
				&& path.length() > 0) {
			if (imported){
				if (loadWarFile(path)){
					updateDependency();
					super.okPressed();
				}
			}else{
				updateDependency();
				super.okPressed();
			}
		} else {
			ErrorDialog.openError(getShell(), "Error",
					"There are fields not filled", new Status(
							IStatus.ERROR, Activator.PLUGIN_ID, "Error missing parameters"));
		}
	}
	
	private boolean loadWarFile(final String selectedDirectory) {
		
		final ProgressMonitorDialog dialog = new ProgressMonitorDialog(this.getShell());
		try {
			log.debug("Selected dir: " + selectedDirectory);
			
			final File warFile = new File(selectedDirectory);	
			if (warFile.exists()) {
				dialog.run(false, true, new IRunnableWithProgress() {
					public void run(IProgressMonitor monitor)
							throws InvocationTargetException {
						try {
							IFolder importFolder = project.getProject()
									.getFolder(ProjectMetadata.IMPORT_FOLDER);
							if (!importFolder.exists())
								importFolder.create(true, true, monitor);
							String name = PackagingUtils.getPackageName(warFile);
							log.debug("Package name is " + name);
							IFolder extractFolder = importFolder.getFolder(name);
							if (!extractFolder.exists()){
								extractWar(warFile, extractFolder, monitor);
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

	/** Check if a library has been added
	 * @return True is added, otherwise false 
	 */
	public boolean isLibraryAdded() {
		return this.libraryAdded;
	}

	/** Getter for the dependency path
	 * @return Dependency path
	 */
	public String getPath() {
		return path;
	}

	/** Getter for the dependency path
	 * @return Dependency type
	 */
	public String getType() {
		return sType;
	}

}
