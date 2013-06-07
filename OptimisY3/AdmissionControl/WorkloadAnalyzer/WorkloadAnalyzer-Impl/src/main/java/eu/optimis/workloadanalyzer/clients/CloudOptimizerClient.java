/* 
 * Copyright (c) 2007, Fraunhofer-Gesellschaft
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 * 
 * (1) Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the disclaimer at the end.
 *     Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in
 *     the documentation and/or other materials provided with the
 *     distribution.
 * 
 * (2) Neither the name of Fraunhofer nor the names of its
 *     contributors may be used to endorse or promote products derived
 *     from this software without specific prior written permission.
 * 
 * DISCLAIMER
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *  
 */
package eu.optimis.workloadanalyzer.clients;

import java.util.List;
import java.util.Vector;

import org.apache.log4j.Logger;

import eu.optimis.cloudoptimizer.rest.client.CloudOptimizerRESTClient;

/**
 * @author hrasheed
 * 
 */
public class CloudOptimizerClient
{
    private static final Logger LOG = Logger.getLogger( CloudOptimizerClient.class );
    
    private CloudOptimizerRESTClient defaultCloudOptimizerClient = null;
    
    public CloudOptimizerClient(String host, int port) throws Exception 
    {    
        defaultCloudOptimizerClient = new CloudOptimizerRESTClient(host,port);
    }
    
    public List<String> getPhysicalHosts()
    {
        return defaultCloudOptimizerClient.getNodesId();
    }
    
    public List<String> getVirtualHosts(String physicalId)
    {
        return defaultCloudOptimizerClient.getVMsId(physicalId);
    }
    
    public List<String> getVirtualHosts(List<String> physicalHosts)
    {
        Vector<String> vmIDs = new Vector<String>();
        
        List<String> currentVMIDs = null;

        for(String physicalId : physicalHosts) 
        {
            if(LOG.isDebugEnabled())
            {
                LOG.debug("Physical Node : " + physicalId);
            }
            
            currentVMIDs = defaultCloudOptimizerClient.getVMsId(physicalId);
            
            for(String virtualId : currentVMIDs)
            {
                if(LOG.isDebugEnabled())
                {
                    LOG.debug("virtual node on " + physicalId + " : " + virtualId);
                }
                vmIDs.add(virtualId);
            }
        }
        
        return vmIDs;   
    }
    
}
