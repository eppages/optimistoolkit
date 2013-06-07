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
package eu.optimis.faulttoleranceengine.core;

import eu.optimis.cloudoptimizer.persistence.DBUtil;
import eu.optimis.cloudoptimizer.persistence.Queries;
import eu.optimis.cloudoptimizer.xml.VirtualResource;
import eu.optimis.faulttoleranceengine.util.Log;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;

public class TestingMonitor extends Monitor {
    public TestingMonitor(FTAssessor FTA) throws IOException {
        super(FTA);
    }

    @Override
    protected Set<String> anticipateNodeFailures(List<String> nodeIds) {
        Set<String> failing = new HashSet<String>();
        Connection conn = DBUtil.getConnection();
        try {
            for(String nodeId : nodeIds) {
                double totalCpus = new Double(Queries.getPhysicalResource(conn,nodeId).getCpu_cores());
                double usedCpus = 0;
                List<String> vmIds = Queries.getVMsId(conn,nodeId);
                for(String vmId : vmIds) {
                    VirtualResource vr = Queries.getVirtualResource(conn, vmId).getVirtualResource().get(0);
                    usedCpus += new Double(vr.getCpu_cores());
                }
                // by the moment, only notify a node each time
                if(isNodeFailing(usedCpus,totalCpus)) {
                    failing.add(nodeId);
                }
            }
        } catch(SQLException e) {
            Log.getLogger().error(Log.getStackTrace(e));
        }
        return failing;
    }

    /**
     * This method is a simplified risk assessor. Used as a temporary patch for testing purposes
     * @param usedCpus
     * @param totalCpus
     * @return
     */
    private static Random rnd = new Random(System.currentTimeMillis());
    private boolean isNodeFailing(double usedCpus, double totalCpus) {

        double percentage = usedCpus / totalCpus;
        double probability;
        if(percentage < 0.9) {
            probability = percentage * 0.05;
        } else if(percentage <= 1) {
            probability = percentage * 0.95;
        } else {
            probability = 1;
        }

        return rnd.nextDouble() < probability;

    }



    /**
     * This method is a simplified risk assessor. Used as a temporary patch for testing purposes
     * @param vmIds
     */
    @Override
    protected void anticipateVMFailures(List<String> vmIds) {
        long now = System.currentTimeMillis();
        for(String vmId : vmIds) {
            Long fm = firstMeasures.get(vmId);
            if(fm == null) {
                fm = new Long(now);
                firstMeasures.put(vmId, fm);
            }
            double runningTime = now - fm;
            double probabilityOfFailure = runningTime / MAX_RUNNING_TIME_WITHOUT_FAILURES;
            if(rnd.nextDouble() < probabilityOfFailure) {
                co.saveVMAndRestart(vmId);
            }

        }
    }
    private static final double MAX_RUNNING_TIME_WITHOUT_FAILURES = 24 * 60 * 60 * 1000; // 1 day in ms.
    private Map<String,Long> firstMeasures = new TreeMap<String, Long>();

}
