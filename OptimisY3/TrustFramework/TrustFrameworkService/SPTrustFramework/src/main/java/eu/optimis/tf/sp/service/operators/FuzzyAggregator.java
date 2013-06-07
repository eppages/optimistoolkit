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
Francisco Javier Nieto. Atos Research and Innovation, Atos SPAIN SA
@email francisco.nieto@atosresearch.eu 

**/

package eu.optimis.tf.sp.service.operators;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;

import org.apache.log4j.Logger;
import org.apache.openjpa.lib.log.Log;

import net.sourceforge.jFuzzyLogic.FIS;
import net.sourceforge.jFuzzyLogic.FunctionBlock;
import net.sourceforge.jFuzzyLogic.rule.Variable;
import eu.optimis.common.trec.db.sp.TrecSP2IPDAO;
import eu.optimis.trec.common.db.sp.model.SpToIp;


public class FuzzyAggregator 
{
	private FIS currentFis = null;
	private String fileName = "";
	
	private Logger log = Logger.getLogger(this.getClass());
	
	public FuzzyAggregator () 
	{		       
       fileName = "fuzzy/IPTrust.fcl";       
	}
	
	public FuzzyAggregator (String rulesFile)
	{
		// Load from 'FCL' file
        String fileName = "fuzzy/" + rulesFile;
        currentFis = FIS.load(fileName,true);
        
        // Error while loading?
        if( currentFis == null ) 
        { 
        	log.error("It was not possible to load fuzzy file: '" + fileName + "'");
            return;
        }

        // Show 
        //currentFis.chart();
	}
	
	public float calculateTrustAggregation (HashMap<String, Double> parameters)
	{		
		//Configure fuzzy file
		//Step 1 --> Get file name corresponding to the aspect		
		// Get class loader, for avoiding problems with Tomcat (loading local files)
        ClassLoader loader = FuzzyAggregator.class.getClassLoader();
        if(loader==null)
          loader = ClassLoader.getSystemClassLoader();

        // We want to load file located in WEB-INF/classes/fuzzy/               
        java.net.URL url = loader.getResource(fileName);
                		
		// Step 2 --> Load from 'FCL' file        
        //currentFis = FIS.load(fileName,true); Removed, as it is not enough for Tomcat
        try
        {        	
        	currentFis = FIS.load(url.openStream(), true);
        }
        catch (IOException ex)
        {
        	log.error("Problem loading the file for Fuzzy!");
        	log.error(url.getPath());
			ex.printStackTrace();
        }
        
        // Error while loading?
        if( currentFis == null ) 
        { 
        	log.error("It was not possible to load fuzzy file: '" + fileName + "'");
        	log.error(url.getPath());
            return -1;
        }

        // Show 
        //currentFis.chart();
                        
		//Step 3 --> Retrieve necessary parameters
        HashMap<String, Double> myHash = new HashMap<String, Double>();
		
		//Table with parameter to be retrieved        
        FunctionBlock fuzzySet = currentFis.iterator().next();
        log.info("FunctionBlock " + fuzzySet.getName() + " opened. Retrieving input variables...");
        Iterator<Variable> varList = fuzzySet.variables().iterator();
        while (varList.hasNext())
        {
        	Variable currentVar = varList.next();
        	if (!currentVar.isOutputVarable())
        	{
        		//Get variable info
        		String varName = currentVar.getName();
        		log.info("Looking for variable: " + varName + "... ");
        		
        		//Retrieve variable value from DB
        		String varValue = "0.0";
        		try
        		{        	
        			if (parameters.containsKey(varName))
    				{
    					//Assign value to the variable as input
    					varValue = parameters.get(varName).toString();
    				}
        			else
        			{
        				throw new Exception ();
        			}
                    
            		log.debug(" Done! --> " + varValue);
        		}
        		catch (Exception ex)
        		{
        			log.error(" Not Found!! --> Skipping!");
        		}
        		
        		//Push value to the Fuzzy Set (only if found)
        		if (!varValue.equalsIgnoreCase("0.0"))
        		{
        			myHash.put(varName, new Double (varValue));
        		}        		
        	}        	
        }
        		
		//Step 5 --> Calculate prediction
		log.info("Calculating trust... \n");
		float trustValue = 0.0f;		
		trustValue = (float)calculateTrust(myHash);
						
		return trustValue;
	}
	
	public float calculateTrustAggregation (String idEntity)
	{		
		//Configure fuzzy file
		//Step 1 --> Get file name corresponding to the aspect		
		// Get class loader, for avoiding problems with Tomcat (loading local files)
        ClassLoader loader = FuzzyAggregator.class.getClassLoader();
        if(loader==null)
          loader = ClassLoader.getSystemClassLoader();

        // We want to load file located in WEB-INF/classes/fuzzy/               
        java.net.URL url = loader.getResource(fileName);
                		
		// Step 2 --> Load from 'FCL' file        
        //currentFis = FIS.load(fileName,true); Removed, as it is not enough for Tomcat
        try
        {        	
        	currentFis = FIS.load(url.openStream(), true);
        }
        catch (IOException ex)
        {
        	log.error("Problem loading the file for Fuzzy!");
        	log.error(url.getPath());
			ex.printStackTrace();
        }
        
        // Error while loading?
        if( currentFis == null ) 
        { 
            log.error("It was not possible to load fuzzy file: '" + fileName + "'");
            log.error(url.getPath());
            return -1;
        }

        // Show 
        //currentFis.chart();
                        
		//Step 3 --> Retrieve necessary parameters and values from DB
        HashMap<String, Double> myHash = new HashMap<String, Double>();
        try
		{         
        	TrecSP2IPDAO myDAO = new TrecSP2IPDAO();
            SpToIp trustValues = myDAO.getLastSP2IPTrust(idEntity);
            myHash.put("vmFormation", trustValues.getServiceWellFormed());
            myHash.put("runtime", trustValues.getSafetyRunGap());
            myHash.put("sla", trustValues.getSlaCompliance()); 
            myHash.put("ipReaction", trustValues.getIpReactionTime());           
            myHash.put("legal", trustValues.getIpComplianceWithLegal());
		}
		catch (Exception ex)
		{
			log.error("Problems retrieving data from DB!!");
			ex.printStackTrace();
		}
                
		//Table with parameters to be retrieved        
        FunctionBlock fuzzySet = currentFis.iterator().next();
        log.info("FunctionBlock " + fuzzySet.getName() + " opened. Retrieving input variables...");
        Iterator<Variable> varList = fuzzySet.variables().iterator();
        while (varList.hasNext())
        {
        	Variable currentVar = varList.next();
        	if (!currentVar.isOutputVarable())
        	{
        		//Get variable info
        		String varName = currentVar.getName();
        		log.info("Looking for variable: " + varName + "... ");
        		
        		//Check if this value is already provided or is missing       		       
        		if (myHash.containsKey(varName))
    			{
        			String varValue = myHash.get(varName).toString();
        			log.info(" Done! --> " + varValue);
    			}
        		else
        		{
        			log.error(" NOT AVAILABLE!!!");
        		}        		
        	}        	
        }
        		
		//Step 4 --> Calculate prediction
		log.info("Calculating trust... \n");
		float trustValue = 0.0f;		
		trustValue = (float)calculateTrust(myHash);
						
		return trustValue;
	}
	
	private double calculateTrust (HashMap<String, Double> arguments)
	{
		//Check inputs required and available
		Iterator<Variable> variablesList = currentFis.getFunctionBlock(null).variablesSorted().iterator();
		int varFound = 0;
		int varTotal = 0;
				
		if (arguments != null)
		{
			while (variablesList.hasNext())
			{
				//Get variable name
				Variable currentVar = variablesList.next();
				String varName = currentVar.getName();
				varTotal++;
				double value = 0.0;
					
				//Look for the variable in the arguments list
				if (arguments.containsKey(varName))
				{
					//Assign value to the variable as input
					value = (Double)(arguments.get(varName)).doubleValue();
					currentFis.setVariable(varName, value);
					varFound++;
					log.info("Variable --" + varName + "-- found: " + value);
				}			
			}
		}
						
		//Warning messages
		if (arguments == null || varTotal-1 != varFound)
		{
			log.warn("WARNING! Input not set for all the required variables!");
		}
				
        //Evaluate the result
        currentFis.evaluate();

        //Show output variable's chart 
        //currentFis.getVariable("trust").chartDefuzzifier(true);
        
		return currentFis.getVariable("trust").getLatestDefuzzifiedValue();		
	}
								
//	public static void main(String[] args) 
//	{
//		FuzzyAggregator myCalculator = new FuzzyAggregator();
//		/*FuzzyCalculator myCalculator = new FuzzyCalculator ("IPTrust.fcl");
//		
//		HashMap<String, Double> myHash = new HashMap<String, Double>();
//		myHash.put("vmFormation", new Double ("4.85"));
//		myHash.put("runtime", new Double ("3.75"));
//		myHash.put("sla", new Double ("4.25"));
//		myHash.put("ipReaction", new Double ("2.45"));
//		myHash.put("legal", new Double ("3.45"));
//		/*
//		double result = myCalculator.calculateTrust(myHash);
//		System.out.println ("Received value: " + result);
//		TTest javi = new TTestImpl();
//		
//		double result = myCalculator.calculateTrustAggregation(myHash);*/
//		double result = myCalculator.calculateTrustAggregation("a60b5031-179f-476f-89c3-532db31180e6");
//		System.out.println ("Received value (CoG): " + result);
//	}
	
}
