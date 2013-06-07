/**

Copyright 2013 ATOS SPAIN S.A.

Licensed under the Apache License, Version 2.0 (the License);
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

Authors :
Juan Luis Prieto, Francisco Javier Nieto. Atos Research and Innovation, Atos SPAIN SA
@email francisco.nieto@atosresearch.eu 

**/

package eu.optimis.tf.clients;

import java.util.ArrayList;
import java.util.List;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;

import eu.optimis.tf.clients.utils.Paths;

/**
 * Social Network Trust API
 */
public class TrustFrameworkSPClient {
	String host;
	int port;
	Client client;
	
	private String url_start;

	public TrustFrameworkSPClient(String host, int port) {
		this.host = host;
		this.port = port;
		this.url_start = "http://"+host+":"+port;
		this.client = setClient();
	}

	private Client setClient() {
		ClientConfig config = new DefaultClientConfig();
		return Client.create(config);
	}
	
	/*
	 * IP DEPLOYMENT
	 */
	public String getDeploymentTrust(String providerId) {
		System.out.println(url_start+Paths.TRUST_SP_DEPLOY+"/getTrust/" + providerId);
		WebResource service = client.resource(url_start+Paths.TRUST_SP_DEPLOY+"/getTrust/" + providerId);

		ClientResponse response = service.type("application/xml").get(
				ClientResponse.class);
		int status = response.getStatus();
		String textEntity = response.getEntity(String.class);
		return textEntity;
	}

	public String getDeploymentTrustSN(String providerId) {
		WebResource service = client.resource(url_start+Paths.TRUST_SP_DEPLOY+Paths.TRUST_SN+"/getTrust/"
				+ providerId);

		ClientResponse response = service.type("application/xml").get(
				ClientResponse.class);
		int status = response.getStatus();
		String textEntity = response.getEntity(String.class);
		return textEntity;
	}
	
	public boolean getIPAssessment(String manifest) {
		WebResource service = client.resource(url_start+Paths.TRUST_SP_DEPLOY + "/getIPassessment");

		ClientResponse response = service.type("application/xml").post(
				ClientResponse.class, manifest);
		int status = response.getStatus();
		String textEntity = response.getEntity(String.class);
		return Boolean.valueOf(textEntity);
	}
	
	
	/*
	 * IP OPERATION
	 */
	
	public String getOperationTrust(String providerId) {
		WebResource service = client.resource(url_start+Paths.TRUST_SP_OPERATION+"/getTrust/" + providerId);

		ClientResponse response = service.type("application/xml").get(
				ClientResponse.class);
		int status = response.getStatus();
		String textEntity = response.getEntity(String.class);
		return textEntity;
	}

	public String getOperationTrustSN(String providerId) {
		WebResource service = client.resource(url_start+Paths.TRUST_SP_OPERATION+Paths.TRUST_SN+"/getTrust/"
				+ providerId);

		ClientResponse response = service.type("application/xml").get(
				ClientResponse.class);
		int status = response.getStatus();
		String textEntity = response.getEntity(String.class);
		return textEntity;
	}
	
	public List<String> getOperationHistoricTrust(String providerId) {
		WebResource service = client.resource(url_start+Paths.TRUST_SP_OPERATION+"/getHistoricTrust/" + providerId);

		ClientResponse response = service.type("application/xml").get(
				ClientResponse.class);
		int status = response.getStatus();
		System.out.println(status);
		String textEntity = response.getEntity(String.class);
		System.out.println(textEntity);
		String[] historic = textEntity.split(",");
		List<String> historicLst = new ArrayList<String>();
		for (int i = 0; i < historic.length; i++){
			historicLst.add(historic[i]);
		}
		return historicLst;
	}
	
	
	public List<String> getOperationHistoricServiceTrust(String serviceId) {
		WebResource service = client.resource(url_start+Paths.TRUST_SP_OPERATION+"/getHistoricServiceTrust/" + serviceId);

		ClientResponse response = service.type("application/xml").get(
				ClientResponse.class);
		int status = response.getStatus();
		String textEntity = response.getEntity(String.class);
		String[] historic = textEntity.split(",");
		List<String> historicLst = new ArrayList<String>();
		for (int i = 0; i < historic.length; i++){
			historicLst.add(historic[i]);
		}
		return historicLst;
	}
	
	
	/*
	 * IP COMMON
	 */
	
	public boolean serviceDeployed(String manifest) {
		WebResource service = client.resource(url_start+Paths.TRUST_SP_COMMON + "/servicedeployed");

		ClientResponse response = service.type("application/xml").post(
				ClientResponse.class, manifest);
		int status = response.getStatus();
		String textEntity = response.getEntity(String.class);
		return Boolean.valueOf(textEntity);
	}
	
	public boolean serviceUndeployed(String serviceId) {
		WebResource service = client.resource(url_start+Paths.TRUST_IP_COMMON + "/serviceundeployed/"+serviceId);

		ClientResponse response = service.type("application/xml").delete(
				ClientResponse.class);
		int status = response.getStatus();
		String textEntity = response.getEntity(String.class);
		return Boolean.valueOf(textEntity);
	}
	
	public String getRank() {
		WebResource service = client.resource(url_start+Paths.TRUST_IP_COMMON + "/getRank/");

		ClientResponse response = service.type("application/xml").get(
				ClientResponse.class);
		int status = response.getStatus();
		String textEntity = response.getEntity(String.class);
		return textEntity;
	}
	
	public boolean provideSLAInfo(String slaInfo) {
		WebResource service = client.resource(url_start+Paths.TRUST_SP_COMMON+ "/slaInfo");

		ClientResponse response = service.type("application/xml").post(
				ClientResponse.class, slaInfo);
		int status = response.getStatus();
		String textEntity = response.getEntity(String.class);
		return Boolean.valueOf(textEntity);
	}

}
