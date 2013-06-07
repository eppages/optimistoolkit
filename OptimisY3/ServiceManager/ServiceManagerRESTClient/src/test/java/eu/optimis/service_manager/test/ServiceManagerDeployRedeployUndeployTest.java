package eu.optimis.service_manager.test;

import eu.optimis.service_manager.client.ServiceManagerClient;
import eu.optimis.manifest.api.sp.*;

import junit.framework.TestCase;

public class ServiceManagerDeployRedeployUndeployTest extends TestCase {

	String NO_VM_SERVICE_XML;
	String port;
	String host;
	String serviceId;
	String infraProvider1Id;

	public ServiceManagerDeployRedeployUndeployTest(String testName) {
		super(testName);
	}

	@Override
	protected void setUp() throws Exception {
		serviceId = "OPTIMIS_VM:1";
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

	public void testServiceManagerDeployRedeployUndeploy() {
		ServiceManagerClient smClient = new ServiceManagerClient(host, port);

		// Delete service if it exists
		try {
			smClient.deleteService("ServiceMangerTest");
		} catch (Exception e) {
		}

		System.out.println("Testing deploy/redeploy/undeploy.");

		// Deploy
		System.out.println("Calling deploy...");
		try {
			System.out.println(smClient.addService("ServiceMangerTest"));
			System.out.println(smClient.deploy("risk", Manifest.Factory
					.newInstance("ServiceMangerTest", "ServiceMangerTest")
					.toString()));
		} catch (Exception e) {
			e.printStackTrace();
		}

		// Undeploy
		System.out.println("Calling undeploy...");
		try {
			System.out.println(smClient.undeploy("ServiceMangerTest", false));
		} catch (Exception e) {
			e.printStackTrace();
		}

		// Redeploy
		System.out.println("Calling redeploy...");
		try {
			try {
				smClient.deleteService(serviceId);
			} catch (Exception e) {
			}
			System.out.println(smClient.addService(serviceId));
			System.out.println("Added service to ServiceManager with ID: "
					+ serviceId);
			System.out.println(smClient.addManifestId(serviceId, serviceId));
			System.out.println("ManifestID is:"
					+ smClient.getManifestId(serviceId));
			System.out.println(smClient.addObjective(serviceId, "risk"));
			System.out.println("Objective is:"
					+ smClient.getObjective(serviceId));
			System.out.println(smClient.redeploy(serviceId, true));
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	protected void tearDown() throws Exception {
		NO_VM_SERVICE_XML = null;
		super.tearDown();
	}
}
