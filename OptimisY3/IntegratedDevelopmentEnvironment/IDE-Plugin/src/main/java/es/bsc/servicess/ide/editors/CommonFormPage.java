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

package es.bsc.servicess.ide.editors;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.core.resources.IFile;
import org.eclipse.jdt.core.IAnnotation;
import org.eclipse.jdt.core.IClassFile;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IImportDeclaration;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMemberValuePair;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.Signature;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.FormPage;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import es.bsc.servicess.ide.ConstraintsUtils;
import es.bsc.servicess.ide.Logger;
import es.bsc.servicess.ide.ProjectMetadata;
import es.bsc.servicess.ide.TitlesAndConstants;
import es.bsc.servicess.ide.model.CoreElementParameter;
import es.bsc.servicess.ide.model.Dependency;
import es.bsc.servicess.ide.model.MethodCoreElement;
import es.bsc.servicess.ide.model.OrchestrationElement;
import es.bsc.servicess.ide.model.Parameter;
import es.bsc.servicess.ide.model.ServiceCoreElement;
import es.bsc.servicess.ide.model.ServiceElement;

/** Common functionality for the different editor pages
 * @author Jorge Ejarque (Barcelona Supercomputing Center)
 *
 */
public class CommonFormPage extends FormPage {
	
	private static Logger log = Logger.getLogger(CommonFormPage.class);

	/** Constructor
	 * @param editor current editor
	 * @param id Form page id
	 * @param title page title
	 */
	public CommonFormPage(FormEditor editor, String id, String title) {
		super(editor, id, title);
	}
	
	/** Get the Orchestration Element Labels
	 * @param classes Orchestration classes
	 * @param project Service project
	 * @param prMetadata project metadata
	 * @return Array with orchestration element labels
	 */
	public static String[] getOrchestrationElementsLabels(String[] classes, 
			IJavaProject project, ProjectMetadata prMetadata) {
		ArrayList<String> str = new ArrayList<String>();
		if (classes != null) {
			for (String s : classes) {
				try {
					LinkedList<ServiceElement> elements = getOrchestrationElements(
							s, project, prMetadata);
					if (elements != null && elements.size() > 0) {
						for (ServiceElement e : elements) {
							String label = e.getLabel();
							str.add(label);
							//log.debug("Adding element label" + label);
						}
					} else {
						log.warn("No orchestration elements found");
					}
				} catch (Exception e) {
					log.error("Error getting Core element from " + s);
					e.printStackTrace();
				}
			}
		} else {
			log.warn("Orchestration classes are null");
		}
		return str.toArray(new String[str.size()]);
	}

	/** Get service elements
	 * @param classes Orchestration classes
	 * @param type Type of service Elements (ProjectMetadata.ORCH_TYPE/CORE_TYPE/BOTH_TYPE
	 * @param project Service implementation project
	 * @param prMetadata Project metadata
	 * @return
	 */
	public static HashMap<String, ServiceElement> getElements(String[] classes,
			String type, IJavaProject project, ProjectMetadata prMetadata) {

		HashMap<String, ServiceElement> map = new HashMap<String, ServiceElement>();
		if (classes != null) {
			for (String s : classes) {
				if (type == null || !type.equals(ProjectMetadata.ORCH_TYPE)) {
					try {
						LinkedList<ServiceElement> elements = getCoreElements(
								s, project, prMetadata);
						for (ServiceElement e : elements) {
							String label = e.getLabel();
							if ((e instanceof MethodCoreElement)&&
									!type.equals(ProjectMetadata.SERVICE_TYPE)){
								if (map.containsKey(label)) {
									updateConstraints(e, map.get(label));
								}
								//log.debug("Adding " + label	+ " to constraints map");
								map.put(label, e);
							}else if((e instanceof ServiceCoreElement)&&
							!type.equals(ProjectMetadata.METHOD_TYPE)){
								//Check if has dependency
								if (!type.equals(ProjectMetadata.SERVICE_TYPE) || 
									( type.equals(ProjectMetadata.SERVICE_TYPE) &&
										prMetadata.getDependencies(new String[]{e.getLabel()}).size()>0)){
									if (map.containsKey(label)) {
										updateConstraints(e, map.get(label));
									}
									//log.debug("Adding " + label	+ " to constraints map");
									map.put(label, e);
								}
							}
						}
					} catch (Exception e) {
						log.error("Error getting Core element from "
								+ s);
						e.printStackTrace();
					}
				}
				if (type == null || type.equals(ProjectMetadata.ORCH_TYPE)
						|| type.equals(ProjectMetadata.BOTH_TYPE)) {
					try {
						LinkedList<ServiceElement> elements = getOrchestrationElements(
								s,project, prMetadata);
						for (ServiceElement e : elements) {
							String label = e.getLabel();
							if (map.containsKey(label)) {
								e = updateConstraints(e, map.get(label));
							}
							//log.debug("Adding " + label	+ " to constraints map");
							map.put(label, e);
						}
					} catch (Exception e) {
						log.error("Error getting Core element from "+ s);
						e.printStackTrace();
					}
				}
			}

		}
		return map;
	}
	
	/** Update element constraints
	 * @param new_el new service element
	 * @param old_el previous version of element
	 * @return updated element
	 * @throws Exception
	 */
	private static ServiceElement updateConstraints(ServiceElement new_el,
			ServiceElement old_el) throws Exception {
		Set<Map<String, String>> set = new HashSet<Map<String, String>>();
		set.add(new_el.getConstraints());
		set.add(old_el.getConstraints());
		if (ConstraintsUtils.checkConsistency(set)) {
			new_el.setConstraints(ConstraintsUtils.getMaxConstraints(set));
			return new_el;
		} else
			throw new Exception(
					"Uncompatible types in different declarations of "
							+ new_el.getLabel());
	}
	
	/** Ge the orchastration element from an orchestration class
	 * @param serviceClass Orchestration class
	 * @param project Service implementation project
	 * @param prMetadata Project Metadata
	 * @return List of orchestration element
	 * @throws Exception 
	 */
	public static LinkedList<ServiceElement> getOrchestrationElements(
			String serviceClass, IJavaProject project, ProjectMetadata prMetadata) 
					throws Exception {
		LinkedList<ServiceElement> elements;
		if(!prMetadata.isExternalOrchestrationClass(serviceClass)){
			elements = getOEsformNonExternalClass(serviceClass, project, prMetadata);
		}else
			elements = getOEsformExternalClass(serviceClass, project, prMetadata);
		log.debug(" There are " + elements.size() + " elements to print");
		return elements;
	}
		
		

	private static LinkedList<ServiceElement> getOEsformExternalClass(
			String serviceClass, IJavaProject project,
			ProjectMetadata prMetadata) throws Exception {
		LinkedList<ServiceElement> elements = new LinkedList<ServiceElement>();
		IType orchClass = getExternalOrchestrationClass(serviceClass, project, prMetadata);
		for (OrchestrationElement oe:prMetadata.getOrchestrationElementFormExternalClass(orchClass).values())
			elements.add(oe);
		return elements;
	}
	
	

	private static LinkedList<ServiceElement> getOEsformNonExternalClass(
			String serviceClass, IJavaProject project, ProjectMetadata prMetadata) 
					throws PartInitException, JavaModelException {
		LinkedList<ServiceElement> elements = new LinkedList<ServiceElement>();
		ICompilationUnit cu = getOrchestrationClass(serviceClass, project, prMetadata);
		if (cu != null) {
		IType[] types = cu.getTypes();
		// if (types != null){
		for (IType type : types) {
			IMethod[] methods = type.getMethods();
			for (IMethod method : methods) {
				IAnnotation[] annotations = method.getAnnotations();
				for (IAnnotation annotation : annotations) {
					/*log.debug("Type: " + type.getElementName()
								+ " Method: " + method.getElementName()
								+ " Annotation: "
								+ annotation.getElementName());*/
					if (annotation.getElementName().equalsIgnoreCase(
							"Orchestration")) {
						//log.debug("adding orchestration element");
						OrchestrationElement ce = new OrchestrationElement(
								method.getElementName(), method.getFlags(),
								getFQType(cu, method.getReturnType(), project),
								method, serviceClass);
						if (method.getAnnotation("WebMethod") != null) {
							ce.setPartOfServiceItf(true);
						} else {
							ce.setPartOfServiceItf(false);
						}
							IAnnotation constraints = method
									.getAnnotation("Constraints");
							if (constraints != null && constraints.exists()) {
								IMemberValuePair[] pairs = constraints
										.getMemberValuePairs();
								for (IMemberValuePair pair : pairs) {
									ce.getConstraints().put(
											pair.getMemberName(),
											pair.getValue().toString());
								}
							}
							// SetParameters
							/*log.debug("Raw param names: "
									+ method.getRawParameterNames());*/
							String[] names = method.getParameterNames();
							String[] partypes = method.getParameterTypes();
							for (int i = 0; i < method
									.getNumberOfParameters(); i++) {
								Parameter p = new Parameter(getFQType(cu,
										partypes[i], project), names[i]);
								ce.getParameters().add(p);
							}
						elements.add(ce);
					}
				}
			}
		}
		return elements;
		} else {
			throw new PartInitException("Compilation Unit not found");

		}
		
	}

	/** Get the eclipse compilation unit for the core element interface 
	 * form a selected orchestration class
	 * @param serviceClass Orchestration class
	 * @param project Service implementation project
	 * @param prMetadata Project Metadata
	 * @return Core element interface
	 * @throws PartInitException
	 * @throws JavaModelException
	 */
	public static ICompilationUnit getCEInterface(String serviceClass, 
			IJavaProject project, ProjectMetadata prMetadata)
			throws PartInitException, JavaModelException {
		IPackageFragmentRoot root = prMetadata.getPackageFragmentRoot(project);
		log.debug("Getting Package fragment "+ Signature.getQualifier(serviceClass) );
		IPackageFragment frag = root.getPackageFragment(Signature.getQualifier(serviceClass));
		log.debug("Comparing " + frag.getElementName() + " with "
				+ serviceClass);
		if (frag.exists()) {
			log.debug("Looking for "+ Signature.getSimpleName(serviceClass) + "Itf.java in "
					+ frag.getPath());
			return frag.getCompilationUnit(Signature.getSimpleName(serviceClass)+ "Itf.java");
		} else
			throw new PartInitException("Package fragment for "+ serviceClass + " not found");
	}

	/** Get the eclipse compilation unit for the orchestration class
	 * @param serviceClass Name of the orchestration class
	 * @param project Service implementation project
	 * @param prMetadata Project Metadata
	 * @return Compilation unit of the orchetration class
	 * @throws PartInitException
	 * @throws JavaModelException
	 */
	public static ICompilationUnit getOrchestrationClass(String serviceClass, 
			IJavaProject project, ProjectMetadata prMetadata)
			throws PartInitException, JavaModelException {
		IPackageFragment frag = prMetadata.getMainPackageFragment(project);
		log.debug("Comparing " + frag.getElementName() + " with "
				+ serviceClass);
		if (serviceClass.startsWith(frag.getElementName())) {
			log.debug("Looking for "+ Signature.getSimpleName(
					serviceClass) + ".java in " + frag.getPath());
			return frag.getCompilationUnit(Signature.getSimpleName(
					serviceClass) + ".java");
		} else
			throw new PartInitException("compilation Unit not found");
	}
	
	/** Get the eclipse compilation unit for the orchestration class
	 * @param serviceClass Name of the orchestration class
	 * @param project Service implementation project
	 * @param prMetadata Project Metadata
	 * @return Compilation unit of the orchetration class
	 * @throws Exception 
	 */
	public static IType getExternalOrchestrationClass(String serviceClass, 
			IJavaProject project, ProjectMetadata prMetadata)
			throws Exception {
		String libraryLocation = (prMetadata.getOrchestrationClass(serviceClass).getLibraryLocation());
		IType type = null;
		for (IPackageFragmentRoot r : project.getAllPackageFragmentRoots()) {
			/*log.debug("PFR: " + r.getElementName()+ " entry: "
				+ r.getResolvedClasspathEntry().getPath() + "(Looking for: "+ libraryLocation+")");*/
			if (r.getResolvedClasspathEntry().getPath().toOSString()
				.trim().equals(libraryLocation.trim())) {
				IPackageFragment frag = r.getPackageFragment(Signature.getQualifier(serviceClass));
				return frag.getClassFile(Signature.getSimpleName(
					serviceClass) + ".class").getType();
			}
		}
		throw new PartInitException("Type not found");
	}

	/** Get the core element of an orchestration class
	 * @param serviceClass Name of the orchestration class
	 * @param project Service implementation project
	 * @param prMetadata Project metadata
	 * @return List of the core elements
	 * @throws JavaModelException
	 * @throws PartInitException
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 */
	public static LinkedList<ServiceElement> getCoreElements(String serviceClass, 
			IJavaProject project, ProjectMetadata prMetadata)throws JavaModelException, 
			PartInitException, ParserConfigurationException, SAXException, IOException {
		LinkedList<ServiceElement> elements = new LinkedList<ServiceElement>();
		ICompilationUnit cu = getCEInterface(serviceClass, project, prMetadata);
		if (cu != null) {
			IType[] types = cu.getTypes();
			log.debug("Number of types: " + types.length);
			for (IType type : types) {
				IMethod[] methods = type.getMethods();
				/*log.debug("Number of methods for type "
						+ type.getElementName() + ": " + methods.length);*/
				for (IMethod method : methods) {
					IAnnotation[] annotations = method.getAnnotations();
					for (IAnnotation annotation : annotations) {
						/*log.debug("Type: " + type.getElementName()
								+ " Method: " + method.getElementName()
								+ " Annotation: "+ annotation.getElementName());*/
						if (annotation.getElementName().equalsIgnoreCase(
								"Method")) {
							//log.debug("Adding method core element");
							String returnType = getFQType(cu,
									method.getReturnType(), project);
							MethodCoreElement ce = new MethodCoreElement(
									method.getElementName(), method.getFlags(),
									returnType, method);
							// Set Constraints
							IAnnotation constraints = method
									.getAnnotation("Constraints");
							if ((constraints != null) && (constraints.exists())) {
								IMemberValuePair[] pairs = constraints
										.getMemberValuePairs();

								for (IMemberValuePair pair : pairs) {
									ce.getConstraints().put(
											pair.getMemberName(),
											pair.getValue().toString());
								}
							}
							// Set Parameters
							String[] names = method.getParameterNames();
							String[] partypes = method.getParameterTypes();
							String source = method.getSource();
							Map<String, String> dirs = new HashMap<String, String>();
							Map<String, String> paramt = new HashMap<String, String>();
							getDirectionsAndTypes(source, names, partypes, dirs, paramt);

							CoreElementParameter p;
							for (int i = 0; i < method.getNumberOfParameters(); i++) {
								//log.debug("Parameter " + names[i] + "Type: " + partypes[i]);
								String t = getFQType(cu, partypes[i], project);
								if (t.equalsIgnoreCase("String")
										|| t.equalsIgnoreCase("java.lang.String")) {
									if (paramt.get(names[i]) != null) {
										if (paramt
												.get(names[i])
												.equalsIgnoreCase(
														CoreElementParameter.FILE)) {
											p = new CoreElementParameter(
													CoreElementParameter.FILE,
													names[i],
													dirs.get(names[i]));
										} else if (paramt
												.get(names[i])
												.equalsIgnoreCase(
														CoreElementParameter.STRING)) {
											p = new CoreElementParameter(
													CoreElementParameter.STRING,
													names[i], dirs
															.get(names[i]));
										} else {
											p = new CoreElementParameter(t,
													names[i],
													dirs.get(names[i]));
										}
									} else {
										p = new CoreElementParameter(t,
												names[i], dirs.get(names[i]));
									}
								} else {
									p = new CoreElementParameter(t, names[i],
											dirs.get(names[i]));
								}
								ce.getParameters().add(p);
							}
							IMemberValuePair[] pairs = annotation
									.getMemberValuePairs();
							for (IMemberValuePair pair : pairs) {
								if (pair.getMemberName().equalsIgnoreCase(
										"declaringClass")) {
									ce.setDeclaringClass((String) pair
											.getValue());
								} else if (pair.getMemberName()
										.equalsIgnoreCase("isModifier")) {
									ce.setModifier((Boolean) pair.getValue());
								} else if (pair.getMemberName()
										.equalsIgnoreCase("isInit")) {
									ce.setInit((Boolean) pair.getValue());
								}
							}
							elements.add(ce);
						} else if (annotation.getElementName()
								.equalsIgnoreCase("Service")) {
							//log.debug("Adding service core element");
							ServiceCoreElement ce = new ServiceCoreElement(
									method.getElementName(), method.getFlags(),
									Signature.toString(method.getReturnType()),
									method);
							IMemberValuePair[] pairs = annotation
									.getMemberValuePairs();
							for (IMemberValuePair pair : pairs) {
								if (pair.getMemberName().equalsIgnoreCase(
										"namespace")) {
									ce.setNamespace((String) pair.getValue());
								} else if (pair.getMemberName()
										.equalsIgnoreCase("name")) {
									ce.setServiceName((String) pair.getValue());
								} else if (pair.getMemberName()
										.equalsIgnoreCase("port")) {
									ce.setPort((String) pair.getValue());
								}
							}
							IAnnotation constraints = method
									.getAnnotation("Constraints");
							if ((constraints != null) && (constraints.exists())) {
								pairs = constraints.getMemberValuePairs();
								for (IMemberValuePair pair : pairs) {
									ce.getConstraints().put(
											pair.getMemberName(),
											pair.getValue().toString());
								}
							}
							// Set Parameters
							String[] names = method.getParameterNames();
							String[] partypes = method.getParameterTypes();
							String source = method.getSource();
							Map<String, String> dirs = new HashMap<String, String>();
							Map<String, String> paramt = new HashMap<String, String>();
							getDirectionsAndTypes(source, names, partypes, dirs, paramt);

							CoreElementParameter p;
							for (int i = 0; i < method.getNumberOfParameters(); i++) {
								String t = Signature.toString(partypes[i]);
								if (t.equalsIgnoreCase("String")
										|| t.equalsIgnoreCase("java.lang.String")) {
									if (paramt.get(names[i]).equalsIgnoreCase(
											CoreElementParameter.FILE)) {
										p = new CoreElementParameter(
												CoreElementParameter.FILE,
												names[i], dirs.get(names[i]));
									} else if (paramt
											.get(names[i])
											.equalsIgnoreCase(
													CoreElementParameter.STRING)) {
										p = new CoreElementParameter(
												CoreElementParameter.STRING,
												names[i], dirs.get(names[i]));
									} else {
										p = new CoreElementParameter(t,
												names[i], dirs.get(names[i]));
									}
								} else {
									p = new CoreElementParameter(t, names[i],
											dirs.get(names[i]));
								}
								ce.getParameters().add(p);
							}
							// Set Locations
							ArrayList<String> locations = getLocations(
									ce.getNamespace(), ce.getServiceName(),
									ce.getPort(), project, prMetadata);
							ce.setWsdlURIs(locations);

							elements.add(ce);
						}
					}
				}

			}
		} else {
			throw new PartInitException("compilation Unit not found");

		}
		log.debug(" There are " + elements.size()+ " elements to print");
		return elements;
	}

	/** Get the fully qualified domain name of a compilation unit
	 * @param cu Compilation unit
	 * @param signature Class signature
	 * @param project Service implementation project
	 * @return Fully qualified domain name
	 * @throws JavaModelException
	 */
	private static String getFQType(ICompilationUnit cu, 
			String signature, IJavaProject project) throws JavaModelException {
		if (signature.equals(Signature.SIG_BOOLEAN)
				|| signature.equals(Signature.SIG_BYTE)
				|| signature.equals(Signature.SIG_CHAR)
				|| signature.equals(Signature.SIG_DOUBLE)
				|| signature.equals(Signature.SIG_FLOAT)
				|| signature.equals(Signature.SIG_INT)
				|| signature.equals(Signature.SIG_LONG)
				|| signature.equals(Signature.SIG_SHORT)
				|| signature.equals(Signature.SIG_VOID)) {
			return Signature.toString(signature);
		} else {
			String qualifier = Signature.getQualifier(signature);
			if (qualifier != null && qualifier.length() > 0) {
				return Signature.toString(signature);
			} else {
				String classname = Signature.getSignatureSimpleName(signature);
				IImportDeclaration[] imports = cu.getImports();
				for (IImportDeclaration imp : imports) {
					String name = imp.getElementName();
					if (name.endsWith(".*")) {
						String fqclass = searchClassInPackages(
								project, name.substring(0, name.indexOf(".*")),
								classname);
						if (fqclass != null)
							return fqclass;
					} else if (name.endsWith(classname)) {
						return name;
					}
				}
				return "java.lang." + classname;
			}
		}
	}

	/** Search a class in a service java package
	 * @param project Service implementation project
	 * @param packageName package name
	 * @param classname class name to search
	 * @return Fully qualified domain name of the class if find, or null if not find
	 * @throws JavaModelException
	 */
	private static String searchClassInPackages(IJavaProject project,
			String packageName, String classname)
			throws JavaModelException {
		for (IPackageFragmentRoot r : project.getAllPackageFragmentRoots()) {
			IPackageFragment pack = r.getPackageFragment(packageName);
			if (pack != null && pack.exists()) {
				ICompilationUnit cu = pack.getCompilationUnit(classname
						+ ".java");
				if (cu != null && cu.exists()) {
					return packageName + "." + classname;
				} else {
					IClassFile cf = pack.getClassFile(classname + ".class");
					if (cf != null && cf.exists()) {
						return packageName + "." + classname;
					}
				}
			}
		}
		return null;
	}

	/** Get locations for a service
	 * @param namespace External service namespace
	 * @param serviceName External service name
	 * @param port External service port name
	 * @param project Service implementation project
	 * @param prMetadata Project metadata
	 * @return  List of service locations URLs
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 * @throws JavaModelException
	 */
	private static ArrayList<String> getLocations(String namespace,
			String serviceName, String port, IJavaProject project, 
			ProjectMetadata prMetadata)	throws ParserConfigurationException,
			SAXException, IOException, JavaModelException {
		ArrayList<String> loc = new ArrayList<String>();
		IFile f = prMetadata.getResourcesFile(project);
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = factory.newDocumentBuilder();
		org.w3c.dom.Document res_doc = docBuilder.parse(f.getRawLocation()
				.toFile());
		NodeList services = res_doc.getDocumentElement().getElementsByTagName(
				"Service");
		/*log.debug("Looking for {" + namespace + "}" + serviceName
				+ "(" + port + ")");*/
		for (int i = 0; i < services.getLength(); i++) {
			Node node = services.item(i);
			NamedNodeMap map = node.getAttributes();
			for (int k = 0; k < map.getLength(); k++) {
				/*log.debug("Attribute:" + map.item(k).getNodeName()
						+ "=" + map.item(k).getTextContent());*/
			}
			NodeList properties = node.getChildNodes();
			boolean sn = false;
			boolean ns = false;
			boolean p = false;
			for (int j = 0; j < properties.getLength(); j++) {
				Node prop = properties.item(j);
				if (prop != null) {
					/*log.debug("Property " + prop.getNodeName() + "="
							+ prop.getTextContent() + " ("
							+ prop.getNodeValue() + ") ");*/
					if (prop.getNodeName().equals("Name")) {
						if (prop.getTextContent().equals(serviceName)) {
							//log.debug("Name " + serviceName + " found");
							sn = true;
						}
					}
					if (prop.getNodeName().equals("Namespace")) {
						if (prop.getTextContent().equals(namespace)) {
							//log.debug("Namespace " + namespace + " found");
							ns = true;
						}
					}
					if (prop.getNodeName().equals("Port")) {
						if (prop.getTextContent().equals(port)) {
							//log.debug("Port " + port + " found");
							p = true;
						}
					}
					if (p && sn && ns) {
						//log.debug("service, namespace and port found");
						String location = node.getAttributes()
								.getNamedItem("wsdl").getTextContent();
						if (location != null) {
							/*log.debug("Location found for service "
									+ serviceName + " " + location);*/
							loc.add(location);
							break;
						} else {
							log.warn("wsdl location not found");
						}
					}
				}
			}
		}
		return loc;
	}

	/** Get parameters directions and types from a source code
	 * @param source Part of source
	 * @param names Array of parameter names
	 * @param partypes Array of java classes Parameters
	 * @param dirs Map of parameter directions
	 * @param paramt Map of parameter types
	 * @throws PartInitException
	 */
	private static void getDirectionsAndTypes(String source, String[] names,
			String[] partypes, Map<String, String> dirs, Map<String, String> paramt)
			throws PartInitException {
		int last_name = 0;
		log.debug("Inspecting source: "+ source);
		for (int i = 0; i < names.length; i++) {
			String direction = null;
			String type = null;
			/*log.debug("looking for parameter "+
					Signature.getSignatureSimpleName(partypes[i])+" "+names[i]);*/
			int name_index = source.indexOf(Signature.getSignatureSimpleName(partypes[i])
					+" "+names[i], last_name);
			if (name_index>=0){
				String parameter = source.substring(last_name, name_index);
				int annotation_index = parameter.indexOf("@Parameter");
				//log.debug("Parameter "+i+ " is "+ parameter);
				if (annotation_index >= 0) {
					// int aparam = annotation_index+10;
					int brak_st = parameter.indexOf("(", annotation_index);
					int brak_end = parameter.indexOf(")", brak_st);
					if (brak_st >= 0 && brak_end >= 0) {
						String annotation = parameter.substring(brak_st + 1,
								brak_end);
						int direction_index = annotation.indexOf("direction");
						int type_index = annotation.indexOf("type");
						if (direction_index >= 0) {
							int direc = annotation.indexOf("Direction.",
									direction_index);
							int comma = annotation.indexOf(",", direc);
							if (comma < 0) {
								direction = annotation.substring(direc + "Direction.".length()).trim();
							} else {
								direction = annotation.substring(direc + "Direction.".length(), comma)
										.trim();
							}
						} else {
							direction = "IN";
						}
						if (type_index >= 0) {
							int tt = annotation.indexOf("Type.", type_index);
							int comma = annotation.indexOf(",", tt);
							if (comma < 0) {
								type = annotation.substring(tt).trim();
							} else {
								type = annotation.substring(tt, comma).trim();
							}
						} else
							type = "Type.OBJECT";
					} else {
						log.debug("Parameter annotation properties not found");
						type = "Type.OBJECT";
						direction = "IN";
					}
				} else {
					log.error("@Parameter annotation not found for parameter");
					throw new PartInitException(
							"@Parameter annotation not found for parameter "
									+ names[i]);
				}
			} else {
				log.error("Parameter "+Signature.getSignatureSimpleName(partypes[i])
						+" "+names[i]+" not found");
				throw new PartInitException("Parameter "+
						Signature.getSignatureSimpleName(partypes[i])+" "+names[i]+" not found");
			}	
			if (direction != null) {
				dirs.put(names[i], direction);
				//log.debug("param: " + names[i] + " direction: "	+ direction);
			}
			if (type != null) {
				paramt.put(names[i], type);
				//log.debug("param: " + names[i] + " type: " + type);
			}

			last_name = name_index;
		}
	}
	
	/**
	 * Check if a the old elements are the same as the new elements
	 * @param currentItems
	 * @param newItems
	 * @return
	 */
	protected boolean hasElementInterfaceChanges(String[] currentItems,
			String[] newItems) {
		
		for (String item:currentItems){
			boolean found = false;
			for (String newItem:newItems){
				//log.debug("Comparing items ("+item+", "+newItem+")");
				if (item.equals(newItem)){
					found = true;
					break;
				}				
			}
			if (!found){
				//log.debug("Item "+item +" not found.");
				return true;
			}
		}
		return false;
	}
	
	/** 
	 * @param pr_meta
	 * @param orchestrationClasses
	 * @return
	 */
	/*public static boolean shouldBeWarFile(Map<String,String> orchestrationClasses) {
		//check if they contains service interfaces
		for (String type:orchestrationClasses.values()){
			//log.debug("Class type "+ type);
			if (type.equalsIgnoreCase(TitlesAndConstants.WS_CLASS))
				return true; 
		}
		return false;
	}*/
}
