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

package eu.optimis.tf;

import java.io.IOException;
import java.util.logging.Logger;

import javax.xml.parsers.ParserConfigurationException;

import junit.framework.TestCase;

import org.xml.sax.SAXException;

import eu.optimis.tf.clients.TrustFrameworkIPClient;

public class CoreClientTest extends TestCase {

	Logger log = Logger.getLogger(this.getClass().getName());

	// String host = "optimis1-ipvm.gird.cs.umu.se";
	// int port = 8080;
	// String host = "192.168.252.56";
	// int port = 8080;
	String host = "127.0.0.1";
	int port = 8080;

	TrustFrameworkIPClient tfcclient;
	private static final String userName = "OPTIMUMWEB";
	private static final String servicemanifestpath = System.getProperty("user.dir")+"\\src\\test\\resources\\optimis-DemoApplication.xml";

	public CoreClientTest() {
		tfcclient = new TrustFrameworkIPClient(host, port);
	}

	public void testClientCore() throws IOException, InterruptedException, ParserConfigurationException, SAXException {
		String manifest = ServiceManifestXMLProcessor.readFileAsString(servicemanifestpath);
//		serviceStart(manifest);
//		for (int i = 0; i < 3;i++){
//			//Pause for 30 seconds
//	        Thread.sleep(15000);
//	        log.info("Asking for entity trust in attempt: "+i); 
//			String serviceId = ServiceManifestXMLProcessor.getAttribute(
//					manifest, "VirtualMachineDescription", "serviceId");
//			getEntityTrust(serviceId);
//		}
//		getEntityTrust("DemoApp");
		getHistoricService("DemoApp");
//		serviceStop("DemoApp");
	}

	private void getEntityTrust(String entityId) {
		log.info("Enity Total Trust: " + tfcclient.getOperationTrust(entityId));
	}

	private void serviceStart(String manifest) {
		log.info("Enity Total Trust: " + tfcclient.serviceDeployed(manifest));
	}
	
	private void getHistoricService(String serviceId){
		log.info("Service historic trust: \n" + tfcclient.getOperationHistoricServiceTrust(serviceId));
	}

	private void getEntitySNTrust(String entityId) {
		log.info("Enity Social Network Trust: "
				+ tfcclient.getOperationTrustSN(entityId));
	}
	
	private void serviceStop(String serviceId) {
		log.info("Enity Total Trust: " + tfcclient.serviceUndeployed(serviceId));
	}
}
