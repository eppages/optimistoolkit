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
package eu.optimis.manifest.api.ovf.ip;

/**
 * @author arumpl
 */
public interface ProductSection
{
    /**
     * @see eu.optimis.manifest.api.ovf.impl.ProductSectionImpl#getProduct()
     */
    String getProduct();

    /**
     * @see eu.optimis.manifest.api.ovf.sp.ProductSection#getVersion()
     */
    String getVersion();

    /**
     * @see eu.optimis.manifest.api.ovf.sp.ProductSection#getInfo()
     */
    String getInfo();

    /**
     * @see eu.optimis.manifest.api.ovf.sp.ProductSection#getPropertyArray()
     */
    ProductProperty[] getPropertyArray();

    /**
     * @see eu.optimis.manifest.api.ovf.sp.ProductSection#getPropertyArray(int)
     */
    ProductProperty getPropertyArray( int i );

    /**
     * @see eu.optimis.manifest.api.ovf.sp.ProductSection#getPropertyByKey(String)
     * @param key
     * @return
     */
    ProductProperty getPropertyByKey( String key );

    ProductProperty addNewProperty( String key, String type, String value );
}
