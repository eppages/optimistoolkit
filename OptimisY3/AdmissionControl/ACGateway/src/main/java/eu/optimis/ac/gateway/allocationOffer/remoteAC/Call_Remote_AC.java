/*
 * Copyright (c) 2013 National Technical University of Athens (NTUA)
 *	
 *   							MIT License
 *   
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */


package eu.optimis.ac.gateway.allocationOffer.remoteAC;

import com.sun.jersey.core.util.MultivaluedMapImpl;
import eu.optimis.ac.ACRestClients.ACRestClient;
import eu.optimis.ac.gateway.configuration.GetIP;
import eu.optimis.ac.gateway.configuration.GetPort;
import javax.ws.rs.core.MultivaluedMap;
import org.apache.log4j.Logger;

public class Call_Remote_AC
{
	public static String call_Remote_AC(String serviceManifest,Logger log)
	{
                String ReturnedServiceManifest="";
                
		String host = GetIP.getRemoteAdmissionControlIP(log);
                String port = GetPort.getRemoteAdmissionControlPort(log);
	    	
                log.info("Remote host ip is : "+host);
                log.info("Remote host port is : "+port);
                
		String url = "http://"+host+":"+port+"/ACGateway/model/performACTest";
		log.info("Remote AC url : "+url);
		
                MultivaluedMap<String, String> formParams = new MultivaluedMapImpl();

                formParams.add("serviceManifest", serviceManifest);
                
		ACRestClient Remote_AC_Client = new ACRestClient( host, port, formParams);
                
		log.info("Call Remote AC Status is: "+Remote_AC_Client.status);
		
                ReturnedServiceManifest = 
                
                Remote_AC_Client.returnedSMs.get("serviceManifest").get(0);
                
                return ReturnedServiceManifest;
                
	}//call_Remote_AC
	
}//class
