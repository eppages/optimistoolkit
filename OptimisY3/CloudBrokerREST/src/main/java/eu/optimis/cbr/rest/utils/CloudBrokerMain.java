package eu.optimis.cbr.rest.utils;


import eu.optimis._do.schemas.Placement;
import eu.optimis._do.schemas.PlacementRequest;
import eu.optimis._do.schemas.Objective;
import eu.optimis._do.schemas.PlacementSolution;
import eu.optimis._do.DeploymentOptimizer;
import eu.optimis.cbr.monitoring.clientlib.Actions;
import eu.optimis.cbr.monitoring.clientlib.BrokerVisualMonitor;
import eu.optimis.cbr.monitoring.clientlib.EndPointType;
import eu.optimis.cbr.monitoring.clientlib.IllegalCallParameter;
import eu.optimis.cbr.monitoring.clientlib.StatusCode;
import eu.optimis.ds.client.DeploymentServiceClient;
import eu.optimis.types.xmlbeans.servicemanifest.XmlBeanServiceManifestDocument;
import eu.optimis.tf.clients.TrustFrameworkSPClient;
import eu.optimis._do.stubs.TRECClient;
import eu.optimis.ipdiscovery.datamodel.Provider;
import eu.optimis.manifest.api.sp.TRECSection;
import eu.optimis.manifest.api.sp.TrustSection;
import eu.optimis.manifest.api.sp.Manifest;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Iterator;
import org.apache.xmlbeans.XmlException;



public class CloudBrokerMain implements Runnable {
	
	ServiceRequest servicerequest;
	
	public CloudBrokerMain(ServiceRequest serreq){
		this.servicerequest = serreq;
	}
	
	public void run(){
		
		//System.out.println("CloudBrokerMain : run");
		String demoOutput = "";
		OutputStub4Demo.write("Request received at Cloud Broker\n\n");
		
		/* Since CBR has been called, it means steps 0 and 1 of sequence has been completed.
		 * Let us draw them at CBR side so that the SP doesn't need to change its code
		 */ 
		try {
			BrokerVisualMonitor.APICall(Actions.SP_SD_0, EndPointType.START, StatusCode.OK, "deploy(manifest,objective)");
			BrokerVisualMonitor.APICall(Actions.SP_SD_0, EndPointType.END, StatusCode.OK, "deploy(manifest,objective)");
			
			BrokerVisualMonitor.APICall(Actions.SD_BFE_1, EndPointType.START, StatusCode.OK, "getDeploymentDetails(serviceManifest,objective)");
			BrokerVisualMonitor.APICall(Actions.SD_BFE_1, EndPointType.END, StatusCode.OK, "getDeploymentDetails(serviceManifest,objective)");
		} catch (IllegalCallParameter e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			Thread.sleep(10);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		XmlBeanServiceManifestDocument parsedManifest = null;
		try {
			parsedManifest = XmlBeanServiceManifestDocument.Factory.parse(this.servicerequest.getManifest());
		} catch (XmlException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
		Manifest spManifest= Manifest.Factory.newInstance(parsedManifest);
		System.out.println("ServiceID :" + spManifest.getVirtualMachineDescriptionSection().getServiceId());
		//System.out.println("Manifest VM serviceID : " + spManifest.getVirtualMachineDescriptionSection().getServiceId());
		//System.out.println("Manifest VM Affinity Rule : " + spManifest.getVirtualMachineDescriptionSection().getAffinityRule(0).toString());
		//System.out.println("Manifest : " + manifest);
		
		
		
		String serviceID = spManifest.getVirtualMachineDescriptionSection().getServiceId();
		
		
		try {
			BrokerVisualMonitor.APICall(Actions.BFE_REG_2, EndPointType.START, StatusCode.OK, "getIPList()");
		} catch (IllegalCallParameter e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		IPRegistry ipreg = IPRegistry.getSingletonObject();
		try {
			BrokerVisualMonitor.APICall(Actions.BFE_REG_2, EndPointType.END, StatusCode.OK, "getIPList()");
		} catch (IllegalCallParameter e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		List<Provider> availableIPList = ipreg.getIPInfoList().getIPList();
		
		try {
			BrokerVisualMonitor.APICall(Actions.REG_BFE_3, EndPointType.START, StatusCode.OK, "IP List");
		} catch (IllegalCallParameter e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			
		try {
			BrokerVisualMonitor.APICall(Actions.REG_BFE_3, EndPointType.END, StatusCode.OK, "IP List Returned");
		} catch (IllegalCallParameter e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		/* Code to filter out IPs with trust level < min trust required */
		//List<Provider> IPlistForDO = availableIPList;
		
		List<Provider> IPlistForDO = new ArrayList(availableIPList.size());
		for(Provider item: availableIPList) IPlistForDO.add(item);
		
				
		System.out.println("Before deletion ->IPlistForDO Size :"+ IPlistForDO.size() + "  availableIPList Size :" + availableIPList.size());
		for (int k=0; k<IPlistForDO.size(); k++) {
			System.out.println("IP : " + IPlistForDO.get(k).getIdentifier());
		}
			
		int i = 0;
		double minTrust = Double.POSITIVE_INFINITY;

		TRECSection trec = spManifest.getTRECSection();
		TrustSection[] trecArray = trec.getTrustSectionArray();
		for (TrustSection trustSection : trecArray)
		{
			int t = trustSection.getMinimumTrustLevel();
			if (t < minTrust)
				minTrust = t;
		} 

		demoOutput = "Minimum Trust allowed for any component in the service: "+ minTrust;
		OutputStub4Demo.write(demoOutput+"\n\n");
		
		demoOutput = "The trust level associated with IPs:";
		for (Iterator itr = IPlistForDO.iterator();itr.hasNext();) {
			Object ob = itr.next();
			TrustFrameworkSPClient tfClient = new TrustFrameworkSPClient(this.getTRECHost("trec.service.host"), Integer.parseInt(this.getTRECPort("trec.service.port")));
			String IPid = IPlistForDO.get(i).getIdentifier();
			String trustval = tfClient.getDeploymentTrust(IPid);
			//System.out.println("Deployment Trust :"+ trustval);
			demoOutput += "\n\t\t IP: " + IPid +" \t Trust: " + trustval;

			if(Double.parseDouble(trustval) < minTrust){
				itr.remove();
				demoOutput += "\t (removed from possible IP candidate list, low trust)";
			}
			else {
				i++; // increment only if the element has not been deleted
			}
		}

		System.out.println("After deletion ->IPlistForDO Size :"+ IPlistForDO.size() + "  availableIPList Size :" + availableIPList.size());
		for (int k=0; k<IPlistForDO.size(); k++) {
			System.out.println("IP : " + IPlistForDO.get(k).getIdentifier());
		}

		
		OutputStub4Demo.write(demoOutput+"\n\n");
		
		demoOutput = "The list of IPs to be passed for further analysis:";
		
		for(Provider providerlistforDO: IPlistForDO){
			//System.out.println(" Provider Name : " +providerlistforDO.getName());
			demoOutput += "\n\t\t IP: "+providerlistforDO.getName();
		}
		OutputStub4Demo.write(demoOutput+"\n\n"+"Hold on. Starting analysis...\n\n");
		
		Objective objective = Objective.valueOf(servicerequest.getObjective());
		
		try {
			this.receivePlacementRequest(serviceID, servicerequest.getManifest(), IPlistForDO , objective);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	
	private void receivePlacementRequest(String serviceID, String serviceManifest, List<Provider> IPList, Objective obj) throws IOException{
	
		System.out.println("CloudBrokerMain: receivePlacementRequest");
		
		
		try {
			BrokerVisualMonitor.APICall(Actions.BFE_DO_4, EndPointType.START, StatusCode.OK, "PlacementRequest()");
		} catch (IllegalCallParameter e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

		PlacementRequest pr = new PlacementRequest(serviceManifest, IPList, obj); 
		//pr.setIsBrokerage();


	
	
		
		try {
			BrokerVisualMonitor.APICall(Actions.DO_BFE_5, EndPointType.START, StatusCode.OK, "PlacementRequest Object Returned");
			BrokerVisualMonitor.APICall(Actions.DO_BFE_5, EndPointType.END, StatusCode.OK, "PlacementRequest Object Returned");
		} catch (IllegalCallParameter e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

		//pr.getProperties().put("trec.service.host", "200.0.01");
		//pr.getProperties().put("trec.service.port", "8080");

		pr.getProperties().put("trec.service.host", this.getTRECHost("trec.service.host"));
		pr.getProperties().put("trec.service.port", this.getTRECPort("trec.service.port"));
		
		DeploymentOptimizer dop = new DeploymentOptimizer();

		
		try {
			BrokerVisualMonitor.APICall(Actions.BFE_DO_6, EndPointType.START, StatusCode.OK, "getPlacementSolution()");
		} catch (IllegalCallParameter e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		PlacementSolution ps = dop.getPlacementSolution(pr);
		
		List<Placement> providerList = ps.getPlacementList();

		
		try {
			BrokerVisualMonitor.APICall(Actions.DO_BFE_15, EndPointType.END, StatusCode.OK, "List of Providers for Components");
		} catch (IllegalCallParameter e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

		
	
		for(Placement pclist: providerList){
			
			System.out.println(" Provider Name : " +pclist.getProvider().getName());
			System.out.println("Provider Type : " + pclist.getProvider().getProviderType());
			System.out.println("Provider " + pclist.getProvider().getIdentifier());
		}
	
		String host = getHost("host");
		int port = getPort("port");
		System.out.println("Host :"+ host + " Port :" + port);
		DeploymentServiceClient dsc = new DeploymentServiceClient(host, port);
		
		//System.out.println("SD. SP called from the broker");
		OutputStub4Demo.write("\n Sending the placement solution back to SP...\n\nThe Cloud Broker's job is done!\n\n");
		
		//Boolean status = dsc.deployBorkerSolution(serviceID, ps);
		
		try {
			BrokerVisualMonitor.APICall(Actions.BFE_SD_16, EndPointType.START, StatusCode.OK, "List of Providers for Components given to SP");
		} catch (IllegalCallParameter e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			BrokerVisualMonitor.APICall(Actions.BFE_SD_16, EndPointType.END, StatusCode.OK, "List of Providers for Components given to SP");
		} catch (IllegalCallParameter e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
	}
	
	
	public String getHost(String host){
		System.out.println("Host recieve : " + host);
		String hostID=null;
		 Properties properties = new Properties();
	    try {
		    InputStream is = getClass().getClassLoader().getResourceAsStream("BrokerServerProperties");
			//properties.load(new FileInputStream("src/main/resources/BrokerServerProperties"));
		    properties.load(is);
		    
			hostID = properties.getProperty(host);
			System.out.println("Host :" + hostID);
	    } catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return hostID;
	}

	public int getPort(String port){
		int portNum=0;
		String portNumber=null;
		Properties properties = new Properties();
		try {
		    InputStream is = getClass().getClassLoader().getResourceAsStream("BrokerServerProperties");
			//properties.load(new FileInputStream("src/main/resources/BrokerServerProperties"));
		    properties.load(is);
			
			portNumber = properties.getProperty(port);
			portNum = Integer.parseInt(portNumber);
			System.out.println("Port :" + portNum);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return portNum;
	}
	
	public String getTRECHost(String TREChost){
		System.out.println("TREC Host received : " + TREChost);
		String hostID=null;
		 Properties properties = new Properties();
	    try {
		    InputStream is = getClass().getClassLoader().getResourceAsStream("BrokerServerProperties");
			//properties.load(new FileInputStream("src/main/resources/BrokerServerProperties"));
		    properties.load(is);
		    
			hostID = properties.getProperty(TREChost);
			System.out.println("TREC Host from Properties:" + hostID);
	    } catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return hostID;
	}

	public String getTRECPort(String TRECport){
		int portNum=0;
		String portNumber=null;
		Properties properties = new Properties();
		try {
		    InputStream is = getClass().getClassLoader().getResourceAsStream("BrokerServerProperties");
			//properties.load(new FileInputStream("src/main/resources/BrokerServerProperties"));
		    properties.load(is);
			
			portNumber = properties.getProperty(TRECport);
			//portNum = Integer.parseInt(portNumber);
			System.out.println("TREC Port from Properties :" + portNumber);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return portNumber;
	}
	
}
