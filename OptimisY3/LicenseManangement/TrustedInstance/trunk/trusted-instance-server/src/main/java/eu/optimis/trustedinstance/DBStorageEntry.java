/* 
 * Copyright (c) 2007, Fraunhofer-Gesellschaft
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
package eu.optimis.trustedinstance;

import eu.elasticlm.schemas.x2009.x05.license.token.LicenseTokenDocument;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.impl.util.Base64;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 *
 * @author hrasheed
 * @author s.reiser
 */
@Entity
@Table(name="ENTRYS")
public class DBStorageEntry {

    @Id
    @Column(name="ID", nullable=false, unique=true, updatable=false)
    @Basic(optional=false)
    private String key;

    @Column(name="BASE64_RESOURCE", length=65535, nullable=false)
    @Basic(optional=false)
    private byte[] base64Resource ;

    public DBStorageEntry() {
    }

    public DBStorageEntry(String key, LicenseTokenDocument token) throws IOException {
        this.key = key;
        setLicenseToken(token);
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    private byte[] getBase64Resource() {
        return base64Resource;
    }

    private void setBase64Resource(byte[] base64Resource) {
        this.base64Resource = base64Resource;
    }

    public void setLicenseToken(LicenseTokenDocument token) throws IOException {
        ByteArrayOutputStream token_bos = new ByteArrayOutputStream();
        token.save(token_bos);
        byte[] token_encoded = Base64.encode(token_bos.toByteArray());
        setBase64Resource(token_encoded);
        token_bos.close();
    }

    public LicenseTokenDocument getLicenseToken() throws XmlException, IOException {
        byte[] decoded = Base64.decode(getBase64Resource());
        ByteArrayInputStream token_bis = new ByteArrayInputStream(decoded);
        LicenseTokenDocument token = LicenseTokenDocument.Factory.parse(token_bis);
        token_bis.close();
        return token;
    }
}