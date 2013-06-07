package eu.optimis.utils.optimislogger;

import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

/**
 * Client for storing status updates to the SDO GUI
 * 
 * @author Daniel Henriksson (<a
 *         href="mailto:danielh@cs.umu.se">danielh@cs.umu.se</a>)
 * 
 */
public class StatusClient {

    private static Logger log = Logger.getLogger(StatusClient.class);
    private String uri;
    private Client client;

    /**
     * Creates a new client
     * 
     * @param uri
     *            The uri of the GUI

     */
    public StatusClient(String uri) {
        log.info("StatusClient created with URI: " + uri);
        this.uri = uri;
        this.client = Client.create();
    }



    /**
     * Store a new status update for a given serviceID
     * 
     * @param serviceID
     *            The affected service
     * @param status
     *            The new status update
     * @return
     */
    public boolean storeStatus(String serviceID, Status status) {
        String appendedURI = uri + serviceID;
        WebResource service = client.resource(appendedURI);
        log.info("Sending status object:" + status);
        ClientResponse response = service.type("application/xml").post(ClientResponse.class, status);
        log.info("Result of status call is: " + response);
        return response.getClientResponseStatus().getFamily().equals(Response.Status.Family.SUCCESSFUL);
    }

    /*
     * Used for testing
     */
    public static void main(String[] args) {
        StatusClient client = new StatusClient("http://localhost:8087/status/");

        // Send normal update
        System.out.println("Sending non-progress update");
        Status status = new Status("Context", "General failuree56 r", true);
        boolean success = client.storeStatus("someServiceID", status);
        System.out.println("Success = " + success);

        System.out.println("\nSending progress update");
        status = new Status("SDO (test)", "Skitsystem", -1, true);
        success = client.storeStatus("someServiceID", status);
        System.out.println("Success = " + success);
    }

}
