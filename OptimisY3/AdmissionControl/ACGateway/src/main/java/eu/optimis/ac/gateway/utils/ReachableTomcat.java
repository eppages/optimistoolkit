/*
 * Copyright (c) 2013 National Technical University of Athens (NTUA)
 *	
 *   							MIT License
 *   
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */


package eu.optimis.ac.gateway.utils;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import org.apache.log4j.Logger;


public class ReachableTomcat {
    
    public static Boolean isReachable(String host,String port,Logger log)
    {
        String url="http://" + host + ":" + port+"/ACGateway/info/ACFinalMessage";
        
        try{
        
            Client client = Client.create();
                
            WebResource webResource = client.resource(url);
		   	                
            ClientResponse response = webResource.accept("text/html").get(ClientResponse.class);

            if(response.getStatus()==200)return true;
            
        } catch (Exception ex) {
            log.info(url+" unreachable "+ex.getMessage());
            return false;
        }
        
        return false;
    }//isReachable()
    
    
    public static Boolean isReachable(String host,String port)
    {
        String url="http://" + host + ":" + port+"/ACGateway/info/ACFinalMessage";
        
        try{
        
            Client client = Client.create();
                
            WebResource webResource = client.resource(url);
		   	                
            ClientResponse response = webResource.accept("text/html").get(ClientResponse.class);

            if(response.getStatus()==200)return true;
            
        } catch (Exception ex) {
            //System.out.println(ex.getMessage());
            return false;
        }
        
        return false;
    }//isReachable()
}//class
