/* $Id: UnzipFile.java 5759 2012-04-04 12:47:40Z rkuebert $ */

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

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class UnzipFile {

	public void unzipFile(String zipFileName, String destination)
			throws IOException {
		FileInputStream fis = new FileInputStream(zipFileName);
		ZipInputStream zin = new ZipInputStream(new BufferedInputStream(fis));
		ZipEntry entry;
		File destinationDirectory = new File(destination);
		System.out.println("Destination: " + destinationDirectory.getParent());

		while ((entry = zin.getNextEntry()) != null) {
			System.out.println("Extracting: " + entry + " ("
					+ (entry.isDirectory() ? " d " : " f ") + ")");

			if (entry.isDirectory()) {
				System.out.println("Directory, not doing anything now");
				File targetDirectory = new File(destinationDirectory, entry.getName());
				targetDirectory.mkdir();
			} else {
				// extract data
				// open output streams
				int BUFFER = 2048;

				File destinationFile = new File(destinationDirectory,
						entry.getName());
				System.out.println("Destination: "
						+ destinationFile.getParent());
				boolean madeParents = destinationFile.getParentFile().mkdirs();
				System.out.println("made parents: " + madeParents);
				FileOutputStream fos = new FileOutputStream(destinationFile);
				BufferedOutputStream dest = new BufferedOutputStream(fos,
						BUFFER);

				int count;
				byte data[] = new byte[BUFFER];

				dest = new BufferedOutputStream(fos, BUFFER);
				while ((count = zin.read(data, 0, BUFFER)) != -1) {
					dest.write(data, 0, count);
				}
				dest.flush();
				dest.close();
			}
		}

		zin.close();
		fis.close();
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		if (args.length != 2) {
			System.err.println("usage: java UnzipFile ZIPFILE DESTINATION");
			System.exit(0);
		}

		String zipFileName = args[0];
		String destination = args[1];

		UnzipFile unzipper = new UnzipFile();
		try {
			unzipper.unzipFile(zipFileName, destination);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
