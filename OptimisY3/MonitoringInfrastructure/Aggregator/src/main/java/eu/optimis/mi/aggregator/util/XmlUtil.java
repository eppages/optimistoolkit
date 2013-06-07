/**

Copyright 2012 University of Stuttgart

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at 

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

**/

package eu.optimis.mi.aggregator.util;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
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

import eu.optimis.mi.monitoring_resources.MonitoringResourceDataset;
import eu.optimis.mi.monitoring_resources.MonitoringResourceDatasets;

public class XmlUtil {

	@SuppressWarnings("unused")
	private final static String[] SERVICE_XML_ELEMENTS = new String[] { "MonitoringResources" };

	// String -> Document
	@SuppressWarnings("unused")
	private static Document getDocument(String xml) {
		try {
			// Create a builder factory
			DocumentBuilderFactory factory = DocumentBuilderFactory
					.newInstance();
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
			// Create a builder factory
			DocumentBuilderFactory factory = DocumentBuilderFactory
					.newInstance();
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
	// xml has original resources collected by a resource collector. 
	public MonitoringResourceDatasets getMRDObj(String xml) {
		try {
			// Create a builder factory
			DocumentBuilderFactory factory = DocumentBuilderFactory
					.newInstance();
			factory.setNamespaceAware(true);
			Document doc = factory.newDocumentBuilder().parse(
					new InputSource(new StringReader(xml)));

			NodeList msList = doc.getElementsByTagName("monitoring_resource");

			ArrayList<MonitoringResourceDataset> rsList = new ArrayList<MonitoringResourceDataset>();
			for (int i = 0; i < msList.getLength(); i++) {
				MonitoringResourceDataset mdata = new MonitoringResourceDataset();
				Element ts = (Element) msList.item(i);
				Element eMetricname = (Element) ts.getElementsByTagName(
						"metric_name").item(0);
				String sMetricname = eMetricname.getTextContent();
				mdata.setMetric_name(sMetricname);

				Element eMetricvalue = (Element) ts.getElementsByTagName(
						"metric_value").item(0);
				String sMetricvalue = eMetricvalue.getTextContent();
				mdata.setMetric_value(sMetricvalue);

				Element eMetricunit = (Element) ts.getElementsByTagName(
						"metric_unit").item(0);
				String sMetricunit = eMetricunit.getTextContent();
				mdata.setMetric_unit(sMetricunit);

				Element eMetrictp = (Element) ts.getElementsByTagName(
						"metric_timestamp").item(0);
				String tsLangType = eMetrictp.getTextContent();
				Date date = null;
				try {
					long millis = Long.valueOf(tsLangType);
					date = new Date(millis*1000);
					@SuppressWarnings("unused")
					String tp = DateFormatUtils.ISO_DATETIME_FORMAT
					.format(date);
				} catch (NumberFormatException e) {

				}
				mdata.setMetric_timestamp(date);
				
				Element eMinfoColType = (Element) ts.getElementsByTagName(
						"monitoring_information_collector_id").item(0);
				if (eMinfoColType != null) {
					String sMinfoColType = eMinfoColType.getTextContent();
					mdata.setMonitoring_information_collector_id(sMinfoColType);
				}

				Element eResType = (Element) ts.getElementsByTagName(
						"resource_type").item(0);
				if (eResType != null) {
					String sResType = eResType.getTextContent();
					mdata.setResource_type(sResType);
				}

				Element ePResId = (Element) ts.getElementsByTagName(
						"physical_resource_id").item(0);
				if (ePResId != null) {
					String sPResId = ePResId.getTextContent();
					mdata.setPhysical_resource_id(sPResId);
				}

				Element eSResId = (Element) ts.getElementsByTagName(
						"service_resource_id").item(0);
				if (eSResId != null) {
					String sSResId = eSResId.getTextContent();
					mdata.setService_resource_id(sSResId);
				}

				Element eVResId = (Element) ts.getElementsByTagName(
						"virtual_resource_id").item(0);
				if (eVResId != null) {
					String sVResId = eVResId.getTextContent();
					mdata.setVirtual_resource_id(sVResId);
				}
				rsList.add(mdata);
				mdata = null;
			}
			MonitoringResourceDatasets mdatasets = new MonitoringResourceDatasets();
			mdatasets.setMonitoring_resource(rsList);
			return mdatasets;

		} catch (SAXException e) {
			return null;
		} catch (ParserConfigurationException e) {
			return null;
		} catch (IOException e) {
			return null;
		}
	}
	
	
	public List<MonitoringResourceDataset> getMRDObjY3(String xml) {
		List<MonitoringResourceDataset> rsList = new ArrayList<MonitoringResourceDataset>();
		try {
			// Create a builder factory
			DocumentBuilderFactory factory = DocumentBuilderFactory
					.newInstance();
			factory.setNamespaceAware(true);
			Document doc = factory.newDocumentBuilder().parse(
					new InputSource(new StringReader(xml)));

			NodeList msList = doc.getElementsByTagName("monitoring_resource");
			for (int i = 0; i < msList.getLength(); i++) {
				MonitoringResourceDataset mdata = new MonitoringResourceDataset();
				Element ts = (Element) msList.item(i);
				Element eMetricname = (Element) ts.getElementsByTagName(
						"metric_name").item(0);
				String sMetricname = eMetricname.getTextContent();
				mdata.setMetric_name(sMetricname);

				Element eMetricvalue = (Element) ts.getElementsByTagName(
						"metric_value").item(0);
				String sMetricvalue = eMetricvalue.getTextContent();
				mdata.setMetric_value(sMetricvalue);

				Element eMetricunit = (Element) ts.getElementsByTagName(
						"metric_unit").item(0);
				String sMetricunit = eMetricunit.getTextContent();
				mdata.setMetric_unit(sMetricunit);

				Element eMetrictp = (Element) ts.getElementsByTagName(
						"metric_timestamp").item(0);
				String tsLangType = eMetrictp.getTextContent();
				Date date = null;
				try {
					long millis = Long.valueOf(tsLangType);
					date = new Date(millis*1000);
					//@SuppressWarnings("unused")
					//String tp = DateFormatUtils.ISO_DATETIME_FORMAT.format(date);
				} catch (NumberFormatException e) {

				}
				mdata.setMetric_timestamp(date);
				
				Element eMinfoColType = (Element) ts.getElementsByTagName(
						"monitoring_information_collector_id").item(0);
				if (eMinfoColType != null) {
					String sMinfoColType = eMinfoColType.getTextContent();
					mdata.setMonitoring_information_collector_id(sMinfoColType);
				}

				Element eResType = (Element) ts.getElementsByTagName(
						"resource_type").item(0);
				if (eResType != null) {
					String sResType = eResType.getTextContent();
					mdata.setResource_type(sResType);
				}

				Element ePResId = (Element) ts.getElementsByTagName(
						"physical_resource_id").item(0);
				if (ePResId != null) {
					String sPResId = ePResId.getTextContent();
					mdata.setPhysical_resource_id(sPResId);
				}

				Element eSResId = (Element) ts.getElementsByTagName(
						"service_resource_id").item(0);
				if (eSResId != null) {
					String sSResId = eSResId.getTextContent();
					mdata.setService_resource_id(sSResId);
				}

				Element eVResId = (Element) ts.getElementsByTagName(
						"virtual_resource_id").item(0);
				if (eVResId != null) {
					String sVResId = eVResId.getTextContent();
					mdata.setVirtual_resource_id(sVResId);
				}
				rsList.add(mdata);
				mdata = null;
			}
//			MonitoringResourceDatasets mdatasets = new MonitoringResourceDatasets();
//			mdatasets.setMonitoring_resource(rsList);
//			return mdatasets;
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

	public void storeXmlResource(String xmlRes) {
		XmlUtil ut = new XmlUtil();
		String objRecordXml = ut.getObjXml(xmlRes);
		JAXBContext context;
		try {
			context = JAXBContext.newInstance(MonitoringResourceDatasets.class);
			StringReader reader = new StringReader(objRecordXml);
			@SuppressWarnings("unused")
			MonitoringResourceDatasets mds = (MonitoringResourceDatasets) context
					.createUnmarshaller().unmarshal(reader);
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
// Main section used for testing of the code.
//	
	public static void main(String[] args){
		String resource = 
			"<?xml version='1.0'?><MonitoringResources><monitoring_resource>" +
			"<physical_resource_id>Tt</physical_resource_id>" +
			"<metric_name>mem_used</metric_name><metric_value>90</metric_value>" +
			"<metric_unit>percent</metric_unit><metric_timestamp>1346680584"+ 
			"</metric_timestamp>" +
			"<service_resource_id></service_resource_id>" +
			"<virtual_resource_id>VirtualUnitTestId</virtual_resource_id>" +
			"<resource_type>virtual</resource_type>" +
			"<monitoring_information_collector_id>test</monitoring_information_collector_id>"+
			"</monitoring_resource><monitoring_resource>" +
			"<physical_resource_id>T</physical_resource_id>" +
			"<metric_name>cpu_user</metric_name><metric_value>0.04</metric_value>" +
			"<metric_unit>percent</metric_unit><metric_timestamp>234234" +
			"</metric_timestamp>" +
			"<service_resource_id></service_resource_id><virtual_resource_id>VirtualUnitTestId</virtual_resource_id>" +
			"<resource_type>virtual</resource_type>" +
			"<monitoring_information_collector_id></monitoring_information_collector_id>"+
			"</monitoring_resource>" +
			"</MonitoringResources>";
		 XmlUtil xmlu = new XmlUtil();
		 List<MonitoringResourceDataset> mrs =  xmlu.getMRDObjY3(resource);
		 
		 for (MonitoringResourceDataset mds: mrs){
			 System.out.println("=====================");
			 System.out.println("physicalId: "+mds.getPhysical_resource_id());
			 System.out.println("metric_name: "+mds.getMetric_name());
			 System.out.println("metric_value: "+mds.getMetric_value());
			 System.out.println("metric_unit: "+mds.getMetric_unit());
			 System.out.println("resource_type: "+mds.getResource_type());
			 System.out.println("timesstamp:"+mds.getMetric_timestamp());
			 System.out.println("micollecotor: "+mds.getMonitoring_information_collector_id());
		 }
	}
}
