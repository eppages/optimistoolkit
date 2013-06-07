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
import eu.optimis.mi.gui.client.model.Sp2IpModel;
import eu.optimis.mi.gui.client.resources.OptimisResource;

public class GraphicReportSp2IpTrustDiagramPanel extends ContentPanel {

	
	private ListStore<Sp2IpModel> store;
	private Grid<Sp2IpModel> grid;
	private ColumnModel cm;

	public GraphicReportSp2IpTrustDiagramPanel() {
		setHeading("SP 2 IP intermediate trust values");
		setLayout(new FitLayout());
		cm = new ColumnModel(OptimisResource.getSp2IpColumnConfig());
		store = new ListStore<Sp2IpModel>();
		grid = new Grid<Sp2IpModel>(store, cm);
		grid.setTitle("SP 2 IP intermediate trust values");
		grid.setBorders(true);
		grid.getView().setForceFit(true);
		add(grid);
	}
	
	public void setGridtData(List<Sp2IpModel> trustlist) {
		store.removeAll();
		store.add(trustlist); 
		grid.setBorders(true);
		grid.setVisible(true);
	}

	public Grid<Sp2IpModel> getGrid() {
		return grid;
	}

	public ListStore<Sp2IpModel> getStore() {
		return store;
	}
	
	public void setStore(ListStore<Sp2IpModel> store) {
		this.store = store;
	}
}
