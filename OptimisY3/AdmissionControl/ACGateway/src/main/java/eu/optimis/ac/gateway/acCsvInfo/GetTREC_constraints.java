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

public class GetTREC_constraints {
	
	public static String getTrust_Constraint(Logger log)
	{
		return FileFunctions.readFileAsStringWithPath(GamsPath.getGamsPath(log)+"TRUST__constraint.csv", log);
		
	}//getTrust_Constraint()
	
	public static String getRisk_Constraint(Logger log)
	{
		return FileFunctions.readFileAsStringWithPath(GamsPath.getGamsPath(log)+"RISK__constraint.csv", log);
		
	}//getRisk_Constraint()
	
	public static String getEco_Constraint(Logger log)
	{
		return FileFunctions.readFileAsStringWithPath(GamsPath.getGamsPath(log)+"ECO__constraint.csv", log);
		
	}//getEco_Constraint()
	
	public static String getCost_Constraint(Logger log)
	{
		return FileFunctions.readFileAsStringWithPath(GamsPath.getGamsPath(log)+"COST__constraint.csv", log);
		
	}//getCost_Constraint()
	
        public static String getTempTrust_Constraint(Logger log)
	{
		return FileFunctions.readFileAsStringWithPath(GamsPath.getGamsPath(log)+"TRUSTconstraint.csv", log);
		
	}//getTrust_Constraint()
	
	public static String getTempRisk_Constraint(Logger log)
	{
		return FileFunctions.readFileAsStringWithPath(GamsPath.getGamsPath(log)+"RISKconstraint.csv", log);
		
	}//getRisk_Constraint()
	
	public static String getTempEco_Constraint(Logger log)
	{
		return FileFunctions.readFileAsStringWithPath(GamsPath.getGamsPath(log)+"ECOconstraint.csv", log);
		
	}//getEco_Constraint()
	
	public static String getTempCost_Constraint(Logger log)
	{
		return FileFunctions.readFileAsStringWithPath(GamsPath.getGamsPath(log)+"COSTconstraint.csv", log);
		
	}//getCost_Constraint()
	
        
	public static void printTREC_ConstraintsToScreen(String message,
			String trust_constraint,String risk_constraint,
                        String eco_constraint,String cost_constraint)
	{
		System.out.println(message);
		
		System.out.println();
		
		System.out.println("trust_constraint : "+trust_constraint);
		System.out.println("risk_constraint : "+risk_constraint);
		System.out.println("eco_constraint : "+eco_constraint);
		System.out.println("cost_constraint : "+cost_constraint);
		
		System.out.println();
	}//printTREC_ConstraintsToScreen()
	
        
        	public static String getTrust_Constraint2(String gamsPath,Logger log)
	{
		return FileFunctions.readFileAsStringWithPath(gamsPath+"TRUST__constraint.csv");
		
	}//getTrust_Constraint()
	
	public static String getRisk_Constraint2(String gamsPath,Logger log)
	{
		return FileFunctions.readFileAsStringWithPath(gamsPath+"RISK__constraint.csv");
		
	}//getRisk_Constraint()
	
	public static String getEco_Constraint2(String gamsPath,Logger log)
	{
		return FileFunctions.readFileAsStringWithPath(gamsPath+"ECO__constraint.csv");
		
	}//getEco_Constraint()
	
	public static String getCost_Constraint2(String gamsPath,Logger log)
	{
		return FileFunctions.readFileAsStringWithPath(gamsPath+"COST__constraint.csv");
		
	}//getCost_Constraint()
	
        public static String getTempTrust_Constraint2(String gamsPath,Logger log)
	{
		return FileFunctions.readFileAsStringWithPath(gamsPath+"TRUSTconstraint.csv");
		
	}//getTrust_Constraint()
	
	public static String getTempRisk_Constraint2(String gamsPath,Logger log)
	{
		return FileFunctions.readFileAsStringWithPath(gamsPath+"RISKconstraint.csv");
		
	}//getRisk_Constraint()
	
	public static String getTempEco_Constraint2(String gamsPath,Logger log)
	{
		return FileFunctions.readFileAsStringWithPath(gamsPath+"ECOconstraint.csv");
		
	}//getEco_Constraint()
	
	public static String getTempCost_Constraint2(String gamsPath,Logger log)
	{
		return FileFunctions.readFileAsStringWithPath(gamsPath+"COSTconstraint.csv");
		
	}//getCost_Constraint()
        
}//class
