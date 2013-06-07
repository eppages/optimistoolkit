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

import com.sun.jersey.core.util.MultivaluedMapImpl;
import javax.ws.rs.core.MultivaluedMap;

public class SetOutsideWarConfiguration {
    
    public static String LoggingLevel ="3";
    
    public static String WhichSolver_HeuristicSolver_Python_243 =  "use_HeuristicSolver_Python_243";
    public static String WhichSolver_Heuristic =  "use_Heuristic";
    public static String WhichSolver_GAMS =  "use_GAMS";
    
    public static String IP_Id = "ATOS";
    
    public static String WhichSolver = WhichSolver_GAMS;
            
    public static String GamsPath = "/opt/optimis/etc/AdmissionControl/gams/";
    public static String AllocationPath = "/opt/optimis/etc/AdmissionControl/AllocationInfo/";
    public static String BackupSMsPath = "/opt/optimis/etc/AdmissionControl/UsedSMs/";
    
    public static String gamsExecutable = "/usr/gams2395/gams";
        
    public static String admissionControllerIP = "localhost";
    public static String admissionControllerPort = "8080";
    public static String remoteAdmissionControlIP = "212.0.127.140";
    public static String remoteAdmissionControlPort = "8080";
    public static String physicalHostsInfoIP = "localhost";
    public static String physicalHostsInfoPort = "8080";
        
    public static String ecoIP = "localhost";
    public static String ecoPort = "8080";
    public static String costIP = "localhost";
    public static String costPort = "8080";
    public static String trustIP = "localhost";
    public static String trustPort = "8080";
    public static String riskIP = "localhost";
    public static String riskPort = "8080";
    
    public static String skipTRECLevel = "2";
    public static String skipTRUSTLevel = "2";
    public static String skipRISKLevel = "2";
    public static String skipRISKhostLevel = "2";
    public static String skipECOhostLevel = "2";
    public static String skipECOLevel = "2";
    public static String skipCOSTLevel = "2";
    public static String skipPhysicalHostInfoLevel = "1";
    
    public static String useRemoteBackup = "true";
    public static String RemoteBackupHost = "109.231.86.35";
    public static String RemoteBackupPort = "8080";
    
    public static String CloudOptimizerIP = "localhost";
    public static String CloudOptimizerPort = "8080";
    
    public static MultivaluedMap<String, String> SetOutsideWarConfigurationFile()
    {
        MultivaluedMap<String, String> formParams = new MultivaluedMapImpl();
		
        formParams.add("gamsPath", GamsPath);
        formParams.add("allocationPath", AllocationPath);
        formParams.add("backupSMsPath", BackupSMsPath);
        
        formParams.add("admissionControllerIP", admissionControllerIP);
        formParams.add("admissionControllerPort", admissionControllerPort);
        formParams.add("remoteAdmissionControlIP", remoteAdmissionControlIP);
        formParams.add("remoteAdmissionControlPort", remoteAdmissionControlPort);
        formParams.add("physicalHostsInfo_aaS_IP", physicalHostsInfoIP);
        formParams.add("physicalHostsInfo_aaS_Port", physicalHostsInfoPort);
        
        formParams.add("gamsExecutable", gamsExecutable);
        
        formParams.add("ecoIP", ecoIP);
        formParams.add("ecoPort", ecoPort);
        formParams.add("costIP", costIP);
        formParams.add("costPort", costPort);
        formParams.add("trustIP", trustIP);
        formParams.add("trustPort", trustPort);
        formParams.add("riskIP", riskIP);
        formParams.add("riskPort", riskPort);
       
        formParams.add("LoggingLevel", LoggingLevel);
        
        formParams.add("WhichSolver", WhichSolver);
        
        formParams.add("IP_Id", IP_Id);
        
        formParams.add("skipTRECLevel", skipTRECLevel);
        formParams.add("skipTRUSTLevel", skipTRUSTLevel);
        formParams.add("skipRISKLevel", skipRISKLevel);
        formParams.add("skipECOLevel", skipECOLevel);
        formParams.add("skipECOhostLevel", skipECOhostLevel);
        formParams.add("skipRISKhostLevel", skipRISKhostLevel);
        formParams.add("skipCOSTLevel", skipCOSTLevel);
        formParams.add("skipPhysicalHostInfoLevel", skipPhysicalHostInfoLevel);
        
        formParams.add("useRemoteBackup", useRemoteBackup);
        formParams.add("RemoteBackupHost", RemoteBackupHost);
        formParams.add("RemoteBackupPort", RemoteBackupPort);
        
        formParams.add("CloudOptimizerIP", CloudOptimizerIP);
        formParams.add("CloudOptimizerPort", CloudOptimizerPort);
        
        return formParams;
        
    }//SetOutsideWarConfigurationFile()
    
    public static MultivaluedMap<String, String> SetOutsideWarConfigurationFileWithoutProperties()
    {
        MultivaluedMap<String, String> formParams = new MultivaluedMapImpl();
	
        return formParams;
        
    }//SetOutsideWarConfigurationFileWithoutProperties()
    
    public static MultivaluedMap<String, String> SetOutsideWarConfigurationFileForUsedSMsAndLoggingAndSolver()
    {
        MultivaluedMap<String, String> formParams = new MultivaluedMapImpl();
	
        formParams.add("backupSMsPath", BackupSMsPath);
        formParams.add("LoggingLevel", LoggingLevel);
        formParams.add("WhichSolver", WhichSolver);
        
        return formParams;
        
    }//SetOutsideWarConfigurationFile()
    
    public static MultivaluedMap<String, String> SetSolver(String Solver)
    {
        MultivaluedMap<String, String> formParams = new MultivaluedMapImpl();
	
        if(Solver.contains("Heuristic"))
            formParams.add("WhichSolver", WhichSolver_Heuristic);
        else if(Solver.contains("GAMS"))
            formParams.add("WhichSolver", WhichSolver_GAMS);
        else {System.err.println("Solver Selection ERROR : "+Solver); throw new RuntimeException("Solver Selection ERROR : "+Solver);}
        
        return formParams;
        
    }//SetOutsideWarConfigurationFile()
}//class
