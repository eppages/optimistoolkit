/*
 * Copyright (c) 2013 National Technical University of Athens (NTUA)
 *	
 *   							MIT License
 *   
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */


package eu.optimis.ac.test;

import eu.optimis.ac.test.solversComparison.GetSMsforSolversComparisonTest;
import eu.optimis.ac.test.solversComparison.getExecutionTimeFromLogs;
import java.util.ArrayList;
import junit.framework.TestCase;

public class SolversComparisonTest  extends TestCase{
    
    
    private static int NumberOfRuns = 5;
    
    private int executionTime_Gams;
    private int executionTime_Heuristic;
    
    private ArrayList<Integer> executionTime_Gams_List = new ArrayList<Integer>();
    private ArrayList<Integer> executionTime_Heuristic_List = new ArrayList<Integer>();
    
    public void testSolvers()
    {
        System.out.println("SolversComparisonTest Started");
       System.out.println("Whe are going to execute "+NumberOfRuns+" Runs");
       System.out.println("And calculate the average execution Time");
       System.out.println("GAMS Model vs Herusistic Solver");
       
       for(int i=1;i<=NumberOfRuns;i++)
       {
           System.out.println("Run : "+i+" started ->->->");
           
           SolversTest();
           
           executionTime_Gams_List.add(executionTime_Gams);
           executionTime_Heuristic_List.add(executionTime_Heuristic);
           
           System.out.println("Run : "+i+" finished <-<-<-");
       }//for
       
       int average_executionTime_Gams = getAverage(executionTime_Gams_List);
       int average_executionTime_Heuristic = getAverage(executionTime_Heuristic_List);
       
       System.out.println("Average Time of GAMS  in Milliseconds : "+average_executionTime_Gams);
       System.out.println("Average Time of Heuristic  in Milliseconds : "+average_executionTime_Heuristic);
       
       if(average_executionTime_Heuristic == 0)
        {
            average_executionTime_Heuristic = 1;
            System.out.println("if (average_executionTime_Heuristic == 0) average_executionTime_Heuristic = 1; "+average_executionTime_Heuristic);
        }//if
        
       
       int timesFaster = (average_executionTime_Gams/average_executionTime_Heuristic);
        
        System.out.println();System.out.println();System.out.println();
        System.out.println(" "+timesFaster+"    times faster the Heuristic Solver than GAMS");
        System.out.println("and must be at least 10");
        System.out.println();System.out.println();System.out.println();
       
       System.out.println("SolversComparisonTest Finished");
    }//testSolvers()
    
    private void SolversTest()
    {
        System.out.println();System.out.println();System.out.println();
        String whichSolver = "use_GAMS";
        
        System.out.println("Solver is : "+whichSolver);
        System.out.println();System.out.println();System.out.println();
        
        ACGatewayTest.doTest(GetSMsforSolversComparisonTest.getSMs(),whichSolver);
        executionTime_Gams = getExecutionTimeFromLogs.getExecutionTime();
        
        System.out.println();
        System.out.println("executionTime for Gams Solver in Milliseconds : "+executionTime_Gams);
        System.out.println();System.out.println();
        
        whichSolver = "use_HeuristicSolver_Python_243";
        
        System.out.println("Solver is : "+whichSolver);
        System.out.println();System.out.println();System.out.println();
        
        ACGatewayTest.doTest(GetSMsforSolversComparisonTest.getSMs(),whichSolver);
        executionTime_Heuristic = getExecutionTimeFromLogs.getExecutionTime();
        
        System.out.println();
        System.out.println("executionTime for Python Heuristic Solver in Milliseconds : "+executionTime_Heuristic);
        System.out.println();System.out.println();
        
    }//SolversTest()
    
    private int getAverage(ArrayList<Integer> list)
    {
        int average = 0;
        
        for( int i : list)
            average +=i;
        
        return average/list.size();
        
    }//getAverage()
}//class
