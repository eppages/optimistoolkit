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
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import net.tomp2p.futures.FutureBootstrap; 
import net.tomp2p.p2p.Peer;
import net.tomp2p.peers.Number160;
import net.tomp2p.storage.Data;
/*
 * @author Ali Sajjad
 */
public class PuttingPeer {

	final private Peer peer;
	public PuttingPeer(int nodeId) throws Exception {
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
		
		PuttingPeer pp = new PuttingPeer(2);
		
		// First store this peer's nodeID and IP address in the DHT
		pp.store("2", pp.getLocalPeerIP());
		pp.store("zaim", "ali");
		pp.store("ruhma", "tahir");
		System.out.println("DONE");
		pp.shut();
		
		//pp.getIP();
	}
	
	private void store(String key, String val) throws IOException {
		peer.put(Number160.createHash(key), new Data(val)).awaitUninterruptibly();
	}
	private void shut() {
		peer.shutdown();
	}
	
	private String getLocalPeerIP() throws SocketException {
		
		String result = null;
	
		Enumeration<NetworkInterface> e = NetworkInterface.getNetworkInterfaces();

		while(e.hasMoreElements()) {
            NetworkInterface nic = (NetworkInterface) e.nextElement();
            System.out.println("Net interface: "+nic.getName());

            Enumeration<InetAddress> e2 = nic.getInetAddresses();

            while (e2.hasMoreElements()) {
               InetAddress ip = (InetAddress) e2.nextElement();
               if (!ip.isLinkLocalAddress() && !ip.isLoopbackAddress())
               {
            	   return ip.getHostAddress();
               }
            }
		}
		System.out.println(result);
		return null;
	}
}
