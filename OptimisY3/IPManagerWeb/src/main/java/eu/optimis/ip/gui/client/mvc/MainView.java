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
package eu.optimis.ip.gui.client.mvc;

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
import com.extjs.gxt.ui.client.widget.layout.MenuLayout;
import com.extjs.gxt.ui.client.widget.layout.BorderLayout;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.RootPanel;

import eu.optimis.ip.gui.client.MainEvents;

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

        createNorth();
        createWest();
        createCenter();

        // registry serves as a global context
        Registry.register(VIEWPORT, viewport);
        Registry.register(WEST_PANEL, west);
        Registry.register(CENTER_PANEL, center);
        Registry.register(NORD_PANEL, north);
        RootPanel.get().add(viewport);
    }

    private void createNorth() {
        ContentPanel north = new ContentPanel();
        BorderLayoutData northData = new BorderLayoutData(LayoutRegion.NORTH, 100);
        northData.setCollapsible(true);
        northData.setFloatable(true);
        northData.setHideCollapseTool(true);
        northData.setSplit(true);
        northData.setMargins(new Margins(0, 0, 4, 0));
        Html h = new Html("<div id='optimisTitle'>"
                            + "<img src=\"./resources/images/optimis/Optimis.jpg\" style=\"float: left;\">Infrastructure Provider Dashboard</div>");
        //Image logo = new Image("./resources/images/optimis/Optimis.jpg");
        //north.add(logo);
        //north.addText("Copyright 2012 OPTIMIS ");
        north.add(north);
        north.add(h);
        north.setStyleAttribute("background-color", "#d9e7f8");
        viewport.add(north, northData);
    }

    private void createWest() {
        BorderLayoutData data = new BorderLayoutData(LayoutRegion.WEST, 200,
                150, 350);
        data.setMargins(new Margins(5, 0, 5, 5));

        west = new ContentPanel();
        west.setBodyBorder(false);
        AccordionLayout layout = new AccordionLayout();
        layout.setFill(false);
        west.setLayout(layout);
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
