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

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.spi.resource.Singleton;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Scanner;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.xml.bind.JAXBException;
import javax.xml.bind.MarshalException;
import javax.xml.bind.PropertyException;
import javax.xml.bind.UnmarshalException;
import javax.xml.bind.ValidationException;
import javax.xml.parsers.ParserConfigurationException;
import net.emotivecloud.commons.ListStrings;
import net.emotivecloud.scheduler.drp4one.DRP4OVF.TargetedDisk;
import net.emotivecloud.utils.oca.OCAComplexComputeWrapper;
import net.emotivecloud.utils.oca.OCAComputeListWrapper;
import net.emotivecloud.utils.oca.OCAComputeListWrapperFactory;
import net.emotivecloud.utils.oca.OCAComputeWrapper;
import net.emotivecloud.utils.oca.OCAComputeWrapperFactory;
import net.emotivecloud.utils.oca.OCADiskWrapper;
import net.emotivecloud.utils.oca.OCANicWrapper;
import net.emotivecloud.utils.ovf.EmotiveOVF;
import net.emotivecloud.utils.ovf.EmotiveOVF.State;
import net.emotivecloud.utils.ovf.OVFAux;
import net.emotivecloud.utils.ovf.OVFDisk;
import net.emotivecloud.utils.ovf.OVFException;
import net.emotivecloud.utils.ovf.OVFNetwork;
import net.emotivecloud.utils.ovf.OVFWrapper;
import net.emotivecloud.utils.ovf.OVFWrapperFactory;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dmtf.schemas.ovf.envelope._1.ProductSectionType;
import org.dmtf.schemas.ovf.envelope._1.SectionType;
import org.opennebula.client.Client;
import org.opennebula.client.OneResponse;
import org.opennebula.client.vm.VirtualMachine;
import org.opennebula.client.vm.VirtualMachinePool;
import org.xml.sax.SAXException;

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
 * OpenNebula. It is an extension of the REST OCCI API, which transforms the
 * http requests to XML-RPC that OpenNebula can execute.
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
 * <code>DRP4OVF</code> the class providing the REST service implementation
 *
 * @author <a href="mailto:saint@eng.it">Gian Uberto Lauri</a>
 * @version $Revision$
 */
public class DRP4OVF {

    private static final String PROPS_FILE_NAME = "/props_file";
    private static final String VM_BASEIMAGE = "VM.baseimage";
    private static final String NET_CONNECTION_NAMES = "net.connectionNames";
    private static final String[] NET_CONNECTION_PROPS = {"ip", "mac",
        "netmask", "gateway"};
    private static HashMap<String, HashMap<String, String>> PRODUCT_PROPS = new HashMap<String, HashMap<String, String>>();
    @Context
    UriInfo uriInfo;
    private Log log = LogFactory.getLog(DRP4OVF.class);
    public final static String Status = " ,Status Code ";
//    private final static String PROPFILE = "ovf4one.properties";
    private final static String PROPFILE = "/ovf4one.properties";
    List<String> arg1 = new ArrayList<String>();
    List<String> arg2 = new ArrayList<String>();
    String netConnectNames = "";
    List<String> nicNames = new ArrayList<String>();
    boolean weGotKernel = false;
    boolean weGotBootloader = false;
    boolean weGotBoot = false;
    boolean weGotDisk = false;
    boolean weUseKVM = false;
    
    private OneExtraFuncs oneExtraFuncs;
    private String user;
    private String passwd;
    private String rpcserver;
    //Optimis: ONE Datastore info
    private String ONE_DATASTORE_NAME = "optimis";
    private int ONE_DATASTORE_ID = 100;
    /**
     * Creates a new instance of
     * <code>DRP4OVF</code> .
     *
     * Throws an error if the property file is missing the values required to
     * configure the retrieval of the Open Nebula access secret.
     */
    public DRP4OVF() {
        /**
         * comentado x smendoza 08/02/2013
         *
         * String className = ovf4oneProperties(OCACLIENT_CLASS, true);
         *
         * if (className == null) { throw new
         * OpenNebulaConfigurationError(OCACLIENT_CLASS + ": missing a value for
         * the property"); }
         *
         * ocaClientProviderURL = ovf4oneProperties(OCACLIENT_URL, false);
         *
         * if (ocaClientProviderURL == null) { throw new
         * OpenNebulaConfigurationError(OCACLIENT_URL + ": missing a value for
         * the property"); }
         *
         * ocaClientProviderQuery = ovf4oneProperties(OCACLIENT_QUERY, false);
         *
         * if (ocaClientProviderQuery == null) { throw new
         * OpenNebulaConfigurationError(OCACLIENT_QUERY + ": missing a value for
         * the property"); }
         *
         * try { clientProvider = (OCAClientProvider) Class.forName(className)
         * .newInstance(); } catch (InstantiationException e) { throw new
         * OpenNebulaConfigurationError( "\n.Bad OCAClientProvider " + className
         * + ", either missing nullary constructor or suitable for an
         * InstantiationError. Message: " + e.getMessage()); } catch
         * (IllegalAccessException e) { throw new OpenNebulaConfigurationError(
         * "\n.Bad OCAClientProvider, no permission to create the instance of :"
         * + className + ". Message: " + e.getMessage()); } catch
         * (ClassNotFoundException e) { throw new OpenNebulaConfigurationError(
         * "\n.Missing OCAClientProvider, " + className + " in the CLASSPATH.
         * Message: " + e.getMessage()); } catch (ClassCastException e) { throw
         * new OpenNebulaConfigurationError("\n.Bad OCAClientProvider, " +
         * className + " does not extend OCAClientProvvider. Message: " +
         * e.getMessage()); } productPropsFolder =
         * ovf4oneProperties(PRODUCT_PROPS_FOLDER, false); scriptlocation =
         * ovf4oneProperties(SCRIPT_LOCATION, false);
         *
         * sshClientUser = ovf4oneProperties(SSH_USER, false);
         *
         * if (sshClientUser == null) { throw new
         * OpenNebulaConfigurationError(SSH_USER + ": missing a value for the
         * property"); }
         *
         * sshClientPrivKeyPath = ovf4oneProperties(SSH_PRIV_KEY_PATH, false);
         *
         * if (sshClientPrivKeyPath == null) { throw new
         * OpenNebulaConfigurationError(SSH_PRIV_KEY_PATH + ": missing a value
         * for the property"); }
         *
         */
        // Defining the projectID against DRP will operate
        this.oneExtraFuncs = new OneExtraFuncs();
        
        PropertiesConfiguration configDRP4OST = ConfigManager.getPropertiesConfiguration(ConfigManager.DRP4OST_CONFIG_FILE);
        this.user = configDRP4OST.getString("opennebula.access.userid");
        this.passwd = configDRP4OST.getString("opennebula.access.passwd");
        this.rpcserver = configDRP4OST.getString("opennebula.access.rpcserver");
        this.ONE_DATASTORE_ID = configDRP4OST.getInt("opennebula.datastore.id");
        
        System.out.println("this.user="+this.user);
        System.out.println("this.passwd="+this.passwd);
        System.out.println("this.rpcserver="+this.rpcserver);
        System.out.println("this.ONE_DATASTORE_ID="+this.ONE_DATASTORE_ID);
        
    }
    private Properties props;
    private OCAClientProvider clientProvider;
    private String ocaClientProviderURL;
    private String ocaClientProviderQuery;
    private String sshClientUser;
    private String sshClientPrivKeyPath;
    private String scriptlocation;
    private String productPropsFolder;
    public final static String OCACLIENT_CLASS = "OCA.CLIENT.CLASS";
    public final static String OCACLIENT_URL = "OCA.CLIENT.URL";
    public final static String OCACLIENT_QUERY = "OCA.CLIENT.QUERY";
    public final static String SSH_USER = "ssh.user";
    public final static String SSH_PRIV_KEY_PATH = "ssh.priv.key.path";
    public final static String SCRIPT_LOCATION = "script.location";
    public final static String PRODUCT_PROPS_FOLDER = "product.properties.location";

    /*
     * ===============================================================
     * 
     * These constants describe the supported Product Properties We use Product
     * Properties to hold informations meaningful for Opennebula but not
     * supported by OVF
     */
    // Path to the kernel image - mostly unused if you don't use XEN
    public final static String KERNEL = "KERNEL";
    // Path to the initrd image - mostly unused if you don't use XEN
    public final static String INITRD = "INITRD";
    // Device to be mounted as root
    public final static String ROOT = "ROOT";
    // Extra arguments to boot the kernel
    public final static String KERNEL_CMD = "KERNEL_CMD";
    // Path to the bootloader executable - mostly unused if you don't use XEN
    public final static String BOOTLOADER = "BOOTLOADER";
    // Tyoe if the boot device, must be one of those in BootType enum
    public final static String BOOT = "BOOT";
    // Architecture to be emulated. We support i386 (default) and x86_64
    public final static String ARCH = "ARCH";
    public final static String I_386 = "i386";
    public final static String I_686 = "i686";
    public final static String X86_64 = "x86_64";
    // Hypervisor in use - we support KVM and XEN
    public final static String HYPERVISOR = "HYPERVISOR";
    public final static String ONE_XMLRPC = "ONE_XMLRPC";
    private final static String OS_subAttributes[] = new String[]{
        KERNEL, INITRD, ROOT, KERNEL_CMD, BOOTLOADER, BOOT
    };
    // Suffixes to build disk related properties.
    public final static String PROP_PATORURL = ".SOURCE";
    public final static String PROP_TYPE = ".TYPE";
    public final static String PROP_FORMAT = ".FORMAT";
    // This is property file only, nothing in the ovf.
    // Specifies a CLUSTER where deploy the VMS created with
    // ovf
    // Maybe we could replace this with a change in the
    // scheduler
    public final static String CLUSTER = "CLUSTER";
    // This is property file only, nothing in the ovf.
    // Holds the UsageTracker endpoint url
    public final static String USAGE_TRACKER_URL = "usage.tracker.url";
    public final static String INFRASTRUCTURE_ID = "infrastructure.id";
    // This is the protocol part for the pseudo URLs used to access
    // pre-registered images
    public final static String ONE_PROTOCOL = "ovf4one://";
    // check if absolute file path is provided for VM image
    public final static String FILE_PATH_SEPARATOR = "/";
    // The protocol part for the pseudo URLs has a fixed size,
    // compute it a compile/init tim.
    private final static int ONE_PROTOCOL_LEN = ONE_PROTOCOL.length();

    private HashMap<String, Integer> idVsOneId = new HashMap<String, Integer>();


    /*
     * ===============================================================
     * 
     * This enum models the supported types for the BOOT.
     */
    private enum BootType {

        hd("hd"), fd("fd"), cdrom("cdrom"), network("network");

        private BootType(String asString) {
            this.asString = asString;
        }
        private String asString;

        public String toString() {
            return asString;
        }

        public static boolean isValid(String toTest) {
            return hd.asString.equals(toTest) || fd.asString.equals(toTest)
                    || cdrom.asString.equals(toTest)
                    || network.asString.equals(toTest);
        }
    }

    /*
     * ===============================================================
     * 
     * This enum models the supported types for the DISK. We consider virtual
     * CD, floppies and such all being 'disks'
     */
    private enum DskType {

        virtualDisk("disk"), virtualSwap("swap"), blockDevice("block"), onTheFlyDisk(
        "fs");
        private String asString;

        private DskType(String asString) {
            this.asString = asString;
        }

        public String toString() {
            return asString;
        }

        public static DskType fromString(String startingString) {
            return virtualDisk.asString.equals(startingString) ? virtualDisk
                    : virtualSwap.asString.equals(startingString) ? virtualSwap
                    : blockDevice.asString.equals(startingString) ? blockDevice
                    : onTheFlyDisk.asString
                    .equals(startingString) ? onTheFlyDisk
                    : null;
        }
    }

    /**
     * *************************************************************************
     * Methods without a correspondence in the OCCI interface. *
     * ************************************************************************
     */
    @GET
    @Path("/info")
    @Produces("text/plain")
    /**
     * <code>info</code> a methods that simply tells that DRP4OVF is up and
     * ready
     *
     * @return a <code>String</code> The message stating that DRP4OVF is ready
     */
    public String info() {

        return "DRP is running";
    }
//
//    @GET
//    @Path("/")
//    @Produces("application/xml")
//    /**
//     * <code>rootMethod</code> Implementation for the default root method.
//     * Returns an OCA format XML for all the user VMsm, the same of
//     * getAllEnvironments()
//     *
//     * @param request an <code>HttpServletRequest</code> with the full http
//     * request
//     * @return a <code>String</code> the OCA format XML for all the user VMs
//     */
//    public String rootMethod(@Context HttpServletRequest request) {
//        return getAllEnvironments(request);
//    }
//
//    @GET
//    @Path("/environments")
//    @Produces("application/xml")
//    /**
//     * <code>getAllEnvironments</code> Non OCCI standard method. Returns an OCA
//     * format XML for all the user VMs
//     *
//     * @param request an <code>HttpServletRequest</code> with the full http
//     * request
//     *
//     * @return a <code>String</code> the OCA format XML for all the user VMs
//     */
//    public String getAllEnvironments(@Context HttpServletRequest request) {
//
//        Client ocaClient = getClient(request);
//
//        VirtualMachinePool vmpool = null;
//
//        try {
//            vmpool = new VirtualMachinePool(ocaClient);
//
//        } catch (DRPOneException e) {
//            throw new DRPOneException(
//                    "Failed getAllEnviroments method!!"
//                    + " An error occurred in the configuration of virtual machine pool."
//                    + "\n Please, control your opennebula configuration ...."
//                    + "variable vmpool is " + e.getCause() + ".\n",
//                    StatusCodes.BAD_REQUEST);
//
//        }
//        // OneResponse rc = VirtualMachinePool.info(ocaClient, 0);
//        OneResponse rc = vmpool.info();
//        if (rc.isError()) {
//            log.error("Failed to retrieve VMs for user : "
//                    + rc.getErrorMessage() + ".\n");
//            throw new DRPOneException("Failed getAllEnviroments method!!"
//                    + "Failed to retrieve all enviroments for user : "
//                    + rc.getErrorMessage() + ".\n", StatusCodes.ONE_FAILURE);
//        }
//        /*
//         * if(rc.isError()) { log.error("Failed to retrieve VMs for user : " +
//         * rc.getErrorMessage()); throw new
//         * DRPOneException("Failed to retrieve VMs for user : " +
//         * rc.getErrorMessage(),StatusCodes.ONE_FAILURE); }
//         */
//
//        return rc.getMessage();
//
//    }
//
//    @POST
//    @Path("/environments/{envid}")
//    /**
//     * <code>submitActivity</code> Basically, it executes a command in the
//     * machine that is specified as "envid". This is by no means OCCI code but a
//     * BSC specific feature addet do have more interoperability.
//     *
//     * The task is specified as a Job Submission Description Language (JSDL) XML
//     * file. I attach you a jar file that contains a parser for it.
//     *
//     * Use JSCH ( http://www.jcraft.com/jsch/), to transfer files by SCP to the
//     * VM, and to start SSH command sessions for calling them.
//     *
//     * @param envid a <code>String</code> id of the vm you want to use for data
//     * transfer/code execution
//     * @param user a <code>String</code> id of the user on the vm you want to
//     * use for data transfer/code execution
//     * @param back a <code>String</code> flag. if the string contains "false"
//     * (case sensitive), the command is run synchronously. Some remnant of BSC
//     * having a grid ?
//     * @param jsdl a <code>String</code> Description of the command to execute
//     * @return a <code>String</code> the pid of the command
//     */
//    public String submitActivity(@PathParam("envid") String envId,
//            @QueryParam("user") String user,
//            @QueryParam("background") String back,
//            @QueryParam("jsdl") String jsdl, @Context HttpServletRequest request)
//            throws DRPOneException {
//
//        /*
//         * First of all, get the VM description from ONE and find where it does
//         * answer on port 22
//         */
//
//        OneResponse rc = getComputeHelper(envId, request);
//
//        OCAComputeWrapper oca = rc2OCA(rc);
//
//        String userToUse = (user == null || "".equals(user.trim())) ? sshClientUser
//                : user;
//
//        Collection<OCANicWrapper> nicList = oca.getTemplate().getNics()
//                .values();
//
//        if (!(oca.getState() == VmStatusCodes.ACTIVE && oca.getLcmState() == LcmStatusCodes.RUNNING)) {
//            throw new DRPOneException("\nFailed getCompute method!!\n"
//                    + "VM id " + envId + " not ready to accept commands.\n .",
//                    StatusCodes.VM_NOT_READY);
//        }
//
//        String ipAddress = null;
//        for (OCANicWrapper nic : nicList) {
//            ipAddress = nic.getIp();
//            if (ipAddress != null) {
//                if (port22Reached(ipAddress)) {
//                    break;
//                }
//            }
//
//        }
//
//        String rv = "";
//        if (ipAddress == null) {
//            throw new DRPOneException("\nFailed getCompute method!!\n"
//                    + "VM id " + envId + " not responding on port 22.\n .",
//                    StatusCodes.VM_NOT_RESPONDING);
//        }
//
//        // We have the IP the machine has something listening on port 22
//        /*
//         * comentado x smendoza 08/02/2013
//         * 
//        if (!jsdl.startsWith("<?xml")) { // Ci possono passare il
//            // comando oppure un JSDL, nel
//            // qual caso comincia con <?xml
//            // e noi facciamo il simpatico
//            // parsing.
//            JSDL jsdlaux = new JSDL();
//            jsdlaux.setCommand(jsdl);
//
//            jsdl = jsdlaux.toString();
//
//            SSH ssh = new SSH(userToUse, sshClientPrivKeyPath, ipAddress);
//            ssh.deploy();
//            rv = ssh.execute(jsdlaux);
//        }
//        */
//        // System.out.println("--Final Command--"+jsdl+"----");
//
//        return rv;
//    }

    /**
     * *************************************************************************
     * Methods with a correspondence in the OCCI interface. *
     * ************************************************************************
     */
    @POST
    @Path("/compute")
    @Consumes("application/xml")
    @Produces("application/xml")
    /**
     * Method that managers the post to http://host/ovf4one/compute. This
     * creates a vm instance
     *
     * @param ovfXml a <code>String</code> an OVF file with the informations
     * that drive the VM instance creation
     * @param request an <code>HttpServletRequest</code> with the full http
     * request
     * @return a <code>String</code> an OVF input with the VM instance ID
     * @exception DRPOneException If something goes wrong, what else ?
     */
    public String createCompute(String ovfXml,
            @Context HttpServletRequest request) throws DRPOneException, ParserConfigurationException, SAXException { // (Map<String,
        // Object>);
        HashMap<String, String> hmap = new HashMap<String, String>();




        log.debug("entering create compute...");

        if (ovfXml == null) {
            throw new DRPOneException("\nFailed createCompute method!!\n."
                    + "\nThe ovf xml is" + ovfXml
                    + "\n.Please enter correct xml ovf and try again.\n",
                    StatusCodes.BAD_OVF);
        }
        log.debug("ovfxml is not null...");
        OVFWrapper ovf;

        try {
            log.debug("parsing the ovfxml...");
            ovf = parse(ovfXml);
            log.debug("parsing successful");
        } catch (DRPOneException e) {
            // This one is expectet to be thrown, and should
            // pass unchanged
            throw e;
        } catch (Exception e) {
            throw new DRPOneException(
                    "\nFailed createCompute method!!\n"
                    + "An error occured to parse ovfXml.\n The parse of ovfxml is "
                    + e.getCause()
                    + "\n.Please enter correct xml ovf anf try again.\n",
                    StatusCodes.BAD_OVF);

        }
        log.debug("parsed ovf:\n" + ovf);

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
        if (ps
                != null) {
            List cop = ps.getCategoryOrProperty();
            ProductSectionType.Property propertyToRemove = null;
            for (Object prop : cop) {
                if (prop instanceof ProductSectionType.Property) {
                    ProductSectionType.Property p = (ProductSectionType.Property) prop;
                    hmap.put(p.getKey(), p.getValueAttribute());
                    log.debug(p.getKey() + " " + p.getValueAttribute());
                }
            }
        }
        Client ocaClient = getClient(request);

        log.debug(
                "created ocaclient");

        //1) Create the Images that will be used at the VM template
        System.out.println(
                "DRP4ONE - createVM> Voy a llamar a oneExtraFuncs.createAllImages()...");
        oneExtraFuncs.createAllImages(getClient(request), ONE_DATASTORE_ID, ovf.getDisks().values(), emotiveOvf.getBaseImage());

        //2) Start creating the VM Template
        String vmTemplate = "";


        try {
            System.out.println("DRP4ONE - createVM> emotiveOvf:" + emotiveOvf);
//            vmTemplate = ovf2OneDescription(emotiveOvf, request);
            vmTemplate = oneExtraFuncs.VMTemplate(ocaClient, emotiveOvf);
            //Sergio is doing a replace for the adequate ONE Network
//            vmTemplate = vmTemplate.replace("NETWORK = \"public\" ", "NETWORK = \"" + myOneNet + "\"");
            System.out.println("***************************************************************");
            System.out.println("DRP4ONE - createVM> vmTemplate2 (REPLACED):" + vmTemplate);
            System.out.println("***************************************************************");
        } catch (Exception e) {
            log.error("Exception occurred:" + e.getMessage());
            System.out.println("DRP4ONE - createVM> EXPCECION SALTADA CREANDO TEMPLATE");
        }

        log.debug(
                "vmtemplate:\n" + vmTemplate);
        // Creates the VM, keeping it at PENDING state (necessary to call deploy())
        System.out.println(
                "DRP4ONE - createVM> allocating the VM");

        OneResponse rc = VirtualMachine.allocate(ocaClient, vmTemplate);

        if (rc.isError()) {
            System.out.println("DRP4ONE - createVM> ERROR while allocating the VM");
            System.out.println("DRP4ONE - createVM> Message: " + rc.getErrorMessage());
            throw new DRPOneException("\nFailed createCompute method!!\n"
                    + "Failed to create VM " + rc.getErrorMessage() + Status,
                    StatusCodes.ONE_FAILURE);
        }
        int newVMID = Integer.parseInt(rc.getMessage());

        System.out.println(
                "DRP4ONE - createVM> the new VM has newVMID=" + newVMID);

        // After created the VM, it is necessary to deploy the VM
        VirtualMachine myvm = new VirtualMachine(newVMID, ocaClient);
        String strHostName = emotiveOvf.getProductProperty(EmotiveOVF.PROPERTYNAME_DESTINATION_HOST);

        System.out.println(
                "DRP4ONE - createVM> the VM host (extracted from OVF, strHostID=" + strHostName);

        //TODO: call the image creation
        // get the path
//        String imgName = emotiveOvf.getBaseImage();
//        String imgPath = imgsHome + "/" + imgName;
//        System.out.println("DRP4ONE - createVM> imgName:" + imgName);
//        System.out.println("DRP4ONE - createVM> imgPath:" + imgPath);
        // get the name
//        oneExtraFuncs.createImage(ocaClient, ONE_DATASTORE_ID, imgName, imgPath);

        // this function gets id from a host name from OpenStack
        int hid = oneExtraFuncs.getIdFromName(getClient(request), strHostName);

        System.out.println(
                "DRP4ONE  - oneExtraFuncs.getIdFromName(c," + strHostName + ")> : " + hid);

//        int hostID = Integer.parseInt(hid);
        OneResponse rc_info = myvm.info();

        System.out.println(
                "DRP4ONE  - createVM> rc_info: " + rc_info);

        //TODO: Use the host got from the ovf (now static "2")
        //Initiates the instance of the VM on the target host.
//        OneResponse rc_deploy = myvm.deploy(hostID);
        System.out.println(
                "B========================================D DRP4ONE  - myvm.deploy(" + hid + ")> : ");
        OneResponse rc_deploy = myvm.deploy(hid);

        if (rc_deploy.isError()) {
            System.out.println("DRP4ONE - createVM> ERROR while deploying the VM");
            throw new DRPOneException("\nFailed createCompute method!!\n"
                    + "Failed to create VM " + rc_deploy.getErrorMessage() + Status,
                    StatusCodes.ONE_FAILURE);
        }

        System.out.println(
                "DRP4ONE - createVM> deploying VM at specific host, message from ONE: " + rc_deploy.getMessage());


        // The response message is the new VM's ID
        log.info(
                "ok, ID " + newVMID + ".");

        OVFNetwork nets[] = this.getNetworks(request, newVMID);
        /*
         * notifyUsageTracker(newVMID, SecureSessionInfo.getUserDN(request));
         * log.info("UsageTracker notified for " + newVMID + ".");
         */
        // We can create a representation for the new VM, using the returned
        // VM-ID
        // Returning created domain.
        // get the disk section properties
        Collection<OVFDisk> tmpDisks = ovf.getDisks().values();
        OVFDisk disks[] = tmpDisks.toArray(new OVFDisk[tmpDisks.size()]);

        // get the network properties
//        Collection<OVFNetwork> tmpNets = ovf.getNetworks().values();
//        OVFNetwork nets[] = tmpNets.toArray(new OVFNetwork[tmpNets.size()]);
        // get the product properties
        // HashMap<String, String> hmap = new HashMap<String,String>();
        // SectionType product =
        // OVFAux.findSection(ovf.getOVFEnvelope().getSection(),
        // ProductSectionType.class);
        log.debug(
                "RAM:" + ovf.getMemoryMB());


        String xx = OVFWrapperFactory.create(ovf.getId(),
                ovf.getCPUsNumber(), ovf.getMemoryMB(), disks, nets, hmap)
                .toCleanString();

        idVsOneId.put(ovf.getId(), newVMID);

        try {
            //Truco Josep pa guardar name 
            EmotiveOVF ovfDomEmo = new EmotiveOVF(xx);
            ovfDomEmo.setProductProperty(EmotiveOVF.PROPERTYNAME_VM_NAME, String.valueOf(newVMID));
        } catch (Exception e) {
            System.out.println("");
            e.printStackTrace();
        }

        if (!hmap.isEmpty()) {
            PRODUCT_PROPS.put(String.valueOf(newVMID), hmap);

//            writeToFile(productPropsFolder + PROPS_FILE_NAME, String.valueOf(newVMID));
        }

        log.debug(
                "ovf returned:" + xx);
        return xx;
    }

    @POST
    @Path("/compute/{vmId}/migrate")
    @Produces("application/xml")
    public String migrate(@PathParam("vmId") String vmId, @QueryParam("dstHost") String dstHost, @Context HttpServletRequest request) {
//
//    @POST
//    @Path("/migrate/{envid}/{dstHost}")
//    @Consumes("application/xml")
//    @Produces("application/xml")
//    public String migrate(@PathParam("envid") String strVMID, @PathParam("dstHost") String strDstHost,@Context HttpServletRequest request) {
        Client ocaClient = getClient(request);
        // After created the VM, it is necessary to deploy the VM
        int fromID = Integer.parseInt(vmId);
        VirtualMachine myvm = new VirtualMachine(fromID, ocaClient);
//        int hostID = Integer.parseInt(strHostName);
        OneResponse rc_info = myvm.info();
        int toID = Integer.parseInt(dstHost);
        OneResponse rc_deploy = myvm.deploy(toID);

//        String xx = OVFWrapperFactory.create(String.valueOf(newVMID),
//                ovf.getCPUsNumber(), ovf.getMemoryMB(), disks, nets, hmap)
//                .toCleanString();


        return null;
    }

    private void writeToFile(String fileName, String vmId) {
        try {

            HashMap<String, String> hmap = PRODUCT_PROPS.get(vmId);

            Iterator<String> hmapIter = hmap.keySet().iterator();
            new File(productPropsFolder + PROPS_FILE_NAME).createNewFile();

            PropertiesConfiguration props_config = new PropertiesConfiguration(
                    new File(productPropsFolder + PROPS_FILE_NAME));
            props_config.addProperty(vmId, hmap);
            log.debug("saving to properties file..");
            props_config.save();

        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    @GET
    @Path("/compute/{envid}")
    @Produces("application/xml")
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
    public String getCompute(@PathParam("envid") String envId,
            @Context HttpServletRequest request) throws DRPOneException {

        String oneID = String.valueOf(idVsOneId.get(envId));

        OneResponse rc = getComputeHelper(envId, request);

        OCAComputeWrapper oca = rc2OCA(rc);

        Collection<OCADiskWrapper> diskList = oca.getTemplate().getDisks()
                .values();

        OVFDisk diskArray[] = new OVFDisk[diskList.size()];

        int counter = 0;

        for (OCADiskWrapper disk : diskList) {
            String diskId = disk.getDiskId();
            diskArray[counter++] = new OVFDisk((diskId == null) ? "AnonDisk_"
                    + counter : diskId, disk.getSource(), disk.getSize());
        }

        Collection<OCANicWrapper> nicList = oca.getTemplate().getNics()
                .values();

        OVFNetwork nicArray[] = new OVFNetwork[nicList.size()];

        counter = 0;
        int vmStatus = oca.getState();
        int lcmState = oca.getLcmState();
        boolean doThePoll = vmStatus == VmStatusCodes.ACTIVE
                && lcmState == LcmStatusCodes.RUNNING;

        boolean gotPort22Connection = false;

        for (OCANicWrapper nic : nicList) {
            String netName = nic.getNetwork();
            String ipAddress = nic.getIp();

            nicArray[counter++] = new OVFNetwork((netName == null) ? "AnonNet_"
                    + counter : netName, ipAddress, nic.getMac());
            if (doThePoll && ipAddress != null) {
                gotPort22Connection |= port22Reached(ipAddress);
            }

        }

        HashMap<String, String> props_hmap = getPropsFromFile(productPropsFolder + PROPS_FILE_NAME, String.valueOf(oca.getId()));

        EmotiveOVF ovfReply = OVFWrapperFactory.create("" + oca.getId(),
                oca.getCpu(), oca.getMemory(), diskArray, nicArray,
                props_hmap);

        ovfReply.setState(one2libvrtStatus(vmStatus, lcmState,
                gotPort22Connection));

        String rv = ovfReply.toCleanString();
        return rv;
    }

    private OneResponse getComputeHelper(String envId,
            HttpServletRequest request) throws DRPOneException {
        Client ocaClient = getClient(request);

        int machineId = 0;

        try {
            machineId = Integer.parseInt(envId);
        } catch (NumberFormatException nfe) {
            throw new DRPOneException("\nFailed getCompute method!!\n"
                    + "Illegal VM id " + envId
                    + ".\n Please enter correct VM id and try again.",
                    StatusCodes.VM_NOT_EXIST);
        }

        OneResponse rc = VirtualMachine.info(ocaClient, machineId);

        log.debug("This is the information OpenNebula stores for the new VM:"
                + rc.getMessage() + "\n");

        if (rc.isError()) {
            throw new DRPOneException("Failed getCompute method!!\n"
                    + " Failed to retrieve VM with ID " + envId + " : "
                    + rc.getErrorMessage(), StatusCodes.ONE_FAILURE);
        }

        return rc;

    }

    private OCAComputeWrapper rc2OCA(OneResponse rc) {
        String tmp = rc.getMessage();
        StringBuilder xmlReply = new StringBuilder(64 + tmp.length());
        xmlReply.append("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>");
        xmlReply.append(tmp);

        return parseOcaCompute(xmlReply.toString());
    }

    /**
     *
     * @return A ListStrings object containing the vmIDs of the domains running
     * in the system.
     */
    @GET
    @Path("/compute")
    @Produces("application/xml")
    public ListStrings getComputes(@Context HttpServletRequest req) {
        ListStrings ret = new ListStrings();

        OCAComputeListWrapper oca = getComputesHelper(req);

        for (OCAComplexComputeWrapper complexCompute : oca) {
//            String id = String.valueOf(complexCompute.getId());
            Integer id = oneExtraFuncs.getKeyOfValue(this.idVsOneId, complexCompute.getId());
            ret.add("" + id);
        }
        return ret;

    }

    @GET
    @Path("/compute/all")
    @Produces("application/xml")
    /**
     * <code>getComputesAll</code> Returns an XML with the ID of all the user
     * VMs
     *
     * @param request an <code>HttpServletRequest</code> with the full http
     * request
     * @return a <code>ListStrings</code> the list of the VMs
     */
    public ListStrings getComputesAll(@Context HttpServletRequest request) {
        ListStrings ret = new ListStrings();
        log.debug("entered computeall method");

        OCAComputeListWrapper oca = getComputesHelper(request);

        for (OCAComplexComputeWrapper complexCompute : oca) {
            Map<String, OCADiskWrapper> disks = complexCompute.getTemplate()
                    .getDisks();

            OVFDisk ovfDisks[] = new OVFDisk[disks.size()];

            int counter = 0;
            for (OCADiskWrapper disk : disks.values()) {
                String diskId = disk.getDiskId();

                ovfDisks[counter++] = new OVFDisk(
                        ((diskId == null) ? "AnonDisk_" + counter : diskId),
                        disk.getSource(), disk.getSize());
            }

            Map<String, OCANicWrapper> nics = complexCompute.getTemplate()
                    .getNics();
            OVFNetwork ovfNics[] = new OVFNetwork[nics.size()];
            counter = 0;

            int vmStatus = complexCompute.getState();
            int lcmState = complexCompute.getLcmState();
            boolean doThePoll = vmStatus == VmStatusCodes.ACTIVE
                    && lcmState == LcmStatusCodes.RUNNING;

            boolean gotPort22Connection = false;

            for (OCANicWrapper nic : nics.values()) {
                String netName = nic.getNetwork();
                String ipAddress = nic.getIp();

                ovfNics[counter++] = new OVFNetwork(
                        (netName == null) ? "AnonNet_" + counter : netName,
                        ipAddress, nic.getMac());
                if (doThePoll && ipAddress != null) {
                    gotPort22Connection |= port22Reached(ipAddress);
                }

            }

            HashMap<String, String> props_hmap = getPropsFromFile(productPropsFolder + PROPS_FILE_NAME, String.valueOf(complexCompute.getId()));


            EmotiveOVF tmpOvf = OVFWrapperFactory.create(complexCompute.getId()
                    + "", complexCompute.getTemplate().getCpu(),
                    complexCompute.getMemory(), ovfDisks, ovfNics,
                    props_hmap);
            tmpOvf.setState(one2libvrtStatus(vmStatus, lcmState,
                    gotPort22Connection));

            tmpOvf.setProductProperty(EmotiveOVF.PROPERTYNAME_VM_NAME,
                    complexCompute.getName());

            ret.add(tmpOvf.toCleanString());
        }

        return ret;
    }

    private HashMap<String, String> getPropsFromFile(String fileName, String vmId) {
        Object product_propsList = "";
        HashMap<String, String> hmap = new HashMap<String, String>();
        String[] product_props = {};
        try {
            PropertiesConfiguration props_config = new PropertiesConfiguration(new File(fileName));

            if (props_config.containsKey(vmId)) {
                log.debug("getting the product properties for vm: " + vmId);
                product_propsList = props_config.getProperty(vmId);
                if (product_propsList.toString().contains(",")) {
                    product_props = product_propsList.toString().substring(1, product_propsList.toString().length() - 1).split(",");

                    for (String product_prop : product_props) {
                        if (product_prop.contains("=")) {
                            String[] product_property = product_prop.split("=");
                            hmap.put(product_property[0], product_property[1]);
                            log.debug("adding product property " + product_property[0] + " " + product_property[1]);
                        }
                    }
                } else {
                    String[] product_property = product_propsList.toString().substring(1, product_propsList.toString().length() - 1).split("=");
                    if (product_property.length == 2) {
                        hmap.put(product_property[0], product_property[1]);
                        log.debug("adding product property " + product_property[0] + " " + product_property[1]);
                    } else {
                        log.error("error in the product property, does nt contain a valid key value pair..");
                    }

                }

            } else {
                log.debug("no such key exists " + vmId);
            }

        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

        return hmap;
    }

    private OCAComputeListWrapper getComputesHelper(HttpServletRequest request) {
        Client ocaClient = getClient(request);

        OneResponse rc = VirtualMachinePool.info(ocaClient, -1);

        if (rc.isError()) {
            throw new DRPOneException("\nFailed getComputesAll method!!\n."
                    + "An error occured in the virtual machine "
                    + "configuration pool. vmpool is " + rc.getMessage()
                    + "\nPlease try again\n.", StatusCodes.VM_NOT_EXIST);
        }

        String tmp = rc.getMessage();
        StringBuilder xmlReply = new StringBuilder(64 + tmp.length());
        xmlReply.append("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>");
        xmlReply.append(tmp);

        return parseOcaComputeList(xmlReply.toString());
    }

    @DELETE
    @Path("/compute/{envid}")
    /**
     * <code>deleteCompute</code> Kills an instance of a VM
     *
     * @param envid a <code>String</code> the id of the VM to kill
     * @param request an <code>HttpServletRequest</code> with the full http
     * request
     * @exception DRPOneException when something goes wrong.
     */
    public void deleteCompute(@PathParam("envid") String envid,
            @Context HttpServletRequest request) throws DRPOneException {

        Client ocaClient = getClient(request);

        int machineId = -1;
        int oneID = -1;
        try {
//            machineId = Integer.parseInt(envid);
            if (idVsOneId.containsKey(envid)) {
                oneID = idVsOneId.get(envid);
            } else {
                machineId = Integer.parseInt(envid);
            }
            System.out.println("DRP4ONE:deleteCompute()> idVsOneId.get(envid)=" + oneID);

        } catch (NumberFormatException nfe) {
            throw new DRPOneException("\nFailed deleteCompute method!!\n"
                    + "Illegal machine ID, it is " + machineId
                    + ".\nPlease enter correct machine id and try again."
                    + Status, StatusCodes.BAD_REQUEST);
        }

        VirtualMachine vm = new VirtualMachine(oneID, ocaClient);

        OneResponse rc = vm.finalizeVM();
        // return a message as "wait please..."
        System.out.println("\n... Trying to finalize (delete) the VM ...."
                + envid + "...");
        if (rc.isError()) {
            // log.error("Failed to delete vm instance " + envid +": " +
            // rc.getErrorMessage());
            throw new DRPOneException(
                    "\nFailed deleteCompute method!!"
                    + "The enviroments "
                    + envid
                    + "is not correct.Please enter correct Id enviroment and try again...\n. The Virtual Machine Error Message is "
                    + rc.getErrorMessage(), StatusCodes.ONE_FAILURE);
        }

        log.info("Environment " + envid + " was destroyed.");
    }

    /*
     * ==============================================================
     * ==============================================================
     * 
     * private helpers for the createCompute method
     */
    private OVFWrapper parse(String ovfXml) throws DRPOneException {
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
            log.error(cause.toString());
            if (log.isTraceEnabled()) {
                log.trace(cause, e);
            }
            throw new DRPOneException(cause.toString(), e,
                    StatusCodes.XML_PROBLEM);
        } catch (OVFException e) {
            cause.append("Problems parsing OVF file: ");
            cause.append(e.getMessage());
            log.error(cause.toString());
            if (log.isTraceEnabled()) {
                log.trace(cause, e);
            }
            throw new DRPOneException(cause.toString(), e,
                    StatusCodes.XML_PROBLEM);
        }
        return rv;
    }

    private boolean isKVMInUse(EmotiveOVF ovf) {

        // TODO: CLEAR THIS CODE!!!
        // String hyperName = ovf.getProductProperty(HYPERVISOR);

        String hyperName = ovf.getProductProperty(HYPERVISOR);
        String propHyperName = null;
        propHyperName = ovf4oneProperties(HYPERVISOR, false);
        hyperName = hyperName == null ? propHyperName : hyperName;
        return hyperName == null || "kvm".equalsIgnoreCase(hyperName);
    }

    // Generates a VM description starting from an ovf wrapper.
    private String ovf2OneDescription(EmotiveOVF ovf, HttpServletRequest request) throws IOException {
        StringBuilder buf = new StringBuilder(1024);
        String netConnectNames = "";
        // Name, CPUs and Memory
        // they are not mandatory , a name is generated by one will be
        // one_<VID> form
//        boolean weUseKVM = isKVMInUse(ovf);


//    These are flags to check if a certain mandatory [*] values are
//    supplied with the OVF.
//
//    [*] some values are mandatory with XEN, other with KVM...
//    See at http://opennebula.org/documentation:archives:rel2.0:template

        List<String> device_map = Arrays.asList("vdb", "vdc", "vdd", "vde");
        int no_of_virtual_disk = 0;
        Object tmp = ovf.getId();
        if (tmp != null) {
            buf.append("NAME = \"");
            buf.append(tmp);
            buf.append("\"\n");
        }

        tmp = ovf.getMemoryMB();
        if (tmp != null) {
            buf.append("MEMORY = ");
            buf.append(tmp);
            buf.append("\n");
        } // @madigiro - 20th july
        else if ((tmp = ovf4oneProperties("MEMORY", false)) != null) {
            buf.append("MEMORY = ");
            buf.append(tmp);
            buf.append("\n");
        } else {
            throw new DRPOneException("OVF file is missing mandatory "
                    + "memory specification.\n Please,",
                    StatusCodes.NOT_ACCEPTABLE);
        }

        tmp = ovf.getCPUsNumber();
        if (tmp != null) {
            // It seems that in our OVF VCPUs and CPUs are the same thing.
            buf.append("CPU = ");
            buf.append(tmp);
            buf.append("\n");
            buf.append("VCPU = ");
            buf.append(tmp);
            buf.append("\n");
        } else if ((tmp = ovf4oneProperties("CPU", false)) != null) {
            buf.append("CPU = ");
            buf.append(tmp);
            buf.append("\n");
            buf.append("VCPU = ");
            buf.append(tmp);
            buf.append("\n");
            // throw new
            // DRPOneException("OVF file is missing mandatory CPU number specification ",StatusCodes.BAD_OVF);
        } else {
            throw new DRPOneException("OVF file is missing mandatory "
                    + "CPU specification.\n", StatusCodes.NOT_ACCEPTABLE);
        }

        // Get the OS section
        String os = VMDescriptionOS(ovf);
        buf.append(os);

        // Get the DISK's section
//        String d = VMDescriptionDisks(ovf);
        String d = oneExtraFuncs.VMDescriptionDisks(getClient(request), ovf.getDisks().values());
        buf.append(d);

        try {
            log.debug("genereated init.sh:");
            // bw.close();
        } catch (Exception e) {
            log.error("io exception occurred");
        }

        // Get the Network section
        String n = VMDescriptionNetwork(ovf);
        buf.append(n);

        buf.append("FEATURES=[ acpi=\"yes\" ]\n\n");

        buf.append("GRAPHICS = [type=\"vnc\", listen=\"127.0.0.1\", password=\"\\/|\\|67-yavin\"]\n\n");

        String c = VMDescriptionContext(ovf);
        buf.append(c);

        /*
         * If the cluster property is set, we add a requirements to the image
         * definition file, so that we can force the VM to be deployed on a
         * certain cluster
         * 
         * TODO: make it a per user choice
         * 
         * At this time this seem very ENG specific, since we need to keep some
         * of the machine reserved to a certain project and some other reserved
         * to internal usage
         */
        String cluster = ovf4oneProperties(CLUSTER, false);
        if (cluster != null && !"".equalsIgnoreCase(cluster)) {
//            buf.append("REQUIREMENTS = \"CLUSTER = \\\"");
//            buf.append(cluster);
//            buf.append("\\\"\"\n");
        }
//        buf.append("REQUIREMENTS = \"NAME = \\optimis1.leeds\\\"\"\n");
        // to a VM template that should not automatically be deployed (http://lists.opennebula.org/pipermail/users-opennebula.org/2012-April/018463.html)
        buf.append("REQUIREMENTS = \"FALSE\"\n");


//        /*
//         * @smendoza:  If using OpenNebula's default match-making scheduler in a hypervisor heterogeneous environment, it is a good idea to add an extra line like the following to the VM template to ensure its placement in a VMWare hypervisor enabled machine
//         */
//        buf.append("REQUIREMENTS = \"HYPERVISOR = \\\"");
//        buf.append(ovf4oneProperties(HYPERVISOR, false));
//        buf.append("\\\"\"\n");

        log.debug(weGotDisk);

        if (!((weGotKernel || weGotBootloader || weUseKVM) // this is mandatory for XEN only
                && (weGotBoot || !weUseKVM) // this is mandatory for KVM only
                && weGotDisk)) {
            StringBuilder msg = new StringBuilder(
                    "These problem in the OVF file prevent correct execution of the command:");
            msg.append((weGotKernel || weUseKVM) ? ""
                    : "\n- Missing KERNEL specification for use with XEN");
            msg.append((weGotBootloader || weUseKVM) ? ""
                    : "\n- Missing BOOTLOADER specification for use with XEN");
            msg.append((weGotBoot || !weUseKVM) ? ""
                    : "\n- Missing BOOT device specification for use with KVM");
            msg.append(weGotDisk ? ""
                    : "\n- Missing any DISK image specification");

            throw new DRPOneException(msg.toString() + Status,
                    StatusCodes.BAD_OVF);

        }

        /*
         String[] rawData = { props.getProperty("raw.data.builder"),props.getProperty("raw.data.shadow_memory"),props.getProperty("raw.data.device_model"),props.getProperty("raw.data.boot"),props.getProperty("raw.TYPE")};
         String rawFormat = "RAW=[DATA=\"builder = '%s' \n shadow_memory = %s \n device_model = '%s' \n boot = \\\"%s\\\"\", \n TYPE=\"%s\"  \n ] \n";
         String raw = String.format(rawFormat,rawData);
         buf.append(raw); 
         */
        HashMap rawHM = new HashMap();
        rawHM.put("TYPE", "xen");
        rawHM.put("DATA", "builder = 'hvm'");// \nshadow_memory = 8 \ndevice_model = '/usr/lib/xen-4.0/bin/qemu-dm' \nboot = \\\"c\\\"");
//        String raw = ONETemplatter.genSection("RAW", rawHM);
//        String raw = "RAW=["
//                + "DATA=\"builder = 'hvm' \n"
//                + "shadow_memory = 8 \n"
//                + "device_model = '/usr/lib/xen-4.0/bin/qemu-dm' \n"
//                + "boot = \\\"c\\\"\", \n"
//                + "TYPE=\"xen\"  \n"
//                + "] \n";
//        buf.append(raw);
        String template = "VCPU = 1\n"
                + "TEMPLATE_ID = 8\n"
                + "MEMORY = 1024\n"
                + "NAME = \"Ubuntu 12.04.1 x86_64 HVM (VNC) Instance\"\n"
                + "CPU = 1\n"
                + "DISK = [\n"
                + "IMAGE = \"Ubuntu 12.04.1 x86_64 HVM Base\",\n"
                + "IMAGE_UNAME = \"oneadmin\",\n"
                + "TARGET = \"hda\",\n"
                + "DRIVER = \"tap:qcow2:\"\n"
                + "]\n"
                + "OS = [\n"
                + "BOOTLOADER = \"/usr/lib/xen-4.0/boot/hvmloader\"\n"
                + "]\n"
                + "GRAPHICS = [\n"
                + "type=\"vnc\", \n"
                + "listen=\"0.0.0.0\", \n"
                + "KEYMAP = \"en-gb\", \n"
                + "password=\"password\"\n"
                + "]\n"
                + "RAW = [ DATA = \"builder = 'hvm'\",\n"
                + "TYPE = \"xen\" ]\n"
                + "NIC = [\n"
                + "MODEL = \"e1000\",\n"
                + "NETWORK = \"Internal NAT\",\n"
                + "NETWORK_UNAME = \"oneadmin\"\n"
                + "]\n";

        String qcow2 = "VCPU = 1\n"
                + "MEMORY = 1024\n"
                + "NAME = \"Sergio QCOW2\"\n"
                + "CPU = 1\n"
                + "DISK = [\n"
                //                + "TYPE=fs,\n"
                + "SOURCE= \"/opt/opennebula/var/datastores/1/dc443c87c4455d06ed96b694eb8c5131\",\n"
                //                + "PATH= \"/home/anthony/my-images/ubuntu_10g_x86_64.qcow2\",\n"
                + "TARGET = \"hda\",\n"
                //                + "DRIVER = \"tap:qcow2:\"\n"
                + "DRIVER = \"tap:qcow2:\"\n"
                + "]\n"
                + "OS = [\n"
                + "BOOTLOADER = \"/usr/lib/xen-4.0/boot/hvmloader\"\n"
                + "]\n"
                + "GRAPHICS = [\n"
                + "type=\"vnc\", \n"
                + "listen=\"0.0.0.0\", \n"
                + "KEYMAP = \"en-gb\", \n"
                + "password=\"password\"\n"
                + "]\n"
                + "RAW = [ DATA = \"builder = 'hvm'\",\n"
                + "TYPE = \"xen\" ]\n"
                + "NIC = [\n"
                + "MODEL = \"e1000\",\n"
                + "NETWORK = \"Internal NAT\",\n"
                + "NETWORK_UNAME = \"oneadmin\"\n"
                + "]\n";

        log.debug(buf);
//        return qcow2;
        return buf.toString();
    }

    /**
     * Asks OpenNebula to obtain the info of the VM
     *
     * @param request
     * @param newVMID
     * @return
     */
    private String[] getIPs(HttpServletRequest request, int newVMID) {
        ArrayList<String> ips = new ArrayList<String>();
        String strFrom = "IP><![CDATA[";
        String strTo = "]]></IP>";
        Client ocaClient = getClient(request);


        OneResponse rc = VirtualMachine.info(ocaClient, newVMID);

        if (rc.isError()) {
            throw new DRPOneException("\nFailed getIP method!!\n"
                    + "Failed while asking for the new VM IP" + rc.getErrorMessage() + Status,
                    StatusCodes.ONE_FAILURE);
        }
        String rpcMessage = rc.getMessage();

        int ipFrom = rpcMessage.indexOf(strFrom);
        int ipTo = rpcMessage.indexOf(strTo);

        while (ipFrom != -1 && ipTo != -1) {

            String tmpIP = rpcMessage.substring(ipFrom + strFrom.length(), ipTo);
            ips.add(tmpIP);

            ipFrom += strFrom.length();
            ipTo += strTo.length();

            //Search for the next appearance
            ipFrom = rpcMessage.indexOf(strFrom, ipFrom);
            ipTo = rpcMessage.indexOf(strTo, ipTo);

        }
        String[] ipsArray = ips.toArray(new String[ips.size()]);

        return ipsArray;
    }

    private OVFNetwork[] getNetworks(HttpServletRequest request, int newVMID) {

//        String[] ips = this.getIPs(serverID);

        String[] ips = getIPs(request, newVMID);

        OVFNetwork[] nets = new OVFNetwork[ips.length];

        for (int i = 0; i < ips.length; ++i) {
            //public OVFNetwork(String connectionName, String ip, String mac, String netmask, String gateway)
            nets[i] = new OVFNetwork("public", ips[i], null, null, null);
        }

        return nets;
    }

    /**
     * Extracts from the *.ovf file all the information related to OS attributes
     * necessaries to generate OpenNebula's VM definition
     *
     * @param ovf
     * @return OS section of OpenNebula's VM definition
     */
    private String VMDescriptionOS(EmotiveOVF ovf) {
        StringBuilder buf = new StringBuilder();
        String attribute = "OS";
        HashMap hmSubAttr = new HashMap();

        // get ARCH from *.ovf
        String archName = ovf.getProductProperty(ARCH);
        if (archName == null && weUseKVM) {
            // if arch is not defined at *.ovf, got from default properties
            archName = ovf4oneProperties(ARCH, false);
            try {
                //if arch from *.ovf is an empty string, throw an exception
                if (archName != null && "".equals(archName.trim())) {
                    throw new DRPOneException(
                            "OVF and property files are both missing mandatory "
                            + " architecutre specification for KVM.\n" + Status,
                            StatusCodes.NOT_ACCEPTABLE);
                } else {
                    hmSubAttr.put(ARCH, archName);
                }
            } catch (Exception e) {
            }
        }

        for (String productProperty : OS_subAttributes) {
            // String value = ovf.getProductProperty(productProperty);
            log.debug("getting the product property value of : "
                    + productProperty);
            String value = ovf.getProductProperty(productProperty);

            // First rescue attempt. If the property is not in the
            // OVF, let's check the default values for this site.
            if (value == null || "".equals(value)) {
                value = ovf4oneProperties(productProperty, false);
            }

            if (value != null && !"".equals(value)) {
                // O.K., if I am here I got a value somehow, let's continue
                hmSubAttr.put(productProperty, value);

                if (productProperty == BOOT) {
                    if (BootType.isValid(value)) {
                        weGotBoot = true;
                    } else {
                        throw new DRPOneException(
                                "OVF file contains a bad value for BOOT "
                                + value, StatusCodes.BAD_OVF);
                    }
                } else if (productProperty == BOOTLOADER) {
                    weGotBootloader = true;
                } else if (productProperty == KERNEL) {
                    weGotKernel = true;
                }
            }
        }

//        return ONETemplatter.genSection(attribute, hmSubAttr);

        return buf.toString();
    }

    /**
     *
     * Extract Disk Attributes for the VM template
     *
     * @param ovf file where the disk attributes will be extractred from
     * @return String with the disk description section for the ONE VM Template
     */
    private String VMDescriptionDisks(EmotiveOVF ovf) {
        StringBuilder buf = new StringBuilder();

        // Let's avoid some work to the JVM :)
        // 100 characters should avoid buffer resizing in most cases.
        // Don't worry, no buffer overflow ahead :)
        StringBuilder propertyName = new StringBuilder(100);
        log.debug("ovfdiskssize:" + ovf.getDisks().size());
        TargetedDisk sortedDisks[] = new TargetedDisk[ovf.getDisks().size()];

        int tgtDskIdx = 0;

        /*
         * It seems that OpenNebula requires that the disks are listed in the
         * physical order, while OVF has no constraint on the position of the
         * disks definition within the list. So I sort the disks according to
         * the physical device name:
         * 
         * TargetedDisk is a Comparable extension of OVFDisk that orders the
         * disks according to the device name, in the same way Linux does.
         */
        for (OVFDisk ovfDisk : ovf.getDisks().values()) {
            log.debug(ovfDisk.getId());
            sortedDisks[tgtDskIdx++] = new TargetedDisk(ovfDisk.getId(),
                    ovfDisk, ovf);
        }
        log.debug("number of sorted disks:" + sortedDisks.length);
        Arrays.sort(sortedDisks);

        // retrieve from product property disk name
        if (ovf.getProductProperty(VM_BASEIMAGE) != null) {
            log.debug(ovf.getProductProperty(VM_BASEIMAGE));
            OVFDisk master_disk = ovf.getDisks().get(
                    ovf.getProductProperty(VM_BASEIMAGE));

            log.debug(master_disk);
            log.debug("master disk id:" + master_disk.getId());
            propertyName.append(master_disk.getId());
            propertyName.append(PROP_PATORURL);

            TargetedDisk ovfDisk1 = new TargetedDisk("master", master_disk, ovf);
            buf.append("DISK = [\n");

            propertyName.append(master_disk.getId());
            propertyName.append(PROP_PATORURL);

            // If we have a path or URL specified, we have a pysical
            // disk. Else we have a pre-registered disk resource.
            String pathOrURL = ovfDisk1.getHref();
            log.debug("got image ref:" + pathOrURL);
            System.out.println("DRP4ONE - VMDescriptionDisks() > buf before: \n" + buf.toString());
            addVirtualDiskConfig(ovfDisk1, pathOrURL, ovf, propertyName, buf);
            System.out.println("DRP4ONE - VMDescriptionDisks() > buf after: \n" + buf.toString());
            buf.append("]\n");
            weGotDisk = true;
        }

        for (TargetedDisk ovfDisk : sortedDisks) {
            if (ovfDisk.getDskName().equals(
                    ovf.getProductProperty(VM_BASEIMAGE))) {
                continue;
            }

            System.out.println("DRP4ONE - VMDescriptionDisks() > for: entro n l for" + buf.toString());

            buf.append("DISK = [\n");

            propertyName.delete(0, propertyName.length());
            log.debug("got disk name:" + ovfDisk.getDskName());
            System.out.println("DRP4ONE - VMDescriptionDisks() > for: got disk name:" + ovfDisk.getDskName());

            propertyName.append(ovfDisk.getDskName());
            propertyName.append(PROP_PATORURL);
            String pathOrURL = ovfDisk.getHref();
            log.debug("got image ref:" + pathOrURL);
            System.out.println("DRP4ONE - VMDescriptionDisks() > for: got image ref:" + pathOrURL);

            if (pathOrURL == null) {
                pathOrURL = ovf4oneProperties(propertyName.toString(), false);
            }

            propertyName.delete(ovfDisk.getDskNameLen(), propertyName.length());
            propertyName.append(PROP_TYPE);

            // String typeName =
            // ovf.getProductProperty(propertyName.toString());

            String typeName = ovf.getProductProperty(propertyName.toString());
            String propTypeName = ovf4oneProperties(propertyName.toString(),
                    false);

            // When no disk type is specified, OpenNebula defaults to disk, and
            // we do so.
            DskType dskType = (typeName == null || "".equals(typeName.trim())) ? ((propTypeName == null || ""
                    .equals(propTypeName.trim())) ? DskType.virtualDisk
                    : DskType.fromString(propTypeName)) : DskType
                    .fromString(typeName);
            log.debug("Disk type" + dskType);
            System.out.println("DRP4ONE - VMDescriptionDisks() > for: Disk type" + dskType);

            if (dskType == null) {
                throw new DRPOneException(
                        "\n.OVF file is missing mandatory disk type for disk "
                        + ovfDisk.getDskName(), StatusCodes.NOT_FOUND);
            }

            Long size = ovfDisk.getCapacityMB();

            if (size == null
                    && (dskType == DskType.virtualSwap || dskType == DskType.onTheFlyDisk)) {
                throw new DRPOneException(
                        "\n.OVF file is missing mandatory size specification for a disk ",
                        StatusCodes.BAD_OVF);
            }

            switch (dskType) {

                case virtualDisk:
                    System.out.println("virtualDisk");

                    addVirtualDiskConfig(ovfDisk, pathOrURL, ovf, propertyName, buf);
                    System.out.println("DRP4ONE > Finishes addVirtualDiskConfig() virtualDisk");
                    weGotDisk = true;
                    break;
                case virtualSwap:
                    System.out.println("virtualSwap");

                    if (pathOrURL != null && !"".equals(pathOrURL)) {
                        buf.append("SOURCE = \"");
                        buf.append(pathOrURL);
                        buf.append("\",\n");
                    }
                    // else
                    // throw new
                    // DRPOneException("\n.OVF file is missing mandatory path or URL specification for swap disk named"
                    // + ovfDisk.getDskName(),
                    // StatusCodes.BAD_OVF);

                    if (ovfDisk.getTarget() == null) {
                        throw new DRPOneException(
                                "Missing mandatory target specification for a swap disk named  "
                                + ovfDisk.getDskName(), StatusCodes.BAD_OVF);
                    }
                    buf.append("TYPE=swap,\n");
                    buf.append("SIZE = ");
                    buf.append(size);
                    buf.append(",\n");
                    buf.append("TARGET = \"");
                    buf.append(ovfDisk.getTarget());
                    buf.append("\"");
                    // update the init.sh file to swapon the swap space

                    arg2.add(ovfDisk.getTarget());

                    try {
                        // bw.write("/sbin/swapon /dev/"+device_map.get(no_of_virtual_disk++)+"\n");
                    } catch (Exception e) {
                        log.error("io exception occurred");
                    }
                    break;

                case blockDevice:
                    System.out.println("blockDevice");

                    buf.append("TYPE=block,\n");
                    buf.append("SOURCE = \"");
                    buf.append(pathOrURL);
                    buf.append("\",\n");
                    if (ovfDisk.getTarget() != null) {
                        buf.append("TARGET = \"");
                    }
                    buf.append(ovfDisk.getTarget());
                    buf.append("\"");

                    break;

                case onTheFlyDisk:
                    System.out.println("onTheFlyDisk");

                    propertyName.delete(ovfDisk.getDskNameLen(), propertyName.length());
                    propertyName.append(PROP_FORMAT);
                    log.debug("received on the fly disk");

                    // //@madigiro - 21 july
                    String format = ovf.getProductProperty(propertyName.toString());

                    if (format == null) {
                        format = ovf4oneProperties(propertyName.toString(), false);
                    }

                    if (format == null) {
                        throw new DRPOneException(
                                "Missing mandatory format specification for an ont the fly disk image "
                                + ovfDisk.getDskName(), StatusCodes.BAD_OVF);
                    }

                    buf.append("TYPE=fs,\n");
                    if (pathOrURL != null && !"".equals(pathOrURL)) {
                        buf.append("SOURCE = \"");
                        buf.append(pathOrURL);
                        buf.append("\",\n");
                    }
                    buf.append("SIZE = ");
                    buf.append(size);
                    buf.append(",\n");
                    buf.append("FORMAT = \"");
                    buf.append(format);
                    buf.append("\",\n");
                    if (ovfDisk.getTarget() != null) {
                        buf.append("TARGET = \"");
                    }
                    buf.append(ovfDisk.getTarget());
                    buf.append("\"");

                    arg1.add(ovfDisk.getTarget());

            }// switch finishes here

            typeName = "";
            pathOrURL = "";
            buf.append("]\n");
        }

        return buf.toString();
    }

    private String VMDescriptionNetwork(EmotiveOVF ovf) {

        StringBuilder buf = new StringBuilder();
        String tmp;

        // check for network attributes, if not available check the properties
        // file

        System.out.println("DRP4ONE - VMDescriptionNetwork()> ovf.getNetworks(): " + ovf.getNetworks());

        if (!ovf.getNetworks().isEmpty()) {

//            added by smendoza
//            String netSection = String.format("NIC = [ NETWORK_ID = %s ] \n",props.getProperty("net.id"));
//            buf.append(netSection);

            // Network attributes
            netConnectNames = ovf4oneProperties(NET_CONNECTION_NAMES, false);
            for (OVFNetwork ovfNetwork : ovf.getNetworks().values()) {
                buf.append("NIC = [\n");
                String nicName = ovfNetwork.getConnectionName();

                // I am working on a new array, so I need to reset the separator
                String separator = "";

                if (nicName == null || "".equals(nicName)) {
                    // We supply IP and MAC for this NIC
                    tmp = ovfNetwork.getIp();
                    if (tmp != null) {
                        buf.append("IP = \"");
                        buf.append(tmp);
                        buf.append("\"");
                        separator = ",\n";
                    }

                    tmp = ovfNetwork.getMac();
                    if (tmp != null) {
                        buf.append(separator);
                        buf.append("MAC = \"");
                        buf.append(tmp);
                        buf.append("\"");
                    }

                } else {
                    // We ask OpenNebula to assign us IP and MAC
                    System.out.println("DRP4ONE - VMDescriptionNetwork()> nicName: " + nicName);

                    buf.append("NETWORK = \"");
                    buf.append(nicName);
                    buf.append("\" ");
                }
                buf.append("\n");
                nicNames.add(nicName);
                buf.append("]\n");
                System.out.println("DRP4ONE - VMDescriptionNetwork()> buf: " + buf);

                nicName = "";

            }

            log.debug("adding the networks configured in props..");
            log.debug("networks configured:" + netConnectNames);
            Scanner commaSperated = new Scanner(netConnectNames);
            commaSperated.useDelimiter(",");

            while (commaSperated.hasNext()) {
                String connectionName = commaSperated.next().trim();
                log.debug("connection name " + connectionName);
                Iterator<String> nicNamesIter = nicNames.iterator();
                while (nicNamesIter.hasNext()) {
                    log.debug("nic names config in input:"
                            + nicNamesIter.next());
                }
                if (!nicNames.contains(connectionName)) {
                    //addNic(buf, connectionName);
                }
            }
        } else {
            log.debug("no network configured in ovf input, hence checking the network config from properties file");
            log.debug("networks configured:" + netConnectNames);
            Scanner commaSperated = new Scanner(netConnectNames);
            commaSperated.useDelimiter(",");

            while (commaSperated.hasNext()) {
                String connectionName = commaSperated.next().trim();
                // addNic(buf, connectionName);
                // buf.append("NIC = [\n");buf.append("NETWORK = \"");
                // buf.append(connectionName); buf.append("\"");
                // buf.append("]\n\n\n");
            }
        }

        return buf.toString();
    }

    private String VMDescriptionContext(EmotiveOVF ovf) {
        StringBuilder buf = new StringBuilder();
        // Context - requires again network attributes
        //
        // We list devices in order eth0, eth1, eth2... The context script will
        // get
        // them out and prepare the network configuration files.

        buf.append("CONTEXT = [\n");
        String separator = "";
//        buf.append("hostname = \"optimis2.leeds\",");

        int ethNumber = 0;
        if (!ovf.getNetworks().isEmpty()) {
            for (OVFNetwork ovfNetwork : ovf.getNetworks().values()) {
                String nicName = ovfNetwork.getConnectionName();

                if (nicName == null || "".equals(nicName)) {
                    String tmp = ovfNetwork.getIp();
                    if (tmp != null) {
                        buf.append(separator);
                        buf.append("IP_");
                        buf.append(ethNumber);
                        buf.append(" = \"");
                        buf.append(tmp);
                        buf.append("\"\n");
                        separator = ",\n";
                    }

                    tmp = ovfNetwork.getMac();
                    if (tmp != null) {
                        buf.append(separator);
                        buf.append("MAC_");
                        buf.append(ethNumber);
                        buf.append(" = \"");
                        buf.append(tmp);
                        buf.append("\"\n");
                        separator = ",\n";

                    }

                } else {
                    addNetworkConfigToContext(buf, separator, ethNumber,
                            nicName);
                }
                ethNumber++;
            }

            log.debug("adding the networks configured in props..");
            log.debug("networks configured:" + netConnectNames);
            Scanner commaSperated = new Scanner(netConnectNames);
            commaSperated.useDelimiter(",");

            while (commaSperated.hasNext()) {
                String connectionName = commaSperated.next().trim();
                if (!nicNames.contains(connectionName)) {
                    addNetworkConfigToContext(buf, separator, ethNumber,
                            connectionName);
                    log.debug("adding network config:" + connectionName);
                    ethNumber++;
                }
            }
        } else {

            log.debug("no network configured in ovf input, hence checking the network config from properties file");
            log.debug("networks configured:" + netConnectNames);
            Scanner commaSperated = new Scanner(netConnectNames);
            commaSperated.useDelimiter(",");

            while (commaSperated.hasNext()) {
                String connectionName = commaSperated.next().trim();
                addNetworkConfigToContext(buf, separator, ethNumber,
                        connectionName);
                ethNumber++;
                // buf.append("NIC = [\n");buf.append("NETWORK = \"");
                // buf.append(connectionName); buf.append("\"");
                // buf.append("]\n\n\n");
            }

        }

        Iterator<String> home_iter = arg1.iterator();
        Iterator<String> swap_iter = arg2.iterator();
        buf.append("HOME = \"");
        while (home_iter.hasNext()) {

            buf.append(home_iter.next());
            buf.append(" ");

        }

        buf.append("\",\n");
        buf.append("SWAP = \"");
        while (swap_iter.hasNext()) {

            buf.append(swap_iter.next());
            buf.append(" ");
        }
        buf.append("\"\n");
//        buf.append("FILES=" + "\"" + scriptlocation + "/init.sh \"");
        buf.append("]\n");
        return buf.toString();
    }

    private void addVirtualDiskConfig(TargetedDisk ovfDisk1, String pathOrURL, EmotiveOVF ovf, StringBuilder propertyName, StringBuilder buf) {

        if (pathOrURL == null) {
            pathOrURL = ovf4oneProperties(propertyName.toString(), false);
        }

        if (pathOrURL == null || "".equals(pathOrURL)) {
            StringBuilder bufx = new StringBuilder(
                    "\n.OVF file is missing mandatory path or url of the image for disk id=\"");
            buf.append(ovfDisk1.getDskName());
            buf.append(" and no default is provided ");
            throw new DRPOneException(bufx.toString(), StatusCodes.BAD_OVF);
        }
        if (!pathOrURL.startsWith(FILE_PATH_SEPARATOR)) {
            System.out.println("DRP4ONE - addVirtualDiskConfig()> FILE_PATH_SEPARATOR: " + FILE_PATH_SEPARATOR);
            String image;
            if (pathOrURL.startsWith(ONE_PROTOCOL)) {
                image = pathOrURL.substring(ONE_PROTOCOL_LEN).trim();
            } else {

                System.out.println("DRP4ONE - addVirtualDiskConfig()> Path sense protocol, pathOrURL: " + pathOrURL);

                image = pathOrURL.trim();

                //Sergio: added the home where the images are hosted
                // Sergio commented, because it will be necessary to add images at the ONE datastore
//                image = imgsHome + "/" + image;
            }

            if ("".equals(image)) {
                StringBuilder bufx = new StringBuilder(
                        "\n.OVF file has an invalid (empty) path or url of the image for disk id=\"");
                buf.append(ovfDisk1.getDskName());
                buf.append(" and no default is provided ");
                throw new DRPOneException(bufx.toString(), StatusCodes.BAD_OVF);
            }

            // Su solo añade IMAGE = , esta coma no debe ir!!
            System.out.println("DRP4ONE - addVirtualDiskConfig()> image: " + image);
            buf.append("IMAGE = \"");
            buf.append(image);
            //Sergio comenta la siguiente linea (2013/05/14)
//            buf.append("\", \n");
            buf.append("\" \n");


            if (ovfDisk1.getTarget() != null) {
                buf.append("TARGET = \"");
                buf.append(ovfDisk1.getTarget());
                buf.append("\"");
            }

        } else {
            if (ovfDisk1.getTarget() == null) {
                throw new DRPOneException("Missing mandatory target specification for a disk " + ovfDisk1.getDskName(), StatusCodes.BAD_OVF);
            }

            HashMap<String, String> diskTemplate = new HashMap<String, String>();
            diskTemplate.put("NAME", ovfDisk1.getDskName());
            diskTemplate.put("PATH", ovfDisk1.getHref());

//            String imgID = ONETemplatter.ONEImageCreate(diskTemplate);
//            diskTemplate.put("IMAGE_ID", imgID);

            String disk = ""
                    //                    + "IMAGE = \"" + ovfDisk1.getDskName()+ "\",\n"
                    //                    + "IMAGE_UNAME = \"anthony\",\n"
                    + "IMAGE_ID = " + diskTemplate.get("IMAGE_ID") + " \n";
//                    + "TYPE = \"DISK\",\n"
//                    + "CLONE = yes,\n"
//                    + "SOURCE = \"" + ovfDisk1.getHref() + "\",\n"
//                    + "TARGET = \"sda\",\n"
            //                    + "DISK_TYPE = \"BLOCK\",\n"
            //                    + "DRIVER = \"raw:\"\n";
//                    + "DRIVER = \"tap:qcow2:,tap:aio\"\n";


            buf.append(disk);

            // We are using a physical disk image
//            buf.append("TYPE=fs,\n");
//            buf.append("SOURCE = \"");
//            buf.append(pathOrURL);
//            buf.append("\",\n");
//            buf.append("TARGET = \"");
//            buf.append(ovfDisk1.getTarget());
//            buf.append("\"");

        }

        String separator = ",\n";

        // This COULD be a good system image...

    }

    private void addNic(StringBuilder buf, String connectionName) {
        // TODO Auto-generated method stub
        log.debug("Adding the network config :" + connectionName);
        System.out.println("*********** I've entered addNIC()!!!:");
        System.out.println("*********** StringBuilder buf: " + buf);
        System.out.println("*********** String connectionName: " + connectionName);
        buf.append("NIC = [\n");
        buf.append("NETWORK = \"");
        buf.append(connectionName);
        buf.append("\" \n");
        buf.append("]\n");

    }

    private void addNetworkConfigToContext(StringBuilder buf, String separator,
            int ethNumber, String nicName) {
        buf.append(separator);
        buf.append("IP_");
        buf.append(ethNumber);
        buf.append(" = \"$NIC[IP, NETWORK=\\\"");
        buf.append(nicName);
        buf.append("\\\"]\",\n");
        buf.append("MAC_");
        buf.append(ethNumber);
        buf.append(" = \"$NIC[MAC, NETWORK=\\\"");
        buf.append(nicName);
        buf.append("\\\"]\",\n");
        buf.append("NETMASK_");
        buf.append(ethNumber);
        buf.append("=\"$NETWORK[NETMASK, NAME=\\\"");
        buf.append(nicName);
        buf.append("\\\"]\",\n");
        buf.append("GATEWAY_");
        buf.append(ethNumber);
        buf.append("=\"$NETWORK[GATEWAY, NAME=\\\"");
        buf.append(nicName);
        buf.append("\\\"]\",\n");
        buf.append("BROADCAST_");
        buf.append(ethNumber);
        buf.append("=\"$NETWORK[BROADCAST, NAME=\\\"");
        buf.append(nicName);
        buf.append("\\\"]\",\n");
        /*
         * Great OpenNebula feature :) fixed size networks have NETWORK, ranged
         * networks have NETWORK_ADDRESS Therefore I set up NE
         */
        buf.append("NETWORK_ADDRESS_");
        buf.append(ethNumber);
        buf.append("=\"$NETWORK[NETWORK, NAME=\\\"");
        buf.append(nicName);
        buf.append("\\\"]\",\n");
        buf.append("HOSTNAME=\"venuscdebianbase-$VMID\",\n");
        separator = ",\n";

    }

    /*
     * private void notifyUsageTracker(int vmId, String user) {
     * 
     * String usageTrackerUrl = ovf4oneProperties(USAGE_TRACKER_URL,false);
     * 
     * // if this property is empty (empty string or null) disable // the
     * tracker notifications. if( usageTrackerUrl == null ||
     * "".equals(usageTrackerUrl.trim())) return;
     * 
     * UsageTrackerRestAPI ut = new UsageTrackerRestAPI(usageTrackerUrl);
     * 
     * VMUsageRecord ur = new VMUsageRecord();
     * 
     * // set the identity of the creator (ovf4one) ur.setCreatorId(
     * this.getClass().getName() );
     * 
     * // set the creation time (NOW) Calendar cal = Calendar.getInstance();
     * ur.setCreateTime(cal); ur.setStartTime(cal); // who actually consumed the
     * resource (received in http header) ur.setConsumerId( (user==null) ?
     * "ovf4one unauthenticated" : user);
     * 
     * String resourceOwner = ovf4oneProperties(INFRASTRUCTURE_ID, false);
     * 
     * resourceOwner = ( resourceOwner == null ) ? "unspecified infrasturecture"
     * : resourceOwner;
     * 
     * ur.setResourceOwner(resourceOwner);
     * 
     * ur.setRefVM(""+vmId);
     * 
     * try { ut.insertUsageRecord(ur); } catch(Exception e) { // Ignore any
     * exception but log it
     * log.warn("Notify Usage Tracker error in insertUsageRecord()",e);
     * 
     * }
     * 
     * }
     */

    /*
     * ==============================================================
     * ==============================================================
     * 
     * private helpers for the getCompute method
     */
    private OCAComputeWrapper parseOcaCompute(String s) {
        OCAComputeWrapper rv = null;
        StringBuilder cause = new StringBuilder();
        try {
            rv = OCAComputeWrapperFactory.parse(s);
        } catch (SAXException se) {
            throw new DRPOneException("XML Parsing error", se,
                    StatusCodes.INTERNAL);
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
            log.error(cause.toString());
            if (log.isTraceEnabled()) {
                log.trace(cause, e);
            }
            throw new DRPOneException(cause.toString(), e,
                    StatusCodes.ONE_FAILURE);
        }
        return rv;
    }

    /*
     * ==============================================================
     * ==============================================================
     * 
     * private helpers for the getComputes method
     */
    private OCAComputeListWrapper parseOcaComputeList(String s) {
        OCAComputeListWrapper rv = null;
        StringBuilder cause = new StringBuilder();
        try {
            rv = OCAComputeListWrapperFactory.parseList(s);
        } catch (SAXException se) {
            throw new DRPOneException("XML Parsing error", se,
                    StatusCodes.INTERNAL);
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
            log.error(cause.toString());
            if (log.isTraceEnabled()) {
                log.trace(cause, e);
            }
            throw new DRPOneException(cause.toString(), e,
                    StatusCodes.ONE_FAILURE);
        }
        return rv;

    }

    // @madigiro
    // Saint - Thu Nov 10 10:39:12 2011
    //
    // Added a flag to not re-read the properties each time we work on them.
    String ovf4oneProperties(String ovfProperty, boolean refresh) {
        if (refresh || (props == null)) {
            props = reloadProperties();
        }

        return props.getProperty(ovfProperty);
    }

    private Properties getProperties() {
        if (props == null) {
            props = reloadProperties();
        }
        return props;
    }

    private Properties reloadProperties() {
        Properties props = new Properties();
        try {
            InputStream is = getClass().getResourceAsStream(PROPFILE);

            // read by smendoza
//            String ONEproperties = ovf4oneProperties.properties;
//            InputStream is = new ByteArrayInputStream(ONEproperties.getBytes("UTF-8"));
            props.load(is);
            // close the stream NOW, don't wait for the garbage collector
            is.close();

        } catch (FileNotFoundException e) {
            throw new OpenNebulaConfigurationError("\n.File NOT FOUND "
                    + PROPFILE + ": " + e.getMessage());
        } catch (IOException e) {
            throw new OpenNebulaConfigurationError("\n. IO exception  "
                    + PROPFILE + ": " + e.getMessage());
        }

        return props;
    }

    // Saint - Thu Oct 6 11:47:30 2011
    //
    // Method to see if we can connect to the VM on port 22 on some
    // nic.
    private boolean port22Reached(String ipAddress) {
        boolean gotPort22Connection = false;

        try {

            InetAddress toPoll = InetAddress.getByName(ipAddress);
            boolean reachable = false;
            try {
                // We have to answer within a WS timeout
                reachable = toPoll.isReachable(5000);
            } catch (IOException e) {
                // same as unreachable, log the problem
                if (log.isDebugEnabled()) {
                    log.error(e);
                } else {
                    log.error(e.getMessage());
                }
            }

            if (reachable) {
                // see if we can ssh connect to port 22
                try {
                    Socket socket = new Socket(toPoll, 22);
                    gotPort22Connection |= socket.isConnected();
                    socket.close();
                } catch (IOException e) {
                    // Happens when there's no one listening
                    // on port 22, for example. Anyway, it
                    // means we can't connect
                }

            }

        } catch (UnknownHostException e) {
            // Ignore, it's not a valid ip!
        } catch (SecurityException e) {

            if (log.isDebugEnabled()) {
                log.fatal(e);
            } else {
                log.fatal(e.getMessage());
            }

            throw e;

        }
        return gotPort22Connection;

    }

    // /Saint
    // Saint - Thu Oct 6 17:32:22 2011
    //
    // Handles the OpenNebula to libvirt state conversion
    private State one2libvrtStatus(int vmStatus, int lcmStatus,
            boolean gotPort22Connection) {
        State rv = null;

        if (lcmStatus == LcmStatusCodes.UNKNOWN) {
            return State.NOSTATE;
        }

        switch (vmStatus) {
            case VmStatusCodes.INIT:
                rv = State.NOSTATE;
                break;

            case VmStatusCodes.PENDING:
                rv = State.NOSTATE;
                break;

            case VmStatusCodes.HOLD:
                rv = State.BLOCKED;
                break;

            case VmStatusCodes.ACTIVE:
                //rv = (gotPort22Connection) ? State.RUNNING_READY : State.RUNNING;
                rv = (gotPort22Connection) ? State.RUNNING_READY : State.RUNNING;
                break;

            case VmStatusCodes.STOPPED:
                rv = State.SHUTOFF;
                break;

            case VmStatusCodes.SUSPENDED:
                rv = State.PAUSED;
                break;

            case VmStatusCodes.DONE:
                rv = State.SHUTOFF;
                break;

            case VmStatusCodes.FAILED:
                rv = State.CRASHED;
                break;

            default:
                rv = State.NOSTATE;

        }

        return rv;

    }

    private String one2libvirtStatusAsText(int vmStatus, int lcmStatus,
            boolean gotPort22Connection) {
        switch (one2libvrtStatus(vmStatus, lcmStatus, gotPort22Connection)) {
            case NOSTATE:
                return "NOSTATE";

            case BLOCKED:
                return "BLOCKED";

            //case RUNNING_READY:
            case RUNNING_READY:
                return "RUNNING_AVAILABLE";

            case RUNNING:
                return "RUNNING";

            case SHUTOFF:
                return "SHUTOFF";

            case PAUSED:
                return "PAUSED";

            case CRASHED:
                return "CRASHED";

            default:
                return "NOSTATE";
        }
    }

    private void mount_disks() {
    }

    /**
     * ********************* NODE MANAGEMENT methods.
     * **************************
     */

    /* =================== END METHODS ==================== */
    public static void main(String[] args) throws Exception {
        // A simple test for DRP4OVF methods
        Client ocaClient = null;

        // First case: occi-compute create http://localhost:8080/ovf4one
        // xxxx.xml
        try {
            ocaClient = new Client();
        } catch (UniformInterfaceException u) {
            System.out.println("Error main : " + u.getMessage());
            ClientResponse cl = u.getResponse();
            if (cl.getStatus() == 500) {
                throw new DRPOneException(
                        "Internal error, if ocaClient is null please insert correct ocaclient and try again.",
                        StatusCodes.INTERNAL);
            } else if (cl.getStatus() == 404) {
                throw new DRPOneException(
                        "Internal error, incorrect user or password, insert correct user or/and password and try againn",
                        StatusCodes.UNAUTHORIZED);
            } else {
                throw new DRPOneException("Error generic: " + u.getMessage(),
                        StatusCodes.BAD_REQUEST);
            }
        }
        try {
            OneResponse rc = VirtualMachine.info(ocaClient, 14);
            if (rc.isError()) {
                System.out.println("Error in the method "
                        + rc.getClass().getMethods() + rc.getErrorMessage());
            }
            System.out.println("Print result of method : " + rc.getMessage());
        } catch (UniformInterfaceException uex) {

            System.err.print("An error ocurred: ");
            ClientResponse cr = uex.getResponse();
            if (cr.getStatus() == 426) {
                throw new DRPOneException(
                        "Name was already assigned in this VtM",
                        StatusCodes.NAME_EXIST);
            } else if (cr.getStatus() == 420) {
                throw new DRPOneException(
                        "Virtual Machine detected some problem",
                        StatusCodes.INTERNAL_ERROR_PARSE);
            } else if (cr.getStatus() == 425) {
                throw new DRPOneException(
                        "Not enough resources (for example memory)",
                        StatusCodes.NOT_RESOURCES);
            } else if (cr.getStatus() == 427) {
                throw new DRPOneException("Creation failed",
                        StatusCodes.VM_NOT_EXIST);
            } else if (cr.getStatus() == 428) {
                throw new DRPOneException("No available nodes",
                        StatusCodes.NOT_AVAILABLE_NODES);
            } else if (cr.getStatus() == 432) {
                throw new DRPOneException("Cannot recognize address",
                        StatusCodes.CANNOT_ADDRESS);
            } else if (cr.getStatus() == 433) {
                throw new DRPOneException(
                        "Domain with this name already present in the system",
                        StatusCodes.DOMAIN_NAME_EXIST);
            } else if (cr.getStatus() == 424) {
                throw new DRPOneException("Error 424",
                        StatusCodes.FAILED_DEPENDECIES);
            } else {
                throw new DRPOneException("Other Undefined Error",
                        StatusCodes.BAD_REQUEST);
            }
        }// end catch

    }

    private Client getClient(HttpServletRequest request) throws DRPOneException {
        /*
         String userid = ""; // "venus";
         String endpoint = ovf4oneProperties(ONE_XMLRPC, false);
         Client rv = null;

         userid = SecureSessionInfo.getUserDN(request);

         // TODO: u
         rv = clientProvider.provideClient(userid, ocaClientProviderURL,
         ocaClientProviderQuery, endpoint, getProperties());
         */
        Client rv = null;
        try {
            String secret = this.user + ":" + this.passwd;
            rv = new Client(secret, this.rpcserver);
        } catch (Exception e) {
            throw new DRPOneException();
        }

        return rv;
    }

    /*
     * Other methods vor emotivecloud interoperability
     */
    @GET
    @Path("/environments/{envid}/status")
    // antic state, falta el destroy per arreglar, al VtM ho fa be
    @Produces("text/plain")
    public String getState(@PathParam("envid") String envId,
            @Context HttpServletRequest request) throws DRPOneException {
        String status = "Unknown";

        OneResponse rc = getComputeHelper(envId, request);

        OCAComputeWrapper oca = rc2OCA(rc);

        Collection<OCANicWrapper> nicList = oca.getTemplate().getNics()
                .values();

        int vmStatus = oca.getState();
        int lcmState = oca.getLcmState();
        boolean doThePoll = vmStatus == VmStatusCodes.ACTIVE
                && lcmState == LcmStatusCodes.RUNNING;

        boolean gotPort22Connection = false;

        if (doThePoll) {
            for (OCANicWrapper nic : nicList) {
                String ipAddress = nic.getIp();

                if (ipAddress != null) {
                    gotPort22Connection |= port22Reached(ipAddress);
                }

            }
        }

        status = one2libvirtStatusAsText(vmStatus, lcmState,
                gotPort22Connection);

        return status;
    }

    @GET
    @Path("/environments/{envid}/{taskid}")
    @Produces("text/plain")
    public String getActivityStatus(@PathParam("envid") String envid,
            @PathParam("taskid") String taskid) throws DRPOneException {
        // String status = null;
        throwNotImplemented("getActivityStatus(String, String)");
        return null;
    }

    private void throwNotImplemented(String name) throws DRPOneException {
        StringBuilder buf = new StringBuilder(39 + name.length());
        buf.append("Emotve specific method ");
        buf.append(name);
        buf.append(" not implemented");

        throw new DRPOneException(buf.toString(), StatusCodes.NOT_IMPLEMENTED);



    }

    /*
     * This inner class is used to sort the disks so thant they are presented to
     * virsh ordered according to their target/physical device name
     * 
     * It "simply adds Comparable to OVFDisk". The OVFDisk behaviour is supplied
     * by the original OVFDisk itself.
     */
    class TargetedDisk extends OVFDisk implements Comparable<TargetedDisk> {

        public final static String PROP_TARGET = ".TARGET";
        String target;
        String dskName;
        int dskNameLen;
        OVFDisk ovfDisk;

        public int compareTo(TargetedDisk that) {
            if (this.target == null && that.target == null) {
                return 0;
            }
            if (this.target == null) {
                return 1;
            } else if (that.target == null) {
                return -1;
            } else {
                return this.target.compareTo(that.target);
            }
        }

        public TargetedDisk(String dskName1, OVFDisk newOvfDisk, EmotiveOVF ovf) {
            ovfDisk = newOvfDisk;
            dskName = newOvfDisk.getId();
            if (dskName == null) {
                throw new DRPOneException(
                        "\n.OVF file is missing mandatory disk Id ",
                        StatusCodes.NOT_ACCEPTABLE);
            }

            dskNameLen = dskName.length();
            if (dskNameLen == 0) {
                throw new DRPOneException("\n.OVF file has a zero lenght ID",
                        StatusCodes.FAILED_DEPENDECIES);
            }

            String propertyName = dskName + PROP_TARGET;

            target = ovf.getProductProperty(propertyName.toString());

            if (target == null) // @madigiro - 21 july
            {
                target = ovf4oneProperties(dskName1 + PROP_TARGET, false);
            }

        }

        public boolean equals(Object obj) {
            if (!(obj instanceof TargetedDisk)) {
                return false;
            }

            return target.equals(((TargetedDisk) obj).target);
        }

        public Long getCapacityMB() {
            return ovfDisk.getCapacityMB();
        }

        public String getHref() {
            return ovfDisk.getHref();
        }

        public String getId() {
            return ovfDisk.getId();
        }

        public int hashCode() {
            return ovfDisk.hashCode();
        }

        public String toString() {
            return ovfDisk.toString();
        }

        public String getTarget() {
            return target;
        }

        public void setTarget(String target) {
            this.target = target;
        }

        public String getDskName() {
            return dskName;
        }

        public void setDskName(String dskName) {
            this.dskName = dskName;
        }

        public int getDskNameLen() {
            return dskNameLen;
        }

        public void setDskNameLen(int dskNameLen) {
            this.dskNameLen = dskNameLen;
        }
    }
}