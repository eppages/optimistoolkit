/* 
 * Copyright (c) 2011, Fraunhofer-Gesellschaft
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
package eu.optimis.sla.notification.impl;

import java.net.URI;
import java.util.List;
import java.util.Vector;

import org.apache.cxf.jaxrs.client.JAXRSClientFactory;

import eu.optimis.sla.notification.providers.ServiceExceptionMapper;
import eu.optimis.sla.notification.providers.URIListProvider;
import eu.optimis.sla.notification.providers.URIProvider;

/**
 * @author owaeld
 * 
 *         The {@link ClientFactory} allows to instantiate JAX-RS annotated clients with the required data
 *         binding providers for XMLBeans, the <code>text/uri</code> and <code>text/uri-list</code> content
 *         types and appropriate exception mappers.
 */
public class ClientFactory
{

    /**
     * Creates a new client proxy for the given interface.
     * 
     * @param <T>
     *            the type of the client proxy
     * @param baseAddress
     *            remote address
     * @param cls
     *            the type of the client proxy
     * @return the client proxy
     */
    public static <T> T create( String baseAddress, Class<T> cls )
    {
        List<Object> providers = new Vector<Object>();
        providers.add( new URIProvider() );
        providers.add( new URIListProvider() );
        providers.add( new ServiceExceptionMapper() );
        return JAXRSClientFactory.create( baseAddress, cls, providers );
    }

    /**
     * Creates a new client proxy for the given interface.
     * 
     * @param <T>
     *            the type of the client proxy
     * @param baseAddress
     *            remote address
     * @param cls
     *            the type of the client proxy
     * @return the client proxy
     */
    public static <T> T create( URI baseAddress, Class<T> cls )
    {
        return create( baseAddress.toASCIIString(), cls );
    }

}
