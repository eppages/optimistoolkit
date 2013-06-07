/* $Id: UpdateSlaIdTest.java 10698 2012-12-18 10:40:17Z django $ */

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

import java.util.UUID;

import junit.framework.TestCase;
//import eu.optimis.serviceManager.InfrastructureProviderDocument;
import eu.optimis.serviceManager.ServiceDocument;
import eu.optimis.serviceManager.InfrastructureProviderDocument.InfrastructureProvider;
import eu.optimis.serviceManager.ServiceDocument.Service;
import eu.optimis.serviceManager.VmDocument.Vm;
import eu.optimis.serviceManager.VmsDocument.Vms;
import eu.optimis.service_manager.core.ServiceManager;
import eu.optimis.service_manager.exception.ItemAlreadyExistsException;
import eu.optimis.service_manager.exception.ItemNotFoundException;

public class UpdateSlaIdTest extends TestCase {

	public void setUp() {
		ServiceManager.getInstance().deleteServices();
	}
	
	public void testUpdateSlaId() {
		assertEquals(0, ServiceManager.getInstance().getServices().size());
		ServiceDocument service = createFullService();
		try {
			ServiceManager.getInstance().addService(service);
		} catch (ItemAlreadyExistsException e) {
			fail();
		}
		assertEquals(1, ServiceManager.getInstance().getServices().size());
		
		String newSlaId = UUID.randomUUID().toString();
		
		try {
			ServiceManager.getInstance().updateSlaId(service.getService().getServiceId(), ipId, newSlaId);
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
		InfrastructureProvider ifp = ServiceManager.getInstance().getInfrastructureProvider(service, ipId);
		
		assertEquals(newSlaId, ifp.getSlaId());
	}
	
	public static String ipId = "ipId";
	public static ServiceDocument createFullService() {
		ServiceDocument serviceDoc = ServiceDocument.Factory.newInstance();
		Service service = serviceDoc.addNewService();
		InfrastructureProvider ip = service.addNewInfrastructureProvider();
		ip.setId(ipId);
		ip.setSlaId("slaId");
		Vms vms = ip.addNewVms();
		Vm vm = vms.addNewVm();
		vm.setId(UUID.randomUUID().toString());
		vm.setStatus("pending");
		vm.setType("TypeA");
		service.setServiceId(UUID.randomUUID().toString());
		service.setStatus("pending");
		return serviceDoc;
	}
}
