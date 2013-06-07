/*
 * Copyright (c) 2013 National Technical University of Athens (NTUA)
 *	
 *   							MIT License
 *   
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package eu.optimis.ac.ac_treccommon_aas.api;

import com.sun.jersey.core.util.MultivaluedMapImpl;
import eu.optimis.ac.ac_treccommon_aas.trec.ListFunctions;
import eu.optimis.ac.ac_treccommon_aas.trec.RiskAsString;
import eu.optimis.ac.smanalyzer.SMAnalyzer;
import eu.optimis.treccommon.TrecApiIP;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import org.apache.log4j.Logger;

@Path("/TRECcommon")
public class AC_TRECcommon {
	
        public static Logger log = Logger.getLogger(AC_TRECcommon.class);
        
	@POST
        @Path("/getRisk")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public static String getRisk(MultivaluedMap<String, String> Params)
	{
		String serviceManifest = Params.get("serviceManifest").get(0);
                String host = Params.get("host").get(0);
		int port = Integer.parseInt(Params.get("port").get(0));
                
		SMAnalyzer smAnalyzer = new SMAnalyzer(serviceManifest);
                String spId = smAnalyzer.spId;
                log.info("spId : "+spId);	
		
		TrecApiIP taip = new TrecApiIP(host, port);
                
                String riskValue = null;
                
                riskValue = RiskAsString.getXml
                        (taip.RISK.preNegotiateSPDeploymentPhase(spId, serviceManifest));
                
                log.info("Risk Common Value: "+riskValue);
                
		return riskValue;
		
	}//getRisk()
	
	@POST
        @Path("/getCost")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public static String getCost(MultivaluedMap<String, String> Params)
	{
		String serviceManifest = Params.get("serviceManifest").get(0);
		String host = Params.get("host").get(0);
		int port = Integer.parseInt(Params.get("port").get(0));
		
		TrecApiIP taip = new TrecApiIP(host, port);
                
                String Cost = null;
            try {
                
                Cost = taip.COST.forecastIPCost(host, port, serviceManifest);
                
                //Cost = taip.COST.predictIPCost(host,port,serviceManifest);
                
                Cost = null;
                
            } catch (NumberFormatException ex) {
                log.error(ex.getMessage());
            } catch (Exception ex) {
                log.error(ex.getMessage());
            }
                
                log.info("Cost Common Value: "+Cost);
                
                return Cost;
	}//getCost()
	
	@POST
        @Path("/getTrust")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public static String getTrust(MultivaluedMap<String, String> Params)
	{
		String serviceManifest = Params.get("serviceManifest").get(0);	
		String host = Params.get("host").get(0);
		int port = Integer.parseInt(Params.get("port").get(0));
		
                SMAnalyzer smAnalyzer = new SMAnalyzer(serviceManifest);
                
                String spId = smAnalyzer.spId;
                
                log.info("spId : "+spId);
                
		TrecApiIP taip = new TrecApiIP(host, port);
                
                String Trust = taip.TRUST.getSPDeploymentTrust(spId);
                
                log.info("Trust : "+Trust);
                
                if(Trust.contains("Error"))
                    throw new RuntimeException("Trust Common Error");
                
                if(Trust.contains("."))
                {
                    String[] temp = Trust.replace(".", " ").split(" ");
                    
                    if(temp[1].length()>2)
                        Trust = temp[0]+"."+temp[1].substring(0,2);
                    
                }//if - .
                
		return Trust;
		
	}//getTrust()
	
	@POST
        @Path("/getEcoSP")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public static String getEcoSP(MultivaluedMap<String, String> Params)
	{
		log.info("getEcoSP TRECcommon Started");
		String serviceManifest = Params.get("serviceManifest").get(0);
		String host = Params.get("host").get(0);
		int port = Integer.parseInt(Params.get("port").get(0));
		
		TrecApiIP taip = new TrecApiIP(host, port); 
		
                String EcoSP = taip.ECO.
                        forecastServiceEcoefficiency
                        (serviceManifest,null,
                        //"ecological"
                        "energy"
                        );
                        
                log.info("EcoSP TRECcommon Value: "+EcoSP);
                
                if(EcoSP.contains("."))
                {
                    String[] temp = EcoSP.replace(".", " ").split(" ");
                    
                    if(temp[1].length()>2)
                        EcoSP = temp[0]+"."+temp[1].substring(0,2);
                    
                }//if - .
                
		return EcoSP;
	}//getEcoSP()
	
	@POST
        @Path("/getEcoHost")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public static MultivaluedMap<String, String> getEcoHost(MultivaluedMap<String, String> Params)
	{
		
		String host = Params.get("host").get(0);
		int port = Integer.parseInt(Params.get("port").get(0));
		
                log.info("getEcoHost host : "+host);
                log.info("getEcoHost port : "+port);
                
		TrecApiIP taip = new TrecApiIP(host, port); 
		return ListFunctions.Get_Eco_Host_fromList(taip.ECO.assessMultipleNodesEcoEfficiency(ListFunctions.Get_Host_Id_AsList(Params,log),
                        //"ecological"
                        "energy"
                        ));
		
	}//getEcoHost()
	
        @POST
        @Path("/getRiskHost")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public static MultivaluedMap<String, String> getRiskHost(MultivaluedMap<String, String> Params)
	{
		
		String host = Params.get("host").get(0);
		int port = Integer.parseInt(Params.get("port").get(0));
		
                log.info("getRiskHost host : "+host);
                log.info("getRiskHost port : "+port);
                
		TrecApiIP taip = new TrecApiIP(host, port); 
                
                MultivaluedMap<String, String> formParams = new MultivaluedMapImpl();
                
                for(int i=0;i<Params.get("host_id").size();i++)	
                {
			String host_id = Params.get("host_id").get(i);
                        
                        String riskHost = Double.toString(taip.RISK.calculatePhyHostPoF(host_id, "24"));
                        
                        log.info(host_id+" "+riskHost);
                        
                        if(riskHost.contains("."))
                        {
                            String[] temp = riskHost.replace(".", " ").split(" ");
                    
                            if(temp[1].length()>4)
                            riskHost = temp[0]+"."+temp[1].substring(0,4);
                    
                        }//if - .
                        
                        formParams.add("riskHost", riskHost);
                }//for-i

                return formParams;
		//return ListFunctions.Get_RiskLevel_Host_fromList(taip.RISK.calculateRiskLevelsOfPhyHostFailures(ListFunctions.Get_Host_Id_As_HashMap(Params,log)));
		
	}//getRiskHost()
        
}//class
