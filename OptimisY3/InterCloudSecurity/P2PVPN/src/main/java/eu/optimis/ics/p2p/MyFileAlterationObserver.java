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

import java.io.File;

import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;
/*
 * @author Ali Sajjad
 */
public class MyFileAlterationObserver{
	
	public static void main(String[] args) throws Exception
	{
		System.out.println("Present Working Directory is : "+System.getProperty("user.dir"));
		File directory = new File(System.getProperty("user.dir")+"/data");
		
		IOFileFilter files = FileFilterUtils.and(
				FileFilterUtils.fileFileFilter(),FileFilterUtils.suffixFileFilter(".txt"));
		
		FileAlterationObserver observer = new FileAlterationObserver(directory, files);
	    observer.initialize();
	    
	    // Add Listener
        MyFileAlterationListener listener = new MyFileAlterationListener();
        observer.addListener(listener);

        FileAlterationMonitor monitor = new FileAlterationMonitor(1000, observer);
        monitor.addObserver(observer);
        
	    monitor.start();
        
	}

}
