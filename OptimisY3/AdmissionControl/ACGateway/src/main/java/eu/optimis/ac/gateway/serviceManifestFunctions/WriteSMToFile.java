/*
 * Copyright (c) 2013 National Technical University of Athens (NTUA)
 *	
 *   							MIT License
 *   
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */



package eu.optimis.ac.gateway.serviceManifestFunctions;

import eu.optimis.types.xmlbeans.servicemanifest.XmlBeanServiceManifestDocument;
import org.apache.xmlbeans.XmlOptions;
import org.apache.xmlbeans.XmlException;

import java.io.FileWriter;

import java.io.BufferedWriter;
import java.io.File;

public class WriteSMToFile {

	private static void writeToFile(XmlBeanServiceManifestDocument manifest, String fileName,String targetDir) {
        try {
            
            File file = new File(targetDir + File.separator + File.separator + fileName + ".xml");
            FileWriter fstream = new FileWriter(file);
            BufferedWriter out = new BufferedWriter(fstream);
            out.write(manifest.xmlText(new XmlOptions().setSavePrettyPrint()));
            
            //System.out.println(fileName + " was written to " + file.getAbsolutePath());
            
            //Close the output stream
            out.close();
        } catch (Exception e) {//Catch exception if any
            System.err.println("Error: " + e.getMessage());
        }
    }//writeToFile(XmlBeanServiceManifestDocument manifest, String fileName)
	
	private static XmlBeanServiceManifestDocument manifestConverter(String manifest)
	{
		XmlBeanServiceManifestDocument parsedManifest = null;
		try {
            parsedManifest = XmlBeanServiceManifestDocument.Factory.parse(manifest);
            
        } catch (XmlException e) {
            
        	System.out.println("XmlException "+e.getMessage()+" Ignored");
            //e.printStackTrace(); 
        }
		
		return parsedManifest;
	}//manifestConverter()
	
	public static void writeToFile(String manifest,String filename,String targetDir)
	{
		writeToFile(manifestConverter(manifest),filename,targetDir);
	}//writeToFile()
	
}//class
