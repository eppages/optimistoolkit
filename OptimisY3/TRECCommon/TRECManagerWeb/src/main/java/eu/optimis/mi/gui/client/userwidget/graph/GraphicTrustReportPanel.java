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
package eu.optimis.mi.gui.client.userwidget.graph;

import java.util.ArrayList;
import java.util.List;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.Style;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.form.Radio;
import com.extjs.gxt.ui.client.widget.form.RadioGroup;
import com.extjs.gxt.ui.client.widget.form.SimpleComboBox;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.layout.FormData;
import com.extjs.gxt.ui.client.widget.layout.FormLayout;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;

import eu.optimis.mi.gui.client.MainEvents;
import eu.optimis.mi.gui.client.MonitoringManagerWebServiceAsync;
import eu.optimis.mi.gui.client.model.Ip2SpModel;
import eu.optimis.mi.gui.client.model.MetricName;
import eu.optimis.mi.gui.client.model.Sp2IpModel;
import eu.optimis.mi.gui.client.model.TrustResourceSP;
import eu.optimis.mi.gui.client.resources.OptimisResource;

public class GraphicTrustReportPanel extends ContentPanel {
	private MonitoringManagerWebServiceAsync service;
	private List<MetricName> names;
	private ListStore<MetricName> store = new ListStore<MetricName>();
	private String submission;

	private SimpleComboBox<String> listView;
	private RadioGroup ipLevel;
	private RadioGroup spLevel;
	private TextField<String> providerId;	
	private LabelField label;
	private Radio radioService = new Radio();
	private Button refresh;
	private Button cancel;
	private String side;
	private String level;
	private String identifier;
	

	public GraphicTrustReportPanel() {
		setHeading("Trust Report");
		createForm();
        adaptToGETParameters();

	}
	
    private void adaptToGETParameters() 
    {    	
    	side = Window.Location.getParameter("side");
        String stage = Window.Location.getParameter("stage");
        level = Window.Location.getParameter("level");
        identifier = Window.Location.getParameter("providerId");
        //Log.debug("Received TREC. Side: " + side + " Stage: " + stage + " Level: " + level + " Identifier: " + identifier);

        label.setVisible(true);
        
        if (side == null || stage == null) {
            return;
        }

        if (side.equalsIgnoreCase("ip")) {
        	listView.setVisible(true);
        	ipLevel.setVisible(true);
        	spLevel.setVisible(false);
        	providerId.setVisible(true);        	
        	refresh.setVisible(true);
        	cancel.setVisible(true); 
        	providerId.setValue(identifier);
        	label.setVisible(false);
        } 
        else if (side.equalsIgnoreCase("sp")) 
        {
            //Deployment information tabs.
        	listView.setVisible(true);
        	ipLevel.setVisible(false);
        	spLevel.setVisible(true);
        	providerId.setVisible(true);        	
        	refresh.setVisible(true);
        	cancel.setVisible(true); 
        	providerId.setValue(identifier);
        	label.setVisible(false);
        }
        else if (level.equalsIgnoreCase("service"))
        {
        	listView.setVisible(true);
        	ipLevel.setVisible(false);
        	spLevel.setVisible(false);
        	providerId.setVisible(true);
        	providerId.setFieldLabel("Service ID");
        	refresh.setVisible(true);
        	cancel.setVisible(true);        	
        	identifier = Window.Location.getParameter("identifier");
        	providerId.setValue(identifier);
        	label.setVisible(false);
        }
    }
	private void createForm() {
		FormData formData = new FormData("-10");
		FormPanel fp = new FormPanel();
		FormLayout layout = new FormLayout();
		layout.setLabelAlign(FormPanel.LabelAlign.TOP);
		fp.setLayout(layout);
		fp.setFrame(false);
		fp.setHeaderVisible(false);
		fp.setAutoWidth(true);
		fp.setBodyBorder(true);
		fp.setButtonAlign(Style.HorizontalAlignment.CENTER);

		// Chooose view (Graphic / Table)
		listView = new SimpleComboBox<String>();
		listView.add("Table");
		listView.add("Graphic");
		listView.setFieldLabel("View type");
		listView.setForceSelection(true);
		listView.setEditable(false);
		//listView.setEmptyText("-Choose a view-");
		listView.setValueField("Table");
        fp.add(listView,formData);
        
        label = new LabelField();
        label.setText("Sorry, but trust does not support this level.");        
        fp.add(label,formData);
		
		// SP Radio
		Radio radioSP = new Radio();
		radioSP.setValueAttribute("sp");
		radioSP.setBoxLabel("SP Trust");

		// IP Radio
		Radio radioIP = new Radio();
		radioIP.setValueAttribute("ip");
		radioIP.setBoxLabel("IP Trust");

		// ///////////
		spLevel = new RadioGroup();
		spLevel.setFieldLabel("Provider Type");
		spLevel.setStyleName("formComponent");
		spLevel.add(radioIP);
		spLevel.setOrientation(Style.Orientation.VERTICAL);
		spLevel.setSpacing(2);
		spLevel.setValue(radioIP);
		fp.add(spLevel, formData);
		
		ipLevel = new RadioGroup();
		ipLevel.setFieldLabel("Provider Type:");
		ipLevel.setStyleName("formComponent");
		ipLevel.add(radioSP);
		ipLevel.setOrientation(Style.Orientation.VERTICAL);
		ipLevel.setSpacing(2);
		ipLevel.setValue(radioSP);
		fp.add(ipLevel, formData);
		
		providerId = new TextField<String>();
		providerId.setFieldLabel("Provider ID");
		providerId.setAllowBlank(false);
		fp.add(providerId, formData);
		
		refresh = new Button("Refresh");
		fp.addButton(refresh);
		cancel = new Button("Cancel");
		fp.addButton(cancel);

		refresh.addSelectionListener(new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent ce) {
				
				if (listView.getSimpleValue().equalsIgnoreCase("table")){
					showTables();
				}else{
					showGraphics();
				}

				
			}

		});

		cancel.addSelectionListener(new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent ce) {
				names = null;
				names = OptimisResource.getServiceMetrics();
				store.removeAll();
				store.add(names);
				radioService.setValue(true);
				providerId.clear();
				Dispatcher.get().dispatch(MainEvents.ReportGraphicCancel);
			}
		});

		this.add(fp);

	}
	
	private void showTables(){
		if (side.equalsIgnoreCase("sp") && spLevel.getValue() == null) {
			ArrayList<String> eventData = new ArrayList<String>();
			eventData.add("Please chosse a provider type!");
			Dispatcher.get().dispatch(MainEvents.ReportGraphicTrustDiagram, eventData);
			return;
		}
		if (side.equalsIgnoreCase("ip") && ipLevel.getValue() == null) {
			ArrayList<String> eventData = new ArrayList<String>();
			eventData.add("Please chosse a provider type!");
			Dispatcher.get().dispatch(MainEvents.ReportGraphicTrustDiagram, eventData);
			return;
		}

		if (!level.equalsIgnoreCase("service") && providerId.getValue() == null) {
			ArrayList<String> eventData = new ArrayList<String>();
			eventData.add("Please type the ID!");
			Dispatcher.get().dispatch(MainEvents.ReportGraphicTrustDiagram, eventData);
			return;
		}

		service = (MonitoringManagerWebServiceAsync) Registry.get("guiservice");
		
		// if (ipLevel.getValue().getValueAttribute().equalsIgnoreCase("sp")) {
		if (side.equalsIgnoreCase("sp")) {
			System.out.println("sp branch");
			service.getIp2SpInfo(providerId.getValue(),
					new AsyncCallback<List<Ip2SpModel>>() {
						public void onFailure(Throwable caught) {
							Dispatcher.forwardEvent(MainEvents.Error,
									caught);
						}

						@SuppressWarnings({ "rawtypes", "unchecked" }) //FIXME Django this is bad... copy and pasted all over...
						public void onSuccess(
								List<Ip2SpModel> result) {
							System.out.println("on success result:"
									+ result.size());
							ArrayList eventData = new ArrayList();
							if (result.size() == 0) {
								eventData
										.add("Sorry, There are no suitable records.");
							} else {
								eventData.add(submission);
								eventData.add(result);
							}
							Dispatcher
									.get()
									.dispatch(
											MainEvents.ReportGraphicTrustSPGrid,
											eventData);
						}

					});
		} else {
			System.out.println("ip branch");
			System.out.println(providerId.getValue());
			service.getSp2IpInfo(providerId.getValue(),
					new AsyncCallback<List<Sp2IpModel>>() {
						public void onFailure(Throwable caught) {
							try {
								Dispatcher.forwardEvent(
										MainEvents.Error, caught);
								} catch (Exception e) {
								System.out.println(e.getMessage());
							}

						}

						@SuppressWarnings({ "rawtypes", "unchecked" }) //FIXME Django this is bad... copy and pasted all over...
						public void onSuccess(
								List<Sp2IpModel> result) {
							System.out.println("on success result:"
									+ result.size());
							submission = providerId.getValue();
							ArrayList eventData = new ArrayList();
							if (result.size() == 0) {
								eventData
										.add("Sorry, There are no suitable records.");
							} else {
								eventData.add(submission);
								eventData.add(result);
								// eventData.addAll(result);
							}
							System.out.println("on success eventData:"
									+ eventData.size());
							Dispatcher
									.get()
									.dispatch(
											MainEvents.ReportGraphicTrustIPGrid,
											eventData);
						}

					});
		}
	}
	
	private void showGraphics()
	{
		if (side.equalsIgnoreCase("sp") && spLevel.getValue() == null) {
			ArrayList<String> eventData = new ArrayList<String>();
			eventData.add("Please chosse a provider type!");
			Dispatcher.get().dispatch(MainEvents.ReportGraphicTrustDiagram, eventData);
			return;
		}
		if (side.equalsIgnoreCase("ip") && ipLevel.getValue() == null) {
			ArrayList<String> eventData = new ArrayList<String>();
			eventData.add("Please chosse a provider type!");
			Dispatcher.get().dispatch(MainEvents.ReportGraphicTrustDiagram, eventData);
			return;
		}

		if (providerId.getValue() == null) {
			ArrayList<String> eventData = new ArrayList<String>();
			eventData.add("Please type the ID!");
			Dispatcher.get().dispatch(
					MainEvents.ReportGraphicTrustDiagram, eventData);
			return;
		}
		
		service = (MonitoringManagerWebServiceAsync) Registry.get("guiservice");
		
		// if (ipLevel.getValue().getValueAttribute().equalsIgnoreCase("sp")) 
		if (side.equalsIgnoreCase("sp"))
		{
			System.out.println("sp branch");
			service.getSPTrustResources(providerId.getValue(),
					new AsyncCallback<List<TrustResourceSP>>() {
						public void onFailure(Throwable caught) {
							Dispatcher.forwardEvent(MainEvents.Error,
									caught);
						}

						@SuppressWarnings({ "rawtypes", "unchecked" }) //FIXME Django this is bad... copy and pasted all over...
						public void onSuccess(
								List<TrustResourceSP> result) {
							System.out.println("on success result:"
									+ result.size());
							ArrayList eventData = new ArrayList();
							if (result.size() == 0) {
								eventData
										.add("Sorry, There are no suitable records.");
							} else {
								eventData.add(submission);
								eventData.add(result);
							}
							Dispatcher
									.get()
									.dispatch(
											MainEvents.ReportGraphicTrustDiagram,
											eventData);
						}

					});
		} else if (side.equalsIgnoreCase("ip")){
			System.out.println("ip branch");
			System.out.println(providerId.getValue());
			service.getIPTrustResources(providerId.getValue(),
					new AsyncCallback<List<TrustResourceSP>>() {
						public void onFailure(Throwable caught) {
							try {
								Dispatcher.forwardEvent(
										MainEvents.Error, caught);
							} catch (Exception e) {
								System.out.println(e.getMessage());
							}

						}

						@SuppressWarnings({ "rawtypes", "unchecked" }) //FIXME Django this is bad... copy and pasted all over...
						public void onSuccess(
								List<TrustResourceSP> result) {
							System.out.println("on success result:"
									+ result.size());
							submission = providerId.getValue();
							ArrayList eventData = new ArrayList();
							if (result.size() == 0) {
								eventData
										.add("Sorry, There are no suitable records.");
							} else {
								eventData.add(submission);
								eventData.add(result);
								// eventData.addAll(result);
							}
							System.out.println("on success eventData:"
									+ eventData.size());
							Dispatcher
									.get()
									.dispatch(
											MainEvents.ReportGraphicTrustDiagram,
											eventData);
						}

					});
		}
		else if (level.equalsIgnoreCase("service")){
			System.out.println("service branch");
			System.out.println(providerId.getValue());
			service.getIp2SpInfo(providerId.getValue(),
					new AsyncCallback<List<Ip2SpModel>>() {
						public void onFailure(Throwable caught) {
							try {
								Dispatcher.forwardEvent(
										MainEvents.Error, caught);
							} catch (Exception e) {
								System.out.println(e.getMessage());
							}

						}

						@SuppressWarnings({ "rawtypes", "unchecked" }) //FIXME Django this is bad... copy and pasted all over...
						public void onSuccess(
								List<Ip2SpModel> result) {
							System.out.println("on success result:"
									+ result.size());
							submission = providerId.getValue();
							ArrayList eventData = new ArrayList();
							if (result.size() == 0) {
								eventData
										.add("Sorry, There are no suitable records.");
							} else {
								eventData.add(submission);
								eventData.add(result);
								// eventData.addAll(result);
							}
							System.out.println("on success eventData:"
									+ eventData.size());
							Dispatcher
									.get()
									.dispatch(
											MainEvents.ReportGraphicTrustDiagram,
											eventData);
						}

					});
		}
	}

}
