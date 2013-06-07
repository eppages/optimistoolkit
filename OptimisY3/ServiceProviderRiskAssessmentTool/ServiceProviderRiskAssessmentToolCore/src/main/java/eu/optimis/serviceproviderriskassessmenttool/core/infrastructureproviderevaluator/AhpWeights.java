/*
 *  Copyright 2013 University of Leeds
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
package eu.optimis.serviceproviderriskassessmenttool.core.infrastructureproviderevaluator;

import org.apache.log4j.Logger;

/**
 * This class is responsible for computing AHP weights.
 * @author Iain Gourlay
 */

public class AhpWeights {
	

         protected static Logger logger = Logger.getLogger(AhpWeights.class);
	
/**
 * 
 * @param pairwiseMatrix A pairwise comparison matrix.
 * @return an array of weights computed using AHP. 
 */
	public double[] createWeights(double[][] pairwiseMatrix)
	{
		int numWeights = pairwiseMatrix.length;
		double[] weights = new double[numWeights];
		double temp = 0;
		for (int i = 0; i< numWeights; i++)
		{
			for (int k = 0; k<numWeights; k++)
			{
				temp = 0;
				for(int l = 0; l<numWeights; l++) temp += pairwiseMatrix[l][k];
				weights[i]+= pairwiseMatrix [i][k]/temp;
			}
			weights[i] = weights [i]/numWeights;
			logger.debug(weights[i]);
			
		}
		
		
		
		return weights;
	}
}
