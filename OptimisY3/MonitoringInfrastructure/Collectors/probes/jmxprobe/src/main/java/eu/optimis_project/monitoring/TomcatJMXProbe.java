/**
 * 
 * @author Daniel Espling (<a href="mailto:espling@cs.umu.se">espling@cs.umu.se</a>)
 * Copyright (C) 2011 Umea University
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package eu.optimis_project.monitoring;

import java.io.File;
import java.io.IOException;

import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.MBeanServerConnection;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.ReflectionException;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.tools.attach.VirtualMachine;
import com.sun.tools.attach.VirtualMachineDescriptor;

public class TomcatJMXProbe {

	private static final String CONNECTOR_ADDRESS = "com.sun.management.jmxremote.localConnectorAddress";
	private static final Object TOMCAT_DISPLAYNAME = "org.apache.catalina.startup.Bootstrap start";

	public TomcatJMXProbe(JMXServiceURL serviceURL) throws IOException,
			MalformedObjectNameException, NullPointerException,
			AttributeNotFoundException, InstanceNotFoundException,
			MBeanException, ReflectionException, ParserConfigurationException,
			SAXException {
		System.out.println("Inside Probe");

		// Connect to target (assuming no security)
		final JMXConnector connector = JMXConnectorFactory.connect(serviceURL);

		// Get an MBeanServerConnection on the remote VM.
		final MBeanServerConnection remote = connector
				.getMBeanServerConnection();

		ObjectName oName = new ObjectName(
				"Catalina:type=Manager,path=/manager,host=localhost");

		Object resultObj = remote.getAttribute(oName, "activeSessions");
		Integer result = (Integer) resultObj;

		System.out.println("Result: " + result);

        String serviceID = "testProbe.fakeserviceID";
        String instanceID = "testProbe.fakeinstanceID";

        publishResult(serviceID, instanceID, "ActiveSessions: " + result.toString(), "activeSessions",
                System.currentTimeMillis());

	}

    private void publishResult(String serviceID, String instanceID, String data, String name, long timestamp)
			throws IOException, ParserConfigurationException, SAXException {
		System.out.println("Sending result to server");
        Client client = Client.create();
        WebResource webResource = client.resource("http://localhost:3000/data"); // XXX
                                                                                 // parameterize

        /*
         * String data =
         * "<?xml version='1.0' encoding='UTF-8'?><measurement><serviceID>" +
         * serviceID + "</serviceID><data>" + result + "</data><name>" + name +
         * "</name><timestamp>" + timestamp + "</timestamp></measurement>";
         */
        
        Measurement measurement = new Measurement(serviceID, instanceID, name, data, timestamp);

        webResource.post(measurement); // XXX Consider response

        /*
         * DocumentBuilderFactory factory =
         * DocumentBuilderFactory.newInstance(); DocumentBuilder builder =
         * factory.newDocumentBuilder(); InputSource is = new InputSource(new
         * StringReader(data)); Document d = builder.parse(is);
         * 
         * StringRepresentation srep = new StringRepresentation(data);
         * DomRepresentation drep = new DomRepresentation(
         * MediaType.APPLICATION_XML, d);
         * 
         * Representation rep = resource.post(drep); System.out.println(rep);
         */
        
	}

	private static JMXServiceURL getURLForPid(String pid) throws Exception {

		// attach to the target application
		final VirtualMachine vm = VirtualMachine.attach(pid);

		// get the connector address
		String connectorAddress = vm.getAgentProperties().getProperty(
				CONNECTOR_ADDRESS);

		// no connector address, so we start the JMX agent
		if (connectorAddress == null) {
			String agent = vm.getSystemProperties().getProperty("java.home")
					+ File.separator + "lib" + File.separator
					+ "management-agent.jar";
			vm.loadAgent(agent);

			// agent is started, get the connector address
			connectorAddress = vm.getAgentProperties().getProperty(
					CONNECTOR_ADDRESS);
			assert connectorAddress != null;
		}
		return new JMXServiceURL(connectorAddress);
	}

	public static void main(String[] args) throws Exception {

		String pid = getTomcatPID();

		if (pid == null) {
			System.err.println("Could not find running Tomcat VM");
			System.exit(-1);
		}

		JMXServiceURL url = getURLForPid(pid);
		System.out.println("Got URL: '" + url + "'");
		new TomcatJMXProbe(url);
	}

	private static String getTomcatPID() {

		for (VirtualMachineDescriptor vd : VirtualMachine.list()) {
			System.out.println("Found vm: '" + vd.toString() + "'");
			if (TOMCAT_DISPLAYNAME.equals(vd.displayName())) {
				System.out.println("Found Tomcat process at pid: " + vd.id());
				return vd.id();
			}
		}

		System.err.println("Could not find Tomcat VM");
		return null;
	}
}
