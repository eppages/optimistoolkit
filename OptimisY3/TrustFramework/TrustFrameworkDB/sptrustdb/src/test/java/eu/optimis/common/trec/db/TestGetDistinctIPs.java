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
Juan Luis Prieto, Francisco Javier Nieto. Atos Research and Innovation, Atos SPAIN SA
@email francisco.nieto@atosresearch.eu 

**/

package eu.optimis.common.trec.db;

import java.util.List;

import junit.framework.TestCase;
import eu.optimis.common.trec.db.sp.TrecSP2IPDAO;
import eu.optimis.trec.common.db.sp.model.IpInfo;

public class TestGetDistinctIPs extends TestCase {
	
	public void testGetDistinctIPs(){
		TrecSP2IPDAO sp2ip = new TrecSP2IPDAO();
		try {
			List<IpInfo> ips = sp2ip.getDistinctIpIDs();
			for (IpInfo ip : ips){
				System.out.println(ip.getIpId());
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
