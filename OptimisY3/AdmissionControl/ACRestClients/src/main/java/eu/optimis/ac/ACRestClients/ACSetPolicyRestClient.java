/*
 * Copyright (c) 2013 National Technical University of Athens (NTUA)
 *	
 *   							MIT License
 *   
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package eu.optimis.ac.ACRestClients;

import eu.optimis.ac.ACRestClients.MyRestClients.RestClient_noInput_String;
import org.apache.log4j.Logger;

public class ACSetPolicyRestClient {
    
    public int status = 0;
    
    public String returnedString = null;
    
    private static String setPolicyString = "/ACGateway/data/setPolicy";
    
    private String getSetPolicyURL(String trust_weight,String risk_weight,String eco_weight,String cost_weight)
    {
        return setPolicyString
                +"/"+trust_weight+"/"+eco_weight+
            		"/"+risk_weight+"/"+cost_weight;
    }//getSetPolicyURL()
    
    public ACSetPolicyRestClient(String host,String port,
			String trust_weight,String risk_weight,String eco_weight,String cost_weight)
    {
        String urlStr = getSetPolicyURL(trust_weight,risk_weight,eco_weight,cost_weight);
        
        RestClient_noInput_String setACPolicyClient = new RestClient_noInput_String(host,port,urlStr);
        
        status = setACPolicyClient.status;
        
        returnedString = setACPolicyClient.returnedString;
        
    }//ACSetPolicyRestClient() - Constructor-1
    
    public ACSetPolicyRestClient(String host,String port,
			String trust_weight,String risk_weight,String eco_weight,String cost_weight,Logger log)
    {
        
        String urlStr = getSetPolicyURL(trust_weight,risk_weight,eco_weight,cost_weight);
        
        RestClient_noInput_String setACPolicyClient = new RestClient_noInput_String(host,port,urlStr,log);
        
        status = setACPolicyClient.status;
        
        returnedString = setACPolicyClient.returnedString;
        
    }//ACSetPolicyRestClient() - Constructor-2
    
}//class
