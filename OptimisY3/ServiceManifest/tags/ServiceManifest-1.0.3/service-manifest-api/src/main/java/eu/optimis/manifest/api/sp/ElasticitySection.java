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
 */

package eu.optimis.manifest.api.sp;

import eu.optimis.manifest.api.impl.ElasticityVariableImpl;

/**
 * @author arumpl
 */
public interface ElasticitySection {
    /**
     * retrieves the elasticity rule at the ith position
     *
     * @param i the position
     * @return the elasticity rule
     */
    ElasticityRule getRule(int i);

    /**
     * retrieves the array of elasticity rules
     *
     * @return the elasticity rule array
     */
    ElasticityRule[] getRuleArray();

    /**
     * adds a new elasticity rule by providing a referenced component id and a name for the rule
     *
     * @param componentId the id of the referenced component
     * @param name        the name of the rule. This must be unique.
     * @return the new elasticity rule
     */
    ElasticityRule addNewRule(String componentId, String name);

    /**
     * removes an elasticity rule at the ith position
     *
     * @param i position of the rule
     */
    void removeRule(int i);

    /**
     * sets the elasticity section to be SP Managed
     * this will remove all elasticity variables and rules
     */
    void setSPManagedElasticity();

    /**
     * checks if the elasicity section is managed by the SP
     *
     * @return true | false
     */
    boolean isSetSPManagedElasticity();

    /**
     * retrieve all variables
     *
     * @return the variable array
     */
    ElasticityVariable[] getVariableArray();

    /**
     * retrieves a variable by its position
     *
     * @param i index
     * @return the variable at the ith position in the variable array
     */
    ElasticityVariable getVariable(int i);

    /**
     * retrieves a variable by its name
     *
     * @param name the name of the variable
     * @return the ElasticityVariable element
     */
    ElasticityVariable getVariable(String name);

    /**
     * removes a variable at the ith position
     *
     * @param i the position
     */
    void removeVariable(int i);

    /**
     * Removes a variable by its name.
     *
     * @param name the name of the variable to remove
     */
    void removeVariable(String name);

    /**
     * adds a new internal variable
     *
     * @param name
     * @param metric   the type of the variable, e.g. int or string
     * @param location an xpath expression that points to a variable inside the manifest document
     * @return the created variable
     */
    ElasticityVariable addNewInternalVariable(String name, String metric, String location);

    /**
     * adds a new external variable
     *
     * @param name     the name of the variable
     * @param metric   the type of the variable, e.g. int or string
     * @param location an url which should provide the current value for the monitored variable
     * @return the new external variable
     */
    ElasticityVariable addNewExternalVariable(String name, String metric, String location);
}
