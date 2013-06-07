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
package eu.optimis.serviceproviderriskassessmenttool.core.historicaldatabase.persistence;

import java.sql.Timestamp;

public class SLAObject {
	
	//		Instance Variables
			double offeredPof = 0.5;//get this value from somewhere
			boolean violated = false;
			public Timestamp startTime;
			
			
			public SLAObject(double offered, boolean status, Timestamp time)
			{
				offeredPof = offered;
				violated = status;
				startTime = time;
			}
			
			public double getOfferedPof()
			{
				return offeredPof;
			}
			
			public boolean getViolated()
			{
				return violated;
			}

			public Timestamp getStartTime() {
				return startTime;
			}
			
					

}
