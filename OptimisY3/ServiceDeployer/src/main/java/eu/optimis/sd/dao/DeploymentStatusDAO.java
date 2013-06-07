/*
 Copyright (C) 2012-2013 Umeå University

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/
package eu.optimis.sd.dao;

import java.util.ArrayList;
import java.util.List;

//import org.apache.log4j.Logger;

import eu.optimis.sd.schemas.st.CompletedStatus;
import eu.optimis.sd.schemas.st.ErrorStatus;
import eu.optimis.sd.schemas.st.NormalStatus;
import eu.optimis.sd.schemas.st.Status;

/**
 * @author Wubin.Li (Viali) 
 * @author Petter Svärd
 *
 */

public class DeploymentStatusDAO
{
//	private Logger logger = Logger.getLogger(DeploymentStatusDAO.class);	

	private  final List<Status> statusList;
	private Status latestRootStatus;
		
    public DeploymentStatusDAO()
    {
        this.statusList = new ArrayList<Status>();
    }
    
    /**
     * @param component
     * @param operation
     * @param operationDone
     * @param componentProgress
     */
    public void addSubComponentStatus(String component, String operation, boolean operationDone, int componentProgress)
    {
    	Status status = new NormalStatus(component, operation, operationDone, componentProgress);
    	this.statusList.add(status);
    //	logger.debug("Normal SubComponent Status: "+component+": "+ operation+" done: "+operationDone+"/"+componentProgress+" stored.");
    }
    
    /**
     * @param component
     * @param operation
     * @param operationDone
     * @param componentProgress
     */
    public void addRootComponentStatus(String component, String operation, boolean operationDone, int componentProgress)
    {
    	Status status = new NormalStatus(component, operation, operationDone, componentProgress, true);
    	this.statusList.add(status);
    	this.latestRootStatus = status;
    //	logger.debug("Normal RootComponent Status: "+component+": "+ operation+" done: "+operationDone+"/"+componentProgress+" stored.");
    }
    
    /**
     * @param component
     * @param operation
     * @param errorMsg
     */
    public void addErrorStatus(String component, String operation, String errorMsg)
    {
    	Status status = new ErrorStatus(component, operation,  errorMsg);
    	this.statusList.add(status);
    	this.latestRootStatus = status;
    //	String errorMessage = "ErrorStatus: "+component+": "+ operation+": "+errorMsg;
    //	logger.debug(errorMessage+" stored.");
    }
    
    
    /**
     * @param message
     */
    public void addCompletedStatus(String message)
    {
    	Status status = new CompletedStatus(message);
    	this.statusList.add(status);
    	this.latestRootStatus = status;
    //	logger.debug("CompletedStatus: "+message+" stored.");
    }
    
    
    /**
     * @return the status stack
     */
    public List<Status> getStatusList()
    {
    	return this.statusList;
    }

	public Status getLatestRootStatus()
	{
		return latestRootStatus;
	}  
}
