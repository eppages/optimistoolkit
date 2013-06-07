package eu.optimis.arsyswrapper;
import java.net.Authenticator;
import java.net.PasswordAuthentication;

import java.io.*;
import java.io.StringReader;
import java.io.StringWriter;

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

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;

import java.io.IOException;
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

public class Connector
{
    HashMap<String, UploadThread> tasks = new HashMap<String, UploadThread>();
    HashMap<String, TerminateServiceThread> terminateTasks = new HashMap<String, TerminateServiceThread>();
    String ftp_username = "";
    String ftp_password = "";
    String ftp_server   = "";

    public Connector()
    {
      authenticate("imagemanager", "opt1M1$12");
    }

    public String uploadNonOptimisFromLocal(String sid, String local, String remoteFileName)
    {
       Date date = new Date();
       System.out.println("[" + date.toString() + "] UploadNonOptimis Request:");

       boolean exists = (new File(local)).exists();

      if( !exists )
       {
          System.out.println("Error: Cannot find file " + local);
          return "failure";
       }

       if( ftp_username.equals("") )
          {
            try
             {
                 boolean result = requestServiceUpload(sid);
             }
             catch(Exception ex)
              {
                 System.out.println("Request ServiceUpload Error:"  + ex);
              }
          }

       if( ftp_username.equals("") )
       {
          TerminateServiceThread.terminateServiceUpload(sid);
            try
             {
                 boolean result = requestServiceUpload(sid);
                 return "failure";
             }
             catch(Exception ex)
              {
                 System.out.println("Request ServiceUpload Error:"  + ex);
                 return "failure";
              }
       }

       UploadThread thread = new UploadThread(sid, local, remoteFileName);
       thread.setFTPCredentials(ftp_server, ftp_username, ftp_password);
       String key = sid + ":" + local;
       thread.start();
       tasks.put(key, thread);
       return "success";
    }

    public String uploadNonOptimisStatusFromLocal(String sid, String url)
    {
       return uploadNonOptimisStatus(sid, url);
    }

    // RPC CALL: uploadNonOptimis request
    public String uploadNonOptimis(String sid, String url, String remoteFileName)
    {
       Date date = new Date();
       System.out.println("[" + date.toString() + "] UploadNonOptimis Request:");

       String fileName = url.substring( url.lastIndexOf('/') + 1, url.length() );
       String local = "/home/spimages/sids/" + fileName;
       boolean exists = (new File(local)).exists();

       if( !exists )
        {
          System.out.println("Error: Cannot find file " + fileName);
          return "failure";
        }

       if( ftp_username.equals("") )
          {
            try
             {
                 boolean result = requestServiceUpload(sid);
             }
             catch(Exception ex)
              {
                 System.out.println("Request ServiceUpload Error:"  + ex);
              }
          }

       if( ftp_username.equals("") )
       {
          TerminateServiceThread.terminateServiceUpload(sid);
            try
             {
                 boolean result = requestServiceUpload(sid);
                 return "failure";
             }
             catch(Exception ex)
              {
                 System.out.println("Request ServiceUpload Error:"  + ex);
                 return "failure";
              }
       }

       UploadThread thread = new UploadThread(sid, local, remoteFileName);
       thread.setFTPCredentials(ftp_server, ftp_username, ftp_password);
       String key = sid + ":" + url;
       thread.start();
       tasks.put(key, thread);

       return "success";
    }

  //  RPC CALL: get status
   public String uploadNonOptimisStatus(String sid, String url)
   {
     String key = sid + ":" + url;

     if( tasks.containsKey(key) )
      {
         String result = tasks.get(key).progress();

         if( result.equals("success") || result.equals("failure") )
                tasks.remove(key);

         return result;
        }

      return "failure";
   }

    public boolean requestServiceUpload(String serviceId)
    {
        try {
            es.arsys.ImageManager service = new es.arsys.ImageManager();
            es.arsys.ImageManagerSoap port = service.getImageManagerSoap();
            es.arsys.RequestServiceUploadResponse.RequestServiceUploadResult result = port.requestServiceUpload(serviceId);

            final JAXBContext context = JAXBContext.newInstance(result.getClass());
            final Marshaller marshaller = context.createMarshaller();
            final StringWriter stringWriter = new StringWriter();

            marshaller.marshal( new JAXBElement(new QName("uri","local"), RequestServiceUploadResult.class, result), stringWriter );

            String xml = stringWriter.toString();
            System.out.println("requestServiceUpload: " + xml);
            System.out.println("--------------");

            DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            InputSource is = new InputSource();
            is.setCharacterStream(new StringReader(xml));

            Document doc = db.parse(is);
            NodeList nodes = doc.getElementsByTagName("requestServiceUpload");

            if( nodes.getLength() > 0 )
            {
               ftp_server   =  getElement(nodes, 0, "Host");
               ftp_username =  getElement(nodes, 0, "User");
               ftp_password =  getElement(nodes, 0, "Password");

               System.out.println("Host: "     + ftp_server );
               System.out.println("User: "     + ftp_username );
               System.out.println("Password: " + ftp_password );
             }

            return true;

        } catch (Exception ex) {
            System.out.println("RequestServiceUpload Exception :" + ex.toString());
        }

        return false;
    }

    // RPC: terminate service
    public String terminateService(String sid)
    {
       System.out.println("RPC: called terminateService");
       TerminateServiceThread thread = new TerminateServiceThread(sid);
       thread.start();
       terminateTasks.put(sid, thread);
       return "success";
    }

    // RPC: get termination status
    public String terminateServiceStatus(String sid)
    {
      if( terminateTasks.containsKey(sid) )
       {
          System.out.println(".");
          String result = terminateTasks.get(sid).getStatus();


          if( result.equals("success") || result.equals("failure") )
          {
             ftp_server   = "";
             ftp_username = "";
             ftp_password = "";
             terminateTasks.remove(sid);
          }

          return result;
        }

      return "failure";
    }

    public static void test()
    {
    try {
       Connector conn = new Connector();

       String provider = "arsys";
       String sid      = "mytest";
       String url   = "/root/video1.img";

       conn.uploadNonOptimisFromLocal(sid, url, "video1.img");

        String status = "";

       while(true)
        {
           status = conn.uploadNonOptimisStatusFromLocal( sid, url);

           if( status.equals("success") || status.equals("failure") )
              break;

           System.out.print("\r" + status);
           Thread.sleep(4000);
        }

       if( status.equals("success") )
         {
           System.out.println("\nUpload finished successfully");
         }
        else
         {
           System.out.println("Upload failed!");
         }

        conn.terminateService(sid);

       while(true)
       {
         status = conn.terminateServiceStatus(sid);

         if( status.equals("success") || status.equals("failure") )
              break;

         System.out.print("\r" + status);
         Thread.sleep(4000);
       }

      } catch(Exception ex)
      {
       System.out.println(ex.toString());
      }
    }

    public static void main( String[] args )
    {
      try
       {

          System.out.println("Arsys Uploader Service");

          EventLoop loop = EventLoop.defaultEventLoop();
          Server svr = new Server(loop);
          Connector conn = new Connector();
          svr.serve(conn);
          svr.listen(9091);
          loop.join();

        }
      catch(Exception ex)
        {
	        System.out.println(ex);
        }
    }

    public void authenticate(final String username, final String password)
    {
          Authenticator myAuth = new Authenticator()
          {
               @Override
               protected PasswordAuthentication getPasswordAuthentication()
               {
                   return new PasswordAuthentication(username, password.toCharArray());
               }
          };

          Authenticator.setDefault(myAuth);
    }


    private String getCharacterDataFromElement(Element e)
    {
      Node child = e.getFirstChild();
      if (child instanceof CharacterData)
      {
        CharacterData cd = (CharacterData) child;
        return cd.getData();
      }
      return "";
    }

    private String getElement(NodeList nodes, int index, String tagname)
    {
        Element element = (Element) nodes.item(index);
        NodeList name = element.getElementsByTagName(tagname);
        Element host = (Element) name.item(0);
        return getCharacterDataFromElement(host);
    }
}
