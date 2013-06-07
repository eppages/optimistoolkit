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

import com.sun.jersey.core.spi.factory.ResponseBuilderImpl;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
//@smendoza commented cxf.jaxrs.ResponseBuilderImpl, because it requires the jersey one
//import org.apache.cxf.jaxrs.impl.ResponseBuilderImpl;

/**
 * Class
 * <code>DRPOneException</code> An exception specially built to get the error
 * condition back to the client.
 *
 * @author <a href="mailto:saint@eng.it">Gian Uberto Lauri</a>
 * @version $Revision$
 */
public class DRPOneException extends WebApplicationException {

    /*
     * WebApplicationException does not provide signatures like those
     * of the common Java exceptions, therefore I use this inner class
     * rather than a raw exception... 
     */
    private static class Problem extends Exception {

        /**
         * I Hate warnings
         */
        private static final long serialVersionUID = 4857887826790469885L;

        Problem(String message, Throwable cause) {
            super(message, cause);
        }
    }
    /**
     * I hate warnings!
     */
    private static final long serialVersionUID = 7050899732693277740L;

    /**
     * Creates a new instance of
     * <code>DRPOneException</code> .
     *
     */
    public DRPOneException() {
        super();
    }

    /**
     * Creates a new instance of
     * <code>DRPOneException</code> .
     *
     * @param status an <code>int</code> HTTP status code to be returned to the
     * remote client
     */
    public DRPOneException(int status) {
        super(status);
    }

    /**
     * Creates a new instance of
     * <code>DRPOneException</code> .
     *
     * @param response a <code>Response</code> HTTP response to be returned to
     * the remote client
     */
    public DRPOneException(Response response) {
        super(response);
    }

    /**
     * Creates a new instance of
     * <code>DRPOneException</code> .
     *
     * @param status a <code>Status</code> the HTTP status to be returned to the
     * remote client
     */
    public DRPOneException(Status status) {
        super(status);
    }

    /* this is the magic to get the error back to the user */
    private static Response getResponseFrom(String message, int status) {
        ResponseBuilderImpl builder = new ResponseBuilderImpl();
        builder.status(status);
        StringBuilder buf = new StringBuilder("<error><code>Error :");
        String href = "http://askapache.com/htaccess/apache-status-code-headers-errordocument.html#10.";
        buf.append(status);
        buf.append("</code>\n<message>" + status + "\n");
        buf.append(message);
        if ((status >= 400 && status <= 426) || (status >= 500 && status <= 510) || (status >= 300 && status <= 307)) {
            buf.append(".\n For more details,"
                    + " you can visit official "
                    + " home pages of HTTP Status Codes "
                    + " and Error Documents: \n");
            switch (status) {
                case 460:
                    href = href + "4.1\n";
                    break;
                case 401:
                    href = href + "4.2\n";
                    break;
                case 403:
                    href = href + "4.4 \n";
                    break;
                case 405:
                    href = href + "4.6 \n";
                    break;
                case 406:
                    href = href + "4.7\n";
                    break;
                case 407:
                    href = href + "4.8 \n";
                    break;
                case 415:
                    href = href + "4.16 \n";
                    break;
                case 500:
                    href = href + "5.1 \n";
                    break;
                case 501:
                    href = href + "5.2 \n";
                    break;
                case 502:
                    href = href + "5.3 \n";
                    break;
                case 503:
                    href = href + "5.4 \n";
                    break;
                case 505:
                    href = href + "5.6 \n";
                    break;
                default:
                    href = href + "\n ";
                    break;

            }
            buf.append(href);
        } else {
            buf.append(".\n You can try to search in \n"
                    + "http://askapache.com/htaccess/apache-status-code-headers-errordocument.html");
        }


        buf.append("</message></error>");
        builder.entity(buf.toString());
        return builder.build();

    }

    /**
     * Creates a new instance of
     * <code>DRPOneException</code> .
     *
     * @param cause a <code>Throwable</code> exception causing the trouble
     * @param status an <code>int</code> the HTTP status code to be returned to
     * the remote client
     */
    public DRPOneException(Throwable cause, int status) {

        super(getResponseFrom(cause.getMessage(), status));
    }

    /**
     * Creates a new instance of
     * <code>DRPOneException</code>. This is to mimick standard Java Exceptions.
     *
     * @param message a <code>String</code> exception message
     * @param status an <code>int</code> the HTTP status code to be returned to
     * the remote client
     */
    public DRPOneException(String message, int status) {
        super(getResponseFrom(message, status));
    }

    /**
     * Creates a new instance of
     * <code>DRPOneException</code> .
     *
     * @param cause a <code>Throwable</code> exception causing the trouble
     * @param status a <code>Status</code> the HTTP status to be returned to the
     * remote client
     */
    public DRPOneException(Throwable cause, Status status) {
        super(getResponseFrom(cause.getMessage(), status.getStatusCode()));
    }

    /**
     * Creates a new instance of
     * <code>DRPOneException</code> .
     *
     * @param message a <code>String</code> as used in java.lang.Exception
     * @param cause a <code>Throwable</code> as used in java.lang.Exception
     * @param Status an <code>int</code> HTTP Status
     */
    public DRPOneException(String message, Throwable cause, int status) {
        super(getResponseFrom(new Problem(message, cause).getMessage(), status));
    }
}
