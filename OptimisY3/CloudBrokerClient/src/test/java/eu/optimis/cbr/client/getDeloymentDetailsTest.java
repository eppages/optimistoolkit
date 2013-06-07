package eu.optimis.cbr.client;

import java.io.File;
import java.io.IOException;

import javax.ws.rs.core.Response;

import org.apache.xmlbeans.XmlException;

import com.sun.jersey.api.client.ClientResponse;

import eu.optimis.manifest.api.sp.Manifest;
import eu.optimis.types.xmlbeans.servicemanifest.XmlBeanServiceManifestDocument;
import junit.framework.TestCase;

/**
 * @author Pramod Pawar
 */

public class getDeloymentDetailsTest extends TestCase {
	
	//CBRClient cbrclient = new CBRClient("200.0.0.80","8080");  //Viali
	CBRClient cbrclient = new CBRClient("200.0.0.81","8080"); 
	//CBRClient cbrclient = new CBRClient("200.0.0.82","8080"); 
	
	
	public void testDeploymentdetails(){
		String obj="COST";
//		String file = "./src/main/resources/SP-Manifest.xml";
		String file = "./src/main/resources/service_manifest_2.xml";

		File myFile = new File(file);
		XmlBeanServiceManifestDocument doc=null ;
		try {
			doc = XmlBeanServiceManifestDocument.Factory.parse(myFile);
		} catch (XmlException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Manifest spManifest= Manifest.Factory.newInstance(doc);
		System.out.println("CBR Client Created");
		ClientResponse response = cbrclient.getDeploymentDetails(spManifest.toString(), obj);

		System.out.println("Cloud Broker Client :" + response.getStatus());
		
		if (Response.Status.OK.getStatusCode() == response.getStatus()) {

			  // normal case, you receive your User object
			  System.out.println("Response is OK");

			} else {

				System.out.println("Response is Problematic");
		}


	}

}
