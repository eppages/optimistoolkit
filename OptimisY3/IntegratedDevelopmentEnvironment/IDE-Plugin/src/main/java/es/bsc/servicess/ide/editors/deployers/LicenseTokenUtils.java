/*
 *  Copyright 2011-2013 Barcelona Supercomputing Center (www.bsc.es)
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
package es.bsc.servicess.ide.editors.deployers;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.xmlbeans.GDuration;
import org.eclipse.jdt.core.IJavaProject;
import org.joda.time.DateTime;
import es.bsc.servicess.ide.ConstraintsUtils;
import es.bsc.servicess.ide.ProjectMetadata;
import es.bsc.servicess.ide.editors.CommonFormPage;
import es.bsc.servicess.ide.editors.ImplementationFormPage;
import es.bsc.servicess.ide.model.ServiceElement;
import eu.elasticlm.api.utils.TokenFactory;
import eu.elasticlm.schemas.x2009.x05.license.token.FeatureType;
import eu.elasticlm.schemas.x2009.x05.license.token.LicenseTokenDocument;
import eu.elasticlm.schemas.x2009.x05.license.token.LicenseTokenType;
import eu.elasticlm.schemas.x2009.x05.lsdl.ApplicationType;
import eu.elasticlm.schemas.x2009.x05.lsdl.ChargeType;
import eu.elasticlm.schemas.x2009.x05.lsdl.ConsumableFeatureType;
import eu.elasticlm.schemas.x2009.x05.lsdl.CurrencyType;
import eu.elasticlm.schemas.x2009.x05.lsdl.FeaturesType;
import eu.elasticlm.schemas.x2009.x05.lsdl.LicenseDescriptionDocument;
import eu.elasticlm.schemas.x2009.x05.lsdl.LicenseDescriptionType;
import eu.elasticlm.schemas.x2009.x05.lsdl.ReservationTimeType;


public class LicenseTokenUtils {
	
	private static final String VERSION = "1.0";
	private static final String APPNAME_FEATURE_ID = "app-name";
	private static final String THREADS_FEATURE_ID = "cfthreads";
	private static final String THREADS_FEATURE = "THREADS";

	public static List<String> getLicensesFromProject(IJavaProject project, ProjectMetadata prMetadata) throws Exception{
		ArrayList<String> licenses = new ArrayList<String>();
		String[] classes = prMetadata.getAllOrchestrationClasses();
		for (String cl:classes){
			List<ServiceElement> els = ImplementationFormPage.getCoreElements(cl, project, prMetadata);
			for (ServiceElement el:els){
				for (String lic:getLicensesFromConstraints(el.getConstraints()))
					licenses.add(lic);
			}
			els = ImplementationFormPage.getOrchestrationElements(cl, project, prMetadata);
						for (ServiceElement el:els){
							for (String lic:getLicensesFromConstraints(el.getConstraints()))
								licenses.add(lic);
						}
		}
		
		return licenses;
	}
	
	public static String[] getLicensesFromConstraints(Map<String, String> map){
		String licenses = map.get(ConstraintsUtils.LICENSE.getName());
		if (licenses != null && licenses.length()>0){
			return licenses.split(",");
		}else{
			return new String[0];
		}
	}
	
	public static Map<String, String> getLicenseTokenFromManifest(byte[][] tokens, List<String> currentLicenses) throws Exception{
		Map<String, String> licenseTokens = new HashMap<String, String>();
		Map<String, String> manifestTokens = new HashMap<String, String>();
		for (byte[] tok:tokens){
			manifestTokens.put(getName(new String(tok)), new String(tok));
		}
		for (String license:currentLicenses){
			if (manifestTokens.containsKey(license)){
				licenseTokens.put(license,manifestTokens.get(license));
			}else{
				licenseTokens.put(license,"");
			}
		}
		return licenseTokens;
	}
	
	public static List<String> getElementWithLicense (String licenseName, IJavaProject project, ProjectMetadata prMetadata){
		ArrayList<String> elements = new ArrayList<String>();
		HashMap<String, ServiceElement> map = CommonFormPage.getElements(
				prMetadata.getAllOrchestrationClasses(),ProjectMetadata.BOTH_TYPE,
				project, prMetadata);
		if (map!= null&& map.size()>0){
			for (ServiceElement el:map.values()){
				String[] licenses = getLicensesFromConstraints(el.getConstraints());
				for (String lic:licenses){
					if (lic.equals(licenseName)){
						elements.add(el.getLabel());
						break;
					}
				}
			}
		}
		return elements;
	}	
	
		
	public static List<String> getPackagesWithLicense(String licenseName, IJavaProject project, ProjectMetadata prMetadata){
		ArrayList<String> packages = new ArrayList<String>();
		List<String> licensed_els = getElementWithLicense (licenseName, project, prMetadata);
		for (String licensed_el:licensed_els){
			//Front End
			String[] oePacks = prMetadata.getPackagesWithCores();
			if (oePacks != null && oePacks.length > 0) {
				for (String p : oePacks) {
					String[] orch_els = prMetadata.getElementsInPackage(p);
					for (String orch_el:orch_els){
						if (orch_el.equals(licensed_el)){
							packages.add(project.getProject().getName());
							break;
						}
					}
					
				}
			}else{
				String[] orch_els = ImplementationFormPage.getOrchestrationElementsLabels(prMetadata.getAllOrchestrationClasses(), project, prMetadata);
				for (String orch_el:orch_els){
					if (orch_el.equals(licensed_el)){
						packages.add(project.getProject().getName());
						break;
					}
				}
			}	
			//Other Packages
			String[] cePacks = prMetadata.getPackagesWithCores();
			if (cePacks != null && cePacks.length > 0) {
				for (String p : cePacks) {
					String[] core_els = prMetadata.getElementsInPackage(p);
					for (String core_el:core_els){
						if (core_el.equals(licensed_el)){
							packages.add(project.getProject().getName());
							break;
						}
					}	
				}
			}
		}
		return packages;
	}
	
	public static int getRequiredConcurrentExecutionsInLicense(String licenseName, IJavaProject project, ProjectMetadata prMetadata ){
		int numThreads = 0;
		List<String> elements = getElementWithLicense(licenseName,project,prMetadata);
		for (String el:elements){
			System.out.println("Element: "+ el + " max_elasticity" + prMetadata.getMaxElasticity(el) );
			numThreads=numThreads+prMetadata.getMaxElasticity(el);
			System.out.println("Accumulated threads: "+numThreads); 
		}
		return numThreads;
	}

	public static String generateToken(String licenseName, String lsERP, String cliProp, int numThreads) throws Exception {
		LicenseTokenDocument licenseTokenDoc = null;
		LicenseDescriptionDocument lsdlDoc = LicenseDescriptionDocument.Factory.newInstance();
		LicenseDescriptionType lsdl = lsdlDoc.addNewLicenseDescription();
		lsdl.setIsUsedOffline(false);
		ApplicationType appType = lsdl.addNewApplication();
		appType.setApplicationId(licenseName);
		appType.setName(licenseName);
		appType.setVersion(VERSION);
		FeaturesType features = appType.addNewFeatures();
		ConsumableFeatureType basicFeature = features.addNewBasicFeature();
		basicFeature.setFeatureId(APPNAME_FEATURE_ID);
		basicFeature.setName(licenseName);
		basicFeature.setVersion(VERSION);
		basicFeature.setValue(1);
		ConsumableFeatureType feature = features.addNewConsumableFeature();
		feature.setFeatureId(THREADS_FEATURE_ID);
		feature.setName(THREADS_FEATURE);
		feature.setVersion(VERSION);
		feature.setValue(numThreads);
		ReservationTimeType res = lsdl.addNewReservation();
		res.setDuration(new GDuration("PT2H"));
		DateTime dfEST = new DateTime(System.currentTimeMillis());
		//DateTime dfEST = new DateTime("2012-10-01T15:00:00+02:00");
		DateTime dfDeadline = new DateTime("2015-12-31T00:00:00");
		res.setEarliestStartTime(dfEST.toGregorianCalendar());
		res.setDeadline(dfDeadline.toGregorianCalendar());
		res.setReservationStartTime(dfEST.toGregorianCalendar());
		Calendar resEndTime = dfEST.toGregorianCalendar();
		resEndTime.add(Calendar.DAY_OF_YEAR, 30);
		res.setReservationEndTime(resEndTime);
		ChargeType charge = lsdl.addNewCharge();
		charge.setCurrency(CurrencyType.EUR);
		charge.setStringValue("0");
		lsdl.setAccountingGroup("/ElasticLM/Users");
		
		licenseTokenDoc = TokenFactory.createToken(lsdl, lsERP, cliProp);
		return licenseTokenDoc.toString();
     
	}
	
	public static String getName(String strToken) throws Exception{
		LicenseTokenDocument doc = LicenseTokenDocument.Factory.parse(strToken);
		LicenseTokenType token = doc.getLicenseToken();
		if 	(token!=null){
			for (FeatureType ft:token.getFeatures().getFeatureArray()){
				if (ft.getFeatureId().equals(APPNAME_FEATURE_ID)){
					return ft.getName();
				}
			}
			throw new Exception("Application name feature not found");
		}else
			throw new Exception("Unable to parse license token");
	}
	
	public static void main(String[] args) {
        try {
        	
        	System.out.println(generateToken("Genewise", "http://optimis-lms.ds.cs.umu.se:8080/elasticlm-license-service", "/home/jorgee/Projects/Optimis/LicenseTokens/client.properties", 2 ));
            
        } catch (Exception e) {
            e.printStackTrace();
        }
	}
	
	

}
