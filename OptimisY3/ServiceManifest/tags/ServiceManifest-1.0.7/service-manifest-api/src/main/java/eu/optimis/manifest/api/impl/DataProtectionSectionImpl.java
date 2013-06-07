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

import java.util.Vector;

import eu.optimis.manifest.api.sp.BCR;
import eu.optimis.manifest.api.sp.DataProtectionSection;
import eu.optimis.manifest.api.sp.DataStorage;
import eu.optimis.manifest.api.sp.IPR;
import eu.optimis.manifest.api.sp.SCC;
import eu.optimis.types.xmlbeans.servicemanifest.XmlBeanBCRType;
import eu.optimis.types.xmlbeans.servicemanifest.XmlBeanDataProtectionLevelType;
import eu.optimis.types.xmlbeans.servicemanifest.XmlBeanDataProtectionSectionType;
import eu.optimis.types.xmlbeans.servicemanifest.XmlBeanDataStorageType;
import eu.optimis.types.xmlbeans.servicemanifest.XmlBeanIPRType;
import eu.optimis.types.xmlbeans.servicemanifest.XmlBeanSCCType;

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
    public SCC getSCC()
    {
    	if ( delegate.isSetSCC() )
        {
    		XmlBeanSCCType type = delegate.getSCC();
    		return new SCCImpl( type );
        } else
        {
        	XmlBeanSCCType type = delegate.addNewSCC();
        	return new SCCImpl( type );
        }
    }
    
    @Override
    public BCR getBCR()
    {
    	if ( delegate.isSetBCR() )
        {
    		XmlBeanBCRType type = delegate.getBCR();
    		return new BCRImpl( type );		
        } else
        {
        	XmlBeanBCRType type = delegate.addNewBCR();
        	return new BCRImpl( type );
        }
    }
    
    @Override
    public IPR getIPR() 
    {
    	if ( delegate.isSetIPR() )
        {
    		XmlBeanIPRType type = delegate.getIPR();
    		return new IPRImpl( type );
        } else
        {
        	XmlBeanIPRType type = delegate.addNewIPR();
        	return new IPRImpl( type );
        }
    }
    
    @Override
    public DataStorage addNewDataStorage( String componentId, String storageName ) 
    {
    	TemplateLoader loader = new TemplateLoader();
    	XmlBeanDataStorageType type = delegate.addNewDataStorage();
    	type.set( loader.loadDataStorageTemplate( new String[] {componentId}, storageName ) );
        return new DataStorageImpl( type );
    }
    
    @Override
    public DataStorage addNewDataStorage( String[] componentIdList, String storageName ) 
    {
    	TemplateLoader loader = new TemplateLoader();
    	XmlBeanDataStorageType type = delegate.addNewDataStorage();
        type.set( loader.loadDataStorageTemplate( componentIdList, storageName ) );
        return new DataStorageImpl( type );
    }
   
    @Override
    public DataStorageImpl[] getDataStorageArray() 
    {
    	if( delegate.sizeOfDataStorageArray() > 0 ) 
		{
			Vector<DataStorageImpl> vector = new Vector<DataStorageImpl>();
	        for ( XmlBeanDataStorageType type : delegate.getDataStorageArray() )
	        {
	            vector.add( new DataStorageImpl( type ) );
	        }
	        return vector.toArray( new DataStorageImpl[vector.size()] );
		}
		return new DataStorageImpl[]{};
    }
    
    @Override
    public DataStorageImpl getDataStorageArray( int i )
    {
    	if( delegate.sizeOfDataStorageArray() > 0 ) 
		{
			return new DataStorageImpl( delegate.getDataStorageArray( i ) );
		}
    	return null;
    }
    
    @Override
    public DataStorageImpl getDataStorage( String storageName )
    {
    	if( delegate.sizeOfDataStorageArray() > 0 ) 
		{
			for ( XmlBeanDataStorageType type : delegate.getDataStorageArray() )
	        {
	            if( type.getName().equals( storageName ) )
	            {
	            	return new DataStorageImpl( type );
	            }
	        }
		} 
		return null;
    }
    
    @Override
    public void removeDataStorage( int i )
    {
    	if( delegate.sizeOfDataStorageArray() > 0 ) 
		{
    		delegate.removeDataStorage( i );
		}
    }
    
    @Override
    public void removeDataStorage( String name )
    {
    	if( delegate.sizeOfDataStorageArray() > 0 ) 
		{
    		for ( int i = 0; i < delegate.getDataStorageArray().length; i++ )
	        {
	            if ( delegate.getDataStorageArray( i ).getName().equals( name ) )
	            {
	            	delegate.removeDataStorage( i );
	            }
	        }
		} 
    }

}
