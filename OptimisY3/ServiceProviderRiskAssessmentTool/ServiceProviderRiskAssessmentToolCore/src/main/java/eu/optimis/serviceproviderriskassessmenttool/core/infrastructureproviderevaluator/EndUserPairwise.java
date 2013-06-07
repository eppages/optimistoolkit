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

/**
 * Provides a method for taking an end-user's preferences 
 *(which criteria are most important to them),
 *specified in terms of pairwise comparisons and creating a pairwise 
 *comparison matrix for use in AHP.
 * @author Iain Gourlay
 *
 */
public class EndUserPairwise {

/**
 * 
 * @param user an object specifying the user's criteria preferences
 * @return cM a pairwise comparison matrix summarising the user's preferences.
 */
	public double [][] createMatrix(UserPreferenceObject user)
	{
		double [][] cM = new double [7][7];
		for (int i=0;i<7;i++) cM[i][i]=1;
		cM[0][1]=user.getGeoVpast();
		cM[0][2]=user.getGeoVcertstd();
		cM[0][3]=user.getGeoVbiz();
		cM[0][4]=user.getGeoVsec();
		cM[0][5]=user.getGeoVinf();
		cM[0][6]=user.getGeoVpriva();
                
                cM[1][2]=user.getPastVcertstd();
		cM[1][3]=user.getPastVbiz();
		cM[1][4]=user.getPastVsec();
		cM[1][5]=user.getPastVinf();
		cM[1][6]=user.getPastVpriva();
                
                cM[2][3]=user.getCertstdVbiz();
		cM[2][4]=user.getCertstdVsec();
		cM[2][5]=user.getCertstdVinf();
		cM[2][6]=user.getCertstdVpriva();
                
                cM[3][4]=user.getBizVsec();
		cM[3][5]=user.getBizVinf();
		cM[3][6]=user.getBizVpriva();
                
                cM[4][5]=user.getSecVinf();
                cM[4][6]=user.getSecVpriva();
                
                cM[5][6]=user.getPrivaVinf();
		
                for(int j=1; j<7;j++)
		{
			for(int k=0;k<j;k++) cM[j][k]=1/cM[k][j];
			
		}
		return cM;
	}
	
	
}
