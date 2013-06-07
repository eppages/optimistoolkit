/* $Id: ProcessResult.java 4259 2012-02-29 18:50:54Z rkuebert $ */

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
package eu.optimis.ics.core.shell;

/**
 * Stores the results of executing a command with the {@link ShellUtil} class.
 * 
 * @author roland
 *
 */
public class ProcessResult {

	private String standardOut;
	private String standardError;
	private int exitCode;
	
	public ProcessResult(String standardOut, String standardError, int exitCode) {
		this.standardOut = standardOut;
		this.standardError = standardError;
		this.exitCode = exitCode;
	}

	public String getStandardOut() {
		return standardOut;
	}

	public String getStandardError() {
		return standardError;
	}

	public int getExitCode() {
		return exitCode;
	}
}
