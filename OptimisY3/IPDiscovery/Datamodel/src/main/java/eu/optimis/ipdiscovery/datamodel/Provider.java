/*Copyright (C) 2012 Umeå University

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
package eu.optimis.ipdiscovery.datamodel;

import java.util.Map;

import javax.xml.bind.annotation.XmlRootElement;


/**
 * @author Wubin.Li (Viali)
 * @author Petter Svärd
 * @author Daniel Espling
 * 
 */

@XmlRootElement
public class Provider
{
	public static enum Type
	{
		Optimis, OCCI, EC2
	}
	
	private String name;
	private String identifier;
	private String ipAddress;
	private String providerType; //aka optimis, occi, ec2, etc 
	private String cloudQosUrl;
	private String agrTemplateName;
	private String agrTemplateId;
	private String dmUrl;
	
	
	private Map<String, String> properties; //any other properties

	//Needed for Jersey REST
	public Provider() {}

	public Provider(String name, String identifier, String ipAddress,
			String providertype, String cloudQoSUrl, String agrTemplateName,
			String agrTemplateId, Map<String, String> properties, String dmurl)
	{
		super();
		this.name = name;
		this.identifier = identifier;
		this.ipAddress = ipAddress;
		this.providerType = providertype;
		this.cloudQosUrl = cloudQoSUrl;
		this.agrTemplateName = agrTemplateName;
		this.agrTemplateId = agrTemplateId;
		this.properties = properties;
		this.dmUrl = dmurl;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public String getIdentifier()
	{
		return identifier;
	}

	public void setIdentifier(String identifier)
	{
		this.identifier = identifier;
	}

	public String getIpAddress()
	{
		return ipAddress;
	}

	public void setIpAddress(String ipAddress)
	{
		this.ipAddress = ipAddress;
	}

	public String getProviderType()
	{
		return providerType;
	}

	public void setProviderType(String providertype)
	{
		this.providerType = providertype;
	}
	
	

	public String getCloudQosUrl()
	{
		return cloudQosUrl;
	}

	public void setCloudQosUrl(String cloudQosUrl)
	{
		this.cloudQosUrl = cloudQosUrl;
	}

	public Map<String, String> getProperties()
	{
		return properties;
	}

	public void setProperties(Map<String, String> properties)
	{
		this.properties = properties;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (obj == null)
			return false;
		if (obj instanceof Provider)
		{
			return this.identifier.equals(((Provider)obj).getIdentifier());
		}
		return false;
	}
	
	
	@Override
	public int hashCode()
	{
		return this.identifier.hashCode();
	}
		
	public String getAgrTemplateName()
	{
		return agrTemplateName;
	}

	public void setAgrTemplateName(String agrTemplateName)
	{
		this.agrTemplateName = agrTemplateName;
	}

	public String getAgrTemplateId()
	{
		return agrTemplateId;
	}

	public void setAgrTemplateId(String agrTemplateId)
	{
		this.agrTemplateId = agrTemplateId;
	}


	public String getDMUrl()
	{
		return this.dmUrl;
	}

	public void setDMUrl(String dmurl)
	{
		this.dmUrl = dmurl;
	}


	@Override
	public String toString() {
		
		StringBuilder propString = new StringBuilder();
		if (properties != null) {
			for (String key : properties.keySet()) {
				propString.append("'" + key + "':'" + properties.get(key) + "'\n");
			}
		}
		
		return "Provider [name=" + name + ", identifier=" + identifier
				+ ", ipAddress=" + ipAddress + ", providertype=" + providerType
				+ ", properties=[\n" + propString.toString() + "]]";
	}
	
	
}
