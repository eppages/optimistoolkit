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

import eu.optimis.ac.gateway.acLogsToWeb.GetACFinalMessage;
import eu.optimis.ac.gateway.acLogsToWeb.GetAClogs;
import eu.optimis.ac.gateway.acLogsToWeb.GetACtrecConstraints;
import eu.optimis.ac.gateway.acLogsToWeb.GetACweights;
import eu.optimis.ac.gateway.configuration.GetFileNames;
import eu.optimis.ac.gateway.utils.FileFunctions;
import eu.optimis.ac.gateway.utils.PropertiesUtils;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import org.apache.log4j.Logger;

@Path("/info")
public class ACInfoApi {
    
    private static Logger log = ACModelApi.log;
	
    public ACInfoApi()
    {
		
    }//constructor
    
        @GET
        @Path("/AClogs")
        @Produces("text/html")
        public String AClogs()
        {
            return GetAClogs.getHTML(false,true);
        }//AClogs()
        
        @GET
        @Path("/ACLogs")
        @Produces("text/html")
        public String ACLogs()
        {
            return GetAClogs.getHTML(true,false);
        }//ACLogs()
	
	@GET
	@Path("/ACFinalMessage")
	@Produces("text/html")
	public String ACFinalMessage()
	{
		return GetACFinalMessage.getHTML();
		
	}//ACFinalMessage()
	        
        @GET
	@Path("/getACLogs")
	@Produces("text/plain")
	public String getACLogs()
	{
		return FileFunctions.
                        readFileLineByLineWithResetString(
                        GetFileNames.getACGatewayLogsFilename(), 
                        PropertiesUtils.getBoundle("StartMessage"), 
                        "\n");
	}//getACLogs()
	
        @GET
	@Path("/ACTrecWeights")
	@Produces("text/html")
	public String ACweights()
	{
		return GetACweights.getHTMLweights(log);
	}//ACweights()
	
        @GET
	@Path("/ACTrecConstraints")
	@Produces("text/html")
	public String ACconstraints()
	{
		return GetACtrecConstraints.getHTMLtrecConstraints(log);
	}//ACconstraints()
        
        @GET
        @Path("Links/{host}")
        @Produces("text/html")
        public String ACLinks(@PathParam("host") String host)
        {
           String result ="<html><body>";
           
           result+=writeURL("http://212.0.127.140:8080/ACGateway/info/ACFinalMessage");
           result+=writeURL("http://213.27.211.124:8080/ACGateway/info/ACFinalMessage");
           result+=writeURL("http://130.239.48.6:8080/ACGateway/info/ACFinalMessage");
           result+=writeURL("http://109.231.120.19:8080/ACGateway/info/ACFinalMessage");
           result+=writeURL("http://109.231.122.54:8080/ACGateway/info/ACFinalMessage");
           result+=writeURL("http://82.223.250.34:8080/ACGateway/info/ACFinalMessage");
           result+=writeURL("http://88.198.134.18:8080/ACGateway/info/ACFinalMessage");
           result+=writeURL("http://172.16.8.220:8080/ACGateway/info/ACFinalMessage");
           result+=writeURL("http://109.231.86.35:8080/ACGateway/info/ACFinalMessage");
           result+="<br>";
           result+=writeURL("http://212.0.127.140:8080/ACGateway/info/AClogs");
           result+=writeURL("http://213.27.211.124:8080/ACGateway/info/AClogs");
           result+=writeURL("http://130.239.48.6:8080/ACGateway/info/AClogs");
           result+=writeURL("http://109.231.120.19:8080/ACGateway/info/AClogs");
           result+=writeURL("http://109.231.122.54:8080/ACGateway/info/AClogs");
           result+=writeURL("http://82.223.250.34:8080/ACGateway/info/AClogs");
           result+=writeURL("http://88.198.134.18:8080/ACGateway/info/AClogs");
           result+=writeURL("http://172.16.8.220:8080/ACGateway/info/AClogs");
           result+=writeURL("http://109.231.86.35:8080/ACGateway/info/AClogs");
           result+="<br>";
           
           result+=writeURL("http://"+host+":8080/ACGateway/info/ACFinalMessage");
           result+=writeURL("http://"+host+":8080/ACGateway/info/AClogs");
           result+=writeURL("http://"+host+":8080/ACGateway/info/ACLogs");
           result+=writeURL("http://"+host+":8080/AdmissionController/info/getLogs");
           result+=writeURL("http://"+host+":8080/TRECAnalyzer/info/getLogs");
           result+=writeURL("http://"+host+":8080/ACGateway/info/getACLogs");
           
           result+="<br>";
           result+=writeURL("http://"+host+":8080/AC_PhHostsInfo_aaS/PhysicalHostsInfo/getXML/localhost/8080");
           result+="<br>";
           result+=writeURL("http://"+host+":8080/CloudOptimizer/physicalresources/ids");
           result+=writeURL("http://"+host+":8080/CloudOptimizer/physicalresources/_PhysicalHostName_");
           result+=writeURL("http://"+host+":8080/CloudOptimizer/virtualresources/vms/_PhysicalHostName_");
           result+=writeURL("http://"+host+":8080/CloudOptimizer/virtualresources/_ServiceId_");
           result+="<br>";
           result+=writeURL("http://"+host+":8080/ACGateway/info/ACTrecWeights");
           result+=writeURL("http://"+host+":8080/ACGateway/info/ACTrecConstraints");
           result+=writeURL("http://"+host+":8080/ACGateway/clear/ACLogsDeletion");
           result+=writeURL("http://"+host+":8080/ACGateway/clear/ACFinalMessage/clear");
           result+=writeURL("http://"+host+":8080/ACGateway/clear/ACBackupSMs/clear");
           result+=writeURL("http://"+host+":8080/ACGateway/clear/ACcleanupScript");
           result+="<br>";
           result+=writeURL("http://"+host+":8080/ACGateway/csv/getCSV/result.csv");
           result+=writeURL("http://"+host+":8080/ACGateway/csv/getCSV/table.csv");
           result+=writeURL("http://"+host+":8080/ACGateway/csv/getCSV/basic.csv");
           result+=writeURL("http://"+host+":8080/ACGateway/csv/getCSV/elastic.csv");
           result+=writeURL("http://"+host+":8080/ACGateway/csv/getCSV/maxCpus.csv");
           result+=writeURL("http://"+host+":8080/ACGateway/csv/getCSV/resCpus.csv");
           result+=writeURL("http://"+host+":8080/ACGateway/csv/getCSV/free_mem.csv");
           result+="<br>";
           result+="</body></html>";
           return result;
        }//ACLinks()
        
        private String writeURL(String url)
        {
            return "<a href="+url+" target=\"_blank\">"+url+"</a><br>";
        }//writeURL()
}//class
