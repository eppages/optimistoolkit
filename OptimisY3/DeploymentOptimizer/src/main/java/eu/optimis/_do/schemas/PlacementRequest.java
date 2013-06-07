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
package eu.optimis._do.schemas;

import java.util.*;

import eu.optimis.ipdiscovery.datamodel.Provider;

/**
 * @author Wubin.Li (Viali)
 * @author Petter Svärd
 * 
 */

public class PlacementRequest
{
	private String manifestXML;
	private List<Provider> providers;
	private Objective objective;
	private Map<String, String> properties;

	public PlacementRequest(String manifestXML, List<Provider> providers,
			Objective objective)
	{
		super();
		this.manifestXML = manifestXML;
		this.providers = providers;
		this.objective = objective;
		this.properties=new HashMap<String, String>();
	}
	
	public String getManifestXML()
	{
		return this.manifestXML;
	}
	public void setManifestXML(String manifestXML)
	{
		this.manifestXML = manifestXML;
	}
	public Map<String, String> getProperties()
	{
		return properties;
	}
	public List<Provider> getProviders()
	{
		return providers;
	}
	public void setProviders(List<Provider> providers)
	{
		this.providers = providers;
	}
	public Objective getObjective()
	{
		return objective;
	}
	public void setObjective(Objective objective)
	{
		this.objective = objective;
	}
}
