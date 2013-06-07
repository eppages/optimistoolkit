package eu.optimis.ipdiscovery.service;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;

import org.apache.log4j.Logger;

import com.sun.jersey.api.container.ContainerFactory;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

/**
 * Controller for the IPDiscovery service. This class allows the service to be
 * run stand-alone using a custom web server instance.
 * 
 * @author Daniel Espling
 * 
 */

public class Controller {

	private final static Logger log = Logger.getLogger(Controller.class);
	private HttpServer server;

	private static final int PORT = 8088;

	/**
	 * Start the service
	 */
	public void start() {

		HttpHandler handler = ContainerFactory.createContainer(
				HttpHandler.class, IPServiceResource.class);

		// Create the HTTP server using the HttpHandler
		try {
			this.server = HttpServer.create(new InetSocketAddress(PORT), 0);
		} catch (IOException e) {
			System.err.println("Failed to create server: " + e.getMessage());
			throw new RuntimeException(e);
		}
		server.createContext("/", handler);
		server.setExecutor(null);
		server.start();
	}

	/**
	 * Stop the service
	 */
	public void stop() {
		server.stop(0);
		log.debug("Container closed.");
	}

	/**
	 * Main method, no arguments expected.
	 */
	public static void main(String... args) throws Exception {
		Controller cont = new Controller();
		cont.start();
		System.out.println("IP Discovery Service running");
		System.out.println("Visit: http://"
				+ InetAddress.getLocalHost().getHostAddress() + ":" + PORT
				+ "/ipdiscovery");
		/*
		 * Some attemps at a nice main loop, TODO: revise this again?
		 */
		// System.out.println("Hit return to stop...");
		// System.in.read(); // This breaks running the app in no-hup
		// Thread.currentThread().wait(); //This breaks with IllegalState on
		// testbed
		// Skrew it. just sleep
		while (true) {
			Thread.sleep(1000);
		}
	}

}
