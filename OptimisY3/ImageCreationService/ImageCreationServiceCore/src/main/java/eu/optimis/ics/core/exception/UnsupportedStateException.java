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
package eu.optimis.ics.core.exception;

public class UnsupportedStateException extends Exception {
	/**
	 * Generated serial version UID.
	 */
	private static final long serialVersionUID = -6488571193867885005L;
	
	/**
	 * Creates a new UnsupportedStateException with the given message.
	 * 
	 * @param message
	 */
	public UnsupportedStateException(String message) {
		super(message);
	}

}
