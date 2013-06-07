/*
 * Copyright 2011 University of Stuttgart
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

package eu.optimis.ics.core.image;

import eu.optimis.ics.core.image.Image;

/**
 * Describes the image requirement for choosing the suitable base image
 * @author Anthony Sulistio
 *
 */
public class ImageRequirement {

    private String OS_;     // operating system
    private String osVersion_;      // OS version
    private int imageSize_;  // in GB
    private String architecture_;  // value: i386 or x86_64

    /**
     * Default constructor
     */
    public ImageRequirement() {
        OS_ = null;
        osVersion_ = null;
        imageSize_ = 0;
        architecture_ = null;
    }

    /**
     * Creates a new object based on the given operating system (OS)
     * @param os    operating system name
     */
    public ImageRequirement(String os) {
        if (os != null) {
            OS_ = os;
        }
    }

    /**
     * Sets the operating system    
     * @param os    operating system name
     */
    public void setOS(String os) {
        if (os != null && os.length() > 0) {
            OS_ = os;
        }
    }

    /**
     * Gets the operating system name
     * @return the name of the operating system
     */
    public String getOS() {
        return OS_;
    }

    /**
     * Sets the operating system version, e.g. 12.04 LTS for Ubuntu or 6.3 for CentOS image
     * @param version   operating system version
     */
    public void setOSVersion(String version) {
        if (version != null && version.length() > 0) {
            osVersion_ = version;
        }
    }

    /**
     * Gets the version of the operating system
     * @return the version of the operating system
     */
    public String getOSVersion() {
        return osVersion_;
    }

    /**
     * Sets the image size (in GB)
     * @param size  image size
     */
    public void setImageSize(int size) {
        if (size > 0) {
            imageSize_ = size;
        }

    }

    /** 
     * Gets the image size (in GB)
     * @return image size
     */
    public int getImageSize() {
        return imageSize_;
    }

    /**
     * Sets the architecture of the image (value: i386 or x86_64)
     * @param arch  the image architecture
     */
    public void setArchitecture(String arch) {
        if (arch != null && arch.length() > 0) {
            architecture_ = arch;
        }
    }

    /**
     * Gets the architecture of the image (value: i386 or x86_64)
     * @return the image architecture
     */
    public String getArchitecture() {
        return architecture_;
    }

    /**
     * Compares the requirement parameters in the given object with this one
     * @param obj   the ImageRequirement object
     * @return <tt>true</tt> if matched all the parameters, <tt>false</tt> otherwise.
     */
    public boolean compareImage(Image obj) {
        boolean result = false;
        int match = 0;

        if (OS_ != null && obj.getOS().equalsIgnoreCase(OS_) == true) {
            match++;
        }

        if (osVersion_ != null
                && obj.getOSVersion().equalsIgnoreCase(osVersion_) == true) {
            match++;
        }

        if (architecture_ != null
                && obj.getArchitecture().equalsIgnoreCase(architecture_) == true) {
            match++;
        }

        if (imageSize_ > 0 && obj.getImageSize() >= imageSize_) {
            match++;
        }

        if (match > 0) {
            result = true;
        }

        return result;
    }

    /**
     * Prints the image requirement in a plain string
     * @return the string listing the image requirement
     */
    public String toString() {
        int capacity = 100;
        StringBuffer buffer = new StringBuffer(capacity);
        //buffer.append(OS_ + ", " + osVersion_ + ", ");
        //buffer.append(imageSize_ + ", " + architecture_);

        if (OS_ != null) {
            buffer.append(OS_);
        }

        if (osVersion_ != null) {
            buffer.append(" " + osVersion_);
        }

        if (architecture_ != null) {
            buffer.append(" " + architecture_);
        }

        if (imageSize_ > 0) {
            buffer.append(" image with a min. of ");
            buffer.append(imageSize_);
            buffer.append(" GB disk size");
        }

        return buffer.toString();
    }

}
