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

//import com.allen_sauer.gwt.log.client.Log;
import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.util.IconHelper;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.Widget;
import eu.optimis.ip.gui.client.IPManagerWebServiceAsync;
import eu.optimis.ip.gui.client.MainEvents;
import eu.optimis.ip.gui.client.model.IP;
import java.util.ArrayList;
import java.util.List;

public class GraphicReportIPRegistryDiagramPanel extends ContentPanel {

    private IPManagerWebServiceAsync service;
    private String textScreen;
    private ListStore<IP> storeIPs;
    private Grid<IP> gridIPs;
    private ColumnModel cm;
    private Widget textArea = new TextArea();

    public GraphicReportIPRegistryDiagramPanel() {
        setHeading("IP Registry");
        setLayout(new FitLayout());

        ToolBar toolBar = new ToolBar();
        Button refreshButton = new Button("Refresh");
        refreshButton.setIcon(IconHelper.createStyle("icon-email-add"));
        toolBar.add(refreshButton);
        setTopComponent(toolBar);

        service = (IPManagerWebServiceAsync) Registry.get("guiservice");

        textScreen = new String("Waiting for response from IP Registry...");
        ((TextArea) textArea).setValue(textScreen);
        add(textArea);
        layout(true);
        setLayoutOnChange(true);

        cm = new ColumnModel(IP.getColumnConfigIP());
        storeIPs = new ListStore<IP>();

        service.ipRegistry(new AsyncCallback<ArrayList<IP>>() {
            public void onFailure(Throwable caught) {
                System.out.println("Error: ip registry");
                Dispatcher.forwardEvent(MainEvents.Error, caught);
            }

            public void onSuccess(ArrayList<IP> ips) {
                System.out.println("Successfully executed: ip registry");
                removeAll();
                storeIPs = new ListStore<IP>();
                storeIPs.add((List<IP>) ips);
                gridIPs = new Grid<IP>(storeIPs, cm);
                //grid3.setSelectionModel(sm);
                gridIPs.disableTextSelection(false);
                gridIPs.setTitle(" Listed ips ");
                gridIPs.setBorders(true);
                gridIPs.getView().setForceFit(true);
                System.out.println("ips(0) = " + ips.get(0).toString());
                add(gridIPs);
                layout(true);
            }
        });

        refreshButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
            @Override
            public void componentSelected(ButtonEvent ce) {
                removeAll();
                textScreen = new String("Waiting for response from IP Registry...");
                ((TextArea) textArea).setValue(textScreen);
                add(textArea);
                layout(true);

                service.ipRegistry(new AsyncCallback<ArrayList<IP>>() {
                    public void onFailure(Throwable caught) {
                        Dispatcher.forwardEvent(MainEvents.Error, caught);
                    }

                    public void onSuccess(ArrayList<IP> ips) {
                        removeAll();
                        storeIPs = new ListStore<IP>();
                        storeIPs.add((List<IP>) ips);
                        gridIPs = new Grid<IP>(storeIPs, cm);
                        gridIPs.setTitle(" Listed ips ");
                        gridIPs.setBorders(true);
                        gridIPs.getView().setForceFit(true);
                        add(gridIPs);
                        layout(true);
                        System.out.println("ips(0) = " + ips.get(0).toString());
                    }
                });
            }
        });
        layout(true);
    }
}
