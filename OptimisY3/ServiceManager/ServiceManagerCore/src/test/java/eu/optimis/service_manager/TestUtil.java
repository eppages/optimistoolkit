/* $Id: TestUtil.java 7310 2012-05-03 15:44:42Z tinghe $ */

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

import eu.optimis.serviceManager.InfrastructureProviderDocument;
import eu.optimis.serviceManager.InfrastructureProviderDocument.InfrastructureProvider;
import eu.optimis.serviceManager.ServiceDocument;
import eu.optimis.serviceManager.ServiceDocument.Service;
import eu.optimis.serviceManager.VmDocument;
import eu.optimis.serviceManager.VmDocument.Vm;
import eu.optimis.serviceManager.VmsDocument.Vms;

public class TestUtil {

	/**
	 * Creates a service with infrastructure provider and a VM.
	 *  
	 * @return
	 */
	public static ServiceDocument createFullService() {
		ServiceDocument serviceDoc = ServiceDocument.Factory.newInstance();
		Service service = serviceDoc.addNewService();
		InfrastructureProvider ip = service.addNewInfrastructureProvider();
		ip.setId(UUID.randomUUID().toString());
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
	
	
	/**
	 * Creates a service with infrastructure provider and no VM.
	 *  
	 * @return
	 */
	public static ServiceDocument createIpNoVmService() {
		ServiceDocument serviceDoc = ServiceDocument.Factory.newInstance();
		Service service = serviceDoc.addNewService();
		InfrastructureProvider ip = service.addNewInfrastructureProvider();
		ip.setId(UUID.randomUUID().toString());
		ip.setSlaId("slaId");
		service.setServiceId(UUID.randomUUID().toString());
		service.setStatus("pending");
		return serviceDoc;
	}
	
	/**
	 * Creates a service without an infrastructure provider.
	 * 
	 * @return
	 */
	public static ServiceDocument createNoIpService() {
		ServiceDocument serviceDoc = ServiceDocument.Factory.newInstance();
		Service service = serviceDoc.addNewService();
		service.setServiceId(UUID.randomUUID().toString());
		service.setStatus("pending");
		return serviceDoc;
	}

	/**
	 * Creates an infrastructure provider with one VM.
	 * 
	 * @return the whole document containing the infrastructure provider
	 */
	public static InfrastructureProviderDocument createIpWithVm() {
		InfrastructureProviderDocument infraProviderDoc = InfrastructureProviderDocument.Factory.newInstance();
		InfrastructureProvider ip = infraProviderDoc.addNewInfrastructureProvider();
		ip.setId(UUID.randomUUID().toString());
		ip.setSlaId("slaId");
		Vms vms = ip.addNewVms();
		Vm vm = vms.addNewVm();
		vm.setId(UUID.randomUUID().toString());
		vm.setStatus("pending");
		vm.setType("TypeA");
		return infraProviderDoc;
	}
	
	/**
	 * Creates an infrastructure provider without any VM.
	 * 
	 * @return the whole document containing the infrastructure provider
	 */
	public static InfrastructureProviderDocument createIpWithoutVm() {
		InfrastructureProviderDocument infraProviderDoc = InfrastructureProviderDocument.Factory.newInstance();
		InfrastructureProvider ip = infraProviderDoc.addNewInfrastructureProvider();
		ip.setId(UUID.randomUUID().toString());
		ip.setSlaId("slaId");
		return infraProviderDoc;
	}
	
	/**
	 * Creates a VM with random UUID as id, status 'pending' and type
	 * 'TypeA'.
	 * 
	 * @return the newly created VM
	 */
	public static VmDocument createVm() {
		VmDocument vmDocument = VmDocument.Factory.newInstance();
		Vm vm = vmDocument.addNewVm();
		vm.setId(UUID.randomUUID().toString());
		vm.setStatus("pending");
		vm.setType("TypeA");
		return vmDocument;
	}
}
