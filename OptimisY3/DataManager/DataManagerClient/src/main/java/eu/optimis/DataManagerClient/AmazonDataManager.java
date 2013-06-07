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
import java.util.List;
import java.net.*;
import eu.optimis.dm.dmclient_s3wrapper.*;

class AmazonDataManager extends OptimisDataManager
{
  S3Wrapper s3;

  public AmazonDataManager(InfrastructureProviderProperties prop)
  {
    super(prop);
  }

  @Override
  public String uploadVMimageRequest(String sid, String url) throws Exception
  {
    String AWS_PUBLIC_KEY = properties.vmImagesAccountName;
    String AWS_SECRET_KEY = properties.vmImagesAccountPassword;

    s3 = new S3Wrapper(AWS_PUBLIC_KEY, AWS_SECRET_KEY);
    URL urlPath = s3.uploadImage(sid, url);
    return urlPath.toString();
  }

  @Override
  public String checkUploadStatus(String sid, String url) throws Exception
  {
    if ( s3 != null )
     {
        int p = s3.getProgress();

        if( p == 100 || p == -1)
            s3.terminate();

        if( p  == 100 )
           return "success";

        if( p == -1 )
           return "failure";

        return "progress:" + Integer.toString(p);
     }
    else
      return "failure";
  }

  @Override
  public Vector getVMs(String sid) throws Exception
    {
      Vector<String> vms = new Vector<String>();
       try {
         String AWS_PUBLIC_KEY = properties.vmImagesAccountName;
         String AWS_SECRET_KEY = properties.vmImagesAccountPassword;

         s3 = new S3Wrapper(AWS_PUBLIC_KEY, AWS_SECRET_KEY);
         List<String> imageList = s3.listImages(sid);

         for(String item : imageList)
         {
           vms.add(item);
         }

      } catch(Exception ex)
      {
      }

      return vms;
    }


  @Override
  public boolean downloadVMimage(String sid, String vmimage, String localFolder) throws Exception
    {
     try
      {
       String AWS_PUBLIC_KEY = properties.vmImagesAccountName;
       String AWS_SECRET_KEY = properties.vmImagesAccountPassword;

       s3 = new S3Wrapper(AWS_PUBLIC_KEY, AWS_SECRET_KEY);
       s3.downloadImage(sid, vmimage, localFolder);
       return true;
      }
     catch(Exception ex)
      {
        return false;
      }
    }
}
