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
import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
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

import com.jezhumble.javasysmon.JavaSysMon;

public class ResourcesFile {
	private static final String RESOURCES_LIST_TAG = "ResourceList";
	private static final String RESOURCE_TAG = "Resource";
	private static final String NAME_ATTR = "Name";
	private static final String NAME_MIN_ATTR = "name";
	private static final String CAPABILITIES_TAG = "Capabilities";
	private static final String HOST_TAG = "Host";
	private static final String PROCESSOR_TAG = "Processor";
	private static final String ARCHITECTURE_TAG = "Architecture";
	private static final String SPEED_TAG = "Speed";
	private static final String COUNT_TAG = "CPUCount";
	private static final String OS_TAG = "OS";
	private static final String OS_TYPE_TAG = "OSType";
	private static final String STORAGE_TAG = "StorageElement";
	private static final String SIZE_TAG = "Size";
	private static final String MEMORY_TAG = "Memory";
	private static final String PHY_MEMORY_SIZE_TAG = "PhysicalSize";
	private static final String APP_SOFT_TAG = "ApplicationSoftware";
	private static final String SOFTWARE_TAG = "Software";
	private Element resources;
	private Document doc;
	private static final String TASK_COUNT_TAG = "TaskCount";
	private static final String FILESYS_TAG = "FileSystem";
	private static final String NETADPT_TAG = "NetworkAdaptor";
	private static final String SERVICE_TAG = "Service";
	private static final String WSDL_ATTR = "wsdl";
	private static final String NAME_TAG = "Name";
	private static final String NAMESPACE_TAG = "Namespace";
	private static final String PORT_TAG = "Port";
	// TODO :MIRAR al file
	private static final String CLOUDPROVIDER_TAG = "CloudProvider";
	private static final String IMAGE_LIST_TAG = "ImageList";
	private static final String IMAGE_TAG = "Image";
	private static final String SHAREDISKS_TAG = "SharedDisks";
	private static final String SHARE_TAG = "Disk";
	private static final String MOUNTPOINT_TAG = "MountPoint";

	private static Logger log = Logger.getLogger(ResourcesFile.class);
	
	public ResourcesFile(File file) throws ParserConfigurationException,
			SAXException, IOException {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = factory.newDocumentBuilder();
		doc = docBuilder.parse(file);
		this.resources = doc.getDocumentElement();
		if (doc == null || resources == null) {
			throw new IOException("Project Element is null");
		}
	}

	public ResourcesFile() throws ParserConfigurationException, SAXException,
			IOException {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = factory.newDocumentBuilder();
		doc = docBuilder.newDocument();
		resources = doc.createElement(RESOURCES_LIST_TAG);
		doc.appendChild(resources);
	}

	public void toFile(File file) throws TransformerException, IOException {
		String xmlString = this.getString();
		byte buf[] = xmlString.getBytes();
		OutputStream f0 = new FileOutputStream(file);
		for (int i = 0; i < buf.length; i++) {
			f0.write(buf[i]);
		}
		f0.close();
	}

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

	public void addLocalhost(String workingdir) {
		OperatingSystemMXBean osbean = ManagementFactory
				.getOperatingSystemMXBean();
		JavaSysMon mon = new JavaSysMon();
		float proc_speed = mon.cpuFrequencyInHz() / 1000000000;
		String proc_arch = osbean.getArch();
		int proc_count = osbean.getAvailableProcessors();
		String os_type = osbean.getName();
		float memory_size = mon.physical().getTotalBytes() / 1000000000;
		File f = new File(workingdir);
		float storage_size = 0;
		if (f.exists()) {
			storage_size = f.getFreeSpace() / 1000000000;
		}
		addResource("localhost", proc_arch, proc_speed, proc_count, os_type,
				storage_size, memory_size, null);
	}

	public void addResource(String name, String proc_arch, float proc_speed,
			int proc_count, String os_type, float storage_size,
			float memory_size, String[] appSoftware) {
		/*
		 * <Processor> <Architecture>IA32</Architecture> <Speed>3.0</Speed>
		 * <CPUCount>1</CPUCount> </Processor> <OS> <OSType>Linux</OSType>
		 * <MaxProcessesPerUser>32</MaxProcessesPerUser> </OS> <StorageElement>
		 * <Size>30</Size> </StorageElement> <Memory>
		 * <PhysicalSize>1</PhysicalSize> <VirtualSize>8</VirtualSize> </Memory>
		 * <ApplicationSoftware> <Software>Java</Software>
		 * </ApplicationSoftware>
		 */
		Element worker = doc.createElement(RESOURCE_TAG);
		worker.setAttribute(NAME_ATTR, name);
		Element capabilities = doc.createElement(CAPABILITIES_TAG);
		Element host = doc.createElement(HOST_TAG);
		Element tc = doc.createElement(TASK_COUNT_TAG);
		tc.setTextContent("0");
		host.appendChild(tc);
		capabilities.appendChild(host);

		if (proc_arch != null || proc_speed > 0 || proc_count > 0) {
			Element processor = doc.createElement(PROCESSOR_TAG);
			if (proc_arch != null) {
				Element arch = doc.createElement(ARCHITECTURE_TAG);
				arch.setTextContent(proc_arch);
				processor.appendChild(arch);
			}
			if (proc_speed > 0) {
				Element arch = doc.createElement(SPEED_TAG);
				arch.setTextContent(new Float(proc_speed).toString());
				processor.appendChild(arch);
			}
			if (proc_count > 0) {
				Element arch = doc.createElement(COUNT_TAG);
				arch.setTextContent(new Integer(proc_count).toString());
				processor.appendChild(arch);
			}
			capabilities.appendChild(processor);
		}
		if (os_type != null) {
			Element processor = doc.createElement(OS_TAG);
			Element arch = doc.createElement(OS_TYPE_TAG);
			arch.setTextContent(os_type);
			processor.appendChild(arch);
			capabilities.appendChild(processor);
		}
		if (storage_size > 0) {
			Element processor = doc.createElement(STORAGE_TAG);
			Element arch = doc.createElement(SIZE_TAG);
			arch.setTextContent(new Float(storage_size).toString());
			processor.appendChild(arch);
			capabilities.appendChild(processor);
		}
		if (memory_size > 0) {
			Element processor = doc.createElement(MEMORY_TAG);
			Element arch = doc.createElement(PHY_MEMORY_SIZE_TAG);
			arch.setTextContent(new Float(memory_size).toString());
			processor.appendChild(arch);
			capabilities.appendChild(processor);
		}
		if (appSoftware != null && appSoftware.length > 0) {
			Element processor = doc.createElement(APP_SOFT_TAG);
			for (String software : appSoftware) {
				Element arch = doc.createElement(SOFTWARE_TAG);
				arch.setTextContent(software);
				processor.appendChild(arch);
			}
			capabilities.appendChild(processor);
		}
		Element filesys = doc.createElement(FILESYS_TAG);
		capabilities.appendChild(filesys);
		Element net_adpt = doc.createElement(NETADPT_TAG);
		capabilities.appendChild(net_adpt);
		worker.appendChild(capabilities);
		resources.appendChild(worker);
	}

	public void addServiceLocation(String serviceName, String namespace,
			String port, String loc) {
		Element service = getService(serviceName, namespace, port, loc);
		if (service == null) {
			service = doc.createElement(SERVICE_TAG);
			service.setAttribute(WSDL_ATTR, loc);
			Element name = doc.createElement(NAME_TAG);
			name.setTextContent(serviceName);
			service.appendChild(name);
			Element ns = doc.createElement(NAMESPACE_TAG);
			ns.setTextContent(namespace);
			service.appendChild(ns);
			Element p = doc.createElement(PORT_TAG);
			p.setTextContent(port);
			service.appendChild(p);
			resources.appendChild(service);
		}
	}

	private Element getService(String serviceName, String namespace,
			String port, String loc) {
		NodeList nl = resources.getElementsByTagName(SERVICE_TAG);
		if (nl != null && nl.getLength() > 0) {
			for (int i = 0; i < nl.getLength(); i++) {
				Element service = (Element) nl.item(i);
				if (service.getAttribute(WSDL_ATTR).equals(loc)) {
					NodeList name = service.getElementsByTagName(NAME_TAG);
					if (name != null && name.getLength() == 1
							&& name.item(0).getNodeValue().equals(serviceName)) {
						NodeList ns = service
								.getElementsByTagName(NAMESPACE_TAG);
						if (ns != null && ns.getLength() == 1
								&& ns.item(0).getNodeValue().equals(namespace)) {
							NodeList p = service.getElementsByTagName(PORT_TAG);
							if (p != null && p.getLength() == 1
									&& p.item(0).getNodeValue().equals(port)) {
								return service;
							}
						}

					}

				}
			}
			return null;
		} else {
			log.warn("No service elements found");
			return null;
		}
	}

	public Element addCloudProvider(String providerName) {
		Element provider = doc.createElement(CLOUDPROVIDER_TAG);
		provider.setAttribute(NAME_MIN_ATTR, providerName);
		Element image_list = doc.createElement(IMAGE_LIST_TAG);
		provider.appendChild(image_list);
		resources.appendChild(provider);
		return provider;
	}

	private Element getCloudProvider(String providerName) {

		NodeList nl = resources.getElementsByTagName(CLOUDPROVIDER_TAG);
		if (nl != null && nl.getLength() > 0) {
			for (int i = 0; i < nl.getLength(); i++) {
				Element provider = (Element) nl.item(i);
				if (provider.getAttribute(NAME_MIN_ATTR).equals(providerName)) {
					return provider;
				}
			}
		}
		return null;
	}

	public void addImageToProvider(String providerName, String id,
			Map<String, String> shares) {
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
		Element el = doc.createElement(SHAREDISKS_TAG);
		for (Entry<String, String> ent : shares.entrySet()) {
			Element share = doc.createElement(SHARE_TAG);
			share.setAttribute(NAME_MIN_ATTR, ent.getKey());
			Element mount = doc.createElement(MOUNTPOINT_TAG);
			mount.setTextContent(ent.getValue());
			share.appendChild(mount);
			el.appendChild(share);
		}
		image.appendChild(el);
		image_list.appendChild(image);
	}

	public void addDisk(String name, String sharedFolder) {
		Element share = doc.createElement(SHARE_TAG);
		share.setAttribute(NAME_MIN_ATTR, name);
		Element mount = doc.createElement(MOUNTPOINT_TAG);
		mount.setTextContent(sharedFolder);
		share.appendChild(mount);
		resources.appendChild(share);
	}

}
