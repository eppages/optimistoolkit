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

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**Dialog for modifying an element argument
 * @author Jorge Ejarque (Barcelona Supercomputing Center)
 *
 */
public class ModifyArgumentDialog extends Dialog {
	protected String argument;
	protected Text argumentText;

	/** Constructor
	 * @param shell Parent window shell
	 * @param argument current argument name
	 */
	public ModifyArgumentDialog(Shell shell,
			String argument) {
		super(shell);
		this.argument = argument;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.Dialog#createDialogArea(org.eclipse.swt.widgets.Composite)
	 */
	public Composite createDialogArea(Composite parent) {
		Composite composite = (Composite) super.createDialogArea(parent);
		composite.setLayout(new GridLayout(2, false));
		Label oeNameLabel = new Label(composite, SWT.NONE);
		oeNameLabel.setText("Name");
		argumentText = new Text(composite, SWT.SINGLE | SWT.BORDER);
		GridData rd = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		rd.grabExcessHorizontalSpace = true;
		argumentText.setLayoutData(rd);
		argumentText.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent arg0) {
				updateArgumentName();
			}
		});
		
		if (argument != null) {
			argumentText.setText(argument);
			
		}
		return composite;

	}

	/** Update the name of the argument
	 * 
	 */
	protected void updateArgumentName() {
			argument = argumentText.getText().trim();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.Dialog#okPressed()
	 */
	protected void okPressed() {
		if (argument != null) {
			super.okPressed();
		} else {
			MessageDialog.openError(getShell(), "Error",
					"There are missing fields");
		}
	}

	/** Getter for the element argument 
	 * @return
	 */
	public String getArgument() {

		return argument;
	}

}
