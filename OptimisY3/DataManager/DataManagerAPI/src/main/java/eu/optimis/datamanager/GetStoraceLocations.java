/*
Copyright (C) 2012 National Technical University of Athens

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

package eu.optimis.datamanager;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.commons.io.FileUtils;
import java.io.File;

@Path("GetStorageLocations")

public class GetStoraceLocations {

	/**
	 * @param args
	 */
	
	//called at http://localhost:8080/eu.optimis.datamanager/rest/GetStorageLocations
	
	//file locations.xml must be on the server dir, or define different path 
	//difference with SetPolicy the commons-io-1.3.1.jar
	
	
	@GET
	@Produces(MediaType.TEXT_XML)
	public String returnLocations() {
		
		File file = new File("locations.xml");
		
		try {
			String content = FileUtils.readFileToString(file);
			return content;
		}catch (Exception e){
			e.printStackTrace();
			return "Failed";
		}	
		
	}
	
	
}
