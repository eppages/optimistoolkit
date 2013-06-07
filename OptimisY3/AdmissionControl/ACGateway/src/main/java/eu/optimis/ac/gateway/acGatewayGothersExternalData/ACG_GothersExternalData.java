/*
 * Copyright (c) 2013 National Technical University of Athens (NTUA)
 *	
 *   							MIT License
 *   
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */



package eu.optimis.ac.gateway.acGatewayGothersExternalData;

import com.sun.jersey.core.util.MultivaluedMapImpl;
import eu.optimis.ac.gateway.acGatewayGothersExternalData.TRECofAhost.GetEcoHostValues;
import eu.optimis.ac.gateway.acGatewayGothersExternalData.TRECofAhost.GetHost_IDs;
import eu.optimis.ac.gateway.acGatewayGothersExternalData.TRECofAhost.GetRiskHostValues;
import eu.optimis.ac.gateway.acGatewayGothersExternalData.TRECofAhost.SortHostsIdList_MinIsFirst;
import eu.optimis.ac.gateway.init_finish.Initialize;
import javax.ws.rs.core.MultivaluedMap;
import org.apache.log4j.Logger;

public class ACG_GothersExternalData {
	
	public MultivaluedMap<String, String> formParams = new MultivaluedMapImpl();
	
	public String finalMessage="";
	
	public ACG_GothersExternalData(MultivaluedMap<String, String> Params,
                MultivaluedMap<String, String> formParams, Initialize initialize, Logger log)
	{
		String physicalHostsInfo = getPhysicalHostsInfo(initialize,formParams,log);
		
		getTRECofAhostInfo(formParams,physicalHostsInfo,initialize,log);
                
		getTRECofAserviceInfo(Params,initialize,log);
		
	}//constructor
	
	private String getPhysicalHostsInfo(Initialize initialize,MultivaluedMap<String, String> Params,Logger log)
	{
                
                 GetPhysicalHostsInfo getPhysicalHostsInfo = new GetPhysicalHostsInfo(initialize,initialize.ip_Id,Params,initialize.physicalHostsInfo_aaS_IP,initialize.physicalHostsInfo_aaS_Port,log);
                 
                 finalMessage+=getPhysicalHostsInfo.finalMessage;
                 
                 formParams.add("physicalHostsInfo", getPhysicalHostsInfo.physicalHostsInfo);
                 
                 return getPhysicalHostsInfo.physicalHostsInfo;
                 
	}//getPhysicalHostsInfo()
	
	private void getTRECofAserviceInfo(MultivaluedMap<String, String> Params,Initialize initialize,Logger log)
	{
		for(int i=0;i<Params.get("serviceManifest").size();i++)
		{				
			String serviceManifest=Params.get("serviceManifest").get(i);
			
			String spId = Params.get("spId").get(i);
			String serviceId = Params.get("serviceId").get(i);
			
			GetTrecOfAserviceValues tv = new GetTrecOfAserviceValues(serviceManifest,spId,serviceId,initialize,log);
			if(i==0)finalMessage+=tv.finalMessage;
			
			formParams.add("serviceManifest", serviceManifest);
			formParams.add("trust", tv.trust);
			formParams.add("risk", tv.risk);
			formParams.add("eco", tv.ecoSP);
			formParams.add("cost", tv.cost);
			formParams.add("serviceId", serviceId);
			
		}//for-i each serviceManifest
		
	}//getTRECofAserviceInfo()
	
        private void getTRECofAhostInfo(MultivaluedMap<String, String> Params,
                String physicalHostsInfo,Initialize initialize,Logger log)
        {
            GetHost_IDs getHost_IDs = new GetHost_IDs(physicalHostsInfo);
            
            GetRiskHostValues getRiskHost = new GetRiskHostValues(initialize,Params,
                    initialize.RiskIP,initialize.RiskPort,getHost_IDs.Host_IDAsList,log);
            finalMessage+=getRiskHost.finalMessage;
            
            GetEcoHostValues getEcoHost = new GetEcoHostValues(initialize,initialize.EcoIP,initialize.EcoPort,getHost_IDs.Host_IDAsList,log);
            finalMessage+=getEcoHost.finalMessage;
            
            for(int i=0;i<getHost_IDs.Host_IDAsList.size();i++)
            {
                String HostNameValue = getHost_IDs.Host_IDAsList.get(i);
                log.info("HostNameValue : "+HostNameValue);
                String ecoHostValue = Double.toString(getEcoHost.EcoHostAsMap.get(HostNameValue));
                log.info("ecoHostValue : "+ecoHostValue);
                String riskHostValue = Double.toString(getRiskHost.RiskHostAsMap.get(HostNameValue));
                log.info("riskHostValue : "+riskHostValue);
                
                formParams.add("riskHost_UNSORTED", riskHostValue);
		formParams.add("ecoHost_UNSORTED", ecoHostValue);
                formParams.add("HostName_UNSORTED", HostNameValue);
                
                log.info("HostName_UNSORTED = "+HostNameValue+" riskHost_UNSORTED = "+riskHostValue);
            }//for - each HostName
            
            SortHostsIdList_MinIsFirst sort = new 
                    SortHostsIdList_MinIsFirst(
                    getHost_IDs.Host_IDAsList,
                    getRiskHost.RiskHostAsMap,log);
            
            for(int i=0;i<sort.HostIdAsList.size();i++)
            {
                String HostNameValue = sort.HostIdAsList.get(i);
                String ecoHostValue = Double.toString(getEcoHost.EcoHostAsMap.get(HostNameValue));
                String riskHostValue = sort.RiskHostAsList.get(i);
                
                formParams.add("riskHost", riskHostValue);
		formParams.add("ecoHost", ecoHostValue);
                formParams.add("HostName", HostNameValue);
                
                log.info("HostName = "+HostNameValue);
            }//for - each HostName
            
        }//getTRECofAhostInfo()
}//class
