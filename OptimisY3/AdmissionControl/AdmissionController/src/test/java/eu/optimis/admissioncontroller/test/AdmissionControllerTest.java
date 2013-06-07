/*
 * Copyright (c) 2013 National Technical University of Athens (NTUA)
 *	
 *   							MIT License
 *   
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */


package eu.optimis.admissioncontroller.test;

import java.io.FileInputStream;
import java.sql.Connection;
import java.util.List;

import javax.ws.rs.core.MultivaluedMap;
import com.sun.jersey.core.util.MultivaluedMapImpl;
import java.util.Properties;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import java.io.IOException;
import java.io.FileNotFoundException;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;


/**
 * Unit test for simple App.
 */
public class AdmissionControllerTest extends TestCase
{
	/**
	 * Create the test case
	 *
	 * @param testName name of the test case
	 */
	public AdmissionControllerTest( String testName )
	{
		super( testName );
	}
	
		
	public void testAdmissionControl() {		
			
		try {	
			Properties props = new Properties();
			props.load(AdmissionControllerTest.class.getClassLoader().getResourceAsStream("config.properties"));
			
			String host = props.getProperty("admission.controller.ip");
			String port = props.getProperty("admission.controller.port");
			String path = props.getProperty("path");
			String expectedResult = "<allocation_offers>";
			
			MultivaluedMap<String, String> pathParams = new MultivaluedMapImpl();
	        pathParams.add("opModel", path);
			/*
			ClientConfig config = new DefaultClientConfig();
			Client client = Client.create(config);
			
			WebResource service = client.resource("http://"+ host + ":" + port + "/AdmissionController/admission");
			ClientResponse response = service.type("application/x-www-form-urlencoded").accept("text/plain").post(ClientResponse.class, pathParams);	
					
			String textResponse = response.getEntity(String.class);
			System.out.println(textResponse);
			String result = textResponse.substring(0,19);	  	    
			
	  	    assertEquals(expectedResult, result);
		*/
		} catch (FileNotFoundException fnfe) {		
			fnfe.printStackTrace();			
		} catch (IOException ioe) {			
			ioe.printStackTrace();			
		}		
	}
}
