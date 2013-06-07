/*
 * Copyright (c) 2010-2013 British Telecom and City University London
 *
 * This file is part of P2PVPN component of the WP 5.4
 * (Inter-Cloud Security) of the EU OPTIMIS project.
 *
 * P2PVPN can be used under the terms of the SHARED SOURCE LICENSE
 * FOR NONCOMMERCIAL USE. 
 *
 * You should have received a copy of the SHARED SOURCE LICENSE FOR
 * NONCOMMERCIAL USE in the project's root directory. If not, please contact the
 * author at ali.sajjad@bt.com
 *
 * Author: Ali Sajjad
 *
 */
package eu.optimis.ics.P2PVPN;

import java.io.IOException;
import java.net.DatagramSocket;

import eu.optimis.ics.p2p.SuperPeer;
import junit.framework.TestCase;

public class SuperPeerTest extends TestCase {
	
	int port = 8088;

	public void testSuperPeerMain() 
	{
		
		SuperPeer sp = null;
		try 
		{
			sp = new SuperPeer(1, port);
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
		
		assertNotNull("Super Peer not initialized", sp);
	}
	
	public void testPortStatus()
	{
		DatagramSocket ds = null;
	    try 
	    {
	        ds = new DatagramSocket(port);
	        ds.setReuseAddress(true);
	        
	    } 
	    catch (IOException e) {
	    }
	    
	    assertNull("Port 8088 is NOT in use", ds);
	    
	    if (ds != null)	ds.close();
	}

}
