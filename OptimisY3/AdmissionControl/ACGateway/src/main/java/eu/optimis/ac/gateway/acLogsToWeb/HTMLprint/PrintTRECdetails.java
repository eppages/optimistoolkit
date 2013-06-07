/*
 * Copyright (c) 2013 National Technical University of Athens (NTUA)
 *	
 *   							MIT License
 *   
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */


package eu.optimis.ac.gateway.acLogsToWeb.HTMLprint;

import eu.optimis.ac.gateway.acCsvInfo.GetTREC_constraints;
import eu.optimis.ac.gateway.acCsvInfo.GetTREC_weights;
import eu.optimis.ac.gateway.allocationOffer.TRECvalues;
import eu.optimis.ac.gateway.init_finish.Initialize;
import java.util.ArrayList;
import org.apache.log4j.Logger;

public class PrintTRECdetails {
    
    public String Position11 = "";
    public ArrayList<String> Header_List = new ArrayList<String>();
    public ArrayList<String> Tag_List = new ArrayList<String>();
    public ArrayList<ArrayList<String>> Info_List = new ArrayList<ArrayList<String>>();
	
    private ArrayList<String> Trust_List = new ArrayList<String>();
    private ArrayList<String> Risk_List = new ArrayList<String>();
    private ArrayList<String> Eco_List = new ArrayList<String>();
    private ArrayList<String> Cost_List = new ArrayList<String>();
    
    public PrintTRECdetails(Initialize initialize,ArrayList<TRECvalues> ListOfTRECvalues, Logger log)
    {
        this.setHeader_List();
		
	this.set_Lists(initialize,ListOfTRECvalues,log);
		
	log.info("Trust_List: "+Trust_List);
	log.info("Risk_List: "+Risk_List);
	log.info("Eco_List: "+Eco_List);
	log.info("Cost_List: "+Cost_List);
		
	Info_List.add(Trust_List);
	Info_List.add(Risk_List);
	Info_List.add(Eco_List);
	Info_List.add(Cost_List);
    }//constructor
    
    private void set_Lists(Initialize initialize,ArrayList<TRECvalues> ListOfTRECvalues, Logger log)
    {
        String gamsPath = initialize.gamsPath;
        
        String trust_weight = GetTREC_weights.getTrust_Weight2(gamsPath,log);
        String risk_weight = GetTREC_weights.getRisk_Weight2(gamsPath,log);
        String eco_weight = GetTREC_weights.getEco_Weight2(gamsPath,log);
        String cost_weight = GetTREC_weights.getCost_Weight2(gamsPath,log);
        
        String trust_constraint = GetTREC_constraints.getTrust_Constraint2(gamsPath,log);
        String risk_constraint = GetTREC_constraints.getRisk_Constraint2(gamsPath,log);
        String eco_constraint = GetTREC_constraints.getEco_Constraint2(gamsPath,log);
        String cost_constraint = GetTREC_constraints.getCost_Constraint2(gamsPath,log);
        
        if(ListOfTRECvalues.size()>1)
        {
            for(int i=0; i<ListOfTRECvalues.size();i++)
            {
                Tag_List.add("TREC values -"+(i+1));
            }//for-i
        }//if
        else
            Tag_List.add("TREC values");
        
        if(eco_constraint.hashCode()==0)Tag_List.add("TREC_constraints");
        else Tag_List.add("TREC constraints");
        if(eco_weight.hashCode()==0)Tag_List.add("TREC_weights");
        else Tag_List.add("TREC weights");
        
        if(trust_weight.hashCode()==0)trust_weight = GetTREC_weights.getTempTrust_Weight2(gamsPath,log);
        if(risk_weight.hashCode()==0)risk_weight = GetTREC_weights.getTempRisk_Weight2(gamsPath,log);
        if(eco_weight.hashCode()==0)eco_weight = GetTREC_weights.getTempEco_Weight2(gamsPath,log);
        if(cost_weight.hashCode()==0)cost_weight = GetTREC_weights.getTempCost_Weight2(gamsPath,log);
        
        if(trust_constraint.hashCode()==0)trust_constraint = GetTREC_constraints.getTempTrust_Constraint2(gamsPath,log);
        if(risk_constraint.hashCode()==0)risk_constraint = GetTREC_constraints.getTempRisk_Constraint2(gamsPath,log);
        if(eco_constraint.hashCode()==0)eco_constraint = GetTREC_constraints.getTempEco_Constraint2(gamsPath,log);
        if(cost_constraint.hashCode()==0)cost_constraint = GetTREC_constraints.getTempCost_Constraint2(gamsPath,log);
        
        for(int i=0; i<ListOfTRECvalues.size();i++)
        {        
            Trust_List.add(ListOfTRECvalues.get(i).trust);
            Risk_List.add(ListOfTRECvalues.get(i).risk);
            Eco_List.add(ListOfTRECvalues.get(i).eco);
            Cost_List.add(ListOfTRECvalues.get(i).cost);
        }//for-i
        
        Trust_List.add(trust_constraint);
        Risk_List.add(risk_constraint);
        Eco_List.add(eco_constraint);
        Cost_List.add(cost_constraint);
        
        Trust_List.add(trust_weight);
        Risk_List.add(risk_weight);
        Eco_List.add(eco_weight);
        Cost_List.add(cost_weight);
        
    }//set_Lists()
    
    private void setHeader_List()
    {
	Position11="";
		
	Header_List.add("Trust");
	Header_List.add("Risk");
	Header_List.add("Eco");
	Header_List.add("Cost");
        
    }//setHeader_List()
    
}//class
