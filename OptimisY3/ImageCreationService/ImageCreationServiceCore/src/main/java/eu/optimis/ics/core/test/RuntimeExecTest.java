/* $Id: RuntimeExecTest.java 4260 2012-02-29 18:51:44Z rkuebert $ */

/*
   Copyright 2012 University of Stuttgart

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
package eu.optimis.ics.core.test;

import java.io.File;

import eu.optimis.ics.core.shell.ProcessResult;
import eu.optimis.ics.core.shell.ShellUtil;

public class RuntimeExecTest {
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
//		String imageFile = "/home/roland/Desktop/centos5.img";
		
		if (args.length != 1) {
			System.out.println("Please specify an image name");
			System.exit(0);
		}
		
		String imageFile = args[0];
		
		String mountDirectory = "/mnt";
		String targetDirectoryName = "optimis";
		File sourceFile = new File("/home/roland/Desktop/foo.xcf");
		ProcessResult result;
		result = ShellUtil.executeShellCommand("sudo kpartx -l " + imageFile + "| grep p2 | awk '{print $1}'");
		
		if (result.getExitCode() != 0) {
			System.err.println("Process exited abnormally (exit code " + result.getExitCode() + ")");
			System.err.println("Output:\n" + result.getStandardError());
			System.exit(0);
		} 

		String loopPartition = result.getStandardOut();
		
		if (loopPartition.length() == 0) {
			System.out.println("Could not determine loop partition - maybe the image file does not exist?");
			System.exit(0);
		} else {
			System.out.printf("Loop partition: '%s'\n", loopPartition);
		}
		
		
		result = ShellUtil.executeShellCommand("sudo kpartx -a " + imageFile);
		if (result.getExitCode() != 0) {
			System.err.println("Process exited abnormally (exit code " + result.getExitCode() + ")");
			System.err.println("Output:\n" + result.getStandardError());
			System.exit(0);
		} 
		
		// TODO Maybe use a random dir instead of /mnt
		ShellUtil.executeShellCommand("sudo mount /dev/mapper/" + loopPartition + " " + mountDirectory);

		File targetDirectory = new File(mountDirectory + "/" + targetDirectoryName); 
		if (targetDirectory.exists() != true) {
			System.out.println("Directory not existing, trying to create");

			
			result = ShellUtil.executeShellCommand("sudo mkdir " + mountDirectory + "/" + targetDirectoryName);
			if (result.getExitCode() != 0) {
				System.err.println("Process exited abnormally (exit code " + result.getExitCode() + ")");
				System.err.println("Output:\n" + result.getStandardError());
				System.exit(0);
			} 

		}
		// Directory exists now, or we have exited already
		System.out.println("Copying file");
		result = ShellUtil.executeShellCommand("sudo cp " + sourceFile + " " + mountDirectory + "/" + targetDirectoryName);
		if (result.getExitCode() != 0) {
			System.err.println("Process exited abnormally (exit code " + result.getExitCode() + ")");
			System.err.println("Output:\n" + result.getStandardError());
			System.exit(0);
		} 
		
		System.out.println("Unmounting...");
		ShellUtil.executeShellCommand("sudo umount " + mountDirectory);
		
		System.out.println("Running kpartx -d");
		ShellUtil.executeShellCommand("sudo kpartx -d " + imageFile);
	}

}
