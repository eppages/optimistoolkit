/* $Id: BaseClient.java 7266 2012-05-03 13:25:15Z rkuebert $ */

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
package eu.optimis.ics.core.test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import eu.optimis.ics.core.ImageCreationService;
import eu.optimis.ics.core.exception.ImageNotFoundException;
import eu.optimis.ics.core.exception.OutOfDiskSpaceException;
import eu.optimis.ics.core.exception.StateChangeException;
import eu.optimis.ics.core.exception.UnsupportedStateException;
import eu.optimis.ics.core.image.Image;
import eu.optimis.ics.core.image.ImageState;
import eu.optimis.ics.core.image.ImageType;

public abstract class BaseClient {

	protected void flushImages() {
		ImageCreationService.getInstance().flushDatabase();
	}

	protected int getNumImages() {
		return ImageCreationService.getInstance().getImages().size();
	}

	protected void printImages() {
		ArrayList<Image> images = getImages();
		for (int i = 0; i < images.size(); i++) {
			Image image = images.get(i);
			System.out.print("#" + i);
			System.out.print(" - UUID: " + image.getUuid() + ", ");
			System.out.print(" file: " + image.getImageFile() + ", ");
			System.out.print(" state: " + image.getState());
			System.out.println();
		}
	}

	protected ArrayList<Image> getImages() {
		return ImageCreationService.getInstance().getImages();
	}

	protected Image createOrchestrationImage() throws IOException,
			OutOfDiskSpaceException {
		return ImageCreationService.getInstance().createImage(
				ImageType.OrchestrationElement);
	}

	protected Image createCoreImage() throws IOException,
			OutOfDiskSpaceException {
		return ImageCreationService.getInstance().createImage(
				ImageType.CoreElement);
	}

	protected Image createCoreImageAndWait() {
		return createImageAndWait(ImageType.CoreElement);
	}

	protected Image createOrchestrationImageAndWait() {
		return createImageAndWait(ImageType.OrchestrationElement);
	}

	private Image createImageAndWait(ImageType imageType) {
		try {
			System.out.println("Creating image, waiting for READY");
			Image image = ImageCreationService.getInstance().createImage(
					imageType);
			while (true) {
				System.out.print(".");
				// System.out.println(newImage.getState());
				if (image.getState().equals(ImageState.READY)) {
					System.out.println();
					System.out.println("Image is in state READY");
					break;
				}

				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

			}
			
			System.out.println("Image created, sleeping 5s for safety");
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}


			return image;
		} catch (IOException ioException) {
			ioException.printStackTrace();
		} catch (OutOfDiskSpaceException noSpaceException) {
			noSpaceException.printStackTrace();
		}

		return null;
	}

	public void finalize(Image image) {
		System.out.printf("Finalizing image '%s'\n", image.getUuid());
		try {
			ImageCreationService.getInstance().finalizeImage(
					image.getUuid().toString());
			System.out.println("Image finalized");
		} catch (StateChangeException stateChangeException) {
			System.out.printf("Cannot finalize image '%s':\n%s\n", image
					.getUuid().toString(), stateChangeException.getMessage());
		} catch (ImageNotFoundException imageNotFoundException) {
			System.out.printf("Cannot finalize image '%s':\n%s\n", image
					.getUuid().toString(), imageNotFoundException.getMessage());
		} catch (IOException ioException) {
			System.out.printf("Cannot finalize image '%s':\n%s\n", image
					.getUuid().toString(), ioException.getMessage());
		}
	}

	public void unfinalize(Image image) {
		System.out.printf("Unfinalizing image '%s'\n", image.getUuid());
		try {
			ImageCreationService.getInstance().unfinalizeImage(
					image.getUuid().toString());
			System.out.println("Image unfinalized");
		} catch (StateChangeException stateChangeException) {
			System.out.printf("Cannot unfinalize image '%s':\n%s\n", image
					.getUuid().toString(), stateChangeException.getMessage());
		} catch (ImageNotFoundException imageNotFoundException) {
			System.out.printf("Cannot unfinalize image '%s':\n%s\n", image
					.getUuid().toString(), imageNotFoundException.getMessage());
		} catch (IOException ioException) {
			System.out.printf("Cannot unfinalize image '%s':\n%s\n", image
					.getUuid().toString(), ioException.getMessage());
		}
	}

	public void putFile(String filename, Image image) {
		try {
			ImageCreationService.getInstance().putFile(
					image.getUuid().toString(), new File(filename));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ImageNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void putFileAndWait(String filename, Image image) {
		System.out.printf("Putting file '%s' to image %s\n", filename, image);
		try {
			ImageCreationService.getInstance().putFile(
					image.getUuid().toString(), new File(filename));
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (ImageNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (UnsupportedStateException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		// image.putFile(new File(filename));
		while (true) {
			System.out.print(".");
			// System.out.print("Image is in state " + image.getState());
			// System.out.println(newImage.getState());
			if (image.getState().equals(ImageState.READY)) {
				System.out.println();
				System.out.println("Image is in state READY");
				break;
			}

			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

		}
	}

	public void putWarAndWait(String filename, Image image) {
		System.out.printf("Putting file '%s' to image %s\n", filename, image);
		try {
			ImageCreationService.getInstance().putWarFile(
					image.getUuid().toString(), new File(filename));
		} catch (Exception exception) {
			exception.printStackTrace();
		}

		while (true) {
			System.out.print(".");
			if (image.getState().equals(ImageState.READY)) {
				System.out.println();
				System.out.println("Image is in state READY");
				break;
			}

			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

		}
	}

	protected void putZipAndWait(String filename, Image image) {
		System.out.printf("Putting file '%s' to image %s\n", filename, image);
		try {
			ImageCreationService.getInstance().putZipFile(
					image.getUuid().toString(), new File(filename));
		} catch (Exception exception) {
			exception.printStackTrace();
		}

		while (true) {
			System.out.print(".");
			if (image.getState().equals(ImageState.READY)) {
				System.out.println();
				System.out.println("Image is in state READY");
				break;
			}

			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

		}

	}

	protected Image getImage(String imageId) throws IOException,
			ImageNotFoundException {
		return ImageCreationService.getInstance().getImage(imageId);
	}

	protected void setPermissions(String imageId, String file, String permissions) {
		System.out.println("Setting permissions for file " + file
				+ " on image " + imageId + " to " + permissions);
		try {
			ImageCreationService.getInstance().setPermissions(
					imageId, file, permissions);
		} catch (Exception exception) {
			exception.printStackTrace();
		}
	}
}
