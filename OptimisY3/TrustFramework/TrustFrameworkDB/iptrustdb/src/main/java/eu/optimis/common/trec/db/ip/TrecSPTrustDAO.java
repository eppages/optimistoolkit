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

package eu.optimis.common.trec.db.ip;

import java.util.Date;
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

import eu.optimis.common.trec.db.ip.utils.HibernateUtil;
import eu.optimis.trec.common.db.ip.model.SpInfo;
import eu.optimis.trec.common.db.ip.model.SpTrust;

public class TrecSPTrustDAO {
	private final Logger logger = LoggerFactory.getLogger(TrecSPTrustDAO.class.getName());

	private static final int MAX_RESULTS = 100;
	public boolean addSp (String spId, double spTrust) throws Exception {
		Session session = null;
		SpTrust sptrust = new SpTrust();
    	SessionFactory sf = HibernateUtil.getSessionFactory();
	      try {
	         session = sf.openSession();
	         Transaction tx= session.beginTransaction();
	         tx.begin();
	         sptrust.setId(null);
	         	sptrust.setSpInfo(getSpInfo(spId));
	         	sptrust.setSpTrust(spTrust);
	         	sptrust.setTstamp(new Date());
	         	session.save(sptrust);
	         tx.commit();
	         if (tx.wasCommitted()){
	        	 logger.info("Transaction commited");
//	        	 System.out.println("Transaction commited");
	         }
	      }
	      catch (Exception e) {
			 logger.info("ERROR "+ e.getMessage());
//			 System.out.println("ERROR "+ e.getMessage());
			 throw new Exception(e.toString());
			 }	
	      finally {
	    	  if (session != null) {
		            try {
		               session.close();
		            }
		            catch (HibernateException e) {
		            }
		         }
		      }
	      sf.close();		
	      return true;
	}
	
	@SuppressWarnings("unchecked")
	public SpTrust getSPTrust(String key) throws Exception {
		Session session = null;
		List<SpTrust> results = null;
		SessionFactory sf = HibernateUtil.getSessionFactory();
		try {
			session = sf.openSession();
			Transaction tx = session.beginTransaction();
			tx.begin();
			// Search by IP Name
			Criteria criteria = session.createCriteria(SpTrust.class);
			criteria.add(Restrictions.like("spInfo", getSpInfo(key)));
			results = (List<SpTrust>) criteria.list();
			tx.commit();
			return results.get(results.size()-1);
		} catch (Exception e) {
			logger.error("ERROR "+ e.getMessage());
			throw new Exception(e.toString());
		}

		
	}
	
	@SuppressWarnings("unchecked")
	public List<SpTrust> getSPTrusts(String key) throws Exception {
		Session session = null;
		List<SpTrust> results = null;
		SessionFactory sf = HibernateUtil.getSessionFactory();
		try {
			session = sf.openSession();
			Transaction tx = session.beginTransaction();
			tx.begin();
			// Search by IP Name
			Criteria criteria = session.createCriteria(SpTrust.class);
			criteria.add(Restrictions.like("spInfo", getSpInfo(key)));
			criteria.setMaxResults(MAX_RESULTS);
			results = (List<SpTrust>) criteria.list();
			tx.commit();
			return results;
		} catch (Exception e) {
			logger.error("ERROR "+ e.getMessage());
//			System.out.println("ERROR "+ e.getMessage());
			throw new Exception(e.toString());
		}

		
	}
	
	public boolean deleteSPTrust(String sp) throws Exception {
		Session session = null;
		SessionFactory sf = HibernateUtil.getSessionFactory();
		int result = 0;
		try {
			session = sf.openSession();
			Transaction tx = session.beginTransaction();
			tx.begin();
			Query query= session.createSQLQuery("DELETE FROM sp_trust WHERE sp_id =:sp");
			query.setParameter("sp", sp);
			result = query.executeUpdate();
			tx.commit();
			sf.close();
		} catch (Exception e) {
			logger.error("ERROR "+ e.getMessage());
//			System.out.println("ERROR "+ e.getMessage());
			throw new Exception(e.toString());
		}

		if (result == 0)
			return false;
		
		return true;
	}
	
	private SpInfo getSpInfo(String spId) throws Exception{
		TrecSPinfoDAO tspiDAO = new TrecSPinfoDAO();
		return tspiDAO.getSP(spId);
	} 
}
