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
package eu.optimis.ip.gui.client.mvc.view;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.mvc.AppEvent;
import com.extjs.gxt.ui.client.mvc.Controller;
import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.extjs.gxt.ui.client.mvc.View;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import eu.optimis.ip.gui.client.MainEvents;
import eu.optimis.ip.gui.client.mvc.MainView;
import eu.optimis.ip.gui.client.userwidget.graph.GraphicOutputReportPanel;

/**
 *
 * @author greig
 */
public class GraphicReportOutputView extends View {

    private ContentPanel graphicReport;

    public GraphicReportOutputView(Controller controller) {
        super(controller);
    }

    @Override
    protected void handleEvent(AppEvent event) {
        if (event.getType() == MainEvents.Init) {
            initUI();
        }

        if (event.getType() == MainEvents.LOGINSUCCESSFUL) {
            showUI();
        }

        if (event.getType() == MainEvents.LOGOUT) {
            hideUI();
        }

    }

    @Override
    protected void initialize() {
    }

    private void initUI() {
        graphicReport = new GraphicOutputReportPanel();
        graphicReport.setAnimCollapse(true);
        graphicReport.addListener(Events.Expand, new Listener<ComponentEvent>() {

            public void handleEvent(ComponentEvent be) {
                LayoutContainer wrapper = (LayoutContainer) Registry.get(MainView.CENTER_PANEL);
                wrapper.removeAll();
                Dispatcher.get().dispatch(MainEvents.Output, null);
                return;
            }
        });
        ContentPanel west = (ContentPanel) Registry.get(MainView.WEST_PANEL);
        west.add(graphicReport);
        graphicReport.setVisible(false);
        graphicReport.disable();
    }

    private void hideUI() {
        graphicReport.setVisible(false);
        graphicReport.disable();
    }

    private void showUI() {
        graphicReport.setVisible(true);
        graphicReport.enable();
    }
}
