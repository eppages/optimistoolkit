/*
 * Copyright (c) 2013 National Technical University of Athens (NTUA)
 *	
 *   							MIT License
 *   
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */


package eu.optimis.ac.ph_info_openNebula;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import org.apache.log4j.Logger;
import org.opennebula.client.ClientConfigurationException;

public class SetUpConnection {
    
    public OneController oneClient;
    
    public SetUpConnection(Logger log)
    {
                Properties props = new Properties();
                
		InputStream in = OneController.class.getClassLoader().getResourceAsStream("server.properties");
		try {
			props.load(in);
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
                
                try {
                    oneClient = new OneController(getAuthToken(props,log),
                            getEndpoint(props,log));
                } catch (ClientConfigurationException ex) {
                    log.error(ex.getMessage());
                }
                
    }//Constructor
    
        private static String getAuthToken(Properties props, Logger log) {

           log.info("AuthToken:"+props.getProperty("authentication_token"));
	   return props.getProperty("authentication_token");
		
	}//getAuthToken()
	
	private static String getEndpoint(Properties props, Logger log) {
            
            log.info("Endpoint:"+props.getProperty("xmlrpc_endpoint"));
            return props.getProperty("xmlrpc_endpoint");
                
	}//getEndpoint()
}//class
