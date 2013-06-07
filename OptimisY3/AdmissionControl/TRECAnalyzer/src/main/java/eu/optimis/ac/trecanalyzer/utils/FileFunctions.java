/*
 * Copyright (c) 2013 National Technical University of Athens (NTUA)
 *	
 *   							MIT License
 *   
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */


package eu.optimis.ac.trecanalyzer.utils;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import org.apache.log4j.Logger;

public class FileFunctions {

	public static void FileWrite(String filename,String msg,Logger log)
	  {
		  try{
		  // Create file 
		  FileWriter fstream = new FileWriter(filename,false);
		  BufferedWriter out = new BufferedWriter(fstream);
		  out.write(msg);
		  //Close the output stream
		  out.close();
		  }catch (Exception e){//Catch exception if any
		  log.error("Error: " + e.getMessage());
		  }
		  
	}//FileWrite()
        
        public static Boolean CreateFile(String filename,Logger log)
        {
            File f;
                
            f=new File(filename);
            
            if(f.exists()) return false;
            
            try {            
                f.createNewFile();
            } catch (IOException ex) {
            log.error(ex.getMessage());
            }
                
            log.info("File Creation : "+filename +" was "+f.exists());
            
            return f.exists();
        }//CreateFile()
	
	public static Boolean FileExists(String filepath,String filename)
    {
        File file=new File(filepath+filename);
        
        boolean exists = file.exists();
        
        return exists;
   }//FileExists(String filepath,String filename)
	
	public static void fileAppender(String filename,String msg,Logger log)
	{
		try {
		    BufferedWriter out = new BufferedWriter(new FileWriter(filename, true));
		    out.write(msg);
		    out.close();
		} catch (IOException e) {
			
			log.error(e.getMessage());
		}
	}//fileAppender()
	
	public static String readfile(String filename,
			String replaceEachLineStr,String newEachLineStr,
			String beforeEachLineStr,String afterEachLineStr) 
	{
		String fileContent = "";

	    File file = new File(filename);
	    FileInputStream fis = null;
	    BufferedInputStream bis = null;
	    DataInputStream dis = null;

	    try {
	      fis = new FileInputStream(file);

	      // Here BufferedInputStream is added for fast reading.
	      bis = new BufferedInputStream(fis);
	      dis = new DataInputStream(bis);

	      // dis.available() returns 0 if the file does not have more lines.
	      while (dis.available() != 0) {

	      // this statement reads the line from the file and print it to
	        // the console.
	        //System.out.println(dis.readLine());
	    	fileContent+="<h5>"+dis.readLine().replace(" ","&nbsp&nbsp&nbsp&nbsp&nbsp")+"</h5>";  
	      }

	      // dispose all the resources after using them.
	      fis.close();
	      bis.close();
	      dis.close();

	    } catch (FileNotFoundException e) {
	      e.printStackTrace();
	    } catch (IOException e) {
	      e.printStackTrace();
	    }
	    
	    return fileContent;
	  }//readfile()
	
	public static String readFileAsStringWithPath(String filePath,Logger log) {
		
		StringBuffer buf = new StringBuffer();		
		BufferedInputStream bin = null;
 
		try
		{ 
			
			bin = new BufferedInputStream(new FileInputStream(filePath));
		
			byte[] contents = new byte[1024];
 
			int bytesRead=0;
			String strFileContents;
 
			while( (bytesRead = bin.read(contents)) != -1){
 
				strFileContents = new String(contents, 0, bytesRead);
				//System.out.print(strFileContents);
				buf.append(strFileContents);
			} 
		}
		catch(FileNotFoundException fnfe)
		{
			//System.out.println("File not found" + fnfe);
			log.error("There was a FileNotFoundException: ", fnfe);
		}
		catch(IOException ioe)
		{
			//System.out.println("Exception while reading the file " + ioe);	
			log.error("There was an IOException: ", ioe);
		}
		finally
		{
			//close the BufferedInputStream using close method
			try {				
				if (bin != null)
					bin.close();
			} catch(IOException ioe) {
				//System.out.println("Error while closing the stream :" + ioe);
				log.error("There was an IOException: ", ioe);
			} 
		}
		
		return buf.toString();
		
	}//readFileAsStringWithPath()
        
        public static String readFileAsStringWithPath(String filePath) {
		
		StringBuffer buf = new StringBuffer();		
		BufferedInputStream bin = null;
 
		try
		{ 
			
			bin = new BufferedInputStream(new FileInputStream(filePath));
		
			byte[] contents = new byte[1024];
 
			int bytesRead=0;
			String strFileContents;
 
			while( (bytesRead = bin.read(contents)) != -1){
 
				strFileContents = new String(contents, 0, bytesRead);
				//System.out.print(strFileContents);
				buf.append(strFileContents);
			} 
		}
		catch(FileNotFoundException fnfe)
		{
			//System.out.println("File not found" + fnfe);
			//log.error("There was a FileNotFoundException: ", fnfe);
		}
		catch(IOException ioe)
		{
			//System.out.println("Exception while reading the file " + ioe);	
			//log.error("There was an IOException: ", ioe);
		}
		finally
		{
			//close the BufferedInputStream using close method
			try {				
				if (bin != null)
					bin.close();
			} catch(IOException ioe) {
				//System.out.println("Error while closing the stream :" + ioe);
				//log.error("There was an IOException: ", ioe);
			} 
		}
		
		return buf.toString();
		
	}//readFileAsStringWithPath()
        
        public static String readFileLineByLineWithResetString(String filePath,String resetString,String lineSeperator) 
        {
            String strFileContents = "";
            
                try{
                     
                    FileInputStream fstream = new FileInputStream(filePath);
  
                    DataInputStream in = new DataInputStream(fstream);
                    BufferedReader br = new BufferedReader(new InputStreamReader(in));
                    
                    String strLine;
  
                    while ((strLine = br.readLine()) != null)   {
                    
                    if(strLine.contains(resetString))strFileContents = "";
                    
                    strFileContents+=strLine+lineSeperator;        
                    
                    }//while
  
                    in.close();
                    }catch (Exception e){//Catch exception if any
                    //System.err.println("Error: " + e.getMessage());
                    }
                return strFileContents;
        }//readFileLineByLineWithResetString()
        
	public static String readFileAsStringFromResources(String fileName,Logger log) {
		
                String str = null;
		try
		{ 
			str = readFileAsStringFromResourcesWithoutLogging(fileName);
		}
		catch(FileNotFoundException fnfe)
		{       
                        str = null;
                        log.error(fnfe.getMessage());
		}
		catch(IOException ioe)
		{
                        str = null;
                        log.error(ioe.getMessage());
		}
                
                return str;
	}//readFileAsStringFromResources(String fileName,Logger log)
	
        public static String readFileAsStringFromResources(String fileName)
        {
                String str = null;
                
		try
		{ 
			str = readFileAsStringFromResourcesWithoutLogging(fileName);
		}
		catch(FileNotFoundException fnfe)
		{       
                        return fnfe.getMessage();
		}
		catch(IOException ioe)
		{
                        return ioe.getMessage();
		}
                
                return str;
	}//readFileAsStringFromResources(String fileName,Logger log)
	
	
        public static String readFileAsStringFromResourcesWithoutLogging(String fileName)
        throws FileNotFoundException,IOException{
		
		StringBuffer buf = new StringBuffer();		
		BufferedInputStream bin = null;
 
		try
		{ 
			
			bin = new BufferedInputStream(FileFunctions.class.getClassLoader().getResourceAsStream(fileName));
			
			byte[] contents = new byte[1024];
 
			int bytesRead=0;
			String strFileContents;
 
			while( (bytesRead = bin.read(contents)) != -1){
 
				strFileContents = new String(contents, 0, bytesRead);
				
				buf.append(strFileContents);
			} 
		}
		catch(FileNotFoundException fnfe)
		{
                        throw new FileNotFoundException(fnfe.getMessage());
		}
		catch(IOException ioe)
		{
                        throw new FileNotFoundException(ioe.getMessage());
		}
		finally
		{			
			try {				
				if (bin != null)
					bin.close();
			} catch(IOException ioe) {
				
                                throw new FileNotFoundException(ioe.getMessage());
			} 
		}
		
		return buf.toString();
		
	}//readFileAsStringFromResources(String fileName)
        
	public static void copyfile(String srFile, String dtFile,Logger log){
		  try{
		  File f1 = new File(srFile);
		  File f2 = new File(dtFile);
		  InputStream in = new FileInputStream(f1);

		  //For Overwrite the file.
		  OutputStream out = new FileOutputStream(f2);

		  byte[] buf = new byte[1024];
		  int len;
		  while ((len = in.read(buf)) > 0){
		  out.write(buf, 0, len);
		  }
		  
		  in.close();
		  out.close();
		  
		  //System.out.println("File copied.");
		  
		  }
		  catch(FileNotFoundException ex){
			  
			  log.error(ex.getMessage() + " in the specified directory.");
		  
		  }
		  catch(IOException e){
			  
			  log.error(e.getMessage());  
		  }
	}//copyfile()

	
	public static int deleteFiles_containingString(String path,String str,Logger log)
	{
		File folder = new File(path);
		File[] listOfFiles = folder.listFiles();
		if (listOfFiles == null)
		{	
			log.info("Folder Emptry");
			return 0;
		}
		
		int DeletedFileCounter=0;
		String filename="";
		
		for (File tmp_file : listOfFiles) {
			if ( (tmp_file.isFile()) && 
				(tmp_file.getName().contains(str) ) )
			{
					filename = tmp_file.getName();
					tmp_file.delete();
					DeletedFileCounter++;
					log.info(filename+" deleted");
			}//if
		}//for
		
		return DeletedFileCounter;
		
	}//deleteFiles_containingString()
	
        public static boolean deletefile(String file)
        {
		File f1 = new File(file);
		return f1.delete();
                
        }//deletefile(String file)
        
	public static boolean deletefile(String file,Logger log)
	{
		boolean success = deletefile(file);
	  
		if (!success)
			log.error(file+" Deletion failed.");
		else
			log.info(file+" File deleted.");
	   
		return success;
			
	}//deletefile(String file,Logger log)
	
        public static void deleteFolder(String str)
        {
            String msg = deleteDir(str);
            
            if(msg.contains("ERROR"))
                throw new RuntimeException(msg);
        }//deleteFolder(String str)
        
        public static String deleteDir(String str) 
        {
            
            String result = clearDir(str);
            if(result.contains("ERROR"))
                return result;
            
            File dir = new File(str);
            
            if(!dir.exists())return "OK";
            
            boolean success = dir.delete();
            if(!success)
                    return "ERROR "+str+" didn't delete it";    
            
            return "OK";
    }//deleteDir()
        
    public static String clearDir(String str)
    {
        File dir = new File(str);
        
        if(!dir.exists())return str+" OK";
            
            String[] children = dir.list();
            for (int i=0; i<children.length; i++) {
                
                String filename = str+File.separator+children[i];
                
                boolean success = deletefile(filename);
                if(!success)
                    return "ERROR "+filename+" didn't delete it";
            }
        
       return "OK";
       
    }//clearDir()

    public static ArrayList<String> getListOfFiles(String path)
	{	
                ArrayList<String> fileList = new ArrayList<String>();
                
		File Folder = new File(path);
		File[] listOfFiles = Folder.listFiles();
		
		if (listOfFiles == null) {
                    return fileList;}
		
		for (File tmp_file : listOfFiles) 
		{
			
			if(!tmp_file.isFile())continue;
			
			fileList.add(tmp_file.getName());	
			
		}//for	
		
                return fileList;
                
	}//getListOfFiles()
	    
}//Class
