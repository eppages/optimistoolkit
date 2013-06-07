/*
 * Copyright (c) 2013 National Technical University of Athens (NTUA)
 *	
 *   							MIT License
 *   
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */



package eu.optimis.ac.gateway.acGateway;

import eu.optimis.ac.gateway.configuration.GamsPath;
import eu.optimis.ac.gateway.utils.FileFunctions;
import eu.optimis.ac.gateway.utils.Numbers;
import java.io.FileWriter;
import java.io.IOException;
import org.apache.log4j.Logger;

public class SetPolicy {

	private static Logger log ;
		
	private String trust_weight="";
	private String eco_weight="";
	private String risk_weight="";
	private String cost_weight="";
		
	private String path="";
		
	public String ReturnedMessage="";
		
	public SetPolicy(String trustweight,String ecoweight,String riskweight,String costweight,Logger the_log)
	{
		log = the_log;
		
		trust_weight=trustweight;
		eco_weight=ecoweight;
		risk_weight=riskweight;
		cost_weight=costweight;
		
		log.info("setPolicy method invoked!");
		
		if(CheckForErrorsInInput())return;
		
		path = GamsPath.getGamsPath(log);
		
		// Delete old csv files
		Boolean DeletionOK=DoDelete_Old_WeightFiles(path);
		if(!DeletionOK)return;
		
		DoWeightCsvFileCreation();	
		
                log.info("setPolicy method Finished!");
	}//Constructor
		
	private void DoWeightCsvFileCreation()
	{
		String filename = null;
		try{
			
            filename = "ECO__weight.csv";
            WeightCsvFileCreation(filename,eco_weight);	
            
            filename = "TRUST__weight.csv";
            WeightCsvFileCreation(filename,trust_weight);
            
            filename = "RISK__weight.csv";
            WeightCsvFileCreation(filename,risk_weight);
            
            filename = "COST__weight.csv";
            WeightCsvFileCreation(filename,cost_weight);
            
            ReturnedMessage+="ECO__weight = "+eco_weight+"\n";
            ReturnedMessage+="TRUST__weight = "+trust_weight+"\n";
            ReturnedMessage+="RISK__weight = "+risk_weight+"\n";
            ReturnedMessage+="COST__weight = "+cost_weight+"\n";
                
        }//try 
        catch (IOException e) 
        {
            ReturnedMessage="IOException in Creation of File "+filename;
            log.error(ReturnedMessage);
            e.printStackTrace();
        }

        log.info("File Creation Finished OK!");
        
	}//DoWeightCsvFileCreation()	
		
	private void WeightCsvFileCreation(String filename,String value) throws IOException
	{
		log.info("Creation of "+ filename +" file");
		
		try{
			
			FileWriter writer = new FileWriter(path + filename); 
	    	
		    writer.append(value);    	 
		    writer.flush();
		    writer.close();
	    
		}catch (IOException e) {
			ReturnedMessage+="IOException in Creation of File "+filename+" "+e.getMessage();
		}
	    
	    // end of  file creation
	               
	    log.info(filename.replace(".csv","") +" = " + value );
	    
        if(FileFunctions.FileExists(path,filename))
        	ReturnedMessage+=filename+" was created!\n";
        else
        	ReturnedMessage+=filename+" was not created!";
        
	}//WeightCsvFileCreation()	
		
	private Boolean DoDelete_Old_WeightFiles(String path)
	{
            int numberOfOldFilesExists=NumberOfOldFilesExists(path);
                
            if(numberOfOldFilesExists==0)
            {
            	String msg="\nOld files not deleted because didn't exist !\n";
            	ReturnedMessage+=msg;
            	log.info(msg);
                return true;
            }
                
            int numberOfFilesDeleted=deleteOld_WeightFiles();
                
            if (( numberOfFilesDeleted>=4 )||(numberOfOldFilesExists==numberOfFilesDeleted))
            {
            	String msg="\nOld files deleted! \n";
            	ReturnedMessage+=msg;
            	log.info(msg);
                return true;
            }
            else
            {	   
            	String msg="\nProblem while deleting old files! Deleted Only "+numberOfFilesDeleted+"/"+numberOfOldFilesExists+" Files\n";
            	ReturnedMessage=msg;
            	log.info(msg);
                return false;
            }
		
	} //DoDelete_Old_WeightFiles()
	
	//auxiliary methods to first delete old files on server 
	// returns numbers of files deleted 
	private int deleteOld_WeightFiles() {
		
		return FileFunctions.deleteFiles_containingString(path, "_weight.csv",log);
		
	}//deleteOld_WeightFiles()
	
	private static int NumberOfOldFilesExists(String filepath)
    {
        int counter=0;
        
        if(FileFunctions.FileExists(filepath,"COST__weight.csv"))counter++;
        if(FileFunctions.FileExists(filepath,"RISK__weight.csv"))counter++;
        if(FileFunctions.FileExists(filepath,"TRUST__weight.csv"))counter++;
        if(FileFunctions.FileExists(filepath,"ECO__weight.csv"))counter++;
        
        return counter;
    }//NumberOfOldFilesExists()
	
	private Boolean CheckForErrorsInInput()
	{
		String VariableName = null;
		
		try {
			VariableName="TrustWeight";
                        Numbers.ConvertStringToFloat(trust_weight);
	    
                        VariableName="EcoWeight";
                        Numbers.ConvertStringToFloat(eco_weight);
	    
                        VariableName="RiskWeight";
                        Numbers.ConvertStringToFloat(risk_weight);
	    
                        VariableName="CostWeight";
                        Numbers.ConvertStringToFloat(cost_weight);
        
		}//try
        catch (NumberFormatException nfe)
        {
            ReturnedMessage="NumberFormatException: " + nfe.getMessage()+" in Variable "+VariableName;
            log.error(ReturnedMessage);
            nfe.printStackTrace();
            return true;
        }
		
    	return false;
	}//CheckForErrorsInInput()
			
}//class
