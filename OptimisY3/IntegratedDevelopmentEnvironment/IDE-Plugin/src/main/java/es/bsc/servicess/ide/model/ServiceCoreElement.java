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

package es.bsc.servicess.ide.model;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringWriter;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jdt.core.Flags;
import org.eclipse.jdt.core.IJavaModelStatus;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.corext.codemanipulation.StubUtility;
import org.eclipse.swt.widgets.Item;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import es.bsc.servicess.ide.ProjectFile;
import es.bsc.servicess.ide.ResourcesFile;

public class ServiceCoreElement extends ServiceElement {
	private String namespace;
	private String serviceName;
	private String port;
	private List<String> wsdlURIs;

	public ServiceCoreElement(String elementName, int flags, String returnType,
			IMethod method) {
		super(elementName, flags, returnType, method);
		this.namespace = "";
		this.serviceName = "";
		this.port = "";
		wsdlURIs = new LinkedList<String>();
	}

	public ServiceCoreElement(String methodName, int modifier,
			String returnType, IMethod method, String namespace,
			String serviceName, String port) {
		super(methodName, modifier, returnType, method);
		this.namespace = namespace;
		this.serviceName = serviceName;
		this.port = port;
		wsdlURIs = new LinkedList<String>();
	}

	public String getNamespace() {
		return namespace;
	}

	public void setNamespace(String namespace) {
		this.namespace = namespace;
	}

	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	public String getPort() {
		return port;
	}

	public void setPort(String port) {
		this.port = port;
	}

	public List<String> getWsdlURIs() {
		return wsdlURIs;
	}

	public void setWsdlURIs(List<String> wsdlURIs) {
		this.wsdlURIs = wsdlURIs;
	}

	@Override
	protected String[] generateParametersAnnotations(IType t,
			String[] parameterTypes, String[] parameterNames,
			String[] currentParamAnnotations, IProgressMonitor arg0)
			throws JavaModelException {
		String[] paramAnnotations = null;
		if (parameterNames != null) {
			if (parameterNames.length == getParameters().size()) {
				String[] paramDir = new String[getParameters().size()];
				for (int i = 0; i < getParameters().size(); i++) {
					CoreElementParameter p = (CoreElementParameter) getParameters()
							.get(i);
					paramDir[i] = p.getDirection();

				}
				if (t.getCompilationUnit().getImport(
						"integratedtoolkit.types.annotations.Parameter") == null) {
					t.getCompilationUnit().createImport(
							"integratedtoolkit.types.annotations.Parameter",
							null, Flags.AccDefault, arg0);
					t.getCompilationUnit().createImport(
							"integratedtoolkit.types.annotations.Parameter.*",
							null, Flags.AccDefault, arg0);
				} else if (!t
						.getCompilationUnit()
						.getImport(
								"integratedtoolkit.types.annotations.Parameter")
						.exists()) {
					t.getCompilationUnit().createImport(
							"integratedtoolkit.types.annotations.Parameter",
							null, Flags.AccDefault, arg0);
					t.getCompilationUnit().createImport(
							"integratedtoolkit.types.annotations.Parameter.*",
							null, Flags.AccDefault, arg0);
				} else {
					System.out
							.println("Parameter already exists in the import container");
				}
				paramAnnotations = new String[parameterNames.length];
				if (currentParamAnnotations == null) {
					for (int i = 0; i < parameterNames.length; i++) {
						if (parameterTypes[i].equals("Type.FILE")) {
							paramAnnotations[i] = "@Parameter(type = Type.FILE, direction = Direction."
									+ paramDir[i] + ") ";
						} else {
							paramAnnotations[i] = "@Parameter(direction = Direction."
									+ paramDir[i] + ") ";
						}
					}
				} else if (currentParamAnnotations.length == parameterNames.length) {
					for (int i = 0; i < parameterNames.length; i++) {
						if (parameterTypes[i].equals("Type.FILE")) {
							paramAnnotations[i] = "@Parameter(type = Type.FILE, direction = Direction."
									+ paramDir[i]
									+ ") "
									+ currentParamAnnotations[i];
						} else {
							paramAnnotations[i] = "@Parameter(direction = Direction."
									+ paramDir[i]
									+ ") "
									+ currentParamAnnotations[i];
						}
					}
				} else
					throw new JavaModelException(
							new Exception(
									"Method number of annotions and names are different"),
							IJavaModelStatus.ERROR);
			} else
				throw new JavaModelException(new Exception(
						"Method number of directions and names are different"),
						IJavaModelStatus.ERROR);
		}
		return paramAnnotations;
	}

	@Override
	protected String generateAnnotations(IType t, IProgressMonitor arg0)
			throws JavaModelException {
		@SuppressWarnings("restriction")
		String lineDelimiter = StubUtility.getLineDelimiterUsed(t
				.getJavaProject());
		String str = new String();

		if (this.getConstraints().size() > 0) {
			if (t.getCompilationUnit().getImport(
					"integratedtoolkit.types.annotations.Constraints") == null) {
				t.getCompilationUnit().createImport(
						"integratedtoolkit.types.annotations.Constraints",
						null, Flags.AccDefault, arg0);
			} else if (!t
					.getCompilationUnit()
					.getImport(
							"integratedtoolkit.types.annotations.Constraints")
					.exists()) {
				t.getCompilationUnit().createImport(
						"integratedtoolkit.types.annotations.Constraints",
						null, Flags.AccDefault, arg0);
			} else {
				System.out
						.println("Orchestration already exists in the import container");
			}
			str = str.concat("@Constraints");
			boolean first = true;
			for (Entry<String, String> e : this.getConstraints().entrySet()) {
				if (first) {
					str = str.concat("(" + e.getKey() + " = " + e.getValue());
					first = false;
				} else {
					str = str.concat(", " + e.getKey() + " = " + e.getValue());
				}
			}
			str = str.concat(")" + lineDelimiter);
		}
		if (t.getCompilationUnit().getImport(
				"integratedtoolkit.types.annotations.Service") == null) {
			t.getCompilationUnit().createImport(
					"integratedtoolkit.types.annotations.Service", null,
					Flags.AccDefault, arg0);
		} else if (!t.getCompilationUnit()
				.getImport("integratedtoolkit.types.annotations.Service")
				.exists()) {
			t.getCompilationUnit().createImport(
					"integratedtoolkit.types.annotations.Service", null,
					Flags.AccDefault, arg0);
		} else {
			System.out
					.println("Service already exists in the import container");
		}
		str = str.concat("@Service(name = \"" + serviceName
				+ "\", namespace = \"" + namespace + "\", port = \"" + port
				+ "\")" + lineDelimiter);

		return str;
	}

	public void writeLocations(File pr_file, File res_file)
			throws ParserConfigurationException, SAXException, IOException,
			TransformerException {
		ProjectFile project = new ProjectFile(pr_file);
		ResourcesFile resources = new ResourcesFile(res_file);
		for (String loc : this.wsdlURIs) {
			// TODO add maximum calls per location
			project.addServiceWorker(loc, 2);
			resources.addServiceLocation(serviceName, namespace, port, loc);

		}
		project.toFile(pr_file);
		resources.toFile(res_file);
	}

	public String getLabel() {
		String str = new String(getMethodName() + "(");
		boolean first = true;
		for (Parameter p : getParameters()) {
			if (first) {
				first = false;
			} else {
				str = str.concat(", ");
			}
			//OLD VERSION str = str.concat(p.getType());
			//NEW VERSION
			if (p.getType().equalsIgnoreCase(CoreElementParameter.FILE))
				str = str.concat(CoreElementParameter.FILE_T);
			else if (p.getType().equalsIgnoreCase(CoreElementParameter.STRING))
				str = str.concat(CoreElementParameter.STRING_T);
			else if (p.getType().equalsIgnoreCase(CoreElementParameter.CHAR)
					|| p.getType().equals("char"))
				str = str.concat(CoreElementParameter.CHAR_T);
			else if (p.getType().equalsIgnoreCase(CoreElementParameter.BYTE)
					|| p.getType().equals("byte"))
				str = str.concat(CoreElementParameter.BYTE_T);
			else if (p.getType().equalsIgnoreCase(CoreElementParameter.BOOLEAN)
					|| p.getType().equals("boolean"))
				str = str.concat(CoreElementParameter.BOOLEAN_T);
			else if (p.getType().equalsIgnoreCase(CoreElementParameter.SHORT)
					|| p.getType().equals("short"))
				str = str.concat(CoreElementParameter.SHORT_T);
			else if (p.getType().equalsIgnoreCase(CoreElementParameter.INT)
					|| p.getType().equals("int"))
				str = str.concat(CoreElementParameter.INT_T);
			else if (p.getType().equalsIgnoreCase(CoreElementParameter.LONG)
					|| p.getType().equals("long"))
				str = str.concat(CoreElementParameter.LONG_T);
			else if (p.getType().equalsIgnoreCase(CoreElementParameter.DOUBLE)
					|| p.getType().equals("double"))
				str = str.concat(CoreElementParameter.DOUBLE_T);
			else if (p.getType().equalsIgnoreCase(CoreElementParameter.FLOAT)
					|| p.getType().equals("float"))
				str = str.concat(CoreElementParameter.FLOAT_T);
			else
				str = str.concat(CoreElementParameter.OBJECT_T);
			//END NEW_VERSION
		}
		str = str.concat(")" + generateServiceLabel());
		return str;
	}

	private String generateServiceLabel() {
		String str = new String("{" + namespace + "}." + serviceName + "."
				+ port);
		return str;
	}

}
