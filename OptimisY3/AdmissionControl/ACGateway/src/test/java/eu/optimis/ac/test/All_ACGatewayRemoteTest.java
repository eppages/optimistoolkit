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
import eu.optimis.ac.gateway.utils.FileFunctions;
import eu.optimis.ac.gateway.utils.ReachableHost;
import eu.optimis.ac.gateway.utils.ReachableTomcat;
import eu.optimis.ac.test.remoteTest.ACdecision;
import eu.optimis.ac.test.remoteTest.DoRemoteTest;
import eu.optimis.ac.test.remoteTest.GetServerDetails;
import eu.optimis.manifest.api.impl.AllocationOfferDecision;
import javax.ws.rs.core.MultivaluedMap;
import junit.framework.TestCase;

public class All_ACGatewayRemoteTest extends TestCase {

        private static Boolean RejectionRuntimeException = true;
        
	public void testR(){;}
	
        public static String All_IPs(int i)
        {
                        String host = "localhost";
            
                        //int x=1;if(x==1)return null;
                        
                        if(i==2)return null;
                        if(i==4)return null;
                        //if(i==3)return null;
                        
			if(i==0)host="212.0.127.140";
			else if(i==1)host="130.239.48.6";
			else if(i==3)host="109.231.120.19";
			else if(i==2)host="213.27.211.124";
                        else if(i==4)host="109.231.122.54";
                        else if(i==5)host="82.223.250.34";
                        else if(i==6)host="172.16.8.220";
                        else if(i==7)host="88.198.134.18";
                        else if(i==8)host="109.231.86.35";
                        
                        //if(!ReachableHost.isReachable(host)){System.out.println(host+" is unreachable");return null;}
                        if(!ReachableTomcat.isReachable(host, "8080")){System.out.println(host+" Tomcat is unreachable");return null;}
                        
                        return host;
        }//All_IPs()
        
	public void testAll_RemoteACG() {
		
            String RejectionReport = "";
            
            String host = "localhost";
            
            if(GetServerDetails.Host.contains("localhost"))return;
            
            for(int i=0;i<9;i++)
            {           
                        
                        host = All_IPs(i);
                        if(host==null)continue;
                        
                        RejectionReport = doTest(FileFunctions.readFileAsStringFromResources(ACdecisionTest.tempfileNameForAcceptance),host,RejectionReport);
                        //RejectionReport = doTest(FileFunctions.readFileAsStringFromResources(ACdecisionTest.fileNameForPartial),host,RejectionReport);
		}//for
		
                if(RejectionReport.hashCode() != "".hashCode())
                    throw new RuntimeException(RejectionReport);
                
                System.out.println();
	}//testAll_RemoteACG()

       public static String doTest(String serviceManifest,String host,String RejectionReport)
       {
            
            MultivaluedMap<String, String> formParams = new MultivaluedMapImpl();

            formParams.add("serviceManifest", serviceManifest);
            formParams.add("cleanTestbed", "");
           
            DoRemoteTest dotest = new DoRemoteTest(formParams,host,"DefaultSolver");
                
            assertEquals(dotest.status,200);
                
            Boolean success = ACdecision.printDecision(dotest.status, dotest.returnedSMs, RejectionRuntimeException,
                                AllocationOfferDecision.rejected.toString());           
            if(!success)
                RejectionReport+="\n"+host+" : "+" Rejection of SM";
            
            return RejectionReport;
       }//doTest()
       
}//class
