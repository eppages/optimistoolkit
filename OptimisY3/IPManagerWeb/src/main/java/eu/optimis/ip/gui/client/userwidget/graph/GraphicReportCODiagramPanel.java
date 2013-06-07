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
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.GridEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.extjs.gxt.ui.client.store.GroupingStore;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.toolbar.LabelToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import eu.optimis.ip.gui.client.IPManagerWebServiceAsync;
import eu.optimis.ip.gui.client.MainEvents;
import eu.optimis.ip.gui.client.model.COInfrastructureOperationData;
import eu.optimis.ip.gui.client.model.COServiceOperationData;
import eu.optimis.ip.gui.client.model.InfrastructureDataResource;
import eu.optimis.ip.gui.client.model.ServiceDataResource;
import eu.optimis.ip.gui.client.mvc.MainView;
import eu.optimis.ip.gui.client.resources.Constants;
import java.util.ArrayList;

/**
 * Center Screen displaying Components Output text.
 *
 * @author jsubirat
 */
public class GraphicReportCODiagramPanel extends ContentPanel {

    private GroupingStore<ServiceDataResource> serviceStore;
    private Grid<ServiceDataResource> serviceGrid;
    private GroupingStore<InfrastructureDataResource> infrastructureStore;
    private Grid<InfrastructureDataResource> infrastructureGrid;

    public GraphicReportCODiagramPanel() {

        setHeading(Constants.MENU_CO_NAME);
        setLayout(new FitLayout());
        ToolBar toolBarInfr = new ToolBar();
        LabelToolItem labelInfrastructure = new LabelToolItem("Infrastructure: ");
        toolBarInfr.add(labelInfrastructure);
        labelInfrastructure.setVisible(true);
        setTopComponent(toolBarInfr);

        LabelToolItem labelInfrastructureTREC = new LabelToolItem("Infrastructure TREC");
        labelInfrastructureTREC.setStyleAttribute("color", "blue");
        labelInfrastructureTREC.addListener(Events.OnClick, new Listener<BaseEvent>() {

            @Override
            public void handleEvent(BaseEvent be) {
                ArrayList eventData = new ArrayList();
                eventData.add("operation");
                eventData.add("infrastructure");
                Dispatcher.get().dispatch(MainEvents.TREC, eventData);
                ContentPanel west = (ContentPanel) Registry.get(MainView.WEST_PANEL);
                west.collapse();
            }
        });
        toolBarInfr.add(labelInfrastructureTREC);
        labelInfrastructureTREC.setVisible(true);

        infrastructureStore = new GroupingStore<InfrastructureDataResource>();

        ColumnModel cmInfrastructure = new ColumnModel(COInfrastructureOperationData.getInfrastructureTableColumnConfig());

        infrastructureGrid = new Grid<InfrastructureDataResource>(infrastructureStore, cmInfrastructure);
        infrastructureGrid.setBorders(true);
        infrastructureGrid.setStripeRows(true);
        infrastructureGrid.setAutoHeight(true);
        infrastructureGrid.getView().setForceFit(true);
        add(infrastructureGrid);

        final int indexColInfrastructure = cmInfrastructure.findColumnIndex("trec");
        infrastructureGrid.addListener(Events.CellClick, new Listener<BaseEvent>() {

            public void handleEvent(BaseEvent be) {
                GridEvent ge = (GridEvent) be;
                if (ge.getColIndex() == indexColInfrastructure) {
                    InfrastructureDataResource resource = infrastructureGrid.getSelectionModel().getSelectedItem();
                    ArrayList eventData = new ArrayList();
                    if (resource.getTREC().equalsIgnoreCase("Node TREC")) {
                        eventData.add("operation");
                        eventData.add("node");
                        eventData.add(resource.getNodeId());
                    } else {
                        eventData.add("operation");
                        eventData.add("vm");
                        eventData.add(resource.getVMId());
                    }
                    Dispatcher.get().dispatch(MainEvents.TREC, eventData);
                    ContentPanel west = (ContentPanel) Registry.get(MainView.WEST_PANEL);
                    west.collapse();
                }
            }
        });

        ToolBar toolBarService = new ToolBar();
        LabelToolItem labelDeployedServices = new LabelToolItem("Deployed Services");
        toolBarService.add(labelDeployedServices);
        labelDeployedServices.setVisible(true);
        add(toolBarService);

        serviceStore = new GroupingStore<ServiceDataResource>();

        ColumnModel cmService = new ColumnModel(COServiceOperationData.getServiceTableColumnConfig());

        serviceGrid = new Grid<ServiceDataResource>(serviceStore, cmService);
        serviceGrid.setBorders(true);
        serviceGrid.setStripeRows(true);
        serviceGrid.setAutoHeight(true);
        serviceGrid.getView().setForceFit(true);
        add(serviceGrid);

        final int indexColService = cmService.findColumnIndex("trec");
        serviceGrid.addListener(Events.CellClick, new Listener<BaseEvent>() {

            public void handleEvent(BaseEvent be) {
                GridEvent ge = (GridEvent) be;
                if (ge.getColIndex() == indexColService) {
                    ServiceDataResource resource = serviceGrid.getSelectionModel().getSelectedItem();
                    ArrayList eventData = new ArrayList();
                    if (resource.getTREC().equalsIgnoreCase("Service TREC")) {
                        eventData.add("operation");
                        eventData.add("service");
                        eventData.add(resource.getServiceId());
                    } else {
                        eventData.add("operation");
                        eventData.add("vm");
                        eventData.add(resource.getVMId());
                    }
                    Dispatcher.get().dispatch(MainEvents.TREC, eventData);
                    ContentPanel west = (ContentPanel) Registry.get(MainView.WEST_PANEL);
                    west.collapse();
                }
            }
        });


        updateServiceTable();
        updateInfrastructureTable();
        Timer t = new Timer() {

            public void run() {
                if (isUpdateNecessary()) {
                    updateServiceTable();
                    updateInfrastructureTable();
                }

            }
        };
        // Schedule the timer to run once in 5 seconds.
        t.scheduleRepeating(5000);
    }

    public boolean isUpdateNecessary() {
        if (this.isVisible()) {
            return true;
        } else {
            return false;
        }
    }

    public void updateServiceTable() {
        IPManagerWebServiceAsync service = (IPManagerWebServiceAsync) Registry.get("guiservice");
        service.getCOServiceOperationData(new AsyncCallback<COServiceOperationData>() {

            public void onFailure(Throwable caught) {
                Dispatcher.forwardEvent(MainEvents.Error, caught);
            }

            public void onSuccess(COServiceOperationData result) {

                serviceStore.removeAll();
                serviceStore.add(result.getServiceDataResources());
            }
        });
    }

    public void updateInfrastructureTable() {
        IPManagerWebServiceAsync service = (IPManagerWebServiceAsync) Registry.get("guiservice");
        service.getCOInfrastructureOperationData(new AsyncCallback<COInfrastructureOperationData>() {

            public void onFailure(Throwable caught) {
                Dispatcher.forwardEvent(MainEvents.Error, caught);
            }

            public void onSuccess(COInfrastructureOperationData result) {

                infrastructureStore.removeAll();
                infrastructureStore.add(result.getInfrastructureDataResources());
            }
        });
    }
}
