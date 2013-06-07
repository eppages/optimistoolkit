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
import es.bsc.servicess.ide.Logger;

/** Dialog to add or modify a service location
 * @author Jorge Ejarque (Barcelona Supercomputing Center)
 *
 */
public class AddServiceLocationDialog extends Dialog {
	protected Text locationText;
	protected String location;
	
	private static Logger log = Logger.getLogger(AddServiceLocationDialog.class);

	/** Constructor
	 * @param shell Eclipse runtime shell
	 * @param location current location
	 */
	public AddServiceLocationDialog(Shell shell, String location) {
		super(shell);
		this.location = location;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.Dialog#createDialogArea(org.eclipse.swt.widgets.Composite)
	 */
	public Composite createDialogArea(Composite parent) {
		Composite composite = (Composite) super.createDialogArea(parent);
		composite.setLayout(new GridLayout(2, false));
		Label oeNameLabel = new Label(composite, SWT.NONE);
		oeNameLabel.setText("Location");
		locationText = new Text(composite, SWT.SINGLE | SWT.BORDER);
		GridData rd = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		rd.grabExcessHorizontalSpace = true;
		locationText.setLayoutData(rd);
		if (location != null) {
			locationText.setText(location.trim());
		}
		locationText.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent arg0) {
				updateLocation();
			}
		});

		return composite;

	}

	/** Update the location of the service to be added or modified
	 * 
	 */
	protected void updateLocation() {
		location = locationText.getText().trim();
		log.debug("Location set to: " + location);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.Dialog#okPressed()
	 */
	protected void okPressed() {
		if (location != null && location.trim().length() > 0) {
			super.okPressed();
		} else {
			ErrorDialog.openError(getShell(), "Error",
					"There are missing fields", new Status(IStatus.ERROR,Activator.PLUGIN_ID, "Missing Fields"));
		}
	}

	/** Get the location of the service to be added or modified
	 * @return
	 */
	public String getLocation() {

		return location;
	}

}
