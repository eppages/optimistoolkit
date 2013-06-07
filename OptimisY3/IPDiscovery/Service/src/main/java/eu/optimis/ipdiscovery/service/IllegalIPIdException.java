package eu.optimis.ipdiscovery.service;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

/**
 * REST exception for illegal IP-ID parameter
 * @author Daniel Espling
 *
 */
public class IllegalIPIdException extends WebApplicationException {

    private static final long serialVersionUID = 1124141L;

    /**
     * Create a HTTP 400 (Bad request) exception with a generic error
     */
    public IllegalIPIdException() {
        this("Illegal IP-ID parameter");
    }

    /**
     * Create a HTTP 400 (Bad request) exception with a specific fault
     */
    public IllegalIPIdException(String message) {
        super(Response.status(Status.BAD_REQUEST).entity(message).type("text/plain").build());
    }
}
