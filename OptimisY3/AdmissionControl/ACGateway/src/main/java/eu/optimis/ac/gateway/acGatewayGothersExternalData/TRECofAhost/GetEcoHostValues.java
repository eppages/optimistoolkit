/*
 * Copyright (c) 2013 National Technical University of Athens (NTUA)
 *	
 *   							MIT License
 *   
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */



package eu.optimis.ac.gateway.acGatewayGothersExternalData.TRECofAhost;

import com.sun.jersey.core.util.MultivaluedMapImpl;
import eu.optimis.ac.ACRestClients.MyRestClients.RestClient_MultivaluedMap_MultivaluedMap;
import eu.optimis.ac.gateway.init_finish.Initialize;
import eu.optimis.ac.gateway.utils.PropertiesUtils;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.ws.rs.core.MultivaluedMap;
import org.apache.log4j.Logger;

public class GetEcoHostValues {

        private List<String> Host_IDAsList;
	
        public Map <String,Double> EcoHostAsMap = new HashMap<String, Double>();
        
	public String finalMessage="eh ";
	
        private Logger log;
        
        private int StartCounter = 0;
        
	public GetEcoHostValues(Initialize initialize, String host,String port, List<String> HostIDAsList,Logger the_log)
	{
                log = the_log;
                
		log.info("Start of GetEcoHostValues"); 
		
                Host_IDAsList = HostIDAsList;
                
                log.info("Host_IDAsList : "+Host_IDAsList);
                
		log.info("EcoHost info : "+host +" "+port);
		
		String AC_TREC_host = PropertiesUtils.getBoundle("AC_TREC.ip");
		
		String AC_TREC_port = PropertiesUtils.getBoundle("AC_TREC.port");
                
                MultivaluedMap<String, String> formParams = new MultivaluedMapImpl();
                
                for(int i=0;i<Host_IDAsList.size();i++) {
                    formParams.add("host_id", Host_IDAsList.get(i));}
		
		formParams.add("host", host);
		formParams.add("port", port);
                
                if(initialize.skipTRECLevel>0)
                    StartCounter = initialize.skipTRECLevel;
                else if(initialize.skipECOhostLevel>0)
                    StartCounter = initialize.skipECOhostLevel;
                
                for(int i=StartCounter;i<3;i++)
                {
                    String url_String = null;
                
                    if(i==0) {url_String = getEcoHostInfoFromTRECcommon();}
                    else if(i==1){url_String = getEcoHostInfoFromTRECclient();}
                    else {getEcoHostInfoFromFile();break;}
                     
                    RestClient_MultivaluedMap_MultivaluedMap client =
                            new RestClient_MultivaluedMap_MultivaluedMap(
                            AC_TREC_host,AC_TREC_port,url_String,formParams,log);
                    
                    int status = client.status;
                    
                    if(status == 200)
                    {
                        for(int j=0;j<client.returnedMap.get("ecoHost").size();j++)
                        {   
                            String value = client.returnedMap.get("ecoHost").get(j);
                            EcoHostAsMap.put(Host_IDAsList.get(j), Double.parseDouble(value));
                            log.info("EcoHost("+Host_IDAsList.get(j)+")="+value);
                                    
                        }
                        checkForWrongInput();
                        break;
                    }//if
                    else {log.info("Status Wasn't 200 but : "+status);}
                    
                }//for-i
                
                
	}//Constructor
	
        private String getEcoHostInfoFromTRECcommon()
        {
            log.info("EcoHost Info From TRECcommon");
            
            finalMessage = "ehC ";
            
            return "/AC_TRECcommon_aaS/TRECcommon/getEcoHost"; 
            
        }//getEcoHostInfoFromTRECcommon()
        
        private String getEcoHostInfoFromTRECclient()
        {
            log.info("EcoHost Info From EcoHostClient");
            
            if(StartCounter!=0)
                finalMessage = "eh("+StartCounter+")L ";
            else    
                finalMessage = "ehL ";
            
            return "/AC_TRECcommon_aaS/TRECclients/getEcoHost"; 
            
        }//getEcoHostInfoFromTRECclient()
        
        private void getEcoHostInfoFromFile()
        {
            log.info("EcoHost Info From file");
            
            for(int i=0;i<Host_IDAsList.size();i++)	
            {   
                 String value = Integer.toString(3+i);
                 
                 EcoHostAsMap.put(Host_IDAsList.get(i), Double.parseDouble(value));
                 log.info("EcoHost("+Host_IDAsList.get(i)+")="+value);
            }
                
            if(StartCounter!=0)
                finalMessage = "eh("+StartCounter+")F ";
            else    
                finalMessage = "ehF ";
		
        }//getEcoHostInfoFromFile()
        
        private void checkForWrongInput()
        {
            for(int i=0;i<Host_IDAsList.size();i++)	
            {   
                
                Double value = EcoHostAsMap.get(Host_IDAsList.get(i));
                
                if(value == 0.0)
                {
                    log.info("--->EcoHost("+Host_IDAsList.get(i)+")="+value);
                    
                    EcoHostAsMap.remove(Host_IDAsList.get(i));
                    String new_value = Integer.toString(3+i);
                    EcoHostAsMap.put(Host_IDAsList.get(i), Double.parseDouble(new_value));
                    
                    log.info("EcoHost("+Host_IDAsList.get(i)+")="+new_value);
                    if(StartCounter!=0)
                        finalMessage = "eh("+StartCounter+")FF ";
                    else    
                        finalMessage = "ehFF ";
                }//value == 0.0
                 
            }//for-i
            
            
        }//checkForWrongInput()
}//Class
