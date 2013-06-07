/*
 * Copyright (c) 2013 National Technical University of Athens (NTUA)
 *	
 *   							MIT License
 *   
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */



package eu.optimis.ac.gateway.analyzeAllocationOffer;

import com.sun.jersey.core.util.MultivaluedMapImpl;
import java.util.ArrayList;
import javax.ws.rs.core.MultivaluedMap;
import org.apache.xmlbeans.XmlObject;

public class AllocationOfferInfo
{
	private String Service_Id = null;
	
	private String IP_Id = null;
	
	private int AdmissionControlDecision = -1;
	
	private XmlObject PricePlan = null;
	
	private String Risk = null;
	
	private int NumberOfAcceptedServiceComponents = 0;
	
	public ArrayList<String> ListOfAccepted_ServiceComponents = new ArrayList<String>();
	
	public MultivaluedMap<String, String> BasicPhysicalHost = new MultivaluedMapImpl();
	
	public MultivaluedMap<String, String> ElasticPhysicalHost = new MultivaluedMapImpl();
	
	public AllocationOfferInfo()
	{
		
	}//constructor
	
	public String getService_Id() {
		return Service_Id;
	}//getService_Id()

	public void setService_Id(String service_Id) {
		Service_Id = service_Id;
	}//setService_Id
	
	public String getIP_Id() {
		return IP_Id;
	}//getIP_Id()

	public void setIP_Id(String iP_Id) {
		IP_Id = iP_Id;
	}//setIP_Id(String iP_Id)
	
	public int getAdmissionControlDecision() {
		return AdmissionControlDecision;
	}//getAdmissionControlDecision()

	public void setAdmissionControlDecision(int the_Decision) {
		AdmissionControlDecision = the_Decision;
	}//setAdmissionControlDecision(int the_Decision)
	
	public String getRisk() {
		return Risk;
	}//getRisk()

	public void setRisk(String risk) {
		Risk = risk;
	}//setRisk(String risk)
	
	public XmlObject getPricePlan() {
		return PricePlan;
	}//getPricePlan()
	
	public void setPricePlan(XmlObject pricePlan) {
		PricePlan = pricePlan;
	}//setPricePlan(XmlObject pricePlan)
	
	public int getNumberOfAcceptedServiceComponents() {
		return NumberOfAcceptedServiceComponents;
	}//getNumberOfAcceptedServiceComponents()

	public void setNumberOfAcceptedServiceComponents(
			int numberOfAcceptedServiceComponents) {
		NumberOfAcceptedServiceComponents = numberOfAcceptedServiceComponents;
	}//setNumberOfAcceptedServiceComponents()
	
	public ArrayList<String> getListOfAccepted_ServiceComponents() {
		return ListOfAccepted_ServiceComponents;
	}//getListOfAccepted_ServiceComponents()
	
}//class