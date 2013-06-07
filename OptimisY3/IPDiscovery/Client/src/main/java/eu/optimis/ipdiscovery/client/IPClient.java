/*Copyright (C) 2012 Umeå University

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
package eu.optimis.ipdiscovery.client;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.GenericType;
import com.sun.jersey.api.client.WebResource;

import eu.optimis.ipdiscovery.datamodel.Provider;

/**
 * Client for storing and retreiving IP information from IP Discovery Service.
 * @author Daniel Espling
 *
 */
public class IPClient {

    private static Logger log = Logger.getLogger(IPClient.class);

	private Client client;
	private final String EXTENSION = "/ip";
	private final String endpoint;

	/**
	 * Creates a new IP client
	 * @param url The address of the IP discovery service, in the form "<ip>:<port>"
	 */
	public IPClient(String url) {
		this.endpoint = url;
		this.client = Client.create();
	}

	/**
	 * Store data for a specific IP
	 * @param provider Populated IP object to store
	 * @return True if successful, false otherwise
	 * @throws IOException If call fails
	 */
    public boolean storeData(Provider provider) throws IOException {
    	WebResource resource = client.resource(this.endpoint + EXTENSION + '/' + provider.getIdentifier());
    	System.out.println("Using resource: " + resource);
		ClientResponse response = resource.post(ClientResponse.class, provider);
		boolean success = (response.getClientResponseStatus().getFamily() == Response.Status.Family.SUCCESSFUL);
		if (success) {
			String storedStr = response.getEntity(String.class);
			boolean stored = (storedStr.equals("true")? true : false);
			log.debug("Stored?: " + stored);
			return stored;
		} else {
			String error = response.getClientResponseStatus().getReasonPhrase();
			throw new IOException("Failed to store data: " + error);
		}
    }
    
    /**
     * Read data for specific IP
     * @param ipId The IP identifier
     * @return A Provider object, or NULL if no data could be found
     * @throws IOException If call fails
     */
	public Provider getData(String ipId) throws IOException {
    	WebResource resource = client.resource(this.endpoint + EXTENSION + '/' + ipId);
		ClientResponse response = resource.get(ClientResponse.class);
		boolean success = (response.getClientResponseStatus().getFamily() == Response.Status.Family.SUCCESSFUL);
		if (success) {
			Provider provider = response.getEntity(Provider.class);
			log.debug("Found provider info: " + provider);
			return provider;
		} else {
			String error = response.getClientResponseStatus().getReasonPhrase();
			throw new IOException("Failed to read provider info: " + error);
		}
	}
	
	/**
	 * Read all available IP data
	 * @return A set of provider objects representing IP data
	 * @throws IOException If call fails
	 */
	public List<Provider> getAllData() throws IOException {
    	WebResource resource = client.resource(this.endpoint + EXTENSION);
		ClientResponse response = resource.get(ClientResponse.class);

		boolean success = (response.getClientResponseStatus().getFamily() == Response.Status.Family.SUCCESSFUL);
		if (success) {
			List<Provider> providers = response.getEntity(new GenericType<List<Provider>>() {});
			log.debug("Found providers #: " + providers.size());
			return providers;
		} else {
			String error = response.getClientResponseStatus().getReasonPhrase();
			throw new IOException("Failed to read data for providers: " + error);
		}
	}
	
	/**
	 * Delete data for a specific IP
	 * @param ipId the IP identifier
	 * @return true if found, false if not found
	 * @throws IOException If call fails
	 */
	public boolean removeData(String ipId) throws IOException {
		WebResource resource = client.resource(this.endpoint + EXTENSION + '/' + ipId);
		ClientResponse response = resource.delete(ClientResponse.class);

		boolean success = (response.getClientResponseStatus().getFamily() == Response.Status.Family.SUCCESSFUL);
		if (success) {
			String dataFoundStr = response.getEntity(String.class);
			boolean dataFound = (dataFoundStr.equals("true")? true : false);
			log.debug("Datafound?: " + dataFound);
			return dataFound;
		} else {
			String error = response.getClientResponseStatus().getReasonPhrase();
			throw new IOException("Failed to remove data: " + error);
		}
	}
	
	/**
	 * Delete all IP data
	 * @return the number of deleted elements
	 * @throws IOException If call fails.
	 */
	public int removeAllData() throws IOException {
		WebResource resource = client.resource(this.endpoint + EXTENSION);
		ClientResponse response = resource.delete(ClientResponse.class);

		boolean success = (response.getClientResponseStatus().getFamily() == Response.Status.Family.SUCCESSFUL);
		if (success) {
			String removedCountStr = response.getEntity(String.class);
			int removedCount = Integer.valueOf(removedCountStr);
			log.debug("Removed: " + removedCount + " entries.");
			return removedCount;
		} else {
			String error = response.getClientResponseStatus().getReasonPhrase();
			throw new IOException("Failed to remove data: " + error);
		}
	}
	
	
	/**
	 * Only used for testing
	 * @param args First and only argument expected is the service endpoint as <ip>:<port>
	 * @throws IOException if something fails
	 */
	public static void main(String args[]) throws IOException {
		
/*		if (args.length != 1) {
			System.err.println("Expected only argument <ip>:<port>");
			System.exit(-1);
		}
		
		String endPoint = args[0];
		IPClient client = new IPClient(endPoint);
		
		String ipId = "some_Id";
		Provider provider = new Provider("some name", ipId, "some ipAddress", "some type", null);
		
		System.out.println("Should be true: " + client.storeData(provider));
		System.out.println(client.getData(ipId));
		
		String ip2Id = "other_Id";
		
		Map<String, String> properties = new HashMap<String, String>();
		properties.put("apa", "bulle");
		Provider provider2 = new Provider("other name", ip2Id, "other ipAddress", "other type", properties);
		System.out.println("Should be true: " + client.storeData(provider2));
		
		List<Provider> allData = client.getAllData();
		for (Provider prov : allData) {
			System.out.println(prov);
		}
		
		System.out.println("Removed #IPs: " + client.removeAllData());
		allData = client.getAllData();
		System.out.println("Should be 0: " + allData.size());
		System.out.println("Should be false: " + client.removeData(ipId));	*/	
		
		if (args.length < 1) {
			System.err.println("Expected argument <ip>:<port> or <ip>:<port>  ip_file");
			System.exit(-1);
		}
		
		String endPoint = args[0];
		IPClient client = new IPClient(endPoint);
		
		String file = "./src/main/resources/ips.txt";
		if (args.length == 2)
			file = args[1];
		System.out.println("Using file: "+file);
		
		try
		{
			BufferedReader reader = new BufferedReader(new FileReader(file));
			String ipstrings="";
            String tempString = null;
            while ((tempString = reader.readLine()) != null) 
            {
				ipstrings += tempString;
            }
            reader.close();			
			JSONArray a= new JSONArray(ipstrings);
			for (int i = 0; i < a.length(); i++)
			{
				JSONObject ip=new JSONObject(a.get(i).toString());
				String name = ip.getString("name");
				String identifier = ip.getString("identifier");
				String ipAddress = ip.getString("ipAddress");
				String providertype = ip.getString("providerType");
				String cloudQosUrl = ip.getString("cloudQosUrl");
				String agrTemplateName = ip.getString("agreementTemplateName");
				String agrTemplateId = ip.getString("agreementTemplateId");
				Map<String, String> properties=new HashMap<String, String>();
				
				Iterator<?> keys = ip.keys();
				while (keys.hasNext())
				{
					String key = keys.next().toString();
					properties.put(key, ip.get(key).toString());					
				}
				Provider provider = new Provider(name, identifier, ipAddress,
						providertype, cloudQosUrl, agrTemplateName,
						agrTemplateId, properties,"");//SHOULD NOT BE USED.
				client.storeData(provider);
			}
			List<Provider> allData = client.getAllData();
			for (Provider prov : allData) {
				System.out.println(prov);
			}
			
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}
