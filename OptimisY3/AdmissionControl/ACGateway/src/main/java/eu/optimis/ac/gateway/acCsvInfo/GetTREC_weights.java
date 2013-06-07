/*
 * Copyright (c) 2013 National Technical University of Athens (NTUA)
 *	
 *   							MIT License
 *   
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package eu.optimis.ac.gateway.acCsvInfo;

import eu.optimis.ac.gateway.configuration.GamsPath;
import eu.optimis.ac.gateway.utils.FileFunctions;
import org.apache.log4j.Logger;

public class GetTREC_weights {
	
	public static String getTrust_Weight(Logger log)
	{
		return FileFunctions.readFileAsStringWithPath(GamsPath.getGamsPath(log)+"TRUST__weight.csv", log);
		
	}//getTrust_Weight()
	
	public static String getRisk_Weight(Logger log)
	{
		return FileFunctions.readFileAsStringWithPath(GamsPath.getGamsPath(log)+"RISK__weight.csv", log);
		
	}//getRisk_Weight()
	
	public static String getEco_Weight(Logger log)
	{
		return FileFunctions.readFileAsStringWithPath(GamsPath.getGamsPath(log)+"ECO__weight.csv", log);
		
	}//getEco_Weight()
	
	public static String getCost_Weight(Logger log)
	{
		return FileFunctions.readFileAsStringWithPath(GamsPath.getGamsPath(log)+"COST__weight.csv", log);
		
	}//getCost_Weight()
	
        public static String getTempTrust_Weight(Logger log)
	{
		return FileFunctions.readFileAsStringWithPath(GamsPath.getGamsPath(log)+"trust_weight.csv", log);
		
	}//getTrust_Weight()
	
	public static String getTempRisk_Weight(Logger log)
	{
		return FileFunctions.readFileAsStringWithPath(GamsPath.getGamsPath(log)+"risk_weight.csv", log);
		
	}//getRisk_Weight()
	
	public static String getTempEco_Weight(Logger log)
	{
		return FileFunctions.readFileAsStringWithPath(GamsPath.getGamsPath(log)+"eco_weight.csv", log);
		
	}//getEco_Weight()
	
	public static String getTempCost_Weight(Logger log)
	{
		return FileFunctions.readFileAsStringWithPath(GamsPath.getGamsPath(log)+"cost_weight.csv", log);
		
	}//getCost_Weight()
	
        
	public static void printTREC_WeightsToScreen(String message,
			String trust_weight,String risk_weight,String eco_weight,String cost_weight)
	{
		System.out.println(message);
		
		System.out.println();
		
		System.out.println("trust_weight : "+trust_weight);
		System.out.println("risk_weight : "+risk_weight);
		System.out.println("eco_weight : "+eco_weight);
		System.out.println("cost_weight : "+cost_weight);
		
		System.out.println();
	}//printTREC_WeightsToScreen()
	
        public static String getTrust_Weight2(String gamsPath,Logger log)
	{
		return FileFunctions.readFileAsStringWithPath(gamsPath+"TRUST__weight.csv");
		
	}//getTrust_Weight()
	
	public static String getRisk_Weight2(String gamsPath,Logger log)
	{
		return FileFunctions.readFileAsStringWithPath(gamsPath+"RISK__weight.csv");
		
	}//getRisk_Weight()
	
	public static String getEco_Weight2(String gamsPath,Logger log)
	{
		return FileFunctions.readFileAsStringWithPath(gamsPath+"ECO__weight.csv");
		
	}//getEco_Weight()
	
	public static String getCost_Weight2(String gamsPath,Logger log)
	{
		return FileFunctions.readFileAsStringWithPath(gamsPath+"COST__weight.csv");
		
	}//getCost_Weight()
	
        public static String getTempTrust_Weight2(String gamsPath,Logger log)
	{
		return FileFunctions.readFileAsStringWithPath(gamsPath+"trust_weight.csv");
		
	}//getTrust_Weight()
	
	public static String getTempRisk_Weight2(String gamsPath,Logger log)
	{
		return FileFunctions.readFileAsStringWithPath(gamsPath+"risk_weight.csv");
		
	}//getRisk_Weight()
	
	public static String getTempEco_Weight2(String gamsPath,Logger log)
	{
		return FileFunctions.readFileAsStringWithPath(gamsPath+"eco_weight.csv");
		
	}//getEco_Weight()
	
	public static String getTempCost_Weight2(String gamsPath,Logger log)
	{
		return FileFunctions.readFileAsStringWithPath(gamsPath+"cost_weight.csv");
		
	}//getCost_Weight()
	
        
}//class
