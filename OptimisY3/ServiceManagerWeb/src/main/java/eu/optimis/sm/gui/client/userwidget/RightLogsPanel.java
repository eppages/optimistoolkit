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

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.form.SimpleComboBox;
import com.extjs.gxt.ui.client.widget.form.TextArea;
import com.extjs.gxt.ui.client.widget.form.SpinnerField;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.toolbar.LabelToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Timer;
import eu.optimis.sm.gui.client.ServiceManagerWebServiceAsync;
import eu.optimis.sm.gui.client.MainEvents;
import eu.optimis.sm.gui.utils.Constants;
import java.util.ArrayList;


public class RightLogsPanel extends ContentPanel {
    private TextArea output;
    private String currentOutput;
    private String selectedComponent;
    private ServiceManagerWebServiceAsync service;
    private SimpleComboBox<String> listComponents, listLogs;
    private Timer timer;
    private SpinnerField numberOfLines;
    private boolean updateLogTextArea = true;
    private LabelToolItem labelStatus;
    private boolean logBeingDisplayed = false;
	private Widget textArea = new TextArea();

    public RightLogsPanel() {

        timer = new Timer() {
            public void run() {
                if (isUpdateNecessary()) {
                    updateLogContent();
                }
            }
        };

        setHeading(Constants.MENU_LOGS);
        setLayout(new FitLayout());

        ToolBar toolBarSelection = new ToolBar();
        LabelToolItem labelComponents = new LabelToolItem("Component:   ");
        toolBarSelection.add(labelComponents);
        labelComponents.setVisible(true);
        setTopComponent(toolBarSelection);

        listComponents = new SimpleComboBox<String>();
        listComponents.setForceSelection(true);
        listComponents.setEditable(false);
        listComponents.setTriggerAction(ComboBox.TriggerAction.ALL);
        listComponents.setEmptyText("-Choose a component-");
        listComponents.setFieldLabel("Component");
        listComponents.setWidth(200);
        listComponents.addSelectionChangedListener(new SelectionChangedListener() {
            @Override
            public void selectionChanged(SelectionChangedEvent se) {
                timer.cancel();
                selectedComponent = listComponents.getSimpleValue();
                output.setEmptyText("Select a component and log file to display and press \"Load\"");
                getLogList();
            }
        });
        getComponentLogList();
        toolBarSelection.add(listComponents);

        LabelToolItem labelFiles = new LabelToolItem("    File:   ");
        toolBarSelection.add(labelFiles);
        labelFiles.setVisible(true);

        listLogs = new SimpleComboBox<String>();
        listLogs.setForceSelection(true);
        listLogs.setEditable(false);
        listLogs.setTriggerAction(ComboBox.TriggerAction.ALL);
        listLogs.setEmptyText("-Choose a log file-");
        listLogs.setFieldLabel("Log");
        listLogs.setWidth(200);
        listLogs.addSelectionChangedListener(new SelectionChangedListener() {
            @Override
            public void selectionChanged(SelectionChangedEvent se) {
                timer.cancel();
                output.setEmptyText("Select a component and log file to display and press \"Load\"");
                output.clear();
            }
        });
        toolBarSelection.add(listLogs);

        LabelToolItem labelLines = new LabelToolItem("    Number of lines:   ");
        toolBarSelection.add(labelLines);
        labelLines.setVisible(true);

        numberOfLines = new SpinnerField();
        numberOfLines.setIncrement(1);
        numberOfLines.getPropertyEditor().setType(Integer.class);
        numberOfLines.getPropertyEditor().setFormat(NumberFormat.getDecimalFormat());
        numberOfLines.setMinValue(1);
        numberOfLines.setValue(500);
        numberOfLines.setMaxValue(5000);
        toolBarSelection.add(numberOfLines);

        Button loadButton = new Button("Load");
        toolBarSelection.add(loadButton);
        loadButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
            @Override
            public void componentSelected(ButtonEvent ce) {
                updateLogContent();
                timer.scheduleRepeating(5000);
            }
        });

        labelStatus = new LabelToolItem("");
        toolBarSelection.add(labelStatus);
        labelStatus.setVisible(true);

        currentOutput = "Select a component and log file to display and press \"Load\"";
        output = new TextArea();
        output.addStyleName("demo-TextArea");
        output.setWidth("800px");
        output.setHeight("400px");
        output.setReadOnly(true);
        output.setEmptyText("Select a component and log file to display and press \"Load\"");
        output.setVisible(true);
        output.sinkEvents(Event.ONCLICK);
        output.addHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                if (logBeingDisplayed == true) {
                    if (updateLogTextArea == true) {
                        updateLogTextArea = false;
                        labelStatus.setLabel("Status: LOCKED");
                    } else {
                        updateLogTextArea = true;
                        labelStatus.setLabel("Status: DISPLAYING");
                    }
                }
            }
        }, ClickEvent.getType());
        add(output);
    }

    public boolean isUpdateNecessary() {
        if (this.isVisible() && updateLogTextArea == true) {
            return true;
        } else {
            return false;
        }
    }

    public void updateLogContent() {
        if (listLogs.getSelectedIndex() != -1) {
            service = (ServiceManagerWebServiceAsync) Registry.get("guiservice");
            service.getLog(LeftPanelLogin.session_id, selectedComponent, listLogs.getSimpleValue(),
            		numberOfLines.getValue().intValue(), new AsyncCallback<String>() {
                @Override
                public void onFailure(Throwable caught) {
                    Dispatcher.forwardEvent(MainEvents.error, caught);
                    logBeingDisplayed = false;
                    labelStatus.setLabel("Status: ERROR.");
                }

                @Override
                public void onSuccess(String result) {
                	if(result!=null) {
                		if (!result.equals(currentOutput)) {
                        logBeingDisplayed = true;
                        labelStatus.setLabel("Status: DISPLAYING");
                        updateLogTextArea = true;
                        currentOutput = result;
                        output.setValue(result);
                		}
                	}
                	else {
        				removeAll();
                		((TextArea)textArea).setValue("Session time elapsed! Please log out, log in again and refresh the selected option");
                		add(textArea);
                		layout(true);
                	}
                }
            });
        } else {
            output.setEmptyText("Please select a log to display.");
        }
    }

    private void getComponentLogList() {
        service = (ServiceManagerWebServiceAsync) Registry.get("guiservice");
        service.getComponentLogList(LeftPanelLogin.session_id, new AsyncCallback<ArrayList<String>>() {
            @Override
            public void onFailure(Throwable caught) {
                Dispatcher.forwardEvent(MainEvents.error, caught);
            }

            @Override
            public void onSuccess(ArrayList<String> result) {
                if (result != null) {
                    listComponents.removeAll();
                    listComponents.clear();
                    for (String component : result) {
                        listComponents.add(component);
                    }
                }
                else {
    				removeAll();
            		((TextArea)textArea).setValue("Session time elapsed! Please log out, log in again and refresh the selected option");
            		add(textArea);
            		layout(true);
                }
            }
        });
    }

    public void getLogList() {
        service = (ServiceManagerWebServiceAsync) Registry.get("guiservice");
        service.getLogList(LeftPanelLogin.session_id, selectedComponent, new AsyncCallback<ArrayList<String>>() {
            @Override
            public void onFailure(Throwable caught) {
                Dispatcher.forwardEvent(MainEvents.error, caught);
            }

            @Override
            public void onSuccess(ArrayList<String> result) {
                if (result != null) {
                    listLogs.removeAll();
                    listLogs.clear();
                    for (String file : result) {
                        listLogs.add(file);
                    }
                }
                else {
    				removeAll();
            		((TextArea)textArea).setValue("Session time elapsed! Please log out, log in again and refresh the selected option");
            		add(textArea);
            		layout(true);
                }
            }
        });
    }
  }
