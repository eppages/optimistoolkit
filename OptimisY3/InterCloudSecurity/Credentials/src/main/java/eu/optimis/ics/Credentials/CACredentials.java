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

import java.util.Date;
import java.io.IOException;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.security.KeyPair;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.AuthorityKeyIdentifier;
import org.bouncycastle.asn1.x509.BasicConstraints;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.asn1.x509.GeneralNames;
import org.bouncycastle.asn1.x509.SubjectKeyIdentifier;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.asn1.x509.X509Extension;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.bouncycastle.cert.X509CertificateHolder;
/**
 * @author Ali Sajjad
 *
 */
public class CACredentials extends Credentials {
	
	public static void main(String[] args) {

        CACredentials sc = new CACredentials();

        // Generate a key pair for the CA and save its private key on file
        KeyPair CAKP = sc.genKeyPair();	
        sc.savePrivateKey("ca.key", CAKP.getPrivate());

        // Generate a certificate for the CA and save it on file
        X509CertificateHolder certHolder = sc.genCACertificate(CAKP);
        
        try 
        {
			sc.saveCertificate("ca.crt", certHolder.getEncoded());
		} 
        catch (IOException e) {
			e.printStackTrace();
		}
        System.out.println("Done");
	}

	protected X509CertificateHolder genCACertificate(KeyPair CAKP)
	{
        BigInteger serial = BigInteger.valueOf(new SecureRandom().nextLong()).abs();

		Date notBefore = new Date(System.currentTimeMillis() - 1000L * 60 * 60 * 24 * 30);
		Date notAfter = new Date(System.currentTimeMillis() + (1000L * 60 * 60 * 24 * 365));

        SubjectPublicKeyInfo publicKeyInfo =  SubjectPublicKeyInfo.getInstance(CAKP.getPublic().getEncoded());

        // Same issuer and subject for the self-signed CA certificate
        X500Name issuer = new X500Name("C=UK, ST=Suffolk, L=Ipswich, O=BT, OU=R&T, CN=CloudShadow, Name=Ali, emailAddress=ali.sajjad@bt.com");
        X500Name subject = new X500Name("C=UK, ST=Suffolk, L=Ipswich, O=BT, OU=R&T, CN=CloudShadow, Name=Ali, emailAddress=ali.sajjad@bt.com");

        X509v3CertificateBuilder v3CertBuilder = new X509v3CertificateBuilder(issuer, serial, notBefore, notAfter, subject, publicKeyInfo);

        GeneralNames gNames = new GeneralNames(new GeneralName(issuer));
        v3CertBuilder.addExtension(X509Extension.subjectKeyIdentifier, false, new SubjectKeyIdentifier(publicKeyInfo));
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
}