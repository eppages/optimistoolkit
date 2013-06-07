/**
 * Copyright (C) 2010-2012 Barcelona Supercomputing Center
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
package eu.optimis.cloudoptimizer.core;

import eu.optimis.cloudoptimizer.data.CODecision;
import eu.optimis.cloudoptimizer.util.Log;
import eu.optimis.cloudoptimizer.rest.client.CloudOptimizerRESTClient;
import eu.optimis.cloudoptimizer.util.Config;
import java.io.IOException;
import eu.optimis.ds.client.DeploymentServiceClient;
import eu.optimis.manifest.api.ip.Manifest;
import eu.optimis.schemas.trec.blo.ObjectiveType;
import eu.optimis.treccommon.TrecApiIP;
import net.emotivecloud.utils.ovf.OVFWrapper;

/**
 * Manage bursting operations
 * @version 1.0
 * @author J. Oriol Fitó (josep.oriol@bsc.es)
 */
public class Outsourcing {   

    private static final Long TIMESPAN = Long.valueOf(3600);

    CloudManager cm;
    
    CloudOptimizerRESTClient co_client;
    DeploymentServiceClient sd_burst;
    TrecApiIP trec;

    public Outsourcing(CloudManager cm, TrecApiIP _trec) {
        this.cm = cm;
        trec = _trec;
        co_client = new CloudOptimizerRESTClient();
        String externalSDHost;
        if (Config.getString("db.location").equals("flexe")) {
            externalSDHost = Config.getString("enhanced.sd.location");
        } else {
            externalSDHost = Config.getString("config.spvm_host_sd");
        }
        int port = Integer.parseInt(Config.getString("config.spvm_port_sd"));
        Log.getLogger().info("External host for outsourcing: " + externalSDHost + ":" + port);
        sd_burst = new DeploymentServiceClient(externalSDHost, port);
    }

    /*
     * It is used to determine where to place a given service (either locally or remotely)
     * return:
     * 		0 ---> local
     * 		1 ---> remote
     * 		2 ---> local, remote
     * 		3 ---> remote, local
     */
    public CODecision decideServicePlacement(Manifest manifest) {
        String ecoFactor = "ecological";

        if(cm.getBLOs() != null && cm.getBLOs().getObjective() != null
                && ObjectiveType.MAX_ENERGY_EFF.equals(cm.getBLOs().getObjective().getType())) {
            ecoFactor = "energy";
        }

        CODecision ret = CODecision.ACCEPT_LOCAL;
        try {
            String ip_eco = trec.ECO.forecastIPEcoefficiency(new Long(0),ecoFactor);
            
            Log.getLogger().info("ECO IP = " + ip_eco);
            if ((Double.parseDouble(ip_eco) >= 0 && Double.parseDouble(ip_eco) <= Double.parseDouble(Config.getString("hm.constraint.eco.min.deployment")))) {
                ret = CODecision.ACCEPT_REMOTE;
            }
            
            //TODO Y3 - missing risk & cost
            //String trust = trec.getTrust(manifest.getServiceProviderId());
            //String ip_cost = trec.predictIPCost(rb.getString("config.ipvm_host"), Integer.parseInt(rb.getString("config.ipvm_port")), manifest.toString())
            //TODO - if ((Double.parseDouble(ip_eco) >= 0 && Double.parseDouble(ip_eco) <= Double.parseDouble(rb.getString("hm.constraint.eco.min"))) 
                    //|| (Integer.parseInt(trust) <= Integer.parseInt(rb.getString("hm.constraint.trust.min")))) {
            
        } catch (NumberFormatException ex) {
            Log.getLogger().error(ex.getMessage(),ex);
            return CODecision.ACCEPT_LOCAL;
        } catch (Exception ex) {
            Log.getLogger().error(ex.getMessage(),ex);
            return CODecision.ACCEPT_LOCAL;
        }   
        return ret;
    }

    @Deprecated
    public CODecision decideVMPlacement(OVFWrapper ovf) {
        StringBuilder sb = new StringBuilder("Deciding local or remote placement: ");
        CODecision ret = CODecision.ACCEPT_LOCAL;

        if(cm.getBLOs() == null ||
           cm.getBLOs().getConstraints() == null ||
                ( cm.getBLOs().getConstraints().getEcoGreaterThan() != null &&
                  cm.getBLOs().getConstraints().getEnergyEfficiencyGreaterThan() != null)) {
            sb.append("There are neither EcoEfficiency nor EnergyEfficiency constraints.");
        } else {
            String ecoFactor;
            double constraintValue;

            if(cm.getBLOs().getConstraints().getEcoGreaterThan() != null) {
                constraintValue = cm.getBLOs().getConstraints().getEcoGreaterThan();
                ecoFactor = "ecological";
            } else {
                constraintValue = cm.getBLOs().getConstraints().getEnergyEfficiencyGreaterThan();
                ecoFactor = "energy";
            }

            try {
                String measuredTREC = trec.ECO.forecastIPEcoEfficiencyVMDeploymentUnknownPlacement(ovf, ecoFactor, new Long(0));
                sb.append("\n\t- TREC assessor returned ").append(ecoFactor).append( "=").append(measuredTREC);
                sb.append("\n\t- The specified constraint value is ").append(ecoFactor).append(">").append(constraintValue);
                if (Double.parseDouble(measuredTREC) < constraintValue) {
                    ret = CODecision.ACCEPT_REMOTE;
                }

                //TODO Y3 - check IP constraints - RISK
            } catch (Exception e) {
                sb.append("\n").append(Log.getStackTrace(e));
                ret = CODecision.ACCEPT_LOCAL;
            }
        }
        sb.append("\n\t*** HM decided next placement: ").append(ret.toString());

        Log.getLogger().info(sb.toString());
        return ret;
    }
    

    /**
     * Returns service identifier to which VMs belong
     * @param ovfXml
     * @return 
     */
    public boolean outsourceVMs(String ovfXml) {
        try {
            sd_burst.deploy(ovfXml, Config.getString("hm.optimizationFactor.bursting"));
        } catch (IOException ex) {
            Log.getLogger().error(ex.getMessage(),ex);
        }
        return true;
    }

    /** FROM SD ----->
     * The 1st key is "TYPE", its value could be "ERROR" or "PROGRESS".
    The 2nd key is "MESSAGE", its value could be percent completion or an error message if the deployment failed. for example:
    {"TYPE":"ERROR", "MESSAGE":"No service deployed with id serviceId1234"}
    {"TYPE":"ERROR", "MESSAGE":"Failed to get an optimal solution due to XXXX!"}
    {"TYPE":"PROGRESS", "MESSAGE":"80%"} 
     */
    public String getServiceStatus(String serviceId) {
        String status = "";
        try {
            status = sd_burst.queryDeploymentStatus(serviceId);
        } catch (IOException ex) {
            Log.getLogger().error(ex.getMessage(),ex);
        }
        return status;
    }

    /**
     * This method is used to remove a bursted VMs during the operation of a given service
     * @return 
     */
    //TODO Y3 - Need to contact with API provided by non-OPTIMIS IPs????
    public void removeBurstedVMs(String[] IPIds, String LocalIPid, String serviceId, String vmId, String SP_IPaddr) {
        CloudOptimizerRESTClient co;
        if (vmId.equals("ALL")) {
            for (String ipId : IPIds) {
                if (!ipId.equals(LocalIPid)) {
                    Log.getLogger().info("Removing VMs running in external provider with id = " + ipId);
                    co = new CloudOptimizerRESTClient(ipId);
                    co.undeploy(serviceId);
                }
            }
        } else {
            String externalIPId = co_client.getNodeIpId(co_client.getNodeId(vmId));
            co = new CloudOptimizerRESTClient(co_client.getIpVmIpAddress(externalIPId));
            co.removeVM(serviceId, vmId, 1, SP_IPaddr);
        }

    }
}
