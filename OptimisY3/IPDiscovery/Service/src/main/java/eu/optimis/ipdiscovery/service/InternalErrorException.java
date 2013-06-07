package eu.optimis.ipdiscovery.service;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

/**
 * Class for internal errors due to REST based calls
 * @author Daniel Espling
 *
 */
public class InternalErrorException extends WebApplicationException {

    private static final long serialVersionUID = 1124141L;

    /**
     * Create a HTTP 400 (Bad request) exception with a generic error 
     * update javadoc
     */
    public InternalErrorException() {
        this("Internal server error");
    }

    /**
     * Create a HTTP 400 (Bad request) exception with a specific fault
     * update javadoc
     */
    public InternalErrorException(String message) {
        super(Response.status(Status.INTERNAL_SERVER_ERROR).entity(message).type("text/plain").build());
    }
}
