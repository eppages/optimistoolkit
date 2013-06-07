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

import com.sun.jersey.core.util.MultivaluedMapImpl;
import eu.optimis.ac.gateway.utils.FileFunctions;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import javax.ws.rs.core.MultivaluedMap;
import org.apache.log4j.Logger;

class UpdateMap {
    
    protected static MultivaluedMap<String, String> update(MultivaluedMap<String, String> formParams, 
            String path, String filename, Logger log)
    {
        Properties props = new Properties();
        
        MultivaluedMap<String, String> Params = new MultivaluedMapImpl();
        
        if(!FileFunctions.FileExists(path, filename))
            return formParams;
        
       try {
    		props.load(new FileInputStream(path+filename));
 
    	} catch (IOException ex) {
    		log.error(ex.getMessage());
        } 

       getValue(props,formParams,Params,"IP_Id","IP_Id",log);
       getValue(props,formParams,Params,"LoggingLevel","LoggingLevel",log);
       getValue(props,formParams,Params,"admission.controller.solver","WhichSolver",log);
       getValue(props,formParams,Params,"gams.path","gamsPath",log);
       getValue(props,formParams,Params,"AllocationInfo.path","allocationPath",log);
       getValue(props,formParams,Params,"path.BackupSMs","backupSMsPath",log);
       getValue(props,formParams,Params,"admission.controller.ip","admissionControllerIP",log);
       getValue(props,formParams,Params,"admission.controller.port","admissionControllerPort",log);
       getValue(props,formParams,Params,"remote.admission.control.ip","remoteAdmissionControlIP",log);
       getValue(props,formParams,Params,"remote.admission.control.port","remoteAdmissionControlPort",log);
       getValue(props,formParams,Params,"physicalHostsInfo_aaS.ip","physicalHostsInfo_aaS_IP",log);
       getValue(props,formParams,Params,"physicalHostsInfo_aaS.port","physicalHostsInfo_aaS_Port",log);
       getValue(props,formParams,Params,"executable","gamsExecutable",log);
       getValue(props,formParams,Params,"eco.ip","ecoIP",log);
       getValue(props,formParams,Params,"eco.port","ecoPort",log);
       getValue(props,formParams,Params,"risk.ip","riskIP",log);
       getValue(props,formParams,Params,"risk.port","riskPort",log);
       getValue(props,formParams,Params,"cost.ip","costIP",log);
       getValue(props,formParams,Params,"cost.port","costPort",log);
       getValue(props,formParams,Params,"trust.ip","trustIP",log);
       getValue(props,formParams,Params,"trust.port","trustPort",log);
       
       getValue(props,formParams,Params,"skipCOSTLevel","skipCOSTLevel",log);
       getValue(props,formParams,Params,"skipECOhostLevel","skipECOhostLevel",log);
       getValue(props,formParams,Params,"skipECOLevel","skipECOLevel",log);
       getValue(props,formParams,Params,"skipRISKhostLevel","skipRISKhostLevel",log);
       getValue(props,formParams,Params,"skipRISKLevel","skipRISKLevel",log);
       getValue(props,formParams,Params,"skipTRUSTLevel","skipTRUSTLevel",log);
       getValue(props,formParams,Params,"skipTRECLevel","skipTRECLevel",log);
       
       getValue(props,formParams,Params,"skipPhysicalHostInfoLevel","skipPhysicalHostInfoLevel",log);
       
       getValue(props,formParams,Params,"useRemoteBackup","useRemoteBackup",log);
       getValue(props,formParams,Params,"RemoteBackupHost","RemoteBackupHost",log);
       getValue(props,formParams,Params,"RemoteBackupPort","RemoteBackupPort",log);
       
       getValue(props,formParams,Params,"CloudOptimizerIP","CloudOptimizerIP",log);
       getValue(props,formParams,Params,"CloudOptimizerPort","CloudOptimizerPort",log);
       return Params;
    }//update()
    
    private static void getValue(Properties props,
            MultivaluedMap<String, String> formParams, 
            MultivaluedMap<String, String> Params, 
            String properties_KEY, String map_KEY,Logger log)
    {
        
        if(formParams.containsKey(map_KEY))
        {
            Params.add(map_KEY, formParams.get(map_KEY).get(0));
            return ;
        }
        
        if(props.getProperty(properties_KEY)!=null)
        {
            Params.add(map_KEY, props.getProperty(properties_KEY));
            return ;
        }
        
    }//getValue()
    
}//class
