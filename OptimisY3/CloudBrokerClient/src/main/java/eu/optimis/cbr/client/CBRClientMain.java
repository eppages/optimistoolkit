package eu.optimis.cbr.client;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.apache.xmlbeans.XmlException;

import eu.optimis.cbr.client.CBRClient;
import eu.optimis.cbr.client.utils.IPInfo;
import eu.optimis.cbr.client.utils.IPInfoList;
import eu.optimis.ipdiscovery.datamodel.Provider;
import eu.optimis.manifest.api.sp.Manifest;
import eu.optimis.types.xmlbeans.servicemanifest.XmlBeanServiceManifestDocument;


import com.sun.jersey.api.client.ClientResponse;

/**
 * @author Pramod Pawar
 */


public class CBRClientMain {

	/**
	 * @param args
	 * @throws Exception 
	 * @throws XmlException 
	 */
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub

		
 		CBRClient cbrclient = new CBRClient("200.00.00.80","8080");
		
/*
		String objective = "COST";
		String file = "./src/main/resources/SP-Manifest.xml";
		File myFile = new File(file);
		XmlBeanServiceManifestDocument doc = XmlBeanServiceManifestDocument.Factory.parse(myFile);
		Manifest spManifest= Manifest.Factory.newInstance(doc);
		System.out.println("CBR Client Created");
		ClientResponse response = cbrclient.getDeploymentDetails(spManifest.toString(), objective);

		System.out.println("Cloud Broker Client :" + response.getStatus());
*/
		
		
		
		/*
		/// IP Registery test cases
		
		IPInfo ip = new IPInfo("99999999", "ATOS", "1200.00.00.80", "A");
		ClientResponse response1 = cbrclient.registerIP(ip);             
		System.out.println("RegisterIP response" + response1);
	
		
		IPInfoList iplist = cbrclient.getAllIP();
		System.out.println("iplist lenght" + iplist.getIPList().toArray().length);
		for(int i=0; i< iplist.getIPList().toArray().length; i++){
			System.out.println("Client Recieved IP" + (i+1) +":" + iplist.getIPList().get(i).getUUID());
		}
		
		*/  
 		
 		
 		/*
 		///// IPRegistry Get test case ///
		IPInfoList iplist = cbrclient.getAllIP();
		System.out.println("iplist lenght" + iplist.getIPList().toArray().length);
		for(int i=0; i< iplist.getIPList().toArray().length; i++){
			//System.out.println("Client Recieved IP" + (i+1) +":" + iplist.getIPList().get(i).getUUID());
			System.out.println("Client Received IP " + (i+1)+ " : " + iplist.getIPList().get(i).getName());
		}
		*/
 		
 		/// IPRegistry RegisterIP Testcase
 		
 		/*
 		// Adding Flex IP
		Map<String, String> properties = new HashMap<String, String>();
		properties.put("BT", "1");
		properties.put("City","2");
		Provider ip = new Provider("flex", "flex", "1200.00.00.80", "optimis",
				"http://1200.00.00.80:8080/optimis-sla", "OPTIMIS-SERVICE-INSTANTIATION","1", properties ); 
				

		ClientResponse response1 = cbrclient.registerIP(ip);             
		System.out.println("RegisterIP response" + response1);
 		
 		 */
 		
 		// Adding Dummy IP
 		/*
		Map<String, String> properties = new HashMap<String, String>();
		properties.put("BT", "1");
		properties.put("City","2");
		Provider ip = new Provider("dummy", "dummy", "1200.00.00.80", "optimis",
				"http://200.00.00.80:8080/sla-management-service-dummy-0.0.1-SNAPSHOT/", "OPTIMIS-SERVICE-INSTANTIATION","1", properties ); 
				

		ClientResponse response1 = cbrclient.registerIP(ip);             
		System.out.println("RegisterIP response" + response1);
		*/

 		/*
 		// Adding UMEA IP
 		
		Map<String, String> properties = new HashMap<String, String>();
		properties.put("BT", "1");
		properties.put("City","2");
		Provider ip = new Provider("umea", "umea", "optimis", "optimis",
				"http://optimis:8080/optimis-sla","OPTIMIS-SERVICE-INSTANTIATION","1", properties ); 
				

		ClientResponse response1 = cbrclient.registerIP(ip);             
		System.out.println("RegisterIP response" + response1);
		*/
	}

		
		
}
