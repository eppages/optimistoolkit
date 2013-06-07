/**
 * Copyright (C) 2010-2012 Barcelona Supercomputing Center
 *
 * This library is free software; you can redistribute it and/or 
 * modify it under the terms of the GNU Lesser General Public 
 * License as published by the Free Software Foundation; either 
 * version 2.1 of the License, or (at your option) any later version.
 * This library is distributed in the hope that it will be useful, 
 * but WITHOUT ANY WARRANTY; without even the implied warranty of 
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU 
 * Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public 
 * License along with this library; if not, write to the Free Software 
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package eu.optimis.cloudoptimizer.xml;

import java.io.IOException;
import java.io.StringReader;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class XmlUtil {

    private final static String[] PHYSICAL_XML_ELEMENTS = new String[]{
        "id", "hostname", "hypervisor", "disk_size_in_gigabytes", "cpu_cores",
        "memory_in_gigabytes", "os", "network_adapter", "public_ip_address",
        "private_ip_address", "infrastructure_provider_id", "active"};
    private final static String[] VIRTUAL_XML_ELEMENTS = new String[]{
        "id", "hostname", "physical_resource_id", "service_id", "type",
        "hypervisor", "disk_size_in_gigabytes", "cpu_cores", "memory_in_gigabytes",
        "os", "network_adapter", "public_ip_address", "private_ip_address", "comments"};

    // string -> Document
    private static Document getDocument(String xml) throws ParserConfigurationException, SAXException, IOException {        
            // Create a builder factory
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);

            return factory.newDocumentBuilder().parse(new InputSource(new StringReader(xml)));
    }

    /**
     * Obtains a physical resource from a given XML string.
     */
    public static PhysicalResource getPhysicalResourceFromXml(String xml) {
        try {
            Document doc = getDocument(xml);

            return new PhysicalResource(doc.getElementsByTagName(PHYSICAL_XML_ELEMENTS[0]).item(0).getTextContent(),
                    doc.getElementsByTagName(PHYSICAL_XML_ELEMENTS[1]).item(0).getTextContent(),
                    doc.getElementsByTagName(PHYSICAL_XML_ELEMENTS[2]).item(0).getTextContent(),
                    doc.getElementsByTagName(PHYSICAL_XML_ELEMENTS[3]).item(0).getTextContent(),
                    doc.getElementsByTagName(PHYSICAL_XML_ELEMENTS[4]).item(0).getTextContent(),
                    doc.getElementsByTagName(PHYSICAL_XML_ELEMENTS[5]).item(0).getTextContent(),
                    doc.getElementsByTagName(PHYSICAL_XML_ELEMENTS[6]).item(0).getTextContent(),
                    doc.getElementsByTagName(PHYSICAL_XML_ELEMENTS[7]).item(0).getTextContent(),
                    doc.getElementsByTagName(PHYSICAL_XML_ELEMENTS[8]).item(0).getTextContent(),
                    doc.getElementsByTagName(PHYSICAL_XML_ELEMENTS[9]).item(0).getTextContent(),
                    doc.getElementsByTagName(PHYSICAL_XML_ELEMENTS[10]).item(0).getTextContent(),
                    doc.getElementsByTagName(PHYSICAL_XML_ELEMENTS[11]).item(0).getTextContent()
                    );
        } catch(Exception e) {
            throw new RuntimeException("Error parsing XML:\n" + xml,e);
        }
    }

    /**
     * Obtains a virtual resource from a given XML string.
     */
    public static VirtualResource getVirtualResourceFromXml(String xml) {
        try {
            Document doc = getDocument(xml);
            return new VirtualResource(
                    doc.getElementsByTagName(VIRTUAL_XML_ELEMENTS[0]).item(0).getTextContent(),
                    doc.getElementsByTagName(VIRTUAL_XML_ELEMENTS[1]).item(0).getTextContent(),
                    doc.getElementsByTagName(VIRTUAL_XML_ELEMENTS[2]).item(0).getTextContent(),
                    doc.getElementsByTagName(VIRTUAL_XML_ELEMENTS[3]).item(0).getTextContent(),
                    doc.getElementsByTagName(VIRTUAL_XML_ELEMENTS[4]).item(0).getTextContent(),
                    doc.getElementsByTagName(VIRTUAL_XML_ELEMENTS[5]).item(0).getTextContent(),
                    doc.getElementsByTagName(VIRTUAL_XML_ELEMENTS[6]).item(0).getTextContent(),
                    doc.getElementsByTagName(VIRTUAL_XML_ELEMENTS[7]).item(0).getTextContent(),
                    doc.getElementsByTagName(VIRTUAL_XML_ELEMENTS[8]).item(0).getTextContent(),
                    doc.getElementsByTagName(VIRTUAL_XML_ELEMENTS[9]).item(0).getTextContent(),
                    doc.getElementsByTagName(VIRTUAL_XML_ELEMENTS[10]).item(0).getTextContent(),
                    doc.getElementsByTagName(VIRTUAL_XML_ELEMENTS[11]).item(0).getTextContent(),
                    doc.getElementsByTagName(VIRTUAL_XML_ELEMENTS[12]).item(0).getTextContent(),
                    doc.getElementsByTagName(VIRTUAL_XML_ELEMENTS[13]).item(0).getTextContent()
            );
        } catch(Exception e) {
            throw new RuntimeException("Error parsing xml: " + xml, e);
        }
        
    }

}
