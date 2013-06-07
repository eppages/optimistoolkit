/*
 *  Copyright 2013 University of Leeds
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
package eu.optimis.serviceproviderriskassessmenttool.core.historicaldatabase.dao.populate;
import java.io.File;

/**
 * This class populates the database tables used for risk
 * @author Richard Kavanagh based upon work by Mariam Kiran
 * project
 */
public class PopulateSLAData {

    public static final File CONFIG_FILE = new File("Populate.ini");

    public static void main(String[] args) {

        CreateSLAData dataGenerator = new CreateSLAData();

        String host = "https://grid-demo-6.cit.tu-berlin.de:8443/wsrf/services/";
        String provider_dn = "optimis-spvm.atosorigin.es";
        int provider_id = 1;
        int numSLA = 10000;
        int slaInterArrivalTimeMaxMinutes = 300; //5 hours
        int slaArrivalToQuotedTimeMaxVal = 20000;
        int quotedTimeMaxValToAcceptedTimeMaxVal = 1000;
        double offeredPof = 0.1;
        double truePof = 0.05;
        double price = 1000.0;
        double acceptanceProb = 1.0;
        try {
            //start of establishing settings
            Settings settings = new Settings(CONFIG_FILE);
            host = settings.getString("host", host);
            provider_dn = settings.getString("provider_dn", provider_dn);
            provider_id = settings.getInt("provider_id", provider_id);
            numSLA = settings.getInt("sla_count", numSLA);
            slaInterArrivalTimeMaxMinutes = settings.getInt("sla_inter_arrival_time_max_minutes", slaInterArrivalTimeMaxMinutes);
            quotedTimeMaxValToAcceptedTimeMaxVal = settings.getInt("quoted_time_max_val_to_accepted_time_max_val_seconds", quotedTimeMaxValToAcceptedTimeMaxVal);
            slaArrivalToQuotedTimeMaxVal = settings.getInt("sla_arrival_to_quoted_time_max_val_seconds", slaArrivalToQuotedTimeMaxVal);
            offeredPof = settings.getDouble("offered_pof", offeredPof);
            truePof = settings.getDouble("true_pof", truePof);
            price = settings.getDouble("price", price);
            acceptanceProb = settings.getDouble("offer_acceptance_probability", acceptanceProb);
            
            dataGenerator.setMaxSlaArrivalTime(slaInterArrivalTimeMaxMinutes);
            dataGenerator.setQuotedTimeMaxValToAcceptedTimeMaxVal(quotedTimeMaxValToAcceptedTimeMaxVal * 1000);
            dataGenerator.setSlaArrivalToQuotedTimeMaxVal(slaArrivalToQuotedTimeMaxVal * 1000);
            dataGenerator.setOfferedPof(offeredPof);
            dataGenerator.setTruePof(truePof);
            dataGenerator.setPrice(price);
            dataGenerator.setAcceptanceProbability(acceptanceProb);

            if (settings.isChanged()) {
                settings.save(CONFIG_FILE);
            }
            String agreementFactoryURL = host + "ProviderAgreementFactory";
            //end of establishing settings
            System.out.println("Data Creation Starting:");
            boolean successful = dataGenerator.addDataToDatabase(provider_id, provider_dn, agreementFactoryURL, numSLA);
            System.out.println("Data added successfully: " + successful);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}