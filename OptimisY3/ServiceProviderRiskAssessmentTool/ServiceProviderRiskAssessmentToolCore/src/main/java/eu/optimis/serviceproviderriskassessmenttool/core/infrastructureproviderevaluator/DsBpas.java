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

import java.util.*;
/**
 * Provides the functionality to create a set of decision alternatives with corresponding
 * basic probability assignments (bpas) as required for DS-AHP. Here, a decision alternative is 
 * a set of providers, corresponding to the proposition, "the providers in this set are to be
 * preferred over other alternatives but not over each other"
 * @author Iain Gourlay
 */
public class DsBpas {

	public Set<BpaForProvidersObject> createBpaSets(Map<Integer,Set<DsAhpProviderObject>> prefMap, double weight,
			DsAhpProviderObject[] providers)
	{
		Map<Double, Set<DsAhpProviderObject>> bpaMap = new HashMap<Double,Set<DsAhpProviderObject>>();
		bpaMap	= createBpaMap( prefMap, weight,providers);
		Set<BpaForProvidersObject> bpaProvs = new HashSet<BpaForProvidersObject>();
		for(Double key : bpaMap.keySet())
		{
			BpaForProvidersObject currentProviderSet = new BpaForProvidersObject();
			Set<DsAhpProviderObject> currentProvs = new HashSet<DsAhpProviderObject>();
			currentProvs = bpaMap.get(key);
			currentProviderSet.setProviders(currentProvs);
			if(key<1.0)
			{
				double currentBpa = key;
				currentProviderSet.setBpa(currentBpa);
	
			}
			else
			{
				double currentBpa = key-1.0;
				currentProviderSet.setBpa(currentBpa);
			}
			bpaProvs.add(currentProviderSet);
		}
		return bpaProvs;
	}

	public Map<Double,Set<DsAhpProviderObject>> createBpaMap(Map<Integer,Set<DsAhpProviderObject>> prefMap, double weight,
			DsAhpProviderObject[] providers)
	{
		Map<Double,Set<DsAhpProviderObject>> bpaMap = new HashMap<Double,Set<DsAhpProviderObject>>();
		
		double sumA=computeSumA(prefMap, weight);
	    int sizeOfPrefMatrix = prefMap.size();
		if(prefMap.keySet().contains(1)&&prefMap.keySet().contains(-1)) sizeOfPrefMatrix--;
		for(Integer key : prefMap.keySet())
		{
			double prefFromKey = 0.0;
			if (key>0) prefFromKey = (double)key;
			else prefFromKey = 1.0/(-(double)key);
		
			double currBpa = prefFromKey*weight/(sumA+Math.sqrt((double)sizeOfPrefMatrix)); 
			
			if(!bpaMap.containsKey(currBpa))bpaMap.put(currBpa, prefMap.get(key));
			else
			{
				HashSet<DsAhpProviderObject> tempSet = new HashSet<DsAhpProviderObject>();
				tempSet = (HashSet<DsAhpProviderObject>) bpaMap.get(currBpa);
				for(DsAhpProviderObject prov : prefMap.get(key)) tempSet.add(prov);
				bpaMap.put(currBpa, tempSet);
				
			}
			// tempSet=prefMap.get(key);
			
			
			
		}
		HashSet<DsAhpProviderObject> frameOfDiscernment = new HashSet<DsAhpProviderObject>();
		for(int i=0;i<providers.length;i++)
		{
			frameOfDiscernment.add(providers[i]);
		}
		double frameBpa = (Math.sqrt((double)sizeOfPrefMatrix)/(sumA+Math.sqrt((double)sizeOfPrefMatrix)));
		bpaMap.put(frameBpa+1.0, frameOfDiscernment);
		
		return bpaMap;
	}
	public double computeSumA(Map<Integer,Set<DsAhpProviderObject>> prefMap, double weight)
	{
		double sumA=0.0;
		for (Integer key : prefMap.keySet())
		{
			int prefInt = key;
			if(prefInt>0) sumA += (double)prefInt;
			else 
			{
				prefInt=-prefInt;
				double actualPref = 1.0/(double)prefInt;
				sumA += actualPref;
			}
		}
		if(prefMap.containsKey(1)&&prefMap.containsKey(-1)) sumA = sumA-1.0;
		sumA = sumA*weight;
		
		
		return sumA;
	}
	
}
