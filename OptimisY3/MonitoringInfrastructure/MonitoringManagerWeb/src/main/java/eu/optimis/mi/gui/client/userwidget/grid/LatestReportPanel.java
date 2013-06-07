/**

Copyright 2012 University of Stuttgart

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at 

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

**/

package eu.optimis.mi.gui.client.userwidget.grid;

import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.event.CheckChangedEvent;
import com.extjs.gxt.ui.client.event.CheckChangedListener;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.TreePanelEvent;
import com.extjs.gxt.ui.client.store.TreeStore;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.layout.FlowData;
import com.extjs.gxt.ui.client.widget.treepanel.TreePanel;

import eu.optimis.mi.gui.client.model.Folder;
import eu.optimis.mi.gui.client.resources.OptimisResource;


public class LatestReportPanel extends ContentPanel {
	public LatestReportPanel() {
		setHeading("Latest Report");

		Folder model = OptimisResource.getMonitoringTreeModel();
		TreeStore<ModelData> store = new TreeStore<ModelData>();
		store.add(model.getChildren(), true);

		final TreePanel<ModelData> tree = new TreePanel<ModelData>(store);
		tree.setDisplayProperty("name");
		tree.setWidth(300);
		tree.setCheckable(true);
		tree.setAutoLoad(true);
		add(tree, new FlowData(10)); 

		// Overall checked state changes
		tree.addCheckListener(new CheckChangedListener<ModelData>() {
			@Override
			public void checkChanged(CheckChangedEvent<ModelData> event) {

			}
		});

		// Change in node check state
		tree.addListener(Events.CheckChange,
				new Listener<TreePanelEvent<ModelData>>() {
					public void handleEvent(TreePanelEvent<ModelData> be) {

					}
				});
	}

}
