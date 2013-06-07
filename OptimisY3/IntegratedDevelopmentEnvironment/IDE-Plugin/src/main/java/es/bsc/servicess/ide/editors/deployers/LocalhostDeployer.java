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

package es.bsc.servicess.ide.editors.deployers;

import integratedtoolkit.util.RuntimeConfigManager;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.io.FileUtils;
import org.eclipse.core.internal.resources.Resource;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.jdt.internal.ui.dialogs.StatusInfo;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.ui.DebugUITools;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;
import org.eclipse.jdt.launching.IRuntimeClasspathEntry;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import com.jezhumble.javasysmon.JavaSysMon;

import es.bsc.servicess.ide.Logger;
import es.bsc.servicess.ide.PackagingUtils;
import es.bsc.servicess.ide.ProjectFile;
import es.bsc.servicess.ide.ProjectMetadata;
import es.bsc.servicess.ide.ResourcesFile;
import es.bsc.servicess.ide.editors.BuildingDeploymentFormPage;
import es.bsc.servicess.ide.editors.CommonFormPage;
import es.bsc.servicess.ide.editors.Deployer;
import es.bsc.servicess.ide.editors.ServiceFormEditor;
import es.bsc.servicess.ide.model.Dependency;
import es.bsc.servicess.ide.model.ServiceElement;
import es.bsc.servicess.ide.views.DeploymentChecker;
import es.bsc.servicess.ide.views.ServiceManagerView;

public class LocalhostDeployer extends Deployer {

	private Text serverText;
	private Text coreFolderText;
	private boolean created;
	private String coresLocation;
	private String tomcatLocation;
	private Label coreElementsLabel;
	private Label serverLabel;
	private Button serverButton;
	private Button coreFolderButton;
	private static Logger log = Logger.getLogger(LicenseTokensTableComposite.class);

	public LocalhostDeployer(ServiceFormEditor editor, IWorkbenchWindow window,
			BuildingDeploymentFormPage page) {
		super(editor, window, page);
	}

	@Override
	public void deploy() {
		ProgressMonitorDialog dialog = new ProgressMonitorDialog(getShell());
		try {
			dialog.run(false, false, new IRunnableWithProgress() {

				@Override
				public void run(IProgressMonitor monitor)
						throws InvocationTargetException, InterruptedException {
					try {
						executeDeployment(monitor);
					} catch (Exception e) {
						throw (new InvocationTargetException(e));
					}
				}
			});
		} catch (InterruptedException e) {
			ErrorDialog.openError(super.getShell(), "Error",
					"Deploying the service",
					new StatusInfo(IStatus.ERROR, e.getMessage()));
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			ErrorDialog.openError(super.getShell(), "Error",
					"Deploying the service",
					new StatusInfo(IStatus.ERROR, e.getMessage()));
			e.printStackTrace();
		}
	}

	public void executeDeployment(IProgressMonitor monitor) throws Exception {
		monitor.beginTask("Creating configuration files", 2);
		addLocalhostToProjectFile(getProject(), getCoreElementsFolder());
		monitor.worked(1);
		addLocalhostToResourcesFile(getProject(), getCoreElementsFolder());
		monitor.worked(1);
		monitor.done();
		ProjectMetadata pr_meta = new ProjectMetadata(super.getEditor()
				.getMetadataFile().getRawLocation().toFile());
		pr_meta.removeAllImages();
		HashMap<String, ServiceElement> allEls = CommonFormPage.getElements(
				pr_meta.getAllOrchestrationClasses(), ProjectMetadata.BOTH_TYPE, 
				super.getProject(), pr_meta);
		String[] allPacks = pr_meta.getPackages();
		String[] oePacks = pr_meta.getPackagesWithOrchestration();
		
		if (oePacks != null && oePacks.length > 0) {
			for (String p : oePacks) {
				deployOrchestration(getProject(), getServerLocation(),
						getCoreElementsFolder(), p, monitor);
			}
		}else{
			String projectName = editor.getProject().getProject().getName();
			deployOrchestration(getProject(), getServerLocation(),
					getCoreElementsFolder(), projectName, monitor);
		}
		deployCoreElements(getProject(), getCoreElementsFolder(), monitor);
		startServer(getProject(), getServerLocation());
		ServiceManagerView smview = (ServiceManagerView) PlatformUI
				.getWorkbench().getActiveWorkbenchWindow().getActivePage()
				.showView("es.bsc.servicess.ide.views.ServiceManagerView");
		smview.addNewDeployedService("localservice",
				new LocalhostDeploymentChecker(), DeploymentChecker.PENDING);
	}

	@Override
	public void diposeComposite() {
		/*
		 * serverLabel.dispose(); serverText.dispose(); serverButton.dispose();
		 * coreElementsLabel.dispose(); coreFolderText.dispose();
		 * coreFolderButton.dispose();
		 */
		// composite.dispose();
		// composite.layout(true);
	}

	public void initiate() {

	}

	@Override
	public Composite createComposite(FormToolkit toolkit,
			Composite deploymentOptions, Composite old_composite) {
		System.out.println("Creating Composite");
		if (old_composite != null) {
			old_composite.dispose();
		}
		old_composite = toolkit.createComposite(deploymentOptions, SWT.NONE);
		old_composite.setLayout(new GridLayout(3, false));
		old_composite.setLayoutData(new GridData());
		serverLabel = toolkit.createLabel(old_composite, "Server Folder",
				SWT.NONE);
		serverText = toolkit.createText(old_composite, "", SWT.SINGLE
				| SWT.BORDER);
		GridData rd = new GridData(GridData.VERTICAL_ALIGN_BEGINNING);
		rd.grabExcessHorizontalSpace = true;
		rd.minimumWidth = 350;
		serverText.setLayoutData(rd);
		serverButton = toolkit.createButton(old_composite, "Select...",
				SWT.NORMAL);
		serverButton.setLayoutData(new GridData(
				GridData.HORIZONTAL_ALIGN_BEGINNING));
		serverButton.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {
				selectServerLocation();
			}

			@Override
			public void widgetSelected(SelectionEvent arg0) {
				selectServerLocation();
			}
		});
		coreElementsLabel = toolkit.createLabel(old_composite,
				"Core Elements Folder", SWT.NONE);
		coreFolderText = toolkit.createText(old_composite, "", SWT.SINGLE
				| SWT.BORDER);
		rd = new GridData();
		rd.grabExcessHorizontalSpace = true;
		rd.minimumWidth = 350;
		coreFolderText.setLayoutData(rd);
		coreFolderButton = toolkit.createButton(old_composite, "Select...",
				SWT.NORMAL);
		coreFolderButton.setLayoutData(new GridData(
				GridData.HORIZONTAL_ALIGN_BEGINNING));
		coreFolderButton.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {
				selectCoreFolder();
			}

			@Override
			public void widgetSelected(SelectionEvent arg0) {
				selectCoreFolder();
			}
		});
		created = true;
		composite = old_composite;
		return old_composite;
	}

	protected void selectCoreFolder() {
		final DirectoryDialog dialog = new DirectoryDialog(composite.getShell());
		dialog.setMessage("Select Core Element Folder");
		String directoryName = coreFolderText.getText().trim();
		if (directoryName.length() > 0) {
			final File path = new File(directoryName);
			if (path.exists())
				dialog.setFilterPath(directoryName);
		}
		final String selectedDirectory = dialog.open();
		if (selectedDirectory != null) {
			coreFolderText.setText(selectedDirectory);
			coresLocation = selectedDirectory + File.separator;
		}
	}

	protected void selectServerLocation() {
		final DirectoryDialog dialog = new DirectoryDialog(composite.getShell());
		dialog.setMessage("Select Server Installation Folder");
		String directoryName = serverText.getText().trim();
		if (directoryName.length() > 0) {
			final File path = new File(directoryName);
			if (path.exists())
				dialog.setFilterPath(directoryName);
		}
		final String selectedDirectory = dialog.open();
		if (selectedDirectory != null) {
			serverText.setText(selectedDirectory);
			tomcatLocation = selectedDirectory;
		}
	}

	public String getServerLocation() {
		return tomcatLocation;
	}

	public String getCoreElementsFolder() {
		return coresLocation;
	}

	private void addLocalhostToProjectFile(IJavaProject pr, String coreLocation)
			throws ConfigurationException, SAXException, IOException,
			CoreException, ParserConfigurationException,
			TransformerFactoryConfigurationError, TransformerException {

		ProjectFile res = new ProjectFile(new File(
				PackagingUtils.getSourceProjectFileLocation(super.getEditor())));
		res.addLocalhostWorker(coreLocation);
		File file = new File(
				PackagingUtils.getPackagesProjectFileLocation(super.getEditor()));
		if (file.exists()) {
			file.delete();
		}
		res.toFile(file);

	}

	private void addLocalhostToResourcesFile(IJavaProject pr,
			String coreLocation) throws ParserConfigurationException,
			SAXException, IOException, CoreException,
			TransformerFactoryConfigurationError, TransformerException {
		ResourcesFile res = new ResourcesFile(
				new File(PackagingUtils.getSourceResourcesFileLocation(super
						.getEditor())));
		res.addLocalhost(coreLocation);
		File file = new File(
				PackagingUtils.getPackagesResourcesFileLocation(super
						.getEditor()));
		if (file.exists()) {
			file.delete();
		}
		res.toFile(file);

	}

	private void createPropertiesForLocalhost(File file, IJavaProject project)
			throws CoreException, ConfigurationException {
		RuntimeConfigManager config = new RuntimeConfigManager(file);
		config.setLog4jConfiguration(getRuntimeLocation(project)
				+ "/../log/it-log4j");
		config.setGraph(true);
		config.setTracing(false);
		config.setMonitorInterval(5);
		config.setGATBrokerAdaptor("sshtrilead");
		config.setGATFileAdaptor("sshtrilead");
		config.setProjectFile(PackagingUtils
				.getPackagesProjectFileLocation(super.getEditor()));
		config.setProjectSchema(getRuntimeLocation(project)
				+ "/../xml/projects/project_schema.xsd");
		config.setResourcesFile(PackagingUtils
				.getPackagesResourcesFileLocation(super.getEditor()));
		config.setResourcesSchema(getRuntimeLocation(project)
				+ "/../xml/resources/resource_schema.xsd");
		config.save();
	}

	private void deployOrchestration(IJavaProject project,
			String serverLocation, String ceLocation, String packName, IProgressMonitor monitor)
			throws Exception {
		File ceDir = new File(serverLocation + File.separator + "webapps");
		if (ceDir.isDirectory()) {	
			IFolder outFolder = project.getProject()
				.getFolder(ProjectMetadata.OUTPUT_FOLDER)
				.getFolder(ProjectMetadata.PACKAGES_FOLDER);
		
			IFile properties = outFolder.getFile("it.properties");
			if (properties != null && properties.exists()) {
				properties.delete(true, monitor);
			}
			properties.create(new ByteArrayInputStream(new String("").getBytes()),
				true, monitor);
			createPropertiesForLocalhost(properties.getLocation().toFile(), project);
			// properties.refreshLocal(0, monitor);
		
			IFile war = outFolder.getFile(packName + ".war");
			if (war.exists()){
				PackagingUtils.addRuntimeConfigTojar(war, properties.getLocation()
					.toFile(), outFolder, PackagingUtils.WAR_CLASSES_PATH, monitor);
			
				// outFolder.refreshLocal(1, monitor);
				monitor.beginTask("deploying dependencies", 2);
				File srcFile = war.getLocation().toFile();
				File f = new File(ceDir.getAbsolutePath() + File.separator
					+ packName + ".war");
				File dir = new File(ceDir.getAbsolutePath() + File.separator
					+ packName);
				if (f.exists()) {
					f.delete();
				}
				if (dir.exists()) {
					PackagingUtils.deleteDirectory(dir);
				}
				FileUtils.copyFileToDirectory(srcFile, ceDir);
			}
			IFile deps = outFolder.getFile(project.getProject().getName()
					+ "_deps.zip");
			if (deps != null && deps.exists())
				PackagingUtils.extractZip(deps.getLocation().toFile(),
						ceLocation, outFolder, monitor);
			monitor.worked(1);
			ProjectMetadata pr_meta = new ProjectMetadata(getEditor()
					.getMetadataFile().getRawLocation().toFile());
			deployZipDeps(pr_meta.getDependencies(pr_meta
					.getElementsInPackage(packName)),
					ceLocation, outFolder, monitor);
			deployWarDeps(pr_meta.getDependencies(pr_meta
					.getElementsInPackage(packName)),packName, outFolder, ceDir, monitor);
			properties.delete(true, monitor);
			monitor.worked(1);
			monitor.done();
		} else
			throw (new Exception("Folder "+serverLocation + File.separator + "webapps  not found"));

	}

	private void deployWarDeps(List<Dependency> deps, String packageName, IFolder packageFolder, File ceDir, IProgressMonitor monitor) throws Exception {
		// TODO Auto-generated method stub
		for (Dependency d : deps) {
			log.debug("Analizing dependency "+ d.getLocation()+ " (Type: " +d.getType()+")");
			if (d.getType().equalsIgnoreCase(ProjectMetadata.WAR_DEP_TYPE)) {
				if(d.isImported()){
					IFile properties = packageFolder.getFile("it.properties");
					IFile war = packageFolder.getFolder(ProjectMetadata.EXTERNAL_PACKS_FOLDER).
							getFolder(packageName).getFile(
									PackagingUtils.getPackageNameWithExtension(d.getLocation()));
					PackagingUtils.addRuntimeConfigTojar(war, properties.getLocation()
							.toFile(), packageFolder, PackagingUtils.WAR_CLASSES_PATH, monitor);
					File srcFile = war.getLocation().toFile();
					File f = new File(ceDir.getAbsolutePath() + File.separator
						+ packageName + ".war");
					File dir = new File(ceDir.getAbsolutePath() + File.separator
						+ packageName);
					if (f.exists()) {
						f.delete();
					}
					if (dir.exists()) {
						PackagingUtils.deleteDirectory(dir);
					}
					FileUtils.copyFileToDirectory(srcFile, ceDir);
				}else
					FileUtils.copyFileToDirectory(new File(d.getLocation()), ceDir);
					
			}
		}
	}

	private void startServer(IJavaProject project, String serverLocation)
			throws CoreException, ConfigurationException {
		ILaunchManager manager = DebugPlugin.getDefault().getLaunchManager();
		ILaunchConfigurationType type = manager
				.getLaunchConfigurationType(IJavaLaunchConfigurationConstants.ID_JAVA_APPLICATION);
		ILaunchConfiguration[] configurations = manager
				.getLaunchConfigurations(type);
		for (int i = 0; i < configurations.length; i++) {
			ILaunchConfiguration configuration = configurations[i];
			if (configuration.getName().equals("Start Tomcat")) {
				configuration.delete();
				break;
			}
		}
		ILaunchConfigurationWorkingCopy workingCopy = type.newInstance(null,
				"Start Tomcat");
		workingCopy.setAttribute(
				IJavaLaunchConfigurationConstants.ATTR_MAIN_TYPE_NAME,
				"org.apache.catalina.startup.Bootstrap");
		workingCopy.setAttribute(
				IJavaLaunchConfigurationConstants.ATTR_PROGRAM_ARGUMENTS,
				"start");

		File jdkHome = JavaRuntime.getVMInstall(project).getInstallLocation();
		IPath toolsPath = new Path(jdkHome.getAbsolutePath()).append("lib")
				.append("tools.jar");
		IRuntimeClasspathEntry toolsEntry = JavaRuntime
				.newArchiveRuntimeClasspathEntry(toolsPath);
		toolsEntry.setClasspathProperty(IRuntimeClasspathEntry.USER_CLASSES);
		IPath bootstrapPath = new Path(serverLocation).append("bin").append(
				"bootstrap.jar");
		IRuntimeClasspathEntry bootstrapEntry = JavaRuntime
				.newArchiveRuntimeClasspathEntry(bootstrapPath);
		bootstrapEntry
				.setClasspathProperty(IRuntimeClasspathEntry.USER_CLASSES);
		IPath systemLibsPath = new Path(JavaRuntime.JRE_CONTAINER);
		IRuntimeClasspathEntry systemLibsEntry = JavaRuntime
				.newRuntimeContainerClasspathEntry(systemLibsPath,
						IRuntimeClasspathEntry.STANDARD_CLASSES);
		List<String> classpath = new ArrayList<String>();
		System.out.println(" class path: " + toolsEntry.getMemento());
		classpath.add(toolsEntry.getMemento());
		System.out.println(" class path: " + bootstrapEntry.getMemento());
		classpath.add(bootstrapEntry.getMemento());
		System.out.println(" class path: " + systemLibsEntry.getMemento());
		classpath.add(systemLibsEntry.getMemento());

		String javaOpts = "-Djava.endorsed.dirs=\"" + serverLocation
				+ "/common/endorsed\" " + "-Dcatalina.base=\"" + serverLocation
				+ "\" " + "-Dcatalina.home=\"" + serverLocation + "\" "
				+ "-Djava.io.tmpdir=\"" + serverLocation + "/temp\" ";
		// + "-Dit.lib=\""+getRuntimeLocation(project)+"/lib\" " TODO remove the
		// missing part
		/*
		 * + "-Dlog4j.configuration=\""+
		 * getRuntimeLocation(project)+"/../log/it-log4j\" " +
		 * "-Dit.project.file=\"" +
		 * PackagingUtils.getPackagesProjectFileLocation(super.getEditor())+
		 * "\" " + "-Dit.resources.file=\"" +
		 * PackagingUtils.getPackagesResourcesFileLocation(super.getEditor())+
		 * "\" " +
		 * "-Dit.to.file=\"false\" -Dit.graph=\"false\" -Dit.tracing=\"false\" -Dit.gat.broker.adaptor=\"sshtrilead\" -Dit.gat.file.adaptor=\"sshtrilead\""
		 * ; System.out.println("JAVA_OPTS: " + javaOpts);
		 */
		workingCopy.setAttribute(
				IJavaLaunchConfigurationConstants.ATTR_CLASSPATH, classpath);
		workingCopy
				.setAttribute(
						IJavaLaunchConfigurationConstants.ATTR_DEFAULT_CLASSPATH,
						false);
		workingCopy.setAttribute(
				IJavaLaunchConfigurationConstants.ATTR_VM_ARGUMENTS, javaOpts);
		File workingDir = new File(serverLocation + "/bin");
		workingCopy.setAttribute(
				IJavaLaunchConfigurationConstants.ATTR_WORKING_DIRECTORY,
				workingDir.getAbsolutePath());
		ILaunchConfiguration configuration = workingCopy.doSave();
		DebugUITools.launch(configuration, ILaunchManager.RUN_MODE);
	}

	private void deployCoreElements(IJavaProject project,
			String coreElementsFolder, IProgressMonitor monitor)
			throws IOException, CoreException, InterruptedException,
			SAXException, ParserConfigurationException {
		IFolder outFolder = project.getProject()
				.getFolder(ProjectMetadata.OUTPUT_FOLDER)
				.getFolder(ProjectMetadata.PACKAGES_FOLDER);
		ProjectMetadata pr_meta = new ProjectMetadata(super.getEditor()
				.getMetadataFile().getRawLocation().toFile());
		String[] packs = pr_meta.getPackagesWithCores();
		if (packs != null && packs.length > 0) {
			for (String p : packs) {
				// create image
				String type = pr_meta.getPackageType(p);
				if (type.equalsIgnoreCase(ProjectMetadata.CORE_TYPE)
						|| type.equalsIgnoreCase(ProjectMetadata.BOTH_TYPE)) {
					IFile jar = outFolder.getFile(p + ".jar");
					File srcFile = jar.getLocation().toFile();
					PackagingUtils.extractZip(srcFile, coreElementsFolder,
							outFolder, monitor);
					IFile deps = outFolder.getFile(p + "_deps.zip");
					//TODO update if dependencies different than zip (war, jars...)
					if (deps != null && deps.exists())
						PackagingUtils.extractZip(deps.getLocation().toFile(),
								coreElementsFolder, outFolder, monitor);
					deployZipDeps(pr_meta.getDependencies(pr_meta
							.getElementsInPackage(p)), coreElementsFolder,
							outFolder, monitor);
				}
			}
		}
	}

	private void deployZipDeps(List<Dependency> deps, String coreElementsFolder,
			IFolder folder, IProgressMonitor monitor) throws IOException,
			CoreException, InterruptedException {
		for (Dependency d : deps) {
			if (d.getType().equalsIgnoreCase(ProjectMetadata.ZIP_DEP_TYPE)) {
				PackagingUtils.extractZip(new File(d.getLocation()),
						coreElementsFolder, folder, monitor);
			}
		}
	}

	private String getRuntimeLocation(IJavaProject project)
			throws ConfigurationException, CoreException {
		IFile metadataFile = project.getProject()
				.getFolder(ProjectMetadata.METADATA_FOLDER)
				.getFile(ProjectMetadata.METADATA_FILENAME);
		if (metadataFile != null && metadataFile.exists()) {
			try {
				ProjectMetadata pr_meta = new ProjectMetadata(new File(
						metadataFile.getRawLocation().toOSString()));
				return pr_meta.getRuntimeLocation();
			} catch (Exception e) {
				e.printStackTrace();
				throw (new CoreException(new StatusInfo(StatusInfo.ERROR,
						e.getMessage())));
			}
		} else {
			throw (new CoreException(new StatusInfo(StatusInfo.ERROR,
					"metadata info not found")));
		}
	}

}
