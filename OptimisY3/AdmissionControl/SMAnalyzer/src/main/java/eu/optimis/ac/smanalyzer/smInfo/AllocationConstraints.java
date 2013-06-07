/*
 * Copyright (c) 2013 National Technical University of Athens (NTUA)
 *	
 *   							MIT License
 *   
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package eu.optimis.ac.smanalyzer.smInfo;

import org.apache.log4j.Logger;
import org.w3c.dom.Element;

class AllocationConstraints {
                  
    protected  static void getAllocationConstraints(Element manifest,ServiceComponentInfo sc_info,
            String componentId,Logger log,Boolean DisplayAllLogs)
    {
        if(DisplayAllLogs)
	log.info("Start of extractAllocationConstraintsFromVMC");
        
        AllocationConstraintsDetails allocationConstraintsDetails = new AllocationConstraintsDetails(manifest);
        
        String UpperBound = allocationConstraintsDetails.UpperBound;
        String Initial = allocationConstraintsDetails.Initial;
        
        sc_info.setElasticVms(UpperBound);
        sc_info.setBaseVms(Initial);
        
        if(DisplayAllLogs)
        log.info("Finish AllocationConstraints");
        
    }//Constructor
    
}//class
