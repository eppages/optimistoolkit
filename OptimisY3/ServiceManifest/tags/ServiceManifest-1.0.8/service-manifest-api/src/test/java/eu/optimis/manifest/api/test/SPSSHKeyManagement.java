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

import eu.optimis.manifest.api.sp.Manifest;
import eu.optimis.manifest.api.sp.VirtualMachineComponentConfiguration;

/**
 * SSH Key Management component test of SP API.
 * 
 * @author owaeld
 */
public class SPSSHKeyManagement extends AbstractTestApi
{
    private Manifest manifest = Manifest.Factory.newInstance( "OptimisDemoService", "jboss" );

    private VirtualMachineComponentConfiguration vmComponentConfig =
        manifest.getServiceProviderExtensionSection().getVirtualMachineComponentConfiguration( "jboss" );

    // CHECKSTYLE:OFF - definition of test tokens
    private byte[] testSshKey1 = { 0x01, 0x17, 0x05, 0x13, 0x0F, 0x0D, 0x05, 0x01, 0x03, 0x14, 0x0F, 0x0B,
        0x05, 0x0E, 0x12, 0x12, 0x12, 0x12 };

    // CHECKSTYLE:ON

    public void testSetSshKey()
    {
        vmComponentConfig.setSSHKey( testSshKey1 );
        byte[] retrievedKey = vmComponentConfig.getSSHKey();
        assertNotNull( "retrieved ssh key is null.", retrievedKey );
        assertTrue( "retrieved ssh key differs from original.",
            java.util.Arrays.equals( testSshKey1, retrievedKey ) );
    }

    public void testRemoveSshKey()
    {
        vmComponentConfig.setSSHKey( testSshKey1 );
        vmComponentConfig.removeSSHKey();
        assertEquals( "retrieved ssh key should be empty", 0, vmComponentConfig.getSSHKey().length );
    }

    public void testAddNullSshKey()
    {

        byte[] nullKey = null;
        vmComponentConfig.setSSHKey( nullKey );
        byte[] retrievedKey = vmComponentConfig.getSSHKey();
        assertEquals( "retrieved key isn't empty.", 0, retrievedKey.length );
    }
}
