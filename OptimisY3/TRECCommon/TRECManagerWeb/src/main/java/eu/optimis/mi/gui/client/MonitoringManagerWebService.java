/**
 *  Copyright 2013 University of Leeds
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package eu.optimis.mi.gui.client;

import java.util.List;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import eu.optimis.mi.gui.client.model.CostResourceIP;
import eu.optimis.mi.gui.client.model.CostResourceSP;
import eu.optimis.mi.gui.client.model.EcoResource;
import eu.optimis.mi.gui.client.model.EcoServiceDeploymentInfoData;
import eu.optimis.mi.gui.client.model.Ip2SpModel;
import eu.optimis.mi.gui.client.model.MonitoringResource;
import eu.optimis.mi.gui.client.model.RiskResource;
import eu.optimis.mi.gui.client.model.Sp2IpModel;
import eu.optimis.mi.gui.client.model.TrustResourceSP;

@RemoteServiceRelativePath("guiservice")
public interface MonitoringManagerWebService extends RemoteService {
	public List<MonitoringResource> getMonitoringResources(String level,
			String id);

	public List<MonitoringResource> getIdMetricDateListMonitoringResources(
			String id, String level, String metricName, String dfrom, String dto);

	public String getIdMetricDateStrMonitoringResources(String id,
			String level, String metricName, String dfrom, String dto);

	// TRUST
	public List<TrustResourceSP> getSPTrustResources(String spId);

	public List<TrustResourceSP> getIPTrustResources(String ipId);

	public List<Ip2SpModel> getIp2SpInfo(String spId);

	public List<Sp2IpModel> getSp2IpInfo(String ipId);

	// Risk
	public List<RiskResource> getRiskResources(String serviceId,
			String providerId, String providerType, String servicePhase,
			String fromDate, String toDate, String test);

	// ECO
	public List<EcoResource> getNodesEcoResources(String ini, String end, String metric);

	public List<EcoResource> getNodeEcoResources(String nodeId, String ini,
			String end, String metric);

	public List<EcoResource> getServiceIPEcoResources(String serviceId,
			String ini, String end, String metric);

	public List<EcoResource> getServiceSPEcoResources(String serviceId,
			String ini, String end, String metric);

	public List<EcoResource> getVMEcoResources(String VMId, String ini,
			String end, String metric);

	public List<EcoResource> getInfrastructureEcoResources(String ini,
			String end, String metric);

	public EcoServiceDeploymentInfoData getServiceDeploymentEcoInfo();
        
        public EcoServiceDeploymentInfoData getServiceDeploymentEcoInfoSP();

	// Cost
	public List<CostResourceIP> getSPCostResources(String spId);

	public List<CostResourceSP> getSPPredictionCostResources(String ipId,
			String assessorId);

	public List<CostResourceIP> getSPServiceCostResources(String ipId,
			String assessorId);

	public List<CostResourceIP> getSPComponentCostResources(String ipId,
			String assessorId);

	public List<CostResourceIP> getIPCostResources(String ipId);

	public List<CostResourceIP> getIPServiceCostResources(String ipId,
			String assessorId);

	public List<CostResourceIP> getIPComponentCostResources(String ipId,
			String assessorId);

	public List<CostResourceIP> getIPNodeCostResources(String ipId,
			String assessorId);

}
