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

import eu.optimis.ac.ACRestClients.ACperServiceConstraintsRestClient;
import eu.optimis.ac.ACRestClients.MyRestClients.RestClient_noInput_String;
import eu.optimis.ac.gateway.acCsvInfo.GetTREC_constraints;
import eu.optimis.ac.test.remoteTest.GetServerDetails;
import junit.framework.TestCase;

public class PerServiceConstraintsTest   extends TestCase {
    
        public static String trustLevel_constraint = "0.0" ;
	public static String ecoValue_constraint = "0";
	public static String riskLevel_constraint = "7.0";
	public static String costInEuro_constraint = "100000";
	
        private static String StartUrl="/ACGateway/csv";
        
        public void testPerServiceConstraints()
        {
            System.out.println("PerServiceConstraintsTest Starts\n");
            
            PerServiceConstraintsTesting(trustLevel_constraint,ecoValue_constraint,
                riskLevel_constraint,costInEuro_constraint);
            
        }//testPerServiceConstraints()
        
	public static void PerServiceConstraintsTesting(String trustLevel_constraint,String ecoValue_constraint,
                String riskLevel_constraint,String costInEuro_constraint)
	{
		String host = GetServerDetails.Host;
		String port = GetServerDetails.Port;
		
                RestClient_noInput_String trustClient = new RestClient_noInput_String(host,port,StartUrl+"/getTrust_Constraint");
                RestClient_noInput_String riskClient = new RestClient_noInput_String(host,port,StartUrl+"/getRisk_Constraint");
                RestClient_noInput_String ecoClient = new RestClient_noInput_String(host,port,StartUrl+"/getEco_Constraint");
                RestClient_noInput_String costClient = new RestClient_noInput_String(host,port,StartUrl+"/getCost_Constraint");
                
		String trustConstraint = trustClient.returnedString;
		String riskConstraint = riskClient.returnedString;
		String ecoConstraint = ecoClient.returnedString;
		String costConstraint = costClient.returnedString;
                
		GetTREC_constraints.printTREC_ConstraintsToScreen("Current TREC Constraints at "+host+" are :", 
				trustConstraint, riskConstraint, 
				ecoConstraint, costConstraint);
		
		GetTREC_constraints.printTREC_ConstraintsToScreen("Set TREC Constraints to :", 
				trustLevel_constraint, riskLevel_constraint, 
				ecoValue_constraint, costInEuro_constraint);
		
		ACperServiceConstraintsRestClient acPerServiceConstraintsRestClient = new ACperServiceConstraintsRestClient(host,port,
				trustLevel_constraint, riskLevel_constraint,
                                ecoValue_constraint, costInEuro_constraint);
		
                assertEquals(acPerServiceConstraintsRestClient.status,200);
                System.out.println();
                System.out.println("Status : "+acPerServiceConstraintsRestClient.status);
                System.out.println();
		System.out.println("Message from Server : "+acPerServiceConstraintsRestClient.returnedString);
                System.out.println();
                
                trustClient = new RestClient_noInput_String(host,port,StartUrl+"/getTrust_Constraint");
                riskClient = new RestClient_noInput_String(host,port,StartUrl+"/getRisk_Constraint");
                ecoClient = new RestClient_noInput_String(host,port,StartUrl+"/getEco_Constraint");
                costClient = new RestClient_noInput_String(host,port,StartUrl+"/getCost_Constraint");
                
                trustConstraint = trustClient.returnedString;
		riskConstraint = riskClient.returnedString;
		ecoConstraint = ecoClient.returnedString;
		costConstraint = costClient.returnedString;
                
		GetTREC_constraints.printTREC_ConstraintsToScreen("New TREC Constraints at "+host+" are :", 
				trustConstraint, riskConstraint, 
				ecoConstraint, costConstraint);
		
                assertEquals(trustConstraint,trustLevel_constraint);
                assertEquals(riskConstraint,riskLevel_constraint);
                assertEquals(ecoConstraint,ecoValue_constraint);
                assertEquals(costConstraint,costInEuro_constraint);
                
	}//PerServiceConstraintsTesting()
        
        public static String getTrustConstraint(String host)
        {
                
		String port = GetServerDetails.Port;
                
                RestClient_noInput_String trustClient = new RestClient_noInput_String(host,port,StartUrl+"/getTrust_Constraint");
                
                String trustConstraint = trustClient.returnedString;
		
                if(trustConstraint.hashCode() == 0)
                    return trustLevel_constraint;
                
                return trustConstraint;
        }//getTrustConstraint()
        
        public static String getRiskConstraint(String host)
        {
                
        		String port = GetServerDetails.Port;
                
                RestClient_noInput_String riskClient = new RestClient_noInput_String(host,port,StartUrl+"/getRisk_Constraint");
                
                String riskConstraint = riskClient.returnedString;
		
                if(riskConstraint.hashCode() == 0)
                    return riskLevel_constraint;
                
                return riskConstraint;
        }//getRiskConstraint()
        
        public static String getEcoConstraint(String host)
        {
                
		String port = GetServerDetails.Port;
                
                RestClient_noInput_String ecoClient = new RestClient_noInput_String(host,port,StartUrl+"/getEco_Constraint");
                
                String ecoConstraint = ecoClient.returnedString;
		
                if(ecoConstraint.hashCode() == 0)
                    return ecoValue_constraint;
                
                return ecoConstraint;
        }//getEcoConstraint()
        
        public static String getCostConstraint(String host)
        {
                
        		String port = GetServerDetails.Port;
                
                RestClient_noInput_String costClient = new RestClient_noInput_String(host,port,StartUrl+"/getCost_Constraint");
                
		String costConstraint = costClient.returnedString;
                
                if(costConstraint.hashCode() == 0)
                    return costInEuro_constraint;
                
                return costConstraint;
        }//getCostConstraint()
}//class
