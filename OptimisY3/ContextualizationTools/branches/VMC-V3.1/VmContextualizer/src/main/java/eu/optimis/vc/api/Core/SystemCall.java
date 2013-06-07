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
package eu.optimis.vc.api.Core;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
//import java.util.Map;

import org.apache.log4j.Logger;

/**
 * Wrapper class for invoking OS level system calls for the purpose of calling
 * external scripts and programs.
 * 
 * @author Django Armstrong (ULeeds)
 * @version 0.0.3
 */
public class SystemCall {

	private String commandName;
	private ArrayList<String> output;
	private int returnValue;
	private String workingDirectory;

	protected final static Logger log = Logger.getLogger(SystemCall.class);

	/**
	 * Initialises an instance of the SystemCall object.
	 */
	public SystemCall(String workingDirectory) {
		this.workingDirectory = workingDirectory;
		output = new ArrayList<String>(50);
		returnValue = -1;
	}

	/**
	 * Run a command via a system call. The return value and output from the
	 * command are accessible via: {@link SystemCall#getOutput()} and
	 * {@link SystemCall#getReturnValue()}.
	 * 
	 * @param commandName
	 *            The program name to execute.
	 * @param arguments
	 *            The argument list to pass to the program.
	 * @throws SystemCallException
	 *             Provides a mechanism to propagate all exception to VMC core.
	 */
	public void runCommand(String commandName, ArrayList<String> arguments)
			throws SystemCallException {

		ArrayList<String> command = new ArrayList<String>();
		String commandString = commandName;

		command.add(commandName);

		for (int i = 0; i < arguments.size(); i++) {
			command.add(arguments.get(i));
			commandString = commandString + " " + arguments.get(i);
		}

		// Run the command...
		log.info("Runnning external command: " + commandString);
		execute(command);
		log.debug("Return value is: " + returnValue);
	}

	/**
	 * Execute using {@link ProcessBuilder} a command using the environment of
	 * the JVM.
	 * 
	 * @param command
	 *            The command to execute.
	 * @throws SystemCallException
	 *             Provides a mechanism to propagate all exception to VMC core.
	 */
	private void execute(List<String> command) throws SystemCallException {
		ProcessBuilder pb = new ProcessBuilder(command);
		// Map<String, String> env = pb.environment();
		// log.debug("Environment variables are:\n" + env.toString());
		// File dir = new File(System.getProperty("user.dir"));
		File dir = new File(workingDirectory);
		pb.directory(dir);
		pb.redirectErrorStream(true);

		Process p = null;
		try {
			p = pb.start();
		} catch (IOException e) {
			log.error("Error!", e);
			try {
				Thread.sleep(250);
			} catch (InterruptedException e1) {
				// Do nothing...
			}
			returnValue = -2;
			throw new SystemCallException("Failed to start process!", e);
		}

		String line = null;
		InputStream stdout = p.getInputStream();
		BufferedReader reader = new BufferedReader(
				new InputStreamReader(stdout));

		try {
			while ((line = reader.readLine()) != null) {
				log.debug("Script output: " + line);
				output.add(line);
			}
		} catch (IOException e) {
			log.error("Error!", e);
			returnValue = -2;
			throw new SystemCallException("Failed to read line from!", e);
		}

		try {
			p.waitFor();
		} catch (InterruptedException e) {
			log.error("Error!", e);
			returnValue = -2;
			throw new SystemCallException(
					"Interrupted while waiting for process to terminate!", e);
		}

		returnValue = p.exitValue();
	}

	/**
	 * @return the commandName
	 */
	public String getCommandName() {
		return commandName;
	}

	/**
	 * @return the output
	 */
	public ArrayList<String> getOutput() {
		return output;
	}

	/**
	 * @return the returnValue
	 */
	public int getReturnValue() {
		return returnValue;
	}
}
