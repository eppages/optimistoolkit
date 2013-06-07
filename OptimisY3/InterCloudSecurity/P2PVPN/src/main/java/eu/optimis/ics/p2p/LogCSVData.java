/*
 * Copyright (c) 2010-2013 British Telecom and City University London
 *
 * This file is part of P2PVPN component of the WP 5.4
 * (Inter-Cloud Security) of the EU OPTIMIS project.
 *
 * P2PVPN can be used under the terms of the SHARED SOURCE LICENSE
 * FOR NONCOMMERCIAL USE. 
 *
 * You should have received a copy of the SHARED SOURCE LICENSE FOR
 * NONCOMMERCIAL USE in the project's root directory. If not, please contact the
 * author at ali.sajjad@bt.com
 *
 * Author: Ali Sajjad
 *
 */
package eu.optimis.ics.p2p;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import au.com.bytecode.opencsv.CSVWriter;
/**
 * @author ali
 *
 */
public class LogCSVData 
{
	public String csvFile = "";
	
	public LogCSVData(String filePath)
	{
		this.csvFile = filePath;
	}

	public static void main(String[] args) throws IOException, InterruptedException {
		
		LogCSVData logCSV = new LogCSVData("/home/ali/SATURN_LIN/Metis/data/peerlist.csv");
		
		System.out.println("Creating the log in : "+logCSV.csvFile);
		
	    // feed in your array (or convert your data to an array)
		ArrayList<String[]> arr = new ArrayList<String[]>();
		//arr.add("name|tags,source".split(","));
		//arr.add("Cloud,IP Address".split(","));
		arr.add(",".split(","));
		arr.add("amazon,10.1.1.1".split(","));
		arr.add("rackspace,192.168.1.1".split(","));
		arr.add("amazon,10.1.1.2".split(","));
		arr.add("rackspace,192.168.1.2".split(","));
		arr.add("google,8.8.8.1".split(","));
		arr.add("amazon,10.1.1.3".split(","));
		arr.add("rackspace,192.168.1.3".split(","));
		arr.add("google,8.8.8.2".split(","));
		arr.add("google,8.8.8.3".split(","));
		arr.add("amazon,10.1.1.4".split(","));
		arr.add("google,8.8.8.4".split(","));
		arr.add("rackspace,192.168.1.4".split(","));
		arr.add("microsoft,1.1.1.1".split(","));
		arr.add("google,8.8.8.5".split(","));
		arr.add("amazon,10.1.1.5".split(","));
		arr.add("rackspace,192.168.1.5".split(","));
		arr.add("google,8.8.8.6".split(","));
		arr.add("google,8.8.8.7".split(","));
		arr.add("amazon,10.1.1.6".split(","));
		arr.add("amazon,10.1.1.7".split(","));
		
	    for (String[] strings : arr) {
	    	
	    	CSVWriter writer = new CSVWriter(new FileWriter(logCSV.csvFile, true), ',', '"', "\n");
			writer.writeNext(strings);
			writer.flush();
			writer.close();
			//Thread.sleep(1000);
		}  
	    System.out.println("Created the Log");
	}
	
	public void recordData(String[] data)
	{
		try 
		{
			CSVWriter writer = new CSVWriter(new FileWriter(csvFile, true));
			writer.writeNext(data);
			writer.flush();
			writer.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
