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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

import junit.framework.TestCase;

import org.apache.xmlbeans.XmlError;
import org.apache.xmlbeans.XmlOptions;

import eu.optimis.manifest.api.sp.Manifest;
import eu.optimis.types.xmlbeans.servicemanifest.XmlBeanServiceManifestDocument;

/**
 * @author owaeld
 */
public abstract class AbstractTestApi extends TestCase
{
    private static final String XPATH_REQ_MEM_SIZE = "//ovf:VirtualSystem[@id='system-${componentId}']"
        + "//ovf:Item[/rasd:AllocationUnits = 'MegaBytes']/rasd:VirtualQuantity/";

    /**
     * Default manifest object for testing.
     */
    private Manifest manifest;

    @Override
    protected void setUp() throws Exception
    {
        super.setUp();
        this.setManifest( Manifest.Factory.newInstance( "DemoApp", "jboss" ) );

        System.out.println( "-- Output from " + this.getName() + " --" );
    }

    // the default template does not provide any elasticity section, but for some tests we need an example
    protected void initializeElasticity()
    {
        getManifest().getElasticitySection().addNewRule( "jboss", "MinMemorySize" );
        /*
         * TODO: reenable for year 3 getManifest().getElasticitySection().addNewInternalVariable(
         * "REQ_MEMORY_SIZE", "int", XPATH_REQ_MEM_SIZE );
         * getManifest().getElasticitySection().addNewExternalVariable( "PROVIDED_MEMORY_SIZE", "int",
         * "http://my-monitoring-system.org/${serviceId}/system-${componentId}/memorySize" ); ElasticityRule
         * rule = getManifest().getElasticitySection().addNewRule( "jboss", "MinMemorySize" );
         * rule.getEffect().setAction( "scaleUp" ); rule.getEffect().setImportance( 10 );
         * rule.getCondition().getAssessmentCriteria().setFrequency( 1 );
         * rule.getCondition().getAssessmentCriteria().setWindow( "P1M" ); rule.getCondition().setExpression(
         * "REQ_MEMORY_SIZE le PROVIDED_MEMORY_SIZE" );
         */
    }

    protected void writeToFile( XmlBeanServiceManifestDocument manifest, String fileName )
    {
        try
        {
            //
            // if system property is not set (i.e. test case was started from IDE )
            // we use the current directory to store the file
            //
            String targetDir = System.getProperty( "manifestSampleDir", "." );

            File file = new File( targetDir + File.separator + File.separator + fileName + ".xml" );
            FileWriter fstream = new FileWriter( file );
            BufferedWriter out = new BufferedWriter( fstream );
            out.write( manifest.xmlText( new XmlOptions().setSavePrettyPrint() ) );
            System.out.println( fileName + " was written to " + file.getAbsolutePath() );
            // Close the output stream
            out.close();
        }
        catch ( Exception e )
        {
            // Catch exception if any
            System.err.println( "Error: " + e.getMessage() );
            fail( e.getMessage() );
        }
    }

    public Manifest getManifest()
    {
        return manifest;
    }

    public void setManifest( Manifest manifest )
    {
        this.manifest = manifest;
    }

    public void printErrors()
    {
        for ( XmlError error : getManifest().getErrors() )
        {
            System.out.println( error );
        }
    }

}
