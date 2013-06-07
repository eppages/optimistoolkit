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

public class TrecSPinfoDAO {

	final static Logger logger = LoggerFactory.getLogger(TrecSPinfoDAO.class.getName());

	public boolean addSP (String SPName, String spId) throws Exception {
		Session session = null;
		SpInfo spi = new SpInfo();
    	SessionFactory sf = HibernateUtil.getSessionFactory();
	      try {
	         session = sf.openSession();
	         Transaction tx= session.beginTransaction();
	         tx.begin();
	         	spi.setSpName(SPName);
	         	spi.setSpId(spId);
				session.save(spi);
	         tx.commit();
	         if (tx.wasCommitted()){
	        	 logger.info("Transaction commited");
	         }
	         addSNProvider(spId);
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
	
	@SuppressWarnings("unchecked")
	public SpInfo getSP(String key) throws Exception {
		Session session = null;
		List<SpInfo> results = null;
		SessionFactory sf = HibernateUtil.getSessionFactory();
		try {
			session = sf.openSession();
			Transaction tx = session.beginTransaction();
			tx.begin();
			// Search by SP Id
			Criteria criteria = session.createCriteria(SpInfo.class);
			criteria.add(Restrictions.like("spId", key));
			results = (List<SpInfo>) criteria.list();
			tx.commit();
			return results.get(results.size()-1);
		} catch (Exception e) {
			logger.error("ERROR "+ e.getMessage());
			throw new Exception(e.toString());
		}

		
	}
	


	
	@SuppressWarnings("unchecked")
	public List<SpInfo> getSPs() throws Exception {
		Session session = null;
		List<SpInfo> results = null;
		SessionFactory sf = HibernateUtil.getSessionFactory();
		try {
			session = sf.openSession();
			Transaction tx = session.beginTransaction();
			tx.begin();
			Criteria criteria = session.createCriteria(SpInfo.class);
			results = (List<SpInfo>) criteria.list();
			tx.commit();
			return results;
		} catch (Exception e) {
			logger.error("ERROR "+ e.getMessage());
			throw new Exception(e.toString());
		}

		
	}
	
	public boolean deleteSP(String sp) throws Exception {
		Session session = null;
		SessionFactory sf = HibernateUtil.getSessionFactory();
		int result = 0;
		try {
			session = sf.openSession();
			Transaction tx = session.beginTransaction();
			tx.begin();
			Query query= session.createSQLQuery("DELETE FROM sp_info WHERE sp_id =:sp OR sp_name =:sp");
			query.setParameter("sp", sp);
			result = query.executeUpdate();
			tx.commit();
			sf.close();
			deleteSNProvider(sp);
		} catch (Exception e) {
			logger.error("ERROR "+ e.getMessage());
			throw new Exception(e.toString());
		}

		if (result == 0)
			return false;
		
		return true;
	}
	
	private boolean addSNProvider(String provider) {
		String providerType = "sp";
		TrecSNProviderDAO tsnpdao = new TrecSNProviderDAO();
		try {
			tsnpdao.getSnProvider(provider, providerType);
			return true;
		} catch (Exception e) {
			try {
				return tsnpdao.addSNProvider(provider, providerType);
			} catch (Exception e1) {
				return false;
			}
		}
	}
	
	private boolean deleteSNProvider(String provider) {
		String providerType = "sp";
		TrecSNProviderDAO tsnpdao = new TrecSNProviderDAO();
		try {
			tsnpdao.deleteIP(provider,providerType);
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	
}
