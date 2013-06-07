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

package es.bsc.servicess.ide.actions;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.io.FileUtils;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.ui.DebugUITools;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.internal.ui.JavaPlugin;
import org.eclipse.jdt.internal.ui.dialogs.StatusInfo;
import org.eclipse.jdt.internal.ui.viewsupport.IViewPartInputProvider;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;
import org.eclipse.jdt.launching.IRuntimeClasspathEntry;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.views.contentoutline.ContentOutline;
import org.eclipse.jface.dialogs.MessageDialog;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import es.bsc.servicess.ide.ProjectMetadata;
import es.bsc.servicess.ide.dialogs.DeployServiceDialog;

/**
 * Our sample action implements workbench action delegate. The action proxy will
 * be created by the workbench and shown in the UI. When the user tries to use
 * the action, this delegate will be created and execution will be delegated to
 * it.
 * 
 * @see IWorkbenchWindowActionDelegate
 */
public class DeployAction implements IWorkbenchWindowActionDelegate {

	private IWorkbenchWindow window;
	private IStructuredSelection selection;

	/**
	 * The constructor.
	 */
	public DeployAction() {

	}

	/**
	 * The action has been activated. The argument of the method represents the
	 * 'real' action sitting in the workbench UI.
	 * 
	 * @see IWorkbenchWindowActionDelegate#run
	 */
	public void run(IAction action) {
		DeployServiceDialog dialog = new DeployServiceDialog(window.getShell());
		if (selection != null) {
			IJavaElement el = getInitialJavaElement(selection);
			if (el != null) {
				dialog.setProject(el.getJavaProject());
			}
		}
		if (dialog.open() == Window.OK) {
			try {
				addLocalhostToProjectFile(dialog.getProject(),
						dialog.getCoreElementsFolder());
				deployOrchestrations(dialog.getProject(),
						dialog.getServerLocation());
				deployCoreElements(dialog.getProject(),
						dialog.getCoreElementsFolder());
				startServer(dialog.getProject(), dialog.getServerLocation());
			} catch (IOException e) {
				MessageDialog.openError(window.getShell(), "Error",
						e.getMessage());
				e.printStackTrace();
			} catch (CoreException e) {
				MessageDialog.openError(window.getShell(), "Error",
						e.getMessage());
				e.printStackTrace();
			} catch (ConfigurationException e) {
				MessageDialog.openError(window.getShell(), "Error",
						e.getMessage());
				e.printStackTrace();
			} catch (SAXException e) {
				MessageDialog.openError(window.getShell(), "Error",
						e.getMessage());
				e.printStackTrace();
			} catch (ParserConfigurationException e) {
				MessageDialog.openError(window.getShell(), "Error",
						e.getMessage());
				e.printStackTrace();
			} catch (TransformerFactoryConfigurationError e) {
				MessageDialog.openError(window.getShell(), "Error",
						e.getMessage());
				e.printStackTrace();
			} catch (TransformerException e) {
				MessageDialog.openError(window.getShell(), "Error",
						e.getMessage());
				e.printStackTrace();
			}
		} else {
			MessageDialog.openError(window.getShell(), "Error",
					"Getting the Project");
		}
	}

	private void addLocalhostToProjectFile(IJavaProject pr, String coreLocation)
			throws ConfigurationException, SAXException, IOException,
			CoreException, ParserConfigurationException,
			TransformerFactoryConfigurationError, TransformerException {
		/*
		 * <Worker Name="enric2"> <InstallDir>/IT_worker/</InstallDir>
		 * <WorkingDir>/home/user/</WorkingDir> <User>user</User>
		 * <LimitOfTasks>1</LimitOfTasks> </Worker>
		 */
		DocumentBuilderFactory docFactory = DocumentBuilderFactory
				.newInstance();
		DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
		Document doc = docBuilder.parse(getProjectFileLocation(pr));

		Element project = doc.getDocumentElement();
		Element worker = doc.createElement("Worker");
		worker.setAttribute("Name", "localhost");
		Element installDir = doc.createElement("InstallDir");
		installDir.setNodeValue(coreLocation);
		worker.appendChild(installDir);
		Element workingDir = doc.createElement("WorkingDir");
		workingDir.setNodeValue(coreLocation);
		worker.appendChild(workingDir);
		Element user = doc.createElement("User");
		user.setNodeValue(System.getProperty("user.name"));
		worker.appendChild(user);
		Element limit = doc.createElement("LimitOfTasks");
		limit.setNodeValue("2");
		worker.appendChild(limit);
		project.appendChild(project);
		Source source = new DOMSource(doc);
		IFolder output = pr.getProject().getFolder("output");
		if (output != null && output.exists()) {
			IFolder war_folder = output.getFolder(pr.getProject().getName());
			// Prepare the output file
			File file = (war_folder.getFile("project.xml").getLocation()
					.toFile());
			Result result = new StreamResult(file);

			// Write the DOM document to the file
			Transformer xformer = TransformerFactory.newInstance()
					.newTransformer();
			xformer.transform(source, result);
		}

	}

	private void addLocalhostToResourcesFile(IJavaProject pr,
			String coreLocation) throws ParserConfigurationException,
			ConfigurationException, SAXException, IOException, CoreException,
			TransformerFactoryConfigurationError, TransformerException {
		DocumentBuilderFactory docFactory = DocumentBuilderFactory
				.newInstance();
		DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
		Document doc = docBuilder.parse(getResourcesFileLocation(pr));

		Element resources = doc.getDocumentElement();
		Element worker = doc.createElement("Resource");
		worker.setAttribute("Name", "localhost");
		resources.appendChild(worker);

		Source source = new DOMSource(doc);

		IFolder output = pr.getProject().getFolder("output");

		if (output != null && output.exists()) {
			IFolder war_folder = output.getFolder(pr.getProject().getName());
			// Prepare the output file
			File file = (war_folder.getFile("resources.xml").getLocation()
					.toFile());
			Result result = new StreamResult(file);

			// Write the DOM document to the file
			Transformer xformer = TransformerFactory.newInstance()
					.newTransformer();
			xformer.transform(source, result);
		}

	}

	private void deployOrchestrations(IJavaProject project,
			String serverLocation) throws IOException {
		IFolder outFolder = project.getProject().getFolder("output");
		IFile war = outFolder.getFile(project.getProject().getName() + ".war");
		File srcFile = war.getLocation().toFile();
		File ceDir = new File(serverLocation + "/webapps");
		if (ceDir.isDirectory()) {
			File f = new File(ceDir.getAbsolutePath() + "/"
					+ project.getProject().getName() + ".war");
			File dir = new File(ceDir.getAbsolutePath() + "/"
					+ project.getProject().getName());
			if (f.exists()) {
				f.delete();
			}
			if (dir.exists()) {
				dir.delete();
			}
			FileUtils.copyFileToDirectory(srcFile, ceDir);
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

		String javaOpts = "-Djava.endorsed.dirs=\""
				+ serverLocation
				+ "/common/endorsed\" "
				+ "-Dcatalina.base=\""
				+ serverLocation
				+ "\" "
				+ "-Dcatalina.home=\""
				+ serverLocation
				+ "\" "
				+ "-Djava.io.tmpdir=\""
				+ serverLocation
				+ "/temp\" "
				// + "-Dit.lib=\""+getRuntimeLocation(project)+"/lib\" "
				+ "-Dlog4j.configuration=\""
				+ getRuntimeLocation(project)
				+ "/../log/it-log4j\" "
				+ "-Dit.project.file=\""
				+ getProjectFileLocation(project)
				+ "\" "
				+ "-Dit.resources.file=\""
				+ getResourcesFileLocation(project)
				+ "\" "
				+ "-Dit.to.file=\"false\" -Dit.graph=\"false\" -Dit.tracing=\"false\" -Dit.gat.broker.adaptor=\"sshtrilead\" -Dit.gat.file.adaptor=\"sshtrilead\"";
		System.out.println("JAVA_OPTS: " + javaOpts);
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

	private String getProjectFileLocation(IJavaProject project)
			throws ConfigurationException, CoreException {
		// TODO Change
		return getRuntimeLocation(project) + "/../xml/projects/emotive_0.xml";
	}

	private String getResourcesFileLocation(IJavaProject project)
			throws ConfigurationException, CoreException {
		// TODO Change
		return getRuntimeLocation(project)
				+ "/../xml/resources/resources_0.xml";
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
			/*
			 * PropertiesConfiguration config = new
			 * PropertiesConfiguration(metadataFile
			 * .getRawLocation().toOSString()); if (config != null){ return
			 * config.getString("runtime.location"); }else{ throw (new
			 * CoreException(new
			 * StatusInfo(StatusInfo.ERROR,"metadata info not found"))); }
			 */
		} else {
			throw (new CoreException(new StatusInfo(StatusInfo.ERROR,
					"metadata info not found")));
		}
	}

	private void deployCoreElements(IJavaProject project,
			String coreElementsFolder) throws IOException {
		IFolder outFolder = project.getProject().getFolder("output");
		// TODO: chage for different ce
		IFile war = outFolder.getFile(project.getProject().getName()
				+ "_CoreElements.jar");
		File srcFile = war.getLocation().toFile();
		File ceDir = new File(coreElementsFolder);
		if (ceDir.isDirectory()) {
			FileUtils.copyFileToDirectory(srcFile, ceDir);
		}

	}

	private IJavaElement getInitialJavaElement(IStructuredSelection selection) {
		IJavaElement jelem = null;
		if (selection != null && !selection.isEmpty()) {
			Object selectedElement = selection.getFirstElement();
			if (selectedElement instanceof IAdaptable) {
				IAdaptable adaptable = (IAdaptable) selectedElement;

				jelem = (IJavaElement) adaptable.getAdapter(IJavaElement.class);
				if (jelem == null || !jelem.exists()) {
					jelem = null;
					IResource resource = (IResource) adaptable
							.getAdapter(IResource.class);
					if (resource != null
							&& resource.getType() != IResource.ROOT) {
						while (jelem == null
								&& resource.getType() != IResource.PROJECT) {
							resource = resource.getParent();
							jelem = (IJavaElement) resource
									.getAdapter(IJavaElement.class);
						}
						if (jelem == null) {
							jelem = JavaCore.create(resource); // java project
						}
					}
				}
			}
		}
		if (jelem == null) {
			IWorkbenchPart part = JavaPlugin.getActivePage().getActivePart();
			if (part instanceof ContentOutline) {
				part = JavaPlugin.getActivePage().getActiveEditor();
			}

			if (part instanceof IViewPartInputProvider) {
				Object elem = ((IViewPartInputProvider) part)
						.getViewPartInput();
				if (elem instanceof IJavaElement) {
					jelem = (IJavaElement) elem;
				}
			}
		}
		return jelem;
	}

	/**
	 * Selection in the workbench has been changed. We can change the state of
	 * the 'real' action here if we want, but this can only happen after the
	 * delegate has been created.
	 * 
	 * @see IWorkbenchWindowActionDelegate#selectionChanged
	 */
	public void selectionChanged(IAction action, ISelection selection) {
		if (selection instanceof IStructuredSelection) {
			this.selection = (IStructuredSelection) selection;
			// System.out.println("Selection Changed: Setting Selection");
		}
	}

	/**
	 * We can use this method to dispose of any system resources we previously
	 * allocated.
	 * 
	 * @see IWorkbenchWindowActionDelegate#dispose
	 */
	public void dispose() {
	}

	/**
	 * We will cache window object in order to be able to provide parent shell
	 * for the message dialog.
	 * 
	 * @see IWorkbenchWindowActionDelegate#init
	 */
	public void init(IWorkbenchWindow window) {
		this.window = window;
	}
}