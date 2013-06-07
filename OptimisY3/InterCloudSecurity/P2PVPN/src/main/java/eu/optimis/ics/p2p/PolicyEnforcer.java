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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
/*
 * @author Ali Sajjad
 */
public class PolicyEnforcer {

	public static void enforceSPDPolicy(String filePath) throws IOException, InterruptedException {
		
		List<String> commands = new ArrayList<String>();
	    //commands.add("sudo");		// will run only if you modify the visudo conf
	    commands.add("setkey");
	    commands.add("-f");
	    commands.add(filePath);
	    
		ProcessBuilder pb = new ProcessBuilder(commands); 
		pb.redirectErrorStream(true);
		
		Process process = pb.start();
		
		InputStream is = process.getInputStream();
		InputStreamReader isr = new InputStreamReader(is);
		BufferedReader br = new BufferedReader(isr);
		String line;
		while ((line = br.readLine()) != null) {
		        System.out.println(line);
		}
		int exitValue = process.waitFor();
		System.out.println("\nPolicy In Force : Exit Value: "+exitValue);
	}
	
	public static int removeSPDPolicy() throws IOException, InterruptedException
	{
		List<String> commands = new ArrayList<String>();
	    //commands.add("sudo");		// will run only if you modify the visudo conf
	    commands.add("setkey");
	    commands.add("-FP");
	    
		ProcessBuilder pb = new ProcessBuilder(commands); 
		pb.redirectErrorStream(true);
		
		Process process = pb.start();
		
		InputStream is = process.getInputStream();
		InputStreamReader isr = new InputStreamReader(is);
		BufferedReader br = new BufferedReader(isr);
		String line;
		while ((line = br.readLine()) != null) {
		        System.out.println(line);
		}
		int exitValue = process.waitFor();
		System.out.println("\nExit Value: "+exitValue);
		return exitValue;
	}

}
