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
import eu.optimis.dm.dmclient_s3wrapper.*;

public class Tests
{
  public Tests()
    {
    }

   private static ICloudProvider getProvider(String name) throws Exception
   {
     return DataManagerClient.getProvider(name);
   }


   public static void testObj()
   {
     try
      {
       String provider = "umea";

       DataManagerClient client = new DataManagerClient();

       String result = client.specifyObjective(provider, DataManagerClient.TREC_OBJECTIVE_FUNCTION_TRUST, 5, 2, 5);
       System.out.println("result = " + result);
      }
     catch(Exception ex)
      {
        System.out.println(ex.toString() );
      }
    }

   public static void testFileExists()
   {
    try {
     String nameProvider = "umea";
     InfrastructureProviderProperties p = ConfigFile.getProviderProperties(nameProvider);
     String url1 = "/home/test/41c3acbc-ada7-4ae8-a39c-bf97d8f9d098.qcow2";
     String url2 = "/home/test/41c3acbc-ada7-4ae8-a39.qcow2";
     String sid = "GeneDetectionBroker";
     String ipAddress = p.getIP();
     String username  = p.vmImagesAccountName;
     String password  = p.vmImagesAccountPassword;
     String repoPath  = p.vmpath;
     boolean result = LocalUpload.isFileExists(url1, sid, ipAddress, repoPath, username, password);
     System.out.println("Result (" + url1 + ") = " + result );
     result = LocalUpload.isFileExists(url2, sid, ipAddress, repoPath, username, password);
     System.out.println("Result ( " + url2 + ") = "  + result );
     } catch(Exception ex)
     {
       System.out.println(ex.toString());
     }
   System.exit(0);
   }

   public static void testFCS()
   {
     try {
           DataManagerClient client = new DataManagerClient();

           String provider        = "umea";
           String sid             = "arsys";
           String numPredictions  = "4";
           String token           = client.startFCSJob(provider, sid, numPredictions);
           String result;

           System.out.println("token = " + token);

           while(true)
            {
                result = client.finishedFCSJob(provider, token);

               if( result.equals("PENDING") )
                 {
                   System.out.print(".");
                   Thread.sleep(2000);
                 }
               else
                  break;
            }

           System.out.println("\nResult = " + result);
           } catch(Exception ex)
            {

            }
   }

   public static void test11(String url)
   {
     try
      {
       String provider = "arsys";
       String sid      = "mytest";

       DataManagerClient client = new DataManagerClient();

       String key = client.createUsersRepository(provider, sid, false);
       System.out.println("For provider = " + provider + " and sid = " + sid + "  the key: \n" + key );

       //DataManagerClient.check_if_exists = true;

       String vmImagePath = client.uploadVMimageRequest(provider, sid, url);
       System.out.println("After client.uploadVMimageRequest:" + vmImagePath );

       String status;

       while(true)
        {
           status = client.checkUploadStatus(provider, sid, url);

           if( status.equals("success") || status.equals("failure") )
              break;

           System.out.print("\r" + status);
           Thread.sleep(500);
        }

       if( status.equals("success") )
         {
           System.out.println("\nUpload finished successfully");
           System.out.println("vmimagepath = " + vmImagePath);
         }
        else
         {
           System.out.println("Upload failed!");
         }

      }
      catch(Exception ex)
      {
         System.out.println(ex.toString());
      }
   }


   public static void test10()
   {
      try
      {
      String manifestPath = "/root/DM/manifest.xml";
      String manifest     = ServiceUtil.readFile(manifestPath);
      String sid             = "3d022660-28c3-4ac4-b1a5-20e9db28acc6";
      String localProvider   = "arsysfull";
      String foreignProvider = "arsysfull";

         DataManagerClient client = new DataManagerClient();
         String status = client.checklegal(sid, manifest, localProvider, foreignProvider);

         System.out.println("Legal status = " + status);
      } catch(Exception ex)
      {
        ex.printStackTrace();
      }
   }

   public static void test9(String url)
   {
     try
      {
       String provider = "amazon";
       String sid      = "mytest";

       DataManagerClient client = new DataManagerClient();

       String key = client.createUsersRepository(provider, sid, false);
       System.out.println("For provider = " + provider + " and sid = " + sid + "  the key: \n" + key );

       String vmImagePath = client.uploadVMimageRequest(provider, sid, url);
       System.out.println("After client.uploadVMimageRequest:" + vmImagePath );

       String status;

       while(true)
        {
           status = client.checkUploadStatus(provider, sid, url);

           if( status.equals("success") || status.equals("failure") )
              break;

           System.out.print("\r" + status);
           Thread.sleep(500);
        }

       if( status.equals("success") )
         {
           System.out.println("\nUpload finished successfully");
           System.out.println("vmimagepath = " + vmImagePath);
         }
        else
         {
           System.out.println("Upload failed!");
         }

      }
      catch(Exception ex)
      {
         System.out.println(ex.toString());
      }
   }

   public static void test8(String urlFile)
   {
     try
     {
       String AWS_PUBLIC_KEY = "";
       String AWS_SECRET_KEY = "";
       String SERVICE_ID     = "mytest";

       S3Wrapper s3 = new S3Wrapper(AWS_PUBLIC_KEY, AWS_SECRET_KEY);
       URL url = s3.uploadImage(SERVICE_ID, urlFile);

       while(true)
       {
            int p = s3.getProgress();
            System.out.println("Progress: " + Integer.toString(p));
            if( p == 100 || p == -1) break;
            Thread.sleep(500);
       }

       System.out.println(url);
       s3.terminate();
     }
     catch(Exception ex)
     {
        System.out.println(ex.toString());
     }
   }

   public static void test7(String msid, String url)
   {
     try
      {
       String provider = "arsys";
       String sid      = msid;

       DataManagerClient client = new DataManagerClient();

       String key = client.createUsersRepository(provider, sid, false);
       System.out.println("For provider = " + provider + " and sid = " + sid + "  the key: \n" + key );

       String vmImagePath = client.uploadVMimageRequest(provider, sid, url);
       System.out.println("After client.uploadVMimageRequest:" + vmImagePath );

       String status;

       while(true)
        {
           status = client.checkUploadStatus(provider, sid, url);

           if( status.equals("success") || status.equals("failure") )
              break;

           System.out.print("\r" + status);
           Thread.sleep(4000);
        }

       if( status.equals("success") )
         {
           System.out.println("\nUpload finished successfully");
           System.out.println("vmimagepath = " + vmImagePath);
         }
        else
         {
           System.out.println("Upload failed!");
         }

      }
      catch(Exception ex)
      {
         System.out.println(ex.toString());
      }
   }

   public static void test6(String url)
   {
     try
      {
       String provider = "atos";
       String sid      = "mytest";

       DataManagerClient client = new DataManagerClient();

       String key = client.createUsersRepository(provider, sid, false);
       System.out.println("For provider = " + provider + " and sid = " + sid + "  the key: \n" + key );

       String vmImagePath = client.uploadVMimageRequest(provider, sid, url);

       String status;

       while(true)
        {
           status = client.checkUploadStatus(provider, sid, url);

           if( status.equals("success") || status.equals("failure") )
              break;

           System.out.print("\r" + status);
           Thread.sleep(4000);
        }

       if( status.equals("success") )
         {
           System.out.println("\nUpload finished successfully");
           System.out.println("vmimagepath = " + vmImagePath);
         }
        else
         {
           System.out.println("Upload failed!");
         }

      }
      catch(Exception ex)
      {
         System.out.println(ex.toString());
      }
   }

   public static void test5()
   {
     try {
       String provider = "atos";
       String sid      = "mytest";
       ICloudProvider manager = getProvider(provider);

       Vector images = manager.getVMs(sid);
       for(int i = 0; i < images.size(); ++i)
         System.out.println("Image:" + images.get(i) );

     } catch(Exception ex)
     {
        System.out.println(ex.toString());
     }
   }

   public static void test4(String url)
   {
     try
      {
       String provider = "bsc";
       String sid      = "mytest";

       ICloudProvider manager = getProvider(provider);

       String key = manager.createUsersRepository(sid, false);
       System.out.println("For provider = " + provider + " and sid = " + sid + "  the key: \n" + key );

       String vmImagePath = manager.uploadVMimageRequest(sid, url);

       String status;

       while(true)
        {
           status = manager.checkUploadStatus(sid, url);

           if( status.equals("success") || status.equals("failure") )
              break;

           System.out.print("\r" + status);
           Thread.sleep(4000);
        }

       if( status.equals("success") )
         {
           System.out.println("\nUpload finished successfully");
           System.out.println("vmimagepath = " + vmImagePath);
         }
        else
         {
           System.out.println("Upload failed!");
         }

      }
      catch(Exception ex)
      {
         System.out.println(ex.toString());
      }
   }


   public static  void test3()
   {
      try
      {
       String provider       = "atos";
       ICloudProvider manager = getProvider(provider);
       String result =  manager.getCPD();
       System.out.println("CPD:\n" + result);
      }
      catch(Exception ex)
      {
         System.out.println(ex.toString());
      }
   }

   public static  void test2()
   {
      try
      {
       String provider       = "atos";
       String remoteProvider = "umea";
       String sid            = "mytest";
       String manifestXML    = ServiceUtil.readFile("/usr/optimis/dm-legal/sample-manifests/manifest3.xml");

       ICloudProvider manager = getProvider(provider);

       String result =  manager.checklegal(sid, manifestXML, provider, remoteProvider);
       System.out.println("Legal Assesment: " + result);
      }
      catch(Exception ex)
      {
         System.out.println(ex.toString());
      }
   }

   public static void test1()
   {
     try
      {
       String provider = "atos";
       String sid      = "mytest";

       ICloudProvider manager = getProvider(provider);

       String key = manager.createUsersRepository(sid, false);
       System.out.println("For provider = " + provider + " and sid = " + sid + "  the key: \n" + key );

      }
      catch(Exception ex)
      {
         System.out.println(ex.toString());
      }
   }

  public static void main(String[] args)
  {
    System.out.println("OPTIMIS DATAMANAGER CLIENT TESTS");

    try{
     //test4("/root/video.img");
     //test4("http://www.loco-toys.de/mirror/images/raspbian/2013-02-09-wheezy-raspbian/2013-02-09-wheezy-raspbian.zip");
     //test6("/root/video1.img");
     //test7(args[0], args[1]);
    
/* 
	System.out.println("Enter something here : ");
 
	try{
	    BufferedReader bufferRead = new BufferedReader(new InputStreamReader(System.in));
	    String s = bufferRead.readLine();
 
	    System.out.println(s);
	}
	catch(IOException e)
	{
		e.printStackTrace();
	}
*/
///     test7("/opt/optimis/vmc/runtime/repository/optimisWinHVM_w2k3.vmdk");
  //   test7("/opt/optimis/vmc/runtime/repository/optimisWinHVM.w2k3.vmdk");

     //test8("/root/video1.img");
     //test9("/root/video1.img");
     //test10();
     test11("/root/video1.img");
     //test11("http://www.loco-toys.de/mirror/images/raspbian/2013-02-09-wheezy-raspbian/2013-02-09-wheezy-raspbian.zip");
     //testFCS();
     //  test10();
     //testObj();
     //test11("http://130.239.48.102/optimis-ics/e361c1de-7ba1-4e7f-b477-6466e632b0dd.qcow2");
     //testFileExists();
//     test11("/home/test/41c3acbc-ada7-4ae8-a39c-bf97d8f9d098.qcow2");
     //test11("http://130.239.48.102/optimis-ics/4e542462-41af-4b1f-83fa-cc9311a2ef97.qcow2");
    // test11("/root/bigimage.qcow");

      //test11("http://a1408.g.akamai.net/5/1408/1388/2005110403/1a1a1ad948be278cff2d96046ad90768d848b41947aa1986/sample_iPod.m4v.zip");
    } catch(Exception ex)
    {
    }
  }
}

