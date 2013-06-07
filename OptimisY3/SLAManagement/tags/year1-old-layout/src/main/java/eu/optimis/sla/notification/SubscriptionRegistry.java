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
package eu.optimis.sla.notification;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import eu.optimis.sla.notification.impl.SubscriptionImpl;

/**
 * @author owaeld
 * 
 */
public class SubscriptionRegistry implements Map<String, SubscriptionImpl>
{
    private Map<String, SubscriptionImpl> subscriptions = new HashMap<String, SubscriptionImpl>();

    private static SubscriptionRegistry instance;

    public static SubscriptionRegistry getInstance()
    {
        if ( instance == null )
        {
            synchronized ( SubscriptionRegistry.class )
            {
                if ( instance == null )
                {
                    instance = new SubscriptionRegistry();
                }
            }
        }
        return instance;
    }
    
    /**
     * allow only singleton instance
     */
    private SubscriptionRegistry() {
        
    }

    /**
     * @return
     * @see java.util.Map#size()
     */
    public int size()
    {
        return subscriptions.size();
    }

    /**
     * @return
     * @see java.util.Map#isEmpty()
     */
    public boolean isEmpty()
    {
        return subscriptions.isEmpty();
    }

    /**
     * @param key
     * @return
     * @see java.util.Map#containsKey(java.lang.Object)
     */
    public boolean containsKey( Object key )
    {
        return subscriptions.containsKey( key );
    }

    /**
     * @param value
     * @return
     * @see java.util.Map#containsValue(java.lang.Object)
     */
    public boolean containsValue( Object value )
    {
        return subscriptions.containsValue( value );
    }

    /**
     * @param key
     * @return
     * @see java.util.Map#get(java.lang.Object)
     */
    public SubscriptionImpl get( Object key )
    {
        return subscriptions.get( key );
    }

    public SubscriptionImpl put( SubscriptionImpl value )
    {
        return subscriptions.put( value.getId(), value );
    }
    
    /**
     * @param key
     * @param value
     * @return
     * @see java.util.Map#put(java.lang.Object, java.lang.Object)
     */
    public SubscriptionImpl put( String key, SubscriptionImpl value )
    {
        return subscriptions.put( key, value );
    }

    /**
     * @param key
     * @return
     * @see java.util.Map#remove(java.lang.Object)
     */
    public SubscriptionImpl remove( Object key )
    {
        return subscriptions.remove( key );
    }

    /**
     * @param t
     * @see java.util.Map#putAll(java.util.Map)
     */
    public void putAll( Map<? extends String, ? extends SubscriptionImpl> t )
    {
        subscriptions.putAll( t );
    }

    /**
     * 
     * @see java.util.Map#clear()
     */
    public void clear()
    {
        subscriptions.clear();
    }

    /**
     * @return
     * @see java.util.Map#keySet()
     */
    public Set<String> keySet()
    {
        return subscriptions.keySet();
    }

    /**
     * @return
     * @see java.util.Map#values()
     */
    public Collection<SubscriptionImpl> values()
    {
        return subscriptions.values();
    }

    /**
     * @return
     * @see java.util.Map#entrySet()
     */
    public Set<java.util.Map.Entry<String, SubscriptionImpl>> entrySet()
    {
        return subscriptions.entrySet();
    }

    /**
     * @param o
     * @return
     * @see java.util.Map#equals(java.lang.Object)
     */
    public boolean equals( Object o )
    {
        return subscriptions.equals( o );
    }

    /**
     * @return
     * @see java.util.Map#hashCode()
     */
    public int hashCode()
    {
        return subscriptions.hashCode();
    }

}
