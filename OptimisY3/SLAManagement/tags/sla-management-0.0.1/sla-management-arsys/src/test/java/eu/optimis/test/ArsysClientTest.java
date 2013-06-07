/**
 *
 */
package eu.optimis.test;

import eu.optimis.interopt.provider.ServiceComponent;
import eu.optimis.interopt.provider.ServiceInstantiationException;
import eu.optimis.interopt.provider.VMProperties;
import eu.optimis.interopt.provider.arsys.ArsysClient;
import junit.framework.TestCase;
import org.junit.After;
import org.junit.Before;

import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.net.UnknownServiceException;
import java.util.List;
import java.util.Vector;

/**
 * @author csalcedo
 */
public class ArsysClientTest extends TestCase
{

    private static final String ARSYS_SYSTEM_URL =
            "https://optimis-ws.servidoresdns.net:8443/OptimisWebService/serviceManager/serviceManager.asmx";

    private static final String SERVICE_ID = "fbf3767f-203a-4b69-a8f9-0463bb7d7678";
    
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

        serviceComponent.put( ServiceComponent.OCCI_COMPUTE_ARCHITECTURE, "x32" );
        serviceComponent.put( ServiceComponent.OCCI_COMPUTE_CORES, "1" );
        serviceComponent.put( ServiceComponent.OCCI_COMPUTE_MEMORY, "2048" );
        serviceComponent.put( ServiceComponent.OCCI_COMPUTE_SPEED, "1995" );
        serviceComponent.put( ServiceComponent.OPTIMIS_VM_IMAGE, VM_IMAGE );  
        serviceComponent.put( ServiceComponent.OPTIMIS_VM_INSTANCES, "1" );

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
            ArsysClient vmManagementSystemClient = new ArsysClient();
            vmManagementSystemClient.setAuth( "servicemanager", "opt1M1$12" );

            //
            // deploy service
            //
            vmManagementSystemClient.deployService( SERVICE_ID, componentList, null );
            System.out.println( "service successfully deployed." );

//            //
//            // Check if service has been deployed and fetch service data
//            //
//            List<VMProperties> vmProperties =
//                    vmManagementSystemClient.queryServiceProperties( SERVICE_ID );
//            assertTrue( vmProperties.size() == 1 );
//            System.out.println( "Service data fetched" );
//            //
//            // terminate the deployed service
//            //
//            vmManagementSystemClient.terminate( SERVICE_ID );
//
//            //
//            // acheck if service has been removed
//            //
//            List<VMProperties> emptyVmProperties =
//                    vmManagementSystemClient.queryServiceProperties( SERVICE_ID );
//            assertTrue( emptyVmProperties.size() == 0 );
//            System.out.println( "Service succesfully removed" );
        }
        catch ( ServiceInstantiationException ex )
        {
            ex.printStackTrace();
            fail( ex.getMessage() );
        }
        catch ( Exception e )
        {
            e.printStackTrace();
            fail( e.getMessage() );
        }
    }
}
