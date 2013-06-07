/* $Id: ImageCreateAndExtractZip.java 4959 2012-03-15 14:00:54Z rkuebert $ */

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

public class ImageCreateAndExtractZip extends BaseClient {

	public ImageCreateAndExtractZip() {
		System.out.printf("Image service contains %d images\n", getNumImages());

		Image image = createCoreImageAndWait();
		
		System.out.println("Client is putting a file");
		putZipAndWait("/home/roland/test.zip", image);
		
		System.out.printf("Image service now contains %d images\n", getNumImages());
	}

	public static void main(String[] args) {
		new ImageCreateAndExtractZip();
	}
	
}