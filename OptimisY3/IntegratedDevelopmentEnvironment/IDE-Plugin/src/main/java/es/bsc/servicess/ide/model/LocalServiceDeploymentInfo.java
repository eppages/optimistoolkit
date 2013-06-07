/*
 *  Copyright 2011-2012 Barcelona Supercomputing Center (www.bsc.es)
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

import org.eclipse.jdt.core.IJavaProject;

public class LocalServiceDeploymentInfo {
	private IJavaProject project;
	private String serverLocation;
	private String coreFolder;

	public LocalServiceDeploymentInfo() {

	}

	public LocalServiceDeploymentInfo(IJavaProject project,
			String serverLocation, String coreFolder) {
		super();
		this.project = project;
		this.serverLocation = serverLocation;
		this.coreFolder = coreFolder;
	}

	public IJavaProject getProject() {
		return project;
	}

	public void setProject(IJavaProject project) {
		this.project = project;
	}

	public String getServerLocation() {
		return serverLocation;
	}

	public void setServerLocation(String serverLocation) {
		this.serverLocation = serverLocation;
	}

	public String getCoreFolder() {
		return coreFolder;
	}

	public void setCoreFolder(String coreFolder) {
		this.coreFolder = coreFolder;
	}

}
