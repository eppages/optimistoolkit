/*
 Copyright (C) 2012-2013 Umeå University

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/
package eu.optimis.sd.util.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;

import org.apache.log4j.Logger;

/**
 * Configuration wrapper. Each Configuration Object is immutable and should be
 * obtained through the {@link ConfigurationFactory}.
 * 
 * @author Daniel Henriksson (<a
 *         href="mailto:danielh@cs.umu.se">danielh@cs.umu.se</a>)
 * 
 */
public class Configuration {

    private static final Logger log = Logger.getLogger(Configuration.class.getName());
    private Properties properties;
    private long lastModified;

    /**
     * Creates a new Configuration using the file specified. If the file cannot
     * be found on disk, the class path is used to locate the file.
     * 
     * @param configFile
     *            The name of the configuration file
     * @throws FileNotFoundException
     *             If the file can neither be found on disk or on the class path
     */
    protected Configuration(File configFile) throws FileNotFoundException {
        loadProperties(configFile);        
        lastModified = configFile.lastModified();
    }
    
    /**
     * The last modified timestamp of this configuration object
     */
    public long getLastModified() {
        return lastModified;
    }

    /**
     * Load properties from file
     * 
     * @param file
     *            The config file
     */
    private synchronized void loadProperties(File file) {
        properties = new Properties();
        try {
            properties.load(new FileInputStream(file));
            log.info("Config file loaded: " + file.getAbsolutePath());
        } catch (IOException e) {
            throw new IllegalStateException("File could not be loaded! (File: "
                    + file.getAbsolutePath() + ')');
        }
    }



    /**
     * Reads a String value without any default value
     * 
     * @param propertyName
     *            The name of the property
     */
    public String getString(String propertyName) {
        return getString(propertyName, null);
    }

    /**
     * Reads a String value and falling back on the default value if not found
     * or if malformed
     * 
     * @param propertyName
     *            The name of the property
     * @param defaultValue
     *            The default value to use
     */
    public synchronized String getString(String propertyName, String defaultValue) {
        String result = properties.getProperty(propertyName);

        if (result == null) {
            result = defaultValue;
        }

        return result;
    }

    /**
     * Reads a Double value without any default value
     * 
     * @param propertyName
     *            The name of the property
     */
    public Double getDouble(String propertyName) {
        return getDouble(propertyName, null);
    }

    /**
     * Reads a Double value and falling back on the default value if not found
     * or if malformed
     * 
     * @param propertyName
     *            The name of the property
     * @param defaultValue
     *            The default value to use
     */
    public synchronized Double getDouble(String propertyName, Double defaultValue) {
        Double result;

        String doubleStr = properties.getProperty(propertyName);

        if (doubleStr != null) {
            try {
                result = Double.valueOf(doubleStr);
            } catch (NumberFormatException e) {
                log.info("Illegal format of double: '" + doubleStr + "' for property: '" + propertyName
                        + "'. Using default value");
                result = defaultValue;
            }
        } else {
            log.info("Property not found: '" + propertyName + "'. Using default value");
            result = defaultValue;
        }

        return result;
    }

    /**
     * Reads a Double value without any default value
     * 
     * @param propertyName
     *            The name of the property
     */
    public URL getURL(String propertyName) {
        return getURL(propertyName, null);
    }

    /**
     * Reads a URL value and falling back on the default value if not found or
     * if malformed
     * 
     * @param propertyName
     *            The name of the property
     * @param defaultValue
     *            The default value to use
     */
    public synchronized URL getURL(String propertyName, URL defaultValue) {
        URL result;

        String urlStr = properties.getProperty(propertyName);

        if (urlStr != null) {
            try {
                result = new URL(urlStr);
            } catch (MalformedURLException e) {
                log.info("Illegal format of URL: '" + urlStr + "' for property: '" + propertyName
                        + "'. Using default value");
                result = defaultValue;
            }
        } else {
            log.info("Property not found: '" + propertyName + "'. Using default value");
            result = defaultValue;
        }

        return result;
    }

    /**
     * Reads a Integer value without any default value
     * 
     * @param propertyName
     *            The name of the property
     */
    public Integer getInteger(String propertyName) {
        return getInteger(propertyName, null);
    }

    /**
     * Reads a Integer value and falling back on the default value if not found
     * or if malformed
     * 
     * @param propertyName
     *            The name of the property
     * @param defaultValue
     *            The default value to use
     */
    public synchronized Integer getInteger(String propertyName, Integer defaultValue) {
        Integer result;

        String intStr = properties.getProperty(propertyName);

        if (intStr != null) {
            try {
                result = Integer.valueOf(intStr);
            } catch (NumberFormatException e) {
                log.info("Illegal format of Integer: '" + intStr + "' for property: '" + propertyName
                        + "'. Using default value");
                result = defaultValue;
            }
        } else {
            log.info("Property not found: '" + propertyName + "'. Using default value");
            result = defaultValue;
        }

        return result;
    }

    /**
     * Reads a Long value without any default value
     * 
     * @param propertyName
     *            The name of the property
     */
    public Long getLong(String propertyName) {
        return getLong(propertyName, null);
    }

    /**
     * Reads a Long value and falling back on the default value if not found or
     * if malformed
     * 
     * @param propertyName
     *            The name of the property
     * @param defaultValue
     *            The default value to use
     */
    public synchronized Long getLong(String propertyName, Long defaultValue) {
        Long result;

        String longStr = properties.getProperty(propertyName);

        if (longStr != null) {
            try {
                result = Long.valueOf(longStr);
            } catch (NumberFormatException e) {
                log.info("Illegal format of Long: '" + longStr + "' for property: '" + propertyName
                        + "'. Using default value");
                result = defaultValue;
            }
        } else {
            log.info("Property not found: '" + propertyName + "'. Using default value");
            result = defaultValue;
        }

        return result;
    }

    /**
     * Reads a Boolean value without any default value
     * 
     * @param propertyName
     *            The name of the property
     */
    public Boolean getBoolean(String propertyName) {
        return getBoolean(propertyName, null);
    }

    /**
     * Reads a Boolean value and falling back on the default value if not found or
     * if malformed
     * 
     * @param propertyName
     *            The name of the property
     * @param defaultValue
     *            The default value to use
     */
    public synchronized Boolean getBoolean(String propertyName, Boolean defaultValue) {
        String booleanStr = properties.getProperty(propertyName);

        if (booleanStr != null) {
            try {
                return Boolean.valueOf(booleanStr);
            } catch (Exception e) {
                log.info("Illegal format of Boolean: '" + booleanStr + "' for property: '" + propertyName
                        + "'. Using default value");
                return defaultValue;
            }
        } else {
            log.info("Property not found: '" + propertyName + "'. Using default value");
            return defaultValue;
        }
    }
}
