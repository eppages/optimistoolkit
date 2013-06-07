/* $Id: UpdateVmStatusTest.java 5641 2012-04-02 13:55:29Z rkuebert $ */

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

public class UpdateVmStatusTest extends TestCase {

	/**
	 * Ensures that the service manager is empty before starting any test.
	 */
	public void setUp() {
		ServiceManager.getInstance().deleteServices();
	}
	
	public void testupdateVmStatus() {
		ServiceManager instance = ServiceManager.getInstance();
		ServiceDocument randomService = TestUtil.createIpNoVmService();
		String serviceId = randomService.getService().getServiceId();
		
		String infraProviderId = randomService.getService().getInfrastructureProviderArray()[0].getId();
		
		int size = instance.getServices().size();
		assertEquals(0, size);
		try {
			instance.addService(randomService);
		} catch (ItemAlreadyExistsException e) {
			fail();
		}
		size = instance.getServices().size();
		assertEquals(1, size);

		// Create VM, set status to lower case and *new* status to same string
		// in upper case.
		VmDocument vm = TestUtil.createVm();
		String vmId = vm.getVm().getId();
		vm.getVm().setStatus(vm.getVm().getStatus().toLowerCase());
		String newStatus = vm.getVm().getStatus().toUpperCase();
		
		try {
//			System.out.println(instance.addVm(serviceId, vmId));
			instance.addVm(serviceId, infraProviderId, vm);
		} catch (ItemNotFoundException e) {
			fail();
		} catch (ItemAlreadyExistsException e) {
			fail();
		}
		
		// Update status in Service Manager
		try {
			instance.updateVmStatus(serviceId, infraProviderId, vmId, newStatus);
		} catch (ItemNotFoundException e) {
			fail();
		}
		

		// Assert correct status
		try {
			assertEquals(newStatus, instance.getVmStatus(serviceId, infraProviderId, vmId));
		} catch (ItemNotFoundException e) {
			fail();
		}

	}
	
}
