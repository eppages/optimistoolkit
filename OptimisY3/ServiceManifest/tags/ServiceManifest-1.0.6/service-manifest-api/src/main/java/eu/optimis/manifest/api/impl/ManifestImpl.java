/*
 * Copyright (c) 2012, Fraunhofer-Gesellschaft
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 * (1) Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the disclaimer at the end.
 *     Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in
 *     the documentation and/or other materials provided with the
 *     distribution.
 *
 * (2) Neither the name of Fraunhofer nor the names of its
 *     contributors may be used to endorse or promote products derived
 *     from this software without specific prior written permission.
 *
 * DISCLAIMER
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * 
 */

package eu.optimis.manifest.api.impl;

import eu.optimis.manifest.api.exceptions.InvalidDocumentException;
import eu.optimis.manifest.api.exceptions.SplittingNotAllowedException;
import eu.optimis.schemas.optimis.JaxBServiceManifest;
import eu.optimis.types.xmlbeans.servicemanifest.XmlBeanServiceManifestDocument;
import eu.optimis.types.xmlbeans.servicemanifest.XmlBeanVirtualMachineDescriptionType;
import eu.optimis.types.xmlbeans.servicemanifest.infrastructure.XmlBeanIncarnatedVirtualMachineComponentsType;
import eu.optimis.types.xmlbeans.servicemanifest.infrastructure.XmlBeanInfrastructureProviderExtensionType;
import eu.optimis.types.xmlbeans.servicemanifest.infrastructure.XmlBeanInfrastructureProviderExtensionsDocument;
import eu.optimis.types.xmlbeans.servicemanifest.service.XmlBeanServiceProviderExtensionType;
import eu.optimis.types.xmlbeans.servicemanifest.service.XmlBeanServiceProviderExtensionsDocument;
import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlObject;
import org.w3c.dom.Node;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import java.util.List;

/**
 * @author owaeld
 */
class ManifestImpl extends AbstractManifestElement<XmlBeanServiceManifestDocument>
        implements eu.optimis.manifest.api.sp.Manifest, eu.optimis.manifest.api.ip.Manifest
{

    protected ManifestImpl( XmlBeanServiceManifestDocument base )
    {
        super( base );
    }

    @Override
    public String toString()
    {
        if ( !delegate.validate() ) 
        {
            throw new InvalidDocumentException(
                    "Document to be exported is invalid!", delegate );
        }
        return getXmlObject().xmlText();
    }

    @Override
    public XmlBeanServiceManifestDocument toXmlBeanObject()
    {
        if ( !delegate.validate() )
        {
            throw new InvalidDocumentException( "Document to be exported is invalid!", delegate );
        }
        return getXmlObject();
    }

    @Override
    public JaxBServiceManifest toJaxB()
    {
        try
        {
            if ( !delegate.validate() )
            {
                throw new InvalidDocumentException( "Document to be exported is invalid! ",
                        delegate );
            }
            JAXBContext jc = JAXBContext.newInstance( new Class[]{ JaxBServiceManifest.class } );
            Unmarshaller u = jc.createUnmarshaller();
            // deserialize byte array to jaxb
            return ( JaxBServiceManifest ) u.unmarshal( this.delegate.getDomNode() );
        }
        catch ( Exception e )
        {
            throw new RuntimeException( e );
        }
    }

    @Override
    public DataProtectionSectionImpl getDataProtectionSection()
    {
        return new DataProtectionSectionImpl(
                delegate.getServiceManifest().getDataProtectionSection() );
    }

    @Override
    public ElasticitySectionImpl getElasticitySection()
    {
        return new ElasticitySectionImpl( delegate.getServiceManifest().getElasticitySection() );
    }

    @Override
    public String getManifestId()
    {
        return delegate.getServiceManifest().getManifestId();
    }

    @Override
    public void setManifestId( String manifestId )
    {
        delegate.getServiceManifest().setManifestId( manifestId );
    }

    @Override
    public VirtualMachineDescriptionSectionImpl getVirtualMachineDescriptionSection()
    {
        return new VirtualMachineDescriptionSectionImpl(
                ( XmlBeanVirtualMachineDescriptionType ) delegate.getServiceManifest()
                        .getServiceDescriptionSection() );
    }

    @Override
    public String getServiceProviderId()
    {
        return delegate.getServiceManifest().getServiceProviderId();
    }

    @Override
    public void setServiceProviderId( String serviceProviderId )
    {
        delegate.getServiceManifest().setServiceProviderId( serviceProviderId );
    }

    @Override
    public TRECSectionImpl getTRECSection()
    {
        return new TRECSectionImpl( delegate.getServiceManifest().getTRECSection() );
    }

    @Override
    public ServiceProviderExtensionImpl getServiceProviderExtensionSection()
    {
        XmlBeanServiceProviderExtensionType ext = selectServiceProviderExtensionType();
        if ( ext != null )
        {
            return new ServiceProviderExtensionImpl( ext );
        }
        else
        {
            return null;
        }
    }

    @Override
    public void initializeIncarnatedVirtualMachineComponents()
    {
        if ( this.getInfrastructureProviderExtensions() == null )
        {
            initializeInfrastructureProviderExtensions();
            assert getInfrastructureProviderExtensions() != null;
        }
        resetIncarnatedVirtualMachineComponents();

        XmlBeanIncarnatedVirtualMachineComponentsType componentsType =
                XmlBeanIncarnatedVirtualMachineComponentsType.Factory.newInstance();

        TemplateLoader loader = new TemplateLoader();

        VirtualMachineComponentImpl[] vComponentArray =
                getVirtualMachineDescriptionSection().getVirtualMachineComponentArray();

        for ( VirtualMachineComponentImpl component : vComponentArray )
        {
            componentsType.addNewIncarnatedVirtualMachineComponent().set(
                    loader.loadIncarnatedVirtualMachineComponentType( component ) );
        }

        XmlBeanInfrastructureProviderExtensionType extensionDocument =
                selectInfrastructureProviderExtensionElement();

        extensionDocument.addNewIncarnatedServiceComponents().set( componentsType );
    }

    private void resetIncarnatedVirtualMachineComponents()
    {
        // we will remove any incarnated components if they exist
        if ( this.getInfrastructureProviderExtensions().isSetIncarnatedVirtualMachineComponents() )
        {
            this.getInfrastructureProviderExtensions().unsetIncarnatedVirtualMachineComponents();
        }
    }

    @Override
    public ManifestImpl extractComponent( String componentId ) throws SplittingNotAllowedException
    {
        ManifestSplitter splitter = new ManifestSplitter( delegate );
        splitter.splitManifest( componentId );
        delegate.set( splitter.getCurrentManifest() );

        return new ManifestImpl( splitter.getExtractedManifest() );
    }

    @Override
    public ManifestImpl extractComponentList( List<String> componentIds )
            throws SplittingNotAllowedException
    {
        ManifestSplitter splitter = new ManifestSplitter( delegate );
        splitter.splitManifest( componentIds );
        delegate.set( splitter.getCurrentManifest() );

        return new ManifestImpl( splitter.getExtractedManifest() );
    }

    @Override
    public InfrastructureProviderExtensionImpl getInfrastructureProviderExtensions()
    {
        if ( selectInfrastructureProviderExtensionElement() != null )
        {
            XmlBeanInfrastructureProviderExtensionType ext =
                    selectInfrastructureProviderExtensionElement();
            return new InfrastructureProviderExtensionImpl( ext );
        }
        else
        {
            return null;
        }
    }

    @Override
    public void unsetInfrastructureProviderExtensions()
    {
        if ( isSetInfrastructureProviderExtensions() )
        {
            XmlCursor editCursor = selectInfrastructureProviderExtensionElement().newCursor();
            editCursor.removeXml();
        }
    }

    private XmlBeanInfrastructureProviderExtensionType selectInfrastructureProviderExtensionElement()
    {
        XmlObject[] result =
                delegate.getServiceManifest().selectChildren(
                        XmlBeanInfrastructureProviderExtensionsDocument.type
                                .getDocumentElementName() );
        if ( result.length > 0 )
        {
            return ( XmlBeanInfrastructureProviderExtensionType ) result[ 0 ];
        }
        return null;
    }

    private XmlBeanServiceProviderExtensionType selectServiceProviderExtensionType()
    {
        XmlObject[] elements =
                delegate.getServiceManifest().selectChildren(
                        XmlBeanServiceProviderExtensionsDocument.type.getDocumentElementName() );
        if ( elements.length > 0 )
        {
            return ( XmlBeanServiceProviderExtensionType ) elements[ 0 ];
        }
        else
        {
            return null;
        }
    }

    @Override
    public void unsetServiceProviderExtensions()
    {
        if ( selectServiceProviderExtensionType() != null )
        {
            selectServiceProviderExtensionType().newCursor().removeXml();
        }
    }

    @Override
    public void initializeInfrastructureProviderExtensions()
    {
        if ( !isSetInfrastructureProviderExtensions() )
        {
            XmlBeanInfrastructureProviderExtensionsDocument newIPExtension =
                    XmlBeanInfrastructureProviderExtensionsDocument.Factory.newInstance();
            newIPExtension.addNewInfrastructureProviderExtensions();
            // now we can import the new node
            Node node = newIPExtension.getInfrastructureProviderExtensions().getDomNode();
            Node importedNode =
                    delegate.getServiceManifest().getDomNode().getOwnerDocument()
                            .importNode( node, true );
            delegate.getServiceManifest().getDomNode().appendChild( importedNode );
        }
    }

    @Override
    public boolean isSetInfrastructureProviderExtensions()
    {
        if ( selectInfrastructureProviderExtensionElement() == null )
        {
            return false;
        }
        else
        {
            return true;
        }
    }
}
