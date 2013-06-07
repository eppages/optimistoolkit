/*
 * Copyright (c) 2013 National Technical University of Athens (NTUA)
 *	
 *   							MIT License
 *   
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */


package eu.optimis.ac.test.remoteTest;

import com.sun.jersey.core.util.MultivaluedMapImpl;
import eu.optimis.ac.ACRestClients.ACRestClient;
import javax.ws.rs.core.MultivaluedMap;

public class DoRemoteTest{
    
	public int status;
	
	public MultivaluedMap<String, String> returnedSMs;
	
	public DoRemoteTest(MultivaluedMap<String, String> formParams,String whichSolver)
	{
            doRemoteTest(formParams, GetServerDetails.Host,whichSolver);
		
	}//Constructor-1
	
	public DoRemoteTest(MultivaluedMap<String, String> formParams, String host, String whichSolver)
	{       
            doRemoteTest(formParams, host,whichSolver);
		
	}//Constructor-2
        
        public DoRemoteTest(String serviceManifest, String whichSolver)
	{
            MultivaluedMap<String, String> formParams = new MultivaluedMapImpl();

            formParams.add("serviceManifest", serviceManifest);
            
            doRemoteTest(formParams, GetServerDetails.Host,whichSolver);
	}//Constructor-3
	
	public DoRemoteTest(String serviceManifest, String host, String whichSolver)
	{       
            MultivaluedMap<String, String> formParams = new MultivaluedMapImpl();

            formParams.add("serviceManifest", serviceManifest);
            
            doRemoteTest(formParams, host,whichSolver);
		
	}//Constructor-4
        
	private void doRemoteTest(MultivaluedMap<String, String> formParams, String ip,String whichSolver)
	{
            formParams.add("doNotBackupSMflag", "True");
            
            formParams.add(whichSolver, whichSolver);
            
            ACRestClient acClient = new ACRestClient( ip, GetServerDetails.Port, formParams); 
        
            if(acClient.returnedSMs.get("serviceManifest").size() != formParams.get("serviceManifest").size())
                    throw new RuntimeException(
                                    "Output SMs and Input SMs sizes are not equall ");
        
            status = acClient.status;
            returnedSMs = acClient.returnedSMs;
        
	}//doRemoteTest
	
}//class
