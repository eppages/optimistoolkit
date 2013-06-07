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

import java.io.Serializable;
import java.util.ArrayList;

import eu.optimis.sm.gui.client.model.IP;
import eu.optimis.sm.gui.client.model.Service;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("guiservice")
public interface ServiceManagerWebService extends RemoteService, Serializable
 {
	
	public ArrayList<Service> availableServices(String sess_id, boolean test);
	public String undeployService(String sess_id, String serviceID, boolean keepData);
	public String redeployService(String sess_id, String serviceID, boolean keepData);
	public ArrayList<IP> ipRegistry(String sess_id);
	public String getTestTest(String id);
	public ArrayList<Object> loginUser(String name, String pass);
	public Boolean logoutUser(String sess_id, String name);
	public String newAccount(String name, String pass);
    public ArrayList<String> getFileList(String sess_id, String selectedComponent);
    public String getFile(String sess_id, String selectedComponent, String file);
    
	public String getSDOurl(String sess_id);
	public String getIPSurl(String sess_id);
	public String getVPNurl(String sess_id);
	public String getSECurl(String sess_id);
	public String getTRECurl(String sess_id);

	public String getLog(String sess_id, String selectedComponent, String file, int lines);
	public ArrayList<String> getComponentLogList(String sess_id);
	public ArrayList<String> getLogList(String sess_id, String selectedComponent);
}

