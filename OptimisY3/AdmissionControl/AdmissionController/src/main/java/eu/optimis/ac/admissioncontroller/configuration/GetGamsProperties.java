/*
 * Copyright (c) 2013 National Technical University of Athens (NTUA)
 *	
 *   							MIT License
 *   
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package eu.optimis.ac.admissioncontroller.configuration;

import eu.optimis.ac.admissioncontroller.utils.FileFunctions;
import eu.optimis.ac.admissioncontroller.utils.InputFunctions;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import org.apache.log4j.Logger;

public class GetGamsProperties {
    
    private Properties props = new Properties();
    
    public String executable = null;
    public String modelFileName = null;
    public String outputMode = null;
    
    public GetGamsProperties(String gamsDirectory, Logger log)
    {
        insideWarConfiguration( log);
        
        modelFileName = props.getProperty("modelFileName");
        outputMode = props.getProperty("outputMode");
        
        OutsideWarConfiguration outsideConfiguration 
                = new OutsideWarConfiguration(log);
        
        if(outsideConfiguration.outsideWarConfigurationFileExists)
        {
            outsideWarConfiguration(outsideConfiguration.filePath, log);
            
            if(props.getProperty("executable").isEmpty())
                insideWarConfiguration(log);
            else
                log.info("Gams Properties Outside War Configuration");
        }//outside War configuraiton
        
        executable = props.getProperty("executable");
                
        copyModelToGamsDirectory(gamsDirectory, modelFileName, log);
    }//Constructor
    
    private void outsideWarConfiguration(String filePath , Logger log)
    {
    	try {
    		props.load(new FileInputStream(filePath));
 
    	} catch (IOException ex) {
    		log.error(ex.getMessage());
        }
 
    }//outsideWarConfiguration()
    
    private void insideWarConfiguration( Logger log)
    {
        try {
            InputStream is = InputFunctions.getInputStream("gams.properties",log);
            props.load(is);
            
        } catch (IOException ex) {
            log.error(ex.getMessage());
        }
    }//insideWarConfiguration()
    
    private static void copyModelToGamsDirectory(String gamsDirectory,String modelFileName, Logger log)
    {
        if(FileFunctions.FileExists(gamsDirectory, modelFileName))return;
        
        String content = FileFunctions.readFileAsStringFromResources("gams/"+modelFileName, log);        
                
        FileFunctions.FileWrite(gamsDirectory+modelFileName, content, log);        
        
    }//copyModelToGamsDirectory()
    
}//class
