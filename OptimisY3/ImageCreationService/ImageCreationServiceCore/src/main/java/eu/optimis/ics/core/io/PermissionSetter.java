/* $Id: PermissionSetter.java 4962 2012-03-15 15:08:04Z rkuebert $ */

/*
 * Copyright 2012 University of Stuttgart
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package eu.optimis.ics.core.io;

import java.io.File;
import org.apache.log4j.Logger;
import eu.optimis.ics.core.image.Image;
import eu.optimis.ics.core.image.ImageState;
import eu.optimis.ics.core.shell.ProcessResult;

/**
 * A thread for setting permissions of a file or a directory on the image.
 * @see FileUtils#setPermissions(String, String)
 * @author Roland Kuebert 
 * @author Anthony Sulistio 
 */
public class PermissionSetter extends Thread {

    /** Log4j logger instance. */
    private static Logger LOG = Logger.getLogger(PermissionSetter.class.getName());

    /** The image that contains a particular file or directory. */
    private Image image;

    /** The name of file or directory whose permissions will be set. */
    private String name;

    /** The Linux permissions to be set, e.g. "755" or "777". */
    private String permissions;

    /**
     * Creates a new PermissionSetter object
     * @param image     image object
     * @param name The name of file or directory whose permissions will be set
     * @param permissions   Linux permissions to be set, e.g. "755" or "777"
     */
    public PermissionSetter(Image image, String name, String permissions) {
        this.image = image;
        this.name = name;
        this.permissions = permissions;
    }

    /**
     * Starts the permission setting operation.
     */
    public void run() {
        boolean retVal = setPermissions();
        if (retVal == false) {
            LOG.warn("ics.core.io.PermissionSetter.run(): There might have been an error in setting the permission to the disk image");
        } else {
            LOG.info("ics.core.io.PermissionSetter.run(): Permission has been set successfully");
        }
        image.setState(ImageState.READY);
    }

    /**
     * Sets the permission of a file / directory located on the image.
     * @return <tt>true</tt> if successful, <tt>false</tt> otherwise
     * @see FileUtils#setPermissions(String, String)
     */
    private boolean setPermissions() {

        // find the empty /dev/nbd partition first before mounting it
        int partitionID = FileUtils.findEmptyPartition();
        String imageLoc = image.getImageLocation();
        File mountDirectory = FileUtils.mountImage(imageLoc, partitionID);
        if (mountDirectory == null) {
            LOG.error("ics.core.io.PermissionSetter.setPermissions(): Could not mount "
                    + imageLoc);
            return false;
        }

        // then sets the permission
        ProcessResult result = FileUtils.setPermissions(mountDirectory + "/"
                + name, permissions);

        // finally umount the image
        FileUtils.unmountImage(mountDirectory.getPath(), partitionID);
        if (result.getExitCode() != 0) {
            LOG.error("Process exited abnormally. Exit code: "
                    + result.getExitCode());
            LOG.error("Output:\n" + result.getStandardError());
            return false;
        }

        return true;
    }
}
