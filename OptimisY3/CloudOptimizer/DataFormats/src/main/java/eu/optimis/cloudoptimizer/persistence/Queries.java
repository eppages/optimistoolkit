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
package eu.optimis.cloudoptimizer.persistence;

import eu.optimis.cloudoptimizer.blo.BLOUtils;
import eu.optimis.cloudoptimizer.xml.VirtualResources;
import eu.optimis.cloudoptimizer.xml.VirtualResource;
import eu.optimis.cloudoptimizer.xml.PhysicalResources;
import eu.optimis.cloudoptimizer.xml.PhysicalResource;
import eu.optimis.schemas.trec.blo.BusinessDescription;
import org.apache.log4j.Logger;
import sun.rmi.runtime.Log;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.StringReader;
import java.sql.*;
import java.util.*;

/**
 * This class contains the methods to make queries to the database tables
 * (physical_resource & virtual_resource) managed by Cloud Optimizer.
 * 
 * @author J. Oriol Fitó (josep.oriol@bsc.es)
 */
public class Queries {

    /**
     * PHYSICAL RESOURCES
     */
    public static boolean insertPhysicalResource(Connection conn, PhysicalResource md) throws SQLException{
        String Query = "INSERT INTO physical_resource VALUES('" + md.getId()
                + "','" + md.getHostname() + "', '" + md.getHypervisor()
                + "', '" + md.getDisk_size_in_gigabytes() + "', '"
                + md.getCpu_cores() + "', '" + md.getMemory_in_gigabytes()
                + "', '" + md.getOs() + "', '" + md.getNetwork_adapter()
                + "', '" + md.getPublic_ip_address()
                + "', '" + md.getPrivate_ip_address()
                + "', '" + md.getInfrastructure_provider_id() + "')";

            Statement st = conn.createStatement();
            st.executeUpdate(Query);
        st.close();
        return true;
    }

    public static PhysicalResource getPhysicalResource(Connection conn, String nodeId) throws SQLException {
        PhysicalResource ms = null;
        String sqlStatement = null;
        sqlStatement = "SELECT id, hostname, hypervisor, disk_size_in_gigabytes, cpu_cores,"
                + "memory_in_gigabytes, os, network_adapter, public_ip_address, "
                + "private_ip_address, infrastructure_provider_id, active FROM physical_resource "
                + "WHERE id ='" + nodeId + "'";

            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(sqlStatement);
            while (rs.next()) {
                ms = new PhysicalResource(rs.getString("id"),
                        rs.getString("hostname"), rs.getString("hypervisor"),
                        rs.getString("disk_size_in_gigabytes"), rs.getString("cpu_cores"),
                        rs.getString("memory_in_gigabytes"), rs.getString("os"),
                        rs.getString("network_adapter"), rs.getString("public_ip_address"),
                        rs.getString("private_ip_address"), rs.getString("infrastructure_provider_id"),
                        rs.getString("active"));
            }
            return ms;
    }

    public static boolean deletePhysicalResource(Connection conn, String nodeId)  throws SQLException {
        String sqlStatement = "DELETE FROM physical_resource WHERE id ='" + nodeId + "'";;
        boolean ret = false;

            Statement st = conn.createStatement();
            ret = st.execute(sqlStatement);

        return ret;
    }

    public static ArrayList<String> getNodesId(Connection conn)  throws SQLException {
        String sqlStatement = "SELECT id FROM physical_resource";

        ArrayList<String> ret = new ArrayList<String>();

            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(sqlStatement);
            while (rs.next()) {
                ret.add(rs.getString("id"));
            }


        return ret;
    }

    public static ArrayList<String> getActiveNodesId(Connection conn)  throws SQLException {
        String sqlStatement = "SELECT id FROM physical_resource WHERE active=true";

        ArrayList<String> ret = new ArrayList<String>();

            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(sqlStatement);
            while (rs.next()) {
                ret.add(rs.getString("id"));
            }


        return ret;
    }

    /**
     * VIRTUAL RESOURCES
     */
    public static boolean insertVirtualResource(Connection conn, VirtualResource md)  throws SQLException {
        String comments = md.getComments();
        if(comments == null) {
            comments = "";
        }
        String Query = "INSERT INTO virtual_resource VALUES('" + md.getId()
                + "','" + md.getHostname() + "','" + md.getPhysical_resource_id() + "', '"
                + md.getService_id() + "', '" + md.getType() + "', '"
                + md.getHypervisor() + "', '" + md.getDisk_size_in_gigabytes()
                + "', '" + md.getCpu_cores() + "', '"
                + md.getMemory_in_gigabytes() + "', '" + md.getOs() + "', '"
                + md.getNetwork_adapter() + "', '" + md.getPublic_ip_address()
                + "', '" + md.getPrivate_ip_address() + "', '"+comments+"')";


            Statement st = conn.createStatement();
            try {
                st.executeUpdate(Query);
                st.close();
            } catch(SQLException e) {
                throw new SQLException("When querying:\n"+Query, e);
            } catch(RuntimeException e) {
                throw new RuntimeException("When querying:\n"+Query, e);
            }


        return true;
    }

    public static VirtualResources getVirtualResource(Connection conn, String id)  throws SQLException {
        String sqlStatement = null;
        sqlStatement = "SELECT id, hostname, physical_resource_id, service_id, type, hypervisor, disk_size_in_gigabytes, cpu_cores,"
                + "memory_in_gigabytes, os, network_adapter, public_ip_address, private_ip_address, comments FROM virtual_resource "
                + "WHERE id ='" + id + "'";

        VirtualResources msets = new VirtualResources();
        List<VirtualResource> list = new ArrayList<VirtualResource>();
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(sqlStatement);
            while (rs.next()) {
                VirtualResource ms = new VirtualResource(rs.getString("id"), rs.getString("hostname"),
                        rs.getString("physical_resource_id"), rs.getString("service_id"),
                        rs.getString("type"), rs.getString("hypervisor"),
                        rs.getString("disk_size_in_gigabytes"), rs.getString("cpu_cores"),
                        rs.getString("memory_in_gigabytes"), rs.getString("os"),
                        rs.getString("network_adapter"), rs.getString("public_ip_address"),
                        rs.getString("private_ip_address"), rs.getString("comments"));
                list.add(ms);
            }
            if (list.size() > 0) {
                msets.setVirtualResource(list);
            } else {
                msets = null;
            }


        return msets;
    }

    public static void activatePhysicalResource(Connection conn, String nodeId, boolean active) throws SQLException {
        String sqlStatement = "UPDATE physical_resource SET active=? where id=?";

        PreparedStatement st = conn.prepareStatement(sqlStatement);
        st.setBoolean(1,active);
        st.setString(2,nodeId);
        st.executeUpdate();
        st.close();
    }


    public static boolean updateVirtualResource(Connection conn, String vmId, String nodeId)  throws SQLException  {
        String sqlStatement = "UPDATE virtual_resource "
                + "SET physical_resource_id='" + nodeId
                + "' WHERE id ='" + vmId + "'";
        boolean ret = false;

            Statement st = conn.createStatement();
            ret = st.execute(sqlStatement);

        return ret;
    }

    public static boolean deleteVirtualResource(Connection conn, String vmId)  throws SQLException  {
        String sqlStatement = "DELETE FROM virtual_resource WHERE id ='" + vmId + "'";
        boolean ret = false;

            Statement st = conn.createStatement();
            ret = st.execute(sqlStatement);


        return ret;
    }

    public static List<String> getVMsId(Connection conn, String nodeId)  throws SQLException {
        String sqlStatement = "SELECT id FROM virtual_resource "
                + "WHERE physical_resource_id ='" + nodeId + "'";

        List<String> ret = new ArrayList<String>();

            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(sqlStatement);
            while (rs.next()) {
                ret.add(rs.getString("id"));
            }


        return ret;
    }

    public static String getNodeId(Connection conn, String vmId)  throws SQLException {
        String sqlStatement = "SELECT physical_resource_id FROM virtual_resource "
                + "WHERE id ='" + vmId + "'";

        String ret = "";

            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(sqlStatement);
            while (rs.next()) {
                ret = rs.getString("physical_resource_id");
            }


        if (ret.isEmpty()) {
            ret = "unknown";
        }
        return ret;
    }

    public static int countVirtualMachines(Connection conn) throws SQLException {
        int count = 0;
        String SQLStatement = "SELECT COUNT(*) FROM virtual_resource";
        Statement st = conn.createStatement();
        ResultSet rs = st.executeQuery(SQLStatement);

        if(rs.next()) {
            count = rs.getInt(1);
        }

        st.close();
        rs.close();
        return count;
    }

    public static int countVirtualMachines(Connection conn, String nodeId) throws SQLException {
        int count = 0;
        PreparedStatement st = conn.prepareStatement("SELECT COUNT(*) FROM virtual_resrouce WHERE physical_resource_id=?");
        st.setString(1,nodeId);
        ResultSet rs = st.executeQuery();

        if(rs.next()) {
            count = rs.getInt(1);
        }

        st.close();
        rs.close();
        return count;
    }

    public static String getVMName(Connection conn, String vmId) throws SQLException  {
        String sqlStatement = "SELECT hostname FROM virtual_resource "
                + "WHERE id ='" + vmId + "'";

        String ret = "";

            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(sqlStatement);
            while (rs.next()) {
                ret = rs.getString("hostname");
            }


        return ret;
    }

    public static String getVMPublicIP(Connection conn, String vmId)  throws SQLException {
        String sqlStatement = "SELECT public_ip_address FROM virtual_resource "
                + "WHERE id ='" + vmId + "'";

        String ret = "";

            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(sqlStatement);
            while (rs.next()) {
                ret = rs.getString("public_ip_address");
            }


        return ret;
    }

    public static String getVMId(Connection conn, String vmName)  throws SQLException {
        String sqlStatement = "SELECT id FROM virtual_resource "
                + "WHERE hostname ='" + vmName + "'";

        String ret = "";

            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(sqlStatement);
            while (rs.next()) {
                ret = rs.getString("id");
            }


        return ret;
    }

    public static String getVMServiceId(Connection conn, String vmId)  throws SQLException {
        String sqlStatement = "SELECT service_id FROM virtual_resource "
                + "WHERE id ='" + vmId + "'";

        String ret = "";

            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(sqlStatement);
            while (rs.next()) {
                ret = rs.getString("service_id");
            }


        return ret;
    }
    public static List<String> getVMsIdsOfService(Connection conn, String serviceId)  throws SQLException {
        String sqlStatement = "SELECT id FROM virtual_resource "
                + "WHERE service_id ='" + serviceId + "'";

        List<String> ret = new ArrayList<String>();

            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(sqlStatement);
            while (rs.next()) {
                ret.add(rs.getString("id"));
            }


        return ret;
    }

    public static String getIpId(Connection conn)  throws SQLException {
        String sqlStatement = "SELECT id FROM infrastructure_provider";

        String ret = "";

            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(sqlStatement);
            while (rs.next()) {
                ret = rs.getString("id");
            }

        return ret;
    }

    public static BusinessDescription getStoredBLO(Connection conn, Logger log) {
        try {

            String sqlStatement = "SELECT blo FROM infrastructure_provider";
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(sqlStatement);
            if(rs.next()) {
                ByteArrayInputStream bar = new ByteArrayInputStream(rs.getString("blo").getBytes());
                BusinessDescription bd = BLOUtils.read(bar);
                return bd;
            }
        } catch(Exception e) {
            if(log != null) { log.warn(e.getMessage()); }
        }
        return null;
    }

    public static void storeBLO(Connection conn, BusinessDescription bd, Logger log) {
        try {
            String sqlStatement = "UPDATE infrastructure_provider SET blo=?";
            PreparedStatement pst = conn.prepareStatement(sqlStatement);
            pst.setString(1,BLOUtils.toString(bd));
            pst.executeUpdate();
            pst.close();

        } catch(Exception e) {
            if(log != null) log.warn(e.getMessage());
        }
    }

    public static String getNodeIpId(Connection conn, String nodeId)  throws SQLException {
        String sqlStatement = "SELECT infrastructure_provider_id FROM physical_resource "
                + "WHERE id ='" + nodeId + "'";

        String ret = "";

            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(sqlStatement);
            while (rs.next()) {
                ret = rs.getString("infrastructure_provider_id");
            }


        return ret;
    }

    public static String getIpVmIpAddress(Connection conn, String ipId)  throws SQLException {
        String sqlStatement = "SELECT ip_vm_ip_address FROM infrastructure_provider "
                + "WHERE id ='" + ipId + "'";

        String ret = "";

            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(sqlStatement);
            while (rs.next()) {
                ret = rs.getString("ip_vm_ip_address");
            }


        return ret;
    }

    public static List<String> getRunningServiceIds(Connection conn) throws SQLException {
        List<String> ls = new ArrayList<String>();
        String q = "SELECT DISTINCT service_id FROM virtual_resource";
        Statement st = conn.createStatement();
        ResultSet rs = st.executeQuery(q);
        while(rs.next()) {
            ls.add(rs.getString(1));
        }
        return ls;
    }

    /**
     * TREC - manifest
     */
    public static boolean insertManifest(Connection conn, String serviceId, String ipManifest)  throws SQLException {
        ipManifest = ipManifest.replace("\'","\\\'");

        String Query = "INSERT INTO manifest_raw (service_id, service_manifest) VALUES(?,?)";

        PreparedStatement st = conn.prepareStatement(Query);
        st.setString(1,serviceId);
        st.setString(2,ipManifest);

        st.executeUpdate();
        st.close();

        return true;
    }
}
