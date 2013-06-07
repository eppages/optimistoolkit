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

import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jface.operation.IRunnableContext;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import es.bsc.servicess.ide.Logger;
import es.bsc.servicess.ide.model.CoreElementParameter;
import es.bsc.servicess.ide.model.Parameter;

/** Dialog class for create/modify a core element parameter dialog
 * @author jorgee
 *
 */
public class ModifyCoreElementParameterDialog extends ModifyParameterDialog {

	private Combo direction;
	
	private static Logger log = Logger.getLogger(ModifyCoreElementParameterDialog.class);

	/** Constructor
	 * @param cont Parent context
	 * @param shell Parent shell
	 * @param p Current parameter ( null if new)
	 * @param project Core Element's project
	 * @param enabled create/modify flag
	 */
	public ModifyCoreElementParameterDialog(IRunnableContext cont, Shell shell,
			Parameter p, IJavaProject project, boolean enabled) {
		super(cont, shell, p, project, enabled);
	}

	/* (non-Javadoc)
	 * @see es.bsc.servicess.ide.dialogs.ModifyParameterDialog#createDialogArea(org.eclipse.swt.widgets.Composite)
	 */
	public Composite createDialogArea(Composite parent) {
		Composite composite = super.createDialogArea(parent);
		Label directionLabel = new Label(composite, SWT.NONE);
		directionLabel.setText("Direction");
		direction = new Combo(composite, SWT.NONE);
		direction.setItems(new String[] { "IN", "OUT", "INOUT" });

		GridData rd = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		rd.grabExcessHorizontalSpace = true;
		direction.setLayoutData(rd);
		if (p != null) {
			direction.setText(((CoreElementParameter) p).getDirection());
		} else
			direction.select(0);
		direction.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent arg0) {
				updateDirection();
			}
		});

		return composite;

	}

	/* (non-Javadoc)
	 * @see es.bsc.servicess.ide.dialogs.ModifyParameterDialog#updateElementName()
	 */
	protected void updateElementName() {
		if (p == null && nameText.getText().trim().length() > 0
				&& oeReturnTypeText.getText().trim().length() > 0
				&& direction.getText().trim().length() > 0) {
			p = new CoreElementParameter(oeReturnTypeText.getText().trim(),
					nameText.getText().trim(), direction.getText().trim());
			log.debug("creating parameter: "
					+ oeReturnTypeText.getText().trim() + " "
					+ nameText.getText().trim() + " "
					+ direction.getText().trim());
		} else if (p != null) {
			p.setName(nameText.getText().trim());
		}
	}

	/* (non-Javadoc)
	 * @see es.bsc.servicess.ide.dialogs.ModifyParameterDialog#updateType()
	 */
	protected void updateType() {
		if (p == null && nameText.getText().trim().length() > 0
				&& oeReturnTypeText.getText().trim().length() > 0
				&& direction.getText().trim().length() > 0
				&& direction.getText().trim().length() > 0) {
			p = new CoreElementParameter(oeReturnTypeText.getText().trim(),
					nameText.getText().trim(), direction.getText().trim());
			log.debug("creating parameter: "
					+ oeReturnTypeText.getText().trim() + " "
					+ nameText.getText().trim() + " "
					+ direction.getText().trim());
		} else if (p != null) {
			p.setType(oeReturnTypeText.getText().trim());
		}
	}

	/** Update the core element parameter direction
	 * 
	 */
	protected void updateDirection() {
		if (p == null && nameText.getText().trim().length() > 0
				&& oeReturnTypeText.getText().trim().length() > 0
				&& direction.getText().trim().length() > 0
				&& direction.getText().trim().length() > 0) {
			p = new CoreElementParameter(oeReturnTypeText.getText().trim(),
					nameText.getText().trim(), direction.getText().trim());
			log.debug("creating parameter: "
					+ oeReturnTypeText.getText().trim() + " "
					+ nameText.getText().trim() + " "
					+ direction.getText().trim());
		} else if (p != null) {
			((CoreElementParameter) p).setDirection(direction.getItem(
					direction.getSelectionIndex()).trim());
		}
	}

}
