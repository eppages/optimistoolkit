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
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import javax.wsdl.WSDLException;
import javax.wsdl.xml.WSDLReader;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.core.JavaModelManager;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.ibm.wsdl.xml.WSDLReaderImpl;
import com.sun.tools.xjc.api.S2JJAXBModel;

import es.bsc.servicess.ide.Activator;
import es.bsc.servicess.ide.Logger;
import es.bsc.servicess.ide.PackagingUtils;
import es.bsc.servicess.ide.ProjectMetadata;
import es.bsc.servicess.ide.dialogs.JarFileDialog;
import es.bsc.servicess.ide.dialogs.URLPatternDialog;
import es.bsc.servicess.ide.model.ElementClass;
import es.bsc.servicess.ide.model.ServiceCoreElement;
import es.bsc.servicess.ide.model.ServiceElement;
import es.bsc.servicess.ide.wizards.ServiceSsCoreElementSecondPage;
import es.bsc.servicess.ide.wizards.TypeSpecificTreatment;

public class ServiceWarSpecificTreatment extends ServiceSpecificTreatment {

	private static final String TMP_PATH = "war_tmp";
	private Text warLocation;
	private Button warSelectButton;
	private String warPath;
	private Text wsdlLocation;
	private Button wsdlSelectButton;
	private Text urlPattern;
	private String serviceURLPath;
	private Button urlPatternSelectButton;
	private Logger log = Logger.getLogger();

	public ServiceWarSpecificTreatment(
			ServiceSsCoreElementSecondPage secondPage, Shell shell) {
		super(secondPage, shell);
	}

	protected void newWarLocation() {
		final FileDialog dialog = new FileDialog(shell);
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
			IFolder extractFolder = secondPage.getJavaProject().getProject()
					.getFolder(ProjectMetadata.OUTPUT_FOLDER).getFolder(TMP_PATH);
			loadWarFile(selectedDirectory, extractFolder);			
		} 
	}
	
	protected void newWSDLLocation() {
		final FileDialog dialog = new FileDialog(shell);
		dialog.setText("Select WSDL File");
		try{
			IFolder wsdlFolder = getWSDLFolder(warPath, secondPage.getJavaProject().getProject()
					.getFolder(ProjectMetadata.OUTPUT_FOLDER).getFolder(TMP_PATH));
			if(wsdlFolder!=null && wsdlFolder.exists()){
				String[] filterExt = { "*.wsdl" };
				dialog.setFilterExtensions(filterExt);
				String relative = wsdlLocation.getText().trim();
				if (relative.length() > 0) {
					File fpath = wsdlFolder.getRawLocation().append(relative).toFile();
					if (fpath.exists())
						dialog.setFilterPath(fpath.getAbsolutePath());
					else
						dialog.setFilterPath(wsdlFolder.getRawLocation().toOSString());
				}else
					dialog.setFilterPath(wsdlFolder.getRawLocation().toOSString());
				final String selectedDirectory = dialog.open();
				if (selectedDirectory != null&& selectedDirectory.length()>0) {
					loadWSDLFile(selectedDirectory);
				}
			} else {
				ErrorDialog.openError(shell, "Error ",
						"There is not wsdl folder in the war file", new Status(
								IStatus.ERROR, Activator.PLUGIN_ID, "Error file not found"));
			}
		}catch (Exception e) {
				ErrorDialog.openError(shell, "Error loading wsdl folder","Exception when loading the wsdl folder", 
						new Status(IStatus.ERROR, Activator.PLUGIN_ID,e.getMessage(), e));
			}

	}
	
	protected void newURLPattern() {
		try{
			File webXMLFile = getWEBXMLFile(warPath, 
					secondPage.getJavaProject().getProject()
					.getFolder(ProjectMetadata.OUTPUT_FOLDER).getFolder(TMP_PATH));
			if(webXMLFile!=null && webXMLFile.exists()){
			URLPatternDialog dialog = new URLPatternDialog(shell, 
					webXMLFile, getWarName(warPath));
			//dialog.setText("Select WSDL File");
			
			if (dialog.open() == Window.OK) {
				
				String pattern = dialog.getURLPattern();
					if (pattern != null && pattern.length()>0) {
						serviceURLPath = pattern;
						urlPattern.setText(pattern);		
					}else
						ErrorDialog.openError(shell, "Error ",
							"There is not pattern selected", new Status(
							IStatus.ERROR, Activator.PLUGIN_ID, "Error file not found"));
				}
			} else {
				ErrorDialog.openError(shell, "Error ",
						"There is not web.xml in the war file", new Status(
								IStatus.ERROR, Activator.PLUGIN_ID, "Error file not found"));
			}
		}catch (Exception e) {
				ErrorDialog.openError(shell, "Error loading url patterns from web.xml ","Exception when loading URL patterns from the web", 
						new Status(IStatus.ERROR, Activator.PLUGIN_ID,e.getMessage(), e));
			}

	}

	private void loadWarFile(final String selectedDirectory, final IFolder extractFolder) {
		final ProgressMonitorDialog dialog = new ProgressMonitorDialog(shell);
		try {
			log.debug("Selected dir: " + selectedDirectory);
			final File warFile = new File(selectedDirectory);	
			if (warFile.exists()) {
				dialog.run(true, true, new IRunnableWithProgress() {
					public void run(IProgressMonitor monitor)
							throws InvocationTargetException {
						try {
							extractWar(warFile, extractFolder, monitor);
						} catch (Exception e) {
							throw (new InvocationTargetException(e));
						}
					}
				});

				resetServiceInfo();
				warLocation.setText(selectedDirectory);
				warPath = selectedDirectory;
				wsdlLocation.setText("");
				urlPattern.setText("");
				wsdl = null;
				serviceURLPath = null;
				wsdlSelectButton.setEnabled(true);
				urlPatternSelectButton.setEnabled(true);
			}else
				throw(new InvocationTargetException(
						new Exception("The selected file doesn't exists")));
		} catch (InvocationTargetException e) {
			ErrorDialog.openError(dialog.getShell(),
					"Error loading new war file", "Exception when loading war", new Status(IStatus.ERROR, Activator.PLUGIN_ID, e.getMessage(),e));
			e.printStackTrace();
		} catch (InterruptedException e) {
			ErrorDialog.openError(dialog.getShell(), "War load interrumped",
					"Exception when loading war",new Status(IStatus.ERROR, Activator.PLUGIN_ID, e.getMessage(),e));
			e.printStackTrace();
		}	
	}
	
	private void loadWSDLFile(final String selectedDirectory) {
		final ProgressMonitorDialog dialog = new ProgressMonitorDialog(
				shell);
		try {
			final File fpath = new File(selectedDirectory);
			if (fpath.exists()) {
				dialog.run(true, true, new IRunnableWithProgress() {
					public void run(IProgressMonitor monitor)
							throws InvocationTargetException {
						try {
							loadWSDL(fpath);
						} catch (Exception e) {
							throw (new InvocationTargetException(e));
						}
					}
				});
				wsdlLocation.setText(selectedDirectory);
				resetServiceInfo();
			} else {
				throw(new InvocationTargetException(
						new Exception("The selected file doesn't exists")));
			}
		} catch (InvocationTargetException e) {
			ErrorDialog.openError(dialog.getShell(),
					"Error loading new WSDL file", e.getMessage(), new Status(IStatus.ERROR, Activator.PLUGIN_ID, e.getMessage(),e));
			e.printStackTrace();
		} catch (InterruptedException e) {
			ErrorDialog.openError(dialog.getShell(), "WSDL load interrumped",
					e.getMessage(),new Status(IStatus.ERROR, Activator.PLUGIN_ID, e.getMessage(),e));
			e.printStackTrace();
		}	
	}

	private void loadWSDL(File wsdlFile) throws Exception {
		//Is extracted without folder
			URL url = wsdlFile.toURL();
			if (url != null) {
				WSDLReader wsdlreader = new WSDLReaderImpl();
				wsdl = wsdlreader.readWSDL(url.toString());
				if (wsdl == null) {
					throw(new Exception("WSDL object is null"));
				} 
			}else{
				throw(new Exception("WSDL URL is null"));
			}
				
			
	}
	private String getWarName(String warFile) throws Exception{
		String name = new File(warFile).getName();
		if (name!=null&& name.length()>0 && name.endsWith(".war")){
			name = name.substring(0,name.indexOf(".war"));
			return name;
		}else{
			log.error("WAR package name not found");
			throw( new Exception("WAR package name not found"));
		}
	}
	
	private IFolder getWSDLFolder(String warFile, IFolder extractFolder) throws Exception {
		//Get war name
		String name = new File(warFile).getName();
		if (name!=null&& name.length()>0 && name.endsWith(".war")){
			name = name.substring(0,name.indexOf(".war"));
			log.debug("WAR package name is " + name);
		}else{
			log.error("WAR package name not found");
			throw( new Exception("WAR package name not found"));
		}
		IFolder webinfFolder;
		log.debug("Checking ..." + 
				extractFolder.getFolder(name).getFolder("WEB-INF").getRawLocation().toOSString() +
				" or " + extractFolder.getFolder("WEB-INF").getRawLocation().toOSString());
		if (extractFolder.getFolder(name).getFolder("WEB-INF").exists()){
			webinfFolder = extractFolder.getFolder(name).getFolder("WEB-INF");
		}else if (extractFolder.getFolder("WEB-INF").exists()){
			webinfFolder = extractFolder.getFolder("WEB-INF");
		}else{
			log.error("WEB-INF folder not found");
			throw( new Exception("WEB-INF folder not found"));
		}
		if (webinfFolder.getFolder("wsdl").exists()){
			return webinfFolder.getFolder("wsdl");	
		}else if (webinfFolder.getFolder("classes").exists()){
			if (hasWSDLFiles(webinfFolder.getFolder("classes").getRawLocation().toFile())){
				return webinfFolder.getFolder("classes");
			}else
				return webinfFolder;
		}else{
			return webinfFolder;
		}
	}
	private File getWEBXMLFile(String warFile, IFolder extractFolder) throws Exception {
		String name = new File(warFile).getName();
		if (name!=null&& name.length()>0 && name.endsWith(".war")){
			name = name.substring(0,name.indexOf(".war"));
			log.debug("WAR package name is " + name);
		}else{
			log.error("WAR package name not found");
			throw( new Exception("WAR package name not found"));
		}
		IFile webXML;
		log.debug("Checking ..." + 
				extractFolder.getFolder(name).getFolder("WEB-INF").getRawLocation().toOSString() +
				" or " + extractFolder.getFolder("WEB-INF").getRawLocation().toOSString());
		if (extractFolder.getFolder(name).getFolder("WEB-INF").exists()){
			webXML = extractFolder.getFolder(name).getFolder("WEB-INF").getFile("web.xml");
			if (webXML.exists()){
				return webXML.getRawLocation().toFile();
			}else{
				log.error("web.xml file not found");
				throw( new Exception("web.xml file not found"));
			}
		}else if (extractFolder.getFolder("WEB-INF").exists()){
			extractFolder.getFolder("WEB-INF").refreshLocal(1, null);
			webXML = extractFolder.getFolder("WEB-INF").getFile("web.xml");
			if (webXML.exists()){
				return webXML.getRawLocation().toFile();
			}else{
				log.error("web.xml file not found");
				throw( new Exception("web.xml file ("+webXML.getRawLocation().toOSString()+") not found"));
			}
			
		}else{
			log.error("WEB-INF folder not found");
			throw( new Exception("WEB-INF folder not found"));
		}
	}
	private boolean hasWSDLFiles(File dir) {
		if (dir.isDirectory()){;
			for (String name:dir.list()){
				if (name.endsWith(".wsdl"))
					return true;
			}
		}
		return false;
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

	public void addDependency(String dir, String urlPath) {
		try {
				IFile m_file = secondPage.getJavaProject().getProject()
						.getFolder(ProjectMetadata.METADATA_FOLDER)
						.getFile(ProjectMetadata.METADATA_FILENAME);

				ProjectMetadata pr_meta = new ProjectMetadata(m_file
						.getRawLocation().toFile());
				if (!pr_meta
						.existsDependency(dir, ProjectMetadata.WAR_DEP_TYPE)) {
					pr_meta.addDependency(dir, ProjectMetadata.WAR_DEP_TYPE, urlPath);
					log.debug("Library " + dir + " Added");
				} 
				pr_meta.addElementToDependency(dir, ProjectMetadata.WAR_DEP_TYPE,element.getLabel());
				pr_meta.toFile(m_file.getRawLocation().toFile());
				log.debug(" Dependency loaded");

		} catch (Exception e) {
			ErrorDialog.openError(shell, "Error updating dependencies","Exception when updating element dependencies", 
					new Status(IStatus.ERROR, Activator.PLUGIN_ID,e.getMessage(), e));
		}
	}

	@Override
	protected void addTypeLocationListener() {
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
		wsdlSelectButton.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {
				newWSDLLocation();
			}

			@Override
			public void widgetSelected(SelectionEvent arg0) {
				newWSDLLocation();
			}
		});
		urlPatternSelectButton.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {
				newURLPattern();
			}

			@Override
			public void widgetSelected(SelectionEvent arg0) {
				newURLPattern();
			}
		});
	}

	@Override
	protected void addTypeLocationComposite(Composite comp) {
		Label serviceWAR = new Label(comp, SWT.NONE);
		serviceWAR.setText("WAR location");
		Composite war = new Composite(comp, SWT.NONE);
		GridData rd = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
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
		
		Label serviceWSDLLabel = new Label(comp, SWT.NONE);
		serviceWSDLLabel.setText("WSDL location");
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
		wsdlLocation = new Text(wsdlc, SWT.SINGLE | SWT.BORDER);
		rd = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		rd.grabExcessHorizontalSpace = true;
		wsdlLocation.setLayoutData(rd);
		wsdlLocation.setEditable(false);
		wsdlSelectButton = new Button(wsdlc, SWT.NONE);
		wsdlSelectButton.setText("Select...");
		wsdlSelectButton.setEnabled(false);
		
		Label urlLabel = new Label(comp, SWT.NONE);
		urlLabel.setText("URL pattern");
		Composite urlc = new Composite(comp, SWT.NONE);
		rd = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		rd.grabExcessHorizontalSpace = true;
		rd.horizontalIndent = 0;
		rd.horizontalSpan = 0;
		urlc.setLayoutData(rd);
		oeReturnLayout = new GridLayout();
		oeReturnLayout.numColumns = 2;
		oeReturnLayout.marginLeft = 0;
		oeReturnLayout.marginRight = 0;
		urlc.setLayout(oeReturnLayout);
		urlPattern = new Text(urlc, SWT.SINGLE | SWT.BORDER);
		rd = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		rd.grabExcessHorizontalSpace = true;
		urlPattern.setLayoutData(rd);
		urlPattern.setEditable(false);
		urlPatternSelectButton = new Button(urlc, SWT.NONE);
		urlPatternSelectButton.setText("Select...");
		urlPatternSelectButton.setEnabled(false);
		
	}

	@Override
	protected void generateTypeSpecificParams(ServiceCoreElement el) {
		// Nothing to do
	}

	@Override
	protected boolean isTypeCompositeCompleate() {
		if (warLocation.getText().trim().length()>0)
			return true;
		else
			return false;
	}

	@Override
	protected void performTypeCompositeCancel() {
		removeTempFolder();
		
	}

	private void removeTempFolder() {
		IFolder extractFolder = secondPage.getJavaProject().getProject()
				.getFolder(ProjectMetadata.OUTPUT_FOLDER).getFolder(TMP_PATH);
		if (extractFolder!= null && extractFolder.exists()){
			try {
				extractFolder.delete(true, null);
			} catch (CoreException e) {
				ErrorDialog.openError(shell, "Error removing war temporal file","Exception removing temporal war folder", 
						e.getStatus());
				e.printStackTrace();
			}
		}
	}

	@Override
	protected void performTypeCompositeFinish() {
		removeTempFolder();
		addDependency(warPath, serviceURLPath);
		
	}

}
