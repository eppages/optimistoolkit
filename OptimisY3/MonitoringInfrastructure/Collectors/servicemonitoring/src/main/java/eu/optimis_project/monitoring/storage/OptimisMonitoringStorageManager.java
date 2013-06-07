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
package eu.optimis_project.monitoring.storage;

import java.io.StringWriter;
import java.util.Set;

import javax.ws.rs.WebApplicationException;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.apache.log4j.Logger;

import eu.optimis.cloudoptimizer.rest.client.CloudOptimizerRESTClient;
import eu.optimis.mi.monitoring_resources.MonitoringResourceDatasets;
import eu.optimis.mi.rest.client.getClient;
import eu.optimis.mi.rest.client.postClient;
import eu.optimis_project.monitoring.Measurement;
import eu.optimis_project.monitoring.MonitoringUtil;

/**
 * 
 * REST based implementation of the StorageManager interface. Sends records
 * along to the Optimis Monitoring Aggregator for permanent storage, and reads
 * back those records upon read requests.
 * 
 * 
 * @author Daniel Espling <espling@cs.umu.se>
 */
public class OptimisMonitoringStorageManager implements StorageManager {

	private final Logger log = Logger.getLogger(OptimisMonitoringStorageManager.class
			.getName());
    private postClient postClient;
    private getClient getClient;
	private CloudOptimizerRESTClient coClient;

    public OptimisMonitoringStorageManager(String monitoring_host, int monitoring_port, String postPath, String getPath, String cloudoptimizer_host, int cloudoptimizer_port) {
        this.postClient = new postClient(monitoring_host, monitoring_port, postPath);
        this.getClient = new getClient(monitoring_host, monitoring_port, getPath);
        this.coClient = new CloudOptimizerRESTClient(cloudoptimizer_host, cloudoptimizer_port);
    }

	@Override
	public synchronized Set<Measurement> getData(String serviceID) {
		/*
        MonitoringResourceDatasets data = getClient.getReportForService(serviceID);
        getClient.getReportForPartServiceId(serviceId, from, to);
        Set<Measurement> results = MonitoringUtil.datasetToMeasurements(data);
        */
		throw new UnsupportedOperationException("Get from Optimis not supported");
	}

    @Override
    public synchronized boolean storeData(Measurement measurement) {
        //MonitoringResourceDatasets dataSet = MonitoringUtil.measurementToDatasets(measurement, coClient);
        String xmlString = MonitoringUtil.measurementToXMLString(measurement, coClient);
        
        boolean success = false;
        
        try {
        	System.out.println("Pushing report!");
        	//success = postClient.pushReport(dataSet);
        	//String convertedString = dataSetToString(dataSet);
        	success = postClient.pushstrReport(xmlString);
        	//System.out.println("Report is: " + convertedString);
        	System.out.println("Done pushing report!");
        } catch (WebApplicationException e) {
        	log.warn("Connection to monitoring failed!");
        	/*
        } catch (JAXBException e) {
			log.error("Converting measurement failed! measurement: " + measurement.toString());
			e.printStackTrace();
			*/
		}

        if (success) {
            log.debug("Sent measurement to Aggregator! ");
        } else {
            log.warn("Failed to send measurement to Aggregator: " + measurement);
        }

        return success;
    }
    
    private String dataSetToString(MonitoringResourceDatasets dataset) throws JAXBException {
    	JAXBContext context = JAXBContext.newInstance(MonitoringResourceDatasets.class);
    	Marshaller m = context.createMarshaller();
    	StringWriter stringWriter = new StringWriter();
    	m.marshal(dataset, stringWriter);
    	String output = stringWriter.toString();
    	return output;
    }

	@Override
	public synchronized Set<Measurement> getAllData() {
        throw new UnsupportedOperationException(
                "Optimis Monitoring Aggregator currently does not support filtering data without serviceID");

	}

	@Override
	public synchronized int removeData(String serviceID) {
        throw new UnsupportedOperationException("Cannot remove data from Monitoring Aggregator");
	}

	@Override
	public synchronized int removeAllData() {
        throw new UnsupportedOperationException("Cannot remove data from Monitoring Aggregator");
	}

	@Override
	public void shutdown() {
        log.debug("Shut down OptimisMonitoringStorageManager.");
	}
}
