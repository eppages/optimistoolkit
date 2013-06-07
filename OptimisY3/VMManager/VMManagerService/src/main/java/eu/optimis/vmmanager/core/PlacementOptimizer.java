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
package eu.optimis.vmmanager.core;

import eu.optimis.cloudoptimizer.blo.BLOException;
import eu.optimis.cloudoptimizer.persistence.DBUtil;
import eu.optimis.cloudoptimizer.persistence.Queries;
import eu.optimis.cloudoptimizer.rest.client.CloudOptimizerRESTClient;
import eu.optimis.ecoefficiencytool.rest.client.EcoEfficiencyToolRESTClientIP;
import eu.optimis.manifest.api.ip.Manifest;
import eu.optimis.schemas.trec.blo.BusinessDescription;
import eu.optimis.schemas.trec.blo.Objective;
import eu.optimis.schemas.trec.blo.ObjectiveType;
import eu.optimis.treccommon.TrecApiIP;
import eu.optimis.vmmanager.util.Config;
import eu.optimis.vmmanager.util.Log;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;

import net.emotivecloud.commons.ListStrings;
import net.emotivecloud.scheduler.drp.client.DRPClient;
import net.emotivecloud.utils.ovf.EmotiveOVF;
import net.emotivecloud.utils.ovf.OVFWrapper;
import net.emotivecloud.vrmm.scheduler.VRMMSchedulerException;

/**
 * Responsible of optimizing the placement of a set of VMs over a private Cloud infrastructure
 */
public class PlacementOptimizer {

    private DRPClient drp;
    private static final int TIMESPAN_RISK = 60000;
    private static final int TIMESPAN_ECO_FORECASTS = 60000;
    
    private BusinessDescription schedulingPolicy;
    private ObjectiveType maximizationPolicy;
    // caches the weights specified at schedulingPolicy

    TrecApiIP trec;
    CloudOptimizerRESTClient cloudOptimizer;
    Manifest manifest;


    public PlacementOptimizer(DRPClient drp) {
        this.drp = drp;
        Log.getLogger().info("Initializing placement optimizer...");

        Log.getLogger().info("Initializing communication with TREC common API...");
        try {
            System.out.println("*** 1");
            EcoEfficiencyToolRESTClientIP eco = new EcoEfficiencyToolRESTClientIP("localhost",8080);
            System.out.println("*** 2");
            Class.forName("eu.optimis.ecoefficiencytool.rest.client.EcoEfficiencyToolRESTClientIP");
        } catch(Exception e) {
            e.printStackTrace();
        }
        System.out.println("**** 3");
        trec = new TrecApiIP("localhost", 8080);
        //trec = new TrecApiIP(rb.getString("config.ipvm_host"), Integer.parseInt(rb.getString("config.ipvm_port")));
        Log.getLogger().info("Initializing communication with CO...");
        cloudOptimizer = new CloudOptimizerRESTClient();
        maximizationPolicy = ObjectiveType.MAX_ECO;
    }

    protected TrecApiIP getTREC() {
        return trec;
    }
    protected DRPClient getDrp() {
        return drp;
    }

    protected CloudOptimizerRESTClient getCloudOptimizer() {
        return cloudOptimizer;
    }

    public void changeSchedulingPolicy(BusinessDescription bd) throws BLOException {
        try {
            schedulingPolicy = bd;
            if(bd.getObjective() != null) {

                Log.getLogger().info("received BLO: " + bd.getObjective().getType().toString());
                maximizationPolicy = bd.getObjective().getType();
            } else {
                Log.getLogger().warn("Received new policy without maximization policy. Leaving as previously");
            }
        } catch(Exception e) {
            Log.getLogger().debug(e.getMessage(), e);            
        }
    }

    /**
     * Assess the optimal placement of a given VM
     * @return nodeId - destination node
     */
    public String assessVMOptimalPlacement(EmotiveOVF ovf) {
//        if (Config.getBoolean("migration.enabled")) {
//            return assessBestNodeMigrateIfRequired(ovf);
//        } else {
            return assessBestNode(ovf);
//        }
    }

    private String findMatchingNode(EmotiveOVF ovf) {
        List<String> activeNodes = cloudOptimizer.getNodesId();
        try {
            Log.getLogger().info("Assessing optimal placement between " + activeNodes.size() + " nodes. Policy: " +maximizationPolicy.toString());
        } catch(Exception e) {
            Log.getLogger().error(e.getMessage()+ " " + e.getStackTrace());
        }

        List<Placement> candidatePlacements = new ArrayList<Placement>(activeNodes.size());
        for(String nodeId : activeNodes) {
            candidatePlacements.add(new Placement(nodeId, ovf, activeNodes, this));
        }

        AbstractPlacement actualPlacement = null;
        for(eu.optimis.vmmanager.core.AbstractPlacement placement : candidatePlacements) {
            if(placement.getUtility() > 0) {
                if(actualPlacement == null || placement.getUtility() > actualPlacement.getUtility()) {
                    actualPlacement = placement;
                }
            }
        }

        return actualPlacement == null ? null : actualPlacement.nodeId;
    }
/**
 * 
 * @param ovf
 * @return the best node to deploy the vm
 */    
    private String assessBestNode(EmotiveOVF ovf) {
        String nodeId = findMatchingNode(ovf);

        if(nodeId == null) {
            Log.getLogger().error("no host fulfills the requirements! -- TODO: SOLVE THIS");
            nodeId = cloudOptimizer.getNodesId().get(0);
        }

        Log.getLogger().debug("Optimal placement is in node " + nodeId);
                
        return nodeId;
    }
    
//    private String assessBestNodeMigrateIfRequired(EmotiveOVF ovf) {
//        String nodeId = findMatchingNode(ovf);
//
//        if(nodeId == null || nodeId.equalsIgnoreCase(ovf.getProductProperty(EmotiveOVF.PROPERTYNAME_SOURCE_HOST))) {
//            Log.getLogger().info("Holistic Management assessed to no migrate VM " + ovf.getId());
//        } else {
//            Log.getLogger().info("Going to migrate VM " + ovf.getId() + " to " + nodeId);
//            try {
//                drp.migrate(ovf.getId(),nodeId);
//            } catch (VRMMSchedulerException e) {
//                Log.getLogger().error(e.getMessage(),e);
//            }
//        }
//        return nodeId;
//    }

    /**
     *
     * @param ovf the information of the vm to migrate
     * @return true if the vm has been migrated
     */
    public boolean migrateVM(EmotiveOVF ovf) {
        String vmId = ovf.getId();
        Log.getLogger().debug("Trying to migrate " + vmId);
        String initialLocation = drp.getLocation(vmId);
        List<String> activeNodes = cloudOptimizer.getNodesId();

        List<eu.optimis.vmmanager.core.AbstractPlacement> candidateMigrs = new ArrayList<eu.optimis.vmmanager.core.AbstractPlacement>(activeNodes.size());
        for(String nodeId : activeNodes) {
            if(nodeId.equals(initialLocation)) {
                candidateMigrs.add(new eu.optimis.vmmanager.core.NoMigration(nodeId,ovf,this));
            } else {
                candidateMigrs.add(new eu.optimis.vmmanager.core.Migration(nodeId,ovf,activeNodes,this));
            }
        }

        eu.optimis.vmmanager.core.AbstractPlacement actualPlacement = null;
        for(eu.optimis.vmmanager.core.AbstractPlacement placement : candidateMigrs) {
            if(actualPlacement == null || placement.getUtility() > actualPlacement.getUtility()) {
                actualPlacement = placement;
            }
        }

        boolean noSpace = true;
        for(eu.optimis.vmmanager.core.AbstractPlacement placement : candidateMigrs) {
            if(placement.requirementsFulfilled == 1) {
                noSpace = false;
            }
        }

        if(noSpace) {
            Log.getLogger().error("no host fulfills the requirements for migration!" +
                    " At least the initial location should fulfill them-- TODO: SOLVE THIS");
        }

        if(actualPlacement.nodeId.equals(initialLocation)) {
            Log.getLogger().info("Migration is not suitable. VM will be kept in " + initialLocation);
            return false;
        } else {
            Log.getLogger().info("Going to migrate from " + initialLocation + " to " + actualPlacement.nodeId + " ...");
            try {
                drp.migrate(vmId, actualPlacement.nodeId);
            } catch (VRMMSchedulerException e) {
                Log.getLogger().error("Can not migrate vm: " + e.getMessage(), e);
                return false;
            }
            Log.getLogger().debug("Migration finished");
            EmotiveOVF eovf = new EmotiveOVF(ovf);
            eovf.setProductProperty("VM.destination.host", actualPlacement.nodeId);
            Connection conn = null;
            try {
                conn = DBUtil.getConnection();
                Queries.updateVirtualResource(conn, eovf.getId(), actualPlacement.nodeId);
            } catch(SQLException e) {
                Log.getLogger().debug("Cannot update virtual resource node in DB", e);
            }
            if(conn != null) try { conn.close(); } catch(SQLException e) { Log.getLogger().debug("",e); };
            return true;
        }
    }

    public ObjectiveType getMaximizationPolicy() {
        return maximizationPolicy;
    }

    /**
     * According to TREC values and BLOs, chooses to migrate/cancel VMs
     */
    public void optimizeInfrastructure() {
        if(!Config.getBoolean("migration.enabled")) {
            return;
        }
        Log.getLogger().debug("Preparing for migration");

        List<Migration> migrations = new ArrayList<Migration>();

        List<String> nodes = cloudOptimizer.getNodesId();

        StringBuilder sb = new StringBuilder("Calculating optimum 1-step placement\n");
        for(String sourceNode : nodes) {
            List<String> vms = cloudOptimizer.getVMsId(sourceNode);
            for(String vm : vms) {
                sb.append("\t" + vm + " at node " + sourceNode+". Considered actions:\n");
                List<AbstractPlacement> actions = new ArrayList<AbstractPlacement>();

                for(String candidateNodeForMigration : nodes) {
                    try {
                        EmotiveOVF ovf = new EmotiveOVF(drp.getCompute(vm));
                        if(candidateNodeForMigration.equalsIgnoreCase(sourceNode)) {
                            // Check TREC for doing nothing
                            actions.add(new NoMigration(sourceNode,ovf,this));
                        } else {
                            // Check TREC for migrating vm from sourceNode to DestinationNode
                            List<String> activeNodes = new ArrayList<String>(nodes);
                            actions.add(new Migration(candidateNodeForMigration, ovf,activeNodes,this));
                        }
                    } catch(Exception ex) {
                        sb.append("\tError when calculating " + vm +". Ignoring." + ex.getMessage());
                    }
                }

                Iterator<AbstractPlacement> ita = actions.iterator();
                AbstractPlacement finalAction = null;
                while(ita.hasNext()) {
                    AbstractPlacement candidateAction = ita.next();
                    sb.append("\t\t").append(candidateAction.toString()).append("\n");
                    if(finalAction == null || candidateAction.getUtility() > finalAction.getUtility()) {
                        finalAction = candidateAction;
                    }
                }

                if(finalAction instanceof Migration) {
                    migrations.add((Migration)finalAction);

                }
            }
        }

        if(migrations.size() > 0) {
            // Only migrating one at a same time (wait for another TREC notification before migrating another)
            Iterator<Migration> it = migrations.iterator();
            Migration bestMigration = it.next();
            while(it.hasNext()) {
                Migration m = it.next();
                if(m.getUtility() > bestMigration.getUtility()) {
                    bestMigration = m;
                }
            }

            String to = bestMigration.nodeId;
            String vm = bestMigration.vmInfo.getId();
            sb.append("* FINAL ACTION: Migrating " + vm + " to " + to);
            Log.getLogger().debug(sb.toString());
            Connection conn = null;
            try {
                conn = DBUtil.getConnection();
                drp.migrate(vm,to);
                Queries.updateVirtualResource(conn, vm, to);
            } catch (Exception e) {
                Log.getLogger().error(Log.getStackTrace(e));
            }
            try { if(conn != null) conn.close(); } catch(Exception e) { Log.getLogger().error(e.getMessage(),e); }

            sb.append("* FINAL ACTION: keep everything as now.");
            Log.getLogger().debug(sb.toString());
        }
    }

    public ListStrings getVMsAtNode(String nodeId) {
        ListStrings vms = new ListStrings();
        for(String v : drp.getComputes()) {
            vms.add(v);
        }
        return vms;
    }
}