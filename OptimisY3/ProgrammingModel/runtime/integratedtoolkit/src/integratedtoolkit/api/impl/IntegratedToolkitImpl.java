/*
 *  Copyright 2002-2013 Barcelona Supercomputing Center (www.bsc.es)
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package integratedtoolkit.api.impl;

import integratedtoolkit.ITConstants;
import integratedtoolkit.api.ITExecution;
import integratedtoolkit.api.IntegratedToolkit;
import integratedtoolkit.components.DataAccess.AccessMode;
import integratedtoolkit.components.impl.RuntimeMonitor;
import integratedtoolkit.components.impl.TaskDispatcher;
import integratedtoolkit.components.impl.TaskProcessor;
import integratedtoolkit.loader.LoaderAPI;
import integratedtoolkit.loader.total.ObjectRegistry;
import integratedtoolkit.log.Loggers;
import integratedtoolkit.types.Parameter;
import integratedtoolkit.types.Parameter.BasicTypeParameter;
import integratedtoolkit.types.Parameter.DependencyParameter.FileParameter;
import integratedtoolkit.types.Parameter.DependencyParameter.ObjectParameter;
import integratedtoolkit.types.data.AccessParams.FileAccessParams;
import integratedtoolkit.types.data.AccessParams.ObjectAccessParams;
import integratedtoolkit.types.data.DataAccessId;
import integratedtoolkit.types.data.DataAccessId.RAccessId;
import integratedtoolkit.types.data.DataAccessId.RWAccessId;
import integratedtoolkit.types.data.DataAccessId.WAccessId;
import integratedtoolkit.types.data.DataInstanceId;
import integratedtoolkit.types.data.Location;
import integratedtoolkit.types.data.ResultFile;
import integratedtoolkit.util.OptimisComponents;
import integratedtoolkit.util.RuntimeConfigManager;
import integratedtoolkit.util.Serializer;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.URI;
import java.net.URL;
import java.net.UnknownHostException;
import java.nio.channels.ClosedChannelException;
import java.util.List;
import java.util.logging.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

public class IntegratedToolkitImpl implements IntegratedToolkit, ITExecution, LoaderAPI {

    // Exception constants definition
    private static final String UNKNOWN_HOST_ERR = "Cannot determine the IP address of the local host";
    private static final String FILE_NAME_ERR = "Error parsing file name";
    private static final String TEMP_DIR_ERR = "Error creating temp dir";
    private static final String OBJECT_SERIALIZE_ERR = "Error serializing object to file";
    private static final String OBJECT_DESERIALIZE_ERR = "Error deserializing object from file";
    private static final String WRONG_DIRECTION_ERR = "Error: invalid parameter direction: ";
    // URI beginning
    private static final String FILE_URI = "file:";
    private static final String SHARED_URI = "shared:";
    // Components
    private TaskProcessor TP;
    private TaskDispatcher TD;
    //Monitor 
    RuntimeMonitor monitor;
    // Application attributes
    public static String appHost;
    private String appWorkingDir;
    // Object registry
    private ObjectRegistry oReg;
    // Temp dir
    private File tempDir;
    private String tempDirPath;
    public static Location masterSafeLocation;
    // Logger
    private static final Logger logger = Logger.getLogger(Loggers.API);
    private static final boolean debug = logger.isDebugEnabled();

    // Configure log4j for the JVM where the application and the IT API belong
    static {
        //jorgee: Code Added to support the configuration files
        String properties_loc = System.getProperty(ITConstants.IT_CONFIG_LOCATION);
        if (properties_loc == null) {
            URL url = IntegratedToolkitImpl.class.getClassLoader().getResource(ITConstants.IT_CONFIG);
            if (url != null) {
                properties_loc = url.getFile();
            }
        }
        setPropertiesFromRuntime(properties_loc);
        PropertyConfigurator.configure(System.getProperty(ITConstants.LOG4J));
    }

    //jorgee: Code Added to support the configuration files
    private static void setPropertiesFromRuntime(String properties_loc) {
        try {
            if (properties_loc != null) {
                RuntimeConfigManager manager = new RuntimeConfigManager(properties_loc);
                if (manager.getLog4jConfiguration() != null) {
                    System.setProperty(ITConstants.LOG4J, manager.getLog4jConfiguration());
                }
                if (manager.getResourcesFile() != null) {
                    System.setProperty(ITConstants.IT_RES_FILE, manager.getResourcesFile());
                }
                if (manager.getResourcesSchema() != null) {
                    System.setProperty(ITConstants.IT_RES_SCHEMA, manager.getResourcesSchema());
                }
                if (manager.getProjectFile() != null) {
                    System.setProperty(ITConstants.IT_PROJ_FILE, manager.getProjectFile());
                }
                if (manager.getProjectSchema() != null) {
                    System.setProperty(ITConstants.IT_PROJ_SCHEMA, manager.getProjectSchema());
                }
                if (manager.getGATBrokerAdaptor() != null) {
                    System.setProperty(ITConstants.GAT_BROKER_ADAPTOR, manager.getGATBrokerAdaptor());
                }
                if (manager.getGATFileAdaptor() != null) {
                    System.setProperty(ITConstants.GAT_FILE_ADAPTOR, manager.getGATFileAdaptor());
                }
                if (manager.getITLib() != null) {
                    System.setProperty(ITConstants.IT_LIB, manager.getITLib());
                }
                if (manager.getMonitorInterval() > 0) {
                    System.setProperty(ITConstants.IT_MONITOR, Long.toString(manager.getMonitorInterval()));
                }
                if (manager.getGATAdaptor() != null) {
                    System.setProperty(ITConstants.GAT_ADAPTOR, manager.getGATAdaptor());
                }
                if (manager.getManifestLocation() != null) {
                    System.setProperty(ITConstants.IT_MANIFEST_LOCATION, manager.getManifestLocation());
                }
                System.setProperty(ITConstants.IT_INTERACT_PERIOD, Long.toString(manager.getOptimisPeriod()));
                System.setProperty(ITConstants.IT_LANG, manager.getLang());
                System.setProperty(ITConstants.IT_GRAPH, Boolean.toString(manager.isGraph()));
                System.setProperty(ITConstants.IT_TRACING, Boolean.toString(manager.isTracing()));
                System.setProperty(ITConstants.IT_CONTEXT, manager.getContext());
                System.setProperty(ITConstants.IT_LICENSE_CERTIFICATES, manager.getCertificatesContext());
                System.setProperty(ITConstants.IT_SCHEDULER_COMPONENT, manager.getSchedulerComponent());
                System.setProperty(ITConstants.IT_COMPONENT, manager.getComponent());
                //System.setProperty(ITConstants.IT_TO_FILE,Boolean.toString(manager.isToFile()));
            } else {
                if (System.getProperty(ITConstants.IT_RES_SCHEMA) == null) {
                    System.setProperty(ITConstants.IT_RES_SCHEMA, System.getenv("IT_HOME") + "/xml/resources/resource_schema.xsd");
                }
                if (System.getProperty(ITConstants.IT_PROJ_SCHEMA) == null) {
                    System.setProperty(ITConstants.IT_PROJ_SCHEMA, System.getenv("IT_HOME") + "/xml/projects/project_schema.xsd");
                }
                if (System.getProperty(ITConstants.GAT_ADAPTOR) == null) {
                    System.setProperty(ITConstants.GAT_ADAPTOR, System.getenv("GAT_LOCATION") + ITConstants.GAT_ADAPTOR_LOC);
                }
                if (System.getProperty(ITConstants.IT_INTERACT_PERIOD) == null) {
                    System.setProperty(ITConstants.IT_INTERACT_PERIOD, "20000");
                }
            }
        } catch (Exception e) {
            System.err.println("WARN: Properties file could not be read");
        }
    }

    public IntegratedToolkitImpl() {
        OptimisComponents.init();
        // Initialization of application attributes
        try {
            InetAddress localHost = InetAddress.getLocalHost();
            appHost = localHost.getCanonicalHostName();
        } catch (UnknownHostException e) {
            logger.fatal("Error: " + UNKNOWN_HOST_ERR, e);
            System.exit(1);
        }
        appWorkingDir = System.getProperty("user.dir") + "/";

        /* Create a temp dir where to store:
         * - Files whose first opened stream is an input one
         * - Object files
         */
        try {
            this.tempDir = File.createTempFile("itf", "tmp", new File(System.getProperty("user.dir")));
            if (!tempDir.delete() || !tempDir.mkdir()) {
                throw new IOException("Error creating temp dir");

            }
            this.tempDirPath = tempDir.getCanonicalPath() + File.separatorChar;
        } catch (IOException e) {
            logger.fatal(TEMP_DIR_ERR, e);
            System.exit(1);
        }

        logger.info("Deploying the Integrated Toolkit");
    }

    // Integrated Toolkit user interface implementation
    public void startIT() {

        logger.info("Starting the Integrated Toolkit");

        logger.info("Initializing components");
        try {
            TP = new TaskProcessor(appHost, tempDirPath);
        } catch (Exception ex) {
            logger.fatal("Can not instantiate the Task Processor", ex);
            System.exit(1);
        }

        if (OptimisComponents.isScheduler(appHost)) {
            System.out.println("Is scheduler");
            TD = new TaskDispatcher();
        } else {
            TD = new TaskDispatcher(OptimisComponents.getScheduler());
        }

        TP.setTD(TD);
        String sleepTime = System.getProperty(ITConstants.IT_MONITOR);
        if (sleepTime != null) {
            monitor = new RuntimeMonitor(TP, TD, Long.parseLong(sleepTime));
        }



        masterSafeLocation = new Location(appHost, tempDirPath);

        logger.info("Ready to process tasks");
    }

    public void executeTask(Long appId,
            String methodClass,
            String methodName,
            boolean hasTarget,
            int parameterCount,
            Object... parameters) {

        if (debug) {
            logger.debug("Creating task from method " + methodName + " in " + methodClass);
            logger.debug("There " + (parameterCount > 1 ? "are " : "is ") + parameterCount + " parameter" + (parameterCount > 1 ? "s" : ""));
        }

        Parameter[] pars = processParameters(parameterCount, parameters);

        TP.newTask(appId, methodClass, methodName, hasTarget, pars);
    }

    public void executeTask(Long appId,
            String namespace,
            String service,
            String port,
            String operation,
            boolean hasTarget,
            int parameterCount,
            Object... parameters) {

        if (debug) {
            logger.debug("Creating task from service " + service + ", namespace " + namespace + ", port " + port + ", operation " + operation);
            logger.debug("There " + (parameterCount > 1 ? "are " : "is ") + parameterCount + " parameter" + (parameterCount > 1 ? "s" : ""));
        }

        Parameter[] pars = processParameters(parameterCount, parameters);

        TP.newTask(appId, namespace, service, port, operation, hasTarget, pars);
    }

    public void noMoreTasks(Long appId, boolean terminate) {
        logger.info("No more tasks for app " + appId);

        // Wait until all tasks have finished
        TP.noMoreTasks(appId);
        // Block the data ids of the result files for obsolete clean and get them
        List<ResultFile> resFiles = TP.blockAndGetResultFiles(appId);

        if (!resFiles.isEmpty()) {
            // Block until all result files are transferred back to the original host
            try {
                TD.transferBackResultFiles(resFiles);
            } catch (Exception e) {
                logger.error("ERROR sending message to the Task Dispatcher", e);
                System.exit(1);
            }
            // Allow the result files to be marked as obsolete again
            TP.unblockResultFiles(resFiles);
        }

        // Delete temporary files
        deleteTempFiles();
    }

    public void stopIT(boolean terminate) {
        logger.info("Stopping IT");
        try {
            TD.deleteIntermediateFiles();
        } catch (Exception e) {
            logger.error("ERROR sending message to the Task Dispatcher", e);
            System.exit(1);
        }
        if (terminate) {
            TP.shutdown();
            clean();
        }

        logger.info("Integrated Toolkit stopped");
    }

    public void clean() {
        tempDir.delete();
        if (monitor != null) {
            monitor.cleanup();
        }
        // TODO: Remote cleanup for orchestration
        TP.cleanup();
        TD.cleanup();
    }

    public String openFile(String fileName, OpenMode m) {
        logger.info("Opening file " + fileName + " in mode " + m);

        // Parse the file name and translate the access mode
        String name = null, path = null, host = null;
        try {
            String[] hostPathName = extractHostPathName(fileName);
            host = hostPathName[0];
            path = hostPathName[1];
            name = hostPathName[2];
        } catch (Exception e) {
            logger.fatal(FILE_NAME_ERR, e);
            System.exit(1);
        }

        // If the file is local and no task has accessed it before, just work with its original source
        boolean alreadyAccessed = TP.alreadyAccessed(name, path, host);
        if (host.equals(appHost) && !alreadyAccessed) {
            return path + name;
        }

        AccessMode am = null;
        switch (m) {
            case READ:
                am = AccessMode.R;
                break;
            case WRITE:
                am = AccessMode.W;
                break;
            case APPEND:
                am = AccessMode.RW;
                break;
        }

        // Tell the DM that the application wants to access a file.
        FileAccessParams fap = new FileAccessParams(am, name, path, host);
        DataAccessId faId = TP.registerDataAccess(fap);

        if (am != AccessMode.W) {
            // Block until the last writer task for the file has finished
            TP.waitForTask(faId.getDataId());
            /* Transfer the file to the application host (the IT will know if this is necessary
             * or not, depending on the access mode).
             * Blocking operation
             */
            try {
                TD.transferFileForOpen(faId, new Location(appHost, appWorkingDir));
            } catch (Exception e) {
                logger.error("ERROR sending message to the Task Dispatcher", e);
                System.exit(1);
            }

        }

        // Obtain the renaming for the file
        String rename = null;
        switch (am) {
            case R:
                RAccessId ra = (RAccessId) faId;
                rename = ra.getReadDataInstance().getRenaming();
                break;
            case W:
                WAccessId wa = (WAccessId) faId;
                rename = wa.getWrittenDataInstance().getRenaming();
                try {
                    TD.newDataVersion(wa.getWrittenDataInstance(), null, null, tempDirPath, fileName);
                } catch (Exception e) {
                    logger.error("ERROR sending message to the Task Dispatcher", e);
                    System.exit(1);
                }
                break;
            case RW:
                /* Get the renaming for the written version
                 * The file has already been transferred with this renaming
                 */
                RWAccessId rwa = (RWAccessId) faId;
                rename = rwa.getWrittenDataInstance().getRenaming();
                break;
        }



        /* Return the path that the application must use to access the (renamed) file
         * The file won't recover its original name until stopIT is called
         */
        return appWorkingDir + rename;
    }

    // IT_Execution interface implementation
    private Parameter[] processParameters(int parameterCount, Object[] parameters) {
        Parameter[] pars = new Parameter[parameterCount];

        // Parameter parsing needed, object is not serializable
        int i = 0;
        for (int npar = 0; npar < parameterCount; npar++) {
            ParamType type = (ParamType) parameters[i + 1];
            ParamDirection direction = (ParamDirection) parameters[i + 2];

            if (debug) {
                logger.debug("  Parameter " + (npar + 1) + " has type " + type.name());
            }
            switch (type) {
                case FILE_T:
                    String name = null,
                     path = null,
                     host = null;
                    try {
                        String[] hostPathName = extractHostPathName((String) parameters[i]);
                        host = hostPathName[0];
                        path = hostPathName[1];
                        name = hostPathName[2];
                    } catch (Exception e) {
                        logger.fatal(FILE_NAME_ERR, e);
                        System.exit(1);
                    }
                    pars[npar] = new FileParameter(direction, name, path, host);
                    break;

                case OBJECT_T:
                    pars[npar] = new ObjectParameter(direction,
                            parameters[i],
                            oReg.newObjectParameter(parameters[i])); // hashCode
                    break;

                default:
                    /* Basic types (including String).
                     * The only possible direction is IN, warn otherwise
                     */
                    if (direction != ParamDirection.IN) {
                        logger.warn(WRONG_DIRECTION_ERR
                                + "Parameter " + npar
                                + " has a basic type, therefore it must have INPUT direction");
                    }
                    pars[npar] = new BasicTypeParameter(type, ParamDirection.IN, parameters[i]);
                    break;
            }
            i += 3;
        }

        return pars;
    }

    // LoaderAPI interface implementation
    public String getFile(String fileName, String destDir) {
        // Parse the file name
        String name = null, path = null, host = null;
        try {
            String[] hostPathName = extractHostPathName(fileName);
            host = hostPathName[0];
            path = hostPathName[1];
            name = hostPathName[2];
        } catch (Exception e) {
            logger.fatal(FILE_NAME_ERR, e);
            System.exit(1);
        }

        // The file is local; if no task has accessed it before, just work with its original source
        boolean alreadyAccessed = TP.alreadyAccessed(name, path, host);
        if (!alreadyAccessed) {
            return path + name;
        }

        // Tell the DM that the application wants to access a file.
        FileAccessParams fap = new FileAccessParams(AccessMode.R, name, path, host);
        DataAccessId faId = TP.registerDataAccess(fap);

        // Wait until the last writer task for the file has finished
        TP.waitForTask(faId.getDataId());

        // Transfer the file to the application host (Blocking operation)
        try {
            TD.transferFileRaw(faId, new Location(appHost, destDir));
        } catch (Exception e) {
            logger.error("ERROR sending message to the Task Dispatcher", e);
            System.exit(1);
        }
        // Obtain the renaming for the file
        RAccessId ra = (RAccessId) faId;
        String rename = ra.getReadDataInstance().getRenaming();

        // Return the name of the file (a renaming) on which the stream will be opened
        return destDir + rename;
    }

    public Object getObject(Object o, int hashCode, String destDir) {
        /* We know that the object has been accessed before by a task, otherwise
         * the ObjectRegistry would have discarded it and this method
         * would not have been called.
         */
        logger.debug("Getting object with hash code " + hashCode);

        DataInstanceId rdId = TP.getLastDataAccess(hashCode);
        if (TP.isHere(rdId)) {
            return null;  // check if we already have the value here
        }

        // Tell the DIP that the application wants to access an object
        ObjectAccessParams oap = new ObjectAccessParams(AccessMode.RW, o, hashCode);
        DataAccessId oaId = TP.registerDataAccess(oap);
        rdId = ((RWAccessId) oaId).getReadDataInstance();
        String rRename = rdId.getRenaming();
        DataInstanceId wId = ((RWAccessId) oaId).getWrittenDataInstance();
        String wRename = wId.getRenaming();
        TP.newVersionSameValue(rRename, wRename);

        // Wait until the last writer task for the object has finished
        TP.waitForTask(oaId.getDataId());
        // TODO: Check if the object was already piggybacked in the task notification

        // Ask for the object
        Object oUpdated = null;
        try {
            oUpdated = TD.transferObject(oaId, destDir, wRename);
            if (oUpdated == null) {
                /* The Object didn't come from a WS but was transferred from a worker
                 * Deserialize the object from the file 
                 */
                try {
                    oUpdated = Serializer.deserialize(destDir + wRename);
                } catch (Exception e) {
                    logger.fatal(OBJECT_DESERIALIZE_ERR + ": " + destDir + wRename, e);
                    System.exit(1);
                }
            }
            TP.checkObsolete(rdId);
            TP.setObjectVersionValue(wRename, oUpdated);
            return oUpdated;
        } catch (Exception e) {
            logger.fatal("Can not request object transfer to the TD", e);
            System.exit(1);
        }
        return null;
    }

    public void serializeObject(Object o, int hashCode, String destDir) {
        String rename = TP.getLastRenaming(hashCode);

        try {
            Serializer.serialize(o, destDir + rename);
        } catch (Exception e) {
            logger.fatal(OBJECT_SERIALIZE_ERR + ": " + destDir + rename, e);
            System.exit(1);
        }
    }

    public void setObjectRegistry(ObjectRegistry oReg) {
        this.oReg = oReg;
    }

    public String getTempDir() {
        return tempDirPath;
    }

    public void deleteTempFiles() {
        // Delete the temp dir
        for (File f : tempDir.listFiles()) {
            f.delete();
        }
    }

    // Private method for file name parsing. TODO: Logical file names?
    private String[] extractHostPathName(String fullName) throws Exception {
        String name, path, host;

        if (fullName.startsWith(FILE_URI)) {
            /* URI syntax with host name and absolute path, e.g. "file://bscgrid01.bsc.es/home/etejedor/file.txt"
             * Only used in grid-aware applications, using IT API and partial loader,
             * since total loader targets sequential applications that use local files.
             */
            URI u = new URI(fullName);
            host = u.getHost();
            String fullPath = u.getPath();
            int pathEnd = fullPath.lastIndexOf("/");
            path = fullPath.substring(0, pathEnd + 1);
            name = fullPath.substring(pathEnd + 1);
        } else if (fullName.startsWith(SHARED_URI)) {
            URI u = new URI(fullName);
            host = "shared:" + u.getHost();
            String fullPath = u.getPath();
            int pathEnd = fullPath.lastIndexOf("/");
            path = fullPath.substring(0, pathEnd + 1);
            name = fullPath.substring(pathEnd + 1);
        } else {
            // Local file, format will depend on OS
            File f = new File(fullName);
            String canonicalPath = f.getCanonicalPath();
            name = f.getName();
            path = canonicalPath.substring(0, canonicalPath.length() - name.length());
            host = null;
        }
        return new String[]{host, path, name};
    }

    public boolean deleteFile(String fileName) {

        logger.info("Deleting file " + fileName);

        // Parse the file name and translate the access mode
        String name = null, path = null, host = null;
        try {
            String[] hostPathName = extractHostPathName(fileName);
            host = hostPathName[0];
            path = hostPathName[1];
            name = hostPathName[2];
        } catch (Exception e) {
            logger.fatal(FILE_NAME_ERR, e);
            System.exit(1);
        }
        TP.markForDeletion(name, path, host);

        return true;



    }
}
