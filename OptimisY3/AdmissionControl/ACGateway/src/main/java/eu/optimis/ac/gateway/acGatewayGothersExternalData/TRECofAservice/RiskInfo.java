/*
 * Copyright (c) 2013 National Technical University of Athens (NTUA)
 *	
 *   							MIT License
 *   
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */



package eu.optimis.ac.gateway.acGatewayGothersExternalData.TRECofAservice;

import eu.optimis.ac.gateway.init_finish.Initialize;
import org.apache.log4j.Logger;

public class RiskInfo {

	public String Risk = "</xml>";
	public String finalMessage ="r "; 
        private Logger log;
        private int status = -1;
        private int StartCounter = 0;
        
	public RiskInfo(Initialize initialize, String spId, String serviceManifest,String  host, String port, Logger the_log)
	{
            log = the_log;
            
                log.info("Risk info : " + host + " " + port);
                log.info("spId : "+spId);
                
                if(initialize.skipTRECLevel>0)
                    StartCounter = initialize.skipTRECLevel;
                else if(initialize.skipRISKLevel>0)
                    StartCounter = initialize.skipRISKLevel;
                
            for (int i = StartCounter; i < 3; i++) {
            
                if (i == 0) {
                    getRiskInfoFromTRECcommon(host,port,serviceManifest);
                    if(status == 200) break;
                } else if (i == 1) {
                    getRiskInfoFromTRECclient(host,port,serviceManifest);
                    if(status == 200) break;
                } else {
                    getRiskInfoFromFile();
                    break;
                }
                
            }//for-i
	    
	}//Constructor
	
        private void getRiskInfoFromTRECcommon(String host,String port,String serviceManifest)
        {
            log.info("Risk Info From TRECcommon");
            
            String url_String = "/AC_TRECcommon_aaS/TRECcommon/getRisk";
        
            TRECInfo trecInfo = new TRECInfo(host,port,serviceManifest,url_String,log);
        
            status = trecInfo.status;
        
            if(status == 200)
            {
                Risk = trecInfo.value;
            
            }//if(status == 200)
            
            log.info("Risk from Riskcommon:"+Risk);
            
            finalMessage = "rC ";
                        
        }//getRiskInfoFromTRECcommon()
        
        private void getRiskInfoFromTRECclient(String host,String port,String serviceManifest)
        {
            log.info("Risk Info From RiskClient");
            
            String url_String = "/AC_TRECcommon_aaS/TRECclients/getRisk";
        
            TRECInfo trecInfo = new TRECInfo(host,port,serviceManifest,url_String,log);
        
            status = trecInfo.status;
        
            if(status == 200)
            {
                Risk = trecInfo.value;
            
            }//if(status == 200)
            
            log.info("Risk from Riskclient:"+Risk);
            
            if(StartCounter!=0)
                finalMessage = "r("+StartCounter+")L ";
            else    
                finalMessage = "rL ";
        }//getRiskInfoFromTRECclient()
        
        private void getRiskInfoFromFile()
        {
            log.info("Risk Info From file");
            
            Risk = riskInfoFromFile();
            
            if(StartCounter!=0)
                finalMessage = "r("+StartCounter+")F ";
            else    
                finalMessage = "rF ";
        }//getRiskInfoFromFile()
        
        private String riskInfoFromFile()
        {
            return "<RiskAssesor><sp rank=\"0\" pof=\"2.0\">OPTIMUMWEB</sp></RiskAssesor>";
            
        }//riskInfoFromFile()
}//Class
