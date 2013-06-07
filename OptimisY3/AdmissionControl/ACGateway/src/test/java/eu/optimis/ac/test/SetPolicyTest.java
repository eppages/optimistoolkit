/*
 * Copyright (c) 2013 National Technical University of Athens (NTUA)
 *	
 *   							MIT License
 *   
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package eu.optimis.ac.test;

import eu.optimis.ac.ACRestClients.ACSetPolicyRestClient;
import eu.optimis.ac.ACRestClients.MyRestClients.RestClient_noInput_String;
import eu.optimis.ac.gateway.acCsvInfo.GetTREC_weights;
import eu.optimis.ac.test.remoteTest.GetServerDetails;
import static junit.framework.Assert.assertEquals;
import junit.framework.TestCase;

public class SetPolicyTest  extends TestCase{
	
        /*
	private String trust_weight="0.1";
	private String eco_weight="0.4";
	private String risk_weight="0.2";
	private String cost_weight="0.3";
	*/
        /*
        private String trust_weight="0.25";
	private String eco_weight="0.25";
	private String risk_weight="0.25";
	private String cost_weight="0.25";
        */
        
        private String trust_weight="0.0";
	private String eco_weight="0.0";
	private String risk_weight="1.0";
	private String cost_weight="0.0";
        
        
        private static String StartUrl="/ACGateway/csv";
        
	public void testSetPolicy()
	{
		System.out.println("SetPolicyTest Starts\n");
		
		String host = GetServerDetails.Host;
		String port = GetServerDetails.Port;
		
                RestClient_noInput_String trustClient = new RestClient_noInput_String(host,port,StartUrl+"/getTrust_Weight");
                RestClient_noInput_String riskClient = new RestClient_noInput_String(host,port,StartUrl+"/getRisk_Weight");
                RestClient_noInput_String ecoClient = new RestClient_noInput_String(host,port,StartUrl+"/getEco_Weight");
                RestClient_noInput_String costClient = new RestClient_noInput_String(host,port,StartUrl+"/getCost_Weight");
                
		String trustWeight = trustClient.returnedString;
		String riskWeight = riskClient.returnedString;
		String ecoWeight = ecoClient.returnedString;
		String costWeight = costClient.returnedString;
                
		GetTREC_weights.printTREC_WeightsToScreen("Current TREC Weights at "+host+" are :", 
				trustWeight, riskWeight, 
				ecoWeight, costWeight);
		
		ACSetPolicyRestClient acSetPolicyRestClient = setWeights(host,port,
                    trust_weight,risk_weight,
                    eco_weight,cost_weight);
                
                System.out.println();
                System.out.println("Status : "+acSetPolicyRestClient.status);
		System.out.println();
                System.out.println("Message from Server : "+acSetPolicyRestClient.returnedString);
                System.out.println();
                
                trustClient = new RestClient_noInput_String(host,port,StartUrl+"/getTrust_Weight");
                riskClient = new RestClient_noInput_String(host,port,StartUrl+"/getRisk_Weight");
                ecoClient = new RestClient_noInput_String(host,port,StartUrl+"/getEco_Weight");
                costClient = new RestClient_noInput_String(host,port,StartUrl+"/getCost_Weight");
                
                trustWeight = trustClient.returnedString;
		riskWeight = riskClient.returnedString;
		ecoWeight = ecoClient.returnedString;
		costWeight = costClient.returnedString;
                
                GetTREC_weights.printTREC_WeightsToScreen("New TREC Weights at "+host+" are :", 
				trustWeight, riskWeight, 
				ecoWeight, costWeight);
                
                assertEquals(trustWeight,trust_weight);
                assertEquals(riskWeight,risk_weight);
                assertEquals(ecoWeight,eco_weight);
                assertEquals(costWeight,cost_weight);
                
	}//testSetPolicy()
	
        protected static ACSetPolicyRestClient setWeights(String host,String port,
            String trust_weight,String risk_weight,
            String eco_weight,String cost_weight)
    {
       GetTREC_weights.printTREC_WeightsToScreen("Set TREC Weights to :", 
				trust_weight, risk_weight, 
				eco_weight, cost_weight);
		
		ACSetPolicyRestClient acSetPolicyRestClient = new ACSetPolicyRestClient(host,port,
				trust_weight,risk_weight,eco_weight,cost_weight);
                
                assertEquals(acSetPolicyRestClient.status,200);
                
                return acSetPolicyRestClient;
    }//setWeights()
}//class
