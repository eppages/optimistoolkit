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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class DsAhpRanking {

    public DsAhpProviderObject[] computeRanking(UserPreferenceObject prefs, DsAhpProviderObject[] providers) {
        double[] endUserWeights = new double[7];
        double[][] userPairwise = new double[7][7];
        EndUserPairwise user = new EndUserPairwise();
        userPairwise = user.createMatrix(prefs);
        AhpWeights userWeights = new AhpWeights();
        endUserWeights = userWeights.createWeights(userPairwise);

        MapPreferences prefMap = new MapPreferences();

        Map<Integer, Set<DsAhpProviderObject>> geography = new HashMap<Integer, Set<DsAhpProviderObject>>();
        geography = prefMap.createPrefMap(providers, "geography");

        Map<Integer, Set<DsAhpProviderObject>> pastPerformance = new HashMap<Integer, Set<DsAhpProviderObject>>();
        pastPerformance = prefMap.createPrefMap(providers, "pastPerf");

        Map<Integer, Set<DsAhpProviderObject>> certandstd = new HashMap<Integer, Set<DsAhpProviderObject>>();
        certandstd = prefMap.createPrefMap(providers, "certandstd");

        Map<Integer, Set<DsAhpProviderObject>> businessStability = new HashMap<Integer, Set<DsAhpProviderObject>>();
        businessStability = prefMap.createPrefMap(providers, "businessStability");

        Map<Integer, Set<DsAhpProviderObject>> security = new HashMap<Integer, Set<DsAhpProviderObject>>();
        security = prefMap.createPrefMap(providers, "security");

        Map<Integer, Set<DsAhpProviderObject>> infrastructure = new HashMap<Integer, Set<DsAhpProviderObject>>();
        infrastructure = prefMap.createPrefMap(providers, "infrastructure");

        Map<Integer, Set<DsAhpProviderObject>> privacy = new HashMap<Integer, Set<DsAhpProviderObject>>();
        privacy = prefMap.createPrefMap(providers, "privacy");

        Set<BpaForProvidersObject> geoSet = new HashSet<BpaForProvidersObject>();
        Set<BpaForProvidersObject> pastPerfSet = new HashSet<BpaForProvidersObject>();
        Set<BpaForProvidersObject> casSet = new HashSet<BpaForProvidersObject>();
        Set<BpaForProvidersObject> bizSet = new HashSet<BpaForProvidersObject>();
        Set<BpaForProvidersObject> secSet = new HashSet<BpaForProvidersObject>();
        Set<BpaForProvidersObject> infSet = new HashSet<BpaForProvidersObject>();
        Set<BpaForProvidersObject> privaSet = new HashSet<BpaForProvidersObject>();
        DsBpas criteriaBpas = new DsBpas();
        pastPerfSet = criteriaBpas.createBpaSets(pastPerformance, endUserWeights[0], providers);
        casSet = criteriaBpas.createBpaSets(certandstd, endUserWeights[1], providers);
        bizSet = criteriaBpas.createBpaSets(businessStability, endUserWeights[2], providers);
        secSet = criteriaBpas.createBpaSets(security, endUserWeights[3], providers);
        infSet = criteriaBpas.createBpaSets(infrastructure, endUserWeights[4], providers);
        privaSet = criteriaBpas.createBpaSets(privacy, endUserWeights[5], providers);
        geoSet = criteriaBpas.createBpaSets(geography, endUserWeights[6], providers);

        DsRuleOfCombination combine = new DsRuleOfCombination();
        Set<BpaForProvidersObject> firstCombine = combine.combineBpaSets(pastPerfSet, casSet);
        Set<BpaForProvidersObject> secondCombine = combine.combineBpaSets(firstCombine, bizSet);
        Set<BpaForProvidersObject> thirdCombine = combine.combineBpaSets(secondCombine, secSet);
        Set<BpaForProvidersObject> fourthCombine = combine.combineBpaSets(thirdCombine, infSet);
        Set<BpaForProvidersObject> fifthCombine = combine.combineBpaSets(fourthCombine, privaSet);
        Set<BpaForProvidersObject> totalCombine = combine.combineBpaSets(fifthCombine, geoSet);

        Set<DsAhpProviderObject> provs = new HashSet<DsAhpProviderObject>();
        DsAhpProviderObject[] providersBelPls = providers.clone();
        System.out.println("Ranking: providers length " + providers.length);
        for (int i = 0; i < providers.length; i++) {
            provs.add((DsAhpProviderObject) providersBelPls[i]);
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