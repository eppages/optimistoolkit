package elasticityRestinterface;
/**
* 
 * @author Ahmed Ali-Eldin (<a
 *         href="mailto:ahmeda@cs.umu.se">ahmeda@cs.umu.se</a>)
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
import static org.junit.Assert.*;
import elasticityRestinterface.RestInterface;
import org.junit.Test;
import java.io.File;
import java.io.IOException;


import eu.optimis.elasticityengine.sc.Util;


public class RestInterfaceTest {

	@Test
	public void testStartElasticity() throws IOException {
		RestInterface ri=new RestInterface();
		String serviceID="ahj";
		String serviceManifest=Util.getManifest("/home/ahmed/workspace/optimis/branches/OptimisY3/ElasticityRestInterface/SM.xml");
		boolean LowRiskMode=true;
		String spAddress="sp";
		assertEquals(false, ri.startElasticity(serviceID, serviceManifest, LowRiskMode,  spAddress));
	}

	@Test
	public void testStopElasticity() {
		RestInterface ri=new RestInterface();
		String serviceID="ahj";
		assertEquals(false, ri.stopElasticity(serviceID));
		
	}

	@Test
	public void testUpdateElasticityRules() throws IOException {
		RestInterface ri=new RestInterface();
		String serviceID="ahj";
		String serviceManifest=Util.getManifest("/home/ahmed/workspace/optimis/branches/OptimisY3/ElasticityRestInterface/SM2.xml");
		assertEquals(false, ri.updateElasticityRules(serviceID, serviceManifest));
	}

	@Test
	public void testSetMode() {
		RestInterface ri=new RestInterface();
		String serviceID="ahj";
		boolean proactive=false;
		assertEquals(false, ri.setMode(serviceID, proactive));
	}

}
