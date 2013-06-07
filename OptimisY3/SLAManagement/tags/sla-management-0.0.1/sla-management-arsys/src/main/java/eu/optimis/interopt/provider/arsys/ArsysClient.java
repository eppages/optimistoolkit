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
package eu.optimis.interopt.provider.arsys;

import es.arsys.ServiceManager;
import es.arsys.ServiceManagerSoap;
import eu.optimis.interopt.provider.ServiceComponent;
import eu.optimis.interopt.provider.ServiceInstantiationException;
import eu.optimis.interopt.provider.VMManagementSystemClient;
import eu.optimis.interopt.provider.VMProperties;
import eu.optimis.types.xmlbeans.servicemanifest.XmlBeanServiceManifestDocument;
import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;
import org.w3c.dom.CharacterData;
import org.w3c.dom.*;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.ws.BindingProvider;
import java.io.StringReader;
import java.net.UnknownServiceException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author hrasheed/carlos
 */
public class ArsysClient
        implements VMManagementSystemClient
{
    private static Logger log = Logger.getLogger( ArsysClient.class );

    private static int maxvms = 10;
    private String username;
    private String password;

    public void setAuth( String auth_username, String password )
    {
        this.username = auth_username;
        this.password = password;
    }

    /*
     * (non-Javadoc)
     * 
     * @see eu.optimis.interopt.provider.VMManagementSystemClient#deployService(java.util.List,
     * eu.optimis.types.xmlbeans.servicemanifest.XmlBeanServiceManifestDocument)
     */
    @Override
    public void deployService( String service_id, List<ServiceComponent> serviceComponents,
                               XmlBeanServiceManifestDocument manifest )
            throws ServiceInstantiationException
    {
        
        // Instantiate the ServiceManager
        ServiceManager sm = new ServiceManager();
        ServiceManagerSoap port = sm.getServiceManagerSoap();
        authenticate( ( BindingProvider ) port );

        log.debug( "arsys service manager is instantiated" );
        
        // Get the number of VMs to deploy
        int k = 0;
        int sum = 0;

        while ( k < serviceComponents.size() )
        {
            sum = sum + Integer.parseInt(
                    serviceComponents.get( k ).get( ServiceComponent.OPTIMIS_VM_INSTANCES ) );
            k++;
        }

        // If sum < ArsysClient.maxvms invoke createVM method as many times as needed
        if ( sum > ArsysClient.maxvms )
        {
            throw new ServiceInstantiationException(
                    "Number of VMs to deploy exceeds the maximum", new java.lang.Throwable() );
        }

        int i = 0;

        while ( i < serviceComponents.size() )
        {
            int j = 0;
            int numInstances = Integer.parseInt( serviceComponents.get( i ).get( ServiceComponent.OPTIMIS_VM_INSTANCES ) );
            log.info( "number of vm instances to deply: " +  numInstances);                
            while ( j < numInstances )
            {
                // Invoke the service and get response
                String vmimg = serviceComponents.get( i ).get( ServiceComponent.OPTIMIS_VM_IMAGE );
                String cores =
                        serviceComponents.get( i ).get( ServiceComponent.OCCI_COMPUTE_CORES );
                String memory =
                        serviceComponents.get( i ).get( ServiceComponent.OCCI_COMPUTE_MEMORY );
                String architecture = serviceComponents.get( i )
                        .get( ServiceComponent.OCCI_COMPUTE_ARCHITECTURE );
                log.info("creating vm for service [" + service_id + "] image [" + vmimg + "] cores [" + cores + "] memory [" + memory + "] architecture [" + architecture + "]" );
                String rv = port.createVM( service_id, vmimg, cores, memory, architecture );
                // Convert base64 response to xml string
                String xml = new String( Base64.decodeBase64( rv.getBytes() ) );
                log.info( "response: " + xml);
                //Check if VM has been succesfully created
                if ( xml.contains( "<result>0</result>" ) )
                {
                    log.info("vm has been successfully created for service [" + service_id + "]");
                    j++;
                }
                else
                {
                    log.error( "service deployment has failed" );
                    throw new ServiceInstantiationException( "Service deployment has failed: " + xml,
                            new java.lang.Throwable() );
                }
                
                log.trace(xml);
            }
            i++;
        }
    }

    /*
    * (non-Javadoc)
    *
    * @see eu.optimis.interopt.provider.VMManagementSystemClient#queryServiceProperties(java.lang.String)
    */
    @Override
    public List<VMProperties> queryServiceProperties( String serviceId )
            throws UnknownServiceException
    {

        List<VMProperties> list = null;

        try
        {
            // Instantiate the ServiceManager
            ServiceManager sm = new ServiceManager();
            ServiceManagerSoap port = sm.getServiceManagerSoap();
            authenticate( ( BindingProvider ) port );

            // Invoke the service and get response
            String rv = port.getServiceData( serviceId );
            // Convert base64 response to xml string
            String xml = new String( Base64.decodeBase64( rv.getBytes() ) );

            //Build the XML doc and fill the List
            DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            InputSource is = new InputSource();
            is.setCharacterStream( new StringReader( xml ) );

            Document doc = db.parse( is );
            NodeList nodes = doc.getElementsByTagName( "vmData" );

            //Create response List
            list = new ArrayList<VMProperties>();
            for ( int i = 0; i < nodes.getLength(); i++ )
            {
                VMProperties vm = new VMProperties();
                Element element = ( Element ) nodes.item( i );

                NodeList name = element.getElementsByTagName( "id" );
                Element line = ( Element ) name.item( 0 );
                vm.setId( getCharacterDataFromElement( line ) );

                NodeList status = element.getElementsByTagName( "status" );
                line = ( Element ) status.item( 0 );
                vm.setStatus( getCharacterDataFromElement( line ) );

                NodeList title = element.getElementsByTagName( "hostname" );
                line = ( Element ) title.item( 0 );
                vm.put( VMProperties.OCCI_COMPUTE_HOSTNAME, getCharacterDataFromElement( line ) );

                list.add( vm );
            }
        }
        catch ( Exception e )
        {
            e.printStackTrace();
        }

        return list;
    }

    public static String getCharacterDataFromElement( Element e )
    {
        Node child = e.getFirstChild();
        if ( child instanceof CharacterData )
        {
            CharacterData cd = ( CharacterData ) child;
            return cd.getData();
        }
        return "";
    }

    /*
     * (non-Javadoc)
     * 
     * @see eu.optimis.interopt.provider.VMManagementSystemClient#terminate(java.lang.String)
     */
    @Override
    public void terminate( String serviceId ) throws UnknownServiceException
    {
        // Instantiate the ServiceManager
        ServiceManager sm = new ServiceManager();
        ServiceManagerSoap port = sm.getServiceManagerSoap();
        authenticate( ( BindingProvider ) port );

        // Invoke the service and get response
        String rv = port.terminateService( serviceId );

        // Convert base64 response to xml string
        String xml = new String( Base64.decodeBase64( rv.getBytes() ) );

        if ( xml.contains( "<result>0</result>" ) == false )
        {
            throw new UnknownServiceException( "Service termination has failed" );
        }
        
        log.info("Servce [" + serviceId + "] terminated successfully.");
        log.trace(xml);
    }

    private void authenticate( BindingProvider port )
    {
        port.getRequestContext()
                .put( BindingProvider.USERNAME_PROPERTY, username );
        port.getRequestContext()
                .put( BindingProvider.PASSWORD_PROPERTY, password );
    }
}
