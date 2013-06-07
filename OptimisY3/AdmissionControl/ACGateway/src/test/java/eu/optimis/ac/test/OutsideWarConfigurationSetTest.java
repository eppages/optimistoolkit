/*
 * Copyright (c) 2013 National Technical University of Athens (NTUA)
 *	
 *   							MIT License
 *   
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */


package eu.optimis.ac.test;

import com.sun.jersey.core.util.MultivaluedMapImpl;
import eu.optimis.ac.test.configurationTest.OutsideWarConfigurationFileAtServer;
import static eu.optimis.ac.test.configurationTest.SetOutsideWarConfiguration.BackupSMsPath;
import static eu.optimis.ac.test.configurationTest.SetOutsideWarConfiguration.WhichSolver_GAMS;
import static eu.optimis.ac.test.configurationTest.SetOutsideWarConfiguration.WhichSolver_Heuristic;
import static eu.optimis.ac.test.configurationTest.SetOutsideWarConfiguration.WhichSolver_HeuristicSolver_Python_243;
import static eu.optimis.ac.test.configurationTest.SetOutsideWarConfiguration.gamsExecutable;
import static eu.optimis.ac.test.configurationTest.SetOutsideWarConfiguration.remoteAdmissionControlIP;
import static eu.optimis.ac.test.configurationTest.SetOutsideWarConfiguration.remoteAdmissionControlPort;
import javax.ws.rs.core.MultivaluedMap;
import junit.framework.TestCase;

public class OutsideWarConfigurationSetTest  extends TestCase {
    
    private Boolean removePreviousConfiguration = true;
    
    public void testChangeConfiguration()
    {
        
        changeConfiguration(get_TST_OutsideWarConfiguration(),"212.0.127.140","8080");
        changeConfiguration(get_UMU_OutsideWarConfiguration(),"130.239.48.6","8080");
        //changeConfiguration(get_BSC_OutsideWarConfiguration(),"172.16.8.220","8080");
        changeConfiguration(get_ULEEDS_OutsideWarConfiguration(),"88.198.134.18","8080");
        
        changeConfiguration(get_ARSYS_OutsideWarConfiguration(),"82.223.250.34","8080");
        //changeConfiguration(get_INT_OutsideWarConfiguration(),"213.27.211.124","8080");
        changeConfiguration(get_FLEX_OutsideWarConfiguration(),"109.231.120.19","8080");
        
        
        changeConfiguration(get_FLEX2_OutsideWarConfiguration(),"109.231.122.54","8080");
        
        changeConfiguration(get_SL_OutsideWarConfiguration(),"109.231.86.35","8080");
        //changeConfiguration(get_LocalHost_OutsideWarConfiguration(),"localhost","8080");
    }//testChangeConfiguration()
    
    private MultivaluedMap<String, String> get_BSC_OutsideWarConfiguration()
    {
        MultivaluedMap<String, String> formParams = new MultivaluedMapImpl();
        
        //formParams.add("admissionControllerIP", "localhost");
        formParams.add("WhichSolver", WhichSolver_GAMS);
        formParams.add("IP_Id", "BSC");
        //formParams.add("LoggingLevel", "3");
        formParams.add("backupSMsPath", BackupSMsPath);
        
        //formParams.add("useRemoteBackup", "true");
        //formParams.add("RemoteBackupHost", "109.231.86.35");
        //formParams.add("RemoteBackupPort", "8080");
        
        return formParams;
    }//get_BSC_OutsideWarConfiguration()
    
    private MultivaluedMap<String, String> get_ULEEDS_OutsideWarConfiguration()
    {
        MultivaluedMap<String, String> formParams = new MultivaluedMapImpl();
        
        //formParams.add("admissionControllerIP", "localhost");
        formParams.add("WhichSolver", WhichSolver_GAMS);
        //formParams.add("IP_Id", "ULEEDS");
        //formParams.add("LoggingLevel", "3");
        formParams.add("backupSMsPath", BackupSMsPath);
        
        //formParams.add("useRemoteBackup", "true");
        //formParams.add("RemoteBackupHost", "109.231.86.35");
        //formParams.add("RemoteBackupPort", "8080");
        
        return formParams;
    }//get_ULEEDS_OutsideWarConfiguration()
    
    private MultivaluedMap<String, String> get_ARSYS_OutsideWarConfiguration()
    {
        MultivaluedMap<String, String> formParams = new MultivaluedMapImpl();
        
        //formParams.add("admissionControllerIP", "localhost");
        formParams.add("WhichSolver", WhichSolver_GAMS);
        formParams.add("IP_Id", "ARSYS");
        //formParams.add("LoggingLevel", "3");
        formParams.add("backupSMsPath", BackupSMsPath);
        
        return formParams;
    }//get_ARSYS_OutsideWarConfiguration()
    
    private MultivaluedMap<String, String> get_FLEX2_OutsideWarConfiguration()
    {
        MultivaluedMap<String, String> formParams = new MultivaluedMapImpl();
        
        formParams.add("admissionControllerIP", "130.239.48.6");
        formParams.add("WhichSolver", WhichSolver_GAMS);
        //formParams.add("WhichSolver", WhichSolver_HeuristicSolver_Python_243);
        //formParams.add("IP_Id", "FLEX 2");
        //formParams.add("LoggingLevel", "3");
        formParams.add("backupSMsPath", BackupSMsPath);
        
        //formParams.add("useRemoteBackup", "true");
        //formParams.add("RemoteBackupHost", "109.231.86.35");
        //formParams.add("RemoteBackupPort", "8080");
        
        return formParams;
    }//get_FLEX2_OutsideWarConfiguration()
    
    private MultivaluedMap<String, String> get_FLEX_OutsideWarConfiguration()
    {
        MultivaluedMap<String, String> formParams = new MultivaluedMapImpl();
        
        //formParams.add("admissionControllerIP", "localhost");
        formParams.add("WhichSolver", WhichSolver_GAMS);
        //formParams.add("IP_Id", "FLEX");
        //formParams.add("LoggingLevel", "3");
        formParams.add("backupSMsPath", BackupSMsPath);
        
        //formParams.add("useRemoteBackup", "true");
        //formParams.add("RemoteBackupHost", "109.231.86.35");
        //formParams.add("RemoteBackupPort", "8080");
        
        formParams.add("skipTRECLevel", "2");
        
        return formParams;
    }//get_FLEX_OutsideWarConfiguration()
    
    private MultivaluedMap<String, String> get_UMU_OutsideWarConfiguration()
    {
        MultivaluedMap<String, String> formParams = new MultivaluedMapImpl();
        
        //formParams.add("admissionControllerIP", "localhost");
        formParams.add("WhichSolver", WhichSolver_GAMS);
        //formParams.add("IP_Id", "UMU");
        //formParams.add("LoggingLevel", "3");
        formParams.add("backupSMsPath", BackupSMsPath);
        
        //formParams.add("useRemoteBackup", "true");
        //formParams.add("RemoteBackupHost", "109.231.86.35");
        //formParams.add("RemoteBackupPort", "8080");
        
        return formParams;
    }//get_UMU_OutsideWarConfiguration()
    
    private MultivaluedMap<String, String> get_INT_OutsideWarConfiguration()
    {
        MultivaluedMap<String, String> formParams = new MultivaluedMapImpl();
        
        //formParams.add("admissionControllerIP", "localhost");
        formParams.add("WhichSolver", WhichSolver_GAMS);
        formParams.add("IP_Id", "ATOS 2");
        //formParams.add("LoggingLevel", "3");
        formParams.add("backupSMsPath", BackupSMsPath);
        
        formParams.add("skipTRECLevel", "2");
        
        formParams.add("CloudOptimizerIP", "212.0.127.140");
        
        formParams.add("skipECOLevel", "2");
        formParams.add("skipECOhostLevel", "2");
        
        //formParams.add("useRemoteBackup", "true");
        //formParams.add("RemoteBackupHost", "109.231.86.35");
        //formParams.add("RemoteBackupPort", "8080");
        
        return formParams;
    }//get_INT_OutsideWarConfiguration()
    
    private MultivaluedMap<String, String> get_TST_OutsideWarConfiguration()
    {
        MultivaluedMap<String, String> formParams = new MultivaluedMapImpl();
        
        //formParams.add("admissionControllerIP", "localhost");
        formParams.add("WhichSolver", WhichSolver_GAMS);
        //formParams.add("WhichSolver", WhichSolver_HeuristicSolver_Python_243);
        //formParams.add("IP_Id", "ATOS");
        //formParams.add("LoggingLevel", "3");
        formParams.add("backupSMsPath", BackupSMsPath);
        
        formParams.add("remoteAdmissionControlIP", "109.231.86.35");
        //formParams.add("remoteAdmissionControlIP", "130.239.48.6");
        formParams.add("remoteAdmissionControlPort", "8080");
        
        //formParams.add("useRemoteBackup", "true");
        //formParams.add("RemoteBackupHost", "109.231.86.35");
        //formParams.add("RemoteBackupPort", "8080");
        
        formParams.add("skipTRECLevel", "2");
        //formParams.add("skipTRUSTLevel", "2");
        //formParams.add("skipRISKLevel", "2");
        //formParams.add("skipECOLevel", "2");
        //formParams.add("skipECOhostLevel", "2");
        //formParams.add("skipRISKhostLevel", "2");
        //formParams.add("skipCOSTLevel", "2");
        //formParams.add("skipPhysicalHostInfoLevel", "1");
        
        
        
        return formParams;
    }//get_TST_OutsideWarConfiguration()
    
    
    private MultivaluedMap<String, String> get_LocalHost_OutsideWarConfiguration()
    {
        MultivaluedMap<String, String> formParams = new MultivaluedMapImpl();
        
        //formParams.add("admissionControllerIP", "localhost");
        formParams.add("WhichSolver", WhichSolver_GAMS);
        //formParams.add("WhichSolver", WhichSolver_HeuristicSolver_Python_243);
        //formParams.add("IP_Id", "ATOS");
        //formParams.add("LoggingLevel", "3");
        //formParams.add("backupSMsPath", BackupSMsPath);
        
        formParams.add("CloudOptimizerIP", "130.239.48.6");
        
        //formParams.add("useRemoteBackup", "true");
        //formParams.add("RemoteBackupHost", "109.231.86.35");
        //formParams.add("RemoteBackupPort", "8080");
        
        //formParams.add("skipTRECLevel", "2");
        //formParams.add("skipTRUSTLevel", "2");
        //formParams.add("skipRISKLevel", "2");
        //formParams.add("skipECOLevel", "2");
        //formParams.add("skipECOhostLevel", "2");
        //formParams.add("skipRISKhostLevel", "2");
        //formParams.add("skipCOSTLevel", "2");
        //formParams.add("skipPhysicalHostInfoLevel", "1");
        
        
        
        return formParams;
    }//get_LocalHost_OutsideWarConfiguration()
    
    private MultivaluedMap<String, String> get_SL_OutsideWarConfiguration()
    {
        MultivaluedMap<String, String> formParams = new MultivaluedMapImpl();
        
        //formParams.add("WhichSolver", WhichSolver_Heuristic);
        formParams.add("WhichSolver", WhichSolver_GAMS);
        
        formParams.add("LoggingLevel", "3");
        formParams.add("backupSMsPath", BackupSMsPath);
        
        formParams.add("IP_Id", "A MAK");
        
        //formParams.add("gamsExecutable", "/usr/gams24/gams");
                
        formParams.add("CloudOptimizerIP", "212.0.127.140");
        //formParams.add("CloudOptimizerIP", "130.239.48.6");
        formParams.add("CloudOptimizerPort", "8080");
        
        return formParams;
    }//get_SL_OutsideWarConfiguration()
    
    private void changeConfiguration(MultivaluedMap<String, String> formParams,String host,String port)
    {
        OutsideWarConfigurationFileAtServer.SetOutsideWarConfigurationFileAtServer(formParams,host,port,removePreviousConfiguration);
    }//changeConfiguration()
}//class
