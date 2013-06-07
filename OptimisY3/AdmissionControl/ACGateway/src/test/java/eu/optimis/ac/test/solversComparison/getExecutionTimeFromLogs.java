/*
 * Copyright (c) 2013 National Technical University of Athens (NTUA)
 *	
 *   							MIT License
 *   
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */


package eu.optimis.ac.test.solversComparison;

import eu.optimis.ac.ACRestClients.MyRestClients.RestClient_noInput_String;
import eu.optimis.ac.test.remoteTest.GetServerDetails;

public class getExecutionTimeFromLogs {
    
    
    private static String AdmissionControllerlastServerLogs = null;
    
    
    public static int getExecutionTime()
    {

        setAdmissionControllerLastServerLogs();
        
        String temp[] = AdmissionControllerlastServerLogs.split("\n");
        
        String startTime = null; String startDate = null;
        String finishTime = null; String finishDate = null;
        
        for(String str : temp)
        {
            if(str.contains("Executing"))
            {startTime = getTime(str);startDate = getDate(str);}
            if(str.contains("executed!"))
            {finishTime = getTime(str);finishDate = getDate(str);}
        }//for

        if(startDate.hashCode()!=finishDate.hashCode())
            throw new RuntimeException("StartDate != FinishDate : "+startDate+" "+finishDate);
        
        String start[] = startTime.split(",");
        String finish[] = finishTime.split(",");
        
        int startMillisec = Integer.parseInt(start[1]);
        int finishMillisec = Integer.parseInt(finish[1]);
        
        start = start[0].split(":");
        finish = finish[0].split(":");
        
        return ((Integer.parseInt(finish[0])*24+
                Integer.parseInt(finish[1]))*60+
                Integer.parseInt(finish[2]))*1000+finishMillisec
                -(((Integer.parseInt(start[0])*24+
                Integer.parseInt(start[1]))*60+
                Integer.parseInt(start[2]))*1000+startMillisec);
        
    }//getExecutionTime()
    
    private static String getDate(String str)
    {
        String temp[] = str.split(" ");
        
        return temp[0];
    }//getTime()
    
    private static String getTime(String str)
    {
        String temp[] = str.split(" ");
        
        return temp[1];
    }//getTime()
    
    private static void setAdmissionControllerLastServerLogs()
    {
        RestClient_noInput_String lastLogsClient = 
                new RestClient_noInput_String(
                		GetServerDetails.Host,GetServerDetails.Port,
                		"/AdmissionController/info/getLogs");
        
        AdmissionControllerlastServerLogs = lastLogsClient.returnedString;
        
    }//setAdmissionControllerLastServerLogs()
}//class
