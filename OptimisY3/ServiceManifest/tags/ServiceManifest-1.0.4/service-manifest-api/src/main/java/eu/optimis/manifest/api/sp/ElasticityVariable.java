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

/**
 * @author arumpl
 */
public interface ElasticityVariable {
    /**
     * retrieves the location where the variable can be found, either an xpath expression if the variable
     * type is internal or an URL if the type is external.
     *
     * @return the location
     */
    String getLocation();

    /**
     * sets the type of the variable
     *
     * @param metric e.g. "int" or "string"
     */
    void setMetric(String metric);

    /**
     * retrieves the type of the variable
     *
     * @return one of [internal | external]
     */
    String getType();

    /**
     * sets the type of a variable
     *
     * @param type one of [internal | external]
     */
    void setType(String type);

    /**
     * sets the location of the variable value.
     *
     * @param location the path where the variable value can be found
     */
    void setLocation(String location);

    /**
     * retrieves the name of the variable
     *
     * @return
     */
    String getName();

    /**
     * sets the name of the variable. It has to be unique.
     *
     * @param name
     */
    void setName(String name);

    /**
     * retrieves the metric of the variable.
     *
     * @return the metric as string. e.g. "int" or "string"
     */
    String getMetric();
}
