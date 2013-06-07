/* $Id: AddVmTest.java 5641 2012-04-02 13:55:29Z rkuebert $ */

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
import eu.optimis.serviceManager.ServiceDocument;
import eu.optimis.serviceManager.VmDocument;
import eu.optimis.service_manager.core.ServiceManager;
import eu.optimis.service_manager.exception.ItemAlreadyExistsException;
import eu.optimis.service_manager.exception.ItemNotFoundException;

public class AddVmTest  extends TestCase {

	/**
	 * Ensures that the service manager is empty before starting any test.
	 */
	public void setUp() {
		ServiceManager.getInstance().deleteServices();
	}
	
	public void testAddVm() {
		ServiceManager instance = ServiceManager.getInstance();
		ServiceDocument randomService = TestUtil.createFullService();
		int size = instance.getServices().size();
		assertEquals(0, size);
		try {
			instance.addService(randomService);
		} catch (ItemAlreadyExistsException e) {
			fail();
		}
		size = instance.getServices().size();
		assertEquals(1, size);
		
		String infraProviderIp = randomService.getService().getInfrastructureProviderArray()[0].getId();
		
		try {
//			System.out.println(instance.addVm(randomService.getService().getServiceId(), vmId));
			instance.addVm(randomService.getService().getServiceId(), infraProviderIp, TestUtil.createVm());
		} catch (ItemNotFoundException e) {
			fail();
		} catch (ItemAlreadyExistsException e) {
			fail();
		}
	}
	
	public void testAddDuplicateVm() {
		ServiceManager instance = ServiceManager.getInstance();
		ServiceDocument randomService = TestUtil.createFullService();
		int size = instance.getServices().size();
		assertEquals(0, size);
		try {
			instance.addService(randomService);
		} catch (ItemAlreadyExistsException e) {
			fail();
		}
		size = instance.getServices().size();
		assertEquals(1, size);

		VmDocument vm = TestUtil.createVm();
		
		String infraProviderIp = randomService.getService().getInfrastructureProviderArray()[0].getId();
		
		// Adding once - should succeed
		try {
			instance.addVm(randomService.getService().getServiceId(), infraProviderIp, vm);
		} catch (ItemNotFoundException e) {
			fail();
		} catch (ItemAlreadyExistsException e) {
			fail();
		}
		
		// Add duplicate - should fail
		try {
			System.out.println(instance.addVm(randomService.getService().getServiceId(),infraProviderIp, vm));
		} catch (ItemNotFoundException e) {
			fail();
		} catch (ItemAlreadyExistsException e) {
			return;
		}

		fail("Adding a duplicate VM should fail");
	}
}
