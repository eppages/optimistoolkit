/* $Id: AddIpTest.java 10698 2012-12-18 10:40:17Z django $ */

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
//import eu.optimis.serviceManager.InfrastructureProviderDocument.InfrastructureProvider;
import eu.optimis.serviceManager.ServiceDocument;
import eu.optimis.service_manager.core.ServiceManager;
import eu.optimis.service_manager.exception.ItemAlreadyExistsException;
import eu.optimis.service_manager.exception.ItemNotFoundException;

public class AddIpTest extends TestCase {

	public void setUp() {
		ServiceManager.getInstance().deleteServices();
	}
	
	public void testAddIp() {
		assertEquals(0, ServiceManager.getInstance().getServices().size());
		ServiceDocument service = TestUtil.createNoIpService();
		try {
			ServiceManager.getInstance().addService(service);
		} catch (ItemAlreadyExistsException e) {
			fail();
		}
		assertEquals(1, ServiceManager.getInstance().getServices().size());
		
		// Adding one IP should be okay
		InfrastructureProviderDocument infraProviderDoc = TestUtil.createIpWithVm();
		System.out.println(infraProviderDoc);
		try {
			System.out.println(ServiceManager.getInstance().addIp(service.getService().getServiceId(), infraProviderDoc));
		} catch (ItemNotFoundException e) {
			e.printStackTrace();
			fail();
		} catch (ItemAlreadyExistsException e) {
			e.printStackTrace();
			fail();
		}
		
		// Adding another IP should be okay
		try {
			System.out.println(ServiceManager.getInstance().addIp(service.getService().getServiceId(), TestUtil.createIpWithVm()));
		} catch (ItemNotFoundException e) {
			e.printStackTrace();
			fail();
		} catch (ItemAlreadyExistsException e) {
			e.printStackTrace();
			fail();
		}
		
//		// Adding it again should should fail
//		try {
//			System.out.println(ServiceManager.getInstance().addIp(service.getService().getServiceId(), infraProviderDoc));
//		} catch (ItemNotFoundException e) {
//			e.printStackTrace();
//			fail();
//		} catch (ItemAlreadyExistsException e) {
//			return;
//		}
//		
//		fail();
	}
	
}
