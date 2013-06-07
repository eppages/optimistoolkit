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
import java.util.*;

public class DataManagerClient
{
  public static boolean check_if_exists = false;
  public static final String TREC_OBJECTIVE_FUNCTION_TRUST = "TRUST";
  public static final String TREC_OBJECTIVE_FUNCTION_ECO   = "ECO";
  public static final String TREC_OBJECTIVE_FUNCTION_COST  = "COST";
  public static final String ERROR_RESULT                  = "ERROR";
  public static final String DM_VERSION                    = "DATAMANAGER: VERSION 0.53-SNAPSHOT Date:30-May-2013 R1";
  private ICloudProvider keepProvider;

  public DataManagerClient()
    {
    }

   public static ICloudProvider getProvider(String name) throws Exception
   {
      InfrastructureProviderProperties p = ConfigFile.getProviderProperties(name);
      String type = p.repoInterface;

      if( type.equals("generic-optimis") )
           return new OptimisDataManager(p);
      else if( type.equals("arsys-soap") )
           return new ArsysDataManager(p);
      else if( type.equals("broker") )
           return new OptimisDataManager(p);
      else if( type.equals("amazon") )
           return new AmazonDataManager(p);
      else if( type.equals("openstack") )
           return new OpenStackDataManager(p);
      else
           System.out.println("Error: Uknown DataManager Driver");

      return null;
   }

  public String createUsersRepository(String nameProvider, String serviceID, boolean isSensitive) throws Exception
    {
     System.out.println("########################################################");
     System.out.println(DM_VERSION);
     System.out.println("########################################################");

     try
      {
        ICloudProvider manager = getProvider(nameProvider);
        String key = manager.createUsersRepository(serviceID, isSensitive);
        return key;
      }
      catch(Exception ex)
      {
         return ERROR_RESULT;
      }
    }

  // Get Cloud Provider Description
  public String getCPD(String nameProvider) throws Exception
    {
     try
      {
        ICloudProvider manager = getProvider(nameProvider);
        String cpd = manager.getCPD();
        return cpd;
      }
      catch(Exception ex)
      {
         return ERROR_RESULT;
      }
    }

    /*
     * Send an Upload Request to DM Upload Manager to upload a virtual machine image to the sid's vm images storage
     * @param nameProvider    One of the global IP provider's name
     * @param sid             Service ID
     * @param url             url of the image
     *
     * @return          a local path which can be found in the mounting point
     */
   public String uploadVMimageRequest(String nameProvider, String sid, String url) throws Exception
    {
     try
      {
        keepProvider = getProvider(nameProvider);
        String result = keepProvider.uploadVMimageRequest(sid, url);
        return result;
      }
      catch(Exception ex)
      {
         return ERROR_RESULT;
      }
    }

    /*
     * Check Upload Status
     */
   public String checkUploadStatus(String nameProvider, String sid, String url) throws Exception
    {
     try
      {
        if( keepProvider == null ) return ERROR_RESULT;

        String result = keepProvider.checkUploadStatus(sid, url);
        return result;
      }
      catch(Exception ex)
      {
         return ERROR_RESULT;
      }
    }


   /* Returns the list of all VMs stored for a specific sid
    * @param nameProvider    One of the global IP provider's name
    * @param  sid Service ID
    *
    * @return  List of VM image names
    */
   public Vector<String> getVMs(String nameProvider, String sid) throws Exception
    {
     Vector<String> vms = new Vector<String>();

     try
      {
        ICloudProvider manager = getProvider(nameProvider);
        vms = manager.getVMs(sid);
      }
      catch(Exception ex)
      {
        System.out.println(ex.toString());
      }
      return vms;
    }

    /*
     * Download a sevice's VM image from a specified IP provider
     * @param nameProvider    One of the global IP provider's name
     * @param sid             Service ID
     * @param vmimage         Virtual Machine Image fileName.  ex. ubuntu-vm-11.0.img
     * @param localfolder     The local folder where the image will be downloaded
     *
     * @return          <code>true</code> if the download was successfull
     *                  <code>false</code> otherwise.
     */
   public boolean downloadVMimage(String nameProvider, String sid, String vmimage, String localFolder) throws Exception
    {
     try
      {
        ICloudProvider manager = getProvider(nameProvider);
        boolean result = manager.downloadVMimage(sid, vmimage, localFolder);
        return result;
      }
      catch(Exception ex)
      {
        return false;
      }
    }

   // Delete Storage Account
   public boolean deleteAccount(String nameProvider, String sid)
    {
     try
      {
        ICloudProvider manager = getProvider(nameProvider);
        boolean result = manager.deleteAccount(sid);
        return result;
      }
      catch(Exception ex)
      {
         return false;
      }
    }

    // Set TREC objective function
    public String specifyObjective(String nameProvider, String objfun, int trust, int eco, int cost)
    {
     try
      {
        ICloudProvider manager = getProvider(nameProvider);
        String result = manager.specifyObjective(objfun, trust, eco, cost);
        return result;
      }
      catch(Exception ex)
      {
         return ERROR_RESULT;
      }
    }

    /// Check if federation is legal
    public String checklegal(String sid, String manifestXML, String localProvider, String federatedIP)
    {
     try
      {
        ICloudProvider manager = getProvider(localProvider);
        String result = manager.checklegal(sid, manifestXML, localProvider, federatedIP);
        return result;
      }
      catch(Exception ex)
      {
         return ERROR_RESULT;
      }
    }

  public String startFCSJob(String provider, String sid, String numPredictions)
    {
     try
      {
        ICloudProvider manager = getProvider(provider);
        String result = manager.startFCSJob(sid, numPredictions);
        return result;
      }
      catch(Exception ex)
      {
         return ERROR_RESULT;
      }
    }

  public String finishedFCSJob(String provider, String token)
    {
     try
      {
        ICloudProvider manager = getProvider(provider);
        String result = manager.finishedFCSJob(token);
        return result;
      }
      catch(Exception ex)
      {
         return ERROR_RESULT;
      }
    }

  public static void main(String[] args)
  {
    Tests.main(args);
  }
}

