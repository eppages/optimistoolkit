/*
 * Copyright (c) 2013 National Technical University of Athens (NTUA)
 *	
 *   							MIT License
 *   
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */



package eu.optimis.ac.gateway.utils;

import eu.optimis.ac.gateway.configuration.GetLoggingLevel;
import java.io.*;
import java.net.*;
import org.apache.log4j.Logger;

public class GetTomcatDir {
	
	public static String getTomcatDir(Logger log)
	{
		
		String parentpath = null;
		
		File dir = null;
		
		try {
			
		dir = new File(new URI(GetTomcatDir.class.getResource("GetTomcatDir.class").toString()));
		
		} catch (URISyntaxException e) {
			
			log.error("URISyntaxException exception "+e.getMessage());
			e.printStackTrace();
			
		}
		
		parentpath = dir.getAbsolutePath();
		
		parentpath = splitPath(parentpath);
		
                //if(parentpath.contains("\\"))
		//	parentpath = parentpath.replace("\\","/");
                
                //if(SystemProperties.isOSWindows())
                //parentpath = parentpath.replace("/", File.separator).concat(File.separator);
                
                if(GetLoggingLevel.getLoggingLevel(log)==0)
                    log.info("Tomcat Dir :"+parentpath);
		
	    return parentpath;
	    
	}//getTomcatDir()
	
	
	private static String splitPath(String str)
	{
		String temp[]=str.split("webapps");
		
		String result=temp[0];
			
		return result;
		
	}//splitPath(String str)
        
        
        public static String getTomcatDir()
	{
		
		String parentpath = null;
		
		File dir = null;
		
		try {
			
		dir = new File(new URI(GetTomcatDir.class.getResource("GetTomcatDir.class").toString()));
		
		} catch (URISyntaxException e) {
			
			//log.error("URISyntaxException exception "+e.getMessage());
			e.printStackTrace();
			
		}
		
		parentpath = dir.getAbsolutePath();
		
		parentpath = splitPath(parentpath);
		
                //if(parentpath.contains("\\"))
		//	parentpath = parentpath.replace("\\","/");
                
                //if(SystemProperties.isOSWindows())
                //parentpath = parentpath.replace("/", File.separator).concat(File.separator);
                
		//log.info("Tomcat Dir :"+parentpath);
		
	    return parentpath;
	    
	}//getTomcatDir()
}//class
