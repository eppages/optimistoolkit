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

package eu.optimis.ics.core.util;

import java.io.StringReader;
import java.io.File;
import java.io.IOException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.InputSource;
import org.apache.log4j.Logger;
import eu.optimis.ics.core.image.ImageRequirement;

/**
 * A SAX parser that parses the given image requirement.
 * For the image requirement, the XML format looks like below:
 * <pre>
 * <ImageTemplate>
 *     <operatingSystem>CentOS</operatingSystem>  <!-- for a wildcard usage, write: Linux -->
 *     <osVersion>5.8</osVersion>      
 *     <architecture>i386</architecture>    <!-- value only i386 or x86_64 -->
 *     <imageSize>10</imageSize>    <!-- in GB (integer only) -->
 * </ImageTemplate>
 * </pre>
 * Note that the above tags are all optional and can be omitted.
 * If this is the case, the ICS will randomly select the eligible base image.
 * 
 * @author Anthony Sulistio
 *
 */
public class SAXParserHandler extends DefaultHandler {

    /** Log4j logger instance. */
    private static Logger log = Logger.getLogger(PropertiesReader.class.getName());

    private String tmpStr_;   // store the data inside of an element tag
    private ImageRequirement tmpObj_;  // store the requirement request

    /**
     * Default constructor
     */
    public SAXParserHandler() {
        tmpObj_ = null;
        tmpStr_ = null;
    }

    /**
     * Parses the given XML description 
     * @param xmlStr    the XML description 
     * @return the image requirement object
     * @see eu.optimis.ics.core.image.ImageRequirement
     */
    public ImageRequirement getRequirement(String xmlStr) {
        if (xmlStr != null) {
            parseDocument(xmlStr);
        }

        return tmpObj_;
    }

    /**
     * Parses the given XML file
     * @param file  the XML file
     * @return the image requirement object
     * @see eu.optimis.ics.core.image.ImageRequirement
     */
    public ImageRequirement getRequirement(File file) {
        if (file != null) {
            parseDocument(file);
        }

        return tmpObj_;
    }

    /**
     * Parses the XML description
     * @param xmlStr    the XML description
     */
    private void parseDocument(String xmlStr) {

        SAXParserFactory spf = SAXParserFactory.newInstance();
        try {
            // get a new instance of parser
            SAXParser sp = spf.newSAXParser();

            // parse the XML string and also register this class for call backs
            InputSource src = new InputSource(new StringReader(xmlStr));
            sp.parse(src, this);
        } catch (SAXException se) {
            //se.printStackTrace();
            log.error("ics.core.util.SAXParserReader.parseDocument(): Exception occurs - "
                    + se.toString());
        } catch (ParserConfigurationException pce) {
            //pce.printStackTrace();
            log.error("ics.core.util.SAXParserReader.parseDocument(): Exception occurs - "
                    + pce.toString());
        } catch (IOException ie) {
            //ie.printStackTrace();
            log.error("ics.core.util.SAXParserReader.parseDocument(): Exception occurs - "
                    + ie.toString());
        }
    }

    /**
     * Parses the XML file
     * @param file  the XML file
     */
    private void parseDocument(File file) {

        SAXParserFactory spf = SAXParserFactory.newInstance();
        try {
            // get a new instance of parser
            SAXParser sp = spf.newSAXParser();

            // parse the file and also register this class for call backs
            sp.parse(file, this);

        } catch (SAXException se) {
            //se.printStackTrace();
            log.error("ics.core.util.SAXParserReader.parseDocument(): Exception occurs - "
                    + se.toString());
        } catch (ParserConfigurationException pce) {
            //pce.printStackTrace();
            log.error("ics.core.util.SAXParserReader.parseDocument(): Exception occurs - "
                    + pce.toString());
        } catch (IOException ie) {
            //ie.printStackTrace();
            log.error("ics.core.util.SAXParserReader.parseDocument(): Exception occurs - "
                    + ie.toString());
        } catch (Exception ex) {
            //ie.printStackTrace();
            log.error("ics.core.util.SAXParserReader.parseDocument(): Exception occurs - "
                    + ex.toString());
        }
    }

    /**
     * An event handler for the starting of each element tag
     */
    public void startElement(String uri, String localName, String qName,
            Attributes attributes) throws SAXException {
        tmpStr_ = "";   // reset the string value
        if (qName.equalsIgnoreCase("ImageTemplate")) {
            tmpObj_ = new ImageRequirement(); // create a new instance
        }
    }

    /**
     * Gets the value of an element tag
     */
    public void characters(char[] ch, int start, int length)
            throws SAXException {
        tmpStr_ = new String(ch, start, length);
    }

    /**
     * An event handle for the ending of an element tag.
     */
    public void endElement(String uri, String localName, String qName)
            throws SAXException {

        /**********
        if (qName.equalsIgnoreCase("ImageTemplate")) {		
        	// do nothing
        } else 
        ***********/
        if (qName.equalsIgnoreCase("operatingSystem")) {
            tmpObj_.setOS(tmpStr_);
        } else if (qName.equalsIgnoreCase("osVersion")) {
            tmpObj_.setOSVersion(tmpStr_);
        } else if (qName.equalsIgnoreCase("architecture")) {
            tmpObj_.setArchitecture(tmpStr_);
        } else if (qName.equalsIgnoreCase("imageSize")) {
            // make sure that it is not an empty element
            if (tmpStr_.length() > 0) {
                log.debug("ics.core.util.SAXParserReader.endElement(): image size = "
                        + tmpStr_);
                tmpObj_.setImageSize(Integer.parseInt(tmpStr_));
                //log.debug("ics.core.util.SAXParserReader.endElement(): after parseInt()");
            }
        }
    }

    /**
     * The main method for testing purposes
     * @param args  input arguments
     */
    public static void main(String[] args) {
        SAXParserHandler spe = new SAXParserHandler();
        ImageRequirement obj = null;

        if (args.length == 0) {
            StringBuffer buffer = new StringBuffer(100);
            //buffer.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
            buffer.append("<ImageTemplate><operatingSystem>CentOS</operatingSystem>");
            buffer.append("<osVersion>50.8</osVersion><architecture>i386</architecture>");
            buffer.append("<imageSize>18</imageSize></ImageTemplate>");

            obj = spe.getRequirement(buffer.toString());
        } else {
            String filename = args[0];
            spe.getRequirement(filename);
        }
        System.out.println("New requirement = " + obj.toString());
    }

}
