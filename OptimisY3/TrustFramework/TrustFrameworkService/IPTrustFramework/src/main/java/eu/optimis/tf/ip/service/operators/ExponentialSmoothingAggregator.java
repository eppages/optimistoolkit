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

package eu.optimis.tf.ip.service.operators;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

import eu.optimis.common.trec.db.ip.TrecIP2SPDAO;
import eu.optimis.common.trec.db.ip.TrecIPTrustDAO;
import eu.optimis.tf.ip.service.utils.PropertiesUtils;
import eu.optimis.trec.common.db.ip.model.IpToSp;
import eu.optimis.trec.common.db.ip.model.IpTrust;

public class ExponentialSmoothingAggregator 
{
	public static final int PERFORMANCE = 0;
	public static final int ELASTICITY = 1;
	public static final int RELIABILITY = 2;
	public static final int CPU = 3;
	public static final int MEMORY = 4;
	public static final int STORAGE = 5;
	public static final int SERVTRUST = 6;
	public static final int IPTRUST = 7;
	Logger log = Logger.getLogger(this.getClass().getName());
		
	public ExponentialSmoothingAggregator ()
	{		}
	
	public double calculateAggregation (String idService, double currentValue, int parameter)
	{
		log.info ("Starting exponential smoothing agregation...");
		
		double result = 0.0;
		ArrayList<Double> valuesList = null;
		
		//Retrieve last calculations done for the service
		switch (parameter)
		{
		case PERFORMANCE:
			valuesList = retrievePreviousPerformance(idService);
			break;
		case ELASTICITY:
			valuesList = retrievePreviousElasticity(idService);
			break;
		case RELIABILITY:
			valuesList = retrievePreviousReliability(idService);
			break;
		case SERVTRUST:
			valuesList = retrievePreviousServiceTrust(idService);			
			break;
		case IPTRUST:
			valuesList = retrievePreviousIPTrust(idService);	
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
		
		log.info ("Calculated aggregation: " + result);
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
			
			/*log.debug ("---Iteration n. " + i + " --");
			log.debug ("Previous Prediction: " + previousPrediction);
			log.debug ("Local Value: " + localValue);
			log.debug ("Local Prediction: " + localPrediction);
			log.debug ("----------------------------");*/
		}
		
		return localPrediction;
	}	
	
	private ArrayList<Double> retrievePreviousElasticity (String idService)
	{
		ArrayList<Double> valuesList = new ArrayList<Double>();
		try
		{
			TrecIP2SPDAO trecsp2ipdao = new TrecIP2SPDAO();
			List<IpToSp> myList = trecsp2ipdao.getIP2SPTrust(idService);			
			Iterator<IpToSp> myIterator = myList.iterator();			
			while (myIterator.hasNext())
			{
				IpToSp myTrust = myIterator.next();
				valuesList.add(myTrust.getServiceReliability());//Wrong, since it'd be elasticity		
			}			
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
		
		return valuesList;
	}
	
	private ArrayList<Double> retrievePreviousPerformance (String idService)
	{
		ArrayList<Double> valuesList = new ArrayList<Double>();
		try
		{
			TrecIP2SPDAO trecsp2ipdao = new TrecIP2SPDAO();
			List<IpToSp> myList = trecsp2ipdao.getIP2SPTrust(idService);			
			Iterator<IpToSp> myIterator = myList.iterator();			
			while (myIterator.hasNext())
			{
				IpToSp myTrust = myIterator.next();
				valuesList.add(myTrust.getPerformance());				
			}			
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
		
		return valuesList;
	}
	
	private ArrayList<Double> retrievePreviousReliability (String idService)
	{
		ArrayList<Double> valuesList = new ArrayList<Double>();
		try
		{
			TrecIP2SPDAO trecsp2ipdao = new TrecIP2SPDAO();
			List<IpToSp> myList = trecsp2ipdao.getIP2SPTrust(idService);			
			Iterator<IpToSp> myIterator = myList.iterator();			
			while (myIterator.hasNext())
			{
				IpToSp myTrust = myIterator.next();
				valuesList.add(myTrust.getServiceReliability());				
			}			
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
		
		return valuesList;
	}
	
	private ArrayList<Double> retrievePreviousServiceTrust (String idService)
	{
		ArrayList<Double> valuesList = new ArrayList<Double>();
		try
		{
			TrecIP2SPDAO trecsp2ipdao = new TrecIP2SPDAO();
			List<IpToSp> myList = trecsp2ipdao.getIP2SPTrustHW(idService);			
			Iterator<IpToSp> myIterator = myList.iterator();			
			while (myIterator.hasNext())
			{
				IpToSp myTrust = myIterator.next();
				valuesList.add(myTrust.getServiceTrust());				
			}			
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
		
		return valuesList;
	}
	
	private ArrayList<Double> retrievePreviousIPTrust (String idProvider)
	{
		ArrayList<Double> valuesList = new ArrayList<Double>();
		try
		{
			TrecIPTrustDAO trecipdao = new TrecIPTrustDAO();
			List<IpTrust> myList = trecipdao.getIPTrustsHW(idProvider);			
			Iterator<IpTrust> myIterator = myList.iterator();			
			while (myIterator.hasNext())
			{
				IpTrust myTrust = myIterator.next();
				valuesList.add(myTrust.getIpTrust());				
			}			
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
		
		return valuesList;
	}
	
	public double calculateTripleAggregation (String idService, double currentValue, int parameter, int m)
	{
		log.info ("Starting triple exponential smoothing agregation...");
		
		// In principle, 1 season = 1 day (minimum seasonality in the data)
		// For the Monitoring, 720 measures per day (1 season = 720 values)
		int period = 720; 
		String interval = PropertiesUtils.getProperty("TRUST","interval");
		
		double result = 0.0;		
		ArrayList<Double> valuesList = null;
		
		//Retrieve last calculations done for the service
		switch (parameter)
		{
		case PERFORMANCE:
			valuesList = retrievePreviousPerformance(idService);
			break;
		case ELASTICITY:
			valuesList = retrievePreviousElasticity(idService);
			break;
		case RELIABILITY:
			valuesList = retrievePreviousReliability(idService);
			break;
		case CPU:
			valuesList = retrievePreviousReliability(idService);
			break;
		case MEMORY:
			valuesList = retrievePreviousReliability(idService);
			break;
		case STORAGE:
			valuesList = retrievePreviousReliability(idService);
			break;
		case SERVTRUST:
			valuesList = retrievePreviousServiceTrust(idService);			
			log.debug ("The current interval is: " + interval);
			period = (60/(Integer.parseInt(interval)/60))*24;
			break;
		case IPTRUST:
			valuesList = retrievePreviousIPTrust(idService);			
			log.debug ("The current interval is: " + interval);
			period = (60/(Integer.parseInt(interval)/60))*24;
			break;
		default: return Double.NaN;
		}		
		
		// Add last value obtained from current calculation for completing the list
		if (!Double.isNaN(currentValue))
		{
			valuesList.add(0, new Double (currentValue));
		}		
		
		// Transform array and call triple exponential smoothing
		valuesList.trimToSize();
		Double[]orderedList = new Double[valuesList.size()];
		valuesList.toArray(orderedList);
		
		// Use period and check the calculation can be done
		if (orderedList.length/period<2)
		{
			// We require minimum 2 seasons. If not available, it's better to return SES
			log.debug("Not enough data for 2 seasons (" + orderedList.length +  ") --> Calculatig Simple Exponential Smoothing");
			return simpleExponentialSmoothing(0.5, orderedList);
		}
		else if (orderedList.length/period>14)
		{
			// We take as period the complete week --> 1 Season = 1 week
			period = period * 7;
		}
		
		result = tripleExponentialSmoothing(0.4863, 0.0001, 0.0011, period, orderedList, m);	
		
		log.info ("Calculated aggregation: " + result);
		return result;
	}
	
	public double tripleExponentialSmoothing (double alpha, double beta, double gamma, int period, Double[] valuesList, int m)
	{
		log.info("Starting Holt-Winters forecast calculation with values...");
		log.info("Alpha: " + alpha + "; Beta: " + beta + "; Gamma: " + gamma + "; Period: " + period + "; M: " + m);
		
		// Calculate initial values for the Holt-Winters
		int seasons = valuesList.length / period;
		double S0 = valuesList[0].doubleValue();
		double b0 = calculateInitialTrend(valuesList, period);
		double[] initSeasonalIndices = calculateInitSeasonalIndices(valuesList, period, seasons);

		log.debug("Total values: " + valuesList.length + ", Seasons: " + seasons + ", Period: " +period);
        log.debug("Initial level value S0: " + S0);
        log.debug("Initial trend value b0: " + b0);
        log.debug("Seasonal Indices: " + initSeasonalIndices.toString());
		
		double forecast = calculateHoltWinters(valuesList, S0, b0, alpha, beta, gamma, initSeasonalIndices, period, m);

		return forecast;		
	}
	
	private double calculateInitialTrend(Double[] values, int period) 
	{
		
        double trend = 0;
        for (int i = 0; i < period; i++) 
        {
            trend = trend + (values[period + i] - values[i]);
        }

        return trend / (period * period);
    }
	
	private double[] calculateInitSeasonalIndices(Double[] values, int period, int seasons) 
	{ 
        // Step 1: Compute the averages for each of the seasons
        double[] seasonalAvg = new double[seasons];
        for (int i = 0; i < seasons; i++) 
        {
        	int actSeason = i * period;
            for (int j = 0; j < period; j++) 
            {
                seasonalAvg[i] = seasonalAvg[i] + values[actSeason + j];
            }
            seasonalAvg[i] = seasonalAvg[i] / period;
        }

        // Step 2: Divide the observations by the appropriate seasonal mean 
        double[] avgObservations = new double[values.length];
        for (int i = 0; i < seasons; i++) 
        {
        	int actSeason = i * period;
            for (int j = 0; j < period; j++) 
            {
                avgObservations[actSeason + j] = values[actSeason + j] / seasonalAvg[i];
            }
        }

        // Step 3: Now the seasonal indices are formed by computing the average of each row 
        // (i.e. Avgs of same parts of the seasons: periods #1 of each season).
        double[] seasonalIndices = new double[period];
        for (int i = 0; i < period; i++) 
        {
            for (int j = 0; j < seasons; j++) 
            {
                seasonalIndices[i] = seasonalIndices[i] + avgObservations[(j * period) + i];
            }
            seasonalIndices[i] = seasonalIndices[i] / seasons;
        }

        return seasonalIndices;
    }
	
	// Multiplicative Holt-Winters (Triple Exponential Smoothing)
	private double calculateHoltWinters(Double[] y, double a0, double b0,
            double alpha, double beta, double gamma,
            double[] initialSeasonalIndices, int period, int m) 
	{

        double[] St = new double[y.length];
        double[] Bt = new double[y.length];
        double[] It = new double[y.length];
        double[] Ft = new double[y.length + m];

        // Initialize base values
        //St[1] = a0;
        //Bt[1] = b0;
        St[0] = a0;
        Bt[0] = b0;

        for (int i = 0; i < period; i++) {
            It[i] = initialSeasonalIndices[i];
        }

        // Start calculations
        for (int i = 1; i < y.length; i++) 
        {
        	log.debug ("---Iteration n. " + i + " --");
            // Calculate overall smoothing
            if ((i - period) >= 0) 
            {
                St[i] = alpha * y[i] / It[i - period] + (1.0 - alpha) * (St[i - 1] + Bt[i - 1]);
            } 
            else 
            {
                St[i] = alpha * y[i] + (1.0 - alpha) * (St[i - 1] + Bt[i - 1]);
            }
            log.debug ("Overall Smoothing: " + St[i]);
            
            // Calculate trend smoothing
            Bt[i] = gamma * (St[i] - St[i - 1]) + (1 - gamma) * Bt[i - 1];
            log.debug ("Trend Smoothing: " + It[i]);
            
            // Calculate seasonal smoothing
            if ((i - period) >= 0) 
            {
                It[i] = beta * y[i] / St[i] + (1.0 - beta) * It[i - period];
            }
            log.debug ("Seasonal Smoothing: " + It[i]);
            
            // Calculate forecast
            if (((i + m) >= period)) 
            {
                Ft[i + m] = (St[i] + (m * Bt[i])) * It[i - period + m];
            }     
            log.debug ("Prediction done: " + Ft[i + m]);
            log.debug ("----------------------------");
        }

        // Return only the next value
        return Ft[Ft.length-1];
    }
	
	private double calculateMSE()
	{
		return 0.0;
	}
	
	public static void main(String[] args) 
	{
		ExponentialSmoothingAggregator myCalculator = new ExponentialSmoothingAggregator();	
		//double result = myCalculator.calculateAggregation("http://www.coin-ip.eu/services/ec#ServiceA1", 2.5, ExponentialSmoothingAggregator.PERFORMANCE);
		Double[] valuesList = new Double[] {1.2, 1.3, 2.5, 1.5, 1.1, 1.2, 2.4, 1.4, 0.9, 1.0, 2.3, 1.3};
		double result = myCalculator.tripleExponentialSmoothing(0.5, 0.1, 0.3, 4, valuesList, 1);
		System.out.println ("Received value: " + result);
		//double res2 = myCalculator.calculateTripleAggregation("a4169454-a7bc-441c-b1b2-378ede095180", 0.276043186320266, ExponentialSmoothingAggregator.SERVTRUST, 1);
		double res2 = myCalculator.calculateTripleAggregation("atos", 0.276043186320266, ExponentialSmoothingAggregator.IPTRUST, 1);
		System.out.println ("Received value: " + res2);
	}
}
