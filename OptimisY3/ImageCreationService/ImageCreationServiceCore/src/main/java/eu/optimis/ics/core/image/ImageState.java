/* $Id: ImageState.java 11131 2013-01-24 15:20:20Z sulistio $ */

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

/**
 * The different states of an image life-cycle.
 * <p/>
 * The states have the following meaning:
 * <ul>
 * <li>BUSY - the image file is currently being copied from a base image</li>
 * <li>READY - the image file has been copied from the base image. Files can be added</li>
 * <li>FINALIZED - the image has been finalized and cannot be changed anymore</li>
 * </ul>
 * <p/>
 * 
 * @author Roland Kuebert 
 */
public enum ImageState {
    BUSY, READY, FINALIZED
}
