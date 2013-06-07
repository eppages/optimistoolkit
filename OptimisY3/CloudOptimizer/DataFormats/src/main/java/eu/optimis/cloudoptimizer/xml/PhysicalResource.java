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
package eu.optimis.cloudoptimizer.xml;

import eu.optimis.cloudoptimizer.persistence.DBUtil;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "physical_resource")
public class PhysicalResource {

    private String id;
    private String hostname;
    private String hypervisor;
    private String disk_size_in_gigabytes;
    private String cpu_cores;
    private String memory_in_gigabytes;
    private String os;
    private String network_adapter;
    private String public_ip_address;
    private String private_ip_address;
    private String infrastructure_provider_id;
    private String active;

    public PhysicalResource() {}

    public PhysicalResource(String id, String hostname, String hypervisor, String disk_size_in_gigabytes, String cpu_cores, String memory_in_gigabytes, String os, String network_adapter, String public_ip_address, String private_ip_address, String infrastructure_provider_id, String active) {

        this.id = id;
        this.hostname = hostname;
        this.hypervisor = hypervisor;
        this.disk_size_in_gigabytes = disk_size_in_gigabytes;
        this.cpu_cores = cpu_cores;
        this.memory_in_gigabytes = memory_in_gigabytes;
        this.os = os;
        this.network_adapter = network_adapter;
        this.public_ip_address = public_ip_address;
        this.private_ip_address = private_ip_address;
        this.infrastructure_provider_id = infrastructure_provider_id;
        if(active != null) {
            active = active.trim();
        }
        this.active = active;
    }
    
    public String getId() {
        return id;
    }

//    public void setId(String id) {
//        this.id = id;
//    }

    public String getHostname() {
        return hostname;
    }

//    public void setHostname(String hostname) {
//        this.hostname = hostname;
//    }

    public String getHypervisor() {
        return hypervisor;
    }

//    public void setHypervisor(String hypervisor) {
//        this.hypervisor = hypervisor;
//    }

    public String getDisk_size_in_gigabytes() {
        return disk_size_in_gigabytes;
    }

//    public void setDisk_size_in_gigabytes(String disk_size_in_gigabytes) {
//        this.disk_size_in_gigabytes = disk_size_in_gigabytes;
//    }

    public String getCpu_cores() {
        return cpu_cores;
    }

//    public void setCpu_cores(String cpu_cores) {
//        this.cpu_cores = cpu_cores;
//    }

    public String getMemory_in_gigabytes() {
        return memory_in_gigabytes;
    }

//    public void setMemory_in_gigabytes(String memory_in_gigabytes) {
//        this.memory_in_gigabytes = memory_in_gigabytes;
//    }

    public String getOs() {
        return os;
    }

//    public void setOs(String os) {
//        this.os = os;
//    }

    public String getNetwork_adapter() {
        return network_adapter;
    }

//    public void setNetwork_adapter(String network_adapter) {
//        this.network_adapter = network_adapter;
//    }

    public String getPublic_ip_address() {
        return public_ip_address;
    }

//    public void setPublic_ip_address(String public_ip_address) {
//        this.public_ip_address = public_ip_address;
//    }

    public String getPrivate_ip_address() {
        return private_ip_address;
    }

//    public void setPrivate_ip_address(String private_ip_address) {
//        this.private_ip_address = private_ip_address;
//    }

    public String getInfrastructure_provider_id() {
        return infrastructure_provider_id;
    }

//    public void setInfrastructure_provider_id(String infrastructure_provider_id) {
//        this.infrastructure_provider_id = infrastructure_provider_id;
//    }

    public boolean isActive() {
        return active == null || // if no "active" information, we assume default value is 'active'                
                !("0".equals(active) || "false".equalsIgnoreCase(active));
                
    }

    /**public void setActive(boolean active) {
        this.active = active ? "true" : "false";

        Connection c = DBUtil.getConnection();
        String sqlStatement = "UPDATE physical_resource SET active=? WHERE id =?;";
        try {
            PreparedStatement pt = c.prepareStatement(sqlStatement);
            pt.setBoolean(1, isActive());
            pt.setString(2, id);
            pt.executeUpdate();
            c.close();
        } catch(Exception ex) {
            try { c.close(); } catch(Exception e) {}
            // if no access to active data, assuming "active" as default
            this.active = "true";
            throw new RuntimeException(ex.getMessage(), ex);
        }
        
    } */
    
    

    // @XmlAttribute
    public String toXml() {
        StringBuilder xml = new StringBuilder();
        xml.append("<physical_resource>");
        xml.append("<id>").append(this.id).append("</id>");
        xml.append("<hostname>").append(this.hostname).append("</hostname>");
        xml.append("<hypervisor>").append(this.hypervisor).append("</hypervisor>");
        xml.append("<disk_size_in_gigabytes>").append(this.disk_size_in_gigabytes).append("</disk_size_in_gigabytes>");
        xml.append("<cpu_cores>").append(this.cpu_cores).append("</cpu_cores>");
        xml.append("<memory_in_gigabytes>").append(this.memory_in_gigabytes).append("</memory_in_gigabytes>");
        xml.append("<os>").append(this.os).append("</os>");
        xml.append("<network_adapter>").append(this.network_adapter).append("</network_adapter>");
        xml.append("<public_ip_address>").append(this.public_ip_address).append("</public_ip_address>");
        xml.append("<private_ip_address>").append(this.private_ip_address).append("</private_ip_address>");
        xml.append("<infrastructure_provider_id>").append(infrastructure_provider_id).append("</infrastructure_provider_id>");
        xml.append("<active>").append(active).append("</active>");
        xml.append("</physical_resource>");

        return xml.toString();
    }
    
    public String toString() {
        return toXml();
    }
    
}
