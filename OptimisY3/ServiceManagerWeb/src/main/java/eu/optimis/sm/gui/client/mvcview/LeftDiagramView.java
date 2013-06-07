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

import java.util.List;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.extjs.gxt.ui.client.mvc.AppEvent;
import com.extjs.gxt.ui.client.mvc.Controller;
import com.extjs.gxt.ui.client.mvc.View;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.layout.BorderLayout;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
import eu.optimis.sm.gui.client.MainEvents;
import eu.optimis.sm.gui.client.userwidget.LeftDiagramPanel;

public class LeftDiagramView extends View {

	private LayoutContainer container;
	private LeftDiagramPanel leftDiagramPanel;

	public LeftDiagramView(Controller controller) {
		super(controller);
		}

	@Override
	protected void handleEvent(AppEvent event) {
		if ((event.getType() == MainEvents.login)
				||(event.getType() == MainEvents.logout)
				||(event.getType() == MainEvents.newAccount)
				||(event.getType() == MainEvents.newAccountSubmit)
				) {
			List eventDataList = (List) event.getData();
			String message = (String) eventDataList.get(0);
			LayoutContainer wrapper = (LayoutContainer) Registry.get(MainView.CENTER_PANEL);
			wrapper.removeAll();
			leftDiagramPanel.setSubmissionText(message);
			
			//if (eventDataList.size() > 1) {
				//leftDiagramPanel.setTextArea((String) eventDataList.get(1));
				//leftDiagramPanel.setToolbarMessageLabel();
				//List<Resource> mrlist = (List<Resource>) eventDataList.get(1);
				//leftDiagramPanel.setChartData(mrlist);
			//}
			//else{
				//leftDiagramPanel.setErrorLabel();
				//leftDiagramPanel.removeChartData();
			//}
			wrapper.add(container);
			wrapper.layout();
			return;
		}

		if ((event.getType() == MainEvents.logout)) {
			
		}
		//if (event.getType() == MainEvents.newAccount) {
			//leftDiagramPanel.removeChartData();
			//leftDiagramPanel.setSubmissionText("Select the option...");
			//return;
		//}
		
	}

	@Override
	protected void initialize() {
		container = new LayoutContainer();
		BorderLayout layout = new BorderLayout();
		layout.setEnableState(false);
		container.setLayout(layout);
		leftDiagramPanel = new LeftDiagramPanel();
		container.add(leftDiagramPanel, new BorderLayoutData(LayoutRegion.CENTER));
	}

}
