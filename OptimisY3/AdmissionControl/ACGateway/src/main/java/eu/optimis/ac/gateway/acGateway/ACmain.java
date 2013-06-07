/*
 * Copyright (c) 2013 National Technical University of Athens (NTUA)
 *	
 *   							MIT License
 *   
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package eu.optimis.ac.gateway.acGateway;

import com.sun.jersey.core.util.MultivaluedMapImpl;
import eu.optimis.ac.ACRestClients.ACinternalMultiClient;
import eu.optimis.ac.ACRestClients.MyRestClients.RestClient_MultivaluedMap_String;
import eu.optimis.ac.gateway.acGatewayGothersExternalData.ACG_GothersExternalData;
import eu.optimis.ac.gateway.acLogsToWeb.AllocationOfferToACLogs;
import eu.optimis.ac.gateway.allocationOffer.CompineServiceManifestAllocationOfferInfo;
import eu.optimis.ac.gateway.allocationOffer.TRECvalues;
import eu.optimis.ac.gateway.analyzeAllocationOffer.AllocationOfferInfoAsList;
import eu.optimis.ac.gateway.analyzeAllocationOffer.printAllocationOfferInfo;
import eu.optimis.ac.gateway.backupSM_FinalMessage.BackupFinalMessage;
import eu.optimis.ac.gateway.backupSM_FinalMessage.BackupSM_and_FinalMessage;
import eu.optimis.ac.gateway.backupSM_FinalMessage.BackupSMs;
import eu.optimis.ac.gateway.init_finish.Finishing;
import eu.optimis.ac.gateway.init_finish.Initialize;
import eu.optimis.ac.gateway.utils.ReachableHost;
import eu.optimis.ac.gateway.utils.ReachableTomcat;
import eu.optimis.ac.smanalyzer.SMsAnalyzer;
import java.util.ArrayList;
import javax.ws.rs.core.MultivaluedMap;
import org.apache.log4j.Logger;

public class ACmain {
        
        private Initialize initialize; 
        
	private SMsAnalyzer smAnalyzer;
	
        private String ListOfServicesIDs = "";
        
        private ACG_GothersExternalData gotherExternalData;
        
	private AllocationOfferInfoAsList allocationOfferInfoList;
	
	private printAllocationOfferInfo pInfo;
	
	private CompineServiceManifestAllocationOfferInfo acResult;
	
        private MultivaluedMap<String, String> modelParams = new MultivaluedMapImpl();
                
	private String the_AllocationOffers = null;
	
        private String AllocationDetails = null;
        
        private ArrayList<TRECvalues> ListOfTRECvalues = new ArrayList<TRECvalues>();
        
        private MultivaluedMap<String, String> inputParams = new MultivaluedMapImpl(); 
        
	public MultivaluedMap<String, String> ReturnsParams; 
	
	public ACmain(MultivaluedMap<String, String> Params,Logger log)
        {

                acMain(Params,log);
        }//constructor

        
        public synchronized void acMain(MultivaluedMap<String, String> Params,Logger log)
        {
		initialize(Params,log);
		
		analyzeSM(Params,log);
		
		acGatewayGothersExternalData(smAnalyzer.formParams,Params,initialize,log);
		
		createModelInput(Params,log);
		
		callAdmissionController(Params,log);
		
		analyzeAllocationOffer(Params,log);
		
		compineServiceManifestAllocationOfferInfo(Params,log);
		
		finishing(log);
		
	}//constructor
	
	private void finishing(Logger log ) {
		
		new Finishing(gotherExternalData, pInfo, acResult, log, initialize,ListOfServicesIDs);
		
                ReturnsParams = acResult.ReturnedServiceManifests;
                ReturnsParams.add("AllocationDetails", AllocationDetails);
                
	}//finishing()

	private void compineServiceManifestAllocationOfferInfo(MultivaluedMap<String, String> Params,Logger log) {
		
		acResult = new CompineServiceManifestAllocationOfferInfo(
						smAnalyzer,
						allocationOfferInfoList.AllocationOfferList,
                                                AllocationDetails,log,Params);	
		
	}//compineServiceManifestAllocationOfferInfo()

	private void analyzeAllocationOffer(MultivaluedMap<String, String> Params,Logger log) {
		
		allocationOfferInfoList = 
        		new AllocationOfferInfoAsList(the_AllocationOffers);
		
                log.info(allocationOfferInfoList.Number_Of_Allocation_Offer);
                
		pInfo = new printAllocationOfferInfo(
						allocationOfferInfoList,smAnalyzer.formParams,log);
		
		AllocationOfferToACLogs.AllocationOfferToWeb(initialize,allocationOfferInfoList,smAnalyzer,ListOfTRECvalues,log);
		
	}//analyzeAllocationOffer()

	private void callAdmissionController(MultivaluedMap<String, String> Params,Logger log) {
		
		log.info("Call Admission Controller");
		
		modelParams.add("IP_ID", initialize.ip_Id);
                
                if(initialize.getWhichSolver.contains("HeuristicSolver_Python_243"))
                    modelParams.add("use_HeuristicSolver_Python_243", "");
                else if(initialize.getWhichSolver.contains("Heuristic"))
                    modelParams.add("use_Heuristic", "");
                else if(initialize.getWhichSolver.contains("GAMS"))       
                    modelParams.add("use_GAMS", "");
                
                ACinternalMultiClient acInternalMultiClient = new ACinternalMultiClient(initialize.AdmissionControllerIP,initialize.AdmissionControllerPort,"/AdmissionController/admission", modelParams,log);
                
                the_AllocationOffers = acInternalMultiClient.returnedMap.get("AllocationOffer").get(0);
                AllocationDetails = acInternalMultiClient.returnedMap.get("AllocationDetails").get(0);
                
                String backupTrust="",backupRisk="",backupEco="",backupCost="";
                for(int i=0;i<Params.get("serviceManifest").size();i++)
                {
                    //String trustValue = acInternalMultiClient.returnedMap.get("TRUST_FOR_NewService").get(i);
                    //String riskValue = acInternalMultiClient.returnedMap.get("PROBABILITY_FOR_ServiceFail").get(i);
                    //String ecoValue = acInternalMultiClient.returnedMap.get("ECO_FOR_NewService").get(i);
                    //String costValue = acInternalMultiClient.returnedMap.get("COST_FOR_HostingService").get(i);
                    
                    String trustValue = inputParams.get("trust").get(i);
                    String riskValue = inputParams.get("risk").get(i);
                    String ecoValue = inputParams.get("eco").get(i);
                    String costValue = acInternalMultiClient.returnedMap.get("COST_FOR_HostingService").get(i);
                    riskValue = riskValue.substring(riskValue.lastIndexOf("pof=\""), riskValue.lastIndexOf("\">")).substring(5);
                    
                    backupTrust+=" Trust"+(i+1)+"="+trustValue;backupRisk+=" Risk"+(i+1)+"="+riskValue;
                    backupEco+=" Eco"+(i+1)+"="+ecoValue;backupCost+=" Cost"+(i+1)+"="+costValue;
                    
                    log.info((i+1)+"-trustValue:"+trustValue);
                    log.info((i+1)+"-riskValue:"+riskValue);
                    log.info((i+1)+"-ecoValue:"+ecoValue);
                    log.info((i+1)+"-costValue:"+costValue);
                
                    TRECvalues TREC_values = new TRECvalues(trustValue,riskValue,ecoValue,costValue);
                    
                    ListOfTRECvalues.add(TREC_values);
                }//for-i, each Service Manifest
                
                
                String backupRiskHost="",backupEcoHost="";
                for(int i=0;i<inputParams.get("HostName").size();i++)
                {
                    backupRiskHost+=" Risk("+inputParams.get("HostName").get(i)+")="+inputParams.get("riskHost").get(i);
                    backupEcoHost+=" Eco("+inputParams.get("HostName").get(i)+")="+inputParams.get("ecoHost").get(i);
                }//for-i
                
                if((initialize.useRemoteBackup)&&(ReachableHost.isReachable(initialize.RemoteBackupHost, log))&&(ReachableTomcat.isReachable(initialize.RemoteBackupHost, initialize.RemoteBackupPort, log)))
                {
                    MultivaluedMap<String, String> params = new MultivaluedMapImpl();
                    
                    params.add("Message", backupTrust);
                    params.add("Message", backupRisk);
                    params.add("Message", backupEco);
                    params.add("Message", backupCost);
                    params.add("Message", backupRiskHost);
                    params.add("Message", backupEcoHost);
                    params.add("TRECfilename", "trustBackup.txt");
                    params.add("TRECfilename", "riskBackup.txt");
                    params.add("TRECfilename", "ecoBackup.txt");
                    params.add("TRECfilename", "costBackup.txt");
                    params.add("TRECfilename", "riskHostBackup.txt");
                    params.add("TRECfilename", "ecoHostBackup.txt");
                    params.add("ip_Id", initialize.ip_Id);
                    
                    
                    RestClient_MultivaluedMap_String client = new RestClient_MultivaluedMap_String
                            (initialize.RemoteBackupHost,initialize.RemoteBackupPort,"/ACGateway/clear/remoteBackupTREC",params,false,log);
                    
                    log.info("Remote Backup Status : "+client.status);
                }//if-RemoteBackup
                else
                {
                    BackupFinalMessage.WriteFinalMessage(backupTrust,log,"trustBackup.txt",initialize.path_BackupSMs);
                    BackupFinalMessage.WriteFinalMessage(backupRisk,log,"riskBackup.txt",initialize.path_BackupSMs);
                    BackupFinalMessage.WriteFinalMessage(backupEco,log,"ecoBackup.txt",initialize.path_BackupSMs);
                    BackupFinalMessage.WriteFinalMessage(backupCost,log,"costBackup.txt",initialize.path_BackupSMs);
                
                    BackupFinalMessage.WriteFinalMessage(backupRiskHost,log,"riskHostBackup.txt",initialize.path_BackupSMs);
                    BackupFinalMessage.WriteFinalMessage(backupEcoHost,log,"ecoHostBackup.txt",initialize.path_BackupSMs);
                }//else
                
                log.info(AllocationDetails);
                
                log.info(the_AllocationOffers);
                
	}//callAdmissionController()
	
	private void createModelInput(MultivaluedMap<String, String> Params,Logger log) {
		
		log.info("calling create model");
		
                if(Params.containsKey("isFederationAllowed"))
                {
                    for(int i=0;i<Params.get("isFederationAllowed").size();i++)
                        inputParams.add("isFederationAllowed", Params.get("isFederationAllowed").get(i));
                }//if
                
                inputParams.add("ModelPath", initialize.gamsPath);
                inputParams.add("AllocationPath", initialize.allocationPath);
                
                if(Params.containsKey("use_GAMS")) 
                    inputParams.add("cleanTestbed", "use_GAMS");
                else if(Params.containsKey("use_Heuristic")) 
                    inputParams.add("cleanTestbed", "use_Heuristic");
                else if(Params.containsKey("use_HeuristicSolver_Python_243")) 
                    inputParams.add("cleanTestbed", "use_HeuristicSolver_Python_243");
                
                if(Params.containsKey("cleanTestbed")) 
                    inputParams.add("cleanTestbed", "");
                
                if(Params.containsKey("changePolicy")) 
                    inputParams.add("changePolicy", Params.get("changePolicy").get(0));
                
                ACinternalMultiClient acInternalMultiClient = new ACinternalMultiClient(initialize.AdmissionControllerIP,initialize.AdmissionControllerPort,"/TRECAnalyzer/createModel", inputParams,log);
                
		String modelPath = acInternalMultiClient.returnedMap.get("opModel").get(0);
                String allocationPath = acInternalMultiClient.returnedMap.get("AllocationInfoPath").get(0);
                
                modelParams = acInternalMultiClient.returnedMap;
                
		log.info("model created by the AC:" +modelPath);
		log.info("allocationPath:" +allocationPath);
                
                if(acInternalMultiClient.returnedMap.containsKey("Exception"))
                {
                    String msg = acInternalMultiClient.returnedMap.get("Exception").get(0);
                    log.error(msg);
                    throw new RuntimeException(msg);
                }//if-Exception
                
                if(!initialize.gamsPath.equals(modelPath))
                {
                    log.error("gamsPath not equals to modelPath");
                    log.error("gamsPath :"+initialize.gamsPath);
                    log.error("modelPath :"+modelPath);
                    throw new RuntimeException("modelPath not equals to gamsPath");
                }//if-modelPath
                
                if(!initialize.allocationPath.equals(allocationPath))
                {
                    log.error("allocationPath not equals to allocationPath");
                    log.error("allocationPath :"+initialize.allocationPath);
                    log.error("allocationPath :"+allocationPath);
                    throw new RuntimeException("allocationPath not equals to allocationPath");
                }//if-allocationPath
                
	}//createModelInput()

	private void acGatewayGothersExternalData(MultivaluedMap<String, String> formParams,
                MultivaluedMap<String, String> Params,Initialize initialize,Logger log) {
		
            gotherExternalData = 
                    new ACG_GothersExternalData(formParams,Params,initialize ,log);
            
            inputParams = gotherExternalData.formParams;
		
	}//acGatewayGothersExternalData()

	private void analyzeSM(MultivaluedMap<String, String> Params,Logger log) {
		
                Boolean DisplayAllLogs = true;
                
                if(initialize.loggingLevel>1)
                DisplayAllLogs = false;
                
		smAnalyzer = new SMsAnalyzer(Params,log,DisplayAllLogs);
		
                for(int i=0;i<smAnalyzer.formParams.get("serviceId").size();i++)
                {
                    String serviceId = smAnalyzer.formParams.get("serviceId").get(i);
                    
                    if(i==0)
                        ListOfServicesIDs = serviceId;
                    else
                        ListOfServicesIDs += ","+serviceId;
                }//for-i
                
                
	}//analyzeSM()

	private void initialize(MultivaluedMap<String, String> Params,Logger log) {
		
		initialize = new Initialize(Params,log);
		
	}//initialize()
	
}//class
