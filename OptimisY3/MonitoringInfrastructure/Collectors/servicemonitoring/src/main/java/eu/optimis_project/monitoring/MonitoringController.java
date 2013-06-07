/**
 * 
 * @author Daniel Espling (<a href="mailto:espling@cs.umu.se">espling@cs.umu.se</a>)
 * Copyright (C) 2011 Umea University
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package eu.optimis_project.monitoring;

import static eu.optimis_project.monitoring.ServiceMonitoringConfigSettings.CONFIG_FILE_NAME;
import static eu.optimis_project.monitoring.ServiceMonitoringConfigSettings.CONFIG_FILE_PATH;
import static eu.optimis_project.monitoring.ServiceMonitoringConfigSettings.CONF_OWN_PORT_KEY;

import java.net.InetAddress;
import java.net.InetSocketAddress;

import org.apache.log4j.Logger;

import com.sun.jersey.api.container.ContainerFactory;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import eu.optimis_project.monitoring.config.ConfigurationManager;
import eu.optimis_project.monitoring.rest.ServiceMonitoringResource;

public class MonitoringController {

	private final static Logger log = Logger
			.getLogger(MonitoringController.class);
    private HttpServer server;

	// TODO: Make exceptions more specific
    public void start(int port) throws Exception {

        HttpHandler handler = ContainerFactory.createContainer(HttpHandler.class,
                ServiceMonitoringResource.class);

        // Create the HTTP server using the HttpHandler
        this.server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext("/", handler);
        server.setExecutor(null);
        server.start();
	}

	public void stop() {
        server.stop(0);
		log.debug("Container closed.");
	}

	/**
	 * @param args
	 * @throws Exception
	 *             TODO: Make more specific?
	 */
    public static void main(String... args) throws Exception {
    	
        ConfigurationManager config = new ConfigurationManager(CONFIG_FILE_PATH, CONFIG_FILE_NAME);
        int port = config.getConfig().getInt(CONF_OWN_PORT_KEY);
    	
        MonitoringController mc = new MonitoringController();
        mc.start(port);
        System.out.println("Server running");
        System.out.println("Visit: http://" + InetAddress.getLocalHost().getHostAddress() + ":" + port + "/data");
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
