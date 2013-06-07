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

import eu.optimis.cloudoptimizer.persistence.Queries;
import eu.optimis.manifest.api.ip.Manifest;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.sql.Connection;
import java.sql.DriverManager;

import junit.framework.TestCase;

/**
 *
 * @author fito
 */
public class DeploymentTest extends TestCase {
    //CloudOptimizerRESTClient co = new CloudOptimizerRESTClient("109.231.120.19", 8080); // ip vm @ flex
    //CloudOptimizerRESTClient co = new CloudOptimizerRESTClient("130.239.48.6", 8080); // ip vm 2 @ umu
    //CloudOptimizerRESTClient co = new CloudOptimizerRESTClient("192.168.252.56", 8080); //ip vm @ atos
    
    public DeploymentTest(String testName) {
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

    public void testIPDeployment() {
        CloudOptimizerRESTClient co = new CloudOptimizerRESTClient("flexe");
        co.addVM("optimis2013B","<xml></xml>","image.img",1,"localhost","bursting");

    }
        /*System.out.println("**************************************");
        System.out.println("************* DEPLOYMENT *************");
        System.out.println("**************************************");
        CloudOptimizerRESTClient co = new CloudOptimizerRESTClient("212.0.127.140", 8080); //ip vm @ atos
        try {
            InputStream file = getClass().getResourceAsStream("/IpManifest.xml");
            //FileInputStream file = new FileInputStream("/home/optimis/SVN/optimis/branches/OptimisY2/CloudOptimizer/CloudOptimizerREST/src/main/webapp/WEB-INF/PM_IPmanifest.xml");
            //FileInputStream file = new FileInputStream("/home/optimis/SVN/optimis/branches/OptimisY2/CloudOptimizer/CloudOptimizerREST/src/main/webapp/WEB-INF/DummySPManifest_1VM_noISO_umea.xml");
            byte[] b = new byte[file.available()];
            file.read(b);
            file.close();
            String SPmanifest = new String(b);

            Manifest ipManifest = Manifest.Factory.newInstance(SPmanifest);
            ipManifest.initializeIncarnatedVirtualMachineComponents();
            System.out.println(ipManifest.getErrors());

            System.out.println("Deploying a new service...");
            String pf = co.deploy(ipManifest.toString(), "slaId");
            assertEquals("Problem when deploying the service.", "DemoApp", pf);
            if (pf.equals("DemoApp")) {
                System.out.println("Service deployed!");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        */

    /*public void testInsertManifestInDB() {
        try {
            InputStream file = getClass().getResourceAsStream("/IpManifest.xml");
            InputStreamReader isr = new InputStreamReader(file);
            char[] txt = new char[file.available()];
            isr.read(txt);
            String xml = new String(txt);

            Class.forName("com.mysql.jdbc.Driver");
            Connection conn = DriverManager.getConnection("jdbc:mysql://optimis-database.atosorigin.es:3306/iptrecdb", "CO_MGR", "");
            Queries.insertManifest(conn,"test"+System.currentTimeMillis(), xml);
            conn.close();

        } catch(Exception e) {
            e.printStackTrace();
        }
    }    */

}
