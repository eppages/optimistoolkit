/* $Id: CoreElement.java 11131 2013-01-24 15:20:20Z sulistio $ */

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
// import java.util.ResourceBundle;

import eu.optimis.ics.core.Constants;
import eu.optimis.ics.core.exception.OutOfDiskSpaceException;
import eu.optimis.ics.core.io.BaseImageCopier;
import org.apache.commons.configuration.PropertiesConfiguration;
import eu.optimis.ics.core.util.PropertiesReader;

public class CoreElement extends Image {

    /**
     * The base image file from which new images are cloned.
     */
    private File baseImageFile;

    public CoreElement() throws IOException, OutOfDiskSpaceException {
        super();

        // NOTE: old approach where no CSV file for describing base images exists
        //ResourceBundle rb = ResourceBundle.getBundle(Constants.BUNDLE_NAME);
        //String imageFilename = rb.getString(Constants.CORE_IMAGE_NAME_PROPERTY);

        PropertiesConfiguration config = PropertiesReader.getPropertiesConfiguration(Constants.ICS_CONFIG_FILE);
        String imageFilename = config.getString(Constants.CORE_IMAGE_NAME_PROPERTY);
        baseImageFile = new File(super.imageDirectory + "/" + imageFilename);

        cloneImage();
    }

    public CoreElement(String imageFilename) throws IOException, OutOfDiskSpaceException {
        super();

        // NOTE: old approach where no CSV file for describing base images exists
        //ResourceBundle rb = ResourceBundle.getBundle(Constants.BUNDLE_NAME);
        //String imageFilename = rb.getString(Constants.CORE_IMAGE_NAME_PROPERTY);

        //baseImageFile = new File(super.imageDirectory + "/" + imageFilename);
        baseImageFile = new File(imageFilename);  // included with dir location

        cloneImage();
    }

    public void cloneImage() throws IOException, OutOfDiskSpaceException {
        this.imageFile = new File(super.imageDirectory + "/" + this.uuid
                + Image.DEFAULT_IMAGE_FILE_EXTENSION);
        BaseImageCopier copier = new BaseImageCopier(baseImageFile, imageFile, this);
        copier.start();

    }
}
