/**

Copyright 2013 ATOS SPAIN S.A. 

Licensed under the Apache License, Version 2.0 (the License);
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

Authors :
Oriol Collell, Francisco Javier Nieto. Atos Research and Innovation, Atos SPAIN SA
@email francisco.nieto@atosresearch.eu 

**/

package eu.optimis.interopt.provider.occi;

import eu.optimis.interopt.provider.Service;
import eu.optimis.interopt.provider.ServiceComponent;
import eu.optimis.interopt.provider.ServiceInstantiationException;
import eu.optimis.interopt.provider.VMManagementSystemClient;
import eu.optimis.interopt.provider.VMProperties;
import eu.optimis.interopt.sla.ComponentConfigurationProvider;
import eu.optimis.types.xmlbeans.servicemanifest.XmlBeanServiceManifestDocument;
import java.net.UnknownServiceException;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import org.apache.log4j.Logger;

public class OCCIClient
        implements VMManagementSystemClient
{
    private static Logger log = Logger.getLogger( OCCIClient.class );

    private static int maxvms = 10;
    private String username;
    private String password;
    private String url = ComponentConfigurationProvider.getString( "ServiceInstantiation.url.arsys" ); 
    
    public void setAuth(String auth_username, String password) {
        this.username = auth_username;
        this.password = password;
    }
    
    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public void deployService(String service_id, List<ServiceComponent> serviceComponents, XmlBeanServiceManifestDocument manifest) throws ServiceInstantiationException {
        
    	log.debug("Using arsys URL: " + url);
        
    	OCCI_RestClient rc = new OCCI_RestClient(username, password, url);
        log.debug( "OCCI REST Client is instantiated for deploying" );
        
        if (isDeployed(service_id)) {
            //throw new ServiceInstantiationException("This service is already deployed! "
            //        + "Terminate it before deploying it again.", new java.lang.Throwable());
        	System.out.println("Service " + service_id + " is already deployed.");
        }
        // Get the number of VMs to deploy
        int total_vms = 0;
        int vmId = 0;

        for (ServiceComponent sc : serviceComponents) {
            total_vms = total_vms + sc.getInstances();
            if (total_vms > 0) vmId = total_vms;
        }

        // If sum < maxvms invoke createVM method as many times as needed
        if ( total_vms > OCCIClient.maxvms )
        {
            throw new ServiceInstantiationException("Number of VMs to deploy exceeds the maximum", new java.lang.Throwable() );
        }

        for (ServiceComponent sc : serviceComponents) {
            int numInstances = sc.getInstances();
            log.info( "Number of max vm instances to deploy: " +  numInstances);                
            
            /* Commented, because it deploys all the possible VMs and we don't want this
            for (int j = 0; j < numInstances; j++) {
                // Invoke the service and get response
            	try{
            		log.info("Creating vm for service [" + service_id + "]");
                    String res = rc.createVM(service_id, sc, j+1);
                    // Convert base64 response to xml string
                    log.info("CREATEVM-response: " + res);
                    //Check if VM has been succesfully created
                 // TODO: Check if creation was correct
                    /*if (true) {
                        log.error( "service deployment has failed" );
                        throw new ServiceInstantiationException( "Service deployment has failed: " + res,
                                new java.lang.Throwable() );
                    }
                    
                    //log.trace(res);
            	} catch(Exception e) {
            		log.error("It was not possible to deploy the VM!");
            		log.error(e.getMessage());
            		e.printStackTrace();
            		try{
            			//terminate(service_id);
            		} catch(Exception ex) {ex.printStackTrace();}
            	}
               
            }*/
            // Only deploy one instance of the VM
            try{
        		log.info("Creating vm for service [" + service_id + "]");
                String res = rc.createVM(service_id, sc, vmId);
                log.info("CREATEVM-response: " + res);                
        	} catch(Exception e) {
        		log.error("It was not possible to deploy the VM!");
        		log.error(e.getMessage());
        		e.printStackTrace();
        		try{
        			//terminate(service_id);
        		} catch(Exception ex) {ex.printStackTrace();}
        	}
        }
    }

    @Override
    public List<VMProperties> queryServiceProperties(String serviceId) throws UnknownServiceException {

        try {        	
            OCCI_RestClient rc = new OCCI_RestClient(username, password, url);
            log.debug("OCCI REST Client is instantiated for querying");

            List<VMProperties> res = rc.getServiceVMs(serviceId);
            return res;
        }
        catch (Exception e) {
            throw new UnknownServiceException("Service not found");
        }
    }

    @Override
    public void terminate(String serviceId) throws UnknownServiceException {
    	
        OCCI_RestClient rc = new OCCI_RestClient(username, password, url);
        log.debug("OCCI REST Client is instantiated for terminating");

        rc.terminateService(serviceId);

        // TODO: Check if result is ok
        
        log.info("Servce [" + serviceId + "] terminated successfully.");
    }
    
    public boolean isDeployed(String serviceId) {
        List<VMProperties> vms = null;        
        try {
            vms = queryServiceProperties(serviceId);
            System.out.println("VMs found: " + vms.size());
        } catch (UnknownServiceException e) {
        	e.printStackTrace();
            return false;
        }
        for (VMProperties vm : vms) {
            System.out.println(vm.getId() + " - " + vm.getStatus());
            if (!(vm.getStatus().equals("terminated") ||
                    vm.getStatus().equals("terminating"))) {
                return true;
            }
        }
        return false;
    }
    
    public void deleteVM(String serviceId, int index) {
    	
    	OCCI_RestClient rc = new OCCI_RestClient(username, password, url);
    	log.debug("OCCI REST Client is instantiated for deleting a VM");
        rc.deleteVM(serviceId, index);
    }
    
    public void updateVM(String serviceId, ServiceComponent sc, int index) {
    	log.debug("OCCI REST Client is instantiated for updating a VM");
        OCCI_RestClient rc = new OCCI_RestClient(username, password, url);
        try {
            rc.updateVM(serviceId, sc, index);
        } catch (ServiceInstantiationException ex) {
            java.util.logging.Logger.getLogger(OCCIClient.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void executeAction(String serviceId, String action, int index, Map<String, String> attrs) {
    	
    	OCCI_RestClient rc = new OCCI_RestClient(username, password, url);
    	log.debug("OCCI REST Client is instantiated for executing an action");
        try {
            rc.executeAction(action, serviceId, index, attrs);
        } catch (ServiceInstantiationException ex) {
            java.util.logging.Logger.getLogger(OCCIClient.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public VMProperties getVM(String serviceId, int index) {
    	OCCI_RestClient rc = new OCCI_RestClient(username, password, url);
    	log.debug("OCCI REST Client is instantiated for getting a VM");
        VMProperties res = null;
        try {
            res = rc.getVM(serviceId, index);
        } catch (ServiceInstantiationException ex) {
            java.util.logging.Logger.getLogger(OCCIClient.class.getName()).log(Level.SEVERE, null, ex);
        }
        return res;
    }
    
    public List<Service> getAllVMs() throws UnknownServiceException {
        try {        	
        	OCCI_RestClient rc = new OCCI_RestClient(username, password, url);
        	log.debug("OCCI REST Client is instantiated for getting all VMs");
            List<Service> res = rc.getAllVMs();
            return res;
        }
        catch (Exception e) {
            throw new UnknownServiceException("Service not found");
        }
    }
    
}
