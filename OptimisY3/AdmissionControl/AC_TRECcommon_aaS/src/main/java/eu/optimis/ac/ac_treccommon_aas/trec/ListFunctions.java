/*
 * Copyright (c) 2013 National Technical University of Athens (NTUA)
 *	
 *   							MIT License
 *   
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package eu.optimis.ac.ac_treccommon_aas.trec;

import com.sun.jersey.core.util.MultivaluedMapImpl;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import javax.ws.rs.core.MultivaluedMap;
import org.apache.log4j.Logger;

public class ListFunctions
{
	public static List<String> Get_Host_Id_AsList(MultivaluedMap<String, String> Params,Logger log)
	{
		List<String> Host_IDAsList = new LinkedList<String>();
		
                log.info(Params);
                
		for(int i=0;i<Params.get("host_id").size();i++)	
			Host_IDAsList.add(Params.get("host_id").get(i));
		
                log.info(Host_IDAsList);
                
		return Host_IDAsList;
		
	}//Get_Host_Id_AsList()
        
        
        public static HashMap<String, String> Get_Host_Id_As_HashMap(MultivaluedMap<String, String> Params,Logger log)
	{
		HashMap<String, String> hosts = new HashMap<String, String>();
		
                log.info(Params);
                
		for(int i=0;i<Params.get("host_id").size();i++)	
                {	
                    hosts.put(Params.get("host_id").get(i), "24");
                }//for-i
                
                log.info(hosts);
                
		return hosts;
		
	}//Get_Host_Id_AsList()
        
        public static MultivaluedMap<String, String> Get_RiskLevel_Host_fromList(List<Integer> RiskHostAsList)
	{
		MultivaluedMap<String, String> formParams = new MultivaluedMapImpl();
		
		for(int i=0;i<RiskHostAsList.size();i++)
                {
                        int riskHost = RiskHostAsList.get(i);
                        
			formParams.add("riskHost", Integer.toString(riskHost));
                        
                }//for-i
			
		return formParams;
			
	}//Get_Risk_Host_fromList()
        
	public static MultivaluedMap<String, String> Get_Eco_Host_fromList(List<String> EcoHostAsList)
	{
		MultivaluedMap<String, String> formParams = new MultivaluedMapImpl();
		
		for(int i=0;i<EcoHostAsList.size();i++)
                {
                        String ecoHost = EcoHostAsList.get(i);
                        
                        if(ecoHost.contains("."))
                        {
                            String[] temp = ecoHost.replace(".", " ").split(" ");
                    
                            if(temp[1].length()>2)
                                ecoHost = temp[0]+"."+temp[1].substring(0,2);
                    
                        }//if - .
                        
			formParams.add("ecoHost", ecoHost);
                        
                }//for-i
			
		return formParams;
			
	}//Get_Eco_Host_fromList()

        public static MultivaluedMap<String, String> Host_Ids_As_Map(List<String> Host_IDAsList)
        {
                MultivaluedMap<String, String> formParams = new MultivaluedMapImpl();
                
                for(int i=0;i<Host_IDAsList.size();i++)
			formParams.add("host_id", Host_IDAsList.get(i));
                
                return formParams;
        }//Host_Ids_As_Map()
}//class