/*
 Copyright (C) 2012-2013 Umeå University

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/
package eu.optimis.ds.client;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.core.util.MultivaluedMapImpl;
import com.thoughtworks.xstream.XStream;

import eu.optimis.ipdiscovery.datamodel.Provider;

/**
 * @author Wubin.Li (Viali)
 * @author Petter Svärd
 * 
 */

public class DeploymentServiceClient
{
	private static Logger logger = Logger.getLogger(DeploymentServiceClient.class);

	private final String rootServicePath = "/service/DeployService";
	private final String deployPath = "/deploy";
	private final String outSourcePath = "/outsource";
	private final String statusPath = "/status";
	private final String suggestFederatedIPpath ="/suggestFederatedIP";
	private final String undeployPath = "/undeploy";
	private final String getPlacementSolution4BrokerPath ="/getPlacementSolution4Broker";
	private final String resultPath = "/result";
	

	private String endpointURL=null;
	/**
	 * @param host, e.g., {optimis-spvm.atosorigin.es}
	 * @param port, e.g., {8087}
	 */
	public DeploymentServiceClient(String host, int port)
	{
		this.endpointURL  = "http://" + host + ":" + port;
	}	
	
	/**
	 * @param url, e.g., {http://optimis-spvm.atosorigin.es:8087}
	 */
	public DeploymentServiceClient(String url)
	{
		logger.debug("DeploymentServiceClient Construction with url: " + url);
		while (url.endsWith("/"))
		{
			url = url.substring(0, url.length() - 1);
		}
		logger.debug("DeploymentServiceClient Construction with url (formated): "+ url);
		this.endpointURL = url;
	}
	
	/**
	 * @param manifestXML
	 * @return a suggested IP name
	 */
	public String suggestFederatedIP(String manifestXML) throws IOException
	{
		String url = this.endpointURL	+ this.rootServicePath + this.suggestFederatedIPpath;
		logger.info("Calling suggestFederatedIP  Operation via url: " + url);
		
		Client client = Client.create();
		WebResource webResource = client.resource(url);
		MultivaluedMap<String, String> formData = new MultivaluedMapImpl();
		formData.add("ManifestString", manifestXML);
		
		ClientResponse response = webResource.post(ClientResponse.class, formData);
		boolean success = (response.getClientResponseStatus().getFamily() == Response.Status.Family.SUCCESSFUL);
		
		if (success)
		{
			String ipName = response.getEntity(String.class);
			return ipName;
		}
		else
		{
			String error = response.getClientResponseStatus().getReasonPhrase();
			logger.error(error);
			throw new IOException("Failed to call suggestFederatedIP Operation: " + error);
		}
	}
	
	/**
	 * @param manifestXML
	 * @param objective
	 */
	public boolean deploy(String manifestXML, String objective) throws IOException
	{
		String url = this.endpointURL	+ this.rootServicePath + this.deployPath;
		
		logger.info("Calling Deployment Service via url: " + url);
		
		Client client = Client.create();
		WebResource webResource = client.resource(url);
		MultivaluedMap<String, String> formData = new MultivaluedMapImpl();
		formData.add("ManifestString", manifestXML);
		formData.add("Objective", objective.toUpperCase());
		
		ClientResponse response = webResource.post(ClientResponse.class, formData);
		boolean success = (response.getClientResponseStatus().getFamily() == Response.Status.Family.SUCCESSFUL);
		
		if (success)
		{
			String deployed = response.getEntity(String.class);
			boolean result = (deployed.equals("true") ? true : false);
			return result;
		}
		else
		{
			String error = response.getClientResponseStatus().getReasonPhrase();
			logger.error(error);
			throw new IOException("Failed to deploy the manifest: " + error);
		}
	}
	
	/**
	 * @param serviceId
	 * @return a json string that represents the status of a deployment status for service with @serviceId
	 */
	public String queryDeploymentStatus(String serviceId) throws IOException
	{
		String url = this.endpointURL	+ this.rootServicePath + "/" + serviceId + this.statusPath;
		
		logger.info("Querying Deployment Status via url: " + url);
		
		Client client = Client.create();
		WebResource webResource = client.resource(url);
		ClientResponse response = webResource.get(ClientResponse.class);
		boolean success = (response.getClientResponseStatus().getFamily() == Response.Status.Family.SUCCESSFUL);
		
		if (success)
		{
			String result = response.getEntity(String.class);
			logger.info("Status returned: " + result);
			return result;
		}
		else
		{
			String error = response.getClientResponseStatus().getReasonPhrase();
			logger.error(error);
			throw new IOException(error);
		}
	}	
	/**
	 * @param serviceId
	 * @param agreementEPR
	 * @return undeployment result
	 * @throws IOException
	 * */
	public boolean undeploy(String serviceId, String agreementEPR,  boolean keepData) throws IOException
	{
		String url = this.endpointURL + this.rootServicePath	+ this.undeployPath;
		logger.info("Calling Undeploy  Operation via url: " + url);
		
		Client client = Client.create();
		WebResource webResource = client.resource(url);
		MultivaluedMap<String, String> formData = new MultivaluedMapImpl();
		formData.add("serviceId", serviceId);
		formData.add("agreementEPR", agreementEPR);
		formData.add("keepData", String.valueOf(keepData));
		
		ClientResponse response = webResource.post(ClientResponse.class, formData);
		boolean success = (response.getClientResponseStatus().getFamily() == Response.Status.Family.SUCCESSFUL);
		
		if (success)
		{
			String r = response.getEntity(String.class);
			logger.debug("Response : " + r);
			if (r.equalsIgnoreCase("true"))
				return true;
			else
				return false;
		}
		else
		{
			String error = response.getClientResponseStatus().getReasonPhrase();
			logger.error(error);
			throw new IOException("Failed to call undeploy Operation: " + error);
		}
	}

	/**
	 * @param manifestXML
	 * @param objective
	 */
	public HashMap<String, Provider> getPlacementSolution4Broker(
			String manifestXML, List<Provider> providers, String objective,
			String trecHost, String trecPort)
			throws IOException
	{
		String url = this.endpointURL	+ this.rootServicePath + this.getPlacementSolution4BrokerPath;
		
		logger.info("Calling getPlacementSolution4Broker via url: " + url);
		
		if(providers==null)
			providers = new ArrayList<Provider>();
		XStream xs = new XStream();
		String providersString=xs.toXML(providers);
		
		Client client = Client.create();
		WebResource webResource = client.resource(url);
		MultivaluedMap<String, String> formData = new MultivaluedMapImpl();
		formData.add("ManifestString", manifestXML);
		formData.add("ProvidersString", providersString);
		formData.add("Objective", objective.toUpperCase());
		formData.add("TrecHost", trecHost);
		formData.add("TrecPort", trecPort);
		
		ClientResponse response = webResource.post(ClientResponse.class, formData);
		boolean success = (response.getClientResponseStatus().getFamily() == Response.Status.Family.SUCCESSFUL);
		
		if (success)
		{
			String res = response.getEntity(String.class);
			@SuppressWarnings("unchecked")
			HashMap<String, Provider> result = (HashMap<String, Provider>) xs.fromXML(res);
			return result;
		}
		else
		{
			String error = response.getClientResponseStatus().getReasonPhrase();
			logger.error(error);
			throw new IOException("Failed to call getPlacementSolution4Broker Operation: " + error);
		}
	}

	/**
	 * @param serviceId
	 * @return a json string that represents the result of a deployment for service with @serviceId
	 */
	public String queryDeploymentResult(String serviceId) throws IOException
	{
		String url = this.endpointURL	+ this.rootServicePath + "/" + serviceId + this.resultPath;
		
		logger.info("Querying Deployment Result via url: " + url);
		
		Client client = Client.create();
		WebResource webResource = client.resource(url);
		ClientResponse response = webResource.get(ClientResponse.class);
		boolean success = (response.getClientResponseStatus().getFamily() == Response.Status.Family.SUCCESSFUL);
		
		if (success)
		{
			String result = response.getEntity(String.class);
			logger.info("Deployment result returned: " + result);
			return result;
		}
		else
		{
			String error = response.getClientResponseStatus().getReasonPhrase();
			logger.error(error);
			throw new IOException(error);
		}
	}	

	/**
	 * @param manifestXML
	 * @param objective
	 * The returned value is an offer in String format.
	 */
	public String outSourceVMs(String manifestXML, String objective) throws IOException
	{
		String url = this.endpointURL	+ this.rootServicePath + this.outSourcePath;
		
		logger.info("Calling Deployment Service via url: " + url);
		
		Client client = Client.create();
		WebResource webResource = client.resource(url);
		MultivaluedMap<String, String> formData = new MultivaluedMapImpl();
		formData.add("ManifestString", manifestXML);
		formData.add("Objective", objective.toUpperCase());
		
		ClientResponse response = webResource.post(ClientResponse.class, formData);
		boolean success = (response.getClientResponseStatus().getFamily() == Response.Status.Family.SUCCESSFUL);
		
		if (success)
		{
			String res = response.getEntity(String.class);
			if (res.equalsIgnoreCase("NO OFFER"))
			{
				logger.error("NO OFFER returned.");
				throw new IOException("NO Valid Offer Returned!");
			}
			return res;
		}
		else
		{
			String error = response.getClientResponseStatus().getReasonPhrase();
			logger.error(error);
			throw new IOException("Failed to outsource the manifest: " + error);
		}
	}
	
	/**
	 * @param serviceId
	 * @param placmentSolution
	 * @return
	 * @throws IOException
	 * This is designed for Cloud Broker. 
	 * When the Broker gets the returned PlacementSolution from DO,
	 * it will use this api to call the deployment service to complete a deployment.
	 */
	/*
	public boolean deployBorkerSolution(String serviceId, PlacementSolution placmentSolution) throws IOException
	{
		String url = this.endPointURL	+ this.rootServicePath + "/" + serviceId	+ this.deployBrokerSolutionPath;
		
		logger.info("Calling Deployment Service (deployBorkerSolution) via url: " + url);
		
		Client client = Client.create();
		WebResource resource = client.resource(url);
		ClientResponse response = resource.post(ClientResponse.class, placmentSolution);
		boolean success = (response.getClientResponseStatus().getFamily() == Response.Status.Family.SUCCESSFUL);
		
		if (success)
		{
			String deployed = response.getEntity(String.class);
			boolean result = (deployed.equals("true") ? true : false);
			return result;
		}
		else
		{
			String error = response.getClientResponseStatus().getReasonPhrase();
			logger.error(error);
			throw new IOException("Failed to deploy the Broker Solution: " + error);
		}
	}
	*/
}
