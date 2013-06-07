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
package eu.optimis._do.utils;

import org.apache.xmlbeans.XmlObject;
import org.ogf.schemas.graap.wsAgreement.ServiceDescriptionTermType;

import eu.optimis.types.xmlbeans.servicemanifest.XmlBeanManifestType;
import eu.optimis.types.xmlbeans.servicemanifest.XmlBeanServiceManifestDocument;

public class SlaUtil
{
	public static final String TEMPLATE_SDT_SERVICE = "OPTIMIS_SERVICE_SDT";
	public static final String TEMPLATE_SDT_PRICE = "OPTIMIS_SERVICE_PRICE_SDT";
	public static final String TEMPLATE_SDT_OBJECTIVE = "OPTIMIS_SERVICE_DEPLOYMENT_OBJECTIVE_SDT";

	//Extract Manifest from SDT.
	//IMPORTANT this manifest is a copy, not a REFERENCE.
	public static String extractManifestFromSDT(ServiceDescriptionTermType serviceSDT)
	{
		XmlObject[] serviceXML = serviceSDT.selectChildren(XmlBeanServiceManifestDocument.type.getDocumentElementName());

		if (serviceXML.length == 0)	return null;

		XmlBeanManifestType serviceManifestType = (XmlBeanManifestType) serviceXML[0];
		
		XmlBeanServiceManifestDocument serviceManifestDoc = XmlBeanServiceManifestDocument.Factory.newInstance();
		serviceManifestDoc.addNewServiceManifest().set(serviceManifestType);
		String maniefstString  =  serviceManifestDoc.toString();
		
		return maniefstString;
	}
	
	//Find ServiceDescriptionTerm in a template
	public static ServiceDescriptionTermType findSDTbyName(ServiceDescriptionTermType[] sdts, String name)
	{
		ServiceDescriptionTermType sdt = null;
		if (sdts != null)
		{
			for (int i = 0; i < sdts.length; i++)
			{
				if (sdts[i].getName().equals(name))
				{
					sdt = sdts[i];
					break;
				}
			}
		}
		return sdt;
	}
	
}
