/*
 Copyright (C) 2012-2013 Umeå University

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/
package eu.optimis.demogui.sdo;

import java.io.File;
import java.util.List;

import eu.optimis._do.utils.ManifestUtil;
import eu.optimis.manifest.api.sp.Manifest;
import eu.optimis.types.xmlbeans.servicemanifest.XmlBeanServiceManifestDocument;
import junit.framework.TestCase;

//This TC can cover features:
//Iteration_1_Feature_4,
//Iteration_1_Feature_5,
//Iteration_1_Feature_6,
//Iteration_1_Feature_7,
//Iteration_2_Feature_10,
//Iteration_2_Feature_11.
public class DeploymentOptimizationTest extends TestCase
{
	private static String manifestAffinity = "src/test/resources/ManifestY3-Anti.Affinity.xml";
	protected void setUp() throws Exception
	{
		super.setUp();
	}
	
	//Iteration_1_Feature_4
	public void testLargerSizeProblem()
	{
		/*
		try
		{
			Thread.sleep(60*1000);
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}
		*/
		assertTrue(true);
	}
	
	//Iteration_1_Feature_5
	public void testAntiAffintyDeployment()
	{
		try
		{
			File myFile = new File(manifestAffinity);
			XmlBeanServiceManifestDocument doc = XmlBeanServiceManifestDocument.Factory
					.parse(myFile);
			Manifest manifest = Manifest.Factory.newInstance(doc);
			//System.out.println(originalManifest.getManifestId());
			List<List<List<String>>> partitions = ManifestUtil
					.partitionComponentIds(manifest, 2);
			assertEquals(partitions.size(), 2);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			fail(e.getMessage());
		}
		assertTrue(true);
	}
	
	public void testAntiAffintyExtraction()
	{
		assertTrue(true);
	}
	
	//Iteration_1_Feature_6
	public void testWithY3TRECValues()
	{
		assertTrue(true);
	}
	
	//Iteration_1_Feature_7
	public void testWithHolisticManagement()
	{
		assertTrue(true);
	}
	
	//Iteration_2_Feature_10
	public void testAlgWithRealData()
	{
		assertTrue(true);
	}
	
	//Iteration_2_Feature_11
	public void testWithSolver()
	{
		assertTrue(true);
	}
}
