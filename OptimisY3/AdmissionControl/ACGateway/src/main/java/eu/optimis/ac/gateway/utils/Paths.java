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

import java.io.File;
import java.io.IOException;
import org.apache.log4j.Logger;

public class Paths {

	public static String getRelativePathUsePropertyResourceFile(String propertyValue,Logger log)
	{
		String path = getStartPath(log) + PropertiesUtils.getBoundle(propertyValue);
		
		log.info("path : "+path);
		
		return path;

	}//setPath
	
	public static String getStartPath(Logger log)
    {
    	String StartPath = null;

    	StartPath = GetTomcatDir.getTomcatDir(log);
       	
       	return StartPath;
   	
    }//getStartPath()
        
        public static String getStartPath()
    {
    	String StartPath = null;

    	StartPath = GetTomcatDir.getTomcatDir();
       	
       	return StartPath;
   	
    }//getStartPath()
        
        
        public static String getTheCurrentPath()
                throws IOException
        {
            String currentPath = null;
            
                try {
                    String current = new java.io.File( "." ).getCanonicalPath();          
                    
                    //System.out.println("Current dir:"+current);   
                    
                    String currentDir = System.getProperty("user.dir");          
                    
                    //System.out.println("Current dir using System:" +currentDir); 
                    
                    currentPath = current;
                    
                } catch (IOException ex) {
                    throw new IOException(ex.getMessage());
                }
                
                return currentPath;
            
        }//getCurrentPath()
        
        public static String getCurrentPath()
        {
            String currentPath = null;
            
                try {
                    currentPath = getTheCurrentPath();
                    
                } catch (IOException ex) {
                    return (ex.getMessage());
                }
                
                return currentPath;
                
        }//getCurrentPath()
        
        public static void CreateDirectory(String directoryName)
        {
            (new File(directoryName)).mkdirs();

        }//CreateDirectory()
        
}//class
