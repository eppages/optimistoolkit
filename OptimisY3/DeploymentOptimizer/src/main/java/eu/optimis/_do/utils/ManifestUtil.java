/*
Copyright (C) 2012-2013 Umeå University

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/
package eu.optimis._do.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;


import eu.optimis._do.schemas.internal.Pair;
import eu.optimis._do.schemas.internal.TrecObj;
import eu.optimis.manifest.api.sp.AffinityRule;
import eu.optimis.manifest.api.sp.AntiAffinityRule;
import eu.optimis.manifest.api.sp.CostSection;
import eu.optimis.manifest.api.sp.EcoEfficiencySection;
import eu.optimis.manifest.api.sp.EcoMetric;
import eu.optimis.manifest.api.sp.Manifest;
import eu.optimis.manifest.api.sp.PricePlan;
import eu.optimis.manifest.api.sp.RiskSection;
import eu.optimis.manifest.api.sp.TRECSection;
import eu.optimis.manifest.api.sp.TrustSection;
import eu.optimis.manifest.api.sp.VirtualMachineComponent;
import eu.optimis.manifest.api.sp.VirtualMachineDescriptionSection;

import eu.optimis.types.xmlbeans.servicemanifest.XmlBeanAffinityConstraintType;
import eu.optimis.types.xmlbeans.servicemanifest.XmlBeanAntiAffinityConstraintType;
import eu.optimis.types.xmlbeans.servicemanifest.XmlBeanServiceManifestDocument;


/**
 * @author Wubin.Li (Viali)
 * @author Petter Svärd
 * 
 */
public class ManifestUtil
{
	private static Logger logger = Logger.getLogger(ManifestUtil.class);
	public static int RISK_MAX = 8;
	/**
	 * @param ids
	 * @param visitFlag
	 * @param currentIndex
	 * @param affinities
	 * @param idGroup
	 */
	private static<Type> void group(Type[] ids, boolean visitFlag[], int currentIndex, List<Pair<Type, Type>> affinities, List<Type> idGroup)
	{
		if (visitFlag[currentIndex] == false)
		{
			visitFlag[currentIndex] = true;
			idGroup.add(ids[currentIndex]);
		}
		Type ida = ids[currentIndex];
		for (int i = 0; i < ids.length; i++)
		{
			Type idb = ids[i];
			if (affinities.contains(new Pair<Type, Type>(ida, idb)))
			{
				if (visitFlag[i] == false)
				{
					visitFlag[i] = true;
					idGroup.add(idb);
					group(ids, visitFlag, i, affinities, idGroup);
				}
			}
		}
	}
	
	/**
	 * Divide all ids into several  non-overlapping and non-empty groups based on affinity constraints.  
	 * @param ids
	 * @param affinities
	 * @return
	 */
	private static<Type> List<List<Type>> groupByAffinity(Type[] ids, List<Pair<Type, Type>> affinities)
	{
		List<List<Type>> groupList = new ArrayList<List<Type>>();
		
		boolean visitFlag[] = new boolean[ids.length];
		for (int i = 0; i < visitFlag.length; i++)
			visitFlag[i] = false;
		
		int groupId=0;
		for (int i = 0; i < ids.length; i++)
		{
			if(visitFlag[i]==false)
			{
				groupId++;
				List<Type> group = new ArrayList<Type>();
				ManifestUtil.group(ids, visitFlag,  i, affinities, group);
				groupList.add(group);
			}
		}	
		//*********************************************************
		logger.debug("Total ID-Group Number (after Affinity Groupping): "+groupId+".");
		for (int i = 0; i < groupList.size(); i++)
		{
			String log = "Group " + i + ": \n";
			List<Type> gl = groupList.get(i);
			for (Type g : gl)
			{
				log += ("  " + g);
			}
			logger.debug(log);
		}
		//*********************************************************
		
		return groupList;
	}

	
	/**
	 * @param manifest
	 * @param noOfprovider number of provider
	 * @return all possible partition: divide ids into at most k subsets.
	 */
	public static List<List<List<String>>> partitionComponentIds(Manifest manifest, int noOfprovider) throws Exception
	{
		VirtualMachineDescriptionSection vmSection = manifest.getVirtualMachineDescriptionSection();
		
		//Extract affinity constraints
		List<Pair<String, String>> affinityPairs = new ArrayList<Pair<String,String>>();
		AffinityRule[] affinityRuls = vmSection.getAffinityRules();
		for (AffinityRule affinityRule : affinityRuls)
		{
			String level = affinityRule.getAffinityConstraints();
			if (level.equalsIgnoreCase(XmlBeanAffinityConstraintType.LOW.toString()))
				continue;

			String[] ids = affinityRule.getScope().getComponentIdArray();
			for (int i = 0; i < ids.length; i++)
			{
				for (int j = i + 1; j < ids.length; j++)
				{
					Pair<String, String> idp = new Pair<String, String>(ids[i],ids[j]);
					affinityPairs.add(idp);
				}
			}
		}
		
		logger.debug("No. of Affinity Pairs: " + affinityPairs.size());
		
		//Extract anti-affinity constraints
		List<Pair<String, String>> antiAffinityPairs = new ArrayList<Pair<String, String>>();
		AntiAffinityRule[] antiAffinityRuls = vmSection.getAntiAffinityRules();
		for (AntiAffinityRule antiAffinityRule : antiAffinityRuls)
		{
			String level = antiAffinityRule.getAntiAffinityConstraints();
			if (level.equalsIgnoreCase(XmlBeanAntiAffinityConstraintType.LOW.toString()))
				continue;
			String[] ids = antiAffinityRule.getScope().getComponentIdArray();
			for (int i = 0; i < ids.length; i++)
			{
				for (int j = i + 1; j < ids.length; j++)
				{
					Pair<String, String> idp = new Pair<String, String>(ids[i],ids[j]);
					//Check conflict with Affinity Constraints.
					if (affinityPairs.contains(idp))
					{
						throw new Exception(
								"Anti-Affinity Constraints conflict with Affinity Constraints:"
										+ ids[i] + " --X-- " + ids[j]);
					}
					antiAffinityPairs.add(idp);
				}
			}
		}
		
		logger.debug("No. of Anti-Affinity Pairs: " + antiAffinityPairs.size());
		
		VirtualMachineComponent[] componentArr = vmSection.getVirtualMachineComponentArray();
		String[] componentIds = new String[componentArr.length];
		for (int i = 0; i < componentArr.length; i++)
		{
			String id = componentArr[i].getComponentId();
			componentIds[i] = id;
		}
		
		List<List<String>> idGroupList = ManifestUtil.groupByAffinity(componentIds, affinityPairs);
		List<Pair<List<String>, List<String>>> normalizedAApairs = ManifestUtil
				.normalizeAntiAffinityPair(antiAffinityPairs, idGroupList);
		logger.debug("No. of Anti-Affinity Pairs after normalization : "
				+ normalizedAApairs.size());
		
		PartitionUtil<List<String>> pObject = new PartitionUtil<List<String>>();
		List<List<List<List<String>>>> partitions = pObject.genPartitions(
				idGroupList, normalizedAApairs, noOfprovider);
		
		//Merge ids in the same group list
		List<List<List<String>>> result = new ArrayList<List<List<String>>>();
		for (List<List<List<String>>> partition : partitions)
		{
			List<List<String>> newPartition = new ArrayList<List<String>>();
			for (List<List<String>> groupList : partition)
			{
				List<String> newGroup = new ArrayList<String>();
				for (List<String> idList : groupList)
					newGroup.addAll(idList);

				Collections.sort(newGroup);
				newPartition.add(newGroup);
			}
			result.add(newPartition);
		}
		return result;
	}
	
	private static List<Pair<List<String>, List<String>>> normalizeAntiAffinityPair(
			List<Pair<String, String>> antiAffinityPairs,
			List<List<String>> idGroupList)
	{
		List<Pair<List<String>, List<String>>> resultPairs = new ArrayList<Pair<List<String>, List<String>>>();
		for (int i = 0; i < idGroupList.size(); i++)
		{
			List<String> g1 = idGroupList.get(i);
			for (int j = i + 1; j < idGroupList.size(); j++)
			{
				List<String> g2 = idGroupList.get(j);
				boolean isAntiGroup = ManifestUtil.isAntiGroup(
						antiAffinityPairs, g1, g2);
				if (isAntiGroup == true)
				{
					Pair<List<String>, List<String>> p = new Pair<List<String>, List<String>>(
							g1, g2);
					resultPairs.add(p);
				}
			}
		}
		return resultPairs;
	}
	
	private static boolean isAntiGroup(
			List<Pair<String, String>> antiAffinityPairs, List<String> g1,
			List<String> g2)
	{
		for (String id1 : g1)
		{
			for (String id2 : g2)
			{
				Pair<String, String> p = new Pair<String, String>(id1, id2);
				if (antiAffinityPairs.contains(p))
					return true;
			}
		}
		return false;
	}
	
	public static TrecObj extractTRECfromSPManifest(String manifestString) throws Exception
	{
		XmlBeanServiceManifestDocument doc = XmlBeanServiceManifestDocument.Factory.parse(manifestString);
		Manifest spManifest = Manifest.Factory.newInstance(doc);
		return ManifestUtil.extractTREC(spManifest);
	}
	
	private static TrecObj extractTREC(Manifest manifest)
	{
		TRECSection trecSection = manifest.getTRECSection();
		double trust = Double.NEGATIVE_INFINITY;
		TrustSection[] trecArray = trecSection.getTrustSectionArray();
		for (TrustSection trustSection : trecArray)
		{
			int t = trustSection.getMinimumTrustLevel();
			if (trust < t)
				trust = t;
		}
		logger.debug("MinimumTrustLevel >= "+ trust); 
	 	
		double risk = Double.POSITIVE_INFINITY;
		RiskSection[] riskArray = trecSection.getRiskSectionArray();
		for (RiskSection riskSection : riskArray)
		{
			int r = riskSection.getRiskLevel();
			logger.debug("RISK LEVER FOUND IN THE MANIFEST: " + r);
			if (risk > r)
				risk = r;
		}
		logger.debug("RiskLevel <= "+ risk);
		
		double eco = Double.NEGATIVE_INFINITY;
		double energy = Double.NEGATIVE_INFINITY;		
		EcoEfficiencySection[] ecoArray = trecSection.getEcoEfficiencySectionArray();
		for (EcoEfficiencySection ecoEfficiencySection : ecoArray)
		{
			EcoMetric[] metric = ecoEfficiencySection.getEcoMetricArray();
			for (EcoMetric ecoMetric : metric)
			{
				String slaType = ecoMetric.getSLAType();
				if (slaType.equalsIgnoreCase("Soft"))
					continue;

				String name = ecoMetric.getName();
				Object thresh = ecoMetric.getThresholdValue();
				logger.debug("Threshold Value for " + name + "Found: " + thresh);

				double value = Double.parseDouble(thresh.toString());

				if (name.equalsIgnoreCase("EcologicalEfficiency")
						&& eco < value)
				{
					eco = value;
				}
				else if (name.equalsIgnoreCase("EnergyEfficiency") 
						&& energy < value)
				{
					energy = value;
				}
			}
		}
		
		double cost = -1;		
		CostSection[] costArray = trecSection.getCostSectionArray();
		for (CostSection costSection : costArray)
		{
			int numberOfComp = costSection.getScope().getComponentIdArray().length;
			
			logger.debug("Number of components = " + numberOfComp);
			
			double maxCap = Double.NEGATIVE_INFINITY;
			
			PricePlan[] pricePlanArray = costSection.getPricePlanArray();
			for (PricePlan pricePlan : pricePlanArray)
			{
				float cap = pricePlan.getPlanCap();
				logger.debug("CAP = "+cap);
				if (maxCap < cap)
					maxCap = cap;
			}
			logger.debug("Max Cap = " + maxCap);
			
			double sectionCost = numberOfComp * maxCap;
			
			logger.debug("Section Cost: "+ sectionCost);
			
			if (cost == -1)
				cost = sectionCost;
			else
				cost += sectionCost;
		}
		if (cost == -1)
			cost = Double.POSITIVE_INFINITY;
		logger.debug("Cost Requirement <= " + cost);

		//IMPORTANT
		logger.debug("RISK REQ before Normalization: "+risk);
		risk = risk * 1.0 / ManifestUtil.RISK_MAX;
		logger.debug("RISK REQ after Normalization: "+risk);
		TrecObj result = new TrecObj(trust, risk, eco, energy, cost);
		return result;
	}

	public static void main(String[] args) throws Exception	
	{		
		String file = "src/test/resources/service_manifest_multi_cloud.xml";
		File myFile = new File(file);
		XmlBeanServiceManifestDocument doc = XmlBeanServiceManifestDocument.Factory.parse(myFile);
		Manifest originalManifest = Manifest.Factory.newInstance(doc);
	/*	
		List<String> ids = new ArrayList<String>();
		//ids.add("optimis-pm-TypeA");
		//ids.add("optimis-pm-TypeB");
		ids.add("optimis-pm-GeneDetection");
		ids.add("optimis-pm-autoMethod0");
		//ids.add("optimis-pm-TypeD");
		//ids.add("optimis-pm-GeneDetection");
		Manifest newSpManifest = originalManifest.extractComponentList(ids);
		System.out.println(newSpManifest);
		
		//TrecObj trecObj = ManifestUtil.extractTREC(newSpManifest);
		//System.out.println(trecObj);
		 
		 */
		List<List<List<String>>> partitions = ManifestUtil.partitionComponentIds(originalManifest, 1);
		System.out.println(partitions);
	}

}
