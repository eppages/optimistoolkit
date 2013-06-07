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

import eu.optimis.ac.gateway.configuration.outsideWarConfiguration.OutsideWarConfiguration;
import eu.optimis.ac.gateway.utils.Paths;
import eu.optimis.ac.gateway.utils.PropertiesUtils;
import java.io.File;
import org.apache.log4j.Logger;

public class GamsPath {
    
    public static String getGamsPath(String startPath, Logger log)
    {
        String gamsPath = null;
        
        if(OutsideWarConfiguration.isOutsideWarConfigurationFileExists())
            gamsPath = OutsideWarConfiguration.getGamsPath(log);
        
        if (gamsPath == null)
            gamsPath = startPath+getRelativeGamsPath();
		
        Paths.CreateDirectory( gamsPath);
                
         return gamsPath;
                
    }//getGamsPath(String startPath, Logger log)
    
    public static String getGamsPath(Logger log)
    {
        return getGamsPath(Paths.getStartPath(log), log);
        
    }//getGamsPath(Logger log)
        
    private static String getRelativeGamsPath()
    {
        String relativiGamsPath = PropertiesUtils.getBoundle("gams.path")
                                  +PropertiesUtils.getBoundle("path.gams");
                
        return relativiGamsPath+File.separator;
    }//getRelativeGamsPath()
        
}//class
