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

import eu.optimis.ac.gateway.acCsvInfo.GetCsvFileContents;
import eu.optimis.ac.gateway.acCsvInfo.GetTREC_constraints;
import eu.optimis.ac.gateway.acCsvInfo.GetTREC_weights;
import eu.optimis.ac.gateway.configuration.AllocationInfoPath;
import eu.optimis.ac.gateway.utils.FileFunctions;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import org.apache.log4j.Logger;

@Path("/csv")
public class ACcsvApi {
    
    private static Logger log = ACModelApi.log;
	
    public ACcsvApi()
    {
		
    }//constructor
    
        @GET
        @Path("/getHostsInfoFile")
        @Produces("text/plain")
        public String getHostsInfoFile()
        {
            return FileFunctions.readFileAsStringWithPath(
                    AllocationInfoPath.getAllocationPath(log)+"hostsInfo.csv"
                    ,log);
            
        }//getHostsInfoFile()
    
        @GET
        @Path("/getCSV/{csv_filename}")
        @Produces("text/plain")
        public String getCSV(@PathParam("csv_filename") String csv_filename)
        {
            return GetCsvFileContents.getContents(csv_filename, log);
            
        }//getCSV()
        
        @GET
	@Path("/getTrust_Weight")
	@Produces("text/plain")
	public String getTrust_Weight()
	{
		return GetTREC_weights.getTrust_Weight(log);
		
	}//getTrust_Weight()
	
        @GET
	@Path("/getTrust_Constraint")
	@Produces("text/plain")
	public String getTrust_Constraint()
	{
		return GetTREC_constraints.getTrust_Constraint(log);
		
	}//getTrust_Constraint()
        
        @GET
	@Path("/getRisk_Weight")
	@Produces("text/plain")
	public String getRisk_Weight()
	{
		return GetTREC_weights.getRisk_Weight(log);

	}//getRisk_Weight()
	
	@GET
	@Path("/getRisk_Constraint")
	@Produces("text/plain")
	public String getRisk_Constraint()
	{
		return GetTREC_constraints.getRisk_Constraint(log);
		
	}//getRisk_Constraint()
        
        @GET
	@Path("/getEco_Weight")
	@Produces("text/plain")
	public String getEco_Weight()
	{
		return GetTREC_weights.getEco_Weight(log);
		
	}//getEco_Weight()
	
	@GET
	@Path("/getEco_Constraint")
	@Produces("text/plain")
	public String getEco_Constraint()
	{
		return GetTREC_constraints.getEco_Constraint(log);
		
	}//getEco_Constraint()
	
	@GET
	@Path("/getCost_Weight")
	@Produces("text/plain")
	public String getCost_Weight()
	{
		return GetTREC_weights.getCost_Weight(log);
		
	}//getCost_Weight()
	
	@GET
	@Path("/getCost_Constraint")
	@Produces("text/plain")
	public String getCost_Constraint()
	{
		return GetTREC_constraints.getCost_Constraint(log);
		
	}//getCost_Constraint()
        
}//class
