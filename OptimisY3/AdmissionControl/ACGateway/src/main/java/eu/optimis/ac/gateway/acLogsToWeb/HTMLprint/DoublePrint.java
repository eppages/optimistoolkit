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

import eu.optimis.ac.gateway.allocationOffer.TRECvalues;
import eu.optimis.ac.gateway.analyzeAllocationOffer.AllocationOfferInfoAsList;
import eu.optimis.ac.gateway.init_finish.Initialize;
import eu.optimis.ac.gateway.utils.HTMLtables;
import eu.optimis.ac.gateway.utils.HTMLtables2;
import eu.optimis.ac.smanalyzer.SMsAnalyzer;
import java.util.ArrayList;
import java.util.Arrays;
import org.apache.log4j.Logger;

public class DoublePrint {
    
    
    public String Position11 = "";
    public ArrayList<String> Header_List = new ArrayList<String>();
    public ArrayList<String> Tag_List = new ArrayList<String>();
    public ArrayList<ArrayList<String>> Info_List = new ArrayList<ArrayList<String>>();
	
    private ArrayList<String> AllocationOffer_List = new ArrayList<String>();
        
    public DoublePrint(Initialize initialize,ArrayList<String> ServiceID_List,String timeStamp, ArrayList<TRECvalues> ListOfTRECvalues,AllocationOfferInfoAsList allocationOfferList,SMsAnalyzer smAnalyzer,Logger log)
    {
                this.setTag_List(allocationOfferList);
		
                this.setHeader_List(ServiceID_List,timeStamp);
                
		this.set_List(initialize,ListOfTRECvalues,allocationOfferList,smAnalyzer,log);
		
		Info_List.add(AllocationOffer_List);
		
    }//constructor
    
    private void set_List(Initialize initialize,ArrayList<TRECvalues> ListOfTRECvalues, AllocationOfferInfoAsList allocationOfferList,SMsAnalyzer smAnalyzer,Logger log)
    {
        int Component_Id_Length = 0;
        int Decision_Length = 0;
        int Basic_Length = 0;
        int Elastic_Length = 0;
        int ElasticNumber_Length = 0;
    
        for(int i=0;i<allocationOfferList.Number_Of_Allocation_Offer;i++)
        {
			log.info("<--PrintAllocationPatternForOneSM : "+(i+1)+"/"+allocationOfferList.Number_Of_Allocation_Offer);
			
			PrintAllocationPatternForOneSM printAllocationPattern = 
					new PrintAllocationPatternForOneSM(allocationOfferList,smAnalyzer,i,log);
			
			log.info("<--PrintAllocationPatternForOneSM : "+(i+1)+"/"+allocationOfferList.Number_Of_Allocation_Offer);
			
                        if(ElasticNumber_Length<printAllocationPattern.physicalHostsNumber_Length)ElasticNumber_Length = printAllocationPattern.physicalHostsNumber_Length;
                        if(Elastic_Length<printAllocationPattern.Elastic_Length)Elastic_Length = printAllocationPattern.Elastic_Length;
                        if(Basic_Length<printAllocationPattern.Basic_Length)Basic_Length = printAllocationPattern.Basic_Length;
                        if(Decision_Length<printAllocationPattern.Decision_Length)Decision_Length = printAllocationPattern.Decision_Length;
                        if(Component_Id_Length<printAllocationPattern.Component_Id_Length)Component_Id_Length = printAllocationPattern.Component_Id_Length;
	}//for -i
        
        int sum = Component_Id_Length + Decision_Length + Basic_Length + Elastic_Length + ElasticNumber_Length;
        
        Component_Id_Length = 1+(int)(100*(Component_Id_Length)/(sum));
        Decision_Length = 1+(int)(100*(Decision_Length)/(sum));
        Basic_Length = 1+(int)(100*(Basic_Length)/(sum));
        
        ElasticNumber_Length = 1+(int)(100*(ElasticNumber_Length)/(sum));
        Elastic_Length = 100-(Component_Id_Length+Decision_Length+Basic_Length+ElasticNumber_Length);
        
        //Elastic_Length = 1+(int)(100*(Elastic_Length)/(sum));
        //ElasticNumber_Length = 100-(Component_Id_Length+Decision_Length+Basic_Length+Elastic_Length);
        
        int widthPercentage11 = Component_Id_Length;
        ArrayList<Integer> widthPercentage_List = new ArrayList<Integer>
                (Arrays.asList(Decision_Length,Basic_Length,Elastic_Length,ElasticNumber_Length));
        
        for(int i=0;i<allocationOfferList.Number_Of_Allocation_Offer;i++)
        {
            PrintAllocationPatternForOneSM printAllocationPattern = 
					new PrintAllocationPatternForOneSM(allocationOfferList,smAnalyzer,i,log);
            
            String allocationPatternTable = HTMLtables2.CreateHTMLtable2
                    ("1",printAllocationPattern.Position11,
                    printAllocationPattern.Header_List,printAllocationPattern.Tag_List,
                    printAllocationPattern.Info_List,
                    widthPercentage11,widthPercentage_List);
			
            AllocationOffer_List.add(allocationPatternTable);
        }//for-i
        
        PrintTRECdetails printTRECdetails = new PrintTRECdetails(initialize,ListOfTRECvalues,log);
                
        String TRECtable = HTMLtables2.CreateHTMLtable2
                ("1",printTRECdetails.Position11,
                printTRECdetails.Header_List,printTRECdetails.Tag_List,
                printTRECdetails.Info_List,
                widthPercentage11,widthPercentage_List);
        
        AllocationOffer_List.add(TRECtable);
    }//set_List()
    
    private void setHeader_List(ArrayList<String> ServiceID_List,String timeStamp)
    {
        Position11="At "+timeStamp;
        
        if(ServiceID_List.size()==1)
            Header_List.add(ServiceID_List.get(0));
        else
            Header_List.add("");
        
    }//setHeader_List()
    
    private void setTag_List(AllocationOfferInfoAsList allocationOfferList)
    {	
                for(int i=0;i<allocationOfferList.Number_Of_Allocation_Offer;i++)
                {
                    String tableName="AllocationOffer";
			if(allocationOfferList.Number_Of_Allocation_Offer!=1)
				tableName+=" of service Manifest -"+Integer.toString(i+1)+"-";
                    Tag_List.add(tableName);
                }//for-i
		
                Tag_List.add("TREC Details");
    }//setTag_List()
}//class
