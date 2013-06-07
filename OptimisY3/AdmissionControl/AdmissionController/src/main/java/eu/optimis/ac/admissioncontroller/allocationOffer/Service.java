/*
 * Copyright (c) 2013 National Technical University of Athens (NTUA)
 *	
 *   							MIT License
 *   
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package eu.optimis.ac.admissioncontroller.allocationOffer;

import java.util.List;

class Service {
	
	// GAMS name
	protected String name;
	protected boolean accepted;
	protected int totalElasticVMs;
	protected float riskValue;
	protected String costFile;
	
	// actual name 
	protected String uniqueID;
	
	protected List<Component> componentList;
	// uniqueID and name should be unique 
        
        protected String TRUST_FOR_NewService;
        protected String PROBABILITY_FOR_ServiceFail;
        protected String ECO_FOR_NewService;
        protected String COST_FOR_HostingService;
        
	Service(String name) {
		this.name = name;
		this.totalElasticVMs = 0;
		this.riskValue = -1;
		this.accepted = false;
		// costFile is initialized in a previously generated file 
		this.costFile = "cost.xml";
                
                
                this.TRUST_FOR_NewService = null;
                this.PROBABILITY_FOR_ServiceFail = null;
                this.ECO_FOR_NewService = null;
                this.COST_FOR_HostingService = null;
	}//Constructor
} //Class