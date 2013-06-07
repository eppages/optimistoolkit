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
Oriol Collell, Francisco Javier Nieto. Atos Research and Innovation, Atos SPAIN SA
@email francisco.nieto@atosresearch.eu 

**/

package eu.optimis.test;

import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.util.List;
import java.util.Vector;

import junit.framework.TestCase;

import org.junit.After;
import org.junit.Before;

import eu.optimis.interopt.provider.Service;
import eu.optimis.interopt.provider.ServiceComponent;
import eu.optimis.interopt.provider.VMProperties;
import eu.optimis.interopt.provider.occi.OCCIClient;

public class ArsysClientTest extends TestCase
{

    //private static final String ARSYS_SYSTEM_URL = "https://optimis-ws.servidoresdns.net:8443/OptimisWebService/serviceManager/serviceManager.asmx";
    
    private static final String ARSYS_SYSTEM_URL =
            "http://optimis-ws.servidoresdns.net:8008/OptimisRestService.svc";

    private static final String SERVICE_ID = "optimis2013";
    
    private static String VM_IMAGE = "optimisWinHVM.w2k3.vmdk"; // work with fbf3767f-203a-4b69-a8f9-0463bb7d7678
    
    //private static final String SERVICE_ID = "DemoApp";
     
    //private static String VM_IMAGE = "optimisWinHVM_w2k3.vmdk";   // work with DemoApp

    /* (non-Javadoc)
      * @see junit.framework.TestCase#setUp()
      */
    @Before
    protected void setUp() throws Exception
    {
        super.setUp();
    }

    /* (non-Javadoc)
      * @see junit.framework.TestCase#tearDown()
      */
    @After
    protected void tearDown() throws Exception
    {
        super.tearDown();
    }

    /**
     * Tests the implementation of the Arsys client in order to deploy a new service, get monitoring
     * information for the service and finally terminate the service in the Arsys backend.
     */
    public void testDefaultAllocationWorkflow()
    {
        Authenticator.setDefault( new Authenticator()
        {
            @Override
            protected PasswordAuthentication getPasswordAuthentication()
            {
                return new PasswordAuthentication( "servicemanager", "opt1M1$12".toCharArray() );
            }
        } );


        // Firstly we create the service to deploy
        ServiceComponent serviceComponent = new ServiceComponent();

        serviceComponent.setArchitecture("x64");
        serviceComponent.setCores(1);
        serviceComponent.setMemory(2);
        serviceComponent.setSpeed(1995);
        serviceComponent.setImage(VM_IMAGE);  
        serviceComponent.setInstances(1);

        // Add the service to the list
        Vector<ServiceComponent> componentList = new Vector<ServiceComponent>();
        componentList.add( serviceComponent );

        //List<ServiceComponent> serviceComponents;
        //serviceComponents.add(service);

        boolean success = false;

        try
        {
            URL systemUrl = new URL( ARSYS_SYSTEM_URL );

            //
            // initialize arsys client
            //
            //VMManagementSystemClient vmManagementSystemClient =
            //    VMManagementClientFactory.createClient( systemUrl );
            OCCIClient vmManagementSystemClient = new OCCIClient();
            vmManagementSystemClient.setAuth( "servicemanager", "opt1M1$12" );
            vmManagementSystemClient.setUrl(ARSYS_SYSTEM_URL);

            // get VMs
            List<Service> myList = vmManagementSystemClient.getAllVMs();
            System.out.println ("VMs found for first service: " + myList.get(0).getVms().size());
            
            //
            // deploy service
            //            
            vmManagementSystemClient.deployService( SERVICE_ID, componentList, null );
            System.out.println( "service successfully deployed." );
            
            // delete VM
            vmManagementSystemClient.deleteVM(SERVICE_ID, 1);
            System.out.println ("VM for service " + SERVICE_ID + " was deleted");

//            //
//            // Check if service has been deployed and fetch service data
//            //
            List<VMProperties> vmProperties = vmManagementSystemClient.queryServiceProperties( SERVICE_ID );
           
            System.out.println( "Service data fetched: " + vmProperties.get(0).getHostname());
//            //
//            // terminate the deployed service
//            //
            vmManagementSystemClient.terminate( SERVICE_ID );
//
//            //
//            // acheck if service has been removed
//            //
            List<VMProperties> emptyVmProperties = vmManagementSystemClient.queryServiceProperties( SERVICE_ID );
            assertTrue( emptyVmProperties.size() == 0 );
            System.out.println( "Service succesfully removed" );
        }
        /*catch ( ServiceInstantiationException ex )
        {
            ex.printStackTrace();
            fail( ex.getMessage() );
        }*/
        catch ( Exception e )
        {
            e.printStackTrace();
            fail( e.getMessage() );
        }
    }
}
