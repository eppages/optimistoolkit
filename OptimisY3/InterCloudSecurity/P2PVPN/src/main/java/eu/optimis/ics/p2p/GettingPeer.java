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

import java.io.IOException;
import net.tomp2p.futures.FutureDHT;
import net.tomp2p.futures.FutureBootstrap; 
import net.tomp2p.p2p.Peer;
import net.tomp2p.peers.Number160;
/*
 * @author Ali Sajjad
 */
public class GettingPeer {
	
	final private Peer peer;
	public GettingPeer(int nodeId) throws Exception {
		this.peer = new Peer(Number160.createHash(nodeId));
		this.peer.listen(4000+nodeId, 4000+nodeId);
		/*
		PeerAddress superAddr = new PeerAddress(Number160.createHash(1), "109.231.77.242", 2001, 2001);
		FutureDiscover future = this.peer.discover(superAddr);
		future.awaitUninterruptibly();
		FutureBootstrap fb = this.peer.bootstrap(superAddr);
		 */
		FutureBootstrap fb = this.peer.bootstrapBroadcast(2001);
		fb.awaitUninterruptibly();
		peer.discover(fb.getBootstrapTo().iterator().next()).awaitUninterruptibly();
	}
	public static void main(String[] args) throws Exception {
		System.out.println("8888888");
		GettingPeer gp = new GettingPeer(2222);
		System.out.println("FFFFFFF");
		System.out.println("Name: "+args[0]+" IP:"+gp.get(args[0]));
	}
	private String get(String name) throws ClassNotFoundException, IOException {
		FutureDHT futureDHT = peer.get(Number160.createHash(name));
		futureDHT.awaitUninterruptibly();
		if(futureDHT.isSuccess()) {
			return futureDHT.getData().values().iterator().next().getObject().toString();
		}
		else return "Not Found";
	}
}
