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

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.mvc.AppEvent;
import com.extjs.gxt.ui.client.mvc.Controller;
import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.extjs.gxt.ui.client.mvc.View;
import com.extjs.gxt.ui.client.store.TreeStore;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.layout.AccordionLayout;
import com.extjs.gxt.ui.client.widget.layout.FlowData;
import com.extjs.gxt.ui.client.widget.treepanel.TreePanel;

import eu.optimis.mi.gui.client.MainEvents;
import eu.optimis.mi.gui.client.model.MonitoringLevel;
import eu.optimis.mi.gui.client.mvc.MainView;
import eu.optimis.mi.gui.client.resources.OptimisResource;

public class LatestReportView extends View {

	private ContentPanel latestReport;
	private TreeStore<MonitoringLevel> store;
	private TreePanel<MonitoringLevel> tree;

	public LatestReportView(Controller controller) {
		super(controller);
	}

	@Override
	protected void handleEvent(AppEvent event) {
		if (event.getType() == MainEvents.Init) {
			initUI();
		}
	}

	@Override
	protected void initialize() {
	}

	private void initUI() {
		ContentPanel west = (ContentPanel) Registry.get(MainView.WEST_PANEL);
		west.setLayout(new AccordionLayout());
		latestReport = new ContentPanel();
		latestReport.setAnimCollapse(false);
		latestReport.setHeading("Latest Report");
		
		latestReport.addListener(Events.Expand, new Listener<ComponentEvent>() {
			public void handleEvent(ComponentEvent be) {
				LayoutContainer wrapper = (LayoutContainer) Registry
						.get(MainView.CENTER_PANEL);
				wrapper.removeAll();
				return;
			}
		});
		
		store = new TreeStore<MonitoringLevel>();
		MonitoringLevel parent = new MonitoringLevel("Monitoring Levels", null);
		store.add(parent, true);
		store.add(parent, OptimisResource.getMonitoringLevels(), false);
		tree = new TreePanel<MonitoringLevel>(store);
		tree.setDisplayProperty("name");
		tree.setWidth(300);
		tree.setAutoLoad(true);
		tree.setAutoExpand(true);

		SelectionChangedListener<MonitoringLevel> scl = new SelectionChangedListener<MonitoringLevel>() {
			@Override
			public void selectionChanged(
					SelectionChangedEvent<MonitoringLevel> se) {
				MonitoringLevel f = (MonitoringLevel) se.getSelection().get(0);
				//AppEvent evt = null;
				if (f.getName().contains("service")) {
					Dispatcher.get().dispatch(MainEvents.ReportServiceIerms);
				} else if (f.getName().contains("virtual")) {
					Dispatcher.get().dispatch(MainEvents.ReportVirtualIerms);
				} else if (f.getName().contains("physical")) {
					Dispatcher.get().dispatch(MainEvents.ReportPhysicalIerms);
				} else if (f.getName().contains("energy")) {
					Dispatcher.get().dispatch(MainEvents.ReportEnergyIerms);
				}
			}

		};
		tree.getSelectionModel().addSelectionChangedListener(scl);
		latestReport.add(tree, new FlowData(10));
		west.add(latestReport);
	}
}
