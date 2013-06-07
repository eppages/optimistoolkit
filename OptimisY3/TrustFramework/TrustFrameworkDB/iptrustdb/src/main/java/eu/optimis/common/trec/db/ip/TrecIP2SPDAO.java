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
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.optimis.common.trec.db.ip.utils.HibernateUtil;
import eu.optimis.trec.common.db.ip.model.IpInfo;
import eu.optimis.trec.common.db.ip.model.IpToSp;
import eu.optimis.trec.common.db.ip.model.ServiceComponent;
import eu.optimis.trec.common.db.ip.model.SpInfo;

public class TrecIP2SPDAO {

	private final Logger logger = LoggerFactory.getLogger(TrecIP2SPDAO.class
			.getName());
	private static final int MAX_RESULTS = 100;

	public boolean addIP2SP(String serviceId, String componentId, String spId,
			String ipId, double serviceTime, double serviceRisk, double security,
			double reliability, double performance, double legal,
			double serviceTrust) throws Exception {
		Session session = null;
		IpToSp ip2sp = new IpToSp();
		SessionFactory sf = HibernateUtil.getSessionFactory();
		try {
			session = sf.openSession();
			Transaction tx = session.beginTransaction();
			tx.begin();
				ip2sp.setServiceId(serviceId);
				ip2sp.setDate(new Date());
				ip2sp.setServiceComponent(getServiceComponent(componentId));
				ip2sp.setSpInfo(getSpInfo(spId));
				ip2sp.setIpInfo(getIpInfo(ipId));
				ip2sp.setServiceTime(serviceTime);
				ip2sp.setServiceRisk(serviceRisk);
				ip2sp.setSercurityAssessment(security);
				ip2sp.setPerformance(performance);
				ip2sp.setLegalOpeness(legal);
				ip2sp.setServiceTrust(serviceTrust);
				ip2sp.setServiceReliability(reliability);
				session.save(ip2sp);
			tx.commit();
			if (tx.wasCommitted()) {
				logger.info("Transaction commited");				
			}
		} catch (Exception e) {
			logger.info("ERROR " + e.getMessage());			
			throw e;
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
	public IpToSp getLastIP2SPTrust(String serviceId) throws Exception{
		Session session = null;
		List<IpToSp> results = null;
		SessionFactory sf = HibernateUtil.getSessionFactory();
		try {
			session = sf.openSession();
			Transaction tx = session.beginTransaction();
			tx.begin();
			// Search by IP Name
			Criteria criteria = session.createCriteria(IpToSp.class);
			criteria.add(Restrictions.like("serviceId", serviceId));
			results = (List<IpToSp>) criteria.list();
			tx.commit();
			return results.get(results.size()-1);
		} catch (Exception e) {
			logger.error("ERROR "+ e.getMessage());
			throw new Exception(e.toString());
		}
	}
	
	@SuppressWarnings("unchecked")
	public List<IpToSp> getIP2SPTrust(String serviceId) throws Exception{
		Session session = null;
		List<IpToSp> results = null;
		SessionFactory sf = HibernateUtil.getSessionFactory();
		try {
			session = sf.openSession();
			Transaction tx = session.beginTransaction();
			tx.begin();
			// Search by IP Name
			Criteria criteria = session.createCriteria(IpToSp.class);
			criteria.add(Restrictions.like("serviceId", serviceId));
			criteria.setMaxResults(MAX_RESULTS);
			//TODO limite to sp
			results = (List<IpToSp>) criteria.list();
			tx.commit();
			return results;
		} catch (Exception e) {
			logger.error("ERROR "+ e.getMessage());
			throw new Exception(e.toString());
		}

	}
	
	@SuppressWarnings("unchecked")
	public List<IpToSp> getIP2SPTrustHW(String serviceId) throws Exception{
		Session session = null;
		List<IpToSp> results = null;
		SessionFactory sf = HibernateUtil.getSessionFactory();
		try {
			session = sf.openSession();
			Transaction tx = session.beginTransaction();
			tx.begin();
			// Search by IP Name
			Criteria criteria = session.createCriteria(IpToSp.class);
			criteria.add(Restrictions.like("serviceId", serviceId));
			//criteria.setMaxResults(MAX_RESULTS); // Removed for Holt-Winters
			//TODO limite to sp
			results = (List<IpToSp>) criteria.list();
			tx.commit();
			return results;
		} catch (Exception e) {
			logger.error("ERROR "+ e.getMessage());
			throw new Exception(e.toString());
		}

	}
	
	@SuppressWarnings("unchecked")
	public List<IpToSp> getIP2SPTrustsByIpId(String ipId) throws Exception{
		Session session = null;
		List<IpToSp> results = null;
		SessionFactory sf = HibernateUtil.getSessionFactory();
		try {
			session = sf.openSession();
			Transaction tx = session.beginTransaction();
			tx.begin();
			// Search by IP Name
			Criteria criteria = session.createCriteria(IpToSp.class);
			criteria.add(Restrictions.like("ipInfo", getIpInfo(ipId)));
			criteria.setMaxResults(MAX_RESULTS);
			results = (List<IpToSp>) criteria.list();
			tx.commit();
			return results;
		} catch (Exception e) {
			logger.error("ERROR "+ e.getMessage());
			throw new Exception(e.toString());
		}

	}
	
	@SuppressWarnings("unchecked")
	public List<IpToSp> getIP2SPTrustsBySpId(String spId) throws Exception{
		Session session = null;
		List<IpToSp> results = null;
		SessionFactory sf = HibernateUtil.getSessionFactory();
		try {
			session = sf.openSession();
			Transaction tx = session.beginTransaction();
			tx.begin();
			// Search by SP Name
			Criteria criteria = session.createCriteria(IpToSp.class);
			criteria.add(Restrictions.like("spInfo", getSpInfo(spId)));
			criteria.setMaxResults(MAX_RESULTS);
			results = (List<IpToSp>) criteria.list();
			tx.commit();
			return results;
		} catch (Exception e) {
			logger.error("ERROR "+ e.getMessage());
			throw new Exception(e.toString());
		}

	}
	
	@SuppressWarnings("unchecked")
	public List<SpInfo> getDistinctSpIDs() throws Exception{
		Session session = null;
		List<SpInfo> results = null;
		SessionFactory sf = HibernateUtil.getSessionFactory();
		try {
			session = sf.openSession();
			Transaction tx = session.beginTransaction();
			tx.begin();
			// Search by IP Name
			Criteria criteria = session.createCriteria(IpToSp.class);
			criteria.setProjection(Projections.distinct(Projections.projectionList().add(Projections.property("spInfo"))));
//			criteria.setMaxResults(MAX_RESULTS);
			results = (List<SpInfo>) criteria.list();
			tx.commit();
			return results;
		} catch (Exception e) {
			logger.error("ERROR "+ e.getMessage());
			throw new Exception(e.toString());
		}
	}
	
	public boolean deleteIP2SPTrust(String serviceId) throws Exception {
		Session session = null;
		SessionFactory sf = HibernateUtil.getSessionFactory();
		int result = 0;
		try {
			session = sf.openSession();
			Transaction tx = session.beginTransaction();
			tx.begin();
			Query query= session.createSQLQuery("DELETE FROM ip_to_sp WHERE service_id =:serviceId");
			query.setParameter("serviceId", serviceId);
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
	
	private ServiceComponent getServiceComponent(String componentId) throws Exception{
		TrecServiceComponentDAO tscDAO = new TrecServiceComponentDAO();
		return tscDAO.getsServiceComponent(componentId);
	}
	
	private IpInfo getIpInfo(String ipId) throws Exception{
		TrecIPinfoDAO tipiDAO = new TrecIPinfoDAO();
		return tipiDAO.getIP(ipId);
	} 
	
	private SpInfo getSpInfo(String spId) throws Exception{
		TrecSPinfoDAO tspiDAO = new TrecSPinfoDAO();
		return tspiDAO.getSP(spId);
	} 
}
