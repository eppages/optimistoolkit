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

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import org.apache.log4j.Logger;

public class InputFunctions {
    
    public static InputStream getInputStream(String filename, Logger log) throws IOException 
    {
    	log.trace("Entering getInputStream");
    	
                ClassLoader cLoader = Thread.currentThread().getContextClassLoader();
		URL resURL = cLoader.getResource(filename);
		URLConnection resConn = resURL.openConnection();
		resConn.setUseCaches(false);
		InputStream is = resConn.getInputStream();
		
		log.trace("Exiting getInputStream");
		return is;
                
	}//getInputStream()
    
    public static InputStream getFileInputStream(String filename, Logger log) throws IOException
    {
        log.trace("Entering getInputStream");
                
                InputStream is = new FileInputStream(filename);
		
		log.trace("Exiting getInputStream");
		return is;
                
    }//getFileInputStream
    
}//class
