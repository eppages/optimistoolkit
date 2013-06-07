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

import java.io.IOException;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.Security;
import java.util.Arrays;

import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.jce.PKCS10CertificationRequest;
/**
 * @author Ali Sajjad
 *
 */
public class Main {
	
	public X509CertificateHolder cacertHolder;
	public String credPath = "./credentials/";

	public Main()
	{
		Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
	}
	
	public static void main(String[] args) throws Exception {
		
		Main main = new Main();	
		
		if(args.length==1) 	// only credPath is given, so generate CA only
		{	  
			main.credPath = args[0];
			// create a new CA certificate
			System.out.println(main.genCACred());
			System.out.println("CA Credentials Created");
		}
		
		else if (args.length > 1 && args[1].matches("servers"))
		{
			String[] argServers = Arrays.copyOfRange(args, 2, args.length);
			main.genServerCred(argServers);
			System.out.println("Server Credentials Created");
		}

		else if (args.length > 1 && args[1].matches("clients"))
		{
			String[] argClients = Arrays.copyOfRange(args, 2, args.length);
			main.genClientsCred(argClients);
			System.out.println("Client Credentials Created");
		}
		else System.out.println("NOOP");
	}
	
	public String genCACred()
	{
		CACredentials cac = new CACredentials();
        // Generate a key pair for the CA and save its private key on file
		KeyPair CAKeyPair = cac.genKeyPair();	
		cac.savePrivateKey(credPath+"ca.key", CAKeyPair.getPrivate());
        // Generate a certificate for the CA and save it on file
		cacertHolder = cac.genCACertificate(CAKeyPair);
        try 
        {
			cac.saveCertificate(credPath+"ca.crt", cacertHolder.getEncoded());
		} 
        catch (IOException e) {
			e.printStackTrace();
		}
        return "SUCCESS";
	}
	
	public String genServerCred(String[] argServers) throws Exception {
		
		ServerCredentials sc = new ServerCredentials();
		for (int i = 0; i < argServers.length; i++) 
		{
			// Generate a key pair for the VPN Server and save its private key on file
			KeyPair serverKeyPair = sc.genKeyPair();
			PrivateKey pri = serverKeyPair.getPrivate();
			sc.savePrivateKey(credPath+argServers[i]+".key", pri);
			// Generate a Certification Request for the VPN Server and save it on file
			PKCS10CertificationRequest serverCSR = sc.genCertificationRequest(serverKeyPair, argServers[i]);
			sc.saveCSR(credPath+argServers[i]+".csr", serverCSR.getDEREncoded());
			// Generate a Certificate for the VPN Server and save it on file
			X509CertificateHolder serverCertHolder = CertificateGenerator.genServerCertificate(serverCSR, credPath);
	        sc.saveCertificate(credPath+argServers[i]+".crt", serverCertHolder.getEncoded());
		}
        return "SUCCESS";
	}
	
	public String genClientsCred(String[] argClients) throws Exception {
		
		ClientCredentials cc = new ClientCredentials();
		for (int i = 0; i < argClients.length; i++) 
		{
			KeyPair clientKeyPair = cc.genKeyPair();
			PrivateKey clientPri = clientKeyPair.getPrivate();
			cc.savePrivateKey(credPath+argClients[i]+".key", clientPri);
			PKCS10CertificationRequest clientCSR = cc.genCertificationRequest(clientKeyPair, argClients[i]);
			cc.saveCSR(credPath+argClients[i]+".csr", clientCSR.getDEREncoded());
			// Generate a Certificate for the VPN Client and save it on file
			X509CertificateHolder clientCertHolder = CertificateGenerator.genClientCertificate(clientCSR, credPath);
			cc.saveCertificate(credPath+argClients[i]+".crt", clientCertHolder.getEncoded());
		}
		return "SUCCESS";
	}
}
