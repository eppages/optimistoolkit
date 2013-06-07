/*
 * Copyright (c) 2013 National Technical University of Athens (NTUA)
 *	
 *   							MIT License
 *   
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package eu.optimis.ac.ph_info_openNebula.test;

import eu.optimis.ac.ph_info_openNebula.HostInfo;
import eu.optimis.ac.ph_info_openNebula.OneController;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import junit.framework.TestCase;
import org.opennebula.client.OneException;
import org.opennebula.client.OneResponse;
import org.opennebula.client.host.HostPool;

public class OneControllerTest  extends TestCase{
	
	private OneController oneClient;
	
        public void testOneController()
        {
            System.out.println("testOneController Started");
            
            try {
                
                oneClient = setUpConection();
                GetHostPoolTest();
                GetHostInfoTest();
                        
            } catch (Exception ex) {
                System.err.println(ex.getMessage());
            }
            
            System.out.println("testOneController Finished");
        }//testOneController()
        
	
	public static OneController setUpConection() throws Exception {
		
                OneController oneClient;
                
                Properties props = new Properties();
                
		InputStream in = OneControllerTest.class.getClassLoader().getResourceAsStream("server.properties");
		try {
			props.load(in);
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		oneClient = new OneController(getAuthToken(props),
				getEndpoint(props));
                
                return oneClient;
	}//setUpConection()

	public void GetHostPoolTest() throws OneException {
		OneResponse res;
		HostPool hp = oneClient.getHostPool();
		if (hp!=null) {
			res = hp.info();
                        //System.out.println("GetHostPoolTest:"+res.getMessage());
			assertFalse(res.isError());
			assertTrue(res.getMessage().startsWith("<HOST_POOL>"));
		}
		//fail("Not yet implemented");
                
	}//GetHostPoolTest()
	
	public void GetHostInfoTest() throws OneException {
		OneResponse res;
		HostInfo hm = oneClient.getHostInfo(0);
		if (hm!=null) {
			res = hm.info();
                        //System.out.println("GetHostInfoTest:"+res.getMessage());
			assertFalse(res.isError());
			assertTrue(res.getMessage().startsWith("<HOST>"));
		}
		//fail("Not yet implemented");
	}//GetHostInfoTest()
	
	private static String getAuthToken(Properties props) {

           System.out.println("AuthToken:"+props.getProperty("authentication_token"));
	   return props.getProperty("authentication_token");
		
	}//getAuthToken()
	
	private static String getEndpoint(Properties props) {
            
            System.out.println("Endpoint:"+props.getProperty("xmlrpc_endpoint"));
            return props.getProperty("xmlrpc_endpoint");
                
	}//getEndpoint()

}//class
