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

import eu.optimis.cloudoptimizer.persistence.DBUtil;
import eu.optimis.cloudoptimizer.persistence.Queries;
import eu.optimis.cloudoptimizer.rest.client.CloudOptimizerRESTClient;
import eu.optimis.cloudoptimizer.xml.PhysicalResource;
import eu.optimis.cloudoptimizer.xml.VirtualResource;
import eu.optimis.cloudoptimizer.xml.XmlUtil;
import eu.optimis.schemas.trec.blo.ObjectiveType;
import eu.optimis.treccommon.TrecApiIP;
import eu.optimis.vmmanager.util.Log;
import net.emotivecloud.utils.ovf.EmotiveOVF;
import net.emotivecloud.utils.ovf.OVFDisk;
import net.emotivecloud.utils.ovf.OVFWrapper;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

abstract class AbstractPlacement {
    protected double requirementsFulfilled; // 0 if requirements are not fulfilled. 1 otherwise
    protected double trecValue;
    protected String nodeId;
    protected PlacementOptimizer po;
    protected EmotiveOVF vmInfo;

    public AbstractPlacement(String nodeId, EmotiveOVF vmInfo, PlacementOptimizer po) {
        this.po = po;
        this.vmInfo = vmInfo;
        //first check if requirements are fulfilled by a vm
        requirementsFulfilled = -1;
        this.nodeId = nodeId;
        CloudOptimizerRESTClient co = po.getCloudOptimizer();

        PhysicalResource pr = XmlUtil.getPhysicalResourceFromXml(co.getPhysicalResource(nodeId));

        if( vmInfo == null || pr == null || pr.getCpu_cores() == null || pr.getMemory_in_gigabytes() == null) {
            StringWriter sb = new StringWriter();
            PrintWriter pw = new PrintWriter(sb);
            pw.println("some values are null:");
            pw.println("\tvmInfo = " + vmInfo);
            pw.println("\tpr = " + pr);
            if(pr != null) {
                pw.println("\tpr.getCpuCores = " + pr.getCpu_cores());
                pw.println("\tpr.getMemory_in_gigabytes() = " + pr.getMemory_in_gigabytes());
            }
            if(vmInfo != null) {
                pw.println("ovf is: " + vmInfo.toCleanString());
            }
            Log.getLogger().warn(sb.toString());
        } else {
            // Calculating Free CPUs and Memory
            long usedCPUs = 0, usedMem = 0;
            Connection conn = null;
            conn = DBUtil.getConnection();


            try {
                List<String> runningVMs = Queries.getVMsId(conn, nodeId);

                for(String vmId : runningVMs) {
                    VirtualResource vr = co.getVirtualResource(vmId);
                    usedCPUs += Integer.parseInt(vr.getCpu_cores());
                    usedMem += Integer.parseInt(vr.getMemory_in_gigabytes()) * 1024;
                }

                usedCPUs += vmInfo.getCPUsNumber();
                usedMem += vmInfo.getMemoryMB();
                if(usedCPUs <= Integer.parseInt(pr.getCpu_cores())
                   && usedMem <= (Integer.parseInt(pr.getMemory_in_gigabytes()) * 1024)) {
                    requirementsFulfilled = 1;
                } else {
                    Log.getLogger().info("Node " + nodeId + " do not fulfill the requirements (not enough CPU and/or Memory). Ommiting"
                            +"\nAccording to accounts, it is using now "+ (usedCPUs - vmInfo.getCPUsNumber()) + "/"+pr.getCpu_cores()+"CPUs and "
                            +((double)(usedMem - vmInfo.getMemoryMB()))/1024+"/"+pr.getMemory_in_gigabytes()+"GB\n"
                            + "The VM under deployment has "+vmInfo.getCPUsNumber()+" CPUs and " + vmInfo.getMemoryMB() + " MB RAM");
                }

                conn.close();
            } catch(Exception e) {
                Log.getLogger().error(e.getMessage(),e);
            }

//            if( vmInfo.getCPUsNumber() <= new Integer(pr.getCpu_cores()) &&
//                    vmInfo.getMemoryMB() <= new Integer(pr.getMemory_in_gigabytes()) * 1024) {
//                long diskSizeMB = 0;
//                if(vmInfo.getDisks() == null) {
//                    Log.getLogger().warn("vmInfo.getDisks() returns null");
//                } else {
//                    for(OVFDisk disk : vmInfo.getDisks().values()) {
//                        if(disk == null) {
//                            Log.getLogger().warn("A disk is null");
//                        } else {
//                            diskSizeMB  += disk.getCapacityMB();
//                        }
//                    }
//                }
//            }
        }
    }

    public double getUtility() {
        return requirementsFulfilled * trecValue;
    }
}
