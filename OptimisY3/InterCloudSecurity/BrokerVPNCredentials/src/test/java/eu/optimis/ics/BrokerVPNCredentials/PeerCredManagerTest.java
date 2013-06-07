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

import java.security.KeyPair;

import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.pkcs.PKCS10CertificationRequest;
import org.junit.BeforeClass;
import org.junit.Test;

public class PeerCredManagerTest 
{
	static String peerCredLocation = "/etc/racoon/certs/";
	static PeerCredManager pcm = null;
	static KeyPair peerKP = null;
	static PKCS10CertificationRequest certReq = null;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception 
	{
		java.security.Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
		pcm = new PeerCredManager("localpeer", "127.0.1.1");
		peerKP = pcm.genPeerKeyPair();
		//pcm.savePeerPrivateKey(peerCredLocation+pcm.peerName+".key", peerKP.getPrivate());
		certReq = pcm.genCertificationRequest(peerKP);
	}

	@Test
	public void testGenPeerKeyPair() 
	{
		assertNotNull("Peer Key Pair is NULL", peerKP);
	}

	@Test
	public void testGenCertificationRequest() 
	{
		assertNotNull("Certificate request is NULL", certReq);
		X500Name name = new X500Name("CN=" + pcm.peerName + ", OU=ARSES, O=ARSES, L=Madrid, C=ES");
		assertEquals("CSR Subject is not same", name, certReq.getSubject());
	}
}
