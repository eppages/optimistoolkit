/*
 *  Copyright 2013 University of Leeds
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package eu.optimis.serviceproviderriskassessmenttool.core.historicaldatabase.dao.populate;

public class PopulateDSAHPProviders {

	final static int numEntries	= 200;

	public static void main(String[] args) {	

		try {		
			CreateDSAHPData newAdditions = new CreateDSAHPData();
                        System.out.println("data");
			boolean successful = newAdditions.addDSAHPProviderDataToDatabase(numEntries);
			
			System.out.print("Fake provider data added successfully: " + successful);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
