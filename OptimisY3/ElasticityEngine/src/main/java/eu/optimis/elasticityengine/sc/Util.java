package eu.optimis.elasticityengine.sc;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import com.thoughtworks.xstream.io.xml.DomReader;

import eu.optimis.elasticityengine.ElasticityRule;
import eu.optimis.elasticityengine.manifest.Rule;
import eu.optimis.elasticityengine.manifest.RulesList;
import eu.optimis.elasticityengine.manifest.Scope;

/**
 * Utility class for Elasticity, contains parser for service manifest
 * 
 * @author Daniel Espling (<a href="mailto:espling@cs.umu.se">espling@cs.umu.se</a>)
 *Copyright (C) 2012 Umeå University

* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.

* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU General Public License for more details.

* You should have received a copy of the GNU General Public License
* along with this program.  If not, see <http://www.gnu.org/licenses/>.

 */
public class Util {

    private static XStream xstream = new XStream(new DomDriver());
    private static Logger log = Logger.getLogger(Util.class);

    /*
     * Configure the xstream serializer
     */
    static {
        xstream.alias("opt:Rule", Rule.class);
        xstream.alias("opt:Scope", Scope.class);
        xstream.alias("opt:VirtualSystemId", String.class);
        xstream.alias("opt:VirtualSystemGroupId", String.class);
        xstream.alias("opt:ElasticitySection", RulesList.class);
        xstream.alias("opt:ComponentId",String.class);

        xstream.aliasField("opt:KPIName", Rule.class, "kpiName");
        xstream.aliasField("opt:Window", Rule.class, "window");
        xstream.aliasField("opt:Frequency", Rule.class, "frequency");
        xstream.aliasField("opt:Quota", Rule.class, "quota");
        xstream.aliasField("opt:Tolerance", Rule.class, "tolerance");
        xstream.aliasField("opt:Scope", Rule.class, "scope");
        xstream.addImplicitCollection(RulesList.class, "rules");
        xstream.addImplicitCollection(Scope.class, "imageIDs");
    }

    // Create instance of DocumentBuilderFactory
    private final static DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    private static final String ELASTICITY_STARTTAG = "<opt:ElasticitySection>";
    private static final String ELASTICITY_STOPTAG = "</opt:ElasticitySection>";

    public static Map<String, ImageController> initiateImageControllers(String serviceManifest) {

        // Get the DocumentBuilder
        DocumentBuilder parser;
        try {
            parser = factory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            log.fatal("Parser configuration failed, Fatal.");
            throw new RuntimeException(e);
        }
        
        // System.out.println("\nServicemanifest:[\n" + serviceManifest +
        // "\n]\n");

        int startIndex = serviceManifest.indexOf(ELASTICITY_STARTTAG);
        int endIndex = serviceManifest.indexOf(ELASTICITY_STOPTAG) + ELASTICITY_STOPTAG.length();
        String subManifest;
        log.debug("startIndex: " + startIndex + ", endIndex: " + endIndex);
        try{ 
        	subManifest = serviceManifest.substring(startIndex, endIndex);
        	log.debug("Sub Manifest created"+subManifest);
        } catch (StringIndexOutOfBoundsException e){
            log.warn("StringIndexOutOfBoundsException, Manifest missing elasticity rules.");
            throw new IllegalArgumentException(e);
        }
        
        Document doc;
        try {
            doc = parser.parse(new InputSource(new StringReader(subManifest.trim())));
        } catch (SAXException e) {
            log.warn("XML parsing failed");
            throw new IllegalArgumentException(e);
        } catch (IOException e) {
            log.warn("IOException, unexpected and fatal.");
            throw new IllegalStateException(e);
        }

        RulesList ruleList = (RulesList) xstream.unmarshal(new DomReader(doc));
        List<Rule> rules = ruleList.getRules();
        log.info("Parsed " + rules.size() + " rules from manifest.");

        // One set of rules per ImageID
        Map<String, Set<ElasticityRule>> eRules = new HashMap<String, Set<ElasticityRule>>();

        for (Rule rule : rules) {
            List<String> imageIDs = rule.scope.getimageIDs();
            for (String imageID : imageIDs) {
                ElasticityRule er = new ElasticityRule(imageID, rule);

                Set<ElasticityRule> imageRules = eRules.get(imageID);
                if (imageRules == null) {
                    imageRules = new HashSet<ElasticityRule>();
                    eRules.put(imageID, imageRules);
                }

                imageRules.add(er);
                // log.debug("Processing parsed rule:" + rule + " to eRule: " +
                // er);
            }
        }

        // Finally Create the ImageControllers
        Map<String, ImageController> imageControllers = new HashMap<String, ImageController>();
        for (String imageID : eRules.keySet()) {
            ImageController iController = new ImageController(imageID, eRules.get(imageID));
            imageControllers.put(imageID, iController);
        }

        return imageControllers;
    }

    public static String getManifest(String filePath) throws IOException {

        InputStream fileInputStream;

        File manifestFile = new File(filePath);
        if (manifestFile.exists()) {

        } else {
            URL fileURI = Class.class.getResource(filePath);
            manifestFile = new File(fileURI.getFile());
            // fileInputStream = Class.class.getResourceAsStream(filePath);
        }

        if (!manifestFile.exists()) {
        	log.error("File not Found");
            throw new IllegalArgumentException("Could not find file: " + filePath);
        }

        fileInputStream = new FileInputStream(manifestFile);
        byte[] buffer = new byte[(int) manifestFile.length()];
        BufferedInputStream iStream = null;
        try {
            iStream = new BufferedInputStream(fileInputStream);
            iStream.read(buffer);
        } finally {
            if (iStream != null)
                try {
                    iStream.close();
                } catch (IOException ignored) {
                }
        }
        return new String(buffer);
    }

}
