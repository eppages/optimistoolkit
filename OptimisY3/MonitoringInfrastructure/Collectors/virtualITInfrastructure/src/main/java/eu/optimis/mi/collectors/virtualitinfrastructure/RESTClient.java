/**

Copyright 2012 University of Stuttgart

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at 

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

 **/
package eu.optimis.mi.collectors.virtualitinfrastructure;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import eu.optimis.mi.monitoring_resources.MonitoringResourceDatasets;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RESTClient {

    private Client client = null;
    private final static String HOST = "localhost";
    private final static int PORT = 8080;
    private final static String PATH = "";
    private String host = HOST;
    private int port = PORT;
    private String path = PATH;

    public RESTClient() {
        this(HOST, PORT, PATH);
    }

    public RESTClient(String host) {
        this(host, PORT, PATH);
    }

    public RESTClient(String host, int port) {
        this(host, port, PATH);
    }

    public RESTClient(String host, int port, String path) {
        this.host = host;
        this.port = port;
        this.path = path;

        DefaultClientConfig config = new DefaultClientConfig();
        config.getFeatures().put(ClientConfig.FEATURE_DISABLE_XML_SECURITY, true);
        client = Client.create(config);
    }

    private String getAddress() throws URISyntaxException {
        String auxPath = path;
        if (!path.startsWith("/")) {
            auxPath = "/" + auxPath;
        }
        return new URI("http", null, host, port, auxPath, null, null).toString();
    }

    /**
     * Returns the number of running instances of the imageId in the serviceid 
     * @param serviceId
     * @param imageId
     * @return
     */
    public MonitoringResourceDatasets getData() {
        WebResource service;
        MonitoringResourceDatasets dataSet = null;
        try {
            service = client.resource(this.getAddress()).path("virtualmonitoring/data");
            dataSet = service.get(MonitoringResourceDatasets.class);
        } catch (URISyntaxException ex) {
            Logger.getLogger(RESTClient.class.getName()).log(Level.SEVERE, null, ex);
        }
        return dataSet;
    }
}
