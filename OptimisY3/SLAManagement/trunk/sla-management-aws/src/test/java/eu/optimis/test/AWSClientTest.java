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

import com.amazonaws.services.ec2.AmazonEC2;
import eu.optimis.interopt.provider.ServiceComponent;
import eu.optimis.interopt.provider.ServiceInstantiationException;
import eu.optimis.interopt.provider.VMManagementClientFactory;
import eu.optimis.interopt.provider.VMManagementSystemClient;
import eu.optimis.interopt.provider.VMProperties;
import eu.optimis.interopt.provider.aws.AmazonClient;
import junit.framework.TestCase;
import org.junit.After;
import org.junit.Before;

import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.net.UnknownServiceException;
import java.util.List;
import java.util.Vector;
import org.junit.Ignore;

public class AWSClientTest extends TestCase
{

    private static String AWS_PUBLIC_KEY = "AKIAJMVELIFD3ZSVY2HQ";
    private static String AWS_SECRET_KEY = "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";
    private static String SERVICE_ID = "d3f64f3f-c57b-460c-97d4-9b84c576c397"; //Random service id for testing
    private static String VM_IMAGE = "ami-937474e7";

    @Before
    protected void setUp() throws Exception
    {
        super.setUp();
    }

    @After
    protected void tearDown() throws Exception
    {
        super.tearDown();
    }

    /**
     * Tests the implementation of the AWS client in order to deploy a new service, get monitoring
     * information for the service and finally terminate the service in the AWS backend.
     */
    @Ignore
    public void testDefaultAllocationWorkflow()
    {
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
    	
    	//VMManagementSystemClient vmManagementSystemClient = VMManagementClientFactory.createClient();
    	VMManagementSystemClient vmManagementSystemClient = new AmazonClient();
    	vmManagementSystemClient.setAuth(AWS_PUBLIC_KEY, AWS_SECRET_KEY);
    	try
    	{
    		//List<VMProperties> myList = vmManagementSystemClient.queryServiceProperties("d3f64f3f-c57b-460c-97d4-9b84c576c397");
    		//System.out.println("Number of VMs found: " + myList.size());
    		//vmManagementSystemClient.terminate("d3f64f3f-c57b-460c-97d4-9b84c576c397");
    		//System.out.println ("Service d3f64f3f-c57b-460c-97d4-9b84c576c397 terminated!");
    		vmManagementSystemClient.deployService("d3f64f3f-c57b-460c-97d4-9b84c576c397", componentList, null);
    	}
    	catch (Exception ex)
    	{
    		System.out.println("Failed!!");
    		ex.printStackTrace();
    	}
    	
    }
}
