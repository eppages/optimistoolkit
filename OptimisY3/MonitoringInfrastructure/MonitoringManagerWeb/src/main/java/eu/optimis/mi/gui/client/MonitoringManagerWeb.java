/**

Copyright 2012 University of Stuttgart

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at 

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

**/

package eu.optimis.mi.gui.client;

import eu.optimis.mi.gui.client.mvc.GraphicReportController;
import eu.optimis.mi.gui.client.mvc.LatestReportController;
import eu.optimis.mi.gui.client.mvc.MainController;
import com.extjs.gxt.ui.client.GXT;
import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.extjs.gxt.ui.client.util.Theme;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class MonitoringManagerWeb implements EntryPoint {
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

	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {

		GXT.setDefaultTheme(Theme.GRAY, true);
		MonitoringManagerWebServiceAsync service = (MonitoringManagerWebServiceAsync) GWT
				.create(MonitoringManagerWebService.class);
		Registry.register(SERVICE, service);

		Dispatcher dispatcher = Dispatcher.get();
		dispatcher.addController(new MainController());
		dispatcher.addController(new LatestReportController());
		dispatcher.addController(new GraphicReportController());
		dispatcher.dispatch(MainEvents.Init);
		GXT.hideLoadingPanel("loading");
	}
}
