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
package eu.optimis.ecoefficiencytool.core;

import eu.optimis.ecoefficiencytool.core.tools.ConfigManager;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.log4j.Logger;

/**
 *
 * @author jsubirat
 */
public class ProactiveService extends Thread{

    protected static Logger log = Logger.getLogger(ProactiveService.class);
    private EcoEffAssessorSP assessorSP = null;
    private String serviceId;
    //private List<Integer> historic;
    private long timeout;
    private boolean finish = false;
    
    public ProactiveService(EcoEffAssessorSP assessor, String serviceId, /*List<Integer> historic,*/ Long timeout) {
        
        PropertiesConfiguration configEco = ConfigManager.getPropertiesConfiguration(ConfigManager.ECO_CONFIG_FILE);
        
        this.assessorSP = assessor;
        this.serviceId = serviceId;
        //this.historic = historic;
        if(timeout != null) {
            this.timeout = timeout.longValue();
        } else {
            this.timeout = configEco.getLong("samplingPeriod");
        }
    }

    public void stopProactiveService() {
        this.finish = true;
    }
    
    public String getServiceId() {
        return this.serviceId;
    }

    @Override
    public void run() {
        while(!finish) {
            try {

                log.info("ECO: Starting Proactive Service EcoAssessment (" + serviceId + ")");
                double ecoService[] = assessorSP.assessServiceEcoEfficiency(serviceId);
                log.info("ECO: Proactive Service EcoAssessment (" + serviceId + "). Ener.Eff: " + ecoService[0] + " Ecol.Eff: "+ ecoService[1]);

                Thread.sleep(timeout);
            } catch (Exception ex) {
                log.error("ECO: PROACTIVE SERVICE EXCEPTION. Error while assessing ecoefficiency of service:  " + serviceId);
                ex.printStackTrace();
                try {
                    Thread.sleep(timeout);
                } catch (InterruptedException ex2) {
                    log.error("ECO: Error while performing sleep.");
                    ex2.printStackTrace();
                }
            }
        }
        log.info("ECO: Stopping Proactive Ecoassessment of Service " + serviceId);
    }
}
