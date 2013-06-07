/*
 * Copyright (c) 2013 National Technical University of Athens (NTUA)
 *	
 *   							MIT License
 *   
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */


package eu.optimis.ac.gateway.cleanup;

import static eu.optimis.ac.gateway.utils.FileFunctions.deletefile;
import java.io.File;

public class InitializeAdmissionControlLogsFolder {
    
   private static String AdmissionControlLogsFolder = 
           "/opt/optimis/var/log/AdmissionControl/" ;
   
   public static String clearLogs()
   {

       String result = "path : "+AdmissionControlLogsFolder+"\n\n";
       
       File dir = new File(AdmissionControlLogsFolder);
        
        if(!dir.exists())return AdmissionControlLogsFolder+" didn't exists";
            
            String[] children = dir.list();
            for (int i=0; i<children.length; i++) 
            {    
                if((children[i].equals("ACModelApi.log"))
                    ||(children[i].equals("ACModelApi.log_Simplified.log"))
                    ||(children[i].equals("ACModelApi_NewLog.log"))
                    ||(children[i].equals("admission.log"))
                    ||(children[i].equals("AdmController.log"))
                    ||(children[i].equals("TRECAnalyzer.log"))
                    ||(children[i].equals("AC_TRECcommon_aaS.log"))
                    ||(children[i].equals("PhysicalHostsInfo_aaS.log")))        
                {
                    result +=children[i]+" ignored\n";
                    continue;
                }//if-ignore
                    
                String filename = AdmissionControlLogsFolder+File.separator+children[i];
                
                boolean success = deletefile(filename);
                if(!success)
                    return "ERROR "+filename+" didn't delete it";
                else
                    result +=children[i]+" deleted \n";
                
            }//for-i
        
       return result;
   }//clearLogs()
    
}//class
