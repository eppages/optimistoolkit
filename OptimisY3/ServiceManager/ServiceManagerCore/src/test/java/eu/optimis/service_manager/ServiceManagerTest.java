package eu.optimis.service_manager;

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

/**
 * Interface containing generic definitions (URIs, inputs and expected outputs)
 * for all tests related to the service manager.
 * 
 * @author roland
 */
public interface ServiceManagerTest {
	
	/** The ServiceManager's IP. */
	//public final static String SERVER_NAME = "localhost";
	public final static String SERVER_NAME = "212.0.127.141";
	
	/** The ServiceManager's base URI. */
	public final static String BASE_URI = "http://" + SERVER_NAME + ":8080/ServiceManager/";
	
	/** The URI to use for creation and querying services. */
	public final static String SERVICES_URI = "http://" + SERVER_NAME + ":8080/ServiceManager/services";

	/** URI for VM operations :*/
	public final static String SERVICE_VMS_URI = "http://" + SERVER_NAME + ":8080/ServiceManager/services/foo/vms";
	
	/** The XML input for the create service call. */
	public final static String SERVICE_XML = "<service xmlns=\"http://www.optimis.eu/service-manager\">"
			+ "<service_id>foo</service_id>"
			+ "<status>pending</status>"
			+ "</service>";

	/** The expected result for the create service call. */
	public final static String EXPECTED_RESPONSE_CREATE_SERVICE = "<service>\n"
			+ "<service_id>foo</service_id>\n"
			+ "<status>pending</status>\n"
			+ "<ip>\n"
			+ "<ip_id>null</ip_id>\n"
			+ "<ip_address>null</ip_address>\n"
			+ "<vms>\n"
			+ "</vms>\n"
			+ "</ip>\n"
			+ "<link>/services/foo</link>\n"
			+ "</service>\n";
	
	/** The expected result for the put VM service call. */
	public final static String EXPECTED_RESPONSE_PUT_VM = "<service>\n"
			+ "<service_id>foo</service_id>\n"
			+ "<status>pending</status>\n"
			+ "<ip>\n"
			+ "<ip_id>null</ip_id>\n"
			+ "<ip_address>null</ip_address>\n"
			+ "<vms>\n"
			+ "<vm>137</vm>\n"
			+ "</vms>\n"
			+ "</ip>\n"
			+ "<link>/services/foo</link>\n"
			+ "</service>\n";
	
	/** The id of a VM used in put VM and delete VM calls. */
	public final static String VM_ID = "137";
	
}
