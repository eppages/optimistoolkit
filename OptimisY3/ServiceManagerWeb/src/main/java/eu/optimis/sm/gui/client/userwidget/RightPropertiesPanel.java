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
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.toolbar.LabelToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
//import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Widget;

import eu.optimis.sm.gui.client.ServiceManagerWebServiceAsync;
import eu.optimis.sm.gui.client.MainEvents;
import eu.optimis.sm.gui.utils.Constants;
import java.util.ArrayList;

public class RightPropertiesPanel extends ContentPanel {

    private TextArea output;
    private String currentOutput;
    private String selectedComponent;
    private ServiceManagerWebServiceAsync service;
    private SimpleComboBox<String> listComponents, listFiles;
	private Widget textArea = new TextArea();

    public RightPropertiesPanel() {

        setHeading(Constants.MENU_PROPS);
        setLayout(new FitLayout());

        ToolBar toolBarSelection = new ToolBar();
        LabelToolItem labelComponents = new LabelToolItem("Component:   ");
        toolBarSelection.add(labelComponents);
        labelComponents.setVisible(true);
        setTopComponent(toolBarSelection);

        listComponents = new SimpleComboBox<String>();
        updateFileList("Folders", listComponents);
        listComponents.add(Constants.COMPONENT_SPMANAGERWEB_NAME);
        listComponents.add("Test");
        listComponents.setForceSelection(true);
        listComponents.setEditable(false);
        listComponents.setTriggerAction(ComboBox.TriggerAction.ALL);
        listComponents.setEmptyText("- Choose a component -");
        listComponents.setFieldLabel("Component");
        listComponents.setWidth(200);
        toolBarSelection.add(listComponents);

        listComponents.addSelectionChangedListener(new SelectionChangedListener() {

            @Override
            public void selectionChanged(SelectionChangedEvent se) {
                selectedComponent = listComponents.getSimpleValue();
                output.setEmptyText("Select a component and configuration file to display and press \"Load\"");
                updateFileList(selectedComponent, listFiles);
            }
        });

        LabelToolItem labelFiles = new LabelToolItem("    File:   ");
        toolBarSelection.add(labelFiles);
        labelFiles.setVisible(true);

        listFiles = new SimpleComboBox<String>();
        listFiles.setForceSelection(true);
        listFiles.setEditable(false);
        listFiles.setTriggerAction(ComboBox.TriggerAction.ALL);
        listFiles.setEmptyText("-Choose a file-");
        listFiles.setFieldLabel("File");
        listFiles.setAutoWidth(false);
        listFiles.setWidth(250);
        toolBarSelection.add(listFiles);

        listFiles.addSelectionChangedListener(new SelectionChangedListener() {

            @Override
            public void selectionChanged(SelectionChangedEvent se) {
                output.setEmptyText("Select a component and configuration file to display and press \"Load\"");
                output.clear();
            }
        });

        Button loadButton = new Button("Load");
        toolBarSelection.add(loadButton);

        loadButton.addSelectionListener(new SelectionListener<ButtonEvent>() {

            @Override
            public void componentSelected(ButtonEvent ce) {
                updateFileContent();
            }
        });

        currentOutput = "Select a component and configuration file to display and press \"Load\"";
        output = new TextArea();
        output.addStyleName("demo-TextArea");
        output.setWidth("800px");
        output.setHeight("400px");
        output.setReadOnly(true);
        output.setEmptyText("Select a component and configuration file to display and press \"Load\"");
        output.setVisible(true);
        add(output);
    }
//-------------------------------------------------------------------------------------
    public void updateFileContent() {
        if (listFiles.getSelectedIndex() != -1) {
            service = (ServiceManagerWebServiceAsync) Registry.get("guiservice");
            service.getFile(LeftPanelLogin.session_id, selectedComponent, listFiles.getSimpleValue(), new AsyncCallback<String>() {

                @Override
                public void onFailure(Throwable caught) {
                    Dispatcher.forwardEvent(MainEvents.error, caught);
                }

                @Override
                public void onSuccess(String result) {
                	if(result!=null){
                    if (!result.equals(currentOutput)) {
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
            output.setEmptyText("Please select a file to display.");
        }
    }
//-------------------------------------------------------------------------------------
    public void updateFileList(String selectedComponent, final SimpleComboBox<String> listFiles) {
        service = (ServiceManagerWebServiceAsync) Registry.get("guiservice");
        service.getFileList(LeftPanelLogin.session_id, selectedComponent, new AsyncCallback<ArrayList<String>>() {
            @Override
            public void onFailure(Throwable caught) {
                Dispatcher.forwardEvent(MainEvents.error, caught);
            }
            @Override
            public void onSuccess(ArrayList<String> result) {
                if (result != null) {
                    listFiles.removeAll();
                    listFiles.clear();
                    for (String file : result) {
                        listFiles.add(file);
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
//-------------------------------------------------------------------------------------
}
