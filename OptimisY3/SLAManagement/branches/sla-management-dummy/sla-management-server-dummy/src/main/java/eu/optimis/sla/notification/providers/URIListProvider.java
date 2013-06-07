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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;

/**
 * @author owaeld
 * 
 */
@Provider
public class URIListProvider
    implements MessageBodyWriter<List<URI>>, MessageBodyReader<List<URI>>
{

    /*
     * (non-Javadoc)
     * 
     * @see javax.ws.rs.ext.MessageBodyReader#isReadable(java.lang.Class, java.lang.reflect.Type,
     * java.lang.annotation.Annotation[], javax.ws.rs.core.MediaType)
     */
    @Override
    public boolean
        isReadable( Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType )
    {
        if ( genericType instanceof ParameterizedType )
        {
            ParameterizedType parameterized = (ParameterizedType) genericType;
            Type[] typeArgs = parameterized.getActualTypeArguments();
            if ( parameterized.getRawType() == List.class && typeArgs.length == 1 && typeArgs[0] == URI.class )
            {
                //
                // make sure that the content type is an uri list
                //
                if ( mediaType.isCompatible( new MediaType( "text", "uri-list" ) ) )
                {
                    return true;
                }
            }
        }
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.ws.rs.ext.MessageBodyReader#readFrom(java.lang.Class, java.lang.reflect.Type,
     * java.lang.annotation.Annotation[], javax.ws.rs.core.MediaType, javax.ws.rs.core.MultivaluedMap,
     * java.io.InputStream)
     */
    @Override
    public List<URI> readFrom( Class<List<URI>> type, Type genericType, Annotation[] annotations,
                               MediaType mediaType, MultivaluedMap<String, String> httpHeaders,
                               InputStream entityStream ) throws IOException
    {
        BufferedReader reader = new BufferedReader( new InputStreamReader( entityStream ) );
        Vector<URI> input = new Vector<URI>();

        String url;
        while ( ( url = reader.readLine() ) != null )
        {
            try
            {
                input.add( new URI( url ) );
            }
            catch ( URISyntaxException e )
            {
                new WebApplicationException( e );
            }
        }

        return input;
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.ws.rs.ext.MessageBodyWriter#isWriteable(java.lang.Class, java.lang.reflect.Type,
     * java.lang.annotation.Annotation[], javax.ws.rs.core.MediaType)
     */
    @Override
    public boolean
        isWriteable( Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType )
    {
        if ( genericType instanceof ParameterizedType )
        {
            ParameterizedType parameterized = (ParameterizedType) genericType;
            Type[] typeArgs = parameterized.getActualTypeArguments();
            if ( parameterized.getRawType() == List.class && typeArgs.length == 1 && typeArgs[0] == URI.class )
            {
                //
                // make sure that the content type is an uri list
                //
                if ( mediaType.isCompatible( new MediaType( "text", "uri-list" ) ) )
                {
                    return true;
                }
            }
        }
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.ws.rs.ext.MessageBodyWriter#getSize(java.lang.Object, java.lang.Class,
     * java.lang.reflect.Type, java.lang.annotation.Annotation[], javax.ws.rs.core.MediaType)
     */
    @Override
    public long getSize( List<URI> t, Class<?> type, Type genericType, Annotation[] annotations,
                         MediaType mediaType )
    {
        return toString( t ).getBytes().length;
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.ws.rs.ext.MessageBodyWriter#writeTo(java.lang.Object, java.lang.Class,
     * java.lang.reflect.Type, java.lang.annotation.Annotation[], javax.ws.rs.core.MediaType,
     * javax.ws.rs.core.MultivaluedMap, java.io.OutputStream)
     */
    @Override
    public void writeTo( List<URI> t, Class<?> type, Type genericType, Annotation[] annotations,
                         MediaType mediaType, MultivaluedMap<String, Object> httpHeaders,
                         OutputStream entityStream ) throws IOException
    {
        entityStream.write( toString( t ).getBytes() );
    }

    private String toString( List<URI> t )
    {
        StringBuffer result = new StringBuffer();

        Iterator<URI> iterator = t.iterator();
        while ( iterator.hasNext() )
        {
            result.append( iterator.next().toASCIIString() );
            result.append( "\n" );

            // if ( iterator.hasNext() )
            // {
            // result.append( "\n" );
            // }
        }
        return result.toString();
    }
}
