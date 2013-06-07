/*
 * Copyright (c) 2013 National Technical University of Athens (NTUA)
 *	
 *   							MIT License
 *   
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package eu.optimis.ac.api;

import com.sun.jersey.core.util.MultivaluedMapImpl;
import eu.optimis.ac.ACRestClients.MyRestClients.RestClient_MultivaluedMap_String;
import eu.optimis.ac.gateway.acGateway.PerServiceConstraints;
import eu.optimis.ac.gateway.acGateway.SetPolicy;
import eu.optimis.ac.gateway.backupSM_FinalMessage.BackupFinalMessage;
import eu.optimis.ac.gateway.configuration.BackupSMsPath;
import eu.optimis.ac.gateway.configuration.GetLoggingLevel;
import eu.optimis.ac.gateway.configuration.Get_IP_ID;
import eu.optimis.ac.gateway.utils.Paths;
import eu.optimis.ac.gateway.utils.PropertiesUtils;
import eu.optimis.ac.gateway.utils.ReachableHost;
import eu.optimis.ac.gateway.utils.ReachableTomcat;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MultivaluedMap;
import org.apache.log4j.Logger;

@Path("/data")
public class ACDataApi {
	
	private static Logger log = ACModelApi.log;
	
	public ACDataApi()
	{
		
	}//constructor
	
	@GET
	@Path("/setPolicy/{trust_weight}/{eco_weight}/{risk_weight}/{cost_weight}")
	@Produces("text/plain")
	public String setPolicy(	
			@PathParam("trust_weight") String trust_weight,
			@PathParam("eco_weight") String eco_weight,
			@PathParam("risk_weight") String risk_weight,
			@PathParam("cost_weight") String cost_weight)
	{
		
		SetPolicy setpolicy = new SetPolicy(trust_weight,eco_weight,risk_weight,cost_weight,log);
                
                
                String Message = "Weights Set To:("+trust_weight+","+risk_weight+","+eco_weight+","+cost_weight+")";
                
                String IP_Id = Get_IP_ID.getIP_Id("localhost", "8080", log);
                
                Message = IP_Id+" : "+Message;  
                
                if((GetLoggingLevel.getUseRemoteBackup(log))&&(ReachableHost.isReachable(GetLoggingLevel.getRemoteBackupHost(log), log))&&(ReachableTomcat.isReachable(GetLoggingLevel.getRemoteBackupHost(log), GetLoggingLevel.getRemoteBackupPort(log), log)))
                {
                    MultivaluedMap<String, String> params = new MultivaluedMapImpl(); 
                    
                    params.add("Message", Message);
                    params.add("TRECfilename", "");
                    params.add("ip_Id", IP_Id);
                    
                    RestClient_MultivaluedMap_String client = new RestClient_MultivaluedMap_String
                            (GetLoggingLevel.getRemoteBackupHost(log),GetLoggingLevel.getRemoteBackupPort(log),"/ACGateway/clear/remoteBackupTREC",params,false,log);
                    
                    log.info("Remote Backup Status : "+client.status);
                }//if-RemoteBackup
                else
                {
                    BackupFinalMessage.WriteFinalMessage(Message, log, PropertiesUtils.getBoundle("filename.BackupFinalMessage"), BackupSMsPath.getFinalMessagePath(Paths.getStartPath(log)));
                }//else
                
		return setpolicy.ReturnedMessage;
		
	}//setPolicy
	
        @GET
        @Path("/perServiceConstraints/{trustLevel_constraint}/{riskLevel_constraint}/{ecoValue_constraint}/{costInEuros_constraint}")
	@Produces("text/plain")
        public String perServiceConstraints(
                        @PathParam("trustLevel_constraint") String trustLevel_constraint,
			@PathParam("riskLevel_constraint") String riskLevel_constraint,
			@PathParam("ecoValue_constraint") String ecoValue_constraint,
			@PathParam("costInEuros_constraint") String costInEuros_constraint)
        {
                PerServiceConstraints perServiceConstraints = new  PerServiceConstraints(
                    trustLevel_constraint, riskLevel_constraint, ecoValue_constraint, costInEuros_constraint, log);
                
                String Message = "Constraints Set To:("+trustLevel_constraint+","+riskLevel_constraint+","+ecoValue_constraint+","+costInEuros_constraint+")";
                
                String IP_Id = Get_IP_ID.getIP_Id("localhost", "8080", log);
                
                Message = IP_Id+" : "+Message;  
                
                if((GetLoggingLevel.getUseRemoteBackup(log))&&(ReachableHost.isReachable(GetLoggingLevel.getRemoteBackupHost(log), log))&&(ReachableTomcat.isReachable(GetLoggingLevel.getRemoteBackupHost(log), GetLoggingLevel.getRemoteBackupPort(log), log)))
                {
                    MultivaluedMap<String, String> params = new MultivaluedMapImpl(); 
                    
                    params.add("Message", Message);
                    params.add("TRECfilename", "");
                    params.add("ip_Id", IP_Id);
                    
                    
                    RestClient_MultivaluedMap_String client = new RestClient_MultivaluedMap_String
                            (GetLoggingLevel.getRemoteBackupHost(log),GetLoggingLevel.getRemoteBackupPort(log),"/ACGateway/clear/remoteBackupTREC",params,false,log);
                    
                    log.info("Remote Backup Status : "+client.status);
                }//if-RemoteBackup
                else
                {
                    BackupFinalMessage.WriteFinalMessage(Message, log, PropertiesUtils.getBoundle("filename.BackupFinalMessage"), BackupSMsPath.getFinalMessagePath(Paths.getStartPath(log)));
                }//else
                
                return perServiceConstraints.ReturnedMessage;
                
        }//perServiceConstraints
        
}//class
