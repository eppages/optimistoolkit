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

package es.bsc.servicess.ide.wizards;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;

/**
 * Wizard page to select the type of Core Element generation
 * @author Jorge Ejarque (Barcelona Supercomputing Center)
 *
 */
public class ServiceSsCoreElementInitialPage extends WizardPage {
	private Button op_a;
	private Button op_b;
	private Button op_c;
	private Button op_e;
	private Button op_d;
	private IStructuredSelection selection;
	

	/**
	 * Constructor.
	 * @param pageName Page name.
	 */
	protected ServiceSsCoreElementInitialPage(String pageName) {
		super(pageName);
		setTitle("Create Core Element");
		setDescription("Select the way to create the new core element");
	}

	@Override
	public void createControl(Composite parent) {
		Group group = new Group(parent, SWT.NONE);
		group.setText("Select the core element creation option");
		group.setLayout(new RowLayout(SWT.VERTICAL));
		op_a = new Button(group, SWT.RADIO);
		op_a.setText("Create new method core element from scratch");
		op_a.setSelection(true);
		setPage(ServiceSsCoreElementSecondPage.METHOD_NEW);
		op_a.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {
				op_a.setSelection(true);
				op_b.setSelection(false);
				op_c.setSelection(false);
				op_d.setSelection(false);
				op_e.setSelection(false);
				setPage(ServiceSsCoreElementSecondPage.METHOD_NEW);
			}

			@Override
			public void widgetSelected(SelectionEvent arg0) {
				op_a.setSelection(true);
				op_b.setSelection(false);
				op_c.setSelection(false);
				op_d.setSelection(false);
				op_e.setSelection(false);
				setPage(ServiceSsCoreElementSecondPage.METHOD_NEW);
			}
		});
		op_d = new Button(group, SWT.RADIO);
		op_d.setText("Create new method core element from executable");
		op_d.setSelection(false);
		op_d.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {
				op_a.setSelection(false);
				op_b.setSelection(false);
				op_c.setSelection(false);
				op_d.setSelection(true);
				op_e.setSelection(false);
				setPage(ServiceSsCoreElementSecondPage.METHOD_BIN);
			}

			@Override
			public void widgetSelected(SelectionEvent arg0) {
				op_a.setSelection(false);
				op_b.setSelection(false);
				op_c.setSelection(false);
				op_d.setSelection(true);
				op_e.setSelection(false);
				setPage(ServiceSsCoreElementSecondPage.METHOD_BIN);
			}
		});
		op_b = new Button(group, SWT.RADIO);
		op_b.setText("Add method core element form existing class method");
		op_b.setSelection(false);
		op_b.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {
				op_b.setSelection(true);
				op_a.setSelection(false);
				op_c.setSelection(false);
				op_d.setSelection(false);
				op_e.setSelection(false);
				setPage(ServiceSsCoreElementSecondPage.METHOD_EXISTS);
			}

			@Override
			public void widgetSelected(SelectionEvent arg0) {
				op_b.setSelection(true);
				op_a.setSelection(false);
				op_c.setSelection(false);
				op_d.setSelection(false);
				op_e.setSelection(false);
				setPage(ServiceSsCoreElementSecondPage.METHOD_EXISTS);
			}
		});
		
		op_c = new Button(group, SWT.RADIO);
		op_c.setText("Add service core element from wsdl");
		op_c.setSelection(false);
		op_c.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {
				op_a.setSelection(false);
				op_b.setSelection(false);
				op_c.setSelection(true);
				op_d.setSelection(false);
				op_e.setSelection(false);
				setPage(ServiceSsCoreElementSecondPage.SERVICE_WSDL);
			}

			@Override
			public void widgetSelected(SelectionEvent arg0) {
				op_a.setSelection(false);
				op_b.setSelection(false);
				op_c.setSelection(true);
				op_d.setSelection(false);
				op_e.setSelection(false);
				setPage(ServiceSsCoreElementSecondPage.SERVICE_WSDL);
			}
		});
		op_e = new Button(group, SWT.RADIO);
		op_e.setText("Add service core element from war");
		op_e.setSelection(false);
		op_e.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {
				op_a.setSelection(false);
				op_b.setSelection(false);
				op_c.setSelection(false);
				op_d.setSelection(false);
				op_e.setSelection(true);
				setPage(ServiceSsCoreElementSecondPage.SERVICE_WAR);
			}

			@Override
			public void widgetSelected(SelectionEvent arg0) {
				op_a.setSelection(false);
				op_b.setSelection(false);
				op_c.setSelection(false);
				op_d.setSelection(false);
				op_e.setSelection(true);
				setPage(ServiceSsCoreElementSecondPage.SERVICE_WAR);
			}
		});
		setControl(group);
		setPageComplete(true);
	}

	/**
	 * Set the second page.
	 * @param type Generation type.
	 */
	protected void setPage(int type) {
		IWizardPage p = getNextPage();
		((ServiceSsCoreElementSecondPage) p)
				.setType(type);

	}

}
