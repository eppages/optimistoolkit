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

package es.bsc.servicess.ide.editors.deployers;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringWriter;
import java.util.ArrayList;

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

public class GridResourcesFile {
	private static final String GRID_TAG = "Grid";
	private static final String RES_TAG = "Resource";
	public static final String TYPE_ATTR = "Type";
	public static final String INSTALL_PATH_ATTR = "InstallDir";
	public static final String WORKING_PATH_ATTR = "WorkingDir";
	public static final String SERVER_PATH_ATTR = "ServerPath";
	public static final String USERNAME_ATTR = "Username";
	public static final String HOSTNAME_ATTR = "Hostname";
	public static final String MASTER = "Master";
	public static final String WORKER = "Worker";
	public static final String BOTH = "Both";
	public static final String[] TYPES = new String[] { MASTER, WORKER, BOTH };

	private Element gridElement;
	private Document gr_doc;

	public GridResourcesFile(File file) throws SAXException, IOException,
			ParserConfigurationException {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = factory.newDocumentBuilder();
		gr_doc = docBuilder.parse(file);
		this.gridElement = gr_doc.getDocumentElement();
		if (gr_doc == null || gridElement == null) {
			throw new IOException("Project Element is null");
		}
	}

	public GridResourcesFile() throws ParserConfigurationException {

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = factory.newDocumentBuilder();
		gr_doc = docBuilder.newDocument();
		gridElement = gr_doc.createElement(GRID_TAG);
		gr_doc.appendChild(gridElement);

	}

	public Element getResource(String hostname) {
		NodeList nl = gridElement.getElementsByTagName(RES_TAG);
		if (nl != null && nl.getLength() > 0) {
			for (int i = 0; i < nl.getLength(); i++) {
				Element res = (Element) nl.item(i);
				if (res.getAttribute(HOSTNAME_ATTR).equals(hostname)) {
					return res;
				}
			}
		}
		return null;
	}

	public void updateResource(String hostname, String type, String username,
			String installPath, String workingPath, String serverPath) {
		Element res = getResource(hostname);
		if (res == null) {
			res = gr_doc.createElement(RES_TAG);
			res.setAttribute(HOSTNAME_ATTR, hostname);
			gridElement.appendChild(res);
		}
		res.setAttribute(TYPE_ATTR, type);
		res.setAttribute(USERNAME_ATTR, username);
		res.setAttribute(INSTALL_PATH_ATTR, installPath);
		if (type.equals(WORKER) || type.equals(BOTH)) {
			res.setAttribute(WORKING_PATH_ATTR, workingPath);
		} else
			res.removeAttribute(WORKING_PATH_ATTR);

		if (type.equals(MASTER) || type.equals(BOTH)) {
			res.setAttribute(SERVER_PATH_ATTR, serverPath);
		} else
			res.removeAttribute(SERVER_PATH_ATTR);
	}

	public void removeResource(String hostname) {
		Element res = getResource(hostname);
		if (res != null) {
			gridElement.removeChild(res);
		}
	}

	public String[] getResources() {
		NodeList nl = gridElement.getElementsByTagName(RES_TAG);
		if (nl != null && nl.getLength() > 0) {
			String[] resources = new String[nl.getLength()];
			for (int i = 0; i < nl.getLength(); i++) {
				Element res = (Element) nl.item(i);
				resources[i] = res.getAttribute(HOSTNAME_ATTR);
			}
			return resources;
		} else
			return new String[0];
	}

	public Element getMasterResource() {
		NodeList nl = gridElement.getElementsByTagName(RES_TAG);
		if (nl != null && nl.getLength() > 0) {
			for (int i = 0; i < nl.getLength(); i++) {
				Element res = (Element) nl.item(i);
				String type = res.getAttribute(TYPE_ATTR);
				if (type.equals(MASTER) || type.equals(BOTH))
					return res;
			}

		}
		return null;
	}

	public Element[] getWorkerResources() {
		NodeList nl = gridElement.getElementsByTagName(RES_TAG);
		if (nl != null && nl.getLength() > 0) {
			ArrayList<Element> resources = new ArrayList<Element>();
			for (int i = 0; i < nl.getLength(); i++) {
				Element res = (Element) nl.item(i);
				String type = res.getAttribute(TYPE_ATTR);
				if (type.equals(WORKER) || type.equals(BOTH))
					resources.add(res);
			}
			return resources.toArray(new Element[resources.size()]);
		} else
			return new Element[0];
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
		DOMSource source = new DOMSource(gr_doc);
		trans.transform(source, result);
		return sw.toString();

	}
}
