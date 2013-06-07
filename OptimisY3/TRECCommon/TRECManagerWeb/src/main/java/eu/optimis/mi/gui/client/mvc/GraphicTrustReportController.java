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
import eu.optimis.mi.gui.client.mvc.view.GraphicReportTrustDiagramView;
import eu.optimis.mi.gui.client.mvc.view.GraphicReportTrustView;
import eu.optimis.mi.gui.client.mvc.view.TrustReportIPGridView;
import eu.optimis.mi.gui.client.mvc.view.TrustReportSPGridView;

public class GraphicTrustReportController extends Controller {

	private GraphicReportTrustView reportView;
	private GraphicReportTrustDiagramView grdView;
	private TrustReportSPGridView tblSPView;
	private TrustReportIPGridView tblIPView;
	
	public GraphicTrustReportController(){
		 registerEventTypes(MainEvents.Init);
		 registerEventTypes(MainEvents.ReportGraphicTrustDiagram);
		 registerEventTypes(MainEvents.ReportGraphicCancel);
		 registerEventTypes(MainEvents.ReportGraphicTrustSPGrid);
		 registerEventTypes(MainEvents.ReportGraphicTrustIPGrid);
	}
	
	@Override
	public void handleEvent(AppEvent event) {
		 EventType type = event.getType();
		    if (type == MainEvents.Init) {
		      forwardToView(reportView, event);
		    }
		    if (type == MainEvents.ReportGraphicTrustDiagram){
		    	forwardToView(grdView, event);
		    }
		    if (type == MainEvents.ReportGraphicCancel){
		    	forwardToView(grdView, event);
		    }
		    if (type == MainEvents.ReportGraphicTrustSPGrid){
		    	forwardToView(tblSPView, event);
		    }
		    if (type == MainEvents.ReportGraphicTrustIPGrid){
		    	forwardToView(tblIPView, event);
		    }
	}
	
	 public void initialize() {
		 super.initialize();
		 reportView = new GraphicReportTrustView(this);
		 grdView = new GraphicReportTrustDiagramView(this);
		 tblSPView = new TrustReportSPGridView(this);
		 tblIPView = new TrustReportIPGridView(this);
   }
}
