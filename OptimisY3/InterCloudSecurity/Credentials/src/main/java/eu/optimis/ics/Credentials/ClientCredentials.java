/*
 * Copyright (c) 2010-2013 British Telecom and City University London
 *
 * This file is part of Credentials component of the WP 5.4
 * (Inter-Cloud Security) of the EU OPTIMIS project.
 *
 * Credentials can be used under the terms of the SHARED SOURCE LICENSE
 * FOR NONCOMMERCIAL USE. 
 *
 * You should have received a copy of the SHARED SOURCE LICENSE FOR
 * NONCOMMERCIAL USE in the project's root directory. If not, please contact the
 * author at ali.sajjad@bt.com
 *
 * Author: Ali Sajjad
 *
 */
package eu.optimis.ics.Credentials;

import java.security.KeyPair;
import java.security.Security;

import org.bouncycastle.jce.PKCS10CertificationRequest;
import org.bouncycastle.cert.X509CertificateHolder;

/**
 * @author Ali Sajjad
 *
 */
public class ClientCredentials extends Credentials {

	public static void main(String[] args) throws Exception {
		
		Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
		
		ClientCredentials cc = new ClientCredentials();
		
		KeyPair clientKP = cc.genKeyPair();
		
		PKCS10CertificationRequest clientCSR = cc.genCertificationRequest(clientKP, "v1");
		cc.saveCSR("client.csr", clientCSR.getDEREncoded());
		
		cc.savePrivateKey("v1.key", clientKP.getPrivate());

		X509CertificateHolder certHolder = CertificateGenerator.genClientCertificate(clientCSR, null);
		
		cc.saveCertificate("v1.crt", certHolder.getEncoded());

		System.out.println("Done");
	}
}