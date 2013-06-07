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
import eu.optimis.trec.common.db.ip.model.IpInfo;

public class TrecIPinfoDAO {

	private final Logger logger = LoggerFactory.getLogger(TrecIPinfoDAO.class
			.getName());

	public boolean addIp(String IPName, String ipId, String location)
			throws Exception {
		Session session = null;
		IpInfo ipi = new IpInfo();
		SessionFactory sf = HibernateUtil.getSessionFactory();
		try {
			session = sf.openSession();
			Transaction tx = session.beginTransaction();
			tx.begin();
			ipi.setName(IPName);
			ipi.setIpId(ipId);
			ipi.setIpLocation(location);
			ipi.setIppType("ip");
			session.save(ipi);
			tx.commit();
			if (tx.wasCommitted()) {
				logger.info("Transaction commited");
				// System.out.println("Transaction commited");
			}
			addSNProvider(ipId);
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
	public IpInfo getIP(String key) throws Exception {
		Session session = null;
		List<IpInfo> results = null;
		SessionFactory sf = HibernateUtil.getSessionFactory();
		try {
			session = sf.openSession();
			Transaction tx = session.beginTransaction();
			tx.begin();
			// Search by IP Name
			Criteria criteria = session.createCriteria(IpInfo.class);
			criteria.add(Restrictions.like("ipId", key));
			results = (List<IpInfo>) criteria.list();
			tx.commit();
			return results.get(results.size()-1);
		} catch (Exception e) {
			logger.error("ERROR " + e.getMessage());
			// System.out.println("ERROR "+ e.getMessage());
			throw new Exception(e.toString());
		}

		
	}

	@SuppressWarnings("unchecked")
	public List<IpInfo> getIPs() throws Exception {
		Session session = null;
		List<IpInfo> results = null;
		SessionFactory sf = HibernateUtil.getSessionFactory();
		try {
			session = sf.openSession();
			Transaction tx = session.beginTransaction();
			tx.begin();
			Criteria criteria = session.createCriteria(IpInfo.class);
			results = (List<IpInfo>) criteria.list();
			tx.commit();
			return results;
		} catch (Exception e) {
			logger.error("ERROR " + e.getMessage());
			// System.out.println("ERROR "+ e.getMessage());
			throw new Exception(e.toString());
		}

		
	}

	public boolean deleteIP(String ip) throws Exception {
		Session session = null;
		SessionFactory sf = HibernateUtil.getSessionFactory();
		int result = 0;
		try {
			session = sf.openSession();
			Transaction tx = session.beginTransaction();
			tx.begin();
			Query query = session
					.createSQLQuery("DELETE FROM ip_info WHERE ip_id =:ip OR name =:ip");
			// Query query=
			// session.createSQLQuery("update ip_info set ip_availibility=0 where ip_id=:ip;");
			query.setParameter("ip", ip);
			result = query.executeUpdate();
			tx.commit();
			sf.close();
			deleteSNProvider(ip);
		} catch (Exception e) {
			logger.error("ERROR " + e.getMessage());
			// System.out.println("ERROR "+ e.getMessage());
			throw new Exception(e.toString());
		}

		if (result == 0)
			return false;

		return true;
	}

	private boolean addSNProvider(String provider) {
		String providerType = "ip";
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
	
	private boolean deleteSNProvider(String ip) {
		String providerType = "ip";
		TrecSNProviderDAO tsnpdao = new TrecSNProviderDAO();
		try {
			tsnpdao.deleteIP(ip,providerType);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

}
