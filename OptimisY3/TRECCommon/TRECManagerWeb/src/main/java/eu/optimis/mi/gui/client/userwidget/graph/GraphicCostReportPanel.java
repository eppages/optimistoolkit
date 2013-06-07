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
import com.extjs.gxt.ui.client.widget.form.Radio;
import com.extjs.gxt.ui.client.widget.form.RadioGroup;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.layout.FormData;
import com.extjs.gxt.ui.client.widget.layout.FormLayout;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;

import eu.optimis.mi.gui.client.MainEvents;
import eu.optimis.mi.gui.client.MonitoringManagerWebServiceAsync;
import eu.optimis.mi.gui.client.model.CostResourceSP;
import eu.optimis.mi.gui.client.model.MetricName;
import eu.optimis.mi.gui.client.model.CostResourceIP;
import eu.optimis.mi.gui.client.resources.OptimisResource;

import com.allen_sauer.gwt.log.client.Log;

public class GraphicCostReportPanel extends ContentPanel {

	private MonitoringManagerWebServiceAsync service;
	private List<MetricName> names;
	private ListStore<MetricName> store = new ListStore<MetricName>();
	private String submission;
	private Radio radioSP;
	private Radio radioSPPrediction;
	private Radio radioSPService;
	private Radio radioIP;
	private Radio radioSPComponent;
	private Radio radioIPService;
	private Radio radioIPComponent;
	private Radio radioIPNode;
	private RadioGroup ipLevel;
	private RadioGroup spLevel;
	private TextField<String> providerId;
	private TextField<String> assessorId;
	private Radio radioService = new Radio();
	private Button refresh;
	private Button cancel;
	private String side;
	private String stage;
	private String provider;

	// private String level;
	// private String identifier;

	public GraphicCostReportPanel() {
		setHeading("Cost Report");
		createForm();
		adaptToGETParameters();
	}

	private void adaptToGETParameters() {
		side = Window.Location.getParameter("side");
		stage = Window.Location.getParameter("stage");
		String level = Window.Location.getParameter("level");
		String identifier = Window.Location.getParameter("identifier");
		provider = Window.Location.getParameter("providerId");

		if (side == null || side.equals("")) {
			ipLevel.setVisible(true);
			ipLevel.clear();
			spLevel.setVisible(true);
			spLevel.clear();
			return;
		}
		Log.debug("Side is not null");

		if (side.equalsIgnoreCase("ip")) {
			Log.debug("Side is IP");
			// Operation information tabs.
			ipLevel.setVisible(true);
			spLevel.setVisible(false);

			if (level != null && level.equals("") == false) {
				Log.debug("Level is not null");
				if (level.equalsIgnoreCase("service")) {
					ipLevel.setValue(radioIPService);
				} else if (level.equalsIgnoreCase("vm")) {
					ipLevel.setValue(radioIPComponent);
				} else if (level.equalsIgnoreCase("node")) {
					ipLevel.setValue(radioIPNode);
				}
			} else {
				Log.debug("Level is null");
				ipLevel.clear();
			}
		} else if (side.equalsIgnoreCase("sp")) {
			Log.debug("Side is SP");
			// Deployment information tabs.
			ipLevel.setVisible(false);
			spLevel.setVisible(true);
			spLevel.setValue(radioSPPrediction);
			// Django: only one radio button value here so no need to adapt on
			// level
		}
		
		// Django: Take these values from the the Get parameters and the hide
		// the form fields
		if (provider != null && provider.equals("") == false) {
			providerId.setValue(provider);
			// this.providerId.hide();
			Log.debug("provider is not null");
		} else {
			Log.debug("provider is null");
		}
		providerId.setVisible(true);

		if (identifier != null && identifier.equals("") == false) {
			assessorId.setValue(identifier);
			// this.assessorId.hide();
			Log.debug("assessor is not null");
		} else { 
			Log.debug("assessor is not null");
		}
		assessorId.setVisible(true);

		// Debug mode, enable in the risk UI
		Log.setCurrentLogLevel(Log.LOG_LEVEL_DEBUG);
		Log.debug("COST Report: Received Get Variables, side: " + side
				+ " stage: " + stage + " providerId: " + provider
				+ " identifier: " + service);

		// Buttons
		refresh.setVisible(true);
		cancel.setVisible(true);
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

		// SP Radio
		radioSP = new Radio();
		radioSP.setValueAttribute("sp");
		radioSP.setBoxLabel("SP Cost");

		radioSPPrediction = new Radio();
		radioSPPrediction.setValueAttribute("predictionSP");
		radioSPPrediction.setBoxLabel("SP Deployment");

		radioSPService = new Radio();
		radioSPService.setValueAttribute("serviceSP");
		radioSPService.setBoxLabel("SP Service Cost");

		radioSPComponent = new Radio();
		radioSPComponent.setValueAttribute("componentSP");
		radioSPComponent.setBoxLabel("SP VM Cost");

		// IP Radio
		radioIP = new Radio();
		radioIP.setValueAttribute("ip");
		radioIP.setBoxLabel("IP Cost");

		radioIPService = new Radio();
		radioIPService.setValueAttribute("serviceIP");
		radioIPService.setBoxLabel("IP Service Cost");

		radioIPComponent = new Radio();
		radioIPComponent.setValueAttribute("componentIP");
		radioIPComponent.setBoxLabel("IP VM Cost");

		radioIPNode = new Radio();
		radioIPNode.setValueAttribute("nodeIP");
		radioIPNode.setBoxLabel("IP Node Cost");

		// SP level
		spLevel = new RadioGroup();
		spLevel.setFieldLabel("Select Provider");
		spLevel.setStyleName("formComponent");
		spLevel.setVisible(true);

		// spLevel.add(radioSP);
		spLevel.add(radioSPPrediction);
		// spLevel.add(radioSPService);
		// spLevel.add(radioSPComponent);
		spLevel.setOrientation(Style.Orientation.VERTICAL);
		spLevel.setSpacing(2);
		fp.add(spLevel, formData);

		// IP level
		ipLevel = new RadioGroup();
		ipLevel.setFieldLabel("Select Provider");
		ipLevel.setStyleName("formComponent");
		ipLevel.setVisible(true);

		// ipLevel.add(radioIP);
		ipLevel.add(radioIPService);
		ipLevel.add(radioIPComponent);
		ipLevel.add(radioIPNode);
		ipLevel.setOrientation(Style.Orientation.VERTICAL);
		ipLevel.setSpacing(2);
		fp.add(ipLevel, formData);

		providerId = new TextField<String>();
		providerId.setFieldLabel("Provider ID");
		providerId.setAllowBlank(false);
		providerId.setVisible(true);
		fp.add(providerId, formData);

		assessorId = new TextField<String>();
		assessorId.setFieldLabel("Assessor ID");
		assessorId.setAllowBlank(false);
		assessorId.setVisible(true);
		fp.add(assessorId, formData);

		refresh = new Button("Refresh");
		fp.addButton(refresh);
		cancel = new Button("Cancel");
		fp.addButton(cancel);

		refresh.addSelectionListener(new SelectionListener<ButtonEvent>() {
			@SuppressWarnings({ "rawtypes", "unchecked" })
			// Django this needs fixing.......
			@Override
			public void componentSelected(ButtonEvent ce) {

				Log.debug("GOT HEREEEEEEEE!!!!!");

				if (side.equalsIgnoreCase("sp") && spLevel.getValue() == null) {
					ArrayList<String> eventData = new ArrayList<String>();
					eventData.add("Please choose a SP provider type!");
					Dispatcher.get().dispatch(
							MainEvents.ReportGraphicCostDiagram, eventData);
					return;
				}
				Log.debug("GOT HEREEEEEEEE2!!!!!");
				if (side.equalsIgnoreCase("ip") && ipLevel.getValue() == null) {
					ArrayList<String> eventData = new ArrayList<String>();
					eventData.add("Please choose a IP provider type!");
					Dispatcher.get().dispatch(
							MainEvents.ReportGraphicCostDiagram, eventData);
					return;
				}
				Log.debug("GOT HEREEEEEEEE3!!!!!");
				if (providerId.getValue() == null) {
					ArrayList<String> eventData = new ArrayList<String>();
					eventData
							.add("Please type the infrastructure provider ID!");
					Dispatcher.get().dispatch(
							MainEvents.ReportGraphicCostDiagram, eventData);
					return;
				}
				Log.debug("GOT HEREEEEEEEE4!!!!!");
				if (assessorId.getValue() == null) {
					ArrayList<String> eventData = new ArrayList<String>();
					eventData.add("Please type the Assessor ID!");
					Dispatcher.get().dispatch(
							MainEvents.ReportGraphicCostDiagram, eventData);
					return;
				}
				Log.debug("GOT HEREEEEEEEE5!!!!!");
				service = (MonitoringManagerWebServiceAsync) Registry
						.get("guiservice");

				Radio radioDefault = new Radio();
				radioDefault.setValueAttribute("default");
				radioDefault.setBoxLabel("Default");
				if (spLevel.getValue().equals(null)) {
					spLevel.add(radioDefault);
					spLevel.setValue(radioDefault);
				} else if (ipLevel.getValue().equals(null)) {
					ipLevel.add(radioDefault);
					ipLevel.setValue(radioDefault);
				}

				// SP
				Log.debug("GOT HEREEEEEEEE6!!!!!");
				Log.debug("spLevel");
				Log.debug("spLevel: " + spLevel.getValue().getBoxLabel());
				Log.debug("ipLevel");
				Log.debug("ipLevel: " + ipLevel.getValue().getBoxLabel());
				Log.debug("OK");

				
				
				// SP Prediction
				if (spLevel.getValue().getBoxLabel().equals("SP Deployment")) {

					Log.debug("SP Prediction");
					Log.debug(providerId.getValue());
					Log.debug(assessorId.getValue());
					Log.debug("Calling getSPPredictionCostResources. ProviderId: "
							+ providerId.getValue()
							+ " AssessorId: "
							+ assessorId.getValue());
					service.getSPPredictionCostResources(providerId.getValue(),
							assessorId.getValue(),
							new AsyncCallback<List<CostResourceSP>>() {
								public void onFailure(Throwable caught) {
									try {
										Dispatcher.forwardEvent(
												MainEvents.Error, caught);
									} catch (Exception e) {
										Log.debug(e.getMessage());
									}

								}

								public void onSuccess(
										List<CostResourceSP> result) {
									Log.debug("on success result:"
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
									Log.debug("on success eventData:"
											+ eventData.size());
									Dispatcher
											.get()
											.dispatch(
													MainEvents.ReportGraphicCostSPDiagram,
													eventData);
								}
							});
				} // IP Service
				else if (ipLevel.getValue().getBoxLabel().equals("IP Service Cost")) {

					Log.debug("IP Service");
					Log.debug(providerId.getValue());
					Log.debug("Calling getIPServiceCostResources. ProviderId: "
							+ providerId.getValue() + " AssessorId: "
							+ assessorId.getValue());
					service.getIPServiceCostResources(providerId.getValue(),
							assessorId.getValue(),
							new AsyncCallback<List<CostResourceIP>>() {
								public void onFailure(Throwable caught) {
									try {
										Dispatcher.forwardEvent(
												MainEvents.Error, caught);
									} catch (Exception e) {
										Log.debug(e.getMessage());
									}

								}

								public void onSuccess(
										List<CostResourceIP> result) {
									Log.debug("on success result:"
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
									Log.debug("on success eventData:"
											+ eventData.size());
									Dispatcher
											.get()
											.dispatch(
													MainEvents.ReportGraphicCostDiagram,
													eventData);
								}
							});
				} // IP Component
				else if (ipLevel.getValue().getBoxLabel().equals("IP VM Cost")) {

					Log.debug("IP Component");
					Log.debug(providerId.getValue());
					Log.debug(assessorId.getValue());
					Log.debug("Calling getIPComponentCostResources. ProviderId: "
							+ providerId.getValue()
							+ " AssessorId: "
							+ assessorId.getValue());
					service.getIPComponentCostResources(providerId.getValue(),
							assessorId.getValue(),
							new AsyncCallback<List<CostResourceIP>>() {
								public void onFailure(Throwable caught) {
									try {
										Dispatcher.forwardEvent(
												MainEvents.Error, caught);
									} catch (Exception e) {
										Log.debug(e.getMessage());
									}

								}

								public void onSuccess(
										List<CostResourceIP> result) {
									Log.debug("on success result:"
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
									Log.debug("on success eventData:"
											+ eventData.size());
									Dispatcher
											.get()
											.dispatch(
													MainEvents.ReportGraphicCostDiagram,
													eventData);
								}
							});
				} // IP Node
				else if (ipLevel.getValue().getBoxLabel().equals("IP Node Cost")) {

					Log.debug("IP node");
					Log.debug(providerId.getValue());
					Log.debug(assessorId.getValue());
					Log.debug("Calling getIPNodeCostResources. ProviderId: "
							+ providerId.getValue() + " AssessorId: "
							+ assessorId.getValue());
					service.getIPNodeCostResources(providerId.getValue(),
							assessorId.getValue(),
							new AsyncCallback<List<CostResourceIP>>() {
								public void onFailure(Throwable caught) {
									try {
										Dispatcher.forwardEvent(
												MainEvents.Error, caught);
									} catch (Exception e) {
										Log.debug(e.getMessage());
									}

								}

								public void onSuccess(
										List<CostResourceIP> result) {
									Log.debug("on success result:"
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
									Log.debug("on success eventData:"
											+ eventData.size());
									Dispatcher
											.get()
											.dispatch(
													MainEvents.ReportGraphicCostDiagram,
													eventData);
								}
							});
				} else if (ipLevel.equals(radioDefault) || ipLevel.equals(radioDefault)) {
					Log.debug("Default Radio Button selected");
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
}
