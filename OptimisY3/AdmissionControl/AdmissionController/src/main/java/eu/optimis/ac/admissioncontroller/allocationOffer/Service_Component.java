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

import java.util.LinkedList;
import java.util.List;

class Service_Component {
	
	protected Service s;
	protected Component c;
	protected int basicVMs;
	protected int elasticVMs;
	protected int totalVMs;
	protected int assignedVMs;
	protected List<PhysicalHost> physicalHostsList;
	protected String uniqueID;
	
        protected String VMS_FOR_FEDERATION;
        
	protected Service_Component(Service s, Component c) {
		this.s = s;
		this.c = c;
		
		this.basicVMs = 0;
		this.elasticVMs = 0;
		this.totalVMs = 0;
		this.assignedVMs = 0;
		this.physicalHostsList = new LinkedList<PhysicalHost>();
		
		this.uniqueID = null;
                
                this.VMS_FOR_FEDERATION = null;
                
	}//Constructor
	
	public void updateBasic() {
		this.basicVMs = this.totalVMs - this.elasticVMs; 
	}//updateBasic()
        
} //Class
