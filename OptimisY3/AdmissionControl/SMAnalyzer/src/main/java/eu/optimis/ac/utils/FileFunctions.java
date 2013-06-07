/*
 * Copyright (c) 2013 National Technical University of Athens (NTUA)
 *	
 *   							MIT License
 *   
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package eu.optimis.ac.utils;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import org.apache.log4j.Logger;

public class FileFunctions {

	public static String readFileAsStringWithPath(String filePath,Logger log) {
		
		StringBuffer buf = new StringBuffer();		
		BufferedInputStream bin = null;
 
		try
		{ 
			
			bin = new BufferedInputStream(new FileInputStream(filePath));
		
			byte[] contents = new byte[1024];
 
			int bytesRead=0;
			String strFileContents;
 
			while( (bytesRead = bin.read(contents)) != -1){
 
				strFileContents = new String(contents, 0, bytesRead);
				//System.out.print(strFileContents);
				buf.append(strFileContents);
			} 
		}
		catch(FileNotFoundException fnfe)
		{
			//System.out.println("File not found" + fnfe);
			log.error("There was a FileNotFoundException: ", fnfe);
		}
		catch(IOException ioe)
		{
			//System.out.println("Exception while reading the file " + ioe);	
			log.error("There was an IOException: ", ioe);
		}
		finally
		{
			//close the BufferedInputStream using close method
			try {				
				if (bin != null)
					bin.close();
			} catch(IOException ioe) {
				//System.out.println("Error while closing the stream :" + ioe);
				log.error("There was an IOException: ", ioe);
			} 
		}
		
		return buf.toString();
		
	}//readFileAsStringWithPath()
	    
}//Class
