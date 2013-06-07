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
package eu.optimis.sla.accounting;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import org.ogf.graap.wsag4j.types.engine.SLAMonitoringNotificationEventType;

/**
 * @author hrasheed
 * 
 */
public class NotificationEventRegistry
{
    
    private static final Logger LOG = Logger.getLogger( NotificationEventRegistry.class );
   
    private Map<String, NotificationEventStore> notificationEventMap = Collections.synchronizedMap(new HashMap<String, NotificationEventStore>());
    
    private static NotificationEventRegistry slaEventRegistryInstsnce = null;
    
    private NotificationEventRegistry()
    {
    }
    
    private static synchronized NotificationEventRegistry getInstance()
    {
        if ( slaEventRegistryInstsnce == null )
        {
            slaEventRegistryInstsnce = new NotificationEventRegistry();
        } 

        return slaEventRegistryInstsnce;
    }
    
    /**
     * Adds the SLA Notification event
     * 
     * @param notificationEvent
     *            the notification event
     */
    public static void addNotificationEvent( SLAMonitoringNotificationEventType notificationEvent )
    {
        
        String agreementID = notificationEvent.getAgreementId();
        
        if( getInstance().notificationEventMap.containsKey( agreementID ) )
        {
            NotificationEventStore eventSource = (NotificationEventStore) getInstance().notificationEventMap.get( agreementID );
            eventSource.addNotificationEvent( notificationEvent );
        } else
        {
            NotificationEventStore eventSource = new NotificationEventStore(agreementID);
            eventSource.addNotificationEvent( notificationEvent );
            getInstance().notificationEventMap.put( agreementID, eventSource );
        }
        
        
    }

    /**
     * @return the SLA Notification events
     */
    public static List<SLAMonitoringNotificationEventType> getNotificationEvents(String agreementID)
    {
        if( getInstance().notificationEventMap.containsKey( agreementID ) )
        {
            NotificationEventStore eventSource = (NotificationEventStore) getInstance().notificationEventMap.get( agreementID );
            return eventSource.getNotificationEvents();
        } else
            return null;
    }
    
    public static void clearnotificationEventMap()
    {
        getInstance().notificationEventMap.clear();
    }
    
    public static Map<String, NotificationEventStore> copy()
    {
        Map<String, NotificationEventStore> clonenotificationEventMap = new HashMap<String, NotificationEventStore>();
        
        clonenotificationEventMap.putAll( getInstance().notificationEventMap);
        
        return clonenotificationEventMap;
    }
    
    public static Map<String, NotificationEventStore> deepClone()
    {
        
        Map<String, NotificationEventStore> clonenotificationEventMap = Collections.synchronizedMap(new HashMap<String, NotificationEventStore>());
        
        Iterator<Map.Entry<String, NotificationEventStore>> iterator = getInstance().notificationEventMap.entrySet().iterator();
        
        while (iterator.hasNext()) {
            Map.Entry<String, NotificationEventStore> eventEntry = (Map.Entry<String, NotificationEventStore>) iterator.next();
            clonenotificationEventMap.put( eventEntry.getKey(), (NotificationEventStore) ((NotificationEventStore) eventEntry.getValue()).clone() );      
        }
        
        return clonenotificationEventMap; 
    }
}
