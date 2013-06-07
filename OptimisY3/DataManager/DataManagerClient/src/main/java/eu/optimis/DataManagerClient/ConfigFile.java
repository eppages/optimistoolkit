/*
   Copyright (C) 2012 National Technical University of Athens

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */
package eu.optimis.DataManagerClient;

import java.io.*;
import java.net.*;
import java.util.*;

public class ConfigFile
{
  private static final String PROPERTY_DIR      = "/opt/optimis/etc/DataManager/";
  private static final String PROPERTY_NAME     = "datamanagerconf.properties";
  private static final String PROPERTY_FILEPATH = PROPERTY_DIR + PROPERTY_NAME;
  private static final String UPLOAD_FLAG       = PROPERTY_DIR + "STATUS_DO_NOT_UPLOAD_ACTIVE";
  private static final String PROPERTY_URL      = "http://optimis.fusion-algorithms.com/optimis/" + PROPERTY_NAME;
  private static boolean propertyFileChecked    = false;
  public  static List<InfrastructureProviderProperties> globalProviders = new ArrayList<InfrastructureProviderProperties>();

  private static void ensureConfigFileExists()
  {
   if( propertyFileChecked ) return;

    try
    {
      RestService rest = new RestService();
      String result = rest.getHTML(PROPERTY_URL);
      new File(PROPERTY_DIR).mkdirs();
      FileWriter fstream = new FileWriter(PROPERTY_FILEPATH);
      BufferedWriter out = new BufferedWriter(fstream);
      out.write(result);
      out.close();
    }
    catch (Exception e)
    {
       System.err.println("Error: " + e.getMessage());
    }


    File cFile = new File(PROPERTY_FILEPATH);

    if(!cFile.exists())
    {
       System.err.println("Error: Cannot Find Property File " + PROPERTY_FILEPATH);
       return;
    }

   propertyFileChecked = true;
  }

    private static InfrastructureProviderProperties loadProviderProperties(String infProvider)
    {
      ensureConfigFileExists();

      InfrastructureProviderProperties provider = new InfrastructureProviderProperties();

      System.out.println("DMCLIENT config file: " + PROPERTY_FILEPATH + "   called with provider =" + infProvider);

      try {
            provider.name = infProvider;
            InputStream is = new FileInputStream(PROPERTY_FILEPATH);
            Properties prop = new Properties();
            prop.load(is);


            String isOptimisEnabled = prop.getProperty(infProvider + ".isOptimis").trim();
            if( isOptimisEnabled.equals("0") )
                  provider.isOptimis = false;
            else
                  provider.isOptimis = true;

            String paramCheckExists = "false";

            if( provider.isOptimis )
             {
               provider.masterLocalIP            = prop.getProperty(infProvider + ".masterLocalIP" ).trim();
               provider.masterPublicIP           = prop.getProperty(infProvider + ".masterPublicIP").trim();
               provider.vmImagesAccountName      = prop.getProperty(infProvider + ".vmImagesAccountName").trim();
               provider.vmImagesAccountPassword  = prop.getProperty(infProvider + ".vmImagesAccountPassword").trim();
               provider.vmpath                   = prop.getProperty(infProvider + ".vmpath").trim();
               provider.repoInterface            = prop.getProperty(infProvider + ".repoInterface").trim();
               paramCheckExists                  = prop.getProperty(infProvider + ".checkExists").trim();
             }
            else
             {
               provider.masterLocalIP            = prop.getProperty("atos.masterLocalIP").trim();
               provider.masterPublicIP           = prop.getProperty("atos.masterPublicIP").trim();
               provider.vmImagesAccountName      = prop.getProperty(infProvider + ".username").trim();
               provider.vmImagesAccountPassword  = prop.getProperty(infProvider + ".password").trim();
               provider.repoInterface            = prop.getProperty(infProvider + ".repoInterface").trim();
               paramCheckExists                  = prop.getProperty(infProvider + ".checkExists").trim();
             }

            if( paramCheckExists.equals("true") || (new File(UPLOAD_FLAG).isFile()) )
              {
                 DataManagerClient.check_if_exists = true;
                 provider.checkExists = true;
              }
            else
              {
                 DataManagerClient.check_if_exists = false;
                 provider.checkExists = false;
              }

            is.close();

        } catch(Exception e)
        {
            System.out.println("DataManager: Failed to read from property file.");
            e.printStackTrace();
        }

      return provider;
    }


  public static InfrastructureProviderProperties getProviderProperties(String name) throws Exception
  {
    Iterator<InfrastructureProviderProperties> iterator = globalProviders.iterator();

    while ( iterator.hasNext() )
     {
       InfrastructureProviderProperties provider = iterator.next();
       if( provider.name.equals(name) )
          return provider;
     }

    InfrastructureProviderProperties p = loadProviderProperties(name);
    globalProviders.add(p);

    return p;
  }
}
