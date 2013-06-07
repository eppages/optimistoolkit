/* $Id: FileUtils.java 11547 2013-02-19 17:34:23Z sulistio $ */

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
import java.io.IOException;
import org.apache.log4j.Logger;
import eu.optimis.ics.core.shell.ProcessResult;
import eu.optimis.ics.core.shell.ShellUtil;

/**
 * File utility functions used by the Image Creation Service, such as create and
 * delete a directory.
 * 
 * @author Roland Kuebert
 * @author Anthony Sulistio
 */
public class FileUtils {

    /** Log4j logger instance. */
    private static Logger LOG = Logger.getLogger(FileUtils.class.getName());

    /**
     * Denotes which /dev/nbd partition number to mount an image for qemu-nbd,
     * e.g. /dev/nbd0p${PARTITION_NUM}
     */
    private final static int PARTITION_NUM = 1;

    /** 
     * Maximum number of NBD partitions. 
     * For example: mount up to 16, i.e. from /dev/nbd0 to /dev/nbd15 
     * */
    private final static int NBD_MAX_PART = 16;

    /** No empty NBD partition is found */
    public final static int NOT_FOUND = -1;

    /**
     * Initializes the <tt>nbd</tt> module.
     * NOTE: The <tt>nbd</tt> module should automatically start during boot time. 
     */
    public synchronized static void initNBDModule() {
        String command = "sudo modprobe nbd max_part=63";
        LOG.debug("ics.core.io.FileUtils.initNBDModule():");
        ProcessResult result = ShellUtil.executeShellCommand(command);
        if (result.getExitCode() != 0) {
            LOG.error("ics.core.io.FileUtils.initNBDModule(): Error - exit code: "
                    + result.getExitCode());
            //LOG.error("ics.core.io.FileUtils.initNBDModule(): Standard output:\n"
            //        + result.getStandardOut());
            //LOG.error("ics.core.io.FileUtils.initNBDModule(): Standard error:\n"
            //        + result.getStandardError());
        }
    }

    /**
     * Kills the <tt>nbd</tt> module.
     */
    private synchronized static void killNBDModule() {
        String command = "sudo killall qemu-nbd";
        LOG.debug("ics.core.io.FileUtils.killNBDModule():");
        ProcessResult result = ShellUtil.executeShellCommand(command);
        if (result.getExitCode() != 0) {
            LOG.error("ics.core.io.FileUtils.killNBDModule(): Error - exit code: "
                    + result.getExitCode());
            //LOG.error("ics.core.io.FileUtils.killNBDModule(): Standard output:\n"
            //        + result.getStandardOut());
            //LOG.error("ics.core.io.FileUtils.killNBDModule(): Standard error:\n"
            //        + result.getStandardError());
        }
    }

    /**
     * Finds an empty <tt>/dev/nbd</tt> partition. 
     * If no partition is found, this method will unmount <tt>/dev/nbd0p1</tt>
     * and disconnect the <tt>/dev/nbd0</tt> partition automatically.
     * @return the <tt>/dev/nbd</tt> partition number
     */
    public synchronized static int findEmptyPartition() {
        int index = NOT_FOUND; // no empty /dev/nbd partition is found
        File file = null;

        for (int i = 0; i < NBD_MAX_PART; i++) {
            // means the /dev/nbd[i] is available to map or bind an image
            file = new File("/dev/nbd" + i + "p" + PARTITION_NUM);
            if (file.exists() == false) {
                index = i;
                break;
            }
        }

        // If all /dev/nbd partitions are busy, then force unmount /dev/nbd0
        if (index == NOT_FOUND) {
            LOG.debug("ics.core.io.FileUtils.findEmptyPartition(): Can't find an empty partition. Force unmount /dev/nbd0");

            String command1 = "sudo umount /dev/nbd0p1";
            ProcessResult result = ShellUtil.executeShellCommand(command1);
            if (result.getExitCode() != 0) {
                LOG.error("ics.core.io.FileUtils.mountImage(): Process exited abnormally. Exit code: "
                        + result.getExitCode());
                LOG.error("ics.core.io.FileUtils.mountImage(): Output:\n"
                        + result.getStandardError());
            }

            String command2 = "sudo qemu-nbd -d /dev/nbd0";
            result = ShellUtil.executeShellCommand(command2);
            if (result.getExitCode() != 0) {
                LOG.error("ics.core.io.FileUtils.mountImage(): Process exited abnormally. Exit code: "
                        + result.getExitCode());
                LOG.error("ics.core.io.FileUtils.mountImage(): Output:\n"
                        + result.getStandardError());
            }
        }

        LOG.debug("ics.core.io.FileUtils.findEmptyPartition(): Empty partition at /dev/nbd"
                + index);
        return index;
    }

    /**
     * This method will unmount the given directory and disconnect 
     * the <tt>/dev/nbd${partitionID}</tt> partition.
     * @param mountDirectory    location of the mounted directory         
     * @param partitionID the <tt>/dev/nbd</tt> partition number
     * @return <tt>true</tt> if successful, otherwise <tt>false</tt>
     */
    public static boolean unmountImage(String mountDirectory, int partitionID) {
        if (partitionID == NOT_FOUND) {
            LOG.error("ics.core.io.FileUtils.umountImage(): Error - invalid partition: /dev/nbd"
                    + partitionID);
            return false;
        }
        LOG.info("ics.core.io.FileUtils.unmountImage(): Unmounting partition at "
                + mountDirectory);

        int max_length = 200; // chars
        StringBuffer command = new StringBuffer(max_length);

        // unmount the directory
        command.append("sudo umount -d ");
        command.append(mountDirectory);
        ProcessResult result = ShellUtil.executeShellCommand(command.toString());
        if (result.getExitCode() != 0) {
            LOG.error("ics.core.io.FileUtils.mountImage(): Process exited abnormally. Exit code: "
                    + result.getExitCode());
            LOG.error("ics.core.io.FileUtils.mountImage(): Output:\n"
                    + result.getStandardError());
            // return false;
        }

        // then disconnect the /dev/nbd partition
        command.setLength(0); // reset the buffer
        command.append("sudo qemu-nbd -d /dev/nbd");
        command.append(partitionID);
        result = ShellUtil.executeShellCommand(command.toString());
        if (result.getExitCode() != 0) {
            LOG.error("ics.core.io.FileUtils.mountImage(): Process exited abnormally. Exit code: "
                    + result.getExitCode());
            LOG.error("ics.core.io.FileUtils.mountImage(): Output:\n"
                    + result.getStandardError());
            // return false;
        }

        deleteDirectory(mountDirectory);  // delete temp directory afterwards
        return true;
    }

    /**
     * This method will connect the <tt>/dev/nbd${partitionID}</tt> partition
     * to the given filename by using the NBD protocol.
     * @param filename  the image filename (incl. directory path) to be connected or mapped
     * @param partitionID  the <tt>/dev/nbd</tt> partition number
     * @return the location of the mounted directory or <tt>null</tt> if fail 
     */
    public static File mountImage(String filename, int partitionID) {

        if (partitionID == NOT_FOUND) {
            LOG.error("ics.core.io.FileUtils.mountImage(): Error - invalid partition: /dev/nbd"
                    + partitionID);
            return null;
        }

        // Mount the image using the NBD protocol
        //initNBDModule();
        LOG.info("ics.core.io.FileUtils.mountImage(): Mounting the image using the NBD protocol");
        int max_length = 250; // chars
        StringBuffer command = new StringBuffer(max_length);
        command.append("sudo qemu-nbd -c /dev/nbd");
        command.append(partitionID);
        command.append(" ");
        command.append(filename);
        ProcessResult result = ShellUtil.executeShellCommand(command.toString());
        if (result.getExitCode() != 0) {
            LOG.error("ics.core.io.FileUtils.mountImage(): Process exited abnormally. Exit code: "
                    + result.getExitCode());
            //LOG.error("ics.core.io.FileUtils.mountImage(): Output:\n"
            //        + result.getStandardError());
            return null;
        }

        // Create a temp directory where the image will be mounted on the host
        File mountDirectory = null;
        try {
            mountDirectory = FileUtils.createTempDirectory();
        } catch (IOException ioException) {
            LOG.error("ics.core.io.FileUtils.mountImage(): Error in creating a temp directory", ioException);
            return null;
        }
        LOG.debug("ics.core.io.FileUtils.mountImage(): Temp directory = "
                + mountDirectory.getAbsolutePath());

        // NOTE: it is important to wait for few seconds! Otherwise, it will 
        // encounter this error message: 
        // mount: special device /dev/nbd0p1 does not exist
        String dev = "/dev/nbd" + partitionID + "p" + PARTITION_NUM;
        File file = new File(dev);

        /***********  // old approach in waiting for the partition mount
        int count = 0;
        while (true) {
            if (file.exists() || count == 9) {
                if (count == 9) {
                    LOG.debug("Exiting because timeout reached after 10 seconds");
                } else {
                    LOG.debug("ics.core.io.FileUtils.mountImage(): Found the image!");
                }
                break;
            } else {
                count++;
                try {
                    int time = 1000;  // in milliseconds
                    LOG.debug("ics.core.io.FileUtils.mountImage(): Sleeping for "
                            + time + " milliseconds");
                    Thread.sleep(time);
                } catch (InterruptedException e) {
                    //e.printStackTrace();
                    LOG.error("ics.core.io.FileUtils.mountImage(): Exception occurs: "
                            + e.toString());
                }
            }

        }
        **************/

        // waiting for the partition mount, e.g. /dev/nbd0p1 to exist
        boolean found = false;   // false means can't found the partition to mount!
        int time = 1000;  // in milliseconds
        for (int i = 0; i < NBD_MAX_PART; i++) {
            if (file.exists() == true) {
                LOG.debug("ics.core.io.FileUtils.mountImage(): Found the partition to mount!");
                found = true;
                break;
            }

            try {
                LOG.debug("ics.core.io.FileUtils.mountImage(): Sleeping for "
                        + time + " milliseconds");
                Thread.sleep(time);
            } catch (InterruptedException e) {
                //e.printStackTrace();
                LOG.error("ics.core.io.FileUtils.mountImage(): Exception occurs: "
                        + e.toString());
            }
        }

        // if can't found, then disconnect qemu-nbd and kill nbd process
        //found = false;  // for debugging purposes -- means always failed
        if (found == false) {
            LOG.debug("ics.core.io.FileUtils.mountImage(): Exiting because timeout reached after "
                    + NBD_MAX_PART + " seconds");

            // Then unmount the partition
            unmountImage(mountDirectory.getAbsolutePath(), partitionID);

            killNBDModule(); // kill the qemu-nbd process
            initNBDModule(); // restarts the process again
            return null;
        }

        // Mount the NBD mapper to the temp directory
        command.setLength(0); // reset the buffer
        command.append("sudo mount ");
        command.append(dev);
        command.append(" ");
        command.append(mountDirectory);
        result = ShellUtil.executeShellCommand(command.toString());
        if (result.getExitCode() != 0) {
            LOG.error("ics.core.io.FileUtils.mountImage(): Process exited abnormally. Exit code: "
                    + result.getExitCode());
            //LOG.error("ics.core.io.FileUtils.mountImage(): Output:\n"
            //        + result.getStandardError());

            // Then unmount the partition
            unmountImage(mountDirectory.getAbsolutePath(), partitionID);
            return null;
        }

        return mountDirectory;
    }

    /**
     * Creates a temporary directory and returns it.
     * <p/>
     * This method basically works around {@link File}'s createTempFile()
     * operation, taking the file name and creating a directory instead with the
     * same name.
     * 
     * @return A newly created temporary directory
     * 
     * @throws IOException
     *             If an error occurs deleting the temporary file or creating
     *             the temporary directory
     */
    public static File createTempDirectory() throws IOException {

        File temp = File.createTempFile("tmp_", Long.toString(System.nanoTime()));
        if (!temp.delete()) {
            LOG.error("ics.core.io.FileUtils.createTempDirectory(): Could not delete "
                    + temp.getAbsolutePath());
            throw new IOException("Could not delete temporary file: "
                    + temp.getAbsolutePath());
        }

        if (!temp.mkdir()) {
            LOG.error("ics.core.io.FileUtils.createTempDirectory(): Could not create "
                    + temp.getAbsolutePath());
            throw new IOException("Could not create temporary directory: "
                    + temp.getAbsolutePath());
        }

        return temp;
    }

    /**
     * Changes the permission of files or directories recursively. When Java
     * unzip a file, it does not preserve the permission of files (e.g. to be
     * executable).
     * 
     * @param target
     *            the target location
     * @param permissions
     *            the permission
     * @return
     */
    public static ProcessResult setPermissions(String target, String permissions) {
        String command = "sudo chmod -R " + permissions + " " + target;
        LOG.debug("ics.core.io.FileUtils.setPermissions(): ");
        return ShellUtil.executeShellCommand(command);
    }

    /**
     * Deletes a given directory
     * @param directory the directory location (absolute path)
     */
    public static void deleteDirectory(String directory) {
        if (directory == null) {
            return;
        }

        File dir = new File(directory);
        if (dir.isDirectory() == false) {
            return;
        }

        deleteDirectory(dir);
    }

    /**
     * Deletes a given directory recursively, i.e. including its sub-directories
     * and files.
     * @param directory the directory location (absolute path)
     */
    public static void deleteDirectory(File directory) {
        // LOG.debug("ics.core.io.FileUtils.deleteDirectory(): Deleting directory "
        // + directory.toString());
        File[] files = directory.listFiles();
        for (int n = 0; n < files.length; n++) {
            File nextFile = files[n];

            // if it's a directory, delete sub-directories and files before
            // removing the empty directory
            if (nextFile.isDirectory() == true) {
                deleteDirectory(nextFile);
            } else {
                nextFile.delete(); // otherwise just delete the file
                // LOG.debug("ics.core.io.FileUtils.deleteDirectory(): Deleting file "
                // + nextFile.toString());
            }
        }

        // finally, delete the specified directory
        if (directory.delete() == false) {
            LOG.warn("ics.core.io.FileUtils.deleteDirectory(): Unable to delete "
                    + directory.toString());
        }
    }

    /**
     * Creates a directory if it doesn't exist.
     * @param directory the directory location (absolute path)
     * @return the newly-created directory or <tt>null</tt> if fail.
     */
    public static File createDirectoryIfNotExists(String directory) {
        if (directory == null) {
            return null;
        }

        // Check if the target directory exists on the input path
        File targetDirectory = new File(directory);
        if (targetDirectory.exists() != true) {
            LOG.debug("ics.core.io.FileUtils.createDirectoryIfNotExists(): Creating "
                    + directory);

            // Don't run as sudo but as a tomcat user instead
            ProcessResult result = ShellUtil.executeShellCommand("mkdir -p "
                    + directory);
            if (result.getExitCode() != 0) {
                LOG.error("ics.core.io.FileUtils.createDirectoryIfNotExists(): Process exited abnormally. Exit code: "
                        + result.getExitCode());
                //LOG.error(result.getStandardError());
                return null;
            }

        }

        return targetDirectory;
    }
}
