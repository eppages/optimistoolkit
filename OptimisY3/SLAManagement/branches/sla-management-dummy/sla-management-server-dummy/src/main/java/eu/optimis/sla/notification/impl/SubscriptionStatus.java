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
package eu.optimis.sla.notification.impl;

import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.Response.Status.Family;
import javax.ws.rs.core.Response.StatusType;

/**
 * @author owaeld
 * 
 */
public enum SubscriptionStatus
    implements StatusType
{

    /**
     * 200 OK, see
     * {@link <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html#sec10.2.1">HTTP/1.1 documentation</a>}
     * .
     */
    OK( 200 ),
    /**
     * 201 Created, see
     * {@link <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html#sec10.2.2">HTTP/1.1 documentation</a>}
     * .
     */
    CREATED( 201 ),
    /**
     * 202 Accepted, see
     * {@link <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html#sec10.2.3">HTTP/1.1 documentation</a>}
     * .
     */
    ACCEPTED( 202 ),
    /**
     * 204 No Content, see
     * {@link <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html#sec10.2.5">HTTP/1.1 documentation</a>}
     * .
     */
    NO_CONTENT( 204 ),
    /**
     * 301 Moved Permanently, see
     * {@link <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html#sec10.3.2">HTTP/1.1 documentation</a>}
     * .
     */
    MOVED_PERMANENTLY( 301 ),
    /**
     * 303 See Other, see
     * {@link <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html#sec10.3.4">HTTP/1.1 documentation</a>}
     * .
     */
    SEE_OTHER( 303 ),
    /**
     * 304 Not Modified, see
     * {@link <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html#sec10.3.5">HTTP/1.1 documentation</a>}
     * .
     */
    NOT_MODIFIED( 304 ),
    /**
     * 307 Temporary Redirect, see
     * {@link <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html#sec10.3.8">HTTP/1.1 documentation</a>}
     * .
     */
    TEMPORARY_REDIRECT( 307 ),
    /**
     * 400 Bad Request, see
     * {@link <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html#sec10.4.1">HTTP/1.1 documentation</a>}
     * .
     */
    BAD_REQUEST( 400 ),
    /**
     * 401 Unauthorized, see
     * {@link <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html#sec10.4.2">HTTP/1.1 documentation</a>}
     * .
     */
    UNAUTHORIZED( 401 ),
    /**
     * 403 Forbidden, see
     * {@link <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html#sec10.4.4">HTTP/1.1 documentation</a>}
     * .
     */
    FORBIDDEN( 403 ),
    /**
     * 404 Not Found, see
     * {@link <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html#sec10.4.5">HTTP/1.1 documentation</a>}
     * .
     */
    NOT_FOUND( 404 ),
    /**
     * 406 Not Acceptable, see
     * {@link <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html#sec10.4.7">HTTP/1.1 documentation</a>}
     * .
     */
    NOT_ACCEPTABLE( 406 ),
    /**
     * 409 Conflict, see
     * {@link <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html#sec10.4.10">HTTP/1.1 documentation</a>}
     * .
     */
    CONFLICT( 409 ),
    /**
     * 410 Gone, see
     * {@link <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html#sec10.4.11">HTTP/1.1 documentation</a>}
     * .
     */
    GONE( 410 ),
    /**
     * 412 Precondition Failed, see
     * {@link <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html#sec10.4.13">HTTP/1.1 documentation</a>}
     * .
     */
    PRECONDITION_FAILED( 412 ),
    /**
     * 415 Unsupported Media Type, see
     * {@link <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html#sec10.4.16">HTTP/1.1 documentation</a>}
     * .
     */
    UNSUPPORTED_MEDIA_TYPE( 415 ),
    /**
     * 500 Internal Server Error, see
     * {@link <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html#sec10.5.1">HTTP/1.1 documentation</a>}
     * .
     */
    INTERNAL_SERVER_ERROR( 500 ),
    /**
     * 503 Service Unavailable, see
     * {@link <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html#sec10.5.4">HTTP/1.1 documentation</a>}
     * .
     */
    SERVICE_UNAVAILABLE( 503 );

    private Family family;

    private String reasonPhrase;

    private int statusCode;

    private SubscriptionStatus( int statusCode )
    {
        Status status = Status.fromStatusCode( statusCode );

        this.family = status.getFamily();
        this.reasonPhrase = status.getReasonPhrase();
        this.statusCode = status.getStatusCode();
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.ws.rs.core.Response.StatusType#getStatusCode()
     */
    public int getStatusCode()
    {
        return statusCode;
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.ws.rs.core.Response.StatusType#getFamily()
     */
    public Family getFamily()
    {
        return family;
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.ws.rs.core.Response.StatusType#getReasonPhrase()
     */
    public String getReasonPhrase()
    {
        return reasonPhrase;
    }

    /**
     * @param reasonPhrase
     *            the reasonPhrase to set
     */
    public void setReasonPhrase( String reasonPhrase )
    {
        this.reasonPhrase = ( reasonPhrase == null ) ? "" : reasonPhrase;
    }

}
