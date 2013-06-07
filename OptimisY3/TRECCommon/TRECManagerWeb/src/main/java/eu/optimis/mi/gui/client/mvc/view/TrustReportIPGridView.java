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
import eu.optimis.mi.gui.client.model.Sp2IpModel;
import eu.optimis.mi.gui.client.mvc.MainView;
import eu.optimis.mi.gui.client.userwidget.grid.GraphicReportSp2IpTrustDiagramPanel;

public class TrustReportIPGridView extends View{

	private LayoutContainer container;
	private GraphicReportSp2IpTrustDiagramPanel grdp;
	
	public TrustReportIPGridView(Controller controller) {
		super(controller);
		// TODO Auto-generated constructor stub
	}

	@SuppressWarnings({ "rawtypes", "unchecked" }) //Django this needs fixing, same code is duplicated 50+ times horrible.......
	@Override
	protected void handleEvent(AppEvent event) {
		if (event.getType() == MainEvents.ReportGraphicTrustIPGrid) {
			LayoutContainer wrapper = (LayoutContainer) Registry
					.get(MainView.CENTER_PANEL);
			wrapper.removeAll();
			List eventDataList = (List) event.getData();
			//String message = (String) eventDataList.get(0);
			if (eventDataList.size() > 1) {
				List<Sp2IpModel> mrlist = (List<Sp2IpModel>) eventDataList.get(1);
				grdp.setGridtData(mrlist);
			}
			wrapper.add(container);
			wrapper.layout();
			return;
		}
		if (event.getType() == MainEvents.ReportGraphicCancel) {
			return;
		}
	}
	
	@Override
	protected void initialize() {
		container = new LayoutContainer();
		BorderLayout layout = new BorderLayout();
		layout.setEnableState(false);
		container.setLayout(layout);
		grdp = new GraphicReportSp2IpTrustDiagramPanel();
		container.add(grdp, new BorderLayoutData(LayoutRegion.CENTER));
	}

}
