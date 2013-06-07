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

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import net.tomp2p.futures.FutureBootstrap; 
import net.tomp2p.futures.FutureDiscover;
import net.tomp2p.p2p.Peer;
import net.tomp2p.peers.Number160;
import net.tomp2p.peers.PeerAddress;
/*
 * @author Ali Sajjad
 */
public class PeerTest {
	
	final static private String superPeerIP = "217.33.61.85";  // 109.231.77.242     217.33.61.85
	final static private int superPeerPort = 8088;
	final private Peer peer;
	final private String localIP;
	final private int localPort;
	final private String serviceID;
	final private Number160 peerID;
    final private String SPDFile;
	
    public PeerTest(String ip, int port, String serviceID, String spdFile) throws Exception 
	{
		this.localIP = ip;
		this.localPort = port;
		this.serviceID = serviceID;
		this.SPDFile = spdFile;
		
		this.peerID = Number160.createHash(localIP);
		
		this.peer = new Peer(peerID);
		this.getPeer().listen(localPort, localPort);
		
		PeerAddress superAddr = new PeerAddress(Number160.createHash(1), superPeerIP, superPeerPort, superPeerPort);
		FutureDiscover future = this.getPeer().discover(superAddr);
		future.awaitUninterruptibly();
		
		FutureBootstrap fb = this.getPeer().bootstrap(superAddr);
		fb.awaitUninterruptibly();
		getPeer().discover(fb.getBootstrapTo().iterator().next()).awaitUninterruptibly();
	}
	
	public static void main(String[] args) throws Exception
	{
		final PeerTest client = new PeerTest(getLocalPeerIP(), 8090, "medical", "/etc/racoon/spd.sh");
		
		// Trying for clean shutdown so that the SuperPeer can detect peer going down
				Runtime.getRuntime().addShutdownHook(new Thread() {
				    public void run() 
				    { 
				       log("Peer shutting down .....");
				       log("Peer listening = "+client.peer.isListening());
				       log("Peer running = "+client.peer.isRunning());
				       boolean temp = client.peer.getPeerBean().getPeerMap().peerOffline(client.peer.getPeerAddress(), true);
				       log("Result of peerOffline = "+temp);
				       temp = client.peer.getPeerBean().getPeerMap().peerOffline(client.peer.getPeerAddress(), false);
				       log("Result of peerOffline = "+temp);
				       client.peer.shutdown();
				       log("Peer listening = "+client.peer.isListening());
				       log("Peer running = "+client.peer.isRunning());
				       log("Peer Shut");
				    }
				 });
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
	
	private static void log(String msg)
	{
	    System.out.println(msg);
	}
	
	public String getServiceID() {
		return serviceID;
	}

	public Peer getPeer() {
		return peer;
	}
	
	public String getlocalIP() {
		return localIP;
	}
	
	public String getSPDFile() {
		return SPDFile;
	}
}