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

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "virtual_resource")
public class VirtualResource {

    private String id;
    private String hostname;
    private String physical_resource_id;
    private String service_id;
    private String type; //requirement from AC {basic, elastic},
    private String hypervisor;
    private String disk_size_in_gigabytes;
    private String cpu_cores;
    private String memory_in_gigabytes;
    private String os;
    private String network_adapter;
    private String public_ip_address;
    private String private_ip_address;
    private String comments;


    public VirtualResource() {
    }

    public VirtualResource(String id, String hostname, String physical_resource_id, String service_id, String type, String hypervisor, String disk_size_in_gigabytes, String cpu_cores, String memory_in_gigabytes, String os, String network_adapter, String public_ip_address, String private_ip_address) {

        this.id = id;
        this.hostname = hostname;
        this.physical_resource_id = physical_resource_id;
        this.service_id = service_id;
        this.type = type;
        this.hypervisor = hypervisor;
        this.disk_size_in_gigabytes = disk_size_in_gigabytes;
        this.cpu_cores = cpu_cores;
        this.memory_in_gigabytes = memory_in_gigabytes;
        this.os = os;
        this.network_adapter = network_adapter;
        this.public_ip_address = public_ip_address;
        this.private_ip_address = private_ip_address;
    }

    public VirtualResource(String id, String hostname, String physical_resource_id, String service_id, String type, String hypervisor, String disk_size_in_gigabytes, String cpu_cores, String memory_in_gigabytes, String os, String network_adapter, String public_ip_address, String private_ip_address, String comments) {
        this(id,hostname,physical_resource_id,service_id,type,hypervisor,disk_size_in_gigabytes,cpu_cores,memory_in_gigabytes,os,network_adapter,public_ip_address,private_ip_address);

        if(comments != null) {
            this.comments = comments.replace("<","&lt;").replace(">","&gt;");
        }
    }

    public String getId() {
        return id;
    }

    public String getPhysical_resource_id() {
        return physical_resource_id;
    }

    public String getService_id() {
        return service_id;
    }

    public String getHostname() {
        return hostname;
    }

    public String getType() {
        return type;
    }

    public String getHypervisor() {
        return hypervisor;
    }

    public String getDisk_size_in_gigabytes() {
        return disk_size_in_gigabytes;
    }

    public String getCpu_cores() {
        return cpu_cores;
    }

    public String getMemory_in_gigabytes() {
        return memory_in_gigabytes;
    }

    public String getOs() {
        return os;
    }

    public String getNetwork_adapter() {
        return network_adapter;
    }

    public String getPublic_ip_address() {
        return public_ip_address;
    }

    public String getPrivate_ip_address() {
        return private_ip_address;
    }

    public String getComments() {
        return comments;
    }

    // @XmlAttribute
    public String toXml() {
        StringBuilder xml = new StringBuilder();
        xml.append("<virtual_resource>");
        xml.append("<id>").append(this.id).append("</id>");
        xml.append("<hostname>").append(this.hostname).append("</hostname>");
        xml.append("<physical_resource_id>").append(this.physical_resource_id).append("</physical_resource_id>");
        xml.append("<service_id>").append(this.service_id).append("</service_id>");
        xml.append("<type>").append(this.type).append("</type>");
        xml.append("<hypervisor>").append(this.hypervisor).append("</hypervisor>");
        xml.append("<disk_size_in_gigabytes>").append(this.disk_size_in_gigabytes).append("</disk_size_in_gigabytes>");
        xml.append("<cpu_cores>").append(this.cpu_cores).append("</cpu_cores>");
        xml.append("<memory_in_gigabytes>").append(this.memory_in_gigabytes).append("</memory_in_gigabytes>");
        xml.append("<os>").append(this.os).append("</os>");
        xml.append("<network_adapter>").append(this.network_adapter).append("</network_adapter>");
        xml.append("<public_ip_address>").append(this.public_ip_address).append("</public_ip_address>");
        xml.append("<private_ip_address>").append(this.private_ip_address).append("</private_ip_address>");
        xml.append("<comments>").append(this.comments).append("</comments>");
        xml.append("</virtual_resource>");

        return xml.toString();
    }

    @Override
    public String toString() {
        return toXml();
    }
}
