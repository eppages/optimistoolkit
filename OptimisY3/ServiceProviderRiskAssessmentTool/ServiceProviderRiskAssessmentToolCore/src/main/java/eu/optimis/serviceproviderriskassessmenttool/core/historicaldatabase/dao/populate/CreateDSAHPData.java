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

import eu.optimis.trec.common.db.sp.model.Dsahpproviders;
import java.util.Random;
import org.hibernate.Session;
import org.hibernate.Transaction;

public class CreateDSAHPData {

    public boolean addDSAHPProviderDataToDatabase(int numEntries) {


        Session session = eu.optimis.serviceproviderriskassessmenttool.core.historicaldatabase.sp.utils.HibernateUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();

        Random generateCriteria = new Random();

        double meanPastPerf = 0.97;
        double sigmaPastPerf = 0.0015;
        double meanExp = 0.7;
        double sigmaExp = 0.02;

        try {

            for (int i = 0; i < numEntries; i++) {

                Dsahpproviders newObject = new Dsahpproviders();

                newObject.setDistName("atos"+i);

                newObject.setPastPerf(meanPastPerf + sigmaPastPerf * generateCriteria.nextGaussian());

                newObject.setBusinessStability(meanExp + sigmaExp * generateCriteria.nextGaussian());

                if (generateCriteria.nextDouble() < 0.5) {
                    newObject.setSecurity(0.0);
                    newObject.setCertandstd(0.1);
                    newObject.setPrivacy(0.2);
                    newObject.setGeography(0.3);
                    newObject.setInfrastructure(0.4);
                } else {
                    newObject.setSecurity(1.0);
                    newObject.setCertandstd(0.9);
                    newObject.setPrivacy(0.8);
                    newObject.setGeography(0.7);
                    newObject.setInfrastructure(0.6);
                }
                
                newObject.setBelief(meanExp + sigmaExp * generateCriteria.nextGaussian());
                newObject.setPlausibility(meanExp + sigmaExp * generateCriteria.nextGaussian());
                newObject.setRating(meanExp + sigmaExp * generateCriteria.nextGaussian());
                newObject.setT50(10);
                newObject.setT95(20);
                
                session.save(newObject);

            }

            tx.commit();

        } catch (Exception e) {
            System.out.println(e.getMessage());
            tx.rollback();
            return false;
        }

        //session.close();

        return true;
    }
}