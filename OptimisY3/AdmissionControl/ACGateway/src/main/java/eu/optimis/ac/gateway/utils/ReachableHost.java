/*
 * Copyright (c) 2013 National Technical University of Athens (NTUA)
 *	
 *   							MIT License
 *   
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */


package eu.optimis.ac.gateway.utils;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import org.apache.log4j.Logger;


public class ReachableHost {
    
    public static Boolean isReachable(String ipAddress, Logger log)
    {
        Boolean answer;
        InetAddress inet;
        
        try {
            inet = InetAddress.getByName(ipAddress);
            
            answer = inet.isReachable(5000);
            
            if(!answer)log.info("ipAddress : "+ipAddress+" unreachable");
            
            } catch (UnknownHostException ex) {
            log.info("ipAddress : "+ipAddress+" returned Message : "+ex.getMessage());
            return false;
            } catch (IOException ex) {
            log.info("ipAddress : "+ipAddress+" returned Message : "+ex.getMessage());
            return false;
        }
        
        return answer;
        
    }//isReachable()
    
    public static Boolean isReachable(String ipAddress)
    {
        Boolean answer;
        InetAddress inet;
        
        try {
            inet = InetAddress.getByName(ipAddress);
            
            answer = inet.isReachable(5000);
            
            
            
            } catch (UnknownHostException ex) {
            
            return false;
            } catch (IOException ex) {
            
            return false;
        }
        
        return answer;
        
    }//isReachable()
    
}//class
