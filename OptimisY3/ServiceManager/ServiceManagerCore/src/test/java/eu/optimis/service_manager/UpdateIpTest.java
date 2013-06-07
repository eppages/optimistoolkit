/* $Id: UpdateIpTest.java 6199 2012-04-18 09:49:33Z rkuebert $ */

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

import eu.optimis.serviceManager.InfrastructureProviderDocument.InfrastructureProvider;
import eu.optimis.serviceManager.ServiceDocument;
import eu.optimis.service_manager.core.ServiceManager;
import eu.optimis.service_manager.exception.ItemAlreadyExistsException;
import eu.optimis.service_manager.exception.ItemNotFoundException;
import junit.framework.TestCase;

public class UpdateIpTest extends TestCase {

	public void setUp() {
		ServiceManager.getInstance().deleteServices();
	}
	
	/**
	 * Tests the update of an Infrastructure Provider functionality.
	 */
	public void testUpdateIp() {
		assertEquals(0, ServiceManager.getInstance().getServices().size());
		ServiceDocument service = TestUtil.createFullService();
		String infraProvId = service.getService().getInfrastructureProviderArray()[0].getId();
		try {
			ServiceManager.getInstance().addService(service);
		} catch (ItemAlreadyExistsException e) {
			fail();
		}
		assertEquals(1, ServiceManager.getInstance().getServices().size());
		
		InfrastructureProvider infrastructureProvider = TestUtil.createIpWithoutVm().getInfrastructureProvider();
		infrastructureProvider.setId(infraProvId);
		
		try {
			ServiceManager.getInstance().updateIp(service.getService().getServiceId(), infrastructureProvider);
		} catch (ItemNotFoundException e) {
			e.printStackTrace();
			fail();
		}
		
		// Still only 1 service
		assertEquals(1, ServiceManager.getInstance().getServices().size());
		
		// Check that the updated data is the same that we put
		try {
			service = ServiceManager.getInstance().getService(service.getService().getServiceId());
		} catch (ItemNotFoundException e) {
			e.printStackTrace();
			fail();
		}
		
		InfrastructureProvider ipFromService = service.getService().getInfrastructureProviderArray()[0];
		assertEquals(infrastructureProvider.getId(), ipFromService.getId());
		assertEquals(infrastructureProvider.getIpAddress(), ipFromService.getIpAddress());
		assertEquals(infrastructureProvider.getVms(), ipFromService.getVms());
	}
	
	
	/**
	 * Tests the update of an Infrastructure Provider (IP) for a service without an
	 * existing IP, which should fail.
	 */
	public void testUpdateServiceWithoutIp() {
		assertEquals(0, ServiceManager.getInstance().getServices().size());
		ServiceDocument service = TestUtil.createNoIpService();
		try {
			ServiceManager.getInstance().addService(service);
		} catch (ItemAlreadyExistsException e) {
			fail();
		}
		assertEquals(1, ServiceManager.getInstance().getServices().size());
		
		InfrastructureProvider infrastructureProvider = TestUtil.createIpWithoutVm().getInfrastructureProvider();
		
		try {
			ServiceManager.getInstance().updateIp(service.getService().getServiceId(), infrastructureProvider);
		} catch (ItemNotFoundException e) {
			return;
		}
		fail();

	}
	

}
