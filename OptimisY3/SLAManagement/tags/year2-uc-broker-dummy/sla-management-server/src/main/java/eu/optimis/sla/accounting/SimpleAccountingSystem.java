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

import org.apache.log4j.Logger;
import org.apache.xmlbeans.XmlOptions;
import org.ogf.graap.wsag.api.logging.LogMessage;
import org.ogf.graap.wsag.server.accounting.SimpleAccountingSystemLogger;
import org.ogf.graap.wsag4j.types.engine.SLAMonitoringNotificationEventType;

/**
 * @author hrasheed
 * 
 */
public class SimpleAccountingSystem extends SimpleAccountingSystemLogger
{

    private static final Logger LOG = Logger.getLogger( SimpleAccountingSystem.class );

    /**
     * Creates a new accounting system that stores guarantee results (evaluated after each monitoring cycle)
     * and is accessible by a notification service to push this data to subscribers.
     */
    public SimpleAccountingSystem()
    {
        super();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.ogf.graap.wsag.server.accounting.SimpleAccountingSystemLogger#issueCompensation(org.ogf.schemas.graap.wsAgreement.CompensationType,
     *      org.ogf.graap.wsag.server.accounting.IAccountingContext)
     */
    @Override
    public void issueCompensation( SLAMonitoringNotificationEventType notificationEvent )
    {
        super.issueCompensation( notificationEvent );

        if ( LOG.isTraceEnabled() )
        {
            LOG.trace( ( LogMessage.getMessage( "SLA evaluation result:\n{0}",
                notificationEvent.xmlText( new XmlOptions().setSavePrettyPrint() ) ) ) );
        }

        NotificationEventRegistry.addNotificationEvent( notificationEvent );
    }
}
