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
package eu.optimis.sd.schemas;

import java.util.ArrayList;
import java.util.List;

public class Deployment 
{
	private String serviceId;
	private List<ManifestDeployment> deploymentList;
	
	public Deployment(String serviceId)
	{
		this.serviceId = serviceId;	
		this.deploymentList = new ArrayList<ManifestDeployment>();
	}
	
	public Deployment(String serviceId, List<ManifestDeployment> deploymentList)
	{
		this.setServiceId(serviceId);
		this.deploymentList = deploymentList;
	}
	
	public List<ManifestDeployment> getDeploymentList()
	{
		return this.deploymentList;
	}
	
	public void setDeploymentList(List<ManifestDeployment> deploymentList)
	{
		this.deploymentList = deploymentList;
	}

	public void setServiceId(String serviceId) 
	{
		this.serviceId = serviceId;
	}

	public String getServiceId() 
	{
		return serviceId;
	}
}
