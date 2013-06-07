/* $Id: BaseImageCopier.java 11122 2013-01-24 14:02:49Z sulistio $ */

/*
 Copyright 2011 University of Stuttgart

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
package eu.optimis.ics.core.io;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileSystemUtils;
import org.apache.log4j.Logger;

import eu.optimis.ics.core.ImageCreationService;
import eu.optimis.ics.core.exception.ImageNotFoundException;
import eu.optimis.ics.core.exception.OutOfDiskSpaceException;
import eu.optimis.ics.core.image.Image;
import eu.optimis.ics.core.image.ImageState;
import eu.optimis.ics.core.shell.ProcessResult;
import eu.optimis.ics.core.shell.ShellUtil;

/**
 * Thread for copying image files from base images.
 * 
 * @author roland
 * 
 */
public class BaseImageCopier extends Thread {

	/** Log4j logger instance. */
	private static Logger LOG = Logger.getLogger(BaseImageCopier.class.getName());
	
	/** The source base image. */
	private File source;
	
	/** The destination file. */
	private File destination;
	
	/** The image encapsulating the relevant information. */
	private Image image;

	/**
	 * Creates a new file copier for creating <code>source</code> to
	 * <code>destination</code> and storing the relevant information in
	 * <code>image</code>.
	 * 
	 * @param source
	 *            the base image
	 * @param destination
	 *            the destination image file
	 * @param image
	 *            the {@link Image} object storing the relevant image
	 *            information
	 * @throws OutOfDiskSpaceException
	 *             If there is not enough free space on the drive where
	 *             <code>destination</code> resides
	 */
	public BaseImageCopier(File source, File destination, Image image)
			throws OutOfDiskSpaceException {
		this.source = source;
		this.destination = destination;
		this.image = image;

		if (checkDiskspace(destination.getParent(), source) == false) {
			throw new OutOfDiskSpaceException(
					"ics.core.io.BaseImageCopier: Not enough free space at target '" + destination + "'");
		}
	}

	/**
	 * Checks that <code>destination</code> has enough space to create a copy of
	 * <code>baseImage</code>.
	 * 
	 * @param destination
	 *            the destination which will be checked for free space
	 * 
	 * @param file
	 *            the file to copy
	 * 
	 * @return <code>True</code> if there is enough space available to create a
	 *         copy of <code>baseImage</code> on <code>destination</code>,
	 *         <code>false</code> otherwise
	 */
	private boolean checkDiskspace(String destination, File file) {
		// Determine free space in KB
		long freeSpaceKb;
		try {
			freeSpaceKb = FileSystemUtils.freeSpaceKb(destination.toString());
		} catch (IOException ioException) {
			LOG.error("ics.core.io.BaseImageCopier.checkDiskspace(): Cannot determine free space in image folder '"
					+ destination.toString() + "'", ioException);
			return false;
		}
		LOG.debug("ics.core.io.BaseImageCopier.checkDiskspace(): Free disk space: " + freeSpaceKb + "KB");

		// Determine required size in KB
		long requiredSizeKb = (file.length() / 1024);
		LOG.debug("ics.core.io.BaseImageCopier.checkDiskspace(): Required disk space: " + requiredSizeKb + "KB");

		// Check if enough space is available
		if (requiredSizeKb > freeSpaceKb) {
			LOG.error("ics.core.io.BaseImageCopier.checkDiskspace(): Not enough disk space for cloning image");
			return false;
		} else {
			LOG.debug("ics.core.io.BaseImageCopier.checkDiskspace(): Image can be cloned, enough disk space");
			return true;
		}
	}

	/**
	 * Performs the actual copying of <code>source</code> to <code>destination</code>.
	 */
	public void run() {
		String rsyncString = "/usr/bin/rsync -L --sparse ";
		String command = rsyncString + " " + source.getPath() + " " + destination.getPath();
		try {
			ProcessResult result = ShellUtil.executeShellCommand(command);
			
			if (result.getExitCode() != 0) {
				LOG.error("ics.core.io.BaseImageCopier.run(): Could not copy image");
			} else {
				LOG.debug("ics.core.io.BaseImageCopier.run(): Image " 
						+ image.getUuid() + " created successfully");
				image.setState(ImageState.READY);
				ImageCreationService.getInstance().updateImage(image);
			}
		} catch (ImageNotFoundException exception) {
			LOG.error("ics.core.io.BaseImageCopier.run(): Image " 
					+ image.getUuid() + " not found", exception);
		}
	}

}
