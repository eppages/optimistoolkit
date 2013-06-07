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

import com.sun.jersey.core.util.MultivaluedMapImpl;
import eu.optimis.ac.gateway.utils.FileFunctions;
import eu.optimis.ac.test.configurationTest.CheckLogsFromServer;
import eu.optimis.ac.test.configurationTest.OutsideWarConfigurationFileAtServer;
import eu.optimis.ac.test.configurationTest.SetOutsideWarConfiguration;
import eu.optimis.ac.test.remoteTest.GetServerDetails;
import javax.ws.rs.core.MultivaluedMap;
import junit.framework.TestCase;

public class OutsideWarConfigurationACTest extends TestCase {
    
    private int TestNumber = 0;
    
    public void testACconfiguration()
    {
        InsideWarConfigurationTest(++TestNumber);
        
        OutsideWarConfigurationTest(++TestNumber, "AllProperties",GetServerDetails.Host,GetServerDetails.Port,null);
        
        OutsideWarConfigurationTest(++TestNumber, "AllProperties",GetServerDetails.Host,GetServerDetails.Port,null);
        
        InsideWarConfigurationTest(++TestNumber);
        
        InsideWarConfigurationTest(++TestNumber);
        
        OutsideWarConfigurationTest(++TestNumber, "WithoutProperties",GetServerDetails.Host,GetServerDetails.Port,null);
        
        InsideWarConfigurationTest(++TestNumber);
        
        OutsideWarConfigurationTest(++TestNumber, "Solver",GetServerDetails.Host,GetServerDetails.Port,SetOutsideWarConfiguration.WhichSolver_GAMS);
        
    }//testACconfiguration()
    
    private void InsideWarConfigurationTest(int testNumber)
    {
        System.out.println(testNumber+"==>InsideWarConfigurationTest Started : ");
        
        OutsideWarConfigurationFileAtServer.RemoveOutsideWarConfigurationFilesFromServer(null,
                GetServerDetails.Host,GetServerDetails.Port);
        
        MultivaluedMap<String, String> formParams = new MultivaluedMapImpl();
        
        doTest(formParams);
        
    }//InsideWarConfiguration()
    
    private void OutsideWarConfigurationTest(int testNumber, String chooseTest,String host,String port,String solver)
    {
        System.out.println(testNumber+"==>outsideWarConfigurationTest Started : "); 
        
        MultivaluedMap<String, String> formParams = null;
        
        if(chooseTest.contains("AllProperties"))
            formParams = SetOutsideWarConfiguration.SetOutsideWarConfigurationFile();
        else if(chooseTest.contains("WithoutProperties"))
            formParams = SetOutsideWarConfiguration.SetOutsideWarConfigurationFileWithoutProperties();
        
        Boolean removePreviousConfiguration = true;
        
        if(chooseTest.contains("Solver"))
        {
            formParams = SetOutsideWarConfiguration.SetSolver(solver);
            removePreviousConfiguration = false;
        }
        
        OutsideWarConfigurationFileAtServer.SetOutsideWarConfigurationFileAtServer(formParams,
                host,port,removePreviousConfiguration);
        
        doTest(formParams);
        
    }//OutsideWarConfiguration()
    
    private void doTest(MultivaluedMap<String, String> formParams)
    {
        
        All_ACGatewayRemoteTest.doTest(FileFunctions.readFileAsStringFromResources(ACdecisionTest.fileNameForAcceptance),GetServerDetails.Host,"");
        
        new CheckLogsFromServer(formParams);
        
    }//doTest()
    
    
}//class
