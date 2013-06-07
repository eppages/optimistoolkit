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



import org.ogf.schemas.graap.wsAgreement.ServiceDescriptionTermType;
import org.ogf.schemas.graap.wsAgreement.negotiation.NegotiationOfferType;

import eu.optimis._do.schemas.internal.TrecObj;
import eu.optimis._do.utils.SlaUtil;
import eu.optimis.ipdiscovery.datamodel.Provider;
import eu.optimis.manifest.api.sp.Manifest;
import eu.optimis.types.xmlbeans.servicemanifest.XmlBeanServiceManifestDocument;

/**
 * @author Wubin.Li (Viali)
 * @author Petter Svärd
 * A Placement object contains a manifest to deploy, 
 * and a list potential ranked destination providers.
 * REMEMBER that all providers are already ranked,
 * i.e., the one with a lower index is better than 
 * the one with a higher index.
 */

//@XmlRootElement
public class Placement
{
	private NegotiationOfferType counterOffer;
	private Provider provider;
	private TrecObj trec;
	
	public String getServiceId() throws Exception
	{
		String manifestXML = this.retrieveManifestCopy();
		XmlBeanServiceManifestDocument doc = XmlBeanServiceManifestDocument.Factory.parse(manifestXML);
		Manifest manifest = Manifest.Factory.newInstance(doc);
		String serviceId = manifest.getVirtualMachineDescriptionSection().getServiceId();
		return serviceId;
	}
	
	public void setTREC(TrecObj _trec)
	{
		this.trec = _trec;
	}
	
	public TrecObj getTREC()
	{
		return this.trec;
	}
	
	public Provider getProvider()
	{
		return provider;
	}

	public void setProvider(Provider provider)
	{
		this.provider = provider;
	}

	public NegotiationOfferType getOffer()
	{
		return this.counterOffer;
	}

	public void setOffer(NegotiationOfferType counterOffer)
	{
		this.counterOffer = counterOffer;
	}
	//COPY
	public String retrieveManifestCopy()
	{
		ServiceDescriptionTermType[] sdts = this.counterOffer.getTerms().getAll().getServiceDescriptionTermArray();
		ServiceDescriptionTermType serviceSDT = SlaUtil.findSDTbyName(sdts , SlaUtil.TEMPLATE_SDT_SERVICE);
		return SlaUtil.extractManifestFromSDT(serviceSDT);
	}
	
	public void updateOffer(Manifest manifest) throws Exception
	{
		ServiceDescriptionTermType[] sdts =  this.counterOffer.getTerms().getAll().getServiceDescriptionTermArray();
		
		//------Manifest SDT Update------
		ServiceDescriptionTermType manifestSDT = SlaUtil.findSDTbyName(sdts, SlaUtil.TEMPLATE_SDT_SERVICE);
        if ( manifestSDT == null )
        {
            throw new Exception( "there is no service  SDT in agreement template." );
        }
		String name = manifestSDT.getName();
		String serviceName = manifestSDT.getServiceName();

		XmlBeanServiceManifestDocument doc = XmlBeanServiceManifestDocument.Factory.parse(manifest.toString());
		manifestSDT.set(doc);
		manifestSDT.setName( name );
		manifestSDT.setServiceName( serviceName );
	}
}