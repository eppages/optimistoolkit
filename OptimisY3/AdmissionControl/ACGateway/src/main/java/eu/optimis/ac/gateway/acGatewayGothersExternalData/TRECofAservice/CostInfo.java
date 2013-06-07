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
import eu.optimis.ac.gateway.utils.FileFunctions;
import org.apache.log4j.Logger;

public class CostInfo {
	
	public String Cost = "</xml>";
	public String finalMessage ="c "; 
        private Logger log;
        private int status = -1;
        private int StartCounter = 2;
        
	public CostInfo(Initialize initialize, String serviceManifest,String host, String port,Logger the_log)
	{
		log = the_log;
                
		log.info("Cost info : "+host +" "+port);
                
                /*
                if(initialize.skipTRECLevel>0)
                    StartCounter = initialize.skipTRECLevel;
                else if(initialize.skipCOSTLevel>0)
                    StartCounter = initialize.skipCOSTLevel;
                */
                
                for (int i = StartCounter; i < 3; i++) {
            
                    if (i == 0) {
                        getCostInfoFromTRECcommon(host,port,serviceManifest);
                        if(status == 200) break;
                    } else if (i == 1) {
                        getCostInfoFromTRECclient(host,port,serviceManifest);
                        if(status == 200) break;
                    } else {
                        getCostInfoFromFile();
                        break;
                    }
            
                }//for-i
                
	}//constructor
        
        private void getCostInfoFromTRECcommon(String host,String port,String serviceManifest)
        {
            log.info("Cost Info From TRECcommon");
            
            String url_String = "/AC_TRECcommon_aaS/TRECcommon/getCost";
        
            TRECInfo trecInfo = new TRECInfo(host,port,serviceManifest,url_String,log);
        
            status = trecInfo.status;
        
            if(status == 200)
            {
                Cost = trecInfo.value;
            
            }//if(status == 200)
            
            finalMessage = "cC ";
                        
        }//getCostInfoFromTRECcommon()
        
        private void getCostInfoFromTRECclient(String host,String port,String serviceManifest)
        {
            log.info("Cost Info From Costclient");
            
            String url_String = "/AC_TRECcommon_aaS/TRECclients/getCost";
        
            TRECInfo trecInfo = new TRECInfo(host,port,serviceManifest,url_String,log);
        
            status = trecInfo.status;
        
            if(status == 200)
            {
                Cost = trecInfo.value;
            
            }//if(status == 200)
            
            if(StartCounter!=0)
                finalMessage = "c("+StartCounter+")L ";
            else    
                finalMessage = "cL ";
        }//getCostInfoFromTRECclient()
        
        private void getCostInfoFromFile()
        {
            log.info("Cost Info From file");
            
            Cost = FileFunctions.readFileAsStringFromResources("cost.xml",log);
            
            if(StartCounter!=0)
                finalMessage = "c("+StartCounter+")F ";
            else    
                finalMessage = "cF ";
        }//getCostInfoFromFile()
        
}//class
