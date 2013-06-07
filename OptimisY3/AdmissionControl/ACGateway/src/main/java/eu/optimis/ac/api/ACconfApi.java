/*
 * Copyright (c) 2013 National Technical University of Athens (NTUA)
 *	
 *   							MIT License
 *   
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */


package eu.optimis.ac.api;

import eu.optimis.ac.gateway.configuration.outsideWarConfiguration.CreateOutsideWarConfiguration;
import eu.optimis.ac.gateway.configuration.outsideWarConfiguration.OutsideWarConfiguration;
import eu.optimis.ac.gateway.configuration.outsideWarConfiguration.PrintOutsideWarConfiguration;
import eu.optimis.ac.gateway.configuration.outsideWarConfiguration.RemoveOutsideWarConfiguration;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import org.apache.log4j.Logger;

@Path("/conf")
public class ACconfApi {
    
    private static Logger log = ACModelApi.log;
    
    public  ACconfApi()
    {
        
    }//constructor
    
        @POST
        @Path("/setOutsideWarConfiguration")
        @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public String setOutsideWarConfiguration(MultivaluedMap<String, String> Params)
	{		
		return  CreateOutsideWarConfiguration.CreateFile(
                        Params, 
                        log)
                        + "\n"+
                        CreateOutsideWarConfiguration.CreateGamsFile(
                        Params , log)
                        + "\n"+
                        CreateOutsideWarConfiguration.CreateTRECFile(
                        Params, log)
                        ;
                
	}//setOutsideWarConfiguration()
        
        @GET
        @Path("/getOutsideWarConfiguration")
        @Produces("text/plain")
        public String getOutsideWarConfiguration()
        {
            return PrintOutsideWarConfiguration.AsString(log);
            
        }//getOutsideWarConfiguration()
        
        @GET
        @Path("/removeOutsideWarConfigurationFiles")
        @Produces("text/plain")
        public String removeOutsideWarConfigurationFiles()
        {
            return RemoveOutsideWarConfiguration.removeOutsideConfigurationFiles(log);
            
        }//removeOutsideWarConfigurationFiles()
        
        @GET
        @Path("/removeOutsideWarConfigurationAdmissionControlFile")
        @Produces("text/plain")
        public String removeOutsideWarConfigurationAdmissionControlFile()
        {
            return RemoveOutsideWarConfiguration.removeFile(OutsideWarConfiguration.path, OutsideWarConfiguration.filename, log);
            
        }//removeOutsideWarConfigurationAdmissionControlFile()
        
        @GET
        @Path("/removeOutsideWarConfigurationGAMSfile")
        @Produces("text/plain")
        public String removeOutsideWarConfigurationGAMSfile()
        {
            return RemoveOutsideWarConfiguration.removeFile(OutsideWarConfiguration.path, OutsideWarConfiguration.fileGAMS, log);
            
        }//removeOutsideWarConfigurationGAMSfile()
        
        @GET
        @Path("/removeOutsideWarConfigurationTRECfile")
        @Produces("text/plain")
        public String removeOutsideWarConfigurationTRECfile()
        {
            return RemoveOutsideWarConfiguration.removeFile(OutsideWarConfiguration.path, OutsideWarConfiguration.fileTREC, log);
            
        }//removeOutsideWarConfigurationTRECfile()
        
}//class