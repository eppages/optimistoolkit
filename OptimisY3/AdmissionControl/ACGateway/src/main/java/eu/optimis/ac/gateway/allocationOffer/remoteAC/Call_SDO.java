/*
 * Copyright (c) 2013 National Technical University of Athens (NTUA)
 *	
 *   							MIT License
 *   
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */


package eu.optimis.ac.gateway.allocationOffer.remoteAC;

import eu.optimis.ds.client.DeploymentServiceClient;
import java.io.IOException;
import org.apache.log4j.Logger;

public class Call_SDO {
    
       protected static String getRemoteManifest(String serviceManifest, Logger log)
       {
                String SDO_result = call_SDO(serviceManifest, log);
           
                String ReturnedServiceManifest = parseOfferAsString(SDO_result);
                
                //log.info("ReturnedServiceManifest : "+ReturnedServiceManifest);
                
                log.info("Offer parsing finished");
                
                if(!ReturnedServiceManifest.contains("AllocationOffer"))
                    log.error("No AllocationOffer on the Returned Manifest");
                
                return ReturnedServiceManifest;
       }//getRemoteManifest()
    
       private static String call_SDO(String serviceManifest, Logger log)
        {
            String sdoURL = "http://localhost:8087";
                DeploymentServiceClient  sdoClient = new DeploymentServiceClient(sdoURL);
                
                
                String manifestXML = serviceManifest;
                String objective = "COST";
                
                String result=null;
            try {
                result = sdoClient.outSourceVMs(manifestXML, objective);
            } catch (IOException ex) {
                log.error(ex.getMessage());
            }
                
                //log.info("sdo Result : "+result);
                
                return result;
        }//Call_SDO()
        
        private static String parseOfferAsString(String result)
        {
            return result.substring(
                    result.indexOf("<opt:ServiceManifest"), 
                    result.indexOf("</opt:ServiceManifest>"))
                    +"</opt:ServiceManifest>";
            
        }//parseOfferAsString()
}//class
