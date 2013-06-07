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

import java.util.EnumSet;

public enum Tags {
		// TODO define in properties file
		// s1: Admission Control Result
		// s2: how many vms of service component i are allocated for service a in host j
		// s3: number of accepted elastic vms for each service
		// s4: number of accepted elastic vms per service component
		 
		XFACTOR("Admission Control Result"),
		XX("how many vms of service component i are allocated for service a in host j"),
		ACCEPTED_ELASTIC_VMS("number of accepted elastic vms for each service"),
		ACCEPTED_ELASTIC_VMS_PER_COMP("number of accepted elastic vms per service component"),
                VMS_FOR_FEDERATION("number of vms for federation"),
                TRUST_FOR_NewService("Trust for new service"),
                PROBABILITY_FOR_ServiceFail("Probability the service will fail"),
                ECO_FOR_NewService("Eco for new service"),
                COST_FOR_HostingService("cost of hosting the service");
                
		private String value;
		
		Tags(String value) {
			//Properties props = new Properties();
			//try {
			//	props.load(AdmissionController.class.getClassLoader().
				//		getResourceAsStream("gams.properties"));
			//} catch (IOException ioe) {
				//ioe.printStackTrace();
				//log.error("There was an IOException: ", ioe);
			//}
            //this.value = props.getProperty(value);
			this.value = value;
        }
		
		public String toString(){
            return value;
        }
		
		public static Tags getByValue(String value) {
            Tags returnValue = null;
            for (final Tags element : EnumSet.allOf(Tags.class)) {
                if (element.toString().equals(value)) {
                    returnValue = element;
                }
            }
            return returnValue;
        }
} //end of enum Tags
