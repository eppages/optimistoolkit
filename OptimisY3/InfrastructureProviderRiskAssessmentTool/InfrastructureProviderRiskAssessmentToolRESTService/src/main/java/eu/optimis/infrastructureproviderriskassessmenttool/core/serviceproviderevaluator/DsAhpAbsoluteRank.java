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
public class DsAhpAbsoluteRank {
	
	public DsAhpProviderObject[] computeAbsoluteRank(UserPreferenceObject prefs, 
			DsAhpProviderObject[] providers, DsAhpProviderObject[] reference)
	{
		Set<DsAhpProviderObject> providerSet = new HashSet<DsAhpProviderObject>();
		
		DsAhpRanking ordered = new DsAhpRanking();
		
		int totalSize = providers.length+ reference.length;
		
		DsAhpProviderObject[] allProviders = new DsAhpProviderObject[totalSize];
		
		for(int i = 0; i< providers.length; i++)  
			{
				providerSet.add(providers[i]);
				allProviders[i] = providers[i];
			}
		
		for(int j=providers.length; j<totalSize; j++) 
			allProviders[j]=reference[j-providers.length];
		
		DsAhpProviderObject[] ranked = new DsAhpProviderObject[totalSize];      
		
		ranked = ordered.computeRanking(prefs, allProviders);
		
		int k = 0;
		int l = 0;
		
		DsAhpProviderObject[] requestedProvs = new DsAhpProviderObject[providers.length];
		
		while(l<providers.length && k<totalSize)
		{
			if(providerSet.contains(ranked[k]))
					{
						requestedProvs[l] =ranked[k];
						requestedProvs[l].setRating(((double)totalSize-(double)k)/(double)totalSize);
						l++;
					}
			k++;
		}
		
		return requestedProvs;
	}
	
	public DsAhpProviderObject[] computeAbsoluteRank(UserPreferenceObject prefs, 
			DsAhpProviderObject[] providers)
	{
		Set<DsAhpProviderObject> providerSet = new HashSet<DsAhpProviderObject>();
		
		DsAhpRanking ordered = new DsAhpRanking();
		
		int totalSize = providers.length;
		
		DsAhpProviderObject[] allProviders = new DsAhpProviderObject[totalSize];
		
		for(int i = 0; i< providers.length; i++)  
			{
				providerSet.add(providers[i]);
				allProviders[i] = providers[i];
			}
		
		
		
		DsAhpProviderObject[] ranked = new DsAhpProviderObject[totalSize];      
		
		ranked = ordered.computeRanking(prefs, allProviders);
		
		int k = 0;
		int l = 0;
		
		DsAhpProviderObject[] requestedProvs = new DsAhpProviderObject[providers.length];
		
		while(l<providers.length && k<totalSize)
		{
			if(providerSet.contains(ranked[k]))
					{
						requestedProvs[l] =ranked[k];
						requestedProvs[l].setRating(((double)totalSize-(double)k)/(double)totalSize);
						l++;
					}
			k++;
		}
		
		return requestedProvs;
	}
	
}
