package eu.optimis.elasticityengine;


/**
 * Controller interface for the Elasticity Engine. Predictions are either
 * received by calling the getPrediction() method or by registering a callback
 * object for asynchronous instructions.
 * 
 * @author Ahmed Ali-Eldin
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
public interface ElasticityEngine {

    /**
     * Called once per service to initiate elasticity for an entire service,
     * with callbacks to the given callback object.
     * 
     * @param serviceID
     *            The ID of the service
     * @param serviceManifest
     *            The service manifest as a String
     * @param callback
     *            The object to notify with elasticity changes.
     */
    public abstract void startElasticity(String serviceID, String serviceManifest,boolean LowRiskMode,
             String spAddress);

    /**
     * Register a callback for asynchronous elasticity control.
     * 
     * Only one callback object per service is allowed. Subsequent calls will
     * replace previous registrations. Elasticity must be started for a service
     * before registering a callback object.
     * 
     * @param serviceID
     *            The service to register for
     * @param callback
     *            The object to call upon elasticity changes
     */
    public abstract void updateCallback(String serviceID, ElasticityCallback callback);

    /**
     * Called to deactivate elasticity for a Service
     * 
     * @param serviceID
     *            The ID of the service
     */
	public abstract void stopElasticity(String serviceID);

	/**
	 * Update the Service Manifest for a particular service. This affects all VM
	 * types associated with this manifest.
	 * 
	 * @param serviceID
	 *            The ID of the service to which the manifest belongs
	 * @param serviceManifest
	 *            The updated manifest
	 */
    public abstract void updateElasticityRules(String serviceID, String serviceManifest);

    /**
     * Called to return a future prediction for a given VM type.
     * 
     * @param serviceID
     *            The service ID to which the VM type belongs
     * @param imageID
     *            The VM image type to get a prediction for
     * @param timeSpanInMinutes
     *            The number of minutes for which the prediction is needed.
     * @return The predicted number represents the amount of VM instances that
     *         are required in the next timespan in minutes. A negative number
     *         indicates an error in the calculation process.
     */
	public int getPrediction(String serviceID, String imageID,
			int timeSpanInMinutes);
	
	public void setMode(String serviceID,boolean Proactive);

    /*
     * Input information to the Elasticity Engine (in case of publish-subscribe)
     * 
     * @param serviceID
     *            The ID of the service the measurement belongs to
     * @param typeID
     *            the ID of the VM type
     * @param measurementName
     *            The name of the measurement
     * @param data
     *            The actual data
     * @param timestamp
     *            The original timestamp (as a UNIX long) where the measurement
     *            was made
     *
    public void inputMonitoringData(String serviceID, String typeID, String measurementName, String data,
            long timestamp);
    */
}
