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
import java.util.Map;
import javax.ws.rs.core.MultivaluedMap;
import org.apache.log4j.Logger;

public class TRECvaluesPerService {
    
    public static void setTRECoutputParams(List<Service> serviceList,
            Map <String,List<ComponentIdentificationDetails>> Service_Component_Map,
            MultivaluedMap<String, String> outputParams,
            Logger log)
    {
        log.info("TRECvaluesPerService invoked");
        
        for (Service s : serviceList) {
            
            outputParams.add("TRUST_FOR_NewService", s.TRUST_FOR_NewService);
            outputParams.add("PROBABILITY_FOR_ServiceFail", s.PROBABILITY_FOR_ServiceFail);
            outputParams.add("ECO_FOR_NewService", s.ECO_FOR_NewService);
            outputParams.add("COST_FOR_HostingService", s.COST_FOR_HostingService);
          
        }//for-s
        
        log.info("TRECvaluesPerService invoked  finished");
        
    }//setTRECoutputParams()
}//class
