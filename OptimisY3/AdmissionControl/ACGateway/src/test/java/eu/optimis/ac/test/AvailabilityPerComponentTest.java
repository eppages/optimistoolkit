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

import com.sun.jersey.core.util.MultivaluedMapImpl;
import eu.optimis.ac.ACRestClients.MyRestClients.RestClient_noInput_String;
import eu.optimis.ac.gateway.utils.FileFunctions;
import eu.optimis.ac.gateway.utils.GetLogger;
import eu.optimis.ac.smanalyzer.SMAnalyzer;
import eu.optimis.ac.smanalyzer.smInfo.Availability;
import eu.optimis.ac.test.remoteTest.DoRemoteTest;
import eu.optimis.ac.test.remoteTest.GetServerDetails;
import javax.ws.rs.core.MultivaluedMap;
import junit.framework.TestCase;

public class AvailabilityPerComponentTest  extends TestCase{
    
    private String filename1 = "SM108/AvailabilityTest/SM_1c5.xml";
    private String filename2 = "SM108/AvailabilityTest/SM_1c4.xml";
    private String filename3 = "SM108/AvailabilityTest/SM_1c3.xml";
    private String filename5 = "SM108/AvailabilityTest/SM_1c1.xml";
    private String filename4 = "SM108/AvailabilityTest/SM_1c2.xml";
    
    private String serviceManifest1 = FileFunctions.readFileAsStringFromResources(filename1);
    private String serviceManifest2 = FileFunctions.readFileAsStringFromResources(filename2);
    private String serviceManifest3 = FileFunctions.readFileAsStringFromResources(filename3);
    private String serviceManifest4 = FileFunctions.readFileAsStringFromResources(filename4);
    private String serviceManifest5 = FileFunctions.readFileAsStringFromResources(filename5);
    
    public void testAvailabilityPerComponent()
    {
        
        SMAnalyzer smInfo1 = new SMAnalyzer(serviceManifest1,GetLogger.getLogger(),false);
        SMAnalyzer smInfo2 = new SMAnalyzer(serviceManifest2,GetLogger.getLogger(),false);
        SMAnalyzer smInfo3 = new SMAnalyzer(serviceManifest3,GetLogger.getLogger(),false);
        SMAnalyzer smInfo4 = new SMAnalyzer(serviceManifest4,GetLogger.getLogger(),false);
        SMAnalyzer smInfo5 = new SMAnalyzer(serviceManifest5,GetLogger.getLogger(),false);
        
        System.out.println("testAvailabilityPerComponent : ");System.out.println();
        
        System.out.println("filename1 : "+filename1+" ");
        printAvailabilityPerComponentToScreen(smInfo1.availability);
        System.out.println("filename2 : "+filename2+" ");
        printAvailabilityPerComponentToScreen(smInfo2.availability);
        System.out.println("filename3 : "+filename3+" ");
        printAvailabilityPerComponentToScreen(smInfo3.availability);
        System.out.println("filename4 : "+filename4+" ");
        printAvailabilityPerComponentToScreen(smInfo4.availability);
        System.out.println("filename5 : "+filename5+" ");
        printAvailabilityPerComponentToScreen(smInfo5.availability);
        
        System.out.println("----------------------------------------------");
    }//testAvailabilityPerComponent()
    
    public void testAvailabilityCsvFile()
    {
        
        System.out.println("testAvailabilityCsvFile : ");System.out.println();
        
        MultivaluedMap<String, String> formParams = new MultivaluedMapImpl();
		
        formParams.add("serviceManifest", serviceManifest1);
        formParams.add("serviceManifest", serviceManifest2);
        formParams.add("serviceManifest", serviceManifest3);
        formParams.add("serviceManifest", serviceManifest4);
        formParams.add("serviceManifest", serviceManifest5);
        
        String host = GetServerDetails.Host;
	String port = GetServerDetails.Port;
        
        DoRemoteTest dotest = new DoRemoteTest(formParams, host);
        
        assertEquals(dotest.status,200);
        
        RestClient_noInput_String availabilityClient = 
                new RestClient_noInput_String(
                host,port,
                "/ACGateway/csv/getCSV/"+"availability_sc.csv");
        
        String availability_csv_file_contents = availabilityClient.returnedString;
        
        System.out.println("Printing from "+host+" Availability_sc.csv : ");
        
        System.out.println(availability_csv_file_contents);
        
        System.out.println("----------------------------------------------");
    }//testAvailabilityCsvFile()
    
   private void printAvailabilityPerComponentToScreen(Availability availability)
   {
      for(int i=0;i<availability.componentId_KeyList.size();i++)
        {
            String componentId = availability.componentId_KeyList.get(i);
            
            if(availability.availability_Map.get(componentId).isEmpty())
                System.out.println("Availability of component :"+componentId+" is -");
            else
		{
                    String availability_value = availability.availability_Map.get(componentId).get(0);
				
                    System.out.println("Availability of component :"+componentId+" is "+availability_value);
		}
        }//for-i
      
   }//printAvailabilityPerComponentToScreen()
   
}//class
