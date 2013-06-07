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


import eu.optimis._do.schemas.Objective;
import eu.optimis.sd.SD;
import eu.optimis.sd.iface.ISD;
import junit.framework.TestCase;

public class Y3BasicsTest extends TestCase
{
	private static String manifest = "src/test/resources/ManifestY3.xml";
	
	private ISD sd  = new SD();
	
	protected void setUp() throws Exception
	{
		super.setUp();
	}

	public void testDeployment()
	{
		Objective objective = Objective.COST;
		File smFile = new File(Y3BasicsTest.manifest);

		try
		{
			this.sd.deploy(smFile, objective);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			assertTrue(false);
		}
		assertTrue(true);
	}

	public void testIPDiscovery()
	{
		assertTrue(true);
	}

	public void testRequestOffer()
	{
		assertTrue(true);
	}

	public void testPlacementOptimization()
	{
		assertTrue(true);
	}

	public void testDeploymentMornitoring()
	{
		assertTrue(true);
	}

	//This TC can cover features:
	//Iteration_1_Feature_3,
	//Iteration_2_Feature_8,
	//Iteration_2_Feature_9.
	public void testUndeployment()
	{
		assertTrue(true);
	}
	
}
