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

import eu.optimis.common.trec.db.sp.utils.HibernateUtil;
import eu.optimis.trec.common.db.sp.model.IpInfo;
import eu.optimis.trec.common.db.sp.model.ServiceComponent;
import eu.optimis.trec.common.db.sp.model.SpInfo;
import eu.optimis.trec.common.db.sp.model.SpToIp;

public class TrecSP2IPDAO {

	private final Logger logger = LoggerFactory.getLogger(TrecSP2IPDAO.class
			.getName());
	
	private static final int MAX_RESULTS = 100;

	public boolean addSP2IP(String serviceId, String componentId, String spId,
			String ipId, Date serviceTime, double serviceWellFormed, double safetyRunGap,
			double elasticityClosely, double ipReactionTime, double slaCompliance,
			double ipComplianceWithLegal, double serviceTrust) throws Exception {
		Session session = null;
		SpToIp sp2ip = new SpToIp();
		SessionFactory sf = HibernateUtil.getSessionFactory();
		try {
			session = sf.openSession();
			Transaction tx = session.beginTransaction();
			tx.begin();
			sp2ip.setServiceId(serviceId);
			sp2ip.setServiceComponent(getServiceComponent(componentId));
			sp2ip.setSpInfo(getSpInfo(spId));
			sp2ip.setIpInfo(getIpInfo(ipId));
			sp2ip.setServiceTime(serviceTime);
			sp2ip.setServiceWellFormed(serviceWellFormed);
			sp2ip.setSafetyRunGap(safetyRunGap);
			sp2ip.setElasticityClosely(elasticityClosely);
			sp2ip.setIpReactionTime(ipReactionTime);
			sp2ip.setSlaCompliance(slaCompliance);
			sp2ip.setIpComplianceWithLegal(ipComplianceWithLegal);
			sp2ip.setServiceTrust(serviceTrust);
			session.save(sp2ip);
			tx.commit();
			if (tx.wasCommitted()) {
				logger.info("Transaction commited");
				// System.out.println("Transaction commited");
			}
		} catch (Exception e) {
			logger.info("ERROR " + e.getMessage());
			// System.out.println("ERROR "+ e.getMessage());
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
	public SpToIp getLastSP2IPTrust(String serviceId) throws Exception{
		Session session = null;
		List<SpToIp> results = null;
		SessionFactory sf = HibernateUtil.getSessionFactory();
		try {
			session = sf.openSession();
			Transaction tx = session.beginTransaction();
			tx.begin();
			// Search by IP Name
			Criteria criteria = session.createCriteria(SpToIp.class);
			criteria.add(Restrictions.like("serviceId", serviceId));
			results = (List<SpToIp>) criteria.list();
			tx.commit();
			return results.get(results.size()-1);
		} catch (Exception e) {
			logger.error("ERROR "+ e.getMessage());
			throw new Exception(e.toString());
		}

		
		
	}
	
	@SuppressWarnings("unchecked")
	public List<SpToIp> getSP2IPTrust(String serviceId) throws Exception{
		Session session = null;
		List<SpToIp> results = null;
		SessionFactory sf = HibernateUtil.getSessionFactory();
		try {
			session = sf.openSession();
			Transaction tx = session.beginTransaction();
			tx.begin();
			// Search by IP Name
			Criteria criteria = session.createCriteria(SpToIp.class);
			criteria.add(Restrictions.like("serviceId", serviceId));
			criteria.setMaxResults(MAX_RESULTS);
			results = (List<SpToIp>) criteria.list();
			tx.commit();
			return results;
		} catch (Exception e) {
			logger.error("ERROR "+ e.getMessage());
			throw new Exception(e.toString());
		}

	}
	
	@SuppressWarnings("unchecked")
	public List<SpToIp> getSP2IPTrustsByIpId(String ipId) throws Exception{
		Session session = null;
		List<SpToIp> results = null;
		SessionFactory sf = HibernateUtil.getSessionFactory();
		try {
			session = sf.openSession();
			Transaction tx = session.beginTransaction();
			tx.begin();
			// Search by IP Name
			Criteria criteria = session.createCriteria(SpToIp.class);
			criteria.add(Restrictions.like("ipInfo", getIpInfo(ipId)));
			criteria.setMaxResults(MAX_RESULTS);
			results = (List<SpToIp>) criteria.list();
			tx.commit();
			return results;
		} catch (Exception e) {
			logger.error("ERROR "+ e.getMessage());
			throw new Exception(e.toString());
		}

	}
	
	@SuppressWarnings("unchecked")
	public List<SpToIp> getSP2IPTrustsBySpId(String spId) throws Exception{
		Session session = null;
		List<SpToIp> results = null;
		SessionFactory sf = HibernateUtil.getSessionFactory();
		try {
			session = sf.openSession();
			Transaction tx = session.beginTransaction();
			tx.begin();
			// Search by IP Name
			Criteria criteria = session.createCriteria(SpToIp.class);
			criteria.add(Restrictions.like("spInfo", getSpInfo(spId)));
			criteria.setMaxResults(MAX_RESULTS);
			results = (List<SpToIp>) criteria.list();
			tx.commit();
			return results;
		} catch (Exception e) {
			logger.error("ERROR "+ e.getMessage());
			throw new Exception(e.toString());
		}

	}
	
	@SuppressWarnings("unchecked")
	public List<IpInfo> getDistinctIpIDs() throws Exception{
		Session session = null;
		List<IpInfo> results = null;
		SessionFactory sf = HibernateUtil.getSessionFactory();
		try {
			session = sf.openSession();
			Transaction tx = session.beginTransaction();
			tx.begin();
			// Search by IP Name
			Criteria criteria = session.createCriteria(SpToIp.class);
			criteria.setProjection(Projections.distinct(Projections.projectionList().add(Projections.property("ipInfo"))));
//			criteria.setMaxResults(MAX_RESULTS);
			results = (List<IpInfo>) criteria.list();
			tx.commit();
			return results;
		} catch (Exception e) {
			logger.error("ERROR "+ e.getMessage());
			throw new Exception(e.toString());
		}
	}


	public boolean deleteIPTrust(String serviceId) throws Exception {
		Session session = null;
		SessionFactory sf = HibernateUtil.getSessionFactory();
		int result = 0;
		try {
			session = sf.openSession();
			Transaction tx = session.beginTransaction();
			tx.begin();
			Query query= session.createSQLQuery("DELETE FROM sp_to_ip WHERE service_id =:serviceId");
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
	
	private ServiceComponent getServiceComponent(String componentId)
			throws Exception {
		TrecServiceComponentDAO tscDAO = new TrecServiceComponentDAO();
		return tscDAO.getsServiceComponent(componentId);
	}

	private IpInfo getIpInfo(String ipId) throws Exception {
		TrecIPinfoDAO tipiDAO = new TrecIPinfoDAO();
		return tipiDAO.getIP(ipId);
	}

	private SpInfo getSpInfo(String spId) throws Exception {
		TrecSPinfoDAO tspiDAO = new TrecSPinfoDAO();
		return tspiDAO.getSP(spId);
	}
}
