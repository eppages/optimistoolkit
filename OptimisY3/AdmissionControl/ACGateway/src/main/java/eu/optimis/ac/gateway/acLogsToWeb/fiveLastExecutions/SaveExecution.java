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
import java.util.ArrayList;
import org.apache.log4j.Logger;

public class SaveExecution {
        
    private static String DetailsAsXMLString(ArrayList<String> Header_List, ArrayList<String> Tag_List, ArrayList<ArrayList<String>> Info_List)
    {
        
        StringBuilder strB = new StringBuilder(200);
        
        strB.append("<execution_details>");
        
        for(int i=0;i<Header_List.size();i++)
        {
            String header = Header_List.get(i);
            
            ArrayList<String> list = Info_List.get(i);
            
            strB.append("<header name=\"");
            strB.append(header);
            strB.append("\"");
            for(int j=0;j<Tag_List.size();j++)
            {
                strB.append(" "+Tag_List.get(j).replace("Model Decision", "Model_Decision") +"=\""
                        +list.get(j)+"\"");
                        
            }//for-j
            
            strB.append(">");
            
            strB.append("</header>");
        }//for-i
        
        strB.append("</execution_details>");
        
        return strB.toString();
    }//DetailsAsXMLString()
    
    private static void swiftFiles(Logger log)
    {
        
        int counter = Integer.parseInt(PropertiesUtils.getBoundle("AClogs.lastExecution"));
        
        while(counter>1)
        {
            
            String FileName = GetFileNames.getAClogs_fileName(log).
                    replace(".txt", "_"+Integer.toString(counter--)+".xml");
            
            if(GetLoggingLevel.getLoggingLevel(log)<=1)
            log.info("FileName:"+FileName+" "+FileFunctions.FileExists(FileName, ""));
            
            if(FileFunctions.FileExists(FileName, ""))
                FileFunctions.deletefile(FileName);
            
            if(GetLoggingLevel.getLoggingLevel(log)<=1)
            log.info("FileName:"+FileName+" "+FileFunctions.FileExists(FileName, ""));
            
            String FileName1 = GetFileNames.getAClogs_fileName(log).
                    replace(".txt", "_"+Integer.toString(counter)+".xml");
            
            String FileName2 = GetFileNames.getAClogs_fileName(log).
                    replace(".txt", "_"+Integer.toString(counter+1)+".xml");
            
            if(GetLoggingLevel.getLoggingLevel(log)<=1){
            log.info("FileName1:"+FileName1+" "+FileFunctions.FileExists(FileName1, "")+" "+FileFunctions.readFileAsStringWithPath(FileName1).hashCode());
            log.info("FileName2:"+FileName2+" "+FileFunctions.FileExists(FileName2, "")+" "+FileFunctions.readFileAsStringWithPath(FileName2).hashCode());}
            
            if(FileFunctions.FileExists(FileName1, ""))
                FileFunctions.copyfile(FileName1, FileName2, log);
            
            if(GetLoggingLevel.getLoggingLevel(log)<=1){
            log.info("FileName1:"+FileName1+" "+FileFunctions.FileExists(FileName1, "")+" "+FileFunctions.readFileAsStringWithPath(FileName1).hashCode());
            log.info("FileName2:"+FileName2+" "+FileFunctions.FileExists(FileName2, "")+" "+FileFunctions.readFileAsStringWithPath(FileName2).hashCode());}
        }//while
    }//swiftFiles()
    
    public static void ToFile(ArrayList<String> Header_List, ArrayList<String> Tag_List, ArrayList<ArrayList<String>> Info_List, Logger log)
    {
        swiftFiles(log);
        
        String FileName = GetFileNames.getAClogs_fileName(log).
                    replace(".txt", "_"+Integer.toString(1)+".xml");
        
        String message  = DetailsAsXMLString(Header_List, Tag_List, Info_List);
        
        FileFunctions.FileWrite(FileName, message, log);
        
    }//ToFile
}//class
