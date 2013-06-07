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

package eu.optimis.manifest.api.utils;

import org.dmtf.schemas.ovf.envelope.x1.MsgType;
import org.dmtf.schemas.wbem.wscim.x1.cimSchema.x2.cimResourceAllocationSettingData.ResourceTypeDocument;
import org.dmtf.schemas.wbem.wscim.x1.common.CimBoolean;
import org.dmtf.schemas.wbem.wscim.x1.common.CimString;
import org.dmtf.schemas.wbem.wscim.x1.common.CimUnsignedLong;

import java.math.BigInteger;

/**
 * @author arumpl
 */
public class XmlSimpleTypeConverter {

    public static CimString toCimString(String string) {
        CimString cimString = CimString.Factory.newInstance();
        cimString.setStringValue(string);
        return cimString;
    }

    public static CimUnsignedLong toCimUnsignedLong(long longValue) {
        CimUnsignedLong cimValue = CimUnsignedLong.Factory.newInstance();
        cimValue.setBigIntegerValue(BigInteger.valueOf(longValue));
        return cimValue;
    }

    public static ResourceTypeDocument.ResourceType toResourceType(int integer) {
        ResourceTypeDocument.ResourceType resourceTypeDoc = ResourceTypeDocument.ResourceType.Factory.newInstance();
        resourceTypeDoc.setIntValue(integer);
        return resourceTypeDoc;
    }

    public static MsgType toMsgType(String string) {
        MsgType msg = MsgType.Factory.newInstance();
        msg.setStringValue(string);
        return msg;
    }

    public static CimBoolean toCimBoolean(boolean booleanValue){
        CimBoolean cimBoolean = CimBoolean.Factory.newInstance();
        cimBoolean.setBooleanValue(booleanValue);
        return cimBoolean;
    }
}
