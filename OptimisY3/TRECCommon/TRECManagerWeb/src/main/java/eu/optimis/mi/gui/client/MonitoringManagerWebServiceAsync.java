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

import com.google.gwt.user.client.rpc.AsyncCallback;

import eu.optimis.mi.gui.client.model.*;

public interface MonitoringManagerWebServiceAsync {

	public void getMonitoringResources(String level, String id,
			AsyncCallback<List<MonitoringResource>> callback);

	public void getIdMetricDateStrMonitoringResources(String id, String level,
			String metricName, String dfrom, String dto,
			AsyncCallback<String> callback);

	public void getIdMetricDateListMonitoringResources(String id, String level,
			String metricName, String dfrom, String dto,
			AsyncCallback<List<MonitoringResource>> callback);

	// TRUST
	public void getSPTrustResources(String spId,
			AsyncCallback<List<TrustResourceSP>> callback);

	public void getIPTrustResources(String ipId,
			AsyncCallback<List<TrustResourceSP>> callback);

	public void getIp2SpInfo(String spId,
			AsyncCallback<List<Ip2SpModel>> callback);

	public void getSp2IpInfo(String ipId,
			AsyncCallback<List<Sp2IpModel>> callback);

	// Risk
	public void getRiskResources(String serviceId, String providerId,
			String providerType, String servicePhase, String fromDate, String toDate, String test,
			AsyncCallback<List<RiskResource>> callback);

	// ECO
	public void getNodesEcoResources(String ini, String end, String metric, 
			AsyncCallback<List<EcoResource>> callback);

	public void getNodeEcoResources(String nodeId, String ini, String end, String metric, 
			AsyncCallback<List<EcoResource>> callback);

	public void getServiceIPEcoResources(String serviceId, String ini, String metric, 
			String end, AsyncCallback<List<EcoResource>> callback);

	public void getServiceSPEcoResources(String serviceId, String ini, String metric, 
			String end, AsyncCallback<List<EcoResource>> callback);

	public void getVMEcoResources(String VMId, String ini, String end, String metric, 
			AsyncCallback<List<EcoResource>> callback);

	public void getInfrastructureEcoResources(String ini, String end, String metric, 
			AsyncCallback<List<EcoResource>> callback);

	public void getServiceDeploymentEcoInfo(
			AsyncCallback<EcoServiceDeploymentInfoData> callback);
        
        public void getServiceDeploymentEcoInfoSP(
			AsyncCallback<EcoServiceDeploymentInfoData> callback);

	// Cost
	public void getSPCostResources(String spId,
			AsyncCallback<List<CostResourceIP>> callback);

	public void getSPPredictionCostResources(String ipId, String assessorId,
			AsyncCallback<List<CostResourceSP>> callback);

	public void getSPServiceCostResources(String ipId, String assessorId,
			AsyncCallback<List<CostResourceIP>> callback);

	public void getSPComponentCostResources(String ipId, String assessorId,
			AsyncCallback<List<CostResourceIP>> callback);

	public void getIPCostResources(String ipId,
			AsyncCallback<List<CostResourceIP>> callback);

	public void getIPServiceCostResources(String ipId, String assessorId,
			AsyncCallback<List<CostResourceIP>> callback);

	public void getIPComponentCostResources(String ipId, String assessorId,
			AsyncCallback<List<CostResourceIP>> callback);

	public void getIPNodeCostResources(String ipId, String assessorId,
			AsyncCallback<List<CostResourceIP>> callback);
}
