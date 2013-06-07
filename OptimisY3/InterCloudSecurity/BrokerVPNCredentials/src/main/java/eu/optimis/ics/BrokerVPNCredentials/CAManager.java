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

import java.io.IOException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.Security;

import org.bouncycastle.cert.X509CertificateHolder;

public class CAManager {
	
	public X509CertificateHolder caCertHolder;
	public String caPath = "/home/ali/workspace/BrokerVPNCredentials/";
	
	public CAManager()
	{
		Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
	}
	
	public static void main(String[] args) throws IOException
	{
		CAManager cam = new CAManager();
		
		System.out.println(cam.genCACredentials());
	
	}
	
	public String genCACredentials()
	{
		CACredentials cac = new CACredentials();
        // Generate a key pair for the CA and save its private key on file
		KeyPair CAKeyPair = cac.genCAKeyPair();	
		cac.saveCAPrivateKey(caPath+"ca.key", CAKeyPair.getPrivate());
        // Generate a certificate for the CA and save it on file
		try 
		{
			caCertHolder = cac.genCACertificate(CAKeyPair);
	        cac.saveCACertificate(caPath+"ca.crt", caCertHolder.getEncoded());
		}
		catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} 
		catch (IOException e) {
			e.printStackTrace();
		}
		
		System.out.println("CA Keys and Certificate Generated and Saved at : "+this.caPath);
		
		return "SUCCESS";
	}
}
