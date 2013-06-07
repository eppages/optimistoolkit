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
package eu.optimis.ip.gui.client.mvc.view;

import java.util.List;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.Style.Orientation;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.mvc.AppEvent;
import com.extjs.gxt.ui.client.mvc.Controller;
import com.extjs.gxt.ui.client.mvc.View;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.layout.AccordionLayout;
//import com.extjs.gxt.ui.client.widget.layout.AccordionLayout;
import com.extjs.gxt.ui.client.widget.layout.RowData;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;
import eu.optimis.ip.gui.client.MainEvents;
import eu.optimis.ip.gui.client.mvc.MainView;
import eu.optimis.ip.gui.client.userwidget.graph.GraphicReportAuthenticationLoginReportPanel;
import eu.optimis.ip.gui.client.userwidget.graph.GraphicReportAuthenticationNewAccountReportPanel;

public class GraphicReportAuthenticationView extends View {

    private ContentPanel leftPanelLogin;
    private ContentPanel leftPanelnewAccount;

    public GraphicReportAuthenticationView(Controller controller) {
        super(controller);
    }

    @Override
    protected void handleEvent(AppEvent event) {
        System.out.println("Event check");
        if (event.getType() == MainEvents.Init) {
            initUI();
        } else if (event.getType() == MainEvents.LOGIN) {
            /*List eventDataList = (List) event.getData();
            String message = (String) eventDataList.get(0);
            if (!message.equals("Login successful.")) {
                return;
            }
            login();*/
        } else if (event.getType() == MainEvents.LOGINSUCCESSFUL) {
            login();
        } else if (event.getType() == MainEvents.LOGOUT) {
            //newAccount(); 
        } else if (event.getType() == MainEvents.NEWACCOUNT) {
            List eventDataList = (List) event.getData();
            //String message = (String) eventDataList.get(0);
            //if(!message.equals("Please select the option")) return;
            newAccount();
        } else if (event.getType() == MainEvents.NEWACCOUNTSUBMIT) {
            List eventDataList = (List) event.getData();
            String message = (String) eventDataList.get(0);
            //if(!message.equals("Please select the option")) return;
            newAccountSubmit();
        } else if (event.getType() == MainEvents.SKIPLOGIN) {
            skipLogin();
        }
    }

    @Override
    protected void initialize() {
    }

    private void initUI() {

        leftPanelLogin = new GraphicReportAuthenticationLoginReportPanel();
        //leftPanelLogin.setAnimCollapse(false);
        leftPanelLogin.addListener(Events.Expand, new Listener<ComponentEvent>() {

            public void handleEvent(ComponentEvent be) {
                LayoutContainer wrapper = (LayoutContainer) Registry.get(MainView.CENTER_PANEL);
                wrapper.removeAll();
                return;
            }
        });
        ContentPanel west = (ContentPanel) Registry.get(MainView.WEST_PANEL);
        //TODO check layout
        //west.setLayout(new RowLayout(Orientation.VERTICAL));
        //west.setLayout(new AccordionLayout());
        //west.add(leftPanel);
        west.add(leftPanelLogin);//, new RowData(1.0, 0.33, new Margins(0, 0, 0, 0)));
        System.out.println("Init event");

        leftPanelnewAccount = new GraphicReportAuthenticationNewAccountReportPanel();
        west.add(leftPanelnewAccount);//, new RowData(1.0, 0.33, new Margins(0, 0, 0, 0)));
        //leftPanelnewAccount.setVisible(false);
        //leftPanelnewAccount.disable();
    }
//-----------------------------------------------------------	

    private void login() {
        System.out.println("Login event");
        leftPanelnewAccount.setVisible(false);
        leftPanelnewAccount.disable();
        leftPanelLogin.expand();
    }

    private void newAccount() {
        System.out.println("newAccount event");
        leftPanelLogin.setVisible(true);
        leftPanelLogin.enable();
        leftPanelnewAccount.setVisible(true);
        leftPanelnewAccount.enable();
    }

    private void newAccountSubmit() {
        System.out.println("newAccount event");
        leftPanelLogin.setVisible(true);
        leftPanelLogin.enable();
        leftPanelnewAccount.setVisible(false);
        leftPanelnewAccount.disable();
    }

    private void skipLogin() {
        System.out.println("skipLogin event");
        leftPanelLogin.setVisible(true);
        leftPanelLogin.enable();
        leftPanelnewAccount.setVisible(false);
        leftPanelnewAccount.disable();
    }
}
