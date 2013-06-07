/**
 * Copyright (C) 2010-2011 Barcelona Supercomputing Center
 *
 * This library is free software; you can redistribute it and/or 
 * modify it under the terms of the GNU Lesser General Public 
 * License as published by the Free Software Foundation; either 
 * version 2.1 of the License, or (at your option) any later version.
 * This library is distributed in the hope that it will be useful, 
 * but WITHOUT ANY WARRANTY; without even the implied warranty of 
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU 
 * Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public 
 * License along with this library; if not, write to the Free Software 
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
 
 package eu.optimis.vmmanager.core;

import eu.optimis.schemas.trec.blo.ObjectiveType;
import eu.optimis.vmmanager.util.Log;
import net.emotivecloud.utils.ovf.EmotiveOVF;
import net.emotivecloud.utils.ovf.OVFWrapper;

import java.util.List;

class Placement extends AbstractPlacement {
    private static final int TREC_MAX_RISK_VALUE = 7;
    public Placement(String nodeId, EmotiveOVF vmInfo, List<String> activeNodes, PlacementOptimizer po) {
        super(nodeId, vmInfo,  po);
        // Make sure that the vmInfo always contains the "serviceId" property (must be set by CO before each call to
        // vmmClient.addVM(...)
        if(ObjectiveType.MAX_TRUST.equals(po.getMaximizationPolicy())) {
            try {
                trecValue = 1; //po.getTREC().TRUST.forecastVMDeployment(vmInfo.getProductProperty("serviceId"));
            } catch(Exception e) {
                Log.getLogger().error("TRUST.forecastVMDeployment("+vmInfo.getProductProperty("serviceId")+") failed: " + e.getMessage());
            }
        } else if(ObjectiveType.MIN_RISK.equals(po.getMaximizationPolicy())) {
            try {
                trecValue = 4; //TREC_MAX_RISK_VALUE-new Integer(po.getTREC().RISK.forecastRiskVMDeploymentKnownHost("1.0",vmInfo.getProductProperty(EmotiveOVF.PROPERTYNAME_VM_ID),nodeId));
            } catch(Exception e) {
                Log.getLogger().error("RISK.forecastRiskVMDeploymentKnownHost(...) failed: " + e.getMessage());
            }
        } else if(ObjectiveType.MAX_ENERGY_EFF.equals(po.getMaximizationPolicy())) {
            try {
                trecValue = new Double(po.getTREC().ECO.forecastIPEcoEfficiencyVMDeploymentKnownPlacement(vmInfo, nodeId, activeNodes, "energy" , new Long(0)));
            } catch(Exception e) {
                Log.getLogger().error("ECO.forecastIPEcoEfficiencyVMDeploymentKnownPlacement() failed: " + e.getMessage());
            }
        } else if(ObjectiveType.MAX_ECO.equals(po.getMaximizationPolicy())) {
            try {
                trecValue = new Double(po.getTREC().ECO.forecastIPEcoEfficiencyVMDeploymentKnownPlacement(vmInfo, nodeId, activeNodes, "ecological" , new Long(0)));
            } catch(Exception e) {
                Log.getLogger().error("ECO.forecastIPEcoEfficiencyVMDeploymentKnownPlacement() failed: " + e.getMessage());
            }

        } else if(ObjectiveType.MIN_COST.equals(po.getMaximizationPolicy())) {
            trecValue = 1; // TODO: cost must provide interfaces
        }
    }

    @Override
    public String toString() {
        return "Deploying " + vmInfo.getId() + " to node " + nodeId + " (Utility: " + getUtility() + ")";
    }
}
