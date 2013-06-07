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
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.search.IJavaSearchConstants;
import org.eclipse.jdt.core.search.IJavaSearchScope;
import org.eclipse.jdt.core.search.SearchEngine;
import org.eclipse.jdt.internal.ui.dialogs.FilteredTypesSelectionDialog;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableContext;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.sun.istack.logging.Logger;

import es.bsc.servicess.ide.Activator;
import es.bsc.servicess.ide.model.Parameter;

/** Dialog for modifying element parameters
 * @author Jorge Ejarque (Barcelona Supercomputing center
 *
 */
public class ModifyParameterDialog extends Dialog {
	protected Parameter p;
	protected Text nameText;
	protected Text oeReturnTypeText;
	private IJavaProject project;
	private IRunnableContext context;
	private boolean enabled;	
	private static Logger log = Logger.getLogger(ModifyParameterDialog.class);

	/** Constructor
	 * @param cont Runnable context
	 * @param shell Parent's shell
	 * @param p Current parameter
	 * @param project element's project
	 * @param enabled Create/modify flag
	 */
	public ModifyParameterDialog(IRunnableContext cont, Shell shell,
			Parameter p, IJavaProject project, boolean enabled) {
		super(shell);
		this.context = cont;
		this.p = p;
		this.project = project;
		this.enabled = enabled;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.Dialog#createDialogArea(org.eclipse.swt.widgets.Composite)
	 */
	public Composite createDialogArea(Composite parent) {
		Composite composite = (Composite) super.createDialogArea(parent);
		composite.setLayout(new GridLayout(2, false));
		Label oeNameLabel = new Label(composite, SWT.NONE);
		oeNameLabel.setText("Name");
		nameText = new Text(composite, SWT.SINGLE | SWT.BORDER);
		GridData rd = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		rd.grabExcessHorizontalSpace = true;
		nameText.setLayoutData(rd);
		nameText.setEnabled(enabled);
		nameText.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent arg0) {
				updateElementName();
			}
		});
		// Type
		Label oeReturnTypeLabel = new Label(composite, SWT.NONE);
		oeReturnTypeLabel.setText("Type");
		Composite oeReturn = new Composite(composite, SWT.NONE);
		rd = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		rd.grabExcessHorizontalSpace = true;
		rd.horizontalIndent = 0;
		rd.horizontalSpan = 0;
		oeReturn.setLayoutData(rd);
		GridLayout oeReturnLayout = new GridLayout();
		oeReturnLayout.numColumns = 2;
		oeReturnLayout.marginLeft = 0;
		oeReturnLayout.marginRight = 0;
		oeReturn.setLayout(oeReturnLayout);
		oeReturnTypeText = new Text(oeReturn, SWT.SINGLE | SWT.BORDER);
		oeReturnTypeText.setEnabled(enabled);
		oeReturnTypeText.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent arg0) {
				updateType();
			}
		});
		rd = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		rd.grabExcessHorizontalSpace = true;
		oeReturnTypeText.setLayoutData(rd);
		Button selectReturnButton = new Button(oeReturn, SWT.NONE);
		selectReturnButton.setText("Select Class...");
		selectReturnButton.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {
				selectType();
			}

			@Override
			public void widgetSelected(SelectionEvent arg0) {
				selectType();
			}
		});
		selectReturnButton.setEnabled(enabled);
		if (p != null) {
			nameText.setText(p.getName());
			oeReturnTypeText.setText(p.getType());
		}
		return composite;

	}

	/** 
	 * Update parameter name
	 */
	protected void updateElementName() {
		if (p == null && nameText.getText().trim().length() > 0
				&& oeReturnTypeText.getText().trim().length() > 0) {
			p = new Parameter(oeReturnTypeText.getText().trim(), nameText
					.getText().trim());
			System.out.println("creating parameter: "
					+ oeReturnTypeText.getText().trim() + " "
					+ nameText.getText().trim());
		} else if (p != null) {
			p.setName(nameText.getText().trim());
		}
	}

	/** 
	 * Update parameter type
	 */
	protected void updateType() {
		if (p == null && nameText.getText().trim().length() > 0
				&& oeReturnTypeText.getText().trim().length() > 0) {
			p = new Parameter(oeReturnTypeText.getText().trim(), nameText
					.getText().trim());
			System.out.println("creating parameter: "
					+ oeReturnTypeText.getText().trim() + " "
					+ nameText.getText().trim());
		} else if (p != null) {
			p.setType(oeReturnTypeText.getText().trim());
		}
	}

	/** 
	 * Open a dialog to select the parameter type
	 */
	protected void selectType() {
		if (project == null) {
			ErrorDialog.openError(getShell(), "Error",
					"There is no java project selected", new Status(IStatus.ERROR, Activator.PLUGIN_ID, "Project is null"));
		}

		IJavaElement[] elements = new IJavaElement[] { project };
		IJavaSearchScope scope = SearchEngine.createJavaSearchScope(elements);

		FilteredTypesSelectionDialog dialog = new FilteredTypesSelectionDialog(
				getShell(), false, context, scope, IJavaSearchConstants.CLASS);
		dialog.setTitle("Type Selection");
		dialog.setMessage("Select the class type");
		// dialog.setInitialPattern(getSuperClass());

		if (dialog.open() == Window.OK) {
			try {
				oeReturnTypeText.setText(((IType) (dialog.getFirstResult()))
						.getFullyQualifiedParameterizedName());
			} catch (JavaModelException e) {
				MessageDialog.openError(getShell(), "Error", e.getMessage());
			}
			// updateType();
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
					"There are missing fields", new Status(IStatus.ERROR, Activator.PLUGIN_ID, "Parameter is null"));
		}
	}

	/** 
	 * Get the created/modified parameter
	 * @return New parameter
	 */
	public Parameter getParameter() {
		return p;
	}

}
