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

import eu.optimis.ac.gateway.acGatewayGothersExternalData.TRECofAservice.CostInfo;
import eu.optimis.ac.gateway.acGatewayGothersExternalData.TRECofAservice.EcoSPInfo;
import eu.optimis.ac.gateway.acGatewayGothersExternalData.TRECofAservice.RiskInfo;
import eu.optimis.ac.gateway.acGatewayGothersExternalData.TRECofAservice.TrustInfo;
import eu.optimis.ac.gateway.init_finish.Initialize;
import org.apache.log4j.Logger;

public class GetTrecOfAserviceValues {

	public String trust="";
	public String risk="";
	public String cost="";
	public String ecoSP="";
	
	public String finalMessage="";
	
	public GetTrecOfAserviceValues(String serviceManifest,String spId,String serviceId,Initialize initialize,Logger log)
	{
		//log.info(serviceManifest);
		
		TrustInfo trustInfo = new TrustInfo(initialize,serviceManifest,initialize.TrustIP,initialize.TrustPort,log);
		RiskInfo riskInfo = new RiskInfo(initialize,spId,serviceManifest,initialize.RiskIP, initialize.RiskPort,log);
                EcoSPInfo ecoSPInfo = new EcoSPInfo(initialize,serviceManifest,initialize.EcoIP, initialize.EcoPort,log);
		CostInfo costInfo = new CostInfo(initialize,serviceManifest,initialize.CostIP, initialize.CostPort,log);
                
		trust = trustInfo.Trust;
		log.info("Trust value: "+trust);
		
		risk = riskInfo.Risk;
		log.info("Risk value: "+risk);
		
                ecoSP = ecoSPInfo.EcoSP;
		log.info("Ecoefficiency value: "+ecoSP);
                
                cost= costInfo.Cost;
		log.info("Cost value: "+cost);
		
		finalMessage+=riskInfo.finalMessage;
		finalMessage+=costInfo.finalMessage;
		finalMessage+=trustInfo.finalMessage;
		finalMessage+=ecoSPInfo.finalMessage;
		
	}//Constructor
			
}//Class
