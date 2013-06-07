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
import com.extjs.gxt.ui.client.widget.Label;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.CheckBox;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.RowExpander;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.toolbar.LabelToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.Widget;
import eu.optimis.sm.gui.client.MainEvents;
import eu.optimis.sm.gui.client.ServiceManagerWebServiceAsync;
import eu.optimis.sm.gui.client.model.Service;
import eu.optimis.sm.gui.client.resources.OptimisResource;

public class RightUndeployPanel extends ContentPanel {
	private ServiceManagerWebServiceAsync service;
	private TextField<String> id;
	private ColumnModel cm = null;
	private String store2;
	private Widget grid2 = new TextArea();
	private ListStore<Service> store3;
	private Grid<Service> grid3;
	private String serviceId;
	private String serviceN;
	private CheckBox checkBox;
	List<Service> resultlocal;
	RowExpander expander;
	
	public RightUndeployPanel() {
		setHeading("Undeploy service among the available services:");
		setLayout(new FitLayout());
		
		ToolBar toolBar = new ToolBar();
		Button undeployButton = new Button("Undeploy");
		checkBox = new CheckBox();
		Label label = new Label("Keep data:");
		checkBox.setValue(true);
		checkBox.setTitle("Keep data");
		undeployButton.setIcon(IconHelper.createStyle("icon-email-add"));
		//undeployButton.setIcon(IconHelper.createStyle("add16"));
		undeployButton.setShadow(true);
		undeployButton.setBorders(true);
		id = new TextField<String>();
		id.setFieldLabel("service ID");
		id.setAllowBlank(false);
		toolBar.add(new LabelToolItem("Service number: "));
		toolBar.add(id);
		toolBar.add(undeployButton);
		toolBar.add(label);
		toolBar.add(checkBox);
		Button refreshButton = new Button("Refresh");
		refreshButton.setIcon(IconHelper.createStyle("icon-email-add"));
		refreshButton.setShadow(true);
		refreshButton.setBorders(true);
		toolBar.add(refreshButton);		
		setTopComponent(toolBar);
		setLayoutOnChange(true);
		
	    expander = new RowExpander();						 						
		XTemplate tpl = XTemplate.create("<b>Infrastructure providers</b>" +
				" {listServiceProviderStr}");
		
		expander.setTemplate(tpl);
		cm = new ColumnModel(OptimisResource.getColumnConfigService(expander));
		store3 = new ListStore<Service>();
		
		refreshButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent ce) {
				
				removeAll();
				store2 = new String("Waiting for available services for undeploy response...");
				((TextArea)grid2).setValue(store2);
				add(grid2);
				layout(true);
				
				service.availableServices(LeftPanelLogin.session_id, true,
						new AsyncCallback<ArrayList<Service>>() {
							public void onFailure(Throwable caught) {
								System.out.println("Error: available services for undeploy");
								Dispatcher.forwardEvent(MainEvents.error, caught);
							}
							public void onSuccess(ArrayList<Service> result) {
								System.out.println("Successfully executed: available services for undeploy");
								store3 = new ListStore<Service>();
								removeAll();
								if(result.get(0).get("service_number").equals("-100"))
								{
									store2 = result.get(0).get("service_id");
									((TextArea)grid2).setValue(store2);
									add(grid2);
									//service.logoutUser(LeftPanelLogin.session_id, LeftPanelLogin.eMail.getValue(), new AsyncCallback<Boolean>() {
										//public void onFailure(Throwable caught) { System.out.println("logoutUser: failure"); }
										//public void onSuccess(Boolean result) { System.out.println("logoutUser: success"); }
									//});	
									layout(true);
								}
								else {
								store3.add((List<Service>)result);
								grid3 = new Grid<Service>(store3, cm);
								grid3.disableEvents(false);
								grid3.enableEvents(true);
								grid3.disableTextSelection(false);
								grid3.setTitle(" Listed services ");
								grid3.setBorders(true);
								grid3.getView().setForceFit(true);
								grid3.addPlugin(expander);
								add(grid3);
								layout(true);
								resultlocal = (List<Service>) result;
								}
							}
						});								
			}
		});		

		undeployButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent ce) {
				
				removeAll();
				store2 = new String("Waiting for available services for undeploy response...");
				((TextArea)grid2).setValue(store2);
				add(grid2);
				layout(true);
				serviceN = id.getValue();
				service = (ServiceManagerWebServiceAsync) Registry.get("guiservice");
				for(Service service1 : resultlocal)
				{
					System.out.println("service1.get(service_number) = "+service1.get("service_number"));
					if(service1.get("service_number").equals(serviceN))
						serviceId = service1.get("service_id");
				}
				service.undeployService(LeftPanelLogin.session_id, serviceId, checkBox.getValue(),
						new AsyncCallback<String>() {
							public void onFailure(Throwable caught) {
								System.out.println("Error: undeploy");
								Dispatcher.forwardEvent(MainEvents.error, caught);
							}
							public void onSuccess(String result) {
								System.out.println("Successfully executed: undeploy");
								removeAll();
								store2 = new String("Undeployment of service with id = " + serviceId + ":\n result = "+ result);
								((TextArea)grid2).setValue(store2);
								add(grid2);
								layout(true);
							}
						});
								
			}
		});
		service = (ServiceManagerWebServiceAsync) Registry.get("guiservice");
		removeAll();
		store2 = new String("Waiting for available services for undeploy response...");
		((TextArea)grid2).setValue(store2);
		add(grid2);
		
		service.availableServices(LeftPanelLogin.session_id, false,
				new AsyncCallback<ArrayList<Service>>() {
					public void onFailure(Throwable caught) {
						System.out.println("Error: available services for undeploy");
						Dispatcher.forwardEvent(MainEvents.error, caught);
					}
					public void onSuccess(ArrayList<Service> result) {
						System.out.println("Successfully executed: available services for undeploy");
						store3 = new ListStore<Service>();
						removeAll();
						if(result.get(0).get("service_number").equals("-100"))
						{
							store2 = result.get(0).get("service_id");
							((TextArea)grid2).setValue(store2);
							add(grid2);
							//service.logoutUser(LeftPanelLogin.session_id, LeftPanelLogin.eMail.getValue(), new AsyncCallback<Boolean>() {
								//public void onFailure(Throwable caught) { System.out.println("logoutUser: failure"); }
								//public void onSuccess(Boolean result) { System.out.println("logoutUser: success"); }
							//});	
							layout(true);
						}
						else {
						store3.add((List<Service>)result);
						grid3 = new Grid<Service>(store3, cm);
						grid3.disableEvents(false);
						grid3.enableEvents(true);
						grid3.disableTextSelection(false);
						grid3.setTitle(" Listed services ");
						grid3.setBorders(true);
						grid3.getView().setForceFit(true);
						//remove(grid2);
						grid3.addPlugin(expander);
						add(grid3);
						layout(true);
						resultlocal = (List<Service>) result;
						}
					}
				});
		layout(true);
	}
}
