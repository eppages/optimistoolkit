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

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import javax.wsdl.WSDLException;
import javax.wsdl.xml.WSDLReader;
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
import com.ibm.wsdl.xml.WSDLReaderImpl;
import com.sun.tools.xjc.api.S2JJAXBModel;
import es.bsc.servicess.ide.model.ElementClass;
import es.bsc.servicess.ide.model.ServiceCoreElement;
import es.bsc.servicess.ide.wizards.ServiceSsCoreElementSecondPage;

public class ServiceWSDLSpecificTreatment extends ServiceSpecificTreatment {

	private Text wsdlURI;


	public ServiceWSDLSpecificTreatment(
			ServiceSsCoreElementSecondPage secondPage, Shell shell) {
		super(secondPage, shell);
	}

	
	protected void updateURI() {
			URL url = null;
			try {
				url = new URL(wsdlURI.getText().trim());
			} catch (MalformedURLException e1) {
				secondPage.getCEStatus().setError("Incorrect url");
				return;
			}
			if (url != null) {
				WSDLReader wsdlreader = new WSDLReaderImpl();
				try {
					wsdl = wsdlreader.readWSDL(url.toString());
					if (wsdl != null) {
						element = null;
						selectedService = null;
						selectedPort = null;
						selectedOperation = null;
						schClasses = new HashMap<String, ElementClass>();
						schModels = new ArrayList<S2JJAXBModel>();
						schemasParsed = false;
						namespace = "";
						service.setText("");
						portNameText.setText("");
						methodNameText.setText("");
						secondPage.getCEStatus()
								.setError("There are missing parameters to complete the Element information");
						secondPage.doStatusUpdate();
					} else {
						MessageDialog.openError(shell, "Error ",
								"Error Getting WSDL");
					}
				} catch (WSDLException e) {
					secondPage.getCEStatus().setError("WSDL not find in the specified URL");
					secondPage.doStatusUpdate();
					e.printStackTrace();
					MessageDialog.openError(shell, "Error WSDL Exception",
							e.getMessage());

				}
			}
	}

		@Override
		protected void addTypeLocationListener() {
			wsdlURI.addModifyListener(new ModifyListener() {

				@Override
				public void modifyText(ModifyEvent arg0) {
					updateURI();
				}
			});
		}

		@Override
		protected void addTypeLocationComposite(Composite comp) {
			Label serviceWSDLLabel = new Label(comp, SWT.NONE);
			serviceWSDLLabel.setText("WSDL location");
			Composite wsdlc = new Composite(comp, SWT.NONE);
			GridData rd = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
			rd.grabExcessHorizontalSpace = true;
			rd.horizontalIndent = 0;
			rd.horizontalSpan = 0;
			wsdlc.setLayoutData(rd);
			GridLayout oeReturnLayout = new GridLayout();
			oeReturnLayout.numColumns = 1;
			oeReturnLayout.marginLeft = 0;
			oeReturnLayout.marginRight = 0;
			wsdlc.setLayout(oeReturnLayout);
			wsdlURI = new Text(wsdlc, SWT.SINGLE | SWT.BORDER);
			rd = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
			rd.grabExcessHorizontalSpace = true;
			wsdlURI.setLayoutData(rd);
			
		}

		@Override
		protected void generateTypeSpecificParams(ServiceCoreElement el) {
			el.getWsdlURIs().add(wsdlURI.getText().trim());
			
		}

		@Override
		protected boolean isTypeCompositeCompleate() {
			if (wsdlURI.getText().trim().length() > 0){
				return true;
			}else
				return false;
		}

		@Override
		protected void performTypeCompositeCancel() {
			// Nothing to do
			
		}

		@Override
		protected void performTypeCompositeFinish() {
			// Nothing to do
			
		}
}
