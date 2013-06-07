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

import org.w3c.dom.*;
import org.xml.sax.InputSource;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.*;
import java.util.Vector;
import java.net.*;
import com.jcraft.jsch.*;

/**
 *  Arsys Wrapper
 */
class ArsysDataManager implements ICloudProvider
{
  InfrastructureProviderProperties properties;
  UploadFTP uploader;
  boolean pendingFinish = false;

  public ArsysDataManager(InfrastructureProviderProperties prop)
  {
    properties = prop;
  }

  public String createUsersRepository(String serviceID, boolean isSensitive) throws Exception
  {
    return "NON_OPTIMIS"; // Not Applicable
  }

  public String checklegal(String sid, String manifestXML, String localProvider, String federatedIP)
  {
    return "ERROR"; // Not Applicable
  }

  public String getCPD() throws Exception
  {
    return "ERROR"; // Not Applicable
  }

  public String uploadVMimageRequest(String sid, String url) throws Exception
  {
    pendingFinish = false;

   if( DataManagerClient.check_if_exists )
    {
       return "NON_OPTIMIS";
    }


    try
    {
       File file = new File(url);
       System.out.println("Input url = " + url);
       if( file.exists() )
       {
         String[] filePath = url.split("/");
         String fileName   = filePath[filePath.length-1];
         RestService service = new RestService();
         String serviceUrl = "http://130.239.48.114:8080/DataManagerAPI/account/arsysCredentials";
         service.addParam("sid", sid);
         String result        = service.post(serviceUrl);
         System.out.println("Result = " + result);
         String[] accountList = result.split("#");
         String server   = accountList[0];
         String username = accountList[1];
         String password = accountList[2];
         uploader = new UploadFTP(sid, url, fileName);
         uploader.setFTPCredentials(server, username, password);
         uploader.start();
       }
       else
       {
          System.out.println("Error: Cannot find file " + url );
          return "ERROR";
       }
    }
    catch(Exception ex)
    {
     System.out.println("Error: Arsys/uploadVMimageRequest:" + ex.toString());
     ex.printStackTrace();
     return "ERROR";
    }

     return "NON_OPTIMIS";
  }

  public String checkUploadStatus(String sid, String url) throws Exception
  {
   System.out.println("checkUploadStatus: " + sid + "  url: " +url);

   if( DataManagerClient.check_if_exists )
    {
       return "success";
    }

    if( pendingFinish )
    {
     RestService service = new RestService();
     String serviceUrl = "http://130.239.48.114:8080/DataManagerAPI/account/arsysStatus";
     service.addParam("sid", sid);
     String status     = service.post(serviceUrl);

     if( status.equals("success") || status.equals("failure") )
     {
        pendingFinish  = false;
        return status;
     }
     else
       return "progress:99";
    }

    try
    {
       if( uploader != null )
       {
          String status = uploader.progress();
          if( status.equals("failure") )
            {
               uploader = null;
               return "failure";
            }

          if( status.equals("success") )
            {
              RestService service = new RestService();
              String serviceUrl = "http://130.239.48.114:8080/DataManagerAPI/account/arsysTerminate";
              service.addParam("sid", sid);
              String result     = service.post(serviceUrl);
              pendingFinish     = true;
              return "progress:99";
            }
          else
            return status;
       }

    }
    catch(Exception ex)
    {
     return "failure";
    }

    return "success";
  }

  public String startFCSJob(String sid, String numPredictions)
   {
      return "ERROR"; // not applicable
   }

  public String finishedFCSJob(String token)
   {
      return "ERROR"; //not applicable
   }

   public Vector getVMs(String sid) throws Exception
    {
      Vector<String> vms = new Vector<String>();

      return vms;
    }

    public String specifyObjective(String objfun, int trust, int eco, int cost)
    {
      return "ERROR"; // not applicable
    }

  public boolean downloadVMimage(String sid, String vmimage, String localFolder) throws Exception
    {
      return false;
    }

   public boolean deleteAccount(String sid)
    {
      return false; // not applicable
    }

}
