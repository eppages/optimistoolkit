/**
 * Copyright (C) 2010-2013 Barcelona Supercomputing Center
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version. This library is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser
 * General Public License for more details. You should have received a copy of
 * the GNU Lesser General Public License along with this library; if not, write
 * to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA
 * 02111-1307 USA
 */
package eu.optimis.ip.gui.client;

import com.extjs.gxt.ui.client.GXT;
import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.extjs.gxt.ui.client.util.Theme;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import eu.optimis.ip.gui.client.mvc.*;

//import org.apache.log4j.Logger;
/**
 * Entry point classes define
 * <code>onModuleLoad()</code>.
 */
public class IPManagerWeb implements EntryPoint {

    /**
     * The message displayed to the user when the server cannot be reached or
     * returns an error.
     */
    private static final String SERVER_ERROR = "An error occurred while "
            + "attempting to contact the server. Please check your network "
            + "connection and try again.";
    public static final String SERVICE = "guiservice";

    /**
     * Create a remote service proxy to talk to the server-side guiservice
     * service.
     */
    //Log
    //private static Logger logger = Logger.getLogger(IPManagerWeb.class);
    /**
     * This is the entry point method.
     */
    public void onModuleLoad() {
        // Simple messages
        /*
         * logger.trace("A message at trace level."); logger.debug("A message at
         * debug level."); logger.info("A message at info level.");
         * logger.warn("A message at warn level.");
         */


        GXT.setDefaultTheme(Theme.GRAY, true);
        IPManagerWebServiceAsync service = (IPManagerWebServiceAsync) GWT.create(IPManagerWebService.class);
        Registry.register(SERVICE, service);


        Dispatcher dispatcher = Dispatcher.get();
        dispatcher.addController(new MainController());

        //Authentication
        dispatcher.addController(new AuthenticationController());
        
        //Admission control
        dispatcher.addController(new ACController());
        
        //DM
        dispatcher.addController(new DMController());
        
        //CO
        dispatcher.addController(new COController());
        
        //EMOTIVE
        dispatcher.addController(new EMOTIVEController());
        
        //TREC GENERAL GUI
        dispatcher.addController(new TRECController());
        
        //MI WEB
        dispatcher.addController(new MonitoringController());
        
        //IP Registry
        dispatcher.addController(new IPRegistryController());
        
        //IP Configuration
        dispatcher.addController(new GraphicIPConfigReportController());
        
        //Configuration
        dispatcher.addController(new ConfigurationController());

        //Components Output
        dispatcher.addController(new OutputController());
  
        dispatcher.dispatch(MainEvents.Init);

        GXT.hideLoadingPanel("loading");

        // nested diagnostic context
       /*
         * NDC.push("ndc1"); NDC.push("ndc2"); logger.info("Test for the NDC.");
         * NDC.clear();
         *
         * // mapped diagnostic context MDC.put("key1", "value1");
         * MDC.put("key2", "value2"); logger.info("Test for the MDC."); // MDC.clear();
         */
    }
}
