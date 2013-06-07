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

import java.util.List;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import es.bsc.servicess.ide.Activator;

/**Dialog class to create/modify the constraints
 * @author Jorge Ejarque (Barcelona Supercomputing Center)
 *
 */
public class ModifyConstraintsDialog extends Dialog {
	private String[] p;
	private Combo nameText;
	private Text valueText;
	private boolean modified;
	private List<String> names;

	/**Constructor
	 * @param shell Parent shell
	 * @param p Current constraint's name-value pair
	 * @param names Possible constraint names
	 * @param b True is constraint, false if new constraint, 
	 */
	public ModifyConstraintsDialog(Shell shell, String[] p, List<String> names,
			boolean b) {
		super(shell);
		this.names = names;
		this.p = p;
		this.modified = b;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.Dialog#createDialogArea(org.eclipse.swt.widgets.Composite)
	 */
	public Composite createDialogArea(Composite parent) {
		Composite composite = (Composite) super.createDialogArea(parent);
		composite.setLayout(new GridLayout(2, false));
		Label oeNameLabel = new Label(composite, SWT.NONE);
		oeNameLabel.setText("Name");
		nameText = new Combo(composite, SWT.SINGLE | SWT.BORDER);

		nameText.setEnabled(!modified);
		nameText.setItems(names.toArray(new String[names.size()]));
		GridData rd = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		rd.grabExcessHorizontalSpace = true;
		nameText.setLayoutData(rd);
		nameText.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent arg0) {
				updateElementName();
			}
		});
		// Value
		Label oeReturnTypeLabel = new Label(composite, SWT.NONE);
		oeReturnTypeLabel.setText("Value");
		valueText = new Text(composite, SWT.SINGLE | SWT.BORDER);
		valueText.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent arg0) {
				updateValue();
			}
		});
		rd = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		rd.grabExcessHorizontalSpace = true;
		valueText.setLayoutData(rd);
		if (p != null) {
			nameText.setText(p[0]);
			valueText.setText(p[1]);
		}
		return composite;
	}

	/**Update constraint name
	 * 
	 */
	protected void updateElementName() {
		if (p == null && nameText.getText().trim().length() > 0
				&& valueText.getText().trim().length() > 0) {
			p = new String[] { nameText.getText().trim(),
					valueText.getText().trim() };
		} else if (p != null) {
			p[0] = nameText.getText().trim();
		}
	}

	/**Update constraint value
	 * 
	 */
	protected void updateValue() {
		if (p == null && nameText.getText().trim().length() > 0
				&& valueText.getText().trim().length() > 0) {
			p = new String[] { nameText.getText().trim(),
					valueText.getText().trim() };
		} else if (p != null) {
			p[1] = valueText.getText().trim();
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.Dialog#okPressed()
	 */
	protected void okPressed() {
		if (p != null) {
			super.okPressed();
		} else {
			ErrorDialog.openError(getShell(), "Error",
					"There are missing fields", new Status(IStatus.ERROR, Activator.PLUGIN_ID, "Name-value pair is null"));
		}
	}

	/** Getter for the constraint name-value pair
	 * @return name-value pair
	 */
	public String[] getConstraint() {
		return p;
	}

}
