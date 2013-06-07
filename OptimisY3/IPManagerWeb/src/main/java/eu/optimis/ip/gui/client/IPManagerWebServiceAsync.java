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
package eu.optimis.ip.gui.client;

import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;
import eu.optimis.ip.gui.client.model.COInfrastructureOperationData;
import eu.optimis.ip.gui.client.model.COServiceOperationData;
import eu.optimis.ip.gui.client.model.IP;
import java.util.ArrayList;

public interface IPManagerWebServiceAsync {

    //IP Configuration
    public void setBLO(String blo, List<String> constraints, AsyncCallback callback);

    public void getCOUrl(AsyncCallback<String> callback);

    //GUIs
    public void getTRECUrl(AsyncCallback<String> callback);

    public void getMonitoringUrl(AsyncCallback<String> callback);

    public void getDMUrl(AsyncCallback<String> callback);

    public void getEmotiveUrl(AsyncCallback<String> callback);
    
    public void getACUrl(AsyncCallback<String> callback);

    //Cloud Optimizer
    public void getCOServiceOperationData(AsyncCallback<COServiceOperationData> callback);
    public void getCOInfrastructureOperationData(AsyncCallback<COInfrastructureOperationData> callback);
        
    
    //Component Output
    public void getComponentLogList(AsyncCallback<ArrayList<String>> callback);
    public void getLogList(String selectedComponent, AsyncCallback<ArrayList<String>> callback);
    public void getLog(String component, String file, int lines, AsyncCallback<String> callback);

    //Component configuration
    public void getComponentConfigurationList(AsyncCallback<ArrayList<String>> callback);
    public void getFileList(String selectedComponent, AsyncCallback<ArrayList<String>> callback);
    public void getFile(String selectedComponent, String file, AsyncCallback<String> callback);
    
    //IP Registry
    public void ipRegistry(AsyncCallback<ArrayList<IP>> callback);

    
    //Authentication
    public void loginUser(String name, String pass, AsyncCallback<ArrayList<Object>> callback);

    public void logoutUser(String sess_id, String name, AsyncCallback<Boolean> callback);

    public void newAccount(String name, String pass, AsyncCallback<String> callback);
}
