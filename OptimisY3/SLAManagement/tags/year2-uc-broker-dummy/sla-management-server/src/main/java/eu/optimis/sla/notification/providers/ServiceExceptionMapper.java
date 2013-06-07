/* 
 * Copyright (c) 2011, Fraunhofer-Gesellschaft
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
package eu.optimis.sla.notification.providers;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

import org.apache.cxf.jaxrs.client.ResponseExceptionMapper;

import eu.optimis.sla.notification.exceptions.AbstractException;
import eu.optimis.sla.notification.exceptions.ResourceNotFoundException;
import eu.optimis.sla.notification.exceptions.UnknownErrorException;

/**
 * @author owaeld
 * 
 */
public class ServiceExceptionMapper
    implements ExceptionMapper<AbstractException>, ResponseExceptionMapper<AbstractException>
{

    /**
     * {@inheritDoc}
     * 
     * @see javax.ws.rs.ext.ExceptionMapper#toResponse(java.lang.Throwable)
     */
    @Override
    public Response toResponse( AbstractException exception )
    {
        Response response =
            Response.status( exception.getErrorCode() ).entity( exception.getMessage() ).build();
        response.getMetadata().add( AbstractException.META_EC_HEADER, exception.getErrorCode() );
        response.getMetadata().add( AbstractException.META_EM_HEADER, exception.getMessage() );
        return response;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.apache.cxf.jaxrs.client.ResponseExceptionMapper#fromResponse(javax.ws.rs.core.Response)
     */
    @Override
    public AbstractException fromResponse( Response r )
    {
        //
        // if a specific error code exists return the associated exception
        //
        String errorCode = (String) r.getMetadata().getFirst( AbstractException.META_EC_HEADER );
        if ( errorCode != null )
        {
            //
            // parse the error code and instantiate the correct exception
            //
            String errorMessage = (String) r.getMetadata().getFirst( AbstractException.META_EM_HEADER );
            try
            {
                int code = Integer.parseInt( errorCode );
                switch ( code )
                {
                    case ResourceNotFoundException.ERROR_CODE:
                        return new ResourceNotFoundException( errorMessage );

                    default:
                        //
                        // return an unknown error as fallback
                        //
                        return new UnknownErrorException( errorMessage, errorCode );
                }
            }
            catch ( NumberFormatException e )
            {
                //
                // return an unknown error in case of a wrong error code
                //
                return new UnknownErrorException( errorMessage, errorCode );
            }
        }
        else
        {
            throw new WebApplicationException( r );
        }
    }
}
