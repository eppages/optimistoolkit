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


public class ACperServiceConstraintsRestClient {
    
    public int status = 0;
    
    public String returnedString = null;
    
    private static String perServiceConstraintsString = "/ACGateway/data/perServiceConstraints";
    
    private String getPerServiceConstraintsURL(int trustLevel_constraint, int riskLevel_constraint,
            String ecoValue_constraint, String costInEuros_constraint)
    {
        return perServiceConstraintsString
                +"/"+trustLevel_constraint+"/"+riskLevel_constraint+
            		"/"+ecoValue_constraint+"/"+costInEuros_constraint;
    }//getPerServiceConstraintsURL()
    
    public ACperServiceConstraintsRestClient(String host,String port,
            int trustLevel_constraint, int riskLevel_constraint,
            String ecoValue_constraint, String costInEuros_constraint)
    {
        String urlStr = getPerServiceConstraintsURL(trustLevel_constraint, riskLevel_constraint,
                ecoValue_constraint, costInEuros_constraint);
        
        RestClient_noInput_String perServiceConstraintsClient = new RestClient_noInput_String(host,port,urlStr);
        
        status = perServiceConstraintsClient.status;
        
        returnedString = perServiceConstraintsClient.returnedString;
        
    }//ACperServiceConstraintsRestClient() - Constructor-1
    
    public ACperServiceConstraintsRestClient(String host,String port,
            int trustLevel_constraint, int riskLevel_constraint,
            String ecoValue_constraint, String costInEuros_constraint,Logger log)
    {
        
        String urlStr = getPerServiceConstraintsURL(trustLevel_constraint, riskLevel_constraint,
                ecoValue_constraint, costInEuros_constraint);
        
        RestClient_noInput_String perServiceConstraintsClient = new RestClient_noInput_String(host,port,urlStr,log);
        
        status = perServiceConstraintsClient.status;
        
        returnedString = perServiceConstraintsClient.returnedString;
        
    }//ACperServiceConstraintsRestClient() - Constructor-2
    
}//class
