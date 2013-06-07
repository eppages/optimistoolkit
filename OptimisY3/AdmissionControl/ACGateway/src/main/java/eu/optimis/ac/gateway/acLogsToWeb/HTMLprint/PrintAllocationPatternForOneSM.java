/*
 * Copyright (c) 2013 National Technical University of Athens (NTUA)
 *	
 *   							MIT License
 *   
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */


package eu.optimis.ac.gateway.acLogsToWeb.HTMLprint;

import eu.optimis.ac.gateway.allocationOffer.Decisions;
import eu.optimis.ac.gateway.analyzeAllocationOffer.AllocationOfferInfo;
import eu.optimis.ac.gateway.analyzeAllocationOffer.AllocationOfferInfoAsList;
import eu.optimis.ac.smanalyzer.SMsAnalyzer;
import java.util.ArrayList;
import org.apache.log4j.Logger;

public class PrintAllocationPatternForOneSM
{
	public String Position11 = "";
	public ArrayList<String> Header_List = new ArrayList<String>();
	public ArrayList<String> Tag_List = new ArrayList<String>();
	public ArrayList<ArrayList<String>> Info_List = new ArrayList<ArrayList<String>>();
	
	private ArrayList<String> decision_List = new ArrayList<String>();
	private ArrayList<String> Basic_List = new ArrayList<String>();
	private ArrayList<String> Elastic_List = new ArrayList<String>();
        
	private ArrayList<String> numberOfElastic_List = new ArrayList<String>();
	private ArrayList<String> numberOfBasic_List = new ArrayList<String>();
        private ArrayList<String> numberOfPhysicalHosts_List = new ArrayList<String>();
	
        public int Component_Id_Length = "TREC constraints".length();
        public int Decision_Length = 0;
        public int Basic_Length = 0;
        public int Elastic_Length = 0;
        public int physicalHostsNumber_Length = 0;
        
	public PrintAllocationPatternForOneSM(AllocationOfferInfoAsList allocationOfferList,
			SMsAnalyzer smAnalyzer,int numberOfManifest,Logger log)
	{   
                
		this.setHeader_List();
		
		this.set_Lists(allocationOfferList,smAnalyzer,numberOfManifest,log);
		
		log.info("decision_List: "+decision_List);
		log.info("Basic_List: "+Basic_List);
		log.info("Elastic_List: "+Elastic_List);
		log.info("numberOfPhysicalHosts_List: "+numberOfPhysicalHosts_List);
                
		Info_List.add(decision_List);
		Info_List.add(Basic_List);
		Info_List.add(Elastic_List);
                Info_List.add(numberOfPhysicalHosts_List);
	}//constructor
	
	private void set_Lists(AllocationOfferInfoAsList allocationOfferList,
			SMsAnalyzer smAnalyzer,int numberOfManifest,Logger log)
	{
			AllocationOfferInfo allocationOffer = allocationOfferList.AllocationOfferList.get(numberOfManifest);
			
			ArrayList<String> componentId_List = smAnalyzer.componentId_List.get(numberOfManifest);
			
			String numberOfServiceComponents = smAnalyzer.formParams.get("numberOfServiceComponents").get(numberOfManifest);
			log.info("numberOfServiceComponents :"+numberOfServiceComponents);
                        
			for(int j=0;j<Integer.parseInt(numberOfServiceComponents);j++)
			{
				
				ArrayList<String> ListOfAccepted_Components = 
						Decisions.correspondAcceptedVirtualSystemIdToComponentId
						(allocationOffer.getListOfAccepted_ServiceComponents(),
								smAnalyzer.ServiceComponentId_Map_componentId_List.get(numberOfManifest));
				
                                log.info("ListOfAccepted_Components : "+ListOfAccepted_Components);
                               
                                String componentId = componentId_List.get(j);
                                		
                                log.info("componentId :"+componentId);
                                Component_Id_Length=update_Lenght(componentId,Component_Id_Length);
                                
				Tag_List.add(componentId);
				
                                
				String Decision ="";
				if(checkIfisAccepted(ListOfAccepted_Components,componentId))
					Decision = "Accepted";
				else
					{
						Decision = "Rejected";
						
						decision_List.add(Decision);
						
						Basic_List.add("-");
						Elastic_List.add("-");
						numberOfElastic_List.add("0");
                                                numberOfBasic_List.add("0");
						numberOfPhysicalHosts_List.add(numberOfBasic_List.get(j)
                                                +" - "+numberOfElastic_List.get(j));
                                                
                                                continue;
					}//Decision = "Rejected";
				
				
				decision_List.add(Decision);
				
				log.info("decision_List "+decision_List);
				
				log.info(allocationOffer.getListOfAccepted_ServiceComponents()+" "+ListOfAccepted_Components);
				
				String virtulComponentId = smAnalyzer.componentId_Map_ServiceComponentId_List.get(numberOfManifest).get(componentId).get(0);
				
				log.info("virtulComponentId :"+ virtulComponentId);
				
				//String basic = allocationOffer.BasicPhysicalHost.get(virtulComponentId).get(0);
				
				//log.info("Basic_List "+basic);
				//Basic_Length=update_Lenght(basic,Basic_Length);
                                
				//Basic_List.add(basic);
				
				//log.info("Basic_List "+Basic_List);
				
                                
                                if(allocationOffer.BasicPhysicalHost.get(virtulComponentId)!=null)
				{
					String basic = "";
					for(int x=0;x<allocationOffer.BasicPhysicalHost.get(virtulComponentId).size();x++)
						basic+=allocationOffer.BasicPhysicalHost.get(virtulComponentId).get(x)+" ";
					Basic_List.add(basic);
					numberOfBasic_List.add(Integer.toString(allocationOffer.BasicPhysicalHost.get(virtulComponentId).size()));
                                        Basic_Length=update_Lenght(basic,Basic_Length);
				}
				else
				{
					Basic_List.add("-");
					numberOfBasic_List.add("0");
				}
                                
                                
				if(allocationOffer.ElasticPhysicalHost.get(virtulComponentId)!=null)
				{
					String elastic = "";
					for(int x=0;x<allocationOffer.ElasticPhysicalHost.get(virtulComponentId).size();x++)
						elastic+=allocationOffer.ElasticPhysicalHost.get(virtulComponentId).get(x)+" ";
					Elastic_List.add(elastic);
					numberOfElastic_List.add(Integer.toString(allocationOffer.ElasticPhysicalHost.get(virtulComponentId).size()));
                                        Elastic_Length=update_Lenght(elastic,Elastic_Length);
				}
				else
				{
					Elastic_List.add("-");
					numberOfElastic_List.add("0");
				}
				
				
				log.info("Elastic_List "+Elastic_List);
				log.info("numberOfElastic_List "+numberOfElastic_List);
				
                                numberOfPhysicalHosts_List.add(numberOfBasic_List.get(j)
                                        +" - "+numberOfElastic_List.get(j));
                                
                                log.info("numberOfPhysicalHosts_List "+numberOfPhysicalHosts_List.get(j));
			}//for -j, each component
			
	}//set_Lists()
	
	private void setHeader_List()
	{
		Position11="componentId";
		Component_Id_Length=update_Lenght(Position11,Component_Id_Length);
                
		Header_List.add("AdmissionControlDecision");
		Header_List.add("Basic physical host");
		Header_List.add("Elastic physical host");
		Header_List.add("No of Physical Hosts   (Basic - Elastic)");
                
                Decision_Length=update_Lenght("Admission Control Decision",Decision_Length);
                Basic_Length=update_Lenght("Basic physical host",Basic_Length);
                Elastic_Length=update_Lenght("Elastic physical hosts",Elastic_Length);
                physicalHostsNumber_Length=update_Lenght("No of Physical Hosts   (Basic - Elastic)",physicalHostsNumber_Length);
                
	}//setHeader_List()
	
	private Boolean checkIfisAccepted(ArrayList<String> ListOfAccepted,String componentId)
	{
		for(int i=0;i<ListOfAccepted.size();i++)
		{
			String AcceptedComponentId=ListOfAccepted.get(i);
			
			if(AcceptedComponentId.contains(componentId))return true;
			
		}//for
		
		return false;
	}//checkIfisAccepted()
	
        private int update_Lenght(String str,int Max_Length)
        {
            if(str.length()>Max_Length)
                return str.length();
            
            return Max_Length;
        }//update_Max_Tag_Lenght()
        
        
}//class