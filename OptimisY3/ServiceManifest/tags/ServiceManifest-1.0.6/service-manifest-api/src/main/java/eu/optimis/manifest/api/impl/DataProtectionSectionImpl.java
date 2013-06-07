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

import eu.optimis.manifest.api.sp.DataProtectionSection;
import eu.optimis.types.xmlbeans.servicemanifest.XmlBeanDataProtectionLevelType;
import eu.optimis.types.xmlbeans.servicemanifest.XmlBeanDataProtectionSectionType;

/**
 * @author owaeld
 */
class DataProtectionSectionImpl extends AbstractManifestElement<XmlBeanDataProtectionSectionType>
    implements DataProtectionSection, eu.optimis.manifest.api.ip.DataProtectionSection
{

    public DataProtectionSectionImpl( XmlBeanDataProtectionSectionType base )
    {
        super( base );
    }

    @Override
    public EncryptionLevelImpl getDataEncryptionLevel()
    {
        return new EncryptionLevelImpl( delegate.getDataEncryptionLevel() );
    }

    @Override
    public String getDataProtectionLevel()
    {
        return delegate.getDataProtectionLevel().toString();
    }

    @Override
    public void setDataProtectionLevel( String dataProtectionLevel )
    {
        delegate.setDataProtectionLevel( XmlBeanDataProtectionLevelType.Enum.forString( dataProtectionLevel ) );
    }

    @Override
    public String[] getEligibleCountryList()
    {
        if ( delegate.getEligibleCountryList() == null )
        {
            return null;
        }

        return delegate.getEligibleCountryList().getCountryArray();
    }

    @Override
    public String[] getNonEligibleCountryList()
    {
        return delegate.getNonEligibleCountryList().getCountryArray();
    }

    @Override
    public void addNewEligibleCountry( String country )
    {
        if ( !delegate.isSetEligibleCountryList() )
        {
            delegate.addNewEligibleCountryList();
        }
        delegate.getEligibleCountryList().addCountry( country );
    }

    @Override
    public void removeEligibleCountry( String country )
    {
        for ( int i = 0; i < delegate.getEligibleCountryList().sizeOfCountryArray(); i++ )
        {
            if ( delegate.getEligibleCountryList().getCountryArray( i ).equals( country ) )
            {
                delegate.getEligibleCountryList().removeCountry( i );
            }
        }
    }

    @Override
    public void addNewNonEligibleCountry( String country )
    {
        if ( !delegate.isSetNonEligibleCountryList() )
        {
            delegate.addNewNonEligibleCountryList();
        }
        delegate.getNonEligibleCountryList().addCountry( country );
    }

    @Override
    public void removeNonEligibleCountry( String country )
    {
        for ( int i = 0; i < delegate.getNonEligibleCountryList().sizeOfCountryArray(); i++ )
        {
            if ( delegate.getNonEligibleCountryList().getCountryArray( i ).equals( country ) )
            {
                delegate.getNonEligibleCountryList().removeCountry( i );
            }
        }
    }

    @Override
    public DataStorageImpl getDataStorage()
    {
        return new DataStorageImpl( delegate.getDataStorage() );
    }
}
