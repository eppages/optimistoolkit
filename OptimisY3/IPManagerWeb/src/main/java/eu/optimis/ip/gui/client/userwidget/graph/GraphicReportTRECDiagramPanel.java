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
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Frame;
import eu.optimis.ip.gui.client.IPManagerWebServiceAsync;
import eu.optimis.ip.gui.client.MainEvents;
import eu.optimis.ip.gui.client.resources.Constants;
//import com.allen_sauer.gwt.log.client.Log;

/**
 *
 * @author greig
 */
public class GraphicReportTRECDiagramPanel extends ContentPanel {

    private Frame frame;

    public GraphicReportTRECDiagramPanel() {

        setHeading(Constants.MENU_TREC_NAME);
        setLayout(new FitLayout());
        frame = new Frame();
        add(frame);
        setLayout(new FitLayout());
        layout(true);
    }

    public void setTRECFrame(final String stage, final String level, final String identifier) {

        //Log.debug("Received TREC. Stage: " + stage + " Level: " + level + " Identifier: " + identifier);

        setHeading(Constants.MENU_TREC_NAME);

        IPManagerWebServiceAsync service = (IPManagerWebServiceAsync) Registry.get("guiservice");
        service.getTRECUrl(new AsyncCallback<String>() {
            @Override
            public void onFailure(Throwable caught) {

                Dispatcher.forwardEvent(MainEvents.Error, caught);
            }

            @Override
            public void onSuccess(String trecURL) {

                trecURL = trecURL.concat("&side=ip");
                if (stage != null) {
                    trecURL = trecURL.concat("&stage=" + stage);
                    if ("operation".equalsIgnoreCase(stage)) {
                        trecURL = trecURL.concat("&level=" + level);
                        if (identifier != null) {
                            setHeading("TREC operation information for " + level + " " + identifier);
                            trecURL = trecURL.concat("&identifier=" + identifier);
                        } else {
                            setHeading("TREC operation information for the IP infrastructure");
                        }
                    } else {
                        setHeading("TREC information at deployment stage");
                    }
                }

                Frame newFrame = new Frame(trecURL);
                newFrame.setWidth("100%");
                remove(frame);
                add(newFrame);
                frame = newFrame;
                setLayout(new FitLayout());
                layout(true);
            }
        });
    }
}