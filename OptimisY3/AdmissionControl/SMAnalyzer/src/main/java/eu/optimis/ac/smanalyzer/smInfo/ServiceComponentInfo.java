/*
 * Copyright (c) 2013 National Technical University of Athens (NTUA)
 *	
 *   							MIT License
 *   
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package eu.optimis.ac.smanalyzer.smInfo;

public class ServiceComponentInfo
{
	private String id;
	private String baseVms;
	private String elasticVms;
	private int virtualCpus;
	
        private int memoryInMBs;
        
	private String affinityConstraints;
	private String antiAffinityConstraints;
	
	public ServiceComponentInfo() {}
	
	protected void setId(String ident) {
		id = ident;
	}
	
        protected void setBaseVms(String baseVms_) {
		baseVms = baseVms_;
	}
	
	protected void setElasticVms(String elasticVms_) {
		elasticVms = elasticVms_;
	}
	
        protected void setVirtualCpus(int virt_cpus) {
		virtualCpus = virt_cpus;
	}
	
        protected void setMemoryInMBs(int MemoryInMBs) {
		memoryInMBs = MemoryInMBs;
	}
        
	public String getId() {
		return id;
	}
	
        public String getBaseVms() {
		return baseVms;
	}
	
	public String getElasticVms() {
		return elasticVms;
	}
			
	public int getVirtualCpus() {
		return virtualCpus;
	}
	
        public int getMemoryInMBs() {
		return memoryInMBs;
	}
        
	protected void setAffinityConstraints(String AffinityConstraints) {
		affinityConstraints = AffinityConstraints;
	}//setAffinityConstraints()
	
	public String getAffinityConstraints() {
		return affinityConstraints;
	}//getAffinityConstraints()
        
        protected void setAntiAffinityConstraints(String AntiAffinityConstraints) {
		antiAffinityConstraints = AntiAffinityConstraints;
	}//setAntiAffinityConstraints()
	
	public String getAntiAffinityConstraints() {
		return antiAffinityConstraints;
	}//getAntiAffinityConstraints()
        
}//ServiceComponentInfo
