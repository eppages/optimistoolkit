/* $Id: GetIpIdsTest.java 10698 2012-12-18 10:40:17Z django $ */

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
package eu.optimis.service_manager;

import junit.framework.TestCase;
import eu.optimis.serviceManager.InfrastructureProviderDocument;
import eu.optimis.serviceManager.ServiceDocument;
import eu.optimis.service_manager.core.ServiceManager;
import eu.optimis.service_manager.exception.ItemAlreadyExistsException;

public class GetIpIdsTest extends TestCase {

	/**
	 * Ensures that the service manager is empty before starting any test.
	 */
	public void setUp() {
		ServiceManager.getInstance().deleteServices();
	}
	
	public void testGetIpIds() {
		
		// Create a random full service and add it to the SM
		ServiceManager instance = ServiceManager.getInstance();
		ServiceDocument randomService = TestUtil.createFullService();
		String serviceId = randomService.getService().getServiceId();
		
		try {
			instance.addService(randomService);
		} catch (ItemAlreadyExistsException e) {
			fail();
		}

		// Create two more infrastructure providers and add them
		InfrastructureProviderDocument infrastructureProvider = TestUtil.createIpWithoutVm();
		try {
			instance.addIp(serviceId, infrastructureProvider);
		} catch (Exception e) {
			fail();
		}
		
		infrastructureProvider = TestUtil.createIpWithoutVm();
		try {
			instance.addIp(serviceId, infrastructureProvider);
		} catch (Exception e) {
			fail();
		}
		
		String[] infraProviderIds = null;
		try {
			String infraProviderIdsString = instance.getInfrastructureProviderIds(serviceId);
			infraProviderIds = infraProviderIdsString.split(";");
		} catch (Exception e) {
			fail();
		}
		assertEquals("Expected 3 infrastructure provider ids, got " + infraProviderIds.length, infraProviderIds.length, 3);
	}
	
	public void testGetIpIdsServiceWithNoIps() {

		// Create a random full service and add it to the SM
		ServiceManager instance = ServiceManager.getInstance();
		ServiceDocument randomService = TestUtil.createNoIpService();
		String serviceId = randomService.getService().getServiceId();
		
		try {
			instance.addService(randomService);
		} catch (ItemAlreadyExistsException e) {
			fail();
		}
		
		String[] infraProviderIds = null;
		try {
			String infraProviderIdsString = instance.getInfrastructureProviderIds(serviceId);
			assertEquals("", infraProviderIdsString);
			infraProviderIds = infraProviderIdsString.split(";");
			assertNotNull(infraProviderIds);
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
		
		
	}
}
