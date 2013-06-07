/*
 * Copyright (c) 2013 National Technical University of Athens (NTUA)
 *	
 *   							MIT License
 *   
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */


package eu.optimis.ac.gateway.acLogsToWeb.fiveLastExecutions;

import eu.optimis.ac.gateway.configuration.GetFileNames;
import eu.optimis.ac.gateway.configuration.GetLoggingLevel;
import eu.optimis.ac.gateway.utils.FileFunctions;
import eu.optimis.ac.gateway.utils.PropertiesUtils;
import org.apache.log4j.Logger;


public class ReadAllocationPatterns {
    
    public String allocationPatternTables = null;
    
    public ReadAllocationPatterns(Logger log)
    {    
        for(int counter=1;counter<=Integer.parseInt(PropertiesUtils.getBoundle("AClogs.lastExecution"));counter++)
        {
            String FileName = GetFileNames.getAClogs_fileName(log).
                    replace(".txt", "-"+Integer.toString(counter)+".txt");
            
            if(GetLoggingLevel.getLoggingLevel(log)<=1)
            log.info(FileName);
            
            if(!FileFunctions.FileExists(FileName, ""))break;;
            
            if(counter == 1)
                allocationPatternTables = FileFunctions.readFileAsStringWithPath(FileName, log);
            
            else 
                allocationPatternTables += FileFunctions.readFileAsStringWithPath(FileName, log);
            
        }//for-i
    }//constructor
    
}//class
