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
package eu.optimis.infrastructureproviderriskassessmenttool.core.serviceproviderevaluator;

/**
 *
 * @author scsmj
 */
public class AssertReputability {

	private ProvidersType providersType;
	private double pastPerformanceRank;
	private	double businessStabilityRank;
	private	double securityRank;
	

	public AssertReputability(ProvidersType providersType, double pastPerformanceRank, double businessStabilityRank, double securityRank){
		this.providersType = providersType;
		this.pastPerformanceRank= pastPerformanceRank;
		this.businessStabilityRank = businessStabilityRank;
		this.securityRank = securityRank;
	}

	public void setPastPerformanceRank(double pastPerformanceRank){
		this.pastPerformanceRank= pastPerformanceRank;
	}
	public void setBusinessStabilityRank(double businessStabilityRank){
		this.businessStabilityRank= businessStabilityRank;
	}
	public void setSecurityRank(double securityRank){
		this.securityRank= securityRank;
	}
	public void setProvidersType(ProvidersType providersType){
		this.providersType = providersType;
	}

	public double getPastPerformanceRank(){
		return this.pastPerformanceRank;
	}
	public double getBusinessStabilityRank(){
		return this.businessStabilityRank;
	}
	public double getSecurityRank(){
		return this.securityRank;
	}
	public ProvidersType getProvidersType(){
		return this.providersType;
	}
}