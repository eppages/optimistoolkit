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
import java.util.List;
import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Label;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.DateField;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.form.FormPanel.LabelAlign;
import com.extjs.gxt.ui.client.widget.layout.FormData;
import com.extjs.gxt.ui.client.widget.layout.FormLayout;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.VerticalPanel;
import eu.optimis.ip.gui.client.IPManagerWebServiceAsync;
import eu.optimis.ip.gui.client.MainEvents;

public class GraphicReportAuthenticationNewAccountReportPanel extends ContentPanel //implements Serializable
{

    private IPManagerWebServiceAsync service;
    DateField date;
    private TextField<String> eMail;
    private TextField<String> pass;
    private TextField<String> pass2;
    VerticalPanel loginPanel = new VerticalPanel();
    Label loginLabel = new Label("Please sign in to your Google Account to access the OPTIMIS Service Provider Dashboard");
    Label logoutLabel = new Label("Do you want to sign out from Service Provider Dashboard?");
    Anchor signInLink = new Anchor("Sign in");
    Anchor signOutLink = new Anchor("Sign out");
    FormPanel fp = new FormPanel();

    public GraphicReportAuthenticationNewAccountReportPanel() {
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
        eMail.setValue("a@b.c");
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
        //Button skipButton = new Button("Skip"); //Skip! [temporal]
        //fp.addButton(skipButton);

        service = (IPManagerWebServiceAsync) Registry.get("guiservice");

        submit.addSelectionListener(new SelectionListener<ButtonEvent>() {

            @Override
            public void componentSelected(ButtonEvent ce) {
                ArrayList eventData = new ArrayList();

                if (!eMail.isValid()) {
                    eventData = new ArrayList();
                    eventData.add("The specified e-mail is invalid.");
                    Dispatcher.get().dispatch(MainEvents.NEWACCOUNTERROR, eventData);
                    return;
                } else if (eMail.getValue() == null) {
                    eventData = new ArrayList();
                    eventData.add("Please enter an e-mail address.");
                    Dispatcher.get().dispatch(MainEvents.NEWACCOUNTERROR, eventData);
                    return;
                } else if (pass.getValue() == null) {
                    eventData = new ArrayList();
                    eventData.add("Please enter a password!");
                    Dispatcher.get().dispatch(MainEvents.NEWACCOUNTERROR, eventData);
                    return;
                } else if (pass2.getValue() == null) {
                    eventData = new ArrayList<String>();
                    eventData.add("Please enter the validation password.");
                    Dispatcher.get().dispatch(MainEvents.NEWACCOUNTERROR, eventData);
                    return;
                }
                if (!pass2.getValue().equals(pass.getValue())) {
                    eventData = new ArrayList<String>();
                    eventData.add("Password mismatch. Please repeat your password.");
                    Dispatcher.get().dispatch(MainEvents.NEWACCOUNTERROR, eventData);
                    return;
                }

                String info = eMail.getValue();
                String passw = pass.getValue();
                service.newAccount(info, passw, new AsyncCallback<String>() {

                    public void onFailure(Throwable caught) {
                        ArrayList eventData = new ArrayList();
                        eventData.add("Server error while creating new user.");
                        Dispatcher.get().dispatch(MainEvents.NEWACCOUNTERROR, eventData);
                    }

                    public void onSuccess(String result) {
                        ArrayList eventData = new ArrayList();
                        eventData.add(result);
                        if (result.startsWith("New account created for user")) {
                            Dispatcher.get().dispatch(MainEvents.NEWACCOUNTSUBMIT, eventData);
                        } else {
                            Dispatcher.get().dispatch(MainEvents.NEWACCOUNTERROR, eventData);
                        }
                        layout(true);
                    }
                });
            }
        });

        /*skipButton.addSelectionListener(new SelectionListener<ButtonEvent>() {

            @Override
            public void componentSelected(ButtonEvent ce) {
                ArrayList<String> eventData = new ArrayList<String>();
                eventData.add("Skipped loggin procedure (temporary debugging).");
                Dispatcher.get().dispatch(MainEvents.LOGINSUCCESSFUL, eventData);
            }
        });*/
        this.add(fp);
    }
}
