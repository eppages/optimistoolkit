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
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.apache.commons.configuration.ConfigurationException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.debug.internal.ui.actions.StatusInfo;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

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

/**Grid Deployer is a class which extends the Deployer class to deploy the service in a
 * user defined GRID.
 * 
 * @author Jorge Ejarque (Barcelona Supercomputing Center)
 * 
 */
@SuppressWarnings("restriction")
public class GridDeployer extends Deployer {
	
	private Section grSection;
	private Section loginSection;
	private Combo resource;
	private Button reset;
	private Button save;
	private Combo type;
	private Text resourceAddress;
	private Text installationPath;
	private Text serverPath;
	private Text username;
	private File resFile;
	private static final String RESOURCES_FILENAME = "gr_props.xml";
	private static final int DEFAULT_TASK_NUM = 2;
	private static final String DEFAULT_ACRH = "x86";
	private static final float DEFAULT_SPEED = 1.2f;
	private static final int DEFAULT_PROC_COUNT = 2;
	private static final String DEFAULT_OS = "Linux";
	private static final float DEFAULT_DISK = 10;
	private static final float DEFAULT_MEM = 1;
	private static final int GRID_COMPOSITE_COL_NUM = 4;
	private static final int TEXT_FIELD_SIZE = 350;
	private static final long MONITOR_INTERVAL_DEFAULT = 5000;
	private GridResourcesFile resGRFile;
	private Text workPath;
	private Button newBtn;
	private Button delBtn;

	/**
	 * Grid Deployer constructor.
	 * 
	 * @param editor
	 *            Eclipse editor where the Grid Deployer is going to be located
	 * @param window
	 *            Workbench window where the Grid Deployer is going to be
	 *            located
	 * @param page
	 *            FormPage where the GridDeployer is going to be located
	 */
	public GridDeployer(final ServiceFormEditor editor,
			final IWorkbenchWindow window, final BuildingDeploymentFormPage page) {
		super(editor, window, page);
		resFile = editor.getProject().getProject()
				.getFolder(ProjectMetadata.METADATA_FOLDER)
				.getFile(RESOURCES_FILENAME).getRawLocation().toFile();
	}

	
	
	/* (non-Javadoc)
	 * @see es.bsc.servicess.ide.editors.Deployer#deploy()
	 * 
	 * Get master and worker elements from a grid resource file, creates a monitoring 
	 * bar and executes the deployment process passing the monitor bar 
	 */
	@Override
	public void deploy() {
		final Element master = resGRFile.getMasterResource();
		if (master != null) {
			final Element[] workers = resGRFile.getWorkerResources();
			if (workers != null && workers.length > 0) {
				ProgressMonitorDialog dialog = new ProgressMonitorDialog(
						getShell());
				try {
					dialog.run(false, false, new IRunnableWithProgress() {

						@Override
						public void run(final IProgressMonitor monitor)
								throws InvocationTargetException,
								InterruptedException {
							try {
								executeDeployment(master, workers, monitor);
							} catch (Exception e) {
								throw (new InvocationTargetException(e));
							}
						}
					});
				} catch (InterruptedException e) {
					String message = e.getMessage();
					System.err.println("Error message: " + message);
					ErrorDialog.openError(super.getShell(), "Error",
							"Deploying the service", new StatusInfo(
									IStatus.ERROR, message));
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					String message = e.getMessage();
					System.err.println("Error message: " + message);
					ErrorDialog.openError(super.getShell(), "Error",
							"Deploying the service", new StatusInfo(
									IStatus.ERROR, message));
					e.printStackTrace();
				}
			} else {
				ErrorDialog.openError(super.getShell(), "Error",
						"Deploying the service", new StatusInfo(IStatus.ERROR,
								"Worker resources not defined"));
			}
		} else {
			ErrorDialog.openError(super.getShell(), "Error",
					"Deploying the service", new StatusInfo(IStatus.ERROR,
							"Master resource not defined"));
		}
	}

	/**
	 * @param master
	 * @param workers
	 * @param monitor
	 * @throws ConfigurationException
	 * @throws InvocationTargetException
	 * @throws InterruptedException
	 * @throws CoreException
	 * @throws IOException
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws TransformerException
	 */
	private void executeDeployment(final Element master,
			final Element[] workers, final IProgressMonitor monitor)
			throws ConfigurationException, InvocationTargetException,
			InterruptedException, CoreException, IOException,
			ParserConfigurationException, SAXException, TransformerException {
		ProjectMetadata prMetadata = new ProjectMetadata(super.getEditor()
				.getMetadataFile().getRawLocation().toFile());
		generateConfigurationFiles(master, workers, monitor);

		deployWorkers(workers, prMetadata, monitor);
		deployMaster(master, prMetadata, monitor);

	}

	private void deployWorkers(final Element[] workers,
			final ProjectMetadata prMeta, final IProgressMonitor monitor)
			throws CoreException, InterruptedException, IOException {
		IFolder folder = super.getProject().getProject()
				.getFolder(ProjectMetadata.OUTPUT_FOLDER)
				.getFolder(ProjectMetadata.PACKAGES_FOLDER);
		String packageFolder = folder.getRawLocation().toOSString();
		String[] jars = getWorkerJars(packageFolder, prMeta);
		String[] zips = getWorkerZips(packageFolder, prMeta);
		PackagingUtils.deployWorkers(workers, jars, zips, folder, monitor);

	}

	private String[] getWorkerJars(final String packageFolder,
			final ProjectMetadata prMetadata) {
		String[] packs = prMetadata.getPackagesWithCores();
		for (int i = 0; i < packs.length; i++) {
			packs[i] = packageFolder + File.separator + packs[i] + ".jar";
		}
		return packs;
	}

	private String[] getWorkerZips(final String packageFolder,
			final ProjectMetadata prMetadata) {
		ArrayList<String> list = new ArrayList<String>();
		String[] packs = prMetadata.getPackagesWithCores();
		for (int i = 0; i < packs.length; i++) {
			list.add(packageFolder + File.separator + packs[i] + "_deps.zip");
			for (Dependency d : prMetadata.getDependencies(prMetadata
					.getElementsInPackage(packs[i]))) {
				if (d.getType().equalsIgnoreCase(ProjectMetadata.ZIP_DEP_TYPE)) {
					if (!list.contains(d.getLocation())) {
						list.add(d.getLocation());
					}
				}
			}
		}
		return list.toArray(new String[list.size()]);
	}

	private void deployMaster(final Element master,
			final ProjectMetadata prMetadata, final IProgressMonitor monitor)
			throws CoreException, InterruptedException, IOException {
		IFolder folder = super.getProject().getProject()
				.getFolder(ProjectMetadata.OUTPUT_FOLDER)
				.getFolder(ProjectMetadata.PACKAGES_FOLDER);
		String packageFolder = folder.getRawLocation().toOSString();
		String[] files = new String[] {
				packageFolder + File.separator + "project.xml",
				packageFolder + File.separator + "resources.xml",
				prMetadata.getRuntimeLocation()
						+ "/../xml/projects/project_schema.xsd",
				prMetadata.getRuntimeLocation() + "/../log/it-log4j",
				prMetadata.getRuntimeLocation()
						+ "/../xml/resources/resource_schema.xsd" };
		String servicename = editor.getProject().getProject().getName();
		HashMap<String, ServiceElement> oEls = CommonFormPage.getElements(
				prMetadata.getAllOrchestrationClasses(), ProjectMetadata.ORCH_TYPE, super.getProject(), prMetadata);
		String[] els = oEls.keySet().toArray(new String[oEls.keySet().size()]);
		String[] wars = getMasterWars(packageFolder, servicename,
				prMetadata.getDependencies(els));
		String[] zips = getMasterZips(packageFolder, servicename,
				prMetadata.getDependencies(els));
		PackagingUtils.deployMaster(master, servicename, files, wars, zips,
				folder, monitor);

	}

	private String[] getMasterWars(final String packageFolder,
			final String servicename, final List<Dependency> deps) {
		ArrayList<String> list = new ArrayList<String>();
		list.add(packageFolder + File.separator + servicename + ".war");
		for (Dependency d : deps) {

			if (d.getType().equalsIgnoreCase(ProjectMetadata.WAR_DEP_TYPE)) {
				System.out.println("adding war " + d.getLocation());
				list.add(d.getLocation());
			}
		}
		return list.toArray(new String[list.size()]);
	}

	private String[] getMasterZips(final String packageFolder, final String serviceName,
			final List<Dependency> deps) {
		ArrayList<String> list = new ArrayList<String>();
		list.add(packageFolder + File.separator + serviceName + "_deps.zip");
		for (Dependency d : deps) {
			if (d.getType().equalsIgnoreCase(ProjectMetadata.ZIP_DEP_TYPE)) {
				list.add(d.getLocation());
			}
		}
		return list.toArray(new String[list.size()]);
	}

	private void generateConfigurationFiles(final Element master, final Element[] workers,
			final IProgressMonitor monitor) throws InvocationTargetException,
			InterruptedException, CoreException, ConfigurationException,
			IOException, ParserConfigurationException, SAXException,
			TransformerException {
		//TODO different orch and cores.
		IFolder outFolder = editor.getProject().getProject()
				.getFolder(ProjectMetadata.OUTPUT_FOLDER)
				.getFolder(ProjectMetadata.PACKAGES_FOLDER);
		IFile war = outFolder.getFile(editor.getProject().getProject()
				.getName()
				+ ".war");
		IFile properties = outFolder.getFile("it.properties");
		if (properties != null && properties.exists()) {
			properties.delete(true, monitor);
		}
		properties.create(new ByteArrayInputStream(new String("").getBytes()),
				true, monitor);
		createProperties(properties.getLocation().toFile(), master);
		PackagingUtils.addRuntimeConfigTojar(war, properties.getLocation()
				.toFile(), outFolder,PackagingUtils.WAR_CLASSES_PATH, monitor);
		properties.delete(true, monitor);
		monitor.beginTask("Updating config file for the selected Grid", 2);
		addResourcesToProject(workers);
		monitor.worked(1);
		addResourcesToResources(workers);
		monitor.done();
		outFolder.refreshLocal(1, monitor);

	}

	private void addResourcesToResources(final Element[] workers)
			throws ParserConfigurationException, SAXException, IOException,
			CoreException, TransformerException {
		ResourcesFile res = new ResourcesFile(
				new File(PackagingUtils.getSourceResourcesFileLocation(super
						.getEditor())));
		for (Element wk : workers) {
			res.addResource(wk.getAttribute(GridResourcesFile.HOSTNAME_ATTR),
					DEFAULT_ACRH, DEFAULT_SPEED, DEFAULT_PROC_COUNT,
					DEFAULT_OS, DEFAULT_DISK, DEFAULT_MEM, null);
		}
		File file = new File(
				PackagingUtils.getPackagesResourcesFileLocation(super
						.getEditor()));
		if (file.exists()) {
			file.delete();
		}
		res.toFile(file);

	}

	private void addResourcesToProject(final Element[] workers)
			throws ParserConfigurationException, SAXException, IOException,
			CoreException, TransformerException {
		File f = new File(PackagingUtils.getSourceProjectFileLocation(super.getEditor()));
		ProjectFile res = new ProjectFile(f);
		if (res== null)
			res = new ProjectFile();
		System.out.println("num workers" + workers.length);
		for (Element wk : workers) {
			System.out.println("Hostname:" + wk.getAttribute(GridResourcesFile.HOSTNAME_ATTR));
			System.out.println(" Install Path: " +wk.getAttribute(GridResourcesFile.INSTALL_PATH_ATTR));
			System.out.println(" Working Path: " +wk.getAttribute(GridResourcesFile.WORKING_PATH_ATTR));
			System.out.println(" UserName: " +wk.getAttribute(GridResourcesFile.USERNAME_ATTR));
			res.addWorker(wk.getAttribute(GridResourcesFile.HOSTNAME_ATTR),
					wk.getAttribute(GridResourcesFile.INSTALL_PATH_ATTR),
					wk.getAttribute(GridResourcesFile.WORKING_PATH_ATTR),
					wk.getAttribute(GridResourcesFile.USERNAME_ATTR),
					DEFAULT_TASK_NUM);
		}

		File file = new File(
				PackagingUtils.getPackagesProjectFileLocation(super.getEditor()));
		if (file.exists()) {
			file.delete();
		}
		res.toFile(file);

	}

	private void createProperties(final File file, final Element master)
			throws ConfigurationException {
		RuntimeConfigManager config = new RuntimeConfigManager(file);
		config.setLog4jConfiguration(master
				.getAttribute(GridResourcesFile.INSTALL_PATH_ATTR)
				+ "/it-log4j");
		config.setGraph(true);
		config.setTracing(false);
		config.setMonitorInterval(MONITOR_INTERVAL_DEFAULT);
		config.setGATBrokerAdaptor("sshtrilead");
		config.setGATFileAdaptor("sshtrilead");
		config.setProjectFile(master
				.getAttribute(GridResourcesFile.INSTALL_PATH_ATTR)
				+ "/project.xml");
		config.setProjectSchema(master
				.getAttribute(GridResourcesFile.INSTALL_PATH_ATTR)
				+ "/project_schema.xsd");
		config.setResourcesFile(master
				.getAttribute(GridResourcesFile.INSTALL_PATH_ATTR)
				+ "/resources.xml");
		config.setResourcesSchema(master
				.getAttribute(GridResourcesFile.INSTALL_PATH_ATTR)
				+ "/resource_schema.xsd");
		config.setGATAdaptor(master
				.getAttribute(GridResourcesFile.INSTALL_PATH_ATTR)
				+ "/lib/adaptors");
		config.save();
	}

	@Override
	public void diposeComposite() {
		// TODO Auto-generated method stub

	}

	@Override
	public final void initiate() {
		resFile = editor.getProject().getProject()
				.getFolder(ProjectMetadata.METADATA_FOLDER)
				.getFile(RESOURCES_FILENAME).getRawLocation().toFile();
		try {
			if (resFile == null || !resFile.exists()) {
				resGRFile = new GridResourcesFile();
			} else {
				resGRFile = new GridResourcesFile(resFile);
			}
			resource.setItems(resGRFile.getResources());
			disable();
		} catch (Exception e) {
			String message = e.getMessage();
			System.err.println("Error message: " + message);
			ErrorDialog.openError(super.getShell(), "Error",
					"Deploying the service", new StatusInfo(IStatus.ERROR,
							message));
			e.printStackTrace();
			disable();

		}
	}

	@Override
	public Composite createComposite(FormToolkit toolkit, Composite options,
		 Composite oldOptions) {
		composite = page.getToolkit().createComposite(options, SWT.NONE);
		GridData rd = new GridData(GridData.FILL_HORIZONTAL
				| GridData.VERTICAL_ALIGN_BEGINNING);
		// rd.grabExcessHorizontalSpace = true;
		composite.setLayoutData(rd);
		composite.setLayout(new GridLayout(1, false));

		createGridResourceSection(composite);

		if (oldOptions != null) {
			oldOptions.dispose();
		}
		oldOptions = composite;
		return composite;
	}

	private void createButtonsComposite(final Composite composite) {
		Composite buttonsComposite = page.getToolkit().createComposite(composite,
				SWT.NONE);
		GridData rd = new GridData(GridData.FILL_HORIZONTAL
				| GridData.VERTICAL_ALIGN_BEGINNING);
		rd.grabExcessHorizontalSpace = true;
		buttonsComposite.setLayoutData(rd);
		buttonsComposite.setLayout(new GridLayout(2, false));
		reset = page.getToolkit().createButton(buttonsComposite, "Reset",
				SWT.NORMAL);
		reset.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetDefaultSelected(final SelectionEvent arg0) {
				cleanResourceDetails(false);
			}

			@Override
			public void widgetSelected(final SelectionEvent arg0) {
				cleanResourceDetails(false);
			}
		});
		save = page.getToolkit().createButton(buttonsComposite, "Save", SWT.NORMAL);
		save.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetDefaultSelected(final SelectionEvent arg0) {
				try {
					saveResourceSection();
				} catch (Exception e) {

					ErrorDialog.openError(getShell(), "Error",
							"Saving Resource", new StatusInfo(IStatus.ERROR,
									"Error writting resource file"));

					e.printStackTrace();
				}
			}

			@Override
			public void widgetSelected(final SelectionEvent arg0) {
				try {
					saveResourceSection();
				} catch (Exception e) {
					ErrorDialog.openError(getShell(), "Error",
							"Saving Resource", new StatusInfo(IStatus.ERROR,
									"Error writting resource file"));
					e.printStackTrace();
				}
			}
		});
	}

	private void saveResourceSection() throws TransformerException, IOException {
		if (type.getText().equals(GridResourcesFile.MASTER)
				|| type.getText().equals(GridResourcesFile.BOTH)) {
			Element master = resGRFile.getMasterResource();
			if (master != null
					&& !resourceAddress
							.getText()
							.trim()
							.equals(master
									.getAttribute(GridResourcesFile.HOSTNAME_ATTR))) {
				ErrorDialog
					.openError(
						getShell(),
						"Error",
						"Saving Resource",
						new StatusInfo(IStatus.ERROR,
						"There is already a master resource. Change the resource type and save again"));
				return;
			}
		}
		resGRFile.updateResource(resourceAddress.getText().trim(), type.getText(),
				username.getText().trim(), installationPath.getText().trim(),
				workPath.getText().trim(), serverPath.getText().trim());
		resGRFile.toFile(resFile);
		resource.setItems(resGRFile.getResources());
		resource.setText(resourceAddress.getText().trim());
		resourceAddress.setEnabled(false);

	}

	private void cleanResourceDetails(final boolean isNew) {
		if (isNew) {
			resourceAddress.setText("");
			type.setText("");
		}
		username.setText("");
		installationPath.setText("");
		serverPath.setText("");
		workPath.setText("");
	}

	private void createGridResourceSection(final Composite composite) {
		grSection = page.getToolkit().createSection(composite,
				Section.TWISTIE | Section.DESCRIPTION | SWT.BORDER);
		grSection.setText("Grid Resources");
		grSection.setDescription("Define the Grid Resources");
		GridData rd = new GridData(GridData.HORIZONTAL_ALIGN_FILL
				| GridData.VERTICAL_ALIGN_BEGINNING);
		rd.grabExcessHorizontalSpace = true;
		grSection.setLayoutData(rd);
		grSection.setLayout(new GridLayout(1, true));
		Composite grCompo = page.getToolkit().createComposite(grSection,
				SWT.BORDER);
		rd = new GridData(GridData.FILL_HORIZONTAL
				| GridData.VERTICAL_ALIGN_BEGINNING);
		rd.grabExcessHorizontalSpace = true;
		grCompo.setLayout(new GridLayout(1, false));
		grCompo.setLayoutData(rd);

		// Resources
		Composite comboComp = page.getToolkit().createComposite(grCompo,
				SWT.BORDER);
		rd = new GridData(GridData.FILL_HORIZONTAL
				| GridData.VERTICAL_ALIGN_BEGINNING);
		rd.grabExcessHorizontalSpace = true;
		comboComp.setLayoutData(rd);
		comboComp.setLayout(new GridLayout(GRID_COMPOSITE_COL_NUM, false));
		page.getToolkit().createLabel(comboComp, "Resources");
		resource = new Combo(comboComp, SWT.NONE);
		rd = new GridData(GridData.FILL_HORIZONTAL);
		rd.grabExcessHorizontalSpace = true;
		rd.minimumWidth = TEXT_FIELD_SIZE;
		resource.setLayoutData(rd);
		resource.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent event) {
				loadResource(resource.getItem(resource.getSelectionIndex()));
			}
		});
		newBtn = page.getToolkit().createButton(comboComp, "New", SWT.NORMAL);
		newBtn.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetDefaultSelected(final SelectionEvent arg0) {
				newResource();
			}

			@Override
			public void widgetSelected(final SelectionEvent arg0) {
				newResource();
			}
		});
		delBtn = page.getToolkit().createButton(comboComp, "Delete",
				SWT.NORMAL);
		delBtn.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetDefaultSelected(final SelectionEvent arg0) {
				try {
					delResource(resource.getItem(resource.getSelectionIndex()));
				} catch (Exception e) {

					ErrorDialog.openError(getShell(), "Error",
							"Deleting Resource", new StatusInfo(IStatus.ERROR,
									"Error writting resource file"));

					e.printStackTrace();
				}
			}

			@Override
			public void widgetSelected(final SelectionEvent arg0) {
				try {
					delResource(resource.getItem(resource.getSelectionIndex()));
				} catch (Exception e) {

					ErrorDialog.openError(getShell(), "Error",
							"Deleting Resource", new StatusInfo(IStatus.ERROR,
									"Error writting resource file"));

					e.printStackTrace();
				}
			}
		});

		// Details part
		createDetailsComposite(grCompo);

		// Buttons part
		createButtonsComposite(grCompo);

		grSection.setClient(grCompo);
		grSection.setExpanded(true);
		// GR_section.setExpanded(false);
	}

	private void createDetailsComposite(final Composite grCompo) {
		Composite detailsComposite = page.getToolkit().createComposite(grCompo,
				SWT.BORDER);
		GridData rd = new GridData(GridData.FILL_HORIZONTAL
				| GridData.VERTICAL_ALIGN_BEGINNING);
		rd.grabExcessHorizontalSpace = true;
		detailsComposite.setLayoutData(rd);
		detailsComposite.setLayout(new GridLayout(2, false));
		page.getToolkit().createLabel(detailsComposite, "Machine Name / Address",
				SWT.NONE);
		resourceAddress = page.getToolkit().createText(detailsComposite, "",
				SWT.SINGLE | SWT.BORDER);
		rd = new GridData(GridData.FILL_HORIZONTAL);
		rd.grabExcessHorizontalSpace = true;
		rd.minimumWidth = TEXT_FIELD_SIZE;
		resourceAddress.setLayoutData(rd);
		page.getToolkit().createLabel(detailsComposite, "Type", SWT.NONE);
		type = new Combo(detailsComposite, SWT.NONE);
		rd = new GridData(GridData.FILL_HORIZONTAL);
		rd.grabExcessHorizontalSpace = true;
		rd.minimumWidth = TEXT_FIELD_SIZE;
		type.setLayoutData(rd);
		type.setItems(GridResourcesFile.TYPES);
		type.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent event) {
				if (type.getItem(type.getSelectionIndex()).equals(
						GridResourcesFile.WORKER)) {
					workPath.setEnabled(true);
					serverPath.setEnabled(false);
				} else if (type.getItem(type.getSelectionIndex()).equals(
						GridResourcesFile.MASTER)) {
					workPath.setEnabled(false);
					serverPath.setEnabled(true);
				} else {
					workPath.setEnabled(true);
					serverPath.setEnabled(true);
				}
			}
		});
		page.getToolkit().createLabel(detailsComposite, "Username", SWT.NONE);
		username = page.getToolkit().createText(detailsComposite, "",
				SWT.SINGLE | SWT.BORDER);
		rd = new GridData(GridData.FILL_HORIZONTAL);
		rd.grabExcessHorizontalSpace = true;
		rd.minimumWidth = TEXT_FIELD_SIZE;
		username.setLayoutData(rd);
		page.getToolkit().createLabel(detailsComposite, "Installation Path",
				SWT.NONE);
		installationPath = page.getToolkit().createText(detailsComposite, "",
				SWT.SINGLE | SWT.BORDER);
		rd = new GridData(GridData.FILL_HORIZONTAL);
		rd.grabExcessHorizontalSpace = true;
		rd.minimumWidth = TEXT_FIELD_SIZE;
		installationPath.setLayoutData(rd);
		page.getToolkit().createLabel(detailsComposite, "Working Path", SWT.NONE);
		workPath = page.getToolkit().createText(detailsComposite, "",
				SWT.SINGLE | SWT.BORDER);
		rd = new GridData(GridData.FILL_HORIZONTAL);
		rd.grabExcessHorizontalSpace = true;
		rd.minimumWidth = TEXT_FIELD_SIZE;
		workPath.setLayoutData(rd);
		page.getToolkit().createLabel(detailsComposite, "Server Path", SWT.NONE);
		serverPath = page.getToolkit().createText(detailsComposite, "",
				SWT.SINGLE | SWT.BORDER);
		rd = new GridData(GridData.FILL_HORIZONTAL);
		rd.grabExcessHorizontalSpace = true;
		rd.minimumWidth = TEXT_FIELD_SIZE;
		serverPath.setLayoutData(rd);
	}

	private void delResource(final String hostname)
			throws TransformerException, IOException {
		resGRFile.removeResource(hostname);
		resGRFile.toFile(resFile);
		resource.setItems(resGRFile.getResources());
		resource.setText("");
		cleanResourceDetails(true);
		disable();

	}

	protected final void newResource() {
		resource.setText("New Resource");
		cleanResourceDetails(true);
		enable();
	}

	protected final void loadResource(final String item) {
		Element res = resGRFile.getResource(item);
		try {
			enable();
			resourceAddress.setText(res.getAttribute(GridResourcesFile.HOSTNAME_ATTR));
			resourceAddress.setEnabled(false);
			installationPath.setText(res
					.getAttribute(GridResourcesFile.INSTALL_PATH_ATTR));
			username.setText(res.getAttribute(GridResourcesFile.USERNAME_ATTR));
			type.setText(res.getAttribute(GridResourcesFile.TYPE_ATTR));
			if (type.getText().equals(GridResourcesFile.WORKER)
					|| type.getText().equals(GridResourcesFile.BOTH)) {
				workPath.setText(res
						.getAttribute(GridResourcesFile.WORKING_PATH_ATTR));
			} else {
				workPath.setText("");
				workPath.setEnabled(false);
			}
			if (type.getText().equals(GridResourcesFile.MASTER)
					|| type.getText().equals(GridResourcesFile.BOTH)) {
				serverPath.setText(res
						.getAttribute(GridResourcesFile.SERVER_PATH_ATTR));
			} else {
				serverPath.setText("");
				serverPath.setEnabled(false);
			}
		} catch (NullPointerException e) {
			ErrorDialog.openError(super.getShell(), "Error",
					"Loading Resource", new StatusInfo(IStatus.ERROR,
							"Resource attributes not found"));
			resource.setText("");
			cleanResourceDetails(true);
			disable();
		}

	}

	private void enable() {
		resourceAddress.setEnabled(true);
		type.setEnabled(true);
		installationPath.setEnabled(true);
		workPath.setEnabled(true);
		serverPath.setEnabled(true);
		username.setEnabled(true);
		save.setEnabled(true);
		reset.setEnabled(true);
	}

	private void disable() {
		resourceAddress.setEnabled(false);
		type.setEnabled(false);
		installationPath.setEnabled(false);
		workPath.setEnabled(false);
		serverPath.setEnabled(false);
		username.setEnabled(false);
		save.setEnabled(false);
		reset.setEnabled(false);
	}

}
