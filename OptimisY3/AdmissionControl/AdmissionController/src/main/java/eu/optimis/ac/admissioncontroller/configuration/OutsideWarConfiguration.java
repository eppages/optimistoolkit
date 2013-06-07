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
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import org.apache.log4j.Logger;

public class OutsideWarConfiguration {

    private Properties props = new Properties();
    
    public String path = null;
    public String filename = null;
    public String filePath = null;
    
    public Boolean outsideWarConfigurationFileExists = false;
    
    public OutsideWarConfiguration(Logger log)
    {
        try {
            InputStream is = InputFunctions.getInputStream("AdmissionController.properties",log);
            props.load(is);
            
        } catch (IOException ex) {
            log.error(ex.getMessage());
        }
        
        path = props.getProperty("OutsideWarConfiguration.path");
        filename = props.getProperty("OutsideWarConfiguration.filename");
        filePath = path+filename;
        
        outsideWarConfigurationFileExists = FileFunctions.FileExists(path, filename);
    }//Constructor
     
}//class

