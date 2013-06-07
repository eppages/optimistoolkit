/*
 * Copyright (c) 2013 National Technical University of Athens (NTUA)
 *	
 *   							MIT License
 *   
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package eu.optimis.ac.gateway.configuration;

import eu.optimis.ac.gateway.utils.Paths;
import eu.optimis.ac.gateway.utils.PropertiesUtils;
import org.apache.log4j.Logger;

public class GetFileNames {
        
        public static String getAClogs_fileName()
	{
		return Paths.getStartPath()
		+ PropertiesUtils.getBoundle("AClogs.path");
		
	}//getAClogs_fileName()
    
	public static String getAClogs_fileName(Logger log)
	{
		return Paths.getStartPath(log)
		+ PropertiesUtils.getBoundle("AClogs.path");
		
	}//getAClogs_fileName(Logger log)
	
        public static String getACFinalMessageFilename()
	{
		return BackupSMsPath.getFinalMessagePath(Paths.getStartPath())
				+PropertiesUtils.getBoundle("filename.BackupFinalMessage");
		
	}//getACFinalMessageFilename(Logger log
        
	public static String getACFinalMessageFilename(Logger log)
	{
		return BackupSMsPath.getFinalMessagePath(Paths.getStartPath(log))
				+PropertiesUtils.getBoundle("filename.BackupFinalMessage");
		
	}//getACFinalMessageFilename(Logger log)
        
         public static String getACGatewayLogsFilename()
        {
            return PropertiesUtils.getBoundle("log4j","log4j.appender.rollingFile.File");
        }//getACGatewayLogsFilename()
        
        public static String getACGatewayLogsFilename(Logger log)
        {
            log.info("getACGatewayLogsFilename Started");
            
            String ACGatewayLogsFilename = getACGatewayLogsFilename();
            
            log.info("ACGatewayLogsFilename : "+ACGatewayLogsFilename);
            log.info("getACGatewayLogsFilename Finished");
            
            return ACGatewayLogsFilename;
        }//getACGatewayLogsFilename()
}//class
