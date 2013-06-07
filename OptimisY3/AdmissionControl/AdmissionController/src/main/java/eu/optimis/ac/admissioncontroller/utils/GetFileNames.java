/*
 * Copyright (c) 2013 National Technical University of Athens (NTUA)
 *	
 *   							MIT License
 *   
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package eu.optimis.ac.admissioncontroller.utils;

import org.apache.log4j.Logger;

public class GetFileNames {
        
        public static String getAdmissionControllerLogsFilename(Logger log)
        {
            log.info("getAdmissionControllerLogsFilename Started");
            
            String log4jXMLContents = FileFunctions.readFileAsStringFromResources("log4j.xml", log);
            
            String str = log4jXMLContents;
            String searchForString = "<param name=\"file\" value=";
            int intIndex = Strings.getIntIntexOfSubString(str, searchForString, log);
            String AdmissionControllerLogsFilename = str.substring(intIndex);
            
            str = AdmissionControllerLogsFilename;
            searchForString = "value=";
            intIndex = Strings.getIntIntexOfSubString(str, searchForString, log);
            AdmissionControllerLogsFilename = str.substring(intIndex);
            
            str = AdmissionControllerLogsFilename;
            searchForString = "\"";
            int beginIntIndex = Strings.getIntIntexOfSubString(str, searchForString, log);
            int endIntIndex = Strings.getIntIntexOfSubString(str.substring(beginIntIndex+1), searchForString, log);
            AdmissionControllerLogsFilename = str.substring(beginIntIndex+1, endIntIndex+beginIntIndex+1);
            
            log.info("AdmissionControllerLogsFilename : "+AdmissionControllerLogsFilename);
            log.info("getAdmissionControllerLogsFilename Finished");
            
            return AdmissionControllerLogsFilename;
            
        }//getAdmissionControllerLogsFilename()
}//class
