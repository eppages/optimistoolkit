/*
 * Copyright (c) 2010-2013 British Telecom and City University London
 *
 * This file is part of BrokerVPNCredentials component of the WP 5.4
 * (Inter-Cloud Security) of the EU OPTIMIS project.
 *
 * BrokerVPNCredentials can be used under the terms of the SHARED SOURCE LICENSE
 * FOR NONCOMMERCIAL USE. 
 *
 * You should have received a copy of the SHARED SOURCE LICENSE FOR
 * NONCOMMERCIAL USE in the project's root directory. If not, please contact the
 * author at ali.sajjad@bt.com
 *
 * Author: Ali Sajjad
 *
 */
package eu.optimis.ics.BrokerVPNCredentials;

import static org.junit.Assert.*;

import java.io.File;
import java.security.KeyPair;

import org.bouncycastle.cert.CertException;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.operator.ContentVerifierProvider;
import org.bouncycastle.operator.jcajce.JcaContentVerifierProviderBuilder;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class CACredentialsTest 
{
	static CACredentials tester = null;
	static KeyPair kp = null;
	static X509CertificateHolder cacertHolder = null;
	static ContentVerifierProvider contentVerifierProvider = null;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception 
	{
		java.security.Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
		
		tester = new CACredentials();
		
		kp = tester.genCAKeyPair();
		tester.saveCAPrivateKey("ca_test.key", kp.getPrivate());
		cacertHolder = tester.genCACertificate(kp);
		tester.saveCACertificate("ca_test.crt", cacertHolder.getEncoded());
		
		contentVerifierProvider = new JcaContentVerifierProviderBuilder().setProvider("BC").build(kp.getPublic());
	}

	@Test
	public void testgenCAKeyPair() {

		assertNotNull("CA Key Pair is NULL", kp);
	}
		
	@Test
	public void testgenCACertificate() throws CertException {

		assertNotNull("CA Certificate is NULL", cacertHolder);
		
		assertEquals("Version of X.509", 3, cacertHolder.getVersionNumber());
		
		// In case of CA certificate, both issuer and subject should be identical
		assertEquals(cacertHolder.getIssuer(), cacertHolder.getSubject());
				
		assertTrue("Certificate is NOT VERIFIED", cacertHolder.isSignatureValid(contentVerifierProvider));
	}
	
	@Test
	public void testsaveCAPrivateKey() {

		File file = new File("ca_test.key");
		assertTrue("File does NOT EXIST", file.isFile());
	}
	
	@Test
	public void testsaveCAcertificate() {

		File file = new File("ca_test.crt");
		assertTrue("File does NOT EXIST", file.isFile());
	}
	@AfterClass
	public static void tearDownAfterClass() throws Exception 
	{
		File file = new File("ca_test.key");
		file.delete();
		file = new File("ca_test.crt");
		file.delete();
	}
}
