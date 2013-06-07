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

import static eu.optimis_project.monitoring.ServiceMonitoringConfigSettings.CONFIG_FILE_NAME;
import static eu.optimis_project.monitoring.ServiceMonitoringConfigSettings.CONFIG_FILE_PATH;
import static eu.optimis_project.monitoring.ServiceMonitoringConfigSettings.CONF_MON_HOSTNAME_KEY;
import static eu.optimis_project.monitoring.ServiceMonitoringConfigSettings.CONF_MON_PORT_KEY;
import static eu.optimis_project.monitoring.ServiceMonitoringConfigSettings.CONF_CO_HOSTNAME_KEY;
import static eu.optimis_project.monitoring.ServiceMonitoringConfigSettings.CONF_CO_PORT_KEY;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.log4j.Logger;

import com.sun.jersey.api.client.ClientResponse.Status;
import com.sun.jersey.spi.resource.Singleton;

import eu.optimis_project.monitoring.Measurement;
import eu.optimis_project.monitoring.MonitoringUtil;
import eu.optimis_project.monitoring.config.ConfigurationManager;
import eu.optimis_project.monitoring.storage.OptimisMonitoringStorageManager;
import eu.optimis_project.monitoring.storage.StorageManager;

@Path("/")
@Singleton
public class ServiceMonitoringResource {

    private static Logger log = Logger.getLogger(ServiceMonitoringResource.class);

    private final StorageManager storageManager;

    // TODO: Add synchronize around stuff?

    public ServiceMonitoringResource() throws ConfigurationException {
    	ConfigurationManager config = new ConfigurationManager(CONFIG_FILE_PATH, CONFIG_FILE_NAME);
        int monitoring_port = config.getConfig().getInt(CONF_MON_PORT_KEY);
        String monitoring_host = config.getConfig().getString(CONF_MON_HOSTNAME_KEY);
        int cloudoptimizer_port = config.getConfig().getInt(CONF_CO_PORT_KEY);
        String cloudoptimizer_host = config.getConfig().getString(CONF_CO_HOSTNAME_KEY);
        storageManager = new OptimisMonitoringStorageManager(monitoring_host, monitoring_port, MonitoringUtil.POST_PATH,
                MonitoringUtil.GET_PATH, cloudoptimizer_host, cloudoptimizer_port);
        //storageManager = new InMemoryStorageManager();
    }


    @POST
    @Path("/data/{serviceid}")
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.TEXT_PLAIN)
    public Response insertXML(@PathParam("serviceid") String serviceID,
            Measurement measurement) {
        log.debug("SINGLE: POST, accept");
        return insertMeasurement(serviceID, measurement);

    }

    @POST
    @Path("/data/{serviceid}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response insertJSON(@PathParam("serviceid") String serviceID, Measurement measurement) {
        log.debug("SINGLE: POST JSON, accept");
        return insertMeasurement(serviceID, measurement);
    }

    @POST
    @Path("/data/{serviceid}")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.TEXT_PLAIN)
    public Response insertFormEncoded(@PathParam("serviceid") String serviceID,
            @FormParam("instanceid") String instanceID,
            @FormParam("name") String name, @FormParam("kpiName") String kpiName,
            @FormParam("timestamp") String timestampStr, @FormParam("value") String value) {
        log.debug("SINGLE: POST FormEncoded, accept");

        long timestamp = Long.valueOf(timestampStr);
        Measurement measurement = new Measurement(serviceID, instanceID, kpiName, value, timestamp);
        return insertMeasurement(serviceID, measurement);
    }

    /**
     * Manage inserts of received Measurements
     * 
     * @param serviceID
     *            The affected service ID
     * @param measurement
     *            The measurement itself
     * @return The resulting response code
     */
    private Response insertMeasurement(String serviceID, Measurement measurement) {
        assertServiceID(serviceID);
        assertMeasurement(measurement);

        System.out.println("Got serviceID:" + serviceID);
        System.out.println("Got measurement: " + measurement);

        try {
            storageManager.storeData(measurement);
        } catch (IOException e) {
            log.warn("Failed to add measurment for serviceID: " + serviceID);
            throw new InternalErrorException();
        }

        return Response.noContent().build();
    }

    private void assertMeasurement(Measurement measurement) {
        if (measurement == null || !measurement.validate()) {
            log.info("illegal measurement: " + measurement);
            throw new WebApplicationException(Response.status(Status.BAD_REQUEST)
                    .entity("Illegal measurement: " + measurement).type("text/plain").build());
        }
    }

    private void assertServiceID(String serviceID) {
        if (serviceID == null || serviceID.length() < 1) {
            log.info("illegal serviceID: " + serviceID);
            throw new IllegalServiceIDException();
        }
    }

    @DELETE
    @Path("/data/{serviceid}")
    public void remove(@PathParam("serviceid") String serviceID) {
        log.debug("SINGLE: DELETE, remove");
        assertServiceID(serviceID);
        System.out.println("Got serviceID:" + serviceID);

        log.info("Deleting data for service: " + serviceID);
        try {
            storageManager.removeData(serviceID);
            log.info("Successfully deleted data for service: " + serviceID);
        } catch (IllegalArgumentException e) {
            log.info("Failed to remove data for serviceID: " + serviceID);
            // Do nothing
        } catch (IOException e) {
            log.warn("Failed to remove data for serviceID: " + serviceID);
            throw new InternalErrorException();
        }
    }

    @DELETE
    @Path("/data")
    public void removeAllData() {
        log.debug("Collections: DELETE, remove");
        log.info("Deleting all data");

        try {
            storageManager.removeAllData();
        } catch (IOException e) {
            log.warn("Failed to delete data from StorageManager.", e);
            throw new InternalErrorException();
        }
    }

    @GET
    @Path("/data/{serviceid}")
    @Produces(MediaType.APPLICATION_XML)
    public List<Measurement> getData(@PathParam("serviceid") String serviceID) {
        log.debug("SINGLE: GET, toXML");
        assertServiceID(serviceID);
        System.out.println("Got call with serviceID: " + serviceID);

        Set<Measurement> measurements;

        try {
            measurements = storageManager.getData(serviceID);
        } catch (IOException e) {
            log.warn("Failed to read data for serviceID: " + serviceID);
            throw new InternalErrorException();
        }

        return new ArrayList<Measurement>(measurements);
    }

    @GET
    @Path("/data")
    @Produces(MediaType.APPLICATION_XML)
    public List<Measurement> getAllData() {
        log.debug("Collections: GET, toXML");
        Set<Measurement> measurements;

        try {
            measurements = storageManager.getAllData();
        } catch (IOException e) {
            log.warn("Failed to read data from StorageManager.", e);
            throw new InternalErrorException();
        }

        log.debug("Returning " + measurements.size() + " measurements");
        return new ArrayList<Measurement>(measurements);
    }
}
