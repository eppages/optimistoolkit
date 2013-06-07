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
import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.DateField;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.form.FormPanel.LabelAlign;
import com.extjs.gxt.ui.client.widget.layout.FormData;
import com.extjs.gxt.ui.client.widget.layout.FormLayout;
import com.google.gwt.user.client.rpc.AsyncCallback;
import eu.optimis.sm.gui.client.MainEvents;
import eu.optimis.sm.gui.client.ServiceManagerWebServiceAsync;
import eu.optimis.sm.gui.client.model.Resource;

public class LeftPanelNewAccount extends ContentPanel
{
    private ServiceManagerWebServiceAsync service;
	List<Resource> resources;

	DateField date;
	private TextField<String> eMail;
	private TextField<String> pass;
	private TextField<String> pass2;

	FormPanel fp = new FormPanel();
	
	public LeftPanelNewAccount() {
		setHeading("New user account");
		createForm();
	}

	private void createForm() {
		FormData formData = new FormData("-10");
		FormLayout layout = new FormLayout();
		layout.setLabelAlign(LabelAlign.TOP);
		fp.setLayout(layout);
		fp.setFrame(false);
		fp.setHeaderVisible(false);
		fp.setAutoWidth(true);
		fp.setBodyBorder(true);
		fp.setButtonAlign(HorizontalAlignment.CENTER);

		eMail = new TextField<String>();
		eMail.setFieldLabel("E-mail address"); //ID
		//resourceId.setStyleName("formComponent");
		eMail.setValue("a@b.cc");
		eMail.setAllowBlank(false);
		
		eMail.setRegex("^[\\w-]+(\\.[\\w-]+)*@[\\w-]+(\\.[\\w-]+)+$");
          
		fp.add(eMail, formData);

		pass = new TextField<String>();
		pass.setPassword(true);
		pass.setValue("pass");
		pass.setFieldLabel("Password");
		fp.add(pass, formData);

		pass2 = new TextField<String>();
		pass2.setPassword(true);
		pass2.setValue("pass");
		pass2.setFieldLabel("Password repeat");
		fp.add(pass2, formData);		

		Button submit = new Button("Submit");
		fp.addButton(submit);
		Button skipButton = new Button("Cancel");
		fp.addButton(skipButton); //This button should be removed to enable log-in skipping!

		service = (ServiceManagerWebServiceAsync) Registry.get("guiservice");

		submit.addSelectionListener(new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent ce) {
				ArrayList eventData = new ArrayList();
			
				if (!eMail.isValid()) {
					eventData = new ArrayList();
					eventData.add("e-mail is invalid!");
					Dispatcher.get().dispatch(MainEvents.newAccountSubmit, eventData);
					return;
				} else
				if (eMail.getValue() == null) {
					eventData = new ArrayList();
					eventData.add("Please enter your e-mail address!");
					Dispatcher.get().dispatch(MainEvents.newAccountSubmit, eventData);
					return;
				} else
				if (pass.getValue() == null) {
					eventData = new ArrayList();
					eventData.add("Please enter the password!");
					Dispatcher.get().dispatch(MainEvents.newAccountSubmit, eventData);
					return;
				} else
				if(pass2.getValue()==null)
				{
					eventData = new ArrayList<String>();
					eventData.add("Please repeat your password!");
					Dispatcher.get().dispatch(MainEvents.newAccountSubmit, eventData);
					return;
				}
				if(!pass2.getValue().equals(pass.getValue()))
				{
					eventData = new ArrayList<String>();
					eventData.add("Wrong password repeat!");
					Dispatcher.get().dispatch(MainEvents.newAccountSubmit, eventData);
					return;
				}				
				eventData.add("Please select the option ");
				Dispatcher.get().dispatch(MainEvents.newAccountSubmit, eventData);
				
				
				String info = eMail.getValue();
				String passw = pass.getValue();
				service.newAccount(info, passw, new AsyncCallback<String>() {
					public void onFailure(Throwable caught) {
						ArrayList eventData = new ArrayList();
						eventData.add("Please select the option");
						Dispatcher.get().dispatch(MainEvents.newAccountSubmit, eventData);
					}
					public void onSuccess(String result) {
						if(!result.equals("This user already exists. Please select another user name!"))
						{
							ArrayList eventData = new ArrayList();
							eventData.add("Please select the option");
							eventData.add(result);
							Dispatcher.get().dispatch(MainEvents.newAccountSubmit, eventData);
							layout(true);
						}
						else
						{
							ArrayList eventData = new ArrayList();
							eventData = new ArrayList<String>();
							eventData.add("This user already exists! Please select another name");
							Dispatcher.get().dispatch(MainEvents.newAccountSubmit, eventData);
							return;
						}
					}
				});	
			}
		});

		skipButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent ce) {
				ArrayList<String> eventData = new ArrayList<String>();
				eventData.add("Please select the option");
				Dispatcher.get().dispatch(MainEvents.skipLogin, eventData);
			}
		});
		this.add(fp);
	}
	}
