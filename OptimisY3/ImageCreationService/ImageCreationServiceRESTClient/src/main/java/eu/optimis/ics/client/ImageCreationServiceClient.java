/* $Id$ */

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
package eu.optimis.ics.client;

import java.net.URI;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;

import org.apache.log4j.Logger;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;

public class ImageCreationServiceClient {


	WebResource imageCreationService;
	String host;
	String port;
	String accepts = MediaType.APPLICATION_XML;
	String contentType = MediaType.APPLICATION_XML;
	
	/** Log4j logger instance. */
	private static Logger LOG = Logger.getLogger(ImageCreationServiceClient.class);

	/**
	 * Creates a client for the ImageCreationService for the given
	 * <code>host</code> and <code>port</code>.
	 * <p/>
	 * Default content type is MediaType.APPLICATION_XML.
	 * 
	 * @param host
	 *            the host where the ImageCreationService is running
	 * 
	 * @param port
	 *            the port where the ImageCreationService service is running
	 */
	public ImageCreationServiceClient(String host, String port) {
		ClientConfig config = new DefaultClientConfig();
		Client client = Client.create(config);
		imageCreationService = client.resource(getBaseURI("http://" + host + ":"
				+ port + "/ImageCreationService/image"));
	}
	
	public String createCoreImage() {
		return createImage("CoreElement");
	}
	
	private String createImage(String type) {
		return imageCreationService.post(String.class, type);
	}
	
	public String createOrchestrationImage() {
		return createImage("OrchestrationElement");
	}
	
	/**
	 * Builds a URI from the given string.
	 * 
	 * @param uri
	 *            the string to convert to a URI
	 * 
	 * @return the URI built from the given string
	 */
	private URI getBaseURI(String uri) {
		return UriBuilder.fromUri(uri).build();
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
			ImageCreationServiceClient client = new ImageCreationServiceClient("localhost",
					"8080");
			client.flushDatabase();
			
			String imageId = "16a4e02e-b162-40be-83cd-44815a769c60";
			
//			String imageId = client.createCoreImage();
//			System.out.println("Created image with id " + imageId);
//			System.out.println("Getting image information");
			client.unfinalize(imageId);
			System.out.println("Finalizing");
			System.out.println("URL: " + client.finalize(imageId));
//			System.out.println(client.getImage(imageId));
//			client.unfinalize(imageId);
//			System.out.println(client.getImage(imageId));
//			client.finalize(imageId);
//			System.out.println(client.getImage(imageId));
			
	}
	
	private void unfinalize(String imageId) {
		LOG.info("Unfinalizing image with id '" + imageId + "' ");
		imageCreationService.path(imageId).path("unfinalize").post();
		LOG.info("Done");
		
	}

	public String finalize(String imageId) {
		LOG.info("Finalizing image with id '" + imageId + "' ");
		String imageUrl = imageCreationService.path(imageId).path("finalize").post(String.class);
		LOG.info("Obtained image URL: " + imageUrl);
		LOG.info("Done");
		return imageUrl;
	}

	public String getImage(String imageId) {
		return imageCreationService.path(imageId).get(String.class);
	}
	
	public void setPermissions(String imageId, String path, String permissions) {
		throw new RuntimeException();
	}
	
	
	
//	private Image getImageObject(String imageId) {
//		return imageCreationService.path(imageId).get(String.class);
//	}
	

	private void flushDatabase() {
		LOG.error("Flush database functionality not implemented");
		//imageCreationService.path("flush").delete();
	}

}
