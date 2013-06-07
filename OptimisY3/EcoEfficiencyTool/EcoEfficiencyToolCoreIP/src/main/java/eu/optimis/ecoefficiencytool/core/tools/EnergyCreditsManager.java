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

import java.util.Iterator;
import java.util.logging.Level;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.log4j.Logger;

/**
 *
 * @author jsubirat
 */
public class EnergyCreditsManager {

    private static Logger log = Logger.getLogger(EnergyCreditsManager.class);
    private static double prevTotalPower = 0.0;
    private static double prevCO2sRate = 0.0;
    private static long prevTimestamp = 0l;

    public synchronized static boolean isDatacenterRunningOnGreen() {
        PropertiesConfiguration configEnergyCredits = ConfigManager.getPropertiesConfiguration(ConfigManager.ENERGYCREDITS_CONFIG_FILE);
        Iterator recs = configEnergyCredits.getKeys("REC");
        while (recs.hasNext()) {
            String key = (String) recs.next();
            double remainingCredit = configEnergyCredits.getDouble(key);
            if (remainingCredit > 0) {
                return true;
            }
        }
        return false;
    }
    
    public synchronized static double getCO2EmissionsExcess() {
        PropertiesConfiguration configEnergyCredits = ConfigManager.getPropertiesConfiguration(ConfigManager.ENERGYCREDITS_CONFIG_FILE);
        return configEnergyCredits.getDouble("exceededEmissions");
    }

    /**
     *
     * @param totalPower Total consumed power in Watts
     */
    public synchronized static void setCurrentPowerConsumption(double totalPower) {
        //Calculate consumed power in elapsed time.
        long now = System.currentTimeMillis();
        double consumedEnergy = prevTotalPower * ((double) (now - prevTimestamp)) / 3600000000.0;
        double emmittedCO2kg = prevCO2sRate * ((double) (now - prevTimestamp)) / 1000000.0;

        log.debug("Power at ts " + now + ": " + totalPower + "W. Consumed energy: " + consumedEnergy + "kWh. Emmited CO2: " + emmittedCO2kg + "kg.");
        try {
            setRemainingEnergyInRECs(consumedEnergy);
            setRemainingEmissionsInEUETSs(emmittedCO2kg);
        } catch (Exception ex) {
            log.error("Error while writing to energycredits.properties file.", ex);
        }
        
        prevTotalPower = totalPower;
        prevCO2sRate = CO2Converter.getCO2FromPower(totalPower);
        prevTimestamp = now;
    }

    /**
     *
     * @param consumedEnergy Consumed energy in kWh.
     */
    private static void setRemainingEnergyInRECs(double consumedEnergy) throws ConfigurationException {
        PropertiesConfiguration configEnergyCredits = ConfigManager.getPropertiesConfiguration(ConfigManager.ENERGYCREDITS_CONFIG_FILE);
        Iterator recs = configEnergyCredits.getKeys("REC");
        while (recs.hasNext()) {
            String key = (String) recs.next();
            double remainingCredit = configEnergyCredits.getDouble(key);
            if (consumedEnergy < remainingCredit) {
                remainingCredit = remainingCredit - consumedEnergy;
                configEnergyCredits.setProperty(key, Double.toString(remainingCredit));
                configEnergyCredits.save();
                return;
            } else {
                consumedEnergy = consumedEnergy - remainingCredit;
                configEnergyCredits.setProperty(key, Double.toString(0.0));
            }
        }

        if (consumedEnergy > 0.0) {
            log.warn("All REC Energy credits are exhausted");
        }
        configEnergyCredits.save();
    }

    /**
     *
     * @param emmittedCO2kg Kg of emmitted CO2.
     */
    private static void setRemainingEmissionsInEUETSs(double emmittedCO2kg) throws ConfigurationException {
        
        updateCurrentEmissionsExcess();
        
        PropertiesConfiguration configEnergyCredits = ConfigManager.getPropertiesConfiguration(ConfigManager.ENERGYCREDITS_CONFIG_FILE);

        Iterator euas = configEnergyCredits.getKeys("EUA");
        while (euas.hasNext()) {
            String key = (String) euas.next();
            double remainingCredit = configEnergyCredits.getDouble(key);
            if (emmittedCO2kg < remainingCredit) {
                remainingCredit = remainingCredit - emmittedCO2kg;
                configEnergyCredits.setProperty(key, Double.toString(remainingCredit));
                configEnergyCredits.save();
                return;
            } else {
                emmittedCO2kg = emmittedCO2kg - remainingCredit;
                configEnergyCredits.setProperty(key, Double.toString(0.0));
            }
        }

        if (emmittedCO2kg > 0.0) {
            double currentExcess = configEnergyCredits.getDouble("exceededEmissions");
            currentExcess += emmittedCO2kg;
            configEnergyCredits.setProperty("exceededEmissions", Double.toString(currentExcess));
            log.warn("Exceeding allowed CO2 emissions by " + currentExcess + "kg.");
        }
        configEnergyCredits.save();
    }

    private static void updateCurrentEmissionsExcess() throws ConfigurationException {
        PropertiesConfiguration configEnergyCredits = ConfigManager.getPropertiesConfiguration(ConfigManager.ENERGYCREDITS_CONFIG_FILE);

        double currentExcess = configEnergyCredits.getDouble("exceededEmissions");
        if (currentExcess > 0.0) {
            Iterator euas = configEnergyCredits.getKeys("EUA");
            while (euas.hasNext()) {
                String key = (String) euas.next();
                double remainingCredit = configEnergyCredits.getDouble(key);
                if (currentExcess < remainingCredit) {
                    remainingCredit = remainingCredit - currentExcess;
                    configEnergyCredits.setProperty(key, Double.toString(remainingCredit));
                    configEnergyCredits.setProperty("exceededEmissions", Double.toString(0.0));
                    configEnergyCredits.save();
                    return;
                } else {
                    currentExcess = currentExcess - remainingCredit;
                    configEnergyCredits.setProperty(key, Double.toString(0.0));
                }
            }
            if(currentExcess > 0.0) {
                configEnergyCredits.setProperty("exceededEmissions", Double.toString(currentExcess));
            }
            configEnergyCredits.save();
        }
    }
}
