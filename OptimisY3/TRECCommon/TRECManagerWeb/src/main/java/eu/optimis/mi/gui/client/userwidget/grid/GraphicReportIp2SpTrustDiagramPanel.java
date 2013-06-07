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

import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;

import eu.optimis.mi.gui.client.model.Ip2SpModel;
import eu.optimis.mi.gui.client.resources.OptimisResource;

public class GraphicReportIp2SpTrustDiagramPanel extends ContentPanel {

	private ListStore<Ip2SpModel> store;
	private Grid<Ip2SpModel> grid;
	private ColumnModel cm;
	
	public GraphicReportIp2SpTrustDiagramPanel() {
		setHeading("IP 2 SP intermediate trust values");
		setLayout(new FitLayout());
		cm = new ColumnModel(OptimisResource.getIp2SpColumnConfig());
		store = new ListStore<Ip2SpModel>();
		System.out.println(store.getCount());
		grid = new Grid<Ip2SpModel>(store, cm);
		grid.setTitle("IP 2 SP intermediate trust values");
		grid.setBorders(true);
		grid.getView().setForceFit(true);
		add(grid);
	}

	public void setGridtData(List<Ip2SpModel> trustlist) {
		store.removeAll();
		store.add(trustlist); 
		grid.setBorders(true);
		grid.setVisible(true);
	}
	
	
	public Grid<Ip2SpModel> getGrid() {
		return grid;
	}

	public ListStore<Ip2SpModel> getStore() {
		return store;
	}
	
	public void setStore(ListStore<Ip2SpModel> store) {
		this.store = store;
	}
}
