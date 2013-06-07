/*
 Copyright (C) 2012-2013 Umeå  University

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
package eu.optimis.sd;


import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.ogf.schemas.graap.wsAgreement.negotiation.NegotiationOfferType;
import org.w3.x2005.x08.addressing.EndpointReferenceType;

import eu.optimis._do.DeploymentOptimizer;
import eu.optimis._do.schemas.Objective;
import eu.optimis._do.schemas.Placement;
import eu.optimis._do.schemas.PlacementRequest;
import eu.optimis._do.schemas.PlacementSolution;
import eu.optimis.cloudoptimizer.rest.client.CloudOptimizerRESTClient;
import eu.optimis.sd.schemas.Deployment;
import eu.optimis.sd.stubs.IPDiscoveryCBRStub;
import eu.optimis.sd.stubs.SpTrecDBStub;
import eu.optimis.sd.util.ComponentNames;
import eu.optimis.sd.util.SDConfigurationKeys;
import eu.optimis.sd.util.config.Configuration;
import eu.optimis.sd.util.config.ConfigurationFactory;
import eu.optimis.ipdiscovery.datamodel.*;
import eu.optimis.manifest.api.sp.Manifest;
import eu.optimis.manifestregistry.client.ManifestRegistryClient;

/**
 * @author Wubin.Li (Viali)
 * @author Petter Svärd
 * 
 */
public class SD extends SDBase
{
	protected final static Logger logger = Logger.getLogger(SD.class);
	
	/*
	 * Yes, please don't worry about the following fields,
	 * because in the SDO-GUI project, SD is encapsulated as a multi-thread program.
	 * */
	private String manifestXML;
	private Objective objective;
	private PlacementSolution placementSolution;
	private boolean solutionReadable = false;

	public PlacementSolution readPlacementSolution()
	{
		if (this.isSolutionReadable())
			return this.placementSolution;
		else
			return null;
	}
	
	public boolean isSolutionReadable()
	{
		return this.solutionReadable;
	}
	
	public SD()
	{
		super();
		logger.debug("SD Object created. Using default propertyfile at: " + this.getConfigurationFile());
		this.statusDAO.addRootComponentStatus(ComponentNames.ServiceDeployer, ComponentNames.ServiceDeployer+" Thread Created", true, 5);
	}

	public SD(String cfgFilePath)
	{
		super(cfgFilePath);
		logger.debug("SD Object. Using propertyfile: " + cfgFilePath);
		this.statusDAO.addRootComponentStatus(ComponentNames.ServiceDeployer, ComponentNames.ServiceDeployer+" Thread Created", true, 5);
	}


	public Deployment deploy(File manifestFile, Objective objective)
	{
		Manifest manifest = this.parseSPServiceManifest(manifestFile);
		return this.deploy(manifest, objective);
	}

	public Deployment deploy(String manifestXML, Objective objective)
	{
		Manifest manifest = this.parseSPServiceManifest(manifestXML);
		return this.deploy(manifest, objective);
	}

	//IMPORTANT Entrance
	public Deployment deploy(Manifest manifest, Objective objective)
	{
		try
		{
			logger.debug("Deploy");
			//TODO Extract blacklist from the manifest..
			List<String> excludedIpIds = new ArrayList<String>();
			boolean actualDeployment = true;
			return this.doDeploy(manifest, objective, excludedIpIds, actualDeployment);
		}
		catch (Exception t)
		{
			logger.error(ComponentNames.ServiceDeployer, t);
			logger.debug("No service is deployed. It's dead, Jim!");
			this.statusDAO.addErrorStatus(ComponentNames.ServiceDeployer, "Deploy",t.getMessage());
			return null;
		}
	}

	//IMPORTANT Entrance
	private Deployment doDeploy(Manifest manifest, Objective objective,
			List<String> excludedIpIds, boolean actualDeployment) throws Exception
	{
		Configuration config = ConfigurationFactory.getConfig(this.getConfigurationFile());
		
		logger.debug("Setting flags for LEGAL elements in the manifest!");
		manifest.getDataProtectionSection().getSCC().enableSCC();
		manifest.getDataProtectionSection().getBCR().enableBCR();
		
		this.manifestXML = manifest.toString();
		this.objective = objective;
		
		logger.debug("Starting to deploy services...");

		String serviceId = manifest.getVirtualMachineDescriptionSection().getServiceId();
		logger.debug("ServiceManifest File Parsing Done, ServiceID = "+ serviceId);

		String rootComponentName = ComponentNames.ServiceDeployer;
		String currentOperation = "ServiceManifest File Parsing";
		this.statusDAO.addRootComponentStatus(rootComponentName,currentOperation, true, 10);

		// Step 1: get all available IPs..
		currentOperation = "Retrieveing list of IPs";
		this.statusDAO.addRootComponentStatus(rootComponentName,currentOperation, false, 20);
		IPDiscoveryCBRStub ipdStub = IPDiscoveryCBRStub.getInstance(this.getConfigurationFile());		
		List<Provider> availableIPList = ipdStub.getAvailableIPs();
		this.statusDAO.addRootComponentStatus(rootComponentName,currentOperation, true,  25);
		
		//Step 2: check 
		currentOperation = "IP Filtration + Checking Legal Operation..";
		this.statusDAO.addRootComponentStatus(rootComponentName,currentOperation, false, 27);
		String localProvider = config.getString(SDConfigurationKeys.LOCAL_PROVIDER_NAME);
		localProvider = localProvider.trim();
		for (Provider provider : availableIPList)
		{
			String targetIP = provider.getIdentifier();
			if (this.checkLegal(serviceId, this.manifestXML, localProvider, targetIP))
				continue;
			logger.warn("IP: " + targetIP + " is not LEGAL!");
			excludedIpIds.add(targetIP);
		}
		availableIPList = ipdStub.ipFiltration(availableIPList, excludedIpIds);
		this.statusDAO.addRootComponentStatus(rootComponentName,currentOperation, true, 30);
		
		if (availableIPList.size() == 0)
		{
			this.statusDAO.addErrorStatus(ComponentNames.ServiceDeployer,
					currentOperation,
					"No IP is available after the filtration + legal check operation");
			throw new Exception("No IP is available after the filtration + legal check operation!");
		}

		// Step 3: create a placement request,
		currentOperation = "Creating placement request";
		String trecHost = config.getString(SDConfigurationKeys.TREC_SERVICE_HOST);
		int trecPort = config.getInteger(SDConfigurationKeys.TREC_SERVICE_PORT, 8080);
		PlacementRequest pr = new PlacementRequest(manifest.toString(), availableIPList, objective);
		
		boolean isTRECcallSkipped=config.getBoolean(SDConfigurationKeys.IS_TREC_CALL_SKIPPED, false);
		if(isTRECcallSkipped)
		{
			logger.debug("TREC calls will be skipped.");
			pr.getProperties().put(SDConfigurationKeys.IS_TREC_CALL_SKIPPED,
					Boolean.TRUE.toString());
		}
		
		pr.getProperties().put(SDConfigurationKeys.TREC_SERVICE_HOST, trecHost);
		pr.getProperties().put(SDConfigurationKeys.TREC_SERVICE_PORT, String.valueOf(trecPort));
		
		//Tell DO if the problem size is hug.
		boolean is_big_problem = config.getBoolean(SDConfigurationKeys.IS_BIG_SIZED_PROBLEM, false);
		pr.getProperties().put(SDConfigurationKeys.IS_BIG_SIZED_PROBLEM, String.valueOf(is_big_problem));
		
		this.statusDAO.addRootComponentStatus(rootComponentName, currentOperation, true, 35);

		// Step 4: get placement solution from DO
		currentOperation = "Getting placement solution";
		this.statusDAO.addRootComponentStatus(rootComponentName, currentOperation, false, 40);
		DeploymentOptimizer deploymentOptimizer = new DeploymentOptimizer();
		this.placementSolution = deploymentOptimizer.getPlacementSolution(pr);
		this.statusDAO.addRootComponentStatus(rootComponentName, currentOperation, true,  45);

		if (this.placementSolution == null
				|| !this.placementSolution.isFeasible()) // Solution is not feasible
		{
			this.statusDAO.addErrorStatus(ComponentNames.DeploymentOptimizer, currentOperation, "Failed");
			throw new Exception("Deployment Optimizer can NOT find a feasible solution!");
		}
		
		//IMPORTANT
		this.solutionReadable = true;
		
		//IMPORTANT
		if (actualDeployment == false)
		{
			logger.debug("There is NO actual deployment, but placement solution is returned.");
			return new Deployment(serviceId);
		}
		
		// Step 5: deploy according to solution
		//IMPORTANT
		String manifestId = manifest.getManifestId();
		Deployment result = this.deployPlacementSolution(serviceId, manifestId,
				this.objective, this.placementSolution, rootComponentName);
			
		SpTrecDBStub sptrecDBstb = SpTrecDBStub.getInstance(this.getConfigurationFile());
		boolean success = sptrecDBstb.updateSpTrecDB(serviceId,this.manifestXML, false, "", 0);
		if (success)
		{
			logger.debug("SP TREC DB Updated.");
		}
		else
		{
			logger.error("SP TREC DB Update Failure..");
		}
		
		Boolean isSVBLegOne = config.getBoolean(SDConfigurationKeys.IS_SERVICE_BURSTING_CASE_LEG_ONE, false);
		Boolean isSVBLegTwo = config.getBoolean(SDConfigurationKeys.IS_SERVICE_BURSTING_CASE_LEG_TWO, false);
		Boolean isServiceVMBursting = isSVBLegOne || isSVBLegTwo;
		
		if(!isServiceVMBursting)
		{
			logger.debug("Adding Manifest to the Manifest repository:");
			String manifestServiceUrl = config.getString(
					SDConfigurationKeys.MANIFEST_REPO_URL,
					"http://localhost:9090/manifest-registry/RegistryService");
			logger.debug("manifest repo url = " + manifestServiceUrl);
			ManifestRegistryClient manifestRClient = new ManifestRegistryClient(manifestServiceUrl);
			success = manifestRClient.add(this.manifestXML);
			if (success)
			{
				logger.debug("Manifest added to REPO: Success..");
			}
			else
			{
				logger.error("Manifest added to REPO: Failure..");
			}
		}
		
		return result;
	}

	public boolean undeploy(String serviceId, EndpointReferenceType agreementEndpoint,  boolean keepData)
	{
		return super.undeployService(serviceId, agreementEndpoint,   keepData);
	}

	@Override
	public Provider suggestFederatedProvider(String manifestXML) throws Exception
	{
		//Step 0: IP Discovery
		IPDiscoveryCBRStub ipdStub = IPDiscoveryCBRStub.getInstance(this.getConfigurationFile());
		List<Provider> availableIPList = ipdStub.getAvailableIPs();
		logger.debug("Found "+availableIPList.size() +" IPs in the IP Registry.");
		
		Manifest manifest = this.parseSPServiceManifest(manifestXML);
		String serviceId = manifest.getVirtualMachineDescriptionSection().getServiceId();
		List<Provider> ips = new ArrayList<Provider>();
		
		//Step 1: Check Legal
		Configuration config = ConfigurationFactory.getConfig(this.getConfigurationFile());
		String localProvider = config.getString(SDConfigurationKeys.LOCAL_PROVIDER_NAME);		
		logger.debug("Local Provider Name : " + localProvider);
		
		for (int i = 0; i < availableIPList.size(); i++)
		{
			Provider ip = availableIPList.get(i);
			String federatedIP = ip.getName();
			if (this.checkLegal(serviceId, manifestXML, localProvider, federatedIP))
				ips.add(ip);
		}
		logger.debug("There are "+ ips.size() + " IPs available after Legal Check.");
		
		//Step 2: Retrieve all VMs
		CloudOptimizerRESTClient coRestClient = new CloudOptimizerRESTClient();
		List<String> vmIds = coRestClient.getVMsIdsOfService(serviceId);
		logger.debug("Service "+ serviceId+" has " + vmIds.size() + " running VM instances.");
		
/*		
		Provider result = null;
		int point = 0;
		//Step 3: Choose best IP
		for (Provider provider : ips)
		{
			int ipPoint = 0;
			String ipAddress = provider.getIpAddress();
			for (int k = 0; k < vmIds.size(); k++)
			{
				String vmId = vmIds.get(k);
				String vmIp = coRestClient.getVMPublicIP(vmId);
				int length = vmIp.length() < ipAddress.length() ? vmIp.length() : ipAddress.length();
				int p = 0;
				int endIndex = 0;
				while (vmIp.charAt(endIndex) == ipAddress.charAt(endIndex)
						&& endIndex < length)
				{
					if(vmIp.charAt(endIndex)=='.')
						p++;
					endIndex++;
				}
				if (p == 3) //both =  X.X.X.X
					ipPoint += 2 * p;
				else
					ipPoint += p;
			}
			if (ipPoint > point || result == null)
			{
				point = ipPoint;
				result = provider;
			}
			logger.debug(provider.getName() + " - IP Address matching point : "	+ ipPoint);
		}
		*/
		List<String> vmIPs = new ArrayList<String>();
		for (String vmId : vmIds)
		{
			String vmIP = coRestClient.getVMPublicIP(vmId);
			vmIPs.add(vmIP);
			logger.debug("IP Address for VM: " + vmId + " is " + vmIP);
		}
		
		DeploymentOptimizer dop = new DeploymentOptimizer();
		Provider result = dop.chooseBestIP(ips, vmIPs);
		
		logger.debug("Result returned, suggested IP = " + result);
		return result;
	}

	@Override
	public NegotiationOfferType outSourceVMs(String manifestXML, Objective objective) throws Exception
	{
		logger.debug("outSourceVMs method called..");
		Manifest manifest = this.parseSPServiceManifest(manifestXML);
		List<String> excludedIpIds = new ArrayList<String>();
		boolean actualDeployment = false;
		this.doDeploy(manifest, objective, excludedIpIds, actualDeployment);
		List<Placement> pl = this.readPlacementSolution().getPlacementList();
		if (pl == null)
			throw new Exception("PlacementSolution is NULL!");
		if (pl.size() != 1)
			throw new Exception(
					"PlacementSolution size is not 1. Plz check IPdiscovery, there might be multiple IPs.");
		Placement placement = pl.get(0);
		NegotiationOfferType offer = placement.getOffer();
		return offer;
	}
}
