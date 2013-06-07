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

package eu.optimis.tf.sp.service;

import junit.framework.TestCase;

public class TestIPAssessment extends TestCase{
	
	String serviceManifest = System.getProperty("user.dir")+"\\src\\test\\resources\\"+"DemoApp6Manifest.xml";
	
	public void testIPAssessment(){
//		System.out.println(serviceManifest);
		SPDeployment spd = new SPDeployment();
		System.out.println(spd.getIPAssessment(serviceManifest));
	}

}
