/* $Id: ImageCreationServiceREST.java 11547 2013-02-19 17:34:23Z sulistio $ */

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

package eu.optimis.ics;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
// import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
// import java.util.ResourceBundle;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.UUID;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import com.sun.jersey.multipart.FormDataParam;
import com.sun.jersey.spi.resource.Singleton;

import eu.optimis.ics.core.Constants;
import eu.optimis.ics.core.ImageCreationService;
import eu.optimis.ics.core.exception.ImageNotFoundException;
import eu.optimis.ics.core.exception.StateChangeException;
import eu.optimis.ics.core.exception.UnsupportedStateException;
import eu.optimis.ics.core.image.Image;
import eu.optimis.ics.core.image.ImageType;
import eu.optimis.ics.core.io.FileUtils;
import eu.optimis.ics.core.util.PropertiesReader;

/**
 * Image Creation Service REST API
 * 
 * @author Roland Kuebert (kuebert@hlrs.de)
 * @author Tinghe Wang (twang@hlrs.de)
 * @author Anthony Sulistio (sulistio@hlrs.de)
 * 
 */
@Path("/")
@Singleton
public class ImageCreationServiceREST {
    /** An instance of the ICS Core object that does all the functionalities */
    private static ImageCreationService ics;

    /** A logger */
    private final static Logger LOGGER = Logger.getLogger(ImageCreationServiceREST.class.getName());

    /** 
     * A temporary directory, where uploaded files (e.g. txt, zip or war) will be stored to initially.
     * It is defined in the ICS properties file.
     */
    private String sourceDirectory;

    /** A directory to store the uploaded files in the image, defined in the ICS properties file */
    private String targetDirectory;

    /** A Tomcat webapps directory to store the war files */
    private String webappDirectory;

    /**
     * A ICS REST Constructor
     */
    public ImageCreationServiceREST() {
        LOGGER.debug("ics.REST is invoked");
        if (ics == null) {
            ics = ImageCreationService.getInstance();
        }

        PropertyConfigurator.configure(PropertiesReader.getConfigFilePath(Constants.LOG4J_CONFIG_FILE));
        //ResourceBundle rb = ResourceBundle.getBundle("ics");  // old approach
        PropertiesConfiguration rb = PropertiesReader.getPropertiesConfiguration(Constants.ICS_CONFIG_FILE);
        sourceDirectory = rb.getString(Constants.SOURCE_DIRECTORY_PROPERTY);
        targetDirectory = rb.getString(Constants.TARGET_DIRECTORY_PROPERTY);
        webappDirectory = null;

        // Debugging info
        LOGGER.debug("ics.REST: source dir = " + sourceDirectory);
        LOGGER.debug("ics.REST: target dir = " + targetDirectory);
    }

    /**
     * Lists all available images
     * 
     * @return a string listing the available images
     */
    @GET
    @Produces("text/plain")
    @Path("/image")
    public String listImages() {
        LOGGER.debug("ics.REST.listImages(): GET /image - returning a list of all images");

        ArrayList<Image> images = ics.getImages();
        int capacity = 100 * images.size();
        StringBuffer result = new StringBuffer(capacity);
        for (Image image : images) {
            result.append(image.toTextPlain());
        }
        return result.toString();
    }

    /**
     * Lists all base images
     * 
     * @return a string listing the available template or base images
     */
    @GET
    @Produces("text/plain")
    @Path("/baseimage")
    public String listBaseImages() {
        LOGGER.debug("ics.REST.listBaseImages(): GET /baseimage - returning a list of all base images");

        ArrayList<Image> images = ics.getBaseImages();
        int capacity = 100 * images.size();
        StringBuffer result = new StringBuffer(capacity);
        for (Image image : images) {
            result.append(image.toTextPlain());
        }
        return result.toString();
    }

    /**
     * Updates the base image database. This is done by reading from a CSV file
     * defined in the ICS properties file and cleaning up the existing database information.
     * 
     * @return a list of updated base images
     */
    @GET
    @Produces("text/plain")
    @Path("/updatebaseimage")
    public String updateBaseImage() {
        LOGGER.debug("ics.REST.updateBaseImage(): GET /updatebaseimage - updating base image db & returning a list of all base images");

        ics.updateBaseImageDB();
        ArrayList<Image> images = ics.getBaseImages();
        int capacity = 100 * images.size();
        StringBuffer result = new StringBuffer(capacity);
        for (Image image : images) {
            result.append(image.toTextPlain());
        }
        return result.toString();
    }

    /**
     * Lists an information of a given image ID
     * 
     * @param imageId   the image ID
     * @return an information of a single image
     */
    @GET
    @Produces("text/plain")
    @Path("/image/{id}")
    public String listImage(@PathParam("id") String imageId) {
        LOGGER.debug("ics.REST.listImage(): Listing image with id '" + imageId
                + "'");
        Image image;
        try {
            image = ics.getImage(imageId.trim());
        } catch (IOException ioException) {
            LOGGER.error("ics.REST.listImage(): IOException looking for image", ioException);
            throw new WebApplicationException(Status.INTERNAL_SERVER_ERROR);
        } catch (ImageNotFoundException e) {
            LOGGER.error("ics.REST.listImage(): Image with id " + imageId
                    + " not found");
            throw new WebApplicationException(Status.NOT_FOUND);
        }

        //return image.toTextPlain();
        return image.detailedInfo();
    }

    /**
     * Lists an information of a given base image ID
     * 
     * @param imageId   the image ID
     * @return an information of a single base image
     */
    @GET
    @Produces("text/plain")
    @Path("/baseimage/{id}")
    public String listBaseImage(@PathParam("id") String imageId) {
        LOGGER.debug("ics.REST.listBaseImage(): Listing image with id '"
                + imageId + "'");
        Image image;
        try {
            image = ics.getBaseImage(imageId.trim());
        } catch (IOException ioException) {
            LOGGER.error("ics.REST.listBaseImage(): IOException looking for image", ioException);
            throw new WebApplicationException(Status.INTERNAL_SERVER_ERROR);
        } catch (ImageNotFoundException e) {
            LOGGER.error("ics.REST.listBaseImage(): Image with id " + imageId
                    + " not found");
            throw new WebApplicationException(Status.NOT_FOUND);
        }

        //return image.toTextPlain();
        return image.detailedInfo();
    }

    /**
     * Creates a new image according to the below image type:
     * <ul>
     * <li>CoreElement</li>
     * <li>OrchestrationElement</li>
     * <li>ImageTemplate : according to the image requirement (described in the XML format)</li>
     * </ul>
     * 
     * For the image requirement, the XML format looks like below:
     * <pre>
     * <ImageTemplate>
     *     <operatingSystem>CentOS</operatingSystem>  <!-- for a wildcard usage, write: Linux -->
     *     <osVersion>5.8</osVersion>      
     *     <architecture>i386</architecture>    <!-- value only i386 or x86_64 -->
     *     <imageSize>10</imageSize>    <!-- in GB (integer only) -->
     * </ImageTemplate>
     * </pre>
     * Note that the above tags are all optional and can be omitted.
     * If this is the case, the ICS will randomly select the eligible base image.
     * 
     * @param imageType     the image type
     * @return the newly-created image ID or 
     *          an exception error if it receives an invalid image type
     */
    @POST
    @Path("/image")
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.TEXT_PLAIN)
    public String deploy(String imageType) {
        LOGGER.debug("ics.REST.deploy(): Received input: " + imageType);
        String ret = "";
        ImageType type = null;

        if (imageType.startsWith("<ImageTemplate>") == true) {
            //LOGGER.info("ics.REST.deploy(): Received a service manifest");
            return createImageFromRequirement(imageType);
        } else if (imageType.equals(ImageType.CoreElement.toString())) {
            LOGGER.info("ics.REST.deploy(): Creating core image");
            type = ImageType.CoreElement;
        } else if (imageType.equals(ImageType.OrchestrationElement.toString())) {
            LOGGER.info("ics.REST.deploy(): Creating orchestration image");
            type = ImageType.OrchestrationElement;
        } else {
            LOGGER.error("ics.REST.deploy(): Unknown image type '" + imageType
                    + "', throwing an error");
            throw new WebApplicationException(Status.BAD_REQUEST);
        }

        try {
            ret = ics.createImage(type).getUuid().toString();
        } catch (Exception e) {
            throw new WebApplicationException(e, Status.INTERNAL_SERVER_ERROR);
        }
        LOGGER.info("ics.REST.deploy(): Created image with id " + ret);

        return ret;
    }

    /**
     * Creates a new image based on the given input image requirement
     * @param input     image requirement (described in the XML format)
     * @return the newly-created image ID or 
     *          an exception error if it receives an invalid image type
     */
    private String createImageFromRequirement(String input) {
        String str = null;
        try {
            str = ics.createImage(input);
        } catch (Exception e) {
            LOGGER.debug("ics.REST.createImageFromRequirement(): image is null due to bad request");
            throw new WebApplicationException(Status.BAD_REQUEST);
        }

        LOGGER.info("ics.REST.createImageFromRequirement(): " + str);
        return str;
    }

    /**
     * Creates a new image from the given XML file.
     * For the image requirement, the XML format looks like below:
     * <pre>
     * <ImageTemplate>
     *     <operatingSystem>CentOS</operatingSystem>  <!-- for a wildcard usage, write: Linux -->
     *     <osVersion>5.8</osVersion>      
     *     <architecture>i386</architecture>    <!-- value only i386 or x86_64 -->
     *     <imageSize>10</imageSize>    <!-- in GB (integer only) -->
     * </ImageTemplate>
     * </pre>
     * Note that the above tags are all optional and can be omitted.
     * If this is the case, the ICS will randomly select the eligible base image.
     * 
     * @param inputStream
     * @param name
     * @return
     */
    @POST
    @Path("/request")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.TEXT_PLAIN)
    public String deployWithInputRequirement(
            @FormDataParam("file") InputStream inputStream,
            @FormDataParam("name") String name) {

        //LOGGER.debug("ics.REST.deployWithInputRequirement(): Received XML input");
        //LOGGER.debug("ics.REST.deployWithInputRequirement(): Receiving multipart file upload for image " + imageId);
        LOGGER.debug("ics.REST.deployWithInputRequirement(): Received XML input: "
                + name);

        File xmlFile = null;
        String scDirectory = null;
        try {
            String uuid = UUID.randomUUID().toString();
            scDirectory = "/tmp/" + uuid + "_";
            xmlFile = new File(scDirectory + name);

            // Write the input stream to the given file
            FileOutputStream fos = new FileOutputStream(xmlFile);
            int read = 0;
            byte[] bytes = new byte[1024];
            while ((read = inputStream.read(bytes)) != -1) {
                fos.write(bytes, 0, read);
            }
            inputStream.close();
            fos.close();
            LOGGER.debug("ics.REST.deployWithInputRequirement(): Written file to "
                    + xmlFile.getAbsolutePath());
        } catch (IOException ioException) {
            throw new WebApplicationException(ioException, Status.INTERNAL_SERVER_ERROR);
        }

        String str = null;
        try {
            str = ics.createImage(xmlFile);
        } catch (Exception e) {
            LOGGER.debug("ics.REST.deployWithInputRequirement(): image is null");
            throw new WebApplicationException(e, Status.INTERNAL_SERVER_ERROR);
        }

        xmlFile.delete();  // remove this temp file afterwards
        LOGGER.info("ics.REST.deployWithInputRequirement(): " + str);
        return str;
    }

    /**
     * Uploads the given file into the selected image ID 
     * @param imageId       image ID
     * @param inputStream   InputStream containing the file's content
     * @param name          filename
     */
    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Path("/image/{id}/file")
    public void putFile(@PathParam("id") String imageId,
            @FormDataParam("file") InputStream inputStream,
            @FormDataParam("name") String name) {
        LOGGER.debug("ics.REST.putFile(): Receiving multipart file upload for image "
                + imageId);
        LOGGER.debug("ics.REST.putFile(): String name: " + name);
        // Save the uploaded file to a temporary file
        boolean bo = saveFileToSourceDirectory(imageId, inputStream, name, false, false);
        if (bo == false) {
            LOGGER.error("ics.REST.putFile(): Image " + imageId
                    + " could be stored under " + sourceDirectory);
            return;
        }
    }

    /**
     * Uploads the given war file into the selected image ID 
     * @param imageId       image ID
     * @param inputStream   InputStream containing the file's content
     * @param name          the war filename
     */
    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Path("/image/{id}/war")
    public void putWarFile(@PathParam("id") String imageId,
            @FormDataParam("file") InputStream inputStream,
            @FormDataParam("name") String name) {
        LOGGER.debug("ics.REST.putWarFile(): Receiving multipart file upload for image "
                + imageId);
        LOGGER.debug("ics.REST.putWarFile(): String file name: " + name);

        try {
            Image image = ics.getImage(imageId.trim());
            webappDirectory = image.getWebappsDirectoryName();
            LOGGER.debug("ics.REST.putWarFile(): webappDirectory = "
                    + webappDirectory);
        } catch (IOException ioException) {
            LOGGER.error("ics.REST.putWarFile(): IOException looking for image", ioException);
            throw new WebApplicationException(Status.INTERNAL_SERVER_ERROR);
        } catch (ImageNotFoundException e) {
            LOGGER.error("ics.REST.putWarFile(): Image with id " + imageId
                    + " not found");
            throw new WebApplicationException(Status.NOT_FOUND);
        }

        // Save uploaded content to a directory
        boolean bo = saveFileToSourceDirectory(imageId, inputStream, name, true, false);
        if (bo == false) {
            LOGGER.error("ics.REST.putWarFile(): Image " + imageId
                    + " could be stored under " + sourceDirectory);
            return;
        }
    }

    /**
     * Uploads the given zip file into the selected image ID 
     * @param imageId       image ID
     * @param inputStream   InputStream containing the file's content
     * @param name          a zip filename
     */
    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Path("/image/{id}/zip")
    public void putZipFile(@PathParam("id") String imageId,
            @FormDataParam("file") InputStream inputStream,
            @FormDataParam("name") String name) {
        LOGGER.debug("ics.REST.putZipFile(): Receiving multipart file upload for image "
                + imageId);
        LOGGER.debug("ics.REST.putZipFile(): String file name: " + name);

        // Save uploaded content to a temporary file
        //File tempFile = saveFileToTemporaryDirectory(inputStream, name);
        boolean bo = saveFileToSourceDirectory(imageId, inputStream, name, false, true);
        if (bo == false) {
            LOGGER.error("ics.REST.putZipFile(): Image " + imageId
                    + " could be stored under " + sourceDirectory);
            return;
        }
    }

    /**
     * Saves the given input stream to a file in a given directory.
     * 
     * @param inputStream   InputStream containing the file's content
     * @param name          input filename
     * @return <tt>true</tt> if successful, <tt>false</tt> otherwise.
     */
    private boolean saveFileToSourceDirectory(String imageId,
            InputStream inputStream, String name, boolean war, boolean zip) {
        File tempFile = null;
        File scDirectory = null;
        try {
            String completeSourceDirectory;
            if (war)
                completeSourceDirectory = sourceDirectory + "/" + imageId + "/"
                        + webappDirectory;
            else
                completeSourceDirectory = sourceDirectory + "/" + imageId + "/"
                        + targetDirectory;
            scDirectory = FileUtils.createDirectoryIfNotExists(completeSourceDirectory);
            tempFile = new File(scDirectory, name);

            // Write the input stream to the given file
            FileOutputStream fos = new FileOutputStream(tempFile);
            int read = 0;
            byte[] bytes = new byte[1024];
            while ((read = inputStream.read(bytes)) != -1) {
                fos.write(bytes, 0, read);
            }
            inputStream.close();
            fos.close();
            LOGGER.debug("ics.REST.saveFileToSourceDirectory(): Written file to "
                    + tempFile.getAbsolutePath());
        } catch (IOException ioException) {
            throw new WebApplicationException(ioException, Status.INTERNAL_SERVER_ERROR);
        }
        if (zip) {
            try {
                unzipFile(tempFile, tempFile.getParent());
                tempFile.delete();
            } catch (IOException io) {
                LOGGER.debug("ics.REST.saveFileToSourceDirectory(): failed by unzipping file");
                return false;
            }
        }
        // Return the temporary file that now contains the input stream
        return true;
    }

    /**
     * Sets the permission of a given file located on the image.
     * If it is a directory, it will change the permission recursively.
     * @param imageId       the image ID
     * @param filename      a filename. It can also be a directory name.
     * @param permissions   a permission mode, e.g. "644" or "600" 
     */
    @PUT
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Path("/image/{id}/permissions")
    public void setPermissions(@PathParam("id") String imageId,
            @FormDataParam("file") String filename,
            @FormDataParam("permissions") String permissions) {
        LOGGER.debug("ics.REST.setPermissions(): Receiving setPermissions for image "
                + imageId);
        LOGGER.debug("ics.REST.setPermissions(): File to change: " + filename);
        LOGGER.debug("ics.REST.setPermissions(): Permissions to set: "
                + permissions);

        try {
            ImageCreationService.getInstance().setPermissions(imageId, filename, permissions);
        } catch (IOException ioException) {
            LOGGER.error("ics.REST.setPermissions(): I/O exception putting file to image "
                    + imageId, ioException);
            throw new WebApplicationException(ioException, Response.Status.INTERNAL_SERVER_ERROR);
        } catch (ImageNotFoundException e) {
            LOGGER.info("ics.REST.setPermissions(): No image with id "
                    + imageId + " could be found");
            throw new WebApplicationException(Status.NOT_FOUND);
        } catch (UnsupportedStateException e) {
            LOGGER.error("ics.REST.setPermissions(): Wrong state for setting permissions on image", e);
            throw new WebApplicationException(Status.FORBIDDEN);
        }
    }

    /**
     * Finalizes the image with the given id.
     * 
     * @param imageId image ID
     * @return the URL of the image or an exception error message if image does not exist
     */
    @POST
    @Path("/image/{id}/finalize")
    public String finalize(@PathParam("id") String imageId) {
        try {
            ImageCreationService.getInstance().putFileDirectory(imageId, sourceDirectory);
        } catch (IOException ioException) {
            LOGGER.error("ics.REST.finalize(): I/O exception putting file to image "
                    + imageId + " -- " + ioException.getMessage());
            throw new WebApplicationException(ioException, Response.Status.INTERNAL_SERVER_ERROR);
        } catch (ImageNotFoundException e) {
            LOGGER.info("ics.REST.finalize(): No image with id " + imageId
                    + " could be found");
            throw new WebApplicationException(Status.NOT_FOUND);
        } catch (UnsupportedStateException e) {
            LOGGER.error("ics.REST.finalize(): Wrong state for uploading file to image", e);
            throw new WebApplicationException(Status.FORBIDDEN);
        }

        LOGGER.debug("ics.REST.finalize(): Finalizing image " + imageId);
        try {
            Image finalizedImage = ics.finalizeImage(imageId);
            return finalizedImage.getUrl() + '\n';
        } catch (IOException ioException) {
            LOGGER.error("ics.REST.finalize(): I/O exception finalizing image "
                    + imageId + " -- " + ioException.getMessage());
            throw new WebApplicationException(ioException, Response.Status.INTERNAL_SERVER_ERROR);
        } catch (ImageNotFoundException noImageException) {
            LOGGER.info("ics.REST.finalize(): No image with id " + imageId
                    + " could be found");
            throw new WebApplicationException(Status.NOT_FOUND);
        } catch (StateChangeException e) {
            LOGGER.error("ics.REST.finalize(): Error changing state: "
                    + e.getMessage());
            throw new WebApplicationException(Status.FORBIDDEN);
        }
    }

    /**
     * Deletes an image with the given ID
     * @param imageId  the image ID
     */
    @DELETE
    @Path("/image/{id}")
    public void deleteImage(@PathParam("id") String imageId) {
        LOGGER.debug("ics.REST.deleteImage(): Deleting image with id "
                + imageId);
        try {
            // Call the ImageCreationService's deleteImage() method
            String completeSourceDirectory = sourceDirectory + "/" + imageId;
            ics.deleteImage(imageId, completeSourceDirectory);

            LOGGER.debug("ics.REST.deleteImage(): Image " + imageId
                    + " deleted successfuly");
        } catch (IOException ioException) {
            LOGGER.error("ics.REST.deleteImage(): I/O exception deleting image", ioException);
            throw new WebApplicationException(ioException, Response.Status.INTERNAL_SERVER_ERROR);
        } catch (ImageNotFoundException noImageException) {
            LOGGER.debug("ics.REST.deleteImage(): Image with id " + imageId
                    + " not found");
            throw new WebApplicationException(Status.NOT_FOUND);
        }
    }

    /**
     * Deletes all the images from the database and in the physical hard disk.
     */
    @DELETE
    @Path("/image/flush")
    public void flushImages() {
        LOGGER.debug("ics.REST.flushImages(): Flusing ImageCreationService");
        ics.flushDatabase();
    }

    /**
     * Gets the status of an image
     * @param imageId   the image ID
     * @return the image status or an exception message if the image is not found
     */
    @GET
    @Path("/image/{id}/status")
    public String getImageStatus(@PathParam("id") String imageId) {
        LOGGER.debug("ics.REST.getImageStatus(): Returning status for image with id '"
                + imageId + "'");
        try {
            Image image = ics.getImage(imageId.trim());
            return image.getState().toString();
        } catch (IOException ioException) {
            LOGGER.error("ics.REST.getImageStatus(): IOException looking for image", ioException);
            throw new WebApplicationException(Status.INTERNAL_SERVER_ERROR);
        } catch (ImageNotFoundException e) {
            LOGGER.error("ics.REST.getImageStatus(): Image with id " + imageId
                    + " not found");
            throw new WebApplicationException(Status.NOT_FOUND);
        }
    }

    /**
     * Changes the image status from FINALIZED to READY
     * @param imageId   the image ID
     */
    @POST
    @Path("/image/{id}/unfinalize")
    public void unfinalize(@PathParam("id") String imageId) {
        LOGGER.debug("ics.REST.unfinalize(): Unfinalizing image with id '"
                + imageId + "'");
        try {
            // Call the ImageCreationService's unfinalizeImage() method
            ics.unfinalizeImage(imageId);
        } catch (IOException ioException) {
            LOGGER.error("ics.REST.unfinalize(): I/O exception accessing image '"
                    + imageId + "'");
            throw new WebApplicationException(ioException, Response.Status.INTERNAL_SERVER_ERROR);
        } catch (ImageNotFoundException noImageException) {
            LOGGER.error("ics.REST.unfinalize(): Image '" + imageId
                    + "' not found");
            throw new WebApplicationException(Status.NOT_FOUND);
        } catch (StateChangeException e) {
            LOGGER.error("ics.REST.unfinalize(): LOGGER: " + e.getMessage());
            throw new WebApplicationException(Status.FORBIDDEN);
        }
    }

    /**
     * Uncompresses the given zip file. Unfortunately, the files' permission is not
     * preserved, especially with regards to an executable file.
     * @param zipFile       the zip file
     * @param destination   the directory location
     * @throws IOException  IO Exception
     */
    private void unzipFile(File zipFile, String destination) throws IOException {
        LOGGER.debug("ics.REST.unzipFile(): Unzipping " + zipFile
                + " to directory " + destination);
        //LOGGER.debug("ics.REST.unzipFile(): Opening input streams");
        FileInputStream fis = new FileInputStream(zipFile);
        ZipInputStream zin = new ZipInputStream(new BufferedInputStream(fis));
        ZipEntry entry;
        File destinationDirectory = new File(destination);

        while ((entry = zin.getNextEntry()) != null) {
            //LOGGER.debug("ics.REST.unzipFile(): Extracting: " + entry);

            if (entry.isDirectory()) {
                //LOGGER.debug("ics.REST.unzipFile(): Directory found, will be created");
                File targetDirectory = new File(destinationDirectory, entry.getName());
                targetDirectory.mkdir();
            } else {
                // extract data
                // open output streams
                int BUFFER = 2048;

                File destinationFile = new File(destinationDirectory, entry.getName());
                destinationFile.getParentFile().mkdirs();

                //LOGGER.debug("ics.REST.unzipFile(): Creating parent file of destination: "
                //        + destinationFile.getParent());
                //boolean parentDirectoriesCreated = destinationFile.getParentFile().mkdirs();                
                //LOGGER.debug("ics.REST.unzipFile(): Result of creating parents: "
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

        LOGGER.debug("ics.REST.unzipFile(): Unzipping file is done");
        zin.close();
        fis.close();
    }

    /*******
    public static void main(String[] args) throws FileNotFoundException {
        FileInputStream is = new FileInputStream(new File("C:\\tmp\\ConvertPDFtoText.zip"));
        ImageCreationServiceREST rest = new ImageCreationServiceREST();
        rest.putZipFile("001", is, "THtest.zip");
    }
    ********/

}
