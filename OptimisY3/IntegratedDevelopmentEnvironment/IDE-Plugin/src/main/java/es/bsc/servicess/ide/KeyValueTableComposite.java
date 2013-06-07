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

package es.bsc.servicess.ide;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.jdt.internal.ui.dialogs.StatusInfo;
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

import es.bsc.servicess.ide.dialogs.ModifyKeyValueDialog;

/** Implements a general composite widget with a table to show different key-value maps in the plug-in
 * 
 * @author Jorge Ejarque (Barcelona Supercomputing Center) 
 *
 */
public class KeyValueTableComposite {
	private FormToolkit toolkit;
	private Composite listsRow;
	protected Map keyValueMap;
	private Button removeButton;
	private String keyTitle;
	private String valueTitle;
	private Button addButton;
	private Button modButton;
	private Table kvTable;
	private Shell shell;
	private boolean withBtns;
	private boolean removable;

	/**Constructor
	 * @param shell Shell of the Eclipse runtime
	 * @param toolkit Toolkit of the form where the table is going to be located
	 * @param keyTitle
	 * @param valueTitle
	 * @param withBtns True for enabling table modification buttons
	 * @param removable True for enabling the option of removing entries in the table
	 */
	public KeyValueTableComposite(Shell shell, FormToolkit toolkit,
			String keyTitle, String valueTitle, boolean withBtns,
			boolean removable) {
		this.shell = shell;
		this.toolkit = toolkit;
		this.valueTitle = valueTitle;
		this.keyTitle = keyTitle;
		this.withBtns = withBtns;
		this.removable = removable;
		this.keyValueMap = new HashMap<String, String>();
	}

	/** Create the composite
	 * @param composite Parent composite where the table is going to be created
	 * @return Table composite
	 */
	public Composite createComposite(Composite composite) {
		if (toolkit != null)
			listsRow = toolkit.createComposite(composite, SWT.BORDER);
		else
			listsRow = new Composite(composite, SWT.BORDER);
		GridData rd = new GridData(GridData.FILL_HORIZONTAL
				| GridData.VERTICAL_ALIGN_BEGINNING);
		rd.grabExcessHorizontalSpace = true;
		listsRow.setLayoutData(rd);
		GridLayout firstRow1Layout = new GridLayout();
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
				if (withBtns) {
					modButton.setEnabled(true);
					if (removable)
						removeButton.setEnabled(true);
				}
			}
		});
		rd = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		rd.grabExcessHorizontalSpace = true;
		rd.grabExcessVerticalSpace = true;
		rd.minimumHeight = 90;
		kvTable.setLayoutData(rd);
		if (withBtns) {
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
				addButton = toolkit
						.createButton(parButtons, "Add...", SWT.NONE);
			else {
				addButton = new Button(parButtons, SWT.NONE);
				addButton.setText("Add...");
			}
			rd = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
			rd.grabExcessHorizontalSpace = true;
			addButton.setLayoutData(rd);
			addButton.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent event) {
					modifyItem(-1);
				}
			});
			if (toolkit != null)
				modButton = toolkit.createButton(parButtons, "Modify...",
						SWT.NONE);
			else {
				modButton = new Button(parButtons, SWT.NONE);
				modButton.setText("Modify...");
			}
			modButton.setEnabled(false);
			rd = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
			rd.grabExcessHorizontalSpace = true;
			modButton.setLayoutData(rd);
			modButton.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent event) {
					modifyItem(kvTable.getSelectionIndex());
				}
			});
			modButton.setEnabled(false);
			if (removable) {
				if (toolkit != null)
					removeButton = toolkit.createButton(parButtons, "Delete",
						SWT.NONE);
				else {
					removeButton = new Button(parButtons, SWT.NONE);
					removeButton.setText("Delete");
				}
				removeButton.setEnabled(false);
				rd = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
				rd.grabExcessHorizontalSpace = true;
				removeButton.setLayoutData(rd);
				removeButton.addSelectionListener(new SelectionAdapter() {
					public void widgetSelected(SelectionEvent event) {
						kvTable.remove(kvTable.getSelectionIndex());
					}
				});
				removeButton.setEnabled(false);
			}
		}
		return listsRow;

	}

	/** Get the current key-value map stored in the table 
	 * @return key-value map
	 */
	public Map<String, String> getKeyValueMap() {
		/*Map<String, String> map = new HashMap<String, String>();
		TableItem[] items = kvTable.getItems();
		for (TableItem it : items) {
			map.put(it.getText(0).trim(), it.getText(1).trim());
		}
		return map;*/
		return keyValueMap;
	}
	
	/** Set the key-value map to the table
	 * @param map key-value map
	 */
	public void setKeyValueMap(Map<String, String> map) {
		keyValueMap = map;
		kvTable.removeAll();
		for (Entry<String, String> e : map.entrySet()) {
			TableItem it = new TableItem(kvTable, SWT.NONE);
			it.setText(new String[] { e.getKey(), e.getValue() });
		}
	}

	/**Enable the addition of new key-value entries to the table and map
	 * 
	 */
	public void enableAdditions() {
		if (withBtns)
			addButton.setEnabled(true);
	}

	/**Modify an Item of the table
	 * @param selection
	 */
	@SuppressWarnings("restriction")
	private void modifyItem(int selection) {
		String[] p = null;
		boolean modified = false;
		if (selection >= 0) {
			p = new String[] { kvTable.getItem(selection).getText(0).trim(),
					kvTable.getItem(selection).getText(1).trim() };
			modified = true;
		}
		try {
			ModifyKeyValueDialog dialog = new ModifyKeyValueDialog(this.shell,
					p, this.keyTitle, this.valueTitle, modified);

			if (dialog.open() == Window.OK) {
				p = dialog.getKeyValuePair();
				if (p != null) {
					if (checkValue(p)){
						TableItem it;
						if (selection < 0) {
							it = new TableItem(kvTable, SWT.NONE);
						} else {
							it = kvTable.getItem(selection);
						
						}
						keyValueMap.put(p[0], p[1]);
						it.setText(p);
						updateInfo();
					}else{
						throw(new Exception("Incorrect value for " +p[0]+"("+p[1]+")"));
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			ErrorDialog.openError(this.shell, "Error Modifying values",
					e.getMessage(),
					new StatusInfo(IStatus.ERROR, e.getMessage()));
		}

	}

	protected void updateInfo() {
		// TODO Auto-generated method stub
		
	}

	/**Check the content of the key and value introduced
	 * @param p Key Value pair
	 * @return true if correct, otherwise false
	 * @throws Exception
	 */
	protected boolean checkValue(String[] p) throws Exception{
		return true;
	}

	/**Reset the values of the table and map, removing all the values.
	 * 
	 */
	public void reset() {
		kvTable.removeAll();
		keyValueMap = new HashMap<String, String>();

	}

	/** Enable or disable the key-value table composite
	 * @param b True for enabling , false for disabling
	 */
	public void setEnabled(boolean b) {
		kvTable.setEnabled(b);
		if (!b) {
			if (withBtns) {
				addButton.setEnabled(false);
				modButton.setEnabled(false);
				if (removable)
					removeButton.setEnabled(false);
			}
		}

	}
}
