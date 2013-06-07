/*
 * Copyright (c) 2013 National Technical University of Athens (NTUA)
 *	
 *   							MIT License
 *   
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package eu.optimis.ac.admissioncontroller;

import eu.optimis.ac.admissioncontroller.allocationOffer.GetAllocationOffer;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import org.apache.log4j.Logger;

@Path("/admission")
public class AdmissionController {
	
	public static Logger log = Logger.getLogger(AdmissionController.class);
        
	public AdmissionController() {
                                
        }//Constructor

    @POST
    @Produces(value="text/plain")
    @Consumes("application/x-www-form-urlencoded")
    public String getAdmissionControl(MultivaluedMap<String, String> formParams) {
    	
        GetAllocationOffer getOffer = new GetAllocationOffer(formParams, log);
        
        return getOffer.offer;
    }//getAdmissionControl()
    
    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public MultivaluedMap<String, String> getAdmissionController(MultivaluedMap<String, String> formParams)
    {
        GetAllocationOffer getOffer = new GetAllocationOffer(formParams, log);
        
        return getOffer.outputParams;
    }//getAdmissionController()
}//class
    