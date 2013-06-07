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
package eu.optimis.serviceproviderriskassessmenttool.core.infrastructureproviderevaluator;

import java.util.Set;

public class BpaForProvidersObject implements Cloneable{
	
public BpaForProvidersObject() {
		super();
		// TODO Auto-generated constructor stub
	}
public BpaForProvidersObject(BpaForProvidersObject b) {
		this.bpa = b.getBpa();
		this.providers = b.getProviders();
	}
public Object clone() {
		
		try {
			return super.clone();
		} catch (CloneNotSupportedException e) {
			return null;
		}
		
		
	}
	
private double bpa;
private Set<DsAhpProviderObject> providers;
public Set<DsAhpProviderObject> getProviders() {
	return providers;
}
public void setProviders(Set<DsAhpProviderObject> providers) {
	this.providers = providers;
}
public double getBpa() {
	
	return bpa;
}
public void setBpa(double bpa) {
	this.bpa = bpa;
}


}
