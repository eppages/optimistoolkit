/**

Copyright 2013 ATOS SPAIN S.A. and City University London

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

package eu.optimis.tf.ip.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

import junit.framework.TestCase;

import org.jfree.util.Log;

import eu.optimis.common.trec.db.ip.TrecIP2SPDAO;
import eu.optimis.common.trec.db.ip.TrecServiceInfoDAO;
import eu.optimis.tf.ip.service.operators.LinearRegression;
import eu.optimis.tf.ip.service.operators.ManifestSimilarity;
import eu.optimis.tf.ip.service.operators.Statistics;
import eu.optimis.tf.ip.service.utils.PropertiesUtils;
import eu.optimis.trec.common.db.ip.model.IpToSp;
import eu.optimis.trec.common.db.ip.model.ServiceInfo;

public class LinearRegressionTest extends TestCase {

	// public void testLR() {
	// double[] x = { 1,1, 2,4, 4 };
	// double[] y = { 0, 1, 2, 3, 4, 5 };
	// LinearRegression lr = new LinearRegression(x, y);
	// System.out.println(lr.getModel());
	// System.out.println("calculate y given an x of 3 " + lr.calculateY(3));
	// System.out.println("calculate x given a y of 4 " + lr.calculateX(4));
	// }

	String manifest1 = "ddbfe9f5-fe93-4366-b3e5-c33f7f11ca96";
	String manifest2 = "859cc28e-9ed3-4f6a-82da-6a53c380213f";
	// String manifest2 = "dfa8d4c2-6c8d-43fe-9b16-3a8ea6ef5073";
	String activeManifest = "3f7d4449-3c3c-42ac-a9b5-4f1017477d27";

	
	
	
	private String getManifestFromDB(String manifestID) {
		TrecServiceInfoDAO tsidao = new TrecServiceInfoDAO();
		try {
			return tsidao.getService(manifestID).getServiceManifest();
		} catch (Exception e) {
			System.err
					.println("Error obtaining the manifest wiht manifest ID = "
							+ manifestID);
			return null;
		}
	}

	private List<HashMap> getActiveManifests() {
		TrecServiceInfoDAO tsidao = new TrecServiceInfoDAO();
		HashMap<String, String> service = new HashMap<String, String>();
		try {
			List<ServiceInfo> silist = tsidao.getActiveServices();
			List<HashMap> manifestList = new ArrayList<HashMap>();
			for (ServiceInfo si : silist) {
				service.put(si.getServiceId(), si.getServiceManifest());
				manifestList.add(service);
			}
			return manifestList;
		} catch (Exception e) {
			System.err.println("There aren't active services");
			return null;
		}
	}

	private List<String> getActiveServiceIDs() {
		TrecServiceInfoDAO tsidao = new TrecServiceInfoDAO();
		try {
			List<ServiceInfo> silist = tsidao.getActiveServices();
			List<String> manifestList = new ArrayList<String>();
			for (ServiceInfo si : silist) {
				manifestList.add(si.getServiceId());
			}
			return manifestList;
		} catch (Exception e) {
			System.err.println("There aren't active services");
			return null;
		}
	}

	private List<Double> getServiceTrust(String serviceId) {
		TrecIP2SPDAO tip2sp = new TrecIP2SPDAO();
		List<Double> serviceTrust = new ArrayList<Double>();
		try {
			List<IpToSp> ip2spList = tip2sp.getIP2SPTrust(serviceId);
			for (IpToSp ip2sp : ip2spList) {
				serviceTrust.add(ip2sp.getServiceTrust());
			}
			return serviceTrust;
		} catch (Exception e) {
			Log.error("error getting trust for service: " + serviceId);
			return serviceTrust;
		}
	}
	
	private double calculateManifestDistance(String manifest1, String manifest2) {
		ManifestSimilarity ms = new ManifestSimilarity();
		try {
			return ms.getSimilarity(manifest1, manifest2);
		} catch (IOException e) {
			System.err.println("error caluclating manifest similarity");
			e.printStackTrace();
			return -1;
		}
	}

	private LinkedHashMap<String,Double> sortList(LinkedHashMap<String,Double> similarList){
		ManifestSimilarity ms = new ManifestSimilarity();
		return ms.sortHashMapByValuesD(similarList);
	}

	private List<Double> normalizeTrustList(ArrayList<Double> lst){
		List<Double> normalizeList = new ArrayList<Double>();
		for (Double d : lst){
			normalizeList.add(d*Double.valueOf(PropertiesUtils.getProperty("TRUST","maxRate")));
		}
		return normalizeList;
	}
	
	private Double getServiceForecast(double[] y, double value){
		//double[] x = { 0, 1, 2, 3, 4, 5 };
		double[] x = new double [y.length];
		for (int i=0; i<y.length; i++)
		{
			x[i] = i;
		}
		LinearRegression lr = new LinearRegression(x,y);
		return lr.calculateY(value);
	}
	
	private double[] toDoubleArray(List<Double> lst){
		double array[] = new double[lst.size()];
		for (int i = 0; i< array.length; i++){
			array[i] = lst.get(i);
		}
		return array;
	}
	
	@SuppressWarnings("rawtypes")
	public void testServiceSimilarity() {
		String keyManifest = getManifestFromDB(manifest1);
		List<String> sidList = getActiveServiceIDs();
		List<HashMap> slist = getActiveManifests();
		System.out.println (slist.size() + " manifests retrieved.");
		LinkedHashMap<String,Double> distanceList = new LinkedHashMap<String,Double>();
		for (HashMap service : slist) {
			for (String sid : sidList) {
				if (service.containsKey(sid)){
					Double distance = calculateManifestDistance(keyManifest,(String) service.get(sid));
					distanceList.put(sid, distance);
				}
				service.remove(sid);
			}			
		}
		
		distanceList = sortList(distanceList);
		System.out.println ("Distances calculated and sorted!");
		
		Set<String> keySet2 = distanceList.keySet();
		Iterator<String> keyit2 = keySet2.iterator();
		String closestService = keyit2.next();
		
		System.out.println ("Closest service is " + closestService);
		
		List<Double> closestListTrust = getServiceTrust(closestService);		
		closestListTrust = normalizeTrustList((ArrayList<Double>)closestListTrust);	
		System.out.println ("Closest service trust is " + closestListTrust.toString());
		Double mean = Statistics.mean((ArrayList<Double>)closestListTrust);		
//		mean = mean * Double.valueOf(PropertiesUtils.getBoundle("maxRate"));
		try
		{
			System.out.println(getServiceForecast(toDoubleArray(closestListTrust), mean));
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
		
	}
}
