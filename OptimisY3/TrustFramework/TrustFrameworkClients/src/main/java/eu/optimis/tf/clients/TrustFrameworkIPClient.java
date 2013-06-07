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

import javax.ws.rs.DELETE;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;

import eu.optimis.tf.clients.utils.Paths;

public class TrustFrameworkIPClient {

	private String host;
	private int port;
	private Client client;
	
	private String url_start;

	public TrustFrameworkIPClient(String host, int port) {
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
		WebResource service = client.resource(url_start + Paths.TRUST_IP_DEPLOY+"/getTrust/" + providerId);

		ClientResponse response = service.type("application/xml").get(
				ClientResponse.class);
		int status = response.getStatus();
		String textEntity = response.getEntity(String.class);
		return textEntity;
	}

	public String getDeploymentTrustSN(String providerId) {
		WebResource service = client.resource(url_start + Paths.TRUST_IP_DEPLOY+Paths.TRUST_SN+"/getTrust/"
				+ providerId);

		ClientResponse response = service.type("application/xml").get(
				ClientResponse.class);
		int status = response.getStatus();
		String textEntity = response.getEntity(String.class);
		return textEntity;
	}
	
	/*
	public double getHMAssessment(String manifest){
		WebResource service = client.resource(url_start + Paths.TRUST_IP_COMMON + "/getHMAssessment");

		ClientResponse response = service.type("application/xml").post(
				ClientResponse.class, manifest);
		int status = response.getStatus();
		String textEntity = response.getEntity(String.class);
		return Double.valueOf(textEntity);
	}
	*/
	
	
	public double getSelfAssessment(String providerId){
		WebResource service = client.resource(url_start + Paths.TRUST_IP_DEPLOY+Paths.TRUST_SN+"/getSelfAssessment/"
				+ providerId);

		ClientResponse response = service.type("application/xml").get(
				ClientResponse.class);
		int status = response.getStatus();
		String textEntity = response.getEntity(String.class);
		return Double.valueOf(textEntity);
	}
	
	
	/*
	 * IP OPERATION
	 */
	
	public String getOperationTrust(String providerId) {
		WebResource service = client.resource(url_start + Paths.TRUST_IP_OPERATION+"/getTrust/" + providerId);

		ClientResponse response = service.type("application/xml").get(
				ClientResponse.class);
		int status = response.getStatus();
		String textEntity = response.getEntity(String.class);
		return textEntity;
	}

	public String getOperationTrustSN(String providerId) {
		WebResource service = client.resource(url_start + Paths.TRUST_IP_OPERATION+Paths.TRUST_SN+"/getTrust/"
				+ providerId);

		ClientResponse response = service.type("application/xml").get(
				ClientResponse.class);
		int status = response.getStatus();
		String textEntity = response.getEntity(String.class);
		return textEntity;
	}
	
	public String getOperationServiceTrust(String serviceId) {
		WebResource service = client.resource(url_start + Paths.TRUST_IP_OPERATION+"/getServiceTrust/" + serviceId);

		ClientResponse response = service.type("application/xml").get(
				ClientResponse.class);
		int status = response.getStatus();
		String textEntity = response.getEntity(String.class);
		return textEntity;
	}
	
	
	public String getOperationHistoricTrust(String providerId) {
		WebResource service = client.resource(url_start + Paths.TRUST_IP_OPERATION+"/getHistoricTrust/" + providerId);

		ClientResponse response = service.type("application/xml").get(
				ClientResponse.class);
		int status = response.getStatus();
		String textEntity = response.getEntity(String.class);
		return textEntity;
	}
	
	
	public String getOperationHistoricServiceTrust(String serviceId) {
		WebResource service = client.resource(url_start + Paths.TRUST_IP_OPERATION+"/getHistoricServiceTrust/" + serviceId);

		ClientResponse response = service.type("application/xml").get(
				ClientResponse.class);
		int status = response.getStatus();
		String textEntity = response.getEntity(String.class);
		return textEntity;
	}
	
	
	/*
	 * IP COMMON
	 */
	
	public boolean serviceDeployed(String manifest) {
		WebResource service = client.resource(url_start + Paths.TRUST_IP_COMMON + "/servicedeployed");

		ClientResponse response = service.type("application/xml").post(
				ClientResponse.class, manifest);
		int status = response.getStatus();
		String textEntity = response.getEntity(String.class);
		return Boolean.valueOf(textEntity);
	}
	
	public boolean serviceUndeployed(String serviceId) {
		WebResource service = client.resource(url_start + Paths.TRUST_IP_COMMON + "/serviceundeployed/"+serviceId);

		ClientResponse response = service.type("application/xml").delete(
				ClientResponse.class);
		int status = response.getStatus();
		String textEntity = response.getEntity(String.class);
		return Boolean.valueOf(textEntity);
	}
	
	public String getRank() {
		WebResource service = client.resource(url_start + Paths.TRUST_IP_COMMON + "/getRank/");

		ClientResponse response = service.type("application/xml").get(
				ClientResponse.class);
		int status = response.getStatus();
		String textEntity = response.getEntity(String.class);
		return textEntity;
	}
		
	public boolean setProactiveTrustAssessor(String entityId, double threshold, int type)
	{
		WebResource service = client.resource(url_start + Paths.TRUST_IP_COMMON + "/setproactivetrustAssessor/"+entityId+"?threshold="+threshold+"&type="+type);

		ClientResponse response = service.type("application/xml").put(
				ClientResponse.class);
		int status = response.getStatus();
		String textEntity = response.getEntity(String.class);
		return Boolean.valueOf(textEntity);		
	}
	
	public boolean stopProactiveTrust (String id)
	{
		WebResource service = client.resource(url_start + Paths.TRUST_IP_COMMON + "/stopproactivetrust/"+id);

		ClientResponse response = service.type("application/xml").delete(
				ClientResponse.class);
		int status = response.getStatus();
		String textEntity = response.getEntity(String.class);
		return Boolean.valueOf(textEntity);		
	}
	
	/**** Forecasting Interfaces ****/
	
	public double forecastServiceTrust(String serviceId, int timeSpan)
	{
		WebResource service = client.resource(url_start + Paths.TRUST_IP_COMMON + "/forecast/forecastServiceTrust/"+serviceId+"?timespan="+timeSpan);

		ClientResponse response = service.type("application/xml").put(ClientResponse.class);
		int status = response.getStatus();
		String textEntity = response.getEntity(String.class);
		return Double.valueOf(textEntity);		
	}
	
	public double forecastIPTrust(String providerId, int timeSpan)
	{
		WebResource service = client.resource(url_start + Paths.TRUST_IP_COMMON + "/forecast/forecastIPTrust/"+providerId+"?timespan="+timeSpan);

		ClientResponse response = service.type("application/xml").put(ClientResponse.class);
		int status = response.getStatus();
		String textEntity = response.getEntity(String.class);
		return Double.valueOf(textEntity);		
	}
	
	public double forecastServiceDeployment(String manifest)
	{
		WebResource service = client.resource(url_start + Paths.TRUST_IP_COMMON + "/forecast/forecastServiceDeployment");

		ClientResponse response = service.type("application/xml").post(ClientResponse.class, manifest);
		int status = response.getStatus();
		String textEntity = response.getEntity(String.class);
		return Double.valueOf(textEntity);
	}
	
	public double forecastServiceDeploymentIP(String manifest)
	{
		WebResource service = client.resource(url_start + Paths.TRUST_IP_COMMON + "/forecast/forecastServiceDeploymentIP");

		ClientResponse response = service.type("application/xml").post(ClientResponse.class, manifest);
		int status = response.getStatus();
		String textEntity = response.getEntity(String.class);
		return Double.valueOf(textEntity);
	}
	
	public double forecastVMDeployment(String serviceId)
	{
		WebResource service = client.resource(url_start + Paths.TRUST_IP_COMMON + "/forecast/forecastVMDeployment/"+serviceId);

		ClientResponse response = service.type("application/xml").put(ClientResponse.class);
		int status = response.getStatus();
		String textEntity = response.getEntity(String.class);
		return Double.valueOf(textEntity);		
	}
	
	public double forecastVMDeploymentIP(String serviceId)
	{
		WebResource service = client.resource(url_start + Paths.TRUST_IP_COMMON + "/forecast/forecastVMDeploymentIP/"+serviceId);

		ClientResponse response = service.type("application/xml").put(ClientResponse.class);
		int status = response.getStatus();
		String textEntity = response.getEntity(String.class);
		return Double.valueOf(textEntity);		
	}
	
	public double forecastVMCancellation(String serviceId)
	{
		WebResource service = client.resource(url_start + Paths.TRUST_IP_COMMON + "/forecast/forecastVMCancellation/"+serviceId);

		ClientResponse response = service.type("application/xml").put(ClientResponse.class);
		int status = response.getStatus();
		String textEntity = response.getEntity(String.class);
		return Double.valueOf(textEntity);		
	}
	
	public double forecastVMCancellationIP(String serviceId)
	{
		WebResource service = client.resource(url_start + Paths.TRUST_IP_COMMON + "/forecast/forecastVMCancellationIP/"+serviceId);

		ClientResponse response = service.type("application/xml").put(ClientResponse.class);
		int status = response.getStatus();
		String textEntity = response.getEntity(String.class);
		return Double.valueOf(textEntity);		
	}
	
}
