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
package eu.optimis.manifest.api.test;

import eu.optimis.manifest.api.sp.Dependency;
import eu.optimis.manifest.api.sp.ServiceProviderExtension;
import eu.optimis.manifest.api.sp.VirtualMachineComponentConfiguration;
import org.apache.xmlbeans.XmlException;

import java.util.Arrays;

/**
 * @author arumpl
 */
public class SPExtensionsTest extends AbstractTestApi
{

    public void testShouldModifySPExtensionSection() throws XmlException
    {
        // given that the sp extension was set
        ServiceProviderExtension ext = getManifest().getServiceProviderExtensionSection();
        Dependency dependency =
                ext.getVirtualMachineComponentConfiguration( "jboss" ).addNewDependency();
        dependency.setArtifactId( "artifact" );
        dependency.setGroupId( "group" );
        dependency.setVersion( "1.0" );

        // when we change something

        ServiceProviderExtension newExt = getManifest().getServiceProviderExtensionSection();
        Dependency newDep =
                newExt.getVirtualMachineComponentConfiguration( "jboss" ).addNewDependency();
        newDep.setArtifactId( "test" );
        newDep.setVersion( "test" );
        newDep.setGroupId( "test" );
        // then the changes are reflected in the manifest

        assertEquals( "test", getManifest().getServiceProviderExtensionSection()
                .getVirtualMachineComponentConfiguration( "jboss" )
                .getSoftwareDependencies( 1 ).getVersion() );
    }

    public void testComponentConfiguration()
    {
        VirtualMachineComponentConfiguration jbossConfiguration =
                getManifest().getServiceProviderExtensionSection()
                        .getVirtualMachineComponentConfiguration(
                                "jboss" );
        jbossConfiguration.addNewComponentProperty( "other", "some value" );
        assertEquals( "other", jbossConfiguration.getComponentProperty( "other" ).getName() );
        jbossConfiguration.getComponentProperty( "other" ).setName( "some" );
        assertEquals( "some", jbossConfiguration.getComponentProperty( "some" ).getName() );
    }

    public void testEncryptedSpace()
    {
        VirtualMachineComponentConfiguration jbossConfiguration =
                getManifest().getServiceProviderExtensionSection()
                        .getVirtualMachineComponentConfiguration(
                                "jboss" );
        assertFalse( jbossConfiguration.isEncryptedSpaceEnabled() );
        // CHECKSTYLE:OFF - test values
        byte[] encryptionkey = { -86, -86, 39, 30 };
        // CHECKSTYLE:ON
        jbossConfiguration.enableEncryptedSpace( encryptionkey );
        assertTrue( jbossConfiguration.isEncryptedSpaceEnabled() );
        assertTrue( Arrays.equals( encryptionkey,
                jbossConfiguration.getEncryptedSpace().getEncryptionKey() ) );
        jbossConfiguration.disableEncryptedSpace();
        assertFalse( jbossConfiguration.isEncryptedSpaceEnabled() );
        //we can also enable the encrypted space without setting the key
        jbossConfiguration.enableEncryptedSpace();
        assertTrue( jbossConfiguration.isEncryptedSpaceEnabled() );

        // then we can add the key afterwards

        jbossConfiguration.getEncryptedSpace().setEncryptionKey( encryptionkey );

        assertTrue( Arrays.equals( encryptionkey,
                jbossConfiguration.getEncryptedSpace().getEncryptionKey() ) );
    }

    public void testShouldAddAndRemoveDataManagerKey()
    {
        //we check that the key was not set before:
        assertFalse( getManifest().getServiceProviderExtensionSection().isSetDataManagerKey() );

        //when we add a key
        byte[] datamManagerKey = { -86, -86, 39, 30 };
        getManifest().getServiceProviderExtensionSection().setDataManagerKey( datamManagerKey );
        //then the key equals our input
        assertTrue( Arrays.equals( datamManagerKey,
                getManifest().getServiceProviderExtensionSection().getDataManagerKey() ) );

        //when we remove the key again

        getManifest().getServiceProviderExtensionSection().unsetDataManagerKey();

        //the key is not set
        assertFalse( getManifest().getServiceProviderExtensionSection().isSetDataManagerKey() );
    }

    public void testSLAId()
    {
        assert getManifest().getServiceProviderExtensionSection().getSLAID() == null;
        getManifest().getServiceProviderExtensionSection().setSLAID( "test" );
        assert getManifest().getServiceProviderExtensionSection().getSLAID() == "test";
        getManifest().getServiceProviderExtensionSection().unsetSLAID();
        assert getManifest().getServiceProviderExtensionSection().getSLAID() == null;
        assert !getManifest().getServiceProviderExtensionSection().isSetSLAID();
    }
    
    public void testBlackListedIPs()
    {
        assert getManifest().getServiceProviderExtensionSection().getBlackListIPs() == null;
        getManifest().getServiceProviderExtensionSection().setBlackListIPs( "test" );
        assert getManifest().getServiceProviderExtensionSection().getBlackListIPs() == "test";
        getManifest().getServiceProviderExtensionSection().unsetBlackListIPs();
        assert getManifest().getServiceProviderExtensionSection().getBlackListIPs() == null;
        assert !getManifest().getServiceProviderExtensionSection().isSetBlackListIPs();
    }
}
