/* $Id: ShellUtil.java 3867 2012-02-22 09:47:35Z rkuebert $ */

/*
 * Copyright 2012 University of Stuttgart
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package eu.optimis.ics.core.shell;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.apache.log4j.Logger;

/**
 * Utility class for performing shell activities.
 * 
 * @author Roland Kuebert
 * 
 */
public class ShellUtil {

    /** Log4j logger instance. */
    private static Logger LOG = Logger.getLogger(ShellUtil.class.getName());

    /**
     * Executes the given shell command and returns the resulting std out, std
     * err and exit code.
     * <p/>
     * The command is performed with <code>/bin/sh -c shellCommand</code>
     * 
     * @param shellCommand
     *            the command to perform
     * 
     * @return the results of the command execution (std out, std err and exit
     *         code)
     */
    public static ProcessResult executeShellCommand(String shellCommand) {
        LOG.trace("Running command: " + shellCommand);
        StringBuffer standardOut = new StringBuffer();
        StringBuffer standardError = new StringBuffer();
        int exitCode = -1;

        String[] command = new String[] { "/bin/sh", "-c", shellCommand };

        try {
            String line;
            Process p = Runtime.getRuntime().exec(command);
            BufferedReader bri = new BufferedReader(new InputStreamReader(p.getInputStream()));
            BufferedReader bre = new BufferedReader(new InputStreamReader(p.getErrorStream()));
            while ((line = bri.readLine()) != null) {
                standardOut.append(line);
            }
            bri.close();
            while ((line = bre.readLine()) != null) {
                standardError.append(line);
            }
            bre.close();
            p.waitFor();
            exitCode = p.exitValue();
        } catch (Exception err) {
            //err.printStackTrace();
            LOG.error("- Exception occurs: " + err.toString());
        }

        LOG.debug("- Exit code: " + exitCode);
        LOG.debug("- Standard error: " + standardError.toString());
        LOG.debug("- Standard out: " + standardOut.toString());
        ProcessResult result = new ProcessResult(standardOut.toString(), standardError.toString(), exitCode);
        return result;
    }

}
