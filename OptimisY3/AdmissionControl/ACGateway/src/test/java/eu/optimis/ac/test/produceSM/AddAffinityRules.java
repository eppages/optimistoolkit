/*
 * Copyright (c) 2013 National Technical University of Athens (NTUA)
 *	
 *   							MIT License
 *   
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */


package eu.optimis.ac.test.produceSM;

import eu.optimis.manifest.api.sp.Manifest;
import eu.optimis.types.xmlbeans.servicemanifest.XmlBeanAffinityConstraintType;
import java.util.Map;

class AddAffinityRules {
    
    protected static Manifest addAffinityRules(Manifest manifest, String affinityRules, Map <Integer,String> componentsNames)
    {
        String temp1[] = affinityRules.split(" ");
        
        for(int i=0;i<temp1.length;i++)
        {
            
            String affinityRule = temp1[i];
            
            String temp2[] = affinityRule.split(",");
            
            String rule = temp2[temp2.length-1];
            
            if(rule.contains("High"))rule=XmlBeanAffinityConstraintType.HIGH.toString();
            else if(rule.contains("Medium"))rule=XmlBeanAffinityConstraintType.MEDIUM.toString();
            else if(rule.contains("Low"))rule=XmlBeanAffinityConstraintType.LOW.toString();
            else {System.err.println("AffinityRule not recognized");throw new RuntimeException("AffinityRule not recognized");}
            
            String components_Ids = componentsNames.get(Integer.parseInt(temp2[0]));
            
            
            for(int j=1;j<temp2.length-1;j++)
            {    
                components_Ids += " "+componentsNames.get(Integer.parseInt(temp2[j]));
                
            }//for-j
            
            if(i==0)
            {
                int previousLength = manifest.getVirtualMachineDescriptionSection().getAffinityRules().length;
			
                manifest.getVirtualMachineDescriptionSection().removeAffinityRule( previousLength-1 );
            }
            
            manifest.getVirtualMachineDescriptionSection().addNewAffinityRule( components_Ids.split(" "),
		            rule);
	    
        }//for-i
        
        return manifest;
        
    }//addAffinityRules()
    
    protected static Manifest addAntiAffinityRules(Manifest manifest, String antiAffinityRules, Map <Integer,String> componentsNames)
    {
        String temp1[] = antiAffinityRules.split(" ");
        
        for(int i=0;i<temp1.length;i++)
        {
            String antiAffinityRule = temp1[i];
            
            String temp2[] = antiAffinityRule.split(",");
            
            String rule = temp2[temp2.length-1];
            
            if(rule.contains("High"))rule=XmlBeanAffinityConstraintType.HIGH.toString();
            else if(rule.contains("Medium"))rule=XmlBeanAffinityConstraintType.MEDIUM.toString();
            else if(rule.contains("Low"))rule=XmlBeanAffinityConstraintType.LOW.toString();
            else {System.err.println("AffinityRule not recognized");throw new RuntimeException("AffinityRule not recognized");}
            
            String components_Ids = componentsNames.get(Integer.parseInt(temp2[0]));
            
            for(int j=1;j<temp2.length-1;j++)
            {    
                components_Ids += " "+componentsNames.get(Integer.parseInt(temp2[j]));
                
            }//for-j
            
            if(i==0)
            {
                int previousLength = manifest.getVirtualMachineDescriptionSection().getAntiAffinityRules().length;
			
                manifest.getVirtualMachineDescriptionSection().removeAntiAffinityRule( previousLength-1 );
            }
            
            manifest.getVirtualMachineDescriptionSection().addNewAntiAffinityRule( components_Ids.split(" "),
		            rule);
	    
        }//for-i
        
        return manifest;
        
    }//addAntiAffinityRules()
    
}//class
