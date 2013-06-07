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

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.Style;
import com.extjs.gxt.ui.client.event.*;
import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.*;
import com.extjs.gxt.ui.client.widget.layout.FormData;
import com.extjs.gxt.ui.client.widget.layout.FormLayout;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import eu.optimis.mi.gui.client.MainEvents;
import eu.optimis.mi.gui.client.MonitoringManagerWebServiceAsync;
import eu.optimis.mi.gui.client.model.EcoResource;
import eu.optimis.mi.gui.client.model.EcoServiceDeploymentInfoData;
import eu.optimis.mi.gui.client.model.MetricName;
import eu.optimis.mi.gui.client.resources.OptimisResource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
//import com.allen_sauer.gwt.log.client.Log;

public class GraphicEcoReportPanel extends ContentPanel {

    private MonitoringManagerWebServiceAsync service;
    private List<MetricName> names;
    private ListStore<MetricName> store = new ListStore<MetricName>();
    private String submission;
    private String ecoLevel;
    //Fields present in the left side of the display
    private SimpleComboBox<String> listRepresentations;
    private SimpleComboBox<String> listMetricsIP;
    private SimpleComboBox<String> listMetricsSP;
    private String metric = "energy";
    private TextField<String> resourceId;
    private DateField dateFrom, dateTo;
    private TimeField timeFrom, timeTo;
    private Button submit, cancel;//, refresh;
    private String displayDeploymentWhenVisible = "none";

    public GraphicEcoReportPanel() {
        setHeading("Eco Graphic Report");
        createForm();
        adaptToGETParameters();
    }

    private void adaptToGETParameters() {
        String side = Window.Location.getParameter("side");
        String stage = Window.Location.getParameter("stage");
        String level = Window.Location.getParameter("level");
        String identifier = Window.Location.getParameter("identifier");

        //Log.debug("Received TREC. Side: " + side + " Stage: " + stage + " Level: " + level + " Identifier: " + identifier);

        if (side == null) {
            return;
        }

        //Display all the eco options, but it still has to be limited depending on the side where we're executing.
        if (stage == null) {
            listRepresentations.removeAll();
            if (side.equalsIgnoreCase("ip")) {
                listRepresentations.add("Service ecoefficiency (IP)");
                listRepresentations.add("VM ecoefficiency");
                listRepresentations.add("Infrastructure ecoefficiency");
                listRepresentations.add("Node ecoefficiency");
                listRepresentations.add("All nodes ecoefficiency");
                listRepresentations.add("Service Deployment (IP)");
            } else {
                listRepresentations.add("Service ecoefficiency (SP)");
                listRepresentations.add("Service Deployment (SP)");
            }
            return;
        }

        if (stage.equalsIgnoreCase("operation")) {
            if (level != null) {
                if (!level.equalsIgnoreCase("infrastructure")) {
                    if (identifier == null) {
                        //It needs an identifier, therefore return as it is incorrect.
                        return;
                    }
                }

                listRepresentations.setVisible(false);
                if (level.equalsIgnoreCase("infrastructure")) {
                    ecoLevel = "infrastructure";
                    resourceId.setVisible(false);
                    listMetricsIP.setVisible(true);
                } else if (level.equalsIgnoreCase("node")) {
                    ecoLevel = "node";
                    resourceId.setVisible(true);
                    resourceId.setFieldLabel("Node Identifier");
                    resourceId.setValue(identifier);
                    resourceId.setReadOnly(true);
                    listMetricsIP.setVisible(true);
                } else if (level.equalsIgnoreCase("service")) {
                    if (side.equalsIgnoreCase("ip")) {
                        ecoLevel = "serviceIP";
                        listMetricsIP.setVisible(true);
                    } else {
                        ecoLevel = "serviceSP";
                        listMetricsSP.setVisible(true);
                    }
                    resourceId.setVisible(true);
                    resourceId.setFieldLabel("Service Identifier");
                    resourceId.setValue(identifier);
                    resourceId.setReadOnly(true);
                } else if (level.equalsIgnoreCase("vm")) {
                    ecoLevel = "vm";
                    resourceId.setVisible(true);
                    resourceId.setFieldLabel("VM Identifier");
                    resourceId.setValue(identifier);
                    resourceId.setReadOnly(true);
                    listMetricsIP.setVisible(true);
                }

                dateFrom.setVisible(true);
                timeFrom.setVisible(true);
                dateTo.setVisible(true);
                timeTo.setVisible(true);
                submit.setVisible(true);
                cancel.setVisible(true);
                //refresh.setVisible(false);
            }
        } else if (stage.equalsIgnoreCase("deployment")) {
            //Deployment information tabs.
            listRepresentations.setVisible(false);
            resourceId.setVisible(false);
            dateFrom.setVisible(false);
            timeFrom.setVisible(false);
            dateTo.setVisible(false);
            timeTo.setVisible(false);
            submit.setVisible(false);
            cancel.setVisible(false);
            //refresh.setVisible(true);
            listRepresentations.removeAll();

            if (side.equalsIgnoreCase("ip")) {
                //listRepresentations.add("Service Deployment (IP)");
                displayDeploymentWhenVisible = "ip";
            } else {
                //listRepresentations.add("Service Deployment (SP)");
                displayDeploymentWhenVisible = "sp";
            }
        }
    }

    public void reloadPanel() {
        if(displayDeploymentWhenVisible.equalsIgnoreCase("ip")) {
            serviceDeploymentSelected();
        } else if(displayDeploymentWhenVisible.equalsIgnoreCase("sp")) {
            serviceDeploymentSelectedSP();
        }
    }

    /* SKELETON
     * 
     * private void adaptToGETParameters() {
     String side = Window.Location.getParameter("side");
     String stage = Window.Location.getParameter("stage");
     String level = Window.Location.getParameter("level");
     String identifier = Window.Location.getParameter("identifier");

     if (side == null || stage == null) {
     return;
     }

     if (stage.equalsIgnoreCase("operation")) {
     if (level != null) {
     if (!level.equalsIgnoreCase("infrastructure")) {
     if (identifier == null) {
     //It needs an identifier, therefore return as it is incorrect.
     return;
     }
     }

     if (level.equalsIgnoreCase("infrastructure")) {

     } else if (level.equalsIgnoreCase("node")) {

     } else if (level.equalsIgnoreCase("service")) {
     if (side.equalsIgnoreCase("ip")) {

     } else {

     }
     } else if (level.equalsIgnoreCase("vm")) {

     }
     }
     } else if (stage.equalsIgnoreCase("deployment")) {
     //Deployment information tabs.

     }
     }*/
    @SuppressWarnings({"rawtypes", "unchecked", "deprecation"}) //FIXME
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


        //Adding Fields: representation type.     
        listRepresentations = new SimpleComboBox<String>();
        listRepresentations.add("Service ecoefficiency (IP)");
        listRepresentations.add("Service ecoefficiency (SP)");
        listRepresentations.add("VM ecoefficiency");
        listRepresentations.add("Infrastructure ecoefficiency");
        listRepresentations.add("Node ecoefficiency");
        listRepresentations.add("All nodes ecoefficiency");
        listRepresentations.add("Service Deployment (IP)");
        listRepresentations.add("Service Deployment (SP)");
        listRepresentations.setFieldLabel("Representation:");
        listRepresentations.setForceSelection(true);
        listRepresentations.setEditable(false);
        listRepresentations.setAutoWidth(true);
        listRepresentations.setTriggerAction(ComboBox.TriggerAction.ALL);
        listRepresentations.setEmptyText("-Choose a representation-");
        fp.add(listRepresentations);

        listRepresentations.addSelectionChangedListener(new SelectionChangedListener() {
            @Override
            public void selectionChanged(SelectionChangedEvent se) {
                String selected = listRepresentations.getSimpleValue();
                Dispatcher.get().dispatch(MainEvents.HideReportGraphicEcoServiceDeploymentInfo, null);

                /*
                 * if (selected.equalsIgnoreCase("-Choose a representation-")) {
                 * resourceId.setVisible(false); dateFrom.setVisible(false);
                 * timeFrom.setVisible(false); dateTo.setVisible(false);
                 * timeTo.setVisible(false); submit.setVisible(false);
                 * cancel.setVisible(false); } else
                 */ if (selected.equalsIgnoreCase("Service Deployment (IP)")) {
                    resourceId.setVisible(false);
                    dateFrom.setVisible(false);
                    timeFrom.setVisible(false);
                    dateTo.setVisible(false);
                    timeTo.setVisible(false);
                    submit.setVisible(false);
                    cancel.setVisible(false);
                    listMetricsIP.setVisible(false);
                    listMetricsSP.setVisible(false);
                    serviceDeploymentSelected();
                    displayDeploymentWhenVisible = "ip";
                } else if (selected.equalsIgnoreCase("Service Deployment (SP)")) {
                    resourceId.setVisible(false);
                    dateFrom.setVisible(false);
                    timeFrom.setVisible(false);
                    dateTo.setVisible(false);
                    timeTo.setVisible(false);
                    submit.setVisible(false);
                    cancel.setVisible(false);
                    listMetricsIP.setVisible(false);
                    listMetricsSP.setVisible(false);
                    serviceDeploymentSelectedSP();
                    displayDeploymentWhenVisible = "sp";
                } else {
                    //Dispatcher.get().dispatch(MainEvents.HideReportGraphicEcoServiceDeploymentInfo, null);
                    if (selected.equalsIgnoreCase("Service ecoefficiency (IP)")) {
                        ecoLevel = "serviceIP";
                        resourceId.setVisible(true);
                        resourceId.setFieldLabel("Service Identifier");
                        listMetricsIP.setVisible(true);
                        listMetricsSP.setVisible(false);
                    } else if (selected.equalsIgnoreCase("Service ecoefficiency (SP)")) {
                        ecoLevel = "serviceSP";
                        resourceId.setVisible(true);
                        resourceId.setFieldLabel("Service Identifier");
                        listMetricsIP.setVisible(false);
                        listMetricsSP.setVisible(true);
                    } else if (selected.equalsIgnoreCase("VM ecoefficiency")) {
                        ecoLevel = "vm";
                        resourceId.setVisible(true);
                        resourceId.setFieldLabel("VM Identifier");
                        listMetricsIP.setVisible(true);
                        listMetricsSP.setVisible(false);
                    } else if (selected.equalsIgnoreCase("Infrastructure ecoefficiency")) {
                        ecoLevel = "infrastructure";
                        resourceId.setVisible(false);
                        listMetricsIP.setVisible(true);
                        listMetricsSP.setVisible(false);
                    } else if (selected.equalsIgnoreCase("Node ecoefficiency")) {
                        ecoLevel = "node";
                        resourceId.setVisible(true);
                        resourceId.setFieldLabel("Node Identifier");
                        listMetricsIP.setVisible(true);
                        listMetricsSP.setVisible(false);
                    } else if (selected.equalsIgnoreCase("All nodes ecoefficiency")) {
                        ecoLevel = "all nodes";
                        resourceId.setVisible(false);
                        listMetricsIP.setVisible(true);
                        listMetricsSP.setVisible(false);
                    }

                    dateFrom.setVisible(true);
                    timeFrom.setVisible(true);
                    dateTo.setVisible(true);
                    timeTo.setVisible(true);
                    submit.setVisible(true);
                    cancel.setVisible(true);
                    displayDeploymentWhenVisible = "none";
                }
            }
        });

        //Resource Id field.
        resourceId = new TextField<String>();
        resourceId.setFieldLabel("Service Identifier");
        // resourceId.setStyleName("formComponent");
        resourceId.setAllowBlank(true);
        fp.add(resourceId, formData);

        //Metric to display (IP).     
        listMetricsIP = new SimpleComboBox<String>();
        listMetricsIP.add("Energy efficiency");
        listMetricsIP.add("Ecological efficiency");
        listMetricsIP.add("Performance");
        listMetricsIP.add("Power");
        listMetricsIP.add("CO2 Emission rate");
        listMetricsIP.setFieldLabel("Metric:");
        listMetricsIP.setForceSelection(true);
        listMetricsIP.setEditable(false);
        listMetricsIP.setAutoWidth(true);
        listMetricsIP.setTriggerAction(ComboBox.TriggerAction.ALL);
        listMetricsIP.setEmptyText("-Choose a metric-");
        fp.add(listMetricsIP);

        listMetricsIP.addSelectionChangedListener(new SelectionChangedListener() {
            @Override
            public void selectionChanged(SelectionChangedEvent se) {
                String selected = listMetricsIP.getSimpleValue();

                if (selected.equalsIgnoreCase("Energy efficiency")) {
                    metric = "energy";
                } else if (selected.equalsIgnoreCase("Ecological efficiency")) {
                    metric = "ecological";
                } else if (selected.equalsIgnoreCase("Performance")) {
                    metric = "performance";
                } else if (selected.equalsIgnoreCase("Power")) {
                    metric = "power";
                } else if (selected.equalsIgnoreCase("CO2 Emission rate")) {
                    metric = "co2";
                }
            }
        });

        //Metric to display (SP).     
        listMetricsSP = new SimpleComboBox<String>();
        listMetricsSP.add("Energy efficiency");
        listMetricsSP.add("Ecological efficiency");
        listMetricsSP.setFieldLabel("Metric:");
        listMetricsSP.setForceSelection(true);
        listMetricsSP.setEditable(false);
        listMetricsSP.setAutoWidth(true);
        listMetricsSP.setTriggerAction(ComboBox.TriggerAction.ALL);
        listMetricsSP.setEmptyText("-Choose a metric-");
        fp.add(listMetricsSP);

        listMetricsSP.addSelectionChangedListener(new SelectionChangedListener() {
            @Override
            public void selectionChanged(SelectionChangedEvent se) {
                String selected = listMetricsSP.getSimpleValue();

                if (selected.equalsIgnoreCase("Energy efficiency")) {
                    metric = "energy";
                } else if (selected.equalsIgnoreCase("Ecological efficiency")) {
                    metric = "ecological";
                }
            }
        });

        //Date from and time from fields.
        dateFrom = new DateField();
        dateFrom.setValue(new Date());
        dateFrom.setFieldLabel("Date from");
        fp.add(dateFrom, formData);
        timeFrom = new TimeField();
        timeFrom.setLabelSeparator("");
        Date time = new Date(0, 0, 0);
        timeFrom.setDateValue(time);
        fp.add(timeFrom, formData);


        //Date to and time to fields.
        dateTo = new DateField();
        dateTo.setValue(new Date());
        dateTo.setFieldLabel("Date to");
        fp.add(dateTo, formData);
        timeTo = new TimeField();
        timeTo.setLabelSeparator("");
        @SuppressWarnings("unused")
        Date time2 = new Date(23, 59, 0); //FIXME
        timeTo.setDateValue(new Date());
        fp.add(timeTo, formData);


        //Submit button.
        submit = new Button("Submit");
        fp.addButton(submit);

        //Cancel button.
        cancel = new Button("Cancel");
        fp.addButton(cancel);

        //Start the screen with default values.
        resourceId.setVisible(false);
        listMetricsIP.setVisible(false);
        listMetricsSP.setVisible(false);
        dateFrom.setVisible(false);
        timeFrom.setVisible(false);
        dateTo.setVisible(false);
        timeTo.setVisible(false);
        submit.setVisible(false);
        cancel.setVisible(false);
        //refresh.setVisible(false);

        //Define action to perform when we click "Submit". Note that "Service Deployment" option doesn't apply here as "Submit" is disabled when selected.
        submit.addSelectionListener(new SelectionListener<ButtonEvent>() {
            @Override
            public void componentSelected(ButtonEvent ce) {

                if (resourceId.getValue() == null) {
                    if (ecoLevel.equalsIgnoreCase("infrastructure") || ecoLevel.equalsIgnoreCase("all nodes")) {
                        ;
                    } else {
                        ArrayList eventData = new ArrayList();
                        eventData.add("Please type the ID! ecolevel: " + ecoLevel);
                        Dispatcher.get().dispatch(MainEvents.ReportGraphicEcoDiagram,
                                eventData);
                        return;
                    }
                }

                if (dateFrom.getValue() == null || timeFrom.getValue() == null) {
                    ArrayList eventData = new ArrayList();
                    eventData.add("Please assign the date and time from!");
                    Dispatcher.get().dispatch(MainEvents.ReportGraphicEcoDiagram,
                            eventData);
                    return;
                }

                if (dateTo.getValue() == null || timeTo.getValue() == null) {
                    ArrayList eventData = new ArrayList();
                    eventData.add("Please assign the date and time to!");
                    Dispatcher.get().dispatch(MainEvents.ReportGraphicEcoDiagram,
                            eventData);
                    return;
                }


                String id = "";
                if (ecoLevel.equalsIgnoreCase("infrastructure") || ecoLevel.equalsIgnoreCase("all nodes")) {
                    submission = ecoLevel + " ecoefficiency";
                } else {
                    id = resourceId.getValue().trim();
                    submission = ecoLevel + " ecoefficiency for " + id;
                }

                //From date time. No problem if not used: already initialized (never null)
                Date gwtDate = dateFrom.getValue();
                DateTimeFormat df = DateTimeFormat.getFormat("yyyyMMdd");
                String dt = df.format(gwtDate);
                Date gwTime = timeFrom.getDateValue();
                DateTimeFormat tf = DateTimeFormat.getFormat("HHmm");
                String tt = tf.format(gwTime);
                final String from = new String(dt + tt);
                String dateFrom = dt + tt + "00";

                //To date time. No problem if not used: already initialized (never null)
                Date gwtDate2 = dateTo.getValue();
                String dt2 = df.format(gwtDate2);
                Date gwTime2 = timeTo.getDateValue();
                String tt2 = tf.format(gwTime2);
                final String to = new String(dt2 + tt2);
                String dateTo = dt2 + tt2 + "59";

                //Contact with the server side and send the query.
                service = (MonitoringManagerWebServiceAsync) Registry.get("guiservice");
                if (ecoLevel.equalsIgnoreCase("node")) {
                    service.getNodeEcoResources(id, dateFrom, dateTo, metric, new AsyncCallback<List<EcoResource>>() {
                        public void onFailure(Throwable caught) {
                            Dispatcher.forwardEvent(MainEvents.Error,
                                    caught);
                        }

                        public void onSuccess(List<EcoResource> result) {
                            ArrayList eventData = new ArrayList();
                            if (result.size() == 0) {
                                eventData.add("Sorry, There are no suitable records.");
                            } else {
                                eventData.add(submission);
                                eventData.add(result);
                                eventData.add(from);
                                eventData.add(to);
                            }
                            Dispatcher.get().dispatch(
                                    MainEvents.ReportGraphicEcoDiagram,
                                    eventData);
                        }
                    });
                } else if (ecoLevel.equalsIgnoreCase("serviceIP")) {
                    service.getServiceIPEcoResources(id, dateFrom, dateTo, metric, new AsyncCallback<List<EcoResource>>() {
                        public void onFailure(Throwable caught) {
                            Dispatcher.forwardEvent(MainEvents.Error,
                                    caught);
                        }

                        public void onSuccess(List<EcoResource> result) {
                            ArrayList eventData = new ArrayList();
                            if (result.size() == 0) {
                                eventData.add("Sorry, There are no suitable records.");
                            } else {
                                eventData.add(submission);
                                eventData.add(result);
                                eventData.add(from);
                                eventData.add(to);
                            }
                            Dispatcher.get().dispatch(
                                    MainEvents.ReportGraphicEcoDiagram,
                                    eventData);
                        }
                    });
                } else if (ecoLevel.equalsIgnoreCase("serviceSP")) {
                    service.getServiceSPEcoResources(id, dateFrom, dateTo, metric, new AsyncCallback<List<EcoResource>>() {
                        public void onFailure(Throwable caught) {
                            Dispatcher.forwardEvent(MainEvents.Error,
                                    caught);
                        }

                        public void onSuccess(List<EcoResource> result) {
                            ArrayList eventData = new ArrayList();
                            if (result.size() == 0) {
                                eventData.add("Sorry, There are no suitable records.");
                            } else {
                                eventData.add(submission);
                                eventData.add(result);
                                eventData.add(from);
                                eventData.add(to);
                            }
                            Dispatcher.get().dispatch(
                                    MainEvents.ReportGraphicEcoDiagram,
                                    eventData);
                        }
                    });
                } else if (ecoLevel.equalsIgnoreCase("vm")) {
                    service.getVMEcoResources(id, dateFrom, dateTo, metric, new AsyncCallback<List<EcoResource>>() {
                        public void onFailure(Throwable caught) {
                            Dispatcher.forwardEvent(MainEvents.Error,
                                    caught);
                        }

                        public void onSuccess(List<EcoResource> result) {
                            ArrayList eventData = new ArrayList();
                            if (result.size() == 0) {
                                eventData.add("Sorry, There are no suitable records.");
                            } else {
                                eventData.add(submission);
                                eventData.add(result);
                                eventData.add(from);
                                eventData.add(to);
                            }
                            Dispatcher.get().dispatch(
                                    MainEvents.ReportGraphicEcoDiagram,
                                    eventData);
                        }
                    });
                } else if (ecoLevel.equalsIgnoreCase("infrastructure")) {
                    service.getInfrastructureEcoResources(dateFrom, dateTo, metric, new AsyncCallback<List<EcoResource>>() {
                        public void onFailure(Throwable caught) {
                            Dispatcher.forwardEvent(MainEvents.Error,
                                    caught);
                        }

                        public void onSuccess(List<EcoResource> result) {
                            ArrayList eventData = new ArrayList();
                            if (result.size() == 0) {
                                eventData.add("Sorry, There are no suitable records.");
                            } else {
                                eventData.add(submission);
                                eventData.add(result);
                                eventData.add(from);
                                eventData.add(to);
                            }
                            Dispatcher.get().dispatch(
                                    MainEvents.ReportGraphicEcoDiagram,
                                    eventData);
                        }
                    });
                } else if (ecoLevel.equalsIgnoreCase("all nodes")) {
                    service.getNodesEcoResources(dateFrom, dateTo, metric, new AsyncCallback<List<EcoResource>>() {
                        public void onFailure(Throwable caught) {
                            Dispatcher.forwardEvent(MainEvents.Error,
                                    caught);
                        }

                        public void onSuccess(List<EcoResource> result) {
                            ArrayList eventData = new ArrayList();
                            if (result.size() == 0) {
                                eventData.add("Sorry, There are no suitable records.");
                            } else {
                                eventData.add(submission);
                                eventData.add(result);
                                eventData.add(from);
                                eventData.add(to);
                            }
                            Dispatcher.get().dispatch(
                                    MainEvents.ReportGraphicEcoDiagram,
                                    eventData);
                        }
                    });
                }

            }
        });


        //Define action to perform when we click "cancel". Reset values.
        cancel.addSelectionListener(new SelectionListener<ButtonEvent>() {
            @Override
            public void componentSelected(ButtonEvent ce) {
                names = null;
                names = OptimisResource.getServiceMetrics();
                store.removeAll();
                store.add(names);
                listRepresentations.clear();
                resourceId.setVisible(false);
                dateFrom.setVisible(false);
                timeFrom.setVisible(false);
                dateTo.setVisible(false);
                timeTo.setVisible(false);
                listMetricsIP.setVisible(false);
                listMetricsSP.setVisible(false);
                submit.setVisible(false);
                cancel.setVisible(false);
                //refresh.setVisible(false);
                dateFrom.setValue(new Date());
                timeFrom.setDateValue(new Date(0, 0, 0));
                dateTo.setValue(new Date());
                timeTo.setDateValue(new Date());
                resourceId.clear();
                Dispatcher.get().dispatch(MainEvents.ReportGraphicCancel);
            }
        });

        this.add(fp);

    }

    private void serviceDeploymentSelected() {
        service = (MonitoringManagerWebServiceAsync) Registry.get("guiservice");
        service.getServiceDeploymentEcoInfo(new AsyncCallback<EcoServiceDeploymentInfoData>() {
            public void onFailure(Throwable caught) {
                Dispatcher.forwardEvent(MainEvents.Error,
                        caught);
            }

            @SuppressWarnings({"rawtypes", "unchecked"}) //FIXME
            public void onSuccess(EcoServiceDeploymentInfoData result) {
                ArrayList eventData = new ArrayList();
                eventData.add(result);

                Dispatcher.get().dispatch(
                        MainEvents.ReportGraphicEcoServiceDeploymentInfo,
                        eventData);
            }
        });
    }

    private void serviceDeploymentSelectedSP() {
        service = (MonitoringManagerWebServiceAsync) Registry.get("guiservice");
        service.getServiceDeploymentEcoInfoSP(new AsyncCallback<EcoServiceDeploymentInfoData>() {
            public void onFailure(Throwable caught) {
                Dispatcher.forwardEvent(MainEvents.Error,
                        caught);
            }

            @SuppressWarnings({"rawtypes", "unchecked"}) //FIXME
            public void onSuccess(EcoServiceDeploymentInfoData result) {
                ArrayList eventData = new ArrayList();
                eventData.add(result);

                Dispatcher.get().dispatch(
                        MainEvents.ReportGraphicEcoServiceDeploymentInfoSP,
                        eventData);
            }
        });
    }
}
