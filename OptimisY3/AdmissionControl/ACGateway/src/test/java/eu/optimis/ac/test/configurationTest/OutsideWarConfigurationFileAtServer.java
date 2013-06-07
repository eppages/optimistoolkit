/*
 * Copyright (c) 2013 National Technical University of Athens (NTUA)
 *	
 *   							MIT License
 *   
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */


package eu.optimis.ac.test.configurationTest;

import eu.optimis.ac.ACRestClients.MyRestClients.RestClient_MultivaluedMap_String;
import eu.optimis.ac.ACRestClients.MyRestClients.RestClient_noInput_String;
import javax.ws.rs.core.MultivaluedMap;

public class OutsideWarConfigurationFileAtServer {
    
    public static void SetOutsideWarConfigurationFileAtServer(
            MultivaluedMap<String, String> formParams,String host,String port,Boolean removePreviousConfiguration)
    {
        if(removePreviousConfiguration)
            OutsideWarConfigurationFileAtServer.RemoveOutsideWarConfigurationFilesFromServer(null,
                host,port);
        
        RestClient_MultivaluedMap_String client = new RestClient_MultivaluedMap_String(
        		host,port,
                "/ACGateway/conf/setOutsideWarConfiguration",formParams,false);
        
        System.out.println("Status : "+client.status);
        System.out.println(client.returnedString);
      
    }//SetOutsideWarConfigurationFileAtServer()
    
    public static void RemoveOutsideWarConfigurationFilesFromServer(String filename,String host,String port)
    {
        String url = null;
        
        if (filename == null)// remove all files
            url = "removeOutsideWarConfigurationFiles";
        else if (filename.contains("AdmissionControl.properties"))
            url = "removeOutsideWarConfigurationAdmissionControlFile";
        else if (filename.contains("gams.properties"))
            url = "removeOutsideWarConfigurationGAMSfile";
        else if (filename.contains("TREC.properties"))
            url = "removeOutsideWarConfigurationTRECfile";
                    
        RestClient_noInput_String client = 
                new RestClient_noInput_String(
                		host,port,
                		"/ACGateway/conf/"+url);
        
        if(client.returnedString.contains("false"))
            throw new RuntimeException(client.returnedString);
        
        System.out.println(client.returnedString);
        
    }//RemoveOutsideWarConfigurationFileAtServer()
    
}//class
