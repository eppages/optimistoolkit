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

public interface StatusCodes {

	//@author maria.digirolamo@eng.it - June 2011
	//@version $Revision$
 

	/*manage geral error code*/
	static final int NOT_FOUND = 404;
	static final int BAD_REQUEST  = 401;
	static final int NET_IP_EXIST   = 405;
	//404 client error , the server has not found anything matching the request URI
	static final int UNAUTHORIZED = 404;
	//An appropriate representation of the requested 
	//resource /e/406 could not be found on this server.
	static final int NOT_ACCEPTABLE = 406;
	static final int NET_NAME_EXIST = 407;

	// TODO: revise these with Mario Macías
	static final int XML_PROBLEM = 415;


	static final int INTERNAL_ERROR_PARSE = 420;
	static final int GET_DOMAIN_ERROR = 420;
	static final int FAILED_DEPENDECIES = 424;
	static final int NETWORK_CONFIG_ERROR = 425;
	static final int NOT_RESOURCES = 425;
	static final int NAME_EXIST = 426;
	static final int VM_NOT_EXIST = 427;
	static final int NOT_AVAILABLE_NODES = 428;
	static final int CANNOT_ADDRESS = 432;
	static final int DOMAIN_NAME_EXIST = 433;
	static final int MISSING_USER = 434;
	static final int VM_NOT_READY = 436;
	static final int VM_NOT_RESPONDING = 437;

	static final int BAD_OVF = 460;
	static final int INTERNAL = 500;
	// Error status code 
	static final int ONE_FAILURE = 510;
	static final int CLIENTPROVIDER_FAILURE = 511;
	static final int NOT_IMPLEMENTED = 599;


    /*    START
    From madigiro to kanchanna
    Creato un insieme di codice errori per il network
    manage error code for network*/
    static final int BRIDGE_NOT_EXIST = 426;
    static final int NO_STARTING_ERROR = 427;
    static final int STARTING_NETWORK = 428;
    static final int IP_BAD = 432;
    static final int REFRESH_NET_INFO = 432;
    static final int ERROR_GET_NET_FULL_XML = 433;
    static final int ERROR_GET_NET_XML = 434;
    /*   END

     */

}
