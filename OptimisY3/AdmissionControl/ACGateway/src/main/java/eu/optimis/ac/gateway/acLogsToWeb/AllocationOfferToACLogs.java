/*
 * Copyright (c) 2013 National Technical University of Athens (NTUA)
 *	
 *   							MIT License
 *   
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */


package eu.optimis.ac.gateway.acLogsToWeb;

import eu.optimis.ac.gateway.acLogsToWeb.HTMLprint.DoublePrint;
import eu.optimis.ac.gateway.acLogsToWeb.HTMLprint.PrintAllocationOfferForEverySM;
import eu.optimis.ac.gateway.acLogsToWeb.HTMLprint.PrintAllocationPatternForOneSM;
import eu.optimis.ac.gateway.acLogsToWeb.fiveLastExecutions.ReadExecutions;
import eu.optimis.ac.gateway.acLogsToWeb.fiveLastExecutions.ReadAllocationPatterns;
import eu.optimis.ac.gateway.acLogsToWeb.fiveLastExecutions.SaveAllocationPatterns;
import eu.optimis.ac.gateway.acLogsToWeb.fiveLastExecutions.SaveExecution;
import eu.optimis.ac.gateway.allocationOffer.TRECvalues;
import eu.optimis.ac.gateway.analyzeAllocationOffer.AllocationOfferInfoAsList;
import eu.optimis.ac.gateway.init_finish.Initialize;
import eu.optimis.ac.gateway.utils.HTMLtables;
import eu.optimis.ac.smanalyzer.SMsAnalyzer;
import java.util.ArrayList;
import org.apache.log4j.Logger;

public class AllocationOfferToACLogs
{
	public static void AllocationOfferToWeb(Initialize initialize,AllocationOfferInfoAsList allocationOfferList,
			SMsAnalyzer smAnalyzer, ArrayList<TRECvalues> ListOfTRECvalues,Logger log)
	{
		
		
		PrintAllocationOfferForEverySM printAllocationOffer = 
				new PrintAllocationOfferForEverySM(allocationOfferList,smAnalyzer.formParams,log);
		
		
                SaveExecution.ToFile(printAllocationOffer.Header_List, printAllocationOffer.Tag_List, printAllocationOffer.Info_List, log);
                
		ReadExecutions readExecutions = new ReadExecutions(printAllocationOffer.Tag_List,log);
                
                
		String allocationOfferTable = HTMLtables.CreateHTMLtable("1",printAllocationOffer.Position11,
                        readExecutions.Header_List,
                        printAllocationOffer.Tag_List,
                        readExecutions.Info_List);
		
		ContentToAClogsFile.WriteContent(allocationOfferTable,log);
                
                /*
		for(int i=0;i<allocationOfferList.Number_Of_Allocation_Offer;i++)
		{
			log.info("<--PrintAllocationPatternForOneSM : "+(i+1)+"/"+allocationOfferList.Number_Of_Allocation_Offer);
			
			PrintAllocationPatternForOneSM printAllocationPattern = 
					new PrintAllocationPatternForOneSM(allocationOfferList,smAnalyzer,i,log);
			
			log.info("<--PrintAllocationPatternForOneSM : "+(i+1)+"/"+allocationOfferList.Number_Of_Allocation_Offer);
			
			String allocationPatternTable = HTMLtables.CreateHTMLtable("1",printAllocationPattern.Position11,printAllocationPattern.Header_List,printAllocationPattern.Tag_List,printAllocationPattern.Info_List);
			
			String tableName="AllocationOffer";
			if(allocationOfferList.Number_Of_Allocation_Offer!=1)
				tableName+=" of service Manifest -"+Integer.toString(i+1)+"-";
			
			String msg=tableName+" :";
			msg="<h4>"+msg+"</h4>";
			
			ContentToAClogsFile.AppendContent(msg,log);
			
			ContentToAClogsFile.AppendContent(allocationPatternTable,log);
		}//for -i
                */
                
                DoublePrint doubleTable = new DoublePrint(initialize,printAllocationOffer.ServiceID_List,printAllocationOffer.timeStamp,ListOfTRECvalues,allocationOfferList,smAnalyzer,log);
                
                String Table = HTMLtables.CreateHTMLtable("1",doubleTable.Position11,doubleTable.Header_List,doubleTable.Tag_List,doubleTable.Info_List);
                
                SaveAllocationPatterns.ToFile("<br>"+Table, log);
                
		ReadAllocationPatterns readAllocationPatterns = new ReadAllocationPatterns(log);
                
                ContentToAClogsFile.AppendContent(readAllocationPatterns.allocationPatternTables,log);
                
	}//AllocationOfferToWeb()
	
}//class