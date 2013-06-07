/*
 * Copyright (c) 2013 National Technical University of Athens (NTUA)
 *	
 *   							MIT License
 *   
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */


package eu.optimis.ac.gateway.serviceManifestFunctions;

import com.sun.jersey.core.util.MultivaluedMapImpl;
import eu.optimis.ac.smanalyzer.SMAnalyzer;
import eu.optimis.manifest.api.ip.AllocationOffer;
import eu.optimis.manifest.api.ip.Manifest;
import eu.optimis.types.xmlbeans.servicemanifest.XmlBeanServiceManifestDocument;
import javax.ws.rs.core.MultivaluedMap;
import org.apache.xmlbeans.XmlException;

public class ReadAllocationPatternFromSM {
    
    public MultivaluedMap<String, String> Basic_Map = new MultivaluedMapImpl(); 
    
    public MultivaluedMap<String, String> Elastic_Map = new MultivaluedMapImpl();
    
    public ReadAllocationPatternFromSM(String serviceManifest)
    {
        AllocationOffer allocationOffer;
        
        try {
            allocationOffer = getAllocationOffer(serviceManifest);
            
            for(int i=0;i<allocationOffer.getAllocationPatternArray().length;i++)
            {
                
                for(int j=0;j<allocationOffer.getAllocationPattern(i).getPhysicalHostArray().length;j++)
                {
                    if(allocationOffer.getAllocationPattern(i).getPhysicalHostArray(j).isElastic())
                    {
                        Elastic_Map.add(allocationOffer.getAllocationPattern(i).getComponentId(), allocationOffer.getAllocationPattern(i).getPhysicalHostArray(j).getHostName());
                    }
                    else
                    {
                        Basic_Map.add(allocationOffer.getAllocationPattern(i).getComponentId(), allocationOffer.getAllocationPattern(i).getPhysicalHostArray(j).getHostName());
                    }
                    
                }//for-j
            }//for-i
            
        } catch (XmlException ex) {
            System.err.println(ex.getMessage());
        }
        
    }//Constructor
        
    private AllocationOffer getAllocationOffer(String serviceManifest)
                throws XmlException
    {
            XmlBeanServiceManifestDocument xmlBeanManifest = XmlBeanServiceManifestDocument.Factory.parse(serviceManifest);
			
            Manifest ipManifest = Manifest.Factory.newInstance(xmlBeanManifest);
            
            AllocationOffer allocationOffer =
                ipManifest.getInfrastructureProviderExtensions().getAllocationOffer();
            
            return allocationOffer;
            
    }//getAllocationOffer()
    
    public static String getBasicPhysicalHostsListAsString(String componentId,MultivaluedMap<String, String> Basic_Map)
    {
        if(Basic_Map.containsKey(componentId)==false)
            return "";
        
        String result = "";
        for(int i=0;i<Basic_Map.get(componentId).size();i++)
        {
            if(i==0)
                result+= Basic_Map.get(componentId).get(i);
            else
                result+= ","+Basic_Map.get(componentId).get(i);
        }//for-i
        
        return result;
    }//printBasicPhysicalHost()
    
    public static String getElasticPhysicalHostsListAsString(String componentId,MultivaluedMap<String, String> Elastic_Map)
    {
        if(Elastic_Map.containsKey(componentId)==false)
            return "";
        
        String result = "";
        for(int i=0;i<Elastic_Map.get(componentId).size();i++)
        {
            if(i==0)
                result+= Elastic_Map.get(componentId).get(i);
            else
                result+= ","+Elastic_Map.get(componentId).get(i);
        }//for-i
        
        return result;
    }//printElasticPhysicalHost()
    
    
    public static String getAllocationPatternAsOneString(SMAnalyzer manifestInfo, ReadAllocationPatternFromSM allocationPattern)
    {
        String result = "";
        
        for(int i=0;i<manifestInfo.componentId_List.size();i++)
         {
             String componentId = manifestInfo.componentId_List.get(i);
             
             String Basic = ReadAllocationPatternFromSM.
                     getBasicPhysicalHostsListAsString(componentId, allocationPattern.Basic_Map);
             
             String Elastic = ReadAllocationPatternFromSM.
                     getElasticPhysicalHostsListAsString(componentId, allocationPattern.Elastic_Map);
             
             result+=Basic+" "+Elastic;
             
         }//int-i
        
        return result;
        
    }//getAllocationPatternAsOneString()
}//class
