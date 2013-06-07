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
package eu.optimis._do;

import java.util.ArrayList;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;


import org.apache.log4j.Logger;
import org.ogf.schemas.graap.wsAgreement.negotiation.NegotiationOfferType;

import eu.optimis._do.iface.IDO;

import eu.optimis._do.schemas.Objective;
import eu.optimis._do.schemas.Placement;
import eu.optimis._do.schemas.PlacementRequest;
import eu.optimis._do.schemas.PlacementSolution;
import eu.optimis._do.schemas.internal.TrecObj;

import eu.optimis._do.stubs.TRECClient;
import eu.optimis._do.utils.ManifestUtil;
import eu.optimis.types.xmlbeans.servicemanifest.XmlBeanServiceManifestDocument;

import eu.optimis.ipdiscovery.datamodel.Provider;
import eu.optimis.manifest.api.sp.Manifest;

/**
 * @author Wubin.Li (Viali)
 * @author Petter Svärd
 * 
 */

public class DeploymentOptimizer implements IDO
{
	private static Logger logger = Logger.getLogger(DeploymentOptimizer.class);
	
	public static final String idSeparator = "+";
	public static final String idProviderSeparator = "@";

	private PlacementRequest placementRequest;
	
	private double totalCost = Double.MAX_VALUE;
	//IMPORTANT totalRisk = 1 - totalRiskReverse, so try to Maximize totalRiskReverse
	//totalRiskReverse = (1-r1)(1-r2)....
	private double totalRiskReverse = Double.NEGATIVE_INFINITY;
	
	//combinedId <--> provider
	private HashMap<String, Provider> ids2providerMap= new HashMap<String, Provider>();
	
	//combinedId <--> manifest
	private HashMap<String, String> ids2manifestMap = new HashMap<String, String>();
	
	//combinedId @ provider <--> manifest with allocation offer
	private HashMap<String, NegotiationOfferType> idsAtProvider2OfferMap = new HashMap<String, NegotiationOfferType>();
	
	//combinedId @ provider  <--> trec assessment object
	private HashMap<String, TrecObj> idsAtProvider2TrecMap = new HashMap<String, TrecObj>();
	
	//combinedId <--> trec requirement object
	private HashMap<String, TrecObj> ids2TrecReqMap = new HashMap<String, TrecObj>();
	
	private PlacementSolution solution;
	
	private TRECClient trecClient;
	
	private boolean is_big_sized_problem= false;
	boolean solutionFound = false;// not has to be the OPTIMAL solution.
	
	/**
	 * Extract elements from Mappings, 
	 * and 
	 * Construct the PlacementSolution object.
	 */
	private void extractSolution()
	{
		this.solution = new PlacementSolution();
		if(this.ids2providerMap.size()<1)
			this.solution.setFeasible(false);
		
		List<Placement> placementList=new ArrayList<Placement>();;
		Set<Entry<String, Provider>> entries = this.ids2providerMap.entrySet();
		for (Entry<String, Provider> entry : entries)
		{
			String combinedId = entry.getKey();

			Provider provider = entry.getValue();
			Placement placement = new Placement();
			NegotiationOfferType offer = null;

			String key = combinedId + DeploymentOptimizer.idProviderSeparator	+ provider.getIdentifier();
			offer = this.idsAtProvider2OfferMap.get(key);
	
			placement.setOffer(offer);
			
			TrecObj trec = this.idsAtProvider2TrecMap.get(key);
			placement.setTREC(trec);
			
			placement.setProvider(provider);
	/*		
			String type = provider.getProviderType();
			if (type.equalsIgnoreCase(Provider.Type.Optimis.toString()))
			{
				placement.setProvider(provider);
			}
			else
			{
				logger.error("The DO Currently supports ONLY Optimis type providers.");
			}
			*/
			
			placementList.add(placement);
		}
		this.solution.setPlacementList(placementList);

		Objective objective = this.placementRequest.getObjective();
		if (objective.equals(Objective.COST))
		{
			this.solution.setOptimum(this.totalCost);
		}
		else if (objective.equals(Objective.RISK))
		{
			this.solution.setOptimum(1 - this.totalRiskReverse);
		}
	}

	/**
	 * @param ids
	 * @return Generate a combinadId using a list of component id.
	 * E.g., "Jboss+VPN"
	 */
	private  String getCombinedId(List<String> ids)
	{
		if (ids.size() < 1)
			return null;
		Collections.sort(ids);
		
		String result = ids.get(0);
		for (int i = 1; i < ids.size(); i++)
		{
			result += DeploymentOptimizer.idSeparator + ids.get(i);
		}
		return result;
	}
	
	/**
	 * @param trecRequirement
	 * @param trecAccessment
	 * @return true if the trecAccessment can fulfill the trecRequirement, otherwise false.
	 */
	private boolean checkTREC(TrecObj trecRequirement, TrecObj trecAccessment)
	{
		if (trecRequirement.getTrust() > trecAccessment.getTrust())
			return false;
		if (trecRequirement.getRisk() < trecAccessment.getRisk())
			return false;
		if (trecRequirement.getEco() > trecAccessment.getEco())
			return false;
		if (trecRequirement.getEnergy() > trecAccessment.getEnergy())
			return false;
		if (trecRequirement.getCost() < trecAccessment.getCost())
			return false;
		return true;
	}
	
	/**
	 * Check if components (ids) can be placed in provider.
	 * @param mapping  <combinedId-Provider>
	 * @param ids
	 * @param provider
	 * @param originalManifest
	 * @return the COST assessment or RISK assessment, 
	 * 		   Double.MAX_VALUE will be returned if ids is no way to be hosted by provider.
	 */
	private double checkFeasibility(HashMap<String,Provider> mapping, List<String> ids, Provider provider, Objective objective)
	{
		// provider is occupied
		if (mapping.containsValue(provider))
		{
			if(objective.equals(Objective.COST))
				return Double.MAX_VALUE;
			else if(objective.equals(Objective.RISK))
			{
				return 1.0;
			}
		}
		try
		{	
			String manifestAsXML = this.placementRequest.getManifestXML();
			String combinedId = this.getCombinedId(ids);
			
			logger.debug("Checking if Service ["+combinedId+"] can be placed in provider " + provider.getIdentifier());
			
			String newSpManifestXML = null;
			if (this.ids2manifestMap.containsKey(combinedId) == false)
			{	
				logger.debug("Splitting ids : "+ids+" from original manifest!");
				Manifest originalManifestCopy = Manifest.Factory.newInstance(manifestAsXML);
				logger.debug("In the splitting cases, no federation is allowed.");
				//IMPORTANT
				originalManifestCopy.getVirtualMachineDescriptionSection()
						.setIsFederationAllowed(false);
				Manifest newSpManifest = originalManifestCopy.extractComponentList(ids);		
				
				logger.debug("Manifest SUCCESSFULLY split!");
				this.ids2manifestMap.put(combinedId, newSpManifest.toString());
			}	
			
			newSpManifestXML = this.ids2manifestMap.get(combinedId);
			
			//TREC REQ
			TrecObj trecReq = null;
			if (this.ids2TrecReqMap.containsKey(combinedId))
			{
				trecReq = this.ids2TrecReqMap.get(combinedId);
			}
			else
			{
				trecReq = ManifestUtil.extractTRECfromSPManifest(newSpManifestXML);
				logger.debug("TREC Requirements: " + trecReq);
				this.ids2TrecReqMap.put(combinedId, trecReq);
			}
			
			//TREC ACC
			TrecObj trecAcc =null;					
			String combinedIdAtProvider = combinedId + DeploymentOptimizer.idProviderSeparator + provider.getIdentifier();
			if (this.idsAtProvider2TrecMap.containsKey(combinedIdAtProvider))
			{
				logger.debug("["+combinedIdAtProvider+"] has been checked before.");
				trecAcc = this.idsAtProvider2TrecMap.get(combinedIdAtProvider);
			}
			else
			{
				trecAcc = this.trecClient.getTrecAssessment(
							newSpManifestXML, provider, combinedId,
							this.idsAtProvider2OfferMap, this.placementRequest.getProperties());

				this.idsAtProvider2TrecMap.put(combinedIdAtProvider, trecAcc);
			}

			logger.debug("REQ = " + trecReq);
			logger.debug("ACC = " + trecAcc);

			if (!this.checkTREC(trecReq, trecAcc))
			{
				logger.debug("Provider " + provider.getIdentifier() + " is NOT OK for IDs: " + combinedId);
				if(objective.equals(Objective.COST))
					return Double.MAX_VALUE;
				else if(objective.equals(Objective.RISK))
				{
					return 1.0;
				}
			}
			
			logger.debug("Provider " + provider.getIdentifier() + " is OK for IDs: " + combinedId);
			
			if (objective.equals(Objective.COST))
				return trecAcc.getCost();
			else if(objective.equals(Objective.RISK))
				return trecAcc.getRisk();					
			return Double.MAX_VALUE;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			logger.error(e.getMessage());
			return Double.MAX_VALUE;
		}
	}
	
	/**
	 * @param objective
	 * @param manifest
	 * this function is used to select one provider to 
	 * deploy a manifest which is not allowed to split.
	 * @throws Exception 
	 */
	private void selectOneProvider(Objective objective, String manifestXML) throws Exception
	{
		logger.debug("The manifest is not allowed to split..");
		logger.debug("Trying to select one provider to deploy it..");
		
		String allIds="ALLIDS_NOT_ALLOW_SPLIT";
		List<Provider> providers = this.placementRequest.getProviders();
		
		logger.debug("Provider Set Size: " + providers.size());
		
		TrecObj trecReq = ManifestUtil.extractTRECfromSPManifest(manifestXML);
		logger.debug("TREC Requirements: " + trecReq);
		
		Provider destination =null;
		for (int i = 0; i < providers.size(); i++)
		{
			Provider provider = providers.get(i);
			TrecObj trecAcc =  this.trecClient.getTrecAssessment(manifestXML, provider, allIds, this.idsAtProvider2OfferMap, this.placementRequest.getProperties());
			
			String key = allIds + DeploymentOptimizer.idProviderSeparator + provider.getIdentifier();
			this.idsAtProvider2TrecMap.put(key, trecAcc);
			
			if (this.checkTREC(trecReq, trecAcc) == false)
			{
				logger.debug("TREC Requirements: " + trecReq);
				logger.debug("TREC Assessments: " + trecAcc);
				logger.debug("Provider " + provider.getIdentifier() + " can not fulfill the TREC Requirements.");
				continue;
			}
			else
			{
				logger.debug("Provider "+provider.getIdentifier()+" might be an option.");
				if (destination == null)
				{
					destination = provider;
					if (objective.equals(Objective.COST))
							this.totalCost = trecAcc.getCost();
					else if(objective.equals(Objective.RISK))
						this.totalRiskReverse = 1 - trecAcc.getRisk();
					continue;
				}

				if (objective.equals(Objective.COST)
						&& trecAcc.getCost() < this.totalCost)
				{
					destination = provider;			
					this.totalCost = trecAcc.getCost();
					continue;
				}
				
				if (objective.equals(Objective.RISK)
						&& 1- trecAcc.getRisk() > this.totalRiskReverse)
				{
					destination = provider;
					this.totalRiskReverse = 1 - trecAcc.getRisk();
					continue;
				}
			}
		}
		if (destination != null)
		{
			this.ids2manifestMap.put(allIds,manifestXML);
			this.ids2providerMap.put(allIds, destination);
			logger.debug("Provider "+ destination.getIdentifier() +" fulfills the TREC requirements!");
		}
		else
		{
			logger.debug("No provider fulfills the TREC requirements!");
		}
	}
	
	//*******************************COST or RISK*******************************************//
	private void checkPartition(Objective objective, HashMap<String, Provider> mapping,
			List<List<String>> partition, int currentIndex,
			double currentTotalCOSTorRISK) throws Exception
	{
		//IMPORTANT We apply First-Fit algorithm for big-sized problems.
		if (this.is_big_sized_problem && this.solutionFound)
			return;
		
		if (currentIndex >= partition.size())
		{
			//Check if all components are placed..
			if (mapping.keySet().size() == partition.size())
			{
				if (objective.equals(Objective.COST)
						&& currentTotalCOSTorRISK < this.totalCost)
				{
					logger.debug("New Optimal COST Retrieved = "+ currentTotalCOSTorRISK);
					
					//Save the optimal COST
					this.totalCost = currentTotalCOSTorRISK;
					//Save the better placement
					this.ids2providerMap.clear();
					this.ids2providerMap.putAll(mapping);
					
					this.solutionFound = true; // at least one solution is found
				}
				else if(objective.equals(Objective.RISK) && currentTotalCOSTorRISK > this.totalRiskReverse)
				{
					logger.debug("New Optimal RISK Retrieved = "+ currentTotalCOSTorRISK);
					
					//Save the optimal RISK
					this.totalRiskReverse = currentTotalCOSTorRISK;
					//Save the better placement
					this.ids2providerMap.clear();
					this.ids2providerMap.putAll(mapping);
					
					this.solutionFound = true; // at least one solution is found
				}
			}			
		}
		else
		{			
			List<Provider> providers = this.placementRequest.getProviders();
			List<String> ids = partition.get(currentIndex);
			String combinedId = this.getCombinedId(ids);
			
			for (int i = 0; i < providers.size(); i++)
			{
				Provider provider = providers.get(i);
				double mCOSTorRISK = this.checkFeasibility(mapping, ids, provider, objective);
				
				logger.debug("Optimal COST/RISK for " + ids + " @"+ provider.getIdentifier()+" = "+ mCOSTorRISK);
				
				double tcr  = currentTotalCOSTorRISK;
				
				if (mCOSTorRISK < Double.MAX_VALUE )		
				{
					if (objective.equals(Objective.COST)
							&& mCOSTorRISK + currentTotalCOSTorRISK < this.totalCost)
					{
						mapping.put(combinedId, provider);
						currentTotalCOSTorRISK += mCOSTorRISK;
						this.checkPartition(objective, mapping, partition, currentIndex+1, currentTotalCOSTorRISK);
					}
					else if (objective.equals(Objective.RISK)
							&& currentTotalCOSTorRISK * (1 - mCOSTorRISK) > this.totalRiskReverse)
					{
						mapping.put(combinedId, provider);
						currentTotalCOSTorRISK *= (1 - mCOSTorRISK);
						this.checkPartition(objective, mapping, partition, currentIndex+1, currentTotalCOSTorRISK);
					}
				}
				else
				{
					continue;
				}
				
				//IMPORTANT
				currentTotalCOSTorRISK = tcr;
				mapping.remove(combinedId);
			}
		}
	}
	//*******************************COST or RISK*******************************************//
		
	/**
	 * Optimization function
	 * @throws Exception 
	 */
	public void optimization() throws Exception
	{
		//Clean first..
		this.ids2providerMap.clear();
		String manifestXML  = this.placementRequest.getManifestXML();
		XmlBeanServiceManifestDocument doc = XmlBeanServiceManifestDocument.Factory.parse(manifestXML);
		Manifest manifest = Manifest.Factory.newInstance(doc);
		List<Provider> providers = this.placementRequest.getProviders();
		Objective objective = this.placementRequest.getObjective();
		
		logger.debug("No of Providers: "+ providers.size());
		
		if (providers.size() < 1)
		{
			throw new Exception("The Provider set is EMPTY!");
		}
		
		//STEP 1: Generate all possible partitions.
		List<List<List<String>>> partitions = ManifestUtil.partitionComponentIds(manifest, providers.size());
		logger.debug("Partitioning done, number of possible partitions: "+partitions.size()+".");
		
		//STEP 2: Special Case, only one Provider.
		if (providers.size() == 1)
		{
			logger.debug("There is only one provider available.");
/*			if (partitions.size() != 1)
			{
				logger.error("The Partition size is not ONE. It might be due to Anti-Affinity Constraints.");
				throw new Exception(
						"Could not partion the components in the manifest, please check the constraints.");
			}
			*/
			logger.debug("Now selecting one provider to host the service/manifest.");
			this.selectOneProvider(objective,
					this.placementRequest.getManifestXML());
			return;
		}
		
		//STEP 2: check every partition
		for (List<List<String>> partition : partitions)
		{
			logger.debug("Checking "+objective+" for partition: " + partition +"   -{"+partitions.indexOf(partition)+"}");
			
			HashMap<String, Provider> mapping = new HashMap<String, Provider>();
			
			if (objective.equals(Objective.COST))
			{
				this.checkPartition(objective, mapping, partition, 0, 0);
				logger.debug("Partition: "+ partition+ " Checked. After that, the Optimal result = " + this.totalCost);
			}
			else if (objective.equals(Objective.RISK))
			{
				this.checkPartition(objective, mapping, partition, 0, 1);
				logger.debug("Partition: "+ partition+ " Checked. After that, the Optimal result = " + this.totalRiskReverse);
			}
			
			logger.debug("******************************************************************************************");
		}
	}	
	
	@Override
	public PlacementSolution getPlacementSolution(PlacementRequest request) 
	{
		Long startTime = System.currentTimeMillis();
		
		this.placementRequest = request;
		
		Map<String, String> properties = request.getProperties();
		
		String host = properties.get("trec.service.host");
		int port = 8080;
		try
		{
			String _port = properties.get("trec.service.port");
			logger.debug("_port: " + _port);
			port = Integer.valueOf(_port.trim());
		}
		catch (Exception e)
		{
			e.printStackTrace();
			logger.error("TREC client setting error: " + e.getMessage());
			return null;
		}
		logger.debug("TREC Endpoint  = http://" + host + ":" + port);
		this.trecClient = new TRECClient(host, port);
		
		this.is_big_sized_problem = Boolean.getBoolean(properties
				.get("is.big.sized.problem"));
		
		logger.debug("Property: is_big_sized_problem = "
				+ this.is_big_sized_problem);
		
		Objective objective = request.getObjective();
		
		logger.debug("Caculating solution for requrest with Objective "+ objective);
		
		//Optimization.
		try
		{
			this.optimization();
		}
		catch (Exception e)
		{
			logger.error(e.getMessage());
			e.printStackTrace();
			return null;
		}

		//Optimize the COST..
		if (objective.equals(Objective.COST))
		{			
			if (this.totalCost < Double.MAX_VALUE)
			{
				Set<Entry<String, Provider>> entries = this.ids2providerMap.entrySet();
				//Print the solution
				for (Entry<String, Provider> entry : entries)
				{
					String vId = entry.getKey();
					Provider ip = entry.getValue();
					String out = vId + " --> " + ip.getIdentifier();
					logger.debug(out);
				}
				String out = "Optimal solution, COST = " + this.totalCost;
				logger.debug(out);
			}	
			else
			{
				logger.debug("Can not achieve an optimal solution!");
			}
		}
		//Optimize the RISK..
		else if (objective.equals(Objective.RISK))
		{
			if (this.totalRiskReverse > Double.NEGATIVE_INFINITY)
			{
				Set<Entry<String, Provider>> entries = this.ids2providerMap.entrySet();
				//Print the solution
				for (Entry<String, Provider> entry : entries)
				{
					String vId = entry.getKey();
					Provider ip = entry.getValue();
					String out = vId + " --> " + ip.getIdentifier();
					logger.debug(out);
				}
				String out = "Optimal solution, RISK = "+ (1 - this.totalRiskReverse);
				logger.debug(out);
			}
			else
			{
				logger.debug("Can not achieve an optimal solution!");
			}
		}	
		logger.debug("ids/manifestMap size = " + this.ids2manifestMap.size());
		logger.debug("ids/provider size = " + this.ids2providerMap.size());
		logger.debug("ids@provider/TREC Size = "+ this.idsAtProvider2TrecMap.size());
		
		//Extract Solution		
		this.extractSolution();		
				
		Long endTime = System.currentTimeMillis();
		logger.debug("Total time in DO is " + (endTime - startTime)/1000.0 + "Seconds");
		
		return this.solution;
	}
	
	public Provider chooseBestIP(List<Provider> ips, List<String> vmIPs)
	{
		Provider result = null;
		int point = 0;
		//Step 3: Choose best IP
		for (Provider provider : ips)
		{
			int ipPoint = 0;
			String ipAddress = provider.getIpAddress();
			for (int k = 0; k < vmIPs.size(); k++)
			{
				String vmIp = vmIPs.get(k);
				int length = vmIp.length() < ipAddress.length() ? vmIp.length() : ipAddress.length();
				int p = 0;
				int endIndex = 0;
				while (vmIp.charAt(endIndex) == ipAddress.charAt(endIndex)
						&& endIndex < length)
				{
					if(vmIp.charAt(endIndex)=='.')
						p++;
					endIndex++;
				}
				if (p == 3) //both =  X.X.X.X
					ipPoint += 2 * p;
				else
					ipPoint += p;
			}
			if (ipPoint > point || result == null)
			{
				point = ipPoint;
				result = provider;
			}
			logger.debug(provider.getName() + " - IP Address matching point : "	+ ipPoint);
		}
		return result;
	}
}
