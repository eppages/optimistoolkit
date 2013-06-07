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

class OptimisDataManager implements ICloudProvider
{
  InfrastructureProviderProperties properties;
  LocalUpload uploader;
  boolean existsFile = false;

  public OptimisDataManager(InfrastructureProviderProperties prop)
  {
    properties = prop;
  }

  public String createUsersRepository(String serviceID, boolean isSensitive) throws Exception
  {
    RestService service = new RestService();
    String url = "http://" + properties.getIP() + ":8080/DataManagerAPI/account/create/" + serviceID;
    System.out.println("createUsersRepository: " + url);
    String result = service.get(url);
    String key    = parseCreateAccountResponse(result);
    return key;
  }

  public String checklegal(String sid, String manifestXML, String localProvider, String federatedIP)
  {
     try
      {
        RestService service = new RestService();
        String url = "http://" + properties.getIP() + ":8080/DataManagerAPI/legal/checklegal";
                System.out.println(url);

        service.addParam("sid", sid);
        service.addParam("manifestXML",   manifestXML);
        service.addParam("localProvider", localProvider);
        service.addParam("federatedIP",   federatedIP);
        String result = service.post(url);
        return result;
      }
     catch(Exception ex)
      {
        return "ERROR";
      }
  }

  public String getCPD() throws Exception
    {
     try
      {
        RestService service = new RestService();
        String url = "http://" + properties.getIP() + ":8080/DataManagerAPI/risk/cloudproviderdescription/";
        String result = service.get(url);
        return result;
      }
     catch(Exception ex)
      {
        return "ERROR";
      }
    }

  public String uploadVMimageRequest(String sid, String url) throws Exception
  {
    String validSID = ServiceUtil.validServiceID(sid);
    String vmMountPath = "/opt/optimis/vmstorage/" + validSID + "/";

    System.out.println("Started uploadVMimageRequest sid = " + sid + " [" + validSID + "]");

    if( url.contains("/opt/optimis/vmstorage/") )
      return vmMountPath;

    System.out.println("check_if_exists = " + DataManagerClient.check_if_exists );

    existsFile = LocalUpload.isFileExists(url, sid, properties.getIP(),
                                                    properties.vmpath,
                                                    properties.vmImagesAccountName,
                                                    properties.vmImagesAccountPassword);

    if( DataManagerClient.check_if_exists && existsFile)
        {
           System.out.println("DataManager: DO NOT UPLOAD FILE MODE IS ACTIVATED!" );
           return vmMountPath;
        }

    // if url points to a local file
    File file = new File(url);
    if( file.exists() )
     {
        uploader = new LocalUpload(validSID, url, properties.vmImagesAccountName,
                                                  properties.vmImagesAccountPassword,
                                                  properties.getIP() );
        uploader.start();
     }
     else
     {
       RestService service = new RestService();
       String serviceUrl = "http://" + properties.getIP() + ":8080/DataManagerAPI/account/upload/";
       service.addParam("sid", sid);
       service.addParam("url", url);
       String result = service.post(serviceUrl);
     }

     return vmMountPath;
  }

  public String checkUploadStatus(String sid, String url) throws Exception
  {
    if( url.contains("/opt/optimis/vmstorage/") )
      return "success";

    if( DataManagerClient.check_if_exists && existsFile )
        {
           System.out.println("DataManager: DO NOT UPLOAD FILE MODE IS ACTIVATED!" );
           return "success";
        }

    // if url points to a local file
    File file = new File(url);
    if( file.exists() )
     {
        if( uploader != null )
            return uploader.getStatus();
        else
            return "ERROR";
     }
    else
     {
       RestService service = new RestService();
       String serviceUrl = "http://" + properties.getIP() + ":8080/DataManagerAPI/account/uploadStatus/";
       service.addParam("sid", sid);
       service.addParam("url", url);
       return service.post(serviceUrl);
     }
  }

   public Vector getVMs(String sid) throws Exception
    {
      Vector<String> vms = new Vector<String>();
      sid = ServiceUtil.validServiceID(sid);

      try
       {
         JSch jsch = new JSch();
         Session session = jsch.getSession(properties.vmImagesAccountName, properties.getIP(), 22);
         session.setConfig("StrictHostKeyChecking", "no");
         session.setPassword(properties.vmImagesAccountPassword);
         session.connect();
         Channel channel = session.openChannel( "sftp" );
         channel.connect();
         ChannelSftp sftpChannel = (ChannelSftp) channel;
         String path = "/home/vmimages/sids/" + sid + "/";
         Vector v = sftpChannel.ls(path);

         for(int i = 0; i < v.size(); ++i)
          {
            String fileName =((ChannelSftp.LsEntry)v.get(i)).getFilename();
            if( !fileName.equals(".") && !fileName.equals(".." ) )
               vms.add(fileName);
          }

         sftpChannel.exit();
         session.disconnect();
       }
       catch (JSchException e)
        {
        }
       catch (SftpException e)
        {
        }

      return vms;
    }

    public String specifyObjective(String objfun, int trust, int eco, int cost)
    {
        try
         {
          RestService service = new RestService();
          String serviceUrl = "http://" + properties.getIP() + ":8080/DataManagerAPI/trec/specifyObjective/" + objfun;
          service.addParam("trust", Integer.toString(trust));
          service.addParam("eco",   Integer.toString(eco));
          service.addParam("cost",  Integer.toString(cost));

          return service.get(serviceUrl);
         }
        catch(Exception ex)
         {
           return "ERROR";
         }
    }

  public boolean downloadVMimage(String sid, String vmimage, String localFolder) throws Exception
    {
      sid = ServiceUtil.validServiceID(sid);

      try
       {
         JSch jsch = new JSch();
         Session session = jsch.getSession(properties.vmImagesAccountName , properties.getIP(), 22);
         session.setConfig("StrictHostKeyChecking", "no");
         session.setPassword(properties.vmImagesAccountPassword);
         session.connect();
         Channel channel = session.openChannel( "sftp" );
         channel.connect();
         ChannelSftp sftpChannel = (ChannelSftp) channel;
         String vmPath = "/home/vmimages/sids/" + sid.toLowerCase() + "/" + vmimage;
         sftpChannel.get(vmPath, localFolder );
         sftpChannel.exit();
         session.disconnect();
       }
       catch (JSchException e)
        {
            return false;
        }
       catch (SftpException e)
        {
            return false;
        }

      return true;
    }

   public boolean deleteAccount(String sid)
    {
       try
        {
          RestService service = new RestService();
          String serviceUrl = "http://" + properties.getIP() + ":8080/DataManagerAPI/account/destroy/" + sid;
          String result = service.get(serviceUrl);
          return true;
        }
        catch(Exception ex)
        {
          System.out.println("Error");
        }

        return false;
    }

  public String startFCSJob(String sid, String numPredictions)
   {
        try
        {
          RestService service = new RestService();
          String serviceUrl = "http://" + properties.getIP() + ":8080/DataManagerAPI/FCS/startFCSJob";
          System.out.println(serviceUrl);
          service.addParam("sid", sid);
          service.addParam("numPredictions", numPredictions);
          String result = service.get(serviceUrl);
          return result;
        }
        catch(Exception ex)
        {
          System.out.println("Error");
          return "ERROR";
        }
   }

  public String finishedFCSJob(String token)
   {
        try
        {
          RestService service = new RestService();
          String serviceUrl = "http://" + properties.getIP() + ":8080/DataManagerAPI/FCS/finishedFCSJob";
          service.addParam("jobToken", token);
          String result = service.get(serviceUrl);
          return result;
        }
        catch(Exception ex)
        {
          System.out.println("Error");
          return "ERROR";
        }
   }

  private String parseCreateAccountResponse(String xml) throws Exception
   {
      DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
      DocumentBuilder db = dbf.newDocumentBuilder();
      InputSource is = new InputSource();
      is.setCharacterStream(new StringReader(xml));
      Document doc = db.parse(is);

      //<xml><key>XXX</key></xml>
      NodeList nodes = doc.getElementsByTagName("key");
      if (nodes.getLength() != 1)
            throw new Exception("Failed to get key object. Nodelist for key object is of the wrong length, should be 1.");
      else
        {
             Element keyNode = (Element) nodes.item(0);
             String key = keyNode.getFirstChild().getNodeValue();
             return key;
        }
   }

}
