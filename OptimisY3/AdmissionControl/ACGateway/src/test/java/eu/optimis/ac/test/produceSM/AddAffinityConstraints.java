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
import java.util.ArrayList;

class AddAffinityConstraints {
    
    protected static Manifest addAffinityConstraints(Manifest manifest, int i, ArrayList<Integer> affinityConstraints_List)
    {
                if((affinityConstraints_List.isEmpty())||(affinityConstraints_List.get(i-1)==0))
                        {
                            manifest.getVirtualMachineDescriptionSection().getVirtualMachineComponentArray(i-1)
                                .setAffinityConstraints(XmlBeanAffinityConstraintType.LOW.toString());
                        }
                        else if(affinityConstraints_List.get(i-1)==1)
                        {
                            manifest.getVirtualMachineDescriptionSection().getVirtualMachineComponentArray(i-1)
                                .setAffinityConstraints(XmlBeanAffinityConstraintType.MEDIUM.toString());
                        }
                        else if(affinityConstraints_List.get(i-1)==2)
                        {
                            manifest.getVirtualMachineDescriptionSection().getVirtualMachineComponentArray(i-1)
                                .setAffinityConstraints(XmlBeanAffinityConstraintType.HIGH.toString());
                        }
                        else if(affinityConstraints_List.get(i-1)>2)
                        {
                            String msg = "affininityConstaints number must be 0,1,2";
                            System.err.println(msg);
                            throw new RuntimeException(msg);
                        }//if-affinintyConstraintaints number
        
         return manifest;
    }//addAffinityConstraints()
    
    protected static Manifest addAntiAffinityConstraints(Manifest manifest, int i, ArrayList<Integer> antiAffinityConstraints_List)
    {
                        if((antiAffinityConstraints_List.isEmpty())||(antiAffinityConstraints_List.get(i-1)==0))
                        {
                            manifest.getVirtualMachineDescriptionSection().getVirtualMachineComponentArray(i-1)
                                .setAntiAffinityConstraints(XmlBeanAffinityConstraintType.LOW.toString());
                        }
                        else if(antiAffinityConstraints_List.get(i-1)==1)
                        {
                            manifest.getVirtualMachineDescriptionSection().getVirtualMachineComponentArray(i-1)
                                .setAntiAffinityConstraints(XmlBeanAffinityConstraintType.MEDIUM.toString());
                        }
                        else if(antiAffinityConstraints_List.get(i-1)==2)
                        {
                            manifest.getVirtualMachineDescriptionSection().getVirtualMachineComponentArray(i-1)
                                .setAntiAffinityConstraints(XmlBeanAffinityConstraintType.HIGH.toString());
                        }
                        else if(antiAffinityConstraints_List.get(i-1)>2)
                        {
                            String msg = "antiAffininityConstaints number must be 0,1,2";
                            System.err.println(msg);
                            throw new RuntimeException(msg);
                        }//if-antiAffinintyConstraintaints number
        
         return manifest;
    }//addAntiAffinityConstraints()
    
    protected static void validateInput(ArrayList<Integer> affinityConstraints_List, ArrayList<Integer> antiAffinityConstraints_List,
            int numberOfComponents)
    {
        if((!affinityConstraints_List.isEmpty())&&(affinityConstraints_List.size()!=numberOfComponents))
            {
                String msg = "numberOfComponents != affinityConstraints_List.size()";
                System.err.println(msg);
                throw new RuntimeException(msg);
            }//if-affinintyConstraintaints.size
            
            if((!antiAffinityConstraints_List.isEmpty())&&(antiAffinityConstraints_List.size()!=numberOfComponents))
            {
                String msg = "numberOfComponents != antiAffinityConstraints_List.size()";
                System.err.println(msg);
                throw new RuntimeException(msg);
            }//if-antiAffinintyConstraintaints.size
    }//validateInput()
    
}//class
