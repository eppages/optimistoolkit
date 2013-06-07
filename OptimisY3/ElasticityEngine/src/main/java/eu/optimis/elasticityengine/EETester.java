package eu.optimis.elasticityengine;

import java.io.IOException;

import org.apache.commons.beanutils.converters.StringArrayConverter;

import org.apache.log4j.Logger;
import eu.optimis.elasticityengine.callback.CallbackPrinter;
import eu.optimis.elasticityengine.monitoring.FakeMonitoringSource;
import eu.optimis.elasticityengine.monitoring.RESTMonitoringSource;
import eu.optimis.elasticityengine.sc.Util;

/**
 * Test class for the EE, used only during development.
 * 
 * @author Daniel Espling (<a href="mailto:espling@cs.umu.se">espling@cs.umu.se</a>)
 * Copyright (C) 2012 Umeå University
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
public class EETester {

    private static void printUsage() {
        System.out.println("Expecting exacly one argument, the full path to the service manifest.");
    }

    public static void main(String[] args) throws IOException, InterruptedException {

        if (args.length != 1) {
            printUsage();
            System.exit(-1);
        }
        
        Logger log = Logger.getLogger(ElasticityEngineImpl.class);
        System.out.println("Testing Elasticity Engine");
        System.out.println("One instance per 100 users expected, starting at 1");

        ElasticityEngine eEngine = new ElasticityEngineImpl();
        String manifest = Util.getManifest(args[0]);
        String serviceID = "EETester"; // TODO parse somehow?
        String sp="SP_Add";
        boolean LowRiskMode;
        

        ElasticityCallback callback = new CallbackPrinter();
        if ("true".equals(System.getProperty("lowrisk"))) {
            System.out.println("USING Low Risk Mode");
            LowRiskMode = true;
        } else {
        	System.out.println("USING Low cost Mode");
            LowRiskMode = false;
        }
        eEngine.startElasticity(serviceID, manifest,LowRiskMode,sp);
        
        //Call a test-specific method which only returns when we got the -2 recommendation, and the test should then be done.
        ((CallbackPrinter)callback).awaitLoad(1);
        System.out.println("\nTest done, exiting");
        Thread.sleep(500);    }
    
    /* -- JMX stuff, not currently used --
    
    MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();

    // Unique identification of MBeans
    ObjectName eeName = null;

    try {
        // Uniquely identify the MBeans and register them with the platform
        // MBeanServer
        eeName = new ObjectName("eu.optimis:name=ElasticityEngine");
        mbs.registerMBean(eEngine, eeName);
    } catch (Exception e) {
        e.printStackTrace();
    }*/

}
