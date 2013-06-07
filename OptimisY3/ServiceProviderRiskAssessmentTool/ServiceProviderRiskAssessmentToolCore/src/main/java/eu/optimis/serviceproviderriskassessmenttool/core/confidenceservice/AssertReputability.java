/*
 *  Copyright 2013 University of Leeds
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */


package eu.optimis.serviceproviderriskassessmenttool.core.confidenceservice;

/**
 *
 * @author scsmj
 */
public class AssertReputability {

	private ProvidersType providersType;
	private double pastPerformanceRank;
	private	double maintenanceRank;
	private	double securityRank;
	private	double customerSupportRank;
	private double infrastructureRank;
	private double experienceRank;

	public AssertReputability(ProvidersType providersType, double pastPerformanceRank, double maintenanceRank, double securityRank, double customerSupportRank, double infrastructureRank, double experienceRank){
		this.providersType = providersType;
		this.pastPerformanceRank= pastPerformanceRank;
		this.maintenanceRank = maintenanceRank;
		this.securityRank = securityRank;
		this.customerSupportRank = customerSupportRank;
		this.infrastructureRank = infrastructureRank;
		this.experienceRank =experienceRank;
	}

	public void setPastPerformanceRank(double pastPerformanceRank){
		this.pastPerformanceRank= pastPerformanceRank;
	}
	public void setMaintenanceRank(double maintenanceRank){
		this.maintenanceRank= maintenanceRank;
	}
	public void setSecurityRank(double securityRank){
		this.securityRank= securityRank;
	}
	public void setCustomerSupportRank(double customerSupportRank){
		this.customerSupportRank= customerSupportRank;
	}
	public void setInfrastructureRank(double infrastructureRank){
		this.infrastructureRank= infrastructureRank;
	}
	public void setExperienceRank(double experienceRank){
		this.experienceRank= experienceRank;
	}
	public void setProvidersType(ProvidersType providersType){
		this.providersType = providersType;
	}

	public double getPastPerformanceRank(){
		return this.pastPerformanceRank;
	}
	public double getMaintenanceRank(){
		return this.maintenanceRank;
	}
	public double getSecurityRank(){
		return this.securityRank;
	}
	public double getCustomerSupportRank(){
		return this.customerSupportRank;
	}
	public double getInfrastructureRank(){
		return this.infrastructureRank;
	}
	public double getExperienceRank(){
		return this.experienceRank;
	}
	public ProvidersType getProvidersType(){
		return this.providersType;
	}
}