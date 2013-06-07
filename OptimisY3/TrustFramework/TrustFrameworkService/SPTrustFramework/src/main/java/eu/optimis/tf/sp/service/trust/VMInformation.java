/**

Copyright 2013 ATOS SPAIN S.A.

Licensed under the Apache License, Version 2.0 (the License);
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

Authors :
Juan Luis Prieto, Francisco Javier Nieto. Atos Research and Innovation, Atos SPAIN SA
@email francisco.nieto@atosresearch.eu 

**/

package eu.optimis.tf.sp.service.trust;

import org.apache.log4j.Logger;

public class VMInformation {
	
	private String serviceId;
	private String vmId;
	private String virtualResourceId;
	private double cpu_usage;
	private double cpu_asked;
	private double cpu_allocated;
	private double memory_used;
	private double memory_asked;
	private double memory_allocated;
	private double disk_asked;
	private double disk_allocated;
	private double disk_used;
	private double num_cpu_asked;
	private double num_cpu_allocated;
	private Logger log = Logger.getLogger(this.getClass().getName());
	
	public VMInformation(String serviceId, String vmId, String virtualResourceId){
		this.serviceId = serviceId;
		this.vmId = vmId;
		this.virtualResourceId = virtualResourceId;
	}
	
	public String getServiceId() {
		return serviceId;
	}
	public String getVmId() {
		return vmId;
	}
	public String getVirtualResourceId() {
		return virtualResourceId;
	}
	public double getCpu_usage() {
		return cpu_usage;
	}
	public void setCpu_usage(double cpu_usage) {
		this.cpu_usage = cpu_usage;
	}
	public double getCpu_asked() {
		return cpu_asked;
	}
	public void setCpu_asked(double cpu_asked) {
		this.cpu_asked = cpu_asked;
	}
	public double getCpu_allocated() {
		return cpu_allocated;
	}
	public void setCpu_allocated(double cpu_allocated) {
		this.cpu_allocated = cpu_allocated;
	}
	public double getMemory_used() {
		return memory_used;
	}
	public void setMemory_used(double memory_used) {
		this.memory_used = memory_used;
	}
	public double getMemory_asked() {
		return memory_asked;
	}
	public void setMemory_asked(double memory_asked) {
		this.memory_asked = memory_asked;
	}
	public double getMemory_allocated() {
		return memory_allocated;
	}
	public void setMemory_allocated(double memory_allocated) {
		this.memory_allocated = memory_allocated;
	}
	public double getDisk_asked() {
		return disk_asked;
	}
	public void setDisk_asked(double disk_asked) {
		this.disk_asked = disk_asked;
	}
	public double getDisk_allocated() {
		return disk_allocated;
	}
	public void setDisk_allocated(double disk_allocated) {
		this.disk_allocated = disk_allocated;
	}
	public double getDisk_used() {
		return disk_used;
	}
	public void setDisk_used(double disk_used) {
		this.disk_used = disk_used;
	}

	public double getNum_cpu_allocated() {
		return num_cpu_allocated;
	}

	public void setNum_cpu_allocated(double num_cpu_allocated) {
		this.num_cpu_allocated = num_cpu_allocated;
	}

	public double getNum_cpu_asked() {
		return num_cpu_asked;
	}

	public void setNum_cpu_asked(double num_cpu_asked) {
		this.num_cpu_asked = num_cpu_asked;
	}
	
	public void printContent(){
		log.info("service id: "+serviceId);
		log.info("vm id: "+vmId);
		log.info("virtual resource id: "+virtualResourceId);
		log.info("cpu usage: "+cpu_usage);
		log.info("cpu asked: "+cpu_asked);
		log.info("cpu allocated: "+cpu_allocated);
		log.info("memory used: "+memory_used);
		log.info("memory asked: "+memory_asked);
		log.info("memory allocated: "+memory_allocated);
		log.info("disk asked: "+disk_asked);
		log.info("disk allocated: "+disk_allocated);
		log.info("disk used: "+disk_used);
		log.info("num cpu asked: "+num_cpu_asked);
		log.info("num cpu allocated: "+num_cpu_allocated);
	}
	
}
