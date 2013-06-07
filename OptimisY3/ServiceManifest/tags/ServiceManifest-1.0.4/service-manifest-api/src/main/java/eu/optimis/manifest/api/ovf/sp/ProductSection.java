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

package eu.optimis.manifest.api.ovf.sp;

/**
 * @author arumpl
 */
public interface ProductSection {
    /**
     * Sets the product name
     *
     * @param product
     */
    void setProduct(String product);

    /**
     * Retrieves the product name
     *
     * @return the product name
     */
    String getProduct();

    /**
     * @see eu.optimis.manifest.api.ovf.impl.ProductSectionImpl#getVersion()
     */
    String getVersion();

    /**
     * @see eu.optimis.manifest.api.ovf.impl.ProductSectionImpl#setVersion(String)
     */
    void setVersion(String version);


    /**
     * @see eu.optimis.manifest.api.ovf.impl.ProductSectionImpl#getInfo()
     */
    String getInfo();

    /**
     * set info
     *
     * @param info
     */
    void setInfo(String info);

    /**
     * looks for a property in the property array with the provided key, if there
     * are more than one with this key, the one first found will be returned.
     *
     * @param key
     * @return product property
     */
    public ProductProperty getPropertyByKey(String key);


    /**
     * @see eu.optimis.manifest.api.ovf.impl.ProductSectionImpl#getPropertyArray()
     */
    public ProductProperty[] getPropertyArray();

    /**
     * @see eu.optimis.manifest.api.ovf.impl.ProductSectionImpl#getPropertyArray(int)
     */
    public ProductProperty getPropertyArray(int i);

}
