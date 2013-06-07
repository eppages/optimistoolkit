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
import com.extjs.gxt.charts.client.model.charts.AreaChart;
import com.extjs.gxt.charts.client.model.charts.LineChart;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.toolbar.LabelToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;

//additional for bars
import com.extjs.gxt.charts.client.model.BarDataProvider;
import com.extjs.gxt.charts.client.model.charts.BarChart;
import com.extjs.gxt.charts.client.model.charts.BarChart.BarStyle;

import eu.optimis.mi.gui.client.model.CostResourceIP;

public class GraphicReportCostDiagramPanel extends ContentPanel {

	//private TextField<String> submission;
	private LabelToolItem labelToolItem = new LabelToolItem();
	private String url = "resources/chart/open-flash-chart.swf";
	private Chart chart = new Chart(url);
	//private String xmlStr;
	private int chartDataCount;
	private ListStore<CostResourceIP> store = new ListStore<CostResourceIP>();
	
	public GraphicReportCostDiagramPanel() {
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

	public ListStore<CostResourceIP> getStore() {
		return store;
	}

	public void setStore(ListStore<CostResourceIP> store) {
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

	public GraphicReportCostDiagramPanel(String submission) {
		setHeading("Cost Graphic Report");
		setLayout(new FitLayout());
		ToolBar toolBar = new ToolBar();
		toolBar.add(new LabelToolItem(submission));

		setTopComponent(toolBar);
	}

	public void setSubmissionText(String text) {
		labelToolItem.setLabel(text);
	}

	public void setChartData(List<CostResourceIP> Costlist) {
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

	public ChartModel getAreaChartModel() {
		ChartModel cm = new ChartModel("");

		cm.setBackgroundColour("#FFFFFF");
		// Create general X- and Y-Label
		// Check Y-Label for unit!
		cm.setXLegend(new Text("Time",
				"font-size: 14px; font-family: Verdana; text-align: center;"));
		//FIXME CosResource need to be updated to get Currency.
		//cm.setYLegend(new Text(store.getAt(1).getCurrency(),"font-size: 14px; font-family: Verdana; text-align: center;"));
		cm.setYLegend(new Text("EUR",
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
		//xa.addLabels(new Label("james"));
		

		// point 2
		Label l;
		for (int i = 0; i < store.getCount(); i++) {
			max_val = Math.max(old_val_max,
					Double.parseDouble(store.getAt(i).getCostTotal()));
			old_val_max = max_val;
			min_val = Math.min(old_val_min,
					Double.parseDouble(store.getAt(i).getCostTotal()));
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

		AreaChart achart = new AreaChart();
	    achart.setFillAlpha(0.3f);
		achart.setText("Energy");
		achart.setColour("#660066");
	    achart.setFillColour("#660066");  
		achart.setAnimateOnShow(true);
		LineDataProvider lineProvider = new LineDataProvider("costPerWatt");
		lineProvider.bind(store);
		achart.setDataProvider(lineProvider);
		cm.addChartConfig(achart);
		
		achart = new AreaChart();
	    achart.setFillAlpha(0.3f);
		achart.setText("CPU");
		achart.setColour("#00aa00");
	    achart.setFillColour("#00aa00");  
		achart.setAnimateOnShow(true);
		lineProvider = new LineDataProvider("costPerVCPU");
		lineProvider.bind(store);
		achart.setDataProvider(lineProvider);
		cm.addChartConfig(achart);
		
		achart = new AreaChart();
	    achart.setFillAlpha(0.3f);
		achart.setText("Memory");
		achart.setColour("#0000cc");
	    achart.setFillColour("#0000cc");  
		achart.setAnimateOnShow(true);
		lineProvider = new LineDataProvider("costPerMBMemory");
		lineProvider.bind(store);
		achart.setDataProvider(lineProvider);
		cm.addChartConfig(achart);
		
		achart = new AreaChart();
	    achart.setFillAlpha(0.3f);
		achart.setText("Storage");
		achart.setColour("#ff6600");
	    achart.setFillColour("#ff6600");  
		achart.setAnimateOnShow(true);
		lineProvider = new LineDataProvider("costPerGBStorage");
		lineProvider.bind(store);
		achart.setDataProvider(lineProvider);
		cm.addChartConfig(achart);
		
		achart = new AreaChart();
	    achart.setFillAlpha(0.3f);
		achart.setText("Upstream");
		achart.setColour("#FF0000");
	    achart.setFillColour("#FF0000");  
		achart.setAnimateOnShow(true);
		lineProvider = new LineDataProvider("costPerGBUploaded");
		lineProvider.bind(store);
		achart.setDataProvider(lineProvider);
		cm.addChartConfig(achart);
		
		achart = new AreaChart();
	    achart.setFillAlpha(0.3f);
		achart.setText("Downstream");
		achart.setColour("#6633FF");
	    achart.setFillColour("#6633FF");  
		achart.setAnimateOnShow(true);
		lineProvider = new LineDataProvider("costPerGBDownloaded");
		lineProvider.bind(store);
		achart.setDataProvider(lineProvider);
		cm.addChartConfig(achart);

		achart = new AreaChart();
	    achart.setFillAlpha(0.3f);
	    achart.setText("Total Cost");
		achart.setColour("#FF0000");
	    achart.setFillColour("#ff0000");  
		achart.setAnimateOnShow(true);
		lineProvider = new LineDataProvider("costTotal");
		lineProvider.bind(store);
		achart.setDataProvider(lineProvider);
		cm.addChartConfig(achart);

		return cm;

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
		cm.setYLegend(new Text("EUR",
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
		xa.addLabels(new Label("Time"));
		

		// point 2
		Label l;
		for (int i = 0; i < store.getCount(); i++) {
			max_val = Math.max(old_val_max,
					Double.parseDouble(store.getAt(i).getCostTotal()));
			old_val_max = max_val;
			min_val = Math.min(old_val_min,
					Double.parseDouble(store.getAt(i).getCostTotal()));
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
		lchart.setColour("#00aa00");
		lchart.setText("CPU");
		lchart.setAnimateOnShow(true);
		LineDataProvider lineProvider = new LineDataProvider("costPerVCPU");
		lineProvider.bind(store);
		lchart.setDataProvider(lineProvider);
		cm.addChartConfig(lchart);
		
		lchart = new LineChart();
		lchart.setColour("#0000cc");
		lchart.setText("Memory");
		lchart.setAnimateOnShow(true);
		lineProvider = new LineDataProvider("costPerMBMemory");
		lineProvider.bind(store);
		lchart.setDataProvider(lineProvider);
		cm.addChartConfig(lchart);
		
		lchart = new LineChart();
		lchart.setColour("#ff6600");
		lchart.setText("Storage");
		lchart.setAnimateOnShow(true);
		lineProvider = new LineDataProvider("costPerGBStorage");
		lineProvider.bind(store);
		lchart.setDataProvider(lineProvider);
		cm.addChartConfig(lchart);
	    
		lchart = new LineChart();
		lchart.setColour("#FF0000");
		lchart.setText("Upstream");
		lchart.setAnimateOnShow(true);
		lineProvider = new LineDataProvider("costPerGBUploaded");
		lineProvider.bind(store);
		lchart.setDataProvider(lineProvider);
		cm.addChartConfig(lchart);
		
		lchart = new LineChart();
		lchart.setColour("#6633FF");
		lchart.setText("Downstream");
		lchart.setAnimateOnShow(true);
		lineProvider = new LineDataProvider("costPerGBDownloaded");
		lineProvider.bind(store);
		lchart.setDataProvider(lineProvider);
		cm.addChartConfig(lchart);
		
		lchart = new LineChart();
		lchart.setColour("#660066");
		lchart.setText("Energy");
		lchart.setAnimateOnShow(true);
		lineProvider = new LineDataProvider("costPerWatt");
		lineProvider.bind(store);
		lchart.setDataProvider(lineProvider);
		cm.addChartConfig(lchart);
		
		lchart = new LineChart();
		lchart.setColour("#FF0000");
		lchart.setText("Total Cost");
		lchart.setAnimateOnShow(true);
		lineProvider = new LineDataProvider("costTotal");
		lineProvider.bind(store);
		lchart.setDataProvider(lineProvider);
		cm.addChartConfig(lchart);

		return cm;

	}
	
	public ChartModel getBarChartModel() {
		
		ChartModel cm = new ChartModel("");

		cm.setBackgroundColour("#FFFFFF");
		// Create general X- and Y-Label
		// Check Y-Label for unit!
		cm.setXLegend(new Text("Time",
		"font-size: 14px; font-family: Verdana; text-align: center;"));
		//FIXME CosResource need to be updated to get Currency.
		//cm.setYLegend(new Text(store.getAt(1).getCurrency(),"font-size: 14px; font-family: Verdana; text-align: center;"));
		cm.setYLegend(new Text("EUR",
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
		xa.addLabels(new Label("james"));
		

		// point 2
		Label l;
		for (int i = 0; i < store.getCount(); i++) {
			max_val = Math.max(old_val_max,
					Double.parseDouble(store.getAt(i).getCostTotal()));
			old_val_max = max_val;
			min_val = Math.min(old_val_min,
					Double.parseDouble(store.getAt(i).getCostTotal()));
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
		
		BarChart bar = new BarChart(BarStyle.NORMAL);
		
		bar.setColour("00aa00");
		BarDataProvider barProvider = new BarDataProvider("costPerVCPU");
		bar.setText("CPU");
		barProvider.bind(store);
		bar.setDataProvider(barProvider);
		cm.addChartConfig(bar);	
		
		bar = new BarChart(BarStyle.NORMAL);  
	    bar.setColour("#0000cc");
	    bar.setText("Memory");
	    barProvider = new BarDataProvider("costPerMBMemory");  
	    barProvider.bind(store);  
	    bar.setDataProvider(barProvider);  
	    cm.addChartConfig(bar);  
	    
	    bar = new BarChart(BarStyle.NORMAL);  
	    bar.setColour("#ff6600");  
	    bar.setText("Storage");
	    barProvider = new BarDataProvider("costPerGBStorage", "test", "test");  
	    barProvider.bind(store);  
	    bar.setDataProvider(barProvider);  
	    cm.addChartConfig(bar);  
	    
	    bar = new BarChart(BarStyle.NORMAL);  
	    bar.setColour("#FF0000");  
	    bar.setText("Upstream");
	    barProvider = new BarDataProvider("costPerGBUploaded", "test", "test");  
	    barProvider.bind(store);  
	    bar.setDataProvider(barProvider);  
	    cm.addChartConfig(bar);  
	    
	    bar = new BarChart(BarStyle.NORMAL);  
	    bar.setColour("#6633FF"); 
	    bar.setText("Downstream");
	    barProvider = new BarDataProvider("costPerGBDownloaded");  
	    barProvider.bind(store);  
	    bar.setDataProvider(barProvider);  
	    cm.addChartConfig(bar);  
	    
	    bar = new BarChart(BarStyle.NORMAL);  
	    bar.setColour("#660066");  
	    bar.setText("Energy");
	    barProvider = new BarDataProvider("costPerWatt");  
	    barProvider.bind(store);  
	    bar.setDataProvider(barProvider);  
	    cm.addChartConfig(bar);  

		// Chart with LineProvider
		LineChart lchart = new LineChart();
		lchart.setColour("#FF0000");
		lchart.setText("Total Cost");
		lchart.setAnimateOnShow(true);
		LineDataProvider lineProvider = new LineDataProvider("costTotal");
		lineProvider.bind(store);
		lchart.setDataProvider(lineProvider);
		cm.addChartConfig(lchart);

		return cm;

	}
	
	



	
	
}
