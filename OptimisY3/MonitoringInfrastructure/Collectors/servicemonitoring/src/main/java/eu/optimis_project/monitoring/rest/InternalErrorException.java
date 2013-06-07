/**
 * 
 * @author Daniel Espling (<a href="mailto:espling@cs.umu.se">espling@cs.umu.se</a>)
 * Copyright (C) 2011 Umea University
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package eu.optimis_project.monitoring.rest;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

public class InternalErrorException extends WebApplicationException {

    private static final long serialVersionUID = 1124141L;

    /**
     * Create a HTTP 400 (Bad request) exception with a generic error TODO
     * update javadoc
     */
    public InternalErrorException() {
        this("Internal server error");
    }

    /**
     * Create a HTTP 400 (Bad request) exception with a specific fault TODO
     * update javadoc
     */
    public InternalErrorException(String message) {
        super(Response.status(Status.INTERNAL_SERVER_ERROR).entity(message).type("text/plain").build());
    }
}
