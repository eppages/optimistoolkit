/*
 * Copyright (c) 2013 National Technical University of Athens (NTUA)
 *	
 *   							MIT License
 *   
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */


package eu.optimis.ac.ph_info.clientsInfo;

import eu.optimis.ac.ACRestClients.MyRestClients.RestClient_noInput_String;
import org.apache.log4j.Logger;

public class InfrastructureProviderId {
    
    public static String getIP_Id(String host,String port,Logger log)
    {
        log.info("getIP_Id Started at host,port : "+host+","+port);
        
        RestClient_noInput_String client = new
                    RestClient_noInput_String(host, port, "/CloudOptimizer/ip/id");
            
                if(client.status!=200)
                {
                    String msg ="getIP_Id Communication with CO Rest Clien failed. Status was : "+client.status;
                    log.error(msg);
                    throw new RuntimeException(msg);
                }//status != 200
        
        log.info("getIP_Id Finished ");
        
        return client.returnedString;
        
    }//getIP_Id()
    
    public static String getIP_Id(String host,String port)
    {
        RestClient_noInput_String client = new
                    RestClient_noInput_String(host, port, "/CloudOptimizer/ip/id");
            
                if(client.status!=200)
                {
                    String msg ="getIP_Id Communication with CO Rest Clien failed. Status was : "+client.status;
                    System.err.println(msg);
                    throw new RuntimeException(msg);
                }//status != 200
        
        return client.returnedString;
        
    }//getIP_Id()
}//class
