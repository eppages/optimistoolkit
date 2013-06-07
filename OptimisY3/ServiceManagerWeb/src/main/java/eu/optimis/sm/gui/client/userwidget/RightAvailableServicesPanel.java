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
import com.extjs.gxt.ui.client.core.XTemplate;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.util.IconHelper;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.RowExpander;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.xml.client.Document;
import eu.optimis.sm.gui.client.MainEvents;
import eu.optimis.sm.gui.client.ServiceManagerWebServiceAsync;
import eu.optimis.sm.gui.client.model.Service;
import eu.optimis.sm.gui.client.resources.OptimisResource;

public class RightAvailableServicesPanel extends ContentPanel {

	private ServiceManagerWebServiceAsync service;
	private ColumnModel cm;
	private String textScreen;
	private ListStore<Service> storeServices;
	private Widget textArea = new TextArea();
	private Grid<Service> gridServices;
	Document doc;
	RowExpander expander;
	
	public RightAvailableServicesPanel() {
		setHeading("Available Services");
		setLayout(new FitLayout());

		ToolBar toolBar = new ToolBar();
		Button refreshButton = new Button("Refresh");
		refreshButton.setIcon(IconHelper.createStyle("icon-email-add"));
		refreshButton.setShadow(true);
		refreshButton.setBorders(true);
		toolBar.add(refreshButton);
		setTopComponent(toolBar);
		
		service = (ServiceManagerWebServiceAsync) Registry.get("guiservice");
	
		textScreen = new String("Waiting for available services response...");
		((TextArea)textArea).setValue(textScreen);
		add(textArea);
		layout(true);
		setLayoutOnChange(true);
		
	    expander = new RowExpander();
	    expander = new RowExpander();	
		XTemplate tpl = XTemplate.create("<b>Infrastructure providers</b>" +
				" {listServiceProviderStr}");
	    
		expander.setTemplate(tpl);
		cm = new ColumnModel(OptimisResource.getColumnConfigService(expander));
		storeServices = new ListStore<Service>();		
		
		refreshButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent ce) {
				
				removeAll();
				textScreen = new String("Waiting for available services response...");
				((TextArea)textArea).setValue(textScreen);
				add(textArea);
				layout(true);
				
		        service.availableServices(LeftPanelLogin.session_id, true,
						new AsyncCallback<ArrayList<Service>>() {
							public void onFailure(Throwable caught) {
								System.out.println("Error: available services");
								Dispatcher.forwardEvent(MainEvents.error, caught);
								System.out.println(caught);
							}
							public void onSuccess(ArrayList<Service> result) {
								System.out.println("Successfully executed: available services");
								removeAll();
								if(result.get(0).get("service_number").equals("-100"))
								{
									removeAll();
									textScreen = result.get(0).get("service_id");
									((TextArea)textArea).setValue(textScreen);
									add(textArea);
									layout(true);
								}
								else {
								storeServices = new ListStore<Service>();
								storeServices.add((List<Service>)result);
								gridServices = new Grid<Service>(storeServices, cm);
								gridServices.disableEvents(false);
								gridServices.enableEvents(true);
								gridServices.disableTextSelection(false);
								gridServices.setTitle(" Listed services ");
								gridServices.setBorders(true);
								gridServices.getView().setForceFit(true);
								gridServices.addPlugin(expander);
								add(gridServices);
								layout(true);
								}
							}
						});								
			}
		});		

		service.availableServices(LeftPanelLogin.session_id, true, new AsyncCallback<ArrayList<Service>>() {
					public void onFailure(Throwable caught) {
						System.out.println("Error: available services");
						Dispatcher.forwardEvent(MainEvents.error, caught);
					}
					public void onSuccess(ArrayList<Service> result) {
						System.out.println("Successfully executed: available services");
						removeAll();
						if(result.get(0).get("service_number").equals("-100"))
						{
							removeAll();
							textScreen = result.get(0).get("service_id");
							((TextArea)textArea).setValue(textScreen);
							add(textArea);
							layout(true);
						}
						else {
						storeServices = new ListStore<Service>();
						storeServices.add((List<Service>)result);
						gridServices = new Grid<Service>(storeServices, cm);
						gridServices.disableTextSelection(false);
						gridServices.setTitle(" Listed services ");
						gridServices.setBorders(true);
						gridServices.getView().setForceFit(true);
						gridServices.addPlugin(expander);
						add(gridServices);
						layout(true);
						}
					}
				});
		layout(true);
	}
}
