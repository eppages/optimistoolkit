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
 * Specification of an EndUserPreferenceObject containing 
 *the end-user's pairwise preferences with respect to the 
 *provider criteria, past performance, experience, 
 *customer support, infrastructure, maintenance and security.
 * @author Iain Gourlay
 *
 *
 */

public class UserPreferenceObject {
	private double pastVexp=1.0;
	private double pastVcust=1.0;
	private double pastVsec=1.0; 
	private double pastVinf=1.0; 
	private double expVcust=1.0;
	private double expVsec=1.0;
	private double expVinf=1.0;
	private double custVsec=1.0; 
	private double custVinf=1.0;
	private double secVinf=1.0;
	private double pastVbizstab=1.0;
	private double expVmaint=1.0;
	private double custVmaint=1.0;
	private double infVmaint=1.0;
	private double secVbizstab = 1.0;
	/**
	 * Accessor for the pairwise comparison value for security
	 * and bizstab.
	 * @return secVmaint a double greater than 1 if security is more 
	 * important and less than 1 (greater than 0) if maintenance is more 
	 * important.
	 */
	public double getSecVBizstab() {
		return secVbizstab;
	}
	public void setSecVBizstab(double secVBizstab) {
		this.secVbizstab = secVBizstab;
	}
	/**
	 * Accessor for the pairwise comparison value for customer support 
	 * and maintenance.
	 * @return custVmaint a double greater than 1 if customer support is more 
	 * important and less than 1 (greater than 0) if maintenance is more 
	 * important.
	 */
	public double getCustVmaint() {
		return custVmaint;
	}
	public void setCustVmaint(double custVmaint) {
		this.custVmaint = custVmaint;
	}
	/**
	 * Accessor for the pairwise comparison value for experience
	 * and maintenance.
	 * @return expVmaint a double greater than 1 if experience is more 
	 * important and less than 1 (greater than 0) if maintenance is more 
	 * important.
	 */
	public double getExpVmaint() {
		return expVmaint;
	}
	public void setExpVmaint(double expVmaint) {
		this.expVmaint = expVmaint;
	}
	/**
	 * Accessor for the pairwise comparison value for infrastructure
	 * and maintenance.
	 * @return infVmaint a double greater than 1 if infrastructure is more 
	 * important and less than 1 (greater than 0) if maintenance is more 
	 * important.
	 */
	public double getInfVmaint() {
		return infVmaint;
	}
	public void setInfVmaint(double infVmaint) {
		this.infVmaint = infVmaint;
	}
	/**
	 * Accessor for the pairwise comparison value for past performance
	 * and maintenance.
	 * @return pastVmaint a double greater than 1 if past performance is more 
	 * important and less than 1 (greater than 0) if maintenance is more 
	 * important.
	 */
	public double getPastVBizstab() {
		return pastVbizstab;
	}
	public void setPastVBizstab(double pastVbizstab) {
		this.pastVbizstab = pastVbizstab;
	}
	/**
	 * Accessor for the pairwise comparison value for customer support 
	 * and infrastructure.
	 * @return custVinf a double greater than 1 if customer support is more 
	 * important and less than 1 (greater than 0) if infrastructure is more 
	 * important.
	 */
	public double getCustVinf() {
		return custVinf;
	}
	public void setCustVinf (double custVinf) throws Exception{
		if(custVinf >0.0) this.custVinf = custVinf;
		else throw new Exception ("custVinf is not greater than zero");
	}
	/**
	 * Accessor for the pairwise comparison value for customer support 
	 * and security.
	 * @return custVsec a double greater than 1 if customer support is more 
	 * important and less than 1 (greater than 0) if security is more 
	 * important.
	 */
	public double getCustVsec() {
		return custVsec;
	}
	public void setCustVsec(double custVsec) throws Exception {
		if (custVsec >0.0) this.custVsec = custVsec;
		else throw new Exception ("custVsec is not greater than zero");
	}
	/**
	 * Accessor for the pairwise comparison value for experience 
	 * and customer support.
	 * @return expVcust a double greater than 1 if experience is more 
	 * important and less than 1 (greater than 0) if customer support is more 
	 * important.
	 */
	public double getExpVcust() {
		return expVcust;
	}
	public void setExpVcust(double expVcust) throws Exception {
		if (expVcust >0.0) this.expVcust = expVcust;
		else throw new Exception("expVcust is not greater than zero");
	}
	/**
	 * Accessor for the pairwise comparison value for experience 
	 * and infrastructure.
	 * @return expVinf a double greater than 1 if experience is more 
	 * important and less than 1 (greater than 0) if infrastructure is more 
	 * important.
	 */
	public double getExpVinf() {
		return expVinf;
	}
	public void setExpVinf(double expVinf) throws Exception {
		if (expVinf >0.0) this.expVinf = expVinf;
		else throw new Exception("expVinf is not greater than zero");
	}
	/**
	 * Accessor for the pairwise comparison value for experience 
	 * and security.
	 * @return expVsec a double greater than 1 if experience is more 
	 * important and less than 1 (greater than 0) if security is more 
	 * important.
	 */
	public double getExpVsec() {
		return expVsec;
	}
	public void setExpVsec(double expVsec)throws Exception {
		if (expVsec >0.0) this.expVsec = expVsec;
		else throw new Exception ("expVsec is not greater than zero");
	}
	/**
	 * Accessor for the pairwise comparison value for past performance 
	 * and customer support.
	 * @return pastVcust a double greater than 1 if past performance is more 
	 * important and less than 1 (greater than 0) if customer support is more 
	 * important.
	 */
	public double getPastVcust() {
		return pastVcust;
	}
	public void setPastVcust(double pastVcust) throws Exception{
		if (pastVcust >0.0) this.pastVcust = pastVcust;
		else throw new Exception ("pastVcust is not greater than zero");
	}
	/**
	 * Accessor for the pairwise comparison value for past performance 
	 * and experience.
	 * @return pastVexp a double greater than 1 if past performance is more 
	 * important and less than 1 (greater than 0) if experience is more 
	 * important.
	 */
	public double getPastVexp() {
		return pastVexp;
	}
	public void setPastVexp(double pastVexp) throws Exception{
		if (pastVexp > 0.0) this.pastVexp = pastVexp;
		else throw new Exception ("pastVexp is not greater than zero");
	}
	/**
	 * Accessor for the pairwise comparison value for past performance 
	 * and infrastructure.
	 * @return pastVinf a double greater than 1 if past performance is more 
	 * important and less than 1 (greater than 0) if infrastructure is more 
	 * important.
	 */
	public double getPastVinf() {
		return pastVinf;
	}
	public void setPastVinf(double pastVinf) throws Exception {
		if (pastVinf > 0.0) this.pastVinf = pastVinf;
		else throw new Exception ("pastVinf is not greater than zero");
	}
	/**
	 * Accessor for the pairwise comparison value for past performance 
	 * and security.
	 * @return pastVsec a double greater than 1 if past performance is more 
	 * important and less than 1 (greater than 0) if security is more 
	 * important.
	 */
	public double getPastVsec() {
		return pastVsec;
	}
	public void setPastVsec(double pastVsec)throws Exception {
		if(pastVsec > 0.0) this.pastVsec = pastVsec;
		else throw new Exception ("pastVsec is not greater than zero");
	}
	/**
	 * Accessor for the pairwise comparison value for security 
	 * and infrastructure.
	 * @return secVinf a double greater than 1 if security is more 
	 * important and less than 1 (greater than 0) if infrastructure is more 
	 * important.
	 */
	public double getSecVinf() {
		return secVinf;
	}
	public void setSecVinf(double secVinf)throws Exception {
		if(secVinf > 0.0) this.secVinf = secVinf;
		else throw new Exception ("secVinf is not greater than zero");
	}
	
}
