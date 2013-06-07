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

import java.util.Random;

public class BasicTestDsAhp {

	/**
	 * @param args
	 */
	public static void main(String[] args)throws Exception {
            
		EndUserRankingObject userranks = new EndUserRankingObject();
		
		userranks.setBusinessStabilityRank(9.0);
		userranks.setPastPerformanceRank(1.0);
		userranks.setSecurityRank(0.0);
		
		UserPreferenceObject prefs = EndUserRankToPreference.computePreferences(userranks);
		
		DsAhpProviderObject provider1 = new DsAhpProviderObject();
		
		
		provider1.setDistName("provider1");
		provider1.setPastPerf(0.9);
		provider1.setSecurity(0.8);
		provider1.setBusinessStability(0.8);
		
		DsAhpProviderObject provider2 = new DsAhpProviderObject();

		provider2.setDistName("provider2");
		provider2.setPastPerf(0.8);
		provider2.setBusinessStability(0.35);
		//provider2.setSecurity(0.9);
		
		DsAhpProviderObject provider3 = new DsAhpProviderObject();
		
		provider3.setDistName("provider3");
		
		provider3.setPastPerf(0.45);
		
		
		DsAhpProviderObject[] providers = new DsAhpProviderObject[3];
		
		providers[0] = provider1;
		providers[1] = provider2;
		providers[2] = provider3;
		
		Random generator = new Random();
		DsAhpProviderObject[] reference = new DsAhpProviderObject[200];
		for(int i = 0; i<200; i++)
		{
			reference[i]=(DsAhpProviderObject)provider1.clone();
			generator.nextDouble();
			
			reference[i].setDistName(""+i);
			reference[i].setBusinessStability(generator.nextDouble());
			reference[i].setPastPerf(generator.nextDouble());
			reference[i].setSecurity(generator.nextDouble());
			
		}
		
		
		DsAhpAbsoluteRank provRank = new DsAhpAbsoluteRank();
		DsAhpProviderObject[] rankedProviders = new DsAhpProviderObject[3];
		rankedProviders = provRank.computeAbsoluteRank(prefs, providers, reference);
		System.out.println("Best prov name:" + rankedProviders[0].getDistName());
		System.out.println("Best prov rating:" + rankedProviders[0].getRating());
		
		System.out.println("2nd prov name:" + rankedProviders[1].getDistName());
		
		System.out.println("2nd prov rating: " + rankedProviders[1].getRating());
                System.out.println("3rd prov name:" + rankedProviders[2].getDistName());
		
		System.out.println("3rd prov rating: " + rankedProviders[2].getRating());
		//try
		//{
	/*	DsAhpRanking provRank = new DsAhpRanking();
		DsAhpProviderObject[] rankedProviders = new DsAhpProviderObject[3];
		rankedProviders = provRank.computeRanking(prefs, providers);*/
		
		
		
	//	}
		//catch (Exception e) 
	//	{
		//System.out.println("Didn't work.");	
	//	}

	}
}
