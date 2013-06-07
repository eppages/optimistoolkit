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
package eu.optimis.mi.gui.client.mvc;

import com.extjs.gxt.ui.client.event.EventType;
import com.extjs.gxt.ui.client.mvc.AppEvent;
import com.extjs.gxt.ui.client.mvc.Controller;

import eu.optimis.mi.gui.client.MainEvents;
import eu.optimis.mi.gui.client.mvc.view.LatestReportEnergyRecordsView;
import eu.optimis.mi.gui.client.mvc.view.LatestReportPhysicalRecordsView;
import eu.optimis.mi.gui.client.mvc.view.LatestReportServiceRecordsView;
import eu.optimis.mi.gui.client.mvc.view.LatestReportView;
import eu.optimis.mi.gui.client.mvc.view.LatestReportVirtualRecordsView;

public class LatestReportController extends Controller {

	private LatestReportView reportView;
	private LatestReportEnergyRecordsView energyRecordsView;
	private LatestReportServiceRecordsView serviceRecordsView;
	private LatestReportVirtualRecordsView virtualRecordsView;
	private LatestReportPhysicalRecordsView physicalRecordsView;

	public LatestReportController() {
		registerEventTypes(MainEvents.Init);
		registerEventTypes(MainEvents.ReportEnergyIerms);
		registerEventTypes(MainEvents.ReportPhysicalIerms);
		registerEventTypes(MainEvents.ReportVirtualIerms);
		registerEventTypes(MainEvents.ReportServiceIerms);

	}

	@Override
	public void handleEvent(AppEvent event) {
		EventType type = event.getType();
		if (type == MainEvents.Init) {
			forwardToView(reportView, event);
		} 
		else if (type == MainEvents.ReportPhysicalIerms) {
			forwardToView(physicalRecordsView, event);
			forwardToView(reportView, event);
		}

		else if (type == MainEvents.ReportVirtualIerms) {
			forwardToView(virtualRecordsView, event);
			forwardToView(reportView, event);
		} else if (type == MainEvents.ReportServiceIerms) {
			forwardToView(serviceRecordsView, event);
			forwardToView(reportView, event);
		}

		else if (type == MainEvents.ReportEnergyIerms) {
			forwardToView(energyRecordsView, event);
			forwardToView(reportView, event);
		}
		else;
		// else if (type == MainEvents.ReportIterms) {
		// //forwardToView(reportView, event);
		// forwardToView(recordsView, event);
		// forwardToView(reportView, event);
		// }

	}

	public void initialize() {
		super.initialize();
		reportView = new LatestReportView(this);
		energyRecordsView = new LatestReportEnergyRecordsView(this);
		serviceRecordsView = new LatestReportServiceRecordsView(this);
		virtualRecordsView = new LatestReportVirtualRecordsView(this);
		physicalRecordsView = new LatestReportPhysicalRecordsView(this);

	}
}
