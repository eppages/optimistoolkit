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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.KeyPair;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import org.bouncycastle.jce.PKCS10CertificationRequest;
import org.bouncycastle.cert.X509CertificateHolder;
/**
 * @author Ali Sajjad
 *
 */
public class ServerCredentials extends Credentials {

	public static void main(String[] args) throws Exception {
	
		ServerCredentials sc = new ServerCredentials();
		
		KeyPair ServerKP = sc.genKeyPair();
		
		PKCS10CertificationRequest serverCSR = sc.genCertificationRequest(ServerKP, "VPNServer");
		
		sc.savePrivateKey("./credentials/VPNServer.key", ServerKP.getPrivate());

		X509CertificateHolder certHolder = CertificateGenerator.genServerCertificate(serverCSR, "./credentials/");
		
		sc.saveCertificate("./credentials/server.crt", certHolder.getEncoded());

		System.out.println("Done");
	}
	
	public KeyPair LoadKeyPair(String path, String algorithm) throws IOException, NoSuchAlgorithmException,	InvalidKeySpecException {
		// Read Public Key.
		File filePublicKey = new File(path + "\\public.key");
		FileInputStream fis = new FileInputStream(path + "\\public.key");
		byte[] encodedPublicKey = new byte[(int) filePublicKey.length()];
		fis.read(encodedPublicKey);
		fis.close();
		 
		// Read Private Key.
		File filePrivateKey = new File(path + "\\private.key");
		fis = new FileInputStream(path + "\\private.key");
		byte[] encodedPrivateKey = new byte[(int) filePrivateKey.length()];
		fis.read(encodedPrivateKey);
		fis.close();
		 
		// Generate KeyPair.
		KeyFactory keyFactory = KeyFactory.getInstance(algorithm);
		X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(encodedPublicKey);
		PublicKey publicKey = keyFactory.generatePublic(publicKeySpec);
		 
		PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(encodedPrivateKey);
		PrivateKey privateKey = keyFactory.generatePrivate(privateKeySpec);
		 
		return new KeyPair(publicKey, privateKey);
	}
}
