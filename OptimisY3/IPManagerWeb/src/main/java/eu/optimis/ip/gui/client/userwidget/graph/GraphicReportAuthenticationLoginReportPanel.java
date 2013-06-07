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

import java.util.ArrayList;
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
import eu.optimis.ip.gui.client.IPManagerWebServiceAsync;
import eu.optimis.ip.gui.client.MainEvents;

public class GraphicReportAuthenticationLoginReportPanel extends ContentPanel {

    public static String session_id = "0";
    public static TextField<String> eMail;
    private IPManagerWebServiceAsync service;
    private TextField<String> pass;
    FormPanel fp = new FormPanel();
    Button loginButton = new Button("Log in");
    Button newAccountButton = new Button("New user?");
    Button logoutButton = new Button("Log out");

    public GraphicReportAuthenticationLoginReportPanel() {
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
                    eventData.add("Please enter a valid e-mail.");
                    Dispatcher.get().dispatch(MainEvents.LOGIN, eventData);
                    return;
                } else if (eMail.getValue() == null) {
                    eventData = new ArrayList();
                    eventData.add("Please enter an e-mail address!");
                    Dispatcher.get().dispatch(MainEvents.LOGIN, eventData);
                    return;
                } else if (pass.getValue() == null) {
                    eventData = new ArrayList();
                    eventData.add("Please enter your password!");
                    Dispatcher.get().dispatch(MainEvents.LOGIN, eventData);
                    return;
                } else {
                    service = (IPManagerWebServiceAsync) Registry.get("guiservice");
                }

                String email = eMail.getValue();
                String password = pass.getValue();
                service.loginUser(email, password, new AsyncCallback<ArrayList<Object>>() {

                    public void onFailure(Throwable caught) {
                        ArrayList eventData = new ArrayList();
                    }

                    public void onSuccess(ArrayList<Object> result) {
                        ArrayList eventData = new ArrayList();
                        if (!result.get(0).equals("User/pass are wrong! Please correct input data or register an account")) {
                            session_id = (String) result.get(1);
                            eventData.add("Login successful.");
                            eventData.add(result.get(0));
                            Dispatcher.get().dispatch(MainEvents.LOGINSUCCESSFUL, eventData);
                            fp.remove(eMail);
                            fp.remove(pass);
                            fp.clear();
                            remove(fp);
                            
                            //Create new form because login has been SUCCESSFUL.
                            FormLayout layout = new FormLayout();
                            layout.setLabelAlign(LabelAlign.TOP);
                            fp = new FormPanel();
                            fp.setLayout(layout);
                            fp.setFrame(false);
                            fp.setHeaderVisible(false);
                            fp.setAutoWidth(true);
                            fp.setBodyBorder(true);
                            fp.setButtonAlign(HorizontalAlignment.CENTER);
                            fp.addText((String) result.get(0));
                            fp.addButton(logoutButton);
                            add(fp);
                        } else {
                            eventData.add("The specified user does not exist or user/password are wrong.");
                            eventData.add(result.get(0));
                            Dispatcher.get().dispatch(MainEvents.NEWACCOUNT, eventData);
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
                
                //When logout has been selected, recreate the login screen.
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
                Dispatcher.get().dispatch(MainEvents.LOGOUT, eventData);

                service.logoutUser(session_id, eMail.getValue(), new AsyncCallback<Boolean>() {

                    public void onFailure(Throwable caught) {
                        System.out.println("logoutUser: failure");
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
                Dispatcher.get().dispatch(MainEvents.NEWACCOUNT, eventData);
            }
        });

        this.add(fp);
    }
}
