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

public class GetIP {
    
    public static String getCloudOptimizerIP(Logger log)
    {
        String CloudOptimizerIP = null;
        
        if(OutsideWarConfiguration.isOutsideWarConfigurationFileExists())
            CloudOptimizerIP = OutsideWarConfiguration.getCloudOptimizerIP(log);
        
        if (CloudOptimizerIP == null)
            CloudOptimizerIP = PropertiesUtils.getBoundle("CloudOptimizer.ip");
	        
         return CloudOptimizerIP;
                
    }//getCloudOptimizerIP(Logger log)
    
    public static String getAdmissionControllerIP(Logger log)
    {
        String admissionControllerIP = null;
        
        if(OutsideWarConfiguration.isOutsideWarConfigurationFileExists())
            admissionControllerIP = OutsideWarConfiguration.getAdmissionControllerIP(log);
        
        if (admissionControllerIP == null)
            admissionControllerIP = PropertiesUtils.getBoundle("admission.controller.ip");
	        
         return admissionControllerIP;
                
    }//getAdmissionControllerIP(Logger log)
    
    public static String getPhysicalHostsInfo_aaS_IP(Logger log)
    {
        String PhysicalHostsInfo_aaS_IP = null;
        
        if(OutsideWarConfiguration.isOutsideWarConfigurationFileExists())
            PhysicalHostsInfo_aaS_IP = OutsideWarConfiguration.getphysicalHostsInfoIP(log);
        
        if (PhysicalHostsInfo_aaS_IP == null)
            PhysicalHostsInfo_aaS_IP = PropertiesUtils.getBoundle("physicalHostsInfo_aaS.ip");
	        
         return PhysicalHostsInfo_aaS_IP;
                
    }//getPhysicalHostsInfo_aaS_IP(Logger log)
    
    public static String getCostIP(Logger log)
    {
        String costIP = null;
        
        if(OutsideWarConfiguration.isOutsideWarConfigurationFileTRECExists())
            costIP = OutsideWarConfiguration.getCostIP(log);
        
        if (costIP == null)
            costIP = PropertiesUtils.getBoundle("cost.ip");
	        
         return costIP;
                
    }//getCostIP(Logger log)
    
    public static String getRiskIP(Logger log)
    {
        String riskIP = null;
        
        if(OutsideWarConfiguration.isOutsideWarConfigurationFileTRECExists())
            riskIP = OutsideWarConfiguration.getRiskIP(log);
        
        if (riskIP == null)
            riskIP = PropertiesUtils.getBoundle("risk.ip");
	        
         return riskIP;
                
    }//getRiskIP(Logger log)
    
    public static String getTrustIP(Logger log)
    {
        String trustIP = null;
        
        if(OutsideWarConfiguration.isOutsideWarConfigurationFileTRECExists())
            trustIP = OutsideWarConfiguration.getTrustIP(log);
        
        if (trustIP == null)
            trustIP = PropertiesUtils.getBoundle("trust.ip");
	        
         return trustIP;
                
    }//getTrustIP(Logger log)
    
    public static String getEcoIP(Logger log)
    {
        String ecoIP = null;
        
        if(OutsideWarConfiguration.isOutsideWarConfigurationFileTRECExists())
            ecoIP = OutsideWarConfiguration.getEcoIP(log);
        
        if (ecoIP == null)
            ecoIP = PropertiesUtils.getBoundle("eco.ip");
	        
         return ecoIP;
                
    }//getEcoIP(Logger log)
    
    public static String getRemoteAdmissionControlIP(Logger log)
    {
        String remoteAdmissionControlIP = null;
        
        if(OutsideWarConfiguration.isOutsideWarConfigurationFileExists())
            remoteAdmissionControlIP = OutsideWarConfiguration.getRemoteAdmissionControlIP(log);
        
        if (remoteAdmissionControlIP == null)
            remoteAdmissionControlIP = PropertiesUtils.getBoundle("remote.admission.control.ip");
	        
         return remoteAdmissionControlIP;
                
    }//getEcoIP(Logger log)
}//class
