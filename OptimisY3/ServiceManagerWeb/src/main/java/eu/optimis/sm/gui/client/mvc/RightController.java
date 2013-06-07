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
package eu.optimis.sm.gui.client.mvc;

import com.extjs.gxt.ui.client.event.EventType;
import com.extjs.gxt.ui.client.mvc.AppEvent;
import com.extjs.gxt.ui.client.mvc.Controller;
import eu.optimis.sm.gui.client.MainEvents;
import eu.optimis.sm.gui.client.mvcview.RightAvailableServicesView;
import eu.optimis.sm.gui.client.mvcview.RightDeployView;
import eu.optimis.sm.gui.client.mvcview.RightIPRegistryView;
import eu.optimis.sm.gui.client.mvcview.RightIPSView;
import eu.optimis.sm.gui.client.mvcview.RightRedeployView;
import eu.optimis.sm.gui.client.mvcview.RightSecureStorageView;
import eu.optimis.sm.gui.client.mvcview.RightTRECView;
import eu.optimis.sm.gui.client.mvcview.RightUndeployView;
import eu.optimis.sm.gui.client.mvcview.RightVPNView;
import eu.optimis.sm.gui.client.mvcview.RightView;
import eu.optimis.sm.gui.client.mvcview.RightPropertiesView;
import eu.optimis.sm.gui.client.mvcview.RightLogsView;

public class RightController extends Controller {

	public static RightView rightView;
	public static RightAvailableServicesView rightAvailableServicesView;
	private RightUndeployView rightUndeployView;
	private RightDeployView rightDeployView;
	private RightTRECView rightTRECView;
	private RightIPRegistryView rightIPRegistryView;
	private RightRedeployView rightRedeployView;
	private RightPropertiesView rightPropertiesView;
	private RightLogsView rightLogsView;
	private RightIPSView rightIPSView;
	private RightSecureStorageView rightSecureStorageView;
	private RightVPNView rightVPNView;

	public RightController() {
		registerEventTypes(MainEvents.init);
		registerEventTypes(MainEvents.login);
		registerEventTypes(MainEvents.logout);
		registerEventTypes(MainEvents.availableServices);
		registerEventTypes(MainEvents.deployService);
		registerEventTypes(MainEvents.undeployService);
		registerEventTypes(MainEvents.ipRegistry);
		registerEventTypes(MainEvents.redeployService);
		registerEventTypes(MainEvents.trecGUIsp);
		registerEventTypes(MainEvents.properties);
		registerEventTypes(MainEvents.logs);
		registerEventTypes(MainEvents.brokerIps);
		registerEventTypes(MainEvents.brokerSecureStorage);
		registerEventTypes(MainEvents.brokerVpn);
		}

	@Override
	public void handleEvent(AppEvent event) {
		EventType type = event.getType();
		if (type == MainEvents.init) {
			//here we can disable login
			forwardToView(rightView, event);
		} 
		else if (type == MainEvents.login) {
			forwardToView(rightView, event);
			forwardToView(rightAvailableServicesView, event);
		}
		else if (type == MainEvents.logout) {
			forwardToView(rightView, event);
			forwardToView(rightAvailableServicesView, event);
		}
		else if (type == MainEvents.availableServices) {
			forwardToView(rightAvailableServicesView, event);
		}
		else if (type == MainEvents.deployService) {
			forwardToView(rightDeployView, event);
		}
		else if (type == MainEvents.undeployService) {
			forwardToView(rightUndeployView, event);
		}
		else if (type == MainEvents.ipRegistry) {
			forwardToView(rightIPRegistryView, event);
		}
		else if (type == MainEvents.redeployService) {
			forwardToView(rightRedeployView, event);
		}
		else if (type == MainEvents.trecGUIsp) {
			forwardToView(rightTRECView, event);
		}
		else if (type == MainEvents.properties) {
			forwardToView(rightPropertiesView, event);
		}
		else if (type == MainEvents.logs) {
			forwardToView(rightLogsView, event);
		}
		else if (type == MainEvents.brokerIps) {
			forwardToView(rightIPSView, event);
		}
		else if (type == MainEvents.brokerSecureStorage) {
			forwardToView(rightSecureStorageView, event);
		}
		else if (type == MainEvents.brokerVpn) {
			forwardToView(rightVPNView, event);
		}
	}

	public void initialize() {
		super.initialize();
		if(rightView==null)
			rightView = new RightView(this);
		rightAvailableServicesView = new RightAvailableServicesView(this);
		rightUndeployView = new RightUndeployView(this);
		rightDeployView = new RightDeployView(this);
		rightRedeployView = new RightRedeployView(this);
		rightIPRegistryView = new RightIPRegistryView(this);
		rightTRECView = new RightTRECView(this);
		rightPropertiesView = new RightPropertiesView(this);
		rightLogsView = new RightLogsView(this);
		rightIPSView = new RightIPSView(this);
		rightSecureStorageView = new RightSecureStorageView(this);
		rightVPNView = new RightVPNView(this);
	}
}
