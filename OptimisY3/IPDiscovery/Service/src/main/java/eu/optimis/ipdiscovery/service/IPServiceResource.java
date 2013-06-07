package eu.optimis.ipdiscovery.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;

import com.sun.jersey.api.client.ClientResponse.Status;
import com.sun.jersey.spi.resource.Singleton;

import eu.optimis.ipdiscovery.datamodel.Provider;
import eu.optimis.ipdiscovery.service.storage.InMemoryStorageManager;
import eu.optimis.ipdiscovery.service.storage.StorageManager;

/**
 * REST Resource for the IP Discovery service. This class provides the code resolving HTTP calls to different URLs.
 * @author Daniel Espling
 *
 */
@Path("/")
@Singleton
public class IPServiceResource {

    private static Logger log = Logger.getLogger(IPServiceResource.class);
    private final StorageManager storageManager;

    /**
     * Constructor, assigns an implementation of Storage interface.
     */
    public IPServiceResource() {
        storageManager = new InMemoryStorageManager();
    }


    /**
     * Insert by XML
     */
    @POST
    @Path("/ip/{ipId}")
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.APPLICATION_XML)
    public String insertXML(@PathParam("ipId") String ipId,
            Provider provider) {
        log.debug("SINGLE: POST XML, accept");
        return insertProvider(ipId, provider);

    }

    /**
     * Insert by JSon
     */
    @POST
    @Path("/ip/{ipId}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String insertJSON(@PathParam("ipId") String ipId, Provider provider) {
        log.debug("SINGLE: POST JSON, accept");
        return insertProvider(ipId, provider);
    }

    /**
     * Manage inserts of received Providers
     * 
     * @param ipdId
     *            The affected IP ID
     * @param provider
     *            Provider info
     * @return The resulting response code
     */
    private String insertProvider(String ipId, Provider provider) {
        assertIPId(ipId);
        assertProvider(provider);

        try {
            boolean result = storageManager.storeData(provider);
            //Convert to String to suit Jersey REST
            return result? "true": "false";
        } catch (IOException e) {
            log.warn("Failed to add provider for ipId: " + ipId);
            throw new InternalErrorException("Storage exception: " + e.getMessage());
        }
    }

    /**
     * Assert that the Provider info is correct
     * @param measurement
     */
    private void assertProvider(Provider provider) {
        if (provider == null) { 
            log.info("illegal provider: " + provider);
            throw new WebApplicationException(Response.status(Status.BAD_REQUEST)
                    .entity("Illegal Provicer info: " + provider).type("text/plain").build());
        } else {
        	//TODO: Add more Provider validation, checking required fields 
        }
    }

    /**
     * Assert that the IP Id is legal
     * @param ipId
     */
    private void assertIPId(String ipId) {
        if (ipId == null || ipId.length() < 1) {
            log.info("illegal IP-Id: " + ipId);
            throw new IllegalIPIdException();
        }
    }

    /**
     * Delete data for a specific IP
     * @param ipId The Id of the IP to remove
     */
    @DELETE
    @Path("/ip/{ipId}")
    public String remove(@PathParam("ipId") String ipId) {
        log.debug("SINGLE: DELETE, remove");
        assertIPId(ipId);
        System.out.println("Got ipId:" + ipId);

        log.info("Deleting data for service: " + ipId);
        try {
            boolean result = storageManager.removeData(ipId);
            log.info("Successfully deleted data for IP: " + ipId);
            //Convert to String to suit Jersey REST
            return result? "true": "false";
        } catch (IllegalArgumentException e) {
            log.info("Failed to remove data for IP: " + ipId);
            throw new InternalErrorException();
        } catch (IOException e) {
            log.warn("Failed to remove data for IP: " + ipId);
            throw new InternalErrorException();
        }
    }

    /**
     * Delete all data in storage
     */
    @DELETE
    @Path("/ip")
    @Produces(MediaType.APPLICATION_XML)
    public String removeAllData() {
        log.debug("Collections: DELETE, remove");
        log.info("Deleting all data");
        try {
            int numberRemoved = storageManager.removeAllData();
            return "" + numberRemoved; //Jersey only will return strings
        } catch (IOException e) {
            log.warn("Failed to delete data from StorageManager.", e);
            throw new InternalErrorException();
        }
    }

    /**
     * Read data for a single IP as XML
     */
    @GET
    @Path("/ip/{ipId}")
    @Produces(MediaType.APPLICATION_XML)
    public Provider getData(@PathParam("ipId") String ipId) {
        log.debug("SINGLE: GET, toXML");
        assertIPId(ipId);
        System.out.println("Got call with ipId: " + ipId);

        try {
            Provider provider = storageManager.getData(ipId);
            return provider;
        } catch (IOException e) {
            log.warn("Failed to read data for serviceID: " + ipId);
            throw new InternalErrorException();
        }
    }

    /**
     * Read all IPs as XML
     */
    @GET
    @Path("/ip")
    @Produces(MediaType.APPLICATION_XML)
    public List<Provider> getAllData() {
        log.debug("Collections: GET, toXML");
        Set<Provider> providers;

        try {
            providers = storageManager.getAllData();
        } catch (IOException e) {
            log.warn("Failed to read data from StorageManager.", e);
            throw new InternalErrorException();
        }

        log.debug("Returning " + providers.size() + " providers");
        return new ArrayList<Provider>(providers);
    }
}