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

package eu.optimis.cloudoptimizer.xml.test;

import eu.optimis.cloudoptimizer.xml.PhysicalResource;
import eu.optimis.cloudoptimizer.xml.XmlUtil;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author mmacias
 */
public class PhysicalResourceTest {
    
    public PhysicalResourceTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }
    // TODO add test methods here.
    // The methods must be annotated with annotation @Test. For example:
    //
    // @Test
    // public void hello() {}
    @Test
    public void testParsing() {
        String xml = "<physical_resource><id>optimis1</id>"
                + "<hostname>optimis1</hostname>"
                + "<hypervisor>xen</hypervisor>"
                + "<disk_size_in_gigabytes>1000</disk_size_in_gigabytes>"
                + "<cpu_cores>8</cpu_cores>"
                + "<memory_in_gigabytes>16</memory_in_gigabytes>"
                + "<os>CentOS</os>"
                + "<network_adapter>eth0</network_adapter>"
                + "<public_ip_address>212.0.127.153</public_ip_address>"
                + "<private_ip_address>192.168.252.36</private_ip_address>"
                + "<infrastructure_provider_id>fackyou</infrastructure_provider_id>"
                + "<active>1</active></physical_resource>";
        
        PhysicalResource pr = XmlUtil.getPhysicalResourceFromXml(xml);
        assertEquals(pr.getId(), "optimis1");
        assertEquals(pr.getHypervisor(),"xen");
        assertEquals(pr.isActive(), true);
    }
}
