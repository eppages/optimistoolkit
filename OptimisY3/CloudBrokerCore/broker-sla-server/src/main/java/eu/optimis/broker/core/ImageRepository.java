package eu.optimis.broker.core;

import java.io.*;
import java.util.*;
import java.security.NoSuchAlgorithmException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class ImageRepository
{
   /**
    *   The list of virtual images belonging to a service
    *   @param    sid   the service ID
    *   @returns  List of absolute file paths
    */
    public static List<String> getFilePaths(String sid)
	{
          String localSID     = validServiceID(sid);
          String servicePath  = repoRootPath + "/" + localSID;
          File dir            = new File(servicePath);

          FilenameFilter filter = new FilenameFilter() {
                public boolean accept(File dir, String name) {
                   return !name.startsWith(".");
                }
          };

          String[] children = dir.list(filter);
          List<String> images = new LinkedList<String>();

          if (children != null)
             for(int i = 0; i < children.length; i++)
                {
                  String imagePath = repoRootPath + "/" + localSID + "/" + children[i];
                  images.add(imagePath);
                }

          return images;
	}

    private static final String repoRootPath = "/home/vmimages/sids";

    private static String convertToHex(byte[] data)
    {
        StringBuffer buf = new StringBuffer();
        for (int i = 0; i < data.length; i++) {
            int halfbyte = (data[i] >>> 4) & 0x0F;
            int two_halfs = 0;
            do {
                if ((0 <= halfbyte) && (halfbyte <= 9))
                    buf.append((char) ('0' + halfbyte));
                else
                    buf.append((char) ('a' + (halfbyte - 10)));
                halfbyte = data[i] & 0x0F;
            } while(two_halfs++ < 1);
        }
        return buf.toString();
    }

	private static String validServiceID(String username)
    {
        try {  return "s" + SHA1(username).substring(0, 19); }
        catch(NoSuchAlgorithmException e)
            { return ""; }
        catch(UnsupportedEncodingException e)
            { return ""; }
    }

	private static String SHA1(String text)  throws NoSuchAlgorithmException, UnsupportedEncodingException
    {
        MessageDigest md;
        md = MessageDigest.getInstance("SHA-1");
        byte[] sha1hash = new byte[40];
        md.update(text.getBytes("iso-8859-1"), 0, text.length());
        sha1hash = md.digest();
        return convertToHex(sha1hash);
    }


/*
   public static void main(String [ ] args)
    {
      //String sid = "mytest";
      String sid = "DemoApp";

      List<String> filePaths = ImageRepository.getFilePaths(sid);
      ListIterator<String> iter = filePaths.listIterator();

      System.out.println("Images for service `" + sid + "`:");
      while( iter.hasNext() )
      {
       String filePath = iter.next();
       System.out.println("\t" + filePath);
      }
    }
    */
}
