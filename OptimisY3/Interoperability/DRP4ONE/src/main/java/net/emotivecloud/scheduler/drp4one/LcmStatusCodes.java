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
package net.emotivecloud.scheduler.drp4one;

/**
 * OpenNebula Lifecycle status codes. Not an enum since we need this 
 * just for documentation npurpose
 *
 *
 * Created: Wed Oct  5 19:55:08 2011
 *
 * @author <a href="mailto:saint@eng.it">Gian Uberto Lauri</a>
 * @version $Revision$
 */
public interface LcmStatusCodes {

	int LCM_INIT = 0;
	int PROLOG = 1;
	int BOOT = 2;
	int RUNNING = 3;
	int MIGRATE = 4;
	int SAVE_STOP = 5;
	int SAVE_SUSPEND = 6;
	int SAVE_MIGRATE = 7;
	int PROLOG_MIGRATE = 8;
	int PROLOG_RESUME = 9;
	int EPILOG_STOP = 10;
	int EPILOG = 11;
	int SHUTDOWN = 12;
	int CANCEL = 13;
	int FAILURE = 14;
	int CLEANUP = 15;
	int UNKNOWN = 16;

}
