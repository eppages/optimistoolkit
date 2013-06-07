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

import java.util.Date;
import org.apache.log4j.Logger;
import org.opennebula.client.Client;
import org.opennebula.client.ClientConfigurationException;
import org.opennebula.client.OneException;
import org.opennebula.client.OneResponse;
import org.opennebula.client.Pool;
import org.opennebula.client.host.Host;
import org.opennebula.client.host.HostPool;


public class OneController {
    
    private static Logger logger = Logger.getLogger(OneController.class);
    
    private Client oneClient;
    
    private static long updateHostInterval = 30000;
    
    private Date lastUpdateHostPool;
    
    private HostPool hostPool;
    
    public OneController(String authenticationToken, String xmlrpcEndpoint) throws ClientConfigurationException 
    {
		oneClient = new Client(authenticationToken, xmlrpcEndpoint);

		if (oneClient != null) {
			hostPool = new HostPool(oneClient);
			
		} else {
			String message = "Error in object initialization " +
					"authentication token might not be valid";
			logger.error(message);
		}
    }//Constructor-1
    
    public OneController() throws ClientConfigurationException 
    {
		oneClient = new Client();

		if (oneClient != null) {
			
                    hostPool = new HostPool(oneClient);
			
		} else {
			String message = "Error in object initialization " +
					"authentication token might not be valid";
			logger.error(message);
		}
    }//Constructor-2
    
    public Client getOneClient() 
    {
		return oneClient;
                
    }//getOneClient()
    
    public HostPool getHostPool() throws OneException 
    {

		if ((lastUpdateHostPool != null) && 
				(System.currentTimeMillis() - lastUpdateHostPool.getTime() < updateHostInterval)) {
			return hostPool;
		}

		if (oneClient == null) {
			String message = "Error retrieving HostPool: " +
					"authentication token might not be valid";
			logger.warn(message);
			throw new OneException(message);
		}

		hostPool = new HostPool(oneClient);
		OneResponse res = hostPool.info();
		if (res.isError()) {
			logger.warn("Error retrieving hostPool: " + res.getErrorMessage());
			throw new OneException(res.getErrorMessage());
		} else {
			lastUpdateHostPool = new Date();
			logger.debug("Retrieved hostPool: " + res.getMessage());
		}

		return hostPool;
                
    }//getHostPool()
    
    public Host getHost(int id) throws OneException {

		if ((lastUpdateHostPool != null) &&
				(System.currentTimeMillis() - lastUpdateHostPool.getTime() < updateHostInterval)) {
			return hostPool.getById(id);
		}

		if (oneClient == null) {
			String message = "Error retrieving host with id " + id + ": " +
					"authentication token might not be valid";
			logger.warn(message);
			throw new OneException(message);
		}

		Host host = new Host(id, oneClient);
		OneResponse res = host.info();
		if (res.isError()) {
			logger.warn("Error retrieving host: " + res.getErrorMessage());
			throw new OneException(res.getErrorMessage());
		} else {
			logger.debug("Retrieved host: " + res.getMessage());
		}

		return host;
	}

	/**
	 * Retrieves HostInfo with given ID.
	 * @param id ID of the host
	 * @return HostInfo instance
	 * @throws OneException
	 */
	public HostInfo getHostInfo(int id) throws OneException {
		
		HostInfo hostInfo = null;
		
		hostInfo = new HostInfo(id, oneClient);
		OneResponse res = hostInfo.info();
		
		if (res.isError()) {
			throw new OneException("Error retrieving host info with ID=" + id);
		} 
		
		logger.info("Retrieved host info with ID: " + hostInfo.id());

		return hostInfo;
	}
    
}//class
