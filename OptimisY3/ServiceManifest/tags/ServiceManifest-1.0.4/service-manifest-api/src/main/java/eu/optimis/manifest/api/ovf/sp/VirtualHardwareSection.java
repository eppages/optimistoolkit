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

package eu.optimis.manifest.api.ovf.sp;

import org.apache.xmlbeans.XmlObject;

/**
 * @author arumpl
 */
public interface VirtualHardwareSection {

    /**
     * @return  the info
     */
    String getInfo();

    /**
     * @return the system section
     */
    System getSystem();

    /**
     * @return the item array of the hardware section
     */
    Item[] getItemArray();

    /**
     * @param i the index
     * @return the item at position i of the hardware section
     */
    Item getItemArray(int i);

    /**
     * OPTIMIS specific: The virtual hardware family is set in  the  VirtualSystemType of the System Element
     * @return String virtualHardwareFamily
     */
    String getVirtualHardwareFamily();

    /**
     * OPTIMIS specific: The number of virtual CPUs is set in the first Item of the ItemArray
     * @return
     */
    int getNumberOfVirtualCPUs();

    /**
     * OPTIMIS specific: The memory size is set in the second Item of the ItemArray
     * @return the memory size
     */
    int getMemorySize();

    /**
     * OPTIMIS specific: The virtual hardware family is set in  the  VirtualSystemType of the System Element
     *
     * @param family
     */
    void setVirtualHardwareFamily(String family);


    /**
     * OPTIMIS specific: The number of virtual CPUs is set in the first Item of the ItemArray
     * @return
     */
    void setNumberOfVirtualCPUs(int numberOfVirtualCPUs);


    /**
     * OPTIMIS specific: The memory size is set in the second Item of the ItemArray
     * @param memorySize the memorySize
     */
    void setMemorySize(int memorySize);

}
