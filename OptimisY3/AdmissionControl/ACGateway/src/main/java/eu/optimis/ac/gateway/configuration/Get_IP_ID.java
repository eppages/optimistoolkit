/*
 * Copyright (c) 2013 National Technical University of Athens (NTUA)
 *	
 *   							MIT License
 *   
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */


package eu.optimis.ac.gateway.configuration;

import eu.optimis.ac.ACRestClients.MyRestClients.RestClient_noInput_String;
import eu.optimis.ac.gateway.configuration.outsideWarConfiguration.OutsideWarConfiguration;
import java.net.InetAddress;
import org.apache.log4j.Logger;

public class Get_IP_ID {
    
    public static String getIP_Id(String host,String port, Logger log)
    {
        String IP_id = null;
        
        if(OutsideWarConfiguration.isOutsideWarConfigurationFileExists())
            IP_id = OutsideWarConfiguration.getIP_Id(log);
        
        if (IP_id == null)
            IP_id = getIP_Id(host,port);
	        
         return IP_id;
                
    }//getIP_Id(Logger log)
    
    private static String getIP_Id(String host,String port)
	{
		String homeId = null;
    	
         RestClient_noInput_String client = new
                    RestClient_noInput_String(host, port, "/CloudOptimizer/ip/id");
            
            if(client.status == 200) homeId = client.returnedString.toUpperCase();       
            
            int x=1; if(x==1)return homeId;
                
    	homeId=getComputerName();
    	
        if(homeId.contains("ipvm-flex-enhanced"))
    		homeId = "FLEX 2";
        //else if(homeId.contains("optimis-ipvm2"))
    	//	homeId = "ATOS 2";
    	//else if(homeId.contains("ipvm2-umea"))
    	//	homeId = "UMU";
    	else if(homeId.contains("centos-optimis-IP"))
    		homeId = "ATOS";
    	else if(homeId.contains("ipvm-flex-full"))
    		homeId = "FLEX";
    	//else if(homeId.contains("optimis-ipvm"))
    	//	homeId = "ARSYS";
        /*
        else
        {
            RestClient_noInput_String client = new
                    RestClient_noInput_String(host, port, "/CloudOptimizer/ip/id");
            
            if(client.status == 200) homeId = client.returnedString.toUpperCase();
            
        }//else
        */
    	return homeId;
	}//getIP_Id()
	
	private static String getComputerName()
	{
		String computername="XXXXX";
		
		try{
			  computername=InetAddress.getLocalHost().getHostName();
			  
			  }catch (Exception e){
                              return e.getMessage();
			  }
		return computername;
	}//GetComputerName()
    
}//class
