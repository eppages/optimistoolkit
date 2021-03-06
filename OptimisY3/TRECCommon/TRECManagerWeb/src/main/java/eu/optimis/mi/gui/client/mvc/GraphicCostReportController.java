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
package eu.optimis.mi.gui.client.mvc;

import com.extjs.gxt.ui.client.event.EventType;
import com.extjs.gxt.ui.client.mvc.AppEvent;
import com.extjs.gxt.ui.client.mvc.Controller;

import eu.optimis.mi.gui.client.MainEvents;
import eu.optimis.mi.gui.client.mvc.view.GraphicReportCostDiagramView;
import eu.optimis.mi.gui.client.mvc.view.GraphicReportCostSPDiagramView;
import eu.optimis.mi.gui.client.mvc.view.GraphicReportCostView;

public class GraphicCostReportController extends Controller {

	private GraphicReportCostView reportView;
	private GraphicReportCostDiagramView grdView;
	private GraphicReportCostSPDiagramView grdViewSP;
	
	public GraphicCostReportController(){
		 registerEventTypes(MainEvents.Init);
		 registerEventTypes(MainEvents.ReportGraphicCostDiagram);
                 registerEventTypes(MainEvents.ReportGraphicCostSPDiagram);
		 registerEventTypes(MainEvents.ReportGraphicCancel);
	}
	
	@Override
	public void handleEvent(AppEvent event) {
		 EventType type = event.getType();
		    if (type == MainEvents.Init) {
		      forwardToView(reportView, event);
		    }
		    if (type == MainEvents.ReportGraphicCostDiagram){
		    	forwardToView(grdView, event);
		    }
	    	if (type == MainEvents.ReportGraphicCostSPDiagram){
	    		forwardToView(grdViewSP, event);
		    }
		    if (type == MainEvents.ReportGraphicCancel){
		    	forwardToView(grdView, event);
		    }
	}
	
	 public void initialize() {
		 super.initialize();
		 reportView = new GraphicReportCostView(this);
		 grdView = new GraphicReportCostDiagramView(this);
                 grdViewSP = new GraphicReportCostSPDiagramView(this);
		    
   }
}
