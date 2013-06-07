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
package eu.optimis.ics.p2p;

import net.tomp2p.p2p.PeerListener;
import net.tomp2p.peers.PeerAddress;

public class MyPeerListener implements PeerListener {
	
	@Override
	public void notifyOnShutdown() {
		System.out.println("Peer Shutdown"); 
		
	}

	@Override
	public void notifyOnStart() {
		System.out.println("Peer Started"); 
		
	}

	@Override
	public void serverAddressChanged(PeerAddress peerAddress, boolean tcp) {
		// TODO Auto-generated method stub
		
	}

}
