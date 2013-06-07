/* $Id: UpdateStatusTest.java 4154 2012-02-28 16:14:38Z rkuebert $ */

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
import eu.optimis.service_manager.core.ServiceManager;
import eu.optimis.service_manager.exception.ItemAlreadyExistsException;
import eu.optimis.service_manager.exception.ItemNotFoundException;

public class UpdateStatusTest extends TestCase {

	public void setUp() {
		ServiceManager.getInstance().deleteServices();
	}
	
	public void testUpdateStatus() {
		assertEquals(0, ServiceManager.getInstance().getServices().size());
		ServiceDocument service = TestUtil.createFullService();
		try {
			ServiceManager.getInstance().addService(service);
		} catch (ItemAlreadyExistsException e) {
			fail();
		}
		assertEquals(1, ServiceManager.getInstance().getServices().size());
		
		String newStatus = "stopped";
		
		try {
			ServiceManager.getInstance().updateStatus(service.getService().getServiceId(), newStatus);
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
		System.out.println(service.toString() + "\n");

		assertEquals(newStatus, service.getService().getStatus());
	}
	
}
