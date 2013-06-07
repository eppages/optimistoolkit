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
package eu.optimis.manifest.api.utils;

import org.apache.xmlbeans.XmlObject;

/**
 * This class can be used to select any arbitrary element in the manifest by using an xpath expression. It
 * expects an XmlObject as input. The only method provided is selectObjects which will return an array of
 * selected objects, if none are found with the xpath, the array is empty.
 *
 * @author arumpl
 */
public final class ManifestElementParser
{
    // CHECKSTYLE:OFF - long lines due to namespace declarations
    protected static final String NAMESPACE_DECLARATION =
            "declare namespace opt='http://schemas.optimis.eu/optimis/'; "
            + "declare namespace opt-ip='http://schemas.optimis.eu/optimis/infrastructure'; "
            + "declare namespace opt-sp='http://schemas.optimis.eu/optimis/service'; "
            +
            "declare namespace rasd='http://schemas.dmtf.org/wbem/wscim/1/cim-schema/2/CIM_ResourceAllocationSettingData'; "
            +
            "declare namespace vssd='http://schemas.dmtf.org/wbem/wscim/1/cim-schema/2/CIM_VirtualSystemSettingData'; "
            + "declare namespace ovf='http://schemas.dmtf.org/ovf/envelope/1'; ";

    // CHECKSTYLE:ON

    public static XmlObject[] selectObjects( XmlObject manifestElement, String xPath )
    {
        return manifestElement.selectPath( NAMESPACE_DECLARATION + xPath );
    }

    private ManifestElementParser()
    {
        //utility class should not have public constructor
    }
}
