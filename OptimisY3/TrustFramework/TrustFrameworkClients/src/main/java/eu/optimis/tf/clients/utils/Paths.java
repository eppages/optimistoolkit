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

package eu.optimis.tf.clients.utils;

public class Paths {

	private static final String IP = "/ip";
	private static final String SP = "/sp";
	private static final String IPTF = "/IPTrustFramework";
	private static final String SPTF = "/SPTrustFramework";

	public static final String TRUST_SN = "/sn";
	
	public static final String TRUST_IP_DEPLOY = IPTF+"/deploy"+IP;
	public static final String TRUST_IP_OPERATION = IPTF+"/operation"+IP;
	public static final String TRUST_IP_COMMON = IPTF+"/common"+IP;
	
	public static final String TRUST_SP_DEPLOY = SPTF+"/deploy"+SP;
	public static final String TRUST_SP_OPERATION = SPTF+"/operation"+SP;
	public static final String TRUST_SP_COMMON = SPTF+"/common"+SP;

}
