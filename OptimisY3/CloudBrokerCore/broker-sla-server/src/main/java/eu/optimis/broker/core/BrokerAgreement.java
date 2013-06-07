package eu.optimis.broker.core;

import java.util.HashMap;
import java.util.Map.Entry;

import org.ogf.graap.wsag.api.client.AgreementClient;

import eu.optimis.broker.sla.BrokerContext;
import eu.optimis.cbr.monitoring.clientlib.Actions;
import eu.optimis.cbr.monitoring.clientlib.BrokerVisualMonitor;
import eu.optimis.cbr.monitoring.clientlib.EndPointType;
import eu.optimis.cbr.monitoring.clientlib.StatusCode;
import eu.optimis.ipdiscovery.datamodel.Provider;
import eu.optimis.manifest.api.sp.Manifest;
import eu.optimis.service_manager.client.ServiceManagerClient;
import eu.optimis.types.xmlbeans.servicemanifest.XmlBeanServiceManifestDocument;

public class BrokerAgreement {
	
	private HashMap<String, Provider> deploymentSolutions;

	public BrokerAgreement(HashMap<String, Provider> doSolutions){
		this.deploymentSolutions = doSolutions;
	}
	
	public void createIPAgreements() throws Exception{
        	OutputStub4Demo.write( "deployment solutions for this agreement offer: " + deploymentSolutions.size() );
        	
        	OutputStub4Demo.write("\nCreating an agreement with IPs via calling CloudQoS service.");
			//Provider provider = placement.getProvider();
        	Provider provider = null;
        	String manifest = null;
        	String templateName="OPTIMIS-SERVICE-INSTANTIATION";
        	String templateId="1";

        	
			for(Entry<String, Provider> entry : this.deploymentSolutions.entrySet())
			{
			
				manifest = entry.getKey();
				provider = entry.getValue();
				OutputStub4Demo.write("\nCreating an agreement with IP "+ provider.getName()+ " IPAddress :"+ provider.getIpAddress());
				
				if(provider.getName().equalsIgnoreCase("umea")){
					BrokerVisualMonitor.APICall(Actions.BFE_IP1_32, EndPointType.START, StatusCode.OK, "status-message");
					BrokerVisualMonitor.APICall(Actions.BFE_IP1_32, EndPointType.END, StatusCode.OK, "status-message");

					
				}else if(provider.getName().equalsIgnoreCase("flex")){
					BrokerVisualMonitor.APICall(Actions.BFE_IP2_34, EndPointType.START, StatusCode.OK, "status-message");
					BrokerVisualMonitor.APICall(Actions.BFE_IP2_34, EndPointType.END, StatusCode.OK, "status-message");
					
				}
					
				
			    				
	   			String cloudqosendpoint = provider.getCloudQosUrl();
    			BrokerCloudQoSClient qosClient = new BrokerCloudQoSClient(cloudqosendpoint);
    			
    			AgreementClient agrment = qosClient.createAgreement(templateName, templateId, manifest); 
    	
    			if (agrment == null)
    			{
    				String errMsg = "Creating agreement FAILED";
    				OutputStub4Demo.write(errMsg);
    				//this.statusDAO.addErrorStatus("CloudQoS", "Create agreement", errMsg);
    				throw new Exception(errMsg);
    			}
    			OutputStub4Demo.write("\nServiceInstantiation: CreateAgreement : Agreement state:" + agrment.getState());

				if(provider.getName().equalsIgnoreCase("umea")){

					BrokerVisualMonitor.APICall(Actions.IP1_BFE_33, EndPointType.START, StatusCode.OK, "status-message");
					BrokerVisualMonitor.APICall(Actions.IP1_BFE_33, EndPointType.END, StatusCode.OK, "status-message");
					
				}else if(provider.getName().equalsIgnoreCase("flex")){

					BrokerVisualMonitor.APICall(Actions.IP2_BFE_35, EndPointType.START, StatusCode.OK, "status-message");
					BrokerVisualMonitor.APICall(Actions.IP2_BFE_35, EndPointType.END, StatusCode.OK, "status-message");

				}

    			
                //Adding Service to ServiceManager//
                
    			XmlBeanServiceManifestDocument parsedManifest = XmlBeanServiceManifestDocument.Factory.parse(manifest);
    			Manifest spManifest= Manifest.Factory.newInstance(parsedManifest);
    			String serviceId = spManifest.getVirtualMachineDescriptionSection().getServiceId();

    			String ipId = provider.getIdentifier();
    			String ipAddress = provider.getIpAddress();
    			String slaId= agrment.getAgreementId();
    			String agreementEndpoint = agrment.getEndpoint().xmlText();
    			
    			OutputStub4Demo.write("\nServiceInstantiation: CreateAgreement : Adding service to Servicemanager");


    			BrokerVisualMonitor.APICall(Actions.BFE_SM_37, EndPointType.START, StatusCode.OK, "status-message");
    			BrokerVisualMonitor.APICall(Actions.BFE_SM_37, EndPointType.END, StatusCode.OK, "status-message");
    			
    			
    			this.addProvider2Service(entry, serviceId, ipId, ipAddress, slaId, agreementEndpoint);

    			
			}
        	
	}

	
	/**ServiceManager*/
	protected void addProvider2Service(Entry<String, Provider> solutions, String serviceId, String ipId, String ipAddress, String slaId, String agreementEndpoint) throws Exception
	{
		OutputStub4Demo.write("\nUpdating Service, Basically add Infrastructure.");
		OutputStub4Demo.write("\nUsing serviceId=" + serviceId+", Infrastrure Id = " + ipId+", ip address=" + ipAddress);
		OutputStub4Demo.write("\nAgreement Endpoint : " + agreementEndpoint);
		Provider provider = solutions.getValue();
		
		try
		{
			//TrecObj trec = placement.getTREC();
			//ServiceManagerClient client = getSMClient();;
			ServiceManagerClient client = new ServiceManagerClient("217.33.61.85", "8080");
			
			double initialTrustValue = 0; //trec.getTrust();//5;//
			double initialRiskValue = 0; // trec.getRisk();//0;//
			double initialEcoValue = 0; //trec.getCost();// 5;//
			double initialCostValue = 0; // trec.getCost();//0;//

			if(provider.getName().equalsIgnoreCase("umea")){
				initialTrustValue = 3.0;
				initialRiskValue = 0.0;
				initialEcoValue = -1.0;
				initialCostValue = 30.0;
			}else if(provider.getName().equalsIgnoreCase("flex")){
				initialTrustValue = 5.0;
				initialRiskValue = 0.0;
				initialEcoValue = -1.0;
				initialCostValue = 30.0;
			}
			client.addInfrastructureProvider(serviceId, ipId, ipAddress, slaId, agreementEndpoint, 
					(float)initialTrustValue , (float)initialRiskValue , (float)initialEcoValue , (float)initialCostValue );
			//client.addInfrastructureProvider(serviceId, ipId, ipAddress, slaId);
		}
		catch (Exception e)
		{
			System.out.println("Error when adding Provider to Service: "+e.getMessage());
			e.printStackTrace();
			throw e;
		}
	}	


	
}
