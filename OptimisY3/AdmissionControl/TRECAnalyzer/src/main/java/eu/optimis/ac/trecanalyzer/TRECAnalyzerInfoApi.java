/*
 * Copyright (c) 2013 National Technical University of Athens (NTUA)
 *	
 *   							MIT License
 *   
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package eu.optimis.ac.trecanalyzer;

import eu.optimis.ac.trecanalyzer.utils.FileFunctions;
import eu.optimis.ac.trecanalyzer.utils.GetFileNames;
import java.io.IOException;
import java.util.Properties;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import org.apache.log4j.Logger;

@Path("/info")
public class TRECAnalyzerInfoApi {
    
    private static Logger log = TRECAnalyzer.log;
	
    public TRECAnalyzerInfoApi()
    {
		
    }//constructor
        
        @GET
	@Path("/getLogs")
	@Produces("text/plain")
	public String getLogs()
	{
            
                Properties props = new Properties();
                try {  
                    props.load(TRECAnalyzerInfoApi.class.getClassLoader().getResourceAsStream("config.properties"));
                } catch (IOException ex) {
                    log.error(ex.getMessage());
                }

		return FileFunctions.
                        readFileLineByLineWithResetString(
                        GetFileNames.getTRECAnalyzerLogsFilename(log), 
                        props.getProperty("TRECAnalyzer.StartMessage"), 
                        "\n");
	}//getLogs()
	
}//class
