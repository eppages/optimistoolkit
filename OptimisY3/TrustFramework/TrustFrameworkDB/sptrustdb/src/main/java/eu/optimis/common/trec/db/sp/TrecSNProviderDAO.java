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
import eu.optimis.trec.common.db.sp.model.SnProvider;
import eu.optimis.trec.common.db.sp.model.SnProviderId;

public class TrecSNProviderDAO {

	private final Logger logger = LoggerFactory.getLogger(TrecIPinfoDAO.class.getName());
	
	public boolean addSNProvider(String providerId, String providerType) throws Exception{
		Session session = null;
		SnProvider snp = new SnProvider();
    	SessionFactory sf = HibernateUtil.getSessionFactory();
	      try {
	         session = sf.openSession();
	         Transaction tx= session.beginTransaction();
	         tx.begin();
	         	snp.setId(setSnProviderId(providerId, providerType));
				session.save(snp);
	         tx.commit();
	         if (tx.wasCommitted()){
	        	 logger.info("Transaction commited");
	         }
	      }
	      catch (Exception e) {
			 logger.info("ERROR "+ e.getMessage());
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
	
	public SnProvider getSnProvider(String providerId, String providerType) throws Exception{
		Session session = null;
		List<SnProvider> results = null;
		SessionFactory sf = HibernateUtil.getSessionFactory();
		try {
			session = sf.openSession();
			Transaction tx = session.beginTransaction();
			tx.begin();
			// Search by IP Name
			Criteria criteria = session.createCriteria(SnProvider.class);
			criteria.add(Restrictions.like("id", setSnProviderId(providerId, providerType)));
			results = (List<SnProvider>) criteria.list();
			tx.commit();
			return results.get(results.size()-1);
		} catch (Exception e) {
			logger.error("ERROR "+ e.getMessage());
			throw new Exception(e.toString());
		}

		
	}
	
	public List<SnProvider> getSnProviders(String providerId, String providerType) throws Exception{
		Session session = null;
		List<SnProvider> results = null;
		SessionFactory sf = HibernateUtil.getSessionFactory();
		try {
			session = sf.openSession();
			Transaction tx = session.beginTransaction();
			tx.begin();
			// Search by IP Name
			Criteria criteria = session.createCriteria(SnProvider.class);
			criteria.add(Restrictions.like("id", setSnProviderId(providerId, providerType)));
			results = (List<SnProvider>) criteria.list();
			tx.commit();
			return results;
		} catch (Exception e) {
			logger.error("ERROR "+ e.getMessage());
			throw new Exception(e.toString());
		}

		
	}
	
	public boolean deleteProvider(String providerId, String providerType) throws Exception {
		Session session = null;
		SessionFactory sf = HibernateUtil.getSessionFactory();
		int result = 0;
		try {
			session = sf.openSession();
			Transaction tx = session.beginTransaction();
			tx.begin();
			Query query= session.createSQLQuery("DELETE FROM sn_provider WHERE provider_id =:providerId AND provider_type=:providerType");
			query.setParameter("providerId", providerId);
			query.setParameter("providerType", providerType);
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
	
	private SnProviderId setSnProviderId(String providerId, String providerType){
		return new SnProviderId(providerId,providerType);
	}
}
