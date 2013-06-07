/* $Id: ImageCreationService.java 11547 2013-02-19 17:34:23Z sulistio $ */

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

package eu.optimis.ics.core;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
// import java.util.ResourceBundle;
import org.apache.log4j.PropertyConfigurator;
import org.apache.log4j.Logger;
import au.com.bytecode.opencsv.CSVReader;
import java.io.FileReader;

import com.db4o.ObjectContainer;
import com.db4o.ObjectServer;
import com.db4o.ObjectSet;
import com.db4o.cs.Db4oClientServer;
import com.db4o.query.Predicate;

import eu.optimis.ics.core.exception.*;
import eu.optimis.ics.core.image.*;
import eu.optimis.ics.core.io.FileUtils;
import eu.optimis.ics.core.util.*;
import org.apache.commons.configuration.PropertiesConfiguration;

/**
 * This is the main class of the Image Creation Service. It interacts with
 * the ImageCreationServiceREST class in providing the functionalities in
 * creating and deleting images.
 * 
 * @author Roland Kuebert
 * @author Tinghe Wang 
 * @author Anthony Sulistio
 * 
 */
public class ImageCreationService {

    /** An instance of this class **/
    private static ImageCreationService instance = null;

    /** ICS database **/
    private static ObjectServer databaseServer;

    /** ICS database for base images **/
    private static ObjectServer baseImageServer_;

    /** Log4j logger instance. */
    private static Logger LOGGER = Logger.getLogger(ImageCreationService.class.getName());

    private String defaultImageFilename_; // absolute path + filename of base image
    private String csvFilename_;          // absolute path + CSV filename
    private String dir_;                  // directory name
    private String baseUrl_ = null;       // base URL for downloading the image
    private SAXParserHandler parser_;     // XML parser for reading the image requirement

    // a temp dir for storing uploaded files before coping them to the image
    private String targetDirectoryName_;

    /**
     * Default constructor
     */
    private ImageCreationService() {

        /******  // old approach
        // reading the ics.properties file locate inside the war bundle
        //ResourceBundle rb = ResourceBundle.getBundle(Constants.BUNDLE_NAME);
        String databaseFilename = rb.getString(Constants.DATABASE_FILENAME_PROPERTY);					
        String baseImageFilename = rb.getString(Constants.DATABASE_BASEIMAGE_FILENAME_PROPERTY);			
        dir_ = rb.getString(Constants.IMAGE_DIRECTORY_PROPERTY);
        baseUrl_ = rb.getString(Constants.BASE_URL_PROPERTY);
        targetDirectoryName_ = rb.getString(Constants.TARGET_DIRECTORY_PROPERTY);
        csvFilename_ = dir_ + "/" + rb.getString(Constants.IMAGE_TEMPLATE_LIST_PROPERTY);
        ********/

        // reading the ics.properties file located outside the war bundle
        PropertyConfigurator.configure(PropertiesReader.getConfigFilePath(Constants.LOG4J_CONFIG_FILE));
        PropertiesConfiguration config = PropertiesReader.getPropertiesConfiguration(Constants.ICS_CONFIG_FILE);
        String databaseFilename = config.getString(Constants.DATABASE_FILENAME_PROPERTY);
        String baseImageFilename = config.getString(Constants.DATABASE_BASEIMAGE_FILENAME_PROPERTY);
        dir_ = config.getString(Constants.IMAGE_DIRECTORY_PROPERTY);
        baseUrl_ = config.getString(Constants.BASE_URL_PROPERTY);
        targetDirectoryName_ = config.getString(Constants.TARGET_DIRECTORY_PROPERTY);
        csvFilename_ = dir_ + "/"
                + config.getString(Constants.IMAGE_TEMPLATE_LIST_PROPERTY);

        if (databaseServer == null) {
            databaseServer = Db4oClientServer.openServer(Db4oClientServer.newServerConfiguration(), databaseFilename, 0);
        }

        if (baseImageServer_ == null) {
            baseImageServer_ = Db4oClientServer.openServer(Db4oClientServer.newServerConfiguration(), baseImageFilename, 0);
        }

        // Debugging info
        LOGGER.debug("ics.core.ImageCreationService(): image database = "
                + databaseFilename);
        LOGGER.debug("ics.core.ImageCreationService(): base image database = "
                + baseImageFilename);
        LOGGER.debug("ics.core.ImageCreationService(): image directory = "
                + dir_);
        LOGGER.debug("ics.core.ImageCreationService(): base URL = " + baseUrl_);
        LOGGER.debug("ics.core.ImageCreationService(): target directory = "
                + targetDirectoryName_);

        parser_ = new SAXParserHandler();
        readCSV();
    }

    /*
     * Creates a new instance or object of this class
     */
    public static ImageCreationService getInstance() {
        if (instance == null) {
            LOGGER.debug("ics.core.ICS.getInstance(): Create a new instance of ICS");
            instance = new ImageCreationService();
        }

        return instance;
    }

    /**
     * Creates an image according to a given image type, i.e. core or orchestration
     * element. Note that with the addition of a new feature of using a list of
     * template / base images, the aforementioned elements will be created from
     * <b>the 1st base image</b> listed in the CSV template file.
     * 
     * @param imageType the image type
     * @return the newly-created image or an exception error if fails
     * @throws IOException any I/O errors during the image creation phase
     * @throws OutOfDiskSpaceException  an error due to run out of disk space 
     */
    public Image createImage(ImageType imageType) throws IOException,
            OutOfDiskSpaceException {
        Image newImage;

        if (imageType.equals(ImageType.CoreElement)) {
            //newImage = new CoreElement();
            newImage = new CoreElement(defaultImageFilename_);
        } else if (imageType.equals(ImageType.OrchestrationElement)) {
            //newImage = new OrchestrationElement();
            newImage = new OrchestrationElement(defaultImageFilename_);
        } else {
            throw new RuntimeException("Unsupported image type: "
                    + imageType.toString());
        }

        ArrayList<Image> list = getBaseImages();
        for (Image image : list) {
            //LOGGER.debug("ics.core.ICS.getImage(): Checking image with id " + image.getUuid().toString());
            if (image.getImageLocation().equals(defaultImageFilename_)) {
                LOGGER.debug("ics.core.ICS.getImage(): Target image found");
                newImage.copyImageMetaData(image);
                break;
            }
        }

        ObjectContainer databaseClient = databaseServer.openClient();
        databaseClient.store(newImage);
        databaseClient.close();

        return newImage;
    }

    /**
     * Creates an image based on the input requirement.
     * @param input the image requirement
     * @return the newly-created image or an exception error if fails
     * @throws IOException any I/O errors during the image creation phase
     * @throws OutOfDiskSpaceException  an error due to run out of disk space 
     */
    public String createImage(String input) throws IOException,
            OutOfDiskSpaceException {

        ImageRequirement imageReq = parser_.getRequirement(input);
        return createImageFromRequirement(imageReq);
    }

    /**
     * Creates an image based on the input file describing the requirement.
     * @param input the requirement file
     * @return the newly-created image or an exception error if fails
     * @throws IOException any I/O errors during the image creation phase
     * @throws OutOfDiskSpaceException  an error due to run out of disk space 
     */
    public String createImage(File xmlFile) throws IOException,
            OutOfDiskSpaceException {

        if (xmlFile == null) {
            return "Error: XML file does not exist";
        }

        ImageRequirement imageReq = parser_.getRequirement(xmlFile);
        return createImageFromRequirement(imageReq);
    }

    /**
     * Creates an image based on the input image requirement object
     * @param imageReq  the ImageRequirement object
     * @return the newly-created image or an exception error if fails
     * @throws IOException any I/O errors during the image creation phase
     * @throws OutOfDiskSpaceException an error due to run out of disk space
     */
    private String createImageFromRequirement(ImageRequirement imageReq)
            throws IOException, OutOfDiskSpaceException {

        String tmpStr = imageReq.getOS();
        if (tmpStr != null && tmpStr.equalsIgnoreCase("linux") == true) {
            tmpStr = null;
        }
        final String imageOS = tmpStr;
        final int imageSize = imageReq.getImageSize();
        final String osVersion = imageReq.getOSVersion();
        final String imageArch = imageReq.getArchitecture();
        LOGGER.debug("ics.core.ICS.createImage(): New requirement = "
                + imageReq.toString());
        //LOGGER.debug("ics.core.ICS.createImage(): " + imageOS + " and "
        //        + imageSize);

        // search the suitable base image in the db.
        // It searches all the requirements (if any).
        ObjectContainer baseImageClient = baseImageServer_.openClient();
        List<Image> result = baseImageClient.query(new Predicate<Image>() {
            private static final long serialVersionUID = 7137024805739192385L;

            public boolean match(Image obj) {
                //System.out.println("img = " + obj.getBaseImageID() + " " + obj.getOS());
                boolean os = true;
                boolean version = true;
                boolean arch = true;

                // search for the matched base image OS, e.g. centos or ubuntu
                if (imageOS != null
                        && obj.getOS().equalsIgnoreCase(imageOS) == false) {
                    os = false;   // doesn't matched 
                }

                // search for the matched base image OS version, e.g. 6.3 or 12.10
                if (osVersion != null
                        && obj.getOSVersion().equalsIgnoreCase(osVersion) == false) {
                    version = false;   // doesn't matched 
                }

                // search for the matched base image architecture, e.g. i386 or x86_64
                if (imageArch != null
                        && obj.getArchitecture().equalsIgnoreCase(imageArch) == false) {
                    arch = false;    // doesn't matched 
                }

                // by default only search the base image size
                return os && version && arch && obj.getImageSize() >= imageSize;
            }
        });

        baseImageClient.close();  // close the base image db

        // if no matched base image(s) found
        if (result.size() == 0) {
            return "Error - can't find a base image with "
                    + imageReq.toString();
        }

        // Debugging
        LOGGER.debug("ics.core.ICS.createImage(): Result size = "
                + result.size());
        for (int i = 0; i < result.size(); i++) {
            Image tmp = result.get(i);
            LOGGER.debug("ics.core.ICS.createImage(): base image ID = "
                    + tmp.getBaseImageID() + " -- " + tmp.toString());
        }

        // choose the base image that is listed the 1st in the search result
        // and store into the image db
        Image baseImage = result.get(0);
        ObjectContainer databaseClient = databaseServer.openClient();
        Image newImage = new CoreElement(baseImage.getImageLocation());
        newImage.copyImageMetaData(baseImage);
        databaseClient.store(newImage);
        databaseClient.close();

        return newImage.toString();   // UUID of the new image
    }

    /**
     * Puts or uploads the input text file into the image
     * @param imageId the image ID
     * @param file  the text file to be uploaded into the image
     * @throws IOException an I/O exception error
     * @throws ImageNotFoundException an exception error if the image is not found
     * @throws UnsupportedStateException an exception error for having the wrong image state 
     */
    public void putFile(String imageId, File file) throws IOException,
            ImageNotFoundException, UnsupportedStateException {
        LOGGER.debug("ics.core.ICS.putFile(): Adding file " + file
                + " to image " + imageId);
        Image image = getImage(imageId);
        if (image == null) {
            throw new ImageNotFoundException();
        } else {
            // Put file only if image is READY
            if (image.getState().equals(ImageState.READY) == true) {
                image.putFile(file);
            } else {
                throw new UnsupportedStateException("Cannot put file to image in state "
                        + image.getState()
                        + "; needs to be "
                        + ImageState.READY);
            }
        }
    }

    /**
     * Puts or uploads the input war file into the image
     * @param imageId the image ID
     * @param file  the war file to be uploaded into the image
     * @throws IOException an I/O exception error
     * @throws ImageNotFoundException an exception error if the image is not found
     * @throws UnsupportedStateException an exception error for having the wrong image state 
     */
    public void putWarFile(String imageId, File warFile) throws IOException,
            ImageNotFoundException, UnsupportedStateException {
        LOGGER.debug("ics.core.ICS.putWarFile(): Adding WAR file " + warFile
                + " to image " + imageId);
        Image image = getImage(imageId);
        if (image == null) {
            throw new ImageNotFoundException();
        } else {
            // Put file only if image is READY
            if (image.getState().equals(ImageState.READY) == true) {
                image.putWarFile(warFile);
            } else {
                throw new UnsupportedStateException("Can not put the file to image in state "
                        + image.getState()
                        + "; needs to be "
                        + ImageState.READY);
            }
        }
    }

    /**
     * Puts or uploads the text file into the image's source directory
     * @param imageId the image ID
     * @param sourceDirectory  the image's source directory
     * @throws IOException an I/O exception error
     * @throws ImageNotFoundException an exception error if the image is not found
     * @throws UnsupportedStateException an exception error for having the wrong image state 
     */
    public void putFileDirectory(String imageId, String sourceDirectory)
            throws IOException, ImageNotFoundException,
            UnsupportedStateException {

        LOGGER.debug("ics.core.ICS.putFileDirectory(): Adding file (normal or zip) in directory "
                + sourceDirectory + " to image " + imageId);
        Image image = getImage(imageId);
        if (image == null) {
            throw new ImageNotFoundException();
        } else {
            // Put file only if image is READY
            if (image.getState().equals(ImageState.READY) == true) {
                boolean result = image.putDirectory(sourceDirectory);
                if (result == false) {
                    throw new IOException("Error in copying the file to the disk image");
                }
            } else {
                throw new UnsupportedStateException("Cannot put file to image in state "
                        + image.getState()
                        + "; needs to be "
                        + ImageState.READY);
            }
        }
    }

    /**
     * Changes the image state to FINALIZED. Thus, preventing any changes from being made.
     * 
     * @param imageId the id of the image to finalize
     * @throws ImageNotFoundException If an image with id <code>imageId</code> is not found
     * @throws IOException If an I/O exception occurs when accessing the image
     * @throws StateChangeException If the image is not in the READY state
     * @return the image object that has been finalized
     */
    public Image finalizeImage(String imageId) throws ImageNotFoundException,
            IOException, StateChangeException {
        LOGGER.debug("ics.core.ICS.finalizeImage(): Finalizing image '"
                + imageId + "'");
        Image image = getImage(imageId);
        if (image == null) {
            LOGGER.info("ics.core.ICS.finalizeImage(): Image '" + imageId
                    + "' not found");
            throw new ImageNotFoundException();
        } else {
            // Finalize can only be done if the image is READY before
            if (image.getState().equals(ImageState.READY) == true) {
                LOGGER.info("ics.core.ICS.finalizeImage(): Image '" + imageId
                        + "' is in state " + ImageState.READY + ", moving to "
                        + ImageState.FINALIZED);
                image.setState(ImageState.FINALIZED);
                updateImage(image);
            } else {
                LOGGER.info("ics.core.ICS.finalizeImage(): Cannot change state from "
                        + image.getState() + " to " + ImageState.FINALIZED);
                throw new StateChangeException("Cannot change state from "
                        + image.getState() + " to " + ImageState.FINALIZED);
            }
        }

        return image;
    }

    /**
     * Changes the image state to READY from FINALIZED. Thus, allowing changes to be made.
     * 
     * @param imageId the id of the image to finalize
     * @throws ImageNotFoundException If an image with id <code>imageId</code> is not found
     * @throws IOException If an I/O exception occurs when accessing the image
     * @throws StateChangeException If the image is not in the FINALIZED state
     */
    public void unfinalizeImage(String imageId) throws ImageNotFoundException,
            IOException, StateChangeException {

        LOGGER.debug("ics.core.ICS.unfinalizeImage(): Unfinalizing image '"
                + imageId + "'");
        Image image = getImage(imageId);
        if (image == null) {
            LOGGER.debug("ics.core.ICS.unfinalizeImage(): Image not found");
            throw new ImageNotFoundException();
        } else {
            if (image.getState().equals(ImageState.FINALIZED) == true) {
                LOGGER.debug("ics.core.ICS.unfinalizeImage(): Image found, state is FINALIZED, change to READY");
                image.setState(ImageState.READY);
                updateImage(image);
            } else {
                LOGGER.debug("ics.core.ICS.unfinalizeImage(): State is "
                        + image.getState() + ", cannot unfinalize");
                throw new StateChangeException("Cannot change state from "
                        + image.getState() + " to " + ImageState.READY);
            }
        }

    }

    /**
     * Deletes the image with id <code>imageId</code> from disk and database.
     * 
     * @param imageId the id of the image to delete
     * @throws IOException If an I/O exception occurs while deleting the image file
     * @throws ImageNotFoundException If an image with id <code>imageId</code> is not found
     */
    public void deleteImage(final String imageId) throws IOException,
            ImageNotFoundException {

        ObjectContainer databaseClient = databaseServer.openClient();
        List<Image> images = databaseClient.query(new Predicate<Image>() {
            private static final long serialVersionUID = 7137024805739192385L;

            public boolean match(Image image) {
                return image.getUuid().toString().equals(imageId);
            }
        });

        if (images.size() == 0) {
            LOGGER.debug("ics.core.ICS.deleteImage(): No image found");
            databaseClient.close();
            throw new ImageNotFoundException();
        }

        if (images.size() > 1) {
            LOGGER.warn("ics.core.ICS.deleteImage(): Something's fishy, found "
                    + images.size() + " images with id " + imageId);
            LOGGER.warn("ics.core.ICS.deleteImage(): Returning first one");
        }

        //LOGGER.info("ics.core.ICS.deleteImage(): Deleting image");
        Image image = images.get(0);
        image.deleteImageFile();
        databaseClient.delete(image);
        databaseClient.close();
        //LOGGER.info("ics.core.ICS.deleteImage(): Done");
    }

    // sourceDirectory = a temp location for storing war / zip / txt files 
    /**
     * Deletes the image physically and from the database. It also deletes
     * the temporary folder.
     * @param imageId   the ID of the image to be deleted
     * @param sourceDirectory   the temporary folder or location for storing files
     * @throws IOException      If an I/O exception occurs while deleting the image file
     * @throws ImageNotFoundException If an image with id <code>imageId</code> is not found
     */
    public void deleteImage(String imageId, String sourceDirectory)
            throws IOException, ImageNotFoundException {

        deleteImage(imageId);

        // delete temp directory
        if (sourceDirectory != null) {
            LOGGER.debug("ics.core.ICS.deleteImage(): Recursively delete "
                    + sourceDirectory);
            FileUtils.deleteDirectory(sourceDirectory);
        }

        return;
    }

    /**
     * Returns the image of a given ID
     * 
     * @param imageId the image ID
     * @return the image, if found. Otherwise, an <code>ImageNotFoundException</code>
     * 	is thrown
     * @throws IOException if an I/O error occurs looking for the image
     * @throws ImageNotFoundException if the image file cannout be found
     */
    public Image getImage(final String imageId) throws IOException,
            ImageNotFoundException {

        LOGGER.debug("ics.core.ICS.getImage(): Getting image with id "
                + imageId);

        // open the db40 database and find the image according to its ID
        ObjectContainer databaseClient = databaseServer.openClient();
        List<Image> images = databaseClient.query(new Predicate<Image>() {
            private static final long serialVersionUID = 7137024805739192385L;

            public boolean match(Image image) {
                return image.getUuid().toString().equals(imageId);
            }
        });
        databaseClient.close();

        if (images.size() == 0) {
            LOGGER.debug("ics.core.ICS.getImage(): No image found");
            throw new ImageNotFoundException();
        }

        if (images.size() > 1) {
            LOGGER.warn("ics.core.ICS.getImage(): Something's fishy, found "
                    + images.size() + " images with id " + imageId);
            LOGGER.warn("ics.core.ICS.getImage(): Returning first one");
        }

        Image image = images.get(0);
        return image;

        /*******  // old approach
        ArrayList<Image> images = getImages();  
        LOGGER.debug("ics.core.ICS.getImage(): ICS contains " + images.size()
                + " images");
        for (Image image : images) {
            LOGGER.debug("ics.core.ICS.getImage(): Checking image with id "
                    + image.getUuid().toString());
            if (image.getUuid().toString().equals(imageId)) {
                LOGGER.debug("ics.core.ICS.getImage(): Target image found");
                return image;
            }
        }

        throw new ImageNotFoundException();
        ********/
    }

    /**
     * Returns the base image of a given id.
     * 
     * @param imageId the base image ID
     * @return the image, if found. Otherwise, an <code>ImageNotFoundException</code>
     * 	is thrown
     * @throws IOException if an I/O error occurs looking for the image
     * @throws ImageNotFoundException if the image file cannout be found
     */
    public Image getBaseImage(String imageId) throws IOException,
            ImageNotFoundException {

        LOGGER.debug("ics.core.ICS.getBaseImage(): Getting image with id "
                + imageId);

        ArrayList<Image> images = getBaseImages();
        LOGGER.debug("ics.core.ICS.getBaseImage(): ICS contains "
                + images.size() + " images");
        for (Image image : images) {
            LOGGER.debug("ics.core.ICS.getBaseImage(): Checking image with id "
                    + image.getUuid().toString());
            if (image.getUuid().toString().equals(imageId)) {
                LOGGER.debug("ics.core.ICS.getBaseImage(): Target image found");
                return image;
            }
        }

        throw new ImageNotFoundException();
    }

    /**
     * Gets a list of available images
     * 
     * @return an ArrayList of all available images
     */
    public ArrayList<Image> getImages() {
        return getImagesFromDB(databaseServer);
    }

    /**
     * Gets a list of available template or base images
     * 
     * @return an ArrayList of all available base images
     */
    public ArrayList<Image> getBaseImages() {
        return getImagesFromDB(baseImageServer_);
    }

    /**
     * Gets a list of available images from a given database server
     * @param dbServer  the database server
     * @return an ArrayList of all available images
     */
    private ArrayList<Image> getImagesFromDB(ObjectServer dbServer) {
        ObjectContainer databaseClient = null;
        ObjectSet<Image> images;
        ArrayList<Image> result;
        try {
            databaseClient = dbServer.openClient();

            images = databaseClient.queryByExample(Image.class);
            result = new ArrayList<Image>();
            Image img = null;
            while (images.hasNext()) {
                img = (Image) images.next();
                result.add(img);
            }
        } finally {
            databaseClient.close();
        }

        return result;
    }

    /**
     * Empties the database and removes all the image files
     */
    public void flushDatabase() {
        flushDatabase(databaseServer, true);
    }

    /**
     * Updates the base image database. This method will clean up the existing database
     * and re-read the CSV template file.
     */
    public void updateBaseImageDB() {
        //flushDatabase(baseImageServer_, false);  // don't delete image
        LOGGER.debug("ics.core.ICS.updateBaseImageDB(): flush base image db and read CSV file");
        readCSV();
    }

    /**
     * Deletes all the images in the database and physical hard disk
     * @param dbServer      the database server
     * @param deleteFile    <tt>true</tt> means to delete the image file
     */
    private void flushDatabase(ObjectServer dbServer, boolean deleteFile) {
        ObjectContainer databaseClient = null;
        ObjectSet<Image> images;
        try {
            databaseClient = dbServer.openClient();
            images = databaseClient.queryByExample(Image.class);
            while (images.hasNext()) {
                Image image = images.next();
                databaseClient.delete(image);
                if (deleteFile == true) {
                    image.deleteImageFile();  // delete image file in the physical location
                }
            }
        } finally {
            databaseClient.close();
        }
    }

    /**
     * Updates the image state in the database.
     * 
     * @param image the image to be updated
     * @throws ImageNotFoundException an exception error if image is not found
     */
    public void updateImage(Image image) throws ImageNotFoundException {
        LOGGER.debug("ics.core.ICS.updateImage(): Updating state of image '"
                + image.getUuid());
        ObjectContainer databaseClient = databaseServer.openClient();

        final String imageId = image.getUuid().toString();

        List<Image> images = databaseClient.query(new Predicate<Image>() {

            /** Generated serial version UID. */
            private static final long serialVersionUID = 6409066093271240828L;

            /**
             * Matches an image if the image's UUID is equal to
             * a specific image id. 
             */
            public boolean match(Image thisImage) {
                return thisImage.getUuid().toString().equals(imageId);
            }
        });

        if (images.size() == 0) {
            LOGGER.warn("ics.core.ICS.updateImage(): No image with id '"
                    + image.getUuid() + "' found");
            throw new ImageNotFoundException();
        }
        if (images.size() > 1) {
            LOGGER.warn("ics.core.ICS.updateImage(): Something's fishy, found "
                    + images.size() + " images with id " + imageId);
            LOGGER.warn("ics.core.ICS.updateImage(): Returning first one");
        }

        LOGGER.debug("ics.core.ICS.updateImage(): Image found");
        Image databaseImage = images.get(0);
        LOGGER.debug("ics.core.ICS.updateImage(): State was "
                + databaseImage.getState() + ", " + "setting to "
                + image.getState());
        databaseImage.setState(image.getState());
        databaseClient.store(databaseImage);

        databaseClient.close();
        return;
    }

    /**
     * Sets the permission of a given file located on the image.
     * If it is a directory, it will change the permission recursively.
     * The image state need to be READY
     * @param imageId   the image ID
     * @param file      a filename. It can also be a directory name.
     * @param permissions   a permission mode, e.g. "644" or "600" 
     * @throws IOException  an I/O exception error
     * @throws ImageNotFoundException   an exception error if image is not found
     * @throws UnsupportedStateException    an exception error if the image state is not READY
     */
    public void setPermissions(String imageId, String file, String permissions)
            throws IOException, ImageNotFoundException,
            UnsupportedStateException {
        LOGGER.debug("ics.core.ICS.setPermissions(): Setting permissions of file "
                + file + " in image " + imageId + " to " + permissions);
        Image image = getImage(imageId);
        if (image == null) {
            throw new ImageNotFoundException();
        } else {
            // Put file only if image is READY
            if (image.getState().equals(ImageState.READY) == true) {
                image.setPermissions(file, permissions);
            } else {
                throw new UnsupportedStateException("Cannot set permissions on image in state "
                        + image.getState()
                        + "; needs to be "
                        + ImageState.READY);
            }
        }
    }

    /**
     * Puts or uploads the input zip file into the image
     * @param imageId the image ID
     * @param archiveFile  the zip file to be uploaded into the image
     * @throws IOException an exception I/O error
     * @throws ImageNotFoundException an exception error if the image is not found
     * @throws UnsupportedStateException an exception error for having the wrong image state 
     */
    public void putZipFile(String imageId, File archiveFile)
            throws IOException, ImageNotFoundException,
            UnsupportedStateException {
        LOGGER.debug("ics.core.ICS.putZipFile(): Adding file " + archiveFile
                + " to image " + imageId);
        Image image = getImage(imageId);
        if (image == null) {
            throw new ImageNotFoundException();
        } else {
            // Put file only if image is READY
            if (image.getState().equals(ImageState.READY) == true) {
                image.putFile(archiveFile, true);
            } else {
                throw new UnsupportedStateException("Can not put the file to image in state "
                        + image.getState()
                        + "; needs to be "
                        + ImageState.READY);
            }
        }
    }

    /**
     * Reads the CSV template file that lists the base images
     */
    private void readCSV() {
        if (csvFilename_ == null) {
            LOGGER.debug("ics.core.ICS.readCSV(): Error - " + csvFilename_
                    + " is empty.");
            return;
        }

        // Create an instance of file object.
        File file = new File(csvFilename_);
        if (file.exists() == false) {
            LOGGER.debug("ics.core.ICS.readCSV(): Error - " + csvFilename_
                    + " does not exist.");
            return;
        }

        LOGGER.debug("ics.core.ICS.readCSV(): Reading " + csvFilename_);
        int lineNum = 0;
        try {
            // open the CSV file and read its content one line at a time
            CSVReader reader = new CSVReader(new FileReader(csvFilename_));
            String[] nextLine;

            // clear the database first before storing new data
            flushDatabase(baseImageServer_, false);  // don't delete image
            ObjectContainer databaseClient = baseImageServer_.openClient();

            // read the CSV file - one line at a time
            while ((nextLine = reader.readNext()) != null) {
                lineNum++;  // line number	        	

                // skip comments and empty lines
                if (nextLine[0].startsWith("#") == true
                        || nextLine[0].trim().length() == 0) {
                    continue;
                }

                //System.out.println("total = " + nextLine.length);
                if (nextLine.length < Constants.MAX_COLUMN) {
                    LOGGER.warn("ics.core.ICS.readCSV(): Warning - "
                            + csvFilename_ + " line " + lineNum
                            + " may have some missing columns.");
                    continue;
                }

                int id = Integer.parseInt(nextLine[0].trim());
                String fname = dir_ + "/" + nextLine[1].trim();

                // check if file doesn't exist
                File tmpFile = new File(fname);
                if (tmpFile.exists() == false) {
                    LOGGER.error("ics.core.ICS.readCSV(): Error in "
                            + csvFilename_ + " line " + lineNum + " -- "
                            + fname + " doesn't exist");
                    continue;
                }

                String os = nextLine[2].trim().toLowerCase();
                String osVersion = nextLine[3].trim().toLowerCase();
                int size = Integer.parseInt(nextLine[4].trim());
                String architecture = nextLine[5].trim().toLowerCase();
                String dirName = nextLine[6].trim().toLowerCase();

                // create the image template
                Image img = new Image(dir_, baseUrl_, targetDirectoryName_, ImageType.BaseImage, tmpFile);
                img.setBaseImageID(id);			// base image ID
                img.setOS(os);					// operating system (OS), e.g. Ubuntu or CentOS
                img.setOSVersion(osVersion);	// OS version, e.g. 12.04 LTS or 6.3
                img.setImageSize(size);			// image size in GB
                img.setArchitecture(architecture);		// OS architecture: i386 or x86_64
                img.setWebappsDirectoryName(dirName);	// location of tomcat webapps directory
                img.setState(ImageState.FINALIZED);

                // NOTE: set the 1st base image in the template list to be the DEFAULT
                // core and orchestration elements.
                if (id == 1) {
                    defaultImageFilename_ = fname;
                    LOGGER.info("ics.core.ICS.readCSV(): Set the default Core and Orchestration image to "
                            + fname);
                }

                databaseClient.store(img);   // store image to the database
                LOGGER.debug("ics.core.ICS.readCSV(): Store " + fname
                        + " to base image db.");
            }
            reader.close();
            databaseClient.close();
        } catch (Exception e) {
            LOGGER.debug("ics.core.ICS.readCSV(): Error in " + csvFilename_
                    + " line " + lineNum + " -- Exception error.");
            LOGGER.debug(e.toString());
        }
    }
}
