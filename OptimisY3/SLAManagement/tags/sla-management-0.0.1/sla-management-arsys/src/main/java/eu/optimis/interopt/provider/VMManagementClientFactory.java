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

import org.springframework.web.context.support.ServletContextAttributeExporter;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

/**
 * The {@link VMManagementClientFactory} provides a mechanism in order to instantiate a new
 * {@link VMManagementSystemClient}. Implementations of the {@link VMManagementSystemClient} interface must
 * provide a default constructor that accepts a {@link URL} as constructor parameter. The URL specifies the
 * location of the VM Management system. A particular client implementation is configured in the
 * <code>arsys.client.properties</code> properties file.
 * 
 * @author owaeld
 */
public class VMManagementClientFactory
{
    private static VMManagementSystemClient defaultClient;

    /**
     * This method creates a new instance of a {@link VMManagementSystemClient}. The client implementation is
     * specified in then <code>arsys.client.properties</code> properties file.
     * 
     * @param systemUrl
     *            The URL of the VM management system
     * 
     * @return a new instance of a {@link VMManagementSystemClient}
     */
    public static VMManagementSystemClient createClient( URL systemUrl )
    {
        if ( defaultClient != null )
        {
            return defaultClient;
        }

        try
        {
            InputStream in = VMManagementSystemClient.class.getResourceAsStream( "/arsys.client.properties" );
            Properties properties = new Properties();
            properties.load( in );

            String clazz = properties.getProperty( VMManagementSystemClient.class.getName() );
            System.out.println("loading default client implementation: " + clazz);
            @SuppressWarnings( "rawtypes" )
            Class clientClass = Class.forName( clazz );
            return (VMManagementSystemClient) clientClass.newInstance();
        }
        catch ( IOException e )
        {
            throw new RuntimeException( "failed to load configuration file", e );
        }
        catch ( Exception e )
        {
            throw new RuntimeException( "failed to instantiate client class", e );
        }
    }

    /**
     * Gets the default client for backend communication.
     * 
     * @return the default client
     */
    public static VMManagementSystemClient getDefaultClient()
    {
        return defaultClient;
    }

    /**
     * Sets the default client for backend communication.
     * 
     * @param defaultClient
     *            the default client
     */
    public static void setDefaultClient( VMManagementSystemClient defaultClient )
    {
        VMManagementClientFactory.defaultClient = defaultClient;
    }

}
