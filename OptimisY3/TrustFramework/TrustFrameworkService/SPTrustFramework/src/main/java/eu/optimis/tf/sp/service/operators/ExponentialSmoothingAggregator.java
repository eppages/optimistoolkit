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
Francisco Javier Nieto. Atos Research and Innovation, Atos SPAIN SA
@email francisco.nieto@atosresearch.eu 

**/

package eu.optimis.tf.sp.service.operators;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.jfree.util.Log;

import eu.optimis.common.trec.db.sp.TrecSP2IPDAO;
import eu.optimis.trec.common.db.sp.model.SpToIp;

public class ExponentialSmoothingAggregator 
{
	public static final int RUNTIME = 0;
	public static final int SLA = 1;
	public static final int IPREACTION = 2;
	
	Logger log = Logger.getLogger(this.getClass());
	
	public ExponentialSmoothingAggregator ()
	{		}
	
	public double calculateAggregation (String idService, double currentValue, int parameter)
	{
		log.debug("Starting exponential smoothing agregation...");
		
		double result = 0.0;
		ArrayList<Double> valuesList = null;
		
		//Retrieve last calculations done for the service
		switch (parameter)
		{
		case RUNTIME:
			valuesList = retrievePreviousRuntime(idService);
			break;
		case SLA:
			valuesList = retrievePreviousSla(idService);
			break;
		case IPREACTION:
			valuesList = retrievePreviousIpReaction(idService);
			break;
		default: return Double.NaN;
		}		
		
		// Add last value obtained from current calculation for completing the list
		valuesList.add(0, new Double (currentValue));
		
		// Transform array and call simple exponential smoothing
		valuesList.trimToSize();
		Double[]orderedList = new Double[valuesList.size()];
		valuesList.toArray(orderedList);
		result = simpleExponentialSmoothing(0.5, orderedList);	
		
		Log.debug("Calculated aggregation: " + result);
		return result;
	}
	
	public double simpleExponentialSmoothing (double alpha, Double[] valuesList)
	{
		double previousPrediction=0.0;
		double localPrediction=0.0;
		
		//Loop for calculating the smoothed series, starting from the last element
		for (int i=valuesList.length-1; i>=0; i--)
		{
			double localValue = valuesList[i].doubleValue();
			previousPrediction = localPrediction;
			localPrediction = alpha * localValue + (1 - alpha) * previousPrediction;
			
			log.debug("---Iteration n. " + i + " --");
			log.debug("Previous Prediction: " + previousPrediction);
			log.debug("Local Value: " + localValue);
			log.debug("Local Prediction: " + localPrediction);
			log.debug("----------------------------");
		}
		
		return localPrediction;
	}	
	
	private ArrayList<Double> retrievePreviousRuntime (String idService)
	{
		ArrayList<Double> valuesList = new ArrayList<Double>();
		try
		{
			TrecSP2IPDAO trecsp2ipdao = new TrecSP2IPDAO();
			List<SpToIp> myList = trecsp2ipdao.getSP2IPTrust(idService);			
			Iterator<SpToIp> myIterator = myList.iterator();			
			while (myIterator.hasNext())
			{
				SpToIp myTrust = myIterator.next();
				valuesList.add(myTrust.getSafetyRunGap());				
			}			
		}
		catch (Exception ex)
		{
			log.error(ex);
			ex.printStackTrace();
		}
		
		return valuesList;
	}
	
	private ArrayList<Double> retrievePreviousSla (String idService)
	{
		ArrayList<Double> valuesList = new ArrayList<Double>();
		try
		{
			TrecSP2IPDAO trecsp2ipdao = new TrecSP2IPDAO();
			List<SpToIp> myList = trecsp2ipdao.getSP2IPTrust(idService);			
			Iterator<SpToIp> myIterator = myList.iterator();			
			while (myIterator.hasNext())
			{
				SpToIp myTrust = myIterator.next();
				valuesList.add(myTrust.getSlaCompliance());				
			}			
		}
		catch (Exception ex)
		{
			log.error(ex);
			ex.printStackTrace();
		}
		
		return valuesList;
	}
	
	private ArrayList<Double> retrievePreviousIpReaction (String idService)
	{
		ArrayList<Double> valuesList = new ArrayList<Double>();
		try
		{
			TrecSP2IPDAO trecsp2ipdao = new TrecSP2IPDAO();
			List<SpToIp> myList = trecsp2ipdao.getSP2IPTrust(idService);			
			Iterator<SpToIp> myIterator = myList.iterator();			
			while (myIterator.hasNext())
			{
				SpToIp myTrust = myIterator.next();
				valuesList.add(myTrust.getIpReactionTime());				
			}			
		}
		catch (Exception ex)
		{
			log.error(ex);
			ex.printStackTrace();
		}
		
		return valuesList;
	}
	
//	public static void main(String[] args) 
//	{
//		ExponentialSmoothingAggregator myCalculator = new ExponentialSmoothingAggregator();		
//		double result = myCalculator.calculateAggregation("http://www.coin-ip.eu/services/ec#ServiceA1", 2.5, ExponentialSmoothingAggregator.RUNTIME);
//		System.out.println ("Received value: " + result);
//	}
}
