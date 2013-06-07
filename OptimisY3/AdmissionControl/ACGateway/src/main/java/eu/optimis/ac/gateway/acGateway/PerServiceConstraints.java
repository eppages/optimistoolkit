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

public class PerServiceConstraints {
    
    private static Logger log ;
		
    private String trustLevel_constraint;
    private String riskLevel_constraint;
    private String ecoValue_constraint="";
    private String costInEuros_constraint="";
		
    private String path="";
		
    public String ReturnedMessage="";
        
    public PerServiceConstraints(String trustLevelConstraint, String riskLevelConstraint, 
            String ecoValueConstraint, String costInEurosConstraint, Logger the_log)
    {
                log = the_log;
		
		trustLevel_constraint = trustLevelConstraint;
		riskLevel_constraint = riskLevelConstraint;
		ecoValue_constraint = ecoValueConstraint;
		costInEuros_constraint = costInEurosConstraint;
		
		log.info("PerServiceConstraints method invoked!");
		
		path = GamsPath.getGamsPath(log);
		
                if(CheckForErrorsInInput())return;
                
		// Delete old csv files
		Boolean DeletionOK=DoDelete_Old_ConstraintFiles(path);
		if(!DeletionOK)return;
		
		DoConstraintCsvFileCreation();	
		
                log.info("PerServiceConstraints method Finished!");
    }//constructor
    
    private void DoConstraintCsvFileCreation()
	{
		String filename = null;
		try{
			
            filename = "ECO__constraint.csv";
            ConstraintCsvFileCreation(filename,ecoValue_constraint);	
            
            filename = "TRUST__constraint.csv";
            ConstraintCsvFileCreation(filename,trustLevel_constraint);
            
            filename = "RISK__constraint.csv";
            ConstraintCsvFileCreation(filename,riskLevel_constraint);
            
            filename = "COST__constraint.csv";
            ConstraintCsvFileCreation(filename,costInEuros_constraint);
            
            ReturnedMessage+="eco_constraint = "+ecoValue_constraint+"\n";
            ReturnedMessage+="trust_constraint = "+trustLevel_constraint+"\n";
            ReturnedMessage+="risk_constraint = "+riskLevel_constraint+"\n";
            ReturnedMessage+="cost_constraint = "+costInEuros_constraint+"\n";
                
        }//try 
        catch (IOException e) 
        {
            ReturnedMessage="IOException in Creation of File "+filename;
            log.error(ReturnedMessage);
            e.printStackTrace();
        }

        log.info("File Creation Finished OK!");
        
    }//DoConstraintCsvFileCreation()	
	
    private void ConstraintCsvFileCreation(String filename,String value) throws IOException
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
        
    }//ConstraintCsvFileCreation()
    
    private Boolean DoDelete_Old_ConstraintFiles(String path)
	{
            int numberOfOldFilesExists=NumberOfOldFilesExists(path);
                
            if(numberOfOldFilesExists==0)
            {
            	String msg="\nOld files not deleted because didn't exist !\n";
            	ReturnedMessage+=msg;
            	log.info(msg);
                return true;
            }
                
            int numberOfFilesDeleted=deleteOld_ConstraintFiles();
                
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
		
	} //DoDelete_Old_ConstraintFiles()
	
	//auxiliary methods to first delete old files on server 
	// returns numbers of files deleted 
	private int deleteOld_ConstraintFiles() {
		
		return FileFunctions.deleteFiles_containingString(path, "_constraint.csv",log);
		
	}//deleteOld_WeightFiles()
	
	private static int NumberOfOldFilesExists(String filepath)
    {
        int counter=0;
        
        if(FileFunctions.FileExists(filepath,"COST__constraint.csv"))counter++;
        if(FileFunctions.FileExists(filepath,"RISK__constraint.csv"))counter++;
        if(FileFunctions.FileExists(filepath,"TRUST__constraint.csv"))counter++;
        if(FileFunctions.FileExists(filepath,"ECO__constraint.csv"))counter++;
        
        return counter;
    }//NumberOfOldFilesExists()
    
    private Boolean CheckForErrorsInInput()
	{
		String VariableName = null;
		
		try {
			
                        VariableName="ecoValue_constraint";
                        Numbers.ConvertStringToFloat(ecoValue_constraint);
	    
                        VariableName="costInEuros_constraint";
                        Numbers.ConvertStringToFloat(costInEuros_constraint);
        
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
