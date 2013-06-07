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

import java.util.Date;

import net.tomp2p.peers.PeerAddress;
import net.tomp2p.peers.PeerMapChangeListener;

public class MyPeerMapChangeListener implements PeerMapChangeListener {

	@Override
	public void peerInserted(PeerAddress peerAddress) {
		System.out.println("Peer Inserted at "+new Date(System.currentTimeMillis()).toString());
		System.out.println(peerAddress.getInetAddress()); 
		System.out.println("--------------------------"); 
	}

	@Override
	public void peerRemoved(PeerAddress peerAddress) {
		System.out.println("Peer Removed "+new Date(System.currentTimeMillis()).toString());
		System.out.println(peerAddress.getInetAddress()); 
		System.out.println("--------------------------"); 
		
	}

	@Override
	public void peerUpdated(PeerAddress peerAddress) {
		System.out.println("Peer Updated "+new Date(System.currentTimeMillis()).toString());
		
	}

}
