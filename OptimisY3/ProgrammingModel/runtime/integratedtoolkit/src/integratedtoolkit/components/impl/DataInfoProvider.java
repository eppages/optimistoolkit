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
package integratedtoolkit.components.impl;

import integratedtoolkit.components.DataAccess;
import integratedtoolkit.components.DataInformation;
import integratedtoolkit.log.Loggers;
import integratedtoolkit.types.data.*;
import integratedtoolkit.types.data.AccessParams.FileAccessParams;
import integratedtoolkit.types.data.AccessParams.ObjectAccessParams;
import integratedtoolkit.types.data.DataAccessId.RAccessId;
import integratedtoolkit.types.data.DataAccessId.RWAccessId;
import integratedtoolkit.types.data.DataAccessId.WAccessId;
import integratedtoolkit.util.Serializer;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeMap;
import java.util.TreeSet;
import org.apache.log4j.Logger;

public class DataInfoProvider implements DataAccess, DataInformation {

    // Constants definition
    private static final String SERIALIZATION_ERR = "Error serializing object to a file";
    // Client interfaces
    private TaskDispatcher TD;
    // Map: filename:host:path -> file identifier
    private TreeMap<String, Integer> nameToId;
    // Map: hash code -> object identifier
    private TreeMap<Integer, Integer> codeToId;
    // Map: file identifier -> file information
    private TreeMap<Integer, DataInfo> idToData;
    // Map: Object_Version_Renaming -> Object value
    private TreeMap<String, Object> renamingToValue; // TODO: Remove obsolete from here
    // Map: Object_Version_Renaming -> Is_Serialized?
    private HashMap<String, Boolean> renamingToIsSerialized; // TODO: Remove obsolete from here
    // Map: Blocked data ids
    private LinkedList<Integer> blockedData;
    LinkedList<String> pendingObsoleteRenamings = new LinkedList<String>();
    // Temporary directory to serialize objects to/from
    private String serialDir;
    // Host where the application runs
    private String appHost;
    // Component logger - No need to configure, ProActive does
    private static final Logger logger = Logger.getLogger(Loggers.DIP_COMP);
    private static final boolean debug = logger.isDebugEnabled();

    public DataInfoProvider(String appHost, String serialDir) {
        nameToId = new TreeMap<String, Integer>();
        codeToId = new TreeMap<Integer, Integer>();
        idToData = new TreeMap<Integer, DataInfo>();
        renamingToValue = new TreeMap<String, Object>();
        renamingToIsSerialized = new HashMap<String, Boolean>();
        blockedData = new LinkedList<Integer>();
        pendingObsoleteRenamings = new LinkedList<String>();

        this.serialDir = serialDir;
        this.appHost = appHost;

        DataInfo.init();

        logger.info("Initialization finished");
    }

    public void setCoWorkers(TaskDispatcher TD) {
        this.TD = TD;
    }

    // DataAccess interface
    public DataAccessId registerDataAccess(AccessParams access) throws IOException {

        if (access instanceof FileAccessParams) {
            FileAccessParams fAccess = (FileAccessParams) access;
            return registerFileAccess(fAccess.getMode(),
                    fAccess.getName(),
                    fAccess.getPath(),
                    fAccess.getHost(),
                    -1);
        } else {
            ObjectAccessParams oAccess = (ObjectAccessParams) access;
            return registerObjectAccess(oAccess.getMode(),
                    oAccess.getValue(),
                    oAccess.getCode(),
                    -1);
        }
    }

    public List<DataAccessId> registerDataAccesses(List<AccessParams> accesses, int readerMethodId) throws IOException {
        ArrayList<DataAccessId> daIds = new ArrayList<DataAccessId>(accesses.size());
        for (AccessParams access : accesses) {
            if (access instanceof FileAccessParams) {
                FileAccessParams fAccess = (FileAccessParams) access;
                daIds.add(registerFileAccess(fAccess.getMode(),
                        fAccess.getName(),
                        fAccess.getPath(),
                        fAccess.getHost(),
                        readerMethodId));
            } else {
                ObjectAccessParams oAccess = (ObjectAccessParams) access;
                daIds.add(registerObjectAccess(oAccess.getMode(),
                        oAccess.getValue(),
                        oAccess.getCode(),
                        readerMethodId));
            }
        }

        return daIds;
    }

    private DataAccessId registerFileAccess(AccessMode mode,
            String fileName,
            String path,
            String host,
            int readerId) throws IOException {
        DataInfo fileInfo;
        String locationKey = fileName + ":" + host + ":" + path;
        Integer fileId = nameToId.get(locationKey);

        // First access to this file
        if (fileId == null) {
            if (debug) {
                logger.debug("FIRST access to " + host + ":" + path + fileName);
            }

            // Update mappings
            fileInfo = new FileInfo(fileName, host, path);
            fileId = fileInfo.getDataId();
            nameToId.put(locationKey, fileId);
            idToData.put(fileId, fileInfo);


            // Inform the File Transfer Manager about the new file
            if (mode != AccessMode.W) {
                TD.newDataVersion(fileInfo.getLastDataInstanceId(), null, host, path, fileName);
            }
        } // The file has already been accessed
        else {
            if (debug) {
                logger.debug("Another access to " + host + ":" + path + fileName);
            }

            fileInfo = idToData.get(fileId);
        }

        // Version management
        return fileInfo.manageAccess(mode, readerId, debug, logger);
    }

    // Object access
    private DataAccessId registerObjectAccess(AccessMode mode,
            Object value,
            int code,
            int readerId) throws IOException {
        DataInfo oInfo;
        Integer aoId = codeToId.get(code);

        // First access to this datum
        if (aoId == null) {
            if (debug) {
                logger.debug("FIRST access to object " + code);
            }

            // Update mappings
            oInfo = new ObjectInfo(code);
            aoId = oInfo.getDataId();
            codeToId.put(code, aoId);
            idToData.put(aoId, oInfo);

            if (readerId != -1) { // Not read from the main program
                // Serialize this first version of the object to a file
                DataInstanceId lastDID = oInfo.getLastDataInstanceId();
                String renaming = lastDID.getRenaming();
                try {
                    Serializer.serialize(value, serialDir + renaming);
                } catch (Exception e) {
                    logger.error(SERIALIZATION_ERR, e);
                    return null;
                }

                // Inform the File Transfer Manager about the new file containing the object
                if (mode != AccessMode.W) {
                    logger.debug("Location for object: " + appHost + "/" + serialDir);
                    TD.newDataVersion(lastDID, null, null, serialDir, renaming);
                }
            }
        } else {// The datum has already been accessed
            if (debug) {
                logger.debug("Another access to object " + code);
            }

            oInfo = idToData.get(aoId);

            if (mode != AccessMode.W) {
                DataInstanceId readInstance = oInfo.getLastDataInstanceId();
                String renaming = readInstance.getRenaming();
                if (renamingToValue.get(renaming) != null && renamingToIsSerialized.get(renaming) == null) {
                    // Serialize data accessed by main program
                    renamingToIsSerialized.put(renaming, true);
                    try {
                        logger.debug("Serialize Main Program Object " + readInstance + " to dir " + serialDir + readInstance.getRenaming());
                        Serializer.serialize(getObject(renaming), serialDir + renaming);
                        TD.newDataVersion(readInstance, null, null, serialDir, renaming);
                    } catch (Exception e) {
                        logger.error(SERIALIZATION_ERR, e);
                        return null;
                    }
                }
            }
        }

        // Version management
        return oInfo.manageAccess(mode, readerId, debug, logger);
    }

    public boolean alreadyAccessed(String fileName, String path, String host) {
        String locationKey = fileName + ":" + host + ":" + path;
        Integer fileId = nameToId.get(locationKey);

        return fileId != null;
    }

    // DataInformation interface
    public String getLastRenaming(int code) {
        Integer aoId = codeToId.get(code);
        DataInfo oInfo = idToData.get(aoId);
        return oInfo.getLastDataInstanceId().getRenaming();
    }

    public String getOriginalName(int fileId) {
        FileInfo info = (FileInfo) idToData.get(fileId);
        return info.getOriginalName();
    }

    public Location getOriginalLocation(int fileId) {
        FileInfo info = (FileInfo) idToData.get(fileId);
        return info.getOriginalLocation();
    }

    public LinkedList<String> dataHasBeenRead(List<DataAccessId> dataIds, int readerId) {
        LinkedList<String> obsoleteRenamings = new LinkedList<String>();
        if (!pendingObsoleteRenamings.isEmpty() && blockedData.isEmpty()) {// Flush pending obsolete renamings when there's no blocked data
            obsoleteRenamings.addAll(pendingObsoleteRenamings);
            pendingObsoleteRenamings.clear();
        }
        for (int i = 0; i < dataIds.size(); i++) {
            DataAccessId dAccId = dataIds.get(i);
            Integer dataId;
            Integer rVersionId;
            Integer wVersionId;
            String rRenaming;
            String wRenaming;
            dataId = dAccId.getDataId();
            DataInfo dataInfo = idToData.get(dataId);

            if (dAccId instanceof RAccessId) {
                rVersionId = ((RAccessId) dAccId).getReadDataInstance().getVersionId();
                rRenaming = ((RAccessId) dAccId).getReadDataInstance().getRenaming();
                checkObsolete(dataInfo, dataId, rVersionId, rRenaming, obsoleteRenamings, true, readerId);
            } else if (dAccId instanceof RWAccessId) {
                rVersionId = ((RWAccessId) dAccId).getReadDataInstance().getVersionId();
                rRenaming = ((RWAccessId) dAccId).getReadDataInstance().getRenaming();
                checkObsolete(dataInfo, dataId, rVersionId, rRenaming, obsoleteRenamings, true, readerId);
                wVersionId = ((RWAccessId) dAccId).getWrittenDataInstance().getVersionId();
                wRenaming = ((RWAccessId) dAccId).getWrittenDataInstance().getRenaming();
                checkObsolete(dataInfo, dataId, wVersionId, wRenaming, obsoleteRenamings, false, readerId);
            } else {
                wVersionId = ((WAccessId) dAccId).getWrittenDataInstance().getVersionId();
                wRenaming = ((WAccessId) dAccId).getWrittenDataInstance().getRenaming();
                checkObsolete(dataInfo, dataId, wVersionId, wRenaming, obsoleteRenamings, false, readerId);
            }
        }

        for (String renaming : obsoleteRenamings) {
            int dataId = DataInstanceId.getDataId(renaming);
            int versionId = DataInstanceId.getVersionId(renaming);
            DataInfo dataInfo = idToData.get(dataId);
            if (versionId == dataInfo.getLastVersionId() && dataInfo instanceof FileInfo) {
                FileInfo fi = (FileInfo) dataInfo;
                String path = fi.getOriginalLocation().getPath() + File.separator + fi.getOriginalName();
                File f = new File(path);
                f.delete();
            }
        }
        if (debug) {
            for (String renaming : obsoleteRenamings) {
                logger.debug("Detected file " + renaming + " as obsolete. ");
            }
            for (String renaming : pendingObsoleteRenamings) {
                logger.debug("File " + renaming + " is in pending obsolete renamings");
            }
        }
        return obsoleteRenamings;
    }

    public void checkObsolete(DataInstanceId daId, LinkedList<String> renamings) {
        int dataId = daId.getDataId();
        DataInfo dataInfo = idToData.get(dataId);
        checkObsolete(dataInfo, dataId, daId.getVersionId(), daId.getRenaming(), renamings, true, -1);

    }

    private void checkObsolete(DataInfo dataInfo, int dataId, int versionId,
            String renaming,
            LinkedList<String> renamings,
            boolean isReader,
            int readerId) {
        boolean isLastReader = (isReader && dataInfo.versionHasBeenRead(versionId, readerId) == 0)
                || (!isReader && dataInfo.getReadersForVersion(versionId) == 0);

        if (isLastReader && (dataInfo.isDeleted() || dataInfo.getLastVersionId() != versionId)) {
            if (blockedData.contains(dataId)) {
                pendingObsoleteRenamings.add(renaming);
            } else {
                renamings.add(renaming);
            }
        }

    }

    public void setObjectVersionValue(String renaming, Object value) {
        renamingToValue.put(renaming, value);
    }

    public boolean isHere(DataInstanceId dId) {
        return renamingToValue.get(dId.getRenaming()) != null;
    }

    public Object getObject(String renaming) {
        return renamingToValue.get(renaming);
    }

    public void newVersionSameValue(String rRenaming, String wRenaming) {
        renamingToValue.put(wRenaming, renamingToValue.get(rRenaming));
    }

    public DataInstanceId getLastDataAccess(int code) {
        Integer aoId = codeToId.get(code);
        DataInfo oInfo = idToData.get(aoId);
        return oInfo.getLastDataInstanceId();
    }

    public List<Integer> getLastVersions(TreeSet<Integer> dataIds) {
        List<Integer> versionIds = new ArrayList<Integer>(dataIds.size());
        for (Integer dataId : dataIds) {
            DataInfo dataInfo = idToData.get(dataId);
            versionIds.add(dataInfo.getLastVersionId());
        }
        return versionIds;
    }

    public void blockDataIds(TreeSet<Integer> dataIds) {
        for (Integer id : dataIds) {
        }
        blockedData.addAll(dataIds);
    }

    public void unblockDataId(Integer dataId) {
        blockedData.remove(dataId);
    }

    public FileInfo deleteData(String fileName, String path, String host) {
        String locationKey = fileName + ":" + host + ":" + path;
        Integer fileId = nameToId.get(locationKey);
        if (fileId == null) {
            System.out.println(" Deleting file" + path + fileName);
            File f = new File(path + fileName);
            f.delete();
            return null;
        }
        FileInfo fileInfo = (FileInfo) idToData.remove(fileId);
        nameToId.remove(locationKey);
        fileInfo.setDeleted(true);
        return fileInfo;
    }

    public LinkedList<String> deleteData(TreeSet<Integer> objectsSet) {
        LinkedList<String> obsoletes = new LinkedList<String>();
        if (objectsSet != null) {
            for (Integer dataId : objectsSet) {
                DataInfo dataInfo = idToData.get(dataId);
                obsoletes.add(dataInfo.getLastDataInstanceId().getRenaming());
            }
        }
        return obsoletes;
    }
}
