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
package eu.optimis.sm.gui.client.mvc;

import com.extjs.gxt.ui.client.event.EventType;
import com.extjs.gxt.ui.client.mvc.AppEvent;
import com.extjs.gxt.ui.client.mvc.Controller;
import eu.optimis.sm.gui.client.MainEvents;
import eu.optimis.sm.gui.client.mvcview.LeftDiagramView;
import eu.optimis.sm.gui.client.mvcview.LeftView;
import eu.optimis.sm.gui.client.mvcview.RightView;

public class LeftController extends Controller{
	
	private LeftView leftView;
	private LeftDiagramView leftDiagramView;
	public LeftController(){
		 registerEventTypes(MainEvents.init);
		 registerEventTypes(MainEvents.login);
		 registerEventTypes(MainEvents.logout);
		 registerEventTypes(MainEvents.newAccount);
		 registerEventTypes(MainEvents.newAccountSubmit);
		 registerEventTypes(MainEvents.skipLogin);
		 registerEventTypes(MainEvents.availableServices);
	}
	
	@Override
	public void handleEvent(AppEvent event) {
		 EventType type = event.getType();
		    if (type == MainEvents.init) {
			    forwardToView(leftView, event);
		    	forwardToView(leftDiagramView, event);
		    }
		    if (type == MainEvents.login){
		    	forwardToView(leftView, event);
		    	forwardToView(leftDiagramView, event);
				forwardToView(RightController.rightAvailableServicesView, event);
		    }
		    if (type == MainEvents.logout){
		    	forwardToView(leftView, event);
		    	forwardToView(leftDiagramView, event);
		    }
		    if (type == MainEvents.newAccount){
		    	forwardToView(leftView, event);
		    	forwardToView(leftDiagramView, event);
				forwardToView(RightController.rightView, event);
		    }
		    if (type == MainEvents.newAccountSubmit){
		    	forwardToView(leftView, event);
		    	forwardToView(leftDiagramView, event);
				forwardToView(RightController.rightView, event);
		    }
		    if (type == MainEvents.skipLogin){
		    	forwardToView(leftView, event);
		    	forwardToView(leftDiagramView, event);
				forwardToView(RightController.rightView, event);
		    }
	}
	
	 public void initialize() {
		 super.initialize();
		 if(RightController.rightView==null)
			 RightController.rightView = new RightView(this);
		 leftView = new LeftView(this);
		 leftDiagramView = new LeftDiagramView(this);
		    
   }
}
