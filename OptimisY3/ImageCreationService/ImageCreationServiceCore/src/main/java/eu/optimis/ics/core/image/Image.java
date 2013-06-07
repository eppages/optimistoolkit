/* $Id: Image.java 11547 2013-02-19 17:34:23Z sulistio $ */

/*
 * Copyright 2011 University of Stuttgart
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
package eu.optimis.ics.core.image;

import java.io.File;
import java.io.IOException;
import java.util.UUID;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.apache.commons.configuration.PropertiesConfiguration;
import eu.optimis.ics.core.Constants;
import eu.optimis.ics.core.exception.OutOfDiskSpaceException;
import eu.optimis.ics.core.io.DirectCopierSystemCall;
import eu.optimis.ics.core.io.FileCopier;
import eu.optimis.ics.core.io.PermissionSetter;
import eu.optimis.ics.core.util.PropertiesReader;

/**
 * Abstract base class for a VM image managed by the Image Creation Service.
 * 
 * @author Roland Kuebert
 * @author Anthony Sulistio
 * 
 */
public class Image {

    /** Log4j logger instance. */
    private static Logger LOGGER = Logger.getLogger(Image.class.getName());

    /** Base URL where the image is stored. */
    private String baseUrl = null;

    /** Default extension for image files. */
    protected final static String DEFAULT_IMAGE_FILE_EXTENSION = ".qcow2";

    /** The directory used to store images and base images. */
    protected String imageDirectory;

    /** The state the image is currently in. */
    protected ImageState state = ImageState.BUSY;

    /** The actual VM image file. */
    protected File imageFile;  // will be created by CoreElement or OrchestrationElement

    /** The image's UUID. */
    protected final UUID uuid;

    private String targetDirectoryName;  // a temp dir for storing txt/zip files before copy to img
    private String webappsDirectoryName; // for storing tomcat war file
    private int baseImageID_;            // base image ID found in the CSV file
    private ImageType imageType_;        // image type

    // image meta data
    private String OS_;     // operating system, e.g. Ubuntu or CentOS
    private String osVersion_;  // OS version number, e.g. 12.04 for Ubuntu or 6.3 for CentOS
    private int imageSize_;  // in GB
    private String architecture_;  // i386 or x86_64

    /**
     * Creates a new image and assigns a random UUID.
     */
    protected Image() {
        uuid = UUID.randomUUID();
        LOGGER.info("ics.core.Image(): Creating image, UUID: "
                + uuid.toString());

        /************
        // reading the ics.properties file locate inside the war bundle
        //ResourceBundle rb = ResourceBundle.getBundle(Constants.BUNDLE_NAME);
        imageDirectory = rb.getString(Constants.IMAGE_DIRECTORY_PROPERTY);
        baseUrl = rb.getString(Constants.BASE_URL_PROPERTY);
        targetDirectoryName = rb.getString(Constants.TARGET_DIRECTORY_PROPERTY);
        **************/

        // reading the ics.properties file located outside the war bundle
        PropertiesConfiguration config = PropertiesReader.getPropertiesConfiguration(Constants.ICS_CONFIG_FILE);
        imageDirectory = config.getString(Constants.IMAGE_DIRECTORY_PROPERTY);
        baseUrl = config.getString(Constants.BASE_URL_PROPERTY);
        targetDirectoryName = config.getString(Constants.TARGET_DIRECTORY_PROPERTY);
        webappsDirectoryName = null;

        LOGGER.debug("ics.core.Image(): Base URL: " + baseUrl);
    }

    /**
     * Creates a new image and assigns a random UUID.
     * @param location directory location that stores this image
     * @param url   URL for downloading the image
     * @param targetDir A directory to store the uploaded files in the image (without leading or trailing '/')
     * @param imageType Image type
     * @param file  The qcow2 file
     * @see eu.optimis.ics.core.image.ImageType
     */
    public Image(String location, String url, String targetDir,
            ImageType imageType, File file) {
        baseUrl = url;
        imageDirectory = location;  // need to add "/" at the end.
        targetDirectoryName = targetDir;
        imageType_ = imageType;
        imageFile = file;

        uuid = UUID.randomUUID();
        LOGGER.info("ics.core.Image(): Creating image with UUID: "
                + uuid.toString() + " -- filename: " + imageFile.getName());

        LOGGER.debug("ics.core.Image(): Base URL is " + baseUrl);
    }

    /**
     * Returns this Image's UUID.
     * 
     * @return this Image's UUID
     */
    public UUID getUuid() {
        return uuid;
    }

    /**
     * Returns this Image's UUID as a string representation.
     * 
     * @return this Image's UUID as a string representation.
     */
    public String toString() {
        return uuid.toString();
    }

    /**
     * Returns a text/plain representation of the image.
     */
    public String toTextPlain() {
        int capacity = 200;
        StringBuffer result = new StringBuffer(capacity);
        result.append("ID: " + uuid.toString());
        result.append(", state: " + getState());
        result.append(", file: " + imageFile);

        if (getState().equals(ImageState.FINALIZED)) {
            result.append(", URL: " + getUrl());
        }

        result.append("\n");

        return result.toString();
    }

    /**
     * Copies the image meta data like OS, OS version, architecture, image size
     * @param img   Source of image meta data
     * @return <tt>true</tt> if the copy procedure successful, <tt>false</tt> otherwise
     */
    public boolean copyImageMetaData(Image img) {
        if (img == null) {
            return false;
        }

        OS_ = img.getOS();
        osVersion_ = img.getOSVersion();
        architecture_ = img.getArchitecture();
        imageSize_ = img.getImageSize();
        webappsDirectoryName = img.getWebappsDirectoryName();
        return true;
    }

    /**
     * Gets the detailed information of this image
     * @return A string containing the detail image information
     */
    public String detailedInfo() {
        int capacity = 500;
        StringBuffer result = new StringBuffer(capacity);
        result.append("ID: " + uuid.toString());
        result.append(", state: " + getState());
        result.append(", file: " + imageFile);

        if (getState().equals(ImageState.FINALIZED)) {
            result.append(", URL: " + getUrl());
        }

        result.append("\n");
        if (OS_ != null) {
            result.append("Operating system: ");
            result.append(OS_);
        }

        if (osVersion_ != null) {
            result.append(" " + osVersion_);
        }

        if (architecture_ != null) {
            result.append("\nArchitecture: ");
            result.append(architecture_);
        }

        if (imageSize_ > 0) {
            result.append("\nImage size: ");
            result.append(imageSize_);
            result.append(" GB");
        }
        result.append("\n");

        return result.toString();
    }

    /**
     * Returns the URL where this image can be obtained.
     * 
     * @return the URL where this image can be obtained.
     */
    public String getUrl() {
        return baseUrl + "/" + imageFile.getName();
    }

    /**
     * Sets this Image's state to <code>state</code>.
     * 
     * @param state
     *            this Image's new state
     */
    public synchronized void setState(ImageState state) {
        LOGGER.info("ics.core.Image.setState(): Setting state of image "
                + uuid.toString() + " from " + this.state + " to " + state);
        this.state = state;
    }

    /**
     * Returns the actual image file for this Image.
     * 
     * @return the actual image file for this Image.
     */
    public synchronized File getImageFile() {
        return imageFile;
    }

    /**
     * Returns this Image's state.
     * 
     * @return this Image's state
     */
    public synchronized ImageState getState() {
        return this.state;
    }

    /**
     * Deletes the actual image file associated with this VM image.
     * 
     * @throws IOException Could not delete file error
     */
    public synchronized void deleteImageFile() {
        LOGGER.info("ics.core.Image.deleteImageFile(): Deleting image file "
                + imageFile);
        try {
            FileUtils.forceDelete(imageFile);
        } catch (IOException ioException) {
            LOGGER.warn("ics.core.Image.deleteImageFile(): Could not delete file '"
                    + imageFile + "'", ioException);
        }

        //LOGGER.info("ics.core.Image.deleteImageFile(): File " + imageFile + " is deleted");
        imageFile = null;
    }

    /**
     * Clones an image from the according base image.
     * <p/>
     * Subclasses of <code>Image</code> need to override this method to clone
     * the correct base image file.
     * 
     * @throws IOException
     *             If an I/O error occurs while cloning the image
     * @throws OutOfDiskSpaceException
     *             If there is not enough disk space for cloning the image
     */
    public void cloneImage() throws OutOfDiskSpaceException, IOException {};

    /**
     * Puts <code>file</code> into this image file at location
     * <code>targetDirectoryName</code>, which is taken from the service's
     * properties file.
     * 
     * @param file the file to put in the image
     */
    public synchronized void putFile(File file) {
        LOGGER.debug("ics.core.Image.putFile(): Putting file to image "
                + getUuid().toString());
        this.setState(ImageState.BUSY);
        FileCopier fileCopier = new FileCopier(this, file, targetDirectoryName);
        fileCopier.start();

    }

    /**
     * Puts <code>file</code> into this image file at location
     * <code>targetDirectoryName</code>, which is taken from the service's
     * properties file.
     * 
     * @param file
     *            the file to put in the image
     * 
     * @param extract
     *            if the image should be extracted (<code>true</code>) or just
     *            copied (<code>false</code>)
     */
    public synchronized void putFile(File file, boolean extract) {
        LOGGER.debug("ics.core.Image.putFile(): Putting file to image "
                + getUuid().toString());
        this.setState(ImageState.BUSY);
        FileCopier fileCopier = new FileCopier(this, file, targetDirectoryName, extract);
        fileCopier.start();
    }

    /**
     * Copies all files located in the <tt>sourceDirectory</tt> to the image
     * @param sourceDirectory   a temporary directory storing all the files
     * @return <tt>true</tt> if successful, <tt>false</tt> otherwise
     */
    public boolean putDirectory(String sourceDirectory) {
        LOGGER.debug("ics.core.Image.putDirectory(): Putting file to image "
                + getUuid().toString());
        this.setState(ImageState.BUSY);
        DirectCopierSystemCall directCopier = new DirectCopierSystemCall(this, sourceDirectory, targetDirectoryName);
        //directCopier.start();  // NOTE: not used a threaded approach anymore

        boolean result = directCopier.run();
        if (result == false) {
            LOGGER.debug("ics.core.Image.putDirectory(): Error in copying the file to the disk image");
        }
        /****
        else {
            LOGGER.debug("ics.core.Image.putDirectory(): No mounting error");
        }
        ****/

        return result;
    }

    /**
     * Sets the permission of a given file located on the image.
     * If it is a directory, it will change the permission recursively.
     * @param file   a filename. It can also be a directory name.
     * @param permissions a permission mode, e.g. "644" or "600" 
     */
    public synchronized void setPermissions(String file, String permissions) {
        LOGGER.debug("ics.core.Image.setPermissions(): Setting permissions of "
                + file + " on image " + getUuid().toString() + " to "
                + permissions);
        this.setState(ImageState.BUSY);
        PermissionSetter permissionSetter = new PermissionSetter(this, file, permissions);
        permissionSetter.start();
    }

    /**
     * Puts a war file into the file
     * @param warFile   a war file
     */
    public void putWarFile(File warFile) {
        LOGGER.debug("ics.core.Image.putWarFile(): Putting WAR file to image "
                + getUuid().toString());
        this.setState(ImageState.BUSY);

        FileCopier fileCopier = new FileCopier(this, warFile, webappsDirectoryName);
        fileCopier.start();
    }

    /**
     * Sets the operating system of this image
     * @param os    operating system
     */
    public void setOS(String os) {
        if (os != null) {
            OS_ = os;
        }
    }

    /**
     * Gets the image's operating system
     * @return operating system of the image
     */
    public String getOS() {
        return OS_;
    }

    /**
     * Sets the operating system version of this image
     * @param version operating system version
     */
    public void setOSVersion(String version) {
        if (version != null) {
            osVersion_ = version;
        }
    }

    /**
     * Gets the operating system version of this image
     * @return operating system version
     */
    public String getOSVersion() {
        return osVersion_;
    }

    /**
     * Sets the image file size (in GB and integer only)
     * @param size image file size (in GB and integer only)
     */
    public void setImageSize(int size) {
        if (size > 0) {
            imageSize_ = size;
        }
    }

    /**
     * Gets the image file size (in GB and integer only)
     * @return image file size (in GB and integer only)
     */
    public int getImageSize() {
        return imageSize_;
    }

    /**
     * Sets the architecture of the image (i386 or x86_64)
     * @param arch the architecture of the image (i386 or x86_64)
     */
    public void setArchitecture(String arch) {
        if (arch != null) {
            architecture_ = arch;
        }
    }

    /**
     * Gets the architecture of the image (i386 or x86_64)
     * @return the architecture of the image (i386 or x86_64)
     */
    public String getArchitecture() {
        return architecture_;
    }

    /**
     * Gets the image location, i.e. absolute path with the image filename
     * @return image location with the filename
     */
    public String getImageLocation() {
        return imageDirectory + "/" + imageFile.getName();
    }

    /**
     * Gets only the image filename (without the directory path)
     * @return image filename (without the directory path)
     */
    public String getImageFilename() {
        return imageFile.getName();
    }

    /**
     * Sets the base image ID that was cloned or copied from
     * @param id    base image ID
     */
    public void setBaseImageID(int id) {
        if (id >= 0) {
            baseImageID_ = id;
        }
    }

    /**
     * Gets the base image ID
     * @return base image ID
     */
    public int getBaseImageID() {
        return baseImageID_;
    }

    /**
     * Sets the directory path for storing war files inside the image, e.g. /var/lib/tomcat6/webapps
     * @param dirName directory path
     */
    public void setWebappsDirectoryName(String dirName) {
        if (dirName != null) {
            webappsDirectoryName = dirName;
        }
    }

    /**
     * Gets the directory path
     * @return directory path
     */
    public String getWebappsDirectoryName() {
        return webappsDirectoryName;
    }

    /**
     * Gets the image type
     * @return image type
     * @see eu.optimis.ics.core.image.ImageType
     */
    public ImageType getImageType() {
        return imageType_;
    }

}
