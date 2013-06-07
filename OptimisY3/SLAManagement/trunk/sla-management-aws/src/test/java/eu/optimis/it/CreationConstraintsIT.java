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
package eu.optimis.it;

import eu.optimis.manifest.api.sp.Manifest;
import eu.optimis.types.xmlbeans.servicemanifest.XmlBeanManifestType;
import eu.optimis.types.xmlbeans.servicemanifest.XmlBeanServiceManifestDocument;
import org.apache.xmlbeans.XmlObject;
import org.ogf.graap.wsag.api.AgreementOffer;
import org.ogf.graap.wsag.api.client.AgreementClient;
import org.ogf.graap.wsag.api.types.AgreementOfferType;
import org.ogf.schemas.graap.wsAgreement.AgreementTemplateType;

/**
 * @author hrasheed
 */
public class CreationConstraintsIT extends AbstractSLAIT
{
    
    public void testAgreementCreationSuccessIfMaxInstances1()
    {
        try {
            
            AgreementTemplateType template = getAgreementFactory().getTemplate(TEMPLATE_NAME, TEMPLATE_ID);
            AgreementOffer offer = new AgreementOfferType(template);
            XmlObject[] serviceXML = getServiceManifestXmlObject(offer);
            Manifest spManifest = getSPManifestFromXmlObject(serviceXML[0]);
            spManifest.getVirtualMachineDescriptionSection().getVirtualMachineComponentArray(0).getAllocationConstraints()
                    .setUpperBound(1);

            serviceXML[0].set(spManifest.toXmlBeanObject().getServiceManifest());
            AgreementClient agreementClient = null;            
            agreementClient = getAgreementFactory().createAgreement(offer);
            
            assertNotNull("Agreement creation failed", agreementClient);
            
        }
        catch (Exception e)
        {
            fail("Agreement creation should not fail because of creation constraints" + e.getMessage());
        }
    }

    public void testAgreementCreationFailsIfMaxInstancesAbove10() 
    {
        try {
            
            AgreementTemplateType template = getAgreementFactory().getTemplate(TEMPLATE_NAME, TEMPLATE_ID);
            AgreementOffer offer = new AgreementOfferType(template);
            XmlObject[] serviceXML = getServiceManifestXmlObject(offer);
            Manifest spManifest = getSPManifestFromXmlObject(serviceXML[0]);

            spManifest.getVirtualMachineDescriptionSection().getVirtualMachineComponentArray(0).getAllocationConstraints()
                    .setUpperBound(800);

            serviceXML[0].set(spManifest.toXmlBeanObject().getServiceManifest());

            AgreementClient agreementClient = null;
            
            agreementClient = getAgreementFactory().createAgreement(offer);
            
            assertNull("Agreement creation did not fail", agreementClient);
            
        }
        catch ( Exception e )
        {
            System.out.println("Agreement creation should fail because of creation constraints");
            System.out.println(e.getMessage());
        }
               
    }

    public void testAgreementCreationShouldFailIfMemoryAboveThreshold(){
        try {

            AgreementTemplateType template = getAgreementFactory().getTemplate(TEMPLATE_NAME, TEMPLATE_ID);
            AgreementOffer offer = new AgreementOfferType(template);
            XmlObject[] serviceXML = getServiceManifestXmlObject(offer);
            Manifest spManifest = getSPManifestFromXmlObject(serviceXML[0]);

            //set memory size 1 zero too much.
            spManifest.getVirtualMachineDescriptionSection().getVirtualMachineComponentArray(0).getOVFDefinition().getVirtualSystem().getVirtualHardwareSection().setMemorySize(81920123);

            serviceXML[0].set(spManifest.toXmlBeanObject().getServiceManifest());

            AgreementClient agreementClient = getAgreementFactory().createAgreement(offer);

            assertNull("Agreement creation did not fail", agreementClient);

        }
        catch (Exception e)
        {
            System.out.println("Agreement creation should fail because of creation constraints");
            System.out.println(e.getMessage());
        }
    }

    /**
     * agreement creation fails if size is above 512000
     */
    public void testAgreementCreationShouldFailIfSizeAboveThreshold(){
        try {

            AgreementTemplateType template = getAgreementFactory().getTemplate(TEMPLATE_NAME, TEMPLATE_ID);
            AgreementOffer offer = new AgreementOfferType(template);
            XmlObject[] serviceXML = getServiceManifestXmlObject(offer);
            Manifest spManifest = getSPManifestFromXmlObject(serviceXML[0]);

            //set vm image size above threshold of 512000
            spManifest.getVirtualMachineDescriptionSection().getVirtualMachineComponentArray(0).getOVFDefinition().getDiskSection().getImageDisk().setCapacity("1512000");
            serviceXML[0].set(spManifest.toXmlBeanObject().getServiceManifest());

            AgreementClient agreementClient = getAgreementFactory().createAgreement(offer);

            assertNull("Agreement creation did not fail", agreementClient);

        }
        catch (Exception e)
        {
            System.out.println("Agreement creation should fail because of creation constraints");
            System.out.println(e.getMessage());
        }
    }


    /**
     * test that if number of cpus is above threshold, agreement creation fails.
     */
    public void testAgreementCreationShouldFailIfCPUNumberAboveThreshold(){
        try {

            AgreementTemplateType template = getAgreementFactory().getTemplate(TEMPLATE_NAME, TEMPLATE_ID);
            AgreementOffer offer = new AgreementOfferType(template);
            XmlObject[] serviceXML = getServiceManifestXmlObject(offer);
            Manifest spManifest = getSPManifestFromXmlObject(serviceXML[0]);

            //set memory size 1 zero too much.
            spManifest.getVirtualMachineDescriptionSection().getVirtualMachineComponentArray(0).getOVFDefinition().getVirtualSystem().getVirtualHardwareSection().setNumberOfVirtualCPUs(81920123);

            serviceXML[0].set(spManifest.toXmlBeanObject().getServiceManifest());

            AgreementClient agreementClient = getAgreementFactory().createAgreement(offer);

            assertNull("Agreement creation did not fail", agreementClient);

        }
        catch ( Exception e )
        {
            System.out.println("Agreement creation should fail because of creation constraints");
            System.out.println(e.getMessage());
        }
    }

    private Manifest getSPManifestFromXmlObject( XmlObject xmlObject )
    {
        XmlBeanManifestType serviceManifestType = (XmlBeanManifestType) xmlObject;

        XmlBeanServiceManifestDocument serviceManifestDoc =
                XmlBeanServiceManifestDocument.Factory.newInstance();
        serviceManifestDoc.addNewServiceManifest().set(serviceManifestType);

        return Manifest.Factory.newInstance(serviceManifestDoc);
    }

    private XmlObject[] getServiceManifestXmlObject(AgreementOffer offerType)
    {
        XmlObject[] serviceXML =
                offerType.getTerms().getAll().getServiceDescriptionTermArray(0).selectChildren(
                        XmlBeanServiceManifestDocument.type.getDocumentElementName());

        if (serviceXML.length == 0)
        {
            fail("there is no service manifest doc in service description terms.");
        }
        return serviceXML;
    }
}
