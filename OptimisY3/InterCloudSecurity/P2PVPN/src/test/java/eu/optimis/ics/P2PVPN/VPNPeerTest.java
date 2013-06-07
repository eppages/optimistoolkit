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

import java.io.File;
import java.io.IOException;
import java.net.SocketException;

import org.apache.commons.validator.routines.InetAddressValidator;

import eu.optimis.ics.p2p.PolicyEnforcer;
import eu.optimis.ics.p2p.RacoonConf;
import eu.optimis.ics.p2p.VPNPeer;

import junit.framework.TestCase;

public class VPNPeerTest extends TestCase {

	public void testGetLocalIP() throws SocketException 
	{
		String ip = VPNPeer.getLocalPeerIP();
		InetAddressValidator addressValid = new InetAddressValidator();
		assertTrue(ip, addressValid.isValid(ip));
	}
	
	public void testConfFile() 
	{
		File file = new File("vpnpeer.conf");
		assertTrue("Conf file doesn't exist", file.isFile());	
	}
	
	public void testIPSecSetkeyPreConditions() throws IOException, InterruptedException
	{
		int a = PolicyEnforcer.removeSPDPolicy();	
		assertEquals("Policy not removed by setkey", 0, a);
	}
	
	public void testIPSecRacoonPreConditions() throws IOException, InterruptedException
	{
		int a = RacoonConf.reloadRacoon();
		assertEquals("Racoon deamon not reloaded", 0, a);
	}
	
	public void testVPNPeer() throws Exception
	{
		VPNPeer testerPeer = new VPNPeer("127.0.0.1", 8090);
		assertNotNull("VPNPeer not initialized", testerPeer);
	}

}
