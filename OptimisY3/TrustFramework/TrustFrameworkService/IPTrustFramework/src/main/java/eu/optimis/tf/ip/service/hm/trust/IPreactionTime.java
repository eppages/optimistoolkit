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

package eu.optimis.tf.ip.service.hm.trust;

import java.util.Random;

import eu.optimis.tf.ip.service.operators.Opinion;

public class IPreactionTime {

	public double getReactiontime(String serviceId){
		Random generator = new Random();
		int r = generator.nextInt(100);
		if (r > 50){
			return 0.7;
		} else {
			return 0.1;
		}
	}
	
	private Opinion calculateErrOP(){
		Random generator = new Random();
		int r = generator.nextInt(10);
		int s = 10 - r;
		return calculateOpinion(r, s);
	}
	
	private Opinion calculateOpinion(double r, double s) {
		Opinion op = new Opinion(r, s);
		op.setExpectation();
		return op;
	}
}
