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

package es.bsc.servicess.ide.editors;

import java.util.List;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;

import es.bsc.servicess.ide.Activator;
import es.bsc.servicess.ide.Logger;
import es.bsc.servicess.ide.ProjectMetadata;
import es.bsc.servicess.ide.dialogs.AddDependencyDialog;
import es.bsc.servicess.ide.model.Dependency;
import es.bsc.servicess.ide.model.ServiceElement;

/**
 * Class for managing the dependency section of the service elements
 * @author Jorge Ejarque (Barcelona Supercomputing Center)
 *
 */
public class DependencySection {
	private FormToolkit toolkit;
	private ScrolledForm form;
	private Shell shell;
	private Table depTable;
	private ServiceElement currentServiceElement;
	private Button addDepButton;
	private Button deleteDepButton;
	private ServiceFormEditor editor;
	private Section dep_section;
	private static Logger log = Logger.getLogger(DependencySection.class);
	
	/** 
	 * Constructor
	 * @param form Parent's form
	 * @param toolkit Parent's toolkit
	 * @param shell Parent's shell
	 * @param editor Parent's editor
	 */
	public DependencySection(ScrolledForm form, FormToolkit toolkit,
			Shell shell, ServiceFormEditor editor) {
		this.form = form;
		this.toolkit = toolkit;
		this.shell = shell;
		this.editor = editor;
	}

	/** 
	 * Set the dependency section element 
	 * @param element Service element which dependency section is bound
	 */
	public void setCurrentElement(ServiceElement element) {
		deleteTableItems();
		this.currentServiceElement = element;
		addDepButton.setEnabled(true);
		printElementDependencies();
	}

	/**
	 *  Delete all the current element dependencies
	 */
	private void deleteTableItems() {
		depTable.removeAll();
	}

	/**
	 * Print current element dependencies
	 */
	public void printElementDependencies() {
		try {
			ProjectMetadata pr_meta = new ProjectMetadata(editor
					.getMetadataFile().getRawLocation().toFile());
			List<Dependency> deps = pr_meta
					.getDependencies(new String[] { currentServiceElement
							.getLabel() });
			for (Dependency d : deps) {
				TableItem it = new TableItem(depTable, SWT.NONE);
				it.setText(new String[] { d.getType(), d.getLocation() });
			}
		} catch (Exception e) {
			log.error("Error Modifying constraint");
			e.printStackTrace();
			ErrorDialog.openError(this.shell, "Error Modifying constraint", e
					.getMessage(), new Status(IStatus.ERROR, Activator.PLUGIN_ID,
					"Error Modifying dependency"));
		}
	}

	/**
	 * Create the dependency section in the selected composite
	 * @param composite Parent's composite
	 */
	public void createComposite(Composite composite) {
		dep_section = toolkit.createSection(composite, Section.TWISTIE);
		dep_section.setText("Dependencies Description");
		GridData rd = new GridData(GridData.HORIZONTAL_ALIGN_FILL
				| GridData.VERTICAL_ALIGN_BEGINNING);
		rd.grabExcessHorizontalSpace = true;
		dep_section.setLayoutData(rd);
		dep_section.setLayout(new GridLayout(1, true));
		Composite com = toolkit.createComposite(dep_section, SWT.NONE);
		rd = new GridData(GridData.HORIZONTAL_ALIGN_FILL
				| GridData.VERTICAL_ALIGN_BEGINNING);
		rd.grabExcessHorizontalSpace = true;
		com.setLayoutData(rd);
		com.setLayout(new GridLayout(2, false));
		depTable = toolkit.createTable(com, SWT.SINGLE | SWT.BORDER
				| SWT.FULL_SELECTION);
		depTable.setHeaderVisible(true);
		depTable.setLinesVisible(true);
		TableColumn depType = new TableColumn(depTable, SWT.NULL);
		depType.setText("Dependency Type");
		depType.pack();
		TableColumn depPath = new TableColumn(depTable, SWT.NULL);
		depPath.setText("Path");
		depPath.pack();
		rd = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		rd.grabExcessHorizontalSpace = true;
		rd.grabExcessVerticalSpace = true;
		rd.minimumHeight = 90;
		depTable.setLayoutData(rd);
		Composite consButtons = toolkit.createComposite(com, SWT.NONE);
		GridLayout tableLayout = new GridLayout();
		tableLayout.numColumns = 1;
		tableLayout.marginLeft = 0;
		tableLayout.marginRight = 0;
		consButtons.setLayout(tableLayout);
		addDepButton = toolkit.createButton(consButtons, "Add...", SWT.NONE);
		rd = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		rd.grabExcessHorizontalSpace = true;
		addDepButton.setLayoutData(rd);
		addDepButton.setEnabled(false);
		deleteDepButton = toolkit.createButton(consButtons, "Delete", SWT.NONE);
		deleteDepButton.setEnabled(false);
		rd = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		rd.grabExcessHorizontalSpace = true;
		deleteDepButton.setLayoutData(rd);
		dep_section.setClient(com);
		dep_section.setExpanded(true);
		dep_section.setExpanded(false);
		addListeners();
	}

	/**
	 * Add a dependency
	 */
	private void AddDependency() {
		try {
			AddDependencyDialog dialog = new AddDependencyDialog(this.shell);
			if (dialog.open() == Window.OK) {
				Dependency d = dialog.getDependency();
				if (d != null) {
					ProjectMetadata pr_meta = new ProjectMetadata(editor
							.getMetadataFile().getRawLocation().toFile());
					pr_meta.addElementToDependency(d.getLocation(),
							d.getType(), currentServiceElement.getLabel());
					pr_meta.toFile(editor.getMetadataFile().getRawLocation()
							.toFile());
					TableItem it = new TableItem(depTable, SWT.NONE);
					it.setText(new String[] { d.getType(), d.getLocation() });
				}
			}
		} catch (Exception e) {
			log.error("Error Modifying constraint");
			e.printStackTrace();
			ErrorDialog.openError(this.shell, "Error Modifying constraint", e
					.getMessage(), new Status(IStatus.ERROR, Activator.PLUGIN_ID,
					"Error Modifying dependency"));
		}
	}

	/**
	 * Delete a dependency
	 * @param selection Index of the dependency to be deleted
	 */
	private void deleteDependency(int selection) {
		if (selection >= 0) {
			try {
				ProjectMetadata pr_meta = new ProjectMetadata(editor
						.getMetadataFile().getRawLocation().toFile());
				pr_meta.removeElementFromDependency(depTable.getItem(selection)
						.getText(1).trim(), currentServiceElement.getLabel());
				pr_meta.toFile(editor.getMetadataFile().getRawLocation()
						.toFile());
				depTable.remove(selection);
			} catch (Exception e) {
				log.error("Error deleting contraint");
				e.printStackTrace();
				ErrorDialog.openError(this.shell, "Error Modifying constraint",
						e.getMessage(), new Status(IStatus.ERROR, Activator.PLUGIN_ID,
								"Error Deleting Dependency"));
			}
		}
	}

	/**
	 * Add the dependency section widget listeners
	 */
	private void addListeners() {
		depTable.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				deleteDepButton.setEnabled(true);
			}
		});
		addDepButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				AddDependency();
			}
		});
		deleteDepButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				deleteDependency(depTable.getSelectionIndex());
			}
		});
	}

	/**
	 * Enable/disable the modification of element dependencies
	 * @param b True for enabling, false for disabling
	 */
	public void setEnabled(boolean b) {
		if (!b)
			dep_section.setExpanded(false);
		dep_section.setEnabled(b);
	}

	public void reset() {
		depTable.removeAll();
		addDepButton.setEnabled(true);
		deleteDepButton.setEnabled(false);
		dep_section.setExpanded(false);
		dep_section.setEnabled(false);
	}
}