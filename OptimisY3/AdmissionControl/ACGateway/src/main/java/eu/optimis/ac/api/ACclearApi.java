/*
 * Copyright (c) 2013 National Technical University of Athens (NTUA)
 *	
 *   							MIT License
 *   
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */


package eu.optimis.ac.api;

import eu.optimis.ac.gateway.backupSM_FinalMessage.BackupFinalMessage;
import eu.optimis.ac.gateway.backupSM_FinalMessage.BackupSM_and_FinalMessage;
import eu.optimis.ac.gateway.cleanup.InitializeAC;
import eu.optimis.ac.gateway.cleanup.InitializeACBackupSMs;
import eu.optimis.ac.gateway.cleanup.InitializeACFinalMessage;
import eu.optimis.ac.gateway.cleanup.InitializeAdmissionControlLogsFolder;
import eu.optimis.ac.gateway.configuration.BackupSMsPath;
import eu.optimis.ac.gateway.init_finish.Initialize;
import eu.optimis.ac.gateway.utils.Paths;
import eu.optimis.ac.gateway.utils.PropertiesUtils;
import java.io.File;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import org.apache.log4j.Logger;

@Path("/clear")
public class ACclearApi {
    
    private static Logger log = ACModelApi.log;
	
    public ACclearApi()
    {
		
    }//constructor
    
        @GET
	@Path("/ACLogsDeletion")
	@Produces("text/plain")
	public String ACLogsDeletion()
	{
		return InitializeAdmissionControlLogsFolder.clearLogs();
		
	}//ACLogsDeletion()
    
	@GET
	@Path("/ACFinalMessage/clear")
	@Produces("text/plain")
	public String ACFinalMessageDeletion()
	{
		return InitializeACFinalMessage.DeleteACFinalMessageFile(log);
		
	}//ACFinalMessageDeletion()
	
        @GET
	@Path("/ACBackupSMs/clear")
	@Produces("text/plain")
	public String ACBackupSMsDeletion()
	{
		return InitializeACBackupSMs.DeleteACBackupSMsFolder(log);
		
	}//ACBackupSMsDeletion()
        
        @GET
	@Path("/ACcleanupScript")
	@Produces("text/plain")
	public String ACcleanupScript()
	{
		return InitializeAC.cleanupAC(log);
		
	}//ACcleanupScript()
        
        @POST
        @Path("/remoteBackup")
        @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public String remoteBackup(MultivaluedMap<String, String> Params)
	{	
                String Message =  Params.get("Message").get(0);
                String Input =  Params.get("Input").get(0);
                String doNotBackupSMflag = Params.get("doNotBackupSMflag").get(0);
                String ip_Id = Params.get("ip_Id").get(0);
                
                String startPath = Paths.getStartPath(log);
                String path_BackupSMs = BackupSMsPath.getBackupSMsPath(startPath, log)+ip_Id+File.separator;
                String path_FinalMessage = BackupSMsPath.getFinalMessagePath(startPath);
                
                Initialize.delete_Previous_last2(path_BackupSMs,log);
		BackupSM_and_FinalMessage.doBackup(Params, log, Message,Input,
                        path_BackupSMs, path_FinalMessage,Boolean.parseBoolean(doNotBackupSMflag));
                
                return "";
	}//remoteBackup()
        
        @POST
        @Path("/remoteBackupTREC")
        @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public String remoteBackupTREC(MultivaluedMap<String, String> Params)
	{	
                String ip_Id = Params.get("ip_Id").get(0);
                
                String startPath = Paths.getStartPath(log);
                String path = BackupSMsPath.getBackupSMsPath(startPath, log)+ip_Id+File.separator;
                
                for(int i=0;i<Params.get("Message").size();i++)
                {
                    String TRECfilename = Params.get("TRECfilename").get(i);    
                    if(TRECfilename.hashCode()=="".hashCode())
                    {
                        TRECfilename = PropertiesUtils.getBoundle("filename.BackupFinalMessage");
                        path = BackupSMsPath.getFinalMessagePath(startPath);
                    }//if-""
                
                    BackupFinalMessage.WriteFinalMessage(Params.get("Message").get(i),log,TRECfilename,path);
                    
                }//for
                
                return "";
	}//remoteBackupTREC()
}//class
