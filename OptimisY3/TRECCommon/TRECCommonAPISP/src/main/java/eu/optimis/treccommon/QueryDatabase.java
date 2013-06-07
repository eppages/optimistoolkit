/*
 *  Copyright 2013 University of Leeds UK, ATOS SPAIN S.A., City University London, Barcelona Supercomputing Centre and SAP
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

package eu.optimis.treccommon;

import eu.optimis.treccommon.HibernateUtil;

import eu.optimis.trec.common.db.sp.model.ManifestRaw;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.Session;

/**
 * 
 * @author scsmk
 */
public class QueryDatabase {
	static Log logger = LogFactory
			.getLog(eu.optimis.treccommon.QueryDatabase.class);

	@SuppressWarnings("rawtypes")
	public static String getManifest(String aserviceId) throws Exception {
		Session session = null;

		try {
			session = HibernateUtil.getSessionFactory().openSession();
			// System.out.println("4");
		} catch (Exception e) {
			// System.out.println("3");

			logger.error("No database session could be retrieved");
			throw new Exception("No database session could be retrieved");
		}

		int i = 0;

		List SMList = new ArrayList<ManifestRaw>();

		// System.out.println("7");
		try {
			Criteria criteria = session.createCriteria(ManifestRaw.class);
			SMList = criteria.list();
			// System.out.println("sm list " + SMList.size());
		} catch (Exception e) {
			logger.error("No records found");
			throw new Exception("Excpetion thrown");
		}
		// System.out.println("8");
		session.close();
		// System.out.println("9");
		// System.out.println("14 kkk"+ SLAOffers.size());
		Iterator SMIterator = SMList.iterator();
		int k = 0;
		ManifestRaw[] sms = new ManifestRaw[SMList.size()];
		while (SMIterator.hasNext()) {

			ManifestRaw tempSMOffer = (ManifestRaw) SMIterator.next();

			sms[k] = new ManifestRaw();
			sms[k].setId(tempSMOffer.getId());

			sms[k].setServiceId(tempSMOffer.getServiceId());
			// System.out.println("serviceid " + serviceId);
			sms[k].setServiceManifest(tempSMOffer.getServiceManifest());
			sms[k].setIsBroker(tempSMOffer.isIsBroker());
			sms[k].setBrokerHost(tempSMOffer.getBrokerHost());
			sms[k].setBrokerPort(tempSMOffer.getBrokerPort());

			// System.out.println("tempRisk"+tempRisk);
			// sms[k] = new
			// ManifestRawObject(id,serviceId,servicemanifest,isBroker,brokerHost,
			// brokerPort);
			k++;
		}
		String sm_ret = null;
		for (i = 0; i < sms.length; i++) {
			if (sms[i].getServiceId().equalsIgnoreCase(aserviceId)) {
				// System.out.println("found");
				// System.out.println("service id = " + sms[i].getserviceId());
				// System.out.println("service mn = " +
				// sms[i].getserviceManifest());
				System.out.println("found");

				sm_ret = sms[i].getServiceManifest();
			}
		}// for
		return sm_ret;
	}// end of fn

}// class

