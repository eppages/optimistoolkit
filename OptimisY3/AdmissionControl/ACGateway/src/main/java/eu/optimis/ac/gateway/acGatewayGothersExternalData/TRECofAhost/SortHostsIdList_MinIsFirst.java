/*
 * Copyright (c) 2013 National Technical University of Athens (NTUA)
 *	
 *   							MIT License
 *   
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */


package eu.optimis.ac.gateway.acGatewayGothersExternalData.TRECofAhost;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;

public class SortHostsIdList_MinIsFirst {
    
    public List<String> HostIdAsList = new LinkedList<String>();
    
    public List<String> RiskHostAsList = new LinkedList<String>();
    
    public SortHostsIdList_MinIsFirst(List<String> Host_IDAsList,
            Map <String,Double> RiskHostAsMap,Logger log)
    {
        
        while(Host_IDAsList.size()>0)
        {
            String host_id = Host_IDAsList.get(0);
            Double value = RiskHostAsMap.get(host_id);
            int ii = 0;
            
            for(int i=1;i<Host_IDAsList.size();i++)
            {
               if(RiskHostAsMap.get(Host_IDAsList.get(i)) < value)
               {
                   host_id = Host_IDAsList.get(i);
                   ii=i;
                   value = RiskHostAsMap.get(host_id);
               }//if
               
            }//for-i
            
            HostIdAsList.add(host_id);
            RiskHostAsList.add(Double.toString(value));
            Host_IDAsList.remove(ii);
            RiskHostAsMap.remove(host_id);
        }//while
        
    }//Constructor
}//class
