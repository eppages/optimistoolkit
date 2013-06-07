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

public class DataManagerTests
{
  class InfrastructureProvider
  {
     public String name;
     public String masterLocalIP;
     public String masterPublicIP;
     public String vmImagesAccountName;
     public String vmImagesAccountPassword;
     private String reachableIP = "";

     public String getIP()
     {
      if( reachableIP != "" )
        return reachableIP;

      try {
       InetAddress address = InetAddress.getByName(masterPublicIP);

       if( address.isReachable(3000) )
        {
          reachableIP = masterPublicIP;
          return masterPublicIP;
        }
       else
        {
          reachableIP = masterLocalIP;
          return masterLocalIP;
        }
     }
     catch (UnknownHostException e) {
         reachableIP = masterLocalIP;
         return masterLocalIP;
     }
     catch (IOException e) {
         reachableIP = masterLocalIP;
         return masterLocalIP;
     }
    }
  }


  public List<InfrastructureProvider> globalProviders = new ArrayList<InfrastructureProvider>();

  private void loadProviders()
  {
   InfrastructureProvider atos = new InfrastructureProvider();
   atos.name                    = "atos";
   atos.masterLocalIP           = "213.27.211.117";                     //"192.168.252.59";
   atos.masterPublicIP          = "213.27.211.117";
   atos.vmImagesAccountName     = "vmimages";
   atos.vmImagesAccountPassword = "vmimages@atos";
   globalProviders.add(atos);

   InfrastructureProvider flexi = new InfrastructureProvider();
   flexi.name                    = "flexiscale";
   flexi.masterLocalIP           = "213.27.211.117";
   flexi.masterPublicIP          = "213.27.211.117";
   flexi.vmImagesAccountName     = "vmimages";
   flexi.vmImagesAccountPassword = "vmimages@atos";
   globalProviders.add(flexi);

   InfrastructureProvider umea  = new InfrastructureProvider();
   umea.name                    = "umea";
   umea.masterLocalIP           = "213.27.211.117";
   umea.masterPublicIP          = "213.27.211.117";
   umea.vmImagesAccountName     = "vmimages";
   umea.vmImagesAccountPassword = "vmimages@atos";
   globalProviders.add(umea);

  }

  private InfrastructureProvider findIP_Provider(String name) throws Exception
  {
    InfrastructureProvider found;
    Iterator<InfrastructureProvider> iterator = globalProviders.iterator();

    while ( iterator.hasNext() )
     {
       InfrastructureProvider provider = iterator.next();
       if( provider.name.equals(name) )
          return provider;
     }

    throw new Exception("Cannot find provider with name '" + name + "'");
  }

  public DataManagerTests()
  {
    loadProviders();
  }

  public String createUsersRepository(String nameProvider, String serviceID, boolean isSensitive) throws Exception
    {
        //System.out.println("infProv.name");

        InfrastructureProvider infProv = findIP_Provider(nameProvider);
        Client client = Client.create();
	String dmURL = "http://" + infProv.getIP() + ":8080/DataManagerAPI/account/create/" + serviceID;
        System.out.println(dmURL);
	WebResource  resource = client.resource(dmURL);
	MultivaluedMap<String, String> queryParams  = new MultivaluedMapImpl();
	String result = resource.queryParams(queryParams).get(String.class);
	String key = parseCreateAccountResponse(result);
        return key;
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

    /*
     * Upload an virtual machine image to the sid's vm images storage
     * @param nameProvider    One of the global IP provider's name
     * @param sid             Service ID
     * @param imageLocalPath  The local path to the the Virtual machine image
     *
     * @return          <code>true</code> if the upload was successfull
     *                  <code>false</code> otherwise.
     */
   public boolean uploadVMimage(String nameProvider, String sid, String imageLocalPath) throws Exception
    {
     InfrastructureProvider infProv = findIP_Provider(nameProvider);

      try {
         JSch jsch = new JSch();
         Session session = jsch.getSession(infProv.vmImagesAccountName , infProv.getIP(), 22);
         session.setConfig("StrictHostKeyChecking", "no");
         session.setPassword(infProv.vmImagesAccountPassword);
         session.connect();
         Channel channel = session.openChannel( "sftp" );
         channel.connect();
         ChannelSftp sftpChannel = (ChannelSftp) channel;
         String vmPath = "/home/vmimages/sids/" + sid + "/";
         sftpChannel.put(imageLocalPath, vmPath );
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

   /* Returns the list of all VMs stored for a specific sid
    * @param nameProvider    One of the global IP provider's name
    * @param  sid Service ID
    *
    * @return  List of VM image names
    */
   public Vector getVMs(String nameProvider, String sid) throws Exception
    {
      Vector<String> vms = new Vector<String>();

      InfrastructureProvider infProv = findIP_Provider(nameProvider);

      try {
         JSch jsch = new JSch();
         Session session = jsch.getSession(infProv.vmImagesAccountName , infProv.getIP(), 22);
         session.setConfig("StrictHostKeyChecking", "no");
         session.setPassword(infProv.vmImagesAccountPassword);
         session.connect();
         Channel channel = session.openChannel( "sftp" );
         channel.connect();
         ChannelSftp sftpChannel = (ChannelSftp) channel;
         String path = "/home/vmimages/sids/" + sid + "/";
         vms = sftpChannel.ls(path);
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
      InfrastructureProvider infProv = findIP_Provider(nameProvider);

      try {
         JSch jsch = new JSch();
         Session session = jsch.getSession(infProv.vmImagesAccountName , infProv.getIP(), 22);
         session.setConfig("StrictHostKeyChecking", "no");
         session.setPassword(infProv.vmImagesAccountPassword);
         session.connect();
         Channel channel = session.openChannel( "sftp" );
         channel.connect();
         ChannelSftp sftpChannel = (ChannelSftp) channel;
         String vmPath = "/home/vmimages/sids/" + sid + "/" + vmimage;
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

   public boolean deleteAccount(String nameProvider, String sid)
    {
      return false;
    }

    public int getStorageUsage(String nameProvider, String sid)
    {
      return 0;
    }

public static String generateString(Random rng, String characters, int length)
{
    char[] text = new char[length];
    for (int i = 0; i < length; i++)
    {
        text[i] = characters.charAt(rng.nextInt(characters.length()));
    }
    return new String(text);
}

public static void generateRandomFile()
{
 final Random random = new Random(System.nanoTime());

 try{
  FileWriter fstream = new FileWriter("test.img");
  BufferedWriter out = new BufferedWriter(fstream);

  for(int i = 0; i < 1000; ++i)
     out.write("Hello Java:" + generateString(random, "qwertyuioplkjhgfdsazxcvbnm0987654321", 82) + "\n");

  out.close();
  } catch (Exception e)
    {//Catch exception if any
        System.err.println("Error: " + e.getMessage());
    }

}

public static String calcHash(String datafile) throws Exception
{
    MessageDigest md = MessageDigest.getInstance("SHA1");
    FileInputStream fis = new FileInputStream(datafile);
    byte[] dataBytes = new byte[1024];
    int nread = 0;
    while ((nread = fis.read(dataBytes)) != -1) {
      md.update(dataBytes, 0, nread);
    };
    byte[] mdbytes = md.digest();

    //convert the byte to hex format
    StringBuffer sb = new StringBuffer("");
    for (int i = 0; i < mdbytes.length; i++) {
    	sb.append(Integer.toString((mdbytes[i] & 0xff) + 0x100, 16).substring(1));
    }

    return sb.toString();

  }

    public static void main( String[] args )
    {
      try {
           final Random random = new Random(System.nanoTime());

            DataManagerTests client = new DataManagerTests();

            System.out.println("== DataManager Unit Test Cases ==\n\n");

            /*************************************************************************/
            System.out.println("\nTest Case: TC_DM_1.1 - Data Management User Credentials");
            String  username = generateString(random, "abcdfgeklmasdnopqwert", 5);
            System.out.println("Try to generate random username account: " + username);

            try {
            System.out.println("Result : " +  client.createUsersRepository("atos", username, true) );
            System.out.println("Test case TC_DM_1.1 completed successfully!");

            }
            catch( Exception e)
            {
               System.out.println("Error: Test case TC_DM_1.1 failed!");
            }

            /*************************************************************************/
            System.out.println("\n\nTest Case: TC_DM_2.1 - Uploading and downloading data from OPTIMIS DFS");
            System.out.println("Create random image file...");
            generateRandomFile();
            String sha1 = calcHash("test.img");
            System.out.println("SHA-1 code = " + sha1 );
            System.out.println("Connecting to optimis dfs");
            System.out.println("Start uploading...");
            client.uploadVMimage("atos", "demoapp", "./test.img");
            System.out.println("Uploading completed successfully...");
            System.out.println("Start downloading...");
            client.downloadVMimage("atos", "demoapp", "test.img", "test-down.img");
            String sha2 = calcHash("test-down.img");
            System.out.println("Checking SHA-1 code = " + sha2 );
            if( sha1.equals(sha2) )
             {
                System.out.println("Success: Hash values are equal!");
                System.out.println("Test case TC_DM_2.1 completed successfully!");
             }
            else
                System.out.println("Failure: Hash values are not equal!");


      } catch( Exception e)
      {
        System.out.println( "Error: !\n" );
      }
    }

}

