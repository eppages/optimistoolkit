/* $Id$ */

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

import eu.optimis.ics.core.image.Image;

public class UnzipTest extends BaseClient {

	public UnzipTest(String zipFile, String imageId) {
		
		Image targetImage = null;
		try {
			targetImage = super.getImage(imageId);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(0);
		}
		
		System.out.println("Client is putting file '" + zipFile + "' to image " + imageId);
		putZipAndWait(zipFile, targetImage);
	}

	public static void main(String[] args) {
		if (args.length != 2) {
			usage();
			System.exit(0);
		}
		
		String zipFile = args[0];
		String imageId = args[1];
		new UnzipTest(zipFile, imageId);
	}

	private static void usage() {
		System.err.println("Usage:\n\tjava UnzipTest file imageId");
	}

}
