package eu.optimis.elasticityengine.callback;

import org.apache.log4j.Logger;

import eu.optimis.cloudoptimizer.rest.client.CloudOptimizerRESTClient;
import eu.optimis.elasticityengine.ElasticityCallback;

/**
 * REST-based callback object for the Elasiticity engine
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
public class RESTCOElasticityCallback implements ElasticityCallback {

    protected final static Logger log = Logger.getLogger(RESTCOElasticityCallback.class);
    private CloudOptimizerRESTClient cloudOptimizerClient;

    public RESTCOElasticityCallback(String host, int port) {
        this.cloudOptimizerClient = new CloudOptimizerRESTClient(host, port,log);
    }

    @Override
    public String getNrInstances(String serviceID, String imageID) {
        log.debug("Calling getNrInstances for serviceID: " + serviceID + ", imageID: " + imageID);
        return cloudOptimizerClient.getNrInstances(serviceID, imageID) + "";
    }

    @Override
    public void addVM(String serviceID, String serviceManifest, String imageID, int delta, String spAddress) {
        log.debug("Calling addVM for serviceID: " + serviceID + ", imageID: " + imageID + ", delta: " + delta + " using spAddress: " + spAddress);
        cloudOptimizerClient.addVM(serviceID, serviceManifest, imageID, delta, spAddress,"EE called AddVM due to increased load");
    }

    @Override
    public void removeVM(String serviceID, String imageID, int delta, String spAddress) {
        log.debug("Calling removeVM for serviceID: " + serviceID + ", imageID: " + imageID + ", delta: "
                + delta + " using spAddress: " + spAddress);
        cloudOptimizerClient.removeVM(serviceID, imageID, delta, spAddress,true);
    }

	@Override
	public String Fake() {
		// TODO Auto-generated method stub
		return null;
	}
}
