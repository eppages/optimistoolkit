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
import eu.optimis.ac.gateway.serviceManifestFunctions.ReadDecisionFromSM;
import eu.optimis.ac.gateway.utils.FileFunctions;
import eu.optimis.ac.test.remoteTest.ACdecision;
import eu.optimis.ac.test.remoteTest.DoRemoteTest;
import eu.optimis.ac.test.remoteTest.GetServerDetails;
import eu.optimis.manifest.api.impl.AllocationOfferDecision;
import java.util.Random;
import static junit.framework.Assert.assertEquals;
import junit.framework.TestCase;

public class TrecConstraintsTest extends TestCase{
    /*
    private String trustLevel_constraint = "0.0";
    private String ecoValue_constraint = "0.0";
    private String riskLevel_constraint = "7.0";
    private String costInEuro_constraint = "1000";
    */
    private Boolean ThrowRuntimeException = false;
    
    private String trustValue;
    private String riskValue;
    private String costValue;
    private String ecoValue;
    
    public void testTrecConstraints()
    {
        
        
        
        String trustConstraint = PerServiceConstraintsTest.getTrustConstraint(GetServerDetails.Host);
        String riskConstraint = PerServiceConstraintsTest.getRiskConstraint(GetServerDetails.Host);
        String ecoConstraint = PerServiceConstraintsTest.getEcoConstraint(GetServerDetails.Host);
        String costConstraint = PerServiceConstraintsTest.getCostConstraint(GetServerDetails.Host);
        
        System.out.println();
        System.out.println("TrecConstraintsTest"+" Started");
        
        String First = doTest("First",AllocationOfferDecision.accepted.toString(),
                PerServiceConstraintsTest.trustLevel_constraint, PerServiceConstraintsTest.ecoValue_constraint, 
                PerServiceConstraintsTest.riskLevel_constraint, PerServiceConstraintsTest.costInEuro_constraint);
        System.out.println("--------------------------------------------------------------------------------");
        
        trustValue = getFromResultCSV("trust");
        riskValue = getFromResultCSV("risk");
        ecoValue = getFromResultCSV("eco");
        costValue = getFromResultCSV("cost");
        
        String Second = doTest("Second Trust Constraint Test",AllocationOfferDecision.rejected.toString(),
                getRandomFloatString(3, 5), PerServiceConstraintsTest.ecoValue_constraint, 
                PerServiceConstraintsTest.riskLevel_constraint, PerServiceConstraintsTest.costInEuro_constraint);
        System.out.println("--------------------------------------------------------------------------------");
        
        String Third = doTest("Third Eco Constraint Test",AllocationOfferDecision.rejected.toString(),
                PerServiceConstraintsTest.trustLevel_constraint, getRandomFloatString(13, 20), 
                PerServiceConstraintsTest.riskLevel_constraint, PerServiceConstraintsTest.costInEuro_constraint);
        System.out.println("--------------------------------------------------------------------------------");
        
        String Forth = doTest("Forth Risk Constraint Test",AllocationOfferDecision.rejected.toString(),
                PerServiceConstraintsTest.trustLevel_constraint, PerServiceConstraintsTest.ecoValue_constraint, 
                getRandomFloatString(0,1), 
                PerServiceConstraintsTest.costInEuro_constraint);
        System.out.println("--------------------------------------------------------------------------------");
        /*
        String Fifth = doTest("Fifth Cost Constraint Test",AllocationOfferDecision.rejected.toString(),
                PerServiceConstraintsTest.trustLevel_constraint, PerServiceConstraintsTest.ecoValue_constraint, 
                PerServiceConstraintsTest.riskLevel_constraint, getRandomFloatString(1, 9));
        System.out.println("--------------------------------------------------------------------------------");
        */
        System.out.println("TrecConstraintsTest"+" Finished.");
        System.out.println();
        
        new ACperServiceConstraintsRestClient(GetServerDetails.Host,GetServerDetails.Port,
            trustConstraint, riskConstraint,
            ecoConstraint, costConstraint);
        System.out.println();
        
        System.out.println("OverallResults(wantedDecision,testDecision):");
        System.out.println("First                        : "+AllocationOfferDecision.accepted.toString()
                +" "+First);
        System.out.println("Second Trust Constraint Test : "+AllocationOfferDecision.rejected.toString()
                +" "+Second);
        System.out.println("Third    Eco Constraint Test : "+AllocationOfferDecision.rejected.toString()
                +" "+Third);
        System.out.println("Forth   Risk Constraint Test : "+AllocationOfferDecision.rejected.toString()
                +" "+Forth);
        /*
        System.out.println("Fifth   Cost Constraint Test : "+AllocationOfferDecision.rejected.toString()
                +" "+Fifth);
        */
        System.out.println();
        
        
    }//testTrecConstraints()
    
    private String doTest(String TestName,String WantedDecision,
            String trustLevel_constraint,String ecoValue_constraint,
            String riskLevel_constraint,String costInEuro_constraint)
    {
        System.out.println("Run "+TestName+" Started");
        
        ACperServiceConstraintsRestClient acPerServiceConstraintsRestClient = 
                new ACperServiceConstraintsRestClient(GetServerDetails.Host,GetServerDetails.Port,
                    trustLevel_constraint, riskLevel_constraint,
                    ecoValue_constraint, costInEuro_constraint);
		
        assertEquals(acPerServiceConstraintsRestClient.status,200);
        
        DoRemoteTest doTest = new DoRemoteTest(FileFunctions.readFileAsStringFromResources("SM108/DecisionTest/SM_1c2.xml"),"DefaultSolver");
        
        assertEquals(doTest.status,200);
         
        Boolean NotSuccess = ACdecision.printDecision(doTest.status, doTest.returnedSMs, ThrowRuntimeException, WantedDecision);
        
        System.out.println("Admission Control Decision Must be : "+WantedDecision);
        
        if((NotSuccess)&&(ThrowRuntimeException)) {
            System.out.println("Run "+TestName+" failed");
            throw new RuntimeException();
        }
        
        System.out.println();
        
        System.out.println("Run "+TestName+" Finished.");
        
        return ReadDecisionFromSM.DecisionIs(doTest.returnedSMs.get("serviceManifest").get(0));
    }//doTest()
 
    private int getRandomInteger(int aStart, int aEnd){
    if ( aStart > aEnd ) {
      throw new IllegalArgumentException("Start cannot exceed End.");
    }
    
    Random aRandom = new Random();

    //get the range, casting to long to avoid overflow problems
    long range = (long)aEnd - (long)aStart + 1;
    // compute a fraction of the range, 0 <= frac < range
    long fraction = (long)(range * aRandom.nextDouble());
    int randomNumber =  (int)(fraction + aStart);    
    
    return randomNumber;
    
  }//getRandomInteger()

  private String getRandomFloatString(int aStart,int aEnd)
  {
      return Integer.toString(getRandomInteger(aStart, aEnd))+"."+Integer.toString(getRandomInteger(0, 99));
  }//getRandomFloatString()

  private String getFromResultCSV(String parameter)
    {
        RestClient_noInput_String resultClient = 
                new RestClient_noInput_String(
                		GetServerDetails.Host,GetServerDetails.Port,
                "/ACGateway/csv/getCSV/result.csv");
        
        String resultCSV = resultClient.returnedString;
        
        String temp[] = resultCSV.substring(
                resultCSV.lastIndexOf(
                "\"**\",\"Trust for new service\",\"**\"\n" +"\n" +"\"a1\","))
                .split("\n");
        
        if(parameter.contains("trust"))
            return temp[2].replace("\"a1\",", "");
        if(parameter.contains("risk"))
            return temp[6].replace("\"a1\",", "");
        if(parameter.contains("eco"))
            return temp[10].replace("\"a1\",", "");
        if(parameter.contains("cost"))
            return temp[14].replace("\"a1\",", "");
        
        return "";
    }//setAClastServerLogs()
}//class

