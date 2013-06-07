/*
Copyright (C) 2012-2013 Umeå University

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
package eu.optimis._do.stubs;

//import java.io.File;
import java.io.File;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.GregorianCalendar;

import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;

import org.apache.log4j.Logger;
import org.apache.xmlbeans.XmlObject;
import org.ogf.graap.wsag.api.AgreementOffer;
import org.ogf.graap.wsag.api.client.AgreementClient;
import org.ogf.graap.wsag.api.client.AgreementFactoryClient;
import org.ogf.graap.wsag.api.client.AgreementFactoryRegistryClient;
import org.ogf.graap.wsag.api.client.NegotiationClient;
import org.ogf.graap.wsag.api.exceptions.ResourceUnavailableException;
import org.ogf.graap.wsag.api.exceptions.ResourceUnknownException;
import org.ogf.graap.wsag.api.security.ISecurityProperties;
import org.ogf.graap.wsag.api.security.SecurityProperties;
import org.ogf.graap.wsag.api.types.AgreementOfferType;
import org.ogf.graap.wsag.api.types.NegotiationOfferTypeImpl;
import org.ogf.graap.wsag.client.AgreementFactoryRegistryLocator;
import org.ogf.graap.wsag.client.remote.Axis2SoapClient;
import org.ogf.graap.wsag.client.remote.RemoteAgreementClientImpl;
import org.ogf.graap.wsag.security.core.KeystoreProperties;
import org.ogf.graap.wsag.security.core.keystore.KeystoreLoginContext;
import org.ogf.schemas.graap.wsAgreement.AgreementTemplateType;
import org.ogf.schemas.graap.wsAgreement.ServiceDescriptionTermType;
import org.ogf.schemas.graap.wsAgreement.negotiation.NegotiationContextDocument;
import org.ogf.schemas.graap.wsAgreement.negotiation.NegotiationContextType;
import org.ogf.schemas.graap.wsAgreement.negotiation.NegotiationOfferContextType;
import org.ogf.schemas.graap.wsAgreement.negotiation.NegotiationOfferStateType;
import org.ogf.schemas.graap.wsAgreement.negotiation.NegotiationOfferType;
import org.ogf.schemas.graap.wsAgreement.negotiation.NegotiationRoleType;
import org.ogf.schemas.graap.wsAgreement.negotiation.NegotiationType;
import org.w3.x2005.x08.addressing.EndpointReferenceType;
import org.ogf.graap.wsag.client.remote.Axis2SoapClient;


import eu.optimis._do.schemas.internal.Pair;
import eu.optimis._do.utils.SlaUtil;
import eu.optimis.manifest.api.sp.Manifest;
import eu.optimis.sla.types.service.price.SLAServicePriceDocument;
import eu.optimis.sla.types.service.price.SLAServicePriceType;
import eu.optimis.types.xmlbeans.servicemanifest.XmlBeanServiceManifestDocument;

/**
 * @author Wubin.Li (Viali)
 * @author Petter Svärd
 * 
 */

public class CloudQoSClient
{
	private static Logger logger = Logger.getLogger(CloudQoSClient.class);

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
	
	public CloudQoSClient(String urlEndpoint)
	{
		this.URLEndpoint = urlEndpoint;
	}

	private static KeystoreProperties getKeystoreProperties()
	{
		logger.debug("Setting keystore properties for CloudQoSClient...");

		KeystoreProperties properties = new KeystoreProperties();
		properties.setKeyStoreAlias(CloudQoSClient.keyStoreAlias);
		properties.setPrivateKeyPassword(CloudQoSClient.privateKeyPassword);

		properties.setKeyStoreType(CloudQoSClient.keyStoreType);
		properties.setKeystoreFilename(CloudQoSClient.keystoreFilename);
		properties.setKeystorePassword(CloudQoSClient.keystorePassword);

		properties.setTruststoreType(CloudQoSClient.truststoreType);
		properties.setTruststoreFilename(CloudQoSClient.truststoreFilename);
		properties.setTruststorePassword(CloudQoSClient.truststorePassword);

		return properties;
	}
	
	private static LoginContext getLoginContext(KeystoreProperties properties)
	{
		logger.debug("Creating the login context for CloudQoSClient...");
		try
		{
			LoginContext loginContext = null;
			loginContext = new KeystoreLoginContext(properties);
			loginContext.login();
			return loginContext;
		}
		catch (LoginException e)
		{
			logger.error("Failed to create the login context : "+ e.getMessage());
			return null;
		}
	}
	
	private AgreementFactoryClient getAgrFactoryClient()
	{
		logger.debug("Getting agreement factory...");
		
		AgreementFactoryClient[] agrFactoryClients = null;
		
		//set up keystore properties
		KeystoreProperties properties = CloudQoSClient.getKeystoreProperties();
		if (properties == null)
		{
			logger.error("Keystore properties == null ...");
			return null;
		}
		
		//create the login context
		LoginContext loginContext = CloudQoSClient.getLoginContext(properties);
		if (loginContext == null)
		{
			logger.error("Login context == null ...");
			return null;
		}
		
		// lookup the agreement factory service
		logger.debug("Looking up the agreement factory service...");
		EndpointReferenceType epr = EndpointReferenceType.Factory.newInstance();
		epr.addNewAddress().setStringValue(URLEndpoint);
		logger.debug("The agreement factory url endpoint is : "+URLEndpoint);
		try
		{
			AgreementFactoryRegistryClient registry = AgreementFactoryRegistryLocator.getFactoryRegistry(epr, loginContext);
			agrFactoryClients = registry.listAgreementFactories();
		}
		catch (Exception e)
		{
			logger.error("Failure when looking up the agreeement factory service : " + e.getMessage());
			e.printStackTrace();
		}
		
		if ((agrFactoryClients == null)||(agrFactoryClients.length == 0))
		{
			logger.error("Error getting factory or there is no factory configured at the given endpoint");
			return null;
		}
		
		//return the first one
		return agrFactoryClients[0];
	}
	
	
	//Update template using manifestXML
	private AgreementTemplateType updateTemplate(AgreementTemplateType template, String manifestXML) throws Exception
	{
		logger.debug("Udating SLA template using a  manifest & objective..");
		
		ServiceDescriptionTermType[] sdts =  template.getTerms().getAll().getServiceDescriptionTermArray();
		
		//------Manifest SDT Update------
		ServiceDescriptionTermType manifestSDT = SlaUtil.findSDTbyName(sdts, SlaUtil.TEMPLATE_SDT_SERVICE);
        if ( manifestSDT == null )
        {
            throw new Exception( "there is no service  SDT in agreement template." );
        }
		String name = manifestSDT.getName();
		String serviceName = manifestSDT.getServiceName();

		XmlBeanServiceManifestDocument doc = XmlBeanServiceManifestDocument.Factory.parse(manifestXML);
		manifestSDT.set(doc);
		manifestSDT.setName( name );
		manifestSDT.setServiceName( serviceName );
		
		logger.debug("SLA template (manifest only) Update done.");		
		return template;
	}
	
	private Double calculateCost(AgreementTemplateType template, String manifestXML) throws Exception
	{
		ServiceDescriptionTermType[] sdts = template.getTerms().getAll().getServiceDescriptionTermArray();
		ServiceDescriptionTermType servicePriceSDT = SlaUtil.findSDTbyName(sdts , SlaUtil.TEMPLATE_SDT_PRICE);
	        
		if (servicePriceSDT == null)
		{
			throw new Exception(
					"there is no service price SDT in agreement template.");
		}

		XmlObject[] servicePriceXML = servicePriceSDT.selectChildren(SLAServicePriceDocument.type.getDocumentElementName());

		if (servicePriceXML.length == 0)
		{
			throw new Exception(
					"there is no service price document in service description terms.");
		}

		SLAServicePriceType servicePriceType = (SLAServicePriceType) servicePriceXML[0];
		BigDecimal amount = servicePriceType.getAmount();
		Double cost = amount.doubleValue();
		logger.debug("cost plan from template is: " + cost);
		
		XmlBeanServiceManifestDocument doc = XmlBeanServiceManifestDocument.Factory.parse(manifestXML);
		Manifest manifest = Manifest.Factory.newInstance(doc);
		int componentNumber = manifest.getVirtualMachineDescriptionSection().getVirtualMachineComponentArray().length;
		logger.debug("There are " + componentNumber+ " components in the manifest, so cost = " 
				+ cost + "*"+ componentNumber + "=" + cost* componentNumber);
		cost *= componentNumber;
		logger.debug("Price Plan returned from CloudQoS : "+ cost);
		return cost;
	}
	
	
	//This method is used for getting offer from the CloudQoS
	public Pair<NegotiationOfferType, Double> getNegotiationOffer(String templateName, String manifestXML) throws Exception
	{
		AgreementFactoryClient factory = this.getAgrFactoryClient();
		//
		// Now creates a negotiation context that defines the roles and obligations
		// of the negotiating parties and specifies the type of the negotiation  process.
		//
		NegotiationContextDocument contextDoc = NegotiationContextDocument.Factory.newInstance();
		NegotiationContextType context = contextDoc.addNewNegotiationContext();
		context.setAgreementFactoryEPR(factory.getEndpoint());
		context.setAgreementResponder(NegotiationRoleType.NEGOTIATION_RESPONDER);
		GregorianCalendar expireDate = new GregorianCalendar();
		expireDate.add(Calendar.HOUR, 12);
		context.setExpirationTime(expireDate);
		//
		// set the nature of the negotiation process (e.g. negotiation or re-negotiation).
		//
		NegotiationType negotiationType = context.addNewNegotiationType();
		negotiationType.addNewNegotiation();
		//
		// creating negotiation instance based on a negotiation context from a selected agreement factory
		//
		NegotiationClient negotiation = factory.initiateNegotiation(context);
		logger.debug("negotiation instance is created successfully");
		
		//IMPORTANT
		int timeS = 60 * 60 * 1000;
		logger.debug("SET TIME OUT VALUE: " + timeS);
		Axis2SoapClient.DEFAULT_TIME_OUT_IN_MILLI_SECONDS = timeS; // 1 Hour!
		
		//
		// retrieve the agreement templates for which negotiation is supported,
		// and select the one with template name
		//e.g., "OPTIMIS-SERVICE-INSTANTIATION".
		//
		AgreementTemplateType[] negotiableTemplates = negotiation.getNegotiableTemplates();
		
		logger.debug("Number of Negotiaable Templates : "+ negotiableTemplates.length);

		AgreementTemplateType template = null;
		
		logger.debug("Looking for template with name " + templateName);

		for (int i = 0; i < negotiableTemplates.length; i++)
		{
			AgreementTemplateType agreementTemplate = negotiableTemplates[i];
			if (agreementTemplate.getName().equals(templateName))
			{
				logger.debug("Template Found!");
				template = agreementTemplate;
			}
		}

		String offerId = template.getContext().getTemplateId() + "-"+ template.getName();
		
		//IMPORTANT Extract Price Plan from the Template and Calculate the Cost
		Double cost = this.calculateCost(template, manifestXML);
		//IMPORTANT Update the Template.
		template = this.updateTemplate(template, manifestXML);

		NegotiationOfferTypeImpl negOffer = new NegotiationOfferTypeImpl(template);

		//
		// creating negotiation offer context
		//
		NegotiationOfferContextType negOfferContext = NegotiationOfferContextType.Factory.newInstance();
		negOfferContext.setCreator(NegotiationRoleType.NEGOTIATION_INITIATOR);
		GregorianCalendar negExpireDate = new GregorianCalendar();
		//expireDate.add(Calendar.MINUTE, 15);
		expireDate.add(Calendar.HOUR, 15);
		negOfferContext.setExpirationTime(negExpireDate);
		NegotiationOfferStateType negOfferState = NegotiationOfferStateType.Factory.newInstance();
		negOfferState.addNewAdvisory();
		negOfferContext.setState(negOfferState);
		negOfferContext.setCounterOfferTo(offerId);

		negOffer.setNegotiationOfferContext(negOfferContext);

		NegotiationOfferType[] counterOffers = negotiation.negotiate(new NegotiationOfferType[] { negOffer.getXMLObject() });

		NegotiationOfferType counterOffer = counterOffers[0];
		
		//
		// check if negotiation offer is rejected or accepted
		//
		NegotiationOfferStateType state = counterOffer.getNegotiationOfferContext().getState();
		//if (state.isSetRejected() || state.isSetAdvisory())
		if (state.isSetRejected())
		{
			logger.debug("service manifest is rejected, i.e.,\n" + state + ".");
			cost = Double.MAX_VALUE;
		}
		//else if (state.isSetAcceptable())
		else
		{
			logger.debug("counter offer state: " + state);
			logger.debug("service manifest is not rejected, so considered to be accepted.");
			ServiceDescriptionTermType serviceSDT = null;

			ServiceDescriptionTermType[] sdts = counterOffer.getTerms().getAll().getServiceDescriptionTermArray();
			serviceSDT = SlaUtil.findSDTbyName(sdts, SlaUtil.TEMPLATE_SDT_SERVICE);
			if (serviceSDT == null)
			{
				throw new Exception("there is no (" + SlaUtil.TEMPLATE_SDT_SERVICE + ") service description term in negotiation offer.");
			}
			String manifestDoc = SlaUtil.extractManifestFromSDT(serviceSDT);
			if (manifestDoc == null)
				throw new Exception("there is no service manifest doc in service term state.");
		}
		//
		// finally terminate the negotiation process
		//
		negotiation.terminate();

		logger.debug("negotiation successfully completed");
		Pair<NegotiationOfferType, Double> result = new Pair<NegotiationOfferType, Double>(counterOffer, cost);
		return result;
	}
	
	public AgreementClient createAgreement(NegotiationOfferType counterOffer) throws Exception
	{
		String contextID = counterOffer.getName();
		AgreementOffer agrOffer = new AgreementOfferType(counterOffer);
		agrOffer.setName(contextID);
		
		AgreementFactoryClient factory = this.getAgrFactoryClient();
		AgreementClient agreement = factory.createAgreement(agrOffer);
		return agreement;
	}
	
	public static boolean terminateAgreement(EndpointReferenceType agreementEndpoint)
	{
		KeystoreProperties properties = CloudQoSClient.getKeystoreProperties();
		LoginContext logContext = CloudQoSClient.getLoginContext(properties);
		ISecurityProperties securityProperties = new SecurityProperties(logContext);
		logger.debug("Creating an RemoteAgreementClientImpl object with endpoint " + agreementEndpoint.getAddress().getStringValue());
		RemoteAgreementClientImpl remoteAgrClient = new RemoteAgreementClientImpl(agreementEndpoint, securityProperties.clone());
		try 
		{
			logger.debug("Starting to terminate agreement with endpoint " + agreementEndpoint.toString());
			remoteAgrClient.terminate();
			logger.debug("Agreement terminated successfully...");
		} 
		catch (ResourceUnknownException e) 
		{
			logger.error("Resource Unknown Exception when terminating agreement..");
			e.printStackTrace();
			return false;
		} 
		catch (ResourceUnavailableException e) 
		{
			logger.error("Resource Unavailable Exception when terminating agreement..");
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/*
	//Serialize the agreement endpoint to xmlText object.
	private String serializeEndpoint(EndpointReferenceType agreementEndpoint)
	{
		return agreementEndpoint.xmlText();
	}
	
	//Deserialize the agreement client from serialized xmlText object.
	private AgreementClient rebuildAgreementClient(String serializedEndpoint,LoginContext loginContext) throws XmlException
	{
		EndpointReferenceType loadedEPR = EndpointReferenceType.Factory.parse(serializedEndpoint);

		ISecurityProperties securityProperties = new SecurityProperties(loginContext);
		AgreementClient reinitialized = new RemoteAgreementClientImpl(loadedEPR, securityProperties);
		return reinitialized;
	}
	
	private AgreementClient rebuildAgreementClient(String serializedEndpoint) throws XmlException
	{
		KeystoreProperties properties=this.getKeystoreProperties();
		LoginContext loginContext=this.getLoginContext(properties);
		AgreementClient reinitialized =this.rebuildAgreementClient(serializedEndpoint,loginContext);
		return reinitialized;
	}
*/	
	
	public  void testSLAService() throws Exception
	{
		String template_name = "OPTIMIS-SERVICE-INSTANTIATION";
		String templateId = "1";
		AgreementFactoryClient factory = this.getAgrFactoryClient();
		AgreementTemplateType template = factory.getTemplate(template_name, templateId);
		System.out.println("Template Returned: \n" + template.getName());
		System.out.println("============================================================");
		
		String f = "src/test/resources/service_manifest.xml";
		File file = new File(f);
		XmlBeanServiceManifestDocument doc = XmlBeanServiceManifestDocument.Factory.parse(file);
		
		String manifestXML = doc.toString();
		
		Pair<NegotiationOfferType, Double> offers = this.getNegotiationOffer(
				template_name, manifestXML);
		System.out.println("Price Returned: " + offers.getB());
		
		//--
		NegotiationOfferType offer = offers.getA();
		System.out.println("Offer Name = "+offer.getName());
		System.out.println(offer);
	//	ServiceDescriptionTermType[] sdts =offer.getTerms().getAll().getServiceDescriptionTermArray();
	//	ServiceDescriptionTermType serviceSDT = SlaUtil.findSDTbyName(sdts , SlaUtil.TEMPLATE_SDT_SERVICE);
	//	manifestXML = SlaUtil.extractManifestFromSDT(serviceSDT);
	//	System.out.println(manifestXML);
		//offer.get
	}
	
	public static void main(String args[]) throws Exception
	{ 
		//String urlEndpoint = "http://optimis-ipvm.atosorigin.es:8080/optimis-sla";
		//String urlEndpoint = "http://optimis-ipvm2.ds.cs.umu.se:8080/optimis-sla";
		String urlEndpoint = "http://optimis-ipvm2.es.atos.net:8080/optimis-sla";
		CloudQoSClient qosClient = new CloudQoSClient(urlEndpoint);
		qosClient.testSLAService();
	}
}
