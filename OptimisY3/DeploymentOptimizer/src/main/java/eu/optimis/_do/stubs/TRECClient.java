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

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.ogf.schemas.graap.wsAgreement.negotiation.NegotiationOfferType;

import eu.optimis._do.DeploymentOptimizer;
import eu.optimis._do.schemas.internal.Pair;
import eu.optimis._do.schemas.internal.TrecObj;
import eu.optimis._do.utils.ManifestUtil;
import eu.optimis.ipdiscovery.datamodel.Provider;
import eu.optimis.manifest.api.sp.Manifest;
import eu.optimis.treccommon.TrecApiSP;
import eu.optimis.treccommon.TrecObject;
import eu.optimis.types.xmlbeans.servicemanifest.XmlBeanServiceManifestDocument;

/**
 * @author Wubin.Li (Viali)
 * @author Petter Svärd
 * 
 */
public class TRECClient
{
	private static Logger logger = Logger.getLogger(TRECClient.class);
	
	private String host;
	private int port;
	private boolean shouldReport;

	/**
	 * @param host
	 * @param port
	 * @param shouldReport, if true, report status to remote cite.
	 */
	public TRECClient(String host, int port)
	{
		super();
		this.host = host;
		this.port = port;
	}
	
	public boolean shouldReport()
	{
		return this.shouldReport;
	}

	private Pair<NegotiationOfferType, Double> getOfferAndCost(String manifestXML, Provider provider) throws Exception
	{
		String urlEndpoint = provider.getCloudQosUrl();
		CloudQoSClient qosClient = new CloudQoSClient(urlEndpoint);
		String templateName = provider.getAgrTemplateName();
		
		Pair<NegotiationOfferType, Double> p = qosClient.getNegotiationOffer(templateName, manifestXML);
		
		return p;
	}
	
	/**
	 * @param trecRequirement
	 * @param trecAccessment
	 * @return true if the trecAccessment can fulfill the trecRequirement, otherwise false.
	 */
	public static boolean checkTREC(TrecObj trecRequirement, TrecObj trecAccessment)
	{
		if (trecRequirement.getTrust() > trecAccessment.getTrust())
			return false;
		if (trecRequirement.getRisk() < trecAccessment.getRisk())
			return false;
		if (trecRequirement.getEco() > trecAccessment.getEco())
			return false;
		if (trecRequirement.getEnergy() > trecAccessment.getEnergy())
			return false;
		if (trecRequirement.getCost() < trecAccessment.getCost())
			return false;
		return true;
	}
	
	public static TrecObject getTREC(String host, int port, String providerId, Manifest manifest)
	{
		TrecApiSP trecApi = new TrecApiSP(host, port);
		double proposedPOF = 0.3;
		logger.debug("SP RISK - Using ProposedPOF = " + proposedPOF);
		Long timeSpan = null;
		TrecObject trec = trecApi.getTRECs(providerId, manifest, proposedPOF, timeSpan);
		logger.debug("TREC CALLED COMPELETED!");
		
		try
		{
			PropertyConfigurator
					.configure("/opt/optimis/sdo/DeploymentService/SDO-GUI/src/main/resources/log4j-jetty-sdo-gui.properties");
			logger.debug("PropertyConfigurator.configure(*) executed!");
		}
		catch (Exception e)
		{
			logger.error("PropertyConfigurator.configure(*) executed!: Failed.");
			e.printStackTrace();
		}
		
		return trec;
	}
	
	public TrecObj getTrecAssessment(String manifestXML, Provider provider,
			String combinedId, HashMap<String, NegotiationOfferType> idsAtProvider2OfferMap,
			Map<String, String> properties)
			throws Exception
	{		
		XmlBeanServiceManifestDocument doc = XmlBeanServiceManifestDocument.Factory.parse(manifestXML);
		Manifest manifest = Manifest.Factory.newInstance(doc);
		
		TrecObject trec=null;
		String isTRECcallSkipped = properties.get("is.trec.call.skip");
		if (isTRECcallSkipped != null
				&& isTRECcallSkipped.equalsIgnoreCase(Boolean.TRUE
						.toString()))
		{
			logger.debug("According to the configuration, TREC calls are skipped. And they will use FAKE values.");
			trec = new TrecObject();
			trec.set_trust(5);
			trec.set_risk(0);
			trec.setEcologicalEfficiency(5.0);
			trec.setEnergyEfficiency(5.0);
		}
		else
		{
			trec = TRECClient.getTREC(this.host, this.port,
					provider.getIdentifier(), manifest);
		}
		
		logger.debug("Trec Values from TREC COMMON API: [" + trec.get_trust()+ "," + trec.get_risk() + ", " 
		+ trec.getEcologicalEfficiency()+ ", " + trec.getEnergyEfficiency() + ", " 
				+ trec.get_cost()+ "]");
		
		logger.debug("Now calling CloudQoS(SlaManagement) to retrieve price plan..");
		
		double cost = Double.POSITIVE_INFINITY;
		try
		{
			 Pair<NegotiationOfferType, Double> offerAndCost = this.getOfferAndCost(manifestXML, provider);
			 //IMPORTANT the cost will be used anyway.
			 NegotiationOfferType offer = offerAndCost.getA();
			 if (offer != null)
			 {
				 String key = combinedId+ DeploymentOptimizer.idProviderSeparator+provider.getIdentifier();
				 idsAtProvider2OfferMap.put(key, offer);
				 cost = offerAndCost.getB();
			 }
		}
		catch(Exception e)
		{
			logger.debug("Failed to get COST from CloudQoS :"+ provider.getCloudQosUrl());
			logger.debug("Double.POSITIVE_INFINITY will be used for Cost.");
			e.printStackTrace();
		}
		
		logger.debug("RISK ASS before Normalization: "+ trec.get_risk());
		double risk = trec.get_risk() * 1.0 / ManifestUtil.RISK_MAX;
		logger.debug("RISK ASS after Normalization: "+ risk);
		TrecObj result = new TrecObj(trec.get_trust(), risk,
				trec.getEcologicalEfficiency(), trec.getEnergyEfficiency(),
				cost);
		logger.debug("TREC : " + result);
		return result;
	}
}
