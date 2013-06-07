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

import java.io.FileWriter;
import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.SecureRandom;
import java.security.cert.CertificateEncodingException;

import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.openssl.PEMWriter;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.bouncycastle.pkcs.PKCS10CertificationRequest;
import org.bouncycastle.pkcs.PKCS10CertificationRequestBuilder;
import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemWriter;

public class PeerCredManager {
	
	public String peerName;
	private final static String peerCredLocation = "/etc/racoon/certs/";
	public String brokerCAHost;
	public String registryName = "RemoteCA";
	
	public PeerCredManager(String pName, String brokerIP) 
	{/*
		if(! new File(peerCredLocation).isDirectory())
		{
			log("Creating the certificate directory");
			new File(peerCredLocation).mkdir();
		}
		*/
		if (System.getSecurityManager() == null) {
            System.setSecurityManager(new SecurityManager());
        }
		
		this.peerName = pName;
		this.brokerCAHost = brokerIP;
		java.security.Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
	}

	public static void main(String[] args) {
		
		PeerCredManager pcm = null;
		if (args.length > 0)
		{
			log("Taking inputs from the commandline:-");
			log("Peer Name = "+args[0]);
			log("Broker IP Address = "+args[1]);
			
			pcm = new PeerCredManager(args[0], args[1]);
		}
		else
		{
			log("Using default values: Assuming RMI server is running locally");
			pcm = new PeerCredManager("peer", "127.0.1.1");
		}
		
		KeyPair peerKP = pcm.genPeerKeyPair();
		pcm.savePeerPrivateKey(peerCredLocation+pcm.peerName+".key", peerKP.getPrivate());
		PKCS10CertificationRequest certReq = pcm.genCertificationRequest(peerKP);
		
		Registry registry;
		
		try 
		{
			registry = LocateRegistry.getRegistry(pcm.brokerCAHost);
		    RemoteCSR stub = (RemoteCSR) registry.lookup(pcm.registryName);
		    
		    // Get the CA certificate from the Broker and save in the racoon directory
		    byte[] caCertficate = stub.getCACertificate();
		    pcm.savePeerCertificate(peerCredLocation+"ca.crt", caCertficate);
		    System.out.println("SUCCESS: CA Certificate stored in -> " + peerCredLocation);
		    
		    // Get the Peer certificate signed from the Broker CA and save in the racoon directory
		    byte[] signedCertBytes = stub.getSignedCertificateBytes(certReq.getEncoded());
		    X509CertificateHolder certHolder = new X509CertificateHolder(signedCertBytes);
		    pcm.savePeerCertificate(peerCredLocation+pcm.peerName+".crt", certHolder.getEncoded());
		    System.out.println("SUCCESS: Peer Credentials generated in -> " + peerCredLocation);
		} 
		catch (RemoteException e) {
			e.printStackTrace();
		} catch (NotBoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (CertificateEncodingException e) {
			e.printStackTrace();
		}
	}
	
	public KeyPair genPeerKeyPair() {
		
		KeyPairGenerator keyPairGenerator = null;
		try 
		{
			keyPairGenerator = KeyPairGenerator.getInstance("RSA", "BC");
			keyPairGenerator.initialize(1024, new SecureRandom());
		} 
		catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} 
		catch (NoSuchProviderException e) {
			e.printStackTrace();
		}
		return keyPairGenerator.generateKeyPair();
	}
	
	public void savePeerPrivateKey(String filePath, PrivateKey privateKey)
	{
		PEMWriter pemWrt;

        try 
        {
        	pemWrt = new PEMWriter(new FileWriter(filePath));
            pemWrt.writeObject(privateKey);
            pemWrt.flush();
            pemWrt.close();
        } 
        catch (IOException e) {
        	e.printStackTrace();
        }
	}
	
	public PKCS10CertificationRequest genCertificationRequest(KeyPair peerKP)  {
		
		X500Name name = new X500Name("CN=" + peerName + ", OU=ARSES, O=ARSES, L=Madrid, C=ES");
		SubjectPublicKeyInfo publicKeyInfo = null;
		PKCS10CertificationRequest certRequest = null;
		ContentSigner sigGen = null;
		
		try 
		{
			publicKeyInfo = new SubjectPublicKeyInfo((ASN1Sequence)ASN1ObjectIdentifier.fromByteArray(peerKP.getPublic().getEncoded()));
		} 
		catch (IOException e1) {
			e1.printStackTrace();
		}
		
		PKCS10CertificationRequestBuilder pb = new PKCS10CertificationRequestBuilder(name, publicKeyInfo);
	    
		try 
	    {
	        sigGen = new JcaContentSignerBuilder("SHA1withRSA").setProvider("BC").build(peerKP.getPrivate());
	        
		} 
	    catch (OperatorCreationException e) {
			e.printStackTrace();
		}
		
	    certRequest = pb.build(sigGen);
	    return certRequest;
	  }
	
	public void savePeerCertificate(String path, byte[] DEREncodedCertificate)
	{
		PemWriter pemWrt;
		
	    try {
	    	pemWrt = new PemWriter(new FileWriter(path));
		    pemWrt.writeObject(new PemObject("CERTIFICATE", DEREncodedCertificate));
		    pemWrt.flush();
		    pemWrt.close();
	    } catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private static void log(String message)
	{
		System.out.println(message);
	}
}
