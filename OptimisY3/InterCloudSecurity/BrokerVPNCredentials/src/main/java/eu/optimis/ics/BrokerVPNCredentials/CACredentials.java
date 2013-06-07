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

import java.util.Date;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.SecureRandom;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.Security;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.AuthorityKeyIdentifier;
import org.bouncycastle.asn1.x509.BasicConstraints;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.asn1.x509.GeneralNames;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.asn1.x509.X509Extension;
import org.bouncycastle.cert.CertIOException;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.openssl.PEMWriter;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.jcajce.JcaX509ExtensionUtils;
import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemWriter;
/*
 * Run this just once to create the CA credentials on the Broker, as there is no 
 * need to generate them again and again
 */
public class CACredentials {
	
	public static void main(String[] args) throws IOException, NoSuchAlgorithmException {

        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());

        CACredentials sc = new CACredentials();

        // Generate a key pair for the CA and save its private key on file
        KeyPair CAKP = sc.genCAKeyPair();	
        sc.saveCAPrivateKey("ca.key", CAKP.getPrivate());

        // Generate a certificate for the CA and save it on file
        X509CertificateHolder certHolder = sc.genCACertificate(CAKP);
        sc.saveCACertificate("ca.crt", certHolder.getEncoded());

        System.out.println("Done");
	}

	public KeyPair genCAKeyPair() {

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

	public X509CertificateHolder genCACertificate(KeyPair CAKP) throws CertIOException, NoSuchAlgorithmException
	{
        BigInteger serial = BigInteger.valueOf(42);

		Date notBefore = new Date(System.currentTimeMillis() - 1000L * 60 * 60 * 24 * 30);
		Date notAfter = new Date(System.currentTimeMillis() + (1000L * 60 * 60 * 24 * 365));

        SubjectPublicKeyInfo publicKeyInfo =  SubjectPublicKeyInfo.getInstance(CAKP.getPublic().getEncoded());

        // Same issuer and subject for the self-signed CA certificate
        X500Name issuer = new X500Name("C=UK, ST=Suffolk, L=Ipswich, O=BT, OU=R&T, CN=CloudShadow, Name=Ali, emailAddress=ali.sajjad@bt.com");
        X500Name subject = new X500Name("C=UK, ST=Suffolk, L=Ipswich, O=BT, OU=R&T, CN=CloudShadow, Name=Ali, emailAddress=ali.sajjad@bt.com");

        X509v3CertificateBuilder v3CertBuilder = new X509v3CertificateBuilder(issuer, serial, notBefore, notAfter, subject, publicKeyInfo);

        GeneralNames gNames = new GeneralNames(new GeneralName(issuer));
        v3CertBuilder.addExtension(X509Extension.subjectKeyIdentifier, false, new JcaX509ExtensionUtils().createSubjectKeyIdentifier(publicKeyInfo));
        v3CertBuilder.addExtension(X509Extension.authorityKeyIdentifier, false, new AuthorityKeyIdentifier(publicKeyInfo, gNames , serial));
        v3CertBuilder.addExtension(X509Extension.basicConstraints, false, new BasicConstraints(true));

        ContentSigner sigGen = null;

        try {
                sigGen = new JcaContentSignerBuilder("SHA1withRSA").setProvider("BC").build(CAKP.getPrivate());
        } catch (OperatorCreationException e) {
                e.printStackTrace();
        }
        return v3CertBuilder.build(sigGen);
}

	public void saveCAPrivateKey(String filePath, PrivateKey privateKey)
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

	public void saveCACertificate(String filePath, byte[] DEREncodedCertificate)
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
