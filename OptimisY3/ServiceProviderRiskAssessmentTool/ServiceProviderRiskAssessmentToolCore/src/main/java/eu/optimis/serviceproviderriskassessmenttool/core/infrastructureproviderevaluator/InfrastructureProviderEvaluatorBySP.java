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
package eu.optimis.serviceproviderriskassessmenttool.core.infrastructureproviderevaluator;

import eu.optimis.trec.common.db.sp.model.Dsahpproviders;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.Transaction;

public class InfrastructureProviderEvaluatorBySP {

    protected static Logger logger = Logger.getLogger(InfrastructureProviderEvaluatorBySP.class);
    

    public InfrastructureProviderEvaluatorBySP() {
    }

    @SuppressWarnings("unchecked")
	public DsAhpProviderObject[] rankInfrastructureProviderComDBSP(Object[] providersArray) throws Exception {

        EndUserRankingObject userranks = new EndUserRankingObject();
        userranks.setBusinessStabilityRank(9.0);
        userranks.setPastPerformanceRank(1.0);
        userranks.setSecurityRank(0.1);
        userranks.setCertandstdRank(0.2);
        userranks.setPrivacyRank(0.3);
        userranks.setGeographyRank(0.4);
        userranks.setInfrastructureRank(0.5);

        UserPreferenceObject prefs = EndUserRankToPreference.computePreferences(userranks);

        DsAhpProviderObject[] providers = new DsAhpProviderObject[providersArray.length];

        for (int i = 0; i < providersArray.length; i++) {
            Random generator = new Random();
            DsAhpProviderObject provider = new DsAhpProviderObject();
            provider.setDistName((String) providersArray[i]);
            provider.setPastPerf(generator.nextDouble());
            provider.setSecurity(generator.nextDouble());
            provider.setBusinessStability(generator.nextDouble());
            provider.setCertandstd(generator.nextDouble());
            provider.setPrivacy(generator.nextDouble());
            provider.setGeography(generator.nextDouble());
            provider.setInfrastructure(generator.nextDouble());
            providers[i] = provider;
        }
        
        Session session = null;
        try {
            session = eu.optimis.serviceproviderriskassessmenttool.core.historicaldatabase.sp.utils.HibernateUtil.getSessionFactory().openSession();
        } catch (Exception e) {
            e.printStackTrace();
            //System.out.println("Exception received: " + e.toString());
            //throw new Exception("No database session could be retrieved"+ e.getMessage());

        }
        Transaction tx = session.beginTransaction();
        tx.begin();
        Criteria criteria = session.createCriteria(Dsahpproviders.class);
        List<Dsahpproviders> ReferenceList = (List<Dsahpproviders>) criteria.list();
        tx.commit();
        //session.close();
        Iterator<Dsahpproviders> ReferenceIterator = ReferenceList.iterator();

        ArrayList<DsAhpProviderObject> References = new ArrayList<DsAhpProviderObject>();
        
        while (ReferenceIterator.hasNext()) {

            DsAhpProviderObject Reference = new DsAhpProviderObject();
            Dsahpproviders d = (Dsahpproviders) ReferenceIterator.next();
            Reference.setDistName(d.getDistName());
            Reference.setPastPerf(d.getPastPerf());
            Reference.setBusinessStability(d.getSecurity());
            Reference.setSecurity(d.getSecurity());
            Reference.setCertandstd(d.getCertandstd());
            Reference.setPrivacy(d.getPrivacy());
            Reference.setGeography(d.getGeography());
            Reference.setInfrastructure(d.getInfrastructure());            
            Reference.setBelief(d.getBelief());
            Reference.setPlausibility(d.getPlausibility());
            Reference.setRating(d.getRating());  
            Reference.setT_50(d.getT50());
            Reference.setT_95(d.getT95());
            References.add(Reference);
        }
        DsAhpProviderObject[] references_from_hdb = (DsAhpProviderObject[]) References.toArray(new DsAhpProviderObject[]{});
        DsAhpAbsoluteRank provRank = new DsAhpAbsoluteRank();
        DsAhpProviderObject[] rankedProviders = new DsAhpProviderObject[providersArray.length];
        rankedProviders = provRank.computeAbsoluteRank(prefs, providers, references_from_hdb);
        return rankedProviders;
    }
}