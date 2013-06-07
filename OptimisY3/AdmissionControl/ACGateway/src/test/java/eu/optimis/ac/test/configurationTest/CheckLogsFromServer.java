/*
 * Copyright (c) 2013 National Technical University of Athens (NTUA)
 *	
 *   							MIT License
 *   
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */


package eu.optimis.ac.test.configurationTest;

import eu.optimis.ac.ACRestClients.MyRestClients.RestClient_noInput_String;
import eu.optimis.ac.gateway.utils.Strings;
import eu.optimis.ac.test.remoteTest.GetServerDetails;
import javax.ws.rs.core.MultivaluedMap;
import junit.framework.TestCase;

public class CheckLogsFromServer extends TestCase {
    
    private String AClastServerLogs = null;
    
    private String AdmissionControllerlastServerLogs = null;
    
    public CheckLogsFromServer(MultivaluedMap<String, String> formParams)
    {
        setAClastServerLogs();
        setAdmissionControllerLastServerLogs();
        
        checkWhichSolver(formParams);
        checkIP_Id(formParams);
        
        checkGamsPath(formParams);
        checkAllocationInfoPath(formParams);
        checkBackupSMsPath(formParams);
        
        checkGamsExecutable(formParams);
        
        checkAdmissionControllerIP(formParams);
        checkAdmissionControllerPort(formParams);
        checkRemoteAdmissionControlIP(formParams);
        checkRemoteAdmissionControlPort(formParams);
        checkPhysicalHostsInfoIP(formParams);
        checkPhysicalHostsInfoPort(formParams);        
        
        checkCostIP(formParams);
        checkCostPort(formParams);
        checkEcoIP(formParams);
        checkEcoPort(formParams);
        checkTrustIP(formParams);
        checkTrustPort(formParams);
        checkRiskIP(formParams);
        checkRiskPort(formParams);
        
        checkLoggingLevel(formParams);
        
        checkSkipTRECLevel (formParams);
        checkSkipTRUSTLevel (formParams);
        checkSkipRISKLevel (formParams);
        checkSkipECOLevel (formParams);
        checkSkipECOhostLevel (formParams);
        checkSkipRISKhostLevel (formParams);
        checkSkipCOSTLevel (formParams);
        
        checkSkipPhysicalHostInfoLevel (formParams);
        
        checkUseRemoteBackup (formParams);
        checkRemoteBackupHost (formParams);
        checkRemoteBackupPort (formParams);
        
        checkCloudOptimizerIP (formParams);
        checkCloudOptimizerPort (formParams);
    }//constructor()
    
    private void checkIt(MultivaluedMap<String, String> Params, String key,
            String Logs, String message, String selectString, String splitString)
    {
        if(!Params.containsKey(key))
            return;
        
	System.out.println(message);

        assertTrue(Logs.contains(message));
                
        String temp[] = Strings.selectLineInManyLinesString(Logs, selectString).split(splitString);
        
        String value = temp[1];
        
        assertEquals(Params.get(key).get(0), value);
        
    }//checkIt()
    
    private void setAClastServerLogs()
    {
        RestClient_noInput_String lastLogsClient = 
                new RestClient_noInput_String(
                		GetServerDetails.Host,GetServerDetails.Port,
                "/ACGateway/info/getACLogs");
        
        AClastServerLogs = lastLogsClient.returnedString;
        
    }//setAClastServerLogs()
    
    private void setAdmissionControllerLastServerLogs()
    {
        RestClient_noInput_String lastLogsClient = 
                new RestClient_noInput_String(
                		GetServerDetails.Host,GetServerDetails.Port,
                		"/AdmissionController/info/getLogs");
        
        AdmissionControllerlastServerLogs = lastLogsClient.returnedString;
        
    }//setAdmissionControllerLastServerLogs()
    
    private void checkGamsPath(MultivaluedMap<String, String> Params)
    {
        
        checkIt(Params, "gamsPath",
            AClastServerLogs, "Got Gams Path Outside War Configuration", "gamsPath : ", " : ");
        
    }//checkGamsPath()
    
    private void checkCloudOptimizerIP(MultivaluedMap<String, String> Params)
    {
        
        checkIt(Params, "CloudOptimizerIP",
            AClastServerLogs, "Got CloudOptimizerIP Outside War Configuration", "CloudOptimizerIP : ", " : ");
        
    }//checkCloudOptimizerIP()
    
    private void checkCloudOptimizerPort(MultivaluedMap<String, String> Params)
    {
        
        checkIt(Params, "CloudOptimizerPort",
            AClastServerLogs, "Got CloudOptimizerPort Outside War Configuration", "CloudOptimizerPort : ", " : ");
        
    }//checkCloudOptimizerPort()
    
    private void checkAllocationInfoPath(MultivaluedMap<String, String> Params)
    {
        
        checkIt(Params, "allocationPath",
            AClastServerLogs, "Got AllocationInfo Path Outside War Configuration", "allocationPath : ", " : ");
        
    }//checkAllocationInfoPath()
    
    private void checkBackupSMsPath(MultivaluedMap<String, String> Params)
    {
        
        checkIt(Params, "backupSMsPath",
            AClastServerLogs, "Got BackupSMs Path Outside War Configuration", "path_BackupSMs : ", " : ");
        
    }//checkBackupSMsPath()
    
    private void checkGamsExecutable(MultivaluedMap<String, String> Params)
    {
        checkIt(Params, "gamsExecutable",
            AdmissionControllerlastServerLogs, "Gams Properties Outside War Configuration", "cmd[0] = ", " = ");
            
    }//checkGamsExecutable()
    
    private void checkPhysicalHostsInfoIP(MultivaluedMap<String, String> Params)
    {
        
        checkIt(Params, "physicalHostsInfo_aaS_IP",
            AClastServerLogs, "Got PhysicalHostsInfo_aaS IP Outside War Configuration", "physicalHostsInfo_aaS_IP : ", " : ");
        
    }//checkPhysicalHostsInfoIP()
    
    private void checkPhysicalHostsInfoPort(MultivaluedMap<String, String> Params)
    {
        
        checkIt(Params, "physicalHostsInfo_aaS_Port",
            AClastServerLogs, "Got PhysicalHostsInfo_aaS Port Outside War Configuration", "physicalHostsInfo_aaS_Port : ", " : ");
        
    }//checkPhysicalHostsInfoPort()
    
    private void checkAdmissionControllerIP(MultivaluedMap<String, String> Params)
    {   
        checkIt(Params, "admissionControllerIP",
            AClastServerLogs, "Got Admission Controller IP Outside War Configuration", "AdmissionControllerIP : ", " : ");
        
    }//checkAdmissionControllerIP()
    
    private void checkAdmissionControllerPort(MultivaluedMap<String, String> Params)
    {
        checkIt(Params, "admissionControllerPort",
            AClastServerLogs, "Got Admission Controller Port Outside War Configuration", "AdmissionControllerPort : ", " : ");
        
    }//checkAdmissionControllerPort()
    
    private void checkRemoteAdmissionControlIP(MultivaluedMap<String, String> Params)
    {   
        checkIt(Params, "remoteAdmissionControlIP",
            AClastServerLogs, "Got Remote Admission Control IP Outside War Configuration", "RemoteAdmissionControlIP : ", " : ");
        
    }//checkRemoteAdmissionControlIP()
    
    private void checkRemoteAdmissionControlPort(MultivaluedMap<String, String> Params)
    {
        checkIt(Params, "remoteAdmissionControlPort",
            AClastServerLogs, "Got Remote Admission Control Port Outside War Configuration", "RemoteAdmissionControlPort : ", " : ");
        
    }//checkRemoteAdmissionControlPort()
    
    private void checkCostIP(MultivaluedMap<String, String> Params)
    {
        
        checkIt(Params, "costIP",
            AClastServerLogs, "Got Cost IP Outside War Configuration", "CostIP : ", " : ");
        
    }//checkCostIP()
    
    private void checkCostPort(MultivaluedMap<String, String> Params)
    {
        
        checkIt(Params, "costPort",
            AClastServerLogs, "Got Cost Port Outside War Configuration", "CostPort : ", " : ");
        
    }//checkCostPort()
    
    private void checkTrustPort(MultivaluedMap<String, String> Params)
    {
        
        checkIt(Params, "trustPort",
            AClastServerLogs, "Got Trust Port Outside War Configuration", "TrustPort : ", " : ");
        
    }//checkTrustPort()
    
    private void checkTrustIP(MultivaluedMap<String, String> Params)
    {
        
        checkIt(Params, "trustIP",
            AClastServerLogs, "Got Trust IP Outside War Configuration", "TrustIP : ", " : ");
        
    }//checkTrustIP()
    
    private void checkRiskPort(MultivaluedMap<String, String> Params)
    {
        
        checkIt(Params, "riskPort",
            AClastServerLogs, "Got Risk Port Outside War Configuration", "RiskPort : ", " : ");
        
    }//checkRiskPort()
    
    private void checkRiskIP(MultivaluedMap<String, String> Params)
    {
        
        checkIt(Params, "riskIP",
            AClastServerLogs, "Got Risk IP Outside War Configuration", "RiskIP : ", " : ");
        
    }//checkRiskIP()
    
    private void checkEcoPort(MultivaluedMap<String, String> Params)
    {
        
        checkIt(Params, "ecoPort",
            AClastServerLogs, "Got Eco Port Outside War Configuration", "EcoPort : ", " : ");
        
    }//checkEcoPort()
    
    private void checkEcoIP(MultivaluedMap<String, String> Params)
    {
        
        checkIt(Params, "ecoIP",
            AClastServerLogs, "Got Eco IP Outside War Configuration", "EcoIP : ", " : ");
        
    }//checkEcoIP()
    
    private void checkLoggingLevel(MultivaluedMap<String, String> Params)
    {
        
        checkIt(Params, "LoggingLevel",
            AClastServerLogs, "Got Logging Level Outside War Configuration", "LoggingLevel : ", " : ");
        
    }//checkLoggingLevel()
    
    private void checkWhichSolver(MultivaluedMap<String, String> Params)
    {
        
        checkIt(Params, "WhichSolver",
            AClastServerLogs, "Got Which Solver Outside War Configuration", "WhichSolver : ", " : ");
        
    }//checkWhichSolver()
    
    private void checkIP_Id(MultivaluedMap<String, String> Params)
    {
        
        checkIt(Params, "IP_Id",
            AClastServerLogs, "Got IP_Id Outside War Configuration", "IP_Id : ", " : ");
        
    }//checkWhichSolver()
    
    private void checkSkipPhysicalHostInfoLevel(MultivaluedMap<String, String> Params)
    {
        
        checkIt(Params, "skipPhysicalHostInfoLevel",
            AClastServerLogs, "Got skipPhysicalHostInfoLevel Outside War Configuration", "skipPhysicalHostInfoLevel : ", " : ");
        
    }//checkSkipPhysicalHostInfoLevel()
    
    private void checkSkipTRECLevel(MultivaluedMap<String, String> Params)
    {
        
        checkIt(Params, "skipTRECLevel",
            AClastServerLogs, "Got skipTRECLevel Outside War Configuration", "skipTRECLevel : ", " : ");
        
    }//checkSkipTRECLevel()
    
    private void checkSkipTRUSTLevel(MultivaluedMap<String, String> Params)
    {
        
        checkIt(Params, "skipTRUSTLevel",
            AClastServerLogs, "Got skipTRUSTLevel Outside War Configuration", "skipTRUSTLevel : ", " : ");
        
    }//checkSkipTRUSTLevel()
    
    private void checkSkipRISKLevel(MultivaluedMap<String, String> Params)
    {
        
        checkIt(Params, "skipRISKLevel",
            AClastServerLogs, "Got skipRISKLevel Outside War Configuration", "skipRISKLevel : ", " : ");
        
    }//checkSkipRISKLevel()
    
    private void checkSkipECOLevel(MultivaluedMap<String, String> Params)
    {
        
        checkIt(Params, "skipECOLevel",
            AClastServerLogs, "Got skipECOLevel Outside War Configuration", "skipECOLevel : ", " : ");
        
    }//checkSkipECOLevel()
    
    private void checkSkipCOSTLevel(MultivaluedMap<String, String> Params)
    {
        
        checkIt(Params, "skipCOSTLevel",
            AClastServerLogs, "Got skipCOSTLevel Outside War Configuration", "skipCOSTLevel : ", " : ");
        
    }//checkSkipCOSTLevel()
    
    private void checkSkipRISKhostLevel(MultivaluedMap<String, String> Params)
    {
        
        checkIt(Params, "skipRISKhostLevel",
            AClastServerLogs, "Got skipRISKhostLevel Outside War Configuration", "skipRISKhostLevel : ", " : ");
        
    }//checkSkipRISKhostLevel()
    
    private void checkSkipECOhostLevel(MultivaluedMap<String, String> Params)
    {
        
        checkIt(Params, "skipECOhostLevel",
            AClastServerLogs, "Got skipECOhostLevel Outside War Configuration", "skipECOhostLevel : ", " : ");
        
    }//checkSkipECOhostLevel()
    
    private void checkUseRemoteBackup(MultivaluedMap<String, String> Params)
    {
        
        checkIt(Params, "useRemoteBackup",
            AClastServerLogs, "Got useRemoteBackup Outside War Configuration", "useRemoteBackup : ", " : ");
        
    }//checkUseRemoteBackup()
    
    private void checkRemoteBackupHost(MultivaluedMap<String, String> Params)
    {
        
        checkIt(Params, "RemoteBackupHost",
            AClastServerLogs, "Got RemoteBackupHost Outside War Configuration", "RemoteBackupHost : ", " : ");
        
    }//checkRemoteBackupHost()
    
    private void checkRemoteBackupPort(MultivaluedMap<String, String> Params)
    {
        
        checkIt(Params, "RemoteBackupPort",
            AClastServerLogs, "Got RemoteBackupPort Outside War Configuration", "RemoteBackupPort : ", " : ");
        
    }//checkRemoteBackupPort()
    
}//class
