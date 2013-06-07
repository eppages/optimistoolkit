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
 * Provides the functionality to re-order an array of providers, represented as
 * DsAhpProviderObjects, according to their criteria values and the end-user preferences.
 * @author Iain Gourlay
 */
import java.util.*;

public class DsAhpRanking {

	public DsAhpProviderObject[] computeRanking(UserPreferenceObject prefs, DsAhpProviderObject[] providers)
	{
		double [] endUserWeights = new double [3];
		double [][] userPairwise = new double [3][3];
		EndUserPairwise user = new EndUserPairwise();
		userPairwise = user.createMatrix(prefs);
		AhpWeights userWeights = new AhpWeights();
		endUserWeights = userWeights.createWeights(userPairwise);
		
       MapPreferences prefMap = new MapPreferences();
       Map<Integer,Set<DsAhpProviderObject>> pastPerformance = new HashMap<Integer,Set<DsAhpProviderObject>>();
       pastPerformance = prefMap.createPrefMap(providers, "pastPerf");    
        
       Map<Integer,Set<DsAhpProviderObject>> businessStability = new HashMap<Integer,Set<DsAhpProviderObject>>();
       businessStability = prefMap.createPrefMap(providers, "businessStability");
       
       Map<Integer,Set<DsAhpProviderObject>> security = new HashMap<Integer,Set<DsAhpProviderObject>>();
       security = prefMap.createPrefMap(providers, "security");
            
       Set<BpaForProvidersObject> pastPerfSet = new HashSet<BpaForProvidersObject>();
       Set<BpaForProvidersObject> businessStabilitySet = new HashSet<BpaForProvidersObject>();
       Set<BpaForProvidersObject> secSet = new HashSet<BpaForProvidersObject>();
       DsBpas criteriaBpas = new DsBpas();
       pastPerfSet = criteriaBpas.createBpaSets(pastPerformance, endUserWeights[0],providers);
       businessStabilitySet= criteriaBpas.createBpaSets(businessStability, endUserWeights[1],providers);
       secSet = criteriaBpas.createBpaSets(security, endUserWeights[2],providers);
       
       DsRuleOfCombination combine = new DsRuleOfCombination();
       Set<BpaForProvidersObject> firstCombine = combine.combineBpaSets(pastPerfSet, businessStabilitySet);
       Set<BpaForProvidersObject> totalCombine = combine.combineBpaSets(firstCombine, secSet);
              
       Set<DsAhpProviderObject> provs = new HashSet<DsAhpProviderObject>();
       DsAhpProviderObject[]providersBelPls = providers.clone();
       for(int i = 0; i<providers.length;i++)
       {
    	   provs.add((DsAhpProviderObject)providersBelPls[i]);
       }
       BelandPlaus belPlsIntervals = new BelandPlaus();
       DsAhpProviderObject[] updatedProviders = new DsAhpProviderObject[providers.length];
       updatedProviders = belPlsIntervals.computeBeliefPlaus(totalCombine, provs);
       DsOrderProviders ordered = new DsOrderProviders();
       DsAhpProviderObject[] finalOrder = new DsAhpProviderObject[providers.length];
       finalOrder = ordered.reorderProviders(updatedProviders);
   	
       return finalOrder;
	
	}
}
