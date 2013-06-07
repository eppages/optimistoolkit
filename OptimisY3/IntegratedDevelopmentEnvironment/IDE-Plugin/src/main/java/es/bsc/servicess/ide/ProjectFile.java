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

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/** Class for managing the creation of the project.xml required for running an application with
 *  the programming model runtime.
 * @author Jorge Ejarque (Barcelona Supercomputing Center)
 *
 */
public class ProjectFile {
	private static final String PROJECT_TAG = "Project";
	private static final String WORKER_TAG = "Worker";
	private static final String NAME_ATTR = "Name";
	private static final String NAME_MIN_ATTR = "name";
	private static final String INSTALLDIR_TAG = "InstallDir";
	private static final String WORKINGDIR_TAG = "WorkingDir";
	private static final String USER_TAG = "User";
	private static final String LIMIT_TASKS_TAG = "LimitOfTasks";
	private static final String CLOUD_TAG = "Cloud";
	private static final String PROVIDER_TAG = "Provider";
	private static final String IMAGE_LIST_TAG = "ImageList";
	private static final String IMAGE_TAG = "Image";
	
	private static Logger log = Logger.getLogger(ProjectFile.class);
	
	private Document doc;
	private Element project;

	/** Constructor
	 * @param file Existing project.xml file
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 */
	public ProjectFile(File file) throws ParserConfigurationException,
			SAXException, IOException {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = factory.newDocumentBuilder();
		doc = docBuilder.parse(file);
		this.project = doc.getDocumentElement();
		if (doc == null || project == null) {
			throw new IOException("Project Element is null");
		}
	}

	/** Default constructor
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 */
	public ProjectFile() throws ParserConfigurationException, SAXException,
			IOException {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = factory.newDocumentBuilder();
		doc = docBuilder.newDocument();
		this.project = doc.createElement(PROJECT_TAG);
		doc.appendChild(project);
	}

	/** Write the object content in a file
	 * @param file File to write the content
	 * @throws TransformerException
	 * @throws IOException
	 */
	public void toFile(File file) throws TransformerException, IOException {
		String xmlString = this.getString();
		byte buf[] = xmlString.getBytes();
		OutputStream f0 = new FileOutputStream(file);
		for (int i = 0; i < buf.length; i++) {
			f0.write(buf[i]);
		}
		f0.close();
	}

	/** Serialize the object in a XML string
	 * @return XML serialization of the object content
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
		DOMSource source = new DOMSource(doc);
		trans.transform(source, result);
		return sw.toString();

	}

	/**Add the localhost as a worker node in the project.xml
	 * @param coreLocation
	 */
	public void addLocalhostWorker(String coreLocation) {
		String working_dir = "/home/" + System.getProperty("user.name")
				+ File.separator;
		addWorker("localhost", coreLocation, working_dir,
				System.getProperty("user.name"), 2);

	}

	/** Add a generic worker node to the project.xml
	 * @param worker_name Hostname/IP of the worker host
	 * @param install_dir Folder where application is installed
	 * @param working_dir Working folder of the worker host
	 * @param user_name Username used in the remote host
	 * @param limitOfTasks number of simultaneous core elements 
	 * invocations in the remote host
	 */
	public void addWorker(String worker_name, String install_dir,
			String working_dir, String user_name, int limitOfTasks) {
		Element worker = doc.createElement(WORKER_TAG);
		worker.setAttribute(NAME_ATTR, worker_name);
		Element installDir = doc.createElement(INSTALLDIR_TAG);
		installDir.setTextContent(install_dir);
		worker.appendChild(installDir);
		Element workingDir = doc.createElement(WORKINGDIR_TAG);
		workingDir.setTextContent(working_dir);
		worker.appendChild(workingDir);
		Element user = doc.createElement(USER_TAG);
		user.setTextContent(user_name);
		worker.appendChild(user);
		Element limit = doc.createElement(LIMIT_TASKS_TAG);
		limit.setTextContent(Integer.toString(limitOfTasks));
		worker.appendChild(limit);
		project.appendChild(worker);
	}

	/** Add a service node as a worker in the project.xml
	 * @param loc WSDL location of the service
	 * @param limitOfTasks number of concurrent invocations
	 */
	public void addServiceWorker(String loc, int limitOfTasks) {

		Element worker = getWorker(loc);
		if (worker == null) {
			worker = doc.createElement(WORKER_TAG);
			worker.setAttribute(NAME_ATTR, loc);
			Element limit = doc.createElement(LIMIT_TASKS_TAG);
			limit.setTextContent(Integer.toString(limitOfTasks));
			worker.appendChild(limit);
			project.appendChild(worker);
		} else {
			Element limit = (Element) worker.getElementsByTagName(
					LIMIT_TASKS_TAG).item(0);
			if (limit != null) {
				limit.setTextContent(Integer.toString(limitOfTasks));
			}
		}

	}

	/** Get a worker node from hostname/IPaddress/WSDL location
	 * @param loc hostname/IPaddress/WSDL location
	 * @return XML element of the worker
	 */
	private Element getWorker(String loc) {
		NodeList nl = project.getElementsByTagName(WORKER_TAG);

		if (nl != null && nl.getLength() > 0) {
			for (int i = 0; i < nl.getLength(); i++) {
				Element worker = (Element) nl.item(i);
				if (worker.getAttribute(NAME_ATTR).equals(loc)) {
					return worker;
				}
			}
			return null;
		} else {
			log.warn("No worker elements found");
			return null;
		}
	}

	/** Add a Cloud Provider to the project.xml
	 * @param providerName Name of the cloud provider
	 * @return XML element of the provider node
	 */
	public Element addCloudProvider(String providerName) {
		NodeList nl = project.getElementsByTagName(CLOUD_TAG);
		Element cloud;
		if (nl != null && nl.getLength() > 0) {
			cloud = (Element) nl.item(0);
		} else {
			cloud = doc.createElement(CLOUD_TAG);
			project.appendChild(cloud);
		}
		Element provider = doc.createElement(PROVIDER_TAG);
		provider.setAttribute(NAME_MIN_ATTR, providerName);
		Element image_list = doc.createElement(IMAGE_LIST_TAG);
		provider.appendChild(image_list);
		cloud.appendChild(provider);
		return provider;
	}

	/** Get a cloud provider from the project.xml
	 * @param providerName Name of the Cloud provider
	 * @return XML element of the provider
	 */
	private Element getCloudProvider(String providerName) {

		NodeList ct = project.getElementsByTagName(CLOUD_TAG);
		if (ct != null && ct.getLength() > 0) {
			NodeList nl = ((Element) ct.item(0))
					.getElementsByTagName(PROVIDER_TAG);
			if (nl != null && nl.getLength() > 0) {
				for (int i = 0; i < nl.getLength(); i++) {
					Element provider = (Element) nl.item(i);
					if (provider.getAttribute(NAME_MIN_ATTR).equals(
							providerName)) {
						return provider;
					}
				}
			}
		}
		return null;
	}

	/** Add an Image description to a Cloud provider
	 * @param providerName Name of the Cloud provider
	 * @param id Image id.
	 * @param user Username in the image.
	 * @param workingDir Working dir in the image.
	 * @param installDir Application install dir in the image. 
	 */
	public void addImageToProvider(String providerName, String id, String user,
			String workingDir, String installDir) {
		Element provider = getCloudProvider(providerName);
		if (provider == null) {
			provider = addCloudProvider(providerName);
		}
		NodeList nl = provider.getElementsByTagName(IMAGE_LIST_TAG);
		Element image_list;
		if (nl != null && nl.getLength() > 0) {
			image_list = (Element) nl.item(0);
		} else {
			image_list = doc.createElement(IMAGE_LIST_TAG);
			provider.appendChild(image_list);
		}
		Element image = doc.createElement(IMAGE_TAG);
		image.setAttribute(NAME_MIN_ATTR, id);
		Element el = doc.createElement(INSTALLDIR_TAG);
		el.setTextContent(installDir);
		image.appendChild(el);
		el = doc.createElement(WORKINGDIR_TAG);
		el.setTextContent(workingDir);
		image.appendChild(el);
		el = doc.createElement(USER_TAG);
		el.setTextContent(user);
		image.appendChild(el);
		image_list.appendChild(image);
	}
}
