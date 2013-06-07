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
import com.google.gwt.user.client.rpc.AsyncCallback;

import eu.optimis.sm.gui.client.model.IP;
import eu.optimis.sm.gui.client.model.Service;

public interface ServiceManagerWebServiceAsync extends Serializable
{
	public void availableServices(String sess_id, boolean test, AsyncCallback<ArrayList<Service>> callback);
	public void ipRegistry(String sess_id, AsyncCallback<ArrayList<IP>> callback);
	public void undeployService(String sess_id, String serviceID, boolean keepData,
			AsyncCallback<String> callback);
	public void redeployService(String sess_id, String serviceID, boolean keepData,
			AsyncCallback<String> callback);
	
	public void getTestTest(String id, AsyncCallback<String> callback);

	public void loginUser(String name, String pass, AsyncCallback<ArrayList<Object>> callback);
	public void logoutUser(String sess_id, String name, AsyncCallback<Boolean> callback);
	public void newAccount(String name, String pass, AsyncCallback<String> callback);
	
    public void getFileList(String sess_id, String selectedComponent, AsyncCallback<ArrayList<String>> callback);
    public void getFile(String sess_id, String selectedComponent, String file, AsyncCallback<String> callback);

	public void getSDOurl(String session_id, AsyncCallback<String> asyncCallback);
	public void getIPSurl(String session_id, AsyncCallback<String> asyncCallback);
	public void getVPNurl(String session_id, AsyncCallback<String> asyncCallback);
	public void getSECurl(String session_id, AsyncCallback<String> asyncCallback);
	public void getTRECurl(String session_id, AsyncCallback<String> asyncCallback);
	
	public void getLog(String sess_id, String selectedComponent, String file, int lines, AsyncCallback<String> callback);
	public void getComponentLogList(String sess_id, AsyncCallback<ArrayList<String>> callback);
	public void getLogList(String sess_id, String selectedComponent, AsyncCallback<ArrayList<String>> callback);
}
