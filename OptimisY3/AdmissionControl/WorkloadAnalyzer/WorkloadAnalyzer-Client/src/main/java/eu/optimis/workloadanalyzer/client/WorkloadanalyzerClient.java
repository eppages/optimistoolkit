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
package eu.optimis.workloadanalyzer.client;

import java.util.Collection;
import java.util.Vector;

import org.apache.log4j.Logger;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import eu.optimis.schemas.workload.WorkloadAnalysisDocument;
import eu.optimis.workloadanalyzer.exceptions.WorkloadAnalyzerException;
import eu.optimis.workloadanalyzer.utils.WorkloadPropertiesUtil;

/**
 * @author hrasheed
 * 
 */
public class WorkloadanalyzerClient {
	
	private static final Logger log = Logger.getLogger(WorkloadanalyzerClient.class);
	
	private String host = "localhost";
    private int port = 9000;
    private String PATH = "WorkloadAnalyzer/WorkloadAnalyzerService";
    
    private final String OPT_WORKLOAD_ANALYSIS = "performWorkloadAnalysis";
    private final String OPT_ADD_SERVICE_MANIFEST = "addServiceManifest";
    private final String OPT_REMOVE_SERVICE_MANIFEST = "removeServiceManifest";
    private final String OPT_LIST_SERVICE_MANIFEST = "listServiceManifests";
    
    private Client client;

    public WorkloadanalyzerClient() {
    	this.init();
		this.client = Client.create();
    }
    
    private void init() {
    	try {
    		WorkloadPropertiesUtil props = new WorkloadPropertiesUtil();
    		this.host = props.getWorkloadAnalyzerHost();
    		this.port = Integer.parseInt(props.getWorkloadAnalyzerPort());
    		this.PATH = props.getWorkloadAnalyzerURLPath();
    	}catch (Exception e) {
			log.error(e);
		}
	}
    
    public WorkloadanalyzerClient(String host, int port) {
    	this.host = host;
        this.port = port;
        this.client = Client.create();
    }

    public WorkloadanalyzerClient(String host, int port, String path) {
    	this.host = host;
        this.port = port;
        this.PATH = path;
        this.client = Client.create();
    }

    private String getAddress(String interface_option) {
        return "http://" + this.host + ":" + this.port + "/" + this.PATH + "/" + interface_option;
    }

    public WorkloadAnalysisDocument getWorkloadAnalysis(String serviceManisfest) throws WorkloadAnalyzerException {
    	
    	try {
    	
            WebResource webResourceClient = client.resource(this.getAddress(OPT_WORKLOAD_ANALYSIS));

            String outcome = webResourceClient.type("text/plain; charset=utf-8").accept("text/plain; charset=utf-8").post(String.class, serviceManisfest);
            WorkloadAnalysisDocument result = WorkloadAnalysisDocument.Factory.parse(outcome);

            if(log.isDebugEnabled()) {
                log.debug("Result of the webservice operation '" + this.getAddress(OPT_WORKLOAD_ANALYSIS) + "': " + result.toString());
            }
            
            return result;
            
        } catch (Exception e) {       
			log.error(e);
			throw new WorkloadAnalyzerException(e);
		}	
    }
    
    public boolean addServiceManifest(String serviceManisfest) throws WorkloadAnalyzerException {
    	
    	try {
    		
            WebResource webResourceClient = client.resource(this.getAddress(OPT_ADD_SERVICE_MANIFEST));

            String outcome = webResourceClient.type("text/plain; charset=utf-8").accept("text/plain; charset=utf-8").post(String.class, "Service_Maifest_Dummy");
            boolean result = Boolean.parseBoolean(outcome);

            if(log.isDebugEnabled()) {
                log.debug("Result of the webservice operation '" + this.getAddress(OPT_ADD_SERVICE_MANIFEST) + "': " + result);
            }
            
            return result;
            
        } catch (Exception e) {
			log.error(e);
			throw new WorkloadAnalyzerException(e);
		}	
    }
    
    public boolean removeServiceManifest(String serviceManisfestID) throws WorkloadAnalyzerException {
    	
    	try {
    		
            WebResource webResourceClient = client.resource(this.getAddress(OPT_REMOVE_SERVICE_MANIFEST));

            String outcome = webResourceClient.type("text/plain; charset=utf-8").accept("text/plain; charset=utf-8").post(String.class, "SlaID_Dummy");
            boolean result = Boolean.parseBoolean(outcome);

            if(log.isDebugEnabled()) {
                log.debug("Result of the webservice operation '" + this.getAddress(OPT_REMOVE_SERVICE_MANIFEST) + "': " + result);
            }
            
            return result;
            
        } catch (Exception e) {
			log.error(e);
			throw new WorkloadAnalyzerException(e);
		}	
    }
    
    public Collection<String> listServiceManifests() throws WorkloadAnalyzerException {
    	
    	try {
    		
            WebResource webResourceClient = client.resource(this.getAddress(OPT_LIST_SERVICE_MANIFEST));

            String outcome = webResourceClient.get(String.class);
            String[] ids = outcome.split(",");

            Collection<String> slaIDs = new Vector<String>();
            for(String slaID : ids) {
                slaIDs.add(slaID);
            }

            if(log.isDebugEnabled()) {
                log.debug("Result of the webservice operation '" + this.getAddress(OPT_LIST_SERVICE_MANIFEST) + "': " + slaIDs);
            }
            
            return slaIDs;
            
        } catch (Exception e) {
			log.error(e);
			throw new WorkloadAnalyzerException(e);
		}	
    }
}
