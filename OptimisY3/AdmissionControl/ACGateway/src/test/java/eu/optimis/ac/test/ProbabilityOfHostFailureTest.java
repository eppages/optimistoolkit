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

import java.util.ArrayList;
import java.util.Arrays;
import com.sun.jersey.core.util.MultivaluedMapImpl;
import eu.optimis.ac.ACRestClients.MyRestClients.RestClient_noInput_String;
import eu.optimis.ac.gateway.serviceManifestFunctions.ReadAllocationPatternFromSM;
import eu.optimis.ac.smanalyzer.SMAnalyzer;
import eu.optimis.ac.test.affinityTest.print.PrintAllocationOffer;
import eu.optimis.ac.test.produceSM.ProduceManifest;
import eu.optimis.ac.test.remoteTest.ACdecision;
import eu.optimis.ac.test.remoteTest.DoRemoteTest;
import eu.optimis.ac.test.remoteTest.GetServerDetails;
import javax.ws.rs.core.MultivaluedMap;
import static junit.framework.Assert.assertEquals;
import junit.framework.TestCase;

public class ProbabilityOfHostFailureTest extends TestCase{
    
	public void testConsiderationPropabilityOfHostFailure()
	{
		String host = GetServerDetails.Host;
		ConsiderationPropabilityOfHostFailure(host);
		
	}//testConsiderationPropabilityOfHostFailure()
	
    public void ConsiderationPropabilityOfHostFailure(String host)
    {
        System.out.println("Consideration PropabilityOfHostFailure Started :");System.out.println();
        
        String port = GetServerDetails.Port;
        
        String serviceManifest = GetSM();		
        
        String result1 = doTest(host,port,0,serviceManifest);
        
        String result2 = doTest(host,port,1,serviceManifest);
        
        if(result1.hashCode()==result2.hashCode())
            throw new RuntimeException();
        else
        {
            System.out.println();System.out.println();
            System.out.println(" First run returned : "+result1);
            System.out.println("Second run returned : "+result2);
            System.out.println();System.out.println();
            System.out.println("Allocation Pattern in both Cases are different");
            System.out.println();
            System.out.println("Test is Success");
            System.out.println();System.out.println();
        }
        for(int i=0 ; i<2; i++)
        {
        
            
            
        }//for-i
        
    }//ConsiderationPropabilityOfHostFailure()

    private String doTest(String host,String port,int i,String serviceManifest)
    {
        MultivaluedMap<String, String> formParams = new MultivaluedMapImpl();
        formParams.add("serviceManifest", serviceManifest);
        formParams.add("doNotBackupSMflag", "True");
        formParams.add("cleanTestbed", "");
        
        System.out.println("Doing Run : "+(i+1));
            
            if(i==1)
                formParams.add("useRiskOfHostFromFile", "MinLastMaxFirst");
            else if (i==0)
                formParams.add("useRiskOfHostFromFile", "MinFirstMaxLast");
            
            SMAnalyzer manifestInfo = new SMAnalyzer(serviceManifest);
            
            DoRemoteTest doTest = new DoRemoteTest(formParams, host);
            
            assertEquals(doTest.status,200);
            
            ACdecision.printDecision(doTest.status, doTest.returnedSMs, false, null);
            
            System.out.println();
            
            RestClient_noInput_String riskHostClient = 
                new RestClient_noInput_String(
                host,port,
                "/ACGateway/csv/getCSV/"+"riskHost.csv");
        
            String riskHost_csv_file_contents = riskHostClient.returnedString;
        
            System.out.println("Printing from "+host+" riskHost.csv : ");
        
            System.out.println(riskHost_csv_file_contents);
            System.out.println();
            
            RestClient_noInput_String riskHostClient_UNSORTED = 
                new RestClient_noInput_String(
                host,port,
                "/ACGateway/csv/getCSV/"+"riskHost_UNSORTED.csv");
        
            String riskHost_csv_file_contents_UNSORTED = riskHostClient_UNSORTED.returnedString;
        
            System.out.println("Printing from "+host+" riskHost_UNSORTED.csv : ");
        
            System.out.println(riskHost_csv_file_contents_UNSORTED);
            System.out.println();
            
            
            RestClient_noInput_String ecoHostClient = 
                new RestClient_noInput_String(
                host,port,
                "/ACGateway/csv/getCSV/"+"ecoHost.csv");
        
            String ecoHost_csv_file_contents = ecoHostClient.returnedString;
        
            System.out.println("Printing from "+host+" ecoHost.csv : ");
        
            System.out.println(ecoHost_csv_file_contents);
            System.out.println();
            
            RestClient_noInput_String ecoHostClient_UNSORTED = 
                new RestClient_noInput_String(
                host,port,
                "/ACGateway/csv/getCSV/"+"ecoHost_UNSORTED.csv");
        
            String ecoHost_csv_file_contents_UNSORTED = ecoHostClient_UNSORTED.returnedString;
        
            System.out.println("Printing from "+host+" ecoHost_UNSORTED.csv : ");
        
            System.out.println(ecoHost_csv_file_contents_UNSORTED);
            System.out.println();
            
            
            RestClient_noInput_String riskHostsInfoClient = 
                new RestClient_noInput_String(
                host,port,
                "/ACGateway/csv/getHostsInfoFile");
        
            String riskHostInfo_csv_file_contents = riskHostsInfoClient.returnedString;
        
            System.out.println("Printing from "+host+" hostsInfo.csv : ");
        
            System.out.println(riskHostInfo_csv_file_contents);
            System.out.println();
            
            String returnedSM = doTest.returnedSMs.get("serviceManifest").get(0);
            
            PrintAllocationOffer.printAllocationOfferForCertainSM(returnedSM, manifestInfo);
            
            ReadAllocationPatternFromSM allocationPattern =
                new ReadAllocationPatternFromSM(returnedSM);
            
            System.out.println();
            
            System.out.println("Finished Run : "+(i+1));
            System.out.println("----------------------------------------------");
            
            return ReadAllocationPatternFromSM.getAllocationPatternAsOneString(manifestInfo, allocationPattern);
    }//doTest()
    
    private String GetSM()
    {
         ArrayList<Integer> affinityConstraints_List = new ArrayList<Integer>(Arrays.asList(0,0,0));
         ArrayList<Integer> antiAffinityConstraints_List = new ArrayList<Integer>(Arrays.asList(0,0,0));
         
         String antiAffinityRules = "1,2,Low";
        
         String affinityRules = "1,2,Low";
        
         ArrayList<String> componentId_List = new ArrayList<String>(Arrays.asList("compA","compB","compC")); 
         
         ArrayList<Integer> AvailabilityP1M_List = new ArrayList<Integer>(Arrays.asList(100,100,100));
         
         ArrayList<Integer> UpperBound_List = new ArrayList<Integer>(Arrays.asList(3,2,2));
         ArrayList<Integer> Initial_List = new ArrayList<Integer>(Arrays.asList(1,1,1));
         
         String serviceManifestAsString = ProduceManifest.manifestAsString(
                 componentId_List,"ACTestsPropabilityOfHostFailureTest",Boolean.FALSE,
                 "2",
                 UpperBound_List,
                 Initial_List,
                 AvailabilityP1M_List,
                 affinityConstraints_List, antiAffinityConstraints_List,
                 affinityRules,antiAffinityRules);
         
         return serviceManifestAsString;
         
    }//GetSM()
}//class
