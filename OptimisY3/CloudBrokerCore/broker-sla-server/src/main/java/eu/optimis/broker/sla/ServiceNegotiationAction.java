/* 
 * Copyright (c) 2007, Fraunhofer-Gesellschaft
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 * 
 * (1) Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the disclaimer at the end.
 *     Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in
 *     the documentation and/or other materials provided with the
 *     distribution.
 * 
 * (2) Neither the name of Fraunhofer nor the names of its
 *     contributors may be used to endorse or promote products derived
 *     from this software without specific prior written permission.
 * 
 * DISCLAIMER
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *  
 */
package eu.optimis.broker.sla;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

import org.apache.log4j.Logger;
import org.ogf.graap.wsag.api.Quote;
import org.ogf.graap.wsag.api.exceptions.NegotiationException;
import org.ogf.graap.wsag.server.actions.AbstractNegotiationAction;
import org.ogf.schemas.graap.wsAgreement.AgreementTemplateType;
import org.ogf.schemas.graap.wsAgreement.ServiceDescriptionTermType;
import org.ogf.schemas.graap.wsAgreement.negotiation.NegotiationOfferStateType;
import org.ogf.schemas.graap.wsAgreement.negotiation.NegotiationOfferType;

import eu.optimis.ipdiscovery.datamodel.Provider;
import eu.optimis.cbr.client.CBRClient;
import java.util.List;
import eu.optimis.cbr.client.utils.IPInfoList;
import eu.optimis.cbr.monitoring.clientlib.Actions;
import eu.optimis.cbr.monitoring.clientlib.BrokerVisualMonitor;
import eu.optimis.cbr.monitoring.clientlib.EndPointType;
import eu.optimis.cbr.monitoring.clientlib.IllegalCallParameter;
import eu.optimis.cbr.monitoring.clientlib.StatusCode;
import eu.optimis.ds.client.DeploymentServiceClient;
import eu.optimis.broker.client.DMDataSynchClient;
import eu.optimis.broker.core.DataUploader;
import eu.optimis.broker.core.OutputStub4Demo;
import eu.optimis.broker.deploymentObjectiveTypes.DeploymentObjectiveType;

import eu.optimis.manifest.api.ip.Manifest;
import eu.optimis.sla.types.service.price.SLAServicePriceDocument;
import eu.optimis.sla.types.service.price.SLAServicePriceType;
import eu.optimis.types.xmlbeans.servicemanifest.XmlBeanServiceManifestDocument;

/**
 * This class is the default negotiation implementation for the OPTIMIS-VM template.
 * 
 * @todo: add description on negotiable parameters
 * 
 * @author hrasheed
 * 
 */
public class ServiceNegotiationAction extends AbstractNegotiationAction
{

    private static final Logger LOG = Logger.getLogger( ServiceNegotiationAction.class );
    
    /**
     * Unsupported method. This method is deprecated.
     * 
     * @see org.ogf.graap.wsag.server.actions.INegotiationAction#negotiate(org.ogf.graap.wsag.api.Quote)
     */
    @Override
    public AgreementTemplateType[] negotiate( Quote quote ) throws NegotiationException
    {
        throw new UnsupportedOperationException( "This method is not supported by " + getClass().getName() );
    }

    /**
     * Negotiates an agreement offer based on the incoming quote.
     * 
     * @see org.ogf.graap.wsag.server.actions.INegotiationAction#negotiate(org.ogf.schemas.graap.wsAgreement.negotiation.NegotiationOfferType,
     *      java.util.Map)
     */
    @Override
    public NegotiationOfferType[] negotiate( NegotiationOfferType negotiationOffer, Map context )
        throws NegotiationException
    {

		OutputStub4Demo.write("\nNegotiationOfferType\n");
    	
    	
		try {

			BrokerVisualMonitor.APICall(Actions.SD_BFE_1, EndPointType.START, StatusCode.OK, "status-message");
			BrokerVisualMonitor.APICall(Actions.SD_BFE_1, EndPointType.END, StatusCode.OK, "status-message");
		} catch (IllegalCallParameter e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

        //
        // simply grep the service manifest document provided in the negotiation offer
        //
        Manifest negotiationManifest = null;

        try
        {
            XmlBeanServiceManifestDocument serviceManifestDoc =
                Tools.negotiationOfferToServiceManifest( negotiationOffer );

            negotiationManifest = Manifest.Factory.newInstance( serviceManifestDoc );

            if ( LOG.isTraceEnabled() )
            {
                //LOG.trace( "negotiation offer manifest: " + negotiationManifest.toXmlBeanObject().xmlText() );
            }
        }
        catch ( Exception e )
        {
            throw new NegotiationException( "Error in retrieving service manifest from negotiation offer.", e );
        }
        
        String deploymentObjective;
        try
        {
        	DeploymentObjectiveType objectiveType = Tools.getDeploymentObjective( negotiationOffer.getTerms().getAll() );

        	deploymentObjective = objectiveType.getStringValue();

            if ( LOG.isInfoEnabled() )
            {
                LOG.info( "negotiation deployment objective: " + objectiveType.getStringValue() );
            }
        }
        catch ( Exception e )
        {
            throw new NegotiationException( "Error in retrieving deployment objective from negotiation offer.", e );
        }
        
        HashMap<String, Provider> deploymentSolutions = null;
        
        try 
        {
        	
            String brokerHost = ComponentConfigurationProvider.getString( "broker.host" ); //$NON-NLS-1$
            
            String brokerPort = ComponentConfigurationProvider.getString( "broker.port" ); //$NON-NLS-1$
            
            String trecHost = ComponentConfigurationProvider.getString( "broker.trec.host" ); //$NON-NLS-1$
            
            String trecPort = ComponentConfigurationProvider.getString( "broker.trec.port" ); //$NON-NLS-1$
            
            String doHost = ComponentConfigurationProvider.getString( "broker.do.host" ); //$NON-NLS-1$
            
            String doPort = ComponentConfigurationProvider.getString( "broker.do.port" ); //$NON-NLS-1$
            

            String monitoringURL = ComponentConfigurationProvider.getString( "VMServiceInstantiation.MonitoringInterval" ); //$NON-NLS-1$
            
            String acURL = ComponentConfigurationProvider.getString( "VMServiceInstantiation.url.ac" ); //$NON-NLS-1$
            
            String coURL = ComponentConfigurationProvider.getString( "VMServiceInstantiation.url.co" ); //$NON-NLS-1$

            
            
            LOG.info("using monitor: " + monitoringURL);
            LOG.info("using AC: " + acURL);
            LOG.info("using CO: " + coURL);
            
            LOG.info("using broker: " + brokerHost + " - " + brokerPort);
            LOG.info("using trec: " + trecHost + " - " + trecPort);
            LOG.info("using DO: " + doHost + " - " + doPort);
            
            CBRClient cbrclient = new CBRClient(brokerHost,brokerPort);
       
            
            BrokerVisualMonitor.APICall(Actions.BFE_REG_2, EndPointType.START, StatusCode.OK, "status-message");
			BrokerVisualMonitor.APICall(Actions.BFE_REG_2, EndPointType.END, StatusCode.OK, "status-message");

    		OutputStub4Demo.write("\nCalling Broker IPRegistry \n");
            IPInfoList iplist = cbrclient.getAllIP();
    		OutputStub4Demo.write("\nReceived IPRegistry all IPs\n");
            BrokerVisualMonitor.APICall(Actions.REG_BFE_3, EndPointType.START, StatusCode.OK, "status-message");
            BrokerVisualMonitor.APICall(Actions.REG_BFE_3, EndPointType.END, StatusCode.OK, "status-message");

     
            List<Provider> IPList = iplist.getIPList();
            
            for (int i = 0; i < IPList.size(); i++) 
            {
            	LOG.info( "IP registry entry: " + ((Provider) IPList.get(i)).getCloudQosUrl() );
    		}
           
            
			BrokerVisualMonitor.APICall(Actions.BFE_DO_4, EndPointType.START, StatusCode.OK, "status-message");
			BrokerVisualMonitor.APICall(Actions.BFE_DO_4, EndPointType.END, StatusCode.OK, "status-message");
			
			BrokerVisualMonitor.APICall(Actions.DO_BFE_5, EndPointType.START, StatusCode.OK, "status-message");
			BrokerVisualMonitor.APICall(Actions.DO_BFE_5, EndPointType.END, StatusCode.OK, "status-message");
			
			BrokerVisualMonitor.APICall(Actions.BFE_DO_6, EndPointType.START, StatusCode.OK, "status-message");
			BrokerVisualMonitor.APICall(Actions.BFE_DO_6, EndPointType.END, StatusCode.OK, "status-message");

    		OutputStub4Demo.write("\nCalling DO for Solution \n");
            DeploymentServiceClient client = new DeploymentServiceClient(doHost, Integer.parseInt( doPort) );
            
            deploymentSolutions = client.getPlacementSolution4Broker(negotiationManifest.toString(), IPList, deploymentObjective, trecHost, trecPort);

            LOG.info("DO Solution :" + deploymentSolutions.size());
    		OutputStub4Demo.write("\nDO Solution :"+ deploymentSolutions.size());
            
			BrokerVisualMonitor.APICall(Actions.DO_BFE_15, EndPointType.START, StatusCode.OK, "status-message");
			BrokerVisualMonitor.APICall(Actions.DO_BFE_15, EndPointType.END, StatusCode.OK, "status-message");

			BrokerVisualMonitor.APICall(Actions.BFE_SD_16, EndPointType.START, StatusCode.OK, "status-message");
			BrokerVisualMonitor.APICall(Actions.BFE_SD_16, EndPointType.END, StatusCode.OK, "status-message");

        }
        catch ( Exception e)
        {
        	System.out.println("error in deployment optimizer");
        	e.printStackTrace();
        	throw new NegotiationException( "Error in deployment optimizer, no suitable placement solutions found.", e );
        }
        
        //
        // once done with placement solutions, we can now create counter negotiation offer
        //
        
        NegotiationOfferType counterOffer = (NegotiationOfferType) negotiationOffer.copy();
        
        String contextID = "" + Calendar.getInstance().getTimeInMillis();
        
        //
        // offer name is used to map the negotiation context with deployment solutions during createAgreement() action
        //
        counterOffer.setOfferId( contextID );
        counterOffer.getNegotiationOfferContext().setCounterOfferTo( negotiationOffer.getOfferId() );
        counterOffer.setName(contextID);
        
        if ( deploymentSolutions.size() == 0 )
        {
        	LOG.error( "no placement solution found by DO, service could not be admitted by broker" );
            counterOffer.getNegotiationOfferContext().setState(NegotiationOfferStateType.Factory.newInstance() );
            counterOffer.getNegotiationOfferContext().getState().addNewRejected();
        }
        else if ( deploymentSolutions.size() > 0 )
        {
//        	// 
//            // update the service price accumulated from composite services
//            //
//            try 
//            {
//            	SLAServicePriceType servicePriceType = Tools.getServicePrice( counterOffer.getTerms().getAll() );
//                servicePriceType.setAmount(new BigDecimal(20.0));
//                
//                SLAServicePriceDocument servicePriceDoc = SLAServicePriceDocument.Factory.newInstance();
//                servicePriceDoc.addNewSLAServicePrice().set(servicePriceType);           
//                
//                ServiceDescriptionTermType priceSDT = null;
//              
//                ServiceDescriptionTermType[] sdts = counterOffer.getTerms().getAll().getServiceDescriptionTermArray();
//             
//                if ( sdts != null )
//                {
//                    for ( int i = 0; i < sdts.length; i++ )
//                    {
//                        if ( sdts[i].getName().equals( "OPTIMIS_SERVICE_PRICE_SDT" ) )
//                        {
//                            priceSDT = sdts[i];
//                            break;
//                        }
//                    }
//                }
//             
//                String name = priceSDT.getName();
//                String serviceName = priceSDT.getServiceName();
//             
//                priceSDT.set( servicePriceDoc );
//                priceSDT.setName( name );
//                priceSDT.setServiceName( serviceName );
//                
//            } catch( Exception e)
//            {
//                throw new NegotiationException( "Error in updating the service price in negotiation counter offer.", e );
//            }   
            
        	LOG.info( "placement solutions are found by DO, service could be admitted by broker" );
            counterOffer.getNegotiationOfferContext().setState(NegotiationOfferStateType.Factory.newInstance() );
            counterOffer.getNegotiationOfferContext().getState().addNewAcceptable();
            
            //BrokerContext.negotiationContext.put( contextID, deploymentSolutions );
            
            LOG.info( "added placement solutions to broker negotiation context with ID: " +  contextID );
        }
     
        
        String servID = negotiationManifest.getVirtualMachineDescriptionSection().getServiceId();
		DMDataSynchClient brokerclient = new DMDataSynchClient(this.getParam("broker.dmsynchclient.host"), this.getParam("broker.dmsynchclient.port"));
	
		String state = brokerclient.deleteIfServiceExist(servID);
		OutputStub4Demo.write("SP Broker Netotiatiate : deleteIfServiceExist:" + state);
		

        
        Thread DataUploadThread = new Thread(new DataUploader(deploymentSolutions, contextID));
        DataUploadThread.start();
                      
        return new NegotiationOfferType[] { counterOffer };
    }   
    
	public String getParam(String param){
		String path=null;
	    Properties properties = new Properties();
		try {
		    InputStream is = getClass().getClassLoader().getResourceAsStream("BrokerProperties");
		    properties.load(is);
		    //properties.load(new FileInputStream("src/main/resources/BrokerClientProperties"));
		    path = properties.getProperty(param);
		    OutputStub4Demo.write("DataUploader : getParam :BrokerProperties:"+param+"=" + path);
		    
		} catch (IOException e) {
			System.out.println("File Read Exception");
		}
		return path;
	}
}
