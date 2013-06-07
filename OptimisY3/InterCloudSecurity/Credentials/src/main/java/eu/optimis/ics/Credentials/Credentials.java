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

import java.io.FileWriter;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.SecureRandom;
import java.security.Security;
import java.security.SignatureException;

import javax.security.auth.x500.X500Principal;

import org.bouncycastle.asn1.DERSet;
import org.bouncycastle.jce.PKCS10CertificationRequest;
import org.bouncycastle.openssl.PEMWriter;
import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemWriter;

/**
 * @author Ali Sajjad
 *
 */
public class Credentials implements CredentialsGenerator {
	
	public Credentials()
	{
		Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
	}

	public KeyPair genKeyPair() 
	{
		KeyPairGenerator keyPairGenerator = null;

        try {
                keyPairGenerator = KeyPairGenerator.getInstance("RSA", "BC");
                keyPairGenerator.initialize(1024, SecureRandom.getInstance("SHA1PRNG"));
        } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
        } catch (NoSuchProviderException e) {
                e.printStackTrace();
        }
        return keyPairGenerator.generateKeyPair();
	}

	public void savePrivateKey(String filePath, PrivateKey privateKey) 
	{
		PEMWriter pemWrt;

        try {
        	pemWrt = new PEMWriter(new FileWriter(filePath));
            pemWrt.writeObject(privateKey);
            pemWrt.flush();
            pemWrt.close();
        } catch (IOException e) {
        	e.printStackTrace();
        }
	}

	public PKCS10CertificationRequest genCertificationRequest(KeyPair keyPair, String CN) 
	{
		PKCS10CertificationRequest CSRequest = null;
		X500Principal name = new X500Principal("CN="+CN+ ", OU=ATOS, O=ATOS, L=Barcelona, C=ES");
		/*
		// challenge password attribute
		
		ASN1EncodableVector challpwd = new ASN1EncodableVector();
		challpwd.add(new DERObjectIdentifier(PKCSObjectIdentifiers.pkcs_9_at_challengePassword.getId()));
		
		ASN1EncodableVector pwdValue = new ASN1EncodableVector();
		pwdValue.add(new DERUTF8String("pakistan"));
		
		challpwd.add(new DERSet(pwdValue));
		
		ASN1EncodableVector vector = new ASN1EncodableVector();
        vector.add(new DERSequence(challpwd));
       
        DERSet attributes = new DERSet(vector);
        */
		DERSet attributes = null;
	    try 
	    {
	    	CSRequest = new PKCS10CertificationRequest("SHA1withRSA", name, keyPair.getPublic(), attributes, keyPair.getPrivate(), "BC");
		} 
	    catch (InvalidKeyException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (NoSuchProviderException e) {
			e.printStackTrace();
		} catch (SignatureException e) {
			e.printStackTrace();
		}
		return CSRequest;
	}
	
	public void saveCSR(String path, byte[] DEREncodedCSR)
	{
		PemWriter pemWrt;
		
	    try {
	    	pemWrt = new PemWriter(new FileWriter(path));
		    pemWrt.writeObject(new PemObject("CERTIFICATE REQUEST", DEREncodedCSR));
		    pemWrt.flush();
		    pemWrt.close();
	    } catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void saveCertificate(String filePath, byte[] DEREncodedCertificate) 
	{
		PemWriter pemWrt;

        try {
        	pemWrt = new PemWriter(new FileWriter(filePath));
            pemWrt.writeObject(new PemObject("CERTIFICATE", DEREncodedCertificate));
            pemWrt.flush();
            pemWrt.close();
        } catch (IOException e) {
        	e.printStackTrace();
        }
	}

}
