/* 
 * Copyright (c) 2012, Fraunhofer-Gesellschaft
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
package eu.optimis.interopt.provider;

import java.util.HashMap;
import java.util.Map;

/**
 * The {@link VMProperties} specify basic monitoring properties of a particular VM instance. In order to be
 * compliant with the OPTIMIS infrastructure a provider MUST support monitoring of the provided virtual
 * machine instances. VM properties must at least specify the state of a VM and the id of the VM instance.
 * 
 * Additionally, an infrastructure provider SHOULD expose at least the following additional properties:
 * <ul>
 * <li>occi.compute.hostname - the hostname/ip of the VM instance</li>
 * </ul>
 * 
 * Besides this information the infrastructure provider may expose additional information, i.e. VM information
 * that is defined by the OCCI specification.
 * 
 * @author owaeld
 * 
 */
public class VMProperties extends HashMap<String, String>
    implements Map<String, String>
{
    private static final long serialVersionUID = 1L;

    private String id;

    private String status;

    /**
     * the name or IP of the VM instance (i.e. "127.0.0.1")
     */
    public static final String OCCI_COMPUTE_HOSTNAME = "occi.compute.hostname";

    /**
     * Returns the id of a particular VM instance.
     * 
     * @return the id of the VM instance
     */
    public String getId()
    {
        return id;
    }

    /**
     * Sets the id of a VM instance.
     * 
     * @param id
     *            the VM id
     */
    public void setId( String id )
    {
        this.id = id;
    }

    /**
     * Retrieves the status of a VM. The status should be compliant to the states as defined by the OCCI
     * specification.
     * 
     * @return VM status
     */
    public String getStatus()
    {
        return status;
    }

    /**
     * Sets the status of a VM. The status should be compliant to the states as defined by the OCCI
     * specification.
     * 
     * @param status
     *            the VM status.
     */
    public void setStatus( String status )
    {
        this.status = status;
    }

}
