/* $Id: UpdateServiceTest.java 7308 2012-05-03 15:42:55Z tinghe $ */

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

import eu.optimis.serviceManager.ServiceDocument;
import eu.optimis.service_manager.core.ServiceManager;
import eu.optimis.service_manager.exception.ItemAlreadyExistsException;
import eu.optimis.service_manager.exception.ItemNotFoundException;
import junit.framework.TestCase;

public class UpdateServiceTest extends TestCase {

	/**
	 * Ensures that the service manager is empty before starting any test.
	 */
	public void setUp() {
		ServiceManager.getInstance().deleteServices();
	}
	
	public void testUpdateService() {
		ServiceManager instance = ServiceManager.getInstance();
		int size = instance.getServices().size();
		assertEquals(0, size);
		// Create original and updated service
		ServiceDocument originalService = TestUtil.createFullService();
		ServiceDocument updatedService = TestUtil.createFullService();
		// Updated service needs to have the same id as the original one 
		updatedService.getService().setServiceId(originalService.getService().getServiceId());
		try {
			instance.addService(originalService);
		} catch (ItemAlreadyExistsException e) {
			fail();
		}
		size = instance.getServices().size();
		assertEquals(1, size);
		
		// Update service
		try {
			instance.updateService(updatedService);
		} catch (ItemNotFoundException e) {
			fail();
		}
		
		// Get updated service from ServiceManager
		try {
			ServiceDocument service = instance.getService(originalService.getService().getServiceId());
			// Ensure that the fields were set
			assertEquals(updatedService.getService().getServiceId(), service.getService().getServiceId());
			assertEquals(updatedService.getService().getStatus(), service.getService().getStatus());
			assertEquals(updatedService.getService().getInfrastructureProviderArray()[0].getId(), service.getService().getInfrastructureProviderArray()[0].getId());
			assertEquals(updatedService.getService().getInfrastructureProviderArray()[0].getIpAddress(), service.getService().getInfrastructureProviderArray()[0].getIpAddress());

			// TODO Compare VMs as well
			
			
		} catch (ItemNotFoundException e) {
			fail();
		}
	}
	
}
