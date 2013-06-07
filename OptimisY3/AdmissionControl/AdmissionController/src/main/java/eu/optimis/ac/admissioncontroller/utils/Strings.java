/*
 * Copyright (c) 2013 National Technical University of Athens (NTUA)
 *	
 *   							MIT License
 *   
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package eu.optimis.ac.admissioncontroller.utils;

import org.apache.log4j.Logger;


public class Strings {
    
    public static int getIntIntexOfSubString(String str, String searchForString, Logger log)
    {
        
        int intIndex = str.indexOf(searchForString);
            
       if(intIndex == - 1){log.error(searchForString+" not found");}
       //else{log.info(searchForString+" found at index "+ intIndex);}

       return intIndex;
    }//getIntIntexOfSubString()
    
    public static int getIntIntexOfSubString(String str, String searchForString)
    {
        
        int intIndex = str.indexOf(searchForString);
            
       if(intIndex == - 1)return -1;// not found
       
       return intIndex;
    }//getIntIntexOfSubString()
    

    public static String selectLineInManyLinesString(String ManyLinesString , String searchForString)
    {
        String str = ManyLinesString;
         
         int beginIntIndex = Strings.getIntIntexOfSubString(str, searchForString);
         int endIntIndex = Strings.getIntIntexOfSubString(str.substring(beginIntIndex), "\n");
         str = str.substring(beginIntIndex, endIntIndex+beginIntIndex);   
         
         return str;
    }//selectLineInManyLinesString
}//class
