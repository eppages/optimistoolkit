/* $Id: Constants.java 11131 2013-01-24 15:20:20Z sulistio $ */

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
package eu.optimis.ics.core;

public final class Constants {

    /** Name of the resource bundle that holds the service's configuration. */
    public static final String BUNDLE_NAME = "ics";

    /** Property in the resource bundle holding the database file name. */
    public static final String DATABASE_FILENAME_PROPERTY = "database.filename";

    /** Property in the resource bundle holding the source file directory */
    public static final String SOURCE_DIRECTORY_PROPERTY = "source.directory";

    /** Property in the resource bundle holding the target directory. */
    public final static String TARGET_DIRECTORY_PROPERTY = "target.directory";

    /** Property in the resource bundle holding the base image directory. */
    public final static String IMAGE_DIRECTORY_PROPERTY = "image.directory";

    /////////////////////////////
    // NOTE: These constants are redundant because in the Y3 version, a CSV file
    // for describing base images (including the webapps directory) are used.
    /** Property in the resource bundle holding the name of the orchestration element base image. */
    public final static String ORCHESTRATION_IMAGE_NAME_PROPERTY = "image.name.orchestration";

    /** Property in the resource bundle holding the name of the core element base image. */
    public final static String CORE_IMAGE_NAME_PROPERTY = "image.name.core";

    /** Property containing the webapps base directory. */
    public static final String BASE_WEBAPPS_DIRECTORY_PROPERTY = "image.webapps.base";
    /////////////////////////////

    /** Property in the resource bundle holding the base URL where to download images. */
    public static final String BASE_URL_PROPERTY = "image.base.url";

    /** Property in the resource bundle holding the list of image templates. */
    public static final String IMAGE_TEMPLATE_LIST_PROPERTY = "image.template.list";

    /** Property in the resource bundle holding the base image database file name. */
    public static final String DATABASE_BASEIMAGE_FILENAME_PROPERTY = "database.baseimage.filename";

    /** Max number of columns or fields used in the image template csv file. */
    public static final int MAX_COLUMN = 7;

    /** The default location of the OPTIMIS directory */
    public static final String OPTIMIS_HOME_DEFAULT = "/opt/optimis";

    /** Location of the Image Creation Service's property file (inside the OPTIMIS_HOME_DEFAULT directory) */
    public static final String ICS_CONFIG_FILE = "/etc/ImageCreationService/ics.properties";

    /** Location of the Image Creation Service's logging file (inside the OPTIMIS_HOME_DEFAULT directory) */
    public static final String LOG4J_CONFIG_FILE = "/etc/ImageCreationService/log4j.properties";
}
