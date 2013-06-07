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
import com.extjs.gxt.ui.client.Style;
import com.extjs.gxt.ui.client.mvc.AppEvent;
import com.extjs.gxt.ui.client.mvc.Controller;
import com.extjs.gxt.ui.client.mvc.View;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.layout.BorderLayout;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
import eu.optimis.ip.gui.client.MainEvents;
import eu.optimis.ip.gui.client.mvc.MainView;
import eu.optimis.ip.gui.client.userwidget.graph.GraphicReportTRECDiagramPanel;
import java.util.List;


/**
 *
 * @author greig
 */
public class GraphicReportTRECDiagramView extends View {

    private LayoutContainer container;
    private GraphicReportTRECDiagramPanel grdp;

    public GraphicReportTRECDiagramView(Controller controller) {
        super(controller);
    }

    @Override
    protected void handleEvent(AppEvent event) {

        if (event.getType() == MainEvents.TREC) {

            LayoutContainer wrapper = (LayoutContainer) Registry.get(MainView.CENTER_PANEL);
            wrapper.removeAll();

            String stage = null;
            String level = null;
            String identifier = null;
            List eventDataList = (List) event.getData();
            if (eventDataList != null && !eventDataList.isEmpty()) {
                stage = (String) eventDataList.get(0);
                if ("operation".equalsIgnoreCase(stage)) {
                    level = (String) eventDataList.get(1);
                    if(!"infrastructure".equalsIgnoreCase(level)) {
                        identifier = (String) eventDataList.get(2);
                    }
                }
            }
            grdp.setTRECFrame(stage, level, identifier);

            wrapper.add(container);
            wrapper.layout();
            return;
        }
    }

    @Override
    protected void initialize() {
        container = new LayoutContainer();
        BorderLayout layout = new BorderLayout();
        layout.setEnableState(false);
        container.setLayout(layout);
        grdp = new GraphicReportTRECDiagramPanel();
        container.add(grdp, new BorderLayoutData(Style.LayoutRegion.CENTER));
    }
}
