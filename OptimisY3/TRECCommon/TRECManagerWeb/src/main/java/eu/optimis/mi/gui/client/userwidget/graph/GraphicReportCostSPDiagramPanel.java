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
import com.extjs.gxt.charts.client.model.LineDataProvider;
import com.extjs.gxt.charts.client.model.Text;
import com.extjs.gxt.charts.client.model.axis.Label;
import com.extjs.gxt.charts.client.model.axis.XAxis;
import com.extjs.gxt.charts.client.model.axis.YAxis;
import com.extjs.gxt.charts.client.model.charts.LineChart;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.toolbar.LabelToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar; 

import eu.optimis.mi.gui.client.model.CostResourceSP;

public class GraphicReportCostSPDiagramPanel extends ContentPanel {

	private LabelToolItem labelToolItem = new LabelToolItem();
	private String url = "resources/chart/open-flash-chart.swf";
	private Chart chart = new Chart(url);
	private int chartDataCount;
	private ListStore<CostResourceSP> store = new ListStore<CostResourceSP>();
	
	public GraphicReportCostSPDiagramPanel() {
		System.out.println(this.getClass().getName());
		setHeading("Cost Graphic Report");
		setLayout(new FitLayout());
		ToolBar toolBar = new ToolBar();
		toolBar.add(labelToolItem);
		setTopComponent(toolBar);
		System.out.println("getChartDataCount:" + getChartDataCount());
		add(chart);
		chart.setVisible(false);
	}

	public ListStore<CostResourceSP> getStore() {
		return store;
	}

	public void setStore(ListStore<CostResourceSP> store) {
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

	public GraphicReportCostSPDiagramPanel(String submission) {
		setHeading("Cost Graphic Report");
		setLayout(new FitLayout());
		ToolBar toolBar = new ToolBar();
		toolBar.add(new LabelToolItem(submission));

		setTopComponent(toolBar);
	}

	public void setSubmissionText(String text) {
		labelToolItem.setLabel(text);
	}

	public void setChartData(List<CostResourceSP> Costlist) {
		store.removeAll();
		store.add(Costlist);
		chart.setChartModel(getLineChartModel());
		//chart.setChartModel(getBarChartModel());
		//chart.setChartModel(getAreaChartModel());
		chart.setBorders(true);
		setChartDataCount(Costlist.size());
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


	public ChartModel getLineChartModel() {
		ChartModel cm = new ChartModel("");

		cm.setBackgroundColour("#FFFFFF");
		// Create general X- and Y-Label
		// Check Y-Label for unit!
		cm.setXLegend(new Text("Last records in time",
		"font-size: 14px; font-family: Verdana; text-align: center;"));
		//FIXME CosResource need to be updated to get Currency.
		//cm.setYLegend(new Text(store.getAt(1).getCurrency(),"font-size: 14px; font-family: Verdana; text-align: center;"));
		cm.setYLegend(new Text("SP Rank (1-5)",
		"font-size: 14px; font-family: Verdana; text-align: center;"));
		
		// Values should be 24*x all the time!!!
		// point 1, timestamp
		double steps = store.getCount()/25.0;
		System.out.println(store.getCount() + "     " + steps);
		// Create min and max values for the y-axis
		// Labeled xAxis.... perhaps there's a better solution available
		// Label and get the values at once!
		double old_val_max = 0;
		double old_val_min = Double.MAX_VALUE;
		double max_val = 0;
		double min_val = Double.MIN_VALUE;
		XAxis xa = new XAxis();
		// Steps
		xa.setSteps(steps);
		xa.addLabels(new Label("Records"));
		

		// point 2
		Label l;
		for (int i = 0; i < store.getCount(); i++) {
			max_val = Math.max(old_val_max,
					Double.parseDouble("6"));
			old_val_max = max_val;
			min_val = Math.min(old_val_min,
					Double.parseDouble("6"));
			old_val_min = min_val;
			// Again, i/Steps
			if (i == 0) {
				l = new Label("0");
				l.setSize(10);
				xa.addLabels(l);
			} else if (i == (int) (store.getCount() / 4.0)) {
				l = new Label("25");
				l.setSize(10);
				xa.addLabels(l);
			} else if (i == (int) (store.getCount() / 2.0)) {
				l = new Label("50");
				l.setSize(10);
				xa.addLabels(l);
			} else if (i == (int) (store.getCount() * 3.0  / 4.0)) { 	
				l = new Label("75");
				l.setSize(10);
				xa.addLabels(l);
			} else if (i == store.getCount() - 1) {
				l = new Label("100");
				l.setSize(10);
				xa.addLabels(l);
			} else {
				l = new Label("");
				xa.addLabels(l);
			}
		}
		xa.setGridColour("#E0E0E0");
		xa.setOffset(false);
		xa.setRange(0, store.getCount());
		cm.setXAxis(xa);

		// If max = min, set new values to get a better visualization
		/*
		if (min_val == max_val) {
			min_val -= steps * 2;
			max_val += steps * 2;
		}*/

		YAxis ya = new YAxis();
		// Calculate Steps and max 
		// ya.setRange(min_val, max_val,
		// (max_val-min_val)/(store.getCount()/steps));
		
		ya.setRange(0, max_val);//, (max_val - min_val) / 100.0);
		ya.setGridColour("#E0E0E0");
		ya.setOffset(false);
		cm.setYAxis(ya);
				
		LineChart lchart = new LineChart();
		lchart.setColour("#FF0000");
		lchart.setText("PlanCAP");
		lchart.setAnimateOnShow(true);
		LineDataProvider lineProvider = new LineDataProvider("plancap");
		lineProvider.bind(store);
		lchart.setDataProvider(lineProvider);
		cm.addChartConfig(lchart);
		
		lchart = new LineChart();
		lchart.setColour("#660066");
		lchart.setText("Average");
		lchart.setAnimateOnShow(true);
		lineProvider = new LineDataProvider("average");
		lineProvider.bind(store);
		lchart.setDataProvider(lineProvider);
		cm.addChartConfig(lchart);

		return cm;

	}
	



	
	
}
