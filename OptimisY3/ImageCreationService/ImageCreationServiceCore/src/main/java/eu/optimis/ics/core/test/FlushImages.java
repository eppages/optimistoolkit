/* $Id: FlushImages.java 2771 2012-01-05 13:18:15Z rkuebert $ */

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


/**
 * Simple class to flush all images from the image creation service.
 * 
 * @author roland
 *
 */
public class FlushImages extends BaseClient {

	/**
	 * Creates a new instance of this class and
	 * flushes all images.
	 */
	public FlushImages() {
		super.flushImages();
	}
	
	/**
	 * Creates a new instance of FlushImages and flushes
	 * the ImageCreationService's database.
	 * 
	 * @param args command line arguments (ignored)
	 */
	public static void main(String[] args) {
		new FlushImages();
	}

}
