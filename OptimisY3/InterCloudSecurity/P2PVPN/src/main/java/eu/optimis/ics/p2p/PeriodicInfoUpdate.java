package eu.optimis.ics.p2p;

import java.io.IOException;

import net.tomp2p.futures.FutureDHT;
import net.tomp2p.peers.Number160;
import net.tomp2p.storage.Data;

/**
 * @author Ali Sajjad
 *
 */
final class PeriodicInfoUpdater implements Runnable {
	
	final private VPNPeer client;
	
	public PeriodicInfoUpdater(VPNPeer client)
	{
		this.client = client;
	}
	
	public void run() 
	{
		Number160 nr = Number160.createHash(client.getServiceID());
		
		Data peerIdData = null;
		
		try 
		{
			
		peerIdData = new Data(client.externalIP);
		
		peerIdData.setTTLSeconds(30);
		
		FutureDHT futureDHT = client.getPeer().add(nr, peerIdData);
		futureDHT.awaitUninterruptibly();
        
		System.out.println("Added: "+peerIdData.toString()+" i.e. "+peerIdData.getObject().toString()+" (" + !futureDHT.isFailed() + ")");
		
		} 
		catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

}
