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

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import eu.optimis.ip.gui.client.model.COInfrastructureOperationData;
import eu.optimis.ip.gui.client.model.COServiceOperationData;
import eu.optimis.ip.gui.client.model.IP;
import java.util.ArrayList;

@RemoteServiceRelativePath("guiservice")
public interface IPManagerWebService extends RemoteService {

    //IP Configuration
    public void setBLO(String BLO, List<String> constraints);

    public String getCOUrl();

    //GUIs
    public String getTRECUrl();

    public String getMonitoringUrl();

    public String getDMUrl();

    public String getEmotiveUrl();
    
    public String getACUrl();

    //Cloud Optimizer
    public COServiceOperationData getCOServiceOperationData();
    public COInfrastructureOperationData getCOInfrastructureOperationData();
    
    
    //Component Output
    public ArrayList<String> getComponentLogList();
    public ArrayList<String> getLogList(String selectedComponent);
    public String getLog(String component, String file, int lines);
    
    //Component configuration
    public ArrayList<String> getComponentConfigurationList();
    public ArrayList<String> getFileList(String selectedComponent);
    public String getFile(String selectedComponent, String file);
    
    //IP Registry
    public ArrayList<IP> ipRegistry();

    //Authentication
    public ArrayList<Object> loginUser(String name, String pass);

    public Boolean logoutUser(String sess_id, String name);

    public String newAccount(String name, String pass);
}
