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
 */

package eu.optimis.manifest.api.impl;

import eu.optimis.manifest.api.utils.TemplateLoader;
import eu.optimis.manifest.api.utils.XmlValidator;
import eu.optimis.schemas.optimis.JaxBServiceManifest;
import eu.optimis.types.xmlbeans.servicemanifest.XmlBeanServiceManifestDocument;
import eu.optimis.types.xmlbeans.servicemanifest.XmlBeanVirtualMachineDescriptionType;
import eu.optimis.types.xmlbeans.servicemanifest.infrastructure.IncarnatedVirtualMachineComponentsType;
import eu.optimis.types.xmlbeans.servicemanifest.infrastructure.InfrastructureProviderExtensionType;
import eu.optimis.types.xmlbeans.servicemanifest.infrastructure.InfrastructureProviderExtensionsDocument;
import eu.optimis.types.xmlbeans.servicemanifest.service.ServiceProviderExtensionType;
import eu.optimis.types.xmlbeans.servicemanifest.service.ServiceProviderExtensionsDocument;
import org.apache.xmlbeans.XmlObject;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

/**
 * @author owaeld
 */
class ManifestImpl extends AbstractManifestElement<XmlBeanServiceManifestDocument>
        implements eu.optimis.manifest.api.sp.Manifest, eu.optimis.manifest.api.ip.Manifest {

    protected ManifestImpl(XmlBeanServiceManifestDocument base) {
        super(base);
    }

    @Override
    public String toString() {
        if (!XmlValidator.validate(delegate)) {
            throw new RuntimeException("Document to be exported is invalid!");
        }
        return getXmlObject().xmlText();
    }


    @Override
    public XmlBeanServiceManifestDocument toXmlBeanObject() {
        if (!XmlValidator.validate(delegate)) {
            throw new RuntimeException("Document to be exported is invalid!");
        }
        return getXmlObject();
    }


    @Override
    public JaxBServiceManifest toJaxB() {
        try {
            if (!XmlValidator.validate(delegate)) {
                throw new RuntimeException("Document to be exported is invalid!");
            }
            JAXBContext jc = JAXBContext.newInstance(new Class[]{JaxBServiceManifest.class});
            Unmarshaller u = jc.createUnmarshaller();
            // deserialize byte array to jaxb
            JaxBServiceManifest jaxbObject = (JaxBServiceManifest) u.unmarshal(this.delegate.getDomNode());
            return jaxbObject;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public DataProtectionSectionImpl getDataProtectionSection() {
        return new DataProtectionSectionImpl(delegate.getServiceManifest().getDataProtectionSection());
    }

    @Override
    public ElasticityArraySectionImpl getElasticitySection() {
        return new ElasticityArraySectionImpl(delegate.getServiceManifest().getElasticitySection());
    }


    @Override
    public String getManifestId() {
        return delegate.getServiceManifest().getManifestId();
    }


    @Override
    public void setManifestId(String manifestId) {
        delegate.getServiceManifest().setManifestId(manifestId);
    }



    @Override
    public VirtualMachineDescriptionSectionImpl getVirtualMachineDescriptionSection() {
        return new VirtualMachineDescriptionSectionImpl(
                (XmlBeanVirtualMachineDescriptionType) delegate.getServiceManifest().getServiceDescriptionSection());
    }


    @Override
    public String getServiceProviderId() {
        return delegate.getServiceManifest().getServiceProviderId();
    }

    @Override
    public void setServiceProviderId(String serviceProviderId) {
        delegate.getServiceManifest().setServiceProviderId(serviceProviderId);
    }


    @Override
    public TRECSectionImpl getTRECSection() {
        return new TRECSectionImpl(delegate.getServiceManifest().getTRECSection());
    }


    @Override
    public ServiceProviderExtensionImpl getServiceProviderExtensionSection() {
        ServiceProviderExtensionType ext = selectServiceProviderExtensionType();
        if (ext != null) {
            return new ServiceProviderExtensionImpl(ext);
        } else {
            return null;
        }
    }



    @Override
    public void initializeIncarnatedVirtualMachineComponentsType() {
        if (this.getInfrastructureProviderExtensions().getIncarnatedVirtualMachineComponents() == null) {
            IncarnatedVirtualMachineComponentsType componentsType = IncarnatedVirtualMachineComponentsType.Factory.newInstance();
            TemplateLoader loader = new TemplateLoader();
            for (VirtualMachineComponentImpl component : getVirtualMachineDescriptionSection().getVirtualMachineComponentArray()) {
                componentsType.addNewIncarnatedVirtualMachineComponent().set(loader.loadIncarnatedVirtualMachineComponentType(component));
            }
            InfrastructureProviderExtensionType extensionDocument = selectInfrastructureProviderExtensionElement();
            extensionDocument.addNewIncarnatedServiceComponents().set(componentsType);
        }
    }

    @Override
    public InfrastructureProviderExtensionImpl getInfrastructureProviderExtensions() {
        InfrastructureProviderExtensionType ext = selectInfrastructureProviderExtensionElement();
        return new InfrastructureProviderExtensionImpl(ext);
    }

    private InfrastructureProviderExtensionType selectInfrastructureProviderExtensionElement() {
        XmlObject[] result = delegate.getServiceManifest().selectChildren(InfrastructureProviderExtensionsDocument.type.getDocumentElementName());
        if (result.length > 0) {
            return (InfrastructureProviderExtensionType) result[0];
        }
        return null;
    }

    private ServiceProviderExtensionType selectServiceProviderExtensionType() {
        XmlObject[] elements = delegate.getServiceManifest().selectChildren(ServiceProviderExtensionsDocument.type.getDocumentElementName());
        if (elements.length > 0) {
            ServiceProviderExtensionType ext = (ServiceProviderExtensionType) elements[0];
            return ext;
        } else {
            return null;
        }
    }


}
