/*
 * Copyright (c) 2013 National Technical University of Athens (NTUA)
 *	
 *   							MIT License
 *   
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */


package eu.optimis.ac.gateway.cleanup;

import eu.optimis.ac.gateway.configuration.BackupSMsPath;
import eu.optimis.ac.gateway.utils.FileFunctions;
import eu.optimis.ac.gateway.utils.Paths;
import org.apache.log4j.Logger;


public class InitializeACBackupSMs {
    
    public static String DeleteACBackupSMsFolder(Logger log)
    {
        log.info("InitializeACBackupSMs Started");
        
        String path_BackupSMs = BackupSMsPath.getBackupSMsPath(Paths.getStartPath(log), log);
        log.info("path_BackupSMs : "+path_BackupSMs);
        
        String str = FileFunctions.deleteDir(path_BackupSMs);
        
        log.info("InitializeACBackupSMs Finished");
        
        return "Deletion "+str;
    }//DeleteACBackupSMsFolder        
    
}//class
