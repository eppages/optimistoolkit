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
import eu.optimis.ac.gateway.backupSM_FinalMessage.BackupSM_and_FinalMessage;
import eu.optimis.ac.gateway.configuration.AllocationInfoPath;
import eu.optimis.ac.gateway.configuration.BackupSMsPath;
import eu.optimis.ac.gateway.configuration.GamsPath;
import eu.optimis.ac.gateway.configuration.GetIP;
import eu.optimis.ac.gateway.configuration.GetLoggingLevel;
import eu.optimis.ac.gateway.configuration.GetPort;
import eu.optimis.ac.gateway.configuration.GetSkipInfo;
import eu.optimis.ac.gateway.configuration.Get_IP_ID;
import eu.optimis.ac.gateway.configuration.WhichSolver;
import eu.optimis.ac.gateway.utils.FileFunctions;
import eu.optimis.ac.gateway.utils.Paths;
import eu.optimis.ac.gateway.utils.PropertiesUtils;
import eu.optimis.ac.gateway.utils.ReachableHost;
import eu.optimis.ac.gateway.utils.ReachableTomcat;
import javax.ws.rs.core.MultivaluedMap;
import org.apache.log4j.Logger;

public class Initialize {

    public int loggingLevel = -1;
    
    public String gamsPath = null;
    public String allocationPath = null;
    public String startPath = null;
    public String path_BackupSMs = null;
    public String path_FinalMessage = null;
    
    public String AdmissionControllerIP = null;
    public String AdmissionControllerPort = null;
    public String RemoteAdmissionControlIP = null;
    public String RemoteAdmissionControlPort = null;
    public String physicalHostsInfo_aaS_IP = null;
    public String physicalHostsInfo_aaS_Port = null;
    public String CostIP = null;
    public String CostPort = null;
    public String RiskIP = null;
    public String RiskPort = null;
    public String TrustIP = null;
    public String TrustPort = null;
    public String EcoIP = null;
    public String EcoPort = null;
    public String getWhichSolver = null;
    public String ip_Id = null;
    
    public Boolean doNotBackupSMflag = null;
    
    public int skipTRECLevel = 0;
    public int skipTRUSTLevel = 0;
    public int skipRISKLevel = 0;
    public int skipRISKhostLevel = 0;
    public int skipECOLevel = 0;
    public int skipECOhostLevel = 0;
    public int skipCOSTLevel = 0;
    
    public int skipPhysicalHostInfoLevel = 0;
    
    public Boolean useRemoteBackup = false;
    public String RemoteBackupHost = "localhost";
    public String RemoteBackupPort = "8080";
    
    public String CloudOptimizerIP = "localhost";
    public String CloudOptimizerPort = "8080";
    
	public Initialize(MultivaluedMap<String, String> Params,Logger log)
	{
		String StartMessage = PropertiesUtils.getBoundle("StartMessage");
                log.info(StartMessage);
                
                loggingLevel = GetLoggingLevel.getLoggingLevel(log);
                
		startPath = Paths.getStartPath(log);
                gamsPath = GamsPath.getGamsPath(startPath,log);
                allocationPath = AllocationInfoPath.getAllocationPath(startPath,log);
                path_BackupSMs = BackupSMsPath.getBackupSMsPath(startPath, log);
                path_FinalMessage = BackupSMsPath.getFinalMessagePath(startPath);
                AdmissionControllerIP = GetIP.getAdmissionControllerIP(log);
                AdmissionControllerPort = GetPort.getAdmissionControllerPort(log);
                RemoteAdmissionControlIP = GetIP.getRemoteAdmissionControlIP(log);
                RemoteAdmissionControlPort = GetPort.getRemoteAdmissionControlPort(log);
                physicalHostsInfo_aaS_IP = GetIP.getPhysicalHostsInfo_aaS_IP(log);
                physicalHostsInfo_aaS_Port = GetPort.getPhysicalHostsInfo_aaS_Port(log);
                CostIP = GetIP.getCostIP(log);
                CostPort = GetPort.getCostPort(log);
                RiskIP = GetIP.getRiskIP(log);
                RiskPort = GetPort.getRiskPort(log);
                TrustIP = GetIP.getTrustIP(log);
                TrustPort = GetPort.getTrustPort(log);
                EcoIP = GetIP.getEcoIP(log);
                EcoPort = GetPort.getEcoPort(log);
                doNotBackupSMflag = DoNotBackupSMflag.getFlag(Params);
                ip_Id = Get_IP_ID.getIP_Id(physicalHostsInfo_aaS_IP, physicalHostsInfo_aaS_Port, log);
                skipPhysicalHostInfoLevel = GetSkipInfo.getSkipPhysicalHostsInfoLevel(skipPhysicalHostInfoLevel, log);
                
                skipCOSTLevel = GetSkipInfo.getSkipCOSTLevel(skipCOSTLevel, log);
                skipTRUSTLevel = GetSkipInfo.getSkipTRUSTLevel(skipTRUSTLevel, log);
                skipECOLevel = GetSkipInfo.getSkipECOLevel(skipECOLevel, log);
                skipRISKLevel = GetSkipInfo.getSkipRISKLevel(skipRISKLevel, log);
                skipECOhostLevel = GetSkipInfo.getSkipECOhostLevel(skipECOhostLevel, log);
                skipRISKhostLevel = GetSkipInfo.getSkipRISKhostLevel(skipRISKhostLevel, log);
                skipTRECLevel = GetSkipInfo.getSkipTRECLevel(skipTRECLevel, log);
                
                CloudOptimizerIP = GetIP.getCloudOptimizerIP(log);
                CloudOptimizerPort = GetPort.getCloudOptimizerPort(log);
                
                useRemoteBackup = GetLoggingLevel.getUseRemoteBackup(log);
                RemoteBackupHost = GetLoggingLevel.getRemoteBackupHost(log);
                RemoteBackupPort = GetLoggingLevel.getRemoteBackupPort(log);
                
                if(Params.containsKey("use_GAMS")) getWhichSolver = "use_GAMS";
                else if(Params.containsKey("use_Heuristic")) getWhichSolver = "use_Heuristic";
                else if(Params.containsKey("use_HeuristicSolver_Python_243")) 
                    getWhichSolver = "use_HeuristicSolver_Python_243";
                else getWhichSolver = WhichSolver.getWhichSolver(log);
                
                if (((getWhichSolver.contains("GAMS"))==false)&&((getWhichSolver.contains("Heuristic"))==false))
                {
                    log.error("No Solver Selected : "+getWhichSolver);
                    throw new RuntimeException("No Solver Selected : "+getWhichSolver);
                }//else
                
                log.info("WhichSolver : "+getWhichSolver);
                
                log.info("LoggingLevel : "+loggingLevel);
                log.info("startPath : "+startPath);
                log.info("gamsPath : "+gamsPath);
                log.info("allocationPath : "+allocationPath);
                log.info("path_BackupSMs : "+path_BackupSMs);
                log.info("path_FinalMessage : "+path_FinalMessage);
                log.info("AdmissionControllerIP : "+AdmissionControllerIP);
                log.info("AdmissionControllerPort : "+AdmissionControllerPort);
                log.info("RemoteAdmissionControlIP : "+RemoteAdmissionControlIP);
                log.info("RemoteAdmissionControlPort : "+RemoteAdmissionControlPort);
                log.info("physicalHostsInfo_aaS_IP : "+physicalHostsInfo_aaS_IP);
                log.info("physicalHostsInfo_aaS_Port : "+physicalHostsInfo_aaS_Port);
                log.info("CostIP : "+CostIP);
                log.info("CostPort : "+CostPort);
                log.info("RiskIP : "+RiskIP);
                log.info("RiskPort : "+RiskPort);
                log.info("TrustIP : "+TrustIP);
                log.info("TrustPort : "+TrustPort);
                log.info("EcoIP : "+EcoIP);
                log.info("EcoPort : "+EcoPort);
                log.info("doNotBackupSMflag : "+doNotBackupSMflag);
                log.info("IP_Id : "+ip_Id);
                
                log.info("skipPhysicalHostInfoLevel : "+skipPhysicalHostInfoLevel);
		log.info("skipCOSTLevel : "+skipCOSTLevel);
                log.info("skipTRUSTLevel : "+skipTRUSTLevel);
                log.info("skipRISKLevel : "+skipRISKLevel);
                log.info("skipECOLevel : "+skipECOLevel);
                log.info("skipECOhostLevel : "+skipECOhostLevel);
                log.info("skipRISKhostLevel : "+skipRISKhostLevel);
                log.info("skipTRECLevel : "+skipTRECLevel);
                
                log.info("RemoteBackupPort : "+RemoteBackupPort);
                log.info("RemoteBackupHost : "+RemoteBackupHost);
                log.info("useRemoteBackup : "+useRemoteBackup);
                
                log.info("CloudOptimizerIP : "+CloudOptimizerIP);
                log.info("CloudOptimizerPort : "+CloudOptimizerPort);
                
                if((useRemoteBackup)&&(ReachableHost.isReachable(RemoteBackupHost, log))&&(ReachableTomcat.isReachable(RemoteBackupHost, RemoteBackupPort, log)))
                {
                    MultivaluedMap<String, String> params = Params;
                    
                    params.add("Message", ip_Id+" : "+StartMessage);
                    params.add("Input", "Input");
                    params.add("ip_Id", ip_Id);
                    params.add("doNotBackupSMflag", Boolean.toString(doNotBackupSMflag));
                    
                    RestClient_MultivaluedMap_String client = new RestClient_MultivaluedMap_String
                            (RemoteBackupHost,RemoteBackupPort,"/ACGateway/clear/remoteBackup",params,false,log);
                    
                    log.info("Remote Backup Status : "+client.status);
                }//if-RemoteBackup
                else
                {
                
                delete_Previous_last(path_BackupSMs,log);
		BackupSM_and_FinalMessage.doBackup(Params, log, StartMessage,"Input",
                        path_BackupSMs, path_FinalMessage,doNotBackupSMflag);
                }//else
	}//constructor
	
	private void delete_Previous_last(String filePath,Logger log)
	{
		int DeletedFileCounter = FileFunctions.deleteFiles_containingString(filePath, "last", log);
		
                if(loggingLevel<2)
		log.info(DeletedFileCounter +" Files Deleted");
		
	}//delete_Previous_last()
        
        public static void delete_Previous_last2(String filePath,Logger log)
	{
		int DeletedFileCounter = FileFunctions.deleteFiles_containingString(filePath, "last", log);
		
		log.info(DeletedFileCounter +" Files Deleted");
		
	}//delete_Previous_last()
	
}//class
