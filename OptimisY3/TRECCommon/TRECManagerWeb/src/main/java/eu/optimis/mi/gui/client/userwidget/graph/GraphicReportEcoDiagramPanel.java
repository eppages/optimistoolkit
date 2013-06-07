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

import java.util.List;

import com.extjs.gxt.charts.client.Chart;
import com.extjs.gxt.charts.client.model.ChartModel;
import com.extjs.gxt.charts.client.model.Text;
import com.extjs.gxt.charts.client.model.axis.Label;
import com.extjs.gxt.charts.client.model.axis.XAxis;
import com.extjs.gxt.charts.client.model.axis.YAxis;
import com.extjs.gxt.charts.client.model.charts.ScatterChart;
import com.extjs.gxt.ui.client.Style;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.toolbar.LabelToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import eu.optimis.mi.gui.client.model.EcoResource;
import java.util.LinkedList;

public class GraphicReportEcoDiagramPanel extends ContentPanel {

    //private TextField<String> submission;
    private LabelToolItem labelToolItem = new LabelToolItem();
    private String url = "resources/chart/open-flash-chart.swf";
    private Chart chart = new Chart(url);
    //private String xmlStr;
    private String yLegend;
    private int chartDataCount;
    private ListStore<EcoResource> store = new ListStore<EcoResource>();
    private String from;
    private String to;
    private static final String colours[] = {"#FF0000", "#329600", "#0000FF", "#A500A5", "#00FFFF", "#FF00FF"};

    public GraphicReportEcoDiagramPanel() {
        setHeading("Eco Graphic Report");
        setLayout(new FitLayout());
        ToolBar toolBar = new ToolBar();
        toolBar.add(labelToolItem);
        setTopComponent(toolBar);
        System.out.println("getChartDataCount:" + getChartDataCount());
        add(chart);
        chart.setVisible(false);
    }

    public ListStore<EcoResource> getStore() {
        return store;
    }

    public void setStore(ListStore<EcoResource> store) {
        this.store = store;
    }

    public void setErrorLabel() {
        labelToolItem.setStyleName("errorMessage");
        labelToolItem.setVisible(true);
    }

    public void setToolbarMessageLabel() {
        labelToolItem.setStyleName("toolbarMessage");
        labelToolItem.setVisible(true);
    }

    public void panelClear() {
        chart.clearState();
    }

    public GraphicReportEcoDiagramPanel(String submission) {
        setHeading("Eco Graphic Report");
        setLayout(new FitLayout());
        ToolBar toolBar = new ToolBar();
        toolBar.add(new LabelToolItem(submission));

        setTopComponent(toolBar);
    }

    public void setSubmissionText(String text) {
        labelToolItem.setLabel(text);
    }

    public void setDateFrom(String day) {
        String year = day.substring(0, 4);
        String month = day.substring(4, 6);
        String d = day.substring(6, 8);
        String hh = day.substring(8, 10);
        String mm = day.substring(10, 12);
        this.from = year + "-" + month + "-" + d + " " + hh + ":" + mm;
    }

    public void setDateTo(String day) {
        String year = day.substring(0, 4);
        String month = day.substring(4, 6);
        String d = day.substring(6, 8);
        String hh = day.substring(8, 10);
        String mm = day.substring(10, 12);
        this.to = year + "-" + month + "-" + d + " " + hh + ":" + mm;
    }

    public void setChartData(List<EcoResource> ecolist) {
        store.removeAll();
        store.add(ecolist);
        chart.setChartModel(getLineChartModel());
        chart.setBorders(true);
        setChartDataCount(ecolist.size());
        chart.refresh();
        chart.setVisible(true);
    }

    public void removeChartData() {
        store.removeAll();
        chart.setVisible(false);
    }

    public int getChartDataCount() {
        return chartDataCount;
    }

    public void setChartDataCount(int chartDataCount) {
        this.chartDataCount = chartDataCount;
    }

    public void setMetric(String metric) {

        if ("energy".equalsIgnoreCase(metric)) {
            yLegend = "Energy Efficiency (MWIPS/W)";
        } else if ("ecological".equalsIgnoreCase(metric)) {
            yLegend = "Ecological Efficiency (MWIPS/gr.CO2)";
        } else if ("performance".equalsIgnoreCase(metric)) {
            yLegend = "Performance (MWIPS)";
        } else if ("power".equalsIgnoreCase(metric)) {
            yLegend = "Power (W)";
        } else if ("co2".equalsIgnoreCase(metric)) {
            yLegend = "CO2 Emission Rate (gr.CO2/s)";
        }
    }

    public ChartModel getLineChartModel() {
        List<String> nodes = new LinkedList<String>();
        List<ScatterChart> scatters = new LinkedList<ScatterChart>();
        ChartModel cm = new ChartModel("");

        cm.setBackgroundColour("#FFFFFF");
        // Create general X- and Y-Label
        // Check Y-Label for unit!
        cm.setXLegend(new Text("From " + from + " to " + to,
                "font-size: 14px; font-family: Verdana; text-align: center;"));
        cm.setYLegend(new Text(yLegend,
                "font-size: 14px; font-family: Verdana; text-align: center;"));

        // Values should be 24*x all the time!!!
        // point 1, timestamp
        int steps = store.getCount();
        System.out.println(store.getCount() + "     " + steps);
        // Create min and max values for the y-axis
        // Labeled xAxis.... perhaps there's a better solution available
        // Label and get the values at once!
        double old_val_max = 0;
        double old_val_min = Double.MAX_VALUE;
        double max_val = 0;
        double min_val = Double.MAX_VALUE;

        // point 2
        for (int i = 0; i < store.getCount(); i++) {
            max_val = Math.max(old_val_max, Double.parseDouble(store.getAt(i).getEcoValue()));
            old_val_max = max_val;
            min_val = Math.min(old_val_min, Double.parseDouble(store.getAt(i).getEcoValue()));
            old_val_min = min_val;
            if (store.getAt(i).getNodeId() != null) {
                if (!nodes.contains(store.getAt(i).getNodeId())) {
                    ScatterChart schart = new ScatterChart(ScatterChart.ScatterStyle.LINE);
                    schart.setColour(colours[nodes.size()]);
                    String addition = "";
                    if (store.getAt(i).getNodeEco() != null) {
                        if (store.getAt(i).getMetric().equalsIgnoreCase("energy")) {
                            addition = " (Max. " + store.getAt(i).getNodeEco() + " MWIPS/W) ";
                        } else {
                            addition = " (Max. " + store.getAt(i).getNodeEco() + " MWIPS/gr.CO2) ";
                        }
                    }
                    schart.setText(store.getAt(i).getNodeId() + addition);
                    nodes.add(store.getAt(i).getNodeId());
                    scatters.add(schart);
                }
            }
        }

        //Only for non-node graphs.
        if (scatters.isEmpty()) {
            ScatterChart schart = new ScatterChart(ScatterChart.ScatterStyle.LINE);
            schart.setColour(colours[0]);
            if (store.getAt(0).getVMId() != null) {
                schart.setText(store.getAt(0).getVMId());
            } else if (store.getAt(0).getServiceId() != null) {
                schart.setText(store.getAt(0).getServiceId());
            }
            scatters.add(schart);
        }

        // If max = min, set new values to get a better visualization
        if (min_val == max_val) {
            min_val -= steps * 2;
            max_val += steps * 2;
        }

        YAxis ya = new YAxis();
        // Calculate Steps and max 
        // ya.setRange(min_val, max_val,
        // (max_val-min_val)/(store.getCount()/steps));
        ya.setRange(min_val, max_val, (max_val - min_val) / 6.0);
        ya.setGridColour("#E0E0E0");
        ya.setOffset(false);
        //cm.setYAxis(ya);

        // Chart with LineProvider
                /*
         * LineChart lchart = new LineChart(); lchart.setColour("#FF0000");
         * lchart.setAnimateOnShow(false); LineDataProvider lineProvider = new
         * LineDataProvider("ecoValue"); store.sort("timestamp",
         * Style.SortDir.ASC); lineProvider.bind(store);
         lchart.setDataProvider(lineProvider);
         */

        store.sort("timestamp", Style.SortDir.ASC);

        for (int i = 0; i < store.getCount(); i++) {
            if (store.getAt(i).getNodeId() != null) {
                scatters.get(nodes.indexOf(store.getAt(i).getNodeId())).addPoint(Double.valueOf(store.getAt(i).getTimestamp()), Double.valueOf(store.getAt(i).getEcoValue()));
            } else {
                scatters.get(0).addPoint(Double.valueOf(store.getAt(i).getTimestamp()), Double.valueOf(store.getAt(i).getEcoValue()));
            }
        }



        XAxis xa = new XAxis();
        // Steps
        steps = 24;
        xa.setSteps(24);
        Label l;
        for (int i = 0; i < steps; i++) {
            l = new Label("");
            xa.addLabels(l);
        }
        xa.setGridColour("#E0E0E0");
        xa.setOffset(false);


        cm.setXAxis(xa);
        cm.setYAxis(ya);
        for (ScatterChart schart : scatters) {
            cm.addChartConfig(schart);
        }

        return cm;

    }
}
