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
package eu.optimis.infrastructureproviderriskassessmenttool.core;

import java.util.ArrayList;

public class ReturnSPPoF{

        private ArrayList<String> SPNames;
	private ArrayList<Double> PoFSLA;

	public ReturnSPPoF()
	{
            SPNames=null;
            PoFSLA=null;
	}
	//getters and setters
        
        public void setSPNames(ArrayList<String> SPNames){
            this.SPNames = SPNames;
        }
        public void setPoFSLA(ArrayList<Double> PoFSLA){
            this.PoFSLA = PoFSLA;
        }
        
        public ArrayList<String> getSPNames(){
            return this.SPNames;
        }
        public ArrayList<Double> getPoFSLA(){
            return this.PoFSLA;
        }	
}