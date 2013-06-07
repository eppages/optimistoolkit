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

package eu.optimis.tf.sp.service.trust;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.log4j.Logger;

import org.ogf.graap.wsag4j.types.engine.SLOEvaluationResultType;

import eu.optimis.common.trec.db.sp.TrecSLADAO;

import eu.optimis.sla.notification.NotificationEndpointFactoryService;
import eu.optimis.sla.notification.Subscription;
import eu.optimis.sla.notification.SubscriptionService;
import eu.optimis.sla.notification.exceptions.ResourceNotFoundException;
import eu.optimis.sla.notification.impl.ClientFactory;
import eu.optimis.sla.types.subscription.NotificationEventType;
import eu.optimis.tf.sp.service.operators.Opinion;
import eu.optimis.tf.sp.service.operators.OpinionModel;
import eu.optimis.tf.sp.service.utils.PropertiesUtils;
import eu.optimis.trec.common.db.sp.model.ServiceSla;

public class SLAAgreement {

	Logger log = Logger.getLogger(this.getClass().getName());

	String slaUrl = "";
	int slaPort = 0;
	String slaNotificationUri = "";
	String slaSubscriptionUri = "";

	String NOTIFICATION_SERVICEURL = "";
	String SUBSCRIPTION_SERVICEURL = "";

	public SLAAgreement() {

		slaUrl = PropertiesUtils.getProperty("TRUST","sla.url");
		slaPort = Integer.valueOf(PropertiesUtils.getProperty("TRUST","sla.port"));
		slaNotificationUri = PropertiesUtils.getProperty("TRUST","sla.notification.uri");
		slaSubscriptionUri = PropertiesUtils.getProperty("TRUST","sla.subscription.uri");

		NOTIFICATION_SERVICEURL = "http://" + slaUrl + ":" + slaPort + "/"
				+ slaNotificationUri;

		SUBSCRIPTION_SERVICEURL = "http://" + slaUrl + ":" + slaPort + "/"
				+ slaSubscriptionUri;

	}

	public double getSLAAssessment(String serviceId){
		return getSLAAssessmentOpinion(serviceId).getExpectation();
	}
	
	public Opinion getSLAAssessmentOpinion(String serviceId){
//		List<String> slaIdsList = getSLAagreementID(serviceId);
//		OpinionModel opmodel = new OpinionModel();
//		Opinion opinion = new Opinion();
//		for (String slaId : slaIdsList){
//			Opinion opsla = getSLAsubscription(slaId);
//			opinion = opmodel.concensus(opinion, opsla);
//		}
//		log.info("sla opinion: "+opinion.getExpectation());
//		return opinion;
		return calculateErrOP();
	}
	
	private List<String> getSLAagreementID(String serviceId) {
		TrecSLADAO tsladao = new TrecSLADAO();
		List<String> slaIdList = new ArrayList<String>();
		try {
			List<ServiceSla> sslaList = tsladao.getSLAbyServiceId(serviceId);
			log.info("sla list length: "+sslaList.size());
			for (ServiceSla ssla : sslaList){
				slaIdList.add(ssla.getServiceId());
			}
			return slaIdList;
		} catch (Exception e) {
			return slaIdList;
		}
	}

	private Opinion getSLAsubscription(String agreementId) {

		int r = 0;
		int s = 0;

		NotificationEndpointFactoryService notificationService = ClientFactory
				.create(NOTIFICATION_SERVICEURL,
						NotificationEndpointFactoryService.class);

		URI notificationUri = notificationService.createNotificationEndpoint();

		SubscriptionService subscriptionService = ClientFactory.create(
				SUBSCRIPTION_SERVICEURL, SubscriptionService.class);

		URI subscriptionUri = subscriptionService.subscribe(agreementId,
				notificationUri);

		Subscription subscription = ClientFactory.create(subscriptionUri,
				Subscription.class);

		try {

			NotificationEventType[] notiEventArray = notificationService
					.getNotification(agreementId).getNotificationEventHistory()
					.getNotificationEventCollection()
					.getNotificationEventArray();

			int nhLength = notiEventArray.length;

			if (nhLength > 0) {
				for (int i = 0; i < nhLength; i++)
					if (notiEventArray[i].getGuaranteeEvaluationResultArray(0)
							.getType() != SLOEvaluationResultType.SLO_VIOLATED)
						r += 1;
					else
						s += 1;
				return calculateOpinion(r, s);
			} else {
				return  calculateErrOP();
			}
			
		} catch (ResourceNotFoundException e) {
			return calculateErrOP();
		}

	}

	private Opinion calculateOpinion(double r, double s) {
		Opinion op = new Opinion(r, s);
		op.setExpectation();
		return op;
	}
	
	private Opinion calculateErrOP(){
		Random generator = new Random();
		int r = generator.nextInt(10);
		int s = 10 - r;
		return calculateOpinion(r, s);
	}
}
