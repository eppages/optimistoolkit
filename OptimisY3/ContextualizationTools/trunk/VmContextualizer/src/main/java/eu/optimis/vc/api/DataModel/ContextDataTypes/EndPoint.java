/**
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
package eu.optimis.vc.api.DataModel.ContextDataTypes;

/**
 * Class for storing the attributes of service end point for use within a VM.
 * 
 * @author Django Armstrong (ULeeds)
 * @version 0.0.2
 */
public class EndPoint {

	private String name;
	private String uri;

	/**
	 * Default constructor.
	 * 
	 * @param uri
	 *            The URI that the endpoint points to.
	 */
	public EndPoint(String name, String uri) {
		this.name = name;
		this.uri = uri;
	}

	/**
	 * @return the uri
	 */
	public String getUri() {
		return uri;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @param uri
	 *            the uri to set
	 */
	public void setUri(String uri) {
		this.uri = uri;
	}

}
