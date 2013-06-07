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
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.mvc.AppEvent;
import com.extjs.gxt.ui.client.mvc.Controller;
import com.extjs.gxt.ui.client.mvc.View;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.layout.AccordionLayout;
import com.extjs.gxt.ui.client.widget.layout.RowData;
import eu.optimis.sm.gui.client.MainEvents;
import eu.optimis.sm.gui.client.userwidget.LeftPanelLogin;
import eu.optimis.sm.gui.client.userwidget.LeftPanelNewAccount;

public class LeftView extends View {
	
	private ContentPanel leftPanelLogin;
	private ContentPanel leftPanelnewAccount;
	public LeftView(Controller controller) {
		super(controller);
	}

	@Override
	protected void handleEvent(AppEvent event) {
		System.out.println("Event check");
		if (event.getType() == MainEvents.init) {
			initUI();
		}
		else if(event.getType() == MainEvents.login) {
			List eventDataList = (List) event.getData();
			String message = (String) eventDataList.get(0);
			if(!message.equals("Please select the option"))
				return;
			login(); 
		}
		//else if(event.getType() == MainEvents.logout) {
			//newAccount(); 
		//}
		else if(event.getType() == MainEvents.newAccount) {
			newAccount(); 
		}
		else if(event.getType() == MainEvents.newAccountSubmit) {
			newAccountSubmit(); 
		}
		else if(event.getType() == MainEvents.skipLogin) {
			skipLogin(); 
		}
	}

	@Override
	protected void initialize() {

	}
	
	private void initUI(){
		
		leftPanelLogin = new LeftPanelLogin();
		//leftPanelLogin.setAnimCollapse(true);
		leftPanelLogin.addListener(Events.Expand, new Listener<ComponentEvent>() {
			public void handleEvent(ComponentEvent be) {
				LayoutContainer wrapper = (LayoutContainer) Registry.get(MainView.CENTER_PANEL);
				wrapper.removeAll();
				return;
			}
		});
	    ContentPanel west = (ContentPanel) Registry.get(MainView.WEST_PANEL);
	    //here we can select layout: RowLayout or AccordionLayout
		//west.setLayout(new RowLayout(Orientation.VERTICAL));
		west.setLayout(new AccordionLayout());
	    west.add(leftPanelLogin, new RowData(1.0, 0.33, new Margins(0, 0, 0, 0)));
		System.out.println("Init event");	
		leftPanelnewAccount = new LeftPanelNewAccount();
	    west.add(leftPanelnewAccount, new RowData(1.0, 0.33, new Margins(0, 0, 0, 0)));
		leftPanelnewAccount.disable();
	}
//-----------------------------------------------------------	
	private void login(){
		System.out.println("Login event");
		leftPanelnewAccount.disable();		
	}
	private void newAccount(){
		System.out.println("newAccount event");
		leftPanelLogin.enable();
		leftPanelnewAccount.enable();
		leftPanelnewAccount.expand();
	}
	private void newAccountSubmit(){
		System.out.println("newAccountSubmit event");
		leftPanelnewAccount.collapse();
		leftPanelnewAccount.disable();
		leftPanelLogin.enable();
		leftPanelLogin.expand();
	}	
	private void skipLogin(){
		System.out.println("skipLogin event");
		leftPanelLogin.enable();
		leftPanelnewAccount.disable();		
	}
}
