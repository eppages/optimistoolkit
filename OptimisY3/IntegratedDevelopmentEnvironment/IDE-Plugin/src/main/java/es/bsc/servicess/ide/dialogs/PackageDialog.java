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

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import es.bsc.servicess.ide.Activator;
import es.bsc.servicess.ide.ProjectMetadata;

/** Dialog to add and modify packages in the Service Project
 * @author Jorge Ejarque (Barcelona Supercomputing Center)
 *
 */
public class PackageDialog extends Dialog {
	private String packageName;
	private Text pNameText;
	private String type;
	private String[] items;
	private boolean done;
	private Combo typeList;

	/** Constructor
	 * @param parentShell Eclipse runtime shell
	 */
	public PackageDialog(Shell parentShell, String[] items) {
		super(parentShell);
		this.items = items;
		done = false;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.Dialog#createDialogArea(org.eclipse.swt.widgets.Composite)
	 */
	public Composite createDialogArea(Composite parent) {
		Composite composite = (Composite) super.createDialogArea(parent);
		composite.setLayout(new GridLayout(2, false));
		Label projectLabel = new Label(composite, SWT.NONE);
		projectLabel.setText("Package Name");
		GridData rd = new GridData(GridData.HORIZONTAL_ALIGN_FILL
				| GridData.VERTICAL_ALIGN_BEGINNING);
		rd.grabExcessHorizontalSpace = true;
		projectLabel.setLayoutData(rd);
		pNameText = new Text(composite, SWT.SINGLE | SWT.BORDER);
		pNameText.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent arg0) {
				packageName = pNameText.getText().trim();
			}
		});
		rd = new GridData(GridData.HORIZONTAL_ALIGN_FILL
				| GridData.VERTICAL_ALIGN_BEGINNING);
		rd.grabExcessHorizontalSpace = true;
		pNameText.setLayoutData(rd);
		
		Label typeLabel = new Label(composite,SWT.BEGINNING);
		typeLabel.setText("Type"); 
		rd = new GridData(GridData.HORIZONTAL_ALIGN_FILL|GridData.VERTICAL_ALIGN_BEGINNING); 
		rd.grabExcessHorizontalSpace = true; 
		typeLabel.setLayoutData(rd);
		typeList = new Combo (composite, SWT.READ_ONLY | SWT.BORDER |SWT.DEFAULT); 
		typeList.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent event){ 
				type =typeList.getItem(typeList.getSelectionIndex()); 
			} 
		});
		 
		//type = ProjectMetadata.CORE_TYPE;
		rd = new GridData(GridData.HORIZONTAL_ALIGN_FILL
				| GridData.VERTICAL_ALIGN_BEGINNING);
		rd.grabExcessHorizontalSpace = true;
		typeList.setLayoutData(rd);
		done = true;
		if(items != null){ 
			 typeList.setItems(items); 
			 typeList.select(0); 
			 type= typeList.getItem(0); 
		}
		return composite;

	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.Dialog#okPressed()
	 */
	public void okPressed() {
		if (type != null && packageName != null) {
			super.okPressed();
		} else {
			ErrorDialog.openError(getShell(), "Error", "Parameters missing", 
					new Status(IStatus.ERROR, Activator.PLUGIN_ID, "Type or pacakge name is null"));
		}

	}

	/** Get the name of the created/modified service package
	 * @return Package name
	 */
	public String getPackageName() {
		return packageName;
	}

	/**Get the type of the created/modified service package
	 * @return
	 */
	public String getType() {
		return type;
	}

}
