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

import eu.optimis.ac.gateway.utils.Paths;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import javax.ws.rs.core.MultivaluedMap;
import org.apache.log4j.Logger;

public class CreateOutsideWarConfiguration {
    
    public static String CreateFile(MultivaluedMap<String, String> formParams, Logger log)
    {
       
        Properties prop = new Properties();
        
        MultivaluedMap<String, String> Params = UpdateMap.update(formParams, 
            OutsideWarConfiguration.path, OutsideWarConfiguration.filename, log);
        
        try {
    		//set the properties value
            
                if(Params.containsKey("LoggingLevel"))
                    prop.setProperty("LoggingLevel", Params.get("LoggingLevel").get(0));
            
                if(Params.containsKey("WhichSolver"))
                    prop.setProperty("admission.controller.solver", Params.get("WhichSolver").get(0));
                
                if(Params.containsKey("IP_Id"))
                    prop.setProperty("IP_Id", Params.get("IP_Id").get(0));
                
                if(Params.containsKey("gamsPath"))
                    prop.setProperty("gams.path", Params.get("gamsPath").get(0));
    		if(Params.containsKey("allocationPath"))
                    prop.setProperty("AllocationInfo.path", Params.get("allocationPath").get(0));
    		if(Params.containsKey("backupSMsPath"))
                    prop.setProperty("path.BackupSMs", Params.get("backupSMsPath").get(0));
                
                if(Params.containsKey("admissionControllerIP"))
                    prop.setProperty("admission.controller.ip", Params.get("admissionControllerIP").get(0));
    		if(Params.containsKey("admissionControllerPort"))
                    prop.setProperty("admission.controller.port", Params.get("admissionControllerPort").get(0));
                
                if(Params.containsKey("remoteAdmissionControlIP"))
                    prop.setProperty("remote.admission.control.ip", Params.get("remoteAdmissionControlIP").get(0));
    		if(Params.containsKey("remoteAdmissionControlPort"))
                    prop.setProperty("remote.admission.control.port", Params.get("remoteAdmissionControlPort").get(0));
                
                if(Params.containsKey("physicalHostsInfo_aaS_IP"))
                    prop.setProperty("physicalHostsInfo_aaS.ip", Params.get("physicalHostsInfo_aaS_IP").get(0));
    		if(Params.containsKey("physicalHostsInfo_aaS_Port"))
                    prop.setProperty("physicalHostsInfo_aaS.port", Params.get("physicalHostsInfo_aaS_Port").get(0));
                
                if(Params.containsKey("skipPhysicalHostInfoLevel"))
                    prop.setProperty("skipPhysicalHostInfoLevel", Params.get("skipPhysicalHostInfoLevel").get(0));
                
                if(Params.containsKey("useRemoteBackup"))
                    prop.setProperty("useRemoteBackup", Params.get("useRemoteBackup").get(0));
                if(Params.containsKey("RemoteBackupHost"))
                    prop.setProperty("RemoteBackupHost", Params.get("RemoteBackupHost").get(0));
                if(Params.containsKey("RemoteBackupPort"))
                    prop.setProperty("RemoteBackupPort", Params.get("RemoteBackupPort").get(0));
                
                
                if(Params.containsKey("CloudOptimizerIP"))
                    prop.setProperty("CloudOptimizerIP", Params.get("CloudOptimizerIP").get(0));
                if(Params.containsKey("CloudOptimizerPort"))
                    prop.setProperty("CloudOptimizerPort", Params.get("CloudOptimizerPort").get(0));
                
                Paths.CreateDirectory(OutsideWarConfiguration.path);
                
    		//save properties to project root folder
    		prop.store(new FileOutputStream(OutsideWarConfiguration.filePath), null);
 
    	} catch (IOException ex) {
    		log.error(ex.getMessage());
                return ex.getMessage();
        }
        
        return OutsideWarConfiguration.filePath;
    }//CreateFile()
    
    public static String CreateGamsFile(MultivaluedMap<String, String> formParams, Logger log)
    {
        
        Properties prop = new Properties();
        
        MultivaluedMap<String, String> Params = UpdateMap.update(formParams, 
            OutsideWarConfiguration.fileGAMSPath, OutsideWarConfiguration.fileGAMS, log);
        
        try {
    		//set the properties value
                if(Params.containsKey("gamsExecutable"))
                    prop.setProperty("executable", Params.get("gamsExecutable").get(0));
    		
                Paths.CreateDirectory(OutsideWarConfiguration.path);
                
    		//save properties to project root folder
    		prop.store(new FileOutputStream(OutsideWarConfiguration.fileGAMSPath), null);
 
    	} catch (IOException ex) {
    		log.error(ex.getMessage());
                return ex.getMessage();
        }
        
        return OutsideWarConfiguration.fileGAMSPath;
    }//CreateGamsFile()
    
    public static String CreateTRECFile(MultivaluedMap<String, String> formParams,Logger log)
    {
        
        Properties prop = new Properties();
        
        MultivaluedMap<String, String> Params = UpdateMap.update(formParams, 
            OutsideWarConfiguration.fileTRECPath, OutsideWarConfiguration.fileTREC, log);
        
        try {
    		//set the properties value
                if(Params.containsKey("ecoIP"))
                    prop.setProperty("eco.ip", Params.get("ecoIP").get(0));
                if(Params.containsKey("ecoPort"))
                    prop.setProperty("eco.port", Params.get("ecoPort").get(0));
    		
                if(Params.containsKey("riskIP"))
                    prop.setProperty("risk.ip", Params.get("riskIP").get(0));
                if(Params.containsKey("riskPort"))
                    prop.setProperty("risk.port", Params.get("riskPort").get(0));
                
                if(Params.containsKey("costIP"))
                    prop.setProperty("cost.ip", Params.get("costIP").get(0));
                if(Params.containsKey("costPort"))
                    prop.setProperty("cost.port", Params.get("costPort").get(0));
                
                if(Params.containsKey("trustIP"))
                    prop.setProperty("trust.ip", Params.get("trustIP").get(0));
                if(Params.containsKey("trustPort"))
                    prop.setProperty("trust.port", Params.get("trustPort").get(0));
                
                if(Params.containsKey("skipTRECLevel"))
                    prop.setProperty("skipTRECLevel", Params.get("skipTRECLevel").get(0));
                
                if(Params.containsKey("skipTRUSTLevel"))
                    prop.setProperty("skipTRUSTLevel", Params.get("skipTRUSTLevel").get(0));
                
                if(Params.containsKey("skipRISKLevel"))
                    prop.setProperty("skipRISKLevel", Params.get("skipRISKLevel").get(0));
                if(Params.containsKey("skipRISKhostLevel"))
                    prop.setProperty("skipRISKhostLevel", Params.get("skipRISKhostLevel").get(0));
                
                if(Params.containsKey("skipECOLevel"))
                    prop.setProperty("skipECOLevel", Params.get("skipECOLevel").get(0));
                if(Params.containsKey("skipECOhostLevel"))
                    prop.setProperty("skipECOhostLevel", Params.get("skipECOhostLevel").get(0));
                
                if(Params.containsKey("skipCOSTLevel"))
                    prop.setProperty("skipCOSTLevel", Params.get("skipCOSTLevel").get(0));
                
                Paths.CreateDirectory(OutsideWarConfiguration.path);
                
    		//save properties to project root folder
    		prop.store(new FileOutputStream(OutsideWarConfiguration.fileTRECPath), null);
 
    	} catch (IOException ex) {
    		log.error(ex.getMessage());
                return ex.getMessage();
        }
        
        return OutsideWarConfiguration.fileTRECPath;
    }//CreateTRECFile()
}//class
