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
package eu.optimis.sm.gui.client;

import com.extjs.gxt.ui.client.event.EventType;

public class MainEvents {

	  public static final EventType init = new EventType();
	  
	  public static final EventType availableServices = new EventType();
	  public static final EventType deployService = new EventType();
	  public static final EventType undeployService = new EventType();
	  public static final EventType redeployService = new EventType();
	  public static final EventType ipRegistry = new EventType();
	  public static final EventType trecGUIsp = new EventType();
	  public static final EventType properties = new EventType();
	  public static final EventType logs = new EventType();
	  
	  public static final EventType login = new EventType();
	  public static final EventType logout = new EventType();
	  public static final EventType newAccount = new EventType();
	  public static final EventType newAccountSubmit = new EventType();
	  public static final EventType skipLogin = new EventType();
	  public static final EventType error = new EventType();
	  
	  public static final EventType brokerIps = new EventType();
	  public static final EventType brokerSecureStorage = new EventType();
	  public static final EventType brokerVpn = new EventType();
}
