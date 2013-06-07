/*
 * Copyright (c) 2013 National Technical University of Athens (NTUA)
 *	
 *   							MIT License
 *   
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package eu.optimis.ac.admissioncontroller.allocationOffer;

import eu.optimis.ac.admissioncontroller.configuration.GetGamsProperties;
import eu.optimis.ac.admissioncontroller.configuration.GetHeuristicSolverLogFilename;
import eu.optimis.ac.admissioncontroller.configuration.GetStartMessage;
import eu.optimis.ac.admissioncontroller.utils.FileFromResources;
import java.io.File;
import java.io.IOException;
import javax.ws.rs.core.MultivaluedMap;
import org.apache.log4j.Logger;

public class GetAllocationOffer {
    
    // String offer will hold the final offer to be returned 
    public String offer = "";
    
    public MultivaluedMap<String, String> outputParams;
    
    public GetAllocationOffer(MultivaluedMap<String, String> formParams,Logger log) {
    	
    	log.info(GetStartMessage.getAdmissionControllerStartMessage(log));
    	
    	log.trace("Entering getAdmissionControl");
        
        try {
            
            String GamsDirectory = formParams.get("opModel").get(0);
            String AllocationInfo = formParams.get("AllocationInfoPath").get(0);
            
            String IP_Id = formParams.get("IP_ID").get(0);
                 
            log.info("gamsDirectory : "+GamsDirectory);
            log.info("AllocationInfo : "+AllocationInfo);
            log.info("IP_Id : "+IP_Id);
            
            // file to place the offer result and 
            String outputFile = GamsDirectory+"result.csv";
            
            Process proc = null;
            
            if(formParams.containsKey("use_GAMS"))
            {
                proc = useGAMS(GamsDirectory, log);
                
                log.info("Solver is GAMS");
            }
            else if(formParams.containsKey("use_HeuristicSolver_Python_243"))
            {
                proc = useHeuristicSolver("HeuristicSolver_Python_243",GamsDirectory, outputFile, log);
                
                log.info("Solver is Heuristic Python_2.4.3");
            }
            else if(formParams.containsKey("use_Heuristic"))
            {
                proc = useHeuristicSolver("HeuristicSolver",GamsDirectory, outputFile, log);
                
                log.info("Solver is Heuristic");
            }
            else
            {
                log.error("No Solver Selected");
                throw new RuntimeException("No Solver Selected");
            }
            
            // any error message? 
            StreamGobbler errorGobbler = new StreamGobbler(proc.getErrorStream(), "ERROR");            
            
            // any output? 
            StreamGobbler outputGobbler = new StreamGobbler(proc.getInputStream(), "OUTPUT");
                
            // kick them off 
            errorGobbler.start();
            outputGobbler.start();

            // any error??? 
            int exitVal = proc.waitFor();
            
            
            log.info("Solver executed!");
            log.info("ExitValue: " + exitVal);
            
            // get output and create the relevant offer 
            // only if Solver exited normally
            // offer should be empty
             
            if (exitVal==0) {
                
                CreateAllocationOffer createAllocationOffer =
                        new CreateAllocationOffer(outputFile, 
                        GamsDirectory, AllocationInfo,
                IP_Id, log);
                
            	offer = createAllocationOffer.AllocationOffer;
                
                outputParams = createAllocationOffer.outputParams;
                
            }//if (exitVal==0)
            
        } catch (IOException ioe) {
                log.error("There was an IOException: ", ioe);
        	ioe.printStackTrace();
        } catch (InterruptedException ie) {
        	log.error("There was a ParserConfigurationException: ", ie);
                ie.printStackTrace();
        }//try
        
        // end of RESTful call /getAdmissionControl 
        log.info("Allocation Offer returned: " + offer);
        log.trace("Exiting getAdmissionControl");
        
    }//constructor
    
    
    private Process useGAMS(String GamsDirectory, Logger log) throws IOException
    {
            // String with command arguments to be used while calling GAMS 
            String[] cmd = new String[4];
            
            GetGamsProperties getGamsProperties = new GetGamsProperties( GamsDirectory, log);
            
            // prepare GAMS command to be later executed 
            // path for GAMS executable on server 
            
            cmd[0] = getGamsProperties.executable;
            log.info("cmd[0] = " + cmd[0]);
            
            // path for GAMS .gms model file on server 
            
            cmd[1] = new File(GamsDirectory + getGamsProperties.modelFileName).getCanonicalPath();
            log.info("cmd[1] = " + cmd[1]);
            
            // output mode used by GAMS 
            
            cmd[2] = "lo=" + getGamsProperties.outputMode;
            log.info("cmd[2] = " + cmd[2]);
            
            // explicit definition of GAMS current directory 
            
            cmd[3] = "curdir=" + GamsDirectory;
            log.info("cmd[3] = " + cmd[3]);
            
            log.info("Gams arguments assigned.");                     
            
            // Execute GAMS with its command line arguments 
            Runtime rt = Runtime.getRuntime();
            log.info("Executing " + cmd[0] + " " + cmd[1] + " " +
            		cmd[2] + " " + cmd[3]);
            Process proc = rt.exec(cmd);
            log.info("Gams start of execution!");
            
            return proc;
    }//useGAMS()
    
    private Process useHeuristicSolver(String HeuristicSolverPath,String GamsDirectory, String outputFile, Logger log) throws IOException
    {
        String[] cmd = new String[8];
        
        cmd[0] = "python";
        
        String heuristicPath = FileFromResources.getPath(HeuristicSolverPath);
        
        log.info("HeuristicSolver : "+heuristicPath);
        
        cmd[1] = heuristicPath+"admission.py";
        
        cmd[2] = "-o";
        
        cmd[3] = outputFile;
        
        cmd[4] = "-i";
        
        cmd[5] = GamsDirectory;
        
        cmd[6] = "-l";
        
        cmd[7] = GetHeuristicSolverLogFilename.getHeuristicSolverLogFilename(log);
        
        Runtime rt = Runtime.getRuntime();
        log.info("Executing " + cmd[0] + " " + cmd[1] + " " +
            		cmd[2] + " " + cmd[3]
                +" "+cmd[4] + " " + cmd[5]+" "+cmd[6] + " " + cmd[7]
                );
        Process proc = rt.exec(cmd);
        
        log.info("HeuristicSolver start of execution!");
        
        return proc;
    }//useHeuristicSolver()
    
}//class
