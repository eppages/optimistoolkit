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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.ClientResponse.Status;
import com.sun.jersey.api.client.GenericType;
import com.sun.jersey.api.client.WebResource;

import eu.optimis_project.monitoring.Measurement;
import eu.optimis_project.monitoring.MonitoringController;

public class ServiceMonitoringResourceTest {

    private static WebResource webResource;
    private MonitoringController monitoringController;
    private static int PORT = 7070;

    @BeforeClass
    public static void initClient() {
        Client client = Client.create();
        webResource = client.resource("http://localhost:" + PORT + "/data"); // XXX
    }

    @Before
    public void initServer() throws Exception {
        this.monitoringController = new MonitoringController();
        this.monitoringController.start(PORT);
    }

    @After
    public void closeServer() {
        this.monitoringController.stop();
        this.monitoringController = null;
    }

    @Test
    public void testInsert() {
        Measurement measurement = new Measurement("testServiceID", "testInstanceID", "testKPI", "1",
                System.currentTimeMillis());
        ClientResponse response = webResource.path("testServiceID").type("application/xml")
                .post(ClientResponse.class, measurement);
        assertEquals(Status.NO_CONTENT, response.getClientResponseStatus());
    }

    @Test
    public void testRemove() {
        ClientResponse response = webResource.path("testServiceID").delete(ClientResponse.class);
        assertEquals(Status.NO_CONTENT, response.getClientResponseStatus());
    }

    @Test
    public void testRemoveAllData() {
        ClientResponse response = webResource.delete(ClientResponse.class);
        assertEquals(Status.NO_CONTENT, response.getClientResponseStatus());
    }

    @Test
    public void testGetData() {
        List<Measurement> measurements = webResource.path("testServiceID").get(
                new GenericType<List<Measurement>>() {
                });
        assertNotNull(measurements);
    }

    @Test
    public void testGetAllData() {
        List<Measurement> measurements = webResource.get(new GenericType<List<Measurement>>() {
        });
        assertNotNull(measurements);
    }

    @Test
    public void testCRD() {
        // Create
        Measurement measurement = new Measurement("testServiceID", "testInstanceID", "testKPI", "1",
                System.currentTimeMillis());
        ClientResponse response = webResource.path("testServiceID").type("application/xml")
                .post(ClientResponse.class, measurement);
        assertEquals(Status.NO_CONTENT, response.getClientResponseStatus());

        // Read
        List<Measurement> measurements = webResource.path("testServiceID").get(
                new GenericType<List<Measurement>>() {
                });
        assertNotNull(measurements);
        assertEquals(measurement, measurements.get(0));

        // Delete
        response = webResource.path("testServiceID").delete(ClientResponse.class);
        assertEquals(Status.NO_CONTENT, response.getClientResponseStatus());
    }

    @Test
    public void testCRDAll() {
        // Create one
        Measurement measurement = new Measurement("testServiceID", "testInstanceID", "testKPI", "1",
                System.currentTimeMillis());
        ClientResponse response = webResource.path("testServiceID").type("application/xml")
                .post(ClientResponse.class, measurement);
        assertEquals(Status.NO_CONTENT, response.getClientResponseStatus());

        // Create two
        Measurement measurement2 = new Measurement("otherServiceID", "testInstanceID", "testKPI", "1",
                System.currentTimeMillis());
        response = webResource.path("otherServiceID").type("application/xml")
                .post(ClientResponse.class, measurement2);
        assertEquals(Status.NO_CONTENT, response.getClientResponseStatus());

        // Read all
        List<Measurement> measurements = webResource.get(new GenericType<List<Measurement>>() {
        });
        assertNotNull(measurements);
        assertEquals(2, measurements.size());

        assertTrue(measurements.contains(measurement));
        assertTrue(measurements.contains(measurement2));

        // Delete all
        response = webResource.delete(ClientResponse.class);
        assertEquals(Status.NO_CONTENT, response.getClientResponseStatus());
    }

}
