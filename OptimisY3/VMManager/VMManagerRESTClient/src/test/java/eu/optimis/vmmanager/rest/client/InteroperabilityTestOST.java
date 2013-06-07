/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.optimis.vmmanager.rest.client;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import junit.framework.TestCase;
import net.emotivecloud.utils.ovf.EmotiveOVF;
import net.emotivecloud.utils.ovf.OVFDisk;
import net.emotivecloud.utils.ovf.OVFNetwork;
import net.emotivecloud.utils.ovf.OVFWrapper;
import net.emotivecloud.utils.ovf.OVFWrapperFactory;

/**
 *
 * @author jsubirat
 */
public class InteroperabilityTestOST extends TestCase {

    public InteroperabilityTestOST(String testName) {
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

    public void testVMCreationDestruction() {
        try {
            VMManagerRESTClient vmm = new VMManagerRESTClient("optimis-ipvm");

            //Prepare OVF
            OVFWrapper ovfDom = OVFWrapperFactory.create("VMManagerInteroperabilityTestVM",
                    1,
                    512,
                    new OVFDisk[]{new OVFDisk("base", "http://130.239.48.102/optimis-ics/interoperabilityTestImages/centos_15g_x86_64.qcow2", 0L),
                                    new OVFDisk("context", "http://130.239.48.102/optimis-ics/interoperabilityTestImages/testingContestIso.iso", 0L)},
                    new OVFNetwork[]{},
                    null);//Additional properties.

            EmotiveOVF ovfDomEmo = new EmotiveOVF(ovfDom);
            ovfDomEmo.setProductProperty(EmotiveOVF.PROPERTYNAME_VM_NAME, "VMManagerInteroperabilityTestVM");
            ovfDomEmo.getNetworks().put("public", new OVFNetwork("public", null, null, null, null));

            //Create VM
            System.out.println("\nCreating test VM");
            String ovfRet = vmm.addVM(ovfDom.toString(), "true");
            EmotiveOVF ovfDomEmoRet = new EmotiveOVF(ovfRet);
            System.out.println("VM " + ovfDomEmoRet.getId() + " was created in node " + ovfDomEmoRet.getProductProperty(EmotiveOVF.PROPERTYNAME_DESTINATION_HOST));
            System.out.println("The returned OVF is:\n\n" + ovfDomEmoRet.toString());
            
            
            //Suspending execution
            
            System.out.println("\nThe test will suspend its execution for 2 minutes. Please check that the VM was created in the specified host.");
            Thread.sleep(120000);
            
            //Destroy VM
            System.out.println("\nVM " + ovfDomEmoRet.getId() + " is going to be destroyed.");
            vmm.removeVM(ovfDomEmoRet.getId());
            System.out.println("VM " + ovfDomEmoRet.getId() + " was destroyed.");
            System.out.println("\nTest COMPLETED SUCCESSFULLY.");
            
        } catch (Exception ex) {
            System.err.println("Test FAILED.");
            ex.printStackTrace();
            assertTrue(false);
        }
    }
}
