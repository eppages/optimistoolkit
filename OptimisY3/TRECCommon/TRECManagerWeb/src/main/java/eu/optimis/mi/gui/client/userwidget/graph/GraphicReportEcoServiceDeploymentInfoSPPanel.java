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
//import com.extjs.gxt.ui.client.store.GroupingStore;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.form.TextArea;
//import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
//import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.toolbar.LabelToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import eu.optimis.mi.gui.client.MainEvents;
import eu.optimis.mi.gui.client.MonitoringManagerWebServiceAsync;
//import eu.optimis.mi.gui.client.model.EcoNodeInfoResource;
import eu.optimis.mi.gui.client.model.EcoServiceDeploymentInfoData;


/**
 * In this class is where the Panel containing the Service Deployment
 * Information is introduced.
 *
 * @author jsubirat
 */
public class GraphicReportEcoServiceDeploymentInfoSPPanel extends ContentPanel {

    private LabelToolItem labelToolItem = new LabelToolItem();
    private ToolBar toolBar;
    private TextArea output;
    private MonitoringManagerWebServiceAsync service;
    private Timer timer;

    public GraphicReportEcoServiceDeploymentInfoSPPanel() {
        setHeading("Ecoefficiency Tool Service Deployment Evaluation (SP)");
        setLayout(new FitLayout());
        toolBar = new ToolBar();
        labelToolItem = new LabelToolItem("Eco-Efficiency Tool Output");
        toolBar.add(labelToolItem);
        labelToolItem.setVisible(true);
        setTopComponent(toolBar);

        ToolBar toolBar2 = new ToolBar();
        toolBar2.setAlignment(Style.HorizontalAlignment.CENTER);
        toolBar2.setAutoHeight(true);
        output = new TextArea();
        output.addStyleName("demo-TextArea");
        output.setWidth("800px");
        output.setHeight("400px");
        output.setReadOnly(true);
        output.setEmptyText("Eco: No reports present.");
        toolBar2.add(output);
        output.setVisible(true);
        add(toolBar2);

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
        service.getServiceDeploymentEcoInfoSP(new AsyncCallback<EcoServiceDeploymentInfoData>() {
            public void onFailure(Throwable caught) {
                Dispatcher.forwardEvent(MainEvents.Error,
                        caught);
            }

            public void onSuccess(EcoServiceDeploymentInfoData result) {
                setOutputText(result.getDeploymentOutput());
            }
        });
    }
}
