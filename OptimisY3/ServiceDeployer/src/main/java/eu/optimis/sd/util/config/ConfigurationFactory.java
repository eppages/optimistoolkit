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
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.logging.Logger;

import eu.optimis.sd.util.SDConfigurationKeys;
import eu.optimis.sd.util.io.FileChangeListener;
import eu.optimis.sd.util.io.FileMonitor;

/**
 * Creates configuration objects. Using a {@link FileMonitor}, the configuration
 * file is automatically watched for changes and the new setting will be
 * available on the next call.
 * 
 * Also supports {@link ConfigurationChangedListener}s to be added. The
 * listeners will be notified on any configuration change
 * 
 * @author Daniel Henriksson (<a href="mailto:danielh@cs.umu.se">danielh@cs.umu.se</a>)
 * 
 */
public class ConfigurationFactory
  implements FileChangeListener
{
  private static final Logger LOGGER = Logger.getLogger(ConfigurationFactory.class.getName());

    // Default refresh value in milliseconds
    private static final Long DEFAULT_CONFIG_REFRESHPERIOD = 30000L;

  private static ConfigurationFactory factory;

  private Long period;
  private FileMonitor fileMonitor;

  private File configFile;

  private Configuration config;


  /**
   * Creates a new ConfigurationFactory that will read and monitor the
   * specified file for updates
   * 
   * @param configFileStr
   *          The configuration file
   * @throws FileNotFoundException
   *           If the file cannot be found on disk or on the classpath
   */
  private ConfigurationFactory (String configFileStr)
    throws FileNotFoundException
  {
    
    if (configFileStr == null) {
        throw new IllegalArgumentException("No configFileStr specified.");
    }

    configFile = new File(configFileStr);

    // Check classpath if not found
        if (!configFile.exists())
    {
      URL fileUrl = getClass().getClassLoader().getResource(configFileStr);
      if (fileUrl == null)
      {
        throw new FileNotFoundException("File not found: " + configFileStr);
      }
      configFile = new File(fileUrl.getFile());
    }

    config = new Configuration(configFile);
        period = config.getLong(SDConfigurationKeys.CONFIG_REFRESHPERIOD, DEFAULT_CONFIG_REFRESHPERIOD);

    fileMonitor = new FileMonitor(configFile,period);
    fileMonitor.addFileChangeListener(this);
  }

  /**
   * Initializes the configFactory
   */
  private static ConfigurationFactory getConfigFactory (String filename)
  {
    if (factory == null)
    {
      try
      {
        factory = new ConfigurationFactory(filename);
      }
      catch (FileNotFoundException e)
      {
        throw new IllegalStateException(e);
      }
      LOGGER.info("Configuration factory initialized");
    }

    return factory;
  }

  /**
   * Provides an immutable config object that is NOT automatically updated on
   * file change.
   */
  public static Configuration getConfig (String filename)
  {
    return getConfigFactory(filename).config;
  }

  @Override
  public synchronized void fireFileChanged (File changedFile)
  {
    try
    {
      Configuration newConfig = new Configuration(configFile);
      config = newConfig;
    }
    catch (FileNotFoundException e)
    {
      LOGGER.warning("Could not read file from disk: " + configFile + ", ignoring detected update.");
      return;
    }

    long newPeriod = config.getLong(SDConfigurationKeys.CONFIG_REFRESHPERIOD);

    if (newPeriod != period)
    {
      this.period = newPeriod;
      fileMonitor.setPeriod(period);
    }
  }
}
