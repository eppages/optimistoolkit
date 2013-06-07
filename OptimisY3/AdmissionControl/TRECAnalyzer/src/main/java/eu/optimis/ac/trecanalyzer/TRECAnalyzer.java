/*
 * Copyright (c) 2013 National Technical University of Athens (NTUA)
 *	
 *   							MIT License
 *   
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package eu.optimis.ac.trecanalyzer;

import com.sun.jersey.core.util.MultivaluedMapImpl;
import eu.optimis.ac.infoCollector.costInfoCollector.CostList;
import eu.optimis.ac.infoCollector.physicalHostsInfoCollector.performPhysicalHostsAnalysis;
import eu.optimis.ac.infoCollector.riskInfoCollector.RiskList;
import eu.optimis.ac.infoCollector.smInfoCollector.AllServiceManifestsInfo;
import eu.optimis.ac.trecanalyzer.compinations.CompinationAll;
import eu.optimis.ac.trecanalyzer.csv.WriteCsv;
import eu.optimis.ac.trecanalyzer.csv.WriteCsvAsTable;
import eu.optimis.ac.trecanalyzer.csv.WriteServicesInfo;
import eu.optimis.ac.trecanalyzer.modelInputAsStrings.WriteServicesInfoAsString;
import eu.optimis.ac.trecanalyzer.modelInputAsStrings.WriteString;
import eu.optimis.ac.trecanalyzer.modelInputAsStrings.WriteStringAsTable;
import eu.optimis.ac.trecanalyzer.trecweights.Check_TREC_weights;
import eu.optimis.ac.trecanalyzer.trecweights.Delete;
import eu.optimis.ac.trecanalyzer.utils.Paths;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import org.apache.log4j.Logger;

@Path("/createModel")
public class TRECAnalyzer {

	protected static Logger log = Logger.getLogger(TRECAnalyzer.class);	
	
	private String GamsPath="";
	
	private String AllocationPath="";
	
        private MultivaluedMap<String, String> outputParams = new MultivaluedMapImpl();
        
    public TRECAnalyzer() {    	
    }//Constructor
    
    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public MultivaluedMap<String, String> createModel(MultivaluedMap<String, String> formParams)
    		throws FileNotFoundException,IOException,Exception,TransformerException,ParserConfigurationException
    {   
    	Properties props = new Properties();
    	
    	try{
               props.load(TRECAnalyzer.class.getClassLoader().getResourceAsStream("config.properties"));
               
               log.info(props.getProperty("TRECAnalyzer.StartMessage"));
               
               log.info("CreateModel method invoked!");
                
    		setPath(formParams);
    		
                Delete.deleteOldFiles( GamsPath, AllocationPath);
                log.info("Old files deleted!");
    		
                new Check_TREC_weights( GamsPath, log);
                
                TREC_constraints eco_constraintList = new TREC_constraints( GamsPath,"eco_constraint",formParams.get("serviceManifest").size(), log);
                String eco_constraint = WriteString.WriteSimpleString(GamsPath,eco_constraintList.ConstraintList,"eco_constraint","a",log);
                setReturnedParams("eco_constraint",eco_constraint);
                
                TREC_constraints cost_constraintList = new TREC_constraints( GamsPath,"cost_constraint",formParams.get("serviceManifest").size(), log);
                String cost_constraint = WriteString.WriteSimpleString(GamsPath,cost_constraintList.ConstraintList,"cost_constraint","a",log);
                setReturnedParams("cost_constraint",cost_constraint);
                
                TREC_constraints trust_constraintList = new TREC_constraints( GamsPath,"trust_constraint",formParams.get("serviceManifest").size(), log);
                String trust_constraint = WriteString.WriteSimpleString(GamsPath,trust_constraintList.ConstraintList,"trust_constraint","a",log);
                setReturnedParams("trust_constraint",trust_constraint);
                
                TREC_constraints risk_constraintList = new TREC_constraints( GamsPath,"risk_constraint",formParams.get("serviceManifest").size(), log);
                String risk_constraint = WriteString.WriteSimpleString(GamsPath,risk_constraintList.ConstraintList,"risk_constraint","a",log);
                setReturnedParams("risk_constraint",risk_constraint);
                
                performPhysicalHostsAnalysis pha = 
                        new performPhysicalHostsAnalysis(formParams.get("physicalHostsInfo").get(0),formParams,"HostName",log);
                
    		RiskList allRiskValues = new RiskList(formParams,log);
    		
    		CostList allCostValues = new CostList(formParams,AllocationPath,log);
    	    
    		AllServiceManifestsInfo all_smi = new AllServiceManifestsInfo(formParams,log);
    		
    		CompinationAll compAll = new CompinationAll(all_smi.max_numberOfComponents);
    		
    		log.info("size="+compAll.ListOfCompinations.size());
    		
    		for(int i=0;i<compAll.ListOfCompinations.size();i++)
    			log.info(compAll.ListOfCompinations.get(i));
    		
                log.info("affinity_sc : "+all_smi.AffinityConstraintsList);
                log.info("anti_affinity_sc : "+all_smi.AntiAffinityConstraintsList);
                
                log.info("affinity_rules : "+all_smi.affinityRule_List);
                log.info("anti_affinity_rules : "+all_smi.antiAffinityRule_List);
                
    		/*
                WriteCsv.WriteSecondaryCsv(GamsPath,pwa.LengthOfPhysicalHostTypes,"hosts","ph",log);
    		*/ 
                WriteCsv.WriteSimpleCsv(GamsPath,formParams,"HostName","hostsInfo",AllocationPath,"ph",log); 
                String hosts = WriteString.WriteSecondaryString(GamsPath,pha.NoOfPhysicalHost,"hosts","ph",log);
                String hostsInfo = WriteString.WriteSimpleString(GamsPath,formParams,"HostName","hostsInfo",AllocationPath,"ph",log);
                setReturnedParams("hosts",hosts);
                setReturnedParams("hostsInfo",hostsInfo);
                
                /*
    		WriteCsv.WriteSimpleCsv(GamsPath,pwa.maxCpusAsList,"maxCpus","ph",log);
    		WriteCsv.WriteSimpleCsv(GamsPath,pwa.resCpusAsList,"resCpus","ph",log);
                */ 
                String maxCpus = WriteString.WriteSimpleString(GamsPath,pha.maxCpusAsList,"maxCpus","ph",log);
                String resCpus = WriteString.WriteSimpleString(GamsPath,pha.resCpusAsList,"resCpus","ph",log);
                String freeMemory = WriteString.WriteSimpleString(GamsPath,pha.freeMemoryAsList,"free_mem","ph",log);
                setReturnedParams("maxCpus",maxCpus);
                setReturnedParams("resCpus",resCpus);
                setReturnedParams("free_mem",freeMemory);
                
                /*
                WriteCsv.WriteSimpleCsv(GamsPath,formParams,"trust","trust",GamsPath,"a",log);
                WriteCsv.WriteSimpleCsv(GamsPath,formParams,"eco","eco",GamsPath,"a",log);
                WriteCsv.WriteSimpleCsv(GamsPath,formParams,"ecoHost","ecoHost",GamsPath,"ph",log);
                */ 
                String trust = WriteString.WriteSimpleString(GamsPath,formParams,"trust","trust",GamsPath,"a",log);
                String eco = WriteString.WriteSimpleString(GamsPath,formParams,"eco","eco",GamsPath,"a",log);       
                String ecoHost = WriteString.WriteSimpleString(GamsPath,formParams,"ecoHost","ecoHost",GamsPath,"ph",log);
                String riskHost = WriteString.WriteSimpleString(GamsPath,formParams,"riskHost","riskHost",GamsPath,"ph",log);
                String ecoHost_UNSORTED = WriteString.WriteSimpleString(GamsPath,formParams,"ecoHost_UNSORTED","ecoHost_UNSORTED",GamsPath,"ph",log);
                String riskHost_UNSORTED = WriteString.WriteSimpleString(GamsPath,formParams,"riskHost_UNSORTED","riskHost_UNSORTED",GamsPath,"ph",log);
                setReturnedParams("trust",trust);
                setReturnedParams("eco",eco);
                setReturnedParams("ecoHost",ecoHost);
                setReturnedParams("riskHost",riskHost);
                setReturnedParams("ecoHost_UNSORTED",ecoHost_UNSORTED );
                setReturnedParams("riskHost_UNSORTED",riskHost_UNSORTED );
                
                
                //WriteCsv.WriteSimpleCsv(GamsPath,allRiskValues.riskAsList,"risk","a",log);
                String risk = WriteString.WriteSimpleString(GamsPath,allRiskValues.riskAsList,"risk","a",log);
                setReturnedParams("risk",risk);
                
                /*
                WriteCsv.WriteSimpleCsv(GamsPath,allCostValues.BasicCost,"basicCost","a",log);
                WriteCsv.WriteSimpleCsv(GamsPath,allCostValues.ExtraCost,"extraCost","a",log);
                */
                String basicCost = WriteString.WriteSimpleString(GamsPath,allCostValues.BasicCost,"basicCost","a",log);
                String extraCost = WriteString.WriteSimpleString(GamsPath,allCostValues.ExtraCost,"extraCost","a",log);
                setReturnedParams("basicCost",basicCost);
                setReturnedParams("extraCost",extraCost);
                
                /*
        	WriteCsv.WriteSimpleCsv(GamsPath,all_smi.AvailabilityList,"availability","a",log);
        	WriteCsv.WriteSimpleCsv(GamsPath,all_smi.isFederationAllowedList,"doNotFederate","a",log);
                */
        	String availability = WriteString.WriteSimpleString(GamsPath,all_smi.AvailabilityList,"availability","a",log);
                String doNotFederate = WriteString.WriteSimpleString(GamsPath,all_smi.isFederationAllowedList,"doNotFederate","a",log);
                setReturnedParams("availability",availability);
                setReturnedParams("doNotFederate",doNotFederate);
                /*
        	WriteCsvAsTable.WriteCsvFileAsTable(GamsPath,all_smi.BasicList,"basic"," ",log);               
        	WriteCsvAsTable.WriteCsvFileAsTable(GamsPath,all_smi.ElasticList,"elastic"," ",log);
        	WriteCsvAsTable.WriteCsvFileAsTable(GamsPath,all_smi.TableList,"table"," ",log);
                WriteCsvAsTable.WriteCsvFileAsTable(GamsPath,all_smi.ServiceComponentList,"vms","no",log);
        	WriteCsvAsTable.WriteCsvFileAsTable(GamsPath,all_smi.AffinityConstraintsList,"affinity_sc"," ",log);
        	WriteCsvAsTable.WriteCsvFileAsTable(GamsPath,all_smi.AntiAffinityConstraintsList,"antiAffinity_sc"," ",log);
                WriteCsvAsTable.WriteCsvFileAsTable(GamsPath,all_smi.AvailabilityPerComponentList,"availability_sc"," ",log);
                */
                String basic = WriteStringAsTable.WriteStringFileAsTable(GamsPath,all_smi.BasicList,"basic"," ",log);               
        	String elastic = WriteStringAsTable.WriteStringFileAsTable(GamsPath,all_smi.ElasticList,"elastic"," ",log);
        	String table = WriteStringAsTable.WriteStringFileAsTable(GamsPath,all_smi.TableList,"table"," ",log);
                String memory_per_component = WriteStringAsTable.WriteStringFileAsTable(GamsPath,all_smi.MemoryPerComponentList,"memory_per_component"," ",log);
                String vms = WriteStringAsTable.WriteStringFileAsTable(GamsPath,all_smi.ServiceComponentList,"vms","no",log);
        	String affinity_sc = WriteStringAsTable.WriteStringFileAsTable(GamsPath,all_smi.AffinityConstraintsList,"affinity_sc"," ",log);
        	String antiAffinity_sc = WriteStringAsTable.WriteStringFileAsTable(GamsPath,all_smi.AntiAffinityConstraintsList,"anti_affinity_sc"," ",log);
                String availability_sc = WriteStringAsTable.WriteStringFileAsTable(GamsPath,all_smi.AvailabilityPerComponentList,"availability_sc"," ",log);
                setReturnedParams("basic",basic);
                setReturnedParams("elastic",elastic);
                setReturnedParams("table",table);
                setReturnedParams("memory_per_component",memory_per_component);
                setReturnedParams("vms",vms);
                setReturnedParams("affinity_sc",affinity_sc);
                setReturnedParams("anti_affinity_sc",antiAffinity_sc);
		setReturnedParams("availability_sc",availability_sc);		
		
                /*
        	WriteCsv.WriteSecondaryCsv(GamsPath,all_smi.max_numberOfComponents,"components","vm",log);
        	WriteCsv.WriteSecondaryCsv(GamsPath,all_smi.max_numberOfServices,"services","a",log);
        	*/
                String components = WriteString.WriteSecondaryString(GamsPath,all_smi.max_numberOfComponents,"components","vm",log);
                String services = WriteString.WriteSecondaryString(GamsPath,all_smi.max_numberOfServices,"services","a",log);
                setReturnedParams("components",components);
                setReturnedParams("services",services);
                
                /*
        	WriteCsv.WriteSecondaryCsv(GamsPath,compAll.ListOfCompinations.size(),"combs_","c",log);
        	WriteCsvAsTable.WriteCsvFileAsTable2(GamsPath,compAll.ListOfCompinations,all_smi.max_numberOfComponents,"combs");
        	*/
                String combs_ = WriteString.WriteSecondaryString(GamsPath,compAll.ListOfCompinations.size(),"combs_","c",log);
                String combs = WriteStringAsTable.WriteStringFileAsTable2(GamsPath,compAll.ListOfCompinations,all_smi.max_numberOfComponents,"combs");
                setReturnedParams("combs_",combs_);
                setReturnedParams("combs",combs);
                
                /*
        	WriteCsvAsTable.WriteCsvFileAsTable3(GamsPath,all_smi.affinityRule_List,compAll.ListOfCompinations,"affinity_rules",log,all_smi.IdList);
        	WriteCsvAsTable.WriteCsvFileAsTable3(GamsPath,all_smi.antiAffinityRule_List,compAll.ListOfCompinations,"antiAffinity_rules",log,all_smi.IdList);
                */
                String affinity_rules = WriteStringAsTable.WriteStringFileAsTable3(GamsPath,all_smi.affinityRule_List,compAll.ListOfCompinations,"affinity_rules",log,all_smi.IdList);
                String antiAffinity_rules = WriteStringAsTable.WriteStringFileAsTable3(GamsPath,all_smi.antiAffinityRule_List,compAll.ListOfCompinations,"anti_affinity_rules",log,all_smi.IdList);
                setReturnedParams("affinity_rules",affinity_rules);
                setReturnedParams("anti_affinity_rules",antiAffinity_rules);
                
                WriteServicesInfo.WriteServicesInfoFile2(AllocationPath,formParams,all_smi.IdList,all_smi.smAnalyzerList,log);
                String ServicesInfo = WriteServicesInfoAsString.WriteServicesInfoFileAsString2(AllocationPath,formParams,all_smi.IdList,all_smi.smAnalyzerList,log);
                log.info("ServicesInfo"+" : "+ServicesInfo);
                outputParams.add("ServicesInfo", ServicesInfo);
                
    	} catch (TransformerException tfe) {
    		
                String msg = "There was a TransformerException: "+ tfe.getMessage(); 
                log.error(msg);
                outputParams.add("Exception", msg);
                return outputParams;
    		
	} catch (ParserConfigurationException pce) {
            
                String msg = "There was a ParserConfigurationException: "+ pce.getMessage();
                log.error(msg);
                outputParams.add("Exception", msg);
                return outputParams;
		    		
	} catch (Exception e) {
                       
                String msg = "There was a Exception: "+ e.getMessage();
                log.error(msg);
                outputParams.add("Exception", msg);
                return outputParams;    
    		
	}//catch	
    	
        // end of RESTful call /createModel
    	
	log.info("opModel was created! at " + GamsPath);
		
	// return path where all GAMS input files are located to AC Gateway 
	
        outputParams.add("opModel", GamsPath);
        outputParams.add("AllocationInfoPath", AllocationPath);
                
        return outputParams;
        
    }//createModel()
    
    private void setPath(MultivaluedMap<String, String> formParams) throws FileNotFoundException,IOException
    {
    		
        GamsPath = formParams.get("ModelPath").get(0);
        AllocationPath = formParams.get("AllocationPath").get(0);       
        
        Paths.CreateDirectory(GamsPath);
        Paths.CreateDirectory(AllocationPath);
        
        log.info("GamsPath: " + GamsPath);
    	log.info("AllocationPath: " + AllocationPath);
    	
    }//setPath()
    
    private void setReturnedParams(String valueName,String value)
            throws IOException,FileNotFoundException
    {
        log.info(valueName+" : "+value);
        
        outputParams.add(valueName, value);
        
        FileWriter writer = new FileWriter(GamsPath + valueName+".csv");
        
        writer = new FileWriter(GamsPath + valueName+".csv");
        writer.append(value);
        writer.flush();
        writer.close();
            
    }//setReturnedParams()
}//TRECAnalyzer class


