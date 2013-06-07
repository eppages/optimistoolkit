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

import eu.optimis.ac.gateway.acCsvInfo.GetTREC_constraints;
import eu.optimis.ac.gateway.utils.AddOptimisPhoto;
import eu.optimis.ac.gateway.utils.HTMLtables;
import java.util.ArrayList;
import org.apache.log4j.Logger;

public class GetACtrecConstraints { 
    
    public static String getHTMLtrecConstraints(Logger log)
    {
                log.info("GetACtrecConstraints Started");
                
		String html="";
		
		html+="<html>";
		
		html+="<head>";
		html+="<title>AClogs</title>";
		html+="</head>";
		html+="<body>";
		html+=AddOptimisPhoto.addPhoto().replace('^','"');
		html+=getBothConstraintsAsHTMLtable(log);
		html+="</body>";
		html+="</html>";
		
                log.info("GetACtrecConstraints Finished");
                
		return html;
                
    }//getHTMLtrecConstraints()
    
    private static String getPosition11()
    {
        return "";
    }//getPosition11()
    
    private static ArrayList<String> getHeader_List()
    {
       ArrayList<String> Header_List = new ArrayList<String>(); 
       
       Header_List.add("*_constraint");
       Header_List.add("*Temp_constraint");
       
       return Header_List;
    }//getHeader_List()
    
    private static ArrayList<String> getTag_List()
    {
        ArrayList<String> Tag_List = new ArrayList<String>();
        
        Tag_List.add("Trust_constraint");
        Tag_List.add("Risk_constraint");
	Tag_List.add("Eco_constraint");
        Tag_List.add("Cost_constraint");
        
        return Tag_List;
    }//getTag_List()
    
    private static ArrayList<String> getConstraints_List(Logger log)
    {
        ArrayList<String> Weights_List = new ArrayList<String>();
        
        Weights_List.add(GetTREC_constraints.getTrust_Constraint(log));
	Weights_List.add(GetTREC_constraints.getRisk_Constraint(log));
        Weights_List.add(GetTREC_constraints.getEco_Constraint(log));
        Weights_List.add(GetTREC_constraints.getCost_Constraint(log));
        
        return Weights_List;
    }//getConstraints_List()
    
    
    private static ArrayList<String> getTempConstraints_List(Logger log)
    {
        ArrayList<String> TempWeights_List = new ArrayList<String>();
        
        TempWeights_List.add(GetTREC_constraints.getTempTrust_Constraint(log));
	TempWeights_List.add(GetTREC_constraints.getTempRisk_Constraint(log));
        TempWeights_List.add(GetTREC_constraints.getTempEco_Constraint(log));
        TempWeights_List.add(GetTREC_constraints.getTempCost_Constraint(log));
        
        return TempWeights_List;
    }//getConstraints_List()
    
    private static ArrayList<ArrayList<String>> getInfo(Logger log)
    {
        ArrayList<ArrayList<String>> Info_List = new ArrayList<ArrayList<String>>();
	
        ArrayList<String> Weights_List = getConstraints_List(log);
	
        ArrayList<String> TempWeights_List = getTempConstraints_List(log);
	
        Info_List.add(getConstraints_List(log));
        Info_List.add(getTempConstraints_List(log));
        
	return Info_List;	
    }//getInfo()
    
    public static String getBothConstraintsAsHTMLtable(Logger log)
    {
        return HTMLtables.CreateHTMLtable("1",getPosition11(),getHeader_List(),getTag_List(),getInfo(log));
    }//getBothConstraintsAsHTMLtable(Logger log)
    
}//class
