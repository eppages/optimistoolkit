/*
 * Copyright (c) 2013 National Technical University of Athens (NTUA)
 *	
 *   							MIT License
 *   
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */


package eu.optimis.ac.test.remoteTest;

import eu.optimis.ac.gateway.serviceManifestFunctions.ReadDecisionFromSM;
import eu.optimis.manifest.api.impl.AllocationOfferDecision;
import javax.ws.rs.core.MultivaluedMap;

public class ACdecision {
    
    public static void printDecision(int status, MultivaluedMap<String, String> returnedSMs)
    {
            printDecision(status,returnedSMs,false,null,false);
            
    }//printDecision(int status, MultivaluedMap<String, String> returnedSMs)
    
    public static void printDecisionWithWriteSM(int status, MultivaluedMap<String, String> returnedSMs)
    {
            printDecision(status,returnedSMs,false,null,true);
            
    }//printDecision(int status, MultivaluedMap<String, String> returnedSMs,)
    
    public static Boolean printDecision(int status, MultivaluedMap<String, String> returnedSMs, Boolean ThrowRuntimeException, String WantedDecision)
    {
            return printDecision(status,returnedSMs,ThrowRuntimeException,WantedDecision,false);
            
    }//printDecision(int status, MultivaluedMap<String, String> returnedSMs,Boolean ThrowRuntimeException,String WantedDecision)
    
    private static Boolean printDecision(int status, MultivaluedMap<String, String> returnedSMs, Boolean ThrowRuntimeException, String WantedDecision, Boolean WriteSMtoFile)
    {
            Boolean success = true;
            
            for(int i=0;i<returnedSMs.get("serviceManifest").size();i++)
            {
                String serviceManifest = returnedSMs.get("serviceManifest").get(i);
                
                if(returnedSMs.get("serviceManifest").size()>1)
                {
                        if(i==0)System.out.println("Status is: "+status);
                	
                        System.out.print("printing SM "+(i+1)+"/"+returnedSMs.get("serviceManifest").size()+"    AC Decision is : ");
                        System.out.print(ReadDecisionFromSM.DecisionIs(serviceManifest));
                }//if
                else    
                {
                        System.out.print("Status is: "+status+"    AC Decision is : ");
                        System.out.print(ReadDecisionFromSM.DecisionIs(serviceManifest));
                }//else
            	
                success = isTestSuccess( serviceManifest, WantedDecision, ThrowRuntimeException);
                
                if(WriteSMtoFile)
                    WriteSM.WriteSMtoFile(serviceManifest,Integer.toString(i+1));
                else
                    System.out.println();
                
            }//for-i
            
            return success;
    }//printDecision(int status, MultivaluedMap<String, String> returnedSMs, Boolean ThrowRuntimeException, String WantedDecision, Boolean WriteSMtoFile)
    
    private static Boolean isTestSuccess(String serviceManifest, String WantedDecision, Boolean ThrowRuntimeException)
    {
            Boolean success = true; 
            
            if(WantedDecision==null)return success;
            
            if(WantedDecision.equals(AllocationOfferDecision.rejected.toString()))
                if((ThrowRuntimeException)&&(ReadDecisionFromSM.isDecisionRejected(serviceManifest)))
                    success = false;
                
            if(WantedDecision.equals(AllocationOfferDecision.partial.toString()))
                if((ThrowRuntimeException)&&(ReadDecisionFromSM.isDecisionPartialAccepted(serviceManifest)))
                    success = false;
                
            if(WantedDecision.equals(AllocationOfferDecision.accepted.toString()))
                if((ThrowRuntimeException)&&(ReadDecisionFromSM.isDecisionAccepted(serviceManifest)))
                    success = false;
            
            return success;
    }//isTestSuccess()
}//class
