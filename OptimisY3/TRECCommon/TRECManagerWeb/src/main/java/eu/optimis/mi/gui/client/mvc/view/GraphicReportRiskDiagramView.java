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
package eu.optimis.mi.gui.client.mvc.view;

import java.util.ArrayList;
import java.util.List;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.extjs.gxt.ui.client.mvc.AppEvent;
import com.extjs.gxt.ui.client.mvc.Controller;
import com.extjs.gxt.ui.client.mvc.View;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.layout.BorderLayout;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;

import eu.optimis.mi.gui.client.MainEvents;
import eu.optimis.mi.gui.client.mvc.MainView;
import eu.optimis.mi.gui.client.userwidget.graph.GraphicReportRiskDiagramPanel;
import eu.optimis.mi.gui.client.model.RiskResource;

import com.allen_sauer.gwt.log.client.Log;

public class GraphicReportRiskDiagramView extends View {

	
	private LayoutContainer container;
	private GraphicReportRiskDiagramPanel graphicReportRiskDiagramPanel;
	
	public GraphicReportRiskDiagramView(Controller controller) {
		super(controller);
	}
	
	@Override
	protected void handleEvent(AppEvent event) {
		if (event.getType() == MainEvents.ReportGraphicRiskDiagram) {
			Log.debug("Risk Report: MainEvents.ReportGraphicRiskDiagram detected by event handler");
			
			List<Object> eventDataList = event.getData();
			String message = (String) eventDataList.get(0);
			LayoutContainer wrapper = (LayoutContainer) Registry
					.get(MainView.CENTER_PANEL);
			wrapper.removeAll();
			graphicReportRiskDiagramPanel.setSubmissionText(message);
			
			if (eventDataList.size() > 1) {
				Log.debug("Risk Report: EventDataList contains a list of RiskResources");
				graphicReportRiskDiagramPanel.setToolbarMessageLabel();
				List<?> list = (List<?>) eventDataList.get(1);
				List<RiskResource> riskResourceList = new ArrayList<RiskResource>();
				for (int i = 0; i < list.size(); i++) {
					riskResourceList.add((RiskResource) list.get(i));
				}
				Log.debug("Risk Report: Setting chart data using list of RiskResources");
				graphicReportRiskDiagramPanel.setChartData(riskResourceList);
			}
			else{
				Log.debug("Risk Report: EventDataList only contains an error message!");
				graphicReportRiskDiagramPanel.setErrorLabel();
				graphicReportRiskDiagramPanel.removeChartData();
			}
			wrapper.add(container);
			wrapper.layout();
			return;
		}

		if (event.getType() == MainEvents.ReportGraphicCancel) {
			Log.debug("Risk Report: MainEvents.ReportGraphicCancel detected by event handler");
			graphicReportRiskDiagramPanel.removeChartData();
			graphicReportRiskDiagramPanel.setSubmissionText("");
			return;
		}
	}

	@Override
	protected void initialize() {
		Log.debug("Risk Report: Initializing GraphicReportRiskDiagramView");
		container = new LayoutContainer();
		BorderLayout layout = new BorderLayout();
		layout.setEnableState(false);
		container.setLayout(layout);
		graphicReportRiskDiagramPanel = new GraphicReportRiskDiagramPanel();
		container.add(graphicReportRiskDiagramPanel, new BorderLayoutData(LayoutRegion.CENTER));
	}
	
}
