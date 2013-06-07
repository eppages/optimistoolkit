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
package eu.optimis.infrastructureproviderriskassessmenttool.core.serviceproviderevaluator;

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
		double [][] cM = new double [6][6];
		for (int i=0;i<6;i++) cM[i][i]=1;
		cM[0][1]=user.getPastVexp();
		cM[0][2]=user.getPastVcust();
		cM[0][3]=user.getPastVsec();
		cM[0][4]=user.getPastVinf();
		cM[0][5]=user.getPastVBizstab();
		cM[1][2]=user.getExpVcust();
		cM[1][3]=user.getExpVsec();
		cM[1][4]=user.getExpVinf();
		cM[1][5]=user.getExpVmaint();
		cM[2][3]=user.getCustVsec();
		cM[2][4]=user.getCustVinf();
		cM[2][5]=user.getCustVmaint();
		cM[3][4]=user.getSecVinf();
		cM[3][5]=user.getSecVBizstab();
		cM[4][5]=user.getInfVmaint();
		for(int j=1; j<6;j++)
		{
			for(int k=0;k<j;k++) cM[j][k]=1/cM[k][j];
			
		}
		return cM;
	}
	
	
}
