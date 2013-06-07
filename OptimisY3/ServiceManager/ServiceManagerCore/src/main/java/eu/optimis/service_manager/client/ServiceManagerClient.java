/* $Id: ServiceManagerClient.java 1074 2011-05-16 09:55:03Z rkuebert $ */

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

import gnu.getopt.Getopt;

import java.net.URI;

import javax.ws.rs.core.UriBuilder;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;

public class ServiceManagerClient {

	WebResource service;
	
	public ServiceManagerClient(String uri) {
		ClientConfig config = new DefaultClientConfig();
		Client client = Client.create(config);
		service = client.resource(getBaseURI(uri));
	}
	
	private URI getBaseURI(String uri) {
		return UriBuilder.fromUri(uri).build();
	}

	public static void main(String[] args) {
		Getopt g = new Getopt("ServiceManagerClient", args, "a:u:m:");

		String action = null;
		String url = null;
		String message = null;

		int c;
		while ((c = g.getopt()) != -1) {
			switch (c) {
			case 'a':
				action = g.getOptarg();
				break;
			case 'u':
				url = g.getOptarg();
				break;
			case 'm':
				message = g.getOptarg();
				break;
			case '?':
				break; // getopt() already printed an error
			default:
				System.out.print("getopt() returned " + c + "\n");
			}
		}
		boolean error = false;

		if (action == null) {
			System.err
					.println("No action specified, you need to specify an action");
			error = true;
		}
		if (url == null) {
			System.err.println("No URL specified, you need to specify an URL");
			error = true;
		}

		if (error == true) {
			usage();
			System.exit(0);
		}
		
		ServiceManagerClient client = new ServiceManagerClient(url);
		try {
			client.performAction(action, message);
		} catch (UniformInterfaceException exception) {
			System.err.println("Error code returned by server: " + exception.getResponse().getStatus());
		}
	}

	private void performAction(String action, String message) {
		String result;
		if (action.toUpperCase().equals(Action.CREATE.toString())) {
			if (message != null) {
				result = service.post(String.class, message);
			} else {
				result = service.post(String.class);
			}
		} else if (action.toUpperCase().equals(Action.UPDATE.toString())) {
			if (message != null) {
				result = service.put(String.class, message);
			} else {
				result = service.put(String.class);
			}
		} else if (action.toUpperCase().equals(Action.RETRIEVE.toString())) {
				result = service.get(String.class);
		} else if (action.toUpperCase().equals(Action.DELETE.toString())) {
			if (message != null) {
				result = service.delete(String.class, message);
			} else {
				result = service.delete(String.class);
			}
		} else {
			throw new UnsupportedActionException("Unsupported action: '" + action + "'");
		}
		
		System.out.print(result);
	}

	private static void usage() {
		System.err
				.println("Usage: java ServiceManagerClient -u URL -a ACTION [-m MESSAGE]");
		System.err
				.println("ACTION can be one of CREATE, RETRIEVE, UPDATE or DELETE");
	}
}
