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

import eu.optimis.manifest.api.sp.AntiAffinityRule;
import eu.optimis.types.xmlbeans.servicemanifest.XmlBeanAntiAffinityConstraintType;

/**
 * Checks adding/removing of anti affinity rules.
 * 
 * @author hrasheed
 * 
 */
public class SPAntiAffinityRuleManagmentTest extends AbstractTestApi
{

    public void testAddAntiAffinityRule()
    {
        int previousLength = getManifest().getVirtualMachineDescriptionSection().getAntiAffinityRules().length;
        getManifest().getVirtualMachineDescriptionSection().addNewAntiAffinityRule( "test-affinity-rule-id",
            XmlBeanAntiAffinityConstraintType.LOW.toString() );

        int finalLength = getManifest().getVirtualMachineDescriptionSection().getAntiAffinityRules().length;
        System.out.println( "final no. of rules: " + finalLength );

        assertEquals( previousLength + 1, finalLength );
    }

    public void testRemoveAntiAffinityRule()
    {
        System.out.println( "\nremoveAntiAffinityRule test invoked." );

        int previousLength = getManifest().getVirtualMachineDescriptionSection().getAntiAffinityRules().length;
        getManifest().getVirtualMachineDescriptionSection().addNewAntiAffinityRule( "test-affinity-rule-id-1",
            XmlBeanAntiAffinityConstraintType.LOW.toString() );

        getManifest().getVirtualMachineDescriptionSection().removeAntiAffinityRule( 1 );

        int finalLength = getManifest().getVirtualMachineDescriptionSection().getAntiAffinityRules().length;
        System.out.println( "final no. of rules: " + finalLength );

        assertEquals( previousLength, finalLength );
    }

    public void testAffinityRuleValues()
    {
    	AntiAffinityRule rule =
            getManifest().getVirtualMachineDescriptionSection().addNewAntiAffinityRule( "xxx",
                XmlBeanAntiAffinityConstraintType.LOW.toString() );

        assertEquals( XmlBeanAntiAffinityConstraintType.LOW.toString(), rule.getAntiAffinityConstraints() );

        rule.setAntiAffinityConstraints( XmlBeanAntiAffinityConstraintType.HIGH.toString() );
        assertEquals( "High", rule.getAntiAffinityConstraints() );
    }

}
