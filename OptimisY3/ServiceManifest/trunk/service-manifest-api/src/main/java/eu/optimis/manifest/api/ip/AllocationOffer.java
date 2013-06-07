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
package eu.optimis.manifest.api.ip;

import org.apache.xmlbeans.XmlObject;

/**
 * @author geli - 3/13/12
 */
public interface AllocationOffer
{
    /**
     * get the allocation pattern array.
     * 
     * @return list of allocation patterns as array
     */
    AllocationPattern[] getAllocationPatternArray();

    /**
     * @param i
     * @return the allocation pattern at position i
     */
    AllocationPattern getAllocationPattern( int i );

    /**
     * retrieve the allocation pattern for this component. As only one application pattern per component can
     * exist, it must return only one application pattern.
     * 
     * @param componentId
     * @return
     */
    AllocationPattern getAllocationPattern( String componentId );

    /**
     * add new allocation pattern
     * 
     * @return the allocation pattern
     */
    AllocationPattern addNewAllocationPattern( String componentId );

    /**
     * remove allocation pattern
     * 
     * @param i
     *            the ith position
     */
    void removeAllocationPattern( int i );

    /**
     * @return an array of external deployments
     */
    ExternalDeployment[] getExternalDeploymentArray();

    /**
     * get the external dpeloyment at the position i
     * 
     * @param i
     *            the ith position
     * @return an external deployment
     */
    ExternalDeployment getExternalDeployment( int i );

    /**
     * add an external deployment to the array of deployments. An external deployment contains a manifest
     * (usually an extracted component of the current manifest) and the provider id, where the service
     * described in the manifest is going to be deployed.
     * 
     * @param providerId
     * @param manifest
     * @return
     */
    ExternalDeployment addNewExternalDeployment( String providerId, Manifest manifest );

    /**
     * remove the external deployment at position i
     * 
     * @param i
     *            the position
     */
    void removeExternalDeployment( int i );

    /**
     * @return the cost description
     */
    XmlObject getCost();

    /**
     * set the cost description
     * 
     * @param cost
     */
    void setCost( XmlObject cost );

    /**
     * get the risk
     * 
     * @return
     */
    float getRisk();

    /**
     * set risk
     * 
     * @param risk
     */
    void setRisk( float risk );

    /**
     * set the decision of the allocation offer rejected : the service cannot be deployed at all accepted: the
     * service can be deployed fully partial: the service can be deployed only partially, some components
     * cannot be hosted.
     * 
     * @param decision
     */
    void setDecision( String decision );

    /**
     * retrieve the decision of admission control
     * 
     * @return the decision: rejected, accepted, partial
     */
    String getDecision();
}
