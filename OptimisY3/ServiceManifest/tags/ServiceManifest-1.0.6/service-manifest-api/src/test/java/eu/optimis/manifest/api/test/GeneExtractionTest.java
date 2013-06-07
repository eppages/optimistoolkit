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
package eu.optimis.manifest.api.test;

import eu.optimis.manifest.api.exceptions.SplittingNotAllowedException;
import eu.optimis.manifest.api.impl.IPManifestFactory;
import eu.optimis.manifest.api.ip.Manifest;
import eu.optimis.types.xmlbeans.servicemanifest.XmlBeanServiceManifestDocument;

import java.io.InputStream;

public class GeneExtractionTest extends AbstractTestApi
{
    /**
     * Extracts the gene detection component from the a manifest.
     * The component has no affinity to other components, but other components have
     * high affinity to each other.
     */
    public void testExtractGeneDetectionComponent() throws SplittingNotAllowedException
    {
        //
        // load the gene detection manifest as XmlBean
        //
        XmlBeanServiceManifestDocument manifestDocument = null;
        try
        {
            InputStream in = XmlObjectSerializationTest.class
                    .getResourceAsStream( "/service_manifest_gene_detection.xml" );
            manifestDocument = XmlBeanServiceManifestDocument.Factory.parse( in );
        }
        catch ( Exception e )
        {
            fail( "could not parse manifest" );
        }

        //
        // parse the sample IP manifest with the OPTIMIS API and extract the gene extraction component
        //
        IPManifestFactory factory = new IPManifestFactory();
        Manifest manifest = factory.newInstance( manifestDocument );

        Manifest extracted = manifest.extractComponent( "optimis-pm-GeneDetection" );

        extracted.toString();
    }
}
