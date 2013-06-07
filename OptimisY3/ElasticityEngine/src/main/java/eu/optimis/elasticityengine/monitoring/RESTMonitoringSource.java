package eu.optimis.elasticityengine.monitoring;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import eu.optimis.mi.monitoring_resources.MonitoringResourceDataset;
import eu.optimis.mi.monitoring_resources.MonitoringResourceDatasets;
import eu.optimis.mi.rest.client.getClient;

/**
 * REST based implementation to receive monitoring data.
 * 
 * @author Daniel Espling (<a href="mailto:espling@cs.umu.se">espling@cs.umu.se</a>)
 *Copyright (C) 2012 Umeå University

* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.

* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU General Public License for more details.

* You should have received a copy of the GNU General Public License
* along with this program.  If not, see <http://www.gnu.org/licenses/>.

 */

public class RESTMonitoringSource implements MonitoringSource {

    private final static Logger log = Logger.getLogger(RESTMonitoringSource.class);
    private getClient client;
    public static String[] temp;

    public RESTMonitoringSource(String hostName, int port) {
        this.client = new getClient(hostName, port, "MonitoringManager/QueryResources");
    }

    @Override
    public List<MonitoringResourceDataset> getData(String serviceID, String kpiString) {

       // log.debug("Requesting data for kpiString: " + kpiString);
        
        if (kpiString.indexOf(':') == -1) {
        	log.warn("Could not find suitable prefix for kpiString: " + kpiString + ", defaulting to \"service:\"");
        	kpiString = "service:" + kpiString;
        }

        String[] parts = kpiString.split(":");
        log.warn("KPI Type: "+ parts[0]+" KPIName: "+parts[1]);
        String prefix = parts[0];
        String kpiName = parts[1];
        
        MonitoringResourceDatasets dataSet = null;
        Calendar FromCal=Calendar.getInstance();
        Calendar ToCal=Calendar.getInstance();
        FromCal.add(Calendar.SECOND, -80);
        Date dateFrom=FromCal.getTime();
        Date dateTo=ToCal.getTime();
        log.info("KPI-Name is:"+kpiName+" DateFrom "+dateFrom+" DateTo "+dateTo);    
    	
        if ("energy".equalsIgnoreCase(prefix)) {  
        	log.debug("Requesting energy for kpiName: " + kpiName+" DateFrom "+dateFrom+" DateTo "+dateTo);
        	dataSet = client.getReportForPartMetricName(kpiName, prefix, dateFrom, dateTo);
        } else if ("virtual".equalsIgnoreCase(prefix)) {
        	log.debug("Requesting virtual for kpiName: " + kpiName+" DateFrom "+dateFrom+" DateTo "+dateTo);
        	dataSet = client.getReportForPartMetricName(kpiName, prefix, dateFrom, dateTo);
        } else if ("physical".equalsIgnoreCase(prefix)) {
        	log.debug("Requesting physical for kpiName: " + kpiName+" DateFrom "+dateFrom+" DateTo "+dateTo);
        	dataSet = client.getReportForPartMetricName(kpiName, prefix, dateFrom, dateTo);
        } else if (prefix.equalsIgnoreCase(prefix)) {
        	log.debug("Requesting service for kpiName: " + kpiName+" DateFrom "+dateFrom+" DateTo "+dateTo);
        	dataSet = client.getReportForPartMetricName(kpiName, prefix, dateFrom, dateTo);
        } else {
        	throw new RuntimeException("Expected to be unreachable: default prefix prepending failed.");
        }
        
        
        // Reads all records instead of the latest one. Loop to get latest is in
        // ReactiveServiceController.java (method getMostRecentValue). If the
        // getLatest calls are working, just use the above line of code instead.
        // MonitoringResourceDatasets dataSet = client.getReportForMetric(kpiName, prefix);

        List<MonitoringResourceDataset> result = dataSet.getMonitoring_resource();
//        log.debug("Found " + result.toString() + " items for kpiString: " + kpiName);
        return result;
    }

    @Override
    public Map<String, List<MonitoringResourceDataset>> getData(String serviceID, Set<String> kpiNames) {
        
    	List<MonitoringResourceDataset> dataSet = new ArrayList<MonitoringResourceDataset>();
    	Map<String, List<MonitoringResourceDataset>> compositeDataset=new HashMap<String, List<MonitoringResourceDataset>>();
        for (String kpiName : kpiNames) {

            List<MonitoringResourceDataset> result = null;
            try {
            	String delimiter = ",";
            	temp = kpiName.split(delimiter);
            	for (int i =0; i < temp.length ; i++){
            		if (i%2==0){ 
            			result = getData(serviceID, temp[i]);
            			if (result != null) {
//                            dataSet.addAll(result);
                            compositeDataset.put(temp[i], result);
                            log.info("Got " + result.toString() + " items for serviceID/kpiName: " + serviceID + '/'
                                    + temp[i]);
//                            System.out.println(result.toString());
                        } else {
                            log.info("Got null result for kpi: " + kpiName);
                        }
            		}
            	}
            } catch (com.sun.jersey.api.client.UniformInterfaceException e) {
            	log.warn("Got error during monitoring query: " + e.getMessage());
            }
            
        }
//        log.debug("Dataset acquired"+dataSet.toString());
        log.debug("Composite DataSet returned");
        return compositeDataset;
    }
    
    @Override
    public String[] getKPIs(){
    	return temp;
    }
    
}
