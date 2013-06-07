/* $Id: DirectCopierSystemCall.java 8219 2012-05-16 08:25:42Z rkuebert $ */

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
// import java.util.ArrayList;
import org.apache.log4j.Logger;
import eu.optimis.ics.core.Constants;
import eu.optimis.ics.core.image.Image;
import eu.optimis.ics.core.image.ImageState;
import eu.optimis.ics.core.shell.ProcessResult;
import eu.optimis.ics.core.shell.ShellUtil;

// To minimize dependency, the following classes have been copied into
// eu.optimis.ics.core.io package. However, the
// eu.optimis.ics.core.shell.ShellUtil class can also do the same thing.
// import eu.optimis.vc.api.Core.SystemCall;
// import eu.optimis.vc.api.Core.SystemCallException;

/**
 * Thread for copying or extracting files to an image file.
 * 
 * @author Roland Kuebert 
 * @author Tinghe Wang 
 * @author Anthony Sulistio 
 */
public class DirectCopierSystemCall { // extends Thread {

    /** Log4j logger instance. */
    private static Logger LOG = Logger.getLogger(DirectCopierSystemCall.class.getName());

    /** The image to which to copy/extract the file. */
    private Image image;
    private String imageId;

    /**
     * The directory where files are copied to on the host the ICS is running.
     */
    private String sourceDirectory;
    private String targetDirectory;

    //private SystemCall systemCall;

    /**
     * Creates a new file copier for copying <code>fileToCopy</code> to the
     * image file specified by <code>image</code> to the directory
     * <code>targetDirectoryName</code>.
     * 
     * @param image
     *            the image to which to add the file
     * 
     * @param fileToCopy
     *            the file to copy to the image
     */

    public DirectCopierSystemCall(Image image, String sourceDirectory,
            String targetDirectoryName) {
        this.image = image;
        this.sourceDirectory = sourceDirectory;
        imageId = image.getUuid().toString();
        targetDirectory = targetDirectoryName;
        //systemCall = new SystemCall(imageDirectory);
    }

    /**
     * Starts the file copying operation and automatically change the permission to 777
     * on the image's {@link Constants#TARGET_DIRECTORY_PROPERTY} directory recursively.
     */
    public boolean run() {
        boolean retVal = copyFilesFromDirectory();
        if (retVal == false) {
            LOG.error("ics.core.io.DirectCopierSystemCall.run(): Error in copying the file to the disk image");
        } else {
            LOG.info("ics.core.io.DirectCopierSystemCall.run(): Files have been added successfully");
        }

        image.setState(ImageState.READY);
        return retVal;
    }

    /**
     * Copies <code>fileToCopy</code> to the image file to the directory stored
     * in <code>targetDirectoryImage</code>.
     * 
     * @return <code>True</code> if the file was copied successfully and the
     *         image unmounted, <code>false</code> otherwise
     */
    private boolean copyFilesFromDirectory() {
        LOG.info("ics.core.io.DirectCopierSystemCall.copyFilesFromDirectory(): Perform these steps:");
        int partitionID = FileUtils.findEmptyPartition();
        String imageLoc = image.getImageLocation();
        File mountDirectory = FileUtils.mountImage(imageLoc, partitionID);
        if (mountDirectory == null) {
            LOG.error("ics.core.io.DirectCopierSystemCall.copyFilesFromDirectory(): Could not mount "
                    + imageLoc);
            return false;
        }

        int max_length = 200; // chars
        StringBuffer command = new StringBuffer(max_length);

        // Copying files
        String completeSourceDirectory = sourceDirectory + "/" + imageId + "/";
        LOG.debug("Copying files from " + completeSourceDirectory);
        LOG.debug("to " + mountDirectory);
        command.append("sudo cp -RT ");
        command.append(completeSourceDirectory);
        command.append(" ");
        command.append(mountDirectory.getPath());
        ProcessResult result = ShellUtil.executeShellCommand(command.toString());
        if (result.getExitCode() != 0) {
            LOG.error("ics.core.io.DirectCopierSystemCall.copyFilesFromDirectory(): Process exited abnormally. Exit code: "
                    + result.getExitCode());
            //LOG.error("ics.core.io.DirectCopierSystemCall.copyFilesFromDirectory(): Output:\n"
            //        + result.getStandardError());
            //return false;
        }

        // Setting files permission
        LOG.debug("Setting permission to files in " + mountDirectory);
        File tfile = new File(mountDirectory.getPath() + "/" + targetDirectory);
        command.setLength(0); // reset the buffer
        command.append("sudo chmod 777 -R ");
        command.append(tfile.getPath());
        result = ShellUtil.executeShellCommand(command.toString());
        if (result.getExitCode() != 0) {
            LOG.error("ics.core.io.DirectCopierSystemCall.copyFilesFromDirectory(): Process exited abnormally. Exit code: "
                    + result.getExitCode());
            //LOG.error("ics.core.io.DirectCopierSystemCall.copyFilesFromDirectory(): Output:\n"
            //        + result.getStandardError());
            //return false;
        }

        boolean output = FileUtils.unmountImage(mountDirectory.getPath(), partitionID);
        return output;
    }

    /**********  // NOTE: old approach -- being replaced by ShellUtil
    private boolean copyFilesFromDirectory() {
        LOG.info("ics.core.io.DirectCopierSystemCall.copyFilesFromDirectory(): Perform these steps:");
        int partitionID = FileUtils.findEmptyPartition();
        String imageLoc = image.getImageLocation();
        File mountDirectory = FileUtils.mountImage(imageLoc, partitionID);
        if (mountDirectory == null) {
            LOG.error("ics.core.io.DirectCopierSystemCall.copyFilesFromDirectory(): Could not mount "
                    + imageLoc);
            return false;
        }

        ArrayList<String> arguments = new ArrayList<String>();
        String command = "sudo";  // Need to run as root or sudo

        // Copying files
        String completeSourceDirectory = sourceDirectory + "/" + imageId + "/";
        LOG.debug("Copying files from " + completeSourceDirectory);
        LOG.debug("to " + mountDirectory);
        arguments.clear();
        arguments.add("cp");
        arguments.add("-RT");
        arguments.add(completeSourceDirectory);
        arguments.add(mountDirectory.getPath());
        //LOG.debug("Step 5 Performing command: " + command + " " 
        //		+ arguments.get(0) + " " + arguments.get(1));
        try {
            systemCall.runCommand(command, arguments);
        } catch (SystemCallException e) {
            //e.printStackTrace();
            LOG.error("Error: " + e.toString());
            return false;
        }

        // Setting files permission
        LOG.debug("Setting permission to files in " + mountDirectory);
        File tfile = new File(mountDirectory.getPath() + "/" + targetDirectory);
        arguments.clear();
        arguments.add("chmod");
        arguments.add("777");
        arguments.add("-R");
        arguments.add(tfile.getPath());
        //LOG.debug("Performing command: " + command + " "
        //		+ arguments.get(0) + " " + arguments.get(1) + " " + arguments.get(2));
        try {
            systemCall.runCommand(command, arguments);
        } catch (SystemCallException e) {
            //e.printStackTrace();
            LOG.error("Error: " + e.toString());
            return false;
        }

        boolean result = FileUtils.unmountImage(mountDirectory.getPath(), partitionID);
        return result;
    }
    ************/
}
