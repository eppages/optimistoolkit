/*
 * Copyright (c) 2013 National Technical University of Athens (NTUA)
 *	
 *   							MIT License
 *   
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package eu.optimis.ac.ACRestClients;

import eu.optimis.ac.ACRestClients.MyRestClients.RestClient_MultivaluedMap_MultivaluedMap;
import javax.ws.rs.core.MultivaluedMap;
import org.apache.log4j.Logger;

public class ACRestClient {
    
    public int status = 0;
    
    public MultivaluedMap<String, String> returnedSMs;
    
    private static String setACString = "/ACGateway/model/performACTest";
    
    public ACRestClient(String host,String port,MultivaluedMap<String, String> formParams)
    {
        RestClient_MultivaluedMap_MultivaluedMap acClient = new RestClient_MultivaluedMap_MultivaluedMap(host,port,setACString,formParams);
        
        status = acClient.status;
        
        returnedSMs = acClient.returnedMap;
        
    }//ACRestClient()
    
    public ACRestClient(String host,String port,MultivaluedMap<String, String> formParams,Logger log)
    {
        
        RestClient_MultivaluedMap_MultivaluedMap acClient = new RestClient_MultivaluedMap_MultivaluedMap(host,port,setACString,formParams,log);
        
        status = acClient.status;
        
        returnedSMs = acClient.returnedMap;
        
    }//ACRestClient()

}//class
