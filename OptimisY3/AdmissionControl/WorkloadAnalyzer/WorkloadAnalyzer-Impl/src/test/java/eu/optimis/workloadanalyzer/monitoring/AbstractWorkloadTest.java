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
package eu.optimis.workloadanalyzer.monitoring;

import junit.framework.TestCase;

import org.apache.log4j.Logger;

import eu.optimis.workloadanalyzer.utils.WorkloadPropertiesUtil;

/**
 * @author hrasheed
 * 
 */
public abstract class AbstractWorkloadTest extends TestCase {

    private static final Logger log = Logger.getLogger(AbstractWorkloadTest.class);
    
    private String mmHost = "localhost";
    private int mmPort = 8080;
    private String mmPath = "";
    
    private String coHost = "localhost";
    private int coPort = 8080;
    
    public AbstractWorkloadTest() {
        super();
    }

    public AbstractWorkloadTest(String name) {
        super(name);
    }
    
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        if(log.isDebugEnabled()) {
            log.debug("================================================================================");
            log.debug("Entering unit test: " + this.getName());
            log.debug("--------------------------------------------------------------------------------");
        } 
        
        WorkloadPropertiesUtil props = new WorkloadPropertiesUtil();
        
        mmHost = props.getMonitoringHost();
        mmPort = Integer.parseInt((props.getMonitoringPort()).trim());
        mmPath = props.getMonitoringURLPath();
        
        coHost = props.getCloudOptimizerHost();
        coPort = Integer.parseInt((props.getCloudOptimizerPort()).trim());
        
        if(log.isDebugEnabled()) {
            log.debug("monitoring-host: " + mmHost);
            log.debug("monitoring-port: " + mmPort);
            log.debug("monitoring-interface-path: " + mmPath);
            log.debug("co-host: " + coHost);
            log.debug("co-port: " + coPort);
        }
    }

    @Override
    protected void tearDown() throws Exception {
        if(log.isDebugEnabled()) {
            log.debug("--------------------------------------------------------------------------------");
            log.debug("Leaving unit test: " + this.getName());
            log.debug("================================================================================");
        }
        super.tearDown();
    }
    
    protected String getMonitoringHost() {
        return mmHost;
    }
    
    protected int getMonitoringPort() {
        return mmPort;
    }
    
    protected String getMonitoringPath() {
        return mmPath;
    }
    
    protected String getCOHost() {
        return coHost;
    }
    
    protected int getCOPort() {
        return coPort;
    }
}
