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
import eu.optimis.ac.gateway.utils.PropertiesUtils;
import org.apache.log4j.Logger;

public class GetSkipInfo {
    
    public static int getSkipPhysicalHostsInfoLevel(int SkipPhysicalHostInfoLevel, Logger log)
    {
        String skipPhysicalHostInfoLevel = null;
        
        if(OutsideWarConfiguration.isOutsideWarConfigurationFileExists())
            skipPhysicalHostInfoLevel = OutsideWarConfiguration.getSkipPhysicalHostInfoLevel(log);
        
        if (skipPhysicalHostInfoLevel == null)
            skipPhysicalHostInfoLevel = Integer.toString(SkipPhysicalHostInfoLevel);
	        
         return Integer.parseInt(skipPhysicalHostInfoLevel);
                
    }//getSkipPhysicalHostsInfoLevel()
    
    public static int getSkipCOSTLevel(int SkipCOSTLevel, Logger log)
    {
        String skipCOSTLevel = null;
        
        if(OutsideWarConfiguration.isOutsideWarConfigurationFileTRECExists())
            skipCOSTLevel = OutsideWarConfiguration.getSkipCOSTLevel(log);
        
        if (skipCOSTLevel == null)
            skipCOSTLevel = Integer.toString(SkipCOSTLevel);
	        
         return Integer.parseInt(skipCOSTLevel);
                
    }//getSkipCOSTLevel()
    
    public static int getSkipTRUSTLevel(int SkipTRUSTLevel, Logger log)
    {
        String skipTRUSTLevel = null;
        
        if(OutsideWarConfiguration.isOutsideWarConfigurationFileTRECExists())
            skipTRUSTLevel = OutsideWarConfiguration.getSkipTRUSTLevel(log);
        
        if (skipTRUSTLevel == null)
            skipTRUSTLevel = Integer.toString(SkipTRUSTLevel);
	        
         return Integer.parseInt(skipTRUSTLevel);
                
    }//getSkipTRUSTLevel()
    
    public static int getSkipTRECLevel(int SkipTRECLevel, Logger log)
    {
        String skipTRECLevel = null;
        
        if(OutsideWarConfiguration.isOutsideWarConfigurationFileTRECExists())
            skipTRECLevel = OutsideWarConfiguration.getSkipTRECLevel(log);
        
        if (skipTRECLevel == null)
            skipTRECLevel = Integer.toString(SkipTRECLevel);
	        
         return Integer.parseInt(skipTRECLevel);
                
    }//getSkipTRECLevel()
    
    public static int getSkipRISKLevel(int SkipRISKLevel, Logger log)
    {
        String skipRISKLevel = null;
        
        if(OutsideWarConfiguration.isOutsideWarConfigurationFileTRECExists())
            skipRISKLevel = OutsideWarConfiguration.getSkipRISKLevel(log);
        
        if (skipRISKLevel == null)
            skipRISKLevel = Integer.toString(SkipRISKLevel);
	        
         return Integer.parseInt(skipRISKLevel);
                
    }//getSkipRISKLevel()
    
    public static int getSkipECOLevel(int SkipECOLevel, Logger log)
    {
        String skipECOLevel = null;
        
        if(OutsideWarConfiguration.isOutsideWarConfigurationFileTRECExists())
            skipECOLevel = OutsideWarConfiguration.getSkipECOLevel(log);
        
        if (skipECOLevel == null)
            skipECOLevel = Integer.toString(SkipECOLevel);
	        
         return Integer.parseInt(skipECOLevel);
                
    }//getSkipECOLevel()
    
    public static int getSkipRISKhostLevel(int SkipRISKhostLevel, Logger log)
    {
        String skipRISKhostLevel = null;
        
        if(OutsideWarConfiguration.isOutsideWarConfigurationFileTRECExists())
            skipRISKhostLevel = OutsideWarConfiguration.getSkipRISKhostLevel(log);
        
        if (skipRISKhostLevel == null)
            skipRISKhostLevel = Integer.toString(SkipRISKhostLevel);
	        
         return Integer.parseInt(skipRISKhostLevel);
                
    }//getSkipRISKhostLevel()
    
    public static int getSkipECOhostLevel(int SkipECOhostLevel, Logger log)
    {
        String skipECOhostLevel = null;
        
        if(OutsideWarConfiguration.isOutsideWarConfigurationFileTRECExists())
            skipECOhostLevel = OutsideWarConfiguration.getSkipECOhostLevel(log);
        
        if (skipECOhostLevel == null)
            skipECOhostLevel = Integer.toString(SkipECOhostLevel);
	        
         return Integer.parseInt(skipECOhostLevel);
                
    }//getSkipECOhostLevel()
}//class
