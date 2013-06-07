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
Juan Luis Prieto, Francisco Javier Nieto. Atos Research and Innovation, Atos SPAIN SA
@email francisco.nieto@atosresearch.eu 

**/

package eu.optimis.tf;

import java.util.logging.Logger;

import junit.framework.TestCase;
import eu.optimis.tf.clients.TrustFrameworkSPClient;

/**
 * Unit test for simple App.
 */
public class SNClientTest extends TestCase {
	
	Logger log = Logger.getLogger(this.getClass().getName());
	
	String host = "127.0.0.1";
	int port = 8000;
 
	private static final String userName = "juanlu";
	private static final String userName2 = "david";
	private static final String userType = "IP";
	private static final String userType2 = "SP";
	private static final Double expectation = 4.2;
	private static final Double expectation2 = 3.5;
	private static final Double or1 = 3.2;
	private static final Double or2 = 2.4;
	
	private static final Double belief1 = 3.2;
	private static final Double belief2 = 2.4;
	private static final Double disbelief1 = 3.2;
	private static final Double disbelief2 = 2.4;
	private static final Double uncertinty1 = 3.2;
	private static final Double uncertinty2 = 2.4;
	private static final Double relativeAutomicity1 = 3.2;
	private static final Double relativeAutomicity2 = 2.4;
	
	private TrustFrameworkSPClient tfsnclient;

	/**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public SNClientTest()
    {
        tfsnclient = new TrustFrameworkSPClient(host, port);
    }
    
    public void testClientSN(){
//    	addEntityTrust(userName, userType, expectation, belief1, disbelief1, uncertinty1, relativeAutomicity1);
//    	addEntityTrust(userName2, userType2, expectation2, belief2, disbelief2, uncertinty2, relativeAutomicity2);
//    	getEntityTrust(userName, userType);
//    	updateEntityTrust(userName, userType, expectation2, belief2, disbelief2, uncertinty2, relativeAutomicity2);
//    	getEntityTrust(userName,userType);
//    	addRelationship();
    	getRelationshipTrust("OPTIMUMWEB", "SP", "OPTIMUMWEB", "IP");
//    	updateRelationship();
//    	getRelationshipTrust(userName, userType, userName2, userType2);
    }
    
    private void addEntityTrust(String entityId, String entityType, Double expectation, Double belief, Double disbelief, Double uncertinty, Double relativeAutomicity){
//    	log.info(String.valueOf(tfsnclient.addEntity(entityId, entityType, expectation, belief, disbelief, uncertinty, relativeAutomicity)));
    }
    private void getEntityTrust(String entityId, String entityType){
//    	log.info(tfsnclient.getEntityTrust(entityId, entityType));
    }
    
	private void updateEntityTrust(String user, String userType, double expectation, double belief, double disbelief, double uncertinty, double relativeAutomicity){
//    	log.info(String.valueOf(tfsnclient.updateEntityTrust(user, userType, expectation, belief, disbelief, uncertinty, relativeAutomicity)));
    }
	
	private void addRelationship(){
//		log.info(String.valueOf(tfsnclient.addRelationship(userName, userType, userName2, userType2, expectation, belief1, disbelief1, uncertinty1, relativeAutomicity1)));
	}
	
	private void updateRelationship(){
//		log.info(String.valueOf(tfsnclient.updateRelationshipTrust(userName, userType, userName2, userType2, expectation2, belief2, disbelief2, uncertinty2, relativeAutomicity2)));
	}
	
	private void getRelationshipTrust(String user, String userType, String user2, String userType2){
//		log.info(tfsnclient.getRelationshipTrust(user, userType, user2, userType2));
	}
}
