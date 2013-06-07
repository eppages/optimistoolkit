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

import java.util.ArrayList;
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
import eu.optimis.trec.common.db.ip.model.ServiceInfo;

public class TrecServiceInfoDAO {

	final static Logger logger = LoggerFactory
			.getLogger(TrecServiceInfoDAO.class.getName());

	public boolean addService(String serviceId, String serviceProviderId,
			String serviceManifest,  boolean deployed)
			throws Exception {
		Session session = null;
		ServiceInfo si = new ServiceInfo();
		SessionFactory sf = eu.optimis.common.trec.db.ip.utils.HibernateUtil
				.getSessionFactory();
		try {
			session = sf.openSession();
			Transaction tx = session.beginTransaction();
			tx.begin();
			si.setServiceId(serviceId);
			si.setSpId(serviceProviderId);
			si.setServiceManifest(serviceManifest);
			si.setDeployed(deployed);
			session.save(si);
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
	public ServiceInfo getService(String key) throws Exception {
		Session session = null;
		List<ServiceInfo> results = null;
		SessionFactory sf = HibernateUtil.getSessionFactory();
		try {
			session = sf.openSession();
			Transaction tx = session.beginTransaction();
			tx.begin();
			// Search by Service ID
			Criteria criteria2 = session.createCriteria(ServiceInfo.class);
			criteria2.add(Restrictions.like("serviceId", key));
			results = (List<ServiceInfo>) criteria2.list();
			tx.commit();
			//return results.get(results.size()-1);
			return results.get(0); //Return the first element
		} catch (Exception e) {
			logger.error("ERROR " + e.getMessage());			
			throw e;
		}

	}

	@SuppressWarnings("unchecked")
	public List<ServiceInfo> getServices() throws Exception {
		Session session = null;
		List<ServiceInfo> results = new ArrayList<ServiceInfo>();
		SessionFactory sf = HibernateUtil.getSessionFactory();
		try {
			session = sf.openSession();
			Transaction tx = session.beginTransaction();
			tx.begin();
			Criteria criteria = session.createCriteria(ServiceInfo.class);
			results = (List<ServiceInfo>) criteria.list();
			tx.commit();
			return results;
		} catch (Exception e) {
			logger.error("ERROR " + e.getMessage());
			throw new Exception(e.toString());
		}
	}

	@SuppressWarnings("unchecked")
	public List<ServiceInfo> getActiveServices() throws Exception {
		Session session = null;
		List<ServiceInfo> results = null;
		SessionFactory sf = HibernateUtil.getSessionFactory();
		try {
			session = sf.openSession();
			Transaction tx = session.beginTransaction();
			tx.begin();
			Criteria criteria = session.createCriteria(ServiceInfo.class);
			criteria.add(Restrictions.like("deployed", true));
			results = (List<ServiceInfo>) criteria.list();
			results = (List<ServiceInfo>) criteria.list();
			tx.commit();
			return results;
		} catch (Exception e) {
			logger.error("ERROR " + e.getMessage());
			throw new Exception(e.toString());
		}
	}
	
	public boolean deleteService(String service) throws Exception {
		Session session = null;
		SessionFactory sf = HibernateUtil.getSessionFactory();
		int result = 0;
		try {
			session = sf.openSession();
			Transaction tx = session.beginTransaction();
			tx.begin();
			Query query = session
					.createSQLQuery("DELETE FROM service_info WHERE service_id =:service OR service_name =:service");
			query.setParameter("service", service);
			result = query.executeUpdate();
			tx.commit();
			sf.close();
		} catch (Exception e) {
			logger.error("ERROR " + e.getMessage());
			throw new Exception(e.toString());
		}

		if (result == 0)
			return false;

		return true;
	}

	public boolean updateDeployed(String service, boolean deployed) throws Exception {
		Session session = null;
		SessionFactory sf = HibernateUtil.getSessionFactory();
		int result = 0;
		try {
			session = sf.openSession();
			Transaction tx = session.beginTransaction();
			tx.begin();
			 Query query=
			 session.createSQLQuery("update service_info set deployed=:deploy where service_id=:service or service_name=:service");
			query.setParameter("service", service);
			query.setParameter("deploy", deployed);
			result = query.executeUpdate();
			tx.commit();
			sf.close();
		} catch (Exception e) {
			logger.error("ERROR " + e.getMessage());
			throw new Exception(e.toString());
		}

		if (result == 0)
			return false;

		return true;
	}
	
	public boolean updateSLAid(String service, String slaId) throws Exception {
		Session session = null;
		SessionFactory sf = HibernateUtil.getSessionFactory();
		int result = 0;
		try {
			session = sf.openSession();
			Transaction tx = session.beginTransaction();
			tx.begin();
			 Query query=
			 session.createSQLQuery("update service_info set sla_id=:sla where service_id=:service or service_name=:service");
			query.setParameter("service", service);
			query.setParameter("sla", slaId);
			result = query.executeUpdate();
			tx.commit();
			sf.close();
		} catch (Exception e) {
			logger.error("ERROR " + e.getMessage());
			throw new Exception(e.toString());
		}

		if (result == 0)
			return false;

		return true;
	}

	

}
