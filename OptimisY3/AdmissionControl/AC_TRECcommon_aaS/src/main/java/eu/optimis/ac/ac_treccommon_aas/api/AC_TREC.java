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
import eu.optimis.ac.ac_treccommon_aas.trec.RiskAsString;
import eu.optimis.treccommon.TrecApiIP;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import org.apache.log4j.Logger;

@Path("/TREC")
public class AC_TREC {
	
        private static Logger log = AC_TRECcommon.log;
    
	@POST
        @Path("/getTRECcommon")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public static MultivaluedMap<String, String> getTRECcommon(MultivaluedMap<String, String> Params)
	{
		String serviceManifest = Params.get("serviceManifest").get(0);
		String host = Params.get("host").get(0);
		int port = Integer.parseInt(Params.get("port").get(0));
		String spId = Params.get("spId").get(0);
		
		MultivaluedMap<String, String> formParams = new MultivaluedMapImpl();
		
		TrecApiIP taip = new TrecApiIP(host, port);
		
                String Cost = null;
                
                try {
                
                        Cost = taip.COST.forecastIPCost(host, port, serviceManifest);
                
                } catch (NumberFormatException ex) {
                    log.error(ex.getMessage());
                } catch (Exception ex) {
                    log.error(ex.getMessage());
                }
                
                log.info("Cost Common Value : "+Cost);
                
                String Trust = taip.TRUST.getSPDeploymentTrust(spId);
                
                if(Trust.contains("."))
                {
                    String[] temp = Trust.replace(".", " ").split(" ");
                    
                    if(temp[1].length()>2)
                        Trust = temp[0]+"."+temp[1].substring(0,2);
                    
                }//if - .
                
                log.info("Trust Common Value : "+Trust);
                
                String EcoSP = taip.ECO.
                        forecastServiceEcoefficiency
                        (serviceManifest,null,
                        //"ecological"
                        "energy"
                        );
                        
                log.info("EcoSP TRECcommon Value : "+EcoSP);
                
                /*
                if(EcoSP.contains("."))
                {
                    String[] temp = EcoSP.replace(".", " ").split(" ");
                    
                    if(temp[1].length()>2)
                        EcoSP = temp[0]+"."+temp[1].substring(0,2);
                    
                }//if - .
                */
                
                String Risk = 
                        RiskAsString.getXml
                        (taip.RISK.preNegotiateSPDeploymentPhase(spId, serviceManifest));
                
                if(Trust.contains("Error"))
                    throw new RuntimeException("Trust Error");
                
		formParams.add("ecoSP", EcoSP);
		formParams.add("trust", Trust);
                formParams.add("cost", Cost);
		formParams.add("risk", Risk);
                
		return formParams;
	}//getTRECcommon()
	
}//class