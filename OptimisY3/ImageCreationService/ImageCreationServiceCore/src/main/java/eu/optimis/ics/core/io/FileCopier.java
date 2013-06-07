/* $Id: FileCopier.java 11547 2013-02-19 17:34:23Z sulistio $ */

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

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import org.apache.log4j.Logger;
import eu.optimis.ics.core.image.Image;
import eu.optimis.ics.core.image.ImageState;
import eu.optimis.ics.core.shell.ProcessResult;
import eu.optimis.ics.core.shell.ShellUtil;

/**
 * A thread for copying or extracting files to an image file.
 * 
 * @author Roland Kuebert
 * 
 */
public class FileCopier extends Thread {

    /** Log4j logger instance. */
    private static Logger LOG = Logger.getLogger(FileCopier.class.getName());

    /** The file to copy or extract to the image. */
    private File fileToCopy;

    /** The image to which to copy/extract the file. */
    private Image image;

    /**
     * Specifies if <code>fileToCopy</code> is an image that should be extracted
     * to the image (<code>true</code>) or if it should be copied (
     * <code>false</code>).
     */
    private boolean extract = false;

    /**
     * The directory where files are copied to on the host the ICS is running.
     */
    private String targetDirectoryName;

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
    public FileCopier(Image image, File fileToCopy, String targetDirectoryName) {
        this.fileToCopy = fileToCopy;
        this.image = image;
        this.targetDirectoryName = targetDirectoryName;
        extract = false;
    }

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
     * 
     * @param extract
     *            If <code>true</code>, <code>fileToCopy</code> is extracted
     *            otherwise it is copied. Default is <code>false</code>, meaning
     *            copy
     */
    public FileCopier(Image image, File fileToCopy, String targetDirectoryName,
            boolean extract) {
        this.fileToCopy = fileToCopy;
        this.image = image;
        this.targetDirectoryName = targetDirectoryName;
        this.extract = extract;
    }

    /**
     * Starts the file copying operation.
     */
    public void run() {
        boolean retVal = copyFile();
        if (retVal == false) {
            LOG.error("ics.core.io.FileCopier.run(): There might have been an error in copying the file to the disk image");
        } else {
            LOG.info("ics.core.io.FileCopier.run(): File has been added successfully");
        }
        image.setState(ImageState.READY);
    }

    /**
     * Copies <code>fileToCopy</code> to the image file to the directory stored
     * in <code>targetDirectoryImage</code>.
     * 
     * @return <code>True</code> if the file was copied successfully and the
     *         image unmounted, <code>false</code> otherwise
     */
    private boolean copyFile() {

        int partitionID = FileUtils.findEmptyPartition();
        String imageLoc = image.getImageLocation();
        File mountDirectory = FileUtils.mountImage(imageLoc, partitionID);
        if (mountDirectory == null) {
            LOG.error("ics.core.io.FileCopier.copyFile(): Could not mount "
                    + imageLoc);
            return false;
        }

        LOG.debug("ics.core.io.FileCopier.copyFile(): Mounting directory = "
                + mountDirectory);
        File targetDirectory = FileUtils.createDirectoryIfNotExists(mountDirectory
                + "/" + targetDirectoryName);
        LOG.debug("ics.core.io.FileCopier.copyFile(): Files will be copied/extracted to "
                + targetDirectory);

        // If we do not extract, we copy
        boolean result = true;
        if (extract == false) {

            // Copy file, log an error if the copying is not successful
            boolean copySuccess = copyFileToImage(targetDirectory.getPath());
            if (copySuccess == false) {
                LOG.error("ics.core.io.FileCopier.copyFile(): Could not copy file to image");
                result = false;
            }
        } else {
            // Ensure that target directory is chmod 777
            FileUtils.setPermissions(targetDirectory.getPath(), "777");

            // We extract
            try {
                unzipFile(fileToCopy, targetDirectory.getPath());
            } catch (IOException ioException) {
                result = false;
                LOG.error("ics.core.io.FileCopier.copyFile(): Error unzipping file", ioException);
            }
        }

        // unmount the image from the /dev/nbd partition
        FileUtils.unmountImage(mountDirectory.getPath(), partitionID);
        return result;
    }

    /**
     * Copies files to an image
     * @param targetDirectory  the target directory located in the image
     * @return <tt>true</tt> if successful, <tt>false</tt> otherwise
     */
    private boolean copyFileToImage(String targetDirectory) {
        ProcessResult result;
        String command;

        // We could use Apache file utils for copying as well, but then we need
        // to take permissions into account
        //LOG.debug("Copying file");
        command = new String("sudo cp " + fileToCopy.getAbsolutePath() + " "
                + targetDirectory);
        //LOG.debug("Performing command: " + command);
        result = ShellUtil.executeShellCommand(command);
        if (result.getExitCode() != 0) {
            LOG.error("ics.core.io.FileCopier.copyFileToImage(): Process exited abnormally. Exit code: "
                    + result.getExitCode());
            //LOG.error("Output: " + result.getStandardError());
            return false;
        }

        return true;
    }

    /**
     * Unzips a file 
     * @param zipFile   the zip file
     * @param destination   the destination folder
     * @throws IOException  I/O Exception
     */
    private void unzipFile(File zipFile, String destination) throws IOException {
        LOG.debug("ics.core.io.FileCopier.unzipFile(): Unzipping " + zipFile
                + " to directory " + destination);
        //LOG.debug("Opening input streams");
        FileInputStream fis = new FileInputStream(zipFile);
        ZipInputStream zin = new ZipInputStream(new BufferedInputStream(fis));
        ZipEntry entry;
        File destinationDirectory = new File(destination);

        while ((entry = zin.getNextEntry()) != null) {
            //LOG.debug("Extracting: " + entry);

            if (entry.isDirectory()) {
                //LOG.debug("Directory found, will be created");
                File targetDirectory = new File(destinationDirectory, entry.getName());
                targetDirectory.mkdir();
            } else {
                // extract data
                // open output streams
                int BUFFER = 2048;

                File destinationFile = new File(destinationDirectory, entry.getName());
                destinationFile.getParentFile().mkdirs();

                //LOG.debug("Creating parent file of destination: "
                //        + destinationFile.getParent());
                //boolean parentDirectoriesCreated = destinationFile.getParentFile().mkdirs();
                //LOG.debug("Result of creating parents: "
                //        + parentDirectoriesCreated);

                FileOutputStream fos = new FileOutputStream(destinationFile);
                BufferedOutputStream dest = new BufferedOutputStream(fos, BUFFER);

                int count;
                byte data[] = new byte[BUFFER];

                dest = new BufferedOutputStream(fos, BUFFER);
                while ((count = zin.read(data, 0, BUFFER)) != -1) {
                    dest.write(data, 0, count);
                }
                dest.flush();
                dest.close();
            }
        }

        LOG.debug("ics.core.io.FileCopier.unzipFile(): Unzipping file is done");
        zin.close();
        fis.close();
    }
}
