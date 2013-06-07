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

import java.util.ArrayList;
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
import com.google.gwt.xml.client.Element;
import com.google.gwt.xml.client.Node;
import com.google.gwt.xml.client.NodeList;

import eu.optimis.mi.gui.client.model.MonitoringResource;
import com.google.gwt.xml.client.Document;
import com.google.gwt.xml.client.XMLParser;

public class GraphicReportDiagramPanel extends ContentPanel {

	//private TextField<String> submission;
	private LabelToolItem labelToolItem = new LabelToolItem();
	private String url = "resources/chart/open-flash-chart.swf";
	private Chart chart = new Chart(url);
	//private String xmlStr;
	private int chartDataCount;
	private ListStore<MonitoringResource> store = new ListStore<MonitoringResource>();

	public GraphicReportDiagramPanel() {
		setHeading("Monitoring Graphic Report");
		setLayout(new FitLayout());
		ToolBar toolBar = new ToolBar();
		toolBar.add(labelToolItem);
		setTopComponent(toolBar);
		System.out.println("getChartDataCount:" + getChartDataCount());
		add(chart);
		chart.setVisible(false);
	}

	public ListStore<MonitoringResource> getStore() {
		return store;
	}

	public void setStore(ListStore<MonitoringResource> store) {
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

	public GraphicReportDiagramPanel(String submission) {
		setHeading("Monitoring Graphic Report");
		setLayout(new FitLayout());
		ToolBar toolBar = new ToolBar();
		toolBar.add(new LabelToolItem(submission));

		setTopComponent(toolBar);
	}

	public void setSubmissionText(String text) {
		labelToolItem.setLabel(text);
	}

	public void setChartData(List<MonitoringResource> list) {
		store.removeAll();
		store.add(list);
		chart.setChartModel(getLineChartModel());
		chart.setBorders(true);
		setChartDataCount(list.size());
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
		cm.setXLegend(new Text("Hours",
				"font-size: 14px; font-family: Verdana; text-align: center;"));
		cm.setYLegend(new Text(store.getAt(1).getMetricName(),
				"font-size: 14px; font-family: Verdana; text-align: center;"));

		// Values should be 24*x all the time!!!
		// point 1, timestamp
		double steps = store.getCount() / 24.0;
		System.out.println(store.getCount() + "     " + steps);
		// Create min and max values for the y-axis
		// Labeled xAxis.... perhaps there's a better solution available
		// Label and get the values at once!
		double old_val_max = 0;
		double old_val_min = Double.MAX_VALUE;
		double max_val = 0;
		double min_val = Double.MAX_VALUE;
		XAxis xa = new XAxis();
		// Steps
		xa.setSteps(steps);

		// point 2
		Label l;
		for (int i = 0; i < store.getCount(); i++) {
			max_val = Math.max(old_val_max,
					Double.parseDouble(store.getAt(i).getMetricValue()));
			old_val_max = max_val;
			min_val = Math.min(old_val_min,
					Double.parseDouble(store.getAt(i).getMetricValue()));
			old_val_min = min_val;
			// Again, i/Steps
			if (i == 0) {
				l = new Label("0");
				l.setSize(10);
				xa.addLabels(l);
			} else if (i == (int) (store.getCount() / 4.0)) {
				l = new Label("6");
				l.setSize(10);
				xa.addLabels(l);
			} else if (i == (int) (store.getCount() / 2.0)) {
				l = new Label("12");
				l.setSize(10);
				xa.addLabels(l);
			} else if (i == (int) (store.getCount() * 3.0 / 4.0)) {
				l = new Label("18");
				l.setSize(10);
				xa.addLabels(l);
			} else if (i == store.getCount() - 1) {
				l = new Label("24");
				l.setSize(10);
				xa.addLabels(l);
			} else {
				l = new Label("");
				xa.addLabels(l);
			}
		}
		xa.setGridColour("#E0E0E0");
		xa.setOffset(false);
		cm.setXAxis(xa);

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
		cm.setYAxis(ya);

		// Chart with LineProvider
		LineChart lchart = new LineChart();
		lchart.setColour("#FF0000");
		lchart.setAnimateOnShow(false);
		LineDataProvider lineProvider = new LineDataProvider("metric_value");
		lineProvider.bind(store);
		lchart.setDataProvider(lineProvider);
		cm.addChartConfig(lchart);

		return cm;

	}

	@SuppressWarnings("unused") //FIXME Django: remove this?
	private List<MonitoringResource> parseXML(String reqtext) {
		Document doc = XMLParser.parse(reqtext);
		doc.getDocumentElement().normalize();
		NodeList nList = doc.getElementsByTagName("monitoring_resource");

		List<MonitoringResource> list = new ArrayList<MonitoringResource>();

		for (int temp = 0; temp < nList.getLength(); temp++) {

			Node nNode = nList.item(temp);
			if (nNode.getNodeType() == Node.ELEMENT_NODE) {

				Element eElement = (Element) nNode;
				list.add(new MonitoringResource(getTagValue(
						"physical_resource_id", eElement), getTagValue(
						"resource_type", eElement), getTagValue("metric_name",
						eElement), getTagValue("metric_value", eElement)));
			}
		}

		System.out.println("Parsed list size: " + list.size());
		return list;
	}

	private String getTagValue(String sTag, Element eElement) {
		NodeList nlList = eElement.getElementsByTagName(sTag).item(0)
				.getChildNodes();
		try {
			Node nValue = (Node) nlList.item(0);
			return nValue.getNodeValue();
		} catch (NullPointerException npe) {
			return null;
		}
	}

}
