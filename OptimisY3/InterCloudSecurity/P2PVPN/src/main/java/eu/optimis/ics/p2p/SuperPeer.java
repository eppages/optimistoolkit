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

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import net.tomp2p.futures.FutureBootstrap; 
import net.tomp2p.futures.FutureDHT;
import net.tomp2p.p2p.Peer;
import net.tomp2p.peers.Number160;
import net.tomp2p.storage.Data;
/*
 * @author Ali Sajjad
 */
public class SuperPeer {
	
	final private Peer peer;
	
	public SuperPeer(int nodeId, int localPort) throws Exception 
	{
		/*
		P2PConfiguration peerConfig = new P2PConfiguration();
		peerConfig.setBagSize(Integer.MAX_VALUE);
		
		ConnectionConfigurationBean connBean = new ConnectionConfigurationBean();       // Thomas suggestion ... see mailing archive 05/12
		connBean.setDefaultPort(8088);
		
		KeyPair kp = KeyPairGenerator.getInstance("RSA").generateKeyPair();
		*/
		this.peer = new Peer(Number160.createHash(nodeId));	
		this.peer.listen(localPort, localPort);
		
		
		FutureBootstrap fb = this.peer.bootstrapBroadcast(localPort);
		fb.awaitUninterruptibly();
		//peer.discover(fb.getBootstrapTo().iterator().next()).awaitUninterruptibly();
		MyPeerListener listener = new MyPeerListener();
		this.peer.addPeerListener(listener);
	}
	
	public static void main(String[] args) throws Exception
	{
		SuperPeer sp = new SuperPeer(1, 8088);
		log("Super Peer Initialized");
		
		LogCSVData csvLog = new LogCSVData("/home/ali/SATURN_LIN/Metis/data/peerlist.csv");
		Format formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		
		String serviceID = "p2pvpn";
		String serviceIP = null;
		Number160 nr = Number160.createHash(serviceID);
		log("Service ID Hash Created");
		
		//Set<String> dataSet = new HashSet<String>(6);
		
		while (true)
		{
			log("Discovering all peers with the same service ID: "+serviceID+" on "+new Date(System.currentTimeMillis()));
			FutureDHT futureDHT = sp.peer.getAll(nr);		// Get all peers with the same location key
	        futureDHT.awaitUninterruptibly();
	        
	        Iterator<Data> it = futureDHT.getData().values().iterator();
	        //log("Here come the list :-"+new Date(System.currentTimeMillis()).toString());
	        
	        if(!it.hasNext())
	        {
	        	log("Nothing to see here folks!!! "+new Date(System.currentTimeMillis()));
	        	// Clear the Set cache when no peers are online, so that data can be logged
	        	// the next time they come online without restarting the SuperPeer
	        	//dataSet.clear();
	       	}
	        
	        while(it.hasNext())		
			{
	        	String tempIP = it.next().getObject().toString();
	        	//log("VM online currently :- "+tempIP);
	        	
	        	//if(dataSet.add(tempIP))
	        	//{
	        		if(tempIP.startsWith("130")) serviceIP = "UMU";
	        		else if(tempIP.startsWith("109")) serviceIP = "FLEX";
	        		else serviceIP = "ATOS";
	        		
	        		log(tempIP+" on the "+serviceIP+" cloud updated at "+formatter.format(new Date(System.currentTimeMillis())));
		        	
	        		csvLog.recordData(serviceIP.concat(",").concat(tempIP).split(","));
		        	
	        		log("--> Data Logged On SATURN");
	        	//}
	        	
	        	//System.out.println(tempIP);
	        	//csvLog.recordData(serviceID.concat(",").concat(tempIP).concat(",")
	        	//		.concat(formatter.format(new Date(System.currentTimeMillis()))).split(","));
	        	//System.out.println("--> Data Logged On SATURN");
			}
	        
	        futureDHT.shutdown();
			Thread.sleep(5000);	
		}
		
		/*
		while(true)
		{
			MyPeerMapChangeListener peerMapChangeListener = new MyPeerMapChangeListener();
			MyPeerStatusListener peerStatusListener = new MyPeerStatusListener();
			sp.peer.getPeerBean().getPeerMap().addPeerMapChangeListener(peerMapChangeListener);
			sp.peer.getPeerBean().getPeerMap().addPeerOfflineListener(peerStatusListener);
			
			log(sp.peer.getPeerBean().getPeerMap().getStatistics().getEstimatedNumberOfNodes());
			Iterator<PeerAddress> it = sp.peer.getPeerBean().getPeerMap().getAll().iterator();
			log("Here it comes :- "+new Date(System.currentTimeMillis()).toString());
	        while(it.hasNext())		
			{
	        	PeerAddress peerAddress = it.next();
				log("Peer IP address = "+peerAddress.getInetAddress());			// Shows the peer IP address even if peer is offline
				log(sp.peer.getPeerBean().getPeerMap().contains(peerAddress));	// Returns "true" even if peer is offline
			}
	        
	        it = null;
			Thread.sleep(4000);
		}
		*/		
	}
	
	private static void log(Object message)
	{
		System.out.println(message);
	}
}
