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
//import com.allen_sauer.gwt.log.client.Log;
import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.form.FormPanel.LabelAlign;
import com.extjs.gxt.ui.client.widget.layout.FormLayout;
import com.google.gwt.user.client.rpc.AsyncCallback;
import eu.optimis.sm.gui.client.MainEvents;
import eu.optimis.sm.gui.client.ServiceManagerWebServiceAsync;

public class LeftPanelLogin extends ContentPanel {

	public static String session_id = "0";
	public static TextField<String> eMail;
	private ServiceManagerWebServiceAsync service;
	private TextField<String> pass;
	FormPanel fp = new FormPanel();
	Button loginButton = new Button("Log in");
	Button newAccountButton = new Button("New user?");
	Button logoutButton = new Button("Log out");

	public LeftPanelLogin() {
		setHeading("Authentication");
		createForm();
	}

	private void createForm() {
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
		eMail.setValue("a@b.cc");
		eMail.setAllowBlank(false);
		
		eMail.setRegex("^[\\w-]+(\\.[\\w-]+)*@[\\w-]+(\\.[\\w-]+)+$");
		fp.add(eMail);
		
		pass = new TextField<String>();
		pass.setPassword(true);
		pass.setValue("pass");
		pass.setFieldLabel("Password");
		fp.add(pass);
		
		fp.addButton(loginButton);
		fp.addButton(newAccountButton);

		loginButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent ce) {

				ArrayList eventData = new ArrayList();
				if (!eMail.isValid()) {
					eventData = new ArrayList();
					eventData.add("e-mail is invalid!");
					Dispatcher.get().dispatch(MainEvents.login,	eventData);
					return;
				} else
				if (eMail.getValue() == null) {
					eventData = new ArrayList();
					eventData.add("Please enter your e-mail address!");
					Dispatcher.get().dispatch(MainEvents.login,	eventData);
					return;
				} else
				if (pass.getValue() == null) {
					eventData = new ArrayList();
					eventData.add("Please enter the password!");
					Dispatcher.get().dispatch(MainEvents.login,	eventData);
					return;
				} else
				
				service = (ServiceManagerWebServiceAsync) Registry.get("guiservice");

				String email = eMail.getValue();
				String password = pass.getValue();
				service.loginUser(email, password, new AsyncCallback<ArrayList<Object>>() {
					public void onFailure(Throwable caught) {
						System.out.println("loginUser: error");
					}
					public void onSuccess(ArrayList<Object> result) {
						System.out.println("loginUser: success; result.get(0) = \n" + result.get(0));
						System.out.println("loginUser: session_id = \n" + result.get(1));
						session_id = (String) result.get(1);
						ArrayList eventData = new ArrayList();
						if(!result.get(0).equals("User/pass are wrong! Please correct input data or register an account"))
						{
							eventData.add("Please select the option");
							eventData.add(result.get(0));
							Dispatcher.get().dispatch(MainEvents.login, eventData);
							fp.remove(eMail);
							fp.remove(pass);
							fp.clear();
							remove(fp);
							FormLayout layout = new FormLayout();
							layout.setLabelAlign(LabelAlign.TOP);						
							fp = new FormPanel();
							fp.setLayout(layout);
							fp.setFrame(false);
							fp.setHeaderVisible(false);
							fp.setAutoWidth(true);
							fp.setBodyBorder(true);
							fp.setButtonAlign(HorizontalAlignment.CENTER);
							fp.addText((String)result.get(0));
							fp.addButton(logoutButton);
							add(fp);
						}
						else
						{
							eventData.add("You need to log in");
							eventData.add(result.get(0));
							Dispatcher.get().dispatch(MainEvents.newAccount, eventData);
						}

						layout(true);
					}
				});	

			}

		});

		logoutButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent ce) {
				fp.clear();
				remove(fp);
				FormLayout layout = new FormLayout();
				layout.setLabelAlign(LabelAlign.TOP);						
				fp = new FormPanel();
				fp.setLayout(layout);
				fp.setFrame(false);
				fp.setHeaderVisible(false);
				fp.setAutoWidth(true);
				fp.setBodyBorder(true);
				fp.setButtonAlign(HorizontalAlignment.CENTER);
				
				fp.add(eMail);
				fp.add(pass);
				fp.addButton(loginButton);
				fp.addButton(newAccountButton);
				add(fp);

				layout(true);
				
				ArrayList eventData = new ArrayList();
				eventData.add("You are logged out!");
				eventData.add("You are logged out, " + eMail.getValue() + "!");
				Dispatcher.get().dispatch(MainEvents.logout, eventData);
				
				service.logoutUser(session_id, eMail.getValue(), new AsyncCallback<Boolean>() {
					public void onFailure(Throwable caught) {
						System.out.println("logoutUser: error");
					}
					public void onSuccess(Boolean result) {
						System.out.println("logoutUser: success");
					}
				});	
			}
		});		
		
		newAccountButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent ce) {
				ArrayList eventData = new ArrayList();
				eventData.add("Please select the option");
				Dispatcher.get().dispatch(MainEvents.newAccount, eventData);
			}
		});
		
		/*
		skipButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent ce) {
				ArrayList eventData = new ArrayList();
				eventData.add("Please select the option");
				Dispatcher.get().dispatch(MainEvents.login, eventData);
			}

		});
		*/		
		this.add(fp);
	
	}
}
