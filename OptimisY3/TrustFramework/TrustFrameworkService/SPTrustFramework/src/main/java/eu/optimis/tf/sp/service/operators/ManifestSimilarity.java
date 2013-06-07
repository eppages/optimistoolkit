/**

Copyright 2013 ATOS SPAIN S.A.

Licensed under the Apache License, Version 2.0 (the License);
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

Authors :
Juan Luis Prieto, Francisco Javier Nieto. Atos Research and Innovation, Atos SPAIN SA
@email francisco.nieto@atosresearch.eu 

**/

package eu.optimis.tf.sp.service.operators;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

import eu.optimis.manifest.api.ip.AffinityRule;
import eu.optimis.manifest.api.ip.Manifest;


public class ManifestSimilarity {
    
    Manifest mf1, mf2;
    
    
    /*public SLASimilarity(String manifest1, String manifest2)
    {
        //super();
      
        
    }*/
    
    public double getSimilarity(String manifest1, String manifest2) throws IOException
    {
        double similarityfactor=0.0;
        this.mf1=Manifest.Factory.newInstance(manifest1);
        this.mf2=Manifest.Factory.newInstance(manifest2);
        
        double componentNumberSimilarity;
        double affinitySectionSimilarity;
        double trustLevelSimilarity;
        double riskLevelSimilarity;
        double ecoLevelSimilarity;
        double costPlancapSimilarity,costPlanfloorSimilarity;
        double dataProtectionSimilarity;
               
        //get number of components
        //VirtualMacineDescriptionSection vmSection;
        int componentNumber1, componentNumber2;
        int i;
        componentNumber1 = mf1.getVirtualMachineDescriptionSection().getVirtualMachineComponentArray().length;
        componentNumber2 = mf2.getVirtualMachineDescriptionSection().getVirtualMachineComponentArray().length;
        
        componentNumberSimilarity= componentNumber1-componentNumber2;
        if(componentNumberSimilarity<0)
        {
            componentNumberSimilarity=componentNumberSimilarity*-1;
        }
       
               
        String affinityLevel1, affinityLevel2;
        int afLevel1=0, afLevel2=0;
        List<AffinityRule> ar1=new ArrayList<AffinityRule>();
        
        int sz=mf1.getVirtualMachineDescriptionSection().getAffinityRules().length;
        
        for(i=0;i<sz;i++)
        {
            // ar1.add(mf1.getVirtualMachineDescriptionSection().getAffinityRule(i));    
             affinityLevel1= mf1.getVirtualMachineDescriptionSection().getAffinityRule(i).getAffinityConstraints();
             if(affinityLevel1.equalsIgnoreCase("LOW"))
             {
                   afLevel1+=1;
             }
             if(affinityLevel1.equalsIgnoreCase("Medium"))
             {
                   afLevel1+=2;
             }
             if(affinityLevel1.equalsIgnoreCase("High"))
             {
                   afLevel1+=3;
             }
        }
        
        int sz2=mf2.getVirtualMachineDescriptionSection().getAffinityRules().length;
        
        for(i=0;i<sz2;i++)
        {
             //ar1.add(mf2.getVirtualMachineDescriptionSection().getAffinityRule(i));    
             affinityLevel2= mf2.getVirtualMachineDescriptionSection().getAffinityRule(i).getAffinityConstraints();
             if(affinityLevel2.equalsIgnoreCase("LOW"))
             {
                   afLevel2+=1;
             }
             if(affinityLevel2.equalsIgnoreCase("Medium"))
             {
                   afLevel2+=2;
             }
             if(affinityLevel2.equalsIgnoreCase("High"))
             {
                   afLevel2+=3;
             }
        }
        
        affinitySectionSimilarity=afLevel2-afLevel1;
        if(affinitySectionSimilarity<0)
        {
            affinitySectionSimilarity=affinitySectionSimilarity*-1;
        }
        
        //get trust
        int trustsz1=mf1.getTRECSection().getTrustSectionArray().length;       
        int trustLevel1=0;
        for(i=0;i<trustsz1;i++)
        {
            trustLevel1+=mf1.getTRECSection().getTrustSectionArray(i).getTrustLevel();
        }
        
        int trustsz2=mf2.getTRECSection().getTrustSectionArray().length;       
        int trustLevel2=0;
        for(i=0;i<trustsz2;i++)
        {
            trustLevel2+=mf2.getTRECSection().getTrustSectionArray(i).getTrustLevel();
        }
        
        trustLevelSimilarity=trustLevel2-trustLevel1;
        if(trustLevelSimilarity<0)
        {
            trustLevelSimilarity=trustLevelSimilarity*-1;
        }
        
         //get risk
        int risksz1=mf1.getTRECSection().getRiskSectionArray().length;       
        int riskLevel1=0;
        for(i=0;i<risksz1;i++)
        {
            riskLevel1+=mf1.getTRECSection().getRiskSectionArray(i).getRiskLevel();
        }
        
        int risksz2=mf2.getTRECSection().getRiskSectionArray().length;       
        int riskLevel2=0;
        for(i=0;i<risksz2;i++)
        {
            riskLevel2+=mf2.getTRECSection().getRiskSectionArray(i).getRiskLevel();
        }
        
        riskLevelSimilarity=riskLevel2-riskLevel1;
        if(riskLevelSimilarity<0)
        {
            riskLevelSimilarity=riskLevelSimilarity*-1;
        }
        
        //eco 2 values used leeds certification and BRREAM certification 
        int ecosz1=mf1.getTRECSection().getEcoEfficiencySectionArray().length;       
        String ecoLeeds1, ecoBreem;
        int ecoLevel1=0;
        for(i=0;i<ecosz1;i++)
        {
            ecoLeeds1=mf1.getTRECSection().getEcoEfficiencySectionArray(i).getLEEDCertification();
            if(ecoLeeds1.equalsIgnoreCase("NotRequired"))
            {
                ecoLevel1+=1;
            }
            if(ecoLeeds1.equalsIgnoreCase("Certified"))
            {
                ecoLevel1+=2;
            }
            if(ecoLeeds1.equalsIgnoreCase("Silver"))
            {
                ecoLevel1+=3;
            }
            if(ecoLeeds1.equalsIgnoreCase("Gold"))
            {
                ecoLevel1+=4;
            }
            if(ecoLeeds1.equalsIgnoreCase("Platinum"))
            {
                ecoLevel1+=5;
            }
            
            ecoBreem=mf1.getTRECSection().getEcoEfficiencySectionArray(i).getBREEAMCertification();
            if(ecoBreem.equalsIgnoreCase("NotRequired"))
            {
                ecoLevel1+=1;
            }
            if(ecoBreem.equalsIgnoreCase("Pass"))
            {
                ecoLevel1+=2;
            }
            if(ecoBreem.equalsIgnoreCase("Good"))
            {
                ecoLevel1+=3;
            }
            if(ecoBreem.equalsIgnoreCase("VeryGood"))
            {
                ecoLevel1+=4;
            }
            if(ecoBreem.equalsIgnoreCase("Excellent"))
            {
                ecoLevel1+=5;
            }
            if(ecoBreem.equalsIgnoreCase("Outstanding"))
            {
                ecoLevel1+=6;
            }                   
        }
        
        int ecosz2=mf2.getTRECSection().getEcoEfficiencySectionArray().length;       
        String ecoLeeds2, ecoBreem2;
        int ecoLevel2=0;
        for(i=0;i<ecosz2;i++)
        {
            ecoLeeds2=mf2.getTRECSection().getEcoEfficiencySectionArray(i).getLEEDCertification();
            if(ecoLeeds2.equalsIgnoreCase("NotRequired"))
            {
                ecoLevel2+=1;
            }
            if(ecoLeeds2.equalsIgnoreCase("Certified"))
            {
                ecoLevel2+=2;
            }
            if(ecoLeeds2.equalsIgnoreCase("Silver"))
            {
                ecoLevel2+=3;
            }
            if(ecoLeeds2.equalsIgnoreCase("Gold"))
            {
                ecoLevel2+=4;
            }
            if(ecoLeeds2.equalsIgnoreCase("Platinum"))
            {
                ecoLevel2+=5;
            }
            
            ecoBreem2=mf2.getTRECSection().getEcoEfficiencySectionArray(i).getBREEAMCertification();
            if(ecoBreem2.equalsIgnoreCase("NotRequired"))
            {
                ecoLevel2+=1;
            }
            if(ecoBreem2.equalsIgnoreCase("Pass"))
            {
                ecoLevel2+=2;
            }
            if(ecoBreem2.equalsIgnoreCase("Good"))
            {
                ecoLevel2+=3;
            }
            if(ecoBreem2.equalsIgnoreCase("VeryGood"))
            {
                ecoLevel2+=4;
            }
            if(ecoBreem2.equalsIgnoreCase("Excellent"))
            {
                ecoLevel2+=5;
            }
            if(ecoBreem2.equalsIgnoreCase("Outstanding"))
            {
                ecoLevel2+=6;
            }                        
        }
        
        ecoLevelSimilarity=ecoLevel2-ecoLevel1;
        if(ecoLevelSimilarity<0)
        {
            ecoLevelSimilarity=ecoLevelSimilarity*-1;
        }
        
        //cost
        
        int costsz1=mf1.getTRECSection().getCostSectionArray().length;
        int costsz11;
        
        float plancap1=(float) 0.0000, planfloor1=(float) 0.0000;
        int j;
        
        for(i=0;i<costsz1;i++)
        {
            costsz11=mf1.getTRECSection().getCostSectionArray(i).getPricePlanArray().length;
            for(j=0;j<costsz11;j++)
            {
                plancap1+=mf1.getTRECSection().getCostSectionArray(i).getPricePlanArray(j).getPlanCap();
                planfloor1+=mf1.getTRECSection().getCostSectionArray(i).getPricePlanArray(j).getPlanFloor();
            }
            
        }
           
        
        int costsz2=mf2.getTRECSection().getCostSectionArray().length;
        int costsz21;
        
        float plancap2=(float) 0.0000, planfloor2=(float) 0.0000;
        
        
        
        for(i=0;i<costsz2;i++)
        {
            costsz21=mf2.getTRECSection().getCostSectionArray(i).getPricePlanArray().length;
            for(j=0;j<costsz21;j++)
            {
                plancap2+=mf2.getTRECSection().getCostSectionArray(i).getPricePlanArray(j).getPlanCap();
                planfloor2+=mf2.getTRECSection().getCostSectionArray(i).getPricePlanArray(j).getPlanFloor();
            }
            
        }
        
        costPlancapSimilarity=plancap2-plancap1;
        if(costPlancapSimilarity<0)
        {
            costPlancapSimilarity=costPlancapSimilarity*-1;
        }
        
        costPlanfloorSimilarity=planfloor2-planfloor1;
        if(costPlanfloorSimilarity<0)
        {
            costPlanfloorSimilarity=costPlanfloorSimilarity*-1;
        }
        
        //elasticity section none found
        
        //dataprotectionsection
        int dataProtectionLevel1=0;
        String dataProtectionSection1=mf1.getDataProtectionSection().getDataEncryptionLevel().getEncryptionAlgorithm();
        if(dataProtectionSection1.equalsIgnoreCase("NotApplicable"))
        {
            dataProtectionLevel1+=1;
        }
        if(dataProtectionSection1.equalsIgnoreCase("AES"))
        {
            dataProtectionLevel1+=2;
        }
        if(dataProtectionSection1.equalsIgnoreCase("Twofish"))
        {
            dataProtectionLevel1+=3;
        }
        
        if(dataProtectionSection1.equalsIgnoreCase("AES-Twofish"))
        {
            dataProtectionLevel1+=4;
        }
        
        if(dataProtectionSection1.equalsIgnoreCase("AES-Twofish-Serpent"))
        {
            dataProtectionLevel1+=5;
        }
        
        if(dataProtectionSection1.equalsIgnoreCase("Serpent-AES"))
        {
            dataProtectionLevel1+=6;
        }
        
        if(dataProtectionSection1.equalsIgnoreCase("Serpent-Twofish-AES"))
        {
            dataProtectionLevel1+=7;
        }
        
          if(dataProtectionSection1.equalsIgnoreCase("Twofish-Serpent"))
        {
            dataProtectionLevel1+=8;
        }
        
        int dataProtectionLevel2=0;
        String dataProtectionSection2=mf2.getDataProtectionSection().getDataEncryptionLevel().getEncryptionAlgorithm();
        if(dataProtectionSection2.equalsIgnoreCase("NotApplicable"))
        {
            dataProtectionLevel2+=1;
        }
        if(dataProtectionSection2.equalsIgnoreCase("AES"))
        {
            dataProtectionLevel2+=2;
        }
        if(dataProtectionSection2.equalsIgnoreCase("Twofish"))
        {
            dataProtectionLevel2+=3;
        }
        
        if(dataProtectionSection2.equalsIgnoreCase("AES-Twofish"))
        {
            dataProtectionLevel2+=4;
        }
        
        if(dataProtectionSection2.equalsIgnoreCase("AES-Twofish-Serpent"))
        {
            dataProtectionLevel2+=5;
        }
        
        if(dataProtectionSection2.equalsIgnoreCase("Serpent-AES"))
        {
            dataProtectionLevel2+=6;
        }
        
        if(dataProtectionSection2.equalsIgnoreCase("Serpent-Twofish-AES"))
        {
            dataProtectionLevel2+=7;
        }
        
          if(dataProtectionSection2.equalsIgnoreCase("Twofish-Serpent"))
        {
            dataProtectionLevel2+=8;
        }
        
        dataProtectionSimilarity=dataProtectionLevel2-dataProtectionLevel1;
        if(dataProtectionSimilarity<0)
        {
            dataProtectionSimilarity=dataProtectionSimilarity*-1;
        }
        
        componentNumberSimilarity=Math.pow(componentNumberSimilarity,2);
        affinitySectionSimilarity=Math.pow(affinitySectionSimilarity,2);
        trustLevelSimilarity=Math.pow(trustLevelSimilarity,2);
        riskLevelSimilarity=Math.pow(riskLevelSimilarity,2);
        ecoLevelSimilarity=Math.pow(ecoLevelSimilarity,2);
        costPlancapSimilarity=Math.pow(costPlancapSimilarity,2);
        costPlanfloorSimilarity=Math.pow(costPlanfloorSimilarity,2);
        dataProtectionSimilarity=Math.pow(dataProtectionSimilarity,2);        
        
        
        similarityfactor = componentNumberSimilarity + affinitySectionSimilarity + trustLevelSimilarity + riskLevelSimilarity
                + ecoLevelSimilarity + costPlancapSimilarity + costPlanfloorSimilarity + dataProtectionSimilarity;        
       //similarityfactor = 1- similarityfactor;
       // similarityfactor =  similarityfactor/ 2;
        similarityfactor=Math.sqrt(similarityfactor);
        
        return similarityfactor;
    }
    
    
    public LinkedHashMap sortHashMapByValuesD(HashMap passedMap) {
		List mapKeys = new ArrayList(passedMap.keySet());
		List mapValues = new ArrayList(passedMap.values());
		Collections.sort(mapValues);
		Collections.sort(mapKeys);

		LinkedHashMap sortedMap = new LinkedHashMap();

		Iterator valueIt = mapValues.iterator();
		while (valueIt.hasNext()) {
			Object val = valueIt.next();
			Iterator keyIt = mapKeys.iterator();

			while (keyIt.hasNext()) {
				Object key = keyIt.next();
				String comp1 = passedMap.get(key).toString();
				String comp2 = val.toString();

				if (comp1.equals(comp2)) {
					passedMap.remove(key);
					mapKeys.remove(key);
					sortedMap.put((String) key, (Double) val);
					break;
				}

			}

		}
		return sortedMap;
	} 
    
    public String toSting(LinkedHashMap lhm){
    	String similHeader = "<TrustSimilarity>";
    	Set keySet = lhm.keySet();
		Iterator keyit = keySet.iterator();
		String similBody = "";
		while (keyit.hasNext()) {
			String next = (String) keyit.next();
			similBody = similBody + "<ip similairty="+lhm.get(next)+">"+next+"</ip>";
		}
		String similFoot = "</TrustSimilarity>";
		return similHeader+similBody+similFoot;
    }
}
