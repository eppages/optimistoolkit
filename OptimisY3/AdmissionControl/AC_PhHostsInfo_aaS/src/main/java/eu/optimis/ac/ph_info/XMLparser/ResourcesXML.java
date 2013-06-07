/*
 * Copyright (c) 2013 National Technical University of Athens (NTUA)
 *	
 *   							MIT License
 *   
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */


package eu.optimis.ac.ph_info.XMLparser;

import org.apache.log4j.Logger;

public class ResourcesXML {
    
    public static Resources parseResourcesXML(String xml_String, Logger log)
    {
        log.info("xml_String : "+xml_String);
        
        String cpu_cores = xml_String.substring(xml_String.lastIndexOf("<cpu_cores>"), xml_String.lastIndexOf("</cpu_cores>")).replace("</cpu_cores>", "").replace("<cpu_cores>", "");
        
        String memoryInGigabytes = xml_String.substring(xml_String.lastIndexOf("<memory_in_gigabytes>"), xml_String.lastIndexOf("</memory_in_gigabytes>")).replace("</memory_in_gigabytes>", "").replace("<memory_in_gigabytes>", "");
        
        log.info("cpu_cores : "+cpu_cores);
        log.info("memoryInGigabytes : "+memoryInGigabytes);
        
        return new Resources(Integer.parseInt(cpu_cores),Integer.parseInt(memoryInGigabytes));
        
    }//parseResourcesXML()
    
    public static Resources parseResourcesXML(String xml_String)
    {
        
        String cpu_cores = xml_String.substring(xml_String.lastIndexOf("<cpu_cores>"), xml_String.lastIndexOf("</cpu_cores>")).replace("</cpu_cores>", "").replace("<cpu_cores>", "");
        
        String memoryInGigabytes = xml_String.substring(xml_String.lastIndexOf("<memory_in_gigabytes>"), xml_String.lastIndexOf("</memory_in_gigabytes>")).replace("</memory_in_gigabytes>", "").replace("<memory_in_gigabytes>", "");
        
        return new Resources(Integer.parseInt(cpu_cores),Integer.parseInt(memoryInGigabytes));
        
    }//parseResourcesXML()

    public ResourcesXML(String xml_String, Logger log) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
    
}//class
