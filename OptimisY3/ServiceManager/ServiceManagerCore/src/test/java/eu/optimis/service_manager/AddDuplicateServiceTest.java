/* $Id: AddDuplicateServiceTest.java 4148 2012-02-28 15:48:19Z rkuebert $ */

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

public class AddDuplicateServiceTest extends TestCase {

	/**
	 * Ensures that the service manager is empty before starting any test.
	 */
	public void setUp() {
		ServiceManager.getInstance().deleteServices();
	}
	
	public void testAddService() {
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
		// Add duplicate service - we should get an exeption here
		try {
			instance.addService(randomService);
		} catch (ItemAlreadyExistsException e) {
			return;
		}
		fail("Duplicate service should not be accepted");
	}
	
}
