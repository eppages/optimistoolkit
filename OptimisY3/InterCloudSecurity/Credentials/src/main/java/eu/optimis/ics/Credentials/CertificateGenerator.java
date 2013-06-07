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

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.cert.CertificateParsingException;
import java.security.cert.X509Certificate;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Date;

import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.BasicConstraints;
import org.bouncycastle.asn1.x509.ExtendedKeyUsage;
import org.bouncycastle.asn1.x509.KeyPurposeId;
import org.bouncycastle.asn1.x509.KeyUsage;
import org.bouncycastle.asn1.x509.SubjectKeyIdentifier;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.asn1.x509.X509Extension;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.jce.PKCS10CertificationRequest;
import org.bouncycastle.openssl.PEMReader;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.bouncycastle.util.io.pem.PemReader;
import org.bouncycastle.x509.extension.AuthorityKeyIdentifierStructure;

public class CertificateGenerator {
	
	public static X509CertificateHolder genServerCertificate(PKCS10CertificationRequest certRequest, String credPath) 
	{
		X509v3CertificateBuilder v3CertBuilder = null;
		ContentSigner sigGen = null;
		try
		{
			
		PEMReader r = new PEMReader(new FileReader(credPath+"ca.crt"));
		X509Certificate rootCert = (X509Certificate) r.readObject();
		r.close();
		
		BigInteger serial = BigInteger.ONE;
        
        Date notBefore = new Date(System.currentTimeMillis() - 1000L * 60 * 60 * 24 * 30);
        Date notAfter = new Date(System.currentTimeMillis() + (1000L * 60 * 60 * 24 * 365*10));
        
		SubjectPublicKeyInfo publicKeyInfo =  SubjectPublicKeyInfo.getInstance(certRequest.getPublicKey().getEncoded());
		
        X500Name issuer = new X500Name(rootCert.getSubjectDN().toString()); 
        System.out.println(issuer.toString());
		@SuppressWarnings("deprecation")
		X500Name subject = new X500Name (certRequest.getCertificationRequestInfo().getSubject().toString());
        
		v3CertBuilder = new X509v3CertificateBuilder(issuer, serial, notBefore, notAfter, subject, publicKeyInfo);
		
		v3CertBuilder.addExtension(X509Extension.subjectKeyIdentifier, false, new SubjectKeyIdentifier(publicKeyInfo));
		v3CertBuilder.addExtension(X509Extension.authorityKeyIdentifier, false, new AuthorityKeyIdentifierStructure(rootCert));
		v3CertBuilder.addExtension(X509Extension.basicConstraints, false, new BasicConstraints(false));
		v3CertBuilder.addExtension(X509Extension.extendedKeyUsage, false, new ExtendedKeyUsage(KeyPurposeId.id_kp_serverAuth));
		v3CertBuilder.addExtension(X509Extension.keyUsage, false, new KeyUsage(KeyUsage.digitalSignature | KeyUsage.keyEncipherment));
		
		sigGen = new JcaContentSignerBuilder("SHA1withRSA").setProvider("BC").build(loadCAPrivateKey(credPath));
		
		}
		catch(IOException ioe) {
			ioe.printStackTrace();
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (NoSuchProviderException e) {
			e.printStackTrace();
		} catch (OperatorCreationException e) {
			e.printStackTrace();
		} catch (InvalidKeySpecException e) {
			e.printStackTrace();
		} catch (CertificateParsingException e) {
			e.printStackTrace();
		}
		
		return v3CertBuilder.build(sigGen);
	}
	
	public static X509CertificateHolder genClientCertificate(PKCS10CertificationRequest certRequest, String credPath) throws Exception 
	{
		PEMReader r = new PEMReader(new FileReader(credPath+"ca.crt"));
		X509Certificate rootCert = (X509Certificate) r.readObject();
		r.close();
		
		BigInteger serial = BigInteger.valueOf(2).abs();
        
        Date notBefore = new Date(System.currentTimeMillis() - 1000L * 60 * 60 * 24 * 30);
        Date notAfter = new Date(System.currentTimeMillis() + (1000L * 60 * 60 * 24 * 365*10));
        
		SubjectPublicKeyInfo publicKeyInfo =  SubjectPublicKeyInfo.getInstance(certRequest.getPublicKey().getEncoded());
		
        X500Name issuer = new X500Name(rootCert.getSubjectDN().toString()); 
        	
		@SuppressWarnings("deprecation")
		X500Name subject = new X500Name (certRequest.getCertificationRequestInfo().getSubject().toString());
        
		X509v3CertificateBuilder v3CertBuilder = new X509v3CertificateBuilder(issuer, serial, notBefore, notAfter, subject, publicKeyInfo);
		
		v3CertBuilder.addExtension(X509Extension.subjectKeyIdentifier, false, new SubjectKeyIdentifier(publicKeyInfo));
		v3CertBuilder.addExtension(X509Extension.authorityKeyIdentifier, false, new AuthorityKeyIdentifierStructure(rootCert));
		v3CertBuilder.addExtension(X509Extension.basicConstraints, false, new BasicConstraints(false));
		v3CertBuilder.addExtension(X509Extension.extendedKeyUsage, false, new ExtendedKeyUsage(KeyPurposeId.id_kp_clientAuth));
		v3CertBuilder.addExtension(X509Extension.keyUsage, false, new KeyUsage(KeyUsage.digitalSignature));
		
		ContentSigner sigGen = null;
		
		try 
		{
			sigGen = new JcaContentSignerBuilder("SHA1withRSA").setProvider("BC").build(loadCAPrivateKey(credPath));
		} catch (OperatorCreationException e) {
			e.printStackTrace();
		}
		
		return v3CertBuilder.build(sigGen);
	}
	
	@SuppressWarnings("resource")
	private static PrivateKey loadCAPrivateKey(String credPath) throws FileNotFoundException, IOException, NoSuchAlgorithmException, InvalidKeySpecException
	{
		FileReader fileReader = new FileReader(credPath+"ca.key");
		
        byte[] encodedPrivateKey = new PemReader(fileReader).readPemObject().getContent();
        
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
		PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(encodedPrivateKey);
		return keyFactory.generatePrivate(privateKeySpec);
	}
}