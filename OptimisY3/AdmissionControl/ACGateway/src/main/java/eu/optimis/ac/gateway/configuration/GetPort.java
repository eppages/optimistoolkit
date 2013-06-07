/*
 * Copyright (c) 2013 National Technical University of Athens (NTUA)
 *	
 *   							MIT License
 *   
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */


package eu.optimis.ac.gateway.configuration;

import eu.optimis.ac.gateway.configuration.outsideWarConfiguration.OutsideWarConfiguration;
import eu.optimis.ac.gateway.utils.PropertiesUtils;
import org.apache.log4j.Logger;

public class GetPort {
    
    public static String getCloudOptimizerPort(Logger log)
    {
        String CloudOptimizerPort = null;
        
        if(OutsideWarConfiguration.isOutsideWarConfigurationFileExists())
            CloudOptimizerPort = OutsideWarConfiguration.getCloudOptimizerPort(log);
        
        if (CloudOptimizerPort == null)
            CloudOptimizerPort = PropertiesUtils.getBoundle("CloudOptimizer.port");
	        
         return CloudOptimizerPort;
                
    }//getCloudOptimizerPort(Logger log)
    
    public static String getAdmissionControllerPort(Logger log)
    {
        String admissionControllerPort = null;
        
        if(OutsideWarConfiguration.isOutsideWarConfigurationFileExists())
            admissionControllerPort = OutsideWarConfiguration.getAdmissionControllerPort(log);
        
        if (admissionControllerPort == null)
            admissionControllerPort = PropertiesUtils.getBoundle("admission.controller.port");
	        
         return admissionControllerPort;
                
    }//getAdmissionControllerPort(Logger log)
    
    public static String getPhysicalHostsInfo_aaS_Port(Logger log)
    {
        String PhysicalHostsInfo_aaS_Port = null;
        
        if(OutsideWarConfiguration.isOutsideWarConfigurationFileExists())
            PhysicalHostsInfo_aaS_Port = OutsideWarConfiguration.getphysicalHostsInfoPort(log);
        
        if (PhysicalHostsInfo_aaS_Port == null)
            PhysicalHostsInfo_aaS_Port = PropertiesUtils.getBoundle("physicalHostsInfo_aaS.port");
	        
         return PhysicalHostsInfo_aaS_Port;
                
    }//getPhysicalHostsInfo_aaS_Port(Logger log)
    
    public static String getCostPort(Logger log)
    {
        String costPort = null;
        
        if(OutsideWarConfiguration.isOutsideWarConfigurationFileTRECExists())
            costPort = OutsideWarConfiguration.getCostPort(log);
        
        if (costPort == null)
            costPort = PropertiesUtils.getBoundle("cost.port");
	        
         return costPort;
                
    }//getCostPort(Logger log)
    
    public static String getRiskPort(Logger log)
    {
        String riskPort = null;
        
        if(OutsideWarConfiguration.isOutsideWarConfigurationFileTRECExists())
            riskPort = OutsideWarConfiguration.getRiskPort(log);
        
        if (riskPort == null)
            riskPort = PropertiesUtils.getBoundle("risk.port");
	        
         return riskPort;
                
    }//getRiskPort(Logger log)
    
    public static String getTrustPort(Logger log)
    {
        String trustPort = null;
        
        if(OutsideWarConfiguration.isOutsideWarConfigurationFileTRECExists())
            trustPort = OutsideWarConfiguration.getTrustPort(log);
        
        if (trustPort == null)
            trustPort = PropertiesUtils.getBoundle("trust.port");
	        
         return trustPort;
                
    }//getTrustPort(Logger log)
    
    public static String getEcoPort(Logger log)
    {
        String ecoPort = null;
        
        if(OutsideWarConfiguration.isOutsideWarConfigurationFileTRECExists())
            ecoPort = OutsideWarConfiguration.getEcoPort(log);
        
        if (ecoPort == null)
            ecoPort = PropertiesUtils.getBoundle("eco.port");
	        
         return ecoPort;
                
    }//getEcoPort(Logger log)
    
    public static String getRemoteAdmissionControlPort(Logger log)
    {
        String remoteAdmissionControlPort = null;
        
        if(OutsideWarConfiguration.isOutsideWarConfigurationFileExists())
            remoteAdmissionControlPort = OutsideWarConfiguration.getRemoteAdmissionControlPort(log);
        
        if (remoteAdmissionControlPort == null)
            remoteAdmissionControlPort = PropertiesUtils.getBoundle("remote.admission.control.port");
	        
         return remoteAdmissionControlPort;
                
    }//getEcoIP(Logger log)
}//class
