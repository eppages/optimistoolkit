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
import java.util.Collection;
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
public class TestSuperPeer {
	
	final private Peer peer;
	
	public TestSuperPeer(int nodeId, int localPort) throws Exception 
	{
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
		TestSuperPeer sp = new TestSuperPeer(1, 8089);
		log("Super Peer Initialized");
		
		LogCSVData csvLog = new LogCSVData("/home/ali/SATURN_LIN/Metis/data/peerlist.csv");
		Format formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		
		String serviceID = "medical";
		Number160 nr = Number160.createHash(serviceID);
		System.out.println("Medical Hash Created");
		
		Set<Data> dataSet = new HashSet<Data>();
		
		while (true)
		{
			System.out.println("Discovering all peers with the same location key");
			FutureDHT futureDHT = sp.peer.getAll(nr);		// Get all peers with the same location key
	        futureDHT.awaitUninterruptibly();
	        
	        Collection<Data> collection = futureDHT.getData().values();
	        Iterator<Data> it = collection.iterator();
	        
	        if(collection.isEmpty())
	        {
	        		System.out.println("Nothing to see here folks!!!");
	        		//Thread.sleep(3000);	
	        }
	        else
	        {
	        	// Add all current IP data to the set
	        	dataSet.addAll(collection);
	        	
	        	while(it.hasNext())		
				{
	        		String tempIP = it.next().getObject().toString();	// Get the IP address from collection
		        	System.out.println("IP of Peer Joining the Overlay = "+tempIP);
		        	
		        	// Add to the set
		        	//if (!dataSet.add(tempIP)) log("Collection already present in the set");	
		        	//System.out.println("Set Size = "+dataSet.size());
		        	
		        	// Log with empty End Time
		        	csvLog.recordData(serviceID.concat(",").concat(tempIP).concat(",")
		        			.concat(formatter.format(new Date(System.currentTimeMillis())))
		        			.concat(",").concat(" ").split(","));
		        	
		        	System.out.println("--> Data Logged On SATURN");
				}
	        }
	        
	        if(dataSet.removeAll(collection))
	        {
	        	Iterator<Data> iter = dataSet.iterator();
	        	while(iter.hasNext())
	        	{
	        		String tempIP = iter.next().getObject().toString();
	        		csvLog.recordData(serviceID.concat(",").concat(tempIP).concat(",").concat(" ").concat(",")
		        			.concat(formatter.format(new Date(System.currentTimeMillis()))).split(","));
	        		System.out.println("--> Data Removed From SATURN");
				}
	        }

	        futureDHT.shutdown();
			Thread.sleep(3000);	
		}
	}
	
	private static void log(Object message)
	{
		System.out.println(message);
	}
}
