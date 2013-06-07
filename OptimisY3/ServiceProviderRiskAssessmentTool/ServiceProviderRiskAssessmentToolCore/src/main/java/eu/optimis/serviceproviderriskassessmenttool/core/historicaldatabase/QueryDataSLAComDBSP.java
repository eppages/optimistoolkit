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
package eu.optimis.serviceproviderriskassessmenttool.core.historicaldatabase;

import eu.optimis.serviceproviderriskassessmenttool.core.historicaldatabase.persistence.SLAObject;
import eu.optimis.trec.common.db.sp.model.Slas;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author scsmj
 */
public class QueryDataSLAComDBSP {

    protected static Logger logger = Logger.getLogger(QueryDataSLAComDBSP.class);

    @SuppressWarnings("rawtypes")
	public static SLAObject[] getDataComDBSP(String providername) throws Exception {

        Session session = null; 

        try {
            session = eu.optimis.serviceproviderriskassessmenttool.core.historicaldatabase.sp.utils.HibernateUtil.getSessionFactory().openSession();
            //  System.out.println("4");
        } catch (Exception e) {
            //  System.out.println("3");

            logger.error("No database session could be retrieved");
            throw new Exception("No database session could be retrieved");
        }

        Transaction tx = session.beginTransaction();

        int i = 0;
        /*
         * PersistentSLAProviders provider = new PersistentSLAProviders(); //
         * System.out.println("value received " + provider.getProvider_id());
         * try { System.out.println("7"); provider = (PersistentSLAProviders)
         * session.createCriteria(PersistentSLAProviders.class).add(Restrictions.eq("provider_dn",
         * providername)).uniqueResult(); System.out.println("value received " +
         * provider.getProvider_id()); } catch (Exception e){
         * System.out.println("8"); logger.error("No provider with DN: " +
         * providername + " is not registered with the broker service, cannot
         * appendReliability"); throw new Exception("No provider with DN: " +
         * providername + " is not registered with the broker service, cannot
         * appendReliability"); } System.out.println("9");
         */
        /*
         * if(provider == null) { System.out.println("10"); logger.error("No
         * provider with DN: " + providername + " is not registered with the
         * broker service, cannot appendReliability"); throw new Exception("No
         * provider with DN: " + providername + " is not registered with the
         * broker service, cannot appendReliability");
         *
         * }
         */

        tx.begin();
        List SLAOffers = new ArrayList<Slas>();
        //    System.out.println("11");
        try {
            System.out.println("12");
            Criteria criteria = session.createCriteria(Slas.class).add(Restrictions.eq("providerDn", providername));
            SLAOffers = criteria.list();

        } catch (Exception e) {
            //System.out.println("13");
            logger.error("No SLAS from provider with DN: " + providername + " could be found");
            throw new Exception("No SLAS from provider with DN: " + providername + " could be found");
        }
        tx.commit();
        //session.close();

        // System.out.println("15");
        
        //System.out.println("14 kkk" + SLAOffers.size());
        Iterator SLAOfferIterator = SLAOffers.iterator();

        SLAObject[] slas = new SLAObject[SLAOffers.size()];
        while (SLAOfferIterator.hasNext()) {

            Slas tempSLAOffer = (Slas) SLAOfferIterator.next();

            double tempRisk = tempSLAOffer.getRisk();
            long ts = tempSLAOffer.getOfferTime().getTime();
            Timestamp tempTime = new Timestamp(ts);

            int intState = tempSLAOffer.getState();
            boolean tempState = (intState != 0);
            //System.out.println("tempRisk" + tempRisk);
            slas[i] = new SLAObject(tempRisk, tempState, tempTime);
            i++;
        }


        return slas;
    }
}
