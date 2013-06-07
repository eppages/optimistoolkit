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

public class SaveAllocationPatterns {
    
    public static void ToFile(String AllocationPatternTable, Logger log)
    {
        swiftFiles(log);
        
        String FileName = GetFileNames.getAClogs_fileName(log).
                    replace(".txt", "-"+Integer.toString(1)+".txt");
        
        FileFunctions.FileWrite(FileName, AllocationPatternTable, log);
        
    }//ToFile
    
    private static void swiftFiles(Logger log)
    {
        
        int counter = Integer.parseInt(PropertiesUtils.getBoundle("AClogs.lastExecution"));
        
        while(counter>1)
        {
            
            String FileName = GetFileNames.getAClogs_fileName(log).
                    replace(".txt", "-"+Integer.toString(counter--)+".txt");
            
            if(GetLoggingLevel.getLoggingLevel(log)<=1)
            log.info("FileName:"+FileName+" "+FileFunctions.FileExists(FileName, ""));
            
            if(FileFunctions.FileExists(FileName, ""))
                FileFunctions.deletefile(FileName);
            
            if(GetLoggingLevel.getLoggingLevel(log)<=1)
            log.info("FileName:"+FileName+" "+FileFunctions.FileExists(FileName, ""));
            
            String FileName1 = GetFileNames.getAClogs_fileName(log).
                    replace(".txt", "-"+Integer.toString(counter)+".txt");
            
            String FileName2 = GetFileNames.getAClogs_fileName(log).
                    replace(".txt", "-"+Integer.toString(counter+1)+".txt");
            
            if(GetLoggingLevel.getLoggingLevel(log)<=1){
            log.info("FileName1:"+FileName1+" "+FileFunctions.FileExists(FileName1, "")+" "+FileFunctions.readFileAsStringWithPath(FileName1).hashCode());
            log.info("FileName2:"+FileName2+" "+FileFunctions.FileExists(FileName2, "")+" "+FileFunctions.readFileAsStringWithPath(FileName2).hashCode());
            }
            
            if(FileFunctions.FileExists(FileName1, ""))
                FileFunctions.copyfile(FileName1, FileName2, log);
            
            if(GetLoggingLevel.getLoggingLevel(log)<=1){
            log.info("FileName1:"+FileName1+" "+FileFunctions.FileExists(FileName1, "")+" "+FileFunctions.readFileAsStringWithPath(FileName1).hashCode());
            log.info("FileName2:"+FileName2+" "+FileFunctions.FileExists(FileName2, "")+" "+FileFunctions.readFileAsStringWithPath(FileName2).hashCode());
            }
        }//while
    }//swiftFiles()
    
}//class
