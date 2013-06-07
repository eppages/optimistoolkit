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
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import es.bsc.servicess.ide.Activator;

public class ModifyKeyValueDialog extends Dialog {
	private String[] p;
	private Text keyText;
	private Text valueText;
	private boolean modified;
	private String keyTitle;
	private String valueTitle;

	/** Constructor
	 * @param shell Parent's shell
	 * @param p Current key-value pair (null if new)
	 * @param keyTitle Name of the key field
	 * @param valueTitle Name of the value field
	 * @param b create/modify flag
	 */
	public ModifyKeyValueDialog(Shell shell, String[] p, String keyTitle,
			String valueTitle, boolean b) {
		super(shell);
		this.p = p;
		this.modified = b;
		this.keyTitle = keyTitle;
		this.valueTitle = valueTitle;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.Dialog#createDialogArea(org.eclipse.swt.widgets.Composite)
	 */
	public Composite createDialogArea(Composite parent) {
		Composite composite = (Composite) super.createDialogArea(parent);
		composite.setLayout(new GridLayout(2, false));
		Label keyLabel = new Label(composite, SWT.NONE);
		keyLabel.setText(keyTitle);
		keyText = new Text(composite, SWT.SINGLE | SWT.BORDER);
		keyText.setEnabled(!modified);
		GridData rd = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		rd.grabExcessHorizontalSpace = true;
		keyText.setLayoutData(rd);
		keyText.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent arg0) {
				updateKey();
			}
		});
		// Value
		Label valueLabel = new Label(composite, SWT.NONE);
		valueLabel.setText(valueTitle);
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
			keyText.setText(p[0]);
			valueText.setText(p[1]);
		}
		return composite;
	}

	/** Update the key
	 * 
	 */
	protected void updateKey() {
		if (p == null && keyText.getText().trim().length() > 0
				&& valueText.getText().trim().length() > 0) {
			p = new String[] { keyText.getText().trim(),
					valueText.getText().trim() };
		} else if (p != null) {
			p[0] = keyText.getText().trim();
		}
	}

	/** Update the value
	 * 
	 */
	protected void updateValue() {
		if (p == null && keyText.getText().trim().length() > 0
				&& valueText.getText().trim().length() > 0) {
			p = new String[] { keyText.getText().trim(),
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
			ErrorDialog.openError(getShell(), "Error", "Error getting values",
					new Status(IStatus.ERROR, Activator.PLUGIN_ID, "There are missing fields"));
		}
	}

	/** Get the created/modified key-value pair
	 * @return key-value pair
	 */
	public String[] getKeyValuePair() {
		return p;
	}
}
