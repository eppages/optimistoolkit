package eu.optimis.ics.p2p;

import java.io.IOException;
import java.util.Collection;
import java.util.Date;

import net.tomp2p.futures.FutureDHT;
import net.tomp2p.peers.Number160;
import net.tomp2p.storage.Data;

/**
 * @author Ali Sajjad
 *
 */
final class PeriodicPeerChecker implements Runnable
{
	final private VPNPeer client;
	
	public PeriodicPeerChecker(VPNPeer client)
	{
		this.client = client;
	}
	
	public void run() 
	{
		Number160 nr = Number160.createHash(client.getServiceID());
		FutureDHT futureDHT = client.getPeer().getAll(nr);		// Get all peers with the same service ID hash
        futureDHT.awaitUninterruptibly();
        
        Collection<Data> collection = futureDHT.getData().values();
        
        try 
        {
        	if(collection.size()>1)	// If current client is not the only client in the DHT
        	{
			PeerSPDConf spd = new PeerSPDConf(client.getLocalIP(), client.externalIP, collection);
			spd.genSPDFile();
        	} 
        	else System.out.println(":( I am Alone :( "+collection.size()+"\t"+new Date(System.currentTimeMillis())); 
        }
        catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
        
        /*
        Iterator<Data> iterator = futureDHT.getData().values().iterator();
        while(iterator.hasNext())
        {
        	try 
        	{
        		peerList.a iterator.next().getObject().toString();
				i++;
                System.out.println("Got: "+result[i]);
			} 
        	catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
        	catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
        */
	}
}
