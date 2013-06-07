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

class TerminateServiceThread extends Thread
{
  String sid;
  String status;

  TerminateServiceThread(String serviceID)
  {
    sid    = serviceID;
    status = "pending";
  }

  public String getStatus()
  {
     return status;
  }

  public void run()
  {
  	status = "pending";

    if( terminateServiceUpload(sid) )
    	status = "success";
    else
    	status = "failure";
  }

  public static boolean terminateServiceUpload(String serviceId)
  {
        try 
        {
            System.out.println("\nTerminateServiceUpload: ");

            es.arsys.ImageManager service = new es.arsys.ImageManager();
            es.arsys.ImageManagerSoap port = service.getImageManagerSoap();
            es.arsys.TerminateServiceUploadResponse.TerminateServiceUploadResult result = port.terminateServiceUpload(serviceId);

            final JAXBContext context       = JAXBContext.newInstance(result.getClass());
            final Marshaller marshaller     = context.createMarshaller();
            final StringWriter stringWriter = new StringWriter();

            marshaller.marshal( new JAXBElement(new QName("uri","local"),
                                                TerminateServiceUploadResult.class, result), stringWriter );

            String xml = stringWriter.toString();
            System.out.println("\nResponse: " + xml);
            System.out.println("-------------------");

            return true;

        } catch (Exception ex)
        {
            System.out.println("Exception terminateServiceUpload: " + ex.toString());
        }

	    return false;
    }

}

