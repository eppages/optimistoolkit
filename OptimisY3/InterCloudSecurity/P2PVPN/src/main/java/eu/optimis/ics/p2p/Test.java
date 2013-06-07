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

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

/**
 * @author Ali Sajjad
 *
 */
public class Test 
{
	//private int i = 0;
	
	public static void main(String[] args) throws InterruptedException 
	{
		//final Test testClass = new Test();
		
		Properties prop = new Properties();
		 
    	try {
			prop.load(new FileInputStream("/home/ali/workspace/P2PVPN/vpnpeer.conf"));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
 
            System.out.println(prop.getProperty("superPeerIP"));
    		System.out.println(prop.getProperty("superPeerPort"));
    	
    		//System.out.println(ExternalIP.show());
    		/*
    		if(! new File("/etc/racoon/certs/").isDirectory())
    		{
    			System.out.println("Creating the certificate directory");
    			System.out.println(new File("/etc/racoon/certs/").mkdir());
    		}
    		
    		*/
    		
    	/*	
		Runtime.getRuntime().addShutdownHook(new Thread() {
		    public void run() 
		    { 
		       System.out.println("Shutting down with final value = "+testClass.i);
		    }
		 });
		
		String test = "/mnt/sdcard/Videos/Videoname";
		
		System.out.println(test.substring(0, test.lastIndexOf("/")+1));
		while(true)
		{
			System.out.println("Hello: "+testClass.i);
			testClass.i++;
			Thread.sleep(1000);
		}
		*/
	}
}
