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
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.log4j.Logger;

/**
 *
 * @author jsubirat
 */
public class CO2Converter {
    
    protected static Logger log = Logger.getLogger(CO2Converter.class);
    
    /**
     * Returns the amount of CO2 gr. emmited to the atmosphere in order to
     * produce the amount of power specified by power. This function takes into
     * account the nature of the energy being consumed by the datacenter
     * facilities (i.e. brown/green, having a 0 CO2 footpring for green energy)
     * and, among the brown energy, its origin (coal, gas, fuel, etc.). It also
     * takes into account the presence of energy credits acquired by the
     * Infrastructure Provider.
     *
     * @param power Amount of power expressed in Watts.
     * @return
     */
    public static double getCO2FromPower(double power) {
        
        //A tiny amount of CO2 is returned (to avoid having Infinity ecological efficiency values)
        if(EnergyCreditsManager.isDatacenterRunningOnGreen()) {
            return 0.000001;
        }
        
        PropertiesConfiguration configEnergy = ConfigManager.getPropertiesConfiguration(ConfigManager.ENERGY_SOURCES_CONFIG_FILE);

        //Calculate which part of the consumed power is brown (green has no emissions).
        double brownPower = power * configEnergy.getDouble("brown.percentage") / 100.0;
        
        //Calculate the amount of gr of CO2 emitted to produce this brown energy.
        double totalGrS = 0.0, totalPercentage = 0.0;
        Iterator it = configEnergy.getKeys("emissions");
        while(it.hasNext()) {
            String key = (String) it.next();
            String energySource = key.split("\\.")[1];
            
            double emissions = ((double)configEnergy.getInt(key)) / 3600000.0;  //From gr/(kW*h) to gr/(W*s)
            Double percentage = configEnergy.getDouble("percentage." + energySource);
            if(percentage == null) {
                log.warn("Percentage associated to " + energySource + " was not found.");
                continue;
            }
            totalPercentage += percentage;
            totalGrS += percentage.doubleValue() * brownPower * emissions;
        }
        if(totalPercentage != 100.0) {
            log.warn("Total percentage wasn't 100.0. Check the energysources.properties configuration file.");
        }
        log.debug("Emitted " + totalGrS + " gr/s of CO2 to deliver " + power + "W of power (" + brownPower + "W brown).");
        
        return totalGrS;
    }
}
