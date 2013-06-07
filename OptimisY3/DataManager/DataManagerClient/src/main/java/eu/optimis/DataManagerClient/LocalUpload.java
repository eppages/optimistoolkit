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

import org.apache.log4j.Logger;
import java.io.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Vector;
import java.net.*;
import com.jcraft.jsch.*;
import java.util.Properties;
import org.w3c.dom.*;
import org.xml.sax.InputSource;
import javax.ws.rs.core.MultivaluedMap;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class LocalUpload extends Thread
{
    private String sid;
    private String imageLocalPath;
    private String status;
    private String infoprov_accountname;
    private String infoprov_password;
    private String infoprov_ipaddress;


    public class ProgressMonitor implements SftpProgressMonitor
    {
	private long initFileSize = 0;
	private LocalUpload parent;
	private long totalLength = 0;
	private int percentTransmitted = 0;

	public ProgressMonitor(LocalUpload p)
         {
	     parent = p;
         }

	public void init(int op, java.lang.String src, java.lang.String dest, long max) 
	{
	     initFileSize = max;
	     System.out.println("STARTING: "+op+" "+src+" -> "+dest+" total: "+max);
             totalLength = 0;
	     percentTransmitted = 0;
             parent.setStatus("progress:0");
	}


	protected final int trackProgress(long filesize, long totalLength, int percentTransmitted)
	{
	    int percent = (int) Math.round(Math.floor((totalLength / (double) filesize) * 100));

	    return percent;
	}

	public boolean count(long bytes)
	{
	    totalLength += bytes;

	    percentTransmitted = (trackProgress(initFileSize, totalLength, percentTransmitted));

            parent.setStatus("progress:" + percentTransmitted);
	    return(true);
	}

	public void end()
	{
	    parent.setStatus("success");
	}
    }

    public LocalUpload(String serviceID, String fileName, String accountName, String password, String ipaddress)
	{
	    infoprov_accountname = accountName;
	    infoprov_password    = password;
	    infoprov_ipaddress   = ipaddress;
	    sid            = serviceID;
	    imageLocalPath = fileName;
	     status = "progress:0.0";
	}

	public synchronized String getStatus()
	{
	  return status;
	}

       public synchronized void setStatus(String st)
        {
	    status = st;
	}

       public static boolean isFileExists(String url, String sid, String ipAddress, String repoPath, String username, String password)
        {
           String SFTPHOST = ipAddress;
           int    SFTPPORT = 22;
           String SFTPUSER = username;
           String SFTPPASS = password;
           String validSID = ServiceUtil.validServiceID(sid);
           String vmMountPath = repoPath + "/" + validSID + "/";
           String[] filePath  = url.split("/");
           String   fileName = filePath[filePath.length-1];
           String SFTPWORKINGDIR = vmMountPath;
           Session     session     = null;
           Channel     channel     = null;
           ChannelSftp channelSftp = null;

         try {
            JSch jsch = new JSch();
            session = jsch.getSession(SFTPUSER,SFTPHOST,SFTPPORT);
            session.setPassword(SFTPPASS);
            java.util.Properties config = new java.util.Properties();
            config.put("StrictHostKeyChecking", "no");
            session.setConfig(config);
            session.connect();
            channel = session.openChannel("sftp");
            channel.connect();
            channelSftp = (ChannelSftp)channel;
            channelSftp.cd(SFTPWORKINGDIR);
            Vector filelist = channelSftp.ls(SFTPWORKINGDIR);
            channelSftp.exit();
            session.disconnect();

            for(int i=0; i < filelist.size(); i++)
            {
               ChannelSftp.LsEntry entry = (ChannelSftp.LsEntry)filelist.get(i);
               String remoteFile = entry.getFilename();
               //System.out.println(" remote = [" + remoteFile + "]  -> filename = [" + fileName + "]");
               if( remoteFile.equals(fileName) ) return true;
            }

        }catch(Exception ex){
            ex.printStackTrace();
        }

        return false;
        }

	public void run()
        {
            System.out.println("In run: local");
	    try
	   {
	       JSch jsch = new JSch();
	       Session session = jsch.getSession(infoprov_accountname, infoprov_ipaddress, 22);
	       session.setConfig("StrictHostKeyChecking", "no");
	       session.setPassword(infoprov_password);

	       session.connect();
	       Channel channel = session.openChannel( "sftp" );
	       channel.connect();
	       ChannelSftp sftpChannel = (ChannelSftp) channel;

               System.out.println("In run: connect");
	       String vmPath = "/home/vmimages/sids/" + sid + "/";

               try {
                   sftpChannel.mkdir(vmPath);
               } catch(SftpException ex)
               {

               }

	       String[] filePath = imageLocalPath.split("/");
	       String fileName = filePath[filePath.length-1];

               System.out.println("In run: try put");
	       sftpChannel.put(imageLocalPath, vmPath, new ProgressMonitor(this));
	       System.out.println("In run: endput");

	       sftpChannel.exit();
	       session.disconnect();

               status = "success";
	   }
	    catch (JSchException e)
		{
		    status = "failure";
		    return;
		}
	    catch (SftpException e)
		{
                    status = "failure";
		    return;
		}
	}
}
