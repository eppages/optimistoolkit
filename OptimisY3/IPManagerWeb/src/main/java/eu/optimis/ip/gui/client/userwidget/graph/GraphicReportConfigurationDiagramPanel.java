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
import com.extjs.gxt.ui.client.widget.form.TextArea;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.toolbar.LabelToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
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
public class GraphicReportConfigurationDiagramPanel extends ContentPanel {

    private TextArea output;
    private String currentOutput;
    private String selectedComponent;
    private IPManagerWebServiceAsync service;
    private SimpleComboBox<String> listComponents, listFiles;

    public GraphicReportConfigurationDiagramPanel() {

        setHeading(Constants.MENU_COMPONENTS_CONFIGURATION);
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
                selectedComponent = listComponents.getSimpleValue();
                output.setEmptyText("Select a component and configuration file to display and press \"Load\"");
                getFileList();
            }
        });
        getComponentList();
        toolBarSelection.add(listComponents);

        LabelToolItem labelFiles = new LabelToolItem("    File:   ");
        toolBarSelection.add(labelFiles);
        labelFiles.setVisible(true);

        listFiles = new SimpleComboBox<String>();
        listFiles.setForceSelection(true);
        listFiles.setEditable(false);
        listFiles.setTriggerAction(ComboBox.TriggerAction.ALL);
        listFiles.setEmptyText("-Choose a file-");
        listFiles.setFieldLabel("File");
        listFiles.setWidth(300);
        listFiles.addSelectionChangedListener(new SelectionChangedListener() {
            @Override
            public void selectionChanged(SelectionChangedEvent se) {
                output.setEmptyText("Select a component and configuration file to display and press \"Load\"");
                output.clear();
            }
        });
        toolBarSelection.add(listFiles);

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

    public void updateFileContent() {
        if (listFiles.getSelectedIndex() != -1) {
            service = (IPManagerWebServiceAsync) Registry.get("guiservice");
            service.getFile(selectedComponent, listFiles.getSimpleValue(), new AsyncCallback<String>() {
                @Override
                public void onFailure(Throwable caught) {
                    Dispatcher.forwardEvent(MainEvents.Error, caught);
                }

                @Override
                public void onSuccess(String result) {
                    if (!result.equals(currentOutput)) {
                        output.setValue(result);
                    }
                }
            });
        } else {
            output.setEmptyText("Please select a file to display.");
        }
    }

    private void getComponentList() {
        service = (IPManagerWebServiceAsync) Registry.get("guiservice");
        service.getComponentConfigurationList(new AsyncCallback<ArrayList<String>>() {
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

    public void getFileList() {
        service = (IPManagerWebServiceAsync) Registry.get("guiservice");
        service.getFileList(selectedComponent, new AsyncCallback<ArrayList<String>>() {
            @Override
            public void onFailure(Throwable caught) {

                Dispatcher.forwardEvent(MainEvents.Error, caught);

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
            }
        });
    }
}
