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
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.openmbean.CompositeData;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import javax.ws.rs.core.Response;
import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.tools.attach.VirtualMachine;
import com.sun.tools.attach.VirtualMachineDescriptor;

public class JBossJMXProbe implements Runnable {

    private static final String CONNECTOR_ADDRESS = "com.sun.management.jmxremote.localConnectorAddress";
    private static final String JBOSS_DISPLAYNAME = "org.jboss.Main";
    private String montoringEndpoint;
    private String instanceID;
    private String serviceID;
    private JMXConnector connector;
    private MBeanServerConnection remote;
    private ScheduledFuture<?> future;

    private static List<MBeanQuery> mbeanQueries;

    static {
        mbeanQueries = new ArrayList<MBeanQuery>();

        /*
         * mbeanQueries.add(new
         * MBeanQuery("jboss.jca:service=ManagedConnectionPool,name=DefaultDS",
         * "MaxSize", "Maximum amount of user threads")); mbeanQueries.add(new
         * MBeanQuery("jboss.jca:service=ManagedConnectionPool,name=DefaultDS",
         * "InUseConnectionCount", "Current amount of active user-threads"));
         * mbeanQueries.add(new MBeanQuery("jboss.system:type=ServerInfo",
         * "TotalMemory", "Total consumed memory inside the container"));
         * mbeanQueries.add(new MBeanQuery("jboss.system:type=ServerInfo",
         * "ActiveThreadCount", "Current amount of threads in the JBoss JVM"));
         * mbeanQueries.add(new MBeanQuery("jboss.system:type=ServerInfo",
         * "MaxMemory", "Maximum memory allocated to JBoss"));
         */
        //
        // mbeanQueries.add(new
        // MBeanQuery("jboss.web:type=ThreadPool,name=http-127.0.0.1-8080",
        // "currentThreadCount", "Current threads in Jboss-web"));
        // mbeanQueries.add(new
        // MBeanQuery("jboss.web:type=ThreadPool,name=http-127.0.0.1-8080",
        // "currentThreadsBusy", "Current busy threads in Jboss-web"));

        // mbeanQueries.add(new MBeanQuery("java.lang:type=Memory",
        // "HeapMemoryUsage",
        // "Current Heap memory usage"));
        // mbeanQueries.add(new
        // MBeanQuery("jboss.web:type=Manager,path=/,host=localhost",
        // "activeSessions",
        // "activeSessions"));
        mbeanQueries.add(new MBeanQuery("java.lang:type=Threading", "ThreadCount",
                "Current amount of JVM threads"));
        // mbeanQueries.add(new MBeanQuery("java.lang:type=OperatingSystem",
        // "SystemLoadAverage",
        // "Current system load"));
    }

    public JBossJMXProbe(JMXServiceURL serviceURL, String monitoringEndpoint, String serviceID,
            String instanceID) throws IOException {

        this.montoringEndpoint = monitoringEndpoint;
        this.serviceID = serviceID;
        this.instanceID = instanceID;

        // Connect to target (assuming no security)
        this.connector = JMXConnectorFactory.connect(serviceURL);

        // Get an MBeanServerConnection on the remote VM.
        this.remote = connector.getMBeanServerConnection();
    }

    public void startMonitoring(long measuringPeriod, long measuringInterval) {
        final ScheduledExecutorService ex = Executors.newScheduledThreadPool(1);
        future = ex.scheduleAtFixedRate(this, 0, measuringInterval, TimeUnit.SECONDS);

        System.out.println("Sending measurement every " + measuringInterval + " seconds.");

        // Start another thread to cancel the first one if the interval is
        // limited (<1)
        if (measuringPeriod > 1) {
            ex.schedule(new Runnable() {
                @Override
                public void run() {
                    System.out.println("Stopping monitoring");
                    future.cancel(false);
                    ex.shutdown();
                }
            }, measuringPeriod, TimeUnit.SECONDS);
            System.out.println("Stopping monitoring in: " + measuringPeriod + " seconds.");
        }

    }

    public void run() {

        Map<MBeanQuery, String> resultMap = new HashMap<MBeanQuery, String>();
            for (MBeanQuery query : mbeanQueries) {
            
        try {

                ObjectName oName = new ObjectName(query.serviceName);
                Object resultObj = remote.getAttribute(oName, query.attributeName);
                System.out.println("Result of " + query.attributeName + ": " + resultObj);

                // TODO: Special case for Heap
                if (resultObj instanceof CompositeData) {
                    CompositeData result = (CompositeData) resultObj;
                    Long usedHeap = (Long) result.get("used");
                    System.out.println("Parsed heap to usedHeap: " + usedHeap);
                    resultMap.put(query, usedHeap + "");
                } else if (resultObj instanceof Long) {
                    resultMap.put(query, resultObj + "");
                } else if (resultObj instanceof Integer) {
                    int value = ((Integer) resultObj).intValue();
                    resultMap.put(query, value + "");
                } else {
                    System.out.println("Unknown resultObj for attribute: " + query.attributeName
                            + ", sending whatever value as String");
                    resultMap.put(query, resultObj.toString());
                }

        } catch (Throwable t) {
            System.err.println("Measuringprocedure failed.");
                System.out.println("Error!");
            t.printStackTrace();
        }
            }
            for (MBeanQuery query : resultMap.keySet()) {
            String result = resultMap.get(query);
                try {
                publishResult(serviceID, instanceID, query.attributeName, result,
                        System.currentTimeMillis());
                } catch (Exception e) {
                    System.err.println("Monitoringcommunication failed.");
                    e.printStackTrace();
                } 
            }

    }

    private void publishResult(String serviceID, String instanceID, String name, String data, long timestamp)
            throws IOException, ParserConfigurationException, SAXException {
        System.out.println("Inside publishresult");
        Client client = Client.create();

        System.out.println("Client created");
        String extendedEndpoint = this.montoringEndpoint + serviceID;
        WebResource webResource = client.resource(extendedEndpoint);
        System.out.println("Resource created");

        Measurement measurement = new Measurement(serviceID, instanceID, name, data, timestamp);
        System.out.println("Sending the POST");
        ClientResponse response = webResource.post(ClientResponse.class, measurement);
        boolean success = response.getClientResponseStatus().getFamily()
                .equals(Response.Status.Family.SUCCESSFUL);

        if (success) {
            System.out.println("Result for: " + name + " sent to server");
        } else {
            System.err.println("Failed to send to server: " + response);
        }

    }

    /*
     * Static startup methods starts here
     */

    private static final String CONFIG_FILENAME = "jbossprobe.properties";

    // Keys to read from configuration
    private static final String MONITORING_ENDPOINT_KEY = "monitoringEndpoint";
    private static final String MEASURING_INTERVAL_KEY = "measuringInterval";
    private static final String MEASURING_PERIOD_KEY = "measuringPeriod";
    private static final String CONTEXTUALIZATION_FILEPATH_KEY = "contextualizationFilepath";

    // Fields to read from contextualization
    private static final String SERVICE_ID_KEY = "serviceID";
    private static final String INSTANCE_ID_KEY = "instanceID";

    public static void main(String[] args) throws Exception {

        String pid = getJBossPID();

        if (pid == null) {
            System.err.println("Could not find running JBoss VM");
            System.exit(-1);
        }

        // Read properties from current working directory
        String currentDir = System.getProperty("user.dir");
        Properties props = readProperties(currentDir + File.separator + CONFIG_FILENAME);
        if (props == null) {
            System.err.println("Failed to read config file, exiting.");
            System.exit(-1);
        }

        // Read monitoringEndpoint
        String monitoringEndpoint = props.getProperty(MONITORING_ENDPOINT_KEY);
        if (monitoringEndpoint == null) {
            System.err.println("monitoringEndpoint could not be read from config, exiting.");
            System.exit(-1);
        }

        // Read measuringInterval
        String measuringIntervalStr = props.getProperty(MEASURING_INTERVAL_KEY);
        if (measuringIntervalStr == null) {
            System.err.println("measuringInterval could not be read from config, exiting.");
            System.exit(-1);
        }

        // Convert to Long
        long measuringInterval;
        try {
            measuringInterval = Long.valueOf(measuringIntervalStr);
        } catch (NumberFormatException e) {
            System.err.println("measuringInterval was not a number, exiting.");
            measuringInterval = -1;
            System.exit(-1);
        }

        // Read measuringInterval
        String measuringPeriodStr = props.getProperty(MEASURING_PERIOD_KEY);
        if (measuringPeriodStr == null) {
            System.err.println("measuringPeriod could not be read from config, exiting.");
            System.exit(-1);
        }

        // Convert to Long
        long measuringPeriod;
        try {
            measuringPeriod = Long.valueOf(measuringPeriodStr);
        } catch (NumberFormatException e) {
            System.err.println("measuringPeriod was not a number, exiting.");
            measuringPeriod = -1;
            System.exit(-1);
        }

        // Read contextualizationfilePath
        String contextualizationFile = props.getProperty(CONTEXTUALIZATION_FILEPATH_KEY);
        if (contextualizationFile == null) {
            System.err.println("contextualizationFile could not be read from config, exiting.");
            System.exit(-1);
        }

        System.out.println("Reading contextualization from file: " + contextualizationFile);

        // Read contextualization file as properties
        Properties contextProps = readProperties(contextualizationFile);

        // Read ServiceID from context
        String serviceID = contextProps.getProperty(SERVICE_ID_KEY);
        if (serviceID == null) {
            System.err.println("serviceID could not be read from contextualization, exiting.");
            System.exit(-1);
        }

        // Read InstanceID from context
        String instanceID = contextProps.getProperty(INSTANCE_ID_KEY);
        if (instanceID == null) {
            System.err.println("instanceID could not be read from contextualization, exiting.");
            System.exit(-1);
        }

        // Read URL to JMX process
        JMXServiceURL url = getURLForPid(pid);

        JBossJMXProbe jbProbe = new JBossJMXProbe(url, monitoringEndpoint, serviceID, instanceID);
        jbProbe.startMonitoring(measuringPeriod, measuringInterval);
    }

    private static Properties readProperties(String configFilename) {
        File propFile = new File(configFilename);
        if (!propFile.exists()) {
            System.err.println("Could not find property file: " + configFilename);
            return null;
        }

        Properties props = new Properties();
        try {
            props.load(new FileReader(propFile));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            System.err.println("failed to read from file: " + configFilename);
            return null;
        }
        return props;
    }

    private static String getJBossPID() {

        for (VirtualMachineDescriptor vd : VirtualMachine.list()) {
            System.out.println("Found vm: '" + vd.displayName() + "'");
            System.out.println("Looking for prefix: " + JBOSS_DISPLAYNAME);
            if (vd.displayName().contains(JBOSS_DISPLAYNAME)) {
                System.out.println("Found JBoss process at pid: " + vd.id());
                return vd.id();
            }
        }

        System.err.println("Could not find JBoss VM");
        return null;
    }

    private static JMXServiceURL getURLForPid(String pid) throws Exception {

        // attach to the target application
        final VirtualMachine vm = VirtualMachine.attach(pid);

        // get the connector address
        String connectorAddress = vm.getAgentProperties().getProperty(CONNECTOR_ADDRESS);

        // no connector address, so we start the JMX agent
        if (connectorAddress == null) {
            String agent = vm.getSystemProperties().getProperty("java.home") + File.separator + "lib"
                    + File.separator + "management-agent.jar";
            vm.loadAgent(agent);

            // agent is started, get the connector address
            connectorAddress = vm.getAgentProperties().getProperty(CONNECTOR_ADDRESS);
            assert connectorAddress != null;
        }
        return new JMXServiceURL(connectorAddress);
    }
}
