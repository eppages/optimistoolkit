/*
 * Copyright (c) 2013 National Technical University of Athens (NTUA)
 *	
 *   							MIT License
 *   
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */



package eu.optimis.ac.gateway.backupSM_FinalMessage;

import eu.optimis.ac.gateway.serviceManifestFunctions.WriteSMToFile;
import eu.optimis.ac.gateway.utils.Date_and_Time;
import eu.optimis.ac.gateway.utils.Paths;
import eu.optimis.ac.smanalyzer.SMAnalyzer;
import java.io.File;
import org.apache.log4j.Logger;

public class BackupSMToFile {
	
	public BackupSMToFile(String serviceManifest,
			String FilePointer,String MaxFilePointer,
			String extraName,String filePath,Logger log)
	{
		
		String filename = Date_and_Time.getDate()
				+"_"+Date_and_Time.getCurrentTime()
				+"_"+extraName;
		
                SMAnalyzer smAnalyzer = new SMAnalyzer(serviceManifest);
                
                String extraPath = smAnalyzer.serviceId;
                
                if(extraPath.contains("-"))
                    extraPath = extraPath.substring(0, extraPath.indexOf("-"));
                
                Paths.CreateDirectory(filePath+extraPath);
                
		if(extraName.contains("Input"))
		{
			WriteSMToFile.writeToFile(serviceManifest,filename+"_"+FilePointer+"-"+MaxFilePointer,filePath+extraPath+File.separator);
			WriteSMToFile.writeToFile(serviceManifest,"last_Input"+"_"+FilePointer+"-"+MaxFilePointer,filePath);
		}
		else
		{	
			WriteSMToFile.writeToFile(serviceManifest,filename+"_"+FilePointer+"-"+MaxFilePointer,filePath+extraPath+File.separator);
			WriteSMToFile.writeToFile(serviceManifest,"last"+"_"+FilePointer+"-"+MaxFilePointer,filePath);
		}
		
	}//constructor
	
}//class
