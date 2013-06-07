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
import eu.optimis.ip.gui.client.mvc.view.GraphicReportOutputDiagramView;
import eu.optimis.ip.gui.client.mvc.view.GraphicReportOutputView;

/**
 *
 * @author greig
 */
public class OutputController extends Controller {

    private GraphicReportOutputView reportView;
    private GraphicReportOutputDiagramView grdView;

    public OutputController() {
        registerEventTypes(MainEvents.Init);
        registerEventTypes(MainEvents.Output);
        registerEventTypes(MainEvents.ReportGraphicCancel);
        registerEventTypes(MainEvents.LOGINSUCCESSFUL);
        registerEventTypes(MainEvents.LOGOUT);
    }

    @Override
    public void handleEvent(AppEvent event) {
        EventType type = event.getType();
        if (type == MainEvents.Init) {
            forwardToView(reportView, event);
        }
        if (type == MainEvents.Output) {
            forwardToView(grdView, event);
        }

        if (type == MainEvents.ReportGraphicCancel) {
            forwardToView(grdView, event);
        }
        
        if (type == MainEvents.LOGINSUCCESSFUL) {
            forwardToView(reportView, event);
        }
        
        if (type == MainEvents.LOGOUT) {
            forwardToView(reportView, event);
        }

    }

    public void initialize() {
        super.initialize();
        reportView = new GraphicReportOutputView(this);
        grdView = new GraphicReportOutputDiagramView(this);
    }
}
