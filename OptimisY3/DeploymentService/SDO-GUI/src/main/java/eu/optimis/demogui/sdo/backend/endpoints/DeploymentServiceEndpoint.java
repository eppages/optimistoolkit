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
package eu.optimis.demogui.sdo.backend.endpoints;

import java.io.File;
import java.io.Reader;
import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;

import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.commons.lang.math.RandomUtils;
import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;
import org.ogf.schemas.graap.wsAgreement.negotiation.NegotiationOfferType;
import org.w3.x2005.x08.addressing.EndpointReferenceType;
import org.zkoss.io.Files;

import eu.optimis._do.schemas.Objective;
import eu.optimis._do.schemas.Placement;
import eu.optimis._do.schemas.PlacementSolution;
import eu.optimis.demogui.sdo.backend.ServiceDeployerThread;
import eu.optimis.demogui.sdo.frontend.GuiComposer;
import eu.optimis.ipdiscovery.datamodel.Provider;
import eu.optimis.manifest.api.sp.Manifest;
import eu.optimis.sd.SD;
import eu.optimis.sd.schemas.st.ErrorStatus;
import eu.optimis.sd.schemas.st.NormalStatus;
import eu.optimis.sd.schemas.st.Status;
import eu.optimis.types.xmlbeans.servicemanifest.XmlBeanServiceManifestDocument;

/**
 * @author Wubin.Li (Viali)
 * @author Petter Svärd
 */
@Path("/DeployService")
public class DeploymentServiceEndpoint
{
	protected final static Logger logger = Logger.getLogger(DeploymentServiceEndpoint.class);
	
	//ServiceId <---> ServiceDeployer Thread
	private final static Hashtable<String, ServiceDeployerThread> sdThreads = new Hashtable<String, ServiceDeployerThread>();
	//ServiceId <--> PlacementSolution
	private final static Hashtable<String, PlacementSolution> brokerQueue = new Hashtable<String, PlacementSolution>();	
	
	public  DeploymentServiceEndpoint()
	{
		logger.debug("DeploymentServiceEndpoint Initialized..");
	}

	public static Hashtable<String, ServiceDeployerThread> getSDthreads()
	{
		return sdThreads;
	}
	
	public static Hashtable<String, PlacementSolution> getBrokerQueue()
	{
		return brokerQueue;
	}
	
	private File writeManifestToFile(String manifestXML) throws Exception
	{
		logger.debug("Saving Manifest String to File..");
		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmssSSS");
		String fileName = formatter.format(new Date()) + "."
				+ RandomUtils.nextInt(10) + ".xml";
		Reader reader = new StringReader(manifestXML);
		File manifestfile = new File(GuiComposer.manifestFilesPath + fileName);
		Files.copy(manifestfile, reader, null);
		Files.close(reader);
		logger.debug("Manifest String written to " + fileName);
		logger.debug("File path: " + manifestfile.getAbsolutePath());
		return manifestfile;
	}
	
	
	@Path("suggestFederatedIP")
	@POST
	public String suggestFederatedIP(@FormParam("ManifestString") String manifestStr)
	{
		logger.info("SuggestFederatedIP endpoint Called.");
		try
		{
			XmlBeanServiceManifestDocument doc = XmlBeanServiceManifestDocument.Factory.parse(manifestStr);
			Manifest spManifest= Manifest.Factory.newInstance(doc);
			String serviceId = spManifest.getVirtualMachineDescriptionSection().getServiceId();
			
			logger.debug("Calling suggestFederatedIP operation, serviceId: "+serviceId);

			SD sd = new SD();
			Provider ip = sd.suggestFederatedProvider(manifestStr);
			logger.debug("Suggested Federated IP == NULL :" + (ip == null));
			
			if (ip != null)
				return ip.getName();
		}
		catch (Exception e)
		{
			logger.error("Exception: " + e.getMessage());
			e.printStackTrace();
		}
		return null;
	}
	
	@Path("deploy")
	@POST
	//@Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	public String deploy(@FormParam("ManifestString") String manifestStr, @FormParam("Objective") String objective)
	{
		//Default Objective -- COST
		Objective obj = Objective.COST; 
		if (objective.equalsIgnoreCase("RISK"))
			obj = Objective.RISK;
		
		logger.info("Deployment Service REST Endpoint called. ");
		logger.info("Objective : "+obj);
		
		try
		{
			XmlBeanServiceManifestDocument doc = XmlBeanServiceManifestDocument.Factory.parse(manifestStr);
			Manifest spManifest= Manifest.Factory.newInstance(doc);
			String serviceId = spManifest.getVirtualMachineDescriptionSection().getServiceId();
			
			logger.info("ServiceId : " + serviceId);
			
			File manifestfile = this.writeManifestToFile(manifestStr);
			
			ServiceDeployerThread sdt = new ServiceDeployerThread(null, manifestfile, obj);
			DeploymentServiceEndpoint.sdThreads.put(serviceId, sdt);
			
			logger.debug("Starting ServiceDeployer Thread..");
			sdt.start();
						
			logger.debug("SD Thread for Service:" + serviceId
					+ "is stored in HashTable.");		
			
			return "true";
		}
		catch (Exception e)
		{
			logger.error(e.getMessage());
			e.printStackTrace();
			return "false";
		}			
	}
	
	@Path("{serviceId}/status")
	@GET
	@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	public String readDeploymentStatus(@PathParam("serviceId") String serviceId) 
	{
		logger.info("Receive a Status read request for service :"+serviceId);
		
		ServiceDeployerThread sdt = DeploymentServiceEndpoint.sdThreads.get(serviceId);
		logger.debug("ServiceDeployer Thread is sd=" + sdt);
		
		JSONObject json = new JSONObject();
		try
		{
			if (sdt == null)
			{
				json.put("TYPE", "ERROR");
				json.put("MESSAGE", "No service deployed with id "+serviceId);
			}
			else
			{
				logger.debug("Extract Status from sd thread.");
				Status status = sdt.getLatestRootStatus();
				logger.debug("Status returned: " + status);
				
				if(status.isErrorType())
				{
					json.put("TYPE", "ERROR");
					json.put("MESSAGE", ((ErrorStatus)status).getErrorMessage());
				}
				else if(status.isNormalType())
				{
					json.put("TYPE", "PROGRESS");
					json.put("MESSAGE", ((NormalStatus)status).getComponentProgress()+"%");					
				}
				else if (status.isCompletedType())
				{
					json.put("TYPE", "PROGRESS");
					json.put("MESSAGE", "100%");	
				}
			}
		}
		catch (JSONException e)
		{
			logger.error(e.getMessage());
			e.printStackTrace();
		}
		logger.debug(json + " will be returned to the Client.");
		return json.toString();
	}
	
	@Path("undeploy")
	@POST
	public String undeploy(@FormParam("serviceId") String serviceId, @FormParam("agreementEPR") String agreementEPR, @FormParam("keepData") boolean keepData)
	{
		logger.info("Undeployment Service REST Endpoint called. ");
		logger.info("Service Id = " + serviceId + ", keepData = " + keepData);
		logger.info("AgreementEndpoint: "+agreementEPR);
		try
		{
			logger.debug("Deserializing EndpointReference from String.");
			EndpointReferenceType agreementEndpoint = EndpointReferenceType.Factory.parse(agreementEPR);
			logger.debug("Deserializing EndpointReference from String: Done!");
			logger.debug("Agreement Qos Endpoint: "	+ agreementEndpoint.getAddress().getStringValue());
			
			SD sd = new SD();
			logger.debug("Calling SD.undeploy(X,X,X)...");
			boolean r = sd.undeploy(serviceId, agreementEndpoint, keepData);
			logger.debug("Service undeployment result : " + r);
			
			if(r)
				return "true";
			else return "false";
		}
		catch (Exception e)
		{
			logger.error("Exception: " + e.getMessage());
			e.printStackTrace();
			logger.error("false is gona returned.");
			return "false";
		}
	}
	
	@Path("{serviceId}/result")
	@GET
	@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	public String readDeploymentResult(@PathParam("serviceId") String serviceId) 
	{
		logger.info("Receive a Result read request for service :" + serviceId);
		
		ServiceDeployerThread sdt = DeploymentServiceEndpoint.sdThreads.get(serviceId);
		logger.debug("ServiceDeployer Thread is sd=" + sdt);
		
		JSONObject json = new JSONObject();
		
		try
		{
			if (sdt == null)
			{
				json.put("TYPE", "ERROR");
				json.put("MESSAGE", "No service deployed with id "+serviceId);
			}
			else
			{
				PlacementSolution ps = sdt.readPlacementResult();
				if (ps == null)
				{
					json.put("TYPE", "ERROR");
					json.put("MESSAGE", "Placement Result is NOT available (YET).");				}
				else
				{
					json.put("TYPE", "RESULT");
					String res = "";
					List<Placement> pList = ps.getPlacementList();
					for (Placement placement : pList)
					{
						String ipId = placement.getProvider().getIdentifier();
						res += ipId + ";";
					}
					json.put("IPS", res);
				}
				/*
				logger.debug("Extract Status from sd thread.");
				Status status = sdt.getLatestRootStatus();
				logger.debug("Status returned: " + status);
				
				if(status.isErrorType())
				{
					json.put("TYPE", "ERROR");
					json.put("MESSAGE", ((ErrorStatus)status).getErrorMessage());
				}
				else if(status.isNormalType())
				{
					json.put("TYPE", "PROGRESS");
					json.put("MESSAGE", ((NormalStatus)status).getComponentProgress()+"%");					
				}
				else if (status.isCompletedType())
				{
					json.put("TYPE", "PROGRESS");
					json.put("MESSAGE", "100%");	
				}
				*/
			}
		}
		catch (JSONException e)
		{
			logger.error(e.getMessage());
			e.printStackTrace();
		}
		
		logger.debug(json + " will be returned to the Client.");
		return json.toString();
	}

	@Path("outsource")
	@POST
	//@Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	public String outSourceVMs(@FormParam("ManifestString") String manifestXML,
			@FormParam("Objective") String objective)
	{
		// Default Objective -- COST
		Objective obj = Objective.COST;
		if (objective.equalsIgnoreCase("RISK"))
			obj = Objective.RISK;

		logger.info("OutSourceVMs Service REST Endpoint called. ");
		logger.info("Objective : " + obj);

		try
		{
			SD sd = new SD();
			NegotiationOfferType offer = sd.outSourceVMs(manifestXML, obj);
			if (offer == null)
			{
				logger.error("SORRY, OFFER = NULL.");
				return "NO OFFER";
			}

			logger.debug("Offer returned with offerId: " + offer.getOfferId());
			return offer.toString();
		}
		catch (Exception e)
		{
			logger.error(e.getMessage());
			e.printStackTrace();
			return "NO OFFER";
		}
	}
	
/*	
	@Path("{serviceId}/deployBrokerSolution")
	@POST
	@Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	//public String deployBrokerSolution(@PathParam("serviceId")String serviceId, JAXBElement<PlacementSolution> placementSolution)
	public String deployBrokerSolution(@PathParam("serviceId")String serviceId, PlacementSolution placementSolution)
	{
		logger.info(serviceId+"/deployBrokerSolution/"+" is called..");
		//logger.info("Placement Solution Feasibility: "+placementSolution.isFeasible());
		logger.info(placementSolution.toString());
		DeploymentServiceEndpoint.getBrokerQueue().put(serviceId, placementSolution);;
		return "true";
	}
	*/
}
