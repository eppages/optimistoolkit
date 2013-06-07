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
package eu.optimis.sd.iface;

//import java.io.File;

import java.io.File;

import eu.optimis._do.schemas.Objective;
import eu.optimis.ipdiscovery.datamodel.Provider;
import eu.optimis.manifest.api.sp.Manifest;
//import eu.optimis.manifest.api.sp.Manifest;

import org.ogf.schemas.graap.wsAgreement.negotiation.NegotiationOfferType;
import org.w3.x2005.x08.addressing.EndpointReferenceType;

import eu.optimis.sd.schemas.Deployment;

/**
 * @author Wubin.Li (Viali) 
 * @author Petter Svärd
 */
public interface ISD
{
	//deploy takes a serviceManifest and returns a list of service deployment IDs
	//Objective is the parameter to optimize, i.e, RISK, COST.
	abstract Deployment deploy(Manifest manifest, Objective objective);
	abstract Deployment deploy(File manifestFile, Objective objective);
	
	//This one should be the only one we need..
	abstract Deployment deploy(String manifestXML, Objective objective);

	//This interface is for service undeployment.
	abstract boolean undeploy(String serviceId, EndpointReferenceType agreementEndpoint, boolean keepData);
		
	//This interface is for Data VM Federation.
	abstract Provider suggestFederatedProvider(String manifestXML) throws Exception;
	
	//This is for Deployment Time Federation.
	abstract NegotiationOfferType outSourceVMs(String manifestXML, Objective objective) throws Exception;
}
