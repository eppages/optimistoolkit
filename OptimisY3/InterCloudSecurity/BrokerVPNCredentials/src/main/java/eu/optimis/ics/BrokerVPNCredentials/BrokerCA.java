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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigInteger;
import java.rmi.AccessException;
import java.rmi.AlreadyBoundException;
import java.rmi.NoSuchObjectException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Security;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Date;

import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.BasicConstraints;
import org.bouncycastle.asn1.x509.ExtendedKeyUsage;
import org.bouncycastle.asn1.x509.KeyPurposeId;
import org.bouncycastle.asn1.x509.KeyUsage;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.asn1.x509.X509Extension;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509ExtensionUtils;
import org.bouncycastle.pkcs.PKCS10CertificationRequest;
import org.bouncycastle.openssl.PEMReader;
import org.bouncycastle.operator.ContentSigner;

import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.bouncycastle.util.io.pem.PemReader;
        
public class BrokerCA implements RemoteCSR {
	
	private final static String caPath = "/home/ali/workspace/BrokerVPNCredentials/credentials/";

	public BrokerCA() {
    	
    	if (System.getSecurityManager() == null) {
            System.setSecurityManager(new SecurityManager());
        }
    	Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
    }
    
    public static void main(String args[]) {
    	
    final String name = "RemoteCA";
    
    try 
    {
    	File caFile = new File(caPath+"ca.crt");
    	
    	if(!caFile.exists())
    	{
    		System.err.println("The CA Certificate does not exist on the Broker, please create one first");
    		System.exit(1);
    	}
    	
    	final BrokerCA brokerCA = new BrokerCA();
    	RemoteCSR stub = (RemoteCSR) UnicastRemoteObject.exportObject(brokerCA, 8081); // Reply port, esp in case of firewall
        // Bind the remote object's stub in the registry
        final Registry registry = LocateRegistry.getRegistry();
        registry.bind(name, stub);

        System.err.println("Broker CA Server Bound");
        
        Runtime.getRuntime().addShutdownHook(new Thread() {
		    public void run() 
		    {
		    	System.out.println("Removing the BrokerCA from RMI runtime");
		    	try 
		    	{
		    		System.out.println("Successfully removed : "+UnicastRemoteObject.unexportObject(brokerCA, false));
		    		registry.unbind(name);
				} 
		    	catch (NoSuchObjectException e) {
					e.printStackTrace();
				} catch (AccessException e) {
					e.printStackTrace();
				} catch (RemoteException e) {
					e.printStackTrace();
				} catch (NotBoundException e) {
					e.printStackTrace();
				}
		    }
		 });
        
    } 
    catch (RemoteException e) {
        System.err.println("Broker Server Exception MUAHAHAHAHA :-\n " + e.toString());
        e.printStackTrace();
    } 
    catch (AlreadyBoundException e) {
    	System.err.println("Broker Already Bound MUAHAHAHAHA :-\n " + e.toString());
    	e.printStackTrace();
	}
    }
	
	public byte[] getSignedCertificateBytes(byte[] sentCSRBytes) 
	{
		X509CertificateHolder certHolder = null;
		byte[] result = null;
		
		try {
		PKCS10CertificationRequest certRequest = new PKCS10CertificationRequest(sentCSRBytes);
		PEMReader r = new PEMReader(new FileReader(caPath+"ca.crt"));
		X509Certificate rootCert = (X509Certificate) r.readObject();
		r.close();
		
		X500Name subject = certRequest.getSubject();
		
		MessageDigest m = MessageDigest.getInstance("MD5");
	    m.update(subject.toString().getBytes(), 0, subject.toString().length());
	    
		BigInteger serial = new BigInteger(m.digest());
        
        Date notBefore = new Date(System.currentTimeMillis() - 1000L * 60 * 60 * 24 * 30);
        Date notAfter = new Date(System.currentTimeMillis() + (1000L * 60 * 60 * 24 * 365));
        
		SubjectPublicKeyInfo publicKeyInfo =  certRequest.getSubjectPublicKeyInfo();
		
        X500Name issuer = new X500Name(rootCert.getSubjectDN().toString()); 
 
		X509v3CertificateBuilder v3CertBuilder = new X509v3CertificateBuilder(issuer, serial, notBefore, notAfter, subject, publicKeyInfo);
		
		v3CertBuilder.addExtension(X509Extension.subjectKeyIdentifier, false, new JcaX509ExtensionUtils().createSubjectKeyIdentifier(publicKeyInfo));
		v3CertBuilder.addExtension(X509Extension.authorityKeyIdentifier, false, new JcaX509ExtensionUtils().createAuthorityKeyIdentifier(rootCert));
		v3CertBuilder.addExtension(X509Extension.basicConstraints, false, new BasicConstraints(false));
		v3CertBuilder.addExtension(X509Extension.extendedKeyUsage, false, new ExtendedKeyUsage(KeyPurposeId.id_kp_ipsecEndSystem));
		v3CertBuilder.addExtension(X509Extension.keyUsage, false, new KeyUsage(KeyUsage.digitalSignature));
		
		ContentSigner sigGen = new JcaContentSignerBuilder("SHA1withRSA").setProvider("BC").build(loadCAPrivateKey(caPath));
		certHolder = v3CertBuilder.build(sigGen);
		result = certHolder.getEncoded();
		} catch (Exception e) {	
			e.printStackTrace(); 
		}
		return result;
	}
	
	public byte[] getCACertificate() throws RemoteException, IOException, CertificateEncodingException 
	{
		PEMReader r = new PEMReader(new FileReader(caPath+"ca.crt"));
		X509Certificate rootCert = (X509Certificate) r.readObject();
		r.close();
		return rootCert.getEncoded();
	}
	
	private PrivateKey loadCAPrivateKey(String credPath) throws FileNotFoundException, IOException, NoSuchAlgorithmException, InvalidKeySpecException
	{
		FileReader fileReader = new FileReader(credPath+"ca.key");
		
        @SuppressWarnings("resource")
		byte[] encodedPrivateKey = new PemReader(fileReader).readPemObject().getContent();
        
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
		PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(encodedPrivateKey);
		return keyFactory.generatePrivate(privateKeySpec);
	}
}

