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

public class TrustInfo {

	public String Trust="0.00";
	public String finalMessage ="t ";
	private int status = -1;
        private Logger log;
        private int StartCounter = 0;
        
	public TrustInfo(Initialize initialize, String serviceManifest,String host, String port, Logger the_log)
	{
		log = the_log;
		
		log.info("Trust info : "+host +" "+port);
		
                if(initialize.skipTRECLevel>0)
                    StartCounter = initialize.skipTRECLevel;
                else if(initialize.skipTRUSTLevel>0)
                    StartCounter = initialize.skipTRUSTLevel;
                
                for (int i = StartCounter; i < 3; i++) {
            
                    if (i == 0) {
                        getTrustInfoFromTRECcommon(host,port,serviceManifest);
                        if (Trust.contains("Error"))continue;
                        if(status == 200) break;
                    } else if (i == 1) {
                        getTrustInfoFromTRECclient(host,port,serviceManifest);
                        if (Trust.contains("Error"))continue;
                        if(status == 200) break;
                    } else {
                        getTrustInfoFromFile();
                        break;
                    }
            
                }//for-i
                
	}//constructor
	
        private void getTrustInfoFromTRECcommon(String host,String port,String serviceManifest)
        {
            log.info("Trust Info From TRECcommon");
            
            String url_String = "/AC_TRECcommon_aaS/TRECcommon/getTrust";
            
            TRECInfo trecInfo = new TRECInfo(host,port,serviceManifest,url_String,log);
        
            status = trecInfo.status;
        
            if(status == 200)
            {
                Trust = trecInfo.value;
            
            }//if(status == 200)
            
            finalMessage = "tC ";
                        
        }//getTrustInfoFromTRECcommon()
        
        private void getTrustInfoFromTRECclient(String host,String port,String serviceManifest)
        {
            log.info("Trust Info From TrustClient");
            
            String url_String = "/AC_TRECcommon_aaS/TRECclients/getTrust";
            
            TRECInfo trecInfo = new TRECInfo(host,port,serviceManifest,url_String,log);
        
            status = trecInfo.status;
        
            if(status == 200)
            {
                Trust = trecInfo.value;
            
            }//if(status == 200)
            
            if(StartCounter!=0)
                finalMessage = "t("+StartCounter+")L ";
            else    
                finalMessage = "tL ";
        }//getTrustInfoFromTRECclient()
        
        private void getTrustInfoFromFile()
        {
            log.info("Trust Info From file");
            
            Trust = "3";
            
            if(StartCounter!=0)
                finalMessage = "t("+StartCounter+")F ";
            else    
                finalMessage = "tF ";
        }//getTrustInfoFromFile()
        
}//class
