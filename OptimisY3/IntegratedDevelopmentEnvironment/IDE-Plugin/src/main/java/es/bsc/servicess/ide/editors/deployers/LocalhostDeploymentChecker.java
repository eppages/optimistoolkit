/*
 *  Copyright 2011-2012 Barcelona Supercomputing Center (www.bsc.es)
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

package es.bsc.servicess.ide.editors.deployers;

import java.util.HashMap;
import java.util.Map;

import es.bsc.servicess.ide.views.DeploymentChecker;

public class LocalhostDeploymentChecker implements DeploymentChecker {

	public LocalhostDeploymentChecker() {

	}

	@Override
	public String getStatus(String serviceID) {
		// TODO Auto-generated method stub
		return DeploymentChecker.DEPLOYED;
	}

	@Override
	public Map<String, Map<String, String>> getMachines(String serviceID) {
		// TODO Auto-generated method stub
		Map<String, Map<String, String>> map = new HashMap<String, Map<String, String>>();
		Map<String, String> vms = new HashMap<String, String>();
		vms.put("cores", "127.0.0.1");
		map.put("localhost", vms);
		return map;
	}

	@Override
	public String getGraph(String serviceID) {
		// TODO
		return new String();
	}

	@Override
	public void undeploy(String serviceID, boolean keepData) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Map getOtherValues(String serviceID) {
		// TODO Auto-generated method stub
		return new HashMap();
	}

}
