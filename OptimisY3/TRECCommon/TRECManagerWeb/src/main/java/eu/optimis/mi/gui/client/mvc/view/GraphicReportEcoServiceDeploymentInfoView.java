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
import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.extjs.gxt.ui.client.mvc.AppEvent;
import com.extjs.gxt.ui.client.mvc.Controller;
import com.extjs.gxt.ui.client.mvc.View;
import com.extjs.gxt.ui.client.store.GroupingStore;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.layout.BorderLayout;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
import eu.optimis.mi.gui.client.MainEvents;
import eu.optimis.mi.gui.client.model.EcoNodeInfoResource;
import eu.optimis.mi.gui.client.model.EcoServiceDeploymentInfoData;
import eu.optimis.mi.gui.client.mvc.MainView;
import eu.optimis.mi.gui.client.userwidget.graph.GraphicReportEcoServiceDeploymentInfoPanel;
import java.util.List;

/**
 *
 * @author jsubirat
 */
public class GraphicReportEcoServiceDeploymentInfoView extends View {

	
	private LayoutContainer container;
	private GraphicReportEcoServiceDeploymentInfoPanel grEcoSDInfoPanel;
        
	
	public GraphicReportEcoServiceDeploymentInfoView(Controller controller) {
		super(controller);
	}
        
        @Override
	protected void initialize() {
		container = new LayoutContainer();
		BorderLayout layout = new BorderLayout();
		layout.setEnableState(false);
		container.setLayout(layout);
		grEcoSDInfoPanel = new GraphicReportEcoServiceDeploymentInfoPanel();
		container.add(grEcoSDInfoPanel, new BorderLayoutData(LayoutRegion.CENTER));
	}
	
    @SuppressWarnings("rawtypes") //Django this needs fixing, same code is duplicated 50+ times horrible.......
	@Override
	protected void handleEvent(AppEvent event) {

            if (event.getType() == MainEvents.ReportGraphicEcoServiceDeploymentInfo) {
                List eventDataList = (List) event.getData();
                EcoServiceDeploymentInfoData resource = (EcoServiceDeploymentInfoData) eventDataList.get(0);
                LayoutContainer wrapper = (LayoutContainer) Registry.get(MainView.CENTER_PANEL);
                wrapper.removeAll();
                
                //Do things with the grEcoSDInfoPanel here now.        
                GroupingStore<EcoNodeInfoResource> nodeStore = grEcoSDInfoPanel.getNodeStore();
                nodeStore.removeAll();
                nodeStore.add(resource.getNodeInfoResources());
                grEcoSDInfoPanel.setOutputText(resource.getDeploymentOutput());
                grEcoSDInfoPanel.restartTimer();
                //End of actions to the grEcoSDInfoPanel.  
                
                wrapper.add(container);
                wrapper.layout();
            }
            else if(event.getType() == MainEvents.HideReportGraphicEcoServiceDeploymentInfo) {
                LayoutContainer wrapper = (LayoutContainer) Registry.get(MainView.CENTER_PANEL);
                wrapper.removeAll();
                grEcoSDInfoPanel.stopTimer();
            }
	}
}
