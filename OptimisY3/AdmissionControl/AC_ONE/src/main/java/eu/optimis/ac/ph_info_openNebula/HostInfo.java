/*
 * Copyright (c) 2013 National Technical University of Athens (NTUA)
 *	
 *   							MIT License
 *   
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */


package eu.optimis.ac.ph_info_openNebula;

import org.apache.log4j.Logger;
import org.opennebula.client.Client;
import org.opennebula.client.OneResponse;
import org.opennebula.client.host.Host;

public class HostInfo extends Host{

	private static Logger logger = Logger.getLogger(HostInfo.class);

	//default monitoring interval 30 seconds
	//unique number assigned to Host not needed because already available through getId
	private String id_string;
	//unique name assigned to Host e.g. localhost 
	private String name;
	//http://lists.opennebula.org/pipermail/users-opennebula.org/2011-August/006126.html
	// 0 -> init, 1 -> monitoring, 2 -> monitored, 3 -> error, 4 -> disabled
	private String state;
	// image driver e.g im_kvm
	private String im_mad;
	// last monitoring time in a weird format single number e.g 1343123505
	private String last_mon_time;
	// allocated memory(KB)
	private String allocated_memory;
	// allocated cpu(100*CPUS)
	private String allocated_cpu;
	// used memory(KB)
	private String used_memory;
	// used cpu(*CPU100S)
	private String used_cpu;
	// maximum memory(KB)
	private String max_memory;
	// maximum cpu(100*CPUS)
	private String max_cpu;
	// number of running vms
	private String running_vms;
	// MHz
	private String cpu_speed;
	// hypervisor name e.g kvm
	private String hypervisor;	

	public HostInfo(int id, Client oc) {
		super(id, oc);
	}

	public OneResponse info() {
		OneResponse res = super.info();
		this.setAttributes(res);
		return res;
	}

	public void setAttributes(OneResponse res) {

		if (!res.isError()) {

			logger.debug("*********************************");
			logger.debug("*****HostInfo.setAttributes()****");
			logger.debug("*********************************");

			id_string = this.xpath("ID");
			logger.debug("Host ID: " + id_string);

			name = this.xpath("NAME");
			logger.debug("Host name: " + name);

			state = this.xpath("STATE");
			logger.debug("Host state: " + state);

			im_mad = this.xpath("IM_MAD");
			logger.debug("Host im_mad: " + im_mad);

			last_mon_time = this.xpath("LAST_MON_TIME");

			allocated_memory = this.xpath("HOST_SHARE/MEM_USAGE");
			allocated_cpu = this.xpath("HOST_SHARE/CPU_USAGE");

			used_memory = this.xpath("HOST_SHARE/USED_MEM");
			used_cpu = this.xpath("HOST_SHARE/USED_CPU");

			max_memory = this.xpath("HOST_SHARE/MAX_MEM");
			max_cpu = this.xpath("HOST_SHARE/MAX_CPU");

			running_vms = this.xpath("HOST_SHARE/RUNNING_VMS");
			logger.debug("Host running vms: " + running_vms);

			cpu_speed = this.xpath("TEMPLATE/CPUSPEED");
			logger.debug("Host cpu_speed: " + cpu_speed);

			hypervisor = this.xpath("TEMPLATE/HYPERVISOR");
			logger.debug("Host hypervisor: " + hypervisor);
		}
	}

	/**
	 * Checks if host is monitored. Only then opennebula considers host available.
	 * Also host needs to have non zero cpu as well
	 * @return is host available?
	 */
	public boolean isAvailable() {
		return ((Integer.parseInt(this.state) == 2)&&
				(Integer.parseInt(this.max_cpu)>Integer.parseInt(this.allocated_cpu)));
	}

	public String getMax_cpu() {
		return max_cpu;
	}

	public String getAllocated_cpu() {
		return allocated_cpu;
	}
	
	public String getUsed_cpu() {
		return used_cpu;
	}

	public String getMax_memory() {
		return max_memory;
	}

	public String getAllocated_memory() {
		return allocated_memory;
	}

	public String getUsed_memory() {
		return used_memory;
	}
	
	public String getRunning_vms() {
		return running_vms;
	}

}//class
