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


import eu.optimis.cloudoptimizer.persistence.DBUtil;
import eu.optimis.cloudoptimizer.persistence.Queries;
import eu.optimis.cloudoptimizer.util.Log;
import eu.optimis.cloudoptimizer.xml.VirtualResource;
import eu.optimis.cloudoptimizer.xml.VirtualResources;
import eu.optimis.treccommon.TrecApiIP;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * This class only prints TREC values with time. It is not a core component of CO, but it is
 * used to measuring and correlating TREC with system metrics in scientific reports.
 */
public class TRECWatcher extends TimerTask {

    private static final int PERIOD = 60000; // checks every minute
    public static final TRECWatcher instance = new TRECWatcher();

    private TrecApiIP trec;
    private Timer timer = null;
    protected String path;

    private TRECWatcher() {

    }

    @Override
    public void run() {
        Connection conn = null;
        FileOutputStream fos = null;
        PrintWriter out = null;
        try {
            fos = new FileOutputStream(path,true);
            out = new PrintWriter(fos,true);

            // printing timestamp

            out.println("time:"+String.valueOf(System.currentTimeMillis()));
            conn = DBUtil.getConnection();

            String ipId = null;
            try {
                ipId = Queries.getIpId(conn);
            } catch(SQLException e) {
                Log.getLogger().error(Log.getStackTrace(e));
            }

            out.print("ip:" + ipId);

            // printing IP information: #VMs, trust, eco (energy), eco (eco) --> risk is not calculated per IP
            int vms = 0;
            try {
                vms = Queries.countVirtualMachines(conn);
            } catch(SQLException e) {
                Log.getLogger().error(Log.getStackTrace(e));
            }

            out.print("\tvms:"+vms);
            out.print("\tenergy:"+trec.ECO.assessIPEcoefficiency("energy"));
            out.print("\tecological:" + trec.ECO.assessIPEcoefficiency("ecological"));
            out.println();

            ArrayList<String> activeNodesIds = Queries.getActiveNodesId(conn);
            for(String nodeId : activeNodesIds) {
                List<String> vmsAtNode = Queries.getVMsId(conn,nodeId);
                out.print("\tnode:"+nodeId);
                out.print("\tvms:"+vmsAtNode.size());
                out.print("\tenergy:"+trec.ECO.assessNodeEcoEfficiency(nodeId,"energy"));
                out.print("\tecological:"+trec.ECO.assessNodeEcoEfficiency(nodeId,"ecological"));
                out.println();
                for(String vmId : vmsAtNode) {
                    VirtualResource vm = Queries.getVirtualResource(conn, vmId).getVirtualResource().get(0);
                    out.print("\t\tvm:"+vmId);
                    out.print("\tcores:"+vm.getCpu_cores());
                    out.println();
                }
            }


        } catch(Exception e) {
            Log.getLogger().error(Log.getStackTrace(e));
        } finally {
            try {
                if(conn != null) conn.close();
            } catch(SQLException e) {
                Log.getLogger().error(Log.getStackTrace(e));
            }
            if(out != null) {
                out.close();
            }
            if(fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    Log.getLogger().error(Log.getStackTrace(e));
                }
            }

        }
    }

    public void initialize(TrecApiIP trec) {
        this.trec = trec;
        this.path = System.getenv("OPTIMIS_HOME") + "/var/log/CloudOptimizer/trecwatch.txt";

        Log.getLogger().info("Starting TREC Watcher. Log file path: " + path);
        try {
            if(timer == null) {
                timer = new Timer(getClass().getName());
                timer.scheduleAtFixedRate(this, PERIOD, PERIOD);
            }
        } catch(Exception e) {
            Log.getLogger().error(e.getMessage(),e);
        }
    }

}
