/*
 *  Copyright 2002-2012 Barcelona Supercomputing Center (www.bsc.es)
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
package integratedtoolkit.util;

import eu.elasticlm.api.LicenseEnforcement;
import eu.elasticlm.api.LicenseEnforcement.elasticLMVerificationConstants;
import eu.elasticlm.api.LicenseEnforcementException;
import eu.elasticlm.schemas.x2009.x05.license.token.FeatureType;
import eu.elasticlm.schemas.x2009.x05.license.token.LicenseTokenDocument;
import eu.elasticlm.schemas.x2009.x05.license.token.LicenseTokenType;
import integratedtoolkit.ITConstants;
import integratedtoolkit.types.Core;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.LinkedList;
import org.apache.xmlbeans.XmlException;

public class LicenseManager {

    private static LinkedList<String>[] coreToNames;
    private static HashMap<String, LinkedList<Integer>> namesToCores;
    private static HashMap<String, Integer> tokenToSimultaneousThreads;
    private final static String contextPath = ((System.getProperty(ITConstants.IT_CONTEXT) == null) ? System.getProperty("user.home") + File.separator + "optimis_context" + File.separator : System.getProperty(ITConstants.IT_CONTEXT));
    private final static String certificatesPath = ((System.getProperty(ITConstants.IT_LICENSE_CERTIFICATES) == null) ? System.getProperty("user.home") + File.separator + "optimis_context" : System.getProperty(ITConstants.IT_LICENSE_CERTIFICATES));

    public static void addLicenseToken(int coreId, String licenseList) {
        System.out.println("Adding license token " + licenseList + " to core " + coreId);
        String[] licenseNames = licenseList.split(",");
        for (String name : licenseNames) {
            coreToNames[coreId].add(name);
            LinkedList<Integer> cores = namesToCores.get(name);
            if (cores == null) {
                cores = new LinkedList<Integer>();
                namesToCores.put(name, cores);
            }
            cores.add(coreId);
        }

    }

    static void init(int coreCount) {
        System.out.println("[LICENSES]Initializing License Management");
        coreToNames = new LinkedList[coreCount];
        namesToCores = new HashMap<String, LinkedList<Integer>>();
        tokenToSimultaneousThreads = new HashMap<String, Integer>();
        for (int i = 0; i < coreCount; i++) {
            coreToNames[i] = new LinkedList<String>();
        }
        String tokenDir = contextPath + File.separator + "licensetoken";
        System.out.println("[LICENSES]TOKEN DIR " + tokenDir);
        File dir = new File(tokenDir);
        if (dir.exists()) {
            String[] tokenFiles = dir.list();
            for (String tokenFile : tokenFiles) {
                readToken(tokenFile);
            }
        }

    }

    private static void readToken(String tokenFile) {
        //Getting License Name
        String licenseName = null;
        LicenseTokenDocument doc;

        String tokenLocation = contextPath + File.separator + "licensetoken" + File.separator + tokenFile;
        System.out.println("[LICENSES]Validating license file at " + tokenLocation);
        try {
            doc = LicenseTokenDocument.Factory.parse(new File(tokenLocation));
        } catch (XmlException ex) {
            System.out.println("Cannot parse the file " + tokenFile + ": " + ex.getMessage());
            ex.printStackTrace();
            return;
        } catch (IOException ex) {
            System.out.println("Cannot read the file " + tokenFile + ": " + ex.getMessage());
            ex.printStackTrace();
            return;
        }

        LicenseTokenType token = doc.getLicenseToken();
        if (token != null) {
            for (FeatureType ft : token.getFeatures().getFeatureArray()) {
                if (ft.getFeatureId().equals("app-name")) {
                    licenseName = ft.getName();
                }
            }
        } else {
            System.out.println("Token has no license Id");
            return;
        }
        if (licenseName == null) {
            return;
        }
        System.out.println("Using certificate Demo_ISV.pem from " + certificatesPath);
        String ISVLocation = certificatesPath + File.separator + "Demo_ISV.pem";


        //Validating Token
        LicenseEnforcement tokenValidator = null;
        X509Certificate isvCertificate = null;
        URL tokenURL = null;
        try {
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            isvCertificate = (X509Certificate) cf.generateCertificate(new FileInputStream(ISVLocation));
        } catch (CertificateException e) {
            System.out.println("isv certificate error");
            return;
        } catch (IOException e) {
            System.out.println("isv certificate not found");
            return;
        }
        try {
            tokenURL = new File(tokenLocation).toURI().toURL();
        } catch (MalformedURLException ex) {
            System.out.println("license token not found ");
        }


        try {
            tokenValidator = LicenseEnforcement.Factory.newInstance(tokenURL, isvCertificate, contextPath);
        } catch (LicenseEnforcementException e) {
            System.out.println("LicenseEnforcementFactory could not be instantiated");
            return;
        }

        //
        // validate ISV authorization
        //
        try {
            if (!tokenValidator.isTokenAuthorizedBy(isvCertificate)) {
                System.out.println("Token not authorized by a given ISV ");
                return;
            }
        } catch (LicenseEnforcementException e) {
            System.out.println("Token not authorized by a given ISV ");
            return;
        }

        //
        // validate token signature and integrity
        //
/*
         try {
         if (!tokenValidator.validate(EnumSet.of(elasticLMVerificationConstants.SIGNATURE))) {
         System.out.println("Token has not been signed by an authorized party.");
         return;
         }
         } catch (LicenseEnforcementException ex) {
         ex.printStackTrace();
         System.out.println("Token has not been signed by an authorized party.");
         return;
         }
         */
        //
        // activates or deactivates the trusted clock for timestamps checking
        //
        //tokenValidator.useTrustedClock(true);

        //
        // validation of the expiration and activation time stamps of the token 
        // as well as the token authorization
        //
        /*try {

         if (!tokenValidator.validate(EnumSet.of(elasticLMVerificationConstants.TIMESTAMP))) {
         System.out.println("Timestamps (isv authorization & token activation/expiration) are not within expected time frames.");
         return;
         }
         } catch (LicenseEnforcementException ex) {
         System.out.println("Timestamps (isv authorization & token activation/expiration) are not within expected time frames.");
         return;
         }*/

        try {
            if (tokenValidator.hasFeature("THREADS")) {
                int threads = Integer.parseInt(tokenValidator.getFeature("THREADS").getValue());
                System.out.println(licenseName + " can run " + threads + " threads at a time");
                tokenToSimultaneousThreads.put(licenseName, threads);
            }
        } catch (Exception e) {
            System.out.println("Has no limit of Threads");
            return;
        }
        return;
    }

    public static LinkedList<String> getCoreLicenses(int coreId) {
        return coreToNames[coreId];
    }

    public static int getLicenseThreads(String name) {
        return tokenToSimultaneousThreads.get(name);
    }

    public static Iterable<String> getAllLicenses() {
        return tokenToSimultaneousThreads.keySet();
    }

    public static LinkedList<Integer> getCores(String licenseName) {
        return namesToCores.get(licenseName);
    }
    
    public static void updateStructures(){
        LinkedList<String>[] coreToNamesTmp= new LinkedList[Core.coreCount];
        System.arraycopy(coreToNames, 0, coreToNamesTmp, 0, coreToNames.length);
        for (int i=coreToNames.length;i<Core.coreCount;i++){
            coreToNamesTmp[i]= new LinkedList();
        }
        coreToNames=coreToNamesTmp;
    }
}
