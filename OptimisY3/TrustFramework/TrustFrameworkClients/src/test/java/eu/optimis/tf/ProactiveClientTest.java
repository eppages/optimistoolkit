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

package eu.optimis.tf;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Locale;
import java.util.ResourceBundle;

import junit.framework.TestCase;

import com.mysql.jdbc.Driver;

import eu.optimis.tf.clients.TrustFrameworkIPClient;

public class ProactiveClientTest extends TestCase 
{
	String serviceId = "a4169454-a7bc-441c-b1b2-378ede095180";
	String host = "optimis-ipvm.atosorigin.es";
	int port = 8080;
		
	public void testStartMonitoring(){
		TrustFrameworkIPClient tfipc = new TrustFrameworkIPClient(host,port);
		System.out.println("Calling proactive registration to service " + serviceId);
		System.out.println("Operation result " + tfipc.setProactiveTrustAssessor(serviceId, 2.5, 1));
		System.out.println("Waiting a while before removing...");
		try
		{
			synchronized (this) {
			    this.wait(63000);
			}		
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
		System.out.println("Calling remove alert for service " + serviceId);
		System.out.println("Operation result " + tfipc.stopProactiveTrust(serviceId));
	}
}
