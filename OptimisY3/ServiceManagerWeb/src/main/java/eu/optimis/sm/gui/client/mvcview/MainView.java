/*
 * Copyright 2012 University of Stuttgart
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package eu.optimis.sm.gui.client.mvcview;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.extjs.gxt.ui.client.Style.Orientation;
import com.extjs.gxt.ui.client.mvc.AppEvent;
import com.extjs.gxt.ui.client.mvc.Controller;
import com.extjs.gxt.ui.client.mvc.View;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.Viewport;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;
import com.extjs.gxt.ui.client.widget.layout.BorderLayout;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.google.gwt.user.client.ui.RootPanel;

import eu.optimis.sm.gui.client.MainEvents;

public class MainView extends View {
	public static final String WEST_PANEL = "west";
	public static final String VIEWPORT = "viewport";
	public static final String CENTER_PANEL = "center";
	public static final String NORD_PANEL = "north";

	private Viewport viewport;
	private BorderLayout layout;
	public static ContentPanel west;
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

		createNorth();
		createWest();
		createCenter();

		Registry.register(VIEWPORT, viewport);
		Registry.register(WEST_PANEL, west);
		Registry.register(CENTER_PANEL, center);
		Registry.register(NORD_PANEL, north);
		RootPanel.get().add(viewport);
	}

	private void createNorth() {
		ContentPanel north = new ContentPanel();
		BorderLayoutData northData = new BorderLayoutData(LayoutRegion.NORTH, 90);
		
		northData.setCollapsible(true);
		northData.setFloatable(false);
		northData.setHideCollapseTool(false);
		northData.setSplit(false);
    
		northData.setMargins(new Margins(0, 0, 5, 0));
        Html h = new Html("<div id='optimisTitle'>"
                + "<img src=\"./resources/images/optimis/optimisnew.png\" style=\"float: left;\">" +
                "Service Provider Dashboard</div>");

//		Html h = new Html("<font size=\"16\" align=center><h1" +
//				"align=center>Service Provider Dashboard</h1></font>");// + 
//		Image logo = new Image("./resources/images/optimis/Optimis2.jpg");
//		north.add(logo);
		//north.addText("Copyright 2013 @ HLRS");
		north.add(north);
		north.add(h);
		viewport.add(north, northData);
	}

	private void createWest() {
		west = new ContentPanel();
		west.setBodyBorder(false);
		//west.setLayout(new AccordionLayout());
		west.setLayout(new RowLayout(Orientation.VERTICAL));
		west.setLayoutOnChange(true);
		west.setHeading("Navigation");
		
		BorderLayoutData westData = new BorderLayoutData(LayoutRegion.WEST, 300, 150, 350); //200/150/350
		westData.setSplit(false);
		westData.setCollapsible(true);
		westData.setMargins(new Margins(5, 0, 5, 5));
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
		if (event.getType() == MainEvents.init) {
			initUI();
		}
	}

}
