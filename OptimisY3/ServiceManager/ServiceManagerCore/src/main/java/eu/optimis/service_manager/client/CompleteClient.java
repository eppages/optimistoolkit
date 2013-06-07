/* $Id: CompleteClient.java 967 2011-05-10 14:19:07Z rkuebert $ */

/*
 Copyright (c) 2011 University of Stuttgart
 All rights reserved.

 Redistribution and use in source and binary forms, with or without
 modification, are permitted provided that the following conditions
 are met:
 1. Redistributions of source code must retain the above copyright
 notice, this list of conditions and the following disclaimer.
 2. Redistributions in binary form must reproduce the above copyright
 notice, this list of conditions and the following disclaimer in the
 documentation and/or other materials provided with the distribution.
 3. The name of the author may not be used to endorse or promote products
 derived from this software without specific prior written permission.

 THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package eu.optimis.service_manager.client;

import java.net.URI;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;

import eu.optimis.serviceManager.ServiceDocument;
import eu.optimis.serviceManager.ServiceDocument.Service;

/**
 * Client for showing how all operations of the Service Manager can be used.
 * 
 * @author Roland Kuebert
 */
public class CompleteClient {

	private static final String SERVICE_MANAGER_URI = "http://localhost:8080/ServiceManager";

	public static void main(String[] args) {
		ClientConfig config = new DefaultClientConfig();
		Client client = Client.create(config);
		WebResource service = client.resource(getBaseURI());
		@SuppressWarnings("unused")
		String result = null;
		ServiceDocument serviceDocument = ServiceDocument.Factory.newInstance();
		Service newService = serviceDocument.addNewService();
		newService.setServiceId("foo");
		newService.setStatus("pending");
		System.out.println("Service document:\n" + serviceDocument.toString());
		
		try {
		result = service
				.path("services")
				.accept(MediaType.APPLICATION_XML)
				.type(MediaType.APPLICATION_XML)
				.post(
						String.class,
						serviceDocument.toString());
		} catch (UniformInterfaceException interfaceException) {
			if (interfaceException.getResponse().getStatus() == 403) {
				System.out.println("Cannot create service, already exists");
			} else {
				interfaceException.printStackTrace();
			}
		}
		System.out.println("Getting all services: " + service.path("services").accept(MediaType.APPLICATION_XML).get(String.class));
		
		System.out.println("Getting one service: " + service.path("services/" + newService.getServiceId()).accept(MediaType.APPLICATION_XML).get(String.class));
		
		try {
			System.out.println("Getting none-existing service: " + service.path("services/1234").accept(MediaType.APPLICATION_XML).get(String.class));
		} catch (UniformInterfaceException interfaceException) {
			if (interfaceException.getResponse().getStatus() == 404) {
				System.out.println("Service does not exist");
			} else {
				interfaceException.printStackTrace();
			}
		}

		try {
			System.out.println("Putting VM");
			service.path("services/" + newService.getServiceId() + "/vms").accept(
					MediaType.APPLICATION_XML)
					.put("123");
			System.out.println("VM added");
			System.out.println("Service is now:");
			System.out.println("Getting one service: " + service.path("services/" + newService.getServiceId()).accept(MediaType.APPLICATION_XML).get(String.class));
		} catch (UniformInterfaceException interfaceException) {
			System.out.println(interfaceException.getResponse().getStatus());
		}
		
		System.out.println("Getting all services: " + service.path("services").accept(MediaType.APPLICATION_XML).get(String.class));
		System.out.println("Deleting service...");
		service.path("services/" + newService.getServiceId()).delete();
		System.out.println("Done.");
		System.out.println("Getting all services: " + service.path("services").accept(MediaType.APPLICATION_XML).get(String.class));
	}

	private static URI getBaseURI() {
		return UriBuilder.fromUri(SERVICE_MANAGER_URI)
				.build();
	}

}
