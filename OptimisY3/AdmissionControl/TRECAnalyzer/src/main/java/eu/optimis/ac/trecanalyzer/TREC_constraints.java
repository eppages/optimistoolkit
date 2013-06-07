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

import eu.optimis.ac.trecanalyzer.utils.FileFunctions;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Properties;
import org.apache.log4j.Logger;

public class TREC_constraints {
	
        public LinkedList<String> ConstraintList = new LinkedList<String>();
    
        public TREC_constraints(String gamsPath,String constraintName,int numberOfServices,Logger log)
        {
            String constraintValue = getConstraintValue(gamsPath,constraintName,log);
            
            for(int i=0;i<numberOfServices;i++)
                ConstraintList.addLast(constraintValue);
            
        }//constructor
        
        private static String getConstraintValue(String path,String constraintName,Logger log)
        {
            Properties props = new Properties();
            
            try {  
                props.load(TREC_constraints.class.getClassLoader().getResourceAsStream("config.properties"));
            } catch (IOException ex) {
                log.error(ex.getMessage());
            }
                    
            if(constraintName.contains("trust_constraint"))
            {
                String constraint = null;
                
                if(FileFunctions.FileExists(path,"TRUST__constraint.csv"))
                    constraint = FileFunctions.readFileAsStringWithPath(path+"TRUST__constraint.csv", log);
                else
                    constraint = props.getProperty("temp.trust_constraint");
                
                FileFunctions.FileWrite(path+"TRUSTconstraint.csv", constraint, log);
                
                return constraint;
            }//trust
            
            if(constraintName.contains("risk_constraint"))
            {
                String constraint = null;
                
                if(FileFunctions.FileExists(path,"RISK__constraint.csv"))
                    constraint = FileFunctions.readFileAsStringWithPath(path+"RISK__constraint.csv", log);
                else
                    constraint = props.getProperty("temp.risk_constraint");
                
                FileFunctions.FileWrite(path+"RISKconstraint.csv", constraint, log);
                
                return constraint;
            }//risk
            
            if(constraintName.contains("eco_constraint"))
            {
                String constraint = null;
                
                if(FileFunctions.FileExists(path,"ECO__constraint.csv"))
                    constraint = FileFunctions.readFileAsStringWithPath(path+"ECO__constraint.csv", log);
                else
                    constraint = props.getProperty("temp.eco_constraint");
                
                FileFunctions.FileWrite(path+"ECOconstraint.csv", constraint, log);
                
                return constraint;
            }//eco
            
            if(constraintName.contains("cost_constraint"))
            {
                String constraint = null;
                
                if(FileFunctions.FileExists(path,"COST__constraint.csv"))
                    constraint = FileFunctions.readFileAsStringWithPath(path+"COST__constraint.csv", log);
                else
                    constraint = props.getProperty("temp.cost_constraint");
                
                FileFunctions.FileWrite(path+"COSTconstraint.csv", constraint, log);
                
                return constraint;
            }//eco
            
            return null;
            	
        }//getConstraintValue()
        
		
}//class