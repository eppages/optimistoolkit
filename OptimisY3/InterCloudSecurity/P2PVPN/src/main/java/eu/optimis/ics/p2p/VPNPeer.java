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

import java.io.File;
import java.io.FileInputStream;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import net.tomp2p.futures.FutureBootstrap; 
import net.tomp2p.futures.FutureDiscover;
import net.tomp2p.p2p.Peer;
import net.tomp2p.peers.Number160;
import net.tomp2p.peers.PeerAddress;
/*
 * @author Ali Sajjad
 */
public class VPNPeer {
	
	final private String superPeerIP;  // 109.231.77.242   217.33.61.85		217.33.61.126
	final private int superPeerPort;
	final private PeerAddress superPeerAddress;
	final private Peer peer;
	final private String localIP;
	final public String externalIP;
	final private int localPort;
	final private String serviceID;
	final private Number160 peerID;
    private ScheduledExecutorService scheduler;
    final private int bootDelay;
    final private int updateDelay;
    final private int discoveryDelay;
    final private String peerConf = "/opt/optimis/vpn/P2PVPN/vpnpeer.conf";
	
    public VPNPeer(String ip, int port) throws Exception 
	{
    	Properties prop = new Properties();
    	
    	File confFile = new File(this.peerConf);
    	
    	if(confFile.isFile())
    		prop.load(new FileInputStream(confFile));
    	else
    		prop.load(new FileInputStream("vpnpeer.conf"));
    	
    	this.superPeerIP = prop.getProperty("superPeerIP");
    	this.superPeerPort = Integer.parseInt(prop.getProperty("superPeerPort"));
    	this.serviceID = prop.getProperty("serviceID");
    	this.bootDelay = Integer.parseInt(prop.getProperty("bootDelay"));
    	this.updateDelay = Integer.parseInt(prop.getProperty("updateDelay"));
    	this.discoveryDelay = Integer.parseInt(prop.getProperty("discoveryDelay"));
    	
		this.scheduler = Executors.newScheduledThreadPool(3);
		this.localIP = ip;
		this.localPort = port;
		
		this.externalIP = ExternalIP.getIP();
		prop.setProperty("externalip", this.externalIP);
		
		this.peerID = Number160.createHash(localIP);
		
		this.peer = new Peer(peerID);
		this.getPeer().listen(localPort, localPort);
		
		this.superPeerAddress = new PeerAddress(Number160.createHash(1), superPeerIP, superPeerPort, superPeerPort);
		
		FutureDiscover future = this.getPeer().discover(superPeerAddress);
		future.awaitUninterruptibly(10000);
		
		FutureBootstrap fb = this.getPeer().bootstrap(superPeerAddress);
		fb.awaitUninterruptibly();
		getPeer().discover(fb.getBootstrapTo().iterator().next()).awaitUninterruptibly();
	}
	
	public static void main(String[] args) throws Exception
	{
		PolicyEnforcer.removeSPDPolicy();	// 	Delete the existing SPD Policy from the local machine, 
		//	as there can be only one at a time

		// Create the racoon configuration file with the shared secret key
		RacoonConf rConf = new RacoonConf();
		rConf.genPSKConfFile("Ali Sajjad\n");
		rConf.genRSAConfFile();
		RacoonConf.reloadRacoon();

		final VPNPeer client = new VPNPeer(getLocalPeerIP(), 8090);
		
		// Trying for clean shutdown so that the SuperPeer can detect peer going down
		Runtime.getRuntime().addShutdownHook(new Thread() {
		    public void run() 
		    { 
		       log("Peer shutting down .....");
		       shutdownAndAwaitTermination(client.scheduler);
		       client.peer.shutdown();
		       log("Peer Shut");
		    }
		 });
	
		log("PEER STARTED");
		log("------------");
		log("\tLocal Peer Internal IP Address = "+client.getLocalIP());
		log("\tLocal Peer External IP Address = "+client.externalIP);
		log("\tOPTIMIS Service ID = "+client.getServiceID());
		log("\tLocal Peer ID = "+client.getPeer().getPeerID().toString());
		log("-----------------------------------------------------------------------");
		
		// Add this client's IP address and service ID to the DHT periodically
		updateLocalInfo(client, client.updateDelay);	
		
		// Starts the Scheduler thread to discover other peers with the same service ID
		activatePeriodicPeerDiscovery(client, client.discoveryDelay);
		
		startBootstrapThread(client, client.bootDelay);
    }
	
	/**
	 * Attempts to cleanly close the PeriodicPeerChecker thread
	 *
	 * @param  pool Description ScheduledExecutorService
	 * @return void
	*/
	private static void shutdownAndAwaitTermination(ScheduledExecutorService pool) 
	{
		try 
		{
			pool.shutdownNow();
			// Wait a while for existing tasks to terminate
		    if (!pool.awaitTermination(10, TimeUnit.SECONDS)) 
		    {
		    	pool.shutdownNow(); // Cancel currently executing tasks
		    	// Wait a while for tasks to respond to being cancelled
		    	if (!pool.awaitTermination(10, TimeUnit.SECONDS))
		    		System.err.println("Pool did not terminate");
		    }
		} 
		catch (InterruptedException ie) 
		{
			pool.shutdownNow();
			// Preserve interrupt status
		    Thread.currentThread().interrupt();
		}
	}

	private static void updateLocalInfo(VPNPeer client, int delay)
	{
		PeriodicInfoUpdater infoUpdater = new PeriodicInfoUpdater(client); 
		client.scheduler.scheduleWithFixedDelay(infoUpdater, 1, delay, TimeUnit.SECONDS);
	}
	
	private static void activatePeriodicPeerDiscovery(VPNPeer client, int delay)
	{
		PeriodicPeerChecker peerChecker = new PeriodicPeerChecker(client);	// Instance of class implementing the peer discovery thread
		client.scheduler.scheduleWithFixedDelay(peerChecker, delay, delay, TimeUnit.SECONDS); 
	}
	
	private static void startBootstrapThread(VPNPeer client, int delay)
	{
		PeriodicBootstrap boot = new PeriodicBootstrap(client); 
		client.scheduler.scheduleWithFixedDelay(boot, 1, delay, TimeUnit.SECONDS);
	}
	
	public static String getLocalPeerIP() throws SocketException {
		
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
	
	public PeerAddress getSuperPeerAddress() {
		return superPeerAddress;
	}
	
	public String getLocalIP() {
		return localIP;
	}
}
