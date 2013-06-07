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
import java.util.Vector;

import net.tomp2p.futures.FutureBootstrap; 
import net.tomp2p.futures.FutureDHT;
import net.tomp2p.futures.FutureDiscover;
import net.tomp2p.p2p.Peer;
import net.tomp2p.peers.Number160;
import net.tomp2p.peers.PeerAddress;
import net.tomp2p.storage.Data;
/*
 * @author Ali Sajjad
 */
public class P2PClient {

	final private Peer peer;
	final private String localIP;
	final private String serviceID;
	final private Number160 peerID;
	public P2PClient () throws Exception {
		
		this.localIP = getLocalPeerIP();
		this.serviceID = "medical";
		this.peerID = Number160.createHash(localIP);
		this.peer = new Peer(peerID);
		this.peer.listen(4000, 4000);
		PeerAddress superAddr = new PeerAddress(Number160.createHash(1), "109.231.77.242", 8888, 8888);
		FutureDiscover future = this.peer.discover(superAddr);
		future.awaitUninterruptibly();
		FutureBootstrap fb = this.peer.bootstrap(superAddr);
		fb.awaitUninterruptibly();
		peer.discover(fb.getBootstrapTo().iterator().next()).awaitUninterruptibly();
	}
	public static void main(String[] args) throws Exception {
		
		P2PClient client = new P2PClient();
		System.out.println(client.localIP+"   "+client.serviceID+"  "+client.peer.getPeerID().toString());
		
		System.out.println("Getting peerIDs for the serviceID \"medical\"");
		Vector <Number160> vec = client.getPeerList(client.serviceID);
		if(vec == null) {
			vec = new Vector<Number160>();
			System.out.println("vec was null, now is empty: "+vec.isEmpty());
			vec.add(client.peerID);
			client.store(client.serviceID, vec);
		}
		else {
			for (Number160 number160 : vec) {
				System.out.println("PeerID = "+number160.toString());
				for (String string : client.get(number160)) {
					System.out.print("\t"+string);
				}
				System.out.println("");
			}
			if(!vec.contains(client.peerID)) {
				vec.add(client.peerID);
				client.store(client.serviceID, vec);
			}
		}
		///////////////////////////////////////////////////
		String[] localVals = { client.localIP, "IPSecrets"};
		client.store(client.peerID, localVals);
		
		for (String string : client.get(client.peerID)) {
			System.out.println(string);
		}
		
		///////////////////////////////////////////////////
		if(args.length > 1) client.store(args[0], args[1]);
		if(args.length == 1) System.out.println("Name: "+args[0]+" IP:"+client.get(args[0]));
	}
	private void store(String key, String val) throws IOException {
		peer.put(Number160.createHash(key), new Data(val)).awaitUninterruptibly();
	}
	private void store(String key, Vector<Number160> val) throws IOException {
		peer.put(Number160.createHash(key), new Data(val)).awaitUninterruptibly();
	}
	
	private void store(Number160 key, String[] vals) throws IOException {
		peer.put(key, new Data(vals)).awaitUninterruptibly();
	}
	
	private String[] get(Number160 name) throws ClassNotFoundException, IOException {
		FutureDHT futureDHT = peer.get(name);
		futureDHT.awaitUninterruptibly();
		if(futureDHT.isSuccess()) {
			return (String[]) futureDHT.getData().values().iterator().next().getObject();
		}
		else return null;
	}
	
	private String get(String name) throws ClassNotFoundException, IOException {
		FutureDHT futureDHT = peer.get(Number160.createHash(name));
		futureDHT.awaitUninterruptibly();
		if(futureDHT.isSuccess()) {
			return futureDHT.getData().values().iterator().next().getObject().toString();
		}
		else return "Not Found";
	}
	
	@SuppressWarnings("unchecked")
	private Vector <Number160> getPeerList(String name) throws ClassNotFoundException, IOException {
		FutureDHT futureDHT = peer.get(Number160.createHash(name));
		futureDHT.awaitUninterruptibly();
		if(futureDHT.isSuccess()) {
			return (Vector<Number160>) futureDHT.getData().values().iterator().next().getObject();
		}
		else return null;
	}
	
	private static String getLocalPeerIP() throws SocketException {
		
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
