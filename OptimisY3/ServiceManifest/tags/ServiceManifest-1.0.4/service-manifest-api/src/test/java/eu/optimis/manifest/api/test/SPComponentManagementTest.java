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

/**
 * Created by IntelliJ IDEA.
 * Email: karl.catewicz@scai.fraunhofer.de
 * Date: 16.01.2012
 * Time: 17:36:49
 */

package eu.optimis.manifest.api.test;

import eu.optimis.manifest.api.sp.VirtualMachineComponent;
import eu.optimis.manifest.api.sp.VirtualMachineDescriptionSection;
import eu.optimis.types.xmlbeans.servicemanifest.XmlBeanAffinityConstraintType;

public class SPComponentManagementTest extends AbstractTestApi {

    private static String id = "ADD-COMPONENT-ID";

    public void testAddComponent() throws Exception {
        System.out.println("\ntestAddComponent invoked.");
        VirtualMachineDescriptionSection vm = manifest.getVirtualMachineDescriptionSection();
        int previous_length = vm.getVirtualMachineComponentArray().length;
        System.out.println("initial VM component number : "+previous_length);        
        vm.addNewVirtualMachineComponent(id);
        VirtualMachineComponent[] vm_array = vm.getVirtualMachineComponentArray();
        int final_length = vm_array.length;
        System.out.println("final VM component number : "+final_length);
        assertEquals(previous_length+1,final_length);
    }


    public void testRetrieveComponentById() throws Exception {
        System.out.println("\ntestRetrieveComponent invoked.");
        VirtualMachineDescriptionSection vm = manifest.getVirtualMachineDescriptionSection();
        vm.addNewVirtualMachineComponent(id);
        VirtualMachineComponent component = vm.getVirtualMachineComponentById("ADD-COMPONENT-ID");
        System.out.println("retrieved component id :"+component.getComponentId());
        assertEquals(id,component.getComponentId());

    }
    public void testRemoveComponent() throws Exception {
        System.out.println("\ntestRemoveComponent invoked.");
        VirtualMachineDescriptionSection vm = manifest.getVirtualMachineDescriptionSection();
        vm.addNewVirtualMachineComponent(id);
        int previous_length = vm.getVirtualMachineComponentArray().length;
        System.out.println("initial VM component number : "+previous_length);
        vm.removeVirtualMachineComponentById(id);
        VirtualMachineComponent[] vm_array = vm.getVirtualMachineComponentArray();
        int final_length = vm_array.length;
        System.out.println("final VM component number : "+final_length);
        assertEquals(previous_length,final_length+1);
    }
}
