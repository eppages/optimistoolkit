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
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.optimis.common.trec.db.sp.utils.HibernateUtil;
import eu.optimis.trec.common.db.sp.model.SnTrustRelationship;

public class TrecSnRelationshipTrustDAO {
	
	private final Logger logger = LoggerFactory.getLogger(TrecSnRelationshipTrustDAO.class
			.getName());

	public boolean addSnRelationshipTrust(String origin, String originType, String destiny, String destinyType,
			double expectation, double belief, double disbelief,
			double uncertinty, double relativeAutomicity) throws Exception {
		Session session = null;
		SnTrustRelationship sntr = new SnTrustRelationship();
		SessionFactory sf = HibernateUtil.getSessionFactory();
		try {
			session = sf.openSession();
			Transaction tx = session.beginTransaction();
			tx.begin();
			sntr.setId(null);
			sntr.setOrigin(origin);
			sntr.setOriginType(originType);
			sntr.setDestiny(destiny);
			sntr.setDestinyType(destinyType);
			sntr.setExpectation(expectation);
			sntr.setBelief(belief);
			sntr.setDisbelief(disbelief);
			sntr.setUncertinty(uncertinty);
			sntr.setRelativeAutomicity(relativeAutomicity);
			session.save(sntr);
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
	public SnTrustRelationship getSnRelationshipByOrigin(String origin, String originType) throws Exception{
		Session session = null;
		List<SnTrustRelationship> results = null;
		SessionFactory sf = HibernateUtil.getSessionFactory();
		try {
			session = sf.openSession();
			Transaction tx = session.beginTransaction();
			tx.begin();
			// Search by IP Name
			Criteria criteria = session.createCriteria(SnTrustRelationship.class);
//			criteria.add(Restrictions.like("origin", origin));
//			criteria.add(Restrictions.like("originType", originType));
			criteria.add(Restrictions.and(Restrictions.like("origin", origin), Restrictions.like("originType", originType)));
			results = (List<SnTrustRelationship>) criteria.list();
			tx.commit();
			return results.get(results.size()-1);
		} catch (Exception e) {
			logger.error("ERROR "+ e.getMessage());
			throw new Exception(e.toString());
		}

		
	}
	
	@SuppressWarnings("unchecked")
	public List<SnTrustRelationship> getSnRelationshipsByOrigin(String origin, String originType) throws Exception{
		Session session = null;
		List<SnTrustRelationship> results = null;
		SessionFactory sf = HibernateUtil.getSessionFactory();
		try {
			session = sf.openSession();
			Transaction tx = session.beginTransaction();
			tx.begin();
			// Search by IP Name
			Criteria criteria = session.createCriteria(SnTrustRelationship.class);
//			criteria.add(Restrictions.like("origin", origin));
//			criteria.add(Restrictions.like("originType", originType));
			criteria.add(Restrictions.and(Restrictions.like("origin", origin), Restrictions.like("originType", originType)));
			results = (List<SnTrustRelationship>) criteria.list();
			tx.commit();
			return results;
		} catch (Exception e) {
			logger.error("ERROR "+ e.getMessage());
			throw new Exception(e.toString());
		}

		
	}
	
	@SuppressWarnings("unchecked")
	public SnTrustRelationship getSnRelationshipByDestiny(String destiny, String destinyType) throws Exception{
		Session session = null;
		List<SnTrustRelationship> results = null;
		SessionFactory sf = HibernateUtil.getSessionFactory();
		try {
			session = sf.openSession();
			Transaction tx = session.beginTransaction();
			tx.begin();
			// Search by IP Name
			Criteria criteria = session.createCriteria(SnTrustRelationship.class);
//			criteria.add(Restrictions.like("origin", origin));
//			criteria.add(Restrictions.like("originType", originType));
			criteria.add(Restrictions.and(Restrictions.like("destiny", destiny), Restrictions.like("destinyType", destinyType)));
			results = (List<SnTrustRelationship>) criteria.list();
			tx.commit();
			return results.get(results.size()-1);
		} catch (Exception e) {
			logger.error("ERROR "+ e.getMessage());
			throw new Exception(e.toString());
		}

		
	}
	
	@SuppressWarnings("unchecked")
	public List<SnTrustRelationship> getSnRelationshipsByDestiny(String destiny, String destinyType) throws Exception{
		Session session = null;
		List<SnTrustRelationship> results = null;
		SessionFactory sf = HibernateUtil.getSessionFactory();
		try {
			session = sf.openSession();
			Transaction tx = session.beginTransaction();
			tx.begin();
			// Search by IP Name
			Criteria criteria = session.createCriteria(SnTrustRelationship.class);
//			criteria.add(Restrictions.like("origin", origin));
//			criteria.add(Restrictions.like("originType", originType));
			criteria.add(Restrictions.and(Restrictions.like("destiny", destiny), Restrictions.like("destinyType", destinyType)));
			results = (List<SnTrustRelationship>) criteria.list();
			tx.commit();
			return results;
		} catch (Exception e) {
			logger.error("ERROR "+ e.getMessage());
			throw new Exception(e.toString());
		}

		
	}
	
	@SuppressWarnings("unchecked")
	public SnTrustRelationship getSnRelationshipByOriginAndDestiny(String origin, String originType, String destiny, String destinyType) throws Exception{
		Session session = null;
		List<SnTrustRelationship> results = null;
		SessionFactory sf = HibernateUtil.getSessionFactory();
		try {
			session = sf.openSession();
			Transaction tx = session.beginTransaction();
			tx.begin();
			// Search by IP Name
			Criteria criteria = session.createCriteria(SnTrustRelationship.class);
//			criteria.add(Restrictions.like("origin", origin));
//			criteria.add(Restrictions.like("originType", originType));
			criteria.add(Restrictions.and(Restrictions.like("origin", origin), Restrictions.like("originType", originType)));
			criteria.add(Restrictions.and(Restrictions.like("destiny", destiny), Restrictions.like("destinyType", destinyType)));
			results = (List<SnTrustRelationship>) criteria.list();
			tx.commit();
			return results.get(results.size()-1);
		} catch (Exception e) {
			logger.error("ERROR "+ e.getMessage());
			throw new Exception(e.toString());
		}

		
	}
	
	@SuppressWarnings("unchecked")
	public List<SnTrustRelationship> getSnRelationshipsByOriginAndDestiny(String origin, String originType, String destiny, String destinyType) throws Exception{
		Session session = null;
		List<SnTrustRelationship> results = null;
		SessionFactory sf = HibernateUtil.getSessionFactory();
		try {
			session = sf.openSession();
			Transaction tx = session.beginTransaction();
			tx.begin();
			// Search by IP Name
			Criteria criteria = session.createCriteria(SnTrustRelationship.class);
//			criteria.add(Restrictions.like("origin", origin));
//			criteria.add(Restrictions.like("originType", originType));
			criteria.add(Restrictions.and(Restrictions.like("origin", origin), Restrictions.like("originType", originType)));
			criteria.add(Restrictions.and(Restrictions.like("destiny", destiny), Restrictions.like("destinyType", destinyType)));
			results = (List<SnTrustRelationship>) criteria.list();
			tx.commit();
			return results;
		} catch (Exception e) {
			logger.error("ERROR "+ e.getMessage());
			throw new Exception(e.toString());
		}

		
	}

	public boolean deleteSnRelationshipByOrigin(String origin, String originType) throws Exception {
		Session session = null;
		SessionFactory sf = HibernateUtil.getSessionFactory();
		int result = 0;
		try {
			session = sf.openSession();
			Transaction tx = session.beginTransaction();
			tx.begin();
			Query query= session.createSQLQuery("DELETE FROM sn_trust_relationship WHERE origin=:origin AND origin_type=:originType");
			query.setParameter("origin", origin);
			query.setParameter("originType", originType);
			result = query.executeUpdate();
			tx.commit();
			sf.close();
		} catch (Exception e) {
			logger.error("ERROR "+ e.getMessage());
			throw new Exception(e.toString());
		}

		if (result == 0)
			return false;
		
		return true;
	}
	
	public boolean deleteSnRelationshipByDestiny(String destiny, String destinyType) throws Exception {
		Session session = null;
		SessionFactory sf = HibernateUtil.getSessionFactory();
		int result = 0;
		try {
			session = sf.openSession();
			Transaction tx = session.beginTransaction();
			tx.begin();
			Query query= session.createSQLQuery("DELETE FROM sn_trust_relationship WHERE destiny=:destiny AND destiny_type=:destinyType");
			query.setParameter("destiny", destiny);
			query.setParameter("destinyType", destinyType);
			result = query.executeUpdate();
			tx.commit();
			sf.close();
		} catch (Exception e) {
			logger.error("ERROR "+ e.getMessage());
			throw new Exception(e.toString());
		}

		if (result == 0)
			return false;
		
		return true;
	}
	
}
