/**

Copyright 2012 University of Stuttgart

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at 

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

**/

package eu.optimis.mi.gui.client.userwidget.graph;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.Style;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.button.Button;

import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.form.ComboBox.TriggerAction;
import com.extjs.gxt.ui.client.widget.form.DateField;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.Radio;
import com.extjs.gxt.ui.client.widget.form.RadioGroup;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.form.FormPanel.LabelAlign;
import com.extjs.gxt.ui.client.widget.layout.FormData;
import com.extjs.gxt.ui.client.widget.layout.FormLayout;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.rpc.AsyncCallback;

import eu.optimis.mi.gui.client.MainEvents;
import eu.optimis.mi.gui.client.MonitoringManagerWebServiceAsync;
import eu.optimis.mi.gui.client.model.IdName;
import eu.optimis.mi.gui.client.model.MetricName;
import eu.optimis.mi.gui.client.model.MonitoringResource;

import eu.optimis.mi.gui.client.resources.OptimisResource;

public class GraphicReportPanel extends ContentPanel {

	private MonitoringManagerWebServiceAsync service;
	private List<MetricName> names;
	private ListStore<MetricName> store = new ListStore<MetricName>();
	private String submission;

	private ComboBox<MetricName> combo = new ComboBox<MetricName>();
	private ComboBox<IdName> comboIds = new ComboBox<IdName>();
	
	private RadioGroup radioGroup;
	private DateField date;
	private TextField<String> resourceId;

	private Radio radioService = new Radio();
	
	public GraphicReportPanel() {
		setHeading("Graphic Report");
		createForm();
	}

	private void createForm() {
		FormData formData = new FormData("-10");
		FormPanel fp = new FormPanel();
		FormLayout layout = new FormLayout();
		layout.setLabelAlign(LabelAlign.TOP);
		fp.setLayout(layout);
		fp.setFrame(false);
		fp.setHeaderVisible(false);
		fp.setAutoWidth(true);
		fp.setBodyBorder(true);
		fp.setButtonAlign(HorizontalAlignment.CENTER);


		radioService.setValueAttribute("service");
		radioService.setBoxLabel("Service");
		radioService.setValue(true);
		radioService.addListener(Events.Change, new Listener<BaseEvent>() {
			
			public void handleEvent(BaseEvent be) {
				names = null;
				names = OptimisResource.getServiceMetrics();
				store.removeAll();
				store.add(names);
				getComboBox().clear();
				getComboBox().setStore(store);
			}

		});

		Radio radioVirtual = new Radio();
		radioVirtual.setValueAttribute("virtual");
		radioVirtual.setBoxLabel("Virtual");
		radioVirtual.addListener(Events.Change, new Listener<BaseEvent>() {
			//@Override
			public void handleEvent(BaseEvent be) {
				names = null;
				names = OptimisResource.getVirtualMetrics();
				store.removeAll();
				store.add(names);
				getComboBox().clear();
				getComboBox().setStore(store);
			}

		});

		Radio radioPhysical = new Radio();
		radioPhysical.setValueAttribute("physical");
		radioPhysical.setBoxLabel("Physical");
		
		radioPhysical.addListener(Events.Change, new Listener<BaseEvent>() {
			
			public void handleEvent(BaseEvent be) {
				names = null;
				names = OptimisResource.getPhysicalMetrics();
				store.removeAll();
				store.add(names);
				getComboBox().clear();
				getComboBox().setStore(store);

			}

		});

		Radio radioEnergy = new Radio();
		radioEnergy.setValueAttribute("energy");
		radioEnergy.setBoxLabel("Energy");
		radioEnergy.addListener(Events.Change, new Listener<BaseEvent>() {
			//@Override
			public void handleEvent(BaseEvent be) {
				names = null;
				names = OptimisResource.getEnergyMetrics();
				store.removeAll();
				store.add(names);
				getComboBox().clear();
				getComboBox().setStore(store);
			}

		});

		radioGroup = new RadioGroup();
		radioGroup.setFieldLabel("Monitoring Level");
		radioGroup.setStyleName("formComponent");
		radioGroup.setVisible(true);
		radioGroup.add(radioService);
		radioGroup.add(radioVirtual);
		radioGroup.add(radioPhysical);
		radioGroup.add(radioEnergy);
		radioGroup.setOrientation(Style.Orientation.VERTICAL);
		radioGroup.setSpacing(2);
		fp.add(radioGroup, formData);

		resourceId = new TextField<String>();
		resourceId.setFieldLabel("ID");
		resourceId.setAllowBlank(false);
		fp.add(resourceId, formData);

		date = new DateField();
		date.setFieldLabel("Date");

		fp.add(date, formData);
		
		names = OptimisResource.getServiceMetrics();
		store.removeAll();
		store.add(names);
		combo.setStore(store);
		combo.setFieldLabel("Choose a metric");
		combo.setDisplayField("name");
		combo.setTriggerAction(TriggerAction.ALL);

		fp.add(combo, formData);

		Button submit = new Button("Submit");
		fp.addButton(submit);
		Button cancel = new Button("Cancel");
		fp.addButton(cancel);

		submit.addSelectionListener(new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent ce) {
				
				if (resourceId.getValue() == null) {
					ArrayList eventData = new ArrayList();
					eventData.add("Please, type the ID.");
					Dispatcher.get().dispatch(MainEvents.ReportGraphicDiagram,
							eventData);
					return;
				}
				if (date.getValue() == null) {
					ArrayList eventData = new ArrayList();
					eventData.add("Please, assign the date.");
					Dispatcher.get().dispatch(MainEvents.ReportGraphicDiagram,
							eventData);
					return;
				}

				if (combo.getSelection().size() == 0) {
					ArrayList eventData = new ArrayList();
					eventData.add("Please, select a metric.");
					Dispatcher.get().dispatch(MainEvents.ReportGraphicDiagram,
							eventData);
					return;
				}

				String dateFromStr = date.getValue().toString();
				String monitoringLevel = radioGroup.getValue().getBoxLabel()
						.toLowerCase();
				String metricName = combo.getSelection().get(0).toString();
				String resourceIdid = resourceId.getValue().trim();
				String radiobuttonStr = radioGroup.getValue().getBoxLabel();
				submission = monitoringLevel + ";  " + resourceIdid + "; "
						+ metricName + ";   " + dateFromStr;
				System.out.println("Your submission: " + submission);
				Date gwtDate = date.getValue();
				DateTimeFormat df = DateTimeFormat.getFormat("yyyyMMdd");
				String dt = df.format(gwtDate);
				String dateFrom = dt +"000000";
				String dateTo = dt +"235959";
				service = (MonitoringManagerWebServiceAsync) Registry
						.get("guiservice");
	
				service.getIdMetricDateListMonitoringResources(resourceIdid,
						monitoringLevel, metricName, dateFrom, dateTo,
						new AsyncCallback<List<MonitoringResource>>() {
							public void onFailure(Throwable caught) {
								ArrayList eventData = new ArrayList();
								eventData.add("Connection failed by loading data, please try again.");
								Dispatcher.get().dispatch(
										MainEvents.ReportGraphicDiagram,
										eventData);
//								Dispatcher.forwardEvent(MainEvents.Error,
//								caught);
							}

							public void onSuccess(
									List<MonitoringResource> result) {
								System.out.println("on success result: "
										+ result.size());
								ArrayList eventData = new ArrayList();
								if (result.size() < 1) {
									eventData.add("Sorry, there are no suitable records.");
								} else {
									eventData.add(submission);
									eventData.add(result);
								}
								Dispatcher.get().dispatch(
										MainEvents.ReportGraphicDiagram,
										eventData);
							}
						});
			}

		});

		cancel.addSelectionListener(new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent ce) {
				names = null;
				names = OptimisResource.getServiceMetrics();
				store.removeAll();
				store.add(names);
				getComboBox().setStore(store);
				radioService.setValue(true);
				date.clear();
				resourceId.clear();
				Dispatcher.get().dispatch(MainEvents.ReportGraphicCancel);
			}
		});

		this.add(fp);

	}

	private ComboBox<MetricName> getComboBox() {
		return combo;
	}

}
