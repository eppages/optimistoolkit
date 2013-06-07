package eu.optimis.ics.p2p;

import net.tomp2p.futures.FutureBootstrap;
import net.tomp2p.futures.FutureDiscover;

/**
 * @author Ali Sajjad
 *
 */
public class PeriodicBootstrap implements Runnable {

	final private VPNPeer client;
	
	public PeriodicBootstrap(VPNPeer client)
	{
		this.client = client;
	}
	
	public void run() 
	{
		FutureDiscover future = client.getPeer().discover(client.getSuperPeerAddress());
		future.awaitUninterruptibly(10000);
		
		FutureBootstrap fb = client.getPeer().bootstrap(client.getSuperPeerAddress());
		fb.awaitUninterruptibly();
		client.getPeer().discover(fb.getBootstrapTo().iterator().next()).awaitUninterruptibly();
	}
}
