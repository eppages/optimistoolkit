/*
 * Copyright (c) 2013 National Technical University of Athens (NTUA)
 *	
 *   							MIT License
 *   
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package eu.optimis.ac.trecanalyzer.modelInputAsStrings;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringWriter;
import java.util.LinkedList;
import javax.ws.rs.core.MultivaluedMap;
import org.apache.log4j.Logger;

public class WriteString {
    
    public static String WriteSecondaryString(String GamsPath,int max,String ValueName,String Identifier,Logger log) 
    		throws IOException,FileNotFoundException
    {
    	log.info("Creation of "+ValueName+".String");	
    	
	StringWriter writer = new StringWriter();
        
	    for(int i=1;i<=max;i++)
		{
	    	writer.append(Identifier+i);
	    	if(i!=max)
	    		writer.append(',');
		}//for
	        
	    writer.flush();
	    writer.close();
	    
	    log.info(ValueName+".String was created!");
	    
            return writer.toString();
    }//WriteSecondaryString
    
    public static String WriteSimpleString(String GamsPath,LinkedList<String> list,String ValueName,String Identifier,Logger log) 
    		throws IOException,FileNotFoundException
    {
    	log.info("Creation of "+ValueName+".String");	
    	
    	StringWriter writer = new StringWriter();
	    
	    for(int i=0;i<list.size();i++)
		{
	    	writer.append(Identifier+(i+1));
		    writer.append(',');
		    writer.append(list.get(i));
		    writer.append('\n');	
  
		}//for
	        
	    writer.flush();
	    writer.close();
	    
	    log.info(ValueName+".String was created!");
	    
            return writer.toString();
    }//WriteSimpleString
    
    public static String WriteSimpleString(String GamsPath,MultivaluedMap<String, String> formParams,String ValueName,String filename,String path,String Identifier,Logger log)
    		throws IOException,FileNotFoundException
    {
    	log.info("Creation of "+filename+".String");	
    	
    	StringWriter writer = new StringWriter();
	    
	    for(int i=0;i<formParams.get(ValueName).size();i++)
		{
	    	writer.append(Identifier+(i+1));
		    writer.append(',');
		    writer.append(formParams.get(ValueName).get(i));
		    writer.append('\n');	
  
		}//for
	        
	    writer.flush();
	    writer.close();
	    
	    log.info(filename+".String was created!");
	    
            return writer.toString();
    }//WriteSimpleString
    
}//class
