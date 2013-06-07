package eu.optimis.DataManagerTests;

import java.io.FileInputStream;
import java.security.MessageDigest;
import java.util.Random;
import java.io.IOException;
import java.io.StringReader;
import java.util.UUID;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Vector;
import java.io.File;
import java.io.*;
import java.net.*;
import java.io.PrintWriter;
import java.io.StringWriter;
import com.jcraft.jsch.*;
import org.w3c.dom.*;
import org.xml.sax.InputSource;
import javax.ws.rs.core.MultivaluedMap;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.core.util.MultivaluedMapImpl;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import eu.optimis.DataManagerClient.*;
import org.apache.log4j.*;


public class DataManagerTests
{
    public static void main( String[] args )
    {
      try {
        DataManagerClient client = new DataManagerClient();

        String currentDir = new File(".").getAbsolutePath();
        String sid       = "mytest";
        String url       = currentDir + "/file.iso";
        String iprovider = "broker";
        String status;

        String key = client.createUsersRepository(iprovider, sid, true);
        String vmImagePath = client.uploadVMimageRequest(iprovider, sid, url);

        while(true)
           {
               status = client.checkUploadStatus(iprovider, sid, url);

               if( status.equals("success") || status.equals("failure") )
                   break;

               System.out.println(status);

               Thread.sleep(4000);
           }

        if( status.equals("success") )
            {
                System.out.println("Upload finished successfully");
                System.out.println("vmimagepath = " + vmImagePath);
            }
        else
            {
                System.out.println("Upload failed!");
            }


        } catch(Exception e)
              {
                  System.out.println("DatamanagerClientTest:" + e);
              }

    }

}



