/*
 * Copyright 2012 University of Stuttgart
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package eu.optimis.sm.gui.server;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.lang.time.DateFormatUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

//import eu.optimis.mi.monitoring_resources.MonitoringResourceDatasets;
import eu.optimis.sm.gui.client.model.Resource;

public class XmlUtil {

	public static Document getDocument(String xml) {
		try {
			// Create a builder factory
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setNamespaceAware(true);

			return factory.newDocumentBuilder().parse(
					new InputSource(new StringReader(xml)));
		} catch (SAXException e) {
			return null;
		} catch (ParserConfigurationException e) {
			return null;
		} catch (IOException e) {
			return null;
		}
	}

	public String getObjXml(String xml) {
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setNamespaceAware(true);
			Document doc = factory.newDocumentBuilder().parse(
					new InputSource(new StringReader(xml)));
			NodeList timestampList = doc
					.getElementsByTagName("metric_timestamp");
			for (int i = 0; i < timestampList.getLength(); i++) {
				Element ts = (Element) timestampList.item(i);
				String tsLangType = ts.getTextContent();
				try {
					long millis = 0;
					millis = Long.parseLong(tsLangType);
					Date udate = new Date(millis*1000);
					String timestamp = DateFormatUtils.ISO_DATETIME_FORMAT
							.format(udate);
					ts.setTextContent(timestamp);
				} catch (NumberFormatException e) {
					 
				}
			}
			String rs = xmlToString(doc);
			return rs;

		} catch (SAXException e) {
			return null;
		} catch (ParserConfigurationException e) {
			return null;
		} catch (IOException e) {
			return null;
		}
	}

	public List<Resource> getMonitoringRsModel(String xml) {
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setNamespaceAware(true);
			Document doc = factory.newDocumentBuilder().parse(
					new InputSource(new StringReader(xml)));

			NodeList msList = doc.getElementsByTagName("monitoring_resource");

			ArrayList<Resource> rsList = new ArrayList<Resource>();
			for (int i = 0; i < msList.getLength(); i++) {
				Resource mdata = new Resource();
				Element ts = (Element) msList.item(i);
				Element eMetricname = (Element) ts.getElementsByTagName(
						"metric_name").item(0);
				String sMetricname = eMetricname.getTextContent();
				mdata.setMetricName(sMetricname);

				Element eMetricvalue = (Element) ts.getElementsByTagName(
						"metric_value").item(0);
				String sMetricvalue = eMetricvalue.getTextContent();
				mdata.setMetricValue(sMetricvalue);

				Element eMetricunit = (Element) ts.getElementsByTagName(
						"metric_unit").item(0);
				String sMetricunit = eMetricunit.getTextContent();
				mdata.setMetricUnit(sMetricunit);

				Element eMetrictp = (Element) ts.getElementsByTagName(
						"metric_timestamp").item(0);
				String tsLangType = eMetrictp.getTextContent();
				mdata.setMetricTimestamp(tsLangType);
				
				Element eMinfoColType = (Element) ts.getElementsByTagName(
						"monitoring_information_collector_id").item(0);
				if (eMinfoColType != null) {
					String sMinfoColType = eMinfoColType.getTextContent();
					mdata.setCollectorId(sMinfoColType);
				}

				Element eResType = (Element) ts.getElementsByTagName(
						"resource_type").item(0);
				if (eResType != null) {
					String sResType = eResType.getTextContent();
					mdata.setResourceType(sResType);
				}

				Element ePResId = (Element) ts.getElementsByTagName(
						"physical_resource_id").item(0);
				if (ePResId != null) {
					String sPResId = ePResId.getTextContent();
					mdata.setPhysicalResourceId(sPResId);
				}

				Element eSResId = (Element) ts.getElementsByTagName(
						"service_resource_id").item(0);
				if (eSResId != null) {
					String sSResId = eSResId.getTextContent();
					mdata.setServiceResourceId(sSResId);
				}

				Element eVResId = (Element) ts.getElementsByTagName(
						"virtual_resource_id").item(0);
				if (eVResId != null) {
					String sVResId = eVResId.getTextContent();
					mdata.setVirtualResourceId(sVResId);
				}
				rsList.add(mdata);
				mdata = null;
			}
			
			return rsList;

		} catch (SAXException e) {
			return null;
		} catch (ParserConfigurationException e) {
			return null;
		} catch (IOException e) {
			return null;
		}
	}

	private static String xmlToString(Node node) {
		try {
			Source source = new DOMSource(node);
			StringWriter stringWriter = new StringWriter();
			Result result = new StreamResult(stringWriter);
			TransformerFactory factory = TransformerFactory.newInstance();
			Transformer transformer = factory.newTransformer();
			transformer.transform(source, result);
			return stringWriter.getBuffer().toString();
		} catch (TransformerConfigurationException e) {
			e.printStackTrace();
		} catch (TransformerException e) {
			e.printStackTrace();
		}
		return null;
	}
	
}
