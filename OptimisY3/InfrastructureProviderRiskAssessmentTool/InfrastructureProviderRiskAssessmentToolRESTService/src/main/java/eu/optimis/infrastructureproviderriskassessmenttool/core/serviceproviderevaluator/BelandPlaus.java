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

import java.util.*;

import org.apache.log4j.Logger;

public class BelandPlaus {

	protected static Logger log = Logger.getLogger(BelandPlaus.class);
	
	public DsAhpProviderObject[] computeBeliefPlaus(Set<BpaForProvidersObject> ProviderSets, Set<DsAhpProviderObject> Providers)
	{
		int numProvs = Providers.size();
		DsAhpProviderObject[] providersArray = new DsAhpProviderObject[numProvs];
		
		for(DsAhpProviderObject current: Providers)
		{
			double Pls = 0.0;
			//Set<DsAhpProviderObject> currentProvider = new HashSet<DsAhpProviderObject>();
			
			//currentProvider.clear();
			//currentProvider.add(current);
			DsAhpProviderObject processing = (DsAhpProviderObject)current.clone();
			boolean foundProv=false;
			Iterator<BpaForProvidersObject> it = ProviderSets.iterator();
			while(!foundProv && it.hasNext())
			{
			
				BpaForProvidersObject currentSet = (BpaForProvidersObject)it.next();
				if(currentSet.getProviders().size()==1 && currentSet.getProviders().contains(current) )
				{
					foundProv=true;
					processing.setBelief( currentSet.getBpa());
				}
				/*Set<DsAhpProviderObject> curr = new HashSet<DsAhpProviderObject>(currentProvider);
				if(curr.containsAll(currentSet.getProviders()))
				{
					current.setBelief( currentSet.getBpa());
					foundProv = true;
				}*/
			}
			if(!foundProv) processing.setBelief(0.0);
			Iterator<BpaForProvidersObject> it2 = ProviderSets.iterator();
			
			while(it2.hasNext())
			{
				BpaForProvidersObject currentSetPls = (BpaForProvidersObject)it2.next();
				Set<DsAhpProviderObject> nextintersect = new HashSet<DsAhpProviderObject>();
				nextintersect.clear();
				nextintersect.add(current);
				
				//if(nextintersect.retainAll(currentSetPls.getProviders())) Pls+= currentSetPls.getBpa();
				if(currentSetPls.getProviders().contains(current))Pls+= currentSetPls.getBpa();
			}
			current.setPlausibility(Pls);
			current.setBelief(processing.getBelief());
		}
		int i =0;
		for(DsAhpProviderObject prov : Providers)
		{
			providersArray[i]=prov;
			i++;
		}
		/*
		for(int j=0;j<providersArray.length; j++)
		{
			logger.debug("Provider DN: " + providersArray[j].getDistName());
			logger.debug("Provider Bel: " + providersArray[j].getBelief());
			logger.debug("Provider Plaus: " + providersArray[j].getPlausibility());
		}
		*/
		return providersArray;
		
	}
}
