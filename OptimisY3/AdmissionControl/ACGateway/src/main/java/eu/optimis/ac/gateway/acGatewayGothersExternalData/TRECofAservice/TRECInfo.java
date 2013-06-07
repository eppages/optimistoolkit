/*
 * Copyright (c) 2013 National Technical University of Athens (NTUA)
 *	
 *   							MIT License
 *   
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */


package eu.optimis.ac.gateway.acGatewayGothersExternalData.TRECofAservice;

import com.sun.jersey.core.util.MultivaluedMapImpl;
import eu.optimis.ac.ACRestClients.MyRestClients.RestClient_MultivaluedMap_String;
import eu.optimis.ac.gateway.utils.PropertiesUtils;
import javax.ws.rs.core.MultivaluedMap;
import org.apache.log4j.Logger;

public class TRECInfo {
    
    public int status = -1;
    
    public String value = null;
    
    public TRECInfo(String host,String port,String serviceManifest,String url_String,Logger log)
    {
        String AC_TREC_host = PropertiesUtils.getBoundle("AC_TREC.ip");

        String AC_TREC_port = PropertiesUtils.getBoundle("AC_TREC.port");

        MultivaluedMap<String, String> formParams = new MultivaluedMapImpl();
        
        formParams.add("serviceManifest", serviceManifest);
        formParams.add("host", host);
        formParams.add("port", port);
        
        RestClient_MultivaluedMap_String client =
                new RestClient_MultivaluedMap_String(
                AC_TREC_host,AC_TREC_port,url_String,formParams,false,log);
        
        status = client.status;
        
        if(status == 200)
        {
            value = client.returnedString;
        }
        
        log.info("Status : "+status+" Value : "+value);
    }//constructor
}//class
