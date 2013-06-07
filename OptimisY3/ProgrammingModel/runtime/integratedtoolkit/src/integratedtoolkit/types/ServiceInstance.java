/*
 *  Copyright 2002-2013 Barcelona Supercomputing Center (www.bsc.es)
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package integratedtoolkit.types;

import java.util.HashMap;
import javax.xml.namespace.QName;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.jaxws.endpoint.dynamic.JaxWsDynamicClientFactory;
import org.apache.cxf.transport.http.HTTPConduit;
import org.apache.cxf.transports.http.configuration.HTTPClientPolicy;

public class ServiceInstance {

    private static JaxWsDynamicClientFactory dcf = JaxWsDynamicClientFactory.newInstance();
    private String wsdl;
    private String name;
    private String namespace;
    private HashMap<String, Client> portToClient;

    public ServiceInstance() {
        this.portToClient = new HashMap<String, Client>();
    }

    public ServiceInstance(String wsdl, String name, String namespace) {
        this.wsdl = wsdl;
        this.name = name;
        this.namespace = namespace;
        this.portToClient = new HashMap<String, Client>();
    }

    public String getWsdl() {
        return wsdl;
    }

    public String getName() {
        return name;
    }

    public String getNamespace() {
        return namespace;
    }

    public Client getClient(String portName) {
        Client client = portToClient.get(portName);
        if (client == null) {
            client = addPort(portName);
        }
        return client;
    }

    public void setWsdl(String wsdl) {
        this.wsdl = wsdl;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public synchronized Client addPort(String portName) {
        Client client=portToClient.get(portName);
        if (client!=null){
            return client;
        }

        QName serviceQName = new QName(namespace, name);
        QName portQName = new QName(namespace, portName);
        client = dcf.createClient(wsdl, serviceQName, portQName);

        HTTPConduit http = (HTTPConduit) client.getConduit();
        HTTPClientPolicy httpClientPolicy = new HTTPClientPolicy();
        httpClientPolicy.setConnectionTimeout(0);
        httpClientPolicy.setReceiveTimeout(0);
        http.setClient(httpClientPolicy);
        
        portToClient.put(portName, client);
        return client;
    }
}
