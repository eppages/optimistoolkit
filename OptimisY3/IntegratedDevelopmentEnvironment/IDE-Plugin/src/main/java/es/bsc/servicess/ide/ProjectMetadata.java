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

package es.bsc.servicess.ide;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.io.FilenameUtils;
import org.eclipse.core.resources.IFile;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import es.bsc.servicess.ide.model.Dependency;
import es.bsc.servicess.ide.model.OrchestrationClass;
import es.bsc.servicess.ide.model.OrchestrationElement;
import es.bsc.servicess.ide.model.ServiceElement;

public class ProjectMetadata {
	/* XML File tags */
	public static final String METADATA_FILENAME = "metadata.xml";
	public static final String PROJECT_FILENAME = "project.xml";
	public static final String RESOURCES_FILENAME = "resources.xml";
	public static final String METADATA_FOLDER = "META-INF";
	public static final String CLASSES_FOLDER = "classes";
	public static final String OUTPUT_FOLDER = "output";
	public static final String GENERATED_FOLDER = "generated";
	private static final String PROJECT_METADATA_TAG = "project_metadata";
	private static final String SOURCEDIR_ATTR = "source_dir";
	private static final String RT_LOCATION_ATTR = "runtime_location";
	private static final String ORCH_CLASS_TAG = "orchestration_class";
	private static final String LOCATION_ATTR = "location";
	private static final String CLASS_ATTR = "class";
	private static final String LABEL_ATTR = "label";
	private static final String DEPENDENCY_TAG = "dependency";
	private static final String ELEMENT_TAG = "element";
	private static final String PACKAGE_TAG = "package";
	private static final String IMAGE_TAG = "image";
	private static final String ELASTICITY_TAG = "elasticity";
	private static final String ELEMENT_ATTR = "element";
	private static final String MAX_ATTR = "max";
	private static final String MIN_ATTR = "min";
	private static final String INI_ATTR = "initial";
	private static final String NAME_ATTR = "name";
	private static final String ID_ATTR = "id";
	private static final String URL_ATTR = "url";
	private static final String MAIN_PACKAGE_ATTR = "main_package";
	private static final String TYPE_ATTR = "type";
	private static final String OTHER_INFO_ATTR = "other_info";
	private static final String PACKAGE_ATTR = "package";
	private static final String MAX_RES_PROPERTIES_TAG = "max_resource_properties";
	private static final String NUM_CORES_ATTR = "num_cores";
	private static final String PHYS_MEM_ATTR = "phys_mem";
	private static final String LOCAL_DISK_ATTR = "local_disk";
	private static final String EXTERNAL_LOC_ATTR = "external_location";
	private static final String RETURN_TYPE_ATTR = "return_type";
	private static final String MODIFIER_ATTR = "modifier";
	private static final String IMPORTED_ATTR = "imported";
	private static final String CONSTRAINT_TAG = "constraint";
	private static final String VALUE_ATTR = "value";
	private static final String LIBRARY_LOC_ATTR = "library_location";
	
	private static final String DEFAULT_RES_PROPERTIES_TAG = "max_resource_properties";
	
	/** Service manifest file name (it will be configurable at Y3) */
	public static final String SERVICE_MANIFEST = "service_manifest.xml";
	
	/** Name of the entry path for the Dependencies Classpath Container */
	public static final String DEPENDENCY_ENTRYPATH = "Dependencies";
	
	/** Suffix added to the runtime location to get the IT jar package */
	public static final String ITJAR_EXT = "/lib/IT.jar";
	
	/* Types of Element Packages */
	public static final String CORE_TYPE = "core";
	public static final String METHOD_TYPE = "method";
	public static final String SERVICE_TYPE = "service";
	public static final String ORCH_TYPE = "orchestration";
	public static final String BOTH_TYPE = "both";
	
	public static final String ORCH_PACK_TYPE = "Orchestration Elements";
	public static final String SER_CORE_PACK_TYPE = "Service Core Elements";
	public static final String METH_CORE_PACK_TYPE = "Method Core Elements";
	public static final String ALL_PACK_TYPE = "All Element Types";
	public static final String[] SUPPORTED_PACK_TYPES = {ORCH_PACK_TYPE, METH_CORE_PACK_TYPE, SER_CORE_PACK_TYPE};
	
	/* Dependency types */
	public static final String JAR_DEP_TYPE = "JAR Library";
	public static final String CLASS_FOLDER_DEP_TYPE = "Class Folder";
	public static final String FOLDER_DEP_TYPE = "Normal Folder";
	public static final String ZIP_DEP_TYPE = "ZIP File";
	public static final String WAR_DEP_TYPE = "WAR File";
	public static final String FILE_DEP_TYPE = "Other Type File";
	
	/** Array to store the different service element dependency options */
	public static final String[] DEP_OPTIONS = new String[] { JAR_DEP_TYPE,
			CLASS_FOLDER_DEP_TYPE, FOLDER_DEP_TYPE, ZIP_DEP_TYPE, WAR_DEP_TYPE,
			FILE_DEP_TYPE };
	
	/* Configurable for Y3*/
	private static final int DEFAULT_ELASTICITY_VALUE = 1;
	
	/*Name of the package folder */
	public static final String PACKAGES_FOLDER = "packages";
	public static final String IMPORT_FOLDER = "imported";	
	public static final String EXTERNAL_PACKS_FOLDER = "external";
	private static Logger log = Logger.getLogger(ProjectMetadata.class);

	private Element projectElement;
	private Document pr_doc;

	/** Constructor
	 * @param file existing Project metadata file
	 * @throws SAXException
	 * @throws IOException
	 * @throws ParserConfigurationException
	 */
	public ProjectMetadata(File file) throws SAXException, IOException,
			ParserConfigurationException {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = factory.newDocumentBuilder();
		pr_doc = docBuilder.parse(file);
		this.projectElement = pr_doc.getDocumentElement();
		if (pr_doc == null || projectElement == null) {
			throw new IOException("Project Element is null");
		}
	}

	/** Constructor
	 * @param name Project name
	 * @throws ParserConfigurationException
	 */
	public ProjectMetadata(String name) throws ParserConfigurationException {

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = factory.newDocumentBuilder();
		pr_doc = docBuilder.newDocument();
		projectElement = pr_doc.createElement(PROJECT_METADATA_TAG);
		pr_doc.appendChild(projectElement);
		projectElement.setAttribute(NAME_ATTR, name);

	}

	/** Set the project source folder
	 * @param sourcedir Project source folder
	 */
	public void setSourceDir(String sourcedir) {
		projectElement.setAttribute(SOURCEDIR_ATTR, sourcedir);
	}

	/** Get the project source folder
	 * @return Project source folder
	 */
	public String getSourceDir() {
		return projectElement.getAttribute(SOURCEDIR_ATTR);
	}
	
	/** Get the Java Package root of the Service source folder 
	 * @param project Java project where the service is implemented
	 * @return
	 * @throws JavaModelException
	 */
	public IPackageFragmentRoot getPackageFragmentRoot(IJavaProject project) throws JavaModelException{
		return project.findPackageFragmentRoot(project.getPath().append(getSourceDir()));
	}

	/**Get the main Java package of the Service source folder
	 * @param project Java project where the service is implemented
	 * @return
	 * @throws JavaModelException
	 */
	public IPackageFragment getMainPackageFragment(IJavaProject project) throws JavaModelException{
		IPackageFragmentRoot root = getPackageFragmentRoot(project);
		if (root != null) {
			return root.getPackageFragment(getMainPackageName());
		}else
			return null;
	}
	
	/**Set the location of the programming model runtime installation
	 * @param runtimeLocation Location of the programming model runtime installation
	 */
	public void setRuntimeLocation(String runtimeLocation) {
		projectElement.setAttribute(RT_LOCATION_ATTR, runtimeLocation);
	}

	/** Get the location of the programming model runtime installation
	 * @return
	 */
	public String getRuntimeLocation() {
		return projectElement.getAttribute(RT_LOCATION_ATTR);
	}

	/**Add a class which contains orchestration elements
	 * @param qualifiedClassname Fully qualified name of the class
	 */
	public void addOrchestrationClass(String qualifiedClassname, String type) {
		Element orch_class = pr_doc.createElement(ORCH_CLASS_TAG);
		orch_class.setAttribute(CLASS_ATTR, qualifiedClassname);
		orch_class.setAttribute(TYPE_ATTR, type);
		projectElement.appendChild(orch_class);
	}

	public void addExternalOrchestrationClass(String qualifiedClassname,
			String externalLocation, String libraryLocation) {
		Element orch_class = pr_doc.createElement(ORCH_CLASS_TAG);
		orch_class.setAttribute(CLASS_ATTR, qualifiedClassname);
		orch_class.setAttribute(TYPE_ATTR, TitlesAndConstants.EXTERNAL_CLASS);
		orch_class.setAttribute(EXTERNAL_LOC_ATTR, externalLocation);
		orch_class.setAttribute(LIBRARY_LOC_ATTR, libraryLocation);
		projectElement.appendChild(orch_class);
	}

	/** Get classes which contains Orchestration Elements
	 * @return Sting with the FQDN of the orchestration classes
	 */
	public String[] getAllOrchestrationClasses() {
		NodeList orch_classes = projectElement
				.getElementsByTagName(ORCH_CLASS_TAG);
		if (orch_classes != null) {
			String[] classes = new String[orch_classes.getLength()];
			for (int i = 0; i < orch_classes.getLength(); i++) {
				classes[i] = ((Element) (orch_classes.item(i)))
						.getAttribute(CLASS_ATTR);
			}
			return classes;
		} else {
			log.warn("No orchestration classes found");
			return new String[0];
		}
	}
	
	/** Get classes which contains Orchestration Elements for external packages
	 * @return Sting with the FQDN of the orchestration classes
	 */
	public String[] getExternalOrchestrationClasses() {
		NodeList orch_classes = projectElement
				.getElementsByTagName(ORCH_CLASS_TAG);
		if (orch_classes != null) {
			ArrayList<String> classes = new ArrayList<String>();
			for (int i = 0; i < orch_classes.getLength(); i++) {
				Element oeClass =(Element) (orch_classes.item(i));
				if (oeClass.getAttribute(TYPE_ATTR).equals(TitlesAndConstants.EXTERNAL_CLASS))
					classes.add(oeClass.getAttribute(CLASS_ATTR));
			}
			return classes.toArray(new String[classes.size()]);
		} else {
			log.warn("No orchestration classes found");
			return new String[0];
		}
	}
	
	/** Get packages which contains Orchestration Elements for external packages
	 * @return Sting with the FQDN of the orchestration classes
	 *
	public Map<String,List<String>> getExternalOrchestrationClassesPackages() throws Exception {
		NodeList orch_classes = projectElement
				.getElementsByTagName(ORCH_CLASS_TAG);
		Map<String,List<String>> map = new HashMap<String,List<String>>();
		if (orch_classes != null) {
			ArrayList<String> classes = new ArrayList<String>();
			for (int i = 0; i < orch_classes.getLength(); i++) {
				Element oeClass =(Element) (orch_classes.item(i));
				if (oeClass.getAttribute(TYPE_ATTR).equals(TitlesAndConstants.EXTERNAL_CLASS)){
					String pack = oeClass.getAttribute(EXTERNAL_LOC_ATTR);
					String library = oeClass.getAttribute(LIBRARY_LOC_ATTR);
					try{
						if (library == null){
							library = "imported/"+PackagingUtils.getPackageName(pack)+"/WEB-INF/classes/";
						}
						if (!map.containsKey(pack)){
							map.put(pack,new LinkedList<String>());
						}
						map.get(pack).add(library);
					}catch(Exception e){
						log.warn(e.getMessage());
					}
				}
			}
		} else {
			log.warn("No orchestration classes found");
		}
		return map;
	}*/
	public Dependency getExternalOrchestrationClassDependency(IType oeType) throws Exception {
		if (oeType != null){
			NodeList orch_classes = projectElement
				.getElementsByTagName(ORCH_CLASS_TAG);
			Map<String,List<String>> map = new HashMap<String,List<String>>();
			if (orch_classes != null) {
				ArrayList<String> classes = new ArrayList<String>();
				for (int i = 0; i < orch_classes.getLength(); i++) {
					Element oeClass =(Element) (orch_classes.item(i));
					if (oeClass.getAttribute(TYPE_ATTR).equals(TitlesAndConstants.EXTERNAL_CLASS)
							&& oeClass.getAttribute(CLASS_ATTR).equals(oeType.getFullyQualifiedName())){
						String pack = oeClass.getAttribute(EXTERNAL_LOC_ATTR);
						String importedPath = oeType.getJavaProject().getProject().
								getFolder(IMPORT_FOLDER).getRawLocation().toOSString();
						if (pack.startsWith(importedPath)){
							try{
								return getParentImportedDependency(	pack, importedPath);
							}catch(Exception e){
								log.warn("Parent imported dependency not found setting " +
										"current external location");
								String type = getTypeByFileName(pack);
								return generateDependency(getDependencyElement(pack, type));
							}
						}else{
							String type = getTypeByFileName(pack);
							return generateDependency(getDependencyElement(pack, type));
						}
					}
				}
				log.warn("External class not found");
				throw new Exception("External class "+ oeType.getFullyQualifiedName()+ " not found");
			}else{
				log.warn("No orchestration classes found");
				throw new Exception("No orchestration classes found");
			}
		}else{
			log.warn("Orchestration element is null");
			throw new Exception("Orchestration element is null");
		}
	}
	
	public Dependency getParentImportedDependency(String pack,
			String importedPath) throws Exception {
		String str = pack.replace(importedPath, "");
		if (str.startsWith("/"))
			str = str.substring(1, str.indexOf(str, 1));
		else
			str = str.substring(0, str.indexOf(str, 1));
		log.debug("looking for "+ str);
		return generateDependency(getImportedDependencyWith(str));
		
	}

	private Element getImportedDependencyWith(String str) throws Exception{
		NodeList nl = projectElement.getElementsByTagName(DEPENDENCY_TAG);
		if (nl != null && nl.getLength() > 0) {
			for (int i = 0; i < nl.getLength(); i++) {
				Element dependency = (Element) nl.item(i);
				String type = dependency.getAttribute(TYPE_ATTR);
				if (dependency.getAttribute(LOCATION_ATTR).contains(str)
						&& dependency.getAttribute(IMPORTED_ATTR).equalsIgnoreCase("true")
						&&(type.equals(WAR_DEP_TYPE)||type.equals(JAR_DEP_TYPE)||type.equals(ZIP_DEP_TYPE))){
					return dependency;
				}
			}
			throw new Exception("No imported dependency found with string " +str);
		}else
			throw new Exception("No dependecies found");
	}
	
	public static String getTypeByFileName(String pack) {
		File f = new File(pack);
		if (f.isDirectory())
			return ProjectMetadata.CLASS_FOLDER_DEP_TYPE;
		else
			if (f.getName().endsWith(".jar")){
				return ProjectMetadata.JAR_DEP_TYPE;
			}else if (f.getName().endsWith(".war")){
				return ProjectMetadata.WAR_DEP_TYPE;
			}else if (f.getName().endsWith(".zip")){
				return ProjectMetadata.ZIP_DEP_TYPE;
			}else
				return ProjectMetadata.FILE_DEP_TYPE;
	}

	/** Get classes which contains Orchestration Elements but not from external packages
	 * @return Sting with the FQDN of the orchestration classes
	 */
	public String[] getNonExternalOrchestrationClasses() {
		NodeList orch_classes = projectElement
				.getElementsByTagName(ORCH_CLASS_TAG);
		if (orch_classes != null) {
			ArrayList<String> classes = new ArrayList<String>();
			for (int i = 0; i < orch_classes.getLength(); i++) {
				Element oeClass =(Element) (orch_classes.item(i));
				if (!oeClass.getAttribute(TYPE_ATTR).equals(TitlesAndConstants.EXTERNAL_CLASS))
					classes.add(oeClass.getAttribute(CLASS_ATTR));
			}
			return classes.toArray(new String[classes.size()]);
		} else {
			log.warn("No orchestration classes found");
			return new String[0];
		}
	}
	
	/** Get classes which contains Orchestration Elements
	 * @return Sting with the FQDN of the orchestration classes
	 */
	public Map<String,String> getOrchestrationClassesTypes() {
		HashMap<String, String> map = new HashMap<String, String>();
		NodeList orch_classes = projectElement
				.getElementsByTagName(ORCH_CLASS_TAG);
		if (orch_classes != null) {
			for (int i = 0; i < orch_classes.getLength(); i++) {
				String classFQDN = ((Element) (orch_classes.item(i)))
						.getAttribute(CLASS_ATTR);
				String type = ((Element) (orch_classes.item(i)))
						.getAttribute(TYPE_ATTR);
				if (type == null){
					type = TitlesAndConstants.getDefaultOrchestrationType();
					log.warn("No type found for class " + classFQDN + " setting to " + type);
				}
				map.put(classFQDN, type);
			}
			
		} else {
			log.warn("No orchestration classes found");
		}
		return map;
	}
	
	/** Get types of classes which contains Orchestration Elements
	 * @param Classes
	 * @return Sting with the FQDN of the orchestration classes
	 * @throws Exception 
	 */
	public Map<String,String> getOrchestrationClassesTypes(String[] classes) throws Exception {
		HashMap<String, String> map = new HashMap<String, String>();
		NodeList orch_classes = projectElement
				.getElementsByTagName(ORCH_CLASS_TAG);
		if (orch_classes != null) {
			for (String OEClass: classes){
				boolean found = false;
				for (int i = 0; i < orch_classes.getLength(); i++) {
					String classFQDN = ((Element) (orch_classes.item(i)))
						.getAttribute(CLASS_ATTR);
					if (classFQDN!= null && classFQDN.equals(OEClass)){	
						String type = ((Element) (orch_classes.item(i)))
								.getAttribute(TYPE_ATTR);
						if (type == null){
							type = TitlesAndConstants.getDefaultOrchestrationType();
							log.warn("No type found for class " + classFQDN + " setting to " + type);
						}
						map.put(classFQDN, type);
						found =true;
						break;
					}
				}
				if (!found)
					throw (new Exception("Class "+ OEClass + " not found"));
			}
			
		} else {
			log.warn("No orchestration classes found");
		}
		return map;
	}
	
	/** Check if Orchestration Element class comes from external package
	 * @return Sting with the FQDN of the orchestration class
	 */
	public boolean isExternalOrchestrationClass(String OEClass) {
		NodeList orch_classes = projectElement
				.getElementsByTagName(ORCH_CLASS_TAG);
		if (orch_classes != null) {
			for (int i = 0; i < orch_classes.getLength(); i++) {
				Element el = (Element) (orch_classes.item(i));
				if (OEClass.equals(el.getAttribute(CLASS_ATTR))){
					String res = el.getAttribute(TYPE_ATTR);
					if (res!=null && res.equals(TitlesAndConstants.EXTERNAL_CLASS)){
						return true;
					}else
						return false;
				}
			}
			return false;
		} else {
			log.warn("No orchestration classes found");
			return false;
		}
	}
	
	/** Check if Orchestration Element class comes from external package
	 * @return Sting with the FQDN of the orchestration class
	 * @throws Exception 
	 */
	public OrchestrationClass getOrchestrationClass(String OEClass) throws Exception {
		NodeList orch_classes = projectElement
				.getElementsByTagName(ORCH_CLASS_TAG);
		if (orch_classes != null) {
			for (int i = 0; i < orch_classes.getLength(); i++) {
				Element el = (Element) (orch_classes.item(i));
				String className = el.getAttribute(CLASS_ATTR);
				if (OEClass.equals(className)){
					String type = el.getAttribute(TYPE_ATTR);
					if (type!=null && type.equals(TitlesAndConstants.EXTERNAL_CLASS)){
						String extLoc = el.getAttribute(EXTERNAL_LOC_ATTR);
						String libLoc = el.getAttribute(LIBRARY_LOC_ATTR);
						return new OrchestrationClass(className, extLoc, libLoc, type);
					}else{
						return new OrchestrationClass(className, type);
					}
				}
			}
			throw(new Exception("Class "+ OEClass +" not found."));
		} else {
			log.warn("No orchestration classes found");
			throw(new Exception("No orchestration classes found."));
		}
	}
	
	/** Check if Orchestration Element class comes from external package
	 * @return Sting with the FQDN of the orchestration class
	 * @throws Exception 
	 */
	/*public String getLibraryLocationFromExternalOrchestrationClass(String OEClass) throws Exception {
		NodeList orch_classes = projectElement
				.getElementsByTagName(ORCH_CLASS_TAG);
		if (orch_classes != null) {
			for (int i = 0; i < orch_classes.getLength(); i++) {
				Element el = (Element) (orch_classes.item(i));
				if (OEClass.equals(el.getAttribute(CLASS_ATTR))){
					String res = el.getAttribute(TYPE_ATTR);
					if (res!=null && res.equals(TitlesAndConstants.EXTERNAL_CLASS)){
						return el.getAttribute(LIBRARY_LOC_ATTR);
					}else
						throw(new Exception("Class is not external."));
				}
			}
			throw(new Exception("Class "+ OEClass +" not found."));
		} else {
			log.warn("No orchestration classes found");
			throw(new Exception("No orchestration classes found."));
		}
	}*/
	
	/** Check if Orchestration Element class comes from external package
	 * @return Sting with the FQDN of the orchestration class
	 */
	public Map<String, OrchestrationElement> getOrchestrationElementFormExternalClass(IType orchClass) {
		
		String oeClass = orchClass.getFullyQualifiedName();
		NodeList orch_classes = projectElement
				.getElementsByTagName(ORCH_CLASS_TAG);
		if (orch_classes != null) {
			for (int i = 0; i < orch_classes.getLength(); i++) {
				Element oec = (Element) (orch_classes.item(i));
				if (oeClass.equals(oec.getAttribute(CLASS_ATTR))){
					HashMap<String, OrchestrationElement> els = new HashMap<String, OrchestrationElement>();
					NodeList elements = oec.getElementsByTagName(ELEMENT_TAG);
					if (elements !=null && elements.getLength()>0){
						
						for (int j=0; j < elements.getLength(); j++){
					
								Element oe = (Element)(elements.item(j));
								try{
									String label = oe.getAttribute(LABEL_ATTR);
									String return_type = oe.getAttribute(RETURN_TYPE_ATTR);	
									int modifier = Integer.parseInt(oe.getAttribute(MODIFIER_ATTR));
									OrchestrationElement orchElement = OrchestrationElement.extractElement(label, modifier, return_type, orchClass);
									orchElement.setConstraints(getConstraintsFromElement(oe));
									els.put(label, orchElement);
								}catch(Exception e){
									log.error("Error loading element ("+oe.toString()+").", e);
								}

						}
						return els;
					}else{
						log.warn("No elements for orchestration class " + oeClass);
						return new HashMap<String, OrchestrationElement>();
					}
				}
			}
			log.warn("No orchestration classes called " + oeClass);
			return new HashMap<String, OrchestrationElement>();
			
		} else {
			log.warn("No orchestration classes found");
			return new HashMap<String, OrchestrationElement>();
		}
	}
	
	public Element getOrchestrationElementFormExternalClass(String oeClass, String oeLabel) throws Exception {
		
		NodeList orch_classes = projectElement
				.getElementsByTagName(ORCH_CLASS_TAG);
		if (orch_classes != null) {
			for (int i = 0; i < orch_classes.getLength(); i++) {
				Element oec = (Element) (orch_classes.item(i));
				if (oeClass.equals(oec.getAttribute(CLASS_ATTR))){
					HashMap<String, OrchestrationElement> els = new HashMap<String, OrchestrationElement>();
					NodeList elements = oec.getElementsByTagName(ELEMENT_TAG);
					if (elements !=null && elements.getLength()>0){
						for (int j=0; j < elements.getLength(); j++){
							Element oe = (Element)(elements.item(j));
							if (oe.getAttribute(LABEL_ATTR).equals(oeLabel)){
								return oe;
							}
						}
					}
					throw new Exception("Element "+ oeLabel+ " not found.");
							
				}
			}
		}
		throw new Exception("Class "+ oeClass+ " not found.");							
	}								

	private Map<String, String> getConstraintsFromElement(Element oe) {
		Map<String,String> constraints = new HashMap<String,String>();
		NodeList consElements = oe.getElementsByTagName(CONSTRAINT_TAG);
		if (consElements != null) {
			for (int i = 0; i < consElements.getLength(); i++) {
				Element cons = (Element) (consElements.item(i));
				constraints.put(cons.getAttribute(NAME_ATTR), cons.getAttribute(VALUE_ATTR));
			}
		}
		return constraints;
	}
	
	private void modifyConstraintFromElement(Element oe, String constraintName, String constraintValue){
		NodeList consElements = oe.getElementsByTagName(CONSTRAINT_TAG);
		if (consElements != null) {
			for (int i = 0; i < consElements.getLength(); i++) {
				Element cons = (Element) (consElements.item(i));
				if (cons.getAttribute(NAME_ATTR).equals(constraintName)){
					log.debug("Constraint " + constraintName + " found. Setting to "+ constraintValue);
					cons.setAttribute(VALUE_ATTR, constraintValue);
					return;
				}
			}
		}
		log.debug("Constraint " + constraintName + " not found. Adding...");
		Element con = pr_doc.createElement(CONSTRAINT_TAG);
		con.setAttribute(NAME_ATTR, constraintName);
		con.setAttribute(VALUE_ATTR, constraintValue);
		oe.appendChild(con);
			
	}
	
	private void deleteConstraintFromElement(Element oe, String constraintName){
		NodeList consElements = oe.getElementsByTagName(CONSTRAINT_TAG);
		if (consElements != null) {
			for (int i = 0; i < consElements.getLength(); i++) {
				Element cons = (Element) (consElements.item(i));
				if (cons.getAttribute(NAME_ATTR).equals(constraintName)){
					log.debug("Constraint " + constraintName + " found. Removing...");
					oe.removeChild(cons);
					return;
				}
			}
		}
		log.warn("Constraint " + constraintName + " not found. Nothing done");
		
			
	}

	/** Add Orchestration Element to an external Orchestration Class
	 * @param OEClass External Orchestration Class
	 * @param oe Orchestration Element to be add 
	 * @throws Exception 
	 */
	public void addOEtoOrchestrationClass(String OEClass, 
			OrchestrationElement oe) throws Exception {
		NodeList orch_classes = projectElement
				.getElementsByTagName(ORCH_CLASS_TAG);
		if (orch_classes != null) {
			for (int i = 0; i < orch_classes.getLength(); i++) {
				Element oec = (Element) (orch_classes.item(i));
				if (OEClass.equals(oec.getAttribute(CLASS_ATTR))){
					Element el = pr_doc.createElement(ELEMENT_TAG);
					el.setAttribute(LABEL_ATTR, oe.getLabel());
					el.setAttribute(RETURN_TYPE_ATTR, oe.getReturnType());
					el.setAttribute(MODIFIER_ATTR, Integer.toString(
							oe.getMethodModifier()));
					Map<String, String> cons = oe.getConstraints();
					for (String constraint:cons.keySet()){
						Element con = pr_doc.createElement(CONSTRAINT_TAG);
						con.setAttribute(NAME_ATTR, constraint);
						con.setAttribute(VALUE_ATTR, cons.get(constraint));
						el.appendChild(con);
					}
					oec.appendChild(el);
					return;
				}
			}
			log.warn("No orchestration classes called " + OEClass);
			throw(new Exception("No orchestration classes called " + OEClass));
		} else {
			log.warn("No orchestration classes found");
			throw(new Exception("No orchestration classes found"));
		}
	}
	
	/**Add a package 
	 * @param packageName Package name
	 * @param type Package type
	 */
	public void addPackage(String packageName, String type) {
		Element dep = pr_doc.createElement(PACKAGE_TAG);
		dep.setAttribute(NAME_ATTR, packageName);
		dep.setAttribute(TYPE_ATTR, type);
		projectElement.appendChild(dep);
	}

	/** Add an element to package
	 * @param name Package name
	 * @param elementLabel Label of the service element
	 */
	public void addElementToPackage(String name, String elementLabel) {
		NodeList nl = projectElement.getElementsByTagName(PACKAGE_TAG);
		if (nl != null && nl.getLength() > 0) {
			for (int i = 0; i < nl.getLength(); i++) {
				Element dependency = (Element) nl.item(i);
				if (dependency.getAttribute(NAME_ATTR).equals(name)) {
					Element el = pr_doc.createElement(ELEMENT_TAG);
					el.setAttribute(LABEL_ATTR, elementLabel);
					dependency.appendChild(el);
					return;
				}
			}
		}
		Element dep = pr_doc.createElement(PACKAGE_TAG);
		dep.setAttribute(NAME_ATTR, name);
		projectElement.appendChild(dep);
		Element el = pr_doc.createElement(ELEMENT_TAG);
		el.setAttribute(LABEL_ATTR, elementLabel);
		dep.appendChild(el);
	}

	/** Remove a elements package
	 * @param name Package name
	 */
	public void removePackage(String name) {
		NodeList nl = projectElement.getElementsByTagName(PACKAGE_TAG);
		if (nl != null && nl.getLength() > 0) {
			for (int i = 0; i < nl.getLength(); i++) {
				Element dependency = (Element) nl.item(i);
				if (dependency.getAttribute(NAME_ATTR).equals(name)) {
					NodeList childs = dependency.getChildNodes();
					if (childs != null && childs.getLength() > 0) {
						for (int j = 0; j < childs.getLength(); j++) {
							dependency.removeChild(childs.item(j));
						}
					}
					projectElement.removeChild(dependency);
				}
			}
		}
	}

	/** Remove element from a elements package
	 * @param name Package name
	 * @param elementLabel Label of a service element
	 */
	public void removeElementFromPackage(String name, String elementLabel) {
		NodeList nl = projectElement.getElementsByTagName(PACKAGE_TAG);
		if (nl != null && nl.getLength() > 0) {
			for (int i = 0; i < nl.getLength(); i++) {
				Element dependency = (Element) nl.item(i);
				if (dependency.getAttribute(NAME_ATTR).equals(name)) {
					NodeList childs = dependency
							.getElementsByTagName(ELEMENT_TAG);
					if (childs != null && childs.getLength() > 0) {
						for (int j = 0; j < childs.getLength(); j++) {
							Element el = (Element) childs.item(j);
							if (el.getAttribute(LABEL_ATTR)
									.equals(elementLabel)) {
								dependency.removeChild(childs.item(j));
								return;
							}
						}
					}
				}
			}
		}
	}

	/**Remove element from all the elements packages
	 * @param elementLabel Label of a service element
	 */
	public void removeElementFromPackages(String elementLabel) {
		NodeList nl = projectElement.getElementsByTagName(PACKAGE_TAG);
		if (nl != null && nl.getLength() > 0) {
			for (int i = 0; i < nl.getLength(); i++) {
				Element dependency = (Element) nl.item(i);
				NodeList childs = dependency.getElementsByTagName(ELEMENT_TAG);
				if (childs != null && childs.getLength() > 0) {
					for (int j = 0; j < childs.getLength(); j++) {
						Element el = (Element) childs.item(j);
						if (el.getAttribute(LABEL_ATTR).equals(elementLabel)) {
							dependency.removeChild(childs.item(j));
							return;
						}
					}
				}

			}
		}
	}

	/** Get a dependency
	 * @param loc Dependency location (file or folder location)
	 * @param type Dependency type
	 * @return XML of the dependency
	 */
	private boolean isImportedDependency(String loc, String type) {
		NodeList nl = projectElement.getElementsByTagName(DEPENDENCY_TAG);
		if (nl != null && nl.getLength() > 0) {
			for (int i = 0; i < nl.getLength(); i++) {
				Element dependency = (Element) nl.item(i);
				if (dependency.getAttribute(LOCATION_ATTR).equals(loc)
						&& dependency.getAttribute(TYPE_ATTR).equalsIgnoreCase(
								type)) {
					if (dependency.getAttribute(IMPORTED_ATTR)!= null && dependency.getAttribute(IMPORTED_ATTR).equalsIgnoreCase("true"))
						return true;
					else
						return false;
				}
			}
		}
		return false;
	}
	
	/** Get a dependency
	 * @param loc Dependency location (file or folder location)
	 * @param type Dependency type
	 * @return XML of the dependency
	 */
	private Element getDependencyElement(String loc, String type) {
		NodeList nl = projectElement.getElementsByTagName(DEPENDENCY_TAG);
		if (nl != null && nl.getLength() > 0) {
			for (int i = 0; i < nl.getLength(); i++) {
				Element dependency = (Element) nl.item(i);
				if (dependency.getAttribute(LOCATION_ATTR).equals(loc)
						&& dependency.getAttribute(TYPE_ATTR).equalsIgnoreCase(
								type)) {
					return dependency;
				}
			}
		}
		return null;
	}

	/**Add a dependency
	 * @param dependencyLocation Dependency location (file or folder location)
	 * @param type Dependency type
	 */
	public void addDependency(String dependencyLocation, String type) {
		Element dep = getDependencyElement(dependencyLocation, type);
		if (dep == null) {
			dep = pr_doc.createElement(DEPENDENCY_TAG);
			dep.setAttribute(LOCATION_ATTR, dependencyLocation);
			dep.setAttribute(TYPE_ATTR, type);
			dep.setAttribute(OTHER_INFO_ATTR, "");
			dep.setAttribute(IMPORTED_ATTR, Boolean.toString(false));
			projectElement.appendChild(dep);
		}
	}
	/**Add a dependency
	 * @param dependencyLocation Dependency location (file or folder location)
	 * @param type Dependency type
	 */
	public void addDependency(String dependencyLocation, String type, boolean imported) {
		Element dep = getDependencyElement(dependencyLocation, type);
		if (dep == null) {
			dep = pr_doc.createElement(DEPENDENCY_TAG);
			dep.setAttribute(LOCATION_ATTR, dependencyLocation);
			dep.setAttribute(TYPE_ATTR, type);
			dep.setAttribute(OTHER_INFO_ATTR, "");
			dep.setAttribute(IMPORTED_ATTR, Boolean.toString(imported));
			projectElement.appendChild(dep);
		}
	}

	/**Add a dependency
	 * @param dependencyLocation Dependency location (file or folder location)
	 * @param type Dependency type
	 */
	public void addDependency(String dependencyLocation, String type, String otherInfo) {
		Element dep = getDependencyElement(dependencyLocation, type);
		if (dep == null) {
			dep = pr_doc.createElement(DEPENDENCY_TAG);
			dep.setAttribute(LOCATION_ATTR, dependencyLocation);
			dep.setAttribute(TYPE_ATTR, type);
			dep.setAttribute(OTHER_INFO_ATTR, otherInfo);
			dep.setAttribute(IMPORTED_ATTR, Boolean.toString(false));
			projectElement.appendChild(dep);
		}
	}
	/** Add a service element to a dependency
	 * @param loc Dependency location (file or folder location)
	 * @param type Dependency type
	 * @param elementLabel Label of a service element
	 */
	public void addElementToDependency(String loc, String type,
			String elementLabel) {
		Element dep = getDependencyElement(loc, type);
		if (dep != null) {
			if (!hasElement(dep, elementLabel)) {
				Element el = pr_doc.createElement(ELEMENT_TAG);
				el.setAttribute(LABEL_ATTR, elementLabel);
				dep.appendChild(el);
			}
		} else {
			dep = pr_doc.createElement(DEPENDENCY_TAG);
			dep.setAttribute(LOCATION_ATTR, loc);
			dep.setAttribute(TYPE_ATTR, type);
			dep.setAttribute(OTHER_INFO_ATTR, "");
			projectElement.appendChild(dep);
			Element el = pr_doc.createElement(ELEMENT_TAG);
			el.setAttribute(LABEL_ATTR, elementLabel);
			dep.appendChild(el);
		}
	}

	/** Check if an element has a dependency
	 * @param dep Dependency location (file or folder location)
	 * @param elementLabel Label of a service element
	 * @return True if element exists, otherwise false
	 */
	private boolean hasElement(Element dep, String elementLabel) {
		NodeList childs = dep.getElementsByTagName(ELEMENT_TAG);
		if (childs != null && childs.getLength() > 0) {
			for (int j = 0; j < childs.getLength(); j++) {
				Element el = (Element) childs.item(j);
				if (el.getAttribute(LABEL_ATTR).equals(elementLabel)) {
					return true;
				}
			}
		}
		return false;
	}

	/** Remove a dependency from the project
	 * @param loc Dependency location (file or folder location)
	 */
	public void removeDependency(String loc) {
		NodeList nl = projectElement.getElementsByTagName(DEPENDENCY_TAG);
		if (nl != null && nl.getLength() > 0) {
			for (int i = 0; i < nl.getLength(); i++) {
				Element dependency = (Element) nl.item(i);
				if (dependency.getAttribute(LOCATION_ATTR).equals(loc)) {
					NodeList childs = dependency
							.getElementsByTagName(ELEMENT_TAG);
					if (childs != null && childs.getLength() > 0) {
						for (int j = 0; j < childs.getLength(); j++) {
							dependency.removeChild(childs.item(j));
						}
					}
					projectElement.removeChild(dependency);
				}
			}
		}
	}

	/**Remove an element form a the dependency
	 * @param loc Dependency location (file or folder location)
	 * @param elementLabel Label of a service element
	 */
	public void removeElementFromDependency(String loc, String elementLabel) {
		NodeList nl = projectElement.getElementsByTagName(DEPENDENCY_TAG);
		if (nl != null && nl.getLength() > 0) {
			for (int i = 0; i < nl.getLength(); i++) {
				Element dependency = (Element) nl.item(i);
				if (dependency.getAttribute(LOCATION_ATTR).equals(loc)) {
					NodeList childs = dependency
							.getElementsByTagName(ELEMENT_TAG);
					if (childs != null && childs.getLength() > 0) {
						for (int j = 0; j < childs.getLength(); j++) {
							Element el = (Element) childs.item(j);
							if (el.getAttribute(LABEL_ATTR)
									.equals(elementLabel)) {
								dependency.removeChild(childs.item(j));
								return;
							}
						}
					}
				}
			}
		}
	}

	/**Remove an element form all the dependencies of the project
	 * @param elementLabel Label of a service element
	 */
	public void removeElementFromDepencies(String elementLabel) {
		NodeList nl = projectElement.getElementsByTagName(DEPENDENCY_TAG);
		if (nl != null && nl.getLength() > 0) {
			for (int i = 0; i < nl.getLength(); i++) {
				Element dependency = (Element) nl.item(i);
				NodeList childs = dependency.getElementsByTagName(ELEMENT_TAG);
				if (childs != null && childs.getLength() > 0) {
					for (int j = 0; j < childs.getLength(); j++) {
						Element el = (Element) childs.item(j);
						if (el.getAttribute(LABEL_ATTR).equals(elementLabel)) {
							dependency.removeChild(childs.item(j));
							return;
						}
					}
				}

			}
		}
	}

	/** Write the XML serialization the project metadata info in a file
	 * @param file Output file
	 * @throws TransformerException
	 * @throws IOException
	 */
	public void toFile(File file) throws TransformerException, IOException {
		String xmlString = this.getString();
		byte[] buf = xmlString.getBytes();
		OutputStream f0 = new FileOutputStream(file);
		for (int i = 0; i < buf.length; i++) {
			f0.write(buf[i]);
		}
		f0.close();
	}

	/** Get the XML serialization the project metadata info in a String
	 * @return XML serialization as String
	 * @throws TransformerException
	 */
	public String getString() throws TransformerException {
		// setting up a transformer
		TransformerFactory transfac = TransformerFactory.newInstance();
		Transformer trans = transfac.newTransformer();
		trans.setOutputProperty("{http://xml.apache.org/xslt}indent-amount",
				"2");
		trans.setOutputProperty(OutputKeys.INDENT, "yes");
		// generating XML from tree
		StringWriter sw = new StringWriter();
		StreamResult result = new StreamResult(sw);
		DOMSource source = new DOMSource(pr_doc);
		trans.transform(source, result);
		return sw.toString();

	}

	/**Set the default java package name for orchestration elements 
	 * classes and core element interfaces
	 * @param packageName Java package name
	 */
	public void setMainPackageName(String packageName) {
		projectElement.setAttribute(MAIN_PACKAGE_ATTR, packageName);

	}

	/** Get the default java package name for orchestration elements 
	 * classes and core element interfaces
	 * @return Java package name
	 */
	public String getMainPackageName() {
		return projectElement.getAttribute(MAIN_PACKAGE_ATTR);

	}

	/** Get the default java package name of core elements classes created from scratch
	 * @return Java package name
	 */
	public String getCoreElementsPackageName() {
		return new String(projectElement.getAttribute(MAIN_PACKAGE_ATTR)
				+ ".coreelements");

	}

	/** Get all the dependencies of the project
	 * @return Array of dependencies
	 * @throws Exception
	 */
	public Dependency[] getDependencies() throws Exception {
		NodeList nl = projectElement.getElementsByTagName(DEPENDENCY_TAG);
		if (nl != null && nl.getLength() > 0) {
			Dependency[] dependencies = new Dependency[nl.getLength()];
			for (int i = 0; i < nl.getLength(); i++) {
				Element dependency = (Element) nl.item(i);
				
				dependencies[i] = generateDependency(dependency);
			}
			return dependencies;
		} else {
			return new Dependency[0];
		}
	}
	
	private Dependency generateDependency(Element dependency) throws Exception{
		if (dependency != null){
			String otherInfo = dependency.getAttribute(OTHER_INFO_ATTR);
			if (otherInfo==null)
				otherInfo = "";
			boolean imp = false;
			String imported = dependency.getAttribute(IMPORTED_ATTR);
			if (imported!=null && imported.equalsIgnoreCase("true"))
				imp = true;
			return new Dependency(dependency.getAttribute(LOCATION_ATTR),
					dependency.getAttribute(TYPE_ATTR),	otherInfo,imp);
		}else
			throw new Exception("Element is null");
	}
	

	/** Check if a dependency exists
	 * @param dir Dependency location ( file or folder location)
	 * @param type Dependency type
	 * @return True if exists, false if not
	 */
	public boolean existsDependency(String dir, String type) {
		NodeList nl = projectElement.getElementsByTagName(DEPENDENCY_TAG);
		if (nl != null && nl.getLength() > 0) {
			for (int i = 0; i < nl.getLength(); i++) {
				Element dependency = (Element) nl.item(i);
				if (dependency.getAttribute(LOCATION_ATTR).equals(dir)
						&& dependency.getAttribute(TYPE_ATTR).equals(type))
					return true;
			}
		}
		return false;
	}

	/** Get all the elements packages created for the project
	 * @return Array of package names
	 */
	public String[] getPackages() {
		NodeList nl = projectElement.getElementsByTagName(PACKAGE_TAG);
		if (nl != null && nl.getLength() > 0) {
			String[] packages = new String[nl.getLength()];
			for (int i = 0; i < nl.getLength(); i++) {
				Element pack = (Element) nl.item(i);
				packages[i] = pack.getAttribute(NAME_ATTR);
			}
			return packages;
		} else {
			return new String[0];
		}
	}
	
	/** Get all packages created for the project which contains core elements
	 * @return Array of package names
	 */
	public String[] getPackagesWithCores() {
		NodeList nl = projectElement.getElementsByTagName(PACKAGE_TAG);
		if (nl != null && nl.getLength() > 0) {
			ArrayList<String> packages = new ArrayList<String>();
			for (int i = 0; i < nl.getLength(); i++) {
				Element pack = (Element) nl.item(i);
				if (pack.getAttribute(TYPE_ATTR).equalsIgnoreCase(METH_CORE_PACK_TYPE)||
						pack.getAttribute(TYPE_ATTR).equalsIgnoreCase(SER_CORE_PACK_TYPE))
					packages.add(pack.getAttribute(NAME_ATTR));
			}
			return packages.toArray(new String[packages.size()]);
		} else {
			return new String[0];
		}
	}
	
	/** Get all packages created for the project which contain orchestration elements
	 * @return Array of package names
	 */
	public String[] getPackagesWithOrchestration() {
		NodeList nl = projectElement.getElementsByTagName(PACKAGE_TAG);
		if (nl != null && nl.getLength() > 0) {
			ArrayList<String> packages = new ArrayList<String>();
			for (int i = 0; i < nl.getLength(); i++) {
				Element pack = (Element) nl.item(i);
				if (pack.getAttribute(TYPE_ATTR).equalsIgnoreCase(ORCH_PACK_TYPE))
					packages.add(pack.getAttribute(NAME_ATTR));
			}
			return packages.toArray(new String[packages.size()]);
		} else {
			return new String[0];
		}
	}

	/** Check if a package already exists
	 * @param selectedpack Package name
	 * @return True if exists, false if not
	 */
	public boolean existsPackage(String selectedpack) {
		NodeList nl = projectElement.getElementsByTagName(PACKAGE_TAG);
		if (nl != null && nl.getLength() > 0) {
			for (int i = 0; i < nl.getLength(); i++) {
				Element dependency = (Element) nl.item(i);
				if (dependency.getAttribute(NAME_ATTR).equals(selectedpack))
					return true;
			}
		}
		return false;
	}

	/** Get service elements in an elements package
	 * @param pName Package name
	 * @return Array of element labels
	 */
	public String[] getElementsInPackage(String pName) {
		NodeList nl = projectElement.getElementsByTagName(PACKAGE_TAG);
		if (nl != null && nl.getLength() > 0) {
			for (int i = 0; i < nl.getLength(); i++) {
				Element dependency = (Element) nl.item(i);
				if (dependency.getAttribute(NAME_ATTR).equals(pName)) {
					NodeList els = dependency.getElementsByTagName(ELEMENT_TAG);
					if (els != null && els.getLength() > 0) {
						String[] dependencies = new String[els.getLength()];
						for (int j = 0; j < els.getLength(); j++) {
							Element el = (Element) els.item(j);
							dependencies[j] = el.getAttribute(LABEL_ATTR);
						}
						return dependencies;
					} else {
						return new String[0];
					}
				}

			}
		}
		return new String[0];

	}

	/** Get the type of a elements package
	 * @param selectedpack Package name
	 * @return Package type
	 */
	public String getPackageType(String selectedpack) {
		NodeList nl = projectElement.getElementsByTagName(PACKAGE_TAG);
		if (nl != null && nl.getLength() > 0) {
			for (int i = 0; i < nl.getLength(); i++) {
				Element dependency = (Element) nl.item(i);
				if (dependency.getAttribute(NAME_ATTR).equals(selectedpack)) {
					return dependency.getAttribute(TYPE_ATTR);
				}
			}
		}
		return null;
	}

	/** Set the type of an element package
	 * @param selectedpack Package name
	 * @param type Package type
	 */
	public void setPackageType(String selectedpack, String type) {
		NodeList nl = projectElement.getElementsByTagName(PACKAGE_TAG);
		if (nl != null && nl.getLength() > 0) {
			for (int i = 0; i < nl.getLength(); i++) {
				Element dependency = (Element) nl.item(i);
				if (dependency.getAttribute(NAME_ATTR).equals(selectedpack)) {
					dependency.setAttribute(TYPE_ATTR, type);
				}
			}
		}

	}

	/** Get dependencies of a set of  service elements
	 * @param elements Array of service elements
	 * @return Array of dependencies
	 */
	public List<Dependency> getDependencies(String[] elements) {

		ArrayList<Dependency> deps = new ArrayList<Dependency>();
		NodeList nl = projectElement.getElementsByTagName(DEPENDENCY_TAG);
		if (nl != null && nl.getLength() > 0) {
			for (int i = 0; i < nl.getLength(); i++) {
				Element dependency = (Element) nl.item(i);
				NodeList childs = dependency.getElementsByTagName(ELEMENT_TAG);
				if (childs != null && childs.getLength() > 0) {
					Boolean found = false;
					for (int j = 0; j < childs.getLength(); j++) {
						Element el = (Element) childs.item(j);
						for (String elementLabel : elements) {
							/*log.debug("Looking for element \""
									+ elementLabel + "\" for "
									+ dependency.getAttribute(LOCATION_ATTR)+
									" element \""+ el.getAttribute(LABEL_ATTR)+"\"");*/
							if (el.getAttribute(LABEL_ATTR).equalsIgnoreCase(
									elementLabel)) {
								found = true;
								String otherInfo = dependency.getAttribute(OTHER_INFO_ATTR);
								if (otherInfo==null)
									otherInfo = "";
								boolean imp = false;
								String imported = dependency.getAttribute(IMPORTED_ATTR);
								if (imported!=null && imported.equalsIgnoreCase("true"))
									imp = true;
								deps.add(new Dependency(dependency
										.getAttribute(LOCATION_ATTR),
										dependency.getAttribute(TYPE_ATTR), otherInfo, imp));
								//log.debug("Element found");
								break;
							}
						}
						if (found) {
							break;
						}
					}
				}

			}
		}
		return deps;
	}
	
	/** Get dependencies of a set of  service elements
	 * @param elements Array of service elements
	 * @return Array of dependencies
	 */
	public List<Dependency> getDependencies(Map<String,List<String>> elements) {
		List<Dependency> deps = new ArrayList<Dependency>();
		for (Entry<String,List<String>> e:elements.entrySet()){
			deps.addAll(getDependencies(e.getValue().toArray(new String[e.getValue().size()])));
		}
		return deps;
		
	}

	/** Get the main package folder
	 * @return main package folder
	 */
	public String getMainPackageFolder() {
		String str = getMainPackageName();
		log.debug("main package name: " + str);
		String new_str = str.replaceAll("\\.", File.separator);
		log.debug("main package name: " + new_str);
		return (getSourceDir() + File.separator + new_str);
	}

	/** Add image to the project metadata
	 * @param id Image identifier
	 * @param url URL where the image is stored
	 * @param pack Name of the package installed in the image
	 */
	public void addImage(String id, String url, String pack) {
		Element dep = pr_doc.createElement(IMAGE_TAG);
		dep.setAttribute(ID_ATTR, id);
		dep.setAttribute(URL_ATTR, url);
		dep.setAttribute(PACKAGE_ATTR, pack);
		projectElement.appendChild(dep);
	}

	/** Remove image
	 * @param id Image identifier
	 */
	public void removeImage(String id) {
		NodeList nl = projectElement.getElementsByTagName(IMAGE_TAG);
		if (nl != null && nl.getLength() > 0) {
			for (int i = 0; i < nl.getLength(); i++) {
				Element image = (Element) nl.item(i);
				if (image.getAttribute(ID_ATTR).equals(id)) {
					projectElement.removeChild(image);
				}
			}
		}
	}

	/** Get a map with the image URL and the package on each image 
	 * @return Map URL image - package name
	 */
	public Map<String, String> getImageURLPackagesMap() {
		HashMap<String, String> map = new HashMap<String, String>();
		NodeList nl = projectElement.getElementsByTagName(IMAGE_TAG);
		if (nl != null && nl.getLength() > 0) {
			for (int i = 0; i < nl.getLength(); i++) {
				Element image = (Element) nl.item(i);
				map.put(image.getAttribute(PACKAGE_ATTR),
						image.getAttribute(URL_ATTR));
			}
		}
		return map;
	}

	/** Get all the image identifiers
	 * @return Array of image IDs
	 */
	public String[] getAllImageIDs() {
		NodeList nl = projectElement.getElementsByTagName(IMAGE_TAG);

		if (nl != null && nl.getLength() > 0) {
			String[] ids = new String[nl.getLength()];
			for (int i = 0; i < nl.getLength(); i++) {
				Element image = (Element) nl.item(i);
				ids[i] = image.getAttribute(ID_ATTR);
			}
			return ids;
		} else
			return new String[0];
	}

	/** Add elasticity constraints for a service element
	 * @param elementLabel Service element label
	 * @param max maximum number of concurrent invocations recommended
	 * @param min minimum number of concurrent invocations required
	 */
	public void addElasticity(String elementLabel, int max, int min) {
		Element dep = pr_doc.createElement(ELASTICITY_TAG);
		dep.setAttribute(ELEMENT_ATTR, elementLabel);
		dep.setAttribute(MAX_ATTR, Integer.toString(max));
		dep.setAttribute(MIN_ATTR, Integer.toString(min));
		projectElement.appendChild(dep);
	}

	/**Get the minimum number of concurrent invocations required per service element
	 * @param elementLabel Label of the service element
	 * @return Minimum number of concurrent invocations required per service element
	 */
	public int getMinElasticity(String elementLabel) {
		NodeList nl = projectElement.getElementsByTagName(ELASTICITY_TAG);
		if (nl != null && nl.getLength() > 0) {
			for (int i = 0; i < nl.getLength(); i++) {
				Element elasticity = (Element) nl.item(i);
				if (elasticity.getAttribute(ELEMENT_TAG).equalsIgnoreCase(
						elementLabel)) {
					String s = elasticity.getAttribute(MIN_ATTR);
					if (s != null) {
						return Integer.parseInt(s);
					} else
						return DEFAULT_ELASTICITY_VALUE;
				}
			}

		}
		return DEFAULT_ELASTICITY_VALUE;
	}

	/**Set the minimum number of concurrent invocations required per service element
	 * @param elementLabel Label of the service element
	 * @param min Minimum number of concurrent invocations required per service element
	 */
	public void setMinElasticity(String elementLabel, int min) {
		NodeList nl = projectElement.getElementsByTagName(ELASTICITY_TAG);
		if (nl != null && nl.getLength() > 0) {
			for (int i = 0; i < nl.getLength(); i++) {
				Element elasticity = (Element) nl.item(i);
				if (elasticity.getAttribute(ELEMENT_TAG).equalsIgnoreCase(
						elementLabel)) {
					elasticity.setAttribute(MIN_ATTR, Integer.toString(min));
					return;
				}
			}
		}
		Element dep = pr_doc.createElement(ELASTICITY_TAG);
		dep.setAttribute(ELEMENT_ATTR, elementLabel);
		dep.setAttribute(MIN_ATTR, Integer.toString(min));
		projectElement.appendChild(dep);
	}

	/**Get the recommended maximum elasticity for a Service element
	 * @param elementLabel Label of the service element
	 * @return Recommended maximum number of concurrent element invocations
	 */
	public int getMaxElasticity(String elementLabel) {
		NodeList nl = projectElement.getElementsByTagName(ELASTICITY_TAG);
		if (nl != null && nl.getLength() > 0) {
			for (int i = 0; i < nl.getLength(); i++) {
				Element elasticity = (Element) nl.item(i);
				if (elasticity.getAttribute(ELEMENT_TAG).equalsIgnoreCase(
						elementLabel)) {
					String s = elasticity.getAttribute(MAX_ATTR);
					if (s != null) {
						return Integer.parseInt(s);
					} else
						return DEFAULT_ELASTICITY_VALUE;
				}
			}

		}
		return DEFAULT_ELASTICITY_VALUE;
	}

	/** Set the recommended maximum elasticity for a Service element
	 * @param elementLabel Label of the service element
	 * @param max Maximum number of concurrent element invocations
	 */
	public void setMaxElasticity(String elementLabel, int max) {
		NodeList nl = projectElement.getElementsByTagName(ELASTICITY_TAG);
		if (nl != null && nl.getLength() > 0) {
			for (int i = 0; i < nl.getLength(); i++) {
				Element elasticity = (Element) nl.item(i);
				if (elasticity.getAttribute(ELEMENT_TAG).equalsIgnoreCase(
						elementLabel)) {
					elasticity.setAttribute(MAX_ATTR, Integer.toString(max));
					return;
				}
			}
		}
		Element dep = pr_doc.createElement(ELASTICITY_TAG);
		dep.setAttribute(ELEMENT_ATTR, elementLabel);
		dep.setAttribute(MAX_ATTR, Integer.toString(max));
		projectElement.appendChild(dep);
	}

	/* NOT USED the same as minimum
	 * public int getInitialElasticity(String elementLabel) {
		NodeList nl = projectElement.getElementsByTagName(ELASTICITY_TAG);
		if (nl != null && nl.getLength() > 0) {
			for (int i = 0; i < nl.getLength(); i++) {
				Element elasticity = (Element) nl.item(i);
				if (elasticity.getAttribute(ELEMENT_TAG).equalsIgnoreCase(
						elementLabel)) {
					String s = elasticity.getAttribute(INI_ATTR);
					if (s != null) {
						return Integer.parseInt(s);
					} else
						return DEFAULT_ELASTICITY_VALUE;
				}
			}

		}
		return DEFAULT_ELASTICITY_VALUE;
	}

	public void setInitialElasticity(String elementLabel, int ini) {
		NodeList nl = projectElement.getElementsByTagName(ELASTICITY_TAG);
		if (nl != null && nl.getLength() > 0) {
			for (int i = 0; i < nl.getLength(); i++) {
				Element elasticity = (Element) nl.item(i);
				if (elasticity.getAttribute(ELEMENT_TAG).equalsIgnoreCase(
						elementLabel)) {
					elasticity.setAttribute(INI_ATTR, Integer.toString(ini));
					return;
				}
			}
		}
		Element dep = pr_doc.createElement(ELASTICITY_TAG);
		dep.setAttribute(ELEMENT_ATTR, elementLabel);
		dep.setAttribute(INI_ATTR, Integer.toString(ini));
		projectElement.appendChild(dep);
	}*/

	/**Get the required minimum elasticity for a set of elements
	 * @param elements Array of elements
	 * @return Map with minimum elasticity per element
	 */
	public Map<String, Integer> getMinElasticity(String[] elements) {
		HashMap<String, Integer> map = new HashMap<String, Integer>();
		for (String e : elements) {
			map.put(e, new Integer(getMinElasticity(e)));
		}
		return map;
	}

	/** Get the recommended maximum elasticity of a set of elements
	 * @param elements Array of elements
	 * @return Map with maximum elasticity per element
	 */
	public Map<String, Integer> getMaxElasticity(String[] elements) {
		HashMap<String, Integer> map = new HashMap<String, Integer>();
		for (String e : elements) {
			map.put(e, new Integer(getMaxElasticity(e)));
		}
		return map;
	}

	/** Remove all the images of the project */
	public void removeAllImages() {
		NodeList nl = projectElement.getElementsByTagName(IMAGE_TAG);
		if (nl != null && nl.getLength() > 0) {
			for (int i = 0; i < nl.getLength(); i++) {
				Element image = (Element) nl.item(i);
				projectElement.removeChild(image);
			}
		}
	}

	/**Get the resource file of the project.
	 * @param project
	 * @return
	 * @throws JavaModelException 
	 */
	public IFile getResourcesFile(IJavaProject project) throws JavaModelException {
		return project.getProject().getFile(getMainPackageFragment(project).getPath()
						.makeRelativeTo(project.getProject().getFullPath())
						.append(RESOURCES_FILENAME));
	}

	/**Get the resource file of the project.
	 * @param project
	 * @return
	 * @throws JavaModelException 
	 */
	public IFile getProjectFile(IJavaProject project) throws JavaModelException {
		return project.getProject().getFile(getMainPackageFragment(project).getPath()
				.makeRelativeTo(project.getProject().getFullPath())
				.append(PROJECT_FILENAME));
	}

	/** Remove all the packages of the project.
	 * 
	 */
	public void removeAllPackages() {
		String[] packs = getPackages();
		if (packs != null&& packs.length>0){
			for (String pack:packs){
				removePackage(pack);
			}
		}
	}
	
	/**Check if project metadata has defined maximum values for the hardware resource properties
	 * 
	 * @return True if defined;
	 */
	public boolean hasMaximumResourceProperties(){
		NodeList nl = projectElement.getElementsByTagName(MAX_RES_PROPERTIES_TAG);
		if (nl != null && nl.getLength()>0)
			return true;
		else
			return false;
	}
	
	public void setMaximumResourceProperties(int numCores, long memory, long disk){
		NodeList nl = projectElement.getElementsByTagName(MAX_RES_PROPERTIES_TAG);
		Element el;
		if (nl != null && nl.getLength()>0)
			el = (Element)nl.item(0);
		else{
			el = pr_doc.createElement(MAX_RES_PROPERTIES_TAG);
			projectElement.appendChild(el);
		}
		el.setAttribute(NUM_CORES_ATTR, Integer.toString(numCores));
		el.setAttribute(PHYS_MEM_ATTR, Long.toString(memory));
		el.setAttribute(LOCAL_DISK_ATTR, Long.toString(disk));
	}
	
	/** Get the maximum number of cores per hardware resource.
	 * @return the maximum of virtual cores
	 */
	public int getMaximumNumCores(){
		try{
			NodeList nl = projectElement.getElementsByTagName(MAX_RES_PROPERTIES_TAG);
			if (nl != null && nl.getLength()>0){
				String str = ((Element)nl.item(0)).getAttribute(NUM_CORES_ATTR);
				return Integer.parseInt(str);	
			}else{
				log.debug("Max properties for this project has not been specified");
				return -1;
			}
		}catch(Exception e){
			log.error("Error getting maximum number of machine cores");
			e.printStackTrace();
			return -1;
		}
	}
	
	/** Get the maximum memory available per hardware resource.
	 * @return the maximum of virtual cores
	 */
	public long getMaximumMemory(){
		try{
			NodeList nl = projectElement.getElementsByTagName(MAX_RES_PROPERTIES_TAG);
			if (nl != null && nl.getLength()>0){
				String str = ((Element)nl.item(0)).getAttribute(PHYS_MEM_ATTR);
				return Long.parseLong(str);	
			}else{
				log.debug("Max properties for this project has not been specified");
				return -1;
			}
		}catch(Exception e){
			log.error("Error getting maximum memory per machine");
			e.printStackTrace();
			return -1;
		}
	}
	
	/** Get the maximum local disk available per hardware resource.
	 * @return the maximum of virtual cores
	 */
	public long getMaximumLocalDisk(){
		try{
			NodeList nl = projectElement.getElementsByTagName(MAX_RES_PROPERTIES_TAG);
			if (nl != null && nl.getLength()>0){
				String str = ((Element)nl.item(0)).getAttribute(LOCAL_DISK_ATTR);
				return Long.parseLong(str);	
			}else{
				log.debug("Max properties for this project has not been specified");
				return -1;
			}
		}catch(Exception e){
			log.error("Error getting maximum local disk per machine");
			e.printStackTrace();
			return -1;
		}
	}

	public Map<String, String> getMaxResourcesProperties() {
		Map<String, String> maxResources = new HashMap<String,String>();
		if (hasMaximumResourceProperties()){
			maxResources.put(ConstraintsUtils.PROC_CPU_COUNT.getName(), Integer.toString(getMaximumNumCores()));
			maxResources.put(ConstraintsUtils.MEM_SIZE.getName(), Long.toString(getMaximumMemory()));
			maxResources.put(ConstraintsUtils.STORAGE_SIZE.getName(), Long.toString(getMaximumLocalDisk()));
		}else{
			try{
				IDEProperties prop = new IDEProperties(System.getenv("HOME")+"/.ide/config.properties");
				maxResources.put(ConstraintsUtils.PROC_CPU_COUNT.getName(), Integer.toString(prop.getMaxNumCores()));
				maxResources.put(ConstraintsUtils.MEM_SIZE.getName(), Long.toString(prop.getMaxMemory()));
				maxResources.put(ConstraintsUtils.STORAGE_SIZE.getName(), Long.toString(prop.getMaxDisk()));
			}catch(Exception e){
				maxResources.put(ConstraintsUtils.PROC_CPU_COUNT.getName(), Integer.toString(IDEProperties.DEFAULT_MAX_NUM_CORES));
				maxResources.put(ConstraintsUtils.MEM_SIZE.getName(), Long.toString(IDEProperties.DEFAULT_MAX_MEM));
				maxResources.put(ConstraintsUtils.STORAGE_SIZE.getName(), Long.toString(IDEProperties.DEFAULT_MAX_DISK));
			}
		}
		return maxResources;
	}
	
	/**Check if project metadata has defined maximum values for the hardware resource properties
	 * 
	 * @return True if defined;
	 */
	public boolean hasDefaultResourceProperties(){
		NodeList nl = projectElement.getElementsByTagName(DEFAULT_RES_PROPERTIES_TAG);
		if (nl != null && nl.getLength()>0)
			return true;
		else
			return false;
	}
	
	public void setDefaultResourceProperties(int numCores, long memory, long disk){
		NodeList nl = projectElement.getElementsByTagName(DEFAULT_RES_PROPERTIES_TAG);
		Element el;
		if (nl != null && nl.getLength()>0)
			el = (Element)nl.item(0);
		else{
			el = pr_doc.createElement(DEFAULT_RES_PROPERTIES_TAG);
			projectElement.appendChild(el);
		}
		el.setAttribute(NUM_CORES_ATTR, Integer.toString(numCores));
		el.setAttribute(PHYS_MEM_ATTR, Long.toString(memory));
		el.setAttribute(LOCAL_DISK_ATTR, Long.toString(disk));
	}
	
	/** Get the maximum number of cores per hardware resource.
	 * @return the maximum of virtual cores
	 */
	public int getDefaultNumCores(){
		try{
			NodeList nl = projectElement.getElementsByTagName(DEFAULT_RES_PROPERTIES_TAG);
			if (nl != null && nl.getLength()>0){
				String str = ((Element)nl.item(0)).getAttribute(NUM_CORES_ATTR);
				return Integer.parseInt(str);	
			}else{
				log.debug("Default properties for this project has not been specified");
				return -1;
			}
		}catch(Exception e){
			log.error("Error getting default number of machine cores");
			e.printStackTrace();
			return -1;
		}
	}
	
	/** Get the default memory per hardware resource when constraints are not defined.
	 * @return the maximum of virtual cores
	 */
	public long getDefaultMemory(){
		try{
			NodeList nl = projectElement.getElementsByTagName(DEFAULT_RES_PROPERTIES_TAG);
			if (nl != null && nl.getLength()>0){
				String str = ((Element)nl.item(0)).getAttribute(PHYS_MEM_ATTR);
				return Long.parseLong(str);	
			}else{
				log.debug("Default properties for this project has not been specified");
				return -1;
			}
		}catch(Exception e){
			log.error("Error getting default memory per machine");
			e.printStackTrace();
			return -1;
		}
	}
	
	/** Get the default local disk property when constraints are not defined.
	 * @return the maximum of virtual cores
	 */
	public long getDefaultLocalDisk(){
		try{
			NodeList nl = projectElement.getElementsByTagName(DEFAULT_RES_PROPERTIES_TAG);
			if (nl != null && nl.getLength()>0){
				String str = ((Element)nl.item(0)).getAttribute(LOCAL_DISK_ATTR);
				return Long.parseLong(str);	
			}else{
				log.debug("Default properties for this project has not been specified");
				return -1;
			}
		}catch(Exception e){
			log.error("Error getting default local disk per machine");
			e.printStackTrace();
			return -1;
		}
	}
	
	public Map<String, String> getDefaultResourcesProperties() {
		Map<String, String> defaultResources = new HashMap<String,String>();
		if (hasDefaultResourceProperties()){
			defaultResources.put(ConstraintsUtils.PROC_CPU_COUNT.getName(), Integer.toString(getDefaultNumCores()));
			defaultResources.put(ConstraintsUtils.MEM_SIZE.getName(), Long.toString(getDefaultMemory()));
			defaultResources.put(ConstraintsUtils.STORAGE_SIZE.getName(), Long.toString(getDefaultLocalDisk()));
		}else{
			try{
				IDEProperties prop = new IDEProperties(System.getenv("HOME")+"/.ide/config.properties");
				defaultResources.put(ConstraintsUtils.PROC_CPU_COUNT.getName(), Integer.toString(prop.getDefaultNumCores()));
				defaultResources.put(ConstraintsUtils.MEM_SIZE.getName(), Long.toString(prop.getDefaultMemory()));
				defaultResources.put(ConstraintsUtils.STORAGE_SIZE.getName(), Long.toString(prop.getDefaultDisk()));
			}catch(Exception e){
				defaultResources.put(ConstraintsUtils.PROC_CPU_COUNT.getName(), Integer.toString(IDEProperties.DEFAULT_NUM_CORES));
				defaultResources.put(ConstraintsUtils.MEM_SIZE.getName(), Long.toString(IDEProperties.DEFAULT_MEM));
				defaultResources.put(ConstraintsUtils.STORAGE_SIZE.getName(), Long.toString(IDEProperties.DEFAULT_DISK));
			}
		}
		return defaultResources;
	}
	
	/** 
	 * @param pr_meta
	 * @param orchestrationClasses
	 * @return
	 */
	public static boolean shouldBeWarFile(Map<String,String> orchestrationClasses) {
		//check if they contains service interfaces
		for (String type:orchestrationClasses.values()){
			//log.debug("Class type "+ type);
			if (type.equalsIgnoreCase(TitlesAndConstants.WS_CLASS))
				return true; 
		}
		return false;
	}
	
	/** 
	 * @param pr_meta
	 * @param orchestrationClasses
	 * @return
	 * @throws Exception 
	 */
	public boolean shouldBeWarFile(String p) throws Exception {
		String[] elements = this.getElementsInPackage(p);
		String[] orchClasses = OrchestrationElement.getClassesFromLabels(elements);
		
		Map<String, String> orchestrationClasses = this.getOrchestrationClassesTypes(orchClasses);
		//check if they contains service interfaces
		for (String type:orchestrationClasses.values()){
			//log.debug("Class type "+ type);
			if (type.equalsIgnoreCase(TitlesAndConstants.WS_CLASS))
				return true; 
		}
		return false;
	}

	public void modifyConstraint(String serviceClass, String oeLabel,
			String constraintName, String constraintValue) throws Exception {
		Element oe = getOrchestrationElementFormExternalClass(serviceClass, oeLabel);
		modifyConstraintFromElement(oe, constraintName, constraintValue);
		
	}

	public void deleteConstraint(String serviceClass, String oeLabel,
			String constraintName) throws Exception {
		Element oe = getOrchestrationElementFormExternalClass(serviceClass, oeLabel);
		deleteConstraintFromElement(oe, constraintName);
	}

	public Map<String, List<String>> getOrchestrationClassesAndElements(
			String[] allElements, boolean b) {
		Map<String, List<String>> map = new HashMap<String, List<String>>();
		for(String el:allElements){
			try{
				String cl = OrchestrationElement.getClassFromLabel(el);
				if (isExternalOrchestrationClass(cl)==b){
					if (map.containsKey(cl)){
						List<String> elements = map.get(cl);
						elements.add(el);
					}else{
						List<String> elements = new LinkedList<String>();
						elements.add(el);
						map.put(cl, elements);
					}
				}
			}catch(Exception e){
				log.error("Error getting element class", e);
			}
		}	
		return map;
	}
}
