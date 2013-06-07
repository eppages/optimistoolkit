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
 * Tests for license token management in API.
 * 
 * @author owaeld
 */
public class SPLicenseTokenManagment extends AbstractTestApi
{

    //
    // SP Manifest initialization
    //
    private Manifest manifest = Manifest.Factory.newInstance( "OptimisDemoService", "jboss" );

    private VirtualMachineComponentConfiguration vmComponentConfig =
        manifest.getServiceProviderExtensionSection().getVirtualMachineComponentConfiguration( "jboss" );

    // CHECKSTYLE:OFF - definition of test tokens
    private byte[] testToken1 = { 0x01, 0x17, 0x05, 0x13, 0x0F, 0x0D, 0x05, 0x01, 0x03, 0x14, 0x0F, 0x0B,
        0x05, 0x0E, 0x12, 0x12, 0x12, 0x12 };

    private byte[] testToken2 = { 0x41, 0x57, 0x45, 0x53, 0x4F, 0x4D, 0x45, 0x20, 0x54, 0x4F, 0x4B, 0x45,
        0x4E, 0x20, 0x6f, 0x2f, 0x10, 0x0b };
    
    private byte[] testCert1 = { 0x31, 0x17, 0x05, 0x13, 0x0F, 0x0D, 0x35, 0x01, 0x03, 0x14, 0x0F, 0x0B,
            0x05, 0x0E, 0x12, 0x12, 0x12, 0x12 };

        private byte[] testCert2 = { 0x21, 0x27, 0x45, 0x33, 0x4F, 0x4D, 0x45, 0x20, 0x54, 0x4F, 0x4B, 0x45,
            0x4E, 0x20, 0x6f, 0x2f, 0x10, 0x0b };

    // CHECKSTYLE:ON

    public void testAddToken()
    {

        vmComponentConfig.addToken( testToken1 );
        byte[] retrievedToken = vmComponentConfig.getToken( 0 );
        assertNotNull( "retrieved token is null.", retrievedToken );
        assertTrue( "retrieved token differs from original.",
            java.util.Arrays.equals( testToken1, retrievedToken ) );
    }

    public void testAddTokens()
    {

        vmComponentConfig.addToken( testToken1 );
        vmComponentConfig.addToken( testToken2 );
        byte[][] retrievedTokens = vmComponentConfig.getTokenArray();
        assertEquals( "token array should have 2 elements.", 2, retrievedTokens.length );
    }

    public void testRemoveToken()
    {

        vmComponentConfig.addToken( testToken1 );
        vmComponentConfig.removeToken( 0 );
        assertEquals( "retrieved token array should be empty", 0, vmComponentConfig.getTokenArray().length );
    }

    public void testAddNullToken()
    {

        byte[] nullToken = null;
        vmComponentConfig.addToken( nullToken );
        byte[] retrievedToken = vmComponentConfig.getToken( 0 );
        assertEquals( "retrieved token isn't empty.", 0, retrievedToken.length );
    }
    
    public void testAddISVCertificate()
    {

        vmComponentConfig.addISVCertificate( testCert1 );
        byte[] retrievedCertificate = vmComponentConfig.getISVCertificate( 0 );
        assertNotNull( "retrieved cert is null.", retrievedCertificate );
        assertTrue( "retrieved cert differs from original.",
            java.util.Arrays.equals( testCert1, retrievedCertificate ) );
    }

    public void testAddISVCertificates()
    {

        vmComponentConfig.addISVCertificate( testCert1 );
        vmComponentConfig.addISVCertificate( testCert2 );
        byte[][] retrievedCertificates = vmComponentConfig.getISVCertificateArray();
        assertEquals( "token array should have 2 elements.", 2, retrievedCertificates.length );
    }

    public void testRemoveISVCertificate()
    {

        vmComponentConfig.addISVCertificate( testCert1 );
        vmComponentConfig.removeISVCertificate( 0 );
        assertEquals( "retrieved cert array should be empty", 0, vmComponentConfig.getISVCertificateArray().length );
    }

    public void testAddNullISVCertificates()
    {

        byte[] nullCert = null;
        vmComponentConfig.addISVCertificate( nullCert );
        byte[] retrievedCert = vmComponentConfig.getISVCertificate( 0 );
        assertEquals( "retrieved cert isn't empty.", 0, retrievedCert.length );
    }

    /*
     * Test shows how to read tokens from streams and add them to manifest
     */
    // public void testAddValidToken() throws XmlException, IOException {
    // InputStream in = SPLicenseTokenManagment.class.getResourceAsStream("/sample-token-normal.xml");
    // LicenseTokenDocument token = null;
    // token = LicenseTokenDocument.Factory.parse(in);
    // ByteArrayOutputStream bos = new ByteArrayOutputStream();
    // token.save(bos);
    // byte[] tokenSerialized = bos.toByteArray();
    // this.vmComponentConfig.addToken(tokenSerialized);
    // byte[] tokenFromManifest = this.vmComponentConfig.getToken(0);
    // ByteArrayInputStream bis = new ByteArrayInputStream(tokenFromManifest);
    // token = LicenseTokenDocument.Factory.parse(bis);
    // assertTrue("token is invalid (xml validation).",token.validate());
    //
    // }
}
