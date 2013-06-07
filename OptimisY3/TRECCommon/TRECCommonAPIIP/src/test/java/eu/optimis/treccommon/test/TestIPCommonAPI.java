/*
 *  Copyright 2013 University of Leeds UK, ATOS SPAIN S.A., City University London, Barcelona Supercomputing Centre and SAP
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package eu.optimis.treccommon.test;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import junit.framework.TestCase;
import eu.optimis.treccommon.ReturnSPPoF;
import eu.optimis.treccommon.TrecApiIP;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TestIPCommonAPI extends TestCase {

	String FlexHost = "109.231.120.19";
	String UmuHost = "optimis-ipvm2.ds.cs.umu.se";
	int port = 8080;
	
	String ipid = "atos";
	String serviceId = "DemoApp";
	String manifestName = "IP-ManifestExampleY3.xml";
	
	public void testTrecCAPIIP_UMU(){
		System.out.println("============== Testing UMU ==================");
		String manifestPath = "";
		if (System.getProperty("file.separator").equalsIgnoreCase("\\")){
			manifestPath = System.getProperty("user.dir")+"\\src\\test\\resources\\"+manifestName;
		} else {	
			manifestPath = System.getProperty("user.dir")+"/src/test/resources/"+manifestName;
		}
		System.out.println(manifestPath);
                String SMDoc = null;
                try {
                SMDoc = readFileAsString(manifestPath);
                }catch (Exception e) {
		}
		TrecApiIP taip = new TrecApiIP(UmuHost, 8080);
                ReturnSPPoF risk = taip.RISK.preNegotiateSPDeploymentPhase(ipid, SMDoc);
                assertEquals(ipid, risk.getSPNames().get(0));
                assertEquals(1.0, risk.getPoFSLA().get(0));
                List<Integer>  risklevels = new ArrayList<Integer>();
                HashMap<String, String> hosts = new HashMap<String, String>();
                hosts.put("optimis1", "24");
                hosts.put("optimis2", "48");
                hosts.put("optimis5", "48");
                hosts.put("optimis6", "48");
                risklevels = taip.RISK.calculateRiskLevelsOfPhyHostFailures(hosts);
                System.out.println("risklevles: " + risklevels.toString());
//		System.out.println(taip.getTrustLevel(ipid));
//		System.out.println(taip.assessServiceEcoefficiency(serviceId));
//		try {
//			System.out.println(taip.predictIPCost(UmuHost, port, readFileAsString(manifestPath)));
//		} catch (NumberFormatException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	}
	
//	public void testTrecCAPIIP_FLEX(){
//		System.out.println("============== Testing FLEX ==================");
//		String manifestPath = "";
//		if (System.getProperty("file.separator").equalsIgnoreCase("\\")){
//			manifestPath = System.getProperty("user.dir")+"\\src\\test\\resources\\"+manifestName;
//		} else {
//			manifestPath = System.getProperty("user.dir")+"/src/test/resources/"+manifestName;
//		}
//		System.out.println(manifestPath);
//		TrecApiIP taip = new TrecApiIP(FlexHost,port);
//		System.out.println(taip.getTrustLevel(ipid));
//		System.out.println(taip.assessServiceEcoefficiency(serviceId, "energy"));
//		try {
//			System.out.println(taip.predictIPCost(UmuHost, port, readFileAsString(manifestPath)));
//		} catch (NumberFormatException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
	
	private String readFileAsString(String filePath)
			throws java.io.IOException {
		byte[] buffer = new byte[(int) new File(filePath).length()];
		BufferedInputStream f = null;
		try {
			f = new BufferedInputStream(new FileInputStream(filePath));
			f.read(buffer);
		} finally {
			if (f != null)
				try {
					f.close();
				} catch (IOException ignored) {
				}
		}
		return new String(buffer);
	}
}
