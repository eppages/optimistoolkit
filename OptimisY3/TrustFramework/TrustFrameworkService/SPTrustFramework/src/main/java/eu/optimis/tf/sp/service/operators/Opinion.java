/**

Copyright 2013 ATOS SPAIN S.A. and City University London

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
Pramod Pawar. City University London
Juan Luis Prieto, Francisco Javier Nieto. Atos Research and Innovation, Atos SPAIN SA
@email francisco.nieto@atosresearch.eu 

**/

package eu.optimis.tf.sp.service.operators;

import org.apache.log4j.Logger;


public class Opinion {
	private double belief;
	private double disBelief;
	private double unCertainty;
	private double relativeAtomicity;
	private double expectation;
	private Logger log = Logger.getLogger(this.getClass());
		
	public Opinion(){
		this.belief = 0.0;
		this.disBelief = 0.0;
		this.unCertainty =0.0;
		this.relativeAtomicity = 0.5;
		this.expectation = 0.0;
	}
		
	/**
	 * 
	 * @param r
	 * Positive evidence to the opinion model 
	 * @param s
	 * Negative evidence to the opinion model
	 */
	public Opinion (double r , double s){
		this.belief = r / (r + s + 2);
		this.disBelief = s / (r + s+ 2);
		this.unCertainty = 2 / (r + s +2);
		this.relativeAtomicity = 0.5f;
		this.expectation = this.belief + this.relativeAtomicity * this.unCertainty ;
		
	}
		
	public double getBelief(){
		return this.belief ;
	}
	
	public double getDisBelief(){
		return this.disBelief ;
	}
	
	public double getUnCertainty(){
		return this.unCertainty ;
	}
	
	public double getRelativeAtomicity(){
		return this.relativeAtomicity ;
	}
	
	public double getExpectation(){
		return this.expectation ;
	}
	

	public void setBelief(double belief){
		this.belief = belief;
	}
	
	public void setDisBelief(double disBelief){
		this.disBelief = disBelief ;
	}
	
	public void setUnCertainty(double unCertainty){
		this.unCertainty = unCertainty;
	}
	
	public void setRelativeAtomicity(double relativeAtomicity ){
		this.relativeAtomicity = relativeAtomicity ;
	}
	
	public void setExpectation(){
		this.expectation = this.belief + this.relativeAtomicity * this.unCertainty ;
	}

	public void displayOpinion(){
		log.info("W("+this.belief +  "," +
				this.disBelief + "," +
				this.unCertainty + "," + 
				this.relativeAtomicity + 
				")" + "\tExpectation :" + this.expectation);
	}
	
}