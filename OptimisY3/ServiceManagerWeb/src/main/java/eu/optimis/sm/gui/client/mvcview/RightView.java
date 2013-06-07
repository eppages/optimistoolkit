/*
 * Copyright 2012 University of Stuttgart
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package eu.optimis.sm.gui.client.mvcview;

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
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.layout.AccordionLayout;
import com.extjs.gxt.ui.client.widget.layout.FlowData;
import com.extjs.gxt.ui.client.widget.layout.RowData;
import com.extjs.gxt.ui.client.widget.treepanel.TreePanel;
import eu.optimis.sm.gui.client.MainEvents;
import eu.optimis.sm.gui.client.model.Level;
import eu.optimis.sm.gui.client.resources.OptimisResource;

public class RightView extends View {

	private ContentPanel rightPanel;
	private TreeStore<Level> store;
	private TreePanel<Level> tree;

	public RightView(Controller controller) {
		super(controller);
	}

	@Override
	protected void handleEvent(AppEvent event) {
		if (event.getType() == MainEvents.init) {
			initUI();
			rightPanel.disable(); //list of options
		}
		else if(event.getType() == MainEvents.login) {
			rightPanel.enable();
			rightPanel.expand();
			}
		else if(event.getType() == MainEvents.logout) {
			rightPanel.disable();
		}
		else if(event.getType() == MainEvents.newAccount) {
			rightPanel.disable();
		}
		else if(event.getType() == MainEvents.newAccountSubmit) {
			rightPanel.disable();
		}
		else if(event.getType() == MainEvents.skipLogin) {
			rightPanel.enable();
		}
	}

	@Override
	protected void initialize() {
	}

	private void initUI() {
		ContentPanel west = (ContentPanel) Registry.get(MainView.WEST_PANEL);
		//west.setLayout(new RowLayout(Orientation.VERTICAL));
		west.setLayout(new AccordionLayout());
		
		rightPanel = new ContentPanel();
		rightPanel.setAnimCollapse(true);
		rightPanel.setHeading("SP Dashboard");
		
		rightPanel.addListener(Events.Expand, new Listener<ComponentEvent>() {
			public void handleEvent(ComponentEvent be) {
				LayoutContainer wrapper = (LayoutContainer) Registry.get(MainView.CENTER_PANEL);
				wrapper.removeAll();
				return;
			}});
		
		store = new TreeStore<Level>();
		Level parent = new Level("SP Dashboard Options", null);
		store.add(parent, true);
		store.add(parent, OptimisResource.getLevels(), false);
		store.add(parent, OptimisResource.getLevels2(), false);
		Level parent2 = new Level("Broker Use Case Options", null);
		store.add(parent2, true);
		store.add(parent2, OptimisResource.getLevels3(), false);
		tree = new TreePanel<Level>(store);
		tree.setDisplayProperty("name");
		tree.setAutoLoad(true);
		tree.setAutoExpand(true);

		SelectionChangedListener<Level> scl = new SelectionChangedListener<Level>() {
			@Override
			public void selectionChanged(
					SelectionChangedEvent<Level> se) {
				Level f = (Level) se.getSelection().get(0);
				if (f.getName().contains("Deploy service")) {
					Dispatcher.get().dispatch(MainEvents.deployService);
				}
				else if (f.getName().contains("Undeploy service")) {
					Dispatcher.get().dispatch(MainEvents.undeployService);
				}
				else if (f.getName().contains("Available services")) {
					Dispatcher.get().dispatch(MainEvents.availableServices);
				}
				else if (f.getName().contains("IP Registry")) {
					Dispatcher.get().dispatch(MainEvents.ipRegistry);
				}
				else if (f.getName().contains("Redeploy service")) {
					Dispatcher.get().dispatch(MainEvents.redeployService);
				}
				else if (f.getName().contains("TREC GUI")) {
					Dispatcher.get().dispatch(MainEvents.trecGUIsp);
					MainView.west.collapse();
				}
				else if (f.getName().contains("Properties")) {
					Dispatcher.get().dispatch(MainEvents.properties);
				}
				else if (f.getName().contains("Logs")) {
					Dispatcher.get().dispatch(MainEvents.logs);
				}
				else if (f.getName().contains("IPS")) {
					Dispatcher.get().dispatch(MainEvents.brokerIps);
				}
				else if (f.getName().contains("Secure Storage")) {
					Dispatcher.get().dispatch(MainEvents.brokerSecureStorage);
				}
				else if (f.getName().contains("VPN")) {
					Dispatcher.get().dispatch(MainEvents.brokerVpn);
				}
				return;
			}

		};
		tree.getSelectionModel().addSelectionChangedListener(scl);
		rightPanel.add(tree, new FlowData(10));
		west.add(rightPanel, new RowData(1.0, 0.33, new Margins(0, 0, 0, 0)));
	}
}
