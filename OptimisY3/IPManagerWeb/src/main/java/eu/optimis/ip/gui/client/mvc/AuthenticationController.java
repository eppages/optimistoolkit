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
package eu.optimis.ip.gui.client.mvc;

import com.extjs.gxt.ui.client.event.EventType;
import com.extjs.gxt.ui.client.mvc.AppEvent;
import com.extjs.gxt.ui.client.mvc.Controller;
import eu.optimis.ip.gui.client.MainEvents;
import eu.optimis.ip.gui.client.mvc.view.GraphicReportAuthenticationDiagramView;
import eu.optimis.ip.gui.client.mvc.view.GraphicReportAuthenticationView;

public class AuthenticationController extends Controller {

    private GraphicReportAuthenticationView grAuthView;
    private GraphicReportAuthenticationDiagramView grAuthDiagramView;

    public AuthenticationController() {
        registerEventTypes(MainEvents.Init);
        registerEventTypes(MainEvents.LOGIN);
        registerEventTypes(MainEvents.LOGINSUCCESSFUL);
        registerEventTypes(MainEvents.LOGOUT);
        registerEventTypes(MainEvents.NEWACCOUNT);
        registerEventTypes(MainEvents.NEWACCOUNTSUBMIT);
        registerEventTypes(MainEvents.NEWACCOUNTERROR);
        registerEventTypes(MainEvents.SKIPLOGIN);
    }

    @Override
    public void handleEvent(AppEvent event) {
        EventType type = event.getType();
        if (type == MainEvents.Init) {
            forwardToView(grAuthView, event);
            forwardToView(grAuthDiagramView, event);
        }
        if (type == MainEvents.LOGIN) {
            forwardToView(grAuthView, event);
            forwardToView(grAuthDiagramView, event);
        }
        if (type == MainEvents.LOGINSUCCESSFUL) {
            forwardToView(grAuthView, event);
            forwardToView(grAuthDiagramView, event);
        }
        if (type == MainEvents.LOGOUT) {
            forwardToView(grAuthView, event);
            forwardToView(grAuthDiagramView, event);
        }
        if (type == MainEvents.NEWACCOUNT) {
            forwardToView(grAuthView, event);
            forwardToView(grAuthDiagramView, event);
        }
        if (type == MainEvents.NEWACCOUNTSUBMIT) {
            forwardToView(grAuthView, event);
            forwardToView(grAuthDiagramView, event);
        }
        if (type == MainEvents.NEWACCOUNTERROR) {
            forwardToView(grAuthDiagramView, event);
        }
        if (type == MainEvents.SKIPLOGIN) {
            forwardToView(grAuthView, event);
            forwardToView(grAuthDiagramView, event);
        }
    }

    public void initialize() {
        super.initialize();
        grAuthView = new GraphicReportAuthenticationView(this);
        grAuthDiagramView = new GraphicReportAuthenticationDiagramView(this);
    }
}
