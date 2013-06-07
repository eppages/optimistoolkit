/**

Copyright 2013 ATOS SPAIN S.A.

Licensed under the Apache License, Version 2.0 (the License);
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

Authors :
Juan Luis Prieto, Francisco Javier Nieto. Atos Research and Innovation, Atos SPAIN SA
@email francisco.nieto@atosresearch.eu 

**/

package eu.optimis.tf.sp.service.utils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class ServiceManifestXMLProcessor {

	private static final String OPTIMIS_NS = "http://schemas.optimis.eu/optimis/";

	public static void main(String args[]) {
		if (args.length < 2) {
			System.out
					.println("USAGE: java ServiceManifestXMLProcessor <path to manifest> <attribute>");
			System.exit(1);
		}

		try {
			String path_to_manifest = args[0];
			String attribute_ = args[1];

			System.out.println("Going to extract attribute " + attribute_
					+ " from manifest " + path_to_manifest);

			String result = extractAttributeFromManifest(path_to_manifest,
					attribute_);

			if (result != null) {
				System.out.println("Attribute " + attribute_
						+ " has the value " + result);
			} else {
				System.out.println("Error processing xml file.");
			}

		} catch (Exception t) {
			t.printStackTrace();
		}
	}

	public static String extractAttributeFromManifest(String path_to_manifest,
			String attribute_) {
		String attribute_value = null;
		try {

			DocumentBuilderFactory builderFactory = DocumentBuilderFactory
					.newInstance();

			builderFactory.setValidating(false);
			builderFactory.setIgnoringElementContentWhitespace(true);

			DocumentBuilder builder = builderFactory.newDocumentBuilder();

			InputSource in = new InputSource();

			String manifest = readFileAsString(path_to_manifest);

			System.out.println(manifest);

			in.setCharacterStream(new StringReader(manifest));

			Document document = builder.parse(in);

			Element elem = document.getDocumentElement();

			NodeList vmdesc_nodes = elem
					.getElementsByTagName("opt:VirtualMachineDescription");

			Element VMDescr_elem = (Element) vmdesc_nodes.item(0);

			attribute_value = VMDescr_elem.getAttribute("opt:serviceId");

		} catch (Exception e) {
			e.printStackTrace();
		}

		return attribute_value;
	}

	public static String readFileAsString(String filePath)
			throws java.io.IOException {
		byte[] buffer = new byte[(int) new File(filePath).length()];
		BufferedInputStream f = null;
		try {
			f = new BufferedInputStream(new FileInputStream(filePath));
			f.read(buffer);
		} finally {
			if (f != null)
				try {
					f.close();
				} catch (IOException ignored) {
				}
		}
		return new String(buffer);
	}

	public static String getAttribute(String manifest, String tag,
			String attribute) throws ParserConfigurationException,
			SAXException, IOException {
		Element manifestElem = getElement(manifest);
		return getAttribute(manifestElem, tag, attribute);
	}

	public static String getAttribute(Element elem, String tag, String attribute) {

		if (tag.equalsIgnoreCase("ServiceManifest")) {
			System.out.println(elem.getLocalName());
			String spId = elem.getAttributeNS(OPTIMIS_NS, attribute);
			System.out.println("Service Provider Id: " + spId);
			return spId;
		}

		NodeList vmdesc_nodes = elem.getElementsByTagNameNS(OPTIMIS_NS, tag);
		System.out.println(vmdesc_nodes.getLength());

		Element VMDescr_elem = (Element) vmdesc_nodes.item(0);

		String attribute_value = VMDescr_elem.getAttributeNS(OPTIMIS_NS,
				attribute);
		return attribute_value;
	}

	private static Element getElement(String manifest)
			throws ParserConfigurationException, SAXException, IOException {
		DocumentBuilderFactory builderFactory = DocumentBuilderFactory
				.newInstance();

		builderFactory.setValidating(false);
		builderFactory.setNamespaceAware(true);
		builderFactory.setIgnoringElementContentWhitespace(true);

		DocumentBuilder builder = builderFactory.newDocumentBuilder();
		InputSource in = new InputSource();
		in.setCharacterStream(new StringReader(manifest));
		Document document = builder.parse(in);
		Element elem = document.getDocumentElement();
		return elem;
	}

	public static String document2String(Document doc) {
		try {
			DOMSource domSource = new DOMSource(doc);
			StringWriter writer = new StringWriter();
			StreamResult result = new StreamResult(writer);
			TransformerFactory tf = TransformerFactory.newInstance();
			Transformer transformer = tf.newTransformer();
			transformer.transform(domSource, result);
			return writer.toString();
		} catch (TransformerException ex) {
			ex.printStackTrace();
			return null;
		}
	}
}