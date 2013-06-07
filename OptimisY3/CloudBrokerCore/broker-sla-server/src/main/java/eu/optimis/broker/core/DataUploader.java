package eu.optimis.broker.core;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import java.util.Set;
import java.util.Map.Entry;

import org.apache.log4j.Logger;
import org.apache.xmlbeans.XmlException;

import com.sun.jersey.api.client.ClientResponse;

import eu.optimis.ipdiscovery.datamodel.Provider;
import eu.optimis.manifest.api.sp.Manifest;
import eu.optimis.manifest.api.sp.VirtualMachineComponent;
import eu.optimis.manifest.api.sp.VirtualMachineDescriptionSection;
import eu.optimis.service_manager.client.ServiceManagerClient;
import eu.optimis.types.xmlbeans.servicemanifest.XmlBeanServiceManifestDocument;
import eu.optimis.vc.api.VmcApi;
import eu.optimis.vc.api.Core.ProgressException;
import eu.optimis.vc.api.DataModel.GlobalConfiguration;
import eu.optimis.vc.api.DataModel.ProgressData;
import eu.optimis.DataManagerClient.DataManagerClient;
import eu.optimis.broker.client.DMDataSynchClient;
import eu.optimis.broker.core.OutputStub4Demo;
import eu.optimis.broker.sla.BrokerContext;
import eu.optimis.broker.sla.ServiceNegotiationAction;
import eu.optimis.cbr.monitoring.clientlib.Actions;
import eu.optimis.cbr.monitoring.clientlib.BrokerVisualMonitor;
import eu.optimis.cbr.monitoring.clientlib.EndPointType;
import eu.optimis.cbr.monitoring.clientlib.IllegalCallParameter;
import eu.optimis.cbr.monitoring.clientlib.StatusCode;

//import java.io.*;
import java.util.*;


public class DataUploader implements Runnable {
	private static final Logger logger = Logger.getLogger( DataUploader.class );
	HashMap<String, Provider> deploymentSolutions;
	HashMap<String, Provider> deploymentSolutions_newpath;

	HashMap<String, Provider> deploymentSolutions_final;
	
	
	private HashMap<String , DataManagerClient > dmcMap = new HashMap<String, DataManagerClient>();
	//TODO trick for data upload
	private HashMap<String, String> uploadedPath0Map = new HashMap<String, String>();
	private HashMap<String, String> uploadedPath1Map = new HashMap<String, String>();
	
	List<String> filePaths = null;
	
	private String contextID=null;
	
	private double componentProgress=0;
	private double totalProgress=0;
	private int numberOfComponents=0;
	private int componentNo=0;
	
	private String dmUserRepoKey=null;
	
	
	
	
	public DataUploader(HashMap<String, Provider> dsSolutions, String context) {
		// TODO Auto-generated constructor stub
		this.deploymentSolutions = dsSolutions;
		this.contextID = context;
	}


	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		System.out.println("DataUploader Thread: run method");
		OutputStub4Demo.write("\nDataUpload Thread is spawned\n\n");

		
		String manifest = null;
		Provider p = null;
		System.out.println("DataUploader : Solution size" + this.deploymentSolutions.size());
		
		for(Entry<String, Provider> entry : this.deploymentSolutions.entrySet())
		{
		
			manifest = entry.getKey();
			p = entry.getValue();
		}
		
		XmlBeanServiceManifestDocument parsedManifest = null;
		try {
				parsedManifest = XmlBeanServiceManifestDocument.Factory.parse(manifest);
			
			
		} catch (XmlException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Manifest spManifest= Manifest.Factory.newInstance(parsedManifest);
		String serviceid=null;
		if(spManifest != null)
		{
			serviceid = spManifest.getVirtualMachineDescriptionSection().getServiceId();
			OutputStub4Demo.write("\nDataUploader : service :" + serviceid );
		}
		else
		{
			OutputStub4Demo.write("DataUploader: spManifest is null");
		}
		
		try {
			
			BrokerVisualMonitor.APICall(Actions.BFE_SM_19, EndPointType.START, StatusCode.OK, "status-message");
			BrokerVisualMonitor.APICall(Actions.BFE_SM_19, EndPointType.END, StatusCode.OK, "status-message");

			this.addService(serviceid);
			System.out.println("DataUploader: Service added to ServiceManager");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	   
		
		
//		DMDataSynchClient brokerclient = new DMDataSynchClient("217.33.61.85","8080");
		DMDataSynchClient brokerclient = new DMDataSynchClient(this.getParam("broker.dmsynchclient.host"), this.getParam("broker.dmsynchclient.port"));
		
		
		String state = brokerclient.isReady2CreateAgreeement(serviceid);

		OutputStub4Demo.write("\nDataUploader: Waiting for the SP to upload Data ");

	
		try {
			BrokerVisualMonitor.APICall(Actions.SD_DM_20, EndPointType.START, StatusCode.OK, "status-message");
			BrokerVisualMonitor.APICall(Actions.SD_DM_20, EndPointType.END, StatusCode.OK, "status-message");

			BrokerVisualMonitor.APICall(Actions.SD_DM_21, EndPointType.START, StatusCode.OK, "status-message");
			BrokerVisualMonitor.APICall(Actions.SD_DM_21, EndPointType.END, StatusCode.OK, "status-message");

		} catch (IllegalCallParameter e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		
		
		
//		while(state=="-1")
		while(true)
		{
			try {
				OutputStub4Demo.write("\nDMDataSynchClientMain : withing While loop");
				Thread.sleep(100000);
				state = brokerclient.isReady2CreateAgreeement(serviceid);
				OutputStub4Demo.write("\nDMDataSynchClientMain : isReady2CreateAgreeement:" + state);
				if(state.equals("-1")){
					continue;
				}
				else{
					OutputStub4Demo.write("\nDataUploader: Exiting the while(true)");
					break;
				}
				
				
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		OutputStub4Demo.write("\nDataUploader: SP Data upload completed");

		try {
			BrokerVisualMonitor.APICall(Actions.SD_BFE_22, EndPointType.START, StatusCode.OK, "status-message");
			BrokerVisualMonitor.APICall(Actions.SD_BFE_22, EndPointType.END, StatusCode.OK, "status-message");

		} catch (IllegalCallParameter e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
		
		this.setBrokerImagePaths();
		
		this.uploadBrokerData();
		
		try {
			BrokerVisualMonitor.APICall(Actions.BFE_SD_30, EndPointType.START, StatusCode.OK, "status-message");
			BrokerVisualMonitor.APICall(Actions.BFE_SD_30, EndPointType.END, StatusCode.OK, "status-message");

		} catch (IllegalCallParameter e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


		
		ClientResponse resp = brokerclient.Ready2CreateAgreeement(serviceid, "100");
		System.out.println("DMDataSynchClientMain :Ready2CreateAgreeement :" + resp.getStatus());
	
//        BrokerContext.negotiationContext.put( this.contextID, this.deploymentSolutions_newpath );
		BrokerContext.negotiationContext.put( this.contextID, this.deploymentSolutions_final);

		
	}
	
	
	public void uploadBrokerData(){

		this.deploymentSolutions_final = new HashMap<String, Provider>();

		
		this.totalProgress=0;
		String manifest_string =null; 
		Provider provider = null;
		Manifest manifest = null;
		String serviceid = null;
		for (Entry<String, Provider> entry : this.deploymentSolutions_newpath .entrySet()) {
		      System.out.print("DataUploader : uploadBrokerData : Path updated manifest" + entry.getKey() + ": ");
		      System.out.println(entry.getValue());
		      
		      manifest_string =  entry.getKey();
		      provider = entry.getValue();
		      manifest = this.parseSPServiceManifest(manifest_string);
		      
		      
		      if(manifest != null)
			  {
					serviceid = manifest.getVirtualMachineDescriptionSection().getServiceId();
					OutputStub4Demo.write("\nDataUploader : uploadBrokerData :" + serviceid);
				}
				else
				{
					OutputStub4Demo.write("\nDataUploader: uploadBrokerData is null");
				}

		           
		      try {
		    	  
		    	 if(provider.getName().equalsIgnoreCase("umea")){
		    		 
		 			BrokerVisualMonitor.APICall(Actions.BFE_IP1_23, EndPointType.START, StatusCode.OK, "status-message");
					BrokerVisualMonitor.APICall(Actions.BFE_IP1_23, EndPointType.END, StatusCode.OK, "status-message");

		    	 }else if(provider.getName().equalsIgnoreCase("flex")){

		 			BrokerVisualMonitor.APICall(Actions.BFE_IP2_25, EndPointType.START, StatusCode.OK, "status-message");
					BrokerVisualMonitor.APICall(Actions.BFE_IP2_25, EndPointType.END, StatusCode.OK, "status-message");

		    	 }
		    	  
				manifest = this.createUserRepoAndWriteKeys(manifest, serviceid, provider.getName());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		    
			manifest = this.writeSMandMI(manifest, provider);
			
			
			
			try {

				BrokerVisualMonitor.APICall(Actions.BFE_VMC_24, EndPointType.START, StatusCode.OK, "status-message");
				BrokerVisualMonitor.APICall(Actions.BFE_VMC_24, EndPointType.END, StatusCode.OK, "status-message");
				
				OutputStub4Demo.write("\nManifest sent to VMC: "+ manifest);
				manifest = this.contextualize(manifest, provider);
				OutputStub4Demo.write("\nManifest after VMC: "+ manifest);
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		     
			String rootComponentName=null;
			String currentRootOperation=null;
			int minRootProgress=0, maxRootProgress=0;
			try {
				

		    	 if(provider.getName().equalsIgnoreCase("umea")){
		 			BrokerVisualMonitor.APICall(Actions.BFE_IP1_26, EndPointType.START, StatusCode.OK, "status-message");
					BrokerVisualMonitor.APICall(Actions.BFE_IP1_26, EndPointType.END, StatusCode.OK, "status-message");

					BrokerVisualMonitor.APICall(Actions.BFE_IP1_27, EndPointType.START, StatusCode.OK, "status-message");
					BrokerVisualMonitor.APICall(Actions.BFE_IP1_27, EndPointType.END, StatusCode.OK, "status-message");
		    		 

			    	 }else if(provider.getName().equalsIgnoreCase("flex")){

			 			BrokerVisualMonitor.APICall(Actions.BFE_IP2_28, EndPointType.START, StatusCode.OK, "status-message");
						BrokerVisualMonitor.APICall(Actions.BFE_IP2_28, EndPointType.END, StatusCode.OK, "status-message");

						BrokerVisualMonitor.APICall(Actions.BFE_IP2_29, EndPointType.START, StatusCode.OK, "status-message");
						BrokerVisualMonitor.APICall(Actions.BFE_IP2_29, EndPointType.END, StatusCode.OK, "status-message");
			    		 
			    	 }

				
				manifest = this.uploadData(manifest, provider, rootComponentName, currentRootOperation, minRootProgress, maxRootProgress);
				OutputStub4Demo.write("\nDataUploader: Prvoider "+ provider.getName() +"  Manifest after SM+MI+COntext :" + manifest);

				this.deploymentSolutions_final.put(manifest.toString(), provider);

				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}			
			
		} // first for loop


		
	}
	
	
	private void setBrokerImagePaths()
	{
		this.deploymentSolutions_newpath = new HashMap<String, Provider>();
		Manifest manifest = null;
		String serviceid = null;
		String manifest_string=null;
		Provider provider = null;
		for (Entry<String, Provider> entry : this.deploymentSolutions.entrySet()) {
		      System.out.print(entry.getKey() + ": ");
		      System.out.println(entry.getValue());
		      
		      manifest_string =  entry.getKey();
		      provider = entry.getValue();
		      
		      manifest = this.parseSPServiceManifest(manifest_string);
		      
		      
		      if(manifest != null)
			  {
					serviceid = manifest.getVirtualMachineDescriptionSection().getServiceId();
					OutputStub4Demo.write("\nDataUploader : uploadBrokerData :" + serviceid);
					System.out.println("DataUploader : uploadBrokerData :" + serviceid);

			  }
		      else
		      {
		    	  OutputStub4Demo.write("\nDataUploader: uploadBrokerData is null");
		    	  System.out.println("DataUploader: uploadBrokerData is null");

		      }
		      		      
		      this.filePaths = ImageRepository.getFilePaths(serviceid);
		      OutputStub4Demo.write("\nNumber of Image files transfered to broker :" + this.filePaths.size() );


		      
				VirtualMachineComponent[] vmCompArray = manifest.getVirtualMachineDescriptionSection().getVirtualMachineComponentArray();
				int vmCompNumber = vmCompArray.length;
				
				OutputStub4Demo.write("\nvmCompNumber: "+vmCompNumber);
				for (int i = 0; i < vmCompNumber; i++)
				{
					
					VirtualMachineComponent component = vmCompArray[i];
					String componentId = component.getComponentId();
					
					String image = component.getOVFDefinition().getReferences().getImageFile().getHref();
					String manifestfile = this.fileName(image);
					
					
				      ListIterator<String> iter = this.filePaths.listIterator();
				      
				      OutputStub4Demo.write("\nImages for service `" + serviceid + "`:");
				      while( iter.hasNext() )
				      {
				       String filePath = iter.next();
				       String origFilePath = filePath;
				       System.out.println("\t" + filePath);
				       String imageFileName =this.fileName(filePath);
				       
				       if(imageFileName.equalsIgnoreCase(manifestfile)){
				    	   component.getOVFDefinition().getReferences().getImageFile().setHref(origFilePath);
				    	   OutputStub4Demo.write("\nSetBrokerImagePath: OrigFile :" + origFilePath + " File from Manifest :" + manifestfile);
				    	   OutputStub4Demo.write("\nSetBrokerImagePath: "+component.getOVFDefinition().getReferences().getImageFile().getHref());
				    	   System.out.println("SetBrokerImagePath: OrigFile :" + origFilePath + " File from Manifest :" + manifestfile);
				    	   System.out.println("SetBrokerImagePath: "+component.getOVFDefinition().getReferences().getImageFile().getHref());

				       }
				       
				      }

					
				}
		 
				this.deploymentSolutions_newpath.put(manifest.toString(), provider);
				OutputStub4Demo.write("DataUploader : setpath : Manifest after DM paths included in the Manifest :" + manifest);
		}
		      
		      
	}

				
private String fileName(String path)
{
    StringTokenizer st = new StringTokenizer(path, "/");
    String filename = null;
    while(st.hasMoreTokens()) {
    	filename = st.nextToken();
    }
    OutputStub4Demo.write("\nfileName : Tokenized file :"+ filename + "\t");
	return filename;
}
				
	
	/**
	 * ServiceManagerClient factory
	 * 
	 */
	private ServiceManagerClient getSMClient() throws Exception
	{
		//Configuration config = ConfigurationFactory.getConfig(configurationFile);
		//String smHost = config.getString(SDConfigurationKeys.SERVICE_MANAGER_HOST);
		//int smPort = config.getInteger(SDConfigurationKeys.SERVICE_MANAGER_PORT);
		//return new ServiceManagerClient("217.33.61.85", "8080");
		return new ServiceManagerClient(this.getParam("service.manager.host"), this.getParam("service.manager.port"));

	}
	
	/**ServiceManager
	 * Creates a service resource
	 * 
	 * @param serviceId
	 *            The service Id
	 */
	protected void addService(String serviceId) throws Exception
	{
		OutputStub4Demo.write("\nCreating service resource..");
		ServiceManagerClient client = this.getSMClient();
		
		
		//TODO the delete function needs to be remove.
		//It's only for integration test.
		try
		{
			OutputStub4Demo.write("\nAccording to Ilknur's request, delete this service first:)..");
			client.deleteService(serviceId);
		}
		catch(Exception e)
		{
			//logger.error("Delete failed..maybe it's not there, no worries..");
			System.out.println("Delete failed..maybe it's not there, no worries..");
		}

		boolean success = client.addService(serviceId);
		
		if (!success)
		{
			throw new Exception("Creating service resource failed!");
		}
		
	}
	
	
	/**
	 * Calls the servicemanifest api to parse a SP manifest in string format
	 * 
	 * @param smString
	 *            Manifest contents as an xml string
	 */
	protected Manifest parseSPServiceManifest(String smString)
	{
		//String currComponent = ComponentNames.ServiceManifestAPI;
		//String currOperation = "Parsing SP Manifest from String."; 
		try
		{
			//logger.debug(currOperation);
			XmlBeanServiceManifestDocument doc = XmlBeanServiceManifestDocument.Factory.parse(smString);
			Manifest serviceManifest = Manifest.Factory.newInstance(doc);
			return serviceManifest;
		}
		catch (Exception e)
		{
			//logger.error(currOperation+" Failed.");
			//this.statusDAO.addErrorStatus(currComponent, currOperation, e.getMessage());
			e.printStackTrace();
			return null;
		}
	}

	
	private Manifest createUserRepoAndWriteKeys(Manifest manifest, String serviceId, String providerName)throws Exception
	{
		String currentOperation = "Creating Users Repository in Provider " + providerName;
		OutputStub4Demo.write(currentOperation);
		DataManagerClient dmc = this.dmcMap.get(providerName);
		if (dmc == null)
		{
			dmc = new DataManagerClient();
			this.dmcMap.put(providerName, dmc);
		}
		
		//Create Users Repository
		String userKey = dmc.createUsersRepository(providerName, serviceId, true);
		//logger.debug("Got user key: " + userKey + " but ignoring it.");	
	
		//////// Added to create the DM Share space - Start ////////////////////////////
		if(this.dmUserRepoKey == null){
			this.dmUserRepoKey = userKey;
			OutputStub4Demo.write("dmUserRepoKey Initialised for the first time");
			
		}
		else
		{	
			userKey = this.dmUserRepoKey;
			OutputStub4Demo.write("dmUserRepoKey using the same key");
			
		}	
		////////Added to create the DM Share space - End ////////////////////////////
		
		
		OutputStub4Demo.write("\nWrite UserKeys to Manifest..");
		manifest.getServiceProviderExtensionSection().setDataManagerKey(userKey.getBytes());
		return manifest;
	}

	protected  Manifest writeSMandMI(Manifest manifest, Provider provider)
	{
		OutputStub4Demo.write("\nSetting Service Endpoints per VMComponent...");
		
		VirtualMachineDescriptionSection vmcDescriptionSection = manifest.getVirtualMachineDescriptionSection();
		String serviceId = vmcDescriptionSection.getServiceId();
		
		//Configuration config = ConfigurationFactory.getConfig(configurationFile);
		
		String monitoringEndpoint ="http://"+provider.getIpAddress()+":7070/data";
		OutputStub4Demo.write("ServiceMonitoring Endpoint: "+ monitoringEndpoint);
		
		
		VirtualMachineComponent[] comArray = vmcDescriptionSection.getVirtualMachineComponentArray();
		for (VirtualMachineComponent vmComponent : comArray)
		{
			OutputStub4Demo.write("\nNot running in Bursting Case Leg 1 nor Leg_2_2, SM endpoint needs to be in the manfiest..");
			//String smHost = config.getString(SDConfigurationKeys.SERVICE_MANAGER_HOST);
			//String smHost = "217.33.61.85";
			String smHost = this.getParam("service.manager.host");

			
			//int smPort = config.getInteger(SDConfigurationKeys.SERVICE_MANAGER_PORT);
			//int smPort = 8080;
			String smPort = this.getParam("service.manager.port");
			
			
			String serviceEndpoint = "http://"+smHost+":"+smPort+"/ServiceManager/services/"+serviceId;
			//logger.debug("ServiceEndpoint: "+serviceEndpoint);
			OutputStub4Demo.write("ServiceManager Endpoint: "+ serviceEndpoint);

			
			vmComponent.addNewServiceEndPoint("ServiceManager", serviceEndpoint);

			vmComponent.addNewServiceEndPoint("MonitoringInfrastructure", monitoringEndpoint);
		}
		//logger.debug("Setting SM/MI Endpoints per VMComponent...Done!");
		return manifest;
	}

	/**
	 * Calls the VM contextualizer api to contextualize the VM images
	 */
	protected Manifest contextualize(Manifest manifest, Provider provider) throws Exception
	{
		//Configuration config = ConfigurationFactory.getConfig(configurationFile);
		
		//String vmcconfigfilepath = config.getString(SDConfigurationKeys.VMC_CONFIG_PATH);
		//String vmcconfigfilepath = "/opt/optimis/vmc/bt/";
		
		
		
		String vmcconfigfilepath = this.getParam("vmc.config.file.path");
		
		if ((vmcconfigfilepath == null)||(vmcconfigfilepath.length() == 0))
		{
			throw new Exception("vmcconfigfilepath == null. Check SDO config file.");
		}
		
		OutputStub4Demo.write("\nInitializing VMC configuration with config file: "+ vmcconfigfilepath);
		GlobalConfiguration globalConfiguration = new GlobalConfiguration(vmcconfigfilepath);
		VmcApi vmcApi = new VmcApi(globalConfiguration);

		//if(provider.getName().equalsIgnoreCase("arsys") || provider.getName().equalsIgnoreCase("amazon")){  //Added for non optimis
		if(provider.getProviderType().equalsIgnoreCase("non-optimis")){  //Added for non optimis
		
			OutputStub4Demo.write("\nContextualization for non-optimis...");
			vmcApi.contextualizeService(manifest,"vmdk");		   //Added for non optimis
		}else												//Added for non optimis
		    vmcApi.contextualizeService(manifest);

		//poll the VMC to get the progress
		int timeout = 500;
		
		int time = 0;
		String manifestId = manifest.getManifestId();
		//String manifestId = manifest.getVirtualMachineDescriptionSection().getServiceId();

    	OutputStub4Demo.write("\nTrying to fetch progress data...");

		while (time++ < timeout)
        {
   		 	// Wait until the service has been registered with the VMC before
            // polling the progress data...
	        try 
	        {
	        	//OutputStub4Demo.write("\nTrying to fetch progress data...");
                vmcApi.contextualizeServiceCallback(manifestId);
                break;
	        } 
	        catch (ProgressException e) 
	        {
	        	//OutputStub4Demo.write("\nCaught ProgressException due to: " + e.getMessage());
                Thread.sleep(250);
	        }
        }
		
		if (time >= timeout)
		{
			throw new Exception("Timeout when waiting to poll the VMC to get the progress.");
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
                throw new Exception("Contextualization failed due to: "+progressData.getException().getMessage());
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
					OutputStub4Demo.write("\nVMC Phase: " + phaseName + " = " + percent);
					String pid = provider.getIdentifier();
					phaseName +=" dest: "+pid;
					/*
					if (percent < 99.9)
						this.statusDAO.addSubComponentStatus(ComponentNames.VMContextualization, phaseName, false, completionPercentage);
					else
						this.statusDAO.addSubComponentStatus(ComponentNames.VMContextualization, phaseName, true, completionPercentage);
					*/
					if (percent < 99.9)
						OutputStub4Demo.write("VMC :" + phaseName +" "+ false +" "+ completionPercentage);
					else
						OutputStub4Demo.write("VMC :" + phaseName +" "+ true +" "+ completionPercentage);
						
				}
            }

	        // 250ms delay between polling...
	        Thread.sleep(250);
	
	        // Test to see if contextualization has finished...
			if (done)
	        {
				 manifest = progressData.getManifest();
				 OutputStub4Demo.write("\nDetected contextualization has completed!");
	        	 break;
	        }
		}
        
        return manifest;
	}

	
	/**uploadData
	 * Calls the VM data manager to upload the vm images
	 */
//	protected void uploadData(Placement placement, String rootComponentName, String currentRootOperation, int minRootProgress, int maxRootProgress) throws Exception
	protected Manifest uploadData(Manifest manifest, Provider provider, String rootComponentName, String currentRootOperation, int minRootProgress, int maxRootProgress) throws Exception
	{	
		
		OutputStub4Demo.write("\n***********manifest before data upload*********");


		String serviceId = null;
		//Provider ip = placement.getProvider();
		Provider ip = provider;
		String providerName = ip.getName().toLowerCase(); //<- this must match the name in the datamangers ip registry...
		
		DataManagerClient dmc = this.dmcMap.get(providerName);
		
//		Manifest manifest = this.parseSPServiceManifest(placement.retrieveManifestCopy());
		//Manifest manifest = this.parseSPServiceManifest(key);
		VirtualMachineDescriptionSection vmDescSec = manifest.getVirtualMachineDescriptionSection();
		serviceId = vmDescSec.getServiceId();

		//String componentName = ComponentNames.DataManager;
		String componentName = "DataManager";
		
		
		OutputStub4Demo.write("\nUsing serviceId for DataManger call to Provider:" + providerName + " ServiceId: "+ serviceId);

		String rootDMOperation = "Uploading data to "+ providerName+ " IP: "+ip.getIpAddress();
		
		//this.statusDAO.addSubComponentStatus(componentName, rootDMOperation, false, 0);

		// Upload each VM image and its associated ISO's, Progress: <20-100>
		VirtualMachineComponent[] vmCompArray = vmDescSec.getVirtualMachineComponentArray();
		int vmCompNumber = vmCompArray.length;
		
		this.numberOfComponents = vmCompArray.length;
		this.componentNo=0;
		
		
		OutputStub4Demo.write("\nNumber of vmComponents: "+this.numberOfComponents);
		for (int i = 0; i < vmCompNumber; i++)
		{
			this.componentNo=i;
		    this.componentProgress = 0;	
			int minrProgress = minRootProgress + (maxRootProgress-minRootProgress)*i/vmCompNumber;
			int maxrProgress = minRootProgress + (maxRootProgress-minRootProgress)*(i+1)/vmCompNumber;
			
			int minDMProgress = 20+80*i/vmCompNumber;
			int maxDMProgress = 20+80*(i+1)/vmCompNumber;
			
			VirtualMachineComponent component = vmCompArray[i];
			String componentId = component.getComponentId();
			
			String uploadOperation = "Uploading image & contextualized files for component: "+ componentId+ " to "+ providerName;
			//this.statusDAO.addSubComponentStatus(componentName, uploadOperation, false, minDMProgress);
			//this.statusDAO.addRootComponentStatus(rootComponentName, currentRootOperation, false, minrProgress);
						
			//Upload the VM image.
			//IMPORTANT
			String image = component.getOVFDefinition().getReferences().getImageFile().getHref();
			String[] paths  = new String[2];
			String uploadKey = image+serviceId+providerName;
			paths[0] = this.uploadedPath0Map.get(uploadKey);
			paths[1] = this.uploadedPath1Map.get(uploadKey);
			if (paths[0] == null || paths[1] == null)
			{
				OutputStub4Demo.write("\n***Image has NOT been uploaded yet, so upload!***");
				paths = this.uploadVMImage(image, providerName, serviceId,  rootComponentName,  currentRootOperation, minrProgress, maxrProgress,
						componentName, dmc, uploadOperation, minDMProgress,maxDMProgress);
				this.uploadedPath0Map.put(uploadKey, paths[0]);
				this.uploadedPath1Map.put(uploadKey, paths[1]);
			}
			else
			{
				OutputStub4Demo.write("\n***Image has already been uploaded, no need to upload again!***");
			}

			//set the path
			String newImagePath = paths[0];

			/////////////////// //Added for non optimis  ///////////////////////////////
			//if (provider.getName().equalsIgnoreCase("arsys") || provider.getName().equalsIgnoreCase("amazon") )     
			if (provider.getProviderType().equalsIgnoreCase("non-optimis") )     

			{
				newImagePath = image.substring(image.lastIndexOf("/")+1);
				OutputStub4Demo.write("For this Service VM non-optimis, no image path prefix required, new path :"+newImagePath);
			}
			///////////////////////Added for non optimis//////////////////////////////////

			
			component.getOVFDefinition().getReferences().getImageFile().setHref(newImagePath);
			OutputStub4Demo.write("\nNew Path for component "+componentId+" now set to "+newImagePath);
			
//			Configuration config = ConfigurationFactory.getConfig(configurationFile);
			Boolean isBrokerCase = false;// config.getBoolean(SDConfigurationKeys.IS_BROKERAGE_CASE, false);
			if(!isBrokerCase)
			{
				//Upload the contextualization file
				//IMPORTANT
				String[] files = component.getIncarnatedContextualizationFileArray();
				OutputStub4Demo.write("\ncontextualization files for component: "+ componentId + " = " + files.length);
				this.uploadContextualizationImages(files, providerName, serviceId, componentName, dmc);
			
				//set the path, its enough to do it for 1 file
				String oldBaseImageName = component.getOVFDefinition().getReferences().getContextualizationFile().getHref();
				String baseImageFileName = oldBaseImageName.substring(oldBaseImageName.lastIndexOf("/") + 1);
				String remoteDirectory = paths[1];
				String newBaseImageName = remoteDirectory + "/"+ baseImageFileName;
			
				OutputStub4Demo.write("\nSetting new path for base ISO image (ContextualizationFile) in manifest: " + newBaseImageName);
				component.getOVFDefinition().getReferences().getContextualizationFile().setHref(newBaseImageName);
			}
			//this.statusDAO.addSubComponentStatus(componentName, uploadOperation, true, maxDMProgress);
			//this.statusDAO.addRootComponentStatus(rootComponentName, currentRootOperation, false, maxrProgress);
			
//			int percent = (i+1)*100/vmCompNumber;
//			ClientResponse resp = brokerclient.Ready2CreateAgreeement(serviceId, Integer.toString(percent));
//			OutputStub4Demo.write("\nPercentage of upload :"+percent);
			
			this.totalProgress = this.totalProgress + this.componentProgress;
			if(this.totalProgress >= 100.0)
				this.totalProgress= 99.00;
			

		}	
		
		//placement.setManifestString(manifest.toString());
		//this.updateOffer(manifest);
		//this.statusDAO.addSubComponentStatus(componentName, rootDMOperation, true, 100);
		return manifest;
	}
	
	
	private String[] uploadVMImage(String image, String providerName, String serviceId, 
			String rootComponentName, String currentRootOperation, int minrProgress, int maxrProgress, 
			String componentName, DataManagerClient dmc, String uploadOperation, int minDMProgress, int maxDMProgress) throws Exception
	{
		DMDataSynchClient brokerclient = new DMDataSynchClient(this.getParam("broker.dmsynchclient.host"), this.getParam("broker.dmsynchclient.port"));
		
		OutputStub4Demo.write("\nUploading Component Image " + image);
		OutputStub4Demo.write("\nProvider Name = "+providerName+"; dmc==null :"+(dmc==null));
		String remoteDirectory = dmc.uploadVMimageRequest(providerName, serviceId, image);


		OutputStub4Demo.write("\ndmc.uploadVMimageRequest called.");
		String status;
		while(true)
		{
			 status = dmc.checkUploadStatus(providerName, serviceId, image);
			 if( status.equals("success") || status.equals("failure") )
				 break;
			 OutputStub4Demo.write("\nUpload Status: "+status);
			 
			try
			{
				if (status != null && status.startsWith("progress:"))
				{
					String[] strs = status.split(":");
					double pro = Double.valueOf(strs[1]);
					double dmProgress = minDMProgress + pro *(maxDMProgress - minDMProgress)/100;
					//this.statusDAO.addSubComponentStatus(componentName, uploadOperation, false, (int)dmProgress);
					
					double rootProgress = minrProgress + pro*(maxrProgress-minrProgress)/100;
					//this.statusDAO.addRootComponentStatus(rootComponentName, currentRootOperation, false, (int)rootProgress);
					
			
					//Broker Data uploadPercentage
//					this.totalProgress = this.totalProgress +  (this.componentNo * (1.0/(this.numberOfComponents * this.deploymentSolutions_newpath.size())) 
//							+ pro * (1.0/(this.numberOfComponents * this.deploymentSolutions_newpath.size() ) ));

					this.componentProgress = (this.componentNo * (1.0/(this.numberOfComponents * this.deploymentSolutions_newpath.size())) 
							+ pro * (1.0/(this.numberOfComponents * this.deploymentSolutions_newpath.size() ) ));

					

					OutputStub4Demo.write("\nBroker Upload Progress: TotalProgress " +this.totalProgress + 
							"ComonentProgress"+ this.componentProgress+
							"  Component Number :"+ this.componentNo + 
							" Number of Components : " + this.numberOfComponents +
							" No of Deplyment solution :" + this.deploymentSolutions_newpath.size() +
							"DM Progres : "+pro );
					
					ClientResponse resp = null;
					if((this.totalProgress + this.componentProgress)>= 100.0)
						resp = brokerclient.Ready2CreateAgreeement(serviceId, Double.toString(99.00));
					else
						resp = brokerclient.Ready2CreateAgreeement(serviceId, Double.toString(this.totalProgress + this.componentProgress));


					
					
				}
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
			
			Thread.sleep(2000);
		}
		if (status != null && status.equals("success"))
		{
			OutputStub4Demo.write("\nimage: "+ image +" uploaded sucessfully.");
		}
		else
		{
			//this.statusDAO.addErrorStatus(componentName, "Uploading Component Image " + image, " Failed.");
			throw new Exception("DataManager: Upload Image for Component: "+componentName+":"+image +" failed");
		}
		String newImagePath = remoteDirectory+"/"+image.substring(image.lastIndexOf("/") + 1);
		String[] returnString = new String[2];
		
		returnString[0] = newImagePath;
		returnString[1] = remoteDirectory;
		
		OutputStub4Demo.write("\nUploading Component Image " + image+" done!");
		return returnString;
	}

	
	private String uploadContextualizationImages(String[] files, String providerName, String serviceId, String componentName, DataManagerClient dmc) throws Exception
	{	

		int filesNumber = files.length;		
		String status;
		OutputStub4Demo.write("\nNumber of contextualized files  for "+componentName+" = "+filesNumber);
		String filePath = "";
		
		for (int k = 0; k < filesNumber; k++)
		{
			String file = files[k];
			OutputStub4Demo.write("\nISO File  ["+k+"] full path :"+file);
			
			String fileName = file.substring(file.lastIndexOf("/") + 1);
			filePath = file.substring(0, file.lastIndexOf("/"));
			String currentOperation = "Uploading iso data to provider:" + providerName + " serviceId:" + serviceId + "path: " + filePath + " iso: " + fileName;
			OutputStub4Demo.write(currentOperation);
			
			dmc.uploadVMimageRequest(providerName, serviceId, file);

		
			while(true)
			{
				 status = dmc.checkUploadStatus(providerName, serviceId, file);
				 if( status.equals("success") || status.equals("failure") )
				  break;
				 OutputStub4Demo.write("\nUpload Status: "+status);
				 Thread.sleep(4000);
			}
			if (status != null && status.equals("success"))
			{
				OutputStub4Demo.write("\nfile: "+ file +" uploaded sucessfully.");
			}
			else
			{
				//this.statusDAO.addErrorStatus(componentName, "Upload Contextualization File", currentOperation+" Failed");
				throw new Exception("DataManager: Contextualization File:"+file+" Upload failed");
			}					
		}
		
		OutputStub4Demo.write("\nUpload Contextualization Files succeeded");
		
		return filePath;
	}

	public String getParam(String param){
		String path=null;
	    Properties properties = new Properties();
		try {
		    InputStream is = getClass().getClassLoader().getResourceAsStream("BrokerProperties");
		    properties.load(is);
		    //properties.load(new FileInputStream("src/main/resources/BrokerClientProperties"));
		    path = properties.getProperty(param);
		    OutputStub4Demo.write("DataUploader : getParam :BrokerProperties:"+param+"=" + path);
		    
		} catch (IOException e) {
			System.out.println("File Read Exception");
		}
		return path;
	}
	
}
