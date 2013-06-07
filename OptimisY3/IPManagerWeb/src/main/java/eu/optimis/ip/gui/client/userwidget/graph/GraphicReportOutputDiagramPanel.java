/**
 * Copyright (C) 2010-2013 Barcelona Supercomputing Center
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version. This library is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser
 * General Public License for more details. You should have received a copy of
 * the GNU Lesser General Public License along with this library; if not, write
 * to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA
 * 02111-1307 USA
 */
package eu.optimis.ip.gui.client.userwidget.graph;

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
import com.extjs.gxt.ui.client.widget.form.SpinnerField;
import com.extjs.gxt.ui.client.widget.form.TextArea;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.toolbar.LabelToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import eu.optimis.ip.gui.client.IPManagerWebServiceAsync;
import eu.optimis.ip.gui.client.MainEvents;
import eu.optimis.ip.gui.client.resources.Constants;
import java.util.ArrayList;

/**
 * Center Screen displaying Components Output text.
 *
 * @author jsubirat
 */
public class GraphicReportOutputDiagramPanel extends ContentPanel {

    private TextArea output;
    private String currentOutput;
    private String selectedComponent;
    private IPManagerWebServiceAsync service;
    private SimpleComboBox<String> listComponents, listLogs;
    private Timer timer;
    private SpinnerField numberOfLines;
    private boolean updateLogTextArea = true;
    private LabelToolItem labelStatus;
    private boolean logBeingDisplayed = false;

    public GraphicReportOutputDiagramPanel() {

        timer = new Timer() {
            public void run() {
                if (isUpdateNecessary()) {
                    updateLogContent();
                }
            }
        };

        setHeading(Constants.MENU_COMPONENTS_NAME);
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
        listComponents.setWidth(300);
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
        listLogs.setWidth(300);
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
        //numberOfLines.setFieldLabel(FeedbackAuthoringStrings.MAX_NUM_MSGS_PROV_LABEL); 
        numberOfLines.setIncrement(1);
        numberOfLines.getPropertyEditor().setType(Integer.class);
        numberOfLines.getPropertyEditor().setFormat(NumberFormat.getDecimalFormat());
        numberOfLines.setMinValue(30);
        numberOfLines.setValue(40);
        numberOfLines.setMaxValue(500);

//        numberOfLines = new NumberField();
//        numberOfLines.setMaxValue(500);
//        numberOfLines.setMinValue(10);
//        numberOfLines.setValue(30);
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
            service = (IPManagerWebServiceAsync) Registry.get("guiservice");
            service.getLog(selectedComponent, listLogs.getSimpleValue(), numberOfLines.getValue().intValue(), new AsyncCallback<String>() {
                @Override
                public void onFailure(Throwable caught) {
                    Dispatcher.forwardEvent(MainEvents.Error, caught);
                    logBeingDisplayed = false;
                    labelStatus.setLabel("Status: ERROR.");
                }

                @Override
                public void onSuccess(String result) {
                    if (!result.equals(currentOutput)) {
                        logBeingDisplayed = true;
                        labelStatus.setLabel("Status: DISPLAYING");
                        updateLogTextArea = true;
                        currentOutput = result;
                        output.setValue(result);
                    }
                }
            });
        } else {
            output.setEmptyText("Please select a log to display.");
        }
    }

    private void getComponentLogList() {
        service = (IPManagerWebServiceAsync) Registry.get("guiservice");
        service.getComponentLogList(new AsyncCallback<ArrayList<String>>() {
            @Override
            public void onFailure(Throwable caught) {

                Dispatcher.forwardEvent(MainEvents.Error, caught);

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
            }
        });
    }

    public void getLogList() {
        service = (IPManagerWebServiceAsync) Registry.get("guiservice");
        service.getLogList(selectedComponent, new AsyncCallback<ArrayList<String>>() {
            @Override
            public void onFailure(Throwable caught) {

                Dispatcher.forwardEvent(MainEvents.Error, caught);

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
            }
        });
    }
}

/*public class GraphicReportOutputDiagramPanel extends ContentPanel {

 private TextArea output;
 private String currentOutput;
 private String selectedComponent;
 private IPManagerWebServiceAsync service;

 public GraphicReportOutputDiagramPanel() {

 setHeading(Constants.MENU_COMPONENTS_NAME);
 setLayout(new FitLayout());
 output = new TextArea();
 output.addStyleName("demo-TextArea");
 output.setWidth("800px");
 output.setHeight("400px");
 output.setReadOnly(true);
 output.setEmptyText("Loading...");
 output.setVisible(true);
 add(output);

 currentOutput = "Loading...";

 Timer t = new Timer() {

 public void run() {
 if (isUpdateNecessary()) {
 updateLog();
 }

 }
 };
 // Schedule the timer to run once in 5 seconds.
 t.scheduleRepeating(5000);
 }

 public boolean isUpdateNecessary() {
 if (this.isVisible()) {
 return true;
 } else {
 return false;
 }
 }

 public void setLog(String selectedComponent) {

 this.selectedComponent = selectedComponent;
 setHeading(selectedComponent + " output:");
 }

 public void updateLog() {
 service = (IPManagerWebServiceAsync) Registry.get("guiservice");
 service.getLog(selectedComponent, new AsyncCallback<String>() {

 @Override
 public void onFailure(Throwable caught) {
 Dispatcher.forwardEvent(MainEvents.Error, caught);
 }

 @Override
 public void onSuccess(String result) {
 if (!result.equals(currentOutput)) {
 currentOutput = result;
 output.setValue(result);
 }
 }
 });
 }
 }*/
