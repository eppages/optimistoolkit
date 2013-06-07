/**
 * Copyright (C) 2010-2013 Barcelona Supercomputing Center
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version. This library is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser
 * General Public License for more details. You should have received a copy of
 * the GNU Lesser General Public License along with this library; if not, write
 * to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA
 * 02111-1307 USA
 */

package net.emotivecloud.scheduler.drp4one;

import java.io.InputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import net.emotivecloud.utils.ovf.EmotiveOVF;
import net.emotivecloud.utils.ovf.OVFDisk;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.opennebula.client.Client;
import org.opennebula.client.OneResponse;
import org.opennebula.client.datastore.Datastore;
import org.opennebula.client.host.Host;
import org.opennebula.client.host.HostPool;
import org.opennebula.client.image.Image;
import org.opennebula.client.image.ImagePool;

public class OneExtraFuncs {

    private String IMGS_DEF_HOME; // = "/home/sergio";
    private String TEMPLATE_NETWORK_NAME; // = "Internal NAT for sergio";
    private String TEMPLATE_BOOTLOADER;
    private String TEMPLATE_REQUIREMENTS;
    

    public OneExtraFuncs() {
        PropertiesConfiguration configDRP4OST = ConfigManager.getPropertiesConfiguration(ConfigManager.DRP4OST_CONFIG_FILE);

        // Defining the projectID against DRP will operate
        this.IMGS_DEF_HOME = configDRP4OST.getString("imgs.def.home");
        this.TEMPLATE_NETWORK_NAME = configDRP4OST.getString("template.network.name");
        this.TEMPLATE_BOOTLOADER = configDRP4OST.getString("template.bootloader");
        this.TEMPLATE_REQUIREMENTS = configDRP4OST.getString("template.requirements");
        
        System.out.println("OneExtraFuncs()");
        System.out.println("this.IMGS_DEF_HOME="+this.IMGS_DEF_HOME);
        System.out.println("this.TEMPLATE_NETWORK_NAME="+this.TEMPLATE_NETWORK_NAME);
        System.out.println("this.TEMPLATE_BOOTLOADER="+this.TEMPLATE_BOOTLOADER);
        System.out.println("this.TEMPLATE_REQUIREMENTS="+this.TEMPLATE_REQUIREMENTS);
        
    }

    /**
     * Analize all the ONE hosts comparing its names and once its found returns
     * its ID.
     *
     * @param cli ONE client
     * @param strHostName the name of the ONE host
     * @return the id of the host that has the name introduces as parameter
     */
    public int getIdFromName(Client cli, String strHostName) {
        String hidStr = null;
        //Get the host ID from the host name
        HostPool hp = new HostPool(cli);
        //hp.info() consulta y carga los hosts de OpenNebula
        hp.info();

        Iterator<Host> oneHosts = hp.iterator();

        while (oneHosts.hasNext()) {
            Host h = oneHosts.next();
            System.out.println("name: " + h.getName());
            String hn = h.getName();

            if (strHostName.equals(hn)) {
                //get its ID
                hidStr = h.getId();
                System.out.println("host id: " + h.getId());
            }

        }

        return Integer.parseInt(hidStr);
    }

    public int createImage(Client cli, int datastoreID, String name, String path, String baseImage) {

        String imgTemplate = null;
        try {
            System.out.println("DRP4ONE-OneExtraFuncs.createImage()> datastore(" + datastoreID + "), name(" + name + "), path(" + path + ") ");
            if (!existsImage(cli, datastoreID, name)) {

                if (path.trim().endsWith(".iso")) {
                    // Si es iso, ha de especificarse como tipo CD
                    imgTemplate = "NAME = \"" + name + "\" \n"
                            + "PATH = " + path + "\n"
                            + "TYPE = CDROM\n"
                            + "TARGET = hdc:cdrom\n"
                            + "DRIVER = file:\n";
                } else if (name.contains(baseImage)) {
                    imgTemplate = "NAME = \"" + name + "\"\n"
                            + "PATH = " + path + "\n"
                            + "TYPE = OS\n"
                            + "TARGET = hda\n"
                            //                            + "DRIVER = file:\n";
                            + "DRIVER = tap:qcow2:\n";
                } else {
                    imgTemplate = "NAME = \"" + name + "\"\n"
                            + "PATH = " + path + " \n"
                            + "+DRIVER = file:\n";
                }

                imgTemplate += "DEV_PREFIX = hd";
                System.out.println("DRP4ONE-OneExtraFuncs.createImage()> Allocating: " + imgTemplate);

                OneResponse or = Image.allocate(cli, imgTemplate, datastoreID);

                if (or.isError()) {
                    System.out.println("DRP4ONE-OneExtraFuncs.createImage()> Allocating: " + or.getErrorMessage());
                } else {
                    System.out.println("DRP4ONE-OneExtraFuncs.createImage()> Allocating: " + or.getMessage());
                    int imgID = Integer.parseInt(or.getMessage());
                    Image i = new Image(imgID, cli);
                    i.info();
                    while (i.stateString() != "READY") {
                        System.out.println("DRP4ONE-OneExtraFuncs.createImage()> STATE(imgID=" + imgID + "): " + i.stateString());
                        Thread.sleep(3000);
                        i.info();
                    }
                }
            } else {
                return Integer.parseInt(getImgIdFromName(cli, name));
            }
        } catch (Exception e) {
            System.out.println("EXCEPCION en OneExtraFuncs > createImage(datastoreID:" + datastoreID + ",name:" + name + "path:" + path + ")");
            e.printStackTrace();
        }

        return 0;
    }

    public void createAllImages(Client cli, int datastoreID, Collection<OVFDisk> disks, String baseImage) {

        for (OVFDisk ovfDisk : disks) {
            String name = ovfDisk.getHref();
            if (name.contains("/")) {
                String path = ovfDisk.getHref();
                createImage(cli, datastoreID, name, path, baseImage);

            } else {
                // Si no contiene / sera el fichero solo (se supone), por tanto se anyade path
                String path = IMGS_DEF_HOME + "/" + ovfDisk.getHref();
                createImage(cli, datastoreID, name, path, baseImage);
            }


        }

        System.out.println("DRP4ONE-OneExtraFuncs.createAllImages()> leaving createAllImages()");

    }

//
//    public int getImageID(Client cli, String name) {
//        ImagePool imgpool = new ImagePool(cli);
//        int imgID = -1;
//
//        Image
//        
//        return -1;
//    }
    /**
     * Exists an image with name 'imgName' at the datastore with id
     * 'datastoreID'?
     *
     * @param cli
     * @param datastoreID
     * @param imgName
     * @return true if exists an image with name 'imgName' at the datastore with
     * id 'datastoreID'?
     */
    public boolean existsImage(Client cli, int datastoreID, String imgName) {

        String imgID = getImgIdFromName(cli, imgName);
        boolean exists = false;

        if (imgID != null) {
            //existeis la imatge
            int imgIDInt = Integer.parseInt(imgID);
            Datastore ds = new Datastore(datastoreID, cli);
            exists = ds.contains(imgIDInt);
        }
        return exists;
    }

    /**
     *
     * @param cli
     * @param imgName
     * @return
     */
    public String getImgIdFromName(Client cli, String imgName) {
        ImagePool imgPool = new ImagePool(cli);
        imgPool.info();
        Iterator<Image> imgs = imgPool.iterator();

        while (imgs.hasNext()) {
            Image tmpImg = imgs.next();
            String tmpName = tmpImg.getName();

            if (tmpName.equals(imgName)) {
                //get its ID
                return tmpImg.getId();
            }
        }
        return null;
    }

    private String ISToString(InputStream is) throws Exception {

        byte[] buffer = new byte[1024];
        int read = 0;
        String str = new String();
        while ((read = is.read(buffer)) != -1) {
            str += new String(buffer);
        }
        return str;
    }

    public String VMTemplate(Client cli, EmotiveOVF ovf) {
//NAME = "VMManagerInteroperabilityTestVM"
//MEMORY = 512
//CPU = 1
//VCPU = 1
//OS = [ BOOTLOADER = "/usr/lib/xen-4.0/boot/hvmloader" ]
//DISK = [
//IMAGE = "contextiso.iso" 
//]
//DISK = [
//IMAGE = "interoperabilityTest.qcow2" 
//]
//NIC = [
//NETWORK = "Internal NAT for sergio"
//]
//FEATURES=[ acpi="yes" ]
//
//GRAPHICS = [type="vnc", listen="127.0.0.1", password="\/|\|67-yavin"]
//
//CONTEXT = [
//IP_0 = "$NIC[IP, NETWORK=\"public\"]",
//MAC_0 = "$NIC[MAC, NETWORK=\"public\"]",
//NETMASK_0="$NETWORK[NETMASK, NAME=\"public\"]",
//GATEWAY_0="$NETWORK[GATEWAY, NAME=\"public\"]",
//BROADCAST_0="$NETWORK[BROADCAST, NAME=\"public\"]",
//NETWORK_ADDRESS_0="$NETWORK[NETWORK, NAME=\"public\"]",
//HOSTNAME="venuscdebianbase-$VMID",
//IP_1 = "$NIC[IP, NETWORK=\"privatenet\"]",
//MAC_1 = "$NIC[MAC, NETWORK=\"privatenet\"]",
//NETMASK_1="$NETWORK[NETMASK, NAME=\"privatenet\"]",
//GATEWAY_1="$NETWORK[GATEWAY, NAME=\"privatenet\"]",
//BROADCAST_1="$NETWORK[BROADCAST, NAME=\"privatenet\"]",
//NETWORK_ADDRESS_1="$NETWORK[NETWORK, NAME=\"privatenet\"]",
//HOSTNAME="venuscdebianbase-$VMID",
//HOME = "",
//SWAP = ""
//]
//REQUIREMENTS = "FALSE"
//RAW = [ DATA = "builder = 'hvm'",
//TYPE = "xen" ]
        String myTemplate = "";

        StringBuilder buf = new StringBuilder(1024);

        // Name, CPUs and Memory
        // they are not mandatory , a name is generated by one will be
        // one_<VID> form
//        boolean weUseKVM = isKVMInUse(ovf);


//    These are flags to check if a certain mandatory [*] values are
//    supplied with the OVF.
//
//    [*] some values are mandatory with XEN, other with KVM...
//    See at http://opennebula.org/documentation:archives:rel2.0:template


        myTemplate += "NAME = \"" + ovf.getId() + "\"\n";
        myTemplate += "MEMORY = " + ovf.getMemoryMB() + "\n";
        myTemplate += "CPU = " + ovf.getCPUsNumber() + "\n";
        myTemplate += "VCPU = " + ovf.getCPUsNumber() + "\n";
        myTemplate += "OS = [ BOOTLOADER = \""+TEMPLATE_BOOTLOADER+"\" ]\n";
        myTemplate += "GRAPHICS = [\n"
                + "TYPE = \"vnc\",\n"
                + "LISTEN  = \"0.0.0.0\"\n"
                + "]\n";
        myTemplate += this.VMDescriptionDisks(cli, ovf.getDisks().values());

        myTemplate += "NIC = [NETWORK = \"" + TEMPLATE_NETWORK_NAME + "\"]\n";
        myTemplate += "FEATURES=[ acpi=\"yes\" ]\n";
        myTemplate += "REQUIREMENTS = \""+TEMPLATE_REQUIREMENTS+"\"\n";
        myTemplate += "RAW=[\n"
                + " DATA=\"builder='hvm'\",\n"
                + " TYPE=\"xen\" ]\n"
                + "RAW=[\n"
                + " DATA=\"device_model='/usr/lib/xen-4.0/bin/qemu-dm'\",\n"
                + " TYPE=\"xen\" ]\n"
                + "RAW=[\n"
                + " DATA=\"on_poweroff='destroy'\",\n"
                + " TYPE=\"xen\" ]\n"
                + "RAW=[\n"
                + " DATA=\"on_reboot='restart'\",\n"
                + " TYPE=\"xen\" ]\n"
                + "RAW=[\n"
                + " DATA=\"on_crash='restart'\",\n"
                + " TYPE=\"xen\" ]\n"
                + "RAW=[\n"
                + " DATA=\"localtime=1\",\n"
                + " TYPE=\"xen\" ]\n"
                + "RAW=[\n"
                + " DATA=\"usbdevice='tablet'\",\n"
                + " TYPE=\"xen\" ]\n"
                + "RAW=[\n"
                + " DATA=\"monitor=1\",\n"
                + " TYPE=\"xen\" ]\n"
                + "RAW=[\n"
                + " DATA=\"serial='pty'\",\n"
                + " TYPE=\"xen\" ]\n"
                + "RAW=[\n"
                + " DATA=\"boot='cd'\",\n"
                + " TYPE=\"xen\" ]\n"
                + "RAW=[\n"
                + " DATA=\"sdl=0\",\n"
                + " TYPE=\"xen\" ]\n"
                + "RAW=[\n"
                + " DATA=\"xen_platform_pci=0\",\n"
                + " TYPE=\"xen\" ]\n";

        System.out.println("DRP4ONE - OneExtraFuncs.VMTemplate()> myTemplate: " + myTemplate);

        return myTemplate;
    }

    public String VMDescriptionDisks(Client cli, Collection<OVFDisk> disks) {

        String disksTemplates = "";

        for (OVFDisk ovfDisk : disks) {
            String name = ovfDisk.getHref();
            disksTemplates += "DISK = [ \n"
                    + "IMAGE = \"" + name + "\"\n"
                    + "]\n";
        }

        System.out.println("DRP4ONE - OneExtraFuncs.VMDescriptionDisks()> disksTemplates: " + disksTemplates);

        return disksTemplates;
    }

    public Integer getKeyOfValue(HashMap<String, Integer> hm, Integer value) {

        Iterator<String> kIter = hm.keySet().iterator();
        while (kIter.hasNext()) {
            String key = kIter.next();
            if (hm.get(key) == value) {
                return hm.get(key);
            }
        }
        return -1;
    }
}
