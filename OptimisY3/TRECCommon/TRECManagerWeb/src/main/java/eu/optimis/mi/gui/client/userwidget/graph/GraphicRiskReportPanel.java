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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.Style;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FieldEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Text;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.CheckBox;
import com.extjs.gxt.ui.client.widget.form.CheckBoxGroup;
import com.extjs.gxt.ui.client.widget.form.DateField;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.Radio;
import com.extjs.gxt.ui.client.widget.form.RadioGroup;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.layout.FormData;
import com.extjs.gxt.ui.client.widget.layout.FormLayout;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.IncompatibleRemoteServiceException;
import com.google.gwt.user.client.rpc.InvocationException;

import eu.optimis.mi.gui.client.MainEvents;
import eu.optimis.mi.gui.client.MonitoringManagerWebServiceAsync;
import eu.optimis.mi.gui.client.model.RiskResource;

import com.allen_sauer.gwt.log.client.DivLogger;
import com.allen_sauer.gwt.log.client.Log;

public class GraphicRiskReportPanel extends ContentPanel {

	private boolean debugDefaultValue = false;
	
	private MonitoringManagerWebServiceAsync service;
	
	private TextField<String> serviceId;
	private TextField<String> providerId;

	private RadioGroup providerType;
	private Radio serviceProvider;
	private Radio infrastructureProvider;

	private RadioGroup servicePhase;
	private Radio deployment;
	private Radio operation;

	private DateField toDate;
	private DateField fromDate;

	private CheckBoxGroup options;
	private CheckBox autoRefresh;
	private CheckBox testData;
	private CheckBox debug;

	private Button submit;
	private Button stop;
	private Button clear;
	private Button reset;

	private String serviceIdResult;
	private String providerIdResult;
	private String providerTypeResult;
	private String servicePhaseResult;
	private String fromDateResult;
	private String toDateResult;
	private String testDataResult;
	
	private boolean oldAutoRefreshValue;

	//Defaults and test variables
	private static String providerIdEmptyText = "Atos";
	private static String serviceIdEmptyText = "76c44bda-4f5a-4f97-806d-011d174bea44";
	private static Date testingFromDate = new Date((long) 1361804843000.0);
	private static Date testingToDate = new Date((long) 1361804872000.0);
	private static boolean deafultAutoRefresh = false;

	private Timer timer = new Timer() {
		public void run() {
			submit.fireEvent(Events.Select);
		}
	};
	private boolean timerRunning = false;
	private boolean error = false;

	private Text text;
	
	// Constructor creates the menu for Risk
	public GraphicRiskReportPanel() {
		setHeading("Risk Report");

		try {
			createForm();
		} catch (Exception e) {
			System.out.println("Risk Report Exception " + e);
		}
	}
	
	
	// Adapt the diagram to appropriate GET variables
	private void adaptToGETParameters() {
		String side = Window.Location.getParameter("side");
		String stage = Window.Location.getParameter("stage");
		String level = Window.Location.getParameter("level");
		String serviceId = Window.Location.getParameter("identifier");
		
		//Risk Only
		String providerId = Window.Location.getParameter("providerId"); // New for risk
		String testData = Window.Location.getParameter("testData");
		
		if (Window.Location.getParameterMap().isEmpty() == false) {
			Log.debug("Risk Report: GET variable 'side' is: " + side);
			Log.debug("Risk Report: GET variable 'stage' is: " + stage);
			Log.debug("Risk Report: GET variable 'level' is: " + level);
			Log.debug("Risk Report: GET variable 'identifier' (serviceId) is: " + serviceId);
			Log.debug("Risk Report: GET variable 'providerId' is: " + providerId);
			Log.debug("Risk Report: GET variable 'testData' is: " + testData);				
		} else {
			Log.debug("Risk Report: No GET variables available not adpating view.");
			return;
		}
		
		if (level.equals("service") != true) {
			Log.debug("Risk Report: 'level' is not service not adpating");
			invalidData();
			return;
		}
			
		if (side == null || side.equals("") || stage == null || stage.equals("")) {
			Log.debug("Risk Report: No 'side' or 'stage' passed as GET variable");
			//Special case when we want to show only IP related formPanel widgets
			if(side != null && providerId != null) {
				Log.debug("Risk Report: Found 'side' and 'provider' GET variables");
				if (side.equalsIgnoreCase("ip")) {
					providerType.setValue(infrastructureProvider);
					providerType.disable();
				} else if (side.equalsIgnoreCase("sp")) {
					providerType.setValue(serviceProvider);
					providerType.disable();
				} else {
					Log.debug("Risk Report: 'Unknown side' passed as GET variable");
					invalidData();
					return;
				}
				
				if (providerId != null && providerId.equals("") == false) {
					this.providerId.setValue(providerId);
					this.providerId.disable();
				} else { 
					Log.debug("Risk Report: No 'providerId' passed as GET variable");
					invalidData();
					return;
				}				
			} else {
				invalidData();
			}
			return;
		}
		
		if (serviceId != null && serviceId.equals("") == false) {
			this.serviceId.setValue(serviceId.toLowerCase());
			this.serviceId.hide();
		} else {
			Log.debug("Risk Report: No 'serviceId' passed as GET variable");
			invalidData();
			return;
		}
		
		if (providerId != null && providerId.equals("") == false) {
			this.providerId.setValue(providerId);
			this.providerId.hide();
		} else { 
			Log.debug("Risk Report: No 'providerId' passed as GET variable");
			invalidData();
			return;
		}	

		if (side.equalsIgnoreCase("ip")) {
			providerType.setValue(infrastructureProvider);
			providerType.hide();
		} else if (side.equalsIgnoreCase("sp")) {
			providerType.setValue(serviceProvider);
			providerType.hide();
		} else {
			Log.debug("Risk Report: 'Unknown side' passed as GET variable");
			invalidData();
			return;
		}
		
		if (stage.equalsIgnoreCase("operation")) {
			servicePhase.setValue(operation);
			servicePhase.hide();
			autoRefresh.setValue(true);
			autoRefresh.enable();
			submit.setText("Start");
			stop.enable();
		} else if (stage.equalsIgnoreCase("deployment")) {
			servicePhase.setValue(deployment);
			servicePhase.hide();
			fromDate.setValue(new Date(0));
			fromDate.hide();
			toDate.setValue(new Date(6661337666000l));
			toDate.hide();
			autoRefresh.setValue(false);
			autoRefresh.disable();
		} else {
			Log.debug("Risk Report: Unknown 'stage' passed as GET variable");
			invalidData();
			return;
		}
		
		if (testData != null) {
			if (testData.equalsIgnoreCase("1")) {
				this.testData.setValue(true);
				this.fromDate.setValue(testingFromDate);
				this.toDate.setValue(testingToDate);
				Log.debug("Risk Report: A GET variable says use testData");
			}
		}
		
		clear.hide();
		this.testData.hide();
	}

	// Informs the user if the GET variables are invalid, incomplete or not used by the Risk Report
	private void invalidData() {
		Log.debug("Risk Report: invalidData() called hiding everything show msg");
		//Hide everything
		providerType.hide();
		servicePhase.hide();
		providerId.hide();
		serviceId.hide();
		fromDate.hide();
		toDate.hide();
		options.hide();
		submit.hide();
		stop.hide();
		clear.hide();
		//Add the info text
		text.show();
		reset.show();
	}
	
	// Creates the form used in the Risk menu
	private void createForm() throws IOException {
		FormData formData = new FormData("-10");
		FormPanel formPanel = new FormPanel();
		FormLayout layout = new FormLayout();
		layout.setLabelAlign(FormPanel.LabelAlign.TOP);
		formPanel.setLayout(layout);
		formPanel.setFrame(false);
		formPanel.setHeaderVisible(false);
		formPanel.setAutoWidth(true);
		formPanel.setBodyBorder(true);
		formPanel.setButtonAlign(Style.HorizontalAlignment.CENTER);

		Log.debug("Inside createForm()");
		
		//General info text box
		text = new Text("There is no Risk Report graph to render for the input parameters. Press 'reset' to use the default Risk Report view.");
		text.setBorders(false);
		text.hide();
		formPanel.add(text, formData);
		
		// Provider type radio group
		providerType = new RadioGroup();
		providerType.setFieldLabel("Provider Type");
		providerType.setStyleName("formComponent");
		serviceProvider = new Radio();
		serviceProvider.setValueAttribute("sp");
		serviceProvider.setBoxLabel("Service Provider");
		providerType.add(serviceProvider);
		infrastructureProvider = new Radio();
		infrastructureProvider.setValueAttribute("ip");
		infrastructureProvider.setBoxLabel("Infrastructure Provider");
		providerType.add(infrastructureProvider);
		providerType.setOrientation(Style.Orientation.VERTICAL);
		providerType.setSpacing(2);
		providerType.setValue(serviceProvider);
		providerType.setVisible(true);
		formPanel.add(providerType, formData);

		// Service phase radio group
		servicePhase = new RadioGroup();
		servicePhase.setFieldLabel("Service Phase");
		servicePhase.setStyleName("formComponent");
		deployment = new Radio();
		deployment.setValueAttribute("deployment");
		deployment.setBoxLabel("Deployment Phase");
		servicePhase.add(deployment);
		operation = new Radio();
		operation.setValueAttribute("operation");
		operation.setBoxLabel("Operation Phase");
		servicePhase.add(operation);
		servicePhase.setOrientation(Style.Orientation.VERTICAL);
		servicePhase.setSpacing(2);
		servicePhase.setValue(deployment);
		servicePhase.setVisible(true);
		formPanel.add(servicePhase, formData);

		// Text field provider ID
		providerId = new TextField<String>();
		providerId.setFieldLabel("Provider ID");
		providerId.setStyleName("formComponent");
		providerId.setAllowBlank(true);
		providerId.setEmptyText(providerIdEmptyText);
		providerId.setSelectOnFocus(true);
		formPanel.add(providerId, formData);

		// Text field service ID
		serviceId = new TextField<String>();
		serviceId.setFieldLabel("Service ID");
		serviceId.setStyleName("formComponent");
		serviceId.setAllowBlank(true);
		serviceId.setEmptyText(serviceIdEmptyText);
		serviceId.setSelectOnFocus(true);
		formPanel.add(serviceId, formData);

		// Date fields
		DateTimeFormat dtFormat = DateTimeFormat
				.getFormat("dd-MM-yyyy HH:mm:ss");
		Date now = new Date(); // In miliseconds
		Date fiveMinutesEarlier = new Date(now.getTime() - 60 * 5 * 1000);
		fromDate = new DateField();
		fromDate.setFieldLabel("Date From");
		fromDate.setValue(fiveMinutesEarlier);
		fromDate.getPropertyEditor().setFormat(dtFormat);
		fromDate.setSelectOnFocus(true);
		formPanel.add(fromDate, formData);
		toDate = new DateField();
		toDate.setFieldLabel("Date To");
		toDate.setValue(now);
		toDate.getPropertyEditor().setFormat(dtFormat);
		toDate.setSelectOnFocus(true);
		formPanel.add(toDate, formData);

		// Options
		options = new CheckBoxGroup();
		options.setStyleName("formComponent");
		options.setFieldLabel("Options");
		autoRefresh = new CheckBox();
		autoRefresh.setBoxLabel("Auto Refresh");
		autoRefresh.setValue(deafultAutoRefresh);
		if (deafultAutoRefresh == false) {
			autoRefresh.disable();
		}
		options.add(autoRefresh);
		testData = new CheckBox();
		testData.setBoxLabel("Test Data");
		testData.setValue(false);
		options.add(testData);
		debug = new CheckBox();
		debug.setBoxLabel("Debug Mode");
		if (debugDefaultValue) {
			Log.setCurrentLogLevel(Log.LOG_LEVEL_DEBUG);	
		} else {
			Log.setCurrentLogLevel(Log.LOG_LEVEL_OFF);
		}
		DivLogger divLogger = Log.getLogger(DivLogger.class);
		divLogger.getWidget().setVisible(debugDefaultValue);
		debug.setValue(Log.isDebugEnabled());
		options.add(debug);
		options.setOrientation(Style.Orientation.VERTICAL);
		options.setSpacing(2);
		options.setVisible(true);
		formPanel.add(options, formData);

		// Buttons
		if (deafultAutoRefresh) {
			submit = new Button("Start");
			submit.setWidth(50);
			formPanel.addButton(submit);
			stop = new Button("Stop");
			stop.setWidth(50);
			formPanel.addButton(stop);
		} else {
			submit = new Button("Submit");
			submit.setWidth(50);
			formPanel.addButton(submit);
			stop = new Button("Stop");
			stop.setWidth(50);
			stop.disable();
			formPanel.addButton(stop);
		}
		clear = new Button("Clear");
		clear.setWidth(50);
		formPanel.addButton(clear);
		reset = new Button("Reset");
		reset.hide();
		formPanel.addButton(reset);
		
		adaptToGETParameters();
		
		// Submit Button
		submit.addSelectionListener(new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent ce) {

				Log.debug("Risk Report: Submit Button listener reports component selected");

				service = (MonitoringManagerWebServiceAsync) Registry
						.get("guiservice");

				// Variables that should be accessible from the form
				providerTypeResult = providerType.getValue()
						.getValueAttribute();
				servicePhaseResult = servicePhase.getValue()
						.getValueAttribute();

				// Decide whether to use default text fields
				if (providerId.getValue().equals("null")) {
					providerIdResult = providerId.getEmptyText();
				} else {
					providerIdResult = providerId.getValue();
				}

				if (serviceId.getValue().equals("null")) {
					serviceIdResult = serviceId.getEmptyText();
				} else {
					serviceIdResult = serviceId.getValue();
				}

				fromDateResult = Long.toString(Math.round(fromDate.getValue()
						.getTime() / 1000));
				toDateResult = Long.toString(Math.round(toDate.getValue()
						.getTime() / 1000));

				testDataResult = testData.getValue().toString();

				Log.debug("Risk Report: Submitted form values are:");
				Log.debug("   Service ID: " + serviceIdResult
						+ " Provider ID: " + providerIdResult
						+ " Provider Type: " + providerTypeResult
						+ " Service Phase: " + servicePhaseResult
						+ " Unix Date: " + fromDateResult + "-" + toDateResult
						+ " Test Data: " + testDataResult);

				// Call the web service to get risk data
				service.getRiskResources(serviceIdResult, providerIdResult,
						providerTypeResult, servicePhaseResult, fromDateResult,
						toDateResult, testDataResult,
						new AsyncCallback<List<RiskResource>>() {

							// On Failure
							public void onFailure(Throwable caught) {
								Log.debug("Risk Report: Failed to call service.getRiskResources()");

								Dispatcher.forwardEvent(MainEvents.Error,
										caught);
								try {
									throw caught;
								} catch (IncompatibleRemoteServiceException e) {
									// This client is not compatible with the
									// server; cleanup and refresh
									// the browser
									Window.alert("IncompatibleRemoteServiceException "
											+ e);
								} catch (InvocationException e) {
									// The call didn't complete cleanly
									Window.alert("InvocationException " + e);
								} catch (Exception e) {
									// One of the 'throws' from the original
									// method
									Window.alert("Exception " + e);
								} catch (Throwable e) {
									// Last resort -- a very unexpected
									// exception
									Window.alert("throwable " + e);
								}
							}

							// On Success
							public void onSuccess(
									List<RiskResource> riskResourceResult) {
								System.out
										.println("Risk Report: Successfully called service.getRiskResources()");
								// First index in array is a message to display,
								// second is the RiskResource results set
								ArrayList<Object> eventData = new ArrayList<Object>();
								if (riskResourceResult == null) {
									eventData.add("Error: No results found!");
									Log.debug("Risk Report: No results found, informing user!");
									error = true;
								} else {
									// We have an error if there is only 1 value
									if (riskResourceResult.size() == 1) {
										eventData.add(riskResourceResult.get(0)
												.getErrorMessage());
										Log.debug("Risk Report: No results found, informing user!");
										error = true;
									} else {
										Log.debug("Risk Report: Size of RiskResource is: "
												+ riskResourceResult.size());

										String message = "Remote Query Used: Service ID: "
												+ serviceIdResult
												+ ", Provider ID: "
												+ providerIdResult
												+ ", Provider Type: "
												+ providerTypeResult
												+ ", Service Phase: "
												+ servicePhaseResult
												+ ", Unix Date: "
												+ fromDateResult
												+ " - "
												+ toDateResult;

										eventData.add(message);
										eventData.add(riskResourceResult);
										error = false;
									}
								}
								Log.debug("Risk Report: Dispatching event MainEvents.ReportGraphicRiskDiagram");
								Dispatcher.get().dispatch(
										MainEvents.ReportGraphicRiskDiagram,
										eventData);
							}
						});

				Log.debug("Risk Report: Checking autorefresh status");
				if (autoRefresh.getValue().equals(true)
						&& timerRunning == false && error == false) {
					Log.debug("Risk Report: Starting autorefresh!");
					timer.scheduleRepeating(2000);
					timerRunning = true;
					providerId.disable();
					serviceId.disable();
					fromDate.disable();
					toDate.disable();
				}

				if (error) {
					Log.debug("Risk Report: Error so canceling autorefresh!");
					if (timer != null) {
						timer.cancel();
					}
					timerRunning = false;
					providerId.enable();
					serviceId.enable();
					fromDate.enable();
					toDate.enable();
				} else if (timerRunning == true) {
					fromDate.setValue(new Date(fromDate.getValue().getTime() + 1000));
					toDate.setValue(new Date(toDate.getValue().getTime() + 1000));
					Log.debug("Risk Report: Incremented dates by 1 second");
				}

				return;
			}
		});

		// Cancel Button
		stop.addSelectionListener(new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent ce) {
				Log.debug("Risk Report: Stop button listener reports component selected");
				if (timer != null) {
					Log.debug("Risk Report: Stop button listener has stopped autorefresh timer");
					timerRunning = false;
					timer.cancel();
					providerId.enable();
					serviceId.enable();
					fromDate.enable();
					toDate.enable();
					if (autoRefresh.getValue().equals(false)) {
						stop.disable();
						submit.setText("Submit");
					}
				}
			}
		});

		// Clear Button
		clear.addSelectionListener(new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent ce) {
				Log.debug("Risk Report: Clear button listener reports component selected");

				providerId.setEmptyText("Atos");
				providerId.setValue("");
				serviceId.setEmptyText("76c44bda-4f5a-4f97-806d-011d174bea44");
				serviceId.setValue("");

				providerType.clear();
				serviceProvider.setValue(true);
				providerType.setValue(serviceProvider);
				servicePhase.clear();
				deployment.setValue(true);
				servicePhase.setValue(serviceProvider);

				Date now = new Date(); // In miliseconds
				toDate.setValue(now);
				Date fiveMinutesEarlier = new Date(now.getTime() - 60 * 5 * 1000);
				fromDate.setValue(fiveMinutesEarlier);

				if (timer != null) {
					timer.cancel();
				}
				timerRunning = false;
				error = false;

				Log.debug("Risk Report: dispatching event MainEvents.ReportGraphicCancel");
				Dispatcher.get().dispatch(MainEvents.ReportGraphicCancel);
			}
		});

		// Clear Button
		reset.addSelectionListener(new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent ce) {
				Log.debug("Risk Report: Reset button listener reports component selected");
				//Hide the button and text
				reset.hide();
				text.hide();
				//Show everything else
				providerType.show();
				servicePhase.show();
				providerId.show();
				serviceId.show();
				fromDate.show();
				toDate.show();
				options.show();
				submit.show();
				stop.show();
				clear.show();
			}
		});
		
		// Service Phase: Disable auto refresh on deployment
		servicePhase.addListener(Events.Change, new Listener<FieldEvent>() {
			@Override
			public void handleEvent(FieldEvent fe) {
				if (servicePhase.getValue().equals(deployment)) {
					Log.debug("Risk Report: servicePhase.FieldEvent heard Change disabling autorefresh");
					oldAutoRefreshValue = autoRefresh.getValue();
					autoRefresh.setValue(false);
					autoRefresh.disable();
				} else {
					autoRefresh.setValue(oldAutoRefreshValue);
					autoRefresh.enable();
					Log.debug("Risk Report: servicePhase.FieldEvent heard Change enabling autorefresh");
				}
			}
		});
		
		// Provider ID: Copy Empty text on select
		providerId.addListener(Events.Focus, new Listener<FieldEvent>() {
			@Override
			public void handleEvent(FieldEvent fe) {
				if (providerId.getValue().equals("null")) {
					Log.debug("Risk Report: providerId.FieldEvent heard Focus using empty text value/selecting all");
					providerId.setEmptyText("");
					providerId.setValue(providerIdEmptyText);
				} else {
					Log.debug("Risk Report: providerId.FieldEvent heard Focus but already have text value");
				}
			}
		});

		// Service ID: Copy Empty text on select
		serviceId.addListener(Events.Focus, new Listener<FieldEvent>() {
			@Override
			public void handleEvent(FieldEvent fe) {
				if (serviceId.getValue().equals("null")) {
					Log.debug("Risk Report: serviceId.FieldEvent heard Focus using empty text value/selecting all");
					serviceId.setEmptyText("");
					serviceId.setValue(serviceIdEmptyText);
				} else {
					Log.debug("Risk Report: serviceId.FieldEvent heard Focus but already have text value");
				}
			}
		});

		// Auto Refresh
		autoRefresh.addListener(Events.Change, new Listener<FieldEvent>() {
			@Override
			public void handleEvent(FieldEvent fe) {
				if (autoRefresh.getValue().equals(false)) {
					Log.debug("Risk Report: autoRefresh.FieldEvent heard Change disabling stop button");
					if (timerRunning == false) {
						stop.disable();
						submit.setText("Submit");
					}
				} else {
					Log.debug("Risk Report: autoRefresh.FieldEvent heard Change enabling stop button");
					stop.enable();
					submit.setText("Start");
				}
			}
		});

		// Test Data: Show relevant form elements
		testData.addListener(Events.Change, new Listener<FieldEvent>() {
			@Override
			public void handleEvent(FieldEvent fe) {
				if (testData.getValue().equals(true)) {
					Log.debug("Risk Report: testData.FieldEvent heard Change adding test data");					
					providerId.setEmptyText("");
					providerId.setValue(providerIdEmptyText);
					serviceId.setEmptyText("");
					serviceId.setValue(serviceIdEmptyText);
					fromDate.setValue(testingFromDate);
					toDate.setValue(testingToDate);
				} else {
					Log.debug("Risk Report: testData.FieldEvent heard Change removing test data");
					providerId.setEmptyText(providerIdEmptyText);
					providerId.setValue("");
					serviceId.setEmptyText(serviceIdEmptyText);
					serviceId.setValue("");
					Date now = new Date();
					Date fiveMinutesEarlier = new Date(now.getTime() - 60 * 5 * 1000);
					fromDate.setValue(fiveMinutesEarlier);
					toDate.setValue(now);
				}
			}
		});

		// Debug: Show gwt-log
		debug.addListener(Events.Change, new Listener<FieldEvent>() {
			@Override
			public void handleEvent(FieldEvent fe) {
				if (debug.getValue().equals(true)) {
					Log.setCurrentLogLevel(Log.LOG_LEVEL_DEBUG);
					DivLogger divLogger = Log.getLogger(DivLogger.class);
					divLogger.getWidget().setVisible(true);
					Log.debug("Risk Report: debug.FieldEvent heard Change enabling debug mode");
				} else {
					Log.setCurrentLogLevel(Log.LOG_LEVEL_OFF);
					DivLogger divLogger = Log.getLogger(DivLogger.class);
					divLogger.getWidget().setVisible(false);
					Log.debug("Risk Report: debug.FieldEvent heard Change disabling debug mode");
				}
			}
		});

		Log.debug("Risk Report: Finally adding formPanel to GraphicRiskReportPanel");
		this.add(formPanel);
	}
}
