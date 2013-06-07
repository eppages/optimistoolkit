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

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.wsdl.BindingOperation;
import javax.wsdl.Definition;
import javax.wsdl.Part;
import javax.wsdl.Port;
import javax.wsdl.Service;
import javax.wsdl.Types;
import javax.wsdl.extensions.schema.Schema;
import javax.xml.namespace.QName;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.Flags;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.core.JavaModelStatus;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;
import org.xml.sax.SAXParseException;

import com.sun.codemodel.ClassGenerator;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JType;
import com.sun.tools.xjc.api.ErrorListener;
import com.sun.tools.xjc.api.S2JJAXBModel;
import com.sun.tools.xjc.api.SchemaCompiler;
import com.sun.tools.xjc.api.XJC;
import com.sun.xml.xsom.XSElementDecl;
import com.sun.xml.xsom.XSModelGroup;
import com.sun.xml.xsom.XSParticle;
import com.sun.xml.xsom.XSSchemaSet;
import com.sun.xml.xsom.XSTerm;
import com.sun.xml.xsom.XSType;
import com.sun.xml.xsom.parser.XSOMParser;

import es.bsc.servicess.ide.Logger;
import es.bsc.servicess.ide.dialogs.ServicePartsSelectionDialog;
import es.bsc.servicess.ide.model.CoreElementParameter;
import es.bsc.servicess.ide.model.ElementClass;
import es.bsc.servicess.ide.model.ServiceCoreElement;
import es.bsc.servicess.ide.model.ServiceElement;
import es.bsc.servicess.ide.wizards.ServiceSsCoreElementSecondPage;
import es.bsc.servicess.ide.wizards.ServiceSsCoreElementWizardPage;
import es.bsc.servicess.ide.wizards.TypeSpecificTreatment;

public abstract class ServiceSpecificTreatment extends TypeSpecificTreatment {

	protected ServiceCoreElement element;
	protected Text service;
	protected Button classServiceButton;
	protected Text portNameText;
	protected Button selectPortButton;
	protected Text methodNameText;
	protected Button selectMethodButton;
	protected String namespace;
	protected Definition wsdl;
	protected Service selectedService;
	protected Port selectedPort;
	protected BindingOperation selectedOperation;
	protected Map<String, ElementClass> schClasses;
	protected List<S2JJAXBModel> schModels;
	protected boolean schemasParsed;

	private Logger log = Logger.getLogger();

	public ServiceSpecificTreatment(
			ServiceSsCoreElementSecondPage secondPage, Shell shell) {
		super(secondPage, shell);
		schClasses = new HashMap<String, ElementClass>();
		schModels = new ArrayList<S2JJAXBModel>();
		schemasParsed = false;
	}

	@Override
	public void updateControlsListeners() {
		
		addTypeLocationListener();

		classServiceButton.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {
					selectService();
			}

			@Override
			public void widgetSelected(SelectionEvent arg0) {
					selectService();
			}
		});
		
		selectPortButton.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {
				selectPort();
			}

			@Override
			public void widgetSelected(SelectionEvent arg0) {
				selectPort();

			}
		});
		
		selectMethodButton.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {
					selectServiceMethod();
			}

			@Override
			public void widgetSelected(SelectionEvent arg0) {
					selectServiceMethod();
			}
		});

	}

	protected abstract void addTypeLocationListener(); 

	@Override
	public Composite updateSecondPageGroupControls(Group group, Composite cp) {
			if (cp != null)
				cp.dispose();
				Composite comp = new Composite(group, SWT.NONE);
				GridData rd = new GridData(GridData.FILL_HORIZONTAL
				| GridData.VERTICAL_ALIGN_BEGINNING);
				// rd.grabExcessHorizontalSpace = true;
				comp.setLayoutData(rd);
				comp.setLayout(new GridLayout(2, false));
				
				//Service Definition Location
				addTypeLocationComposite(comp);

				// Service
				Label serviceClassLabel = new Label(comp, SWT.NONE);
				serviceClassLabel.setText("Service");
				Composite dc = new Composite(comp, SWT.NONE);
				rd = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
				rd.grabExcessHorizontalSpace = true;
				rd.horizontalIndent = 0;
				rd.horizontalSpan = 0;
				dc.setLayoutData(rd);
				GridLayout oeReturnLayout = new GridLayout();
				oeReturnLayout.numColumns = 2;
				oeReturnLayout.marginLeft = 0;
				oeReturnLayout.marginRight = 0;
				dc.setLayout(oeReturnLayout);
				service = new Text(dc, SWT.SINGLE | SWT.BORDER);
				rd = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
				rd.grabExcessHorizontalSpace = true;
				service.setLayoutData(rd);
				classServiceButton = new Button(dc, SWT.NONE);
				classServiceButton.setText("Select...");
				
				//Port
				Label portLabel = new Label(comp, SWT.NONE);
				portLabel.setText("Port");
				Composite port = new Composite(comp, SWT.NONE);
				rd = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
				rd.grabExcessHorizontalSpace = true;
				rd.horizontalIndent = 0;
				rd.horizontalSpan = 0;
				port.setLayoutData(rd);
				oeReturnLayout = new GridLayout();
				oeReturnLayout.numColumns = 2;
				oeReturnLayout.marginLeft = 0;
				oeReturnLayout.marginRight = 0;
				port.setLayout(oeReturnLayout);
				portNameText = new Text(port, SWT.SINGLE | SWT.BORDER);
				rd = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
				rd.grabExcessHorizontalSpace = true;
				portNameText.setLayoutData(rd);
				selectPortButton = new Button(port, SWT.NONE);
				selectPortButton.setText("Select...");
				
				//Method
				Label methodLabel = new Label(comp, SWT.NONE);
				methodLabel.setText("Method");
				Composite method = new Composite(comp, SWT.NONE);
				rd = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
				rd.grabExcessHorizontalSpace = true;
				rd.horizontalIndent = 0;
				rd.horizontalSpan = 0;
				method.setLayoutData(rd);
				oeReturnLayout = new GridLayout();
				oeReturnLayout.numColumns = 2;
				oeReturnLayout.marginLeft = 0;
				oeReturnLayout.marginRight = 0;
				method.setLayout(oeReturnLayout);
				methodNameText = new Text(method, SWT.SINGLE | SWT.BORDER);
				rd = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
				rd.grabExcessHorizontalSpace = true;
				methodNameText.setLayoutData(rd);
				selectMethodButton = new Button(method, SWT.NONE);
				selectMethodButton.setText("Select...");
				
				return comp;
	}

	protected abstract void addTypeLocationComposite(Composite comp);

	@Override
	public ServiceElement generateCoreElement() throws JavaModelException {
		
		ServiceCoreElement el = new ServiceCoreElement(methodNameText.getText().trim(),
				Flags.AccPublic, getServiceOperationReturnType(), null,
				namespace, service.getText().trim(), portNameText
						.getText().trim());
			generateTypeSpecificParams(el);
		return el;
	}

	protected abstract void generateTypeSpecificParams(ServiceCoreElement el); 

	private void selectServiceMethod() {
		if (selectedPort == null) {

			MessageDialog.openError(shell, "Error",
					"Getting the selected service");
			return;
		}
		Collection<BindingOperation> ss = selectedPort.getBinding()
				.getBindingOperations();
		ServicePartsSelectionDialog dialog = new ServicePartsSelectionDialog(
				shell, ss);
		dialog.setTitle("Select Operation");
		String before = methodNameText.getText().trim();
		if (methodNameText.getText().trim().length() > 0) {
			dialog.setInitialPattern(methodNameText.getText().trim());
		} else
			dialog.setInitialPattern("?");
		if (dialog.open() == Dialog.OK) {
			selectedOperation = (BindingOperation) dialog.getFirstResult();
			if (!selectedOperation.getName().equals(before)) {
				methodNameText.setText(selectedOperation.getName());
				updateMethodName();
			}
		}

	}
	
	protected void selectPort() {
		if (selectedService == null) {

			MessageDialog.openError(shell, "Error",
					"Getting the selected service");
			return;
		}
		Collection<Port> ss = selectedService.getPorts().values();
		ServicePartsSelectionDialog dialog = new ServicePartsSelectionDialog(
				shell, ss);
		dialog.setTitle("Select Port");
		if (portNameText.getText().trim().length() > 0)
			dialog.setInitialPattern(portNameText.getText().trim());
		else
			dialog.setInitialPattern("?");
		if (dialog.open() == Dialog.OK) {
			selectedPort = (Port) dialog.getFirstResult();
			if (!portNameText.getText().trim().equals(selectedPort.getName())) {
				portNameText.setText(selectedPort.getName());
				methodNameText.setText("");
				selectedOperation = null;
				element = null;
				schClasses = new HashMap<String, ElementClass>();
				schModels = new ArrayList<S2JJAXBModel>();
				schemasParsed = false;
				updatePortName();
			}
		}
	}
	
	private void selectService() {
		if (wsdl == null) {
			MessageDialog.openError(shell, "Error", "Getting the Class");
			return;
		}
		Collection<Service> ss = wsdl.getAllServices().values();
		System.out.println("There are " + ss.size() + " services to select");
		ServicePartsSelectionDialog dialog = new ServicePartsSelectionDialog(
				shell, ss);
		dialog.setTitle("Select Service");
		if (service.getText().trim().length() > 0
				&& namespace.length() > 0) {
			dialog.setInitialPattern(new QName(service.getText().trim(),
					namespace).toString());
		} else
			dialog.setInitialPattern("?");
		if (dialog.open() == Dialog.OK) {
			selectedService = (Service) dialog.getFirstResult();
			namespace = selectedService.getQName().getNamespaceURI();
			if (!service.getText().trim()
					.equals(selectedService.getQName().getLocalPart())) {
				service.setText(selectedService.getQName()
						.getLocalPart());
				portNameText.setText("");
				selectedPort = null;
				methodNameText.setText("");
				selectedOperation = null;
				element = null;
				schClasses = new HashMap<String, ElementClass>();
				schModels = new ArrayList<S2JJAXBModel>();
				schemasParsed = false;
				updateService();
			}
		}

	}
	protected void updateService() {
		if (element != null) {
				((ServiceCoreElement) element).setServiceName(methodNameText
						.getText());
				IWizardPage p = secondPage.getNextPage();
				((ServiceSsCoreElementWizardPage) p).updateElement(element);
		} else if (isPageComplete()) {
			IWizardPage p = secondPage.getNextPage();
			((ServiceSsCoreElementWizardPage) p).setType(ServiceSsCoreElementSecondPage.SERVICE_WSDL);
			
				try {
					element = (ServiceCoreElement) generateCoreElement();
					((ServiceSsCoreElementWizardPage) p).updateElement(element);
					secondPage.getCEStatus().setOK();
					secondPage.doStatusUpdate();
				} catch (JavaModelException e) {
					MessageDialog
							.openError(shell, "Error", e.getMessage());
				}

		} else {
			secondPage.getCEStatus()
					.setError("There are missing parameters to complete the Element information");
			secondPage.doStatusUpdate();
		}
	}
	
	protected void updatePortName(){
		if (element != null) {
				((ServiceCoreElement) element)
						.setPort(methodNameText.getText());
				IWizardPage p = secondPage.getNextPage();
				((ServiceSsCoreElementWizardPage) p).updateElement(element);
		} else if (isPageComplete()) {
			try {
				element = (ServiceCoreElement)generateCoreElement();
				IWizardPage p = secondPage.getNextPage();
				((ServiceSsCoreElementWizardPage) p).setType(ServiceSsCoreElementSecondPage.SERVICE_WSDL);
				((ServiceSsCoreElementWizardPage) p).updateElement(element);
				secondPage.getCEStatus().setOK();
				secondPage.doStatusUpdate();
			} catch (JavaModelException e) {
				MessageDialog.openError(shell, "Error", e.getMessage());
			}

		} else {
			secondPage.getCEStatus()
					.setError("There are missing parameter to complete the Element information");
			secondPage.doStatusUpdate();
		}

	}
	
	protected void resetServiceInfo(){
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
	}

	
	protected void updateMethodName() {
		System.out.println("Updating Method Name");
		IWizardPage p = secondPage.getNextPage();
		if (element != null) {
			element.setMethodName(methodNameText.getText());
			((ServiceSsCoreElementWizardPage) p).setType(ServiceSsCoreElementSecondPage.SERVICE_WSDL);
			try {

				element = setServiceOperationParamters(element);
				((ServiceSsCoreElementWizardPage) p).updateElement(element);
				secondPage.getCEStatus().setOK();
				secondPage.doStatusUpdate();

			} catch (JavaModelException e) {
				MessageDialog.openError(shell, "Error", e.getMessage());
			}
		} else if (isPageComplete()) {
			((ServiceSsCoreElementWizardPage) p).setType(ServiceSsCoreElementSecondPage.SERVICE_WSDL);

				try {
					element = (ServiceCoreElement)generateCoreElement();
					element = setServiceOperationParamters(element);
					((ServiceSsCoreElementWizardPage) p).updateElement(element);
					secondPage.getCEStatus().setOK();
					secondPage.doStatusUpdate();
				} catch (JavaModelException e) {
					MessageDialog
							.openError(shell, "Error", e.getMessage());
				}

		} else {
			secondPage.getCEStatus()
					.setError("There are missing parameters to complete the Element information");
			secondPage.doStatusUpdate();
		}

	}
	
	private String getServiceOperationReturnType() throws JavaModelException {
		boolean wrappered = isSelectedOperationWrappered();
		Map<String, Part> pp = selectedOperation.getOperation().getOutput()
				.getMessage().getParts();
		QName out_parameters = null;
		if (pp.size() == 1) {
			for (Part p : pp.values()) {
				log.debug("Part (Name: " + p.getName() + " ele: "+ p.getElementName().toString());
				out_parameters = p.getElementName();
			}
		} else {
			throw new JavaModelException(new Exception(
					"Unexpected number of output parts"), JavaModelStatus.ERROR);
		}
		if (out_parameters != null) {
			log.debug("Looking for " + out_parameters.toString());
			Types t = wsdl.getTypes();
			Element schema = null;
			XSSchemaSet xsset = null;
			boolean found = false;
			String sType = null;
			for (Object e : t.getExtensibilityElements()) {
				if (e instanceof Schema) {
					log.debug("BaseURI: "+ ((Schema) e).getDocumentBaseURI());
					schema = ((Schema) e).getElement();
					log.debug("Imports: "+((Schema)e).getImports());
					try {
						XSOMParser xs = new XSOMParser();
						Source source = new DOMSource(schema);
						StringWriter xmlAsWriter = new StringWriter();
						StreamResult result = new StreamResult(xmlAsWriter);
						TransformerFactory.newInstance().newTransformer().transform(source, result);
						log.debug("Schema:\n "	+ xmlAsWriter.toString());
						StringReader xmlReader = new StringReader(xmlAsWriter.toString());
						SchemaCompiler schComp = XJC.createSchemaCompiler();
						// schComp.forcePackageName(new URI(namespace).getAuthority());
						schComp.setErrorListener(new ErrorListener() {
							public void error(SAXParseException exception) {
								exception.printStackTrace();
							}

							@Override
							public void fatalError(SAXParseException arg0) {
								arg0.printStackTrace();
							}

							@Override
							public void info(SAXParseException arg0) {
								arg0.printStackTrace();
							}

							@Override
							public void warning(SAXParseException arg0) {
								arg0.printStackTrace();
							}
						});
						InputSource is = new InputSource(xmlReader);
						String s = ((Schema)e).getDocumentBaseURI();
						is.setSystemId(s.substring(0, s.lastIndexOf("/")+1));
						schComp.parseSchema(is);
						S2JJAXBModel schModel = schComp.bind();
						if (!schemasParsed)
							schModels.add(schModel);
						if (found)
							break;
						JCodeModel jCodeModel = schModel.generateCode(null,	null);
						source = new DOMSource(schema);
						xmlAsWriter = new StringWriter();
						result = new StreamResult(xmlAsWriter);
						TransformerFactory.newInstance().newTransformer().transform(source, result);
						xmlReader = new StringReader(xmlAsWriter.toString());
						is = new InputSource(xmlReader);
						is.setSystemId(s.substring(0, s.lastIndexOf("/")+1));
						xs.parse(is);
						xsset = xs.getResult();
						if (xsset != null) {
							log.debug("XS set is not null");
							XSElementDecl el = xsset.getElementDecl(out_parameters.getNamespaceURI(),
									out_parameters.getLocalPart());
							XSType type = el.getType();
							if (!wrappered) { 
								log.debug("Not wappered");
								JType jt = schModel.getJavaType(new QName(type.getTargetNamespace(),
										type.getName())).getTypeClass();
								String t1 = jt.fullName();
								// if (!jt.isPrimitive()){
								ElementClass ec = new ElementClass(new QName(type.getTargetNamespace(),
										type.getName()),jCodeModel._getClass(t1));
								schClasses.put(t1, ec);
								// }
								if (schemasParsed) {
									return t1;
								} else {
									found = true;
									sType = t1;
								}
							} else { //Wrappered type
								log.debug("Schema Element: "+ el.getName() + " (" + el.getClass()+ ")");
								if (type.isComplexType()) {
									XSParticle particle = type.asComplexType().getContentType().asParticle();
									if (particle != null) {
										XSTerm term = particle.getTerm();
										log.debug("Term: "+ term.getClass());
										if (term.isModelGroup()) {
											XSModelGroup xsModelGroup = term.asModelGroup();
											XSParticle[] particles = xsModelGroup.getChildren();
											for (XSParticle p : particles) {
												XSTerm pterm = p.getTerm();
												if (pterm.isElementDecl()) {
													XSElementDecl ed = pterm.asElementDecl();
													log.debug("Element: "+ ed.getName()	+ " type {"
															+ ed.getType().getTargetNamespace()	+ "}"
															+ ed.getType().getName());
													JType jt = schModel.getJavaType(new QName(
															ed.getType().getTargetNamespace(), 
															ed.getType().getName())).getTypeClass();
													String t1 = jt.fullName();
													// if (!jt.isPrimitive()){
													ElementClass ec = new ElementClass(	new QName(
															ed.getType().getTargetNamespace(),
															ed.getType().getName()),jCodeModel._getClass(t1));
													schClasses.put(t1, ec);
													// }
													if (schemasParsed) {
														return t1;
													} else {
														found = true;
														sType = t1;
													}
												}
											}
											if (!found) {
												// throw new JavaModelException(new Exception("Return element not found"),
												// JavaModelStatus.ERROR);
												return "void";
											}
										}
									} else
										throw new JavaModelException(new Exception("Unexpected complex type model"),
														JavaModelStatus.ERROR);
								} else if (type.isSimpleType()) {
									log.debug("Element: "+ type.asSimpleType().getName()+ " type "
											+ type.asSimpleType().getBaseType().getName());
									JType jt = schModel.getJavaType(new QName(type.asSimpleType()
											.getBaseType().getTargetNamespace(), type.asSimpleType()
											.getBaseType().getName())).getTypeClass();
									String t1 = jt.fullName();
									// if (!jt.isPrimitive()){
									ElementClass ec = new ElementClass(	new QName(type.asSimpleType().getBaseType()
											.getTargetNamespace(), type.asSimpleType().getBaseType().getName()),
											jCodeModel._getClass(t1));
									schClasses.put(t1, ec);
									// }
									if (schemasParsed) {
										return t1;
									} else {
										found = true;
										sType = t1;
									}
								} else
									throw new JavaModelException(new Exception("Unknown element type"), JavaModelStatus.ERROR);
							}
						}else
						log.debug("Schema set not found");

					} catch (Exception ex) {
						ex.printStackTrace();
						throw new JavaModelException(ex, JavaModelStatus.ERROR);
					}
				}
			}
			if (found && sType != null) {
				return sType;
			} else
				throw new JavaModelException(new Exception("Return type not found"), JavaModelStatus.ERROR);
		} else
			throw new JavaModelException(new Exception("return element not found"), JavaModelStatus.ERROR);
	}

	private boolean isSelectedOperationWrappered() {
		QName messageName = selectedOperation.getOperation().getInput()
				.getMessage().getQName();
		Map<String, Part> pp = selectedOperation.getOperation().getInput()
				.getMessage().getParts();
		if (pp.size() > 1)
			return false;
		else {
			QName elementName = pp.get("parameters").getElementName();
			if (messageName.toString().equals(elementName.toString())) {
				return true;
			} else
				return false;
		}
	}

	protected ServiceCoreElement setServiceOperationParamters(
			ServiceCoreElement element) throws JavaModelException {
		System.out.println("Setting Parameters");
		boolean wrappered = isSelectedOperationWrappered();
		// Map<String,Part> pp =
		// selectedOperation.getOperation().getInput().getMessage().getParts();
		List<Part> parts = selectedOperation.getOperation().getInput()
				.getMessage().getOrderedParts(null);
		for (Part part : parts) {
			System.out.println("Part (Name: " + part.getName() + " ele: "
					+ part.getElementName().toString());

			QName in_parameters = part.getElementName();
			System.out.println("Getting type: " + in_parameters.toString());
			Types t = wsdl.getTypes();
			Element schema = null;
			XSSchemaSet xsset = null;
			for (Object e : t.getExtensibilityElements()) {
				if (e instanceof Schema) {
					System.out.println("BaseURI: "
							+ ((Schema) e).getDocumentBaseURI());
					schema = ((Schema) e).getElement();
					try {
						XSOMParser xs = new XSOMParser();
						Source source = new DOMSource(schema);
						StringWriter xmlAsWriter = new StringWriter();
						StreamResult result = new StreamResult(xmlAsWriter);

						TransformerFactory.newInstance().newTransformer()
						.transform(source, result);
						System.out.println("Schema:\n "
								+ xmlAsWriter.toString());
						StringReader xmlReader = new StringReader(
								xmlAsWriter.toString());
						InputSource is = new InputSource(xmlReader);
						is.setSystemId(((Schema) e).getDocumentBaseURI());
						xs.parse(is);

						SchemaCompiler schComp = XJC.createSchemaCompiler();
						// schComp.forcePackageName(new
						// URI(namespace).getAuthority());
						schComp.setErrorListener(new ErrorListener() {
							public void error(SAXParseException exception) {
								exception.printStackTrace();
							}

							@Override
							public void fatalError(SAXParseException arg0) {
								arg0.printStackTrace();

							}

							@Override
							public void info(SAXParseException arg0) {
								arg0.printStackTrace();

							}

							@Override
							public void warning(SAXParseException arg0) {
								arg0.printStackTrace();

							}
						});
						source = new DOMSource(schema);
						xmlAsWriter = new StringWriter();
						result = new StreamResult(xmlAsWriter);
						TransformerFactory.newInstance().newTransformer()
						.transform(source, result);
						xmlReader = new StringReader(xmlAsWriter.toString());
						is = new InputSource(xmlReader);
						is.setSystemId(((Schema) e).getDocumentBaseURI());
						schComp.parseSchema(is);
						S2JJAXBModel schModel = schComp.bind();
						JCodeModel jCodeModel = schModel.generateCode(null,
								null);
						xsset = xs.getResult();
						if (xsset != null) {
							XSElementDecl el = xsset.getElementDecl(
									in_parameters.getNamespaceURI(),
									in_parameters.getLocalPart());
							if (el != null) {
								XSType type = el.getType();
								if (!wrappered) {
									log.debug("WRAPPERED!!");
									JType jt = schModel.getJavaType(new QName(type.getTargetNamespace(),
													type.getName())).getTypeClass();
									String t1 = jt.fullName();
									log.debug("Element: "+ part.getName() + " type {"+ type.getTargetNamespace() 
											+ "}"+ type.getName() + " javaType "+ t1);
									CoreElementParameter cep = new CoreElementParameter(
											t1, part.getName(), "IN");
									log.debug("Adding parameter to the element");
									element.getParameters().add(cep);
									if (!jt.isPrimitive()) {
										ElementClass ec = new ElementClass(	new QName(type.getTargetNamespace(),
														type.getName()),jCodeModel._getClass(t1));
										schClasses.put(t1, ec);
									}
									break;

								} else {
									log.debug("Schema Element: "+ el.getName() + " ("+ el.getClass() + ")");
									if (type.isComplexType()) {
										XSParticle particle = type.asComplexType().getContentType().asParticle();
										if (particle != null) {
											XSTerm term = particle.getTerm();
											log.debug("Term: "	+ term.getClass());
											if (term.isModelGroup()) {
												XSModelGroup xsModelGroup = term.asModelGroup();
												XSParticle[] particles = xsModelGroup.getChildren();
												for (XSParticle p : particles) {
													XSTerm pterm = p.getTerm();
													if (pterm.isElementDecl()) {
														XSElementDecl ed = pterm.asElementDecl();
														JType jt = schModel.getJavaType(new QName(
																		ed.getType().getTargetNamespace(),
																		ed.getType().getName())).getTypeClass();
														String t1 = jt.fullName();
														log.debug("Element: "+ ed.getName()	+ " type {"
																+ ed.getType().getTargetNamespace()	+ "}"
																+ ed.getType().getName()+ " javaType "	+ t1);
														CoreElementParameter cep = new CoreElementParameter(
																t1,	ed.getName(),"IN");
														log.debug("Adding parameter to the element");
														element.getParameters().add(cep);
														if (!jt.isPrimitive()) {
															ElementClass ec = new ElementClass(	new QName(
																			ed.getType().getTargetNamespace(),
																			ed.getType().getName()),
																			jCodeModel._getClass(t1));
															schClasses.put(t1,ec);
														}
														// break;
													}
												}
											} else
												throw new JavaModelException(new Exception(
														"Unexpected complex type model"),JavaModelStatus.ERROR);
										} else
											throw new JavaModelException(new Exception(
														"Unexpected complex type model"),JavaModelStatus.ERROR);

									} else if (type.isSimpleType()) {
										String t1 = schModel.getJavaType(new QName(
														type.asSimpleType().getBaseType().getTargetNamespace(),
														type.asSimpleType().getBaseType().getName()))
													.getTypeClass().fullName();
										log.debug("Element: "+ type.asSimpleType().getName()+ " type "
												+ type.asSimpleType().getBaseType().getName()+ " javaType " + t1);
										CoreElementParameter cep = new CoreElementParameter(
												t1, type.asSimpleType().getName(), "IN");
										element.getParameters().add(cep);
										log.debug("Adding parameter to the element");
										break;
									} else
										throw new JavaModelException(new Exception(
													"Unknown element type"),JavaModelStatus.ERROR);
								}
							} else
								log.debug("Element not found in schema set");
						} else
							throw new JavaModelException(new Exception(
									"Schema set not found"),JavaModelStatus.ERROR);
					} catch (Exception ex) {
						ex.printStackTrace();
						throw new JavaModelException(ex, JavaModelStatus.ERROR);
					}
				}
			}
		}
		return element;
	}

	public void generateServiceCode(IProgressMonitor arg0) throws CoreException {
		// TODO generateServiceCode
		// Check if already exists
		IJavaProject project = secondPage.getJavaProject();
		IFolder generated = project.getProject().getFolder("generated");
		if (!generated.exists()) {
			generated.create(true, true, arg0);
			IClasspathEntry cpe = JavaCore.newSourceEntry(generated
					.getFullPath());
			IClasspathEntry[] e = project.getRawClasspath();
			IClasspathEntry[] e2 = new IClasspathEntry[e.length + 1];
			for (int i = 0; i < e.length; i++) {
				e2[i] = e[i];
			}
			e2[e.length] = cpe;
			project.setRawClasspath(e2, arg0);
		}
		try {
			ClassGenerator cg = new ClassGenerator(System.out, generated, project);
			// schClasses = cg.addReferenceClasses(schClasses, schModels);
			// cg.generateClassesCode(schClasses, schModels, arg0);
			cg.generateClassesCode(schModels, arg0);
			cg.generateDummy((ServiceCoreElement) element, secondPage.getClassName()
					.substring(0, secondPage.getClassName().length() - 3), arg0);
			generated.refreshLocal(IFolder.DEPTH_INFINITE, arg0);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	@Override
	public boolean isPageComplete() {
		if (methodNameText.getText().trim().length() > 0
				&& service.getText().trim().length() > 0 
				&& isTypeCompositeCompleate()
				&& portNameText.getText().trim().length() > 0) {
			return true;
		} else
			return false;

	}

	protected abstract boolean isTypeCompositeCompleate();

	@Override
	public void performCancel() {
		performTypeCompositeCancel();

	}

	protected abstract void performTypeCompositeCancel();

	@Override
	public void performFinish(IProgressMonitor monitor) throws CoreException {
		generateServiceCode(monitor);
		performTypeCompositeFinish();

	}

	protected abstract void performTypeCompositeFinish();
}
