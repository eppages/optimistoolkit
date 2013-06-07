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

import eu.optimis.manifest.api.sp.LegalItemSection;
import eu.optimis.manifest.api.sp.SCC;
import eu.optimis.types.xmlbeans.servicemanifest.XmlBeanSCCType;
import eu.optimis.types.xmlbeans.servicemanifest.XmlBeanSectionType;

/**
 * @author hrasheed
 */
public class SCCImpl extends AbstractManifestElement<XmlBeanSCCType>
    implements SCC
{

    public SCCImpl( XmlBeanSCCType base )
    {
        super( base );
    }
    
    @Override
    public boolean isSCCEnabled()
    {
    	return delegate.getApply();
    }
    
    @Override
    public void enableSCC()
    {
    	delegate.setApply( true );
    }
    
    @Override
    public void disableSCC()
    {
    	delegate.setApply( false );
    }
    
    @Override
	public String getLocation() 
	{
		return delegate.getLocation();
	}

	@Override
	public void setLocation( String location ) 
	{
		delegate.setLocation( location );
	}

    @Override
    public String getDescription()
    {
        return delegate.getDescription();
    }

    @Override
    public void setDescription( String desc )
    {
        delegate.setDescription( desc );
    }

    @Override
    public LegalItemSection addStandardContractualClause()
    {
        XmlBeanSectionType sectionType = delegate.addNewClause();
        return new LegalItemSectionImpl(sectionType);
    }
    
    @Override
    public void removeStandardContractualClause( int index )
    {
    	if( delegate.sizeOfClauseArray() > 0 ) 
		{
			delegate.removeClause( index );
		}
    }
    
    @Override
    public LegalItemSection getStandardContractualClause(int index) 
    {
    	if( delegate.sizeOfClauseArray() > 0 ) 
		{
			return new LegalItemSectionImpl( delegate.getClauseArray( index ) );
		}
    	return null;
    }
    
    @Override
    public LegalItemSection[] getStandardContractualClauses() 
    {
    	if( delegate.sizeOfClauseArray() > 0 ) 
		{
			Vector<LegalItemSection> vector = new Vector<LegalItemSection>();
	        for ( XmlBeanSectionType type : delegate.getClauseArray() )
	        {
	            vector.add( new LegalItemSectionImpl( type ) );
	        }
	        return vector.toArray( new LegalItemSectionImpl[vector.size()] );
		}
		return new LegalItemSectionImpl[]{};
    }
}