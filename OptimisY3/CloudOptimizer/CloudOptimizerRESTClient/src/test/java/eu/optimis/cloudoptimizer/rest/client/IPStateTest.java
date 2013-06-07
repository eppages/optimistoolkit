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

package eu.optimis.cloudoptimizer.rest.client;

import java.util.List;
import junit.framework.TestCase;

/**
 *
 * @author fito
 */
public class IPStateTest extends TestCase {
    //CloudOptimizerRESTClient co = new CloudOptimizerRESTClient("109.231.120.19", 8080); // ip vm @ flex
    //CloudOptimizerRESTClient co = new CloudOptimizerRESTClient("130.239.48.6", 8080); // ip vm 2 @ umu
    CloudOptimizerRESTClient co = new CloudOptimizerRESTClient("192.168.252.56", 8080); //ip vm @ atos

    public IPStateTest(String testName) {
        super(testName);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testIPState_PhysicalAndVirtualDDBBs() {
        CloudOptimizerRESTClient co = new CloudOptimizerRESTClient("212.0.127.140");
        List<String> nodes = co.getInfrastructureNodesId();
        System.out.println("Nodes: ");
        for (String n : nodes) {
            System.out.println(n);
        }
        nodes = co.getNodesId();
        System.out.println("Active Nodes: ");
        for (String n : nodes) {
            System.out.println(n);
        }
        if(nodes.size() > 0) {
            System.out.println("Showing info for node " + nodes.get(0));
            System.out.println(co.getPhysicalResource(nodes.get(0)));
        }
        
        
        /*System.out.println("**************************************");
        System.out.println("********* IP STATE MANAGEMENT ********");
        System.out.println("**************************************");

        List<String> nodesId = co.getNodesId();
        assertEquals(2, nodesId.size());

        String id = co.getIpId();
        assertEquals("Error when getting IP identifier.", "atos", id);
        System.out.println("IP identifier = " + id);



        System.out.println("Adding physical resource...");
        String ret = co.addPhysicalResource(new String("<physical_resource><id>a</id><hostname>b</hostname>"
                + "<hypervisor>c</hypervisor><disk_size_in_gigabytes>1000</disk_size_in_gigabytes>"
                + "<cpu_cores>1</cpu_cores><memory_in_gigabytes>100</memory_in_gigabytes><os>g</os>"
                + "<network_adapter>h</network_adapter><public_ip_address>i</public_ip_address>"
                + "<private_ip_address>j</private_ip_address><infrastructure_provider_id>atos</infrastructure_provider_id></physical_resource>"));
        assertEquals("Error when adding a physical resource into database.", "1", ret);
        System.out.println("Obtaining all physical resources...");
        List<String> l = co.getNodesId();
        System.out.println(l.size() + " node obtained.");
        System.out.println("Obtaining physical resource...");
        String b = co.getPhysicalResource("a");
        //System.out.println("Physical resource = " + b);
        System.out.println("Deleting physical resource...");
        co.deletePhysicalResource("a");



        System.out.println("Adding virtual resource...");
        co.addVirtualResource(new String("<virtual_resource><id>a</id><hostname>b</hostname>"
                + "<physical_resource_id>a</physical_resource_id><service_id>DemoApp</service_id><type>base</type><hypervisor>c</hypervisor>"
                + "<disk_size_in_gigabytes>1000</disk_size_in_gigabytes><cpu_cores>1</cpu_cores>"
                + "<memory_in_gigabytes>100</memory_in_gigabytes><os>g</os><network_adapter>h</network_adapter>"
                + "<public_ip_address>i</public_ip_address><private_ip_address>j</private_ip_address></virtual_resource>"));

        System.out.println("Obtaining virtual resource...");
        String aux = co.getVirtualResource("a");
        //System.out.println("VM obtained = " + aux);
        System.out.println("Obtaining VMs id...");
        List<String> l2 = co.getVMsId("a");
        System.out.println(l2.size() + " vms in the node");
        String nodeId = co.getNodeId("a");
        System.out.println("VM running in node = " + nodeId);
        co.updateVirtualResource("a", "new");
        System.out.println("Updating the node where this VM is running...");
        nodeId = co.getNodeId("a");
        System.out.println("VM now running in node = " + nodeId);
        l2 = co.getVMsId("a");
        System.out.println(l2.size() + " vms in the old node");
        System.out.println("Deleting virtual resource...");
        co.deleteVirtualResource("a");
        */
    }
}
