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

public class GetRiskHostValues {
    
    private List<String> Host_IDAsList;
    
    public Map <String,Double> RiskHostAsMap = new HashMap<String, Double>();
	
    public String finalMessage="rh ";
	
    private Logger log;
    
    private int StartCounter = 0;
        
	public GetRiskHostValues(Initialize initialize, MultivaluedMap<String, String> Params,
                String host,String port, List<String> HostIDAsList,Logger the_log)
	{
                log = the_log;
                
		log.info("Start of GetRiskHostValues"); 
		
                Host_IDAsList = HostIDAsList;
                
                log.info("Host_IDAsList : "+Host_IDAsList);
                
                if(Params.containsKey("useRiskOfHostFromFile"))
                    {getForcedRiskInfoFromFile(Params);return;}
                
		log.info("RiskHost info : "+host +" "+port);
		
		String AC_TREC_host = PropertiesUtils.getBoundle("AC_TREC.ip");
		
		String AC_TREC_port = PropertiesUtils.getBoundle("AC_TREC.port");
                
                MultivaluedMap<String, String> formParams = new MultivaluedMapImpl();
                
                for(int i=0;i<Host_IDAsList.size();i++) {
                    formParams.add("host_id", Host_IDAsList.get(i));}
		
		formParams.add("host", host);
		formParams.add("port", port);
                
                if(initialize.skipTRECLevel>0)
                    StartCounter = initialize.skipTRECLevel;
                else if(initialize.skipRISKhostLevel>0)
                    StartCounter = initialize.skipRISKhostLevel;
                
                for(int i=StartCounter;i<3;i++)
                {
                    String url_String = null;
                
                    if(i==0) {url_String = getRiskHostInfoFromTRECcommon();}
                    else if(i==1){url_String = getRiskHostInfoFromTRECclient();}
                    else {getRiskHostInfoFromFile();break;}
                     
                    RestClient_MultivaluedMap_MultivaluedMap client =
                            new RestClient_MultivaluedMap_MultivaluedMap(
                            AC_TREC_host,AC_TREC_port,url_String,formParams,log);
                    
                    int status = client.status;
                    
                    if(status == 200)
                    {
                        for(int j=0;j<client.returnedMap.get("riskHost").size();j++)
                        {
                            String value = client.returnedMap.get("riskHost").get(j);
                            RiskHostAsMap.put(Host_IDAsList.get(j), Double.parseDouble(value));
                            log.info("RiskHost("+Host_IDAsList.get(j)+")="+value);
                        }
                        break;
                    }//if
                    else {log.info("Status Wasn't 200 but : "+status);}
                    
                }//for-i
                
	}//Constructor
	
        private String getRiskHostInfoFromTRECcommon()
        {
            log.info("RiskHost Info From TRECcommon");
            
            finalMessage = "rhC ";
            
            return "/AC_TRECcommon_aaS/TRECcommon/getRiskHost"; 
            
        }//getRiskHostInfoFromTRECcommon()
        
        private String getRiskHostInfoFromTRECclient()
        {
            log.info("RiskHost Info From RiskHostClient");
            
            if(StartCounter!=0)
                finalMessage = "rh("+StartCounter+")L ";
            else    
                finalMessage = "rhL ";
            
            return "/AC_TRECcommon_aaS/TRECclients/getRiskHost"; 
            
        }//getRiskHostInfoFromTRECclient()
        
        private void getRiskHostInfoFromFile()
        {
            log.info("RiskHost Info From File");
            
            for(int i=0;i<Host_IDAsList.size();i++)	
            {
                String value = "0.01";
                RiskHostAsMap.put(Host_IDAsList.get(i), Double.parseDouble(value));
                log.info("RiskHost("+Host_IDAsList.get(i)+")="+value);
            }//if-i    
            
            if(StartCounter!=0)
                finalMessage = "rh("+StartCounter+")F ";
            else    
                finalMessage = "rhF ";
        }//getRiskHostInfoFromFile()
        
        private void getForcedRiskInfoFromFile(MultivaluedMap<String, String> formParams)
        {
            log.info("RiskHost Info From Forced File");
            
            if(formParams.get("useRiskOfHostFromFile").get(0).contains("MinLastMaxFirst"))
            for(int i=Host_IDAsList.size();i>0;i--)
            {
                String value = "0."+Integer.toString(1+Host_IDAsList.size()-i+1);
                log.info("value = "+value +" Host_IDAsList.get(i-1) = "+Host_IDAsList.get(i-1));
                RiskHostAsMap.put(Host_IDAsList.get(i-1), Double.parseDouble(value));
            }//if-i
            
            else if(formParams.get("useRiskOfHostFromFile").get(0).contains("MinFirstMaxLast"))
            for(int i=0;i<Host_IDAsList.size();i++)	
            {
                String value = "0."+Integer.toString(i+1);
                RiskHostAsMap.put(Host_IDAsList.get(i), Double.parseDouble(value));
            }//if-i
            
            if(StartCounter!=0)
                finalMessage = "rh("+StartCounter+")f ";
            else    
                finalMessage = "rhf ";
        }//getForcedRiskInfoFromFile()        
        
}//Class
