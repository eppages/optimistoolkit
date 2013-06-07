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

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.UUID;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.apache.commons.configuration.ConfigurationException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.FormColors;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Hyperlink;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.ide.IDE;
import org.xml.sax.SAXException;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.core.util.MultivaluedMapImpl;
import com.sun.jersey.multipart.FormDataMultiPart;

import es.bsc.servicess.ide.Activator;
import es.bsc.servicess.ide.ConstraintsUtils;
import es.bsc.servicess.ide.IDEProperties;
import es.bsc.servicess.ide.KeyValueTableComposite;
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
import es.bsc.servicess.ide.model.MethodCoreElement;
import es.bsc.servicess.ide.model.ServiceCoreElement;
import es.bsc.servicess.ide.model.ServiceElement;
import es.bsc.servicess.ide.views.DeploymentChecker;
import es.bsc.servicess.ide.views.ServiceManagerView;
import eu.optimis.manifest.api.ovf.sp.ProductSection;
import eu.optimis.manifest.api.ovf.sp.VirtualHardwareSection;
import eu.optimis.manifest.api.sp.AffinityRule;
import eu.optimis.manifest.api.sp.AntiAffinityRule;
import eu.optimis.manifest.api.sp.Availability;
import eu.optimis.manifest.api.sp.BCR;
import eu.optimis.manifest.api.sp.CostSection;
import eu.optimis.manifest.api.sp.DataProtectionSection;
import eu.optimis.manifest.api.sp.DataStorage;
import eu.optimis.manifest.api.sp.EcoEfficiencySection;
import eu.optimis.manifest.api.sp.EcoMetric;
import eu.optimis.manifest.api.sp.ElasticityRule;
import eu.optimis.manifest.api.sp.LegalItemSection;
import eu.optimis.manifest.api.sp.Manifest;
import eu.optimis.manifest.api.sp.RiskSection;
import eu.optimis.manifest.api.sp.SCC;
import eu.optimis.manifest.api.sp.Scope;
import eu.optimis.manifest.api.sp.TrustSection;
import eu.optimis.manifest.api.sp.VirtualMachineComponent;
import eu.optimis.manifest.api.sp.VirtualMachineComponentConfiguration;

/**
 * Class for visualizing the license tokens
 * @author Jorge Ejarque (Barcelona Supercomputing Center)
 *
 */
class LicenseTokensTableComposite{
	private FormToolkit toolkit;
	private Composite composite;
	private String keyTitle;
	private String valueTitle;
	private Button selectButton;
	private Button generateButton;
	private Button generateAllButton;
	private Table kvTable;
	private Shell shell;
	private OptimisDeployer deployer;
	private static Logger log = Logger.getLogger(LicenseTokensTableComposite.class);
	
	/**
	 * Constructor
	 * @param shell Parent's shell
	 * @param toolkit Editor's toolkit
	 * @param deployer Deployer who contains the license token
	 */
	public LicenseTokensTableComposite(Shell shell, FormToolkit toolkit, OptimisDeployer deployer) {
		this.shell = shell;
		this.toolkit = toolkit;
		this.deployer = deployer;
		this.valueTitle = "Token";
		this.keyTitle = "License";
	}
	
	/** 
	 * Creates the license token composite in the parents composite
	 * @param cp Parent's composite
	 * @return License token composite
	 */
	public Composite createComposite(Composite cp) {
		if (toolkit != null)
			composite = toolkit.createComposite(cp, SWT.BORDER);
		else
			composite = new Composite(cp, SWT.BORDER);
		GridData rd = new GridData(GridData.FILL_HORIZONTAL
				| GridData.VERTICAL_ALIGN_BEGINNING);
		rd.grabExcessHorizontalSpace = true;
		composite.setLayoutData(rd);
		GridLayout firstRow1Layout = new GridLayout();
		firstRow1Layout.numColumns = 1;
		composite.setLayout(firstRow1Layout);
		Composite listsRow;
		if (toolkit != null)
			listsRow = toolkit.createComposite(composite, SWT.NONE);
		else
			listsRow = new Composite(composite, SWT.NONE);
		rd = new GridData(GridData.FILL_HORIZONTAL
				| GridData.VERTICAL_ALIGN_BEGINNING);
		rd.grabExcessHorizontalSpace = true;
		listsRow.setLayoutData(rd);
		firstRow1Layout = new GridLayout();
		firstRow1Layout.numColumns = 2;
		listsRow.setLayout(firstRow1Layout);
		if (toolkit != null)
			kvTable = toolkit.createTable(listsRow, SWT.SINGLE | SWT.BORDER
					| SWT.FULL_SELECTION | SWT.FILL | SWT.V_SCROLL
					| SWT.H_SCROLL);
		else
			kvTable = new Table(listsRow, SWT.SINGLE | SWT.BORDER
					| SWT.FULL_SELECTION | SWT.FILL | SWT.V_SCROLL
					| SWT.H_SCROLL);
		kvTable.setHeaderVisible(true);
		kvTable.setLinesVisible(true);
		rd = new GridData(GridData.FILL_HORIZONTAL
				| GridData.VERTICAL_ALIGN_BEGINNING);
		rd.grabExcessHorizontalSpace = true;
		rd.heightHint=90;
		rd.minimumHeight = 90;
		kvTable.setLayoutData(rd);
		TableColumn keyColumn = new TableColumn(kvTable, SWT.FILL);
		keyColumn.setText(keyTitle);
		keyColumn.setAlignment(SWT.FILL);
		keyColumn.setResizable(true);
		keyColumn.pack();
		TableColumn valueColumn = new TableColumn(kvTable, SWT.FILL);
		valueColumn.setText(valueTitle);
		valueColumn.setAlignment(SWT.FILL);
		keyColumn.setResizable(true);
		valueColumn.pack();
		kvTable.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
					selectButton.setEnabled(true);
					generateButton.setEnabled(true);
			}
		});
		rd = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		rd.grabExcessHorizontalSpace = true;
		rd.grabExcessVerticalSpace = true;
		rd.minimumHeight = 90;
		rd.heightHint=90;
		kvTable.setLayoutData(rd);
		Composite parButtons;
		if (toolkit != null)
				parButtons = toolkit.createComposite(listsRow, SWT.NONE);
		else
				parButtons = new Composite(listsRow, SWT.NONE);
		GridLayout btLayout = new GridLayout();
		btLayout.numColumns = 1;
		btLayout.marginLeft = 0;
		btLayout.marginRight = 0;
		parButtons.setLayout(btLayout);
		if (toolkit != null)
				selectButton = toolkit
						.createButton(parButtons, "Select...", SWT.NONE);
		else {
			selectButton = new Button(parButtons, SWT.NONE);
			selectButton.setText("Select...");
		}
		rd = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		rd.grabExcessHorizontalSpace = true;
		selectButton.setLayoutData(rd);
		selectButton.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent event) {
					selectToken(kvTable.getSelectionIndex());
				}
		});
		if (toolkit != null)
			generateButton = toolkit.createButton(parButtons, "Generate",
					SWT.NONE);
		else {
			generateButton = new Button(parButtons, SWT.NONE);
			generateButton.setText("Generate");
		}
		generateButton.setEnabled(false);
		rd = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		rd.grabExcessHorizontalSpace = true;
		generateButton.setLayoutData(rd);
		generateButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				generateToken(kvTable.getSelectionIndex());
			}
		});
		if (toolkit != null)
			generateAllButton = toolkit.createButton(composite, "Generate All Tokens",
					SWT.NONE);
		else {
			generateAllButton = new Button(parButtons, SWT.NONE);
			generateAllButton.setText("Generate All Tokens");
		}
		rd = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		rd.grabExcessHorizontalSpace = true;
		generateAllButton.setLayoutData(rd);
		generateAllButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				generateAllTokens();
			}
		});
		generateButton.setEnabled(false);
		selectButton.setEnabled(false);
		
		return listsRow;

	}
	
	/** 
	 * Method call when selecting a license
	 * @param selectionIndex selected license
	 */
	protected void selectToken(int selectionIndex) {
			final FileDialog dialog = new FileDialog(shell);
			dialog.setText("Select Token");
			String[] filterExt = { "*.xml" };
			dialog.setFilterExtensions(filterExt);
			String directoryName = "";
			if (directoryName.length() > 0) {
				final File path = new File(directoryName);
				if (path.exists())
					dialog.setFilterPath(directoryName);
			}
			final String selectedFile = dialog.open();
			if (selectedFile != null) {
				File f = new File(selectedFile);
				if (f.exists() && f.isFile()) {
					try {
						String token = new String();
						BufferedReader bufread = new BufferedReader(new FileReader(
								f));

						String line = bufread.readLine();
						while (line != null) {
							token = token + line;
							line = bufread.readLine();
						}
						String name =  LicenseTokenUtils.getName(token);
						if (name!=null && kvTable.getItem(selectionIndex).getText(0)!= null 
								&&name.equalsIgnoreCase(kvTable.getItem(selectionIndex).getText(0).trim())){
								kvTable.getItem(selectionIndex).setText(1,token);
						}else{
							throw(new Exception("Token license identifier "+ name + " is not the specified in the constraints ("+ kvTable.getItem(selectionIndex).getText(0).trim()+")"));
						}
					} catch (Exception e) {
						log.error("Exception selecting token", e);
						ErrorDialog.openError(shell, "Error",
								"Selecting the license token", new Status(
										IStatus.ERROR,Activator.PLUGIN_ID, e.getMessage(), e));
						
					}
				} else {
					ErrorDialog.openError(shell, "Error",
							"Deploying the service", new Status(IStatus.ERROR, Activator.PLUGIN_ID, 
									"File " + selectedFile
											+ " doesn't exists or is not a file"));
				}
			} else {
				ErrorDialog.openError(shell, "Error",
						"Deploying the service", new Status(IStatus.ERROR, Activator.PLUGIN_ID,
								"Error returned value from dialog is null"));
			}	
	}
	
	/**
	 * Invoke the runnable to generate the license token
	 * @param selectionIndex Index of selected license in the license token table
	 */
	protected void generateToken(final int selectionIndex) {
		ProgressMonitorDialog dialog = new ProgressMonitorDialog(shell);
		try {
			dialog.run(false, false, new IRunnableWithProgress() {

				@Override
				public void run(IProgressMonitor monitor)
						throws InvocationTargetException, InterruptedException {
					try {
						String token = deployer.executeTokenGeneration(
								kvTable.getItem(selectionIndex).getText(0), monitor);
						if (token != null && token.length()>0){
							String name =  LicenseTokenUtils.getName(token);
							if (name.equalsIgnoreCase(kvTable.getItem(selectionIndex).getText(0).trim())){
									kvTable.getItem(selectionIndex).setText(1,token);
							}else{
								throw(new Exception("Token license identifier "+ name + " is not the specified in the constraints ("+ kvTable.getItem(selectionIndex).getText(0).trim()+")"));
							}
						}else{
							log.debug("Generated token is invalid");
							throw (new Exception("Generated token is invalid"));
						}
					} catch (Exception e) {
						log.debug("Exception:"+e.getMessage());
						throw (new InvocationTargetException(e));
						
					}
				}
			});
		} catch (InterruptedException e) {
			log.error("Exception generating licenses", e);
			ErrorDialog.openError(shell, "Exception Generating license token ",
					e.getMessage(), new Status(IStatus.ERROR, Activator.PLUGIN_ID,e.getLocalizedMessage(), e));
		} catch (InvocationTargetException e) {
			log.error("Exception generating licenses", e);
			ErrorDialog.openError(shell, "Exception Generating license token ",
					e.getMessage(), new Status(IStatus.ERROR,Activator.PLUGIN_ID,e.getLocalizedMessage(), e));
		}
	}
	
	/**
	 * Execute the runnable to generate all the license tokens
	 */
	protected void generateAllTokens() {

		ProgressMonitorDialog dialog = new ProgressMonitorDialog(shell);
		try {
			dialog.run(false, false, new IRunnableWithProgress() {

				@Override
				public void run(IProgressMonitor monitor)
						throws InvocationTargetException, InterruptedException {
					try {
						if (kvTable.getItemCount()>0){
							for (int index = 0; index < kvTable.getItemCount(); index++){
								String token = deployer.executeTokenGeneration(kvTable.getItem(index).getText(0), monitor);
								if (token != null && token.length()>0){
									String name =  LicenseTokenUtils.getName(token);
									if (name.equalsIgnoreCase(kvTable.getItem(index).getText(0).trim())){
										kvTable.getItem(index).setText(1,token);
									}else{
										throw(new Exception("Token license identifier "+ name + " is not the specified in the constraints ("+ kvTable.getItem(index).getText(0).trim()+")"));
									}
						
								}else
									throw (new Exception("Generated token for " + kvTable.getItem(index).getText(0)+ " is invalid"));
							}
						}
					} catch (Exception e) {
						log.debug("Exception during generation:" + e.getMessage());
						throw (new InvocationTargetException(e));
					}
				}
			});
		} catch (InterruptedException e) {
			ErrorDialog.openError(shell, "Exception Generating license ",
					e.getMessage(), new Status(IStatus.ERROR, Activator.PLUGIN_ID, e.getLocalizedMessage(), e));
			log.error("Exception Generating License tokens", e);
		} catch (InvocationTargetException e) {
			ErrorDialog.openError(shell, "Exception Generating license",
					e.getMessage(), new Status(IStatus.ERROR, Activator.PLUGIN_ID, e.getLocalizedMessage(), e));
			log.error("Exception Generating License tokens", e);
		}
	}
	
	/** Get the current license tokens map stored in the table 
	 * @return token map
	 */
	public Map<String, String> getTokenMap() {
		Map<String, String> map = new HashMap<String, String>();
		TableItem[] items = kvTable.getItems();
		for (TableItem it : items) {
			map.put(it.getText(0).trim(), it.getText(1).trim());
		}
		return map;
	}
	
	/** Set the license tokens map to the table
	 * @param map token map
	 */
	public void setTokenMap(Map<String, String> map) {
		kvTable.removeAll();
		if (map.size()>0){
			for (Entry<String, String> e : map.entrySet()) {
				TableItem it = new TableItem(kvTable, SWT.V_SCROLL|SWT.H_SCROLL);
				it.setText(new String[] { e.getKey(), e.getValue() });
			}
			generateAllButton.setEnabled(true);
		}
	}

	/**Reset the values of the table and map, removing all the values.
	 * 
	 */
	public void reset() {
		for (TableItem it:kvTable.getItems()){
			it.setText(1, "");
		}
	}
	
	/** Enable/disable the license token composite widgets
	 * @param b True for enabling, false for disabling
	 */
	public void setEnabled(boolean b) {
		kvTable.setEnabled(b);
		selectButton.setEnabled(b);
		generateButton.setEnabled(b);
	}
}

/**
 * Class for managing the components scope of the different deployment options 
 * @author Jorge Ejarque (Barcelona Supercomputing Center)
 *
 */
class ScopedListsComposite {

	private FormToolkit toolkit;
	private Group listsRow;
	private List elList;
	private Button removeButton;
	private List compList;
	private Button addButton;
	private String title;

	private static Logger log = Logger.getLogger(ScopedListsComposite.class);
	
	/**
	 * Constructor
	 * @param toolkit Editor's toolkit
	 */
	public ScopedListsComposite(FormToolkit toolkit, String title) {
		this.toolkit = toolkit;
		this.title = title;
	}

	/**
	 * Create the scope composite in the specified parent composite
	 * @param composite Parent composite
	 * @return new scoped composite
	 */
	public Composite createComposite(Composite composite) {
		
		listsRow = new Group(composite, SWT.BORDER);
		GridData rd = new GridData(GridData.FILL_HORIZONTAL
				| GridData.VERTICAL_ALIGN_BEGINNING);
		rd.grabExcessHorizontalSpace = true;
		listsRow.setLayoutData(rd);
		GridLayout firstRow1Layout = new GridLayout();
		firstRow1Layout.numColumns = 4;
		listsRow.setLayout(firstRow1Layout);
		if (title!= null && !title.isEmpty())
			listsRow.setText(title);
		elList = new List(listsRow, SWT.READ_ONLY | SWT.MULTI | SWT.V_SCROLL |SWT.H_SCROLL
				| SWT.BORDER );
		elList.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				addButton.setEnabled(true);
			}
		});
		rd = new GridData(GridData.HORIZONTAL_ALIGN_FILL
				| GridData.VERTICAL_ALIGN_CENTER);
		rd.grabExcessHorizontalSpace = true;
		rd.grabExcessVerticalSpace = true;
		rd.minimumHeight = 70;
		rd.heightHint =70;
		elList.setLayoutData(rd);
		removeButton = toolkit.createButton(listsRow, "<", SWT.NONE);
		rd = new GridData(GridData.VERTICAL_ALIGN_CENTER);
		removeButton.setLayoutData(rd);
		removeButton.setEnabled(false);
		removeButton.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {
				removePackages(compList.getSelection());
			}

			@Override
			public void widgetSelected(SelectionEvent arg0) {
				removePackages(compList.getSelection());
			}
		});
		addButton = toolkit.createButton(listsRow, ">", SWT.NONE);
		rd = new GridData(GridData.VERTICAL_ALIGN_CENTER);
		addButton.setLayoutData(rd);
		addButton.setEnabled(false);
		addButton.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {
				addPackages(elList.getSelection());
			}

			@Override
			public void widgetSelected(SelectionEvent arg0) {
				addPackages(elList.getSelection());
			}
		});
		compList = new List(listsRow, SWT.READ_ONLY | SWT.MULTI | SWT.V_SCROLL |SWT.H_SCROLL
				| SWT.BORDER);
		compList.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				removeButton.setEnabled(true);
			}
		});
		rd = new GridData(GridData.HORIZONTAL_ALIGN_FILL
				| GridData.VERTICAL_ALIGN_CENTER);
		rd.grabExcessHorizontalSpace = true;
		rd.grabExcessVerticalSpace = true;
		rd.minimumHeight = 70;
		rd.heightHint =70;
		compList.setLayoutData(rd);
		return listsRow;
	}

	/**
	 * Remove packages in the scope
	 * @param packs Array of package names
	 */
	protected void removePackages(String[] packs) {
		for (String p : packs) {
			compList.remove(p);
			elList.add(p);
		}
		removeButton.setEnabled(false);
	}

	/**
	 * Add packages to the scope
	 * @param packs Array of packages
	 */
	protected void addPackages(String[] packs) {
		for (String p : packs) {
			compList.add(p);
			if (elList.indexOf(p) >= 0)
				elList.remove(p);
		}
		addButton.setEnabled(false);
	}

	/**
	 * Reset the scope values without scoped packages
	 */
	protected void reset() {
		elList.setItems(new String[0]);
		compList.setItems(new String[0]);
		addButton.setEnabled(false);
		removeButton.setEnabled(false);
	}

	/**
	 * Reset adding a set of packages in the possible packages list
	 * @param elements Scoped packages
	 */
	protected void reset(String[] elements) {
		reset();
		elList.setItems(elements);
	}

	/**
	 * Set the scoped list with a set of possible packages and a set of selected packages 
	 * @param totalElements Possible packages
	 * @param selectedElements Selected packages
	 */
	protected void setPackagesLists(String[] totalElements,
			String[] selectedElements) {
		reset(totalElements);
		addPackages(selectedElements);

	}

	/** 
	 * Get the set of selected packages
	 * @return Array of selected packages names
	 */
	protected String[] getSelectedPackages() {
		return compList.getItems();
	}

	/**
	 * Get the set of possible packages not selected
	 * @return
	 */
	protected String[] getNonSelectedPackages() {
		return elList.getItems();
	}

	/** Enable/Disable composite widgets
	 * @param b True for enabling, false for disabling
	 */
	public void setEnabled(boolean b) {
		elList.setEnabled(b);
		compList.setEnabled(b);
		if (!b) {
			addButton.setEnabled(false);
			removeButton.setEnabled(false);
		}
	}
}

class SaveResetButtonComposite {
	private String type;
	private Button reset;
	private Button save;
	private Shell shell;
	private FormToolkit toolkit;
	private OptimisDeployer deployer;
	protected Logger log = Logger.getLogger(SaveResetButtonComposite.class);

	public SaveResetButtonComposite(Shell shell, FormToolkit toolkit, String type, OptimisDeployer deployer) {
		this.type = type;
		this.shell = shell;
		this.toolkit = toolkit;
		this.deployer = deployer;
		
	}

	public Composite createComposite(Composite composite) {
		Composite buttons_comp = toolkit.createComposite(
				composite, SWT.NONE);
		GridData rd = new GridData(GridData.FILL_HORIZONTAL
				| GridData.VERTICAL_ALIGN_BEGINNING);
		rd.grabExcessHorizontalSpace = true;
		buttons_comp.setLayoutData(rd);
		buttons_comp.setLayout(new GridLayout(2, false));
		reset = toolkit.createButton(buttons_comp, "Reset",
				SWT.NORMAL);
		reset.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {
				cleanDetails();
			}

			@Override
			public void widgetSelected(SelectionEvent arg0) {
				cleanDetails();
			}
		});
		save = toolkit.createButton(buttons_comp, "Save",
				SWT.NORMAL);
		save.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {
				try {
					saveSection();
				} catch (Exception e) {
					ErrorDialog.openError(shell,
							"Error updating manifest", e.getMessage(), new Status(IStatus.ERROR,Activator.PLUGIN_ID,"Exception", e));
					log.error("Exception updating manifest", e);
				}
			}

			@Override
			public void widgetSelected(SelectionEvent arg0) {
				try {
					saveSection();
				} catch (Exception e) {
					ErrorDialog.openError(shell,
							"Error updating manifest", e.getMessage(),new Status(IStatus.ERROR, Activator.PLUGIN_ID, "Exception", e));
					
				}
			}
		});
		return buttons_comp;
	}

	private void cleanDetails() {
		if (type.equals(OptimisDeployer.TRUST)) {
			deployer.cleanTrustDetails();
		} else if (type.equals(OptimisDeployer.RISK)) {
			deployer.cleanRiskDetails();
		} else if (type.equals(OptimisDeployer.ECO)) {
			deployer.cleanEcoDetails();
		} else if (type.equals(OptimisDeployer.COST)) {
			deployer.cleanCostDetails();
		} else if (type.equals(OptimisDeployer.DP)) {
			deployer.cleanDPDetails();
		} else if (type.equals(OptimisDeployer.AFFINITY)) {
			deployer.cleanAFRuleDetails();
		} else if (type.equals(OptimisDeployer.ANTIAFFINITY)) {
			deployer.cleanAntiAFRuleDetails();
		} else if (type.equals(OptimisDeployer.INTRA_AF)) {
			deployer.cleanComponentAFRuleDetails();
		} else if (type.equals(OptimisDeployer.INTRA_ANTIAF)) {
			deployer.cleanComponentAntiAFRuleDetails();
		} else if (type.equals(OptimisDeployer.LICENSE)) {
			deployer.cleanLicenseDetails();
		}

	}

	private void saveSection() throws Exception {
		if (type.equals(OptimisDeployer.TRUST)) {
			deployer.saveTrustSection();
		} else if (type.equals(OptimisDeployer.RISK)) {
			deployer.saveRiskSection();
		} else if (type.equals(OptimisDeployer.ECO)) {
			deployer.saveEcoSection();
		} else if (type.equals(OptimisDeployer.COST)) {
			deployer.saveCostSection();
		} else if (type.equals(OptimisDeployer.DP)) {
			deployer.saveDataProtection();
		} else if (type.equals(OptimisDeployer.AFFINITY)) {
			deployer.saveAffinityRule();
		} else if (type.equals(OptimisDeployer.ANTIAFFINITY)) {
				deployer.saveAntiAffinityRule();
		} else if (type.equals(OptimisDeployer.INTRA_AF)) {
			deployer.saveComponentAffinityRule();
		} else if (type.equals(OptimisDeployer.INTRA_ANTIAF)) {
				deployer.saveComponentAntiAffinityRule();
		} else if (type.equals(OptimisDeployer.LICENSE)) {
			deployer.saveLicenseToken();
		}
	}

	public void setEnabled(boolean b) {
		reset.setEnabled(b);
		save.setEnabled(b);
	}

}

class IPRMode {
	
	private static HashMap<String, IPRMode> modes;
	private static final IPRMode ipr_1 = new IPRMode(1, "IPR_1", "SP retains service data ownership, but not metadata", 
			"Service Provider ( as Cloud customer) retains ownership of any intellectual property rights in the content it deployed to" +
			" the cloud. Any intellectual property developed by the cloud provider during the performance of the " +
			"services belongs to the cloud provider.");
	private static final IPRMode ipr_2 = new IPRMode(2, "IPR_2", "SP retains service data and metadata ownership", 
			"Service Provider ( as Cloud customer) retains ownership of any intellectual property rights in the content it deployed to " +
			"the cloud, including the metadata accruing from the services");
	private static final IPRMode ipr_3 = new IPRMode(3, "IPR_3", "SP retains data ownership and metadata " +
			"ownership will be negotiated with IP", "Service Provider ( as Cloud customer) retains ownership of any intellectual property " +
			"rights in the content it deployed to the cloud. Any intellectual property in the metadata accruable from " +
			"the services shall be negotiated between the cloud customer and the cloud provider.");
	private int mode;
	private String modeNameDisplay;
	private String modeCode;
	private String iprText;
	static{
		HashMap<String, IPRMode> map = new HashMap<String, IPRMode>();
		map.put(ipr_1.getModeNameDisplay(),ipr_1);
		map.put(ipr_2.getModeNameDisplay(),ipr_2);
		map.put(ipr_3.getModeNameDisplay(),ipr_3);
		modes=map;
	}
	
	public IPRMode(int mode, String modeCode, String displayName, String text){
		this.mode= mode;
		this.modeCode = modeCode;
		this.modeNameDisplay = displayName;
		this.iprText = text;
		
	}
	
	public int getMode() {
		return mode;
	}
	public void setMode(int mode) {
		this.mode = mode;
	}
	public String getModeNameDisplay() {
		return modeNameDisplay;
	}
	public void setModeNameDisplay(String modeNameDisplay) {
		this.modeNameDisplay = modeNameDisplay;
	}
	public String getModeNameCode() {
		return modeCode;
	}
	public void setModeNameCode(String modeNameCode) {
		this.modeCode = modeNameCode;
	}
	public String getIprText() {
		return iprText;
	}
	public void setIprText(String iprText) {
		this.iprText = iprText;
	}
	
	public static String[] getIPRModes(){
		return modes.keySet().toArray(new String[modes.size()]);
	}
	
	public static IPRMode getIPRMode(String ipr){
		return modes.get(ipr);
		
	}
}

/**
 * Implements the Deployer class for the Optimis cloud
 * @author Jorge Ejarque (Barcelona Supercomputing Center)
 *
 */
public class OptimisDeployer extends Deployer {

	private Text serverText;
	private boolean created;
	private boolean toBeUpdated;
	private Text icsText;
	private Composite ics_location;
	private Button icsButton;
	private Hyperlink link;
	private Manifest manifest;
	private Composite trec_type_composite;
	private CTabFolder trec_tab;
	private Composite trec_composite;
	private Section trec_section;
	private Section SPEndP_section;
	private Section DP_section;
	private Composite dp_composite;
	private Text eaText;
	private Section AF_section;
	private Composite af_composite;
	private Combo optim;
	private Button allowFederated;
	private Combo risk_sec;
	private Text risk_level;
	private ScopedListsComposite risk_scope;
	private Combo eco_sec;
	private Combo BREEAM_cert;
	private Combo LEED_cert;
	private Button eu_coc;
	private Text energyStar;
	private ScopedListsComposite eco_scope;
	private Combo cost_sec;
	private Text cost_cap;
	private Combo currency;
	private ScopedListsComposite cost_scope;
	private Combo trust_sec;
	private Text trust_level;
	private ScopedListsComposite trust_scope;
	private Combo af_level;
	private Combo af_sec;
	private ScopedListsComposite af_scope;
	private Text dpText;
	private Text ds_storageText;
	private Text ds_unitsText;
	private ScopedListsComposite dp_scope;
	private Text ea_keyText;
	// private Text lmsText;
	// private Button lmsButton;
	private KeyValueTableComposite risk_avail;
	private SaveResetButtonComposite af_but;
	private SaveResetButtonComposite dp_but;
	private SaveResetButtonComposite cost_but;
	private SaveResetButtonComposite eco_but;
	private SaveResetButtonComposite risk_but;
	private SaveResetButtonComposite trust_but;
	private LicenseTokensTableComposite license_tks;
	private Text license_server;
	private Text license_user_options;
	private SaveResetButtonComposite license_but;
	private Section license_section;
	private Composite license_composite;
	private OptimisProperties op_prop;
	private Section DS_section;
	private Composite options;

	protected final static String TRUST = "TRUST";
	protected final static String RISK = "RISK";
	protected final static String ECO = "ECO";
	protected final static String COST = "COST";
	protected final static String AFFINITY = "AFF";
	protected final static String INTRA_AF = "INTRA_AF";
	protected final static String ANTIAFFINITY = "ANTIAFF";
	protected final static String INTRA_ANTIAF = "INTRA_ANTIAF";
	protected static final String DP = "DP";
	protected static final String LICENSE = "License";
	
	private static Logger log = Logger.getLogger(OptimisDeployer.class);

	//public static final String OPTIMIS_PREFIX = "optimis-pm-";
	private static final String RISK_SECTION = "Risk Section ";
	private static final String TRUST_SECTION = "Trust Section ";
	private static final String ECO_SECTION = "Eco-Eff Section ";
	private static final String COST_SECTION = "Cost Section ";
	private static final String AFFINITY_RULE = "Affinity Rule ";
	private static final String ANTI_AFFINITY_RULE = "Anti-affinity Rule ";
	private static final String DATA_STORAGE = "Data Storage ";
	
	private static final String PROPERTIES_FILENAME = "optimis.properties";
	private static final String DEFAULT_IMAGE_CREATION_MODE = "Default Mode";
	private static final String BROKER_IMAGE_CREATION_MODE = "Broker Optimization Mode";
	private static final String[] IMAGE_CREATION_MODES = 
			new String[]{DEFAULT_IMAGE_CREATION_MODE,BROKER_IMAGE_CREATION_MODE};
	private static final String ENERGY_EFFICIENCY = "EnergyEfficiency";
	private static final String ECOLOGICAL_EFFICIENCY = "EcologicalEfficiency";
	private static final String IPR_MODE_SP_DATA = "SP retain service data IPR ownership";
	private static final String IPR_MODE_SP_METADATA = "SP retain service metadata IPR ownership";
	private static final String IPR_MODE_SP_DATA_METADATA = "SP retain service data and metadata IPR ownership";
			
	//private static final String[] IPR_MODES= new String[]{IPR_MODE_SP_DATA,IPR_MODE_SP_METADATA, IPR_MODE_SP_DATA_METADATA}; 
	public static final String ENCRYPTED_DS = "Encrypted Storage";
	public static final String SHARED_DS = "Shared Storage";
	
	private File propFile;
	private Text cliPropText;
	private Button cliPropButton;
	private Combo icsMode;
	private Combo ds_sec;
	private Button dp_BCR;
	private Button dp_SCC;
	private Combo dp_IPR;
	private ScopedListsComposite dp_white_list;
	private ScopedListsComposite ips_scope;
	private ScopedListsComposite vpn_scope;
	private Button ISO_cert;
	private Combo greenStar;
	private Combo CASBEE_cert;
	private Text eco_thres;
	private Combo eco_type;
	private Text ener_thres;
	private Combo ener_type;
	private Text ener_mag;
	private Text ener_time;
	private Text eco_mag;
	private Text eco_time;
	private Combo af_component;
	private Combo af_component_level;
	private SaveResetButtonComposite af_component_but;
	private Combo anti_af_component;
	private Combo anti_af_component_level;
	private SaveResetButtonComposite anti_af_component_but;
	private Combo anti_af_sec;
	private Combo anti_af_level;
	private ScopedListsComposite anti_af_scope;
	private SaveResetButtonComposite anti_af_but;
	private CTabFolder af_tab;
	private Composite af_type_composite;


	public OptimisDeployer(ServiceFormEditor editor, IWorkbenchWindow window,
			BuildingDeploymentFormPage page) {
		super(editor, window, page);
		toBeUpdated = true;
		propFile = editor.getProject().getProject()
				.getFolder(ProjectMetadata.METADATA_FOLDER)
				.getFile(PROPERTIES_FILENAME).getRawLocation().toFile();
	}
	
	/* (non-Javadoc)
	 * @see es.bsc.servicess.ide.editors.Deployer#createComposite(org.eclipse.ui.forms.widgets.FormToolkit, org.eclipse.swt.widgets.Composite, org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public Composite createComposite(FormToolkit toolkit,
			Composite deploymentOptions, Composite old_composite) {
		composite = page.getToolkit().createComposite(deploymentOptions,
				SWT.NONE);
		GridData rd = new GridData(GridData.FILL_HORIZONTAL
				| GridData.VERTICAL_ALIGN_BEGINNING);
		// rd.grabExcessHorizontalSpace = true;
		composite.setLayoutData(rd);
		composite.setLayout(new GridLayout(1, false));

		createImageSection(composite);
		createLicenseSection(composite);
		createAFFSection(composite);
		createTRECSection(composite);
		createDataProtectionSection(composite);
		createDeploymentSection(composite);

		link = toolkit.createHyperlink(composite, "View Service Manifest",
				SWT.NONE);
		link.addHyperlinkListener(new HyperlinkAdapter() {
			public void linkActivated(HyperlinkEvent e) {
				editServiceManifest();
			}
		});
		link.setLayoutData(rd);

		created = true;
		if (old_composite != null) {
			old_composite.dispose();
		}
		old_composite = composite;
		return composite;
	}
	
	public void update() {
		if (toBeUpdated) {

		}
	}
	
	public void initiate() {
		// TODO add to be updated;
		try {
			op_prop = new OptimisProperties(propFile);
			if (getProject().getProject()
					.getFolder(ProjectMetadata.OUTPUT_FOLDER)
					.getFolder(ProjectMetadata.PACKAGES_FOLDER).getLocation()
					.append(ProjectMetadata.SERVICE_MANIFEST).toFile().exists())
				readManifestFromFile();
			else
				generateNewManifest();
			icsText.setText(op_prop.getICSLocation());
			serverText.setText(op_prop.getDSLocation());
			trec_tab.setSelection(0);
			trec_type_composite = createTRUSTComposite(trec_composite,
					page.getToolkit());
			initTRUSTparameters();
			trec_section.setClient(trec_composite);
			trec_section.setExpanded(true);
			trec_section.setExpanded(false);
			af_tab.setSelection(0);
			af_type_composite = createAffinityComposite(af_composite,
					page.getToolkit());
			initAFparameters();
			AF_section.setClient(af_composite);
			AF_section.setExpanded(true);
			AF_section.setExpanded(false);
			loadDPparameters();
			loadLicenseParameters();
	
		} catch (Exception e) {
			log.error("Error loading service manifest file", e);
			
		}
	
	}

	//**** Image Creation interaction part
	
	/**Create the image section
	 * @param composite Parent's composite
	 */
	private void createImageSection(Composite composite) {
		SPEndP_section = page.getToolkit().createSection(composite,
				Section.TWISTIE | Section.DESCRIPTION);
		SPEndP_section.setText("Image Creation");
		SPEndP_section
				.setDescription("Define the option for Image Creation in the OPTIMIS Service Provider");
		GridData rd = new GridData(GridData.HORIZONTAL_ALIGN_FILL
				| GridData.VERTICAL_ALIGN_BEGINNING);
		rd.grabExcessHorizontalSpace = true;
		SPEndP_section.setLayoutData(rd);
		SPEndP_section.setLayout(new GridLayout(1, true));
		ics_location = page.getToolkit().createComposite(SPEndP_section,
				SWT.BORDER);
		rd = new GridData(GridData.FILL_HORIZONTAL);
		rd.grabExcessHorizontalSpace = true;
		ics_location.setLayoutData(rd);
		ics_location.setLayout(new GridLayout(3, false));
		page.getToolkit().createLabel(ics_location, "Image Creation Service",
				SWT.NONE);
		icsText = page.getToolkit().createText(ics_location, "",
				SWT.SINGLE | SWT.BORDER);
		icsText.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent arg0) {
				op_prop.setICSLocation(icsText.getText().trim());
				try {
					op_prop.save();
				} catch (ConfigurationException e) {
					log.error("Error modifiying optimis properties", e);
					ErrorDialog.openError(getShell(),
							"Saving optimis properties", e.getMessage(), new Status(IStatus.ERROR,Activator.PLUGIN_ID, e.getMessage(), e));
					;
				}
			}
		});
		rd = new GridData();
		rd.grabExcessHorizontalSpace = true;
		rd.minimumWidth = 400;
		icsText.setLayoutData(rd);
		icsButton = page.getToolkit().createButton(ics_location,
				"Create Images", SWT.NORMAL);
		icsButton.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {
				createServiceImages();
			}
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				createServiceImages();
			}
		});
		page.getToolkit().createLabel(ics_location, "Image Creation Mode",
				SWT.NONE);
		icsMode = af_sec = new Combo(ics_location, SWT.NONE);
		rd = new GridData(GridData.FILL_HORIZONTAL);
		rd.grabExcessHorizontalSpace = true;
		rd.minimumWidth = 400;
		icsMode.setLayoutData(rd);
		icsMode.setItems(IMAGE_CREATION_MODES);
		SPEndP_section.setClient(ics_location);
		SPEndP_section.setExpanded(true);
		SPEndP_section.setExpanded(false);
	}
	
	/**
	 * Invokes the runnable for creating the service images
	 */
	protected void createServiceImages() {
		ProgressMonitorDialog dialog = new ProgressMonitorDialog(getShell());
		try {
			dialog.run(false, false, new IRunnableWithProgress() {

				@Override
				public void run(IProgressMonitor monitor)
						throws InvocationTargetException, InterruptedException {
					try {
						executeImageCreation(monitor);
					} catch (Exception e) {
						throw (new InvocationTargetException(e));
					}
				}
			});
		} catch (InterruptedException e) {
			log.error("Error creating images", e);
			ErrorDialog.openError(super.getShell(), "Error creating images",
					e.getMessage(), new Status(Status.ERROR, Activator.PLUGIN_ID,"Exception creating Images", e));
		} catch (InvocationTargetException e) {
			log.error("Error creating images", e);
			ErrorDialog.openError(super.getShell(), "Error creating images",
					e.getMessage(), new Status(Status.ERROR, Activator.PLUGIN_ID,"Exception creating Images", e));
		}
	}

	/**
	 * Executes the creation of the service images invoking the 
	 * Image Creation service and installing the service packages
	 * 
	 * @param monitor Object to monitor the image creation progress
	 * @throws Exception 
	 */
	protected void executeImageCreation(IProgressMonitor monitor)
			throws Exception {
		String location = icsText.getText().trim();
		if (location != null && location.length() > 0) {
			Client c = Client.create();
			WebResource resource = c.resource(location);
			if (resource != null) {
				ProjectMetadata pr_meta = new ProjectMetadata(super.getEditor()
						.getMetadataFile().getRawLocation().toFile());
				pr_meta.removeAllImages();
				HashMap<String, ServiceElement> allEls = CommonFormPage.getElements(
						pr_meta.getAllOrchestrationClasses(), ProjectMetadata.BOTH_TYPE, 
						super.getProject(), pr_meta);
				String[] allPacks = pr_meta.getPackages();
				String[] oePacks = pr_meta.getPackagesWithOrchestration();
				String[] cePacks = pr_meta.getPackagesWithCores();
				IFolder packageFolder = super.getProject().getProject()
						.getFolder(ProjectMetadata.OUTPUT_FOLDER)
						.getFolder(ProjectMetadata.PACKAGES_FOLDER);
				if (this.manifest == null) {
					generateNewManifest();
				}
				writeManifestToFile();
				if (icsMode.getItem(icsMode.getSelectionIndex()).
						equalsIgnoreCase(BROKER_IMAGE_CREATION_MODE)){
					log.debug("Broker Mode");
					String[] id_url;
					//Y2 ICS
					//String imageDescription = "OrchestrationElement";
					//Y3 ICS
					String imageDescription = ImageCreation.getFullImageDescription(pr_meta, allEls);
					//"<ImageTemplate><operatingSystem>CentOS</operatingSystem><imageSize>9</imageSize></ImageTemplate>";
					log.debug("Requesting image creation: " +imageDescription);
					if (oePacks != null && oePacks.length > 0){ 
						id_url = ImageCreation.createFullImage(resource, oePacks, allPacks, cePacks, 
								packageFolder, pr_meta,	imageDescription, monitor);
					}else{
						log.debug("No oe packages, Creating single one by default.");
						id_url = ImageCreation.createFullImage(resource, new String[]{editor.getProject()
								.getProject().getName()}, allPacks, cePacks, packageFolder, pr_meta,
								imageDescription, monitor);		
						pr_meta.addImage(id_url[0], id_url[1], editor.getProject()
							.getProject().getName());
					}
					if (allPacks != null && allPacks.length > 0) {
						for (String p : allPacks) {
							pr_meta.addImage(id_url[0], id_url[1], p);
						}
					}
				}else{
					log.debug("Default Mode");
					//String[] oePacks = pr_meta.getPackagesWithCores();
					if (oePacks != null && oePacks.length > 0) {
						for (String p : oePacks) {
							//Y2 ICS
							//String imageDescription = "OrchestrationElement";
							//Y3 ICS
							String imageDescription = ImageCreation.getImageDescription(pr_meta,p ,allEls, 
									true, editor.getProject());
							log.debug("Requesting image creation: " +imageDescription);
							String[] id_url;
							id_url = ImageCreation.createFrontEndImage(resource, p, oePacks[0], allPacks, 
									packageFolder, pr_meta, imageDescription, monitor);
							pr_meta.addImage(id_url[0], id_url[1], p);
						}
					}else{
						//Y2 ICS
						//String imageDescription = "OrchestrationElement";
						//Y3 ICS
						String projectName = editor.getProject().getProject().getName();
						String imageDescription = ImageCreation.getImageDescription(pr_meta, projectName
								,allEls, true,editor.getProject() );
						log.debug("Requesting image creation: " +imageDescription);
						String[] id_url = ImageCreation.createFrontEndImage(resource, projectName, projectName,
								allPacks, packageFolder, pr_meta, imageDescription, monitor);
						pr_meta.addImage(id_url[0], id_url[1], editor.getProject().getProject().getName());
					}
					//String[] cePacks = pr_meta.getPackagesWithCores();
					if (cePacks != null && cePacks.length > 0) {
						for (String p : cePacks) {
							// create package image
							//Y2 ICS
							//String packImageDesc = "CoreElement";
							//Y3 ICS
							String packImageDesc = ImageCreation.getImageDescription(pr_meta, p ,allEls, false
									,editor.getProject());
							log.debug("Requesting image creation: " +packImageDesc);
							String[] id_url = ImageCreation.createPackageImage(resource, p, packageFolder, pr_meta, 
									packImageDesc, monitor);
							pr_meta.addImage(id_url[0], id_url[1], p);
						}
					}
				}
				pr_meta.toFile(editor.getMetadataFile().getLocation().toFile());
				ManifestCreation.setImagesInManifest(pr_meta.getImageURLPackagesMap(), this.manifest);
			}
		}
	}

	// ***********************  License Management Part **************************
	
	private void createLicenseSection(Composite composite) {
		license_section = page.getToolkit().createSection(composite,
				Section.TWISTIE | Section.DESCRIPTION);
		license_section.setText("License Tokens");
		license_section.setDescription("Define license tokens");
		GridData rd = new GridData(GridData.HORIZONTAL_ALIGN_FILL
				| GridData.VERTICAL_ALIGN_BEGINNING);
		rd.grabExcessHorizontalSpace = true;
		license_section.setLayoutData(rd);
		license_section.setLayout(new GridLayout(1, true));
		license_composite = page.getToolkit().createComposite(license_section,
				SWT.BORDER);
		rd = new GridData(GridData.FILL_HORIZONTAL);
		rd.grabExcessHorizontalSpace = true;
		license_composite.setLayoutData(rd);
		license_composite.setLayout(new GridLayout(1, false));
		Composite combo_comp = page.getToolkit().createComposite(
				license_composite, SWT.NONE);
		rd = new GridData(GridData.FILL | GridData.VERTICAL_ALIGN_BEGINNING);
		rd.grabExcessHorizontalSpace = true;
		combo_comp.setLayoutData(rd);
		combo_comp.setLayout(new GridLayout(2, false));
		page.getToolkit().createLabel(combo_comp, " License Tokens");
		license_tks = new LicenseTokensTableComposite(getShell(), page.getToolkit(), this);
		license_tks.createComposite(combo_comp);
		page.getToolkit().createLabel(combo_comp, " License Server");
		license_server = page.getToolkit().createText(combo_comp, "",
				SWT.V_SCROLL | SWT.BORDER | SWT.FILL);
		rd = new GridData(GridData.FILL);
		rd.grabExcessHorizontalSpace = true;
		rd.minimumWidth = 300;
		rd.minimumHeight = 100;
		rd.widthHint = 500;
		license_server.setLayoutData(rd);
		license_server.addModifyListener(new ModifyListener() {
	
			@Override
			public void modifyText(ModifyEvent arg0) {
				op_prop.setLSLocation(license_server.getText().trim());
				try {
					op_prop.save();
				} catch (ConfigurationException e) {
					log.error("Exception savin properties", e);
					ErrorDialog.openError(getShell(), "Saving optimis properties", 
						e.getMessage(), new Status(Status.ERROR, Activator.PLUGIN_ID,"Exception saving propertie", e));
					
				}
			}
	
		});
		page.getToolkit().createLabel(combo_comp, " Client Properties");
		Composite clientProp = page.getToolkit().createComposite(
				combo_comp, SWT.NONE);
		rd = new GridData(GridData.FILL | GridData.VERTICAL_ALIGN_BEGINNING);
		rd.grabExcessHorizontalSpace = true;
		clientProp.setLayoutData(rd);
		clientProp.setLayout(new GridLayout(2, false));
		cliPropText = page.getToolkit().createText(clientProp, "",
				SWT.SINGLE | SWT.BORDER);
		cliPropText.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent arg0) {
				op_prop.setLSClientProperties(cliPropText.getText().trim());
				try {
					op_prop.save();
				} catch (ConfigurationException e) {
					log.error("Exception saving properties", e);
					ErrorDialog.openError(getShell(),"Saving optimis properties", e.getMessage(),
						new Status(Status.ERROR, Activator.PLUGIN_ID,"Exception saving properties", e));
				}
			}
		});
		rd = new GridData();
		rd.grabExcessHorizontalSpace = true;
		rd.minimumWidth = 400;
		cliPropText.setLayoutData(rd);
		cliPropButton = page.getToolkit().createButton(clientProp,"Select...", SWT.NORMAL);
		cliPropButton.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {
				selectClientPropertiesFile();
			}
	
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				selectClientPropertiesFile();
			}
		});
			
		// Buttons part
		license_but = new SaveResetButtonComposite(getShell(),page.getToolkit(), LICENSE, this);
		license_but.createComposite(license_composite);
		license_section.setClient(license_composite);
		license_section.setExpanded(true);
		license_section.setExpanded(false);
	}
	
	/** Load the current license information stored in the manifest
	 * @throws Exception
	 */
	private void loadLicenseParameters() throws Exception {
		java.util.List<String> currentLicenses = LicenseTokenUtils.getLicensesFromProject(
				editor.getProject(), page.getProjectMetadata());
		Map<String, String> tokenMap = new HashMap<String,String>();
		String[] oePacks = page.getProjectMetadata().getPackagesWithOrchestration();
		if (oePacks != null && oePacks.length >0){
			String frontend_id = ManifestCreation.generateManifestName(
				oePacks[0]);
			byte[][] tokens = manifest.getServiceProviderExtensionSection()
				.getVirtualMachineComponentConfiguration(frontend_id)
				.getTokenArray();
			tokenMap = LicenseTokenUtils.getLicenseTokenFromManifest(
				tokens, currentLicenses);
		}else{
			for (String lic:currentLicenses)
				tokenMap.put(lic,"");
		}
		license_tks.setTokenMap(tokenMap);
		license_server.setText(op_prop.getLSLocation());
		cliPropText.setText(op_prop.getLSClientProperties());
	}

	/**
	 * Open a dialog to select the client properties file to request 
	 * the license tokens
	 */
	protected void selectClientPropertiesFile() {
		// TODO Auto-generated method stub
		final FileDialog dialog = new FileDialog(getShell());
		dialog.setText("Select Client Properties");
		String[] filterExt = { "*.properties" };
		dialog.setFilterExtensions(filterExt);
		String directoryName = cliPropText.getText().trim();
		if (directoryName.length() > 0) {
			final File path = new File(directoryName);
			if (path.exists())
				dialog.setFilterPath(directoryName);
		}
		final String selectedFile = dialog.open();
		if (selectedFile != null) {
			File f = new File(selectedFile);
			if (f.exists() && f.isFile()) {
				cliPropText.setText(selectedFile);
			} else {
				ErrorDialog.openError(getShell(), "Error",
						"Selecting client properties file", new Status(IStatus.ERROR, Activator.PLUGIN_ID,
								"File " + selectedFile
										+ " doesn't exists or is not a file"));
			}
		} else {
			ErrorDialog.openError(getShell(), "Error",
					"Selecting client properties file", new Status(IStatus.ERROR,Activator.PLUGIN_ID,
							"Error returned value from dialog is null"));
		}	
	}

	/**
	 * Clean the license parameters
	 */
	public void cleanLicenseDetails() {
		license_tks.reset();
	}

	/**
	 * Save the license token
	 * @throws Exception 
	 */
	public void saveLicenseToken() throws Exception {

		if (manifest == null) {
			generateNewManifest();
		}
		VirtualMachineComponentConfiguration[] comps = manifest
				.getServiceProviderExtensionSection()
				.getVirtualMachineComponentConfigurationArray();
		for (VirtualMachineComponentConfiguration com : comps) {
			//remove all
			byte[][] tokens = com.getTokenArray(); 
			if (tokens != null && tokens.length>0){	
				for (int i=0; i<tokens.length; i++)
					com.removeToken(i);
			}
			//add new ones (TODO:Only the used in the machine)
			Collection<String> newTokens = license_tks.getTokenMap().values();
			if(newTokens!= null && newTokens.size() >0){
				for (String token:newTokens)
					com.addToken(token.getBytes());
			}
		}
	}

	/**
	 * Execute the token generation process contacting 
	 * 
	 * @param licenseName Name of the requested license
	 * @param monitor Progress monitor
	 * @return License token
	 * @throws Exception
	 */
	public String executeTokenGeneration(String licenseName, IProgressMonitor monitor) throws Exception{
		//TODO complete the LS invocation adding other parameters
		String lsERP = license_server.getText().trim();
		String cliProp = cliPropText.getText().trim();
		if (lsERP != null && lsERP.length()>0 && cliProp != null && cliProp.length()>0){
			monitor.subTask("Analysing core elments requiring license " + licenseName);
			int numThreads = LicenseTokenUtils.getRequiredConcurrentExecutionsInLicense(
					licenseName, getEditor().getProject(), getPage().getProjectMetadata());
			if (numThreads >0){
				monitor.subTask("Generating core elments requiring license " + licenseName);
				return LicenseTokenUtils.generateToken(licenseName, lsERP, cliProp, numThreads);
			}else
				log.debug("The number of threads for license "+ licenseName+" is less than 0");
				throw(new Exception("The number of threads for license "+ licenseName+" is less than 0"));
		}else
			log.debug("License Server location or client properties are undefined.");
			throw(new Exception("License Server location or client properties are undefined. " +
					"Please define them."));
	}

	//******************** Affinity Section ********************************************
	
	/**
	 * Create affinity section in the indicated composite
	 * 
	 * @param composite Parent's composite
	 */
	private void createAFFSection(Composite composite) {
		AF_section = page.getToolkit().createSection(composite,
				Section.TWISTIE | Section.DESCRIPTION | SWT.BORDER);
		AF_section.setText("Affinity and Anti-affinity Rules");
		AF_section.setDescription("Define the Affinity and Anti-affinity rules " +
				"between the different components of a service and the different " +
				"instances of the components ");
		GridData rd = new GridData(GridData.HORIZONTAL_ALIGN_FILL
				| GridData.VERTICAL_ALIGN_BEGINNING);
		rd.grabExcessHorizontalSpace = true;
		AF_section.setLayoutData(rd);
		AF_section.setLayout(new GridLayout(1, true));
		af_composite = page.getToolkit().createComposite(AF_section, SWT.BORDER);
		rd = new GridData(GridData.FILL_HORIZONTAL);
		rd.grabExcessHorizontalSpace = true;
		af_composite.setLayoutData(rd);
		af_composite.setLayout(new GridLayout(1, false));
		af_type_composite = page.getToolkit().createComposite(af_composite,
				SWT.NONE);
		rd = new GridData(GridData.HORIZONTAL_ALIGN_FILL
				| GridData.VERTICAL_ALIGN_BEGINNING);
		rd.heightHint = 0;
		rd.grabExcessHorizontalSpace = true;
		af_type_composite.setLayoutData(rd);
		af_tab = new CTabFolder(af_composite, SWT.FLAT | SWT.TOP);
		page.getToolkit().adapt(af_tab, true, true);
		GridData gd = new GridData(GridData.HORIZONTAL_ALIGN_FILL
				| GridData.VERTICAL_ALIGN_BEGINNING);
		gd.heightHint = 0;
		af_tab.setLayoutData(gd);
		Color selectedColor = page.getToolkit().getColors()
				.getColor(FormColors.SEPARATOR);
		af_tab.setSelectionBackground(new Color[] { selectedColor,
				page.getToolkit().getColors().getBackground() },
				new int[] { 50 });
		page.getToolkit().paintBordersFor(af_tab);
		createAFFTabs(af_tab);
		af_tab.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				updateAFFSelection();
			}
	
			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {
				updateAFFSelection();
			}
		});	
		
	}
	
	private Composite createAffinityComposite(Composite comp, FormToolkit toolkit){
		Composite af_details_comp = toolkit.createComposite(comp, SWT.BORDER);
		GridData rd = new GridData(GridData.FILL_HORIZONTAL
				| GridData.VERTICAL_ALIGN_BEGINNING);
		rd.grabExcessHorizontalSpace = true;
		af_details_comp.setLayoutData(rd);
		af_details_comp.setLayout(new GridLayout(1, false));
		Group intra_af_comp = new Group(af_details_comp, SWT.BORDER);
		rd = new GridData(GridData.FILL_HORIZONTAL
				| GridData.VERTICAL_ALIGN_BEGINNING);
		rd.grabExcessHorizontalSpace = true;
		intra_af_comp.setLayoutData(rd);
		intra_af_comp.setLayout(new GridLayout(1, false));
		intra_af_comp.setText("Component Instance Affinity Rules");
		Composite af_comp_details = toolkit.createComposite(intra_af_comp, SWT.BORDER);
		rd = new GridData(GridData.FILL_HORIZONTAL
				| GridData.VERTICAL_ALIGN_BEGINNING);
		rd.grabExcessHorizontalSpace = true;
		af_comp_details.setLayoutData(rd);
		af_comp_details.setLayout(new GridLayout(2, false));
		toolkit.createLabel(af_comp_details, "Component");
		af_component = new Combo(af_comp_details, SWT.NONE);
		rd = new GridData(GridData.FILL_HORIZONTAL);
		rd.grabExcessHorizontalSpace = true;
		rd.minimumWidth = 350;
		af_component.setLayoutData(rd);
		af_component.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				loadComponentAffinityRule(af_component.getSelectionIndex());
			}
		});
		toolkit.createLabel(af_comp_details, "Level");
		af_component_level = new Combo(af_comp_details, SWT.NONE);
		rd = new GridData(GridData.FILL_HORIZONTAL);
		rd.grabExcessHorizontalSpace = true;
		rd.minimumWidth = 350;
		af_component_level.setLayoutData(rd);
		af_component_level.setItems(new String[] { "Low", "Medium", "High" });
		af_component_but = new SaveResetButtonComposite(getShell(), toolkit, INTRA_AF, this);
		af_component_but.createComposite(intra_af_comp);
		
		Group inter_af_comp = new Group(af_details_comp, SWT.BORDER);
		rd = new GridData(GridData.FILL_HORIZONTAL
				| GridData.VERTICAL_ALIGN_BEGINNING);
		rd.grabExcessHorizontalSpace = true;
		inter_af_comp.setLayoutData(rd);
		inter_af_comp.setLayout(new GridLayout(1, false));
		inter_af_comp.setText("Inter-Component Affinity Rules");
		Composite combo_comp = toolkit.createComposite(inter_af_comp,
				SWT.BORDER);
		rd = new GridData(GridData.FILL_HORIZONTAL
				| GridData.VERTICAL_ALIGN_BEGINNING);
		rd.grabExcessHorizontalSpace = true;
		combo_comp.setLayoutData(rd);
		combo_comp.setLayout(new GridLayout(3, false));
		toolkit.createLabel(combo_comp, "Rules");
		af_sec = new Combo(combo_comp, SWT.NONE);
		rd = new GridData(GridData.FILL_HORIZONTAL);
		rd.grabExcessHorizontalSpace = true;
		rd.minimumWidth = 300;
		af_sec.setLayoutData(rd);
		af_sec.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				loadAffinityRule(af_sec.getSelectionIndex());
			}
		});
		Button new_btn = toolkit.createButton(combo_comp, "New",
				SWT.NORMAL);
		new_btn.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {
				newAffinityRule();
			}
	
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				newAffinityRule();
			}
		});
	
		// Details part
		Group details_comp = new Group(
				inter_af_comp, SWT.BORDER);
		rd = new GridData(GridData.FILL_HORIZONTAL
				| GridData.VERTICAL_ALIGN_BEGINNING);
		rd.grabExcessHorizontalSpace = true;
		details_comp.setLayoutData(rd);
		details_comp.setLayout(new GridLayout(2, false));
		details_comp.setText("Rule Details");
		toolkit.createLabel(details_comp, "Level", SWT.NONE);
		af_level = new Combo(details_comp, SWT.NONE);
		rd = new GridData(GridData.FILL_HORIZONTAL);
		rd.grabExcessHorizontalSpace = true;
		rd.minimumWidth = 350;
		af_level.setLayoutData(rd);
		af_level.setItems(new String[] { "Low", "Medium", "High" });
	
		// Scope part;
		af_scope = new ScopedListsComposite(toolkit, "Components scope");
		af_scope.createComposite(inter_af_comp);
	
		// Buttons part
		af_but = new SaveResetButtonComposite(getShell(), toolkit, AFFINITY, this);
		af_but.createComposite(inter_af_comp);
		
		return af_details_comp;
	}
	
	private Composite createAntiAffinityComposite(Composite comp, FormToolkit toolkit){
		Composite anti_details_comp = toolkit.createComposite(comp,	SWT.BORDER);
		GridData rd = new GridData(GridData.FILL_HORIZONTAL
				| GridData.VERTICAL_ALIGN_BEGINNING);
		rd.grabExcessHorizontalSpace = true;
		anti_details_comp.setLayoutData(rd);
		anti_details_comp.setLayout(new GridLayout(1, false));
		Group intra_af_comp = new Group(anti_details_comp, SWT.BORDER);
		rd = new GridData(GridData.FILL_HORIZONTAL
				| GridData.VERTICAL_ALIGN_BEGINNING);
		rd.grabExcessHorizontalSpace = true;
		intra_af_comp.setLayoutData(rd);
		intra_af_comp.setLayout(new GridLayout(1, false));
		intra_af_comp.setText("Component Instance Anti-affinity Rules");
		Composite af_comp_details = toolkit.createComposite(intra_af_comp, SWT.BORDER);
		rd = new GridData(GridData.FILL_HORIZONTAL
				| GridData.VERTICAL_ALIGN_BEGINNING);
		rd.grabExcessHorizontalSpace = true;
		af_comp_details.setLayoutData(rd);
		af_comp_details.setLayout(new GridLayout(2, false));
		toolkit.createLabel(af_comp_details, "Component");
		anti_af_component = new Combo(af_comp_details, SWT.NONE);
		rd = new GridData(GridData.FILL_HORIZONTAL);
		rd.grabExcessHorizontalSpace = true;
		rd.minimumWidth = 300;
		anti_af_component.setLayoutData(rd);
		anti_af_component.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				loadComponentAntiAffinityRule(anti_af_component.getSelectionIndex());
			}
		});
		toolkit.createLabel(af_comp_details, "Level");
		anti_af_component_level = new Combo(af_comp_details, SWT.NONE);
		rd = new GridData(GridData.FILL_HORIZONTAL);
		rd.grabExcessHorizontalSpace = true;
		rd.minimumWidth = 350;
		anti_af_component_level.setLayoutData(rd);
		anti_af_component_level.setItems(new String[] { "Low", "Medium", "High" });
		anti_af_component_but = new SaveResetButtonComposite(getShell(), toolkit, INTRA_ANTIAF, this);
		anti_af_component_but.createComposite(intra_af_comp);
		
		Group inter_af_comp = new Group(anti_details_comp, SWT.BORDER);
		rd = new GridData(GridData.FILL_HORIZONTAL
				| GridData.VERTICAL_ALIGN_BEGINNING);
		rd.grabExcessHorizontalSpace = true;
		inter_af_comp.setLayoutData(rd);
		inter_af_comp.setLayout(new GridLayout(1, false));
		inter_af_comp.setText("Inter-Component Anti-affinity Rules");
		Composite combo_comp = toolkit.createComposite(inter_af_comp,
				SWT.BORDER);
		rd = new GridData(GridData.FILL_HORIZONTAL
				| GridData.VERTICAL_ALIGN_BEGINNING);
		rd.grabExcessHorizontalSpace = true;
		combo_comp.setLayoutData(rd);
		combo_comp.setLayout(new GridLayout(3, false));
		toolkit.createLabel(combo_comp, "Rules");
		anti_af_sec = new Combo(combo_comp, SWT.NONE);
		rd = new GridData(GridData.FILL_HORIZONTAL);
		rd.grabExcessHorizontalSpace = true;
		rd.minimumWidth = 300;
		anti_af_sec.setLayoutData(rd);
		anti_af_sec.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				loadAntiAffinityRule(anti_af_sec.getSelectionIndex());
			}
		});
		Button new_btn = toolkit.createButton(combo_comp, "New", SWT.NORMAL);
		new_btn.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {
				newAntiAffinityRule();
			}
	
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				newAntiAffinityRule();
			}
		});
	
		// Details part
		Group details_comp = new Group(
				inter_af_comp, SWT.BORDER);
		rd = new GridData(GridData.FILL_HORIZONTAL
				| GridData.VERTICAL_ALIGN_BEGINNING);
		rd.grabExcessHorizontalSpace = true;
		details_comp.setLayoutData(rd);
		details_comp.setLayout(new GridLayout(2, false));
		details_comp.setText("Rule Details");
		toolkit.createLabel(details_comp, "Level", SWT.NONE);
		anti_af_level = new Combo(details_comp, SWT.NONE);
		rd = new GridData(GridData.FILL_HORIZONTAL);
		rd.grabExcessHorizontalSpace = true;
		rd.minimumWidth = 350;
		anti_af_level.setLayoutData(rd);
		anti_af_level.setItems(new String[] { "Low", "Medium", "High" });
	
		// Scope part
		//page.getToolkit().createLabel(af_composite, "Components");
		anti_af_scope = new ScopedListsComposite(toolkit, "Components scope");
		anti_af_scope.createComposite(inter_af_comp);
	
		// Buttons part
		anti_af_but = new SaveResetButtonComposite(getShell(), toolkit, ANTIAFFINITY, this);
		anti_af_but.createComposite(inter_af_comp);
		
		return anti_details_comp;
	}
	
	/**
	 * Create the Tab for the different TREC parameters
	 * @param AFF_tab Tab component
	 *
	 */
	private void createAFFTabs(CTabFolder AFF_tab) {
		CTabItem trust_item = new CTabItem(AFF_tab, SWT.NULL);
		trust_item.setText("Affinity");
		trust_item.setData(AFFINITY);
	
		CTabItem risk_item = new CTabItem(AFF_tab, SWT.NULL);
		risk_item.setText("Anti-Affinity");
		risk_item.setData(ANTIAFFINITY);
	
	
	}

	/**
	 * Update the Affinity-AntiAffinity section according the selected type of rules
	 */
	private void updateAFFSelection() {
		CTabItem item = af_tab.getSelection();
		if (af_type_composite != null) {
			af_type_composite.dispose();
		}
		if (((String) item.getData()).equals(AFFINITY)) {
			af_type_composite = createAffinityComposite(af_composite,
					page.getToolkit());
			initAFparameters();
		} else if (((String) item.getData()).equals(ANTIAFFINITY)) {
			af_type_composite = createAntiAffinityComposite(af_composite,
					page.getToolkit());
			initAntiAFparameters();
		} 
		af_type_composite.layout(true);
		af_type_composite.redraw();
		af_composite.layout(true);
		af_composite.redraw();
		// composite.layout(true);
		// composite.redraw();
		page.getForm().reflow(true);
	
	}

	/**
	 * Initialize the Affinity Rules
	 */
	private void initAFparameters() {
		af_component.setItems(getAllPackages());
		cleanComponentAFRuleDetails();
		enableComponentAFRuleDetails(false);
		af_sec.setItems(generateSectionNames(
				manifest.getVirtualMachineDescriptionSection()
						.getAffinityRules().length, AFFINITY_RULE));
		cleanAFRuleDetails();
		enableAFRuleDetails(false);
	}
	
	/**
	 * Initialize the Affinity Rules
	 */
	private void initAntiAFparameters() {
		anti_af_component.setItems(getAllPackages());
		cleanComponentAntiAFRuleDetails();
		enableComponentAntiAFRuleDetails(false);
		anti_af_sec.setItems(generateSectionNames(
				manifest.getVirtualMachineDescriptionSection()
						.getAntiAffinityRules().length, ANTI_AFFINITY_RULE));
		cleanAntiAFRuleDetails();
		enableAntiAFRuleDetails(false);
	}

	/** 
	 * Enable/Disable the affinity rules details
	 * 
	 * @param b True for enabling, false for disabling
	 */
	private void enableAFRuleDetails(boolean b) {
		af_scope.setEnabled(b);
		af_level.setEnabled(b);
		af_but.setEnabled(b);
	}
	
	/** 
	 * Enable/Disable the affinity rules details
	 * 
	 * @param b True for enabling, false for disabling
	 */
	private void enableComponentAFRuleDetails(boolean b) {
		af_component_level.setEnabled(b);
		af_component_but.setEnabled(b);
	}
	
	/** 
	 * Enable/Disable the anti-affinity rules details
	 * 
	 * @param b True for enabling, false for disabling
	 */
	private void enableAntiAFRuleDetails(boolean b) {
		anti_af_scope.setEnabled(b);
		anti_af_level.setEnabled(b);
		anti_af_but.setEnabled(b);
	}
	
	/** 
	 * Enable/Disable the affinity intra component rules details
	 * 
	 * @param b True for enabling, false for disabling
	 */
	private void enableComponentAntiAFRuleDetails(boolean b) {
		anti_af_component_level.setEnabled(b);
		anti_af_component_but.setEnabled(b);
	}

	/**
	 * Clean the affinity rule details
	 */
	public void cleanAFRuleDetails() {
		String[] totalEls = getAllPackages();
		af_scope.reset(totalEls);
		af_level.setText("");
	}
	
	/**
	 * Clean the anti-affinity rule details
	 */
	public void cleanAntiAFRuleDetails() {
		String[] totalEls = getAllPackages();
		anti_af_scope.reset(totalEls);
		anti_af_level.setText("");
	}
	
	/**
	 * Clean the affinity rule details
	 */
	public void cleanComponentAFRuleDetails() {
		af_level.setText("");
	}
	
	/**
	 * Clean the anti-affinity rule details
	 */
	public void cleanComponentAntiAFRuleDetails() {
		anti_af_level.setText("");
	}

	/** 
	 * Load an affinity rule
	 * 
	 * @param number Number of the affinity rule
	 */
	private void loadAffinityRule(int number) {
		String[] totalEls = getAllPackages();
		String[] selectedEls = ManifestCreation.getPackageNames(manifest
				.getVirtualMachineDescriptionSection().getAffinityRule(number)
				.getScope().getComponentIdArray());
		af_scope.setPackagesLists(totalEls, selectedEls);
		if (manifest.getVirtualMachineDescriptionSection()
				.getAffinityRule(number).getAffinityConstraints() != null)
			af_level.setText(manifest.getVirtualMachineDescriptionSection()
					.getAffinityRule(number).getAffinityConstraints());
		else
			af_level.setText("");
		enableAFRuleDetails(true);
	}
	
	/** 
	 * Load an affinity rule
	 * 
	 * @param number Number of the affinity rule
	 */
	private void loadAntiAffinityRule(int number) {
		String[] totalEls = getAllPackages();
		String[] selectedEls = ManifestCreation.getPackageNames(manifest
				.getVirtualMachineDescriptionSection().getAntiAffinityRule(number)
				.getScope().getComponentIdArray());
		anti_af_scope.setPackagesLists(totalEls, selectedEls);
		if (manifest.getVirtualMachineDescriptionSection()
				.getAntiAffinityRule(number).getAntiAffinityConstraints() != null)
			anti_af_level.setText(manifest.getVirtualMachineDescriptionSection()
					.getAntiAffinityRule(number).getAntiAffinityConstraints());
		else
			anti_af_level.setText("");
		enableAntiAFRuleDetails(true);
	}
	
	/** 
	 * Load an affinity rule
	 * 
	 * @param number Number of the affinity rule
	 */
	private void loadComponentAffinityRule(int number) {
		String component = ManifestCreation.generateManifestName(
				af_component.getItem(number));
		if (manifest.getVirtualMachineDescriptionSection().getVirtualMachineComponentById(component)
				.getAffinityConstraints() != null)
			af_component_level.setText(manifest.getVirtualMachineDescriptionSection()
					.getVirtualMachineComponentById(component).getAffinityConstraints());
		else
			af_component_level.setText("");
		enableComponentAFRuleDetails(true);
	}
	
	/** 
	 * Load an affinity rule
	 * 
	 * @param number Number of the affinity rule
	 */
	private void loadComponentAntiAffinityRule(int number) {
		String component = ManifestCreation.generateManifestName(
				anti_af_component.getItem(number));
		if (manifest.getVirtualMachineDescriptionSection().getVirtualMachineComponentById(component)
				.getAntiAffinityConstraints() != null)
			anti_af_component_level.setText(manifest.getVirtualMachineDescriptionSection()
					.getVirtualMachineComponentById(component).getAntiAffinityConstraints());
		else
			anti_af_component_level.setText("");
		enableComponentAntiAFRuleDetails(true);
	}

	/**
	 * Create a new affinity rule
	 */
	private void newAffinityRule() {
		af_sec.setText(AFFINITY_RULE + (af_sec.getItems().length + 1));
		cleanAFRuleDetails();
		enableAFRuleDetails(true);
	}
	
	/**
	 * Create a new affinity rule
	 */
	private void newAntiAffinityRule() {
		anti_af_sec.setText(ANTI_AFFINITY_RULE + (af_sec.getItems().length + 1));
		cleanAntiAFRuleDetails();
		enableAntiAFRuleDetails(true);
	}

	/**
	 * Save the affinity rule
	 * @throws Exception
	 */
	public void saveAffinityRule() throws Exception {
		int number = getNumberFromName(AFFINITY_RULE, af_sec.getText().trim());
		if (number >= af_sec.getItems().length) {
			af_sec.add(AFFINITY_RULE + (af_sec.getItems().length + 1));
			if (manifest == null) {
				generateNewManifest();
			}
			manifest.getVirtualMachineDescriptionSection().addNewAffinityRule(
					ManifestCreation.generateManifestNames(af_scope.getSelectedPackages()),
					af_level.getText().trim());
		} else {
			AffinityRule rule = manifest.getVirtualMachineDescriptionSection()
					.getAffinityRule(af_sec.getSelectionIndex());
			rule.setAffinityConstraints(af_level.getText().trim());
			rule.getScope().setComponentIdArray(
					ManifestCreation.generateManifestNames(af_scope.getSelectedPackages()));
		}
		writeManifestToFile();
	}
	
	/**
	 * Save the component affinity rule
	 * @throws Exception
	 */
	public void saveComponentAffinityRule() throws Exception {
		String component = ManifestCreation.generateManifestName(
				af_component.getItem(af_component.getSelectionIndex()));
		if (manifest == null) {
			generateNewManifest();
		}
		manifest.getVirtualMachineDescriptionSection().getVirtualMachineComponentById(component).
			setAffinityConstraints(af_component_level.getText().trim());
		writeManifestToFile();
	}
	
	/**
	 * Save the anti-affinity rule
	 * @throws Exception
	 */
	public void saveAntiAffinityRule() throws Exception {
		int number = getNumberFromName(ANTI_AFFINITY_RULE, anti_af_sec.getText().trim());
		if (number >= anti_af_sec.getItems().length) {
			anti_af_sec.add(ANTI_AFFINITY_RULE + (anti_af_sec.getItems().length + 1));
			if (manifest == null) {
				generateNewManifest();
			}
			manifest.getVirtualMachineDescriptionSection().addNewAntiAffinityRule(
					ManifestCreation.generateManifestNames(anti_af_scope.getSelectedPackages()),
					anti_af_level.getText().trim());
		} else {
			AntiAffinityRule rule = manifest.getVirtualMachineDescriptionSection()
					.getAntiAffinityRule(anti_af_sec.getSelectionIndex());
			rule.setAntiAffinityConstraints(anti_af_level.getText().trim());
			rule.getScope().setComponentIdArray(
					ManifestCreation.generateManifestNames(anti_af_scope.getSelectedPackages()));
		}
		writeManifestToFile();
	}
	
	/**
	 * Save the component anti-affinity rule
	 * @throws Exception
	 */
	public void saveComponentAntiAffinityRule() throws Exception {
		String component = ManifestCreation.generateManifestName(
				anti_af_component.getItem(anti_af_component.getSelectionIndex()));
		if (manifest == null) {
			generateNewManifest();
		}
		manifest.getVirtualMachineDescriptionSection().getVirtualMachineComponentById(component).
			setAntiAffinityConstraints(anti_af_component_level.getText().trim());
		writeManifestToFile();
	}

	//************************* TREC Section ***********************************
	
	/**
	 * Creates the TREC section in the specified composite
	 * @param composite Parent's composite
	 */
	private void createTRECSection(Composite composite) {
		trec_section = page.getToolkit().createSection(composite,
				Section.TWISTIE | Section.DESCRIPTION);
		trec_section.setText("TREC Factors");
		trec_section.setDescription("Define the TREC parameters " +
				"for the service deployment");
		GridData rd = new GridData(GridData.HORIZONTAL_ALIGN_FILL
				| GridData.VERTICAL_ALIGN_BEGINNING);
		rd.grabExcessHorizontalSpace = true;
		trec_section.setLayoutData(rd);
		trec_section.setLayout(new GridLayout(1, true));
		trec_composite = page.getToolkit().createComposite(trec_section,
				SWT.NONE);
		rd = new GridData(GridData.HORIZONTAL_ALIGN_FILL
				| GridData.VERTICAL_ALIGN_BEGINNING);
		rd.grabExcessHorizontalSpace = true;
		trec_composite.setLayoutData(rd);
		trec_composite.setLayout(new GridLayout(1, false));
		trec_type_composite = page.getToolkit().createComposite(trec_composite,
				SWT.NONE);
		rd = new GridData(GridData.HORIZONTAL_ALIGN_FILL
				| GridData.VERTICAL_ALIGN_BEGINNING);
		rd.heightHint = 0;
		rd.grabExcessHorizontalSpace = true;
		trec_type_composite.setLayoutData(rd);
		trec_tab = new CTabFolder(trec_composite, SWT.FLAT | SWT.TOP);
		page.getToolkit().adapt(trec_tab, true, true);
		GridData gd = new GridData(GridData.HORIZONTAL_ALIGN_FILL
				| GridData.VERTICAL_ALIGN_BEGINNING);
		gd.heightHint = 0;
		trec_tab.setLayoutData(gd);
		Color selectedColor = page.getToolkit().getColors()
				.getColor(FormColors.SEPARATOR);
		trec_tab.setSelectionBackground(new Color[] { selectedColor,
				page.getToolkit().getColors().getBackground() },
				new int[] { 50 });
		page.getToolkit().paintBordersFor(trec_tab);
		createTRECTabs(trec_tab, trec_composite, page.getToolkit());
		trec_tab.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				updateTRECSelection();
			}
	
			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {
				updateTRECSelection();
			}
		});
	}
	
	/**
	 * Create the Tab for the different TREC parameters
	 * @param trec_tab Tab component
	 * @param trec_composite TREC parent composite
	 * @param toolkit Form Editor toolkit
	 */
	private void createTRECTabs(CTabFolder trec_tab, Composite trec_composite,
			FormToolkit toolkit) {
		CTabItem trust_item = new CTabItem(trec_tab, SWT.NULL);
		trust_item.setText("Trust");
		trust_item.setData(TRUST);
	
		CTabItem risk_item = new CTabItem(trec_tab, SWT.NULL);
		risk_item.setText("Risk");
		risk_item.setData(RISK);
	
		CTabItem eco_item = new CTabItem(trec_tab, SWT.NULL);
		eco_item.setText("Eco-efficiency");
		eco_item.setData(ECO);
	
		CTabItem cost_item = new CTabItem(trec_tab, SWT.NULL);
		cost_item.setText("Cost");
		cost_item.setData(COST);
	}

	/**
	 * Update the TREC section according the selected TREC parameter
	 */
	private void updateTRECSelection() {
		CTabItem item = trec_tab.getSelection();
		if (trec_type_composite != null) {
			trec_type_composite.dispose();
		}
		if (((String) item.getData()).equals(TRUST)) {
			trec_type_composite = createTRUSTComposite(trec_composite,
					page.getToolkit());
			initTRUSTparameters();
		} else if (((String) item.getData()).equals(RISK)) {
			trec_type_composite = createRISKComposite(trec_composite,
					page.getToolkit());
			initRISKparameters();
		} else if (((String) item.getData()).equals(ECO)) {
			trec_type_composite = createECOComposite(trec_composite,
					page.getToolkit());
			initECOparameters();
		} else if (((String) item.getData()).equals(COST)) {
			trec_type_composite = createCOSTComposite(trec_composite,
					page.getToolkit());
			initCOSTparameters();
		}
		trec_type_composite.layout(true);
		trec_type_composite.redraw();
		trec_composite.layout(true);
		trec_composite.redraw();
		// composite.layout(true);
		// composite.redraw();
		page.getForm().reflow(true);
	
	}
	
	// TRUST PARAMETERS
	
	/** Create the Trust tab as a composite
	 * @param trec_composite parent TREC composite
	 * @param toolkit Form editor toolkit
	 * @return Trust composite
	 */
	private Composite createTRUSTComposite(Composite trec_composite,
			FormToolkit toolkit) {
		Composite trust_comp = toolkit.createComposite(trec_composite,
				SWT.BORDER);
		GridData rd = new GridData(GridData.FILL_HORIZONTAL
				| GridData.VERTICAL_ALIGN_BEGINNING);
		rd.grabExcessHorizontalSpace = true;
		trust_comp.setLayoutData(rd);
		trust_comp.setLayout(new GridLayout(1, false));
		// Combo part
		Composite combo_comp = toolkit.createComposite(trust_comp, SWT.BORDER);
		rd = new GridData(GridData.FILL_HORIZONTAL
				| GridData.VERTICAL_ALIGN_BEGINNING);
		rd.grabExcessHorizontalSpace = true;
		combo_comp.setLayoutData(rd);
		combo_comp.setLayout(new GridLayout(3, false));
		toolkit.createLabel(combo_comp, "Trust Section");
		trust_sec = new Combo(combo_comp, SWT.NONE);
		rd = new GridData(GridData.FILL_HORIZONTAL);
		rd.grabExcessHorizontalSpace = true;
		rd.minimumWidth = 300;
		trust_sec.setLayoutData(rd);
		trust_sec.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				loadTrustSection(trust_sec.getSelectionIndex());
			}
		});
		Button new_btn = toolkit.createButton(combo_comp, "New", SWT.NORMAL);
		new_btn.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {
				newTrustSection();
			}
	
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				newTrustSection();
			}
		});
	
		// Details part
		Group details_comp = new Group(trust_comp, SWT.BORDER);
		rd = new GridData(GridData.FILL_HORIZONTAL
				| GridData.VERTICAL_ALIGN_BEGINNING);
		rd.grabExcessHorizontalSpace = true;
		details_comp.setLayoutData(rd);
		details_comp.setLayout(new GridLayout(2, false));
		details_comp.setText("Trust Details");
		toolkit.createLabel(details_comp, "Trust Level", SWT.NONE);
		trust_level = toolkit.createText(details_comp, "", SWT.BORDER);
		rd = new GridData(GridData.FILL_HORIZONTAL);
		rd.grabExcessHorizontalSpace = true;
		rd.minimumWidth = 350;
		trust_level.setLayoutData(rd);
	
		// Scope part
		//toolkit.createLabel(trust_comp, "Section Scope");
		trust_scope = new ScopedListsComposite(toolkit, "Components Scope");
		trust_scope.createComposite(trust_comp);
	
		// Buttons part
		trust_but = new SaveResetButtonComposite(getShell(),page.getToolkit(),TRUST, this);
		trust_but.createComposite(trust_comp);
	
		return trust_comp;
	}

	/**
	 * Initialize the TRUST parameters
	 */
	private void initTRUSTparameters() {
		// Update Combo
		trust_sec.setItems(generateSectionNames(manifest.getTRECSection()
				.getTrustSectionArray().length, TRUST_SECTION));
		// Clean details
		cleanTrustDetails();
		enableTrustDetails(false);
	}

	/**
	 * Load a trust description
	 * 
	 * @param number Number of the Trust description
	 */
	private void loadTrustSection(int number) {
		String[] totalEls = getAllPackages();
		String[] selectedEls = ManifestCreation.getPackageNames(manifest.getTRECSection()
				.getTrustSectionArray(number).getScope().getComponentIdArray());
		trust_scope.setPackagesLists(totalEls, selectedEls);
		if (manifest.getTRECSection().getTrustSectionArray(number)
				.getMinimumTrustLevel() > 0)
			trust_level.setText(Integer.toString(manifest.getTRECSection()
					.getTrustSectionArray(number).getMinimumTrustLevel()));
		else
			trust_level.setText("");
		enableTrustDetails(true);
	}

	/**
	 * Create a new trust description
	 */
	private void newTrustSection() {
		trust_sec.setText(TRUST_SECTION + (trust_sec.getItems().length + 1));
		cleanTrustDetails();
		enableTrustDetails(true);
	}
	
	/** 
	 * Enable/disable trust description details
	 * 
	 * @param b True for enabling, False for disabling
	 */
	private void enableTrustDetails(boolean b) {
		trust_scope.setEnabled(b);
		trust_level.setEnabled(b);
		trust_but.setEnabled(b);
	}

	/** 
	 * Save the trust description
	 * 
	 * @throws Exception
	 */
	public void saveTrustSection() throws Exception {
		int number = getNumberFromName(TRUST_SECTION, trust_sec.getText()
				.trim());
		TrustSection section;
		if (number >= trust_sec.getItems().length) {
			trust_sec.add(TRUST_SECTION + (trust_sec.getItems().length + 1));
			if (manifest == null) {
				generateNewManifest();
			}
			section = manifest.getTRECSection().addNewTrustSection(
					ManifestCreation.generateManifestNames(trust_scope.getSelectedPackages()));
		} else {
			section = manifest.getTRECSection().getTrustSectionArray(
					trust_sec.getSelectionIndex());
			section.getScope().setComponentIdArray(
					ManifestCreation.generateManifestNames(trust_scope.getSelectedPackages()));
		}
		if (trust_level.getText() != null
				&& trust_level.getText().trim().length() > 0)
			section.setMinimumTrustLevel(Integer.parseInt(trust_level.getText()
					.trim()));
		writeManifestToFile();
	}

	/**
	 * Clean the selected trust description
	 */
	public void cleanTrustDetails() {
		String[] totalEls = getAllPackages();
		trust_scope.reset(totalEls);
		trust_level.setText("");
	}

	//RISK PARAMETERS
	
	/** Create the Risk tab
	 * @param trec_composite Parent's TREC composite
	 * @param toolkit Form editor toolkit
	 * @return Risk composite
	 */
	private Composite createRISKComposite(Composite trec_composite,
			FormToolkit toolkit) {
		Composite risk_comp = toolkit.createComposite(trec_composite,
				SWT.BORDER);
		GridData rd = new GridData(GridData.FILL_HORIZONTAL
				| GridData.VERTICAL_ALIGN_BEGINNING);
		rd.grabExcessHorizontalSpace = true;
		risk_comp.setLayoutData(rd);
		risk_comp.setLayout(new GridLayout(1, false));
	
		// Combo part
		Composite combo_comp = toolkit.createComposite(risk_comp, SWT.BORDER);
		rd = new GridData(GridData.FILL_HORIZONTAL
				| GridData.VERTICAL_ALIGN_BEGINNING);
		rd.grabExcessHorizontalSpace = true;
		combo_comp.setLayoutData(rd);
		combo_comp.setLayout(new GridLayout(3, false));
		toolkit.createLabel(combo_comp, "Risk Section");
		risk_sec = new Combo(combo_comp, SWT.NONE);
		rd = new GridData(GridData.FILL_HORIZONTAL);
		rd.grabExcessHorizontalSpace = true;
		rd.minimumWidth = 300;
		risk_sec.setLayoutData(rd);
		risk_sec.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				loadRiskSection(risk_sec.getSelectionIndex());
			}
		});
		Button new_risk = toolkit.createButton(combo_comp, "New", SWT.NORMAL);
		new_risk.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {
				newRiskSection();
			}
	
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				newRiskSection();
			}
		});
	
		// Details part
		Group details_comp = new Group(risk_comp, SWT.BORDER);
		rd = new GridData(GridData.FILL_HORIZONTAL
				| GridData.VERTICAL_ALIGN_BEGINNING);
		rd.grabExcessHorizontalSpace = true;
		details_comp.setLayoutData(rd);
		details_comp.setLayout(new GridLayout(2, false));
		details_comp.setText("Risk Details");
		toolkit.createLabel(details_comp, "Risk Level");
		risk_level = toolkit.createText(details_comp, "", SWT.BORDER);
		rd = new GridData(GridData.FILL_HORIZONTAL);
		rd.grabExcessHorizontalSpace = true;
		rd.minimumWidth = 350;
		risk_level.setLayoutData(rd);
		toolkit.createLabel(details_comp, "Availability");
		risk_avail = new KeyValueTableComposite(page.getSite().getShell(),
				toolkit, "Interval", "Availability", true, false);
		risk_avail.createComposite(details_comp);
	
		// Scope part
		//toolkit.createLabel(risk_comp, "Section Scope");
		risk_scope = new ScopedListsComposite(toolkit, "Components Scope");
		risk_scope.createComposite(risk_comp);
	
		// Buttons part
		risk_but = new SaveResetButtonComposite(getShell(),page.getToolkit(),RISK,this);
		risk_but.createComposite(risk_comp);
	
		return risk_comp;
	}
	
	/**
	 * Initialize the risk descriptions
	 */
	private void initRISKparameters() {
		// Update Combo
		risk_sec.setItems(generateSectionNames(manifest.getTRECSection()
				.getRiskSectionArray().length, RISK_SECTION));
		// Clean details
		cleanRiskDetails();
		enableRiskDetails(false);
	}

	/**
	 * Load the selected Risk description
	 * 
	 * @param number Number of the risk description
	 */
	private void loadRiskSection(int number) {
		// load added list
		String[] totalEls = getAllPackages();
		String[] selectedEls = ManifestCreation.getPackageNames(manifest.getTRECSection()
				.getRiskSectionArray(number).getScope().getComponentIdArray());
		risk_scope.setPackagesLists(totalEls, selectedEls);
		if (manifest.getTRECSection().getRiskSectionArray(number)
				.getRiskLevel() > 0)
			risk_level.setText(Integer.toString(manifest.getTRECSection()
					.getRiskSectionArray(number).getRiskLevel()));
		else
			risk_level.setText("");
		enableRiskDetails(true);
		readAvailability(manifest.getTRECSection().getRiskSectionArray(number));
		risk_avail.enableAdditions();
	}

	/**
	 * Create a new risk description
	 */
	private void newRiskSection() {
		risk_sec.setText(RISK_SECTION + (risk_sec.getItems().length + 1));
		cleanRiskDetails();
		enableRiskDetails(true);
		risk_avail.enableAdditions();
	}

	/**
	 * Enable/Disable the details of the risk description
	 * @param b True for enabling, false for disabling
	 */
	private void enableRiskDetails(boolean b) {
		risk_scope.setEnabled(b);
		risk_level.setEnabled(b);
		risk_avail.setEnabled(b);
		risk_but.setEnabled(b);
	}

	/**
	 * Read the availability section of the Trust description
	 * @param section Risk section of the service manifest
	 */
	private void readAvailability(RiskSection section) {
		Map<String, String> map = new HashMap<String, String>();
		Availability[] avails = section.getAvailabilityArray();
		if (avails != null && avails.length > 0) {
			for (Availability av : avails) {
				map.put(av.getAssessmentInterval(),
						Double.toString(av.getValue()));
			}
		}
		risk_avail.setKeyValueMap(map);
	}

	/**
	 * Update the availability parameter of an risk description
	 * @param section Risk section of the service manifest
	 */
	private void updateAvailability(RiskSection section) {
		Map<String, String> map = risk_avail.getKeyValueMap();
		int size = section.getAvailabilityArray().length;
		int i = 0;
		for (Entry<String, String> e : map.entrySet()) {
			if (i < size) {
				log.debug("Rewriting avilabitlty (" + i + " of "
						+ size + ")");
				section.getAvailabilityArray(i).setAssessmentInterval(
						e.getKey());
				section.getAvailabilityArray(i).setValue(
						Double.parseDouble(e.getValue()));
			} else {
				log.debug("Writing new avilabitlty (" + i + ")");
				section.addNewAvailability(e.getKey(),
						Double.parseDouble(e.getValue()));
			}
			i++;
		}
	}

	/**
	 * Save the current risk description
	 * 
	 * @throws Exception
	 */
	public void saveRiskSection() throws Exception {
		int number = getNumberFromName(RISK_SECTION, risk_sec.getText().trim());
		RiskSection section;
		if (number >= risk_sec.getItems().length) {
			risk_sec.add(RISK_SECTION + (risk_sec.getItems().length + 1));
			if (manifest == null) {
				generateNewManifest();
			}
			section = manifest.getTRECSection().addNewRiskSection(
					ManifestCreation.generateManifestNames(risk_scope.getSelectedPackages()));
		} else {
			section = manifest.getTRECSection().getRiskSectionArray(
					risk_sec.getSelectionIndex());
			section.getScope().setComponentIdArray(
					ManifestCreation.generateManifestNames(risk_scope.getSelectedPackages()));
		}
		if (risk_level.getText() != null
				&& risk_level.getText().trim().length() > 0)
			section.setRiskLevel(Integer.parseInt(risk_level.getText().trim()));
		// Set availability array
		updateAvailability(section);
		writeManifestToFile();
	}

	/**
	 * Clean the details of a risk description
	 */
	public void cleanRiskDetails() {
		String[] totalEls = getAllPackages();
		risk_scope.reset(totalEls);
		risk_level.setText("");
		risk_avail.reset();
	
	}
	// ECO PARAMETERS
	
	/**
	 * Create the ECO tab as a composite
	 * 
	 * @param trec_composite Parent's TREC composite
	 * @param toolkit Form Editor toolkit
	 * @return ECO tab composite
	 */
	private Composite createECOComposite(Composite trec_composite,
			FormToolkit toolkit) {
		Composite eco_comp = toolkit
				.createComposite(trec_composite, SWT.BORDER);
		GridData rd = new GridData(GridData.FILL_HORIZONTAL
				| GridData.VERTICAL_ALIGN_BEGINNING);
		rd.grabExcessHorizontalSpace = true;
		eco_comp.setLayoutData(rd);
		eco_comp.setLayout(new GridLayout(1, false));
	
		// Combo part
		Composite combo_comp = toolkit.createComposite(eco_comp, SWT.BORDER);
		rd = new GridData(GridData.FILL_HORIZONTAL
				| GridData.VERTICAL_ALIGN_BEGINNING);
		rd.grabExcessHorizontalSpace = true;
		combo_comp.setLayoutData(rd);
		combo_comp.setLayout(new GridLayout(3, false));
		toolkit.createLabel(combo_comp, "Eco-Efficiency Section");
		eco_sec = new Combo(combo_comp, SWT.NONE);
		rd = new GridData(GridData.FILL_HORIZONTAL);
		rd.grabExcessHorizontalSpace = true;
		rd.minimumWidth = 300;
		eco_sec.setLayoutData(rd);
		eco_sec.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				loadEcoSection(eco_sec.getSelectionIndex());
			}
		});
		Button new_btn = toolkit.createButton(combo_comp, "New", SWT.NORMAL);
		new_btn.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {
				newEcoSection();
			}
	
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				newEcoSection();
			}
		});
		Group eco_details_comp = new Group(eco_comp, SWT.BORDER);
		rd = new GridData(GridData.FILL_HORIZONTAL
				| GridData.VERTICAL_ALIGN_BEGINNING);
		rd.grabExcessHorizontalSpace = true;
		eco_details_comp.setLayoutData(rd);
		eco_details_comp.setLayout(new GridLayout(1, false));
		eco_details_comp.setText("Eco-Efficiency Details");
		Group details_comp = new Group(eco_details_comp, SWT.BORDER);
		rd = new GridData(GridData.FILL_HORIZONTAL
				| GridData.VERTICAL_ALIGN_BEGINNING);
		rd.grabExcessHorizontalSpace = true;
		details_comp.setLayoutData(rd);
		details_comp.setLayout(new GridLayout(2, false));
		details_comp.setText("Provider Eco-Efficiency Certifications");
		toolkit.createLabel(details_comp, "LEED Certification");
		LEED_cert = new Combo(details_comp, SWT.NONE);
		rd = new GridData(GridData.FILL_HORIZONTAL);
		rd.grabExcessHorizontalSpace = true;
		rd.minimumWidth = 350;
		LEED_cert.setLayoutData(rd);
		LEED_cert.setItems(new String[] { "NotRequired", "Certified", "Silver",
				"Gold", "Platinum" });
		toolkit.createLabel(details_comp, "BREEAM Certification");
		BREEAM_cert = new Combo(details_comp, SWT.NONE);
		rd = new GridData(GridData.FILL_HORIZONTAL);
		rd.grabExcessHorizontalSpace = true;
		rd.minimumWidth = 350;
		BREEAM_cert.setLayoutData(rd);
		BREEAM_cert.setItems(new String[] { "NotRequired", "Pass", "Good",
				"VeryGood", "Excellent", "Outstanding" });
		toolkit.createLabel(details_comp, "EU CoC compliant");
		eu_coc = toolkit.createButton(details_comp, "", SWT.CHECK);
		toolkit.createLabel(details_comp, "Energy Star Rating");
		energyStar = toolkit.createText(details_comp, "", SWT.BORDER);
		rd = new GridData(GridData.FILL_HORIZONTAL);
		rd.grabExcessHorizontalSpace = true;
		rd.minimumWidth = 350;
		energyStar.setLayoutData(rd);
		toolkit.createLabel(details_comp, "ISO 14000 Certification");
		ISO_cert =  toolkit.createButton(details_comp, "", SWT.CHECK);
		toolkit.createLabel(details_comp, "Green Star Rating");
		greenStar = new Combo(details_comp, SWT.NONE);
		rd = new GridData(GridData.FILL_HORIZONTAL);
		rd.grabExcessHorizontalSpace = true;
		rd.minimumWidth = 350;
		greenStar.setLayoutData(rd); 
		greenStar.setItems(new String[] { "No", "4", "5", "6"});
		toolkit.createLabel(details_comp, "CASBEE Certification");
		CASBEE_cert = new Combo(details_comp, SWT.NONE);
		rd = new GridData(GridData.FILL_HORIZONTAL);
		rd.grabExcessHorizontalSpace = true;
		rd.minimumWidth = 350;
		CASBEE_cert.setLayoutData(rd);
		CASBEE_cert.setItems(new String[] { "No", "C", "B-", "B+", "A", "S" });
		Group ener_metric_comp = new Group(eco_details_comp, SWT.BORDER);
		rd = new GridData(GridData.FILL_HORIZONTAL
				| GridData.VERTICAL_ALIGN_BEGINNING);
		rd.grabExcessHorizontalSpace = true;
		ener_metric_comp.setLayoutData(rd);
		ener_metric_comp.setLayout(new GridLayout(2, false));
		ener_metric_comp.setText("Service Energy-Efficiency Metric");
		toolkit.createLabel(ener_metric_comp, "Threshold");
		ener_thres = toolkit.createText(ener_metric_comp, "NotSpecified", SWT.BORDER);
		rd = new GridData(GridData.FILL_HORIZONTAL);
		rd.grabExcessHorizontalSpace = true;
		rd.minimumWidth = 350;
		ener_thres.setLayoutData(rd);
		toolkit.createLabel(ener_metric_comp, "Type");
		ener_type = new Combo(ener_metric_comp, SWT.NONE);
		rd = new GridData(GridData.FILL_HORIZONTAL);
		rd.grabExcessHorizontalSpace = true;
		rd.minimumWidth = 350;
		ener_type.setLayoutData(rd);
		ener_type.setItems(new String[] { "Soft", "Hard"});
		toolkit.createLabel(ener_metric_comp, "Magnitude");
		ener_mag = toolkit.createText(ener_metric_comp, "NA", SWT.BORDER);
		rd = new GridData(GridData.FILL_HORIZONTAL);
		rd.grabExcessHorizontalSpace = true;
		rd.minimumWidth = 350;
		ener_mag.setLayoutData(rd);
		toolkit.createLabel(ener_metric_comp, "Time");
		ener_time = toolkit.createText(ener_metric_comp, "NA", SWT.BORDER);
		rd = new GridData(GridData.FILL_HORIZONTAL);
		rd.grabExcessHorizontalSpace = true;
		rd.minimumWidth = 350;
		ener_time.setLayoutData(rd);
		Group eco_metric_comp = new Group(eco_details_comp, SWT.BORDER);
		rd = new GridData(GridData.FILL_HORIZONTAL
				| GridData.VERTICAL_ALIGN_BEGINNING);
		rd.grabExcessHorizontalSpace = true;
		eco_metric_comp.setLayoutData(rd);
		eco_metric_comp.setLayout(new GridLayout(2, false));
		eco_metric_comp.setText("Service Ecological-Efficiency Metric");
		toolkit.createLabel(eco_metric_comp, "Threshold");
		eco_thres = toolkit.createText(eco_metric_comp, "NotSpecified", SWT.BORDER);
		rd = new GridData(GridData.FILL_HORIZONTAL);
		rd.grabExcessHorizontalSpace = true;
		rd.minimumWidth = 350;
		eco_thres.setLayoutData(rd);
		toolkit.createLabel(eco_metric_comp, "Type");
		eco_type = new Combo(eco_metric_comp, SWT.NONE);
		rd = new GridData(GridData.FILL_HORIZONTAL);
		rd.grabExcessHorizontalSpace = true;
		rd.minimumWidth = 350;
		eco_type.setLayoutData(rd);
		eco_type.setItems(new String[] { "Soft", "Hard"});
		toolkit.createLabel(eco_metric_comp, "Magnitude");
		eco_mag = toolkit.createText(eco_metric_comp, "NA", SWT.BORDER);
		rd = new GridData(GridData.FILL_HORIZONTAL);
		rd.grabExcessHorizontalSpace = true;
		rd.minimumWidth = 350;
		eco_mag.setLayoutData(rd);
		toolkit.createLabel(eco_metric_comp, "Time");
		eco_time = toolkit.createText(eco_metric_comp, "NA", SWT.BORDER);
		rd = new GridData(GridData.FILL_HORIZONTAL);
		rd.grabExcessHorizontalSpace = true;
		rd.minimumWidth = 350;
		eco_time.setLayoutData(rd);
		
		// Scope part
		//toolkit.createLabel(eco_comp, "Section Scope");
		eco_scope = new ScopedListsComposite(toolkit, "Components scope");
		eco_scope.createComposite(eco_comp);
	
		// Buttons part
		eco_but = new SaveResetButtonComposite(getShell(),page.getToolkit(),ECO, this);
		eco_but.createComposite(eco_comp);
	
		return eco_comp;
	}

	/**
	 * Initialize the Eco descriptions
	 */
	private void initECOparameters() {
		eco_sec.setItems(generateSectionNames(manifest.getTRECSection()
				.getEcoEfficiencySectionArray().length, ECO_SECTION));
		cleanEcoDetails();
		enableEcoDetails(false);
	}

	/**
	 * Load a certain Eco description
	 * 
	 * @param number Number of eco description
	 */
	private void loadEcoSection(int number) {
		String[] totalEls = getAllPackages();
		String[] selectedEls = ManifestCreation.getPackageNames(manifest.getTRECSection()
				.getEcoEfficiencySectionArray(number).getScope()
				.getComponentIdArray());
		eco_scope.setPackagesLists(totalEls, selectedEls);
		if (manifest.getTRECSection().getEcoEfficiencySectionArray(number)
				.getBREEAMCertification() != null)
			BREEAM_cert.setText(manifest.getTRECSection()
					.getEcoEfficiencySectionArray(number)
					.getBREEAMCertification());
		else
			BREEAM_cert.setText("");
		if (manifest.getTRECSection().getEcoEfficiencySectionArray(number)
				.getLEEDCertification() != null)
			LEED_cert.setText(manifest.getTRECSection()
					.getEcoEfficiencySectionArray(number)
					.getLEEDCertification());
		else
			LEED_cert.setText("");
		eu_coc.setSelection(manifest.getTRECSection()
				.getEcoEfficiencySectionArray(number).getEuCoCCompliant());
		if (manifest.getTRECSection().getEcoEfficiencySectionArray(number)
				.getEnergyStarRating() != null)
			energyStar.setText(manifest.getTRECSection()
					.getEcoEfficiencySectionArray(number).getEnergyStarRating()
					.toString());
		else
			energyStar.setText("");
		//Y3 SM Added
		ISO_cert.setSelection(manifest.getTRECSection()
				.getEcoEfficiencySectionArray(number).getISO14000().equals("ISO14001-Compliant"));
		if (manifest.getTRECSection().getEcoEfficiencySectionArray(number)
				.getEnergyStarRating() != null)
			greenStar.setText(manifest.getTRECSection()
					.getEcoEfficiencySectionArray(number).getGreenStar());
		else
			greenStar.setText("");
		if (manifest.getTRECSection().getEcoEfficiencySectionArray(number)
				.getEnergyStarRating() != null)
			CASBEE_cert.setText(manifest.getTRECSection()
					.getEcoEfficiencySectionArray(number).getCASBEE());
		else
			CASBEE_cert.setText("");
		
		//Energy Efficiency
		if (manifest.getTRECSection().getEcoEfficiencySectionArray(number).getEcoMetric(ENERGY_EFFICIENCY) != null){
			EcoMetric ecoMetric = manifest.getTRECSection().getEcoEfficiencySectionArray(number).
					getEcoMetric(ENERGY_EFFICIENCY);
			ener_thres.setText(ecoMetric.getThresholdValue().toString());
			if (ecoMetric.getSLAType().equalsIgnoreCase("Hard")){
				ener_type.setText(ecoMetric.getSLAType());
				ener_mag.setText(ecoMetric.getMagnitudePenalty().toString());
				ener_mag.setEnabled(true);
				ener_time.setText(ecoMetric.getTimePenalty().toString());
				ener_time.setEnabled(true);
			}else{
				ener_type.setText("Soft");
				ener_mag.setText("NA");
				ener_mag.setEnabled(false);
				ener_time.setText("NA");
				ener_time.setEnabled(false);
			}		
		}else{
			ener_thres.setText("NotSpecified");
			ener_type.setText("Soft");
			ener_mag.setText("NA");
			ener_mag.setEnabled(false);
			ener_time.setText("NA");
			ener_time.setEnabled(false);
		}
		if (manifest.getTRECSection().getEcoEfficiencySectionArray(number).getEcoMetric(ECOLOGICAL_EFFICIENCY) != null){
			EcoMetric ecoMetric = manifest.getTRECSection().getEcoEfficiencySectionArray(number).
					getEcoMetric(ECOLOGICAL_EFFICIENCY);
			eco_thres.setText(ecoMetric.getThresholdValue().toString());
			if (ecoMetric.getSLAType().equalsIgnoreCase("Hard")){
				eco_type.setText(ecoMetric.getSLAType());
				eco_mag.setText(ecoMetric.getMagnitudePenalty().toString());
				eco_mag.setEnabled(true);
				eco_time.setText(ecoMetric.getTimePenalty().toString());
				eco_time.setEnabled(true);
			}else{
				eco_type.setText("Soft");
				eco_mag.setText("NA");
				eco_mag.setEnabled(false);
				eco_time.setText("NA");
				eco_time.setEnabled(false);
			}
		}else{
			eco_thres.setText("NotSpecified");
			eco_type.setText("Soft");
			eco_mag.setText("NA");
			eco_mag.setEnabled(false);
			eco_time.setText("NA");
			eco_time.setEnabled(false);
		}
		//Y3 SM Added END
		enableEcoDetails(true);
	}

	/**
	 * Creates a new eco description
	 */
	private void newEcoSection() {
		eco_sec.setText(ECO_SECTION + (eco_sec.getItems().length + 1));
		cleanEcoDetails();
		enableEcoDetails(true);
	}

	/**
	 * Enable/disable the modifiacation of the eco description details
	 * 
	 * @param b True for enabling, false for disabling
	 */
	private void enableEcoDetails(boolean b) {
		eco_scope.setEnabled(b);
		BREEAM_cert.setEnabled(b);
		LEED_cert.setEnabled(b);
		eu_coc.setEnabled(b);
		energyStar.setEnabled(b);
		ISO_cert.setEnabled(b);
		greenStar.setEnabled(b);
		CASBEE_cert.setEnabled(b);
		if (ener_type.getText().trim().equalsIgnoreCase("Soft")){
			ener_mag.setEnabled(false);
			ener_time.setEnabled(false);
		}else{
			ener_mag.setEnabled(b);
			ener_time.setEnabled(b);
		}
		if (eco_type.getText().trim().equalsIgnoreCase("Soft")){
			eco_mag.setEnabled(false);
			eco_time.setEnabled(false);
		}else{
			eco_mag.setEnabled(b);
			eco_time.setEnabled(b);
		}
		eco_but.setEnabled(b);
	}

	/**
	 * Save the eco description details
	 * @throws Exception
	 */
	public void saveEcoSection() throws Exception {
		int number = getNumberFromName(ECO_SECTION, eco_sec.getText().trim());
		EcoEfficiencySection section;
		if (number >= eco_sec.getItems().length) {
			//If new section
			eco_sec.add(ECO_SECTION + (eco_sec.getItems().length + 1));
			if (manifest == null) {
				generateNewManifest();
			}
			section = manifest.getTRECSection().addNewEcoEfficiencySection(
					ManifestCreation.generateManifestNames(eco_scope.getSelectedPackages()));
		} else {
			//Modify old one
			section = manifest.getTRECSection().getEcoEfficiencySectionArray(
					eco_sec.getSelectionIndex());
			log.debug("Scoped packages:" + eco_scope.getSelectedPackages().length);
			updatePackagesInScope(section.getScope(), eco_scope);
			/*section.getScope().setComponentIdArray(
					ManifestCreation.generateManifestNames(eco_scope.getSelectedPackages()));*/
			log.debug("Scoped packages:"+ section.getScope().getComponentIdArray().length);
		}
		log.debug("Setting BREEAM cert");
		if (BREEAM_cert.getText() != null
				&& BREEAM_cert.getText().trim().length() > 0)
			section.setBREEAMCertification(BREEAM_cert.getText().trim());
		else{
			section.setBREEAMCertification("NotRequired");
		}
		log.debug("Setting LEED cert");
		if (LEED_cert.getText() != null
				&& LEED_cert.getText().trim().length() > 0)
			section.setLEEDCertification(LEED_cert.getText().trim());
		else{
			section.setLEEDCertification("NotRequired");
		}
		log.debug("Setting Energy Star rating");
		if (energyStar.getText() != null
				&& energyStar.getText().trim().length() > 0)
			section.setEnergyStarRating(energyStar.getText().trim());
		else{
			section.setEnergyStarRating("No");
		}
		log.debug("Setting EU COC");
		section.setEuCoCCompliant(eu_coc.getSelection());
		log.debug("Setting ISO 14000 cert");
		if (ISO_cert.getSelection())
			section.setISO14000("ISO14001-Compliant");
		else
			section.setISO14000("No");
		log.debug("Setting Green Star rate");
		if (greenStar.getText() != null
				&& greenStar.getText().trim().length() > 0)
			section.setEnergyStarRating(greenStar.getText().trim());
		else{
			section.setEnergyStarRating("No");
		}
		log.debug("Setting CASBEE cert");
		if (CASBEE_cert.getText() != null
				&& CASBEE_cert.getText().trim().length() > 0)
			section.setCASBEE(CASBEE_cert.getText().trim());
		else{
			section.setCASBEE("No");
		}
		log.debug("Setting Energy Efficiency");
		if (section.getEcoMetric(ENERGY_EFFICIENCY) != null){
			EcoMetric ecoMetric = section.getEcoMetric(ENERGY_EFFICIENCY);
			ecoMetric.setThresholdValue(ener_thres.getText().trim());
			ecoMetric.setSLAType(ener_type.getText().trim());
			ecoMetric.setMagnitudePenalty(ener_mag.getText().trim());
			ecoMetric.setTimePenalty(ener_time.getText().trim());
		}else{
			EcoMetric ecoMetric = section.addNewEcoMetric(ENERGY_EFFICIENCY);
			ecoMetric.setThresholdValue(ener_thres.getText().trim());
			ecoMetric.setSLAType(ener_type.getText().trim());
			ecoMetric.setMagnitudePenalty(ener_mag.getText().trim());
			ecoMetric.setTimePenalty(ener_time.getText().trim());
		}
		log.debug("Setting Ecological Efficiency");
		if (section.getEcoMetric(ECOLOGICAL_EFFICIENCY) != null){
			EcoMetric ecoMetric = section.getEcoMetric(ECOLOGICAL_EFFICIENCY);
			ecoMetric.setThresholdValue(eco_thres.getText().trim());
			ecoMetric.setSLAType(eco_type.getText().trim());
			ecoMetric.setMagnitudePenalty(eco_mag.getText().trim());
			ecoMetric.setTimePenalty(eco_time.getText().trim());
		}else{
			EcoMetric ecoMetric = section.addNewEcoMetric(ECOLOGICAL_EFFICIENCY);
			ecoMetric.setThresholdValue(eco_thres.getText().trim());
			ecoMetric.setSLAType(eco_type.getText().trim());
			ecoMetric.setMagnitudePenalty(eco_mag.getText().trim());
			ecoMetric.setTimePenalty(eco_time.getText().trim());
		}
		/*log.debug(section.getBREEAMCertification());
		log.debug(section.getCASBEE());
		log.debug(section.getGreenStar());
		log.debug(section.getISO14000());
		log.debug(section.getLEEDCertification());
		log.debug(section.getEnergyStarRating().toString());
		log.debug(Boolean.toString(section.getEuCoCCompliant()));
		log.debug("Number of Ecometrics: "+section.getEcoMetricArray().length);
		for (EcoMetric m: section.getEcoMetricArray()){
			log.debug(m.getName());
			log.debug(m.getSLAType());
			log.debug(m.getMagnitudePenalty().toString());
			log.debug(m.getThresholdValue().toString());
			log.debug(m.getTimePenalty().toString());
			log.debug(m.toString());
		}*/
		writeManifestToFile();
	}

	private void updatePackagesInScope(Scope scope,
			ScopedListsComposite scopedList) {
		if (scope.getComponentIdArray()!=null)
		for (String s: scope.getComponentIdArray()){
			scope.removeComponentId(s);
		}
		for (String s: ManifestCreation.generateManifestNames(scopedList.getSelectedPackages())){
			scope.addComponentId(s);
		}
		
	}

	/**
	 * Clean the eco description details
	 */
	public void cleanEcoDetails() {
		String[] totalEls = getAllPackages();
		eco_scope.reset(totalEls);
		BREEAM_cert.setText("NotRequired");
		LEED_cert.setText("NotRequired");
		eu_coc.setSelection(false);
		energyStar.setText("No");
		ISO_cert.setSelection(false);
		greenStar.setText("No");
		CASBEE_cert.setText("No");
		
		ener_thres.setText("NotSpecified");
		ener_type.setText("Soft");
		ener_mag.setText("NA");
		ener_mag.setEnabled(false);
		ener_time.setText("NA");
		ener_time.setEnabled(false);
		
		eco_thres.setText("NotSpecified");
		eco_type.setText("Soft");
		eco_mag.setText("NA");
		eco_mag.setEnabled(false);
		eco_time.setText("NA");
		eco_time.setEnabled(false);
	}
	
	//COST PARAMETERS
	
	/**
	 * Create the tab for the Cost parameters description
	 * 
	 * @param trec_composite Parent's TREC composite
	 * @param toolkit Form Editor toolkit
	 * @return Cost composite
	 */
	private Composite createCOSTComposite(Composite trec_composite,
			FormToolkit toolkit) {
		Composite cost_comp = toolkit.createComposite(trec_composite,
				SWT.BORDER);
		GridData rd = new GridData(GridData.FILL_HORIZONTAL
				| GridData.VERTICAL_ALIGN_BEGINNING);
		rd.grabExcessHorizontalSpace = true;
		cost_comp.setLayoutData(rd);
		cost_comp.setLayout(new GridLayout(1, false));
		Composite combo_comp = toolkit.createComposite(cost_comp, SWT.BORDER);
		rd = new GridData(GridData.FILL_HORIZONTAL
				| GridData.VERTICAL_ALIGN_BEGINNING);
		rd.grabExcessHorizontalSpace = true;
		combo_comp.setLayoutData(rd);
		combo_comp.setLayout(new GridLayout(3, false));
		toolkit.createLabel(combo_comp, "Cost Section");
		cost_sec = new Combo(combo_comp, SWT.NONE);
		rd = new GridData(GridData.FILL_HORIZONTAL);
		rd.grabExcessHorizontalSpace = true;
		rd.minimumWidth = 300;
		cost_sec.setLayoutData(rd);
		cost_sec.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				loadCostSection(cost_sec.getSelectionIndex());
			}
		});
		Button new_btn = toolkit.createButton(combo_comp, "New", SWT.NORMAL);
		new_btn.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {
				newCostSection();
			}
	
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				newCostSection();
			}
		});
		// Details part
		Group details_comp = new Group(cost_comp, SWT.BORDER);
		rd = new GridData(GridData.FILL_HORIZONTAL
				| GridData.VERTICAL_ALIGN_BEGINNING);
		rd.grabExcessHorizontalSpace = true;
		details_comp.setLayoutData(rd);
		details_comp.setLayout(new GridLayout(2, false));
		details_comp.setText("Cost Details");
		toolkit.createLabel(details_comp, "Plan Cap", SWT.NONE);
		cost_cap = toolkit
				.createText(details_comp, "", SWT.SINGLE | SWT.BORDER);
		rd = new GridData(GridData.FILL_HORIZONTAL);
		rd.grabExcessHorizontalSpace = true;
		rd.minimumWidth = 350;
		cost_cap.setLayoutData(rd);
		toolkit.createLabel(details_comp, "Currency", SWT.NONE);
		currency = new Combo(details_comp, SWT.NONE);
		rd = new GridData(GridData.FILL_HORIZONTAL);
		rd.grabExcessHorizontalSpace = true;
		rd.minimumWidth = 350;
		currency.setLayoutData(rd);
		currency.setItems(new String[] { "EUR", "USD", "GBP", "JPY" });
	
		// Scope part
		//toolkit.createLabel(cost_comp, "Section Scope");
		cost_scope = new ScopedListsComposite(toolkit, "Components Scope");
		cost_scope.createComposite(cost_comp);
	
		// Buttons part
		cost_but = new SaveResetButtonComposite(getShell(), page.getToolkit(), COST, this);
		cost_but.createComposite(cost_comp);
	
		return cost_comp;
	}

	/**
	 * Initialize the COST parameter description
	 */
	private void initCOSTparameters() {
		cost_sec.setItems(generateSectionNames(manifest.getTRECSection()
				.getCostSectionArray().length, COST_SECTION));
		cleanCostDetails();
		enableCostDetails(false);
	}

	/**
	 * Load a Cost description
	 * @param number Number of the Cost description
	 */
	private void loadCostSection(int number) {
		String[] totalEls = getAllPackages();
		String[] selectedEls = ManifestCreation.getPackageNames(manifest.getTRECSection()
				.getCostSectionArray(number).getScope().getComponentIdArray());
		cost_scope.setPackagesLists(totalEls, selectedEls);
		if (manifest.getTRECSection().getCostSectionArray(number)
				.getPricePlanArray(0).getPlanCap() > 0)
			cost_cap.setText(Float.toString(manifest.getTRECSection()
					.getCostSectionArray(number).getPricePlanArray(0)
					.getPlanCap()));
		else
			cost_cap.setText("");
		if (manifest.getTRECSection().getCostSectionArray(number)
				.getPricePlanArray(0).getCurrency() != null)
			currency.setText(manifest.getTRECSection()
					.getCostSectionArray(number).getPricePlanArray(0)
					.getCurrency());
		else
			currency.setText("");
		enableCostDetails(true);
	}

	/**
	 * Create a new cost description
	 */
	private void newCostSection() {
		cost_sec.setText(COST_SECTION + (cost_sec.getItems().length + 1));
		cleanCostDetails();
		enableCostDetails(true);
	}

	/**
	 * Enables/disables the modification of cost description details
	 * 
	 * @param b True for enabling, false for disabling
	 */
	private void enableCostDetails(boolean b) {
		cost_scope.setEnabled(b);
		cost_cap.setEnabled(b);
		currency.setEnabled(b);
		cost_but.setEnabled(b);
	}

	/**
	 * Save the cost description
	 * @throws Exception
	 */
	public void saveCostSection() throws Exception {
		int number = getNumberFromName(COST_SECTION, cost_sec.getText().trim());
		CostSection section;
		if (number >= cost_sec.getItems().length) {
			cost_sec.add(COST_SECTION + (cost_sec.getItems().length + 1));
			if (manifest == null) {
				generateNewManifest();
			}
			section = manifest.getTRECSection().addNewCostSection(
					ManifestCreation.generateManifestNames(cost_scope.getSelectedPackages()));
		} else {
			section = manifest.getTRECSection().getCostSectionArray(
					cost_sec.getSelectionIndex());
			section.getScope().setComponentIdArray(
					ManifestCreation.generateManifestNames(cost_scope.getSelectedPackages()));
		}
		if (currency.getText() != null
				&& currency.getText().trim().length() > 0)
			section.getPricePlanArray(0).setCurrency(currency.getText().trim());
		if (cost_cap.getText() != null
				&& cost_cap.getText().trim().length() > 0)
			section.getPricePlanArray(0).setPlanCap(
					Float.parseFloat(cost_cap.getText().trim()));
		writeManifestToFile();
	}

	/**
	 * Clean cost description details
	 */
	public void cleanCostDetails() {
		String[] totalEls = getAllPackages();
		cost_scope.reset(totalEls);
		cost_cap.setText("");
		currency.setText("");
	}

	//***************************** data protection section ***************************
	
	/**
	 * Create the Data protection section
	 * @param composite Parent's composite
	 */
	private void createDataProtectionSection(Composite composite) {
		// TODO Add DP parameters
		DP_section = page.getToolkit().createSection(composite,
				Section.TWISTIE | Section.DESCRIPTION);
				
		DP_section.setText("Security and Data Protection");
		DP_section.setDescription("Define data protection and security parameters for the service");
		GridData rd = new GridData(GridData.HORIZONTAL_ALIGN_FILL
				| GridData.VERTICAL_ALIGN_BEGINNING);
		rd.grabExcessHorizontalSpace = true;
		DP_section.setLayoutData(rd);
		DP_section.setLayout(new GridLayout(1, true));
		dp_composite = page.getToolkit()
				.createComposite(DP_section, SWT.BORDER);
		rd = new GridData(GridData.FILL_HORIZONTAL);
		rd.grabExcessHorizontalSpace = true;
		dp_composite.setLayoutData(rd);
		dp_composite.setLayout(new GridLayout(1, false));
		/*Y2 removed 
		Composite dp_level = page.getToolkit().createComposite(dp_composite,
				SWT.BORDER);
		rd = new GridData(GridData.FILL_HORIZONTAL);
		rd.grabExcessHorizontalSpace = true;
		dp_level.setLayoutData(rd);
		dp_level.setLayout(new GridLayout(2, false));
		page.getToolkit().createLabel(dp_level, "Data Protection Level",
				SWT.NONE);
		dpText = page.getToolkit().createText(dp_level, "",
				SWT.SINGLE | SWT.BORDER);
		rd = new GridData();
		rd.grabExcessHorizontalSpace = true;
		rd.minimumWidth = 350;
		dpText.setLayoutData(rd);
		Y2 removed*/
		
		Label legal = page.getToolkit().createLabel(dp_composite, "Legal Constraints", SWT.NONE);
		FontData[] fD = legal.getFont().getFontData();
		fD[0].setStyle(SWT.BOLD);
		legal.setFont( new Font(legal.getDisplay(),fD[0]));
		Composite dp_legal_items = page.getToolkit().createComposite(
				dp_composite, SWT.BORDER);
		rd = new GridData(GridData.FILL_HORIZONTAL);
		rd.grabExcessHorizontalSpace = true;
		dp_legal_items.setLayoutData(rd);
		dp_legal_items.setLayout(new GridLayout(1, false));
		
		Group dp_ipr_comp = new Group(
				dp_legal_items, SWT.BORDER);
		rd = new GridData(GridData.FILL_HORIZONTAL);
		rd.grabExcessHorizontalSpace = true;
		dp_ipr_comp.setLayoutData(rd);
		dp_ipr_comp.setLayout(new GridLayout(2, false));
		dp_ipr_comp.setText("Intelectual Property Rights");
		page.getToolkit().createLabel(dp_ipr_comp, "IPR mode",
				SWT.NONE);
		dp_IPR = new Combo(dp_ipr_comp,	SWT.SINGLE | SWT.BORDER);
		rd = new GridData();
		rd.grabExcessHorizontalSpace = true;
		rd.minimumWidth = 350;
		dp_IPR.setLayoutData(rd);
		dp_IPR.setItems(IPRMode.getIPRModes());
		Group dp_provider_comp = new Group(
				dp_legal_items, SWT.BORDER);
		rd = new GridData(GridData.FILL_HORIZONTAL);
		rd.grabExcessHorizontalSpace = true;
		dp_provider_comp.setLayoutData(rd);
		dp_provider_comp.setLayout(new GridLayout(1, false));
		dp_provider_comp.setText("Provider Selection");
		//page.getToolkit().createLabel(dp_provider_comp, "Country White List");
		dp_white_list = new ScopedListsComposite(page.getToolkit(), "Countries White List" );
		dp_white_list.createComposite(dp_provider_comp);
		dp_BCR = page.getToolkit().createButton(dp_provider_comp, "Require authorized Binding Corporate Rules",
				SWT.CHECK);
		rd = new GridData();
		rd.grabExcessHorizontalSpace = true;
		rd.minimumWidth = 350;
		dp_BCR.setLayoutData(rd);
		dp_SCC = page.getToolkit().createButton(dp_provider_comp, "Require acceptance of Standard Contractual Clauses",
				SWT.CHECK);
		rd = new GridData();
		rd.grabExcessHorizontalSpace = true;
		rd.minimumWidth = 350;
		dp_SCC.setLayoutData(rd);
		
		
		// Combo part Y3 
		/*page.getToolkit().createLabel(dp_composite, "Shared Data Storage", SWT.NONE);
		Composite dp_storage = page.getToolkit().createComposite(dp_composite, SWT.BORDER);
		rd = new GridData(GridData.FILL_HORIZONTAL
				| GridData.VERTICAL_ALIGN_BEGINNING);
		rd.grabExcessHorizontalSpace = true;
		dp_storage.setLayoutData(rd);
		dp_storage.setLayout(new GridLayout(1, false));
		Composite combo_comp = page.getToolkit().createComposite(dp_storage, SWT.BORDER);
		rd = new GridData(GridData.FILL_HORIZONTAL
				| GridData.VERTICAL_ALIGN_BEGINNING);
		rd.grabExcessHorizontalSpace = true;
		combo_comp.setLayoutData(rd);
		combo_comp.setLayout(new GridLayout(3, false));
		page.getToolkit().createLabel(combo_comp, "Storage Space", SWT.BORDER);
		ds_sec = new Combo(combo_comp, SWT.NONE);
		rd = new GridData(GridData.FILL_HORIZONTAL);
		rd.grabExcessHorizontalSpace = true;
		rd.minimumWidth = 300;
		ds_sec.setLayoutData(rd);
		ds_sec.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				loadDataStorageSection(ds_sec.getSelectionIndex());
			}
		});
		Button new_btn = page.getToolkit().createButton(combo_comp, "New", SWT.NORMAL);
		new_btn.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {
				newDataStorageSection();
			}
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				newDataStorageSection();
			}
		});*/
		//End Combo part Y3 SM change
		//No combo part Y3 (Only encrypted storage)
		Label encryp = page.getToolkit().createLabel(dp_composite, "Encrypted Storage", SWT.NONE);
		fD = encryp.getFont().getFontData();
		fD[0].setStyle(SWT.BOLD);
		encryp.setFont( new Font(encryp.getDisplay(),fD[0]));
		Composite dp_storage = page.getToolkit().createComposite(dp_composite, SWT.BORDER);
		rd = new GridData(GridData.FILL_HORIZONTAL
				| GridData.VERTICAL_ALIGN_BEGINNING);
		rd.grabExcessHorizontalSpace = true;
		dp_storage.setLayoutData(rd);
		dp_storage.setLayout(new GridLayout(1, false));
		// End No combo part Y3 
		//page.getToolkit().createLabel(dp_storage, "Capacity Details");
		Group dp_details = new Group(dp_storage, SWT.BORDER);
		rd = new GridData(GridData.FILL_HORIZONTAL);
		rd.grabExcessHorizontalSpace = true;
		dp_details.setLayoutData(rd);
		dp_details.setLayout(new GridLayout(2, false));
		dp_details.setText("Capacity Details");
		page.getToolkit().createLabel(dp_details, "Capacity", SWT.NONE);
		ds_storageText = page.getToolkit().createText(dp_details, "",
				SWT.SINGLE | SWT.BORDER);
		rd = new GridData();
		rd.grabExcessHorizontalSpace = true;
		rd.minimumWidth = 350;
		ds_storageText.setLayoutData(rd);
		page.getToolkit().createLabel(dp_details, "Units", SWT.NONE);
		ds_unitsText = page.getToolkit().createText(dp_details, "",
				SWT.SINGLE | SWT.BORDER);
		rd = new GridData();
		rd.grabExcessHorizontalSpace = true;
		rd.minimumWidth = 350;
		ds_unitsText.setLayoutData(rd);
		
		//page.getToolkit().createLabel(dp_storage, "Component Scope");
		dp_scope = new ScopedListsComposite(page.getToolkit(), "Components scope");
		dp_scope.createComposite(dp_storage);
		
		// Encrypted part (Finally not used)
		/*page.getToolkit().createLabel(dp_composite, "Encription Level",
				SWT.NONE);
		Composite dp_enc_level = page.getToolkit().createComposite(
				dp_composite, SWT.BORDER);
		rd = new GridData(GridData.FILL_HORIZONTAL);
		rd.grabExcessHorizontalSpace = true;
		dp_enc_level.setLayoutData(rd);
		dp_enc_level.setLayout(new GridLayout(2, false));
		page.getToolkit().createLabel(dp_enc_level, "Encription Algorithm",
				SWT.NONE);
		eaText = page.getToolkit().createText(dp_enc_level, "",
				SWT.SINGLE | SWT.BORDER);
		rd = new GridData();
		rd.grabExcessHorizontalSpace = true;
		rd.minimumWidth = 350;
		eaText.setLayoutData(rd);
		page.getToolkit().createLabel(dp_enc_level, "Encription key size",
				SWT.NONE);
		ea_keyText = page.getToolkit().createText(dp_enc_level, "",
				SWT.SINGLE | SWT.BORDER);
		rd = new GridData();
		rd.grabExcessHorizontalSpace = true;
		rd.minimumWidth = 350;
		ea_keyText.setLayoutData(rd);
		*/
		//End Encrypted part
		// Y3 Application Protection
		Label appProt = page.getToolkit().createLabel(dp_composite, "Aplication Protection", SWT.NONE);
		fD = appProt.getFont().getFontData();
		fD[0].setStyle(SWT.BOLD);
		appProt.setFont( new Font(appProt.getDisplay(),fD[0]));
		Composite ap_storage = page.getToolkit().createComposite(dp_composite, SWT.BORDER);
		rd = new GridData(GridData.FILL_HORIZONTAL
				| GridData.VERTICAL_ALIGN_BEGINNING);
		rd.grabExcessHorizontalSpace = true;
		ap_storage.setLayoutData(rd);
		ap_storage.setLayout(new GridLayout(1, false));
		//page.getToolkit().createLabel(ap_storage, "Intelligent Protection System");
		ips_scope = new ScopedListsComposite(page.getToolkit(), "Intelligent Protection System");
		ips_scope.createComposite(ap_storage);
		//page.getToolkit().createLabel(ap_storage, "Virtual Private Network");
		vpn_scope = new ScopedListsComposite(page.getToolkit(), "Virtual Private Network");
		vpn_scope.createComposite(ap_storage);
		// End Y3 Application Protection
		
		// Buttons part
		dp_but = new SaveResetButtonComposite(getShell(), page.getToolkit(), DP, this);
		dp_but.createComposite(dp_composite);
	
		DP_section.setClient(dp_composite);
		DP_section.setExpanded(true);
		DP_section.setExpanded(false);
	}

	/*protected void newDataStorageSection() {
		// Finally not used
		
	}*/

	protected void loadDataStorageSection(String dsName) throws Exception {
		DataProtectionSection section = manifest.getDataProtectionSection();
		String[] totalEls = getAllPackages();
		DataStorage ds = section.getDataStorage(dsName);
		if (ds!= null){
			String[] selectedEls = ManifestCreation.getPackageNames(
					ds.getScope().getComponentIdArray());
			dp_scope.setPackagesLists(totalEls, selectedEls);
			if (ds.getAllocationUnit()!=null)
				ds_unitsText.setText(ds.getAllocationUnit());
			else
				ds_unitsText.setText("");
			if (ds.getCapacity()>0){
				ds_storageText.setText(Long.toString(ds.getCapacity()));
			}
			else
				ds_storageText.setText("");
			enableDataStorageDetails(true);
		} else{
			log.warn("Data storage "+ dsName +" not found.");
			dp_scope.setPackagesLists(totalEls, new String[0]);
		}
	}

	/**
	 * Save the data protection details
	 * 
	 * @throws Exception
	 */
	protected void saveDataProtection() throws Exception {
		if (manifest == null) {
			generateNewManifest();
		}
		DataProtectionSection section = manifest.getDataProtectionSection();
		// Y3
		if (dp_SCC.getSelection()){
			section.getSCC().enableSCC();
			/*if (section.getSCC().getStandardContractualClause(0)== null)
				section.getSCC().addStandardContractualClause();*/
		}else{
			section.getSCC().disableSCC();
			/*if (section.getSCC().getStandardContractualClause(0)!= null)
				section.getSCC().removeStandardContractualClause(0);*/
		}
		if (dp_BCR.getSelection()){
			section.getBCR().enableBCR();
			/*if (section.getBCR().getBindingContactualRule(0)== null)
				section.getBCR().addBindingContactualRule();*/
		}else{
			section.getBCR().disableBCR();
			/*if (section.getBCR().getBindingContactualRule(0)!= null)
				section.getBCR().removeBindingContactualRule(0);*/
		}
		if (dp_IPR.getText()!=null && !dp_IPR.getText().trim().isEmpty()){
			IPRMode iprmode = IPRMode.getIPRMode(dp_IPR.getText().trim());
			if (iprmode!= null){
				section.getIPR().enableIPR();
				if (section.getIPR().getIntellectualPropertyRule(0)== null)
					section.getIPR().addIntellectualPropertyRule();
				if (iprmode.getModeNameCode()!=null)
					section.getIPR().getIntellectualPropertyRule(0).setTitle(iprmode.getModeNameCode());
				else
					log.error("No IPR code found");
				if (iprmode.getModeNameDisplay()!=null){
					section.getIPR().getIntellectualPropertyRule(0).setDescription(iprmode.getModeNameDisplay());
				}else
					log.error("No IPR display text found");
				if (iprmode.getIprText()!=null){
					String[] its = section.getIPR().getIntellectualPropertyRule(0).getItems();
					if (its!= null && its.length>0){
						for(int i=0; i<its.length;i++){
							section.getIPR().getIntellectualPropertyRule(i).removeItem(i);
						}	
					}
					section.getIPR().getIntellectualPropertyRule(0).addItem(iprmode.getIprText());
				}else
					log.error("No IPR text found");
			}else{
				log.error("No IPR mode found");
			}
		}else{
			section.getIPR().disableIPR();
			if (section.getIPR().getIntellectualPropertyRule(0)!= null)
				section.getIPR().removeIntellectualPropertyRule(0);
		}
		String[] currentList = section.getEligibleCountryList();
		if (currentList!=null && currentList.length>0){
			for (String country: currentList)
				section.removeEligibleCountry(country);
		}
		String[] whiteList = dp_white_list.getSelectedPackages();
		if (whiteList!=null && whiteList.length>0){
			for (String country: whiteList)
				section.addNewEligibleCountry(ISOCountryCodeMap.getCountryCode(country));
		}
		//Encrypted Space
		String[] selectedPackages = ManifestCreation.generateManifestNames(dp_scope.getSelectedPackages());
		if (selectedPackages!=null && selectedPackages.length>0){
			for (String componentID : selectedPackages) {
				//Update vmcc
				VirtualMachineComponentConfiguration vmcc = manifest
					.getServiceProviderExtensionSection()
					.getVirtualMachineComponentConfiguration(componentID);
				if (vmcc == null) {
					log.warn("Configuration not found for: "
						+ componentID);
					vmcc = manifest
						.getServiceProviderExtensionSection()
						.addNewVirtualMachineComponentConfiguration(componentID);
				}
				vmcc.enableEncryptedSpace("".getBytes());
			}
		}
		//Update ds encripted scope
		if (selectedPackages!=null && selectedPackages.length>0){
			DataStorage ds = section.getDataStorage(ENCRYPTED_DS);
			if (ds == null){
				ds = section.addNewDataStorage(ENCRYPTED_DS, "");
				ds.setName(ENCRYPTED_DS);
			}
			if (!ds_storageText.getText().trim().isEmpty())
				ds.setCapacity(Long.parseLong(ds_storageText.getText().trim()));
			else
				ds.setCapacity(0);
			if(!ds_storageText.getText().trim().isEmpty())
				ds.setAllocationUnit(ds_unitsText.getText().trim());
			else
				ds.setAllocationUnit("");

			ds.getScope().setComponentIdArray(selectedPackages);
		}else{
			section.removeDataStorage(ENCRYPTED_DS);
		}

		for (String componentID : ManifestCreation.generateManifestNames(
				dp_scope.getNonSelectedPackages())) {
			VirtualMachineComponentConfiguration vmcc = manifest
					.getServiceProviderExtensionSection()
					.getVirtualMachineComponentConfiguration(componentID);
			if (vmcc != null) {
				if (vmcc.isEncryptedSpaceEnabled()) {
					vmcc.disableEncryptedSpace();
				}
			}
		}
		
		//VPN
		for (String componentID : ManifestCreation.generateManifestNames(
				vpn_scope.getSelectedPackages())) {
			VirtualMachineComponentConfiguration vmcc = manifest
				.getServiceProviderExtensionSection()
				.getVirtualMachineComponentConfiguration(componentID);
			if (vmcc == null) {
				log.warn("Configuration not found for: "
					+ componentID);
				vmcc = manifest
					.getServiceProviderExtensionSection()
					.addNewVirtualMachineComponentConfiguration(componentID);
			}
			vmcc.enableVPNSecurity();
		}

		for (String componentID : ManifestCreation.generateManifestNames(
				vpn_scope.getNonSelectedPackages())) {
			VirtualMachineComponentConfiguration vmcc = manifest
				.getServiceProviderExtensionSection()
				.getVirtualMachineComponentConfiguration(componentID);
			if (vmcc != null) {
				if (vmcc.isSecurityVPNbased()) {
					vmcc.disableVPNSecurity();
				}
			}
		}
		//IPS 
		for (String componentID : ManifestCreation.generateManifestNames(
				ips_scope.getSelectedPackages())) {
			VirtualMachineComponentConfiguration vmcc = manifest
				.getServiceProviderExtensionSection()
				.getVirtualMachineComponentConfiguration(componentID);
			if (vmcc == null) {
				log.warn("Configuration not found for: "
					+ componentID);
				vmcc = manifest
					.getServiceProviderExtensionSection()
					.addNewVirtualMachineComponentConfiguration(componentID);
			}
			vmcc.enableIPS();
		}

		for (String componentID : ManifestCreation.generateManifestNames(
				ips_scope.getNonSelectedPackages())) {
			VirtualMachineComponentConfiguration vmcc = manifest
				.getServiceProviderExtensionSection()
				.getVirtualMachineComponentConfiguration(componentID);
			if (vmcc != null) {
				if (vmcc.isIPSEnabled()) {
					vmcc.disableIPS();
				}
			}
		}
		// Y3 End
		
		/* Y2 removed 
		section.setDataProtectionLevel(dpText.getText().trim());
		section.getDataEncryptionLevel().setEncryptionAlgorithm(
				eaText.getText().trim());
		if (ea_keyText.getText().trim().length() > 0)
			section.getDataEncryptionLevel().setEncryptionKeySize(
					Integer.parseInt(ea_keyText.getText().trim()));
		if (ds_storageText.getText().trim().length() > 0)
			section.getDataStorage().setCapacity(Long.parseLong(ds_storageText.getText().trim()));
			section.getDataStorage().setAllocationUnit(ds_unitsText.getText().trim());
		*/
	}

	/**
	 * Clean the data protection details
	 */
	protected void cleanDPDetails() {
		String[] totalEls = getAllPackages();
		dp_white_list.reset(ISOCountryCodeMap.getAllWhiteList());
		dp_SCC.setSelection(false);
		dp_BCR.setSelection(false);
		dp_IPR.setText("");
		/*Y2 removed
		dpText.setText("");
		eaText.setText("");
		ea_keyText.setText("");
		Y2 removed*/
		ds_storageText.setText("");
		ds_unitsText.setText("");
		dp_scope.reset(totalEls);
		vpn_scope.reset(totalEls);
		ips_scope.reset(totalEls);
	}

	/**
	 *  Load the data protection parameters from the service manifest
	 * @throws Exception 
	 */
	private void loadDPparameters() throws Exception {
		DataProtectionSection section = manifest.getDataProtectionSection();
		// Load legal requirements SCC, BCR, white list and IPR
		SCC scc = section.getSCC();
		if (scc!=null && scc.isSCCEnabled()){
			dp_SCC.setSelection(true);
		}else
			dp_SCC.setSelection(false);
		BCR bcr = section.getBCR();
		if (bcr!=null && bcr.isBCREnabled()){
			dp_BCR.setSelection(true);
		}else
			dp_BCR.setSelection(false);
		LegalItemSection ipr = section.getIPR().getIntellectualPropertyRule(0);
		if (ipr!=null){
			dp_IPR.setText(ipr.getDescription());
		}else
			dp_IPR.setText("");
		String[] countryCodes = section.getEligibleCountryList();
		dp_white_list.reset();
		dp_white_list.setPackagesLists(ISOCountryCodeMap.getAllWhiteList(), ISOCountryCodeMap.getCountryNames(countryCodes));
		
		
		loadDataStorageSection(ENCRYPTED_DS);
		//VPN
		VirtualMachineComponentConfiguration[] vmccs = manifest
				.getServiceProviderExtensionSection()
				.getVirtualMachineComponentConfigurationArray();
		ArrayList<String> comp = new ArrayList<String>();
		for (VirtualMachineComponentConfiguration vmcc : vmccs) {
			if (vmcc.isSecurityVPNbased())
				comp.add(vmcc.getComponentId());
		}
		vpn_scope.setPackagesLists(getAllPackages(),
			ManifestCreation.getPackageNames(comp.toArray(new String[comp.size()])));
		//TODO: IPS to be added after manifest change
		comp = new ArrayList<String>();
		for (VirtualMachineComponentConfiguration vmcc : vmccs) {
			if (vmcc.isIPSEnabled())
				comp.add(vmcc.getComponentId());
		}
		ips_scope.setPackagesLists(getAllPackages(),
			ManifestCreation.getPackageNames(comp.toArray(new String[comp.size()])));		
		// END Y3 SM added
		
		/*Y2 removed 
		dpText.setText(section.getDataProtectionLevel());
		eaText.setText(section.getDataEncryptionLevel()
				.getEncryptionAlgorithm());
		ea_keyText.setText(Integer.toString(section.getDataEncryptionLevel()
				.getEncryptionKeySize()));
		 SM ds_storageText.setText(Long.toString(section.getDataStorage()
				.getCapacity()));
		ds_unitsText.setText(section.getDataStorage().getAllocationUnit());*/
		/*TODO to be moved to load DataStorage details to check if scope is correct */
		/*Y2 removed
		VirtualMachineComponentConfiguration[] vmccs = manifest
				.getServiceProviderExtensionSection()
				.getVirtualMachineComponentConfigurationArray();
		ArrayList<String> comp = new ArrayList<String>();
		for (VirtualMachineComponentConfiguration vmcc : vmccs) {
			vmcc.isEncryptedSpaceEnabled();
			comp.add(vmcc.getComponentId());
		}
		dp_scope.setPackagesLists(getAllPackages(),
				getPackageNames(comp.toArray(new String[comp.size()])));
		Y2 removed */
		
	}

	private void enableDataStorageDetails(boolean b) {
		// TODO Auto-generated method stub
		
	}

	private void cleanDataStorageDetails() {
		// TODO Auto-generated method stub
		
	}

	//***************************** Deployment section ****************************************
	/**
	 * Create the deployment section
	 * 
	 * @param composite Parents composite
	 */
	private void createDeploymentSection(Composite composite) {
		DS_section = page.getToolkit().createSection(composite,
				Section.TWISTIE | Section.DESCRIPTION | SWT.BORDER);
		DS_section.setText("Deployment Service");
		DS_section
				.setDescription("Define location and parameters for the Deployment Service");
		GridData rd = new GridData(GridData.HORIZONTAL_ALIGN_FILL
				| GridData.VERTICAL_ALIGN_BEGINNING);
		rd.grabExcessHorizontalSpace = true;
		DS_section.setLayoutData(rd);
		DS_section.setLayout(new GridLayout(1, true));
		options = page.getToolkit().createComposite(DS_section, SWT.BORDER);
		rd = new GridData(GridData.FILL_HORIZONTAL
				| GridData.VERTICAL_ALIGN_BEGINNING);
		rd.grabExcessHorizontalSpace = true;
		options.setLayout(new GridLayout(3, false));
		options.setLayoutData(rd);
		page.getToolkit().createLabel(options, "Optimization Objective",
				SWT.NONE);
		optim = new Combo(options, SWT.NONE);
		rd = new GridData();
		rd.grabExcessHorizontalSpace = true;
		rd.minimumWidth = 400;
		optim.setLayoutData(rd);
		optim.setItems(new String[] { "RISK", "COST" });
		allowFederated = page.getToolkit().createButton(options,
				"Allow Federation", SWT.CHECK);
		allowFederated.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {
				updateAllowFederated();
			}
	
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				updateAllowFederated();
			}
		});
		page.getToolkit().createLabel(options, "Deployment Service", SWT.NONE);
		serverText = page.getToolkit().createText(options, "",
				SWT.SINGLE | SWT.BORDER);
		rd = new GridData();
		rd.grabExcessHorizontalSpace = true;
		rd.minimumWidth = 400;
		serverText.setLayoutData(rd);
		serverText.addModifyListener(new ModifyListener() {
	
			@Override
			public void modifyText(ModifyEvent arg0) {
				op_prop.setDSLocation(serverText.getText().trim());
				try {
					op_prop.save();
				} catch (ConfigurationException e) {
					log.error("Exception svaing properties", e);
					ErrorDialog.openError(getShell(),
							"Saving optimis properties", e.getMessage(), 
							new Status(Status.ERROR, Activator.PLUGIN_ID,"Exception saving properties", e));
				}
			}
	
		});
		DS_section.setClient(options);
		DS_section.setExpanded(true);
		DS_section.setExpanded(false);
	}

	/**
	 * Update the allow federated falg in the service manifest
	 */
	protected void updateAllowFederated() {
		if (manifest != null) {
			manifest.getVirtualMachineDescriptionSection()
					.setIsFederationAllowed(allowFederated.getSelection());
		}
	
	}

	/** 
	 * Generate the section name from prefix and number
	 * 
	 * @param number Number of section
	 * @param prefix Prefix of the section name
	 * @return
	 */
	private String[] generateSectionNames(int number, String prefix) {
		String[] sections = new String[number];
		for (int i = 0; i < number; i++) {
			sections[i] = prefix + (i + 1);
		}
		return sections;
	}
	
	/** 
	 * Get data storage names
	 * 
	 * @param dataStorages Array of Data storages
	 * @return Array of data storage names
	 */
	private String[] getDataStorageNames(eu.optimis.manifest.api.sp.DataStorage[] dataStorages) {
		if (dataStorages!=null){
			String[] str = new String[dataStorages.length];
			for (int i=0; i <dataStorages.length ; i++) {
				str[i] = dataStorages[i].getName();
			}
			return str;
		}else
			return new String[0];
	}
	
	/**
	 * Get the number from a section name
	 * 
	 * @param prefix Section prefix
	 * @param name Section name
	 * @return
	 */
	private int getNumberFromName(String prefix, String name) {
		int num = Integer.parseInt(name.substring(name.indexOf(prefix)
				+ prefix.length()));
		return num - 1;
	}

	/* (non-Javadoc)
	 * @see es.bsc.servicess.ide.editors.Deployer#deploy()
	 */
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
			String message = e.getMessage();
			log.error("Error message: " + message, e);
			ErrorDialog.openError(super.getShell(), "Error",
					"Deploying the service: "+ message, new Status(IStatus.ERROR,Activator.PLUGIN_ID,
							message, e));
		} catch (InvocationTargetException e) {
			String message = e.getMessage();
			log.error("Error message: " + message,e);
			ErrorDialog.openError(super.getShell(), "Error",
					"Deploying the service: "+ message, new Status(IStatus.ERROR,Activator.PLUGIN_ID,
							message, e));
		}
	}

	/**
	 * Perform the service deployment contacting the Deployment service 
	 * 
	 * @param monitor Progress monitor
	 * @throws Exception 
	 */
	public void executeDeployment(IProgressMonitor monitor)
			throws Exception {
		//Random serviceID
		//String serviceID = UUID.randomUUID().toString();
		//ProjectName as serviceID
		String serviceID = editor.getProject().getProject().getName();
		// TODO: Check if images created
		// check if manifest already created
		if (manifest == null) {
			generateNewManifest();
		}
		manifest.getVirtualMachineDescriptionSection().setServiceId(serviceID);

		String location = serverText.getText().trim();
		if (location != null && location.length() > 0) {
			monitor.beginTask("Deploying service", 100);
			Client c = Client.create();
			WebResource resource = c.resource(location);
			if (resource != null) {
				MultivaluedMap<String, String> part = new MultivaluedMapImpl();
				part.add("ManifestString", manifest.toString());
				part.add("Objective", optim.getText().trim());
				log.debug("Deploying...");
				ClientResponse response = resource.path("deploy").post(
						ClientResponse.class, part);
				if ((response.getStatus() >= 200)
						&& (response.getStatus() < 300)) {
					// Check the progress
					monitorProgress(resource, serviceID, monitor);
					// response =
					// resource.path(serviceID).path("status").get(ClientResponse.class);

				} else {
					log.debug("Response: " + response.toString()
							+ "\nreason: " + response.getEntity(String.class));
					throw (new OptimisDeploymentException(
							"Error deploying service because of "
									+ response.getEntity(String.class)));
				}
				// Add the
				ServiceManagerView smview = (ServiceManagerView) PlatformUI
						.getWorkbench()
						.getActiveWorkbenchWindow()
						.getActivePage()
						.showView(
								"es.bsc.servicess.ide.views.ServiceManagerView");
				// TODO change to pending
				smview.addNewDeployedService(serviceID,
						new OptimisDeploymentChecker(op_prop.getSMLocation()),
						DeploymentChecker.PENDING);

			} else {
				throw (new OptimisDeploymentException(
						"Error creating SD client"));
			}
			monitor.done();
		} else {
			throw (new OptimisDeploymentException("Error incorrect location"));
		}

	}

	/** 
	 * Monitor the progress of the service deployment process
	 * 
	 * @param resource Deployment Service web resource URL
	 * @param serviceID Service Identifier
	 * @param monitor Progress monitor
	 * @throws InterruptedException
	 * @throws OptimisDeploymentException
	 */
	private void monitorProgress(WebResource resource, String serviceID,
			IProgressMonitor monitor) throws InterruptedException,
			OptimisDeploymentException {
		int retries = 0;
		int progress = 0;
		ClientResponse response;
		while (progress >= 0 && progress < 100 & retries < 30) {
			Thread.sleep(10000);
			response = resource.path(serviceID).path("status")
					.get(ClientResponse.class);
			if (response.getStatus() == com.sun.jersey.api.client.ClientResponse.Status.OK.getStatusCode()) {
				String resp = response.getEntity(String.class);
				if (resp.contains("ERROR")) {
					throw (new OptimisDeploymentException(resp));
				} else if (resp.contains("PROGRESS")) {
					int st = resp.indexOf(":", resp.indexOf("MESSAGE")) + 2;
					String prog = resp.substring(st, resp.indexOf("%", st));
					try {
						int new_progress = Integer.parseInt(prog);
						if (new_progress <= 40) {
							monitor.subTask("Evaluating TREC and Selecting Provider");
						} else if (new_progress > 40 && new_progress <= 45) {
							monitor.subTask("Contectualizing service VM");
						} else if (new_progress > 45 && new_progress < 95) {
							monitor.subTask("Uploading images");
						} else if (new_progress >= 95 && new_progress < 100) {
							monitor.subTask("Creating Agreement");
						}
						monitor.worked(new_progress - progress);
						progress = new_progress;
						log.debug("Progressing...(" + progress + ")");
					} catch (Exception e) {
						log.error("Error getting progress from "
								+ prog + " Response is: " + resp);
						throw (new OptimisDeploymentException(
								"Error getting progress from " + prog
										+ ". Response is: " + resp));
					}
				} else {
					throw (new OptimisDeploymentException("Unknown response: "
							+ resp));

				}
			} else {
				throw (new OptimisDeploymentException(
						"Error getting service deployment status: "
								+ response.toString() + "\nreason: "
								+ response.getEntity(String.class)));
			}
		}
	}

	@Override
	public void diposeComposite() {
		/*
		 * link.dispose(); //sdo_location.dispose(); serverLabel.dispose();
		 * serverText.dispose(); ics_location.dispose(); icsLabel.dispose();
		 * icsText.dispose(); icsButton.dispose();
		 * trec_type_composite.dispose(); trec_type_composite.layout(true);
		 * trec_composite.dispose(); trec_composite.layout(true);
		 */
		// composite.dispose();
		// composite.layout(true);
	}

	/**
	 * Get all the generated service packages
	 * 
	 * @return Array of package names
	 */
	private String[] getAllPackages() {
		try {
			ProjectMetadata pr_meta = new ProjectMetadata(super.getEditor()
					.getMetadataFile().getRawLocation().toFile());
			String[] packs = pr_meta.getPackages();
			if (packs!= null && packs.length>0){
				String[] oe_packs = pr_meta.getPackagesWithOrchestration();
				String[] all_packs;
				if (oe_packs!=null&& oe_packs.length>0)
					all_packs = packs;
				else{
					all_packs = new String[packs.length + 1];
					all_packs[0] = editor.getProject().getProject().getName();
					for (int i = 0; i < packs.length; i++) {
						all_packs[i + 1] = packs[i];
					}
				}
				return all_packs;
			}else{
				log.warn("No elements found.");
				return new String[0];
			}
				
		} catch (Exception e) {
			log.error("Exception getting elements.",e);
			return new String[0];
		}
	}

	/**
	 * Open the current service manifest file for editing
	 */
	protected void editServiceManifest() {
		try {
			writeManifestToFile();
			IFile sm = getProject().getProject()
					.getFolder(ProjectMetadata.OUTPUT_FOLDER)
					.getFolder(ProjectMetadata.PACKAGES_FOLDER)
					.getFile(ProjectMetadata.SERVICE_MANIFEST);
			IDE.openEditor(this.getWorkbenchPage(), sm);
		} catch (PartInitException e) {
			log.error("Exception opening manifest", e);
			ErrorDialog.openError(getShell(), "Opening service manifest",
					e.getMessage(), new Status(IStatus.ERROR, Activator.PLUGIN_ID, e.getMessage(), e));
		}
	}

	/**
	 * Generate a new service manifest
	 * @throws Exception 
	 */
	protected void generateNewManifest() throws Exception {
		ProjectMetadata pr_meta = new ProjectMetadata(super.getEditor()
				.getMetadataFile().getRawLocation().toFile());
		HashMap<String, ServiceElement> allEls = CommonFormPage.getElements(
				pr_meta.getAllOrchestrationClasses(), ProjectMetadata.BOTH_TYPE, super.getProject(), pr_meta);
		//String frontend_id = ManifestCreation.generateManifestName(editor.getProject().getProject().getName());
		InputStream in = this.getClass().getResourceAsStream(
				"sm_default.properties");
		Properties properties = new Properties();
		properties.load(in);
		in.close();
		// manifest =
		// Manifest.Factory.newInstance(editor.getProject().getProject().getName(),
		// frontend_id, properties);
		Manifest oldManifest = manifest;
		String[] oePacks = pr_meta.getPackagesWithOrchestration();
		if (oePacks == null || oePacks.length <= 0) {
			oePacks = new String[]{editor.getProject().getProject().getName()};
		}
			manifest = Manifest.Factory.newInstance(editor.getProject()
					.getProject().getName(), ManifestCreation.generateManifestName(oePacks[0]));
			for (String p : oePacks) {
				log.debug("Creating Component for package " + p );
				String componentID = ManifestCreation.generateManifestName(p);
				VirtualMachineComponent component = manifest.getVirtualMachineDescriptionSection().
						getVirtualMachineComponentById(componentID);
				if (component == null){
					component = manifest.getVirtualMachineDescriptionSection()
						.addNewVirtualMachineComponent(componentID);
				}
				ManifestCreation.setComponentDescription(component, pr_meta, p, super.getProject(), 
						this.manifest, allEls, false, this.op_prop);
				VirtualMachineComponentConfiguration comp_conf = manifest.getServiceProviderExtensionSection().
						getVirtualMachineComponentConfiguration(componentID);
				if (comp_conf == null){
					comp_conf = manifest.getServiceProviderExtensionSection()
						.addNewVirtualMachineComponentConfiguration(componentID);
				}
				comp_conf.enableSSHSecurity();
				/*if (allowFederated.getSelection())
					comp_conf.enableVPNSecurity();*/
			}
		String[] cePacks = pr_meta.getPackagesWithCores();
		if (cePacks != null && cePacks.length > 0) {
			for (String p : cePacks) {
				log.debug("Creating Component for package " + p );
				String componentID = ManifestCreation.generateManifestName(p);
				VirtualMachineComponent component = manifest.getVirtualMachineDescriptionSection()
						.addNewVirtualMachineComponent(componentID);
				ManifestCreation.setComponentDescription(component, pr_meta, p, super.getProject(), 
						this.manifest, allEls, false, this.op_prop);
				VirtualMachineComponentConfiguration comp_conf = manifest.getServiceProviderExtensionSection()
						.addNewVirtualMachineComponentConfiguration(componentID);
				comp_conf.enableSSHSecurity();
				/*if (allowFederated.getSelection())
					comp_conf.enableVPNSecurity();*/
			}
		}else{
			log.warn("No packages found generating only master");
		}
		//Set Default Affinity - Anti-Affinity Rules
		ManifestCreation.initAffinityDescription(manifest,oldManifest,oePacks, cePacks, pr_meta);
		
		//Set Default TREC (Minimum)
		ManifestCreation.initTRECDescription(manifest,oldManifest,oePacks, cePacks, pr_meta);
		
		//Set DataProtection Section
		ManifestCreation.initDataProtectionDescription(manifest, allEls, oldManifest, pr_meta,
				oePacks, cePacks, super.getProject());
		
		ManifestCreation.setImagesInManifest(pr_meta.getImageURLPackagesMap(), this.manifest);
	}

	/**
	 * Write the service manifest to a file
	 */
	protected void writeManifestToFile() {
		try {
			final ProgressMonitorDialog dialog = new ProgressMonitorDialog(
					getShell());
			final IFile sm = getProject().getProject()
					.getFolder(ProjectMetadata.OUTPUT_FOLDER)
					.getFolder(ProjectMetadata.PACKAGES_FOLDER)
					.getFile(ProjectMetadata.SERVICE_MANIFEST);
			dialog.run(true, true, new IRunnableWithProgress() {
				public void run(IProgressMonitor monitor)
						throws InvocationTargetException {
					try {
						if (sm.exists()) {
							sm.delete(true, monitor);
						}
						if (manifest == null) {
							generateNewManifest();
						}
						log.debug("writing the manifest in the file ");
						sm.create(new ByteArrayInputStream(manifest.toString()
								.getBytes()), true, monitor);
					} catch (Exception e) {
						log.debug("Exception writing manifest");
						throw (new InvocationTargetException(e));
					}
				}
			});
		} catch (InvocationTargetException e) {
			log.error("Exception writing manifest", e);
			ErrorDialog.openError(getShell(), "Error writing manifest",
					e.getMessage(),new Status(IStatus.ERROR, Activator.PLUGIN_ID, "Invocation Exception", e) );
		} catch (InterruptedException e) {
			log.error("Exception writing manifest", e);
			ErrorDialog.openError(getShell(), "Building interrumped",
					e.getMessage(), new Status(IStatus.ERROR, Activator.PLUGIN_ID, "Interruption Exception", e));
		}
	}

	/**
	 * Read the service manifest form a existing file
	 * 
	 * @throws IOException
	 */
	protected void readManifestFromFile() throws IOException {
		log.debug("Reading manifest file");
		StringBuffer manifestData = new StringBuffer(1000);
		BufferedReader reader = new BufferedReader(new FileReader(getProject()
				.getProject().getFolder(ProjectMetadata.OUTPUT_FOLDER)
				.getFolder(ProjectMetadata.PACKAGES_FOLDER).getLocation()
				.append(ProjectMetadata.SERVICE_MANIFEST).toFile()));
		char[] buf = new char[1024];
		int numRead = 0;
		while ((numRead = reader.read(buf)) != -1) {
			String readData = String.valueOf(buf, 0, numRead);
			manifestData.append(readData);
			buf = new char[1024];
		}
		reader.close();
		manifest = Manifest.Factory.newInstance(manifestData.toString());
	}

}