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

import org.w3.x2005.x08.addressing.EndpointReferenceType;

public class ManifestDeployment 
{
	private String manifest;
	private EndpointReferenceType endpointReference;
	
	public ManifestDeployment()
	{
		this.manifest = null;
		this.endpointReference = null;
	}
	
	public ManifestDeployment(String manifest, EndpointReferenceType endpointReference)
	{
		this.manifest = manifest;
		this.endpointReference = endpointReference;
	}
	
	public void setManifest(String manifest) 
	{
		this.manifest = manifest;
	}
	
	public String getManifest() 
	{
		return manifest;
	}

	public void setEndpointReference(EndpointReferenceType endpointReference) 
	{
		this.endpointReference = endpointReference;
	}

	public EndpointReferenceType getEndpointReference() 
	{
		return endpointReference;
	}	
}
