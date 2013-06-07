/*
 *  Copyright 2011-2013 Barcelona Supercomputing Center (www.bsc.es)
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

package es.bsc.servicess.ide.model;

import es.bsc.servicess.ide.TitlesAndConstants;

public class OrchestrationClass {
	
	private String className;
	private String externalLocation;
	private String libraryLocation;
	private String type;
	
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public OrchestrationClass(String className, String externalLocation,
			String libraryLocation, String type) {
		this.className = className;
		this.externalLocation = externalLocation;
		this.libraryLocation = libraryLocation;
		this.type = type;
	}
	
	public OrchestrationClass(String className, String type){
		this.className = className;
		this.externalLocation = null;
		this.libraryLocation = null;
		this.type = type;
	}
	
	public OrchestrationClass(String className, String externalLocation,
			String libraryLocation){
		this.className = className;
		this.externalLocation = externalLocation;
		this.libraryLocation = libraryLocation;
		this.type = TitlesAndConstants.EXTERNAL_CLASS;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public String getExternalLocation() {
		return externalLocation;
	}

	public void setExternalLocation(String externalLocation) {
		this.externalLocation = externalLocation;
	}

	public String getLibraryLocation() {
		return libraryLocation;
	}

	public void setLibraryLocation(String libraryLocation) {
		this.libraryLocation = libraryLocation;
	}
	
	
}
