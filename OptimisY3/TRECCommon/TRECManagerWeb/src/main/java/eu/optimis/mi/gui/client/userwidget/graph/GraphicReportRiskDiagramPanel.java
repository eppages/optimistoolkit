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

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import com.allen_sauer.gwt.log.client.Log;
import com.extjs.gxt.charts.client.Chart;
import com.extjs.gxt.charts.client.model.ChartModel;
import com.extjs.gxt.charts.client.model.Text;
import com.extjs.gxt.charts.client.model.axis.XAxis;
import com.extjs.gxt.charts.client.model.axis.YAxis;
import com.extjs.gxt.charts.client.model.charts.BarChart.Bar;
import com.extjs.gxt.charts.client.model.charts.ScatterChart;
import com.extjs.gxt.charts.client.model.charts.BarChart;
import com.extjs.gxt.charts.client.model.charts.BarChart.BarStyle;
import com.extjs.gxt.ui.client.Style;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.toolbar.LabelToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.i18n.client.DateTimeFormat;

import eu.optimis.mi.gui.client.model.RiskResource;

public class GraphicReportRiskDiagramPanel extends ContentPanel {

	private ToolBar toolBar;
	private LabelToolItem labelToolItem = new LabelToolItem();
	private String url = "resources/chart/open-flash-chart.swf";
	private Chart chart = new Chart(url);
	private ListStore<RiskResource> riskResourceListStore = new ListStore<RiskResource>();
	private String from;
	private String to;
	private static final String colours[] = { "#FF0000", "#329600", "#0000FF",
			"#A500A5", "#00FFFF", "#FF00FF" };

	// Draws the content pane for risk
	public GraphicReportRiskDiagramPanel() {

		Log.debug("Risk Report: Inside GraphicReportRiskDiagramPanel()");

		setHeading("Risk Reports");
		setCollapsible(false);
		setExpanded(true);
		setLayout(new FitLayout());
		setScrollMode(Scroll.AUTOY);

		toolBar = new ToolBar();
		toolBar.add(labelToolItem);
		setTopComponent(toolBar);
		add(chart);
		chart.setVisible(false);
	}

	public void setErrorLabel() {
		labelToolItem.setStyleName("errorMessage");
		labelToolItem.setVisible(true);
	}

	public void setToolbarMessageLabel() {
		labelToolItem.setStyleName("toolbarMessage");
		labelToolItem.setVisible(true);
	}

	public void setSubmissionText(String text) {
		if (text.equals("")) {
			labelToolItem.setStyleName("toolbarMessage");
			labelToolItem.setLabel("Successfully cleared graph");
		} else {
			labelToolItem.setLabel(text);
		}
	}

	// This is where we store the data to render
	public void setChartData(List<RiskResource> riskResourceList) {
		Log.debug("Risk Report: riskResourceList size is: "
				+ riskResourceList.size());
		riskResourceListStore.removeAll();
		riskResourceListStore.add(riskResourceList);
		chart.setChartModel(getChartModel());
		chart.setBorders(true);
		chart.refresh();
		chart.setVisible(true);
	}

	public void removeChartData() {
		riskResourceListStore.removeAll();
		chart.setVisible(false);
	}

	public ChartModel getChartModel() {
		List<ScatterChart> scatterChartList = new LinkedList<ScatterChart>();
		List<BarChart> barChartChartList = new LinkedList<BarChart>();

		DateTimeFormat dtFormat = DateTimeFormat
				.getFormat("dd-MM-yyyy HH:mm:ss");

		// Set up chart
		ChartModel chartModel = new ChartModel();
		chartModel.setBackgroundColour("#FFFFFF");
		chartModel.setThousandSeparatorDisabled(true);

		// Check which graphs we are going to render
		String providerType = riskResourceListStore.getAt(0).getProviderType();
		String servicePhase = riskResourceListStore.getAt(0).getServicePhase();

		// X-Axis
		XAxis xa = new XAxis();
		xa.setGridColour("#E0E0E0");
		xa.setOffset(false);

		if (providerType == "sp") {
			// Service Provider graphs Series

			if (servicePhase == "deployment") {
				// Series data for Service Provider during deployment

				// Setup chart title
				String providerId = riskResourceListStore.getAt(0)
						.getProviderId();
				String serviceId = riskResourceListStore.getAt(0)
						.getServiceId();
				chartModel.setTitle(new Text(
						"Risk Report - Service Provider (ID: " + providerId
								+ ") Phase: " + servicePhase + " Service ID: "
								+ serviceId));

				// Series: DS-AHP
				BarChart barChart = new BarChart(BarStyle.GLASS);
				barChart.setColour(colours[0]);
				barChart.setText("Relative IP Risk Level");
				barChartChartList.add(barChart);
				// Series: APOF of SLA returned from IP
				barChart = new BarChart(BarStyle.GLASS);
				barChart.setColour(colours[1]);
				barChart.setText("Normalised SLA Risk Level");
				barChartChartList.add(barChart);

				// Sort by time
				riskResourceListStore.sort("timeStamp", Style.SortDir.ASC);

				// Setup X-Axis
				xa.setOffset(true);

				// Setup variables for general X-Axis Label
				from = dtFormat.format(new Date(
						Long.valueOf(riskResourceListStore.getAt(1)
								.getTimeStamp()) * 1000));
				to = dtFormat.format(new Date(Long
						.valueOf(riskResourceListStore.getAt(
								riskResourceListStore.getCount() - 1)
								.getTimeStamp()) * 1000));

				// Add the data to the Bar series here
				for (int i = 1; i < riskResourceListStore.getCount(); i++) {
					Long timeStamp = Long.valueOf(riskResourceListStore
							.getAt(i).getTimeStamp());
					Double riskValue = Double.valueOf(riskResourceListStore
							.getAt(i).getRiskValue());

					if (riskResourceListStore.getAt(i).getGraphType()
							.equals("1")) { // Relative
						barChartChartList.get(0).addBars(new Bar(riskValue));
						xa.addLabels(Long.toString(timeStamp)); // Might not be
																// equal to
																// bellow but
																// close enough
					} else if (riskResourceListStore.getAt(i).getGraphType()
							.equals("2")) { // Normalised
						barChartChartList.get(1).addBars(new Bar(riskValue));
					} else {
						Log.debug("Risk Report: Unknown graphType for Service Provider graph series");
						break;
					}
				}

			} else if (servicePhase == "operation") {
				// Series data for Service Provider during operation

				// Setup chart title
				String providerId = riskResourceListStore.getAt(0)
						.getProviderId();
				String serviceId = riskResourceListStore.getAt(0)
						.getServiceId();
				chartModel.setTitle(new Text(
						"Risk Report - Service Provider (ID: " + providerId
								+ ") Phase: " + servicePhase + " Service ID: "
								+ serviceId));

				// Series: SLA
				ScatterChart schart = new ScatterChart(
						ScatterChart.ScatterStyle.LINE);
				schart.setColour(colours[0]);
				schart.setText("Service Level Agreement Risk Level");
				scatterChartList.add(schart);

				// Sort by time
				riskResourceListStore.sort("timeStamp", Style.SortDir.ASC);

				// Setup X-Axis
				Long fromLong = Long.parseLong(riskResourceListStore.getAt(1)
						.getTimeStamp());
				Long toLong = Long.parseLong(riskResourceListStore.getAt(
						riskResourceListStore.getCount() - 1).getTimeStamp());
				xa.setRange(fromLong, toLong,
						Math.floor((toLong - fromLong) / 4));
				Log.debug("Risk Report: X-Axis Steps set to " + xa.getSteps());
				xa.setOffset(true);

				// Setup variables for general X-Axis Label
				from = dtFormat.format(new Date(
						Long.valueOf(riskResourceListStore.getAt(1)
								.getTimeStamp()) * 1000));
				to = dtFormat.format(new Date(Long
						.valueOf(riskResourceListStore.getAt(
								riskResourceListStore.getCount() - 1)
								.getTimeStamp()) * 1000));

				// Add the data to the scatter series here
				for (int i = 1; i < riskResourceListStore.getCount(); i++) {

					Double timeStamp = Double.valueOf(riskResourceListStore
							.getAt(i).getTimeStamp());
					Double riskValue = Double.valueOf(riskResourceListStore
							.getAt(i).getRiskValue());

					if (riskResourceListStore.getAt(i).getGraphType()
							.equals("1")) { // SLA
						scatterChartList.get(0).addPoint(timeStamp, riskValue);

					} else {
						Log.debug("Risk Report: Unknown graphType for Service Provider graph series");
						break;
					}
				}

			} else {
				Log.debug("Risk Report: Unknown servicePhase for Infrastructure Provider graph series");
			}

		} else if (providerType == "ip") {
			// Infrastructure Provider Graph Series

			if (servicePhase == "deployment") {
				// Series data for Infrastructure Provider during operation

				// Setup chart title
				String providerId = riskResourceListStore.getAt(0)
						.getProviderId();
				String serviceId = riskResourceListStore.getAt(0)
						.getServiceId();
				chartModel.setTitle(new Text(
						"Risk Report - Infrastructure Provider (ID: "
								+ providerId + ") Phase: " + servicePhase
								+ " Service ID: " + serviceId));

				BarChart barChart = new BarChart(BarStyle.GLASS);

				// Series: SLA
				barChart = new BarChart(BarStyle.GLASS);
				barChart.setColour(colours[1]);
				barChart.setText("SLA Risk Level");
				barChartChartList.add(barChart);

				// Sort by time
				riskResourceListStore.sort("timeStamp", Style.SortDir.ASC);

				// Setup X-Axis
				xa.setOffset(true);

				// Setup variables for general X-Axis Label
				from = dtFormat.format(new Date(
						Long.valueOf(riskResourceListStore.getAt(1)
								.getTimeStamp()) * 1000));
				to = dtFormat.format(new Date(Long
						.valueOf(riskResourceListStore.getAt(
								riskResourceListStore.getCount() - 1)
								.getTimeStamp()) * 1000));

				// Add the data to the Bar series here
				for (int i = 1; i < riskResourceListStore.getCount(); i++) {
					Long timeStamp = Long.valueOf(riskResourceListStore
							.getAt(i).getTimeStamp());
					Double riskValue = Double.valueOf(riskResourceListStore
							.getAt(i).getRiskValue());

					if (riskResourceListStore.getAt(i).getGraphType()
							.equals("1")) { // SLA
						barChartChartList.get(0).addBars(new Bar(riskValue));
						xa.addLabels(Long.toString(timeStamp));
					} else {
						Log.debug("Risk Report: Unknown graphType for Service Provider graph series");
						break;
					}
				}

			} else if (servicePhase == "operation") {
				// Series data for Infrastructure Provider during operation

				// Setup chart title
				String providerId = riskResourceListStore.getAt(0)
						.getProviderId();
				String serviceId = riskResourceListStore.getAt(0)
						.getServiceId();
				chartModel.setTitle(new Text(
						"Risk Report - Infrastructure Provider (ID: "
								+ providerId + ") Phase: " + servicePhase
								+ " Service ID: " + serviceId));

				// Series: Physical Host
				ScatterChart schart = new ScatterChart(
						ScatterChart.ScatterStyle.LINE);
				schart.setColour(colours[0]);
				schart.setText("Physical Host Risk Level");
				scatterChartList.add(schart);
				// Series: Virtual Machine
				schart = new ScatterChart(ScatterChart.ScatterStyle.LINE);
				schart.setColour(colours[1]);
				schart.setText("Virtual Machine Risk Level");
				scatterChartList.add(schart);
				// Series: SLA
				schart = new ScatterChart(ScatterChart.ScatterStyle.LINE);
				schart.setColour(colours[2]);
				schart.setText("Service Level Agreement Risk Level");
				scatterChartList.add(schart);
				// Series: IP
				schart = new ScatterChart(ScatterChart.ScatterStyle.LINE);
				schart.setColour(colours[3]);
				schart.setText("Infrastructure Provider Risk Level");
				scatterChartList.add(schart);

				// Sort by time
				riskResourceListStore.sort("timeStamp", Style.SortDir.ASC);

				// Setup X-Axis
				Long fromLong = Long.parseLong(riskResourceListStore.getAt(1)
						.getTimeStamp());
				Long toLong = Long.parseLong(riskResourceListStore.getAt(
						riskResourceListStore.getCount() - 1).getTimeStamp());
				xa.setRange(fromLong, toLong,
						Math.floor((toLong - fromLong) / 4));
				Log.debug("Risk Report: X-Axis Steps set to " + xa.getSteps());
				xa.setOffset(true);

				// Setup variables for general X-Axis Label
				from = dtFormat.format(new Date(
						Long.valueOf(riskResourceListStore.getAt(1)
								.getTimeStamp()) * 1000));
				to = dtFormat.format(new Date(Long
						.valueOf(riskResourceListStore.getAt(
								riskResourceListStore.getCount() - 1)
								.getTimeStamp()) * 1000));

				// Add the data to the scatter series here
				for (int i = 1; i < riskResourceListStore.getCount(); i++) {

					Double timeStamp = Double.valueOf(riskResourceListStore
							.getAt(i).getTimeStamp());
					Double riskValue = Double.valueOf(riskResourceListStore
							.getAt(i).getRiskValue());

					if (riskResourceListStore.getAt(i).getGraphType()
							.equals("1")) { // Physical
						scatterChartList.get(0).addPoint(timeStamp, riskValue);
					} else if (riskResourceListStore.getAt(i).getGraphType()
							.equals("2")) { // Virtual Machine
						scatterChartList.get(1).addPoint(timeStamp, riskValue);
					} else if (riskResourceListStore.getAt(i).getGraphType()
							.equals("3")) { // SLA
						scatterChartList.get(2).addPoint(timeStamp, riskValue);
					} else if (riskResourceListStore.getAt(i).getGraphType()
							.equals("4")) { // IP
						scatterChartList.get(3).addPoint(timeStamp, riskValue);
					} else {
						Log.debug("Risk Report: Unknown graphType for Infrastructure Provider graph series");
						break;
					}
				}
			} else {
				Log.debug("Risk Report: Unknown servicePhase for Infrastructure Provider graph series");
			}

		} else {
			Log.debug("Risk Report: Unknown providerType");
		}

		// Create general X-Axis and Y-Axis labels
		chartModel.setXLegend(new Text("Unix Timestamp - Samples From " + from
				+ " to " + to,
				"font-size: 14px; font-family: Verdana; text-align: center;"));
		chartModel.setYLegend(new Text("Risk Level",
				"font-size: 14px; font-family: Verdana; text-align: center;"));

		// Y-Axis
		YAxis ya = new YAxis();
		// Calculate interval and max
		ya.setRange(0, 7, 1);
		ya.setGridColour("#E0E0E0");
		ya.setOffset(false);

		// Set axis
		chartModel.setXAxis(xa);
		chartModel.setYAxis(ya);

		// Add the chart series to the ChartModel
		for (ScatterChart schart : scatterChartList) {
			chartModel.addChartConfig(schart);
		}

		for (BarChart barChart : barChartChartList) {
			chartModel.addChartConfig(barChart);
		}

		return chartModel;
	}
}
