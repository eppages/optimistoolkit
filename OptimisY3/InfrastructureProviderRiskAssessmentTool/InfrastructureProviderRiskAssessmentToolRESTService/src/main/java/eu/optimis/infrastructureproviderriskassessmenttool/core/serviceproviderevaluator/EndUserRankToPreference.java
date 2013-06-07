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
 * Provides the functionality to create a UserPreferenceObject
 * of pairwise comparison values form an EndUserRankingObject.
 * @author Iain Gourlay
 */
public class EndUserRankToPreference {

	public static UserPreferenceObject computePreferences(EndUserRankingObject ranks) throws Exception
	{


		double pastVsec=1.0; 
		double pastVbizstab=1.0;
		double secVstab = 1.0;
		
		UserPreferenceObject preferences = new UserPreferenceObject();
		
		double diff = 0;
		
		diff = ranks.getPastPerformanceRank()-ranks.getSecurityRank();
		if(diff>=0.0)
		{
			if(diff>=8.0) pastVsec = 9.0;
			else if(diff>=6.0) pastVsec = 7.0;
			else if (diff>=4.0)pastVsec = 5.0;
			else if (diff>=2.0) pastVsec = 3.0;
			else pastVsec = 1.0;
		}
		else
		{
			diff = 1-diff;
			if(diff>=8.0) pastVsec = 1.0/9.0;
			else if(diff>=6.0) pastVsec = 1.0/7.0;
			else if (diff>=4.0)pastVsec = 1.0/5.0;
			else if (diff>=2.0) pastVsec = 1.0/3.0;
			else pastVsec = 1.0;
		}
		preferences.setPastVsec(pastVsec);
	
		
		diff = ranks.getPastPerformanceRank()-ranks.getBusinessStabilityRank();
		if(diff>=0.0)
		{
			if(diff>=8.0) pastVbizstab = 9.0;
			else if(diff>=6.0) pastVbizstab = 7.0;
			else if (diff>=4.0)pastVbizstab = 5.0;
			else if (diff>=2.0) pastVbizstab = 3.0;
			else pastVbizstab = 1.0;
		}
		else
		{
			diff = 1-diff;
			if(diff>=8.0) pastVbizstab = 1.0/9.0;
			else if(diff>=6.0) pastVbizstab = 1.0/7.0;
			else if (diff>=4.0)pastVbizstab = 1.0/5.0;
			else if (diff>=2.0) pastVbizstab = 1.0/3.0;
			else pastVbizstab = 1.0;
		}
		preferences.setPastVBizstab(pastVbizstab);
		
		
		diff = ranks.getSecurityRank()-ranks.getBusinessStabilityRank();
		if(diff>=0.0)
		{
			if(diff>=8.0) secVstab = 9.0;
			else if(diff>=6.0) secVstab = 7.0;
			else if (diff>=4.0)secVstab = 5.0;
			else if (diff>=2.0) secVstab = 3.0;
			else secVstab = 1.0;
		}
		else
		{
			diff = 1-diff;
			if(diff>=8.0) secVstab = 1.0/9.0;
			else if(diff>=6.0) secVstab = 1.0/7.0;
			else if (diff>=4.0)secVstab = 1.0/5.0;
			else if (diff>=2.0) secVstab = 1.0/3.0;
			else secVstab = 1.0;
		}
		preferences.setSecVBizstab(secVstab);
		
		
		
		return preferences;
	}
}
