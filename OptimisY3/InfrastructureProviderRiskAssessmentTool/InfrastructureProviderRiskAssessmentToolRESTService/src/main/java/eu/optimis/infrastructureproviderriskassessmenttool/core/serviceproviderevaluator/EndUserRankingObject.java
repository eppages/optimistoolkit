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
 * Specification of end-user preferences with respect to the 
 *provider criteria, past performance, experience, 
 *customer support, infrastructure, maintenance
 * and security in the form of ratings out of 10.
 * @author Iain Gourlay
 *
 *
 */
public class EndUserRankingObject {

	private double pastPerformanceRank = 0.0;
	private double businessStabilityRank = 0.0;
	private double securityRank = 0.0;
		
	/**
	 * Constructor which takes the assertReputability interface request object.
	 */
	
	public EndUserRankingObject(AssertReputability request) {
		
		this.pastPerformanceRank 	= request.getPastPerformanceRank();
		this.businessStabilityRank 		= request.getBusinessStabilityRank();
		this.securityRank 			= request.getSecurityRank();
		
	
	}

	public EndUserRankingObject() {}
	
		
	public double getBusinessStabilityRank() {
		return businessStabilityRank;
	}
	public void setBusinessStabilityRank(double businessStabilityRank)throws Exception {
		if( businessStabilityRank>=0.0 && businessStabilityRank <=10.0)
		this.businessStabilityRank = businessStabilityRank;
		else throw new Exception ("businessStabilityRank not between 0 and 10");
	}
	public double getPastPerformanceRank() {
		return pastPerformanceRank;
	}
	public void setPastPerformanceRank(double pastPerformanceRank) throws Exception {
		if( pastPerformanceRank>=0.0 && pastPerformanceRank <=10.0)
		this.pastPerformanceRank = pastPerformanceRank;
		else throw new Exception ("pastPerformanceRank not between 0 and 10");
	}
	public double getSecurityRank() {
		return securityRank;
	}
	public void setSecurityRank(double securityRank)throws Exception {
		if( securityRank>=0.0 && securityRank <=10.0)
		this.securityRank = securityRank;
		else throw new Exception ("securityRank not between 0 and 10");
	}
	
}
