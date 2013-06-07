/*
 Copyright (C) 2012-2013  Umeå  University

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
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.log4j.Logger;
import org.ogf.graap.wsag.api.client.AgreementClient;
import org.ogf.schemas.graap.wsAgreement.negotiation.NegotiationOfferType;
import org.w3.x2005.x08.addressing.EndpointReferenceType;

import eu.optimis.DataManagerClient.DataManagerClient;
import eu.optimis.manifest.api.sp.Manifest;
import eu.optimis.manifest.api.sp.VirtualMachineComponent;
import eu.optimis.manifest.api.sp.VirtualMachineDescriptionSection;
import eu.optimis.sd.dao.DeploymentStatusDAO;
import eu.optimis.sd.schemas.Deployment;
import eu.optimis.sd.schemas.ManifestDeployment;
import eu.optimis._do.schemas.Objective;
import eu.optimis._do.schemas.Placement;
import eu.optimis._do.schemas.PlacementSolution;
import eu.optimis._do.schemas.internal.TrecObj;
import eu.optimis.sd.schemas.st.Status;
import eu.optimis.sd.iface.ISD;
import eu.optimis.sd.util.ComponentNames;
import eu.optimis.sd.util.SDConfigurationKeys;
import eu.optimis.sd.util.config.Configuration;
import eu.optimis.sd.util.config.ConfigurationFactory;
import eu.optimis._do.stubs.CloudQoSClient;
import eu.optimis._do.utils.ManifestUtil;
import eu.optimis.ipdiscovery.datamodel.Provider;

//import eu.optimis.types.xmlbeans.servicemanifest.XmlBeanServiceManifestDocument;
import eu.optimis.vc.api.VmcApi;
import eu.optimis.vc.api.Core.ProgressException;
import eu.optimis.vc.api.DataModel.GlobalConfiguration;
import eu.optimis.vc.api.DataModel.ProgressData;
import eu.optimis.service_manager.client.ServiceManagerClient;


public abstract class SDBase implements ISD
{
	protected final static Logger logger = Logger.getLogger(SDBase.class);
	
	private final String configurationFile;

	// This is a standard placement pattern agreed on
	private final String DEFAULT_CONFIGURATIONFILE = "/opt/optimis/etc/sdo/sdo.properties";

	protected DeploymentStatusDAO statusDAO = new DeploymentStatusDAO();
	
	//TODO trick for data upload
	private HashMap<String, String> uploadedPath0Map = new HashMap<String, String>();
	private HashMap<String, String> uploadedPath1Map = new HashMap<String, String>();
	
	private HashMap<String , DataManagerClient > dmcMap = new HashMap<String, DataManagerClient>();
	
	public DeploymentStatusDAO getStatusDAO()
	{ 
		return this.statusDAO;
	}
	
	public List<Status> getDeploymentStatusList()
	{
		return this.statusDAO.getStatusList();
	}
	
	public Status getLatestRootStatus()
	{
		return this.statusDAO.getLatestRootStatus();
	}

	public String getConfigurationFile()
	{
		return this.configurationFile;
	}


	/**
	 * Default constructor
	 */
	public SDBase()
	{
		this.configurationFile = DEFAULT_CONFIGURATIONFILE;
	}

	/**
	 * Constructor providing a path to a configuration file
	 * 
	 * @param configurationFile
	 *            Path to a configuration file
	 */
	public SDBase(String configurationFile)
	{
		this.configurationFile = configurationFile;
	}
	
	/**
	 * ServiceManagerClient factory
	 * 
	 */
	private ServiceManagerClient getSMClient() throws Exception
	{
		Configuration config = ConfigurationFactory.getConfig(configurationFile);
		String smHost = config.getString(SDConfigurationKeys.SERVICE_MANAGER_HOST);
		int smPort = config.getInteger(SDConfigurationKeys.SERVICE_MANAGER_PORT);
		return new ServiceManagerClient(smHost, String.valueOf(smPort));
	}

	/**ServiceManager
	 * Creates a service resource
	 * 
	 * @param serviceId
	 *            The service Id
	 */
	protected void addService(String serviceId,String status,
			String manifestId, String objective) throws Exception
	{
		logger.debug("Creating service resource...");
		ServiceManagerClient client = this.getSMClient();
		
		//TODO the delete function needs to be remove.
		//It's only for integration test.
/*		try
		{
			logger.debug("Deleting service with serviceID: "+serviceId);
			client.deleteService(serviceId);
		}
		catch(Exception e)
		{
			logger.error("Delete failed..maybe it's not there, no worries..");
		}
*/
		//boolean success = client.addService(serviceId);
		logger.debug("Calling ServiceManager addService(" + serviceId + ", " + status
				+ ", " + manifestId + ", " + objective + ");");
		boolean success =  false;
		try
		{
			success = client.addService(serviceId, status, manifestId, objective);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			logger.debug("Error Calling addService, Now check getManifestId operation..");
		}
		if (!success)
		{
			try
			{
				String mId = client.getManifestId(serviceId);
				if (mId != null && mId.equalsIgnoreCase(manifestId))
				{
					logger.debug("Manifest Id Retrieved from SM : " + mId);
					logger.debug("Add Service Call Not Success, but getManifestId Call OK! : Continue!");
				}
				else
				{
					logger.debug("Manifest Id Retrieved from SM : " + mId);
					logger.debug("Add Service Call Not Success! getManifestId Call Not Success!");
					throw new Exception("Creating service resource failed!");
				}
			}
			catch (Exception e)
			{
				e.printStackTrace();
				throw new Exception("Creating service resource failed!");
			}
		}
		else
		{
			logger.debug("Add Service Call: Success!");
		}
		logger.debug("addService call: Done!");
	}
	
	/**ServiceManager*/
	protected void addProvider2Service(Placement placement, String serviceId, String ipId, String ipAddress, String slaId, String agreementEndpoint) throws Exception
	{
		logger.debug("Updating Service, add Infrastructure.");
		logger.debug("Using serviceId=" + serviceId+", Infrastrure Id = " + ipId+", ip address=" + ipAddress);
		logger.debug("Agreement Endpoint : " + agreementEndpoint);
		try
		{
			TrecObj trec = placement.getTREC();
			ServiceManagerClient client = getSMClient();;
			double initialTrustValue = trec.getTrust();
			double initialRiskValue = trec.getRisk() * ManifestUtil.RISK_MAX;
			double initialEcoValue =trec.getEco();
			double initialCostValue = trec.getCost();
			logger.debug("TREC Values set to SM: = " + initialTrustValue + " "+ initialRiskValue + " " + initialEcoValue + " "+ initialCostValue);
			
			client.addInfrastructureProvider(serviceId, ipId, ipAddress, slaId, agreementEndpoint, 
					(float)initialTrustValue , (float)initialRiskValue , (float)initialEcoValue , (float)initialCostValue );
			//client.addInfrastructureProvider(serviceId, ipId, ipAddress, slaId);
		}
		catch (Exception e)
		{
			logger.error("Error when adding Provider to Service: "+e.getMessage());
			e.printStackTrace();
			throw e;
		}
	}	


	/**CloudQoS
	 * Creates a service agreement
	 */
	protected AgreementClient createAgreement(Placement placement) throws Exception
	{
		try
		{
			logger.debug("Creating an agreement, calling CloudQoS.");
			Provider provider = placement.getProvider();

			String cloudqosendpoint = provider.getCloudQosUrl();
			CloudQoSClient qosClient = new CloudQoSClient(cloudqosendpoint);
			NegotiationOfferType counterOffer = placement.getOffer();
			AgreementClient agrment = qosClient.createAgreement(counterOffer);

			if (agrment == null)
			{
				String errMsg = "CloudQoS: Creating agreement FAILED";
				logger.error(errMsg);
				this.statusDAO.addErrorStatus("CloudQoS", "Create agreement", errMsg);
				throw new Exception(errMsg);
			}
			logger.debug("Agreement state:" + agrment.getState());

			return agrment;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			throw new Exception("CloudQoS: Error when creating agreement.");
		}
	}

	/**uploadData
	 * Calls the VM data manager to upload the vm images
	 */
	protected void uploadData(Placement placement, String rootComponentName, String currentRootOperation, int minRootProgress, int maxRootProgress) throws Exception
	{	
		Configuration config = ConfigurationFactory.getConfig(configurationFile);
		Boolean isSVBLegTwo = config.getBoolean(SDConfigurationKeys.IS_SERVICE_BURSTING_CASE_LEG_TWO, false);
		
		//	logger.debug("***********manifest before data upload*********");
		String serviceId = null;
		Provider ip = placement.getProvider();
		String providerName = ip.getName().toLowerCase(); //<- this must match the name in the datamangers ip registry...
		
		DataManagerClient dmc = this.dmcMap.get(providerName);
		
		Manifest manifest = this.parseSPServiceManifest(placement.retrieveManifestCopy());
		VirtualMachineDescriptionSection vmDescSec = manifest.getVirtualMachineDescriptionSection();
		serviceId = vmDescSec.getServiceId();

		String componentName = ComponentNames.DataManager;
		
		
		logger.debug("Using serviceId for DataManger call to Provider:" + providerName + " ServiceId: "+ serviceId);

		String rootDMOperation = "Uploading data to "+ providerName+ " IP: "+ip.getIpAddress();
		
		this.statusDAO.addSubComponentStatus(componentName, rootDMOperation, false, 0);

		// Upload each VM image and its associated ISO's, Progress: <20-100>
		VirtualMachineComponent[] vmCompArray = vmDescSec.getVirtualMachineComponentArray();
		int vmCompNumber = vmCompArray.length;
		
		logger.debug("vmCompNumber: "+vmCompNumber);
		for (int i = 0; i < vmCompNumber; i++)
		{
			int minrProgress = minRootProgress + (maxRootProgress-minRootProgress)*i/vmCompNumber;
			int maxrProgress = minRootProgress + (maxRootProgress-minRootProgress)*(i+1)/vmCompNumber;
			
			int minDMProgress = 20+80*i/vmCompNumber;
			int maxDMProgress = 20+80*(i+1)/vmCompNumber;
			
			VirtualMachineComponent component = vmCompArray[i];
			String componentId = component.getComponentId();
			
			String uploadOperation = "Uploading image & contextualized files for component: "+ componentId+ " to "+ providerName;
			this.statusDAO.addSubComponentStatus(componentName, uploadOperation, false, minDMProgress);
			this.statusDAO.addRootComponentStatus(rootComponentName, currentRootOperation, false, minrProgress);
						
			//Upload the VM image.
			//IMPORTANT
			String image = component.getOVFDefinition().getReferences().getImageFile().getHref();
			String[] paths  = new String[2];
			String uploadKey = image + serviceId + providerName;
			paths[0] = this.uploadedPath0Map.get(uploadKey);
			paths[1] = this.uploadedPath1Map.get(uploadKey);
			logger.debug("Image to be uploaded: " + image);
			if (paths[0] == null || paths[1] == null)
			{
				logger.debug("Image has NOT been uploaded yet, upload!");
				paths = this.uploadVMImage(image, providerName, serviceId,  rootComponentName,  currentRootOperation, minrProgress, maxrProgress,
						componentName, dmc, uploadOperation, minDMProgress,maxDMProgress);
				this.uploadedPath0Map.put(uploadKey, paths[0]);
				this.uploadedPath1Map.put(uploadKey, paths[1]);
			}
			else
			{
				logger.debug("Image has already been uploaded, no need to upload again!");
			}

			//set the path
			String newImagePath = paths[0];

			if (isSVBLegTwo == true)
			{
				newImagePath = image.substring(image.lastIndexOf("/")+1);
				logger.debug("For this Service VM Bursing LEG 2, no image path prefix required, new path :"+newImagePath);
			}

			component.getOVFDefinition().getReferences().getImageFile().setHref(newImagePath);
			logger.debug("Path for component "+componentId+" set to "+newImagePath);
			
			//Upload the contextualization file
			//IMPORTANT
			String[] files = component.getIncarnatedContextualizationFileArray();
			logger.debug("contextualization files for component: "+ componentId + " = " + files.length);
			this.uploadContextualizationImages(files, providerName, serviceId, componentName, dmc);
			
			//set the path, its enough to do it for 1 file
			String oldBaseImageName = component.getOVFDefinition().getReferences().getContextualizationFile().getHref();
			String baseImageFileName = oldBaseImageName.substring(oldBaseImageName.lastIndexOf("/") + 1);
			String remoteDirectory = paths[1];
			String newBaseImageName = remoteDirectory + "/"+ baseImageFileName;
			
			logger.debug("Setting new path for base ISO image (ContextualizationFile) in manifest: " + newBaseImageName);
			component.getOVFDefinition().getReferences().getContextualizationFile().setHref(newBaseImageName);
			
			this.statusDAO.addSubComponentStatus(componentName, uploadOperation, true, maxDMProgress);
			this.statusDAO.addRootComponentStatus(rootComponentName, currentRootOperation, false, maxrProgress);
		}	
		
		//placement.setManifestString(manifest.toString());
		placement.updateOffer(manifest);
		this.statusDAO.addSubComponentStatus(componentName, rootDMOperation, true, 100);
	}
	
	private String[] uploadVMImage(String image, String providerName, String serviceId, 
			String rootComponentName, String currentRootOperation, int minrProgress, int maxrProgress, 
			String componentName, DataManagerClient dmc, String uploadOperation, int minDMProgress, int maxDMProgress) throws Exception
	{
		logger.debug("Uploading Component Image " + image);
		logger.debug("Provider Name = "+providerName+"; dmc==null :"+(dmc==null));
		String remoteDirectory = dmc.uploadVMimageRequest(providerName, serviceId, image);
		logger.debug("dmc.uploadVMimageRequest called.");
		String status;
		while(true)
		{
			 status = dmc.checkUploadStatus(providerName, serviceId, image);
			 logger.debug("Upload Status Retrieved: " + status);
			 
			 if( status.equals("success") || status.equals("failure") )
				 break;
			 
			if (status != null
					&& (status.trim().equalsIgnoreCase("") || 
							status.trim().equalsIgnoreCase("ERROR")))
			{
				this.statusDAO.addErrorStatus(componentName,
						"Uploading Component Image " + image,
						" Failed, the reurned upload status message is '"+ status + "'!");
				logger.error("DataManager Client: Returned upload status message is '"+ status + "'!");
				throw new Exception(
						"DataManager Client: Returned upload status message returned is '"+ status + "'!");
			}
			 
			try
			{
				if (status != null && status.startsWith("progress:"))
				{
					String[] strs = status.split(":");
					double pro = Double.valueOf(strs[1]);
					double dmProgress = minDMProgress + pro *(maxDMProgress - minDMProgress)/100;
					this.statusDAO.addSubComponentStatus(componentName, uploadOperation, false, (int)dmProgress);
					
					double rootProgress = minrProgress + pro*(maxrProgress-minrProgress)/100;
					this.statusDAO.addRootComponentStatus(rootComponentName, currentRootOperation, false, (int)rootProgress);
				}
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
			
			Thread.sleep(2000);
		}
		
		logger.debug("Upload status check loop breaks with status: " + status);
		if (status != null && status.equals("success"))
		{
			logger.debug("image: "+ image +" uploaded sucessfully.");
		}
		else
		{
			this.statusDAO.addErrorStatus(componentName, "Uploading Component Image " + image, " FAILED.");
			throw new Exception("DataManager: Upload Image for Component: "+componentName+":"+image +" FAILED.");
		}
		String newImagePath = remoteDirectory+"/"+image.substring(image.lastIndexOf("/") + 1);
		String[] returnString = new String[2];
		
		returnString[0] = newImagePath;
		returnString[1] = remoteDirectory;
		
		logger.debug("Uploading Component Image " + image+" done!");
		return returnString;
	}
	
	private String uploadContextualizationImages(String[] files, String providerName, String serviceId, String componentName, DataManagerClient dmc) throws Exception
	{	
		int filesNumber = files.length;		
		String status;
		//logger.debug("Number of contextualized files  for "+componentName+" = "+filesNumber);
		String filePath = "";
		
		for (int k = 0; k < filesNumber; k++)
		{
			String file = files[k];
			logger.debug("ISO File  ["+k+"] full path :"+file);
			
			String fileName = file.substring(file.lastIndexOf("/") + 1);
			filePath = file.substring(0, file.lastIndexOf("/"));
			String currentOperation = "DataManager: Uploading iso data to provider:" + providerName + " serviceId:" + serviceId + "path: " + filePath + " iso: " + fileName;
			logger.debug(currentOperation);
			
			dmc.uploadVMimageRequest(providerName, serviceId, file);
			while(true)
			{
				 status = dmc.checkUploadStatus(providerName, serviceId, file);
				 if( status.equals("success") || status.equals("failure") )
				  break;
				 logger.debug("Upload Status: "+status);
				 Thread.sleep(4000);
			}
			if (status != null && status.equals("success"))
			{
				logger.debug("DataManager: file: "+ file +" uploaded sucessfully.");
			}
			else
			{
				this.statusDAO.addErrorStatus(componentName, "Upload Contextualization File", currentOperation+" FAILED");
				throw new Exception("DataManager: Contextualization File:"+file+" Upload failed");
			}					
		}
		
		logger.debug("DataManager: Upload Contextualization Files SUCCESSFUL");
		
		return filePath;
	}
	
	private DataManagerClient getDataManagerClient(String providerName)
	{
		DataManagerClient dmc = this.dmcMap.get(providerName);
		if (dmc == null)
		{
			logger.debug("DMC for " + providerName+ " is null, now creat a new one!");
			dmc = new DataManagerClient();
			this.dmcMap.put(providerName, dmc);
		}
		return dmc;
	}
	
	private Manifest createUserRepoAndWriteKeys(Manifest manifest, String serviceId, String providerName)throws Exception
	{
		String currentOperation = "DataManager: Creating Users Repository in Provider " + providerName;
		logger.debug(currentOperation);
		DataManagerClient dmc = this.getDataManagerClient(providerName);
		
		//Create Users Repository
		String userKey = dmc.createUsersRepository(providerName, serviceId, true);
		logger.debug("DataManager: Got user key: " + userKey + " but ignoring it.");		
		
 		logger.debug("DataManager: Write UserKeys to Manifest..");
		manifest.getServiceProviderExtensionSection().setDataManagerKey(userKey.getBytes());
		return manifest;
	}
	
	protected boolean checkLegal(String serviceId, String manifestXML, String localProvider,
			String targetIP)
	{
		Configuration config = ConfigurationFactory.getConfig(configurationFile);
		
		boolean ignoreCheckLegal = config.getBoolean(SDConfigurationKeys.IS_CHECK_LEGAL_IGNORED, false);
		if (ignoreCheckLegal)
		{
			logger.debug("Check Legal is ignored according to the settings in the sdo config file, just return true.");
			return true;
		}
		
		logger.debug("DataManager: Checking Legal with inputs: serviceId = " + serviceId
				+ ", localProvider = " + localProvider + ", targetIP = "
				+ targetIP);
		DataManagerClient dmc = this.getDataManagerClient(targetIP);
		String res = dmc.checklegal(serviceId, manifestXML, localProvider, targetIP);
		logger.debug("DataManager: Check Legal operation result for IP " + targetIP + ": "+ res);
		
		
		if (res != null
				&& (res.equalsIgnoreCase("LEGAL") || 
						res.equalsIgnoreCase("<xml>LEGAL</xml>")))
			return true;
		return false;
	}
	
	protected  Manifest writeSMandMI(Manifest manifest, Provider provider)
	{
		logger.debug("Setting Service Endpoints per VMComponent...");
		
		VirtualMachineDescriptionSection vmcDescriptionSection = manifest.getVirtualMachineDescriptionSection();
		String serviceId = vmcDescriptionSection.getServiceId();
		
		Configuration config = ConfigurationFactory.getConfig(configurationFile);
		Boolean isSVBLegTwo = config.getBoolean(SDConfigurationKeys.IS_SERVICE_BURSTING_CASE_LEG_TWO, false);
		
		String monitoringEndpoint ="http://"+provider.getIpAddress()+":7070/data";
		logger.debug("ServiceMonitoring Endpoint: "+ monitoringEndpoint);
		
		
		VirtualMachineComponent[] comArray = vmcDescriptionSection.getVirtualMachineComponentArray();
		for (VirtualMachineComponent vmComponent : comArray)
		{
			if (isSVBLegTwo == false)
			{
				logger.debug("Not running in Bursting Case Leg 1 nor Leg_2_2, SM endpoint needs to be in the manfiest..");
				String smHost = config.getString(SDConfigurationKeys.SERVICE_MANAGER_HOST);
				int smPort = config.getInteger(SDConfigurationKeys.SERVICE_MANAGER_PORT);
				String serviceEndpoint = "http://"+smHost+":"+smPort+"/ServiceManager/services/"+serviceId;
				logger.debug("ServiceEndpoint: "+serviceEndpoint);
				
				vmComponent.addNewServiceEndPoint("ServiceManager", serviceEndpoint);
			}
			vmComponent.addNewServiceEndPoint("MonitoringInfrastructure", monitoringEndpoint);
		}
		logger.debug("Setting SM/MI Endpoints per VMComponent...Done!");
		return manifest;
	}

	/**
	 * Calls the VM contextualizer api to contextualize the VM images
	 */
	protected Manifest contextualize(Manifest manifest, Provider provider) throws Exception
	{
		Configuration config = ConfigurationFactory.getConfig(configurationFile);
		
		Boolean isSVBLegTwo = config.getBoolean(SDConfigurationKeys.IS_SERVICE_BURSTING_CASE_LEG_TWO, false);
		
		String vmcconfigfilepath = config.getString(SDConfigurationKeys.VMC_CONFIG_PATH);
		if ((vmcconfigfilepath == null)||(vmcconfigfilepath.length() == 0))
		{
			throw new Exception("vmcconfigfilepath == null. Check SDO config file.");
		}
		
		logger.debug("VMC: Initializing VMC configuration with config file: "+ vmcconfigfilepath);
		GlobalConfiguration globalConfiguration = new GlobalConfiguration(vmcconfigfilepath);
		VmcApi vmcApi = new VmcApi(globalConfiguration);

		if (isSVBLegTwo == true)
		{
			logger.debug("Running in Bursting case Leg 2, so have to convert image:vmdk");
			vmcApi.contextualizeService(manifest, "vmdk");
		}
		else
		{
			vmcApi.contextualizeService(manifest);
		}

		//poll the VMC to get the progress
		int timeout = 500;
		int time = 0;
		String manifestId = manifest.getManifestId();
		while (time++ < timeout)
        {
   		 	// Wait until the service has been registered with the VMC before
            // polling the progress data...
	        try 
	        {
                logger.debug("VMC: Trying to fetch progress data...");
                vmcApi.contextualizeServiceCallback(manifestId);
                break;
	        } 
	        catch (ProgressException e) 
	        {
	        	logger.debug("VMC: Caught ProgressException due to: " + e.getMessage());
                Thread.sleep(250);
	        }
        }
		
		if (time >= timeout)
		{
			throw new Exception("VMC: Timeout when waiting to poll the VMC to get the progress.");
		}

        // Poll the progress data until the completion...
        while (true) 
        {
            // We have progress data, do something with it...
        	ProgressData  progressData = vmcApi.contextualizeServiceCallback(manifestId);
            
            boolean done = progressData.isComplete();
            
            // We have an error so stop everything!
            if (progressData.isError()) 
            {
                // Say what the error is...
                logger.debug(progressData.getException().getMessage(), progressData.getException());
                // report failure
                throw new Exception("VMC: Contextualization failed due to: "+progressData.getException().getMessage());
         	}
            else 
            {      	
            	int completionPercentage = progressData.getTotalProgress().intValue();
            	HashMap<Integer, Double> history = progressData.getHistory();
            	Set<Entry<Integer, Double>> enties = history.entrySet();
            	Object[] arr = enties.toArray();
				for (int i = 0; i < arr.length; i++)
            	//for (Entry<Integer, Double> entry : enties)
				{
            		@SuppressWarnings("unchecked")
					Entry<Integer, Double> entry = (Entry<Integer, Double>)arr[i]; 
					Integer phaseId = entry.getKey();
					String phaseName = progressData.getPhaseName(phaseId);
					Double percent = entry.getValue();
//					logger.debug("VMC Phase: " + phaseName + " = " + percent);
					String pid = provider.getIdentifier();
					phaseName +=" dest: "+pid;
					if (percent < 99.9)
						this.statusDAO.addSubComponentStatus(ComponentNames.VMContextualization, phaseName, false, completionPercentage);
					else
						this.statusDAO.addSubComponentStatus(ComponentNames.VMContextualization, phaseName, true, completionPercentage);
				}
            }

	        // 250ms delay between polling...
	        Thread.sleep(250);
	
	        // Test to see if contextualization has finished...
			if (done)
	        {
				 manifest = progressData.getManifest();
	        	 logger.debug("VMC: Detected contextualization has completed!");
	        	 break;
	        }
		}
        
        return manifest;
	}

	/**
	 * Calls the servicemanifest api to parse a manifest file
	 * 
	 * @param manifestFile
	 *            File containing the manifest
	 */
	protected Manifest parseSPServiceManifest(File manifestFile) 
	{
		String currComponent = ComponentNames.ServiceManifestAPI;
		String currOperation = "Parsing Manifest from File:" + manifestFile;
		try
		{
			logger.debug(currOperation);
			/*
			XmlBeanServiceManifestDocument doc = XmlBeanServiceManifestDocument.Factory.parse(manifestFile);
			Manifest manifest = Manifest.Factory.newInstance(doc);
			*/
			InputStream file = new FileInputStream(manifestFile);
			byte[] b = new byte[file.available()];
			file.read(b);
			file.close();
			String manifestXML = new String(b);
			Manifest manifest = Manifest.Factory.newInstance(manifestXML);
			return manifest;
		}
		catch (Exception e)
		{
			logger.error(currOperation+" FAILED.");
			this.statusDAO.addErrorStatus(currComponent, currOperation, e.getMessage());
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Calls the servicemanifest api to parse a SP manifest in string format
	 * 
	 * @param manifestXML
	 *            Manifest contents as an xml string
	 */
	protected Manifest parseSPServiceManifest(String manifestXML)
	{
		String currComponent = ComponentNames.ServiceManifestAPI;
		String currOperation = "Parsing SP Manifest from String."; 
		try
		{
			logger.debug(currOperation);
			/*
			XmlBeanServiceManifestDocument doc = XmlBeanServiceManifestDocument.Factory.parse(manifestXML);
			Manifest manifest = Manifest.Factory.newInstance(doc);
			*/
			Manifest manifest = Manifest.Factory.newInstance(manifestXML);
			return manifest;
		}
		catch (Exception e)
		{
			logger.error(currOperation+" FAILED.");
			this.statusDAO.addErrorStatus(currComponent, currOperation, e.getMessage());
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Calls the servicemanifest api to parse an IP manifest file
	 * 
	 * @param manifestFile
	 *            File containing the manifest
	 */
	/*
	protected eu.optimis.manifest.api.ip.Manifest parseIPServiceManifest(File manifestFile) 
	{
		String currComponent = ComponentNames.ServiceManifestAPI;
		String currOperation = "Parsing IP Manifest from File:" + manifestFile;
		try
		{
			logger.debug(currOperation);
			XmlBeanServiceManifestDocument doc = XmlBeanServiceManifestDocument.Factory.parse(manifestFile);
			eu.optimis.manifest.api.ip.Manifest manifest = eu.optimis.manifest.api.ip.Manifest.Factory.newInstance(doc);
			return manifest;
		}
		catch (Exception e)
		{
			logger.error(currOperation+" FAILED.");
			this.statusDAO.addErrorStatus(currComponent, currOperation, e.getMessage());
			e.printStackTrace();
			return null;
		}
	}
	*/
	/**
	 * Calls the servicemanifest api to parse an IP manifest in string format
	 * 
	 * @param manifestXML
	 *            Manifest contents as an xml string
	 */
	/*
	protected eu.optimis.manifest.api.ip.Manifest parseIPServiceManifest(String manifestXML)
	{
		String currComponent = ComponentNames.ServiceManifestAPI;
		String currOperation = "Parsing Manifest from String."; 
		try
		{
			logger.debug(currOperation);
			XmlBeanServiceManifestDocument doc = XmlBeanServiceManifestDocument.Factory.parse(manifestXML);
			eu.optimis.manifest.api.ip.Manifest serviceManifest = eu.optimis.manifest.api.ip.Manifest.Factory.newInstance(doc);
			return serviceManifest;
		}
		catch (Exception e)
		{
			logger.error(currOperation+" FAILED.");
			this.statusDAO.addErrorStatus(currComponent, currOperation, e.getMessage());
			e.printStackTrace();
			return null;
		}
	}
	*/

	
	protected Deployment deployPlacementSolution(String serviceId,
			String manifestId, Objective objective,
			PlacementSolution placementSolution, String rootComponentName)
			throws Exception
	{	
		String rootOperation ="Deploy the placement solution";
		this.statusDAO.addRootComponentStatus(rootComponentName, rootOperation, false, 45);
		
		String currentOperation = "Call ServiceManager to add service for service with Id:"+serviceId;
		//STEP 6: add service
		Configuration config = ConfigurationFactory.getConfig(configurationFile);
		
		Boolean isSVBLegOne = config.getBoolean(SDConfigurationKeys.IS_SERVICE_BURSTING_CASE_LEG_ONE, false);
		Boolean isSVBLegTwo = config.getBoolean(SDConfigurationKeys.IS_SERVICE_BURSTING_CASE_LEG_TWO, false);
		Boolean isServiceVMBursting = isSVBLegOne || isSVBLegTwo;
		
		if (isServiceVMBursting)
		{
			logger.debug("SDO is in the Service VM Bursting mode, no need to call ServiceManager.");
		}
		else
		{
			this.statusDAO.addRootComponentStatus(rootComponentName, currentOperation, false, 45);
			String status = "pending";
			this.addService(serviceId, status, manifestId, objective.toString());
			this.statusDAO.addRootComponentStatus(rootComponentName, currentOperation, true, 45);
		}
		
		Deployment deployment = new Deployment(serviceId);
		List<Placement> placementList = placementSolution.getPlacementList();
		
		for (int i = 0; i < placementList.size(); i++)// Progress Bar = < 45 --95 >...
		{
			Placement placement = placementList.get(i);
			//String manifestStr = placement.getManifestString();
			String manifestStr = placement.retrieveManifestCopy();
			Provider provider = placement.getProvider();
			String providerId = provider.getIdentifier();
			
			Manifest currManifest = this.parseSPServiceManifest(manifestStr);
			String subServiceId = currManifest.getVirtualMachineDescriptionSection().getServiceId();	
			
			if (!subServiceId.equals(serviceId))
			{
				throw new Exception("The Service ID in the manifest does not match the provided service ID");
			}

			//STEP 7: deploy a manifest
			currentOperation = "Deploying manifest {" + i + "} for service "+ subServiceId + " to provider " + providerId;
			
			int minRootProgress = 45 + 50 * i/placementList.size();
			int maxRootProgress = 45 + 50 * (i+1)/placementList.size();
			
			this.statusDAO.addRootComponentStatus(rootComponentName, currentOperation, false, minRootProgress);
			//IMPORTANT
			ManifestDeployment manifestDeployment = this.deployPlacement(placement, rootComponentName, currentOperation, minRootProgress, maxRootProgress);
			deployment.getDeploymentList().add(manifestDeployment);
			this.statusDAO.addRootComponentStatus(rootComponentName,currentOperation, true, maxRootProgress);
		}
		
		this.statusDAO.addRootComponentStatus(rootComponentName, rootOperation, true, 100);

		// Do not forget to add a complete status when a deployment is done.
		this.statusDAO.addCompletedStatus("Service " + serviceId+ " successfully deployed.");
		
		return deployment;
	}
	
	
	/**
	 * Deploys a manifest to a provider specified in an offer
	 * 
	 * @param placement
	 *            A placement
	 */
	protected ManifestDeployment deployPlacement(Placement placement, String rootComponentName, String currentRootOperation, int minRootProgress, int maxRootProgress) throws Exception
	{
		String currentOperation = "";
		Manifest manifest = this.parseSPServiceManifest(placement.retrieveManifestCopy());
		VirtualMachineDescriptionSection vmcDescriptionSection = manifest.getVirtualMachineDescriptionSection();
		
		String serviceId = vmcDescriptionSection.getServiceId(); 
		Provider provider = placement.getProvider();
		String providerId = provider.getIdentifier();
		String providerName = provider.getName();
		String providerIpAddress = provider.getIpAddress();
		
		Configuration config = ConfigurationFactory.getConfig(configurationFile);
		Boolean isSVBLegOne = config.getBoolean(SDConfigurationKeys.IS_SERVICE_BURSTING_CASE_LEG_ONE, false);
		
		if (isSVBLegOne)
		{
			logger.debug("SDO is running in the Service VM Bursting mode: LEG 1, no need to call VMC/DataManager.");
		}
		else
		{
			logger.debug("Create User Repository and Write keys to manifest before Contextualization..");
			//Step 0: Create User Repository and Write keys to manifest
			manifest = this.createUserRepoAndWriteKeys(manifest, serviceId, providerName);
		
			//Step : write SM, and MI info.
			manifest = this.writeSMandMI(manifest, provider);
		
			// Step 1: Contextualization
			currentOperation="Contextualizing data for service: "+serviceId+"@"+providerId;
			this.statusDAO.addSubComponentStatus(ComponentNames.VMContextualization, currentOperation, false,0);
			manifest = this.contextualize(manifest, provider);
			//placement.setManifestString(manifest.toString());
			placement.updateOffer(manifest);
			this.statusDAO.addSubComponentStatus(ComponentNames.VMContextualization, currentOperation, true,100);
    		
			// Step 2: Upload data
			this.uploadData(placement, rootComponentName, currentRootOperation, minRootProgress,  maxRootProgress);
		}
		
		// Step 3: Create agreement
		currentOperation="Creating agreement @"+providerId;
		this.statusDAO.addSubComponentStatus(ComponentNames.CloudQoS, currentOperation, false,50);
		//logger.debug(placement.getManifestString());
		AgreementClient agrClient = this.createAgreement(placement);
		this.statusDAO.addSubComponentStatus(ComponentNames.CloudQoS, currentOperation, true,100);
		
		logger.debug("Agreement Created, Agreement ID = "+ agrClient.getAgreementId());

		Boolean isSVBLegTwo = config.getBoolean(SDConfigurationKeys.IS_SERVICE_BURSTING_CASE_LEG_TWO, false);
		Boolean isServiceVMBursting = isSVBLegOne || isSVBLegTwo;
		if (isServiceVMBursting)
		{
			logger.debug("SDO is running in the Service VM Bursting mode, no need to call ServiceManager.");
		}
		else
		{
			// Step 4: Update service resource using Service Manager Client
			currentOperation="Updating service resource for service:"+serviceId+"@"+providerId;

			this.statusDAO.addSubComponentStatus(ComponentNames.ServiceManager, currentOperation, false,50);
			String slaId=agrClient.getAgreementId();
			String agrEndpointStr= agrClient.getEndpoint().xmlText();
			this.addProvider2Service(placement, serviceId, providerId, providerIpAddress, slaId, agrEndpointStr);
			this.statusDAO.addSubComponentStatus(ComponentNames.ServiceManager, currentOperation, true,100);
		}
		
		//manifest = this.parseSPServiceManifest(placement.getManifestString());
		ManifestDeployment manifestDeployment = new ManifestDeployment(manifest.toString(), agrClient.getEndpoint());
		logger.debug("Service Id:" + serviceId + " deployed to IP:" + providerId);
		
		return manifestDeployment;
	}


	
	/**
	 * Terminates agreements and undeploy a service
	 * 
	 * @param serviceId
	 *            ID of the service to undeploy
	 * @param agreementEndpoint
	 *            Endpoint where the service is deployed
	 */
	protected boolean undeployService(String serviceId, EndpointReferenceType agreementEndpoint,  boolean keepData) 
	{
		try
		{
			logger.debug("Undeploy of service with ID: " + serviceId + " started.");
			this.statusDAO.addSubComponentStatus("SD", "Terminating agreement",false, 20);


			logger.debug("Terminating an agreement through endpoint reference..");
			boolean terminated = CloudQoSClient.terminateAgreement(agreementEndpoint);
			if (terminated)
			{
				logger.debug("Agreement terminated for service with ID "+ serviceId);
				this.statusDAO.addSubComponentStatus("SD","Terminating agreement", false, 40);
				logger.debug("Service is undeployed.");
				this.statusDAO.addSubComponentStatus("SD", "Service is undeployed", false, 100);
			}
			else
			{
				logger.error("Agreement is not teminated correctly for service with ID "+ serviceId);
				this.statusDAO.addErrorStatus("SD","Terminating agreement failed", "-1");
				logger.debug("Service undeploy FAILED.");
				this.statusDAO.addErrorStatus("SD", "Service undeploy FAILED.", "-1");
			}

			return terminated;
		}
		catch (Exception e)
		{
			logger.error("AgreementEndpoint termination Exception: "
					+ e.getMessage());
			e.printStackTrace();
			return false;
		}
	}
}
