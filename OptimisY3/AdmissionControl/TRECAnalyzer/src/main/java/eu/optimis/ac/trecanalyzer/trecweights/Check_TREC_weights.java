/*
 * Copyright (c) 2013 National Technical University of Athens (NTUA)
 *	
 *   							MIT License
 *   
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */


package eu.optimis.ac.trecanalyzer.trecweights;

import eu.optimis.ac.trecanalyzer.utils.FileFunctions;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;
import org.apache.log4j.Logger;

public class Check_TREC_weights {
	
	public Check_TREC_weights(String gamsPath, Logger log)
	{
		    
                 copyWeights(gamsPath,log);
                 
                 if(NumberOfOldFilesExists(gamsPath)!=4)
                 {
                try {
                    log.error("Temp _weight csv files not 4");
                    
                    Properties props = new Properties();
                    props.load(Check_TREC_weights.class.getClassLoader().getResourceAsStream("config.properties"));  
       
                    String filename = "cost_weight.csv";
                    String value = props.getProperty("temp.cost_weight");
                    
                    IfWeightNotExistsCreateIt(gamsPath,filename,value,log);
                    
                    filename = "risk_weight.csv";
                    value = props.getProperty("temp.risk_weight");
                    
                    IfWeightNotExistsCreateIt(gamsPath,filename,value,log);
                    
                    filename = "trust_weight.csv";
                    value = props.getProperty("temp.trust_weight");
                    
                    IfWeightNotExistsCreateIt(gamsPath,filename,value,log);
                    
                    filename = "eco_weight.csv";
                    value = props.getProperty("temp.eco_weight");
                    
                    IfWeightNotExistsCreateIt(gamsPath,filename,value,log);
                    
                } //-if
                catch (IOException ex) {
                    
                    log.error(ex.getMessage());
                }
                     
                 }//-if
                 
	}//constructor
	
        private void IfWeightNotExistsCreateIt(String gamsPath,String filename,String value,Logger log)
        {
            if(FileFunctions.FileExists(gamsPath,filename))
                return;
            
            log.info("Creating "+filename+" because is missing");
            
            WeightCsvFileCreation(filename, gamsPath, value, log);
            
        }//IfWeightNotExistsCreateIt()
        
        private void copyWeights(String path,Logger log)
        {
            int x=1;if(x==1)return;        
            
            FileFunctions.copyfile(path+"TRUST__weight.csv", path+"trust_weight.csv", log);
            FileFunctions.copyfile(path+"ECO__weight.csv", path+"eco_weight.csv", log);
            FileFunctions.copyfile(path+"RISK__weight.csv", path+"risk_weight.csv", log);
            FileFunctions.copyfile(path+"COST__weight.csv", path+"cost_weight.csv", log);
		
        }//copyWeights()
        
        private static int NumberOfOldFilesExists(String filepath)
        {
            int counter=0;
        
            if(FileFunctions.FileExists(filepath,"cost_weight.csv"))counter++;
            if(FileFunctions.FileExists(filepath,"risk_weight.csv"))counter++;
            if(FileFunctions.FileExists(filepath,"trust_weight.csv"))counter++;
            if(FileFunctions.FileExists(filepath,"eco_weight.csv"))counter++;
        
            return counter;
        }//NumberOfOldFilesExists()
        
        private void WeightCsvFileCreation(String filename, String path, String value, Logger log)
	{
                try{
			FileWriter writer = new FileWriter(path + filename); 
	    	
                        writer.append(value);    	 
                        writer.flush();
                        writer.close();
	    
		}catch (IOException e) {
			log.error("IOException in Creation of File "+filename+" "+e.getMessage());
		}	
              
	}//WeightCsvFileCreation()	
		
}//class