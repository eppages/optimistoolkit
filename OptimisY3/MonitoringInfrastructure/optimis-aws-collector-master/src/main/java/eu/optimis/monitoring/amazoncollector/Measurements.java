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

import java.util.List;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author A545568
 */
@XmlRootElement(name="MonitoringResources")
public class Measurements {
    
    
    private List<Measurement> measurements;
    
    public Measurements() {
        
    }
    
    public Measurements(List<Measurement> measurements) {
        this.measurements = measurements;
    }
    
    @XmlElement(name="monitoring_resource")
    public List<Measurement> getMeasurements() {
        return measurements;
    }
}
