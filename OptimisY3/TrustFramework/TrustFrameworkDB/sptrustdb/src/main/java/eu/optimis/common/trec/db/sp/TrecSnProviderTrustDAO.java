/**

Copyright 2013 ATOS SPAIN S.A.

Licensed under the Apache License, Version 2.0 (the License);
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

Authors :
Juan Luis Prieto, Francisco Javier Nieto. Atos Research and Innovation, Atos SPAIN SA
@email francisco.nieto@atosresearch.eu 

**/

package eu.optimis.common.trec.db.sp;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.optimis.common.trec.db.sp.utils.HibernateUtil;
import eu.optimis.trec.common.db.sp.model.SnTrustProvider;
import eu.optimis.trec.common.db.sp.model.SnProviderId;

public class TrecSnProviderTrustDAO {

	private final Logger logger = LoggerFactory.getLogger(TrecSnProviderTrustDAO.class
			.getName());

	public boolean addSnProviderTrust(String providerId, String providerType,
			double expectation, double belief, double disbelief,
			double uncertinty, double relativeAutomicity) throws Exception {
		Session session = null;
		SnTrustProvider sntp = new SnTrustProvider();
		SessionFactory sf = HibernateUtil.getSessionFactory();
		try {
			session = sf.openSession();
			Transaction tx = session.beginTransaction();
			tx.begin();
			sntp.setId(null);
			sntp.setProviderId(providerId);
			sntp.setProviderType(providerType);
			sntp.setExpectation(expectation);
			sntp.setBelief(belief);
			sntp.setDisbelief(disbelief);
			sntp.setUncertinty(uncertinty);
			sntp.setRelativeAutomicity(relativeAutomicity);
			session.save(sntp);
			tx.commit();
			if (tx.wasCommitted()) {
				logger.info("Transaction commited");
			}
		} catch (Exception e) {
			logger.info("ERROR " + e.getMessage());
			throw new Exception(e.toString());
		} finally {
			if (session != null) {
				try {
					session.close();
				} catch (HibernateException e) {
				}
			}
		}
		sf.close();
		return true;
	}

	@SuppressWarnings("unchecked")
	public SnTrustProvider getSnProviderTrust(String providerId,
			String providerType) throws Exception {
		Session session = null;
		List<SnTrustProvider> results = null;
		SessionFactory sf = HibernateUtil.getSessionFactory();
		try {
			session = sf.openSession();
			Transaction tx = session.beginTransaction();
			tx.begin();
			// Search by IP Name
			Criteria criteria = session.createCriteria(SnTrustProvider.class);
			criteria.add(Restrictions.like("id",
					setSnTrustProviderId(providerId, providerType)));
			results = (List<SnTrustProvider>) criteria.list();
			tx.commit();
			return results.get(results.size()-1);
		} catch (Exception e) {
			logger.error("ERROR " + e.getMessage());
			throw new Exception(e.toString());
		}

		
	}
	
	@SuppressWarnings("unchecked")
	public List<SnTrustProvider> getSnProvidersTrust(String providerId,
			String providerType) throws Exception {
		Session session = null;
		List<SnTrustProvider> results = null;
		SessionFactory sf = HibernateUtil.getSessionFactory();
		try {
			session = sf.openSession();
			Transaction tx = session.beginTransaction();
			tx.begin();
			// Search by IP Name
			Criteria criteria = session.createCriteria(SnTrustProvider.class);
			criteria.add(Restrictions.like("id",
					setSnTrustProviderId(providerId, providerType)));
			results = (List<SnTrustProvider>) criteria.list();
			tx.commit();
			return results;
		} catch (Exception e) {
			logger.error("ERROR " + e.getMessage());
			throw new Exception(e.toString());
		}

		
	}

	private SnProviderId setSnTrustProviderId(String providerId,
			String providerType) {
		return new SnProviderId(providerId, providerType);
	}
}
