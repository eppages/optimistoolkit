/**
 * Copyright (C) 2010-2013 Barcelona Supercomputing Center
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version. This library is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser
 * General Public License for more details. You should have received a copy of
 * the GNU Lesser General Public License along with this library; if not, write
 * to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA
 * 02111-1307 USA
 */
package eu.optimis.ecoefficiencytool.core.tools;

import eu.optimis.ecoefficiencytool.core.Constants;
import eu.optimis.manifest.api.ip.EcoEfficiencySection;
import eu.optimis.manifest.api.ip.Manifest;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.log4j.Logger;

/**
 *
 * @author jsubirat
 */
public class CertificateTools {

    private static Logger log = Logger.getLogger(CertificateTools.class);
    private boolean EuCoCCompliant;
    private int LEEDCertification;
    private int BREEAMCertification;
    private int EnergyStarRating;
    private boolean ISO14000;
    private int GreenStar;
    private int CASBEE;

    public CertificateTools() {
        //log = Log.getLog(getClass());
        PropertiesConfiguration configEco = ConfigManager.getPropertiesConfiguration(ConfigManager.ECO_CONFIG_FILE);
        EuCoCCompliant = configEco.getBoolean("EuCoCCompliant");
        LEEDCertification = parseLEEDCertification(configEco.getString("LEEDCertification"));
        BREEAMCertification = parseBREEAMCertification(configEco.getString("BREEAMCertification"));
        EnergyStarRating = configEco.getInt("EnergyStarRating");
        ISO14000 = configEco.getBoolean("ISO14000");
        GreenStar = parseGreenStar(configEco.getString("GreenStar"));
        CASBEE = parseCASBEE(configEco.getString("CASBEE"));
    }

    public boolean checkCertificates(String servicemanifest) {
        Manifest manifest = Manifest.Factory.newInstance(servicemanifest);
        boolean success = true;

        log.debug("Checking certificates of service " + manifest.getVirtualMachineDescriptionSection().getServiceId());
        //Checking of certificates
        for (EcoEfficiencySection ecoSection : manifest.getTRECSection().getEcoEfficiencySectionArray()) {
            if ((ecoSection.getEuCoCCompliant() == true) && (EuCoCCompliant == false)) {
                log.info("EuCoCCompilant checking FAILED");
                success = false;
            } else {
                log.debug("EuCoCCompilant was not needed.");
            }
            if (parseLEEDCertification(ecoSection.getLEEDCertification()) > LEEDCertification) {
                log.info("LEEDCertification checking FAILED");
                success = false;
            } else {
                log.debug("LEEDCertification checking was successful");
            }
            if (parseBREEAMCertification(ecoSection.getBREEAMCertification()) > BREEAMCertification) {
                log.info("BREEAMCertification checking FAILED");
                success = false;
            } else {
                log.debug("BREEAMCertification checking was successful");
            }

            if (parseEnergyStarRating(ecoSection.getEnergyStarRating()) > EnergyStarRating) {
                log.info("EnergyStarRating checking FAILED");
                success = false;
            } else {
                log.debug("EnergyStarRating checking was successful");
            }

            if ((parseISO14000(ecoSection.getISO14000()) == true) && (ISO14000 == false)) {
                log.info("ISO14000 checking FAILED");
                success = false;
            } else {
                log.debug("ISO14000 checking was successful");
            }
            
            if (parseGreenStar(ecoSection.getGreenStar()) > GreenStar) {
                log.info("GreenStar checking FAILED");
                success = false;
            } else {
                log.debug("GreenStar checking was successful");
            }
            
            if (parseCASBEE(ecoSection.getCASBEE()) > CASBEE) {
                log.info("CASBEE checking FAILED");
                success = false;
            } else {
                log.debug("CASBEE checking was successful");
            }

        }
        return success;
    }

    private int parseLEEDCertification(String certString) {
        if (certString.equalsIgnoreCase("NotRequired")) {
            return Constants.LEED_CERT_NOTREQUIRED;
        } else if (certString.equalsIgnoreCase("Certified")) {
            return Constants.LEED_CERT_CERTIFIED;
        } else if (certString.equalsIgnoreCase("Silver")) {
            return Constants.LEED_CERT_SILVER;
        } else if (certString.equalsIgnoreCase("Gold")) {
            return Constants.LEED_CERT_GOLD;
        } else if (certString.equalsIgnoreCase("Platinum")) {
            return Constants.LEED_CERT_PLATINUM;
        } else {
            log.error("Required LEED Certification was specified incorrectly.");
            return -1;
        }
    }

    private int parseBREEAMCertification(String certString) {
        if (certString.equalsIgnoreCase("NotRequired")) {
            return Constants.BREEAM_CERT_NOTREQUIRED;
        } else if (certString.equalsIgnoreCase("Pass")) {
            return Constants.BREEAM_CERT_PASS;
        } else if (certString.equalsIgnoreCase("Good")) {
            return Constants.BREEAM_CERT_GOOD;
        } else if (certString.equalsIgnoreCase("VeryGood")) {
            return Constants.BREEAM_CERT_VERYGOOD;
        } else if (certString.equalsIgnoreCase("Excellent")) {
            return Constants.BREEAM_CERT_EXCELLENT;
        } else if (certString.equalsIgnoreCase("Outstanding")) {
            return Constants.BREEAM_CERT_OUTSTANDING;
        } else {
            log.error("Required BREEAM Certification was specified incorrectly.");
            return -1;
        }
    }

    private int parseEnergyStarRating(Object obj) {

        if (obj instanceof String) {
            if (((String) obj).equalsIgnoreCase("No")) {
                return 0;
            } else {
                log.error("Incorrect EnergyStarRating value.");
                return -1;
            }
        } else if (obj instanceof Integer) {
            return ((Integer) obj).intValue();
        } else {
            log.error("Incorrect EnergyStarRating value.");
            return -1;
        }
    }

    private boolean parseISO14000(String certString) {
        if (certString.equalsIgnoreCase("ISO14001-Compliant")) {
            return true;
        } else {
            return false;
        }
    }

    /**
     *
     * @param certString GreenStar value in the Service Manifest.
     * @return Conversed GreenStar value (from 0 to 6).
     */
    private int parseGreenStar(String certString) {

        int greenStar = -1;
        if (certString.equalsIgnoreCase("No")) {
            greenStar = 0;
        } else {
            try {
                greenStar = Integer.parseInt(certString);
            } catch (Exception ex) {
                log.error("Incorrect GreenStar value.");
                greenStar = -1;
            }
        }
        return greenStar;
    }

    /**
     * CASBEE = No / C, B-, B+, A, S
     *
     * @param certString CASBEE value in the Service Manifest
     * @return Conversed CASBEE value (from 0 to 5).
     */
    private int parseCASBEE(String certString) {

        if (certString.equalsIgnoreCase("No")) {
            return Constants.CASBEE_No;
        } else if (certString.equalsIgnoreCase("C")) {
            return Constants.CASBEE_C;
        } else if (certString.equalsIgnoreCase("B-")) {
            return Constants.CASBEE_Bminus;
        } else if (certString.equalsIgnoreCase("B+")) {
            return Constants.CASBEE_Bplus;
        } else if (certString.equalsIgnoreCase("A")) {
            return Constants.CASBEE_A;
        } else if (certString.equalsIgnoreCase("S")) {
            return Constants.CASBEE_S;
        } else {
            log.error("Required CASBEE Certification was specified incorrectly.");
            return -1;
        }
    }
}
