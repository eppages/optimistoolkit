package eu.optimis.service_manager;

/*
   Copyright 2012 University of Stuttgart

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
*/

//import java.util.logging.Logger;

import junit.framework.TestCase;

import org.apache.xmlbeans.XmlException;

import eu.optimis.serviceManager.ServiceDocument;

/**
 * Test case for creating a service in the service
 * manager.
 * 
 * @author roland
 *
 */
public class CreateServiceFromStringTest extends TestCase implements ServiceManagerTest {
	
	private static String FULL_SERVICE_XML =
			"<service xmlns=\"http://www.optimis.eu/service-manager\">" +
					"<service_id>dc091234-ae6e-44ff-af55-0c84095de811</service_id>" +
					"<sla_id>3c840218-9273-463b-bb96-69c15b95842c</sla_id>" +
					"<status>pending</status>" +
					"<infrastructure-provider>" +
						"<id>b15f2e08-2b86-42d6-97a0-673c90d737e3</id>" +
						"<vms>" +
							"<vm>" +
								"<id>5ef8cf5b-f969-4eea-ac64-f24896770ea2</id>" +
								"<type>TypeA</type>" +
								"<status>pending</status>" +
							"</vm>" +
						"</vms>" +
					"</infrastructure-provider>" +
				"</service>";

	private static String NO_VM_SERVICE_XML =
			"<service xmlns=\"http://www.optimis.eu/service-manager\">" +
					"<service_id>dc091234-ae6e-44ff-af55-0c84095de811</service_id>" +
					"<sla_id>3c840218-9273-463b-bb96-69c15b95842c</sla_id>" +
					"<status>pending</status>" +
					"<infrastructure-provider>" +
						"<id>b15f2e08-2b86-42d6-97a0-673c90d737e3</id>" +
					"</infrastructure-provider>" +
				"</service>";
	
	private static String VM_ONLY_ID_XML =
			"<service xmlns=\"http://www.optimis.eu/service-manager\">" +
					"<service_id>dc091234-ae6e-44ff-af55-0c84095de811</service_id>" +
					"<sla_id>3c840218-9273-463b-bb96-69c15b95842c</sla_id>" +
					"<status>pending</status>" +
					"<infrastructure-provider>" +
						"<id>b15f2e08-2b86-42d6-97a0-673c90d737e3</id>" +
						"<vms>" +
							"<vm>" +
								"<id>5ef8cf5b-f969-4eea-ac64-f24896770ea2</id>" +
							"</vm>" +
						"</vms>" +
					"</infrastructure-provider>" +
				"</service>";
	
	private static String MINIMAL_XML =
			"<service xmlns=\"http://www.optimis.eu/service-manager\">" +
					"<service_id>dc091234-ae6e-44ff-af55-0c84095de811</service_id>" +
				"</service>";
	
	public void testCreateFullService() {
		try {
			ServiceDocument.Factory.parse(FULL_SERVICE_XML);
		} catch (XmlException xmlException) {
			xmlException.printStackTrace();
			fail("Exception parsing service: " + xmlException.getMessage());
		}
	}
	
	public void testCreateNoVmService() {
		try {
			ServiceDocument.Factory.parse(NO_VM_SERVICE_XML);
		} catch (XmlException xmlException) {
			xmlException.printStackTrace();
			fail("Exception parsing service: " + xmlException.getMessage());
		}
	}
	
	public void testCreateOnlyVmIdService() {
		try {
			ServiceDocument.Factory.parse(VM_ONLY_ID_XML);
		} catch (XmlException xmlException) {
			xmlException.printStackTrace();
			fail("Exception parsing service: " + xmlException.getMessage());
		}
	}
	
	public void testCreateMinimalService() {
		try {
			ServiceDocument.Factory.parse(MINIMAL_XML);
		} catch (XmlException xmlException) {
			xmlException.printStackTrace();
			fail("Exception parsing service: " + xmlException.getMessage());
		}
	}
	
	public static void main(String args[]) {
		System.out.println(MINIMAL_XML);
	}
	
}
