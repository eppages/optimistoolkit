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
/**
 * Provides the functionality to create a Map of decision alternative, which are 
 * sets of provider objects, with key based on their preference values with respect 
 * to a particular criterion. 
 * @author Iain Gourlay
 */
public class MapPreferences {

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Map<Integer,Set<DsAhpProviderObject>> createPrefMap(DsAhpProviderObject[] providers, String criterion)
	{
		Map<Integer,Set<DsAhpProviderObject>> prefMap = new HashMap<Integer,Set<DsAhpProviderObject>>();
		double avg = 0.0;
		int numProvs = providers.length;
		int numWithInfo = numProvs;
		int prefTemp = 0;
		int intPref = 1;
		double currVal=0.0;
		
			for(int i = 0; i<numProvs; i++)
			{
				if(getVal(providers[i], criterion) <=1.0)
				{
					avg += getVal(providers[i], criterion);
				}
				else numWithInfo--;

			}
			
			avg = avg/numWithInfo;
			double vRel = 0.0;
			if(criterion.equals("customer") || criterion.equals("security"))
			{
			
				for(int j = 0; j<numProvs; j++)
				{
					prefTemp=0;
					currVal = getVal(providers[j], criterion);
				
					if(currVal <=1.0)
					{
					
						if((currVal-avg)>=0.0)
					
						{
							vRel=(currVal-avg)*12;
							intPref=1;
					
							while(prefTemp<1)
							{
								if(vRel<=(double)intPref)
								{
									prefTemp=intPref;
								}
							else intPref++;
							}
						if(prefTemp>6) prefTemp = 6;
					
						}
				
					
						else
						{
							vRel=(avg-currVal)*12;
							intPref=1;
				
							while(prefTemp<1)
							{
								if(vRel<=(double)intPref)
								{
									prefTemp=intPref;
								}
							else intPref++;
							}
						if(prefTemp>6) prefTemp = 6;
						prefTemp=-prefTemp;
					
						}
					}
					if(criterion.equals("security"))
					{
						if (prefTemp>3) prefTemp = 3;
						if (prefTemp<-3) prefTemp = -3;
					}
					Set tempSet = new HashSet<DsAhpProviderObject>();
					
					if (prefMap.containsKey(prefTemp)) tempSet=prefMap.get(prefTemp);
					
					tempSet.add(providers[j]);
					prefMap.put(prefTemp, (HashSet<DsAhpProviderObject>)tempSet);
				}
			}
				else 
				{
					for(int k = 0; k<numProvs; k++)
					{
						prefTemp=0;
						currVal = getVal(providers[k], criterion);
						if(currVal <=1.0)
						{
							if((currVal-avg)>=0.0)
							{
								vRel=(currVal-avg)*200;
								intPref=1;
				
				
								while(prefTemp<1)
								{
									if(vRel<=(double)intPref)
									{
										prefTemp=intPref;
									}
									else intPref++;
								}
								if(prefTemp>100) prefTemp = 100;
							}
			
							else
							{
								vRel=(avg-currVal)*200;
								intPref=1;
			
								while(prefTemp<1)
								{
									if(vRel<=(double)intPref)
									{
										prefTemp=intPref;
									}
									else intPref++;
								}
								if(prefTemp>100) prefTemp = 100;
								prefTemp=-prefTemp;
				
							}
				
				
						}
						Set tempSet = new HashSet<DsAhpProviderObject>();
						
						if (prefMap.containsKey(prefTemp)) tempSet=prefMap.get(prefTemp);
						
						tempSet.add(providers[k]);
						prefMap.put(prefTemp, (HashSet<DsAhpProviderObject>)tempSet);
					}
				}
					
				
			
		
		
		return prefMap;
	}
	public double getVal(DsAhpProviderObject provider, String criterion)
	{
		double val = 0.0;
		if(criterion.equals("pastPerf")) val = provider.getPastPerf();
		else if (criterion.equals("security")) val = provider.getSecurity();
		else val = provider.getBusinessStability();
		return val;
	}
	
}
