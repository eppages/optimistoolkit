/**

Copyright 2013 ATOS SPAIN S.A. and City University London

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
Francisco Javier Nieto. Atos Research and Innovation, Atos SPAIN SA
@email francisco.nieto@atosresearch.eu 

**/

package eu.optimis.tf.ip.service;

import junit.framework.TestCase;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import eu.optimis.tf.ip.service.operators.ExponentialSmoothingAggregator;
import eu.optimis.tf.ip.service.utils.PropertiesUtils;

/**
 * Unit test for simple App.
 */
public class ForecastTest extends TestCase {
	Logger log = Logger.getLogger(this.getClass().getName());
		
		
	public void testHoltWintersForecastCalculation ()
	{
		PropertyConfigurator.configure(PropertiesUtils.getLogConfig());
		String serviceId = "a4169454-a7bc-441c-b1b2-378ede095180";
		
		log.info("Provide trust forecast for the service "+serviceId);
		ExponentialSmoothingAggregator forecaster = new ExponentialSmoothingAggregator ();
		double forecast = forecaster.calculateTripleAggregation(serviceId, Double.NaN, ExponentialSmoothingAggregator.SERVTRUST, 1);
		log.info("Forecast for the service is: " + forecast);
	}
		
}
