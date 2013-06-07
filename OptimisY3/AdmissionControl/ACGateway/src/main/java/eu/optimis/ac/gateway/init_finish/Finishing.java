/*
 * Copyright (c) 2013 National Technical University of Athens (NTUA)
 *	
 *   							MIT License
 *   
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package eu.optimis.ac.gateway.init_finish;

import eu.optimis.ac.ACRestClients.MyRestClients.RestClient_MultivaluedMap_String;
import eu.optimis.ac.gateway.acGatewayGothersExternalData.ACG_GothersExternalData;
import eu.optimis.ac.gateway.allocationOffer.CompineServiceManifestAllocationOfferInfo;
import eu.optimis.ac.gateway.analyzeAllocationOffer.printAllocationOfferInfo;
import eu.optimis.ac.gateway.backupSM_FinalMessage.BackupSM_and_FinalMessage;
import eu.optimis.ac.gateway.backupSM_FinalMessage.BackupSMs;
import eu.optimis.ac.gateway.utils.ReachableHost;
import eu.optimis.ac.gateway.utils.ReachableTomcat;
import javax.ws.rs.core.MultivaluedMap;
import org.apache.log4j.Logger;

public class Finishing {

	public Finishing(ACG_GothersExternalData gotherExternalData,
			printAllocationOfferInfo pInfo,
			CompineServiceManifestAllocationOfferInfo acResult,
			Logger log, Initialize initialize,String ListOfServicesIDs)
	{
                Boolean doNotBackupSMflag = initialize.doNotBackupSMflag;
                
		String path_BackupSMs = initialize.path_BackupSMs;
		String path_FinalMessage = initialize.path_FinalMessage;
                
                if(acResult.finalMessage.contains("rp"))
			new BackupSMs(acResult.ReturnedServiceManifests,"rp",path_BackupSMs,log, doNotBackupSMflag);
                else if(acResult.finalMessage.contains("r"))
			new BackupSMs(acResult.ReturnedServiceManifests,"r",path_BackupSMs,log, doNotBackupSMflag);
                else if(acResult.finalMessage.contains("p"))
			new BackupSMs(acResult.ReturnedServiceManifests,"p",path_BackupSMs,log, doNotBackupSMflag);
		
                String which_solver = null;
                
                if(initialize.getWhichSolver.contains("use_HeuristicSolver_Python_243"))
                    which_solver = "HS_P_243";
                else if(initialize.getWhichSolver.contains("use_Heuristic"))
                    which_solver = "HS";
                else if(initialize.getWhichSolver.contains("use_GAMS"))
                    which_solver = "GAMS";
                
		String FinalMessage=pInfo.FinalMessage+": "+which_solver+" "+
                        gotherExternalData.finalMessage+
                        acResult.finalMessage
                        + "   "+ListOfServicesIDs;
		
                if((initialize.useRemoteBackup)&&(ReachableHost.isReachable(initialize.RemoteBackupHost, log))&&(ReachableTomcat.isReachable(initialize.RemoteBackupHost, initialize.RemoteBackupPort, log)))
                {
                    MultivaluedMap<String, String> params = acResult.ReturnedServiceManifests;
                    
                    params.add("Message", FinalMessage);
                    params.add("Input", "");
                    params.add("ip_Id", initialize.ip_Id);
                    params.add("doNotBackupSMflag", Boolean.toString(doNotBackupSMflag));
                    
                    RestClient_MultivaluedMap_String client = new RestClient_MultivaluedMap_String
                            (initialize.RemoteBackupHost,initialize.RemoteBackupPort,"/ACGateway/clear/remoteBackup",params,false,log);
                    
                    log.info("Remote Backup Status : "+client.status);
                }//if-RemoteBackup
                else
                {
                    new BackupSMs(acResult.ReturnedServiceManifests,"",path_BackupSMs,log, doNotBackupSMflag);
		
                    BackupSM_and_FinalMessage.doBackup(acResult.ReturnedServiceManifests, log, FinalMessage, "", path_BackupSMs, path_FinalMessage, doNotBackupSMflag);
                }//else
                
		log.info(FinalMessage);
	}//constructor
	
}//class
