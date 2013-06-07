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

import eu.optimis.ac.gateway.acCsvInfo.GetTREC_weights;
import eu.optimis.ac.gateway.utils.AddOptimisPhoto;
import eu.optimis.ac.gateway.utils.HTMLtables;
import java.util.ArrayList;
import org.apache.log4j.Logger;

public class GetACweights { 
    
    public static String getHTMLweights(Logger log)
    {
                log.info("GetACweights Started");
                
		String html="";
		
		html+="<html>";
		
		html+="<head>";
		html+="<title>AClogs</title>";
		html+="</head>";
		html+="<body>";
		html+=AddOptimisPhoto.addPhoto().replace('^','"');
		html+=getBothWeightsAsHTMLtable(log);
		html+="</body>";
		html+="</html>";
		
                log.info("GetACweights Finished");
                
		return html;
                
    }//getHTMLweights()
    
    private static String getPosition11()
    {
        return "";
    }//getPosition11()
    
    private static ArrayList<String> getHeader_List()
    {
       ArrayList<String> Header_List = new ArrayList<String>(); 
       
       Header_List.add("*_weight");
       Header_List.add("*Temp_weight");
       
       return Header_List;
    }//getHeader_List()
    
    private static ArrayList<String> getTag_List()
    {
        ArrayList<String> Tag_List = new ArrayList<String>();
        
        Tag_List.add("Trust_weight");
        Tag_List.add("Risk_weight");
	Tag_List.add("Eco_weight");
        Tag_List.add("Cost_weight");
        
        return Tag_List;
    }//getTag_List()
    
    private static ArrayList<String> getWeights_List(Logger log)
    {
        ArrayList<String> Weights_List = new ArrayList<String>();
        
        Weights_List.add(GetTREC_weights.getTrust_Weight(log));
	Weights_List.add(GetTREC_weights.getRisk_Weight(log));
        Weights_List.add(GetTREC_weights.getEco_Weight(log));
        Weights_List.add(GetTREC_weights.getCost_Weight(log));
        
        return Weights_List;
    }//getWeights_List()
    
    
    private static ArrayList<String> getTempWeights_List(Logger log)
    {
        ArrayList<String> TempWeights_List = new ArrayList<String>();
        
        TempWeights_List.add(GetTREC_weights.getTempTrust_Weight(log));
	TempWeights_List.add(GetTREC_weights.getTempRisk_Weight(log));
        TempWeights_List.add(GetTREC_weights.getTempEco_Weight(log));
        TempWeights_List.add(GetTREC_weights.getTempCost_Weight(log));
        
        return TempWeights_List;
    }//getWeights_List()
    
    private static ArrayList<ArrayList<String>> getInfo(Logger log)
    {
        ArrayList<ArrayList<String>> Info_List = new ArrayList<ArrayList<String>>();
	
        ArrayList<String> Weights_List = getWeights_List(log);
	
        ArrayList<String> TempWeights_List = getTempWeights_List(log);
	
        Info_List.add(getWeights_List(log));
        Info_List.add(getTempWeights_List(log));
        
	return Info_List;	
    }//getInfo()
    
    public static String getBothWeightsAsHTMLtable(Logger log)
    {
        return HTMLtables.CreateHTMLtable("1",getPosition11(),getHeader_List(),getTag_List(),getInfo(log));
    }//getBothWeightsAsHTMLtable(Logger log)
    
}//class
