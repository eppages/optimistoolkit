/* $Id: PutFile.java 4962 2012-03-15 15:08:04Z rkuebert $ */

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


public class SetPermissions extends BaseClient {

	public SetPermissions(String imageId, String file, String permissions) {
		
		System.out.println("Client is setting permissions on file '" + file + "' in image " + imageId + " to " + permissions);
		setPermissions(imageId, file, permissions);
	}

	public static void main(String[] args) {
		if (args.length != 3) {
			usage();
			System.exit(0);
		}
		
		String imageId = args[0];
		String file = args[1];
		String permissions = args[2];
		
		new SetPermissions(imageId, file, permissions);
	}

	private static void usage() {
		System.err.println("Usage:\n\tjava SetPermission file permissions imageId");
	}

}
