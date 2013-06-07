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
package eu.optimis.vc.api.ImageConverter;

import java.io.File;
import java.util.ArrayList;

import org.apache.log4j.Logger;

import eu.optimis.vc.api.Core.SystemCall;
import eu.optimis.vc.api.Core.SystemCallException;
import eu.optimis.vc.api.DataModel.GlobalConfiguration;
import eu.optimis.vc.api.DataModel.Image.HardDisk;

/**
 * Class for converting VM HardDisk images using qemu-img.
 * 
 * @author Django Armstrong (ULeeds)
 * @version 0.0.4
 */
public class ImageConversion implements Runnable {

	protected final static Logger log = Logger.getLogger(ImageConversion.class);

	private String currentFormat;
	private String desiredFormat;

	private GlobalConfiguration configuration;
	private SystemCall systemCall;
	private String repository;

	private HardDisk hardDisk;

	/**
	 * Constructor initialises an instance of the Image Conversion tool.
	 * 
	 * @param globalConfiguration
	 *            The VMC global config object to fetch the image repo URI from.
	 * @param desiredFormat The image format to convert to.
	 * @param hardDisk The HardDisk object to convert.
	 */
	public ImageConversion(GlobalConfiguration globalConfiguration,
			String desiredFormat, HardDisk hardDisk) {
		this.configuration = globalConfiguration;
		systemCall = new SystemCall(configuration.getInstallDirectory());
		repository = configuration.getRepository();
		this.desiredFormat = desiredFormat;
		this.hardDisk = hardDisk;
	}

	/**
	 * Method for converting images
	 */
	public void run() {

		FormatDetection formatDetection = new FormatDetection();
		this.currentFormat = formatDetection.detect();

		String commandName = "qemu-img";
		ArrayList<String> arguments = new ArrayList<String>();

		arguments.add("convert");
		if (this.desiredFormat .equals("qcow2")) {
			arguments.add("-c"); // Compression on
		}
		arguments.add("-p"); // Progress
		if (this.currentFormat != null) {
			arguments.add("-fmt");
			arguments.add(this.currentFormat);
		}
		arguments.add("-O");
		arguments.add(this.desiredFormat);
		if (this.desiredFormat .equals("vmdk")) {
			arguments.add("-o"); // Add an option
			arguments.add("scsi"); // SCSI image format (requires patch and qemu-0.15.1)
		}
		arguments.add(this.hardDisk.getUri());
		// TODO: This will fall on its face if there is no extension...
		String newFileName = this.hardDisk.getFileName().substring(0,
				this.hardDisk.getFileName().lastIndexOf("."))
				+ "." + desiredFormat;
		String newUri = repository + "/" + newFileName;
		arguments.add(newUri);

		// Executed command looks like so:
		// qemu-img convert -fmt raw -O qcow2 /path/test.img test.qcow2 -c -p
		try {
			systemCall.runCommand(commandName, arguments);

			while (systemCall.getReturnValue() == -1) {
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

		} catch (SystemCallException e) {
			if (configuration.isDefaultValues()) {
				log.warn(
						"Failed to run command, is this invocation in a unit test?",
						e);
			}
		}

		if (systemCall.getReturnValue() != 0) {
			log.error("Hardisk conversion Failed! Error code was: "
					+ systemCall.getReturnValue());
		}

		this.hardDisk.setConverted(true);
		this.hardDisk.setFormat(desiredFormat);
		this.hardDisk.setUri(newUri);
		log.info("Harddisk created with URI: " + this.hardDisk.getUri());
		this.hardDisk.setFileName(newFileName);
		log.info("Harddisk filename: " + this.hardDisk.getFileName());

		File newImage = new File(this.hardDisk.getUri());
		if (newImage.exists()) {
			log.info("Harddisk size: " + (newImage.length() / 1024 / 1024)
					+ "MB");
		}
	}

	/**
	 * @return the hardDisk
	 */
	public HardDisk getHardDisk() {
		return hardDisk;
	}

	/**
	 * @return the systemCall
	 */
	public SystemCall getSystemCall() {
		return systemCall;
	}
}
