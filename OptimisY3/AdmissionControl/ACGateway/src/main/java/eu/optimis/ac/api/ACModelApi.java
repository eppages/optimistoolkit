/*
 * Copyright (c) 2013 National Technical University of Athens (NTUA)
 *	
 *   							MIT License
 *   
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package eu.optimis.ac.api;

import com.sun.jersey.core.util.MultivaluedMapImpl;
import eu.optimis.ac.gateway.acGateway.ACmain;
import eu.optimis.ac.gateway.init_finish.DoNotBackupSMflag;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import org.apache.log4j.Logger;

import eu.optimis.manifest.api.impl.AllocationOfferDecision;

@Path("/model")
public class ACModelApi {

    public static Logger log = Logger.getLogger(ACModelApi.class);	
	
    @POST
    @Path("/performACTest")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public MultivaluedMap<String, String> performACTest(MultivaluedMap<String, String> Params)
	{		
		return ACTest(Params);
                
	}//perfomACTest()
    
    @POST
    @Path("/performOneManifestACTest")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public String performACTest(String serviceManifest)
	{
                MultivaluedMap<String, String> Params = new MultivaluedMapImpl();
		Params.add("serviceManifest", serviceManifest);
		
		return ACTest(Params).get("serviceManifest").get(0);
		
	}//perfomACTest()
    
    private MultivaluedMap<String, String> ACTest(MultivaluedMap<String, String> Params)
    {           
                
                ACmain ac = new ACmain(Params,log);
                
                MultivaluedMap<String, String> params = new MultivaluedMapImpl();
                for(int i=0;i<ac.ReturnsParams.get("serviceManifest").size();i++)
                {
                    String serviceManifest = ac.ReturnsParams.get("serviceManifest").get(i);
                    
                    if(!Params.containsKey(DoNotBackupSMflag.flagName)
                            &&(serviceManifest.contains("AllocationPattern")))
                        serviceManifest = serviceManifest.replace(
                            AllocationOfferDecision.partial.toString()
                            , AllocationOfferDecision.accepted.toString());
                    
                    //log.info("---------------- printing SM"+(i+1));
                    //log.info(serviceManifest);
                    //log.info("---------------- end of printing SM"+(i+1));
                    
                    params.add("serviceManifest", serviceManifest);
                }//for-i
                params.add("AllocationDetails", ac.ReturnsParams.get("AllocationDetails").get(0));
                
                return params;
        
    }//ACTest()
    
}//Class 




    