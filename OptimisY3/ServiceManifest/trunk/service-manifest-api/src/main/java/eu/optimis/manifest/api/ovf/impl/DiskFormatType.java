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
package eu.optimis.manifest.api.ovf.impl;

/**
 * @author arumpl
 */
public enum DiskFormatType
{
    VMDK( "http://www.vmware.com/interfaces/specifications/vmdk.html#streamOptimized" ), VHD(
        "http://technet.microsoft.com/en-us/library/bb676673.aspx" ), // Xen and Microsoft Hyper-V
    QCOW2( "http://www.gnome.org/~markmc/qcow-image-format.html" );

    private String specificationUrl;

    DiskFormatType( String formatSpecificationUrl )
    {
        this.specificationUrl = formatSpecificationUrl;
    }

    public String getSpecificationUrl()
    {
        return specificationUrl;
    }

    public static DiskFormatType findBySpecificationURI( String specificationUri )
    {
        if ( specificationUri != null )
        {
            for ( DiskFormatType df : DiskFormatType.values() )
            {
                if ( df.getSpecificationUrl().equals( specificationUri ) )
                {
                    return df;
                }
            }
        }
        throw new IllegalArgumentException( "There is no disk getSpecificationUrl with getSpecificationUrl '"
            + specificationUri + "' specified." );
    }
}
