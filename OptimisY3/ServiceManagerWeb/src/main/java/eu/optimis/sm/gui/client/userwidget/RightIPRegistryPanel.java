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
package eu.optimis.sm.gui.client.userwidget;

import java.util.ArrayList;
import java.util.List;
//import com.allen_sauer.gwt.log.client.Log;
import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.util.IconHelper;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.Widget;
import eu.optimis.sm.gui.client.MainEvents;
import eu.optimis.sm.gui.client.ServiceManagerWebServiceAsync;
import eu.optimis.sm.gui.client.model.IP;
import eu.optimis.sm.gui.client.resources.OptimisResource;

public class RightIPRegistryPanel extends ContentPanel {

	private ServiceManagerWebServiceAsync service;
	private String textScreen;
	private ListStore<IP> storeIPs;
	private Grid<IP> gridIPs;
	private ColumnModel cm;
	private Widget textArea = new TextArea();
	
	public RightIPRegistryPanel() {
		setHeading("IP Registry");
		setLayout(new FitLayout());

		ToolBar toolBar = new ToolBar();
		Button refreshButton = new Button("Refresh");
		refreshButton.setIcon(IconHelper.createStyle("icon-email-add"));
		toolBar.add(refreshButton);
		setTopComponent(toolBar);
		
		service = (ServiceManagerWebServiceAsync) Registry.get("guiservice");

		textScreen = new String("Waiting for response about IP Registry...");
		((TextArea)textArea).setValue(textScreen);
		add(textArea);
		layout(true);
		setLayoutOnChange(true);
		
		cm = new ColumnModel(OptimisResource.getColumnConfigIP());
		storeIPs = new ListStore<IP>();		

		service.ipRegistry(LeftPanelLogin.session_id, new AsyncCallback<ArrayList<IP>>() {
			public void onFailure(Throwable caught) {
				System.out.println("Error: ip registry");
				Dispatcher.forwardEvent(MainEvents.error, caught);
			}
			public void onSuccess(ArrayList<IP> ips) {
				System.out.println("Successfully executed: ip registry");
				removeAll();
				if(ips.get(0).get("ip_name").equals("-100"))
				{
					textScreen = ips.get(0).get("ip_ip");
					((TextArea)textArea).setValue(textScreen);
					add(textArea);
					layout(true);
				}
				else {
				storeIPs = new ListStore<IP>();
				storeIPs.add((List<IP>)ips);
				gridIPs = new Grid<IP>(storeIPs, cm);
				gridIPs.disableTextSelection(false);
				gridIPs.setTitle(" Listed ips ");
				gridIPs.setBorders(true);
				gridIPs.getView().setForceFit(true);
				System.out.println("ips(0) = " + ips.get(0).toString());
				add(gridIPs);
				layout(true);
				}
			}
		});

		refreshButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent ce) {
				removeAll();
				textScreen = new String("Waiting for response about IP Registry...");
				((TextArea)textArea).setValue(textScreen);
				add(textArea);
				layout(true);
				
				service.ipRegistry(LeftPanelLogin.session_id, new AsyncCallback<ArrayList<IP>>() {
					public void onFailure(Throwable caught) {
						System.out.println("Error: get ipRegistry");
						Dispatcher.forwardEvent(MainEvents.error, caught);
					}
					public void onSuccess(ArrayList<IP> ips) {
						System.out.println("Successfully executed: ip registry");
						removeAll();
						storeIPs = new ListStore<IP>();
						storeIPs.add((List<IP>)ips);
						gridIPs = new Grid<IP>(storeIPs, cm);
						gridIPs.setTitle(" Listed ips ");
						gridIPs.setBorders(true);
						gridIPs.getView().setForceFit(true);
						add(gridIPs);
						layout(true);
						System.out.println("ips(0) = " + ips.get(0).toString());
					}
				});								
			}
		});		
		layout(true);
	}
}
