/*
 * Copyright (c) 2013 National Technical University of Athens (NTUA)
 *	
 *   							MIT License
 *   
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package eu.optimis.ac.gateway.backupSM_FinalMessage;

import eu.optimis.ac.gateway.utils.PropertiesUtils;
import javax.ws.rs.core.MultivaluedMap;
import org.apache.log4j.Logger;

public class BackupSM_and_FinalMessage {
	
	public static void doBackup(MultivaluedMap<String, String> Params,Logger log,String Message,String extraName,String path_BackupSMs, String path_BackupFinalMessage,Boolean doNotBackupSMflag)
	{	
		String filename_BackupFinalMessage = PropertiesUtils.getBoundle("filename.BackupFinalMessage");
		
		log.info("path_BackupSMs :"+path_BackupSMs);
		
                BackupFinalMessage.WriteFinalMessage(Message,log,filename_BackupFinalMessage,path_BackupFinalMessage);
                
		new BackupSMs(Params,extraName,path_BackupSMs,log, doNotBackupSMflag);
		
	}//doBackup()
	
}//class
