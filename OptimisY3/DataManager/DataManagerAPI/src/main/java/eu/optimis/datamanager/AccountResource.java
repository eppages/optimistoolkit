/**
 * OPTIMIS PROJECT - Data Manager RESTful API

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
package eu.optimis.datamanager;

import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;
import java.util.*;
import java.io.*;

@Path("account")
public class AccountResource {
  public void createDFS(String sid, String folder)
  {
      List<String> command = new ArrayList<String>();
      command.add("/usr/local/hadoop/bin/hdfs");
      command.add("dfs");
      command.add("-mkdir");
      command.add( "/services/" + sid + "/" + folder);

     try
     {
      SystemCommandExecutor commandExecutor = new SystemCommandExecutor(command);
      int result = commandExecutor.executeCommand();

      StringBuilder stdout = commandExecutor.getStandardOutputFromCommand();
      StringBuilder stderr = commandExecutor.getStandardErrorFromCommand();
     }
     catch (IOException ioe)
     {
     }
     catch (InterruptedException e)
     {
     }
  }

  public String readKey(String filePath)
  {
	try {
			StringBuffer fileData;
       fileData = new StringBuffer(1000);
	        BufferedReader reader = new BufferedReader(
	                new FileReader(filePath));
	        char[] buf = new char[1024];
	        int numRead=0;
	        while((numRead=reader.read(buf)) != -1){
	            String readData = String.valueOf(buf, 0, numRead);
	            fileData.append(readData);
	            buf = new char[1024];
	        }
	        reader.close();
	     return fileData.toString();
	}
	 catch (Exception ioe)
     {
     }

   return "";
  }

  public void createKey(String sid)
  {
     //ssh-keygen -b 1024 -f key -t dsa -q -P ""
	  List<String> command = new ArrayList<String>();
      command.add("ssh-keygen");
      command.add("-b");
      command.add("1024");
      command.add("-f");
      command.add("/tmp/" + sid + "-key");
      command.add("-t");
      command.add("dsa");
      command.add("-q");
      command.add("-P");
      command.add("\"\"");

     try
     {
      SystemCommandExecutor commandExecutor = new SystemCommandExecutor(command);
      int result = commandExecutor.executeCommand();

      StringBuilder stdout = commandExecutor.getStandardOutputFromCommand();
      StringBuilder stderr = commandExecutor.getStandardErrorFromCommand();
      System.out.println("edo:" + stdout);

     }
     catch (IOException ioe)
     {
      System.out.println("exceptions.");
     }
     catch (InterruptedException e)
     {
      // ignore
      System.out.println("exceptionsinter.");
     }
  }

    @GET
    @Produces(MediaType.TEXT_XML)
    @Path("/create/{sid}")
    public String createUser(@PathParam("sid") String sid)
    {
      createDFS(sid, "vm_images");
      createDFS(sid, "encrypted-volumes");
      createKey(sid);
      String prive = readKey("/tmp/" + sid +"-key");
      String pub   = readKey("/tmp/" + sid +"-key.pub");

      String resp = "<xml>\n" + "<private>\n" + prive + "\n</private>\n<public>\n" + pub + "</public>\n</xml>\n";
      return resp;
    }

}

