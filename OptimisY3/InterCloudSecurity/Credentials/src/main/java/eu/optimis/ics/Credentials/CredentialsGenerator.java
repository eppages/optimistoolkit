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
import java.security.PrivateKey;

import org.bouncycastle.jce.PKCS10CertificationRequest;

/**
 * @author Ali Sajjad
 *
 */
public interface CredentialsGenerator 
{
	public KeyPair genKeyPair();
	
	public void savePrivateKey(String filePath, PrivateKey privateKey);

	public PKCS10CertificationRequest genCertificationRequest(KeyPair keyPair, String CN);
	
	public void saveCSR(String path, byte[] DEREncodedCSR);
	
	public void saveCertificate(String filePath, byte[] DEREncodedCertificate);
	
}
