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

import eu.optimis.ac.gateway.utils.FileFunctions;
import eu.optimis.ac.gateway.utils.PropertiesUtils;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import org.apache.log4j.Logger;

public class OutsideWarConfiguration {
    
    public static String path = PropertiesUtils.getBoundle("OutsideWarConfiguration.path");
    public static String filename = PropertiesUtils.getBoundle("OutsideWarConfiguration.filename");
    public static String filePath = path+filename;
    public static String fileTREC = PropertiesUtils.getBoundle("OutsideWarConfiguration.fileTREC");
    public static String fileTRECPath = path+fileTREC;
    public static String fileGAMS = PropertiesUtils.getBoundle("OutsideWarConfiguration.fileGAMS");
    public static String fileGAMSPath = path+fileGAMS;
    
    
    public static Boolean isOutsideWarConfigurationFileExists()
    {
        return FileFunctions.FileExists(path, filename);
        
    }//isOutsideWarConfigurationFileExists()
    
    public static Boolean isOutsideWarConfigurationFileTRECExists()
    {
        return FileFunctions.FileExists(path, fileTREC);
        
    }//isOutsideWarConfigurationFileTRECExists()
    
    public static String getGamsPath(Logger log)
    {
        return getApropertyFromOutsideWarConfiguration(log, "Gams Path",
                filePath, "gams.path");
    }//getGamsPath()
    
    public static String getAllocationInfoPath(Logger log)
    {
        return getApropertyFromOutsideWarConfiguration(log, "AllocationInfo Path",
                filePath, "AllocationInfo.path");
    }//getAllocationInfoPath()
    
    public static String getBackupSMsPath(Logger log)
    {
        return getApropertyFromOutsideWarConfiguration(log, "BackupSMs Path",
                filePath, "path.BackupSMs");
    }//getBackupSMsPath()
    
    public static String getRemoteAdmissionControlIP(Logger log)
    {
        return getApropertyFromOutsideWarConfiguration(log, "Remote Admission Control IP",
                filePath, "remote.admission.control.ip");
    }//getRemoteAdmissionControlIP()
    
    public static String getRemoteAdmissionControlPort(Logger log)
    {
        return getApropertyFromOutsideWarConfiguration(log, "Remote Admission Control Port",
                filePath, "remote.admission.control.port");
    }//getRemoteAdmissionControlPort()
    
    public static String getAdmissionControllerIP(Logger log)
    {
        return getApropertyFromOutsideWarConfiguration(log, "Admission Controller IP",
                filePath, "admission.controller.ip");
    }//getAdmissionControllerIP()
    
    public static String getAdmissionControllerPort(Logger log)
    {
        return getApropertyFromOutsideWarConfiguration(log, "Admission Controller Port",
                filePath, "admission.controller.port");
    }//getAdmissionControllerPort()
    
    public static String getphysicalHostsInfoIP(Logger log)
    {
        return getApropertyFromOutsideWarConfiguration(log, "PhysicalHostsInfo_aaS IP",
                filePath, "physicalHostsInfo_aaS.ip");
    }//getphysicalHostsInfoIP()
    
    public static String getphysicalHostsInfoPort(Logger log)
    {
        return getApropertyFromOutsideWarConfiguration(log, "PhysicalHostsInfo_aaS Port",
                filePath, "physicalHostsInfo_aaS.port");
    }//getphysicalHostsInfoPort()
    
    public static String getRiskIP(Logger log)
    {
        return getApropertyFromOutsideWarConfiguration(log, "Risk IP",
                fileTRECPath, "risk.ip");
    }//getRiskIP()
    
    public static String getRiskPort(Logger log)
    {
        return getApropertyFromOutsideWarConfiguration(log, "Risk Port",
                fileTRECPath, "risk.port");
    }//getRiskPort()
    
    public static String getCostIP(Logger log)
    {
        return getApropertyFromOutsideWarConfiguration(log, "Cost IP",
                fileTRECPath, "cost.ip");
    }//getCostIP()
    
    public static String getCostPort(Logger log)
    {
        return getApropertyFromOutsideWarConfiguration(log, "Cost Port",
                fileTRECPath, "cost.port");
    }//getCostPort()
    
    public static String getEcoIP(Logger log)
    {
        return getApropertyFromOutsideWarConfiguration(log, "Eco IP",
                fileTRECPath, "eco.ip");
    }//getEcoIP()
    
    public static String getEcoPort(Logger log)
    {
        return getApropertyFromOutsideWarConfiguration(log, "Eco Port",
                fileTRECPath, "eco.port");
    }//getEcoPort()
    
    public static String getTrustIP(Logger log)
    {
        return getApropertyFromOutsideWarConfiguration(log, "Trust IP",
                fileTRECPath, "trust.ip");
    }//getTrustIP()
    
    public static String getTrustPort(Logger log)
    {
        return getApropertyFromOutsideWarConfiguration(log, "Trust Port",
                fileTRECPath, "trust.port");
    }//getTrustPort()
    
    public static String getLoggingLevel(Logger log)
    {
        return getApropertyFromOutsideWarConfiguration(log, "Logging Level",
                filePath, "LoggingLevel");
    }//getLoggingLevel()
    
    public static String getWhichSolver(Logger log)
    {
        return getApropertyFromOutsideWarConfiguration(log, "Which Solver",
                filePath, "admission.controller.solver");
    }//getWhichSolver()
    
    public static String getRemoteBackupHost(Logger log)
    {
        return getApropertyFromOutsideWarConfiguration(log, "RemoteBackupHost",
                filePath, "RemoteBackupHost");
    }//getRemoteBackupHost()
    
    public static String getRemoteBackupPort(Logger log)
    {
        return getApropertyFromOutsideWarConfiguration(log, "RemoteBackupPort",
                filePath, "RemoteBackupPort");
    }//getRemoteBackupPort()
    
    public static String getUseRemoteBackup(Logger log)
    {
        return getApropertyFromOutsideWarConfiguration(log, "useRemoteBackup",
                filePath, "useRemoteBackup");
    }//getUseRemoteBackup()
    
    public static String getIP_Id(Logger log)
    {
        return getApropertyFromOutsideWarConfiguration(log, "IP_Id",
                filePath, "IP_Id");
    }//getWhichSolver()
    
    public static String getCloudOptimizerIP(Logger log)
    {
        return getApropertyFromOutsideWarConfiguration(log, "CloudOptimizerIP",
                filePath, "CloudOptimizerIP");
    }//getCloudOptimizerIP()
    
    public static String getCloudOptimizerPort(Logger log)
    {
        return getApropertyFromOutsideWarConfiguration(log, "CloudOptimizerPort",
                filePath, "CloudOptimizerPort");
    }//getCloudOptimizerPort()
    
    public static String getSkipPhysicalHostInfoLevel(Logger log)
    {
        return getApropertyFromOutsideWarConfiguration(log, "skipPhysicalHostInfoLevel",
                filePath, "skipPhysicalHostInfoLevel");
    }//getSkipPhysicalHostInfoLevel()
    
    public static String getSkipTRECLevel(Logger log)
    {
        return getApropertyFromOutsideWarConfiguration(log, "skipTRECLevel",
                fileTRECPath, "skipTRECLevel");
    }//getSkipTRECLevel()
    
    public static String getSkipTRUSTLevel(Logger log)
    {
        return getApropertyFromOutsideWarConfiguration(log, "skipTRUSTLevel",
                fileTRECPath, "skipTRUSTLevel");
    }//getSkipTRUSTLevel()
    
    public static String getSkipRISKLevel(Logger log)
    {
        return getApropertyFromOutsideWarConfiguration(log, "skipRISKLevel",
                fileTRECPath, "skipRISKLevel");
    }//getSkipRISKLevel()
    
    public static String getSkipRISKhostLevel(Logger log)
    {
        return getApropertyFromOutsideWarConfiguration(log, "skipRISKhostLevel",
                fileTRECPath, "skipRISKhostLevel");
    }//getSkipRISKhostLevel()
    
    public static String getSkipECOLevel(Logger log)
    {
        return getApropertyFromOutsideWarConfiguration(log, "skipECOLevel",
                fileTRECPath, "skipECOLevel");
    }//getSkipECOLevel()
    
    public static String getSkipECOhostLevel(Logger log)
    {
        return getApropertyFromOutsideWarConfiguration(log, "skipECOhostLevel",
                fileTRECPath, "skipECOhostLevel");
    }//getSkipECOhostLevel()
    
    public static String getSkipCOSTLevel(Logger log)
    {
        return getApropertyFromOutsideWarConfiguration(log, "skipCOSTLevel",
                fileTRECPath, "skipCOSTLevel");
    }//getSkipCOSTLevel()
    
    public static String getGAMSexecutable(Logger log)
    {
        return getApropertyFromOutsideWarConfiguration(log, "GAMSexecutable",
                fileGAMSPath, "executable");
    }//getGAMSexecutable()
    
    private static String getApropertyFromOutsideWarConfiguration(Logger log, String WhatIwant,
            String FilePath, String key)
    {
        
        log.info("Trying Get "+WhatIwant+" Outside War Configuration");
        
        String value = null;
        
        Properties prop = new Properties();
        
        try {
    		prop.load(new FileInputStream(FilePath));
                
                value = prop.getProperty(key);
                
    	} catch (IOException ex) {
    		log.error(ex.getMessage());
        }
        
        if (value !=null)
            log.info("Got "+WhatIwant+" Outside War Configuration");
        
        return value;
        
    }//getApropertyFromOutsideWarConfiguration()
    
}//class
