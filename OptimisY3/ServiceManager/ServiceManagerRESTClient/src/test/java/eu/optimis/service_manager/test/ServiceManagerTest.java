package eu.optimis.service_manager.test;

import java.util.UUID;

import eu.optimis.service_manager.client.ServiceManagerClient;

import junit.framework.TestCase;


public class ServiceManagerTest extends TestCase {
    
	String NO_VM_SERVICE_XML;
	String port;
	String host;
	String serviceId;
	String infraProvider1Id;
    public ServiceManagerTest(String testName) {
        super(testName);
    }
    
    
    @Override
    protected void setUp() throws Exception {
    	serviceId = "12345678910";
		infraProvider1Id = "b15f2e08-2b86-42d6-97a0-673c90d737e3";

		NO_VM_SERVICE_XML = "<service xmlns=\"http://www.optimis.eu/service-manager\">"
				+ "<service_id>"
				+ serviceId
				+ "</service_id>"
				+ "<status>pending</status>"
				+ "<infrastructure-provider>"
				+ "<id>"
				+ infraProvider1Id
				+ "</id>"
				+ "<sla_id>3c840218-9273-463b-bb96-69c15b95842c</sla_id>"
				+ "</infrastructure-provider>" + "</service>";
		host = "optimis-spvm.atosorigin.es";
		port = "8080";
        super.setUp();
    }
    
    public void testServiceManager(){
    	ServiceManagerClient smClient = new ServiceManagerClient(host,port);
    	
    	// Delete service if it exists
    	try {
			smClient.deleteService(serviceId);
		} catch (Exception e) {
		}
		// Add service and two VMs
		System.out.println(smClient.addServiceFromXml(NO_VM_SERVICE_XML));
		System.out.println("------------------------------");
		System.out.println("Adding VM");
		System.out.println(smClient.addVm(serviceId, infraProvider1Id, "9999"));

		System.out.println("Adding infrastructure provider");
		System.out.println(smClient.addInfrastructureProvider(serviceId,
				"randomInfraId", "192.168.10.10", "slaId1", "agreementendpoint1",
				1.1f, 2.2f, 3.3f, 4.4f));
		System.out.println("------------------------------");
		System.out.println("Adding another infrastructure provider");
		System.out.println(smClient.addInfrastructureProvider(serviceId,
				"randomInfraId2", "192.168.10.11", "slaId2", "agreementendpoint2",
				1.1f, 2.2f, 3.3f, 4.4f));
		System.out.println("------------------------------");
		System.out.println("Getting the initial trust value");
		System.out.println(smClient.getInitialTrustValue(serviceId, "randomInfraId2"));
		System.out.println("------------------------------");
		System.out.println("Updating the agreement endpoint");
		System.out.println(smClient.updateAgreementEndpoint(serviceId, "randomInfraId2",
				"updated agreementendpoint2"));
		System.out.println("------------------------------");		
		System.out.println("Getting infrastructure provider ids:");
		String[] infrastructureProviderIds = smClient
				.getInfrastructureProviderIds(serviceId);
		for (int i = 0; i < infrastructureProviderIds.length; i++) {
			System.out.println("\t" + infrastructureProviderIds[i]);
		}
		System.out.println("------------------------------");
		System.out.println("Adding VM");
		System.out.println(smClient.addVm(serviceId, infraProvider1Id, "9998"));
		System.out.println("------------------------------");
		System.out.println("Updating status for VM 9998");
		System.out.println(smClient.updateVmStatus(serviceId, infraProvider1Id,
				"9998", "running-as-fast-as-sonic"));
		System.out.println("------------------------------");
		System.out.println("Getting status for VM 9998");
		System.out.println(smClient.getVmStatus(serviceId, infraProvider1Id,
				"9998"));
		System.out.println("------------------------------");
		System.out.println("Adding VM with status and type");
		System.out.println(smClient.addVm(serviceId, infraProvider1Id, "9997",
				"CE", "paused", 1000));
		System.out.println("Updating VM with new deployment duration in ms");
		System.out.println(smClient.updateDeploymentDurationInMs(serviceId, infraProvider1Id,
				"9997", 5000));
		System.out.println("------------------------------");
		System.out.println("Updating VM status to running");
		System.out.println(smClient.updateVmStatus(serviceId, infraProvider1Id,
				"9997", "running"));
		System.out.println("------------------------------");
		System.out.println(smClient.getServices());
		System.out.println("------------------------------");
		System.out.println("Listing VMs");
		System.out.println(smClient.getVms(serviceId, infraProvider1Id));
		System.out.println("------------------------------");

		System.out.println("------------------------------");
		System.out.println("Listing VMs as array");
		String[] vmIdsAsArray = smClient.getVmIdsAsArray(serviceId,
				infraProvider1Id);
		for (String string : vmIdsAsArray) {
			System.out.println("VM id: " + string);
		}
		System.out.println("------------------------------");
		System.out.println(smClient.getServices());
		System.out.println("------------------------------");

		// Delete VMs, then service
		smClient.deleteVm(serviceId, infraProvider1Id, "9999");
		System.out.println(smClient.getServices());
		System.out.println("------------------------------");
		smClient.deleteVm(serviceId, infraProvider1Id, "9998");
		System.out.println(smClient.getServices());
		System.out.println("------------------------------");
		System.out.println("Setting service to 'running'");
		System.out.println("New status: "
				+ smClient.updateServiceStatus(serviceId, "running"));
		System.out.println("------------------------------");
		String newSlaId = UUID.randomUUID().toString();
		System.out.println("Updating SLA id to '" + newSlaId + "'");
		smClient.updateSlaId(serviceId, infraProvider1Id, newSlaId);
		System.out.println("------------------------------");

		System.out.println("------------------------------");
		System.out.println("get sla of  infra provider");
		System.out.println("slaId: "
				+ smClient.getSlaId(serviceId, infraProvider1Id));

		String newIpAddress = "2.2.2.2";
		System.out.println("Updating infra provider to new IP address '"
				+ newIpAddress + "'");
		System.out.println("Infra provider id: " + infraProvider1Id);
		System.out.println(smClient.updateInfrastructureProviderIpAddress(
				serviceId, infraProvider1Id, newIpAddress));

		System.out.println("------------------------------");
		smClient.deleteService(serviceId);
		System.out.println(smClient.getServices());
		System.out.println("------------------------------");
    }
    
    @Override
    protected void tearDown() throws Exception {
    	NO_VM_SERVICE_XML = null;
        super.tearDown();
    }
}
