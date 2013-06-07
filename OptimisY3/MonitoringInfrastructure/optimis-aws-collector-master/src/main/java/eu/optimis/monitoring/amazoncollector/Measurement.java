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

import java.util.Date;
import javax.xml.bind.annotation.XmlElement;

/**
 *
 * @author oriol.collell
 */
public class Measurement {
    
    private static final String PHYSICAL_ID = "optimisaws001";
    private static final String RESOURCE_TYPE = "virtual";
    
    private static String def_collector_id;
       
    public static void setDefCollectorId(String id) {
        Measurement.def_collector_id = id;
    }
    
    private String physical_resource_id;
    private String metric_name;
    private String metric_value;
    private String metric_unit;
    private Date metric_timestamp;
    private String service_resource_id;
    private String virtual_resource_id;
    private String resoruce_type;
    private String collector_id;

    public Measurement() {
        
    }
    
    public Measurement(String metric_name, String metric_value, String metric_unit, Date timestamp, String virtual_resource_id, String service_id) {
        this.physical_resource_id = PHYSICAL_ID;
        this.metric_name = metric_name;
        this.metric_value = metric_value;
        this.metric_unit = metric_unit;
        this.metric_timestamp = timestamp;
        this.virtual_resource_id = virtual_resource_id;
        this.service_resource_id = service_id;
        this.resoruce_type = RESOURCE_TYPE;
        this.collector_id = Measurement.def_collector_id;
    }

    @Override
    public String toString() {
        return "Physical Resource ID: " + physical_resource_id + "\n" +
                "Metric Name: " + metric_name + "\n" +
                "Metric Unit: " + metric_unit + "\n" +
                "Metric Value: " + metric_value + "\n" +
                "Timestamp: " + metric_timestamp + "\n" +
                "Virtual resource ID: " + virtual_resource_id + "\n" +
                "Service ID: " + service_resource_id + "\n" +
                "Resource Type: " + resoruce_type + "\n" +
                "Collector ID: " + collector_id;
    }
    
    @XmlElement
    public String getPhysical_resource_id() {
        return physical_resource_id;
    }

    public void setPhysical_resource_id(String physical_resource_id) {
        this.physical_resource_id = physical_resource_id;
    }

    @XmlElement
    public String getMetric_name() {
        return metric_name;
    }

    public void setMetric_name(String metric_name) {
        this.metric_name = metric_name;
    }

    @XmlElement
    public String getMetric_value() {
        return metric_value;
    }

    public void setMetric_value(String metric_value) {
        this.metric_value = metric_value;
    }

    @XmlElement
    public String getMetric_unit() {
        return metric_unit;
    }

    public void setMetric_unit(String metric_unit) {
        this.metric_unit = metric_unit;
    }

    @XmlElement(name="metric_timestamp")
    public Date getTimestamp() {
        return metric_timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.metric_timestamp = timestamp;
    }

    @XmlElement
    public String getVirtual_resource_id() {
        return virtual_resource_id;
    }

    public void setVirtual_resource_id(String virtual_resource_id) {
        this.virtual_resource_id = virtual_resource_id;
    }

    @XmlElement(name="service_resource_id")
    public String getService_id() {
        return service_resource_id;
    }

    public void setService_id(String service_id) {
        this.service_resource_id = service_id;
    }

    @XmlElement
    public String getResoruce_type() {
        return resoruce_type;
    }

    public void setResoruce_type(String resoruce_type) {
        this.resoruce_type = resoruce_type;
    }

    @XmlElement(name="monitoring_information_collector_id")
    public String getCollector_id() {
        return collector_id;
    }

    public void setCollector_id(String collector_id) {
        this.collector_id = collector_id;
    }
    
}
