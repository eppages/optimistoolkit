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
 * Provides the functionality to combine sets of decision alternatives computed for different
 * criteria (using DsBpas.java) into a single set. For example, a set of decision alternatives
 * may be derived based on the information about the maintenance criterion and another set
 * based on the information about past performance. These can be combined into a single set,
 * taking account of the information about both past performance and maintenance.
 * @author Iain Gourlay
 */
import java.util.*;

public class DsRuleOfCombination  {

	public Set<BpaForProvidersObject> combineBpaSets
	(Set<BpaForProvidersObject> firstSet, Set<BpaForProvidersObject> secondSet)
	{
		Set<BpaForProvidersObject> combined = new HashSet<BpaForProvidersObject>();

		int size = firstSet.size()*secondSet.size();
		
		BpaForProvidersObject[] firstProvs = new BpaForProvidersObject[size];
		BpaForProvidersObject[] secondProvs = new BpaForProvidersObject[secondSet.size()];
		
		int i = 0;
		int j = 0;
		double sumEmpty = 0.0;
		
		for( BpaForProvidersObject currentProvidersFirst : firstSet)
		{
			j=0;
			for( BpaForProvidersObject currentProvidersSecond: secondSet)
			{
				
				
				
				//Set<DsAhpProviderObject> firstProvs = new HashSet<DsAhpProviderObject> ();
					
					//BpaForProvidersObject temp1 = (BpaForProvidersObject) currentProvidersFirst.clone();
				int s = i*secondSet.size()+j;
			BpaForProvidersObject first = new BpaForProvidersObject(currentProvidersFirst);
				firstProvs[s] = (BpaForProvidersObject)first;
				secondProvs[j] = (BpaForProvidersObject)currentProvidersSecond;
			
					Set<DsAhpProviderObject> intersection = new HashSet<DsAhpProviderObject>(firstProvs[s].getProviders());
					intersection.retainAll(secondProvs[j].getProviders());
				
				
				
				
				if(intersection.isEmpty())
				{
					
					sumEmpty+= currentProvidersFirst.getBpa()*currentProvidersSecond.getBpa(); 
					
				}
				else
				{
					firstProvs[s].setBpa(currentProvidersFirst.getBpa()*currentProvidersSecond.getBpa());
				
				}
				firstProvs[s].setProviders(intersection);
			
				j++;
			}
			i++;
		}
		
		
		boolean[] processed = new boolean[size];
		for (int n=0; n<size; n++) processed[n]=false;
		for(int l = 0; l<size; l++)
		{
			if(!firstProvs[l].getProviders().isEmpty()&&processed[l]==false)
			{
				
				
				for(int m = l+1; m<size; m++)
				{
					if(!firstProvs[m].getProviders().isEmpty()&& processed[m]==false)
					{
						Set<DsAhpProviderObject> ds1 = firstProvs[l].getProviders();
						Set<DsAhpProviderObject> ds2 = firstProvs[m].getProviders();
						Set<DsAhpProviderObject> symmetricDiff = new HashSet<DsAhpProviderObject>(ds1);
						symmetricDiff.addAll(ds2);
						Set<DsAhpProviderObject> tmp = new HashSet<DsAhpProviderObject>(ds1);
						tmp.retainAll(ds2);
						symmetricDiff.removeAll(tmp);
						if(symmetricDiff.isEmpty()) 
						{
							firstProvs[l].setBpa(firstProvs[l].getBpa()+firstProvs[m].getBpa());
							processed[m]=true;
						}
					}
				}
			}
		}
		for(int p = 0; p<size; p++)
		{
			if(!firstProvs[p].getProviders().isEmpty()&& processed[p]==false)
			{
				firstProvs[p].setBpa(firstProvs[p].getBpa()/(1.0-sumEmpty));
				combined.add(firstProvs[p]);
			}
			
		}
		
	
		return combined;
	}
}
