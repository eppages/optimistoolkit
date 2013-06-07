/* $Id: DeleteServiceTest.java 4148 2012-02-28 15:48:19Z rkuebert $ */

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

public class DeleteServiceTest extends TestCase {

	/**
	 * Ensures that the service manager is empty before starting any test.
	 */
	public void setUp() {
		ServiceManager.getInstance().deleteServices();
	}
	
	public void testDeleteService() {
		ServiceManager instance = ServiceManager.getInstance();
		int size = instance.getServices().size();
		assertEquals(0, size);
		ServiceDocument service = TestUtil.createFullService();
		try {
			instance.addService(service);
		} catch (ItemAlreadyExistsException itemExistsException) {
			fail("Exception when adding service");
		}
		size = instance.getServices().size();
		assertEquals(1, size);
		try {
			instance.deleteService(service.getService().getServiceId());
		} catch (ItemNotFoundException itemNotFoundException) {
			fail("Service could not be found for deletion");
		}
		size = instance.getServices().size();
		assertEquals(0, size);
	}
	
}
