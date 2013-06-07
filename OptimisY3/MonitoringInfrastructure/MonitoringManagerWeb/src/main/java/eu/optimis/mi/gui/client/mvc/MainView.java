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

package eu.optimis.mi.gui.client.mvc;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.extjs.gxt.ui.client.mvc.AppEvent;
import com.extjs.gxt.ui.client.mvc.Controller;
import com.extjs.gxt.ui.client.mvc.View;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.Viewport;
import com.extjs.gxt.ui.client.widget.layout.AccordionLayout;
import com.extjs.gxt.ui.client.widget.layout.BorderLayout;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.RootPanel;

import eu.optimis.mi.gui.client.MainEvents;

public class MainView extends View {
	public static final String WEST_PANEL = "west";
	public static final String VIEWPORT = "viewport";
	public static final String CENTER_PANEL = "center";
	public static final String NORD_PANEL = "north";

	private Viewport viewport;
	private BorderLayout layout;
	private ContentPanel west;
	private LayoutContainer center;
	private ContentPanel north;

	public MainView(Controller controller) {
		super(controller);
	}

	protected void initialize() {
	}

	private void initUI() {
		viewport = new Viewport();
		layout = new BorderLayout();
		viewport.setLayout(layout);
		viewport.setBorders(true);

		
		createWest();
		createCenter();

		// Registry serves as a global context
		Registry.register(VIEWPORT, viewport);
		Registry.register(WEST_PANEL, west);
		Registry.register(CENTER_PANEL, center);
		String noheader = Window.Location.getParameter("noheader");
		if (noheader==null ||  !noheader.contains("y")){
			createNorth();
			Registry.register(NORD_PANEL, north);
		}
		RootPanel.get().add(viewport);
	}

	private void createNorth() {
		ContentPanel north = new ContentPanel();
		BorderLayoutData northData = new BorderLayoutData(LayoutRegion.NORTH,
				120);
		northData.setCollapsible(true);
		northData.setFloatable(true);
		northData.setHideCollapseTool(true);
		northData.setSplit(true);
		northData.setMargins(new Margins(0, 0, 3, 0));
		Html h = new Html("<h1 align=center>OPTIMIS Monitoring</h1>");
		Image logo = new Image("./resources/images/optimis/Optimis.jpg");
		north.add(logo);
		north.addText("Copyright 2012 @ HLRS");
		north.add(north);
		north.add(h);
		viewport.add(north, northData);
	}

	private void createWest() {
		BorderLayoutData data = new BorderLayoutData(LayoutRegion.WEST, 200,
				150, 350);
		data.setMargins(new Margins(5, 0, 5, 5));

		west = new ContentPanel();
		west.setBodyBorder(false);
		west.setLayout(new AccordionLayout());
		west.setLayoutOnChange(true);
		west.setHeading("Navigation");

		BorderLayoutData westData = new BorderLayoutData(LayoutRegion.WEST,
				200, 150, 350);
		westData.setSplit(true);
		westData.setCollapsible(true);
		westData.setMargins(new Margins(0, 0, 0, 0));
		viewport.add(west, westData);

	}

	private void createCenter() {
		center = new LayoutContainer();
		center.setLayout(new FitLayout());
		
		BorderLayoutData data = new BorderLayoutData(LayoutRegion.CENTER);
		data.setMargins(new Margins(5, 5, 5, 5));
		viewport.add(center, data);
	}

	@Override
	protected void handleEvent(AppEvent event) {
		if (event.getType() == MainEvents.Init) {
			initUI();
		}
	}

}
