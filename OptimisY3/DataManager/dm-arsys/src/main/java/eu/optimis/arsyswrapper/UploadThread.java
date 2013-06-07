package eu.optimis.arsyswrapper;
import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.io.*;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlRootElement;

import es.arsys.RequestServiceUploadResponse.*;
import es.arsys.TerminateServiceUploadResponse.*;
import es.arsys.ValidateImageUploadResponse.*;

import javax.xml.bind.*;
import javax.xml.namespace.QName;
import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.CharacterData;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import java.security.*;

import org.msgpack.rpc.Server;
import org.msgpack.rpc.loop.EventLoop;
import org.msgpack.rpc.Request;


import org.apache.commons.net.PrintCommandListener;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPHTTPClient;
import org.apache.commons.net.ftp.FTPClientConfig;
import org.apache.commons.net.ftp.FTPConnectionClosedException;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.commons.net.ftp.FTPSClient;
import org.apache.commons.net.io.CopyStreamEvent;
import org.apache.commons.net.io.CopyStreamListener;
import org.apache.commons.net.util.TrustManagerUtils;
import java.util.Date;
import java.util.HashMap;
import java.util.*;
import java.util.Iterator;
import java.util.Map;

class UploadThread extends Thread
{
  ProgressInputStream pStream;
  String input_sid;
  String input_local;
  String input_remote;
  String ftp_server    = "";
  String ftp_username  = "";
  String ftp_password  = "";
  boolean isFailure;

  public UploadThread(String sid, String local, String remote)
  {
    input_sid    = sid;
    input_local  = local;
    input_remote = remote;
    isFailure    = false;
	}

  public void setFTPCredentials(String server, String username, String password)
  {
    ftp_server   = server;
    ftp_username = username;
    ftp_password = password;
  }

  public void run()
  {
      isFailure = false;
      uploadVM(input_sid, input_local, input_remote);
      System.out.println("======================================\n");
  }

  public void uploadVM(String serviceID, String local, String remote)
  {
    System.out.println("================ UPLOAD VM  ================\n");
    System.out.println("sid    = " + serviceID);
    System.out.println("local  = " + local);
    System.out.println("remote = " + remote);

    try
     {
        boolean result;

	System.out.println("Calculating MD5Sum...");
        String md5 = MD5Util.getMD5Checksum(local);
        System.out.println("MD5sum = " + md5);

        System.out.println("Start Uploading:");
        result = upload(ftp_server, ftp_username, ftp_password, local, remote);
        if( result == false) { isFailure = true;  return; }

        System.out.println("Validate Image Upload:");
        result = validateImageUpload(serviceID, remote, md5);
        if( result == false) { isFailure = true;  return; }

      }
        catch(Exception ex)
	 {
            isFailure = true;
            System.out.println("Error:" + ex.toString());
         }
    }

    public String progress()
    {
      if( isFailure )
        return "failure";

      if( pStream == null)
        return "progress:0";
      else
       {
         int prog = pStream.getProgress();

         if( prog == 100 )
            return "success";
         else
           return "progress:" + prog;
       }
    }

    public boolean upload(String server, String username, String password, String local, String remote)
    {
       boolean binaryTransfer = true;
       FTPClient ftp = new FTPClient();
       int reply;

       try
       {
         ftp.connect(server);
         System.out.println("Connected to ftp: " + server);
         reply = ftp.getReplyCode();

         if (!FTPReply.isPositiveCompletion(reply))
          {
             ftp.disconnect();
             System.err.println("Error: FTP server refused connection.");
             pStream = null;
             return false;
          }

         if(!ftp.login(username, password))
          {
             ftp.logout();
             pStream = null;
            return false;
          }

         System.out.println("Remote system is " + ftp.getSystemType());

         if(binaryTransfer)
         {
           ftp.setFileType(FTP.BINARY_FILE_TYPE);
         }

        java.io.File file = new java.io.File(local);
        long filesize = file.length();

        InputStream input;
        input = new FileInputStream(local);
        pStream = new ProgressInputStream(local, input, filesize);

        ftp.setControlKeepAliveTimeout(300);

        boolean exists = false;
        String[] listfiles = ftp.listNames(".");
        for(int i = 0; i < listfiles.length; ++i)
          if( listfiles[i].equals("./" + remote) )
           {
             System.out.println("FILE EXISTS: " + listfiles[i] );
             exists = true;
           }

        if(!exists)
        {
         // ftp.deleteFile(remote);
          ftp.storeFile(remote, pStream);
        }

        pStream.close();
        input.close();

        ftp.noop();
        ftp.logout();

       } catch(Exception ex)
       {
           System.out.println("FTP Upload Error: " + ex.toString());
           ex.printStackTrace();

           pStream = null;
           return false;
       }

       return true;
    }

    public boolean validateImageUpload(String serviceId, String imageFilename, String md5Sum)
    {
       System.out.println("ValidateImageUpload: ");
       System.out.println("filename: " + imageFilename);

        try {
            es.arsys.ImageManager service = new es.arsys.ImageManager();
            es.arsys.ImageManagerSoap port = service.getImageManagerSoap();
            ValidateImageUploadResult result = port.validateImageUpload(serviceId, imageFilename, md5Sum);

            final JAXBContext context       = JAXBContext.newInstance(result.getClass());
            final Marshaller marshaller     = context.createMarshaller();
            final StringWriter stringWriter = new StringWriter();

            marshaller.marshal( new JAXBElement(new QName("uri","local"), 
                                ValidateImageUploadResult.class, result),
				                        stringWriter );

            String xml = stringWriter.toString();
            
            System.out.println("response: " + xml);
            System.out.println("\n-----------------------");

            return true;
        } catch (Exception ex) {
	         System.out.println("Error: " + ex.toString());
        }

	      return false;
    }

   }
