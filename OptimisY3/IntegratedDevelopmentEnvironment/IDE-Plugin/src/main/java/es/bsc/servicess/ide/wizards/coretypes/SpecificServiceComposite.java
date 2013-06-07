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

package es.bsc.servicess.ide.wizards.coretypes;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.eclipse.core.runtime.IProgressMonitor;
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
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.xml.sax.SAXException;

import es.bsc.servicess.ide.dialogs.AddServiceLocationDialog;
import es.bsc.servicess.ide.model.Parameter;
import es.bsc.servicess.ide.model.ServiceCoreElement;
import es.bsc.servicess.ide.model.ServiceElement;
import es.bsc.servicess.ide.wizards.ServiceSsCoreElementWizardPage;
import es.bsc.servicess.ide.wizards.SpecificComposite;

public class SpecificServiceComposite extends SpecificComposite{
	
	private Composite seDesc;
	private Text namespaceText;
	private Text portText;
	private List wsdlList;
	private Button addWsdlButton;
	private Button modWsdlButton;
	private Text ceServiceText;
	
	public SpecificServiceComposite(ServiceSsCoreElementWizardPage page,
			Shell shell) {
		super(page, shell);
	
	}
	
	@Override
	public Composite createComposite(Composite ceBar) {
		seDesc = new Composite(ceBar, SWT.NONE);
		seDesc.setLayout(new GridLayout(2, false));
		Label serviceLabel = new Label(seDesc, SWT.NONE);
		serviceLabel.setText("Service Name");
		ceServiceText = new Text(seDesc, SWT.SINGLE | SWT.BORDER);
		GridData rd = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		rd.grabExcessHorizontalSpace = true;
		ceServiceText.setLayoutData(rd);
		Label namespaceLabel = new Label(seDesc, SWT.NONE);
		namespaceLabel.setText("Service Name");
		namespaceText = new Text(seDesc, SWT.SINGLE | SWT.BORDER);
		rd = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		rd.grabExcessHorizontalSpace = true;
		namespaceText.setLayoutData(rd);

		Label portLabel = new Label(seDesc, SWT.NONE);
		portLabel.setText("Port Name");
		portText = new Text(seDesc, SWT.SINGLE | SWT.BORDER);
		rd = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		rd.grabExcessHorizontalSpace = true;
		portText.setLayoutData(rd);

		Label wsdlLabel = new Label(seDesc, SWT.NONE);
		wsdlLabel.setText("Locations");
		Composite wsdl = new Composite(seDesc, SWT.NONE);
		GridLayout wsdlLayout = new GridLayout();
		wsdlLayout.numColumns = 2;
		wsdlLayout.marginLeft = 0;
		wsdlLayout.marginRight = 0;
		wsdl.setLayout(wsdlLayout);
		rd = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		rd.grabExcessHorizontalSpace = true;
		rd.horizontalIndent = 0;
		rd.horizontalSpan = 0;
		wsdl.setLayoutData(rd);
		wsdlList = new List(wsdl, SWT.BORDER);
		rd = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		rd.grabExcessHorizontalSpace = true;
		rd.grabExcessVerticalSpace = true;
		rd.minimumHeight = 70;
		wsdlList.setLayoutData(rd);
		Composite wsdlButtons = new Composite(wsdl, SWT.NONE);
		wsdlButtons.setLayout(new GridLayout(1, false));
		addWsdlButton = new Button(wsdlButtons, SWT.NONE);
		addWsdlButton.setText("Add...");
		rd = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		rd.grabExcessHorizontalSpace = true;
		addWsdlButton.setLayoutData(rd);
		modWsdlButton = new Button(wsdlButtons, SWT.NONE);
		modWsdlButton.setText("Modify...");
		rd = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		rd.grabExcessHorizontalSpace = true;
		modWsdlButton.setLayoutData(rd);
		return seDesc;
	}
	@Override
	public boolean isCompositeCompleated() {
		if (ceServiceText.getText().trim().length() > 0
		&& portText.getText().trim().length() > 0
		&& namespaceText.getText().trim().length() > 0) {
			return true;
		}else
			return false;
	}
	@Override
	public ServiceElement generateElement(String name, int modifier,
			String returnType) {
		return new ServiceCoreElement(name, modifier,
				returnType, null, namespaceText.getText().trim(), 
				ceServiceText.getText().trim(),	portText.getText().trim());
	}
	@Override
	public void addListeners() {

		ceServiceText.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent arg0) {
				updateService();
			}
		});
		wsdlList.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {
				modWsdlButton.setEnabled(true);
			}

			@Override
			public void widgetSelected(SelectionEvent arg0) {
				modWsdlButton.setEnabled(true);

			}
		});
		modWsdlButton.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {
				addLocation(wsdlList.getSelectionIndex());
			}

			@Override
			public void widgetSelected(SelectionEvent arg0) {
				addLocation(wsdlList.getSelectionIndex());

			}

		});
		addWsdlButton.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {
				addLocation(-1);
			}

			@Override
			public void widgetSelected(SelectionEvent arg0) {
				addLocation(-1);

			}

		});
		
	}
	
	protected void addLocation(int selection) {
		String s = null;
		if (selection >= 0) {
			s = wsdlList.getItem(selection);
		}
		AddServiceLocationDialog dialog = new AddServiceLocationDialog(
				shell, s);
		if (dialog.open() == Window.OK) {
			s = dialog.getLocation();
			if (selection < 0) {
				wsdlList.add(s);
				if (thirdPage.getElement() != null) {
					((ServiceCoreElement) thirdPage.getElement()).getWsdlURIs().add(s);
				}
			} else {
				wsdlList.setItem(selection, s);
				if (thirdPage.getElement() != null) {
					((ServiceCoreElement) thirdPage.getElement()).getWsdlURIs().set(selection,
							s);
				}
			}
		}

	}
	
	protected void updateService() {
		if (thirdPage.getElement() != null) {
				((ServiceCoreElement) thirdPage.getElement()).setServiceName(ceServiceText
						.getText().trim());
		} else if (thirdPage.isElementCompleted()) {
			thirdPage.updateElement(thirdPage.generateElement());
			thirdPage.getCEStatus().setOK();
			thirdPage.doStatusUpdate();
		}

	}

	@Override
	public void printElement(ServiceElement element) {
		// TODO Auto-generated method stub
		namespaceText
		.setText(((ServiceCoreElement) element).getNamespace());
		ceServiceText.setText(((ServiceCoreElement) element)
		.getServiceName());
		portText.setText(((ServiceCoreElement) element).getPort());
		wsdlList.removeAll();
		for (String str : ((ServiceCoreElement) element).getWsdlURIs()) {
			wsdlList.add(str);
		}
		namespaceText.setEnabled(false);
		portText.setEnabled(false);
		ceServiceText.setEnabled(false);
		thirdPage.getNameText().setEnabled(false);
		thirdPage.getReturnTypeText().setEnabled(false);
		thirdPage.getCEStatus().setOK();
		thirdPage.doStatusUpdate();
	}

	@Override
	public String getParameterDirection(Parameter p) {
		return "IN";
	}

	public void writeServiceLocations() throws ParserConfigurationException,
	SAXException, IOException, TransformerException {
		File pr_file = thirdPage.getJavaProject().getProject().getLocation()
				.append(thirdPage.getPackageFragment().getPath().makeRelativeTo(
				thirdPage.getJavaProject().getPath())).append("project.xml").toFile();
		File res_file = thirdPage.getJavaProject().getProject().getLocation()
				.append(thirdPage.getPackageFragment().getPath().makeRelativeTo(
				thirdPage.getJavaProject().getPath())).append("resources.xml")
		.toFile();
		((ServiceCoreElement) thirdPage.getElement()).writeLocations(pr_file, res_file);

	}
	
	@Override
	public void performFinish(IProgressMonitor monitor) {
		try {
			writeServiceLocations();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
	}

	@Override
	public void performCancel() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addSpecificParams(ServiceElement el) {
		ArrayList wsdls = new ArrayList<String>();
		for (String s:wsdlList.getItems())
				wsdls.add(s);
		((ServiceCoreElement) el).setWsdlURIs(wsdls);
		
	}
}

