/*
	1Copyright (C) 2012-2013 Umeå University

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/
package eu.optimis.broker.core;



import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;

import org.ogf.graap.wsag.api.AgreementOffer;
import org.ogf.graap.wsag.api.client.AgreementClient;
import org.ogf.graap.wsag.api.client.AgreementFactoryClient;
import org.ogf.graap.wsag.api.client.AgreementFactoryRegistryClient;
import org.ogf.graap.wsag.api.types.AgreementOfferType;
import org.ogf.graap.wsag.client.AgreementFactoryRegistryLocator;
import org.ogf.graap.wsag.security.core.KeystoreProperties;
import org.ogf.graap.wsag.security.core.keystore.KeystoreLoginContext;
import org.ogf.schemas.graap.wsAgreement.AgreementTemplateType;
import org.ogf.schemas.graap.wsAgreement.ServiceDescriptionTermType;
import org.w3.x2005.x08.addressing.EndpointReferenceType;

import eu.optimis.types.xmlbeans.servicemanifest.XmlBeanServiceManifestDocument;



/**
 * @author Wubin.Li (Viali)
 * @author Petter Svärd
 * 
 * Modified by Pramod
 */

public class BrokerCloudQoSClient
{
	//private static Logger logger = Logger.getLogger(CloudQoSClient.class);

	private String URLEndpoint;
	
	//All Default Values..
	private static String keyStoreAlias="wsag4j-user";
	private static String privateKeyPassword="user@wsag4j";
	private static String keyStoreType="JKS";

	private static String keystoreFilename="/wsag4j-client-keystore.jks";
	private static String keystorePassword="user@wsag4j";

	private static String truststoreType="JKS";
	private static String truststoreFilename="/wsag4j-client-keystore.jks";
	private static String truststorePassword="user@wsag4j";
	
	public BrokerCloudQoSClient(String urlEndpoint)
	{
		this.URLEndpoint = urlEndpoint;
	}

	private static KeystoreProperties getKeystoreProperties()
	{
		OutputStub4Demo.write("Setting keystore properties for CloudQoSClient...");

		KeystoreProperties properties = new KeystoreProperties();
		properties.setKeyStoreAlias(BrokerCloudQoSClient.keyStoreAlias);
		properties.setPrivateKeyPassword(BrokerCloudQoSClient.privateKeyPassword);

		properties.setKeyStoreType(BrokerCloudQoSClient.keyStoreType);
		properties.setKeystoreFilename(BrokerCloudQoSClient.keystoreFilename);
		properties.setKeystorePassword(BrokerCloudQoSClient.keystorePassword);

		properties.setTruststoreType(BrokerCloudQoSClient.truststoreType);
		properties.setTruststoreFilename(BrokerCloudQoSClient.truststoreFilename);
		properties.setTruststorePassword(BrokerCloudQoSClient.truststorePassword);

		return properties;
	}
	
	private static LoginContext getLoginContext(KeystoreProperties properties)
	{
		OutputStub4Demo.write("Creating the login context for CloudQoSClient...");
		try
		{
			LoginContext loginContext = null;
			loginContext = new KeystoreLoginContext(properties);
			loginContext.login();
			return loginContext;
		}
		catch (LoginException e)
		{
			OutputStub4Demo.write("Failed to create the login context : "+ e.getMessage());
			return null;
		}
	}
	
	private AgreementFactoryClient getAgrFactoryClient()
	{
		OutputStub4Demo.write("Getting agreement factory...");
		
		AgreementFactoryClient[] agrFactoryClients = null;
		
		//set up keystore properties
		KeystoreProperties properties = BrokerCloudQoSClient.getKeystoreProperties();
		if (properties == null)
		{
			OutputStub4Demo.write("Keystore properties == null ...");
			return null;
		}
		
		//create the login context
		LoginContext loginContext = BrokerCloudQoSClient.getLoginContext(properties);
		if (loginContext == null)
		{
			OutputStub4Demo.write("Login context == null ...");
			return null;
		}
		
		// lookup the agreement factory service
		OutputStub4Demo.write("Looking up the agreement factory service...");
		EndpointReferenceType epr = EndpointReferenceType.Factory.newInstance();
		epr.addNewAddress().setStringValue(URLEndpoint);
		OutputStub4Demo.write("The agreement factory url endpoint is : "+URLEndpoint);
		try
		{
			AgreementFactoryRegistryClient registry = AgreementFactoryRegistryLocator.getFactoryRegistry(epr, loginContext);
			agrFactoryClients = registry.listAgreementFactories();
		}
		catch (Exception e)
		{
			OutputStub4Demo.write("Failure when looking up the agreeement factory service : " + e.getMessage());
			e.printStackTrace();
		}
		
		if ((agrFactoryClients == null)||(agrFactoryClients.length == 0))
		{
			OutputStub4Demo.write("Error getting factory or there is no factory configured at the given endpoint");
			return null;
		}
		
		//return the first one
		return agrFactoryClients[0];
	}
	
	
	//Update template using manifestString 
	private AgreementTemplateType updateTemplate(AgreementTemplateType template, String manifestString) throws Exception
	{
		OutputStub4Demo.write("Udating SLA template using a  manifest & objective..");
		
		ServiceDescriptionTermType[] sdts =  template.getTerms().getAll().getServiceDescriptionTermArray();

		//------Manifest Service SDT Update------
		
		ServiceDescriptionTermType manifestSDT= null;
		
		if (sdts != null)
		{
			for (int i = 0; i < sdts.length; i++)
			{
				if (sdts[i].getName().equals("OPTIMIS_SERVICE_SDT"))
				{
					manifestSDT = sdts[i];
					break;
				}
			}
		}
		
		
        if ( manifestSDT == null )
        {
            throw new Exception( "there is no service  SDT in agreement template." );
        }

        String name = manifestSDT.getName();
		String serviceName = manifestSDT.getServiceName();

		XmlBeanServiceManifestDocument doc = XmlBeanServiceManifestDocument.Factory.parse(manifestString);
		manifestSDT.set(doc);
		manifestSDT.setName( name );
		manifestSDT.setServiceName( serviceName );
		
		OutputStub4Demo.write("\nSLA template Update (manifest ) done.");
		
		return template;
	}
	
	public AgreementClient createAgreement(String templateName, String templateId, String manifestString) throws Exception
	{
		//Get Factory
		AgreementFactoryClient agrFactoryClient = this.getAgrFactoryClient();
		OutputStub4Demo.write("\nAgreement factory endpoint: "+ agrFactoryClient.getEndpoint().getAddress());
		
		//Get template
		OutputStub4Demo.write("\nGetting aggreement template by name and id...");
		AgreementTemplateType template = agrFactoryClient.getTemplate(templateName, templateId);
		
		//Update template using manifest, but no need to update the objective SDT
		template = this.updateTemplate(template, manifestString);
		
		//Construct Offer
		OutputStub4Demo.write("\nGet agrOffer using template, an AgreeementOffer is gonna returned..");
		AgreementOffer agrOffer = new AgreementOfferType(template);
					
		AgreementClient agreement = agrFactoryClient.createAgreement(agrOffer);
		return agreement;
	}
	

	
	public static void main(String args[]) throws Exception
	{ 
		//testNegotiateWithBroker();
		/*
		String TEMPLATE_NAME = "OPTIMIS-SERVICE-INSTANTIATION";
		//String urlEndpoint = "http://optimis-ipvm.atosorigin.es:8080/optimis-sla";
		String urlEndpoint = "http://109.231.120.19:8080/optimis-sla";
		CloudQoSClient qosClient = new CloudQoSClient(urlEndpoint);
		Pair<String, Double> offer = qosClient.getNegotiationOffer(TEMPLATE_NAME, "");
		if (offer == null)
			System.out.println("offer==null");
			*/
	}
}
