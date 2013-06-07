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
import java.util.Date;

import org.apache.commons.io.monitor.FileAlterationListener;
import org.apache.commons.io.monitor.FileAlterationObserver;
/*
 * @author Ali Sajjad
 */
public class MyFileAlterationListener implements FileAlterationListener {

	public void onDirectoryChange(File directory) {
	
	}

	public void onDirectoryCreate(File directory) {

	}

	public void onDirectoryDelete(File directory) {

	}

	public void onFileChange(File file) {
		// TODO Auto-generated method stub
		System.out.println("onFileChange : "+new Date(System.currentTimeMillis()).toString());
		
	}

	public void onFileCreate(File file) {
		
	}

	
	public void onFileDelete(File file) {
		
	}

	
	public void onStart(FileAlterationObserver observer) {
		
	}

	
	public void onStop(FileAlterationObserver observer) {
		
	}

}