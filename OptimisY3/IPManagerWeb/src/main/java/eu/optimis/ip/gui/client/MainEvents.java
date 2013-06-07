/**
 * Copyright (C) 2010-2013 Barcelona Supercomputing Center
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version. This library is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser
 * General Public License for more details. You should have received a copy of
 * the GNU Lesser General Public License along with this library; if not, write
 * to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA
 * 02111-1307 USA
 */
package eu.optimis.ip.gui.client;

import com.extjs.gxt.ui.client.event.EventType;

public class MainEvents {

    public static final EventType Init = new EventType();
    public static final EventType Navigation = new EventType();
    public static final EventType ReportIterms = new EventType();
    public static final EventType ReportGraphicDiagram = new EventType();
    public static final EventType ReportGraphicCancel = new EventType();
    
    //IP Configuration
    public static final EventType IP_CONFIG = new EventType();
    
    //Other GUIs
    public static final EventType EMOTIVE = new EventType();
    public static final EventType TREC = new EventType();
    public static final EventType Monitoring = new EventType();
    public static final EventType DM = new EventType();
    
    //Admission Control 
    public static final EventType AC = new EventType();
    
    //Components log output 
    public static final EventType Output = new EventType();
    public static final EventType Error = new EventType();
    
    //CO
    public static final EventType CO = new EventType();
    
    //CONFIGURATION
    public static final EventType CONFIGURATION = new EventType();
    
    //IPRegistry
    public static final EventType IPRegistry = new EventType();
    
    //Authentication
    public static final EventType LOGIN = new EventType();
    public static final EventType LOGINSUCCESSFUL = new EventType();
    public static final EventType LOGOUT = new EventType();
    public static final EventType NEWACCOUNT = new EventType();
    public static final EventType NEWACCOUNTSUBMIT = new EventType();
    public static final EventType NEWACCOUNTERROR = new EventType();
    public static final EventType SKIPLOGIN = new EventType();
}
