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

/**
 * A service component specifies the number of instances, CPU speed, memory, etc. of the VMs that should be
 * allocated for a component. It is a name value map that specifies the type (key) and the value (value) of
 * the component properties. The property names rely (as far as possible) on the OCCI specification. The
 * following properties are defined:
 * <ul>
 * <li>occi.compute.architecture</li>
 * <li>occi.compute.speed</li>
 * <li>occi.compute.memory</li>
 * <li>occi.compute.cores</li>
 * <li>eu.optimis.vm.image</li>
 * <li>eu.optimis.vm.contextualization.${instance-id} - the URI of the contextualization image for each VM
 * instance. The instance id is hereby a loop variable from 1..n, where n is the number of instances that
 * should be allocated (i.e. if n=2 there will be two properties - eu.optimis.vm.contextualization.1 and
 * eu.optimis.vm.contextualization.2).</li>
 * </ul>
 * All of the above mentioned properties must be specified for a component.
 * 
 * @author owaeld
 * 
 */
public class ServiceComponent extends HashMap<String, String>
{
    /**
     * compute architecture of the VM instances (i.e. "x86")
     */
    public static final String OCCI_COMPUTE_ARCHITECTURE = "occi.compute.architecture";

    /**
     * the individual CPU speed for the VM instances (i.e. "1.33")
     */
    public static final String OCCI_COMPUTE_SPEED = "occi.compute.speed";

    /**
     * the required individual memory of each compute instance (i.e. "2.0")
     */
    public static final String OCCI_COMPUTE_MEMORY = "occi.compute.memory";

    /**
     * the individual CPU cores of each VM instance (i.e. "2")
     */
    public static final String OCCI_COMPUTE_CORES = "occi.compute.cores";

    /**
     * the URI of the VM image to start for each instance (i.e.
     * http://datamanager.optimis.eu/vm#e3ac-a34l-1234)
     */
    public static final String OPTIMIS_VM_IMAGE = "eu.optimis.vm.image";

    /**
     * the number of VM instances to provide for this component (i.e. "4")
     */
    public static final String OPTIMIS_VM_INSTANCES = "eu.optimis.vm.instances";
}
