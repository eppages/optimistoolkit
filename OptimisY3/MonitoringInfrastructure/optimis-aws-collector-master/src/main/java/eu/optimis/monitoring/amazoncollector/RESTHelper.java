/**

Copyright 2013 ATOS SPAIN S.A.

Licensed under the Apache License, Version 2.0 (the License);
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

Authors :
Oriol Collell, Francisco Javier Nieto. Atos Research and Innovation, Atos SPAIN SA
@email francisco.nieto@atosresearch.eu 

**/

package eu.optimis.monitoring.amazoncollector;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.filter.LoggingFilter;
import javax.ws.rs.core.MediaType;

/**
 *
 * @author A545568
 */
public class RESTHelper {
    
    private String url;
    private Client client = null;
    
    public RESTHelper(String url) {
        this.url = url;
        client = Client.create();
        client.addFilter(new LoggingFilter(System.out));
    }
    
    public void sendDocument(Measurements ms) {
        WebResource r = client.resource(url);
        /*ClientResponse res = r.type(MediaType.APPLICATION_XML_TYPE).post(ClientResponse.class, doc);
        if (res.getStatus() != ClientResponse.Status.OK.getStatusCode()) {
            throw new RuntimeException("There was a problem while sending the metrics to the aggregator: " +
                    res.getStatus(), new Exception());
        }*/
        /*Measurements res = r.type(MediaType.APPLICATION_XML_TYPE).accept(MediaType.APPLICATION_XML_TYPE).post(Measurements.class, ms);
        System.out.println("Measurement received back!!");
        for (Measurement m : res.getMeasurements()) {
            System.out.println(m.toString());
        }*/
        r.type(MediaType.APPLICATION_XML_TYPE).post(ms);        
    }
    
    public void sendDocument(String ms) {
        WebResource r = client.resource(url);
        /*ClientResponse res = r.type(MediaType.APPLICATION_XML_TYPE).post(ClientResponse.class, doc);
        if (res.getStatus() != ClientResponse.Status.OK.getStatusCode()) {
            throw new RuntimeException("There was a problem while sending the metrics to the aggregator: " +
                    res.getStatus(), new Exception());
        }*/
        /*Measurements res = r.type(MediaType.APPLICATION_XML_TYPE).accept(MediaType.APPLICATION_XML_TYPE).post(Measurements.class, ms);
        System.out.println("Measurement received back!!");
        for (Measurement m : res.getMeasurements()) {
            System.out.println(m.toString());
        }*/
        //r.type(MediaType.APPLICATION_XML_TYPE).post(ms);
        r.type(MediaType.TEXT_PLAIN_TYPE).post(ms);
    }
}
