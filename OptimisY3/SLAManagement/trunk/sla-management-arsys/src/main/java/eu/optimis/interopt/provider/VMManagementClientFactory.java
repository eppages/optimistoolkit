/**

Copyright 2013 ATOS SPAIN S.A. 

Licensed under the Apache License, Version 2.0 (the License);
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

Authors :
Oriol Collell, Francisco Javier Nieto. Atos Research and Innovation, Atos SPAIN SA
@email francisco.nieto@atosresearch.eu 

**/

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
