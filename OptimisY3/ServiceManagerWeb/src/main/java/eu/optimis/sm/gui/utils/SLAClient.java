/*
 * Copyright 2012 University of Stuttgart
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package eu.optimis.sm.gui.utils;

import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;

//import org.apache.log4j.Logger;
import org.ogf.graap.wsag.api.client.AgreementClient;
import org.ogf.graap.wsag.api.security.ISecurityProperties;
import org.ogf.graap.wsag.api.security.SecurityProperties;
import org.ogf.graap.wsag.client.remote.RemoteAgreementClientImpl;
import org.ogf.graap.wsag.security.core.KeystoreProperties;
import org.ogf.graap.wsag.security.core.keystore.KeystoreLoginContext;
import org.w3.x2005.x08.addressing.EndpointReferenceType;

public class SLAClient
{

	private static String keyStoreAlias="wsag4j-user";
	private static String privateKeyPassword="user@wsag4j";
	private static String keyStoreType="JKS";

	private static String keystoreFilename="/wsag4j-client-keystore.jks";
	private static String keystorePassword="user@wsag4j";

	private static String truststoreType="JKS";
	private static String truststoreFilename="/wsag4j-client-keystore.jks";
	private static String truststorePassword="user@wsag4j";
	
	private static KeystoreProperties getKeystoreProperties()
	{
		System.out.println("Setting keystore properties for SLAClient...");

		KeystoreProperties properties = new KeystoreProperties();
		properties.setKeyStoreAlias(SLAClient.keyStoreAlias);
		properties.setPrivateKeyPassword(SLAClient.privateKeyPassword);

		properties.setKeyStoreType(SLAClient.keyStoreType);
		properties.setKeystoreFilename(SLAClient.keystoreFilename);
		properties.setKeystorePassword(SLAClient.keystorePassword);

		properties.setTruststoreType(SLAClient.truststoreType);
		properties.setTruststoreFilename(SLAClient.truststoreFilename);
		properties.setTruststorePassword(SLAClient.truststorePassword);

		return properties;
	}
	
	private static LoginContext getLoginContext(KeystoreProperties properties)
	{
		System.out.println("Creating the login context for SLAClient...");
		try
		{
			LoginContext loginContext = null;
			loginContext = new KeystoreLoginContext(properties);
			loginContext.login();
			return loginContext;
		}
		catch (LoginException e)
		{
			System.out.println("Failed to create the login context : "+ e.getMessage());
			return null;
		}
	}

	public AgreementClient getSLA(EndpointReferenceType agreementEndpoint) throws Exception
	{
		KeystoreProperties properties = SLAClient.getKeystoreProperties();
		LoginContext logContext = SLAClient.getLoginContext(properties);
		ISecurityProperties securityProperties = new SecurityProperties(logContext);
		System.out.println("Creating an RemoteAgreementClientImpl object with endpoint "
				+ agreementEndpoint.getAddress().getStringValue());
		RemoteAgreementClientImpl remoteAgrClient = 
				new RemoteAgreementClientImpl(agreementEndpoint, securityProperties.clone());
		return remoteAgrClient;
	}
	
	public AgreementClient getSLA(String serializedEndpoint) throws Exception
	{
		EndpointReferenceType loadedEPR = EndpointReferenceType.Factory.parse(serializedEndpoint);
		AgreementClient ac = getSLA(loadedEPR);

		return ac;
	}
	
	public static void main(String[] args) throws Exception
	{
		EndpointReferenceType agreementEndpoint = null;
		SLAClient slaClient = new SLAClient();
		AgreementClient sla = slaClient.getSLA(agreementEndpoint);
		sla.terminate();
		System.out.println("SLA = " + sla.toString());
	}
}
