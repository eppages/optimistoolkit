/*
 * Copyright (c) 2013 National Technical University of Athens (NTUA)
 *	
 *   							MIT License
 *   
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */


package eu.optimis.ac.test;

import eu.optimis.ac.ACRestClients.ACperServiceConstraintsRestClient;
import eu.optimis.ac.gateway.utils.FileFunctions;
import eu.optimis.ac.smanalyzer.SMAnalyzer;
import eu.optimis.ac.test.remoteTest.GetServerDetails;
import java.io.File;
import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import junit.framework.TestCase;

public class ACGatewayRemoteTest_ManyFiles extends TestCase {

        private static Boolean AutoDeletion = true; //Only If SM is accepted
        
	public void testR(){;}
        
	public void testRemoteACG_ManyFiles() {
            
		String path = "UsedServiceManifests";
                
                String RejectionReport = "";
                
                String resourcesPath = getResourcePath()+File.separator;
                
		ArrayList<String> fileList = FileFunctions.getListOfFiles(resourcesPath+path);
		
		for(int i=0;i<fileList.size();i++)
		{
                    
			String filename = fileList.get(i);
		System.out.println(filename);	
                        String temp[] = filename.split(path);
                        
                        if(temp.length>2)
                        {
                            System.err.println("filename spliting not as wanted, filename is : "+filename);
                            throw new RuntimeException();
                        }//if-lengh>2
                        
                        String relative_filename = temp[1];
                        
			relative_filename = path+relative_filename;
			
                        String serviceManifest = FileFunctions.readFileAsStringFromResources(relative_filename);
                        
                        if(serviceManifest.contains("opt:isFederationAllowed=\"true\""))
                        {
                            System.out.println("isFederationAllowed was true");
                            serviceManifest = serviceManifest.replace("opt:isFederationAllowed=\"true\"", "opt:isFederationAllowed=\"false\"");
                        }//if-true   
                        
                        SMAnalyzer smAnalyzer = new SMAnalyzer(serviceManifest);
                        
			System.out.println((i+1)+"/"+fileList.size()+"=> "+relative_filename+" "+smAnalyzer.serviceId);
			
                        RejectionReport = All_ACGatewayRemoteTest.doTest(serviceManifest,GetServerDetails.Host,RejectionReport);
                        
                        if(RejectionReport.hashCode() != "".hashCode())
                            throw new RuntimeException(RejectionReport+" : "+relative_filename);
                        
                        else if(AutoDeletion)
                            FileFunctions.deletefile(filename);
                    try {                        
                        Thread.sleep(1);
                    } catch (InterruptedException ex) {
                        System.err.println(ex.getMessage());
                    }
                    
		}//for -i
		
	}//testRemoteACG_ManyFiles()
	
        private String getResourcePath()
	{
		String parentpath="";
		File dir = null;
		
		String className = this.getClass().getName();
		
		String temp[]=className.replace("."," ").split(" ");
		int numberOfPackage=temp.length;
		
		className=temp[numberOfPackage-1];
		
		className=className+".class";
		
		try {
				
			dir = new File(new URI(this.getClass().getResource(className).toString()));
			
		} catch (URISyntaxException e) {
					
			System.err.println("URISyntaxException exception "+e.getMessage());
			e.printStackTrace();
					
		}
		
		parentpath = dir.getAbsolutePath();
		
		parentpath=parentpath.replace(className,"");
		
		for(int i=0;i<numberOfPackage-1;i++)
			parentpath=parentpath+"../";
		
		//String AtTargetPath=parentpath;
		
		//--------------------------------
		
		String current_path=parentpath+"../../src/test/";
			
		current_path +="resources";
                
		try {
		
		File currectPath = new File(current_path);
		
		current_path = currectPath.getCanonicalPath();
		
		} catch (IOException e) {
			
			System.err.println("IOException exception "+e.getMessage());
			e.printStackTrace();
					
		}
		
		String AtsrcTestResources=current_path+File.separator;
		
		return AtsrcTestResources;
	}//setPath()
	
}//class
