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
 */package eu.optimis.mi.gui.client.mvc.view;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.mvc.AppEvent;
import com.extjs.gxt.ui.client.mvc.Controller;
import com.extjs.gxt.ui.client.mvc.View;
import com.extjs.gxt.ui.client.widget.ContentPanel;

import eu.optimis.mi.gui.client.MainEvents;
import eu.optimis.mi.gui.client.mvc.MainView;
import eu.optimis.mi.gui.client.userwidget.grid.SearchReportPanel;

public class SearchReportView extends View {
	
	private ContentPanel searchReport;
	public SearchReportView(Controller controller) {
		super(controller);
		// TODO Auto-generated constructor stub
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
	
	private void initUI(){
		searchReport = new SearchReportPanel();
		searchReport.setAnimCollapse(false);
	    ContentPanel west = (ContentPanel) Registry.get(MainView.WEST_PANEL);
	    west.add(searchReport);
	}

}
