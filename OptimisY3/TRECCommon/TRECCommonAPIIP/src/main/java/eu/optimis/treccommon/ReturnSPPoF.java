/*
 *  Copyright 2013 University of Leeds UK, ATOS SPAIN S.A., City University London, Barcelona Supercomputing Centre and SAP
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

package eu.optimis.treccommon;

import java.util.List;

public class ReturnSPPoF{

        private List<String> SPNames;
	private List<Double> PoFSLA;

	public ReturnSPPoF()
	{
            SPNames=null;
            PoFSLA=null;
	}
	//getters and setters
        
        public void setSPNames(List<String> SPNames){
            this.SPNames = SPNames;
        }
        public void setPoFSLA(List<Double> PoFSLA){
            this.PoFSLA = PoFSLA;
        }
        
        public List<String> getSPNames(){
            return this.SPNames;
        }
        public List<Double> getPoFSLA(){
            return this.PoFSLA;
        }	
}