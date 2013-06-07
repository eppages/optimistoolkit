/**
 * Copyright (C) 2010-2012 Barcelona Supercomputing Center
 *
 * This library is free software; you can redistribute it and/or 
 * modify it under the terms of the GNU Lesser General Public 
 * License as published by the Free Software Foundation; either 
 * version 2.1 of the License, or (at your option) any later version.
 * This library is distributed in the hope that it will be useful, 
 * but WITHOUT ANY WARRANTY; without even the implied warranty of 
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU 
 * Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public 
 * License along with this library; if not, write to the Free Software 
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
import eu.optimis.cloudoptimizer.util.Log;
import eu.optimis.manifest.api.ip.AllocationPattern;
import eu.optimis.manifest.api.ip.Manifest;
import eu.optimis.manifest.api.ip.PhysicalHost;
import junit.framework.TestCase;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.InputStream;

public class TestManifest extends TestCase {
    public TestManifest(String testName) {
        super(testName);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testManifest() throws Exception {
        InputStream file = new FileInputStream("/home/mmacias/optimisy3/CloudOptimizer/CloudOptimizerServer/src/test/java/testMan.xml");
        byte[] b = new byte[file.available()];
        file.read(b);
        file.close();
        String SPmanifest = new String(b);

        Manifest manifest = Manifest.Factory.newInstance(SPmanifest);
        AllocationPattern[] allocPatternFromAC = manifest.getInfrastructureProviderExtensions().getAllocationOffer().getAllocationPatternArray();

        for (AllocationPattern a : allocPatternFromAC) {
            String compId = a.getComponentId();
            System.out.println("componentId = " + compId);
            PhysicalHost physicalHostArray = a.getPhysicalHostArray(0);
            System.out.println("PHarray(0)="+physicalHostArray.getHostName());
        }

    }
}
