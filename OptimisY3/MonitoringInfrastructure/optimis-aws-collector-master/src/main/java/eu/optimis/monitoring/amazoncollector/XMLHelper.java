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

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

/**
 *
 * @author A545568
 */
public class XMLHelper {
    
    public static final String ROOT = "MonitoringResources";
    public static final String MON_RESOURCE = "monitoring_resource";
    
    public static final String PHYSICAL_RESOURCE= "physical_resource_id";
    public static final String METRIC_NAME="metric_name";
    public static final String METRIC_VALUE="metric_value";
    public static final String METRIC_UNIT="metric_unit";
    public static final String METRIC_TIMESTAMP="metric_timestamp";
    public static final String SERVICE_RESOURCE_ID="service_resource_id";
    public static final String VIRTUAL_RESOURCE_ID="virtual_resource_id";
    public static final String RESOURCE_TYPE="resource_type";
    public static final String COLLECTOR_ID="monitoring_information_collector_id";
    
    public static String createDocument(List<Measurement> measurements) {
        
        Element root = new Element(ROOT);
        Document doc = new Document(root);        

        for (Measurement m : measurements) {
            root.addContent(createMonitoringResource(m));
        }
        
        XMLOutputter outputter = new XMLOutputter();
        outputter.setFormat(Format.getPrettyFormat());
        try {
            outputter.output(doc, new FileOutputStream("./aws.xml"));
        } catch (FileNotFoundException ex) {
            Logger.getLogger(XMLHelper.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(XMLHelper.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        String xmlResult = outputter.outputString(doc);
        String result = xmlResult.replace("encoding=\"UTF-8\"", "");        
        //System.out.println (result);
        return result;
    }
    
    public static Element createMonitoringResource(Measurement measurement) {
        
        Element mon_res = new Element(MON_RESOURCE);
        
        mon_res.addContent(new Element(PHYSICAL_RESOURCE).setText(measurement.getPhysical_resource_id()));
        mon_res.addContent(new Element(METRIC_NAME).setText(measurement.getMetric_name()));
        mon_res.addContent(new Element(METRIC_VALUE).setText(measurement.getMetric_value()));
        mon_res.addContent(new Element(METRIC_UNIT).setText(measurement.getMetric_unit()));
        mon_res.addContent(new Element(METRIC_TIMESTAMP).setText(Long.toString(measurement.getTimestamp().getTime()/1000)));
        mon_res.addContent(new Element(SERVICE_RESOURCE_ID).setText(measurement.getService_id()));
        mon_res.addContent(new Element(VIRTUAL_RESOURCE_ID).setText(measurement.getVirtual_resource_id()));
        mon_res.addContent(new Element(RESOURCE_TYPE).setText(measurement.getResoruce_type()));
        mon_res.addContent(new Element(COLLECTOR_ID).setText(measurement.getCollector_id()));
        
        return mon_res;
    }
}
