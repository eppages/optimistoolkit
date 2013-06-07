/* Copyright (C) 2012-2013 Umeå University

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

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import eu.optimis.ipdiscovery.datamodel.Provider;


//NOT for Unit Tests....
public class DSClientTest
{
	private final static String manifestFile = "src/test/resources/service_manifest.xml";
	public static DeploymentServiceClient getClient()
	{
		String host = "localhost";
		int port =8087;
		DeploymentServiceClient client = new DeploymentServiceClient(host, port);
		return client;
	}
	public static DeploymentServiceClient getClient(String host, int port)
	{
		DeploymentServiceClient client = new DeploymentServiceClient(host, port);
		return client;
	}
	
	public static String produceManifestString(String manifestPath) throws Exception
	{
		String filePath = DSClientTest.manifestFile;
		if (manifestPath != null)
			filePath = manifestPath;
		FileInputStream fstream = new FileInputStream(filePath);
		DataInputStream in = new DataInputStream(fstream);
		BufferedReader br = new BufferedReader(new InputStreamReader(in));
		
		String manifest = "";
		String strLine;
		while ((strLine = br.readLine()) != null)
		{
			manifest += strLine + "\n";
		}
		in.close();
		return manifest;
	}
	
	public static void testSuggestFederatedIP() throws Exception
	{
		DeploymentServiceClient dsClient = DSClientTest.getClient();
		String manifestString = DSClientTest.produceManifestString(null);
		String res = dsClient.suggestFederatedIP(manifestString);
		System.out.println("Suggested IP: = " + res);
	}
	
	public static void testSuggestFederatedIP(String manifestPath) throws Exception
	{
		DeploymentServiceClient dsClient = DSClientTest.getClient();
		String manifestString = DSClientTest.produceManifestString(manifestPath);
		String res = dsClient.suggestFederatedIP(manifestString);
		System.out.println("Suggested IP: = " + res);
	}
	
	public static void testDeploy()throws Exception
	{
		DeploymentServiceClient dsClient = DSClientTest.getClient();
		String manifestString = DSClientTest.produceManifestString(null);
		String objective = "COST";
		dsClient.deploy(manifestString, objective );
	}
	

	public static void testUnDeploy()throws Exception
	{
		DeploymentServiceClient dsClient = DSClientTest.getClient();
		boolean keepData = true;
		String agreementEPR = "<agreementEPR></agreementEPR>";
		String serviceId = "Service_ID_XXX";
		dsClient.undeploy(serviceId , agreementEPR , keepData);
	}

	public static void testGetPlacementSolution4Broker() throws Exception
	{
		DeploymentServiceClient dsClient = DSClientTest.getClient();
		String manifest =  DSClientTest.produceManifestString(null);
		List<Provider> providers = null;
		String trecHost = "localhost";
		String trecPort = "8080";
		HashMap<String, Provider> mapping = dsClient.getPlacementSolution4Broker(manifest, providers, "RISK",
				trecHost, trecPort);
		Set<Entry<String, Provider>> entries = mapping.entrySet();
		for (Entry<String, Provider> entry : entries)
		{
			String serviceId = entry.getKey();
			Provider provider = entry.getValue();
			System.out.println(serviceId + " ->  " + provider);
		}
	}
	
	public static void main(String[] args) throws Exception
	{
		//System.out.println(produceManifestString());
		//testSuggestFederatedIP();
		//testDeploy();
		//testUnDeploy();
		//testGetPlacementSolution4Broker();
		
//		if(args.length<1)
//		{
//			System.out.println("Usage : use a filepath a the input!");
//			return;
//		}
//		String filePath = args[0];
//		System.out.println("File Path = " + filePath);
//		DSClientTest.testSuggestFederatedIP(filePath);
		//testSuggestFederatedIP("/Users/viali/Documents/workspace/optimis/branches/OptimisY3/DeploymentService/DeploymentServiceClient/src/test/resources/service_manifest.xml");
	//	String url = "http://localhost:8087//";
		//System.out.println(url.endsWith("//"));
	//	DeploymentServiceClient client =new DeploymentServiceClient(url);
	}

}
