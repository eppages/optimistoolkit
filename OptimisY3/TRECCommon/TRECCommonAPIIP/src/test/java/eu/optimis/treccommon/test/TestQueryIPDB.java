/*
 *  Copyright 2013 University of Leeds UK, ATOS SPAIN S.A., City University London, Barcelona Supercomputing Centre and SAP
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

package eu.optimis.treccommon.test;

import eu.optimis.treccommon.QueryDatabase;
import junit.framework.TestCase;

public class TestQueryIPDB extends TestCase {

	public void testGetServiceManifestIP(){
		String serviceId = "72a2768d-7f6f-4fdb-9388-5c5dfda1d2f9";
		
		QueryDatabase qd = new QueryDatabase();
		System.out.println(qd.getIpServiceManifest(serviceId));
	}
}
