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
package net.emotivecloud.scheduler.drp4ost;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.ws.rs.WebApplicationException;
import net.emotivecloud.utils.ovf.EmotiveOVF;
import net.emotivecloud.utils.ovf.OVFDisk;
import net.emotivecloud.utils.ovf.OVFNetwork;
import net.emotivecloud.utils.ovf.OVFWrapper;
import net.emotivecloud.utils.ovf.OVFWrapperFactory;
import net.emotivecloud.virtmonitor.ResourceNotFoundException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.cxf.helpers.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

/**
 *
 * @author smendoza
 */
public class OStackClient {

    /**
     * # openstack OS environment os.url.auth=http://172.16.8.1:5000/v2.0;
     * os.tenant.id=f6cb51c22ef94d80823ec498d5f94355; os.tenant.name=optimis
     * os.username=smendoza os.password=77Lx61 #ip where the openstack api is
     * requested openstack.api.ip=172.16.8.1
     *
     * #img default parameters img.default.path=/home/smendoza
     * img.default.disk.format=raw img.default.disk.size=10
     * img.default.container.format=ovf img.default.public=true
     */
    // OpenStack login Credentials
    private String OS_AUTH_URL;
    private String OS_TENANT_ID;
    private String OS_TENANT_NAME;
    private String OS_USERNAME;
    private String OS_PASSWORD;
    private String DRP_HOST_IP;
    private String IMG_DEFAULT_PATH;
    private String IMG_DEFAULT_DISK_FORMAT;
    private String IMG_DEFAULT_CONTAINER_FORMAT;
    private String IMG_DEFAULT_IS_PUBLIC;
    private String IMG_DEFAULT_DISK_SIZE;
    private String token;
    private String drpHost;

    public OStackClient() {

        System.out.println("**************************************************");
        System.out.println("******** oStackClient() constructor init *********");
        System.out.println("**************************************************");

        PropertiesConfiguration configDRP4OST = ConfigManager.getPropertiesConfiguration(ConfigManager.DRP4OST_CONFIG_FILE);

        // Defining the projectID against DRP will operate
        this.OS_AUTH_URL = configDRP4OST.getString("os.url.auth");
        this.OS_TENANT_ID = configDRP4OST.getString("os.tenant.id");
        this.OS_TENANT_NAME = configDRP4OST.getString("os.tenant.name");
        this.OS_USERNAME = configDRP4OST.getString("os.username");
        this.OS_PASSWORD = configDRP4OST.getString("os.password");
        this.DRP_HOST_IP = configDRP4OST.getString("openstack.api.ip");
        this.IMG_DEFAULT_PATH = configDRP4OST.getString("img.default.path");
        this.IMG_DEFAULT_DISK_FORMAT = configDRP4OST.getString("img.default.path");
        this.IMG_DEFAULT_CONTAINER_FORMAT = configDRP4OST.getString("img.default.container.format");
        this.IMG_DEFAULT_IS_PUBLIC = configDRP4OST.getString("img.default.public");
        this.IMG_DEFAULT_DISK_SIZE = configDRP4OST.getString("img.default.disk.size");

        this.drpHost = "http://" + DRP_HOST_IP;
        this.token = this.getToken();

        System.out.println("this.OS_AUTH_URL: " + this.OS_AUTH_URL);
        System.out.println("this.OS_TENANT_ID: " + this.OS_TENANT_ID);
        System.out.println("this.OS_TENANT_NAME: " + this.OS_TENANT_NAME);
        System.out.println("this.OS_USERNAME: " + this.OS_USERNAME);
        System.out.println("this.OS_PASSWORD: " + this.OS_PASSWORD);
        System.out.println("this.DRP_HOST_IP: " + this.DRP_HOST_IP);
        System.out.println("this.IMG_DEFAULT_PATH: " + this.IMG_DEFAULT_DISK_FORMAT);
        System.out.println("this.IMG_DEFAULT_CONTAINER_FORMAT: " + this.IMG_DEFAULT_CONTAINER_FORMAT);
        System.out.println("this.IMG_DEFAULT_IS_PUBLIC: " + this.IMG_DEFAULT_IS_PUBLIC);
        System.out.println("this.IMG_DEFAULT_DISK_SIZE: " + this.IMG_DEFAULT_DISK_SIZE);
        System.out.println("this.drpHost: " + this.drpHost);
        System.out.println("this.token: " + this.token);

        System.out.println("**************************************************");
        System.out.println("******** oStackClient() constructor end  *********");
        System.out.println("**************************************************");

    }

    public String createVM(String flavorID, String baseImageID, String vmName) {

        String apiAddress = ":8774/v2/" + this.OS_TENANT_ID + "/servers";
        String vmDetails = null;

        //Instantiate an HttpClient
        HttpClient client = new DefaultHttpClient();
        try {
            //Instantiate a POST HTTP method
            HttpPost request = new HttpPost(this.DRP_HOST_IP + apiAddress);
            request.setHeader("X-Auth-Token", this.token);
            request.setHeader("Content-type", "application/json");

            String serverPattern = "{\"server\":{\"flavorRef\":\"%s\",\"imageRef\":\"%s\",\"name\":\"%s\"}}";
            String jsonServer = String.format(serverPattern, flavorID, baseImageID, vmName);

            StringEntity se = new StringEntity(jsonServer);
            request.setEntity(se);

            HttpResponse response = client.execute(request);

            //Get the Response Content in a String
            String responseContent = IOUtils.toString(response.getEntity().getContent());
            vmDetails = responseContent;


        } catch (IOException ex) {
            throw new DRPOSTException("IO Exception Creating the Image", StatusCodes.BAD_REQUEST);
        } catch (Exception e) {
            throw new DRPOSTException("Exception Creating the Image", StatusCodes.BAD_REQUEST);
        }

        return vmDetails;

    }

    public String createVM(String flavorID, String baseImageID, String vmName, String host) {

        // http://docs.openstack.org/essex/openstack-compute/admin/content/specify-host-to-boot-instances-on.html

        // nova boot --image 1 --flavor 2 --key_name test --hint force_hosts=server2 my-first-server

        String ret = null;

        try {
            // execution of the add command 

            System.out.println("DRP4OST>HOST: " + host);
            // Create a list with command and its arguments
            ArrayList<String> myCmd = new ArrayList<String>();
            myCmd.add("nova");
            myCmd.add("boot");
            myCmd.add(String.format("--flavor=%s", flavorID));
            myCmd.add(String.format("--image=%s", baseImageID));
            myCmd.add(String.format("--availability_zone=nova:%s", host));
            myCmd.add(String.format("%s", vmName));

            ProcessBuilder pb = new ProcessBuilder(myCmd);

            // Set up the environment to communicate with OpenStack
            Map<String, String> envir = pb.environment();
            envir.put("OS_AUTH_URL", this.OS_AUTH_URL);
            envir.put("OS_TENANT_ID", this.OS_TENANT_ID);
            envir.put("OS_TENANT_NAME", this.OS_TENANT_NAME);
            envir.put("OS_USERNAME", this.OS_USERNAME);
            envir.put("OS_PASSWORD", this.OS_PASSWORD);
            envir.put("mycommand", "nova boot");

            pb.redirectErrorStream(true);

            Process p = pb.start();

            InputStream pis = p.getInputStream();

            String novaOutput = ISToString(pis);
            System.out.println("DRP4OST-createVM > what nova boot returns me" + novaOutput);

            // Read the id line from the nova-client output
            String vmID = getIdFromNova(novaOutput);
            // Get Server details, that will be returned
            ret = getServer(vmID);

            pis.close();

        } catch (Exception e) {
            e.printStackTrace();
            throw new DRPOSTException("Exception creating the image", StatusCodes.BAD_REQUEST);
        }

        return ret;
    }

    public String createImage(OVFDisk disk, String pathLocalBaseImage) {
//        disk_format=qcow2
//The disk_format field specifies the format of the image file. In this case, the image file format is QCOW2, which can be verified using the file command:
//
//$ file stackimages/cirros.img
//Other valid formats are raw, vhd, vmdk, vdi, iso, aki, ari and ami.

//        container-format=bare
//The container-format field is required by the glance image-create command but isn't actually used by any of the OpenStack services, so the value specified here has no effect on system behavior. We specify bare to indicate that the image file is not in a file format that contains metadata about the virtual machine.
//
//Because the value is not used anywhere, it safe to always specify bare as the container format, although the command will accept other formats: ovf, aki, ari, ami.
//        glance image-create --name centos63-image --disk-format=qcow2 --container-format=raw --is-public=True < ./centos63.qcow2
//        glance add name=cirros-0.3.0-x86_64 disk_format=qcow2 container_format=bare < stackimages/cirros.img

        System.out.println("DRP4OST-this.createImage()> disk.getHref()=" + disk.getHref() + "disk.getId()" + disk.getId());

        String imagePath = disk.getHref(); //"/home/smendoza/cirros-0.3.0-x86_64-disk.img";
        String name = imagePath.trim().substring(imagePath.lastIndexOf("/") + 1, imagePath.length());
//        String name = disk.getId();        
//        String name = disk.getId() + "_" + (new Random()).nextInt(99999999);
        String diskFormat = "qcow2"; // permitted: img, raw, vhd, vmdk, vdi, iso, aki, ari and ami
        boolean download = false;
        String openStackID = null;

        try {
            // execution of the add command 
//            Process p = Runtime.getRuntime().exec(cmdAdd, env);

            // Obtained the arguments from 'glance help add'
            ArrayList<String> myCmd = new ArrayList<String>();
            myCmd.add("glance");
            myCmd.add("add");
//            myCmd.add(String.format("id=%s", disk.getId()));
            myCmd.add(String.format("name=%s", name));
            myCmd.add(String.format("disk_format=%s", diskFormat));
            myCmd.add(String.format("container_format=%s", this.IMG_DEFAULT_CONTAINER_FORMAT));
            myCmd.add(String.format("is_public=%s", this.IMG_DEFAULT_IS_PUBLIC));

            if (disk.getHref().startsWith("http://")) {
                //The image has been already downloaded and merged, and is staying at pathLocalBaseImage
                imagePath = pathLocalBaseImage;
                download = false;
            } else {
                // App will add the full path
                download = false;
                if (!disk.getHref().startsWith("/")) {
                    //Incomplete path, necessary to add default path (img.default.path parameter)
                    imagePath = this.IMG_DEFAULT_PATH + "/" + imagePath;
                } else {
                    //Complete path (expected), not necessary to add nothing
                }
            }

            ProcessBuilder pb = new ProcessBuilder(myCmd);
            pb.redirectErrorStream(true);

            // Set up the environment to communicate with OpenStack
            Map<String, String> envir = pb.environment();
            envir.put("OS_AUTH_URL", this.OS_AUTH_URL);
            envir.put("OS_TENANT_ID", this.OS_TENANT_ID);
            envir.put("OS_TENANT_NAME", this.OS_TENANT_NAME);
            envir.put("OS_USERNAME", this.OS_USERNAME);
            envir.put("OS_PASSWORD", this.OS_PASSWORD);

            // Execute the command specified with its environment
            Process p = pb.start();

            InputStream pis = p.getInputStream();

            // if image not downloaded, it will have to uploaded 
            if (!download) {
                OutputStream pos = p.getOutputStream();

                File imgFile = new File(imagePath);
                System.out.println("DRP4OST-OStackClient.createImage()> START UPLOADING (" + imgFile.getPath() + ") TO OPENSTACK REPOSITORY");
                if (imgFile.exists()) {
                    InputStream fis = new FileInputStream(imgFile);
                    byte[] buffer = new byte[1024];
                    int read = 0;
                    while ((read = fis.read(buffer)) != -1) {
                        pos.write(buffer, 0, read);
                    }
                    // Close the file stream
                    fis.close();
                    System.out.println("DRP4OST-OStackClient.createImage()> FINISH UPLOADING (" + imgFile.getPath() + ")TO OPENSTACK REPOSITORY");
                } else {
                    System.out.println("DRP4OST-OStackClient.createImage()> The file " + imgFile.getPath() + " does not exist! Abort VM creation!");
                    throw new Exception("DRP4OST-OStackClient.createImage()> The file " + imgFile.getPath() + " does not exist! Abort VM instance creation!");
                }

                // Close the process stream. If not, OpenStack keeps the image at "Saving" status
                pos.close();
            } else {
                System.out.println("DRP4OST-OStackClient.createImage()> The image is expected to be download automatically by OpenStack-glance from " + imagePath);
            }
            System.out.println("DRP4OST-OStackClient.createImage()> glance output: " + ImageMerge.ISToString(pis));
            pis.close();

            openStackID = getImageID(name);

            System.out.println("DRP4OST-OStackClient.createImage()> getImageStatus(" + openStackID + ")=" + getImageStatus(openStackID));

            // Wait while hte image is not ready to be used
            while (!getImageStatus(openStackID).equals("ACTIVE")) {
                System.out.println("DRP4OST-OStackClient.createImage()> getImageStatus(" + openStackID + ")=" + getImageStatus(openStackID));
                Thread.sleep(1000);
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw new DRPOSTException("Exception creating the image", StatusCodes.BAD_REQUEST);
        }
        return openStackID;
    }

    public String createImage(String name, String path, String baseImage) {

        String format = null;
        try {
            System.out.println("DRP4ONE-OneExtraFuncs.createImage()> name(" + name + "), path(" + path + ") ");

            if (!existsImage(name)) {
                //If the image does not exist, create it

                if (path.trim().endsWith(".iso")) {
                    format = "iso";

                } else if (name.contains(baseImage)) {
                    // TODO: create the image, base image does not require volume
                    String[] fileSplit = path.split("[.]");
                    format = fileSplit[fileSplit.length - 1];
                } else {
                    format = this.IMG_DEFAULT_DISK_FORMAT;
                }

                System.out.println("DRP4ONE-OneExtraFuncs.createImage()> crating image=" + name + " with format=" + format);

                // TODO: create the image at the OpenStack repository
                ArrayList<String> myCmd = new ArrayList<String>();
                myCmd.add("glance");
                myCmd.add("add");
                myCmd.add(String.format("id=%s", name));
                myCmd.add(String.format("name=%s", name));
                myCmd.add(String.format("disk_format=%s", format));
                myCmd.add(String.format("container_format=%s", this.IMG_DEFAULT_CONTAINER_FORMAT));
                myCmd.add(String.format("is_public=%s", this.IMG_DEFAULT_IS_PUBLIC));

                ProcessBuilder pb = new ProcessBuilder(myCmd);
                pb.redirectErrorStream(true);

                // Set up the environment to communicate with OpenStack
                Map<String, String> envir = pb.environment();
                envir.put("OS_AUTH_URL", this.OS_AUTH_URL);
                envir.put("OS_TENANT_ID", this.OS_TENANT_ID);
                envir.put("OS_TENANT_NAME", this.OS_TENANT_NAME);
                envir.put("OS_USERNAME", this.OS_USERNAME);
                envir.put("OS_PASSWORD", this.OS_PASSWORD);

                // Execute the command specified with its environment
                Process p = pb.start();

                OutputStream pos = p.getOutputStream();

                InputStream fis = new FileInputStream(new File(path));
                byte[] buffer = new byte[1024];
                int read = 0;
                while ((read = fis.read(buffer)) != -1) {
                    pos.write(buffer, 0, read);
                }
                // Close the file stream
                fis.close();
                // Close the process stream. If not, OpenStack keeps the image at "Saving" status
                pos.close();

                //TODO: verify error creating image





//                if (or.isError()) {
//                    //TODO: if error creating image...
//                    System.out.println("DRP4ONE-OneExtraFuncs.createImage()> Error creating image: " + name);
//
//                } else {
//                    //TODO: if ok creating image 
//                    System.out.println("DRP4ONE-OneExtraFuncs.createImage()> OK creating image: " + name);
//                    int imgID = Integer.parseInt(or.getMessage());
//                    //TODO: not leave while image not ready to being used
//                    while (i.stateString() != "READY") {
//                        String tmpState = i.stateString();
//                        System.out.println("DRP4ONE-OneExtraFuncs.createImage()> STATE(imgID=" + imgID + "): " + tmpState);
//                        Thread.sleep(3000);
//                    }
//                }
            } else {
                //If the image already exists, don't create it
                //TODO: return the identifier
                return name;
            }
        } catch (Exception e) {
            System.out.println("DRP4ONE-OneExtraFuncs.createImage()> name(" + name + "), path(" + path + ") ");
            e.printStackTrace();
        }

        return name;
    }

    /**
     * No much sense method, because just one Image per instance can be used
     *
     * @param disks
     * @param baseImage
     */
    public void createAllImages(Collection<OVFDisk> disks, String baseImage) {

        for (OVFDisk ovfDisk : disks) {
            String path = ovfDisk.getHref();

            //TODO: if (disk is baseImage): create image; else create volume containing the image

            if (path.contains("/")) {
                //Complete path, not necessary to add default path
                createImage(ovfDisk.getId(), path, baseImage);

            } else {
                //Incomplete path, not necessary to add default path
                path = this.IMG_DEFAULT_PATH + "/" + path;
                createImage(ovfDisk.getId(), path, baseImage);
            }
        }

        System.out.println("DRP4ONE-OneExtraFuncs.createAllImages()> leaving createAllImages()");

    }

    public String createFlavor(EmotiveOVF ovf) {

        String apiAddress = ":8774/v2/" + OS_TENANT_ID + "/flavors";
        String flavorID = null;

        System.out.println("DRP4OST>OStackClient.createFlavor(): entra en createFlavor");

// Mandatory OpenStack's Flavor attributes
//        name: The name for the flavor.
//        ram: The amount of RAM, in MBs, for the flavor.
//        vcpus: The virtual CPUs, in a whole integer amount, for the flavor.
//        disk: The amount of disk space, in GBs, for the flavor.
//        id: The ID, a unique integer value, for the flavor.

        String name = ovf.getId();
        Object ram = ovf.getMemoryMB();
        Object vcpus = ovf.getCPUsNumber();
        Map<String, OVFDisk> allDisks = ovf.getDisks();
        long size = Long.parseLong(this.IMG_DEFAULT_DISK_SIZE);
        String diskSize = this.IMG_DEFAULT_DISK_SIZE;

        // Get the DISK's section
        for (OVFDisk ovfDisk : allDisks.values()) {
            if (ovfDisk.getHref().contains(ovf.getBaseImage())) {
                size = ovfDisk.getCapacityMB() / 1024;
                System.out.println("DRP4OST>OStackClient.createFlavor(): size(" + ovfDisk.getCapacityMB() + " / 1024)=" + size + "");

                if (size <= 1 || size < Long.parseLong(this.IMG_DEFAULT_DISK_SIZE)) {
                    diskSize = this.IMG_DEFAULT_DISK_SIZE;
                }
            }
        }

        // If the VM already has Flavor, update it. Necessary to delete & create 
        // because OpenStack does not permit to update flavors
        // Cannot delete flavors that are already in use by another 
        if (this.activeInstancesWithFlavor(name) > 0) {
            String myFlavorID = this.getFlavorID(name);
            System.out.println("DRP4OST-OStackClient.createFlavor(): Flavor used by running VM, cannot be updated, this flavor will be used myFlavorID=" + myFlavorID);
            return myFlavorID;
        } else if (this.existsFlavor(name) && this.activeInstancesWithFlavor(name) == 0) {
            System.out.println("DRP4OST-OStackClient.createFlavor(): Update obligates to delete the Flavor flavorID=" + this.getFlavorID(name));
            this.deleteFlavor(name);
        } else {
            System.out.println("DRP4OST-OStackClient.createFlavor(): ELSE, name=" + name + ", myFlavorID=" + this.getFlavorID(name));
        }

        String flavorPattern = "{"
                + "     \"flavor\": {"
                + "     \"disk\": %s,"
                + "     \"vcpus\": %s, "
                + "     \"ram\": %s,"
                + "     \"name\": \"%s\","
                + "     \"id\": \"%s\","
                + "     \"OS-FLV-EXT-DATA:ephemeral\": %s"
                + "     }"
                + "}";

        // OS-FLV-EXT-DATA:ephemeral has to be "2", otherwise it does'nt works
        String flavor = String.format(flavorPattern, diskSize, vcpus, ram, name, name, "2");

        //Instantiate an HttpClient
        HttpClient client = new DefaultHttpClient();
        try {
            //Instantiate a POST HTTP method
            HttpPost request = new HttpPost(this.drpHost + apiAddress);
            request.setHeader("X-Auth-Token", this.token);
            request.setHeader("Content-type", "application/json");
            request.setHeader("Accept", "application/json");
            request.setHeader("User-Agent", "python-novaclient");
            request.setHeader("X-Auth-Project-Id", this.OS_TENANT_NAME);

            StringEntity se = new StringEntity(flavor);
            request.setEntity(se);

            //Execute the request, obtain a Response
            HttpResponse response = client.execute(request);
            System.out.println("DRP4OST>OStackClient.createFlavor(), response=" + response);


            //If the response is not OK, throw an exception
            if (response.getStatusLine().getStatusCode() != 200) {
                System.out.println("DRP4OST>OStackClient.createFlavor");
                throw new DRPOSTException(response.getStatusLine().getReasonPhrase(), StatusCodes.BAD_REQUEST);
            }

            //Get the Response Content in a String
            String responseContent = IOUtils.toString(response.getEntity().getContent());
            System.out.println("DRP4OST>OStackClient.createFlavor, responseContent=" + responseContent);

            // Parsing the JSON response to get the flavor ID
            JSONObject jo = (JSONObject) JSONValue.parse(responseContent);
            jo = (JSONObject) jo.get("flavor");
            flavorID = jo.get("id").toString();
            System.out.println("DRP4OST>OStackClient.createFlavor, flavorID=" + flavorID);

        } catch (IOException ex) {

            throw new DRPOSTException("Exception creating the Flavor", StatusCodes.BAD_REQUEST);
        }
//        return "2";
        return flavorID;
    }

    public String getVM(String vmID) {
        String apiAddress = ":8774/v2/" + OS_TENANT_ID + "/servers/" + vmID;
        String vmDetails = null;

        //Instantiate an HttpClient
        HttpClient client = new DefaultHttpClient();
        try {
            //Instantiate a GET HTTP method
            HttpGet request = new HttpGet(this.drpHost + apiAddress);
            request.setHeader("X-Auth-Token", this.token);

            //Execute the request, obtain a Response
            HttpResponse response = client.execute(request);

            //If the response is not OK, throw an exception
//            if (response.getStatusLine().getStatusCode() != 200) {
//                throw new DRPOSTException(response.getStatusLine().getReasonPhrase(), StatusCodes.BAD_REQUEST);
//            }
            if (response == null) {
                throw new WebApplicationException(new ResourceNotFoundException("cannot retrieve " + vmID, null), 404);
            }

            //Get the Response Content in a String
            String responseContent = IOUtils.toString(response.getEntity().getContent());
            vmDetails = responseContent;

        } catch (IOException ex) {
            throw new DRPOSTException("IO Exception getting details from the VM", StatusCodes.BAD_REQUEST);
        } catch (Exception e) {
            throw new DRPOSTException("Exception getting details from the VM", StatusCodes.BAD_REQUEST);
        }
        return vmDetails;
    }

    /**
     * Gets all the identifiers of the VMs running on this project
     *
     * @return all the identifiers of the VMs running on this project
     */
    public ArrayList<String> getAllVMsIDs() {
        String apiAddress = ":8774/v2/" + this.OS_TENANT_ID + "/servers";
        ArrayList<String> vmsIDsList = new ArrayList<String>();

        //Instantiate an HttpClient
        HttpClient client = new DefaultHttpClient();
        try {
            //Instantiate a GET HTTP method
            HttpGet request = new HttpGet(this.drpHost + apiAddress);
            request.setHeader("X-Auth-Token", this.token);

            //Execute the request, obtain a Response
            HttpResponse response = client.execute(request);
            //Get the Response Content in a String

            String responseContent = IOUtils.toString(response.getEntity().getContent());
            // Parsing the JSON response to get all the flavors
            JSONObject allServers = (JSONObject) JSONValue.parse(responseContent);
            JSONArray jsonServers = (JSONArray) allServers.get("servers");

            for (int i = 0; i < jsonServers.size(); ++i) {
                JSONObject flavor = (JSONObject) jsonServers.get(i);
                String vmID = flavor.get("id").toString();
                vmsIDsList.add("" + vmID);
            }

        } catch (IOException ex) {
            throw new DRPOSTException("IO Exception getting IDs from the VMs", StatusCodes.BAD_REQUEST);
        } catch (Exception e) {
            throw new DRPOSTException("Exception getting IDs from the VMs", StatusCodes.BAD_REQUEST);
        }
        return vmsIDsList;

    }

    //Migrates a server to a host. The scheduler chooses the host. Specify the migrate action in the request body.
    public String migrate(String vmID, String hostDestiny) {
//        v2/{tenant_id}/servers/{server_id}/action

        String apiAddress = ":8774/v2/" + OS_TENANT_ID + "/servers/" + vmID + "/action";
        ArrayList<String> vmsIDsList = new ArrayList<String>();
        String action = "{\"migrate\": null}";

//        {
//    "os-migrateLive": {
//        "host": "bscgrid22",
//        "block_migration": true,
//        "disk_over_commit": false
//    }
//}


        //Instantiate an HttpClient
        HttpClient client = new DefaultHttpClient();
        try {
            //Instantiate a POST HTTP method
            HttpPost request = new HttpPost(this.drpHost + apiAddress);
            request.setHeader("X-Auth-Token", this.token);

            StringEntity se = new StringEntity(action);
            request.setEntity(se);

            //Execute the request, obtain a Response
            HttpResponse response = client.execute(request);

            //If the response is not OK, throw an exception
            if (response.getStatusLine().getStatusCode() != 200) {
                throw new DRPOSTException(response.getStatusLine().getReasonPhrase(), StatusCodes.BAD_REQUEST);
            }
        } catch (Exception e) {
            throw new DRPOSTException("Exception migrating VM", StatusCodes.BAD_REQUEST);
        }
        return null;
    }

    public String getHosts() {

        String apiAddress = ":8774/v2/" + OS_TENANT_ID + "/os-hosts";
        String hosts = null;

        //Instantiate an HttpClient
        HttpClient client = new DefaultHttpClient();
        try {
            //Instantiate a GET HTTP method
            HttpGet request = new HttpGet(this.drpHost + apiAddress);
            request.setHeader("X-Auth-Token", this.token);

            //Execute the request, obtain a Response
            HttpResponse response = client.execute(request);

            //If the response is not OK, throw an exception
            if (response.getStatusLine().getStatusCode() != 200) {
                throw new DRPOSTException(response.getStatusLine().getReasonPhrase(), StatusCodes.BAD_REQUEST);
            }

            //Get the Response Content in a String
            String responseContent = IOUtils.toString(response.getEntity().getContent());
            hosts = responseContent;

        } catch (IOException ex) {
            throw new DRPOSTException("Exception getting all images", StatusCodes.BAD_REQUEST);
        }

        return hosts;
    }

    public String getAllServers() {
        return getAllServers(new HashMap());
    }

    public String getAllServers(HashMap options) {
        String apiAddress = ":8774/v2/" + OS_TENANT_ID + "/servers";
        String servers = null;

        //Instantiate an HttpClient
        HttpClient client = new DefaultHttpClient();
        try {
            // Add the options to the request
            URIBuilder builder = new URIBuilder();

            Iterator it = options.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry pairs = (Map.Entry) it.next();
                builder.setParameter((String) pairs.getKey(), (String) pairs.getValue());
                it.remove(); // avoids a ConcurrentModificationException
            }
            URI uri = builder.build();

            //Instantiate a GET HTTP method
            HttpGet request = new HttpGet(this.drpHost + apiAddress + uri.toString());
            request.setHeader("X-Auth-Token", this.token);

            //Execute the request, obtain a Response
            HttpResponse response = client.execute(request);

            //If the response is not OK, throw an exception
            if (response.getStatusLine().getStatusCode() != 200) {
                throw new DRPOSTException(response.getStatusLine().getReasonPhrase(), StatusCodes.BAD_REQUEST);
            }

            //Get the Response Content in a String
            String responseContent = IOUtils.toString(response.getEntity().getContent());
            servers = responseContent;

        } catch (IOException ex) {
            throw new DRPOSTException("Exception getting all servers", StatusCodes.BAD_REQUEST);
        } catch (Exception e) {
            throw new DRPOSTException("Exception getting all servers", StatusCodes.BAD_REQUEST);
        }
        return servers;
    }

    public String getToken(String username, String psswd, String projectName) {

        String apiAddress = ":5000/v2.0/tokens";
        String json_auth_pattern = "{\"auth\":{\"passwordCredentials\":{\"username\":\"%s\",\"password\":\"%s\"},\"tenantName\":\"%s\"}}";
        String json_auth = String.format(json_auth_pattern, username, psswd, projectName);
        String token = null;

        //Instantiate an HttpClient
        HttpClient client = new DefaultHttpClient();
        try {
            //Instantiate a GET HTTP method
            HttpPost request = new HttpPost(drpHost + apiAddress);
            request.setHeader("Content-type", "application/json");
            StringEntity se = new StringEntity(json_auth);
            StringEntity se2 = new StringEntity("{\"auth\":{\"passwordCredentials\":{\"username\":\"smendoza\",\"password\":\"77Lx61\"},\"tenantName\":\"optimis\"}}");
            request.setEntity(se);

            HttpResponse response = client.execute(request);
            String responseContent = IOUtils.toString(response.getEntity().getContent());

            JSONObject jo = (JSONObject) JSONValue.parse(responseContent);
            jo = (JSONObject) jo.get("access");
            jo = (JSONObject) jo.get("token");
            token = jo.get("id").toString();

        } catch (IOException ex) {
            ex.printStackTrace();
            throw new DRPOSTException("Exception getting the Token", StatusCodes.BAD_REQUEST);
        }
        return token;
    }

    public String getToken() {

        String username = this.OS_USERNAME;
        String psswd = this.OS_PASSWORD;
        String projectName = this.OS_TENANT_NAME;
        String apiAddress = this.drpHost + ":5000/v2.0/tokens";
        String json_auth_pattern = "{\"auth\":{\"passwordCredentials\":{\"username\":\"%s\",\"password\":\"%s\"},\"tenantName\":\"%s\"}}";
        String json_auth = String.format(json_auth_pattern, username, psswd, projectName);
        String token = null;

        //Instantiate an HttpClient
        HttpClient client = new DefaultHttpClient();
        try {
            //Instantiate a GET HTTP method
            HttpPost request = new HttpPost(apiAddress);
            request.setHeader("Content-type", "application/json");
            StringEntity se = new StringEntity(json_auth);
            StringEntity se2 = new StringEntity("{\"auth\":{\"passwordCredentials\":{\"username\":\"smendoza\",\"password\":\"77Lx61\"},\"tenantName\":\"optimis\"}}");
            request.setEntity(se);

            HttpResponse response = client.execute(request);
            String responseContent = IOUtils.toString(response.getEntity().getContent());

            JSONObject jo = (JSONObject) JSONValue.parse(responseContent);
            jo = (JSONObject) jo.get("access");
            jo = (JSONObject) jo.get("token");
            token = jo.get("id").toString();

        } catch (IOException ex) {
            ex.printStackTrace();
            throw new DRPOSTException("Exception getting the Token", StatusCodes.BAD_REQUEST);
        }
        return token;
    }

    public String getAllImages() {
        String apiAddress = this.drpHost + ":8774/v2/" + OS_TENANT_ID + "/images/detail";
        String images = null;

        //Instantiate an HttpClient
        HttpClient client = new DefaultHttpClient();
        try {
            //Instantiate a GET HTTP method
            HttpGet request = new HttpGet(apiAddress);
            request.setHeader("X-Auth-Token", this.token);

            //Execute the request, obtain a Response
            HttpResponse response = client.execute(request);

            //If the response is not OK, throw an exception
            if (response.getStatusLine().getStatusCode() != 200) {
                System.out.println("DRP4OST>Exception: response=" + response.getStatusLine().getReasonPhrase());
                throw new DRPOSTException(response.getStatusLine().getReasonPhrase(), StatusCodes.BAD_REQUEST);
            }
            //Get the Response Content in a String
            String responseContent = IOUtils.toString(response.getEntity().getContent());
            images = responseContent;

        } catch (IOException ex) {
            throw new DRPOSTException("Exception getting all images", StatusCodes.BAD_REQUEST);
        }
        return images;
    }

    public String getAllFlavors() {
        String apiAddress = ":8774/v2/" + OS_TENANT_ID + "/flavors";
        String flavors = null;

        //Instantiate an HttpClient
        HttpClient client = new DefaultHttpClient();
        try {
            //Instantiate a GET HTTP method
            HttpGet request = new HttpGet(this.drpHost + apiAddress);
            request.setHeader("X-Auth-Token", this.token);

            //Execute the request, obtain a Response
            HttpResponse response = client.execute(request);

            //If the response is not OK, throw an exception
            if (response.getStatusLine().getStatusCode() != 200) {
                throw new DRPOSTException(response.getStatusLine().getReasonPhrase(), StatusCodes.BAD_REQUEST);
            }

            //Get the Response Content in a String
            String responseContent = IOUtils.toString(response.getEntity().getContent());
            flavors = responseContent;

        } catch (IOException ex) {
            throw new DRPOSTException("Exception getting all flavors", StatusCodes.BAD_REQUEST);
        }
        return flavors;
    }

    public String getServer(String serverId) {
        String apiAddress = ":8774/v2/" + OS_TENANT_ID + "/servers/" + serverId;
        String server = null;

        //Instantiate an HttpClient
        HttpClient client = new DefaultHttpClient();
        try {
            //Instantiate a GET HTTP method
            HttpGet request = new HttpGet(this.drpHost + apiAddress);
            request.setHeader("X-Auth-Token", this.token);

            //Execute the request, obtain a Response
            HttpResponse response = client.execute(request);

            //If the response is not OK, throw an exception
            if (response.getStatusLine().getStatusCode() != 200) {
                System.out.println("DRP4OST>serverId: " + serverId);
                System.out.println("DRP4OST>request: " + request);
                System.out.println("DRP4OST>response: " + response);
                System.out.println("DRP4OST>getServer: " + response.getStatusLine().getReasonPhrase());
                throw new DRPOSTException(response.getStatusLine().getReasonPhrase(), StatusCodes.BAD_REQUEST);
            }

            //Get the Response Content in a String
            String responseContent = IOUtils.toString(response.getEntity().getContent());
            server = responseContent;

        } catch (IOException ex) {
            throw new DRPOSTException("Exception getting all images", StatusCodes.BAD_REQUEST);
        }

        return server;
    }

    public String getServerAsOVF(String serverId) {

        String ovf = null;
        String jsonServer = this.getServer(serverId);
        String vmName = null;
        int nCPUs = -1;
        int ramSize = -1;
        String imgName = null; //base
        String imgPath = null; //"interoperabilityTest.qcow2"

        //TODO: extract cpus from jsonServer

        //TODO: extract ram from jsonServer

        //TODO: extract imgName from jsonServer

        //TODO: extract imgPath from jsonServer

        // TODO: convert to XML with OVFWrapperFactory!!!

        //Prepare OVF
        OVFWrapper ovfDom = OVFWrapperFactory.create(vmName,
                nCPUs,
                ramSize,
                new OVFDisk[]{new OVFDisk(imgName, imgPath, 0L)},
                //                    new OVFDisk[]{new OVFDisk("base", "/commonpool/smendoza/interoperability/centos_15g_x86_64.qcow2", 0L),
                //                                    new OVFDisk("context", "/commonpool/smendoza/interoperability/testingContestIso.iso", 0L)},
                new OVFNetwork[]{},
                null);//Additional properties.

        EmotiveOVF ovfDomEmo = new EmotiveOVF(ovfDom);
        ovfDomEmo.setProductProperty(EmotiveOVF.PROPERTYNAME_VM_NAME, "VMManagerInteroperabilityTestVM");
        ovfDomEmo.getNetworks().put("public", new OVFNetwork("public", null, null, null, null));

        ovf = ovfDom.toCleanString();

        return ovf;

    }

    /**
     * Returns de flavor ID of the VM instance
     *
     * @param vmID
     * @return
     */
    public String getFlavor(String vmID) {

        String apiAddress = ":8774/v2/" + OS_TENANT_ID + "/servers/" + vmID;
        String flavorID = null;

        //Instantiate an HttpClient
        HttpClient client = new DefaultHttpClient();
        try {
            //Instantiate a GET HTTP method
            HttpGet request = new HttpGet(drpHost + apiAddress);
            request.setHeader("X-Auth-Token", this.token);

            HttpResponse response = client.execute(request);
            String responseContent = IOUtils.toString(response.getEntity().getContent());

            // Parse the deailed server to get the flavor ID
            JSONObject jo = (JSONObject) JSONValue.parse(responseContent);
            jo = (JSONObject) jo.get("server");
            jo = (JSONObject) jo.get("flavor");
            flavorID = jo.get("id").toString();

        } catch (IOException ex) {
            throw new DRPOSTException("Exception getting the Flavor ID", StatusCodes.BAD_REQUEST);

        }
        return flavorID;
    }

    public boolean deleteVM(String vmID) {
        String apiAddress = ":8774/v2/" + OS_TENANT_ID + "/servers/" + vmID;
        boolean requestAccepted = false;

        //Instantiate an HttpClient
        HttpClient client = new DefaultHttpClient();
        try {
            // Have to get VM flavor's before deleting the VM
            String flavorID = getFlavor(vmID);
            //Instantiate a DELETE HTTP method
            HttpDelete request = new HttpDelete(drpHost + apiAddress);
            request.setHeader("X-Auth-Token", this.token);

            //Openstack API does not return a response body!
            HttpResponse response = client.execute(request);
            String statusCode = String.valueOf(response.getStatusLine().getStatusCode());
            if (statusCode.startsWith("2")) {
                requestAccepted = true;
            }

            System.out.println("DELETE response.getStatusLine().getStatusCode()=" + response.getStatusLine().getStatusCode());

            // After killing the VM...
            // If there is not any active VM using this flavor, delete flavor
            // Else, do nothing
            if (activeInstancesWithFlavor(flavorID) == 0) {
                deleteFlavor(flavorID);
            }

        } catch (IOException ex) {
            throw new DRPOSTException("IO Exception Deleting the VM", StatusCodes.BAD_REQUEST);
        } catch (Exception e) {
            throw new DRPOSTException("Exception Deleting the VM", StatusCodes.BAD_REQUEST);
        }
        return requestAccepted;
    }

    public void deleteFlavor(String flavorID) {
        String apiAddress = ":8774/v2/" + this.OS_TENANT_ID + "/flavors/" + flavorID;

        //Instantiate an HttpClient
        HttpClient client = new DefaultHttpClient();
        try {
            //Instantiate a DELETE HTTP method
            HttpDelete request = new HttpDelete(this.drpHost + apiAddress);
            request.setHeader("X-Auth-Token", this.token);
            //Execute the request, obtain a Response
            HttpResponse response = client.execute(request);

            //If the response is not OK, throw an exception
            if (response.getStatusLine().getStatusCode() != 202) {
                throw new DRPOSTException(response.getStatusLine().getReasonPhrase(), StatusCodes.BAD_REQUEST);
            }
        } catch (IOException ex) {
            throw new DRPOSTException("Exception deleting the Flavor", StatusCodes.BAD_REQUEST);
        }
    }

    /**
     * Start a host with the name passed as argument.
     *
     * @param hostName
     */
    public void startup(String hostName) {
//        v2/{tenant_id}/os-hosts/{host_name}/startup
        String apiAddress = ":8774/v2/" + OS_TENANT_ID + "/os-hosts/" + hostName + "/startup";

        //Instantiate an HttpClient
        HttpClient client = new DefaultHttpClient();
        try {
            //Instantiate a GET HTTP method
            HttpGet request = new HttpGet(this.drpHost + apiAddress);
            request.setHeader("X-Auth-Token", this.token);

            //Execute the request, obtain a Response
            HttpResponse response = client.execute(request);

            //If the response is not OK, throw an exception
            if (response.getStatusLine().getStatusCode() != 200) {
                throw new DRPOSTException(response.getStatusLine().getReasonPhrase(), StatusCodes.BAD_REQUEST);
            }

            //Get the Response Content in a String
            String responseContent = IOUtils.toString(response.getEntity().getContent());

        } catch (IOException ex) {
            throw new DRPOSTException("Exception getting all images", StatusCodes.BAD_REQUEST);
        }

    }

    /**
     * Returns the Status of the Image with name passed as argument.
     *
     * @param name
     * @return
     */
    public String getImageStatus(String imageId) {
        String status = null;
        String images = this.getAllImages();

        System.out.println("imageId: " + imageId + ", getImageStatus(): \n" + images);

        // Parsing the JSON response to get all the images
        JSONObject allImages = (JSONObject) JSONValue.parse(images);
        JSONArray jsonImages = (JSONArray) allImages.get("images");

        for (int i = 0; i < jsonImages.size(); ++i) {
            JSONObject image = (JSONObject) jsonImages.get(i);
            String tmpImgID = image.get("id").toString();
            if (tmpImgID.equals(imageId)) {
                System.out.println("found: \n" + tmpImgID);
                status = image.get("status").toString();
                break;
            }
        }
        return status;
    }

    /**
     * Returns the ID of the Image with name passed as argument.
     *
     * @param name
     * @return
     */
    public String getImageID(String name) {
        String id = null;
        String images = this.getAllImages();
        // Parsing the JSON response to get all the images
        JSONObject allFlavors = (JSONObject) JSONValue.parse(images);
        JSONArray jsonFlavors = (JSONArray) allFlavors.get("images");

        for (int i = 0; i < jsonFlavors.size(); ++i) {
            JSONObject image = (JSONObject) jsonFlavors.get(i);
            String tmpImgID = image.get("name").toString();
            if (tmpImgID.equals(name)) {
                id = image.get("id").toString();
                break;
            }
        }
        return id;
    }

    /**
     * Shut down the host with the name passed as argument.
     *
     * @param hostName
     */
    public void shutdown(String hostName) {

        String apiAddress = ":8774/v2/" + OS_TENANT_ID + "/os-hosts/" + hostName + "/shutdown";

        //Instantiate an HttpClient
        HttpClient client = new DefaultHttpClient();
        try {
            //Instantiate a GET HTTP method
            HttpGet request = new HttpGet(this.drpHost + apiAddress);
            request.setHeader("X-Auth-Token", this.token);

            //Execute the request, obtain a Response
            HttpResponse response = client.execute(request);

            //If the response is not OK, throw an exception
            if (response.getStatusLine().getStatusCode() != 200) {
                throw new DRPOSTException(response.getStatusLine().getReasonPhrase(), StatusCodes.BAD_REQUEST);
            }

            //Get the Response Content in a String
            String responseContent = IOUtils.toString(response.getEntity().getContent());

        } catch (IOException ex) {
            throw new DRPOSTException("Exception getting all images", StatusCodes.BAD_REQUEST);
        }

    }

    /**
     * Returns the numbers of all servers in ACTIVE state that are using the
     * flavorID.
     *
     * @param envid
     * @return
     */
    public int activeInstancesWithFlavor(String flavorID) {
        HashMap options = new HashMap();
        options.put("flavor", flavorID);
        options.put("status", "ACTIVE");

        String servers = getAllServers(options);

        JSONObject allServers = (JSONObject) JSONValue.parse(servers);
        JSONArray jsonServers = (JSONArray) allServers.get("servers");

        return jsonServers.size();
    }

    public int activeInstancesWithImage(String imageID) {
        HashMap options = new HashMap();
        options.put("image", imageID);
        options.put("status", "ACTIVE");

        String servers = getAllServers(options);
//        System.out.println("OStackClient.activeInstancesWithImage() > getAllServers(options)=\n"+servers);

        JSONObject allServers = (JSONObject) JSONValue.parse(servers);
        JSONArray jsonServers = (JSONArray) allServers.get("servers");

        return jsonServers.size();
    }

    public boolean existsImage(String imageID) {
        boolean exists = false;

        //Get the Response Content in a String
        String images = this.getAllImages();

        // Parsing the JSON response to get all the images
        JSONObject allFlavors = (JSONObject) JSONValue.parse(images);
        JSONArray jsonFlavors = (JSONArray) allFlavors.get("images");

        for (int i = 0; i < jsonFlavors.size(); ++i) {
            JSONObject image = (JSONObject) jsonFlavors.get(i);
            String tmpImgID = image.get("id").toString();
            if (tmpImgID.equals(imageID)) {
                exists = true;
                break;
            }
        }
        return exists;
    }

    public boolean existsFlavor(String name) {
        boolean exists = false;

        //Get the Response Content in a String
        String flavors = this.getAllFlavors();

        // Parsing the JSON response to get all the flavors
        JSONObject allFlavors = (JSONObject) JSONValue.parse(flavors);
        JSONArray jsonFlavors = (JSONArray) allFlavors.get("flavors");

        for (int i = 0; i < jsonFlavors.size(); ++i) {
            JSONObject flavor = (JSONObject) jsonFlavors.get(i);
            String flavorID = flavor.get("id").toString();
            if (flavorID.equals(name)) {
                exists = true;
                break;
            }
        }
        return exists;
    }

    /**
     * Returns the ID of the Flavor with name passed as argument.
     *
     * @param name
     * @return
     */
    private String getFlavorID(String name) {
        String id = null;
        String flavors = this.getAllFlavors();

        System.out.println("DRP4OST - getFlavorID> name: " + name);

        // Parsing the JSON response to get all the images
        JSONObject allFlavors = (JSONObject) JSONValue.parse(flavors);
        JSONArray jsonFlavors = (JSONArray) allFlavors.get("flavors");

        System.out.println("DRP4OST - getFlavorID> all flavors in JSON: " + flavors);

        for (int i = 0; i < jsonFlavors.size(); ++i) {
            JSONObject image = (JSONObject) jsonFlavors.get(i);
            String tmpImgID = image.get("name").toString();
            if (tmpImgID.equals(name)) {
                id = image.get("id").toString();
                break;
            }
        }
        return id;
    }

    /**
     * Converts InputStream to String
     *
     * @param is
     * @return
     */
    private String ISToString(InputStream is) throws Exception {

        byte[] buffer = new byte[1024];
        int read = 0;
        String str = new String();
        while ((read = is.read(buffer)) != -1) {
            str += new String(buffer);
        }
        return str;
    }

    public String getVMStatus(String serverId) {
        String status = null;

        String osResponse = this.getServer(serverId);

        // Parsing the JSON response to get all the flavors
        JSONObject serverInfo = (JSONObject) JSONValue.parse(osResponse);
        JSONObject jsonServer = (JSONObject) serverInfo.get("server");
        status = jsonServer.get("status").toString();

        return status;
    }

    /**
     * Extracts the VM id from the command "nova boot ..." output
     *
     * @param in output of the VM creation by the nova boot output
     * @return id from the VM
     */
    private String getIdFromNova(String novaBootOutput) {
        String cnst = "|id|";
        String id = null;

        BufferedReader reader = new BufferedReader(new StringReader(novaBootOutput));
        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                String line_trimmed = line.replaceAll("\\s", "");
                if (line_trimmed.contains(cnst)) {
                    line = line.replaceAll("\\s", "");
                    String[] cols = line.split("[|]");
                    id = cols[2];
                    return id;
                }
            }

        } catch (IOException e) {
            System.out.println("DRP4OST-oStackClient().getIdFromNova()> Exception looking for the VM ID");
            e.printStackTrace();
        }

        return id;
    }

    public String context() {
//        return (new oStackClient()).getClass().getClassLoader().getResource("/");
        return null;
    }

    /**
     * Returns the image ID being used by the VM passed as parameter
     *
     * @param vmID identifier of the VM
     * @return ID of the image used by this VM
     */
    public String getServerImage(String vmID) {
        String imageID = null;
        String jsonResponse = this.getServer(vmID);

        // Parsing the JSON response to get all the flavors
        JSONObject serverInfo = (JSONObject) JSONValue.parse(jsonResponse);
        JSONObject jsonServer = (JSONObject) serverInfo.get("server");
        JSONObject jsonImage = (JSONObject) jsonServer.get("image");
        imageID = jsonImage.get("id").toString();
        return imageID;
    }

    public void deleteImage(String imageID) {
        String apiAddress = ":8774/v2/" + this.OS_TENANT_ID + "/images/" + imageID;

        //Instantiate an HttpClient
        HttpClient client = new DefaultHttpClient();
        try {
            //Instantiate a DELETE HTTP method
            HttpDelete request = new HttpDelete(this.drpHost + apiAddress);
            request.setHeader("X-Auth-Token", this.token);
            //Execute the request, obtain a Response
            HttpResponse response = client.execute(request);

            //If the response is not OK, throw an exception
            if (response.getStatusLine().getStatusCode() != 202) {
                throw new DRPOSTException(response.getStatusLine().getReasonPhrase(), StatusCodes.BAD_REQUEST);
            }
        } catch (IOException ex) {
            throw new DRPOSTException("Exception deleting the Image " + imageID, StatusCodes.BAD_REQUEST);
        }

    }
}
