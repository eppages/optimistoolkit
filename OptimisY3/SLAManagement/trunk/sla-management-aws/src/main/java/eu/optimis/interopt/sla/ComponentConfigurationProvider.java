/* 
 * Copyright (c) 2007, Fraunhofer-Gesellschaft
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 * 
 * (1) Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the disclaimer at the end.
 *     Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in
 *     the documentation and/or other materials provided with the
 *     distribution.
 * 
 * (2) Neither the name of Fraunhofer nor the names of its
 *     contributors may be used to endorse or promote products derived
 *     from this software without specific prior written permission.
 * 
 * DISCLAIMER
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *  
 */
package eu.optimis.interopt.sla;

import java.io.InputStream;
import java.util.Properties;
import org.apache.log4j.Logger;

/**
 * Helper class that reads the component configuration files. Files can be changed at runtime and changes will
 * directly affect runtime configuration.
 * 
 * @author hrasheed
 * 
 */
public class ComponentConfigurationProvider
{

    private static Logger log = Logger.getLogger(ComponentConfigurationProvider.class);

    private static final String BUNDLE_NAME = "/component-connection.properties"; //$NON-NLS-1$

    private ComponentConfigurationProvider()
    {
    }

    /**
     * Reads the value for the given key from the component configuration file. If there is a system property
     * with the same key, the system property will override the value from the configuration file.
     * 
     * @param key
     *            key of the configuration property
     * 
     * @return the configured value
     */
    public static String getString(String key)
    {
        try
        {
            Properties bundle = new Properties();
            InputStream in = ComponentConfigurationProvider.class.getResource(BUNDLE_NAME).openStream();
            bundle.load(in);

            // if there is a system property with the same key, the system property will be used instead of
            // the value from the configuration file.
            String defaultValue = bundle.getProperty(key);
            return System.getProperty(key, defaultValue);
        }
        catch (Exception e)
        {
            log.error("failed to retrive property !" + key + "!");
            throw new RuntimeException(e);
        }
    }

    public static boolean getBoolean(String key, boolean defaultValue)
    {
        try
        {
            Properties bundle = new Properties();
            InputStream in = ComponentConfigurationProvider.class.getResource(BUNDLE_NAME).openStream();
            bundle.load(in);

            return Boolean.parseBoolean(bundle.getProperty(key, Boolean.valueOf(defaultValue).toString()));
        }
        catch (Exception e)
        {
            log.error("failed to retrive property !" + key + "!");
            return defaultValue;
        }
    }
}