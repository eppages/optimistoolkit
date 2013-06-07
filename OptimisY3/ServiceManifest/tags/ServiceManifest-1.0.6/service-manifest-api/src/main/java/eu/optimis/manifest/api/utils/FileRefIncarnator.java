/*
 * Copyright (c) 2012, Fraunhofer-Gesellschaft
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 * (1) Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the disclaimer at the end.
 *     Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in
 *     the documentation and/or other materials provided with the
 *     distribution.
 *
 * (2) Neither the name of Fraunhofer nor the names of its
 *     contributors may be used to endorse or promote products derived
 *     from this software without specific prior written permission.
 *
 * DISCLAIMER
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 */
package eu.optimis.manifest.api.utils;

/**
 * This class makes it easy to provide an url and an index and retrieve back an updated URL with the index
 * appended to the filename.
 * <p/>
 *
 * @author geli - 3/13/12
 */
public final class FileRefIncarnator
{
    /**
     * This method is used by the api to retrieve the list of incarnated file refs (requested by the SDO).
     *
     * @param href  the path to a file, e.g. "/opt/optimis/contextualization-file.iso" the important thing is
     *              that it has a file extension
     * @param index the index that will be attached. e.g. 1
     * @return the updated href, e.g. "/opt/optimis/contextualization-file_2.iso"
     * @see eu.optimis.manifest.api.sp.VirtualMachineComponent#getIncarnatedContextualizationFileArray()
     */
    public static String updateHrefWithIndex( String href, int index )
    {
        //if there is no extension, we simply append the index to the file location (in case of urls)
        if ( getLastDotPosition( href ) == -1 )
        {
            return href + "_" + index;
        }
        return getFilePath( href ) + "_" + index + "." + getFileExtension( href );
    }

    /**
     * returns the path, without the extension. By retrieving the position of the last "." and then
     * returning everything before the dot position.
     *
     * @param href the file url
     * @return the string before the "." position.
     */
    public static String getFilePath( String href )
    {
        int dotPosition = getLastDotPosition( href );
        //if the string is empty or there is no dot in the href, we simply return the whole string
        if ( href != null && !href.isEmpty() && dotPosition > 0 )
        {
            return href.substring( 0, dotPosition );
        }
        return href;
    }

    private static String getFileExtension( String href )
    {
        if ( href != null && !href.isEmpty() )
        {
            int dotPos = getLastDotPosition( href );
            return href.substring( dotPos + 1 );
        }
        return href;
    }

    /**
     * will return the position of the last dot. returns -1 if there is no "." in the string.
     *
     * @param href the string to parse for a dot
     * @return the position of the dot
     */
    public static int getLastDotPosition( String href )
    {
        return href.lastIndexOf( "." );
    }

    private FileRefIncarnator()
    {
        //nothing to do here
    }
}
