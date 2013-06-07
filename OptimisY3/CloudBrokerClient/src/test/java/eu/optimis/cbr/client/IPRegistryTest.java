package eu.optimis.cbr.client;

import com.sun.jersey.api.client.ClientResponse;

import java.util.HashMap;
import java.util.Map;



import eu.optimis.cbr.client.utils.IPInfo;
import eu.optimis.cbr.client.utils.IPInfoList;
import eu.optimis.ipdiscovery.datamodel.Provider;
import junit.framework.TestCase;

/**
 * @author Pramod Pawar
 */

public class IPRegistryTest extends TestCase {
	//CBRClient cbrclient = new CBRClient("200.0.0.80","8080");
	CBRClient cbrclient = new CBRClient("200.0.0.81","8080");
	//CBRClient cbrclient = new CBRClient("200.0.0.82","8080");

	
	public void testRegisterIP(){
		
		
//		IPInfo ip = new IPInfo("99999999", "ATOS", "172.16.1.2", "A");
		Map<String, String> properties = new HashMap<String, String>();
		properties.put("BT", "1");
		properties.put("City","2");
		Provider ip = new Provider("flex", "flex", "200.0.0.85", "optimis",
				"http://200.0.0.80:8080/optimis-sla", "OPTIMIS-SERVICE-INSTANTIATION","1", properties, "dmurl" ); 
				

		ClientResponse response1 = cbrclient.registerIP(ip);             
		System.out.println("RegisterIP response" + response1);
		
	}

	
	public void testGetAllIPs(){
		IPInfoList iplist = cbrclient.getAllIP();
		System.out.println("iplist lenght" + iplist.getIPList().toArray().length);
		for(int i=0; i< iplist.getIPList().toArray().length; i++){
			//System.out.println("Client Recieved IP" + (i+1) +":" + iplist.getIPList().get(i).getUUID());
			System.out.println("Client Received IP " + (i+1)+ " : " + iplist.getIPList().get(i).getName());
		}
	}
	
}
