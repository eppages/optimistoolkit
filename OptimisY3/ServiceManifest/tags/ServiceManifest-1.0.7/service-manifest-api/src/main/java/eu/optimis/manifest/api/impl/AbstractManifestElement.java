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

import java.util.ArrayList;
import java.util.List;

import org.apache.xmlbeans.XmlError;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlOptions;

/**
 * Abstract base class of XML types used in the service manifest API. Each XML type implementation must define
 * the type of the underlying XMLBean object. This type definition is used for the internal delegation object.
 * 
 * @param <T>
 *            type of the underlying XMLBean object
 * 
 * @author owaeld
 */
public abstract class AbstractManifestElement<T extends XmlObject>
{

    /**
     * internal delegate
     */
    // CHECKSTYLE:OFF - base type declaration, exception to the general rule by purpose
    public T delegate;

    // CHECKSTYLE:ON
    /**
     * Default constructor.
     * 
     * @param base
     *            the base type is used as internal delegation and data store object
     */
    public AbstractManifestElement( T base )
    {
        delegate = base;
    }

    /**
     * Returns the internal XML representation of the API object.
     * 
     * @return internal representation as a XMLBean
     */
    @SuppressWarnings( "unchecked" )
    public T getXmlObject()
    {
        return (T) delegate.copy();
    }

    /**
     * Returns the internal XML representation of the API object as a String.
     * 
     * @return internal representation as a String.
     */
    @Override
    public String toString()
    {
        XmlOptions options = new XmlOptions();
        options.setSavePrettyPrint();
        options.setSaveOuter();
        return delegate.xmlText( options );
    }

    /**
     * returns validation errors found in the xml document. The validation is done by the xmlbeans validate
     * method.
     * 
     * @return a list of errors
     */
    public List<XmlError> getErrors()
    {
        List<XmlError> validationErrors = new ArrayList<XmlError>();
        XmlOptions voptions = new XmlOptions();
        voptions.setErrorListener( validationErrors );
        delegate.validate( voptions );
        return validationErrors;
    }

    /**
     * returns true if the xml object is not valid.
     * 
     * @return true | false
     */
    public boolean hasErrors()
    {
        return !delegate.validate();
    }

}
