/*
 * Copyright (c) 2013 National Technical University of Athens (NTUA)
 *	
 *   							MIT License
 *   
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */


package eu.optimis.ac.gateway.configuration.outsideWarConfiguration;

import org.apache.log4j.Logger;

public class PrintOutsideWarConfiguration {
    
    public static String AsString(Logger log)
    {
        String result = "";
        
        String Gams_Path = OutsideWarConfiguration.getGamsPath(log);
        String AllocationInfo_Path = OutsideWarConfiguration.getAllocationInfoPath(log);
        
        String BackupSMs_Path = OutsideWarConfiguration.getBackupSMsPath(log);
        String Remote_Admission_Control_IP = OutsideWarConfiguration.getRemoteAdmissionControlIP(log);
        String Remote_Admission_Control_Port = OutsideWarConfiguration.getRemoteAdmissionControlPort(log);
        String Admission_Controller_IP = OutsideWarConfiguration.getAdmissionControllerIP(log);
        String Admission_Controller_Port = OutsideWarConfiguration.getAdmissionControllerPort(log);
        String PhysicalHostsInfo_aaS_IP = OutsideWarConfiguration.getphysicalHostsInfoIP(log);
        String PhysicalHostsInfo_aaS_Port = OutsideWarConfiguration.getphysicalHostsInfoPort(log);                                                
        String Risk_IP = OutsideWarConfiguration.getRiskIP(log);
        String Risk_Port = OutsideWarConfiguration.getRiskPort(log);
        String Cost_IP = OutsideWarConfiguration.getCostIP(log);
        String Cost_Port = OutsideWarConfiguration.getCostPort(log);
        String Eco_IP = OutsideWarConfiguration.getEcoIP(log);
        String Eco_Port = OutsideWarConfiguration.getEcoPort(log);
        String Trust_IP = OutsideWarConfiguration.getTrustIP(log);
        String Trust_Port = OutsideWarConfiguration.getTrustPort(log);
        String Logging_Level = OutsideWarConfiguration.getLoggingLevel(log);
        String Which_Solver = OutsideWarConfiguration.getWhichSolver(log);
        String IP_Id = OutsideWarConfiguration.getIP_Id(log);
        String skipTRECLevel = OutsideWarConfiguration.getSkipTRECLevel(log);
        String skipTRUSTLevel = OutsideWarConfiguration.getSkipTRUSTLevel(log);
        String skipRISKLevel = OutsideWarConfiguration.getSkipRISKLevel(log);
        String skipRISKhostLevel = OutsideWarConfiguration.getSkipRISKhostLevel(log);
        String skipECOLevel = OutsideWarConfiguration.getSkipECOLevel(log);
        String skipECOhostLevel = OutsideWarConfiguration.getSkipECOhostLevel(log);
        String skipCOSTLevel = OutsideWarConfiguration.getSkipCOSTLevel(log);
        String skipPhysicalHostInfoLevel = OutsideWarConfiguration.getSkipPhysicalHostInfoLevel(log);
        
        String useRemoteBackup = OutsideWarConfiguration.getUseRemoteBackup(log);
        String RemoteBackupHost = OutsideWarConfiguration.getRemoteBackupHost(log);
        String RemoteBackupPort = OutsideWarConfiguration.getRemoteBackupPort(log);
        
        String CloudOptimizerIP = OutsideWarConfiguration.getCloudOptimizerIP(log);
        String CloudOptimizerPort = OutsideWarConfiguration.getCloudOptimizerPort(log);
        
        String GAMSexecutable = OutsideWarConfiguration.getGAMSexecutable(log);
        
        if(GAMSexecutable!=null)result+="GAMSexecutable = "+GAMSexecutable+"\n";
        
        if(skipTRECLevel!=null)result+="skipTRECLevel = "+skipTRECLevel+"\n";
        if(skipTRUSTLevel!=null)result+="skipTRUSTLevel = "+skipTRUSTLevel+"\n";
        if(skipRISKLevel!=null)result+="skipRISKLevel = "+skipRISKLevel+"\n";
        if(skipRISKhostLevel!=null)result+="skipRISKhostLevel = "+skipRISKhostLevel+"\n";
        if(skipECOLevel!=null)result+="skipECOLevel = "+skipECOLevel+"\n";
        if(skipECOhostLevel!=null)result+="skipECOhostLevel = "+skipECOhostLevel+"\n";
        if(skipCOSTLevel!=null)result+="skipCOSTLevel = "+skipCOSTLevel+"\n";
        if(skipPhysicalHostInfoLevel!=null)result+="skipPhysicalHostInfoLevel = "+skipPhysicalHostInfoLevel+"\n";
        
        if(IP_Id!=null)result+="IP_Id = "+IP_Id+"\n";
        if(Which_Solver!=null)result+="Which_Solver = "+Which_Solver+"\n";
        if(Logging_Level!=null)result+="Logging_Level = "+Logging_Level+"\n";
        if(Admission_Controller_IP!=null)result+="Admission_Controller_IP = "+Admission_Controller_IP+"\n";
        if(Admission_Controller_Port!=null)result+="Admission_Controller_Port = "+Admission_Controller_Port+"\n";
        if(BackupSMs_Path!=null)result+="BackupSMs_Path = "+BackupSMs_Path+"\n";
        if(Gams_Path!=null)result+="Gams_Path = "+Gams_Path+"\n";
        if(AllocationInfo_Path!=null)result+="AllocationInfo_Path = "+AllocationInfo_Path+"\n";
        if(Remote_Admission_Control_IP!=null)result+="Remote_Admission_Control_IP = "+Remote_Admission_Control_IP+"\n";
        if(Remote_Admission_Control_Port!=null)result+="Remote_Admission_Control_Port = "+Remote_Admission_Control_Port+"\n";
        if(PhysicalHostsInfo_aaS_IP!=null)result+="PhysicalHostsInfo_aaS_IP = "+PhysicalHostsInfo_aaS_IP+"\n";
        if(PhysicalHostsInfo_aaS_Port!=null)result+="PhysicalHostsInfo_aaS_Port = "+PhysicalHostsInfo_aaS_Port+"\n";
        if(Risk_IP!=null)result+="Risk_IP = "+Risk_IP+"\n";
        if(Risk_Port!=null)result+="Risk_Port = "+Risk_Port+"\n";
        if(Cost_IP!=null)result+="Cost_IP = "+Cost_IP+"\n";
        if(Cost_Port!=null)result+="Cost_Port = "+Cost_Port+"\n";
        if(Eco_IP!=null)result+="Eco_IP = "+Eco_IP+"\n";
        if(Eco_Port!=null)result+="Eco_Port = "+Eco_Port+"\n";
        if(Trust_IP!=null)result+="Trust_IP = "+Trust_IP+"\n";
        if(Trust_Port!=null)result+="Trust_Port = "+Trust_Port+"\n";
        
        if(useRemoteBackup!=null)result+="useRemoteBackup = "+useRemoteBackup+"\n";
        if(RemoteBackupHost!=null)result+="RemoteBackupHost = "+RemoteBackupHost+"\n";
        if(RemoteBackupPort!=null)result+="RemoteBackupPort = "+RemoteBackupPort+"\n";
        
        if(CloudOptimizerIP!=null)result+="CloudOptimizerIP = "+CloudOptimizerIP+"\n";
        if(CloudOptimizerPort!=null)result+="CloudOptimizerPort = "+CloudOptimizerPort+"\n";
        
        return result;
    }//AsString()
    
}//class
