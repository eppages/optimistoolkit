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

/**
 * Provides the functionality to sort an array of providers, represented as
 * DsAhpProviderObjects, according to their belief and plausibility values.
 * @author Iain Gourlay
 */
public class DsOrderProviders {
	public DsAhpProviderObject[] reorderProviders(DsAhpProviderObject[] providers)
	{
		for(int i = providers.length-1; i>=0; i--)
		{
			int highestIndex = i;
			for(int j = i; j>=0; j--)
			{
				if(preference(providers[j], providers[highestIndex])<0.5)
					highestIndex=j;
			}
			DsAhpProviderObject temp = providers[i];
			providers[i] = providers[highestIndex];
			providers[highestIndex] = temp;
			
		}
		return providers;
	}
	
	private double preference(DsAhpProviderObject prov1, DsAhpProviderObject prov2)
	{
		double pref = 0.0;
		double pls1bel2 = prov1.getPlausibility()-prov2.getBelief();
		if(pls1bel2<0.0) pls1bel2 = 0.0;
		double bel1pls2 = prov1.getBelief()-prov2.getPlausibility();
		if(bel1pls2<0.0) bel1pls2 = 0.0;
		double pls1bel1 = prov1.getPlausibility()-prov1.getBelief();
		double pls2bel2 = prov2.getPlausibility()-prov2.getBelief();
		pref = (pls1bel2 - bel1pls2)/(pls1bel1+pls2bel2);
		return pref;
	}

}
