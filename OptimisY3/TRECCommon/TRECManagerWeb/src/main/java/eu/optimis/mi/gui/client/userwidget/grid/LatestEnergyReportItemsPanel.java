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
package eu.optimis.mi.gui.client.userwidget.grid;

import java.util.List;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.util.IconHelper;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.toolbar.LabelToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.user.client.rpc.AsyncCallback;

import eu.optimis.mi.gui.client.MainEvents;
import eu.optimis.mi.gui.client.MonitoringManagerWebServiceAsync;
import eu.optimis.mi.gui.client.model.MonitoringResource;
import eu.optimis.mi.gui.client.resources.OptimisResource;


public class LatestEnergyReportItemsPanel extends ContentPanel {

	private MonitoringManagerWebServiceAsync service;
	private ListStore<MonitoringResource> store;
	private Grid<MonitoringResource> grid;
	private TextField<String> id;
	private ColumnModel cm;

	public LatestEnergyReportItemsPanel() {
		setHeading("Energy Level Monitoring");
		setLayout(new FitLayout());

		ToolBar toolBar = new ToolBar();
		Button reflesh = new Button("Refresh");
		reflesh.setIcon(IconHelper.createStyle("icon-email-add"));
		id = new TextField<String>();
		id.setFieldLabel("Physical ID");
		id.setAllowBlank(false);
		toolBar.add(new LabelToolItem("Physical ID: "));
		toolBar.add(id);
		toolBar.add(reflesh);
		setTopComponent(toolBar);
		reflesh.addSelectionListener(new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent ce) {
				String physicalId = id.getValue();
				service = (MonitoringManagerWebServiceAsync) Registry
						.get("guiservice");
				service.getMonitoringResources("energy", physicalId,
						new AsyncCallback<List<MonitoringResource>>() {
							public void onFailure(Throwable caught) {
								Dispatcher.forwardEvent(MainEvents.Error,
										caught);
							}

							public void onSuccess(
									List<MonitoringResource> result) {
								getStore().removeAll();
								if (result.size() > 0)
									store.add(result);
							}
						});
			}
		});
		
		cm = new ColumnModel(OptimisResource.getColumnConfig());
		store = new ListStore<MonitoringResource>();
		grid = new Grid<MonitoringResource>(store, cm);
		grid.setTitle(" Energy Level Resources ");
		grid.setBorders(true);
		grid.getView().setForceFit(true);
		add(grid);
	}

	public Grid<MonitoringResource> getGrid() {
		return grid;
	}

	public ListStore<MonitoringResource> getStore() {
		return store;
	}

}
