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
package eu.optimis.ip.gui.client.userwidget.graph;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Frame;
import eu.optimis.ip.gui.client.IPManagerWebServiceAsync;
import eu.optimis.ip.gui.client.MainEvents;
import eu.optimis.ip.gui.client.resources.Constants;

/**
 *
 * @author greig
 */
public class GraphicReportDMDiagramPanel extends ContentPanel {

    public GraphicReportDMDiagramPanel() {
        setHeading(Constants.MENU_DM_NAME);
        setLayout(new FitLayout());

        IPManagerWebServiceAsync service = (IPManagerWebServiceAsync) Registry.get("guiservice");
        service.getDMUrl(new AsyncCallback<String>() {

            @Override
            public void onFailure(Throwable caught) {

                Dispatcher.forwardEvent(MainEvents.Error, caught);
            }

            @Override
            public void onSuccess(String result) {

                Frame frame = new Frame(result);
                frame.setWidth("100%");
                add(frame);
                layout(true);

                setLayout(new FitLayout());
            }
        });
    }
}