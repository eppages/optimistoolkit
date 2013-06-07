/*
 * Copyright (c) 2013 National Technical University of Athens (NTUA)
 *	
 *   							MIT License
 *   
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package eu.optimis.ac.gateway.serviceManifestFunctions;

import eu.optimis.manifest.api.impl.AllocationOfferDecision;
import eu.optimis.manifest.api.ip.Manifest;
import eu.optimis.types.xmlbeans.servicemanifest.XmlBeanServiceManifestDocument;
import org.apache.log4j.Logger;
import org.apache.xmlbeans.XmlException;

public class ReadDecisionFromSM {

        public static String getDecision(String serviceManifest)
                throws XmlException
        {
            XmlBeanServiceManifestDocument xmlBeanManifest = XmlBeanServiceManifestDocument.Factory.parse(serviceManifest);
			
            Manifest ipManifest = Manifest.Factory.newInstance(xmlBeanManifest);
			
            return ipManifest.getInfrastructureProviderExtensions().getAllocationOffer().getDecision();
			
        }//getDecision(String serviceManifest)
    
	public static String DecisionIs(String serviceManifest,Logger log)
	{
		String theDecisionIs = null;
                
		try{
			theDecisionIs = getDecision(serviceManifest);
                        
		} catch (XmlException e) {
			log.error(e.getMessage());
			e.printStackTrace();
		}
		
		return theDecisionIs;
	}//DecisionIs()
	
        public static String DecisionIs(String serviceManifest)
	{
		String theDecisionIs = null;
		try{
			theDecisionIs = getDecision(serviceManifest);
                        
		} catch (XmlException e) {
			return e.getMessage();
		}
		
		return theDecisionIs;
	}//DecisionIs()
	
        public static Boolean isDecisionAccept(String serviceManifest,Logger log)
	{
		
		if(DecisionIs(serviceManifest,log).equals(AllocationOfferDecision.accepted.toString()))
			return true;
		
		return false;
		
	}//isDecisionAccept()
        
	public static Boolean isDecisionAccepted(String serviceManifest)
	{
		
		if(DecisionIs(serviceManifest).equals(AllocationOfferDecision.accepted.toString()))
			return true;
		
		return false;
		
	}//isDecisionAccept()
        
        public static Boolean isDecisionRejected(String serviceManifest)
	{
		
		if(DecisionIs(serviceManifest).equals(AllocationOfferDecision.rejected.toString()))
			return true;
		
		return false;
		
	}//isDecisionAccept()
        
        public static Boolean isDecisionPartialAccepted(String serviceManifest)
	{
		if(DecisionIs(serviceManifest).equals(AllocationOfferDecision.partial.toString()))
			return true;
		
		return false;
		
	}//isDecisionPartialAccept()
}//class
