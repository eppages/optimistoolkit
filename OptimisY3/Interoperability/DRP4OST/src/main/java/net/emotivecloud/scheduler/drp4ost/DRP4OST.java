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

import com.sun.jersey.spi.resource.Singleton;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.xml.bind.JAXBException;
import javax.xml.bind.MarshalException;
import javax.xml.bind.PropertyException;
import javax.xml.bind.UnmarshalException;
import javax.xml.bind.ValidationException;
import net.emotivecloud.commons.ListStrings;
import net.emotivecloud.utils.ovf.EmotiveOVF;
import net.emotivecloud.utils.ovf.OVFAux;
import net.emotivecloud.utils.ovf.OVFDisk;
import net.emotivecloud.utils.ovf.OVFException;
import net.emotivecloud.utils.ovf.OVFNetwork;
import net.emotivecloud.utils.ovf.OVFWrapper;
import net.emotivecloud.utils.ovf.OVFWrapperFactory;
import org.apache.commons.io.FileUtils;
import org.dmtf.schemas.ovf.envelope._1.ProductSectionType;
import org.dmtf.schemas.ovf.envelope._1.SectionType;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

/**
 * *************************************************************************
 * Este es el componente con el cual la capa de Gestión de Cloud Federada *
 * interactúa directamente. Se trata de un interfaz REST. La implementación* que
 * se hará será mediante el protocolo HTTP y los métodos GET, POST, PUT* y
 * DELETE. *
 *
 * ------------------------------------------------------------------------*
 * Skeleton reused by ENG with BSC fine OVFWrapper to use OpenNebula OCA *
 * ************************************************************************
 */
/**
 * This is the OCCI API interface with whom the VMManager interacts with
 * OpenStack. It is an extension of the REST OCCI API which transforms the http
 * requests to http requests that OpenNebula can execute.
 *
 * REST Interface:
 *
 * @GET
 * @Path("/compute"):
 * @GET
 * @Path("/compute/{envid}"):
 * @GET
 * @Path("/compute/all"):
 * @GET
 * @Path("/environments/{envid}/status"):
 * @GET
 * @Path("/environments/{envid}/{taskid}"):
 *
 * @POST /compute:
 *
 * @DELETE
 * @Path("/compute/{envid}"):
 *
 * @author
 */
@Path("/")
@Singleton
/**
 * Class
 * <code>DRP4OST</code> the class providing the REST service implementation
 *
 * @author smendoza
 */
public class DRP4OST { // implements Closeable {

    private OStackClient oStackClient = null;
    private String tmpPath = "/tmp/";

    public DRP4OST() {
        oStackClient = new OStackClient();
    }

    @GET
    @Path("/info")
    @Produces("text/plain")
    public String info() {
        return "DRP is running";
    }

    @GET
    @Path("/mergetest")
    public void merge() {
        String isoImage = "/home/smendoza/contextiso.iso.test";
        String qcow2Image = "/home/smendoza/interoperabilityTest.qcow2.test";
        System.out.println("DRP4OST-merge()> isoImage=" + isoImage + ", qcow2Image=" + qcow2Image);
        ImageMerge.merge(isoImage, qcow2Image);
    }

    /**
     * *************************************************************************
     * Methods with a correspondence in the OCCI interface. *
     * ************************************************************************
     */
    /**
     * Method to manage the get to http://host/ovf4one/compute/[envid], that
     * retrieves data about the given VM instance
     *
     * @param envId a <code>String</code> the ID of the VM instance we are
     * interested in
     * @param request an <code>HttpServletRequest</code> with the full http
     * request
     * @return a <code>String</code> an OVF file with the required informations
     * @exception DRPOneException If something goes wrong, what else ?
     *
     */
    /**
     * Creates a VM on OpenStack with the information received from the *.ovf
     * content received attached to the POST request. Returns an OVF with the VM
     * launched
     *
     * @param ovfXml
     * @return String containin an OVF input with the VM instance ID
     * @throws DRPOSTException
     */
    @POST
    @Path("/compute")
    @Consumes("application/xml")
    @Produces("application/xml")
    public String createCompute(String ovfXml) throws DRPOSTException {

        HashMap<String, String> hmap = new HashMap<String, String>();

        String vmName = null;
        String vmID = null;

        System.out.println("**********************************************");
        System.out.println("**********************************************");
        System.out.println("**********************************************");
        System.out.println("THE OVF: \n" + ovfXml);
        System.out.println("**********************************************");
        System.out.println("**********************************************");
        System.out.println("**********************************************");


        //(0) Parse the info from the *.ovf
        if (ovfXml == null) {
            throw new DRPOSTException("\nFailed createCompute method!!\n."
                    + "\nThe ovf xml is" + ovfXml
                    + "\n.Please enter correct xml ovf and try again.\n",
                    StatusCodes.BAD_OVF);
        }
        OVFWrapper ovf;
        try {
            ovf = parse(ovfXml);
        } catch (DRPOSTException e) {
            // This one is expectet to be thrown, and should pass unchanged
            throw e;
        } catch (Exception e) {
            throw new DRPOSTException(
                    "\nFailed createCompute method!!\n"
                    + "An error occured to parse ovfXml.\n The parse of ovfxml is "
                    + e.getCause()
                    + "\n.Please enter correct xml ovf anf try again.\n",
                    StatusCodes.BAD_OVF);

        }
        EmotiveOVF emotiveOvf = new EmotiveOVF(ovf);
        List<SectionType> sections = OVFAux.findSections(emotiveOvf
                .getOVFEnvelope().getSection(), ProductSectionType.class);
        ProductSectionType ps = null;
        for (SectionType s : sections) {
            if (s instanceof ProductSectionType
                    && ((ProductSectionType) s).getClazz().equals(
                    OVFWrapper.class.getName())) {
                ps = (ProductSectionType) s;
                break;

            }
        }
        if (ps != null) {
            List cop = ps.getCategoryOrProperty();
            ProductSectionType.Property propertyToRemove = null;
            for (Object prop : cop) {
                if (prop instanceof ProductSectionType.Property) {
                    ProductSectionType.Property p = (ProductSectionType.Property) prop;
                    hmap.put(p.getKey(), p.getValueAttribute());
                }
            }
        }

        //(1) we must create an Image for the VM

        //(1.1) Merge ISO & QCOW2
        Collection<OVFDisk> tmpDisks = ovf.getDisks().values();
        OVFDisk oDisks[] = tmpDisks.toArray(new OVFDisk[tmpDisks.size()]);
        String baseImageID = emotiveOvf.getBaseImage();
        String pathLocalIso = null;
        String pathLocalBaseImage = null;
        OVFDisk baseDisk = null;
        boolean isoFound = false;
        boolean baseFound = false;

        String name = emotiveOvf.getId();

        for (OVFDisk disk : oDisks) {

            String path = disk.getHref();

            System.out.println("DRP4OST>createCompute()Disk info- disk.getId()=" + disk.getId() + ", disk.getHref()=" + disk.getHref());
            System.out.println("DRP4OST>createCompute()Disk info- disk.getHref()=" + disk.getHref());

            if (path.trim().endsWith(".iso")) {
                pathLocalIso = this.tmpPath + getFileNameFromPath(path);
                downloadImage(path, pathLocalIso);
                isoFound = true;
                System.out.println("DRP4OST>createCompute()Found ISO to merge isoPath=" + pathLocalIso);
            } else if (path.contains(baseImageID)) {
                pathLocalBaseImage = this.tmpPath + getFileNameFromPath(path);
                downloadImage(path, pathLocalBaseImage);
                baseDisk = disk;
                baseFound = true;
                System.out.println("DRP4OST>createCompute()Found baseImage to merge baseImagePath=" + pathLocalBaseImage);
            } else {
                // if not base, and not iso, leave the images
                System.out.println("DRP4OST>createCompute(), This disk will be ignored (suposed to use just QCOW2 and ISO)");
            }
        }

        // Verify that merge is possible
        if (isoFound && baseFound) {
            ImageMerge.merge(pathLocalIso, pathLocalBaseImage);
        } else {
            System.out.println("DRP4OST>createCompute() -> ISO or BASE missing: isoFound=" + isoFound + ", baseFound=" + baseFound);
        }

        //(1.2) Create the baseImage at the OpenStack repository (if iso found, it has been merged)
        if (!existsImage(getFileNameFromPath(baseDisk.getHref()))) {
            baseImageID = createImage(baseDisk, pathLocalBaseImage);
        } else {
            System.out.println("DRP4OST>CREATE IMAGE: NO (ALREADY exist), baseDisk.getId()=" + baseDisk.getId());
            //TODO: get image ID
        }
        
        //Remove the downloaded images, after merged and uploaded to OpenStack
        FileUtils.deleteQuietly(new File(pathLocalBaseImage));
        FileUtils.deleteQuietly(new File(pathLocalIso));

        //(2) we must create a flavor for the VM
        String flavorID = createFlavor(emotiveOvf);

        //(3) we must create VM with Flavor & Image created at sections (1)&(2)
        vmName = ovf.getId(); // + "_" + (new Random()).nextInt(99999999);

        String host = emotiveOvf.getProductProperty(EmotiveOVF.PROPERTYNAME_DESTINATION_HOST);

        if (host != null) {
            String response = oStackClient.createVM(flavorID, baseImageID, vmName, host);
            // Parsing the JSON response to get the flavor ID
            JSONObject jo = (JSONObject) JSONValue.parse(response);
            jo = (JSONObject) jo.get("server");
            vmID = jo.get("id").toString();
        } else {
            String response = oStackClient.createVM(flavorID, baseImageID, vmName);
            JSONObject jo = (JSONObject) JSONValue.parse(response);
            jo = (JSONObject) jo.get("server");
            vmID = jo.get("id").toString();
        }
        System.out.println("DRP4OST>DRP4OST.createImage(), VM created vmID=" + vmID);

        // get the network properties
//        Collection<OVFNetwork> tmpNets = ovf.getNetworks().values();
//        OVFNetwork nets[] = tmpNets.toArray(new OVFNetwork[tmpNets.size()]);

//        public OVFNetwork(String connectionName, String ip, String mac, String netmask, String gateway)
        OVFNetwork nets[] = this.getNetworks(vmID);

        // Create the ovf
        String xx = OVFWrapperFactory.create(vmID,
                ovf.getCPUsNumber(), ovf.getMemoryMB(), oDisks, nets, hmap)
                .toCleanString();

        return xx;
    }

    @POST
    @Path("/compute/{vmId}/migrate")
    @Produces("application/xml")
    public String migrate(@PathParam("vmId") String vmId,
            @QueryParam("dstHost") String dstHost) throws DRPOSTException {

        System.out.println("DRP4OST: VMID: " + vmId);
        System.out.println("DRP4OST: DSTHOST: " + dstHost);
//        int intVmId = Integer.parseInt(vmId);
//        int intDstHost = Integer.parseInt(dstHost);
//        intVmId = 4;
//        intDstHost = 6;
        oStackClient.migrate(vmId, dstHost);
        // Create the ovf
//        String xx = OVFWrapperFactory.create(String.valueOf(baseImageID),
//                ovf.getCPUsNumber(), ovf.getMemoryMB(), oDisks, nets, hmap)
//                .toCleanString();
//
//        return xx;
        return "el retorno";
    }

    @GET
    @Path("/compute/{envid}")
    @Produces("application/xml")
    public String getCompute(@PathParam("envid") String envId) throws DRPOSTException {

        String vmDetails = null;

        vmDetails = oStackClient.getServerAsOVF(envId);

        return vmDetails;
    }

    /**
     *
     * @return A ListStrings object containing the vmIDs of the VMs launched by
     * the user
     */
    @GET
    @Path("/compute")
    @Produces("application/xml")
    public ListStrings getComputes() {

        ArrayList<String> allVMsIDs = oStackClient.getAllVMsIDs();
        ListStrings vmsIDsList = new ListStrings();
        vmsIDsList.addAll(allVMsIDs);

        return vmsIDsList;

    }

    /**
     * <code>deleteCompute</code> Kills an instance of a VM
     *
     * @param envid a <code>String</code> the id of the VM to kill
     * @exception DRPOSTException when something goes wrong.
     */
    @DELETE
    @Path("/compute/{envid}")
    public void deleteCompute(@PathParam("envid") String envId) throws DRPOSTException {

        String imageId = oStackClient.getServerImage(envId);

        int nactive = oStackClient.activeInstancesWithImage(imageId);
        boolean deleteAccepted = oStackClient.deleteVM(envId);

        //If the delete request was accepted and it was the last VM, proceed deleting the Image
        if (deleteAccepted && nactive == 1) {
            System.out.println("DRP4OST-deleteCompute()> DELETING image=" + imageId + ", oStackClient.activeInstancesWithImage("+imageId+")=" + oStackClient.activeInstancesWithImage(imageId));
            oStackClient.deleteImage(imageId);
        } else {
            System.out.println("DRP4OST-deleteCompute()> NOT DELETING image=" + imageId + ", oStackClient.activeInstancesWithImage("+imageId+")=" + oStackClient.activeInstancesWithImage(imageId));
        }

    }

    /**
     * *************************COMPUTE methods.******************************
     */
    /**
     * Get a List with all the running environments.
     *
     * @return A ListStrings object containing a List with a set of OVFWrapper
     * objects expressed in XML, as Strings (using OVFWrapper.toCleanString).
     * They can be converted back to OVFWrapper objects using
     * OVFWrapperFactory.parse(String ovfxml).
     */
    @GET
    @Path("/compute/all")
    @Produces("application/xml")
    public ListStrings getAllEnvironments() {

        OVFWrapper[] w = this.getAllServersWrappers();
        ListStrings ret = new ListStrings();

        for (OVFWrapper ovf : w) {
            ret.add(ovf.toCleanString());
        }

        return ret;
    }

    @GET
    @Path("/environments/{envid}/status") //antic state, falta el destroy per arreglar, al VtM ho fa be
    @Produces("text/plain")
    public String getState(@PathParam("envid") String envId) {
        String status = "Unknown";

        try {
            // Status receives the OpenStack STATUS, we should transform to the equivalent in EMOTIVE
            // cause is the unique understood by the DRP4OST clients
            status = oStackClient.getVMStatus(envId);
        } catch (Exception e) {
            throw new DRPOSTException("Exception getting VM status", StatusCodes.BAD_REQUEST);
        }

        return status;
    }

    /**
     * *********************NODE MANAGEMENT methods.**************************
     */
    @GET
    @Path("/")
    @Produces("text/plain")
    public String getLocation(@QueryParam("id") String id) {
        String ret = "";
        if (id != null) {
            try {
//                ret = super.getLocation(id, SecureSessionInfo.getUserDN(req));
//            } catch (VRMMSchedulerException e) { //catch (ItemNotFoundException e) {
//                e.printStackTrace();
//                if (e.getMessage().contains("cannot be found in any node")) {
//                    throw new WebApplicationException(e, 426);
//                } else if (e.getMessage().contains("VirtMonitor")) {//REPAIR:VirtMonitor: getDomainId error.
//                    throw new WebApplicationException(e, 420);
//                } else if (e.getMessage().contains("Not enough resources")) {//REPAIR:INFO: Not enough resources: Memory
//                    throw new WebApplicationException(e, 425);
//                } else if (e.getMessage().contains("VM does not exist")) {//REPAIR:INFO: Not enough resources: Memory
//                    throw new WebApplicationException(e, 427);
//                } else if (e.getMessage().contains("No available nodes")) {
//                    throw new WebApplicationException(e, 428);
//                } else if (e.getMessage().contains("Cannot connect with the Scheduler ")) {
//                    throw new WebApplicationException(e, 429);
//                } else if (e.getMessage().contains("Cannot connect with Simple Scheduler")) {
//                    throw new WebApplicationException(e, 430);
//                } else if (e.getMessage().contains("Cannot connect with Hadoop Scheduler")) {
//                    throw new WebApplicationException(e, 431);
//                } else if (e.getMessage().contains("Cannot recognize address ")) {
//                    throw new WebApplicationException(e, 432);
//                } else if (e.getMessage().contains("is not in any VM")) {
//                    throw new WebApplicationException(e, 433);
//                } else if (e.getMessage().contains("cannot be found in any node")) {
//                    throw new WebApplicationException(e, 434);
//                } else {
//                    throw new WebApplicationException(e, 424);
//                }
//            }
            } catch (Exception ex) {
                ex.printStackTrace();
                throw new DRPOSTException("Exception getting Location", StatusCodes.BAD_REQUEST);
            }
        }
        return ret;
    }

    @GET
    @Path("/resources")
    @Produces("application/xml")
    public ListStrings getNodes() {
        ListStrings list = new ListStrings();
        ArrayList<String> hostsNames = new ArrayList<String>();

        String hosts = oStackClient.getHosts();
        JSONObject jsonHosts = (JSONObject) JSONValue.parse(hosts);
        JSONArray jsonHostsArray = (JSONArray) jsonHosts.get("hosts");

        for (int i = 0; i < jsonHostsArray.size(); ++i) {
            JSONObject flavor = (JSONObject) jsonHostsArray.get(i);
            String hostName = flavor.get("host_name").toString();
            hostsNames.add(hostName);
        }

        list.addAll(hostsNames);

//        list.addAll(super.getNodes());
        //GenericEntity<ListStrings> entity = new GenericEntity<ListStrings>(list) {};
        //Response response = Response.ok(entity).build();
        return list;
    }

    @POST
    @Path("/resources")
    public void nodeUp(@QueryParam("node") String nodeId) {
//        super.nodeUp(nodeId);
        oStackClient.startup(nodeId);

    }

    @DELETE
    @Path("/resources")
    public void nodeDown(@QueryParam("node") String node,
            @QueryParam("cause") String type) {
//        super.nodeDown(node, type);
        oStackClient.shutdown(node);
    }

//    public String getLocation(String id) throws VRMMSchedulerException {
//        return getLocation(id, (String) null);
//    }
    /*
     * *************************************************************************
     * Functions to help OCCI Interface
     * *************************************************************************
     */
    /**
     *
     * @param username
     * @param psswd
     * @param projectName
     * @return
     */
    private String getToken(String username, String psswd, String projectName) {

        return oStackClient.getToken(username, psswd, projectName);

    }

    /**
     * Returns the private IPs associated to the server ID
     *
     * @param vmID
     * @return
     */
    public String[] getIPs(String serverID) {

        ArrayList<String> ips = new ArrayList<String>();
        String serverDetails = null;
        String status = "UNKNOWN";

        // Until VM has ACTIVE status, no IP is assigned
        while (!status.equals("ACTIVE")) {
            serverDetails = oStackClient.getServer(serverID);
            status = oStackClient.getVMStatus(serverID);
            System.out.println("DRP4OST>DRP4OST.getIPs(), VM has status=" + status);
            try {
                Thread.sleep(5000);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // Parsing the JSON response to get all the images
        JSONObject allFlavors = (JSONObject) JSONValue.parse(serverDetails);
        JSONObject jsonServer = (JSONObject) allFlavors.get("server");
        JSONObject jsonAddresses = (JSONObject) jsonServer.get("addresses");
        JSONArray jsonPrivate = (JSONArray) jsonAddresses.get("private");

        for (int i = 0; i < jsonPrivate.size(); ++i) {
            JSONObject addr = (JSONObject) jsonPrivate.get(i);
            String tmpAddr = addr.get("addr").toString();
            ips.add(tmpAddr);
            System.out.println("DRP4OST>DRP4OST.getIPs(), VM has ip=" + tmpAddr);
        }

        String[] ipsArray = ips.toArray(new String[ips.size()]);
        return ipsArray;

    }

    private OVFNetwork[] getNetworks(String serverID) {

        String[] ips = this.getIPs(serverID);

        OVFNetwork[] nets = new OVFNetwork[ips.length];

        for (int i = 0; i < ips.length; ++i) {
            //public OVFNetwork(String connectionName, String ip, String mac, String netmask, String gateway)
//            nets[i] = new OVFNetwork(null, ips[i], null, null, null);
            nets[i] = new OVFNetwork("public", ips[i], null, null, null);
        }

        return nets;
    }

//    //        public OVFNetwork(String connectionName, String ip, String mac, String netmask, String gateway)
//    private OVFNetwork getNetworkDetails(String label) {
//        String apiAddress = ":8774/v2/" + projectID + "/os-networks/" + networkID;
//        OVFNetwork net = null;
//        String connectionName = null;
//        String ip = null;
//        String mac = null;
//        String netmask = null;
//        String gateway = null;
//
//        //Instantiate an HttpClient
//        HttpClient client = new DefaultHttpClient();
//        try {
//            //Instantiate a GET HTTP method
//            HttpGet request = new HttpGet(this.serverIP + apiAddress);
//            request.setHeader("X-Auth-Token", this.token);
//
//            //Execute the request, obtain a Response
//            HttpResponse response = client.execute(request);
//
//            //If the response is not OK, throw an exception
//            if (response.getStatusLine().getStatusCode() != 200) {
//                throw new DRPOSTException(response.getStatusLine().getReasonPhrase(), StatusCodes.BAD_REQUEST);
//            }
//
//            //Get the Response Content in a String
//            String responseContent = IOUtils.toString(response.getEntity().getContent());
//            images = responseContent;
//
//        } catch (IOException ex) {
//            throw new DRPOSTException("Exception getting all images", StatusCodes.BAD_REQUEST);
//        }
//
//        return net;
//    }
    // Generates a VM description starting from an ovf disk
    private String createImage(OVFDisk disk, String pathLocalBaseImage) {

        String name = oStackClient.createImage(disk, pathLocalBaseImage);

//        return "e487d76e-ffdd-4356-9c9b-6f6356542262"; // La imagen del cirros por defecto
        return name;
    }

    /**
     * This function creates a Flavor with the extracted information from the
     * *.ovf . To create the Flavor, I'll need disk space (disk), VM ram (ram),
     * number of virtual CPU's (vcpus), Flavor name (name) and flavor
     * identification (name)
     *
     * @param ovf
     * @return ID of the created Flavor
     */
    private String createFlavor(EmotiveOVF ovf) {

        String flavorID = oStackClient.createFlavor(ovf);

        return flavorID;
    }

    private boolean existsImage(String imageID) {
        boolean exists = false;

        //Get the Response Content in a String
        String images = oStackClient.getAllImages();

        // Parsing the JSON response to get all the images
        JSONObject allImages = (JSONObject) JSONValue.parse(images);
        JSONArray jsonImages = (JSONArray) allImages.get("images");

        for (int i = 0; i < jsonImages.size(); ++i) {
            JSONObject image = (JSONObject) jsonImages.get(i);
            String tmpImgID = image.get("name").toString();
            if (tmpImgID.equals(imageID)) {
                exists = true;
                break;
            }
        }
        return exists;
    }

    private OVFWrapper[] getAllServersWrappers() {
        ArrayList<OVFWrapper> wrappers = new ArrayList<OVFWrapper>();
        String servers = oStackClient.getAllServers();

        JSONObject allServers = (JSONObject) JSONValue.parse(servers);
        JSONArray jsonServers = (JSONArray) allServers.get("servers");

        for (int i = 0; i < jsonServers.size(); ++i) {
            JSONObject server = (JSONObject) jsonServers.get(i);
            OVFWrapper wTmp = this.parse(server.toString());
            wrappers.add(wTmp);
        }

        OVFWrapper vms[] = wrappers.toArray(new OVFWrapper[wrappers.size()]);

        return vms;
    }

    private OVFWrapper parse(String ovfXml) {
        OVFWrapper rv = null;
        StringBuilder cause = new StringBuilder();

        try {
            rv = OVFWrapperFactory.parse(ovfXml);
        } catch (JAXBException e) {
            if (e instanceof PropertyException) {
                cause.append("Access to property failed: " + e.getErrorCode());
            } else if (e instanceof MarshalException) {
                cause.append("Marshalling failed: " + e.getLocalizedMessage());
            } else if (e instanceof UnmarshalException) {
                cause.append("Unmarshalling failed: " + e.getCause());
            } else if (e instanceof ValidationException) {
                cause.append("XML Validation failed: " + e.getErrorCode());
            } else {
                cause.append("Unespected " + e.getErrorCode());
                cause.append(e.getClass().getName());
                cause.append(": ");
            }

            cause.append(e.getMessage());

            throw new DRPOSTException(cause.toString(), e, StatusCodes.XML_PROBLEM);
        } catch (OVFException e) {
            cause.append("Problems parsing OVF file: ");
            cause.append(e.getMessage());

            throw new DRPOSTException(cause.toString(), e, StatusCodes.XML_PROBLEM);
        }
        return rv;
    }

    private void downloadImage(String imageURL, String dest) {
        try {
//            System.out.println("DRP4OST.downloadImage()>START Downloading from imageURL=" + imageURL + " to dest=" + dest);
            FileUtils.copyURLToFile(new URL(imageURL), new File(dest));
//            System.out.println("DRP4OST.downloadImage()>FINISH Downloading from imageURL=" + imageURL + " to dest=" + dest);
        } catch (Exception e) {
            System.out.println("DRP4OST.downloadImage()>Exception downloading the file");
            e.printStackTrace();
        }
    }

    private String getFileNameFromPath(String path) {
        String[] pathSplit = path.split("[/]");
        String fileName = pathSplit[pathSplit.length - 1];
        return fileName;

    }
}
