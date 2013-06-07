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

package eu.optimis.tf.sp.service.operators;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class Statistics 
{
	
	public static double mean(ArrayList<Double> arr) {
		
		double sum = 0.0;

		for (int i = 0; i < arr.size(); i++) {
			sum += arr.get(i);
		}

		return sum / arr.size();
	}

	public static double variance(ArrayList<Double> arr, double mean) {
		double variance = 0.0;

//		variance = sum((x_i - mean)^2) / (n - 1) 
		for (int i = 0; i < arr.size(); i++) {
			variance += ((Math.pow(arr.get(i) - mean, 2))/(arr.size() - 1 ));
		}

		return variance;
	}
	
	public static double covariance(ArrayList<Double> arrx, ArrayList<Double> arry){
		
		double sum = 0.0;
		double meanx = mean(arrx);
		double meany = mean(arry);
		
		for (int i = 0; i < arrx.size(); i++) {
			sum += arrx.get(i) * arry.get(i) - meanx*meany;
		}
		
		return 1/arrx.size() * sum;
	}
	
	public static double estdesv(double variance){
		return Math.sqrt(variance);
	}
	
	public static double coefficientOfVariance(double estdev, double mean){
		return estdev / mean;
	}
	
	public static double spearman(ArrayList<Double> arrx, ArrayList<Double> arry){
		
		double dividend = 0.0;
		double divisorx = 0.0;
		double divisory = 0.0;
		double meanx = mean(arrx);
		double meany = mean(arry);
		
		for (int i = 0; i < arrx.size(); i++) {
			dividend += (arrx.get(i) - meanx) * (arry.get(i) -meany);
			divisorx = Math.pow((arrx.get(i) - meanx),2);
			divisorx = Math.pow((arry.get(i) - meany),2);
		}
		
		return dividend / Math.sqrt(divisorx * divisory);
		
	}
}
