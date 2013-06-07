/**
 *  Copyright 2013 University of Leeds
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package eu.optimis.mi.gui.client.userwidget.graph;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.Style;
import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.extjs.gxt.ui.client.store.GroupingStore;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.form.TextArea;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.toolbar.LabelToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import eu.optimis.mi.gui.client.MainEvents;
import eu.optimis.mi.gui.client.MonitoringManagerWebServiceAsync;
import eu.optimis.mi.gui.client.model.EcoNodeInfoResource;
import eu.optimis.mi.gui.client.model.EcoServiceDeploymentInfoData;

/**
 * In this class is where the Panel containing the Service Deployment
 * Information is introduced.
 *
 * @author jsubirat
 */
public class GraphicReportEcoServiceDeploymentInfoPanel extends ContentPanel {

    private LabelToolItem labelToolItem = new LabelToolItem();
    //private static final String colours[] = {"#FF0000", "#329600", "#0000FF", "#A500A5", "#00FFFF", "#FF00FF"};
    private ColumnModel cm;
    private GroupingStore<EcoNodeInfoResource> nodeStore;
    private Grid<EcoNodeInfoResource> nodeGrid;
    private ToolBar toolBar;
    private TextArea output;
    private MonitoringManagerWebServiceAsync service;
    private Timer timer;

    public GraphicReportEcoServiceDeploymentInfoPanel() {
        setHeading("Ecoefficiency Tool Service Deployment Evaluation (IP)");
        setLayout(new FitLayout());
        toolBar = new ToolBar();
        labelToolItem = new LabelToolItem("Node Characteristics");
        toolBar.add(labelToolItem);
        labelToolItem.setVisible(true);
        setTopComponent(toolBar);

        nodeStore = new GroupingStore<EcoNodeInfoResource>();
        nodeStore.groupBy("nodeId");
        
        cm = new ColumnModel(EcoServiceDeploymentInfoData.getNodeTableColumnConfig());

        nodeGrid = new Grid<EcoNodeInfoResource>(nodeStore, cm);
        nodeGrid.setTitle("Node Characteristics");
        nodeGrid.setBorders(true);
        nodeGrid.setStripeRows(true);
        nodeGrid.setAutoHeight(true);
        nodeGrid.getView().setForceFit(true);
        add(nodeGrid);
        
        ToolBar toolBar2 = new ToolBar();
        LabelToolItem labelToolItem2 = new LabelToolItem("Eco-Efficiency Tool Output");
        toolBar2.add(labelToolItem2);
        labelToolItem2.setVisible(true);
        add(toolBar2);
        
        ToolBar toolBar3 = new ToolBar();
        toolBar3.setAlignment(Style.HorizontalAlignment.CENTER);
        toolBar3.setAutoHeight(true);
        output = new TextArea();
        output.addStyleName("demo-TextArea");
        output.setWidth("800px");
        output.setHeight("400px");
        output.setReadOnly(true);
        output.setEmptyText("Eco: No reports present.");
        toolBar3.add(output);
        output.setVisible(true);
        add(toolBar3);
        
        timer = new Timer() {
            public void run() {
                if (isUpdateNecessary()) {
                    updateInfo();
                }
            }
        };
    }

    public void setSubmissionText(String text) {
        labelToolItem.setLabel(text);
        labelToolItem.setVisible(true);
    }
    
    public GroupingStore<EcoNodeInfoResource> getNodeStore() {
        return nodeStore;
    }
    
    public void setOutputText(String text) {
        if(text != null && !text.equalsIgnoreCase(output.getValue())) {
            output.setValue(text);
        }
    }
    
    public void restartTimer() {
        timer.scheduleRepeating(5000);
    }
    
    public void stopTimer() {
        timer.cancel();
    }
    
    public boolean isUpdateNecessary() {
        if (this.isVisible()) {
            return true;
        } else {
            return false;
        }
    }
    
    private void updateInfo() {
        service = (MonitoringManagerWebServiceAsync) Registry.get("guiservice");
        service.getServiceDeploymentEcoInfo(new AsyncCallback<EcoServiceDeploymentInfoData>() {
            public void onFailure(Throwable caught) {
                Dispatcher.forwardEvent(MainEvents.Error,
                        caught);
            }

            public void onSuccess(EcoServiceDeploymentInfoData result) {
                GroupingStore<EcoNodeInfoResource> nodeStore = getNodeStore();
                nodeStore.removeAll();
                nodeStore.add(result.getNodeInfoResources());
                setOutputText(result.getDeploymentOutput());
            }
        });
    }
}
