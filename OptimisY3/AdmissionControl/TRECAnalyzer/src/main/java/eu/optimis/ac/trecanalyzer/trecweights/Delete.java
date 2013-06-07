/*
 * Copyright (c) 2013 National Technical University of Athens (NTUA)
 *	
 *   							MIT License
 *   
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package eu.optimis.ac.trecanalyzer.trecweights;

import java.io.File;
import java.io.IOException;

public class Delete {
    
    public static void deleteOldFiles(String gamsPath, String AllocationPath) throws IOException
	{	
		File GamsFolder = new File(gamsPath);
		File[] listOfFiles = GamsFolder.listFiles();
		
		if (listOfFiles == null) return;
		
		// delete and cost.xml,ids.xml and all *.csv files except _weight.csv
		for (File tmp_file : listOfFiles) 
		{
			
			if(!tmp_file.isFile())continue;
			
			if (tmp_file.getName().contains("_weight.csv"))
				continue;
			if (tmp_file.getName().contains("__constraint.csv"))
				continue;
                        
			if (tmp_file.getName().contains(".csv"))
				tmp_file.delete();
			
		}//for
		
		File AllocationFolder = new File(AllocationPath);
		File[] listofFiles = AllocationFolder.listFiles();
		
		if (listofFiles == null) return;
		
		// delete and cost.xml,ids.xml and all *.csv files except _weight.csv
		for (File tmp_file : listofFiles) 
		{
			
			if(!tmp_file.isFile())continue;
			
			if (tmp_file.getName().contains("cost_"))//cost_i.xml
				tmp_file.delete();
			
			if (tmp_file.getName().contains("ServicesInfo.xml"))
				tmp_file.delete();
			
			if (tmp_file.getName().contains(".csv"))
				tmp_file.delete();
			
		}//for
		
	}//deleteOldFiles
}//class
