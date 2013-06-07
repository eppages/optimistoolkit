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
package eu.optimis.infrastructureproviderriskassessmenttool.core.serviceproviderevaluator;

import eu.optimis.trec.common.db.ip.model.Dsahpproviders;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.Transaction;

/**
 *
 * @author scsmj
 */
public class ServiceProviderEvaluatorByIP {
	protected static Logger log = Logger.getLogger(ServiceProviderEvaluatorByIP.class);
    public ServiceProviderEvaluatorByIP() {        
    }

    public DsAhpProviderObject[] rankServiceProviderComDBIP(Object[] providersArray) throws Exception {

        EndUserRankingObject userranks = new EndUserRankingObject();
        userranks.setBusinessStabilityRank(9.0);
        userranks.setPastPerformanceRank(1.0);
        userranks.setSecurityRank(0.0);

        UserPreferenceObject prefs = EndUserRankToPreference.computePreferences(userranks);

        DsAhpProviderObject[] providers = new DsAhpProviderObject[providersArray.length];

        for (int i = 0; i < providersArray.length; i++) {
            Random generator = new Random();
            DsAhpProviderObject provider = new DsAhpProviderObject();
            provider.setDistName((String) providersArray[i]);
            provider.setPastPerf(generator.nextDouble());
            provider.setSecurity(generator.nextDouble());
            provider.setBusinessStability(generator.nextDouble());
            providers[i] = provider;
        }
        
        
        Session session = null;
        try {
            session = eu.optimis.infrastructureproviderriskassessmenttool.core.historicaldatabase.ip.utils.HibernateUtil.getSessionFactory().openSession();
        } catch (Exception e) {
            e.printStackTrace();
            //System.out.println("Exception received: " + e.toString());
            //throw new Exception("No database session could be retrieved"+ e.getMessage());
            
        }
        Transaction tx = session.beginTransaction();
        tx.begin();
        Criteria criteria = session.createCriteria(Dsahpproviders.class);
        @SuppressWarnings("unchecked") //FIXME
		List<Dsahpproviders> ReferenceList = (List<Dsahpproviders>) criteria.list();
        tx.commit();
        //session.close();
        Iterator<Dsahpproviders> ReferenceIterator = ReferenceList.iterator();

        ArrayList<DsAhpProviderObject> References = new ArrayList<DsAhpProviderObject>();

        while (ReferenceIterator.hasNext()) {
            
            DsAhpProviderObject Reference =  new DsAhpProviderObject();
            Dsahpproviders d = (Dsahpproviders)ReferenceIterator.next();
            Reference.setBelief(d.getBelief());
            Reference.setBusinessStability(d.getBusinessStability());
            Reference.setDistName(d.getDistName());
            Reference.setPastPerf(d.getPastPerf());
            Reference.setPlausibility(d.getPlausibility());
            Reference.setRating(d.getRating());
            Reference.setSecurity(d.getSecurity());
            Reference.setBelief(d.getBelief());
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