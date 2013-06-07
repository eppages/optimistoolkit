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

import eu.optimis.ac.ACRestClients.MyRestClients.RestClient_MultivaluedMap_String;
import javax.ws.rs.core.MultivaluedMap;
import org.apache.log4j.Logger;

public class ACinternalClient {
    
    public int status = 0;
    
    public String returnedString = null;
    
    public ACinternalClient(String host,String port,String url_String,MultivaluedMap<String, String> formParams,Logger log)
    {
            RestClient_MultivaluedMap_String acInternalClient = new RestClient_MultivaluedMap_String(host,port,url_String,formParams,true,log);
            
            status = acInternalClient.status;
            
            returnedString = acInternalClient.returnedString;
            
    }//Constructor
        
}//class
