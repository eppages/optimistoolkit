/**
 * Copyright (C) 2010-2013 Barcelona Supercomputing Center
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version. This library is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser
 * General Public License for more details. You should have received a copy of
 * the GNU Lesser General Public License along with this library; if not, write
 * to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA
 * 02111-1307 USA
 */
package eu.optimis.ecoefficiencytool.rest.ip;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import com.sun.jersey.spi.resource.Singleton;
import eu.optimis.ecoefficiencytool.core.EcoEffAssessorIP;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import net.emotivecloud.commons.HashMapStrings;
import net.emotivecloud.commons.ListStrings;
import net.emotivecloud.utils.ovf.OVFWrapper;

@Path("/")
@Singleton
/**
 * RESTFul Webservice interface of the EcoEffAssessorIP class. Refer to the parent
 * class for documentation of each method.
 */
public class EcoEfficiencyToolRESTIP extends EcoEffAssessorIP {

    HashMap<String,Integer> futureServiceLayout;
  
    /**************************************************************************/
    /*                                                                        */
    /*                             Service                                    */
    /*                                                                        */
    /**************************************************************************/

    @POST
    @Path("/service/{serviceid}")
    public void startServiceAssessmentREST(@PathParam("serviceid") String serviceId, @QueryParam("timeout") String timeout) {
        Long tout = null;
        if (timeout != null) {
            tout = Long.valueOf(timeout);
        }

        proactiveManager.startServiceAssessment(serviceId, tout);
    }

    @DELETE
    @Path("/service/{serviceid}")
    public void stopServiceAssessmentREST(@PathParam("serviceid") String serviceId) {
        proactiveManager.stopServiceAssessment(serviceId);
    }

    @GET
    @Path("/service/{serviceid}/assessecoefficiency")
    @Produces(MediaType.TEXT_PLAIN)
    public String assessServiceEcoEfficiencyREST(@PathParam("serviceid") String serviceId, @QueryParam("type") String type) {
        if(type == null) {
            log.warn("ECO: Warning: eco-efficiency type wasn't specified. Using default: energy efficiency.");
            type = "energy";
        }
        return super.assessServiceEcoEfficiency(serviceId, type);
    }
    
    @GET
    @Path("/service/{serviceid}/getPerfPowCO2")
    @Produces(MediaType.APPLICATION_XML)
    public ListStrings getServicePerformancePowerAndCO2REST(@PathParam("serviceid") String serviceId) {

        double[] results = super.getServicePerformancePowerAndCO2(serviceId);
        
        ListStrings ret = new ListStrings();
        ret.add(Double.toString(results[0]));
        ret.add(Double.toString(results[1]));
        ret.add(Double.toString(results[2]));
        return ret;
    }

    @POST
    @Path("/service/forecastecoefficiency")
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.TEXT_PLAIN)
    public String forecastServiceEcoEfficiencyREST(@QueryParam("timeSpan") String timeSpan, @QueryParam("type") String type , String manifest) {

        Long tspan = null;
        if (timeSpan != null) {
            tspan = Long.valueOf(timeSpan);
        }
        if(type == null) {
            log.warn("ECO: Warning: eco-efficiency type wasn't specified. Using default: energy efficiency.");
            type = "energy";
        }
        return ecoForecaster.forecastServiceEcoEfficiency(manifest,type,tspan);
    }
    
    @POST
    @Path("/service/forecastenecoeff")
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.APPLICATION_XML)
    public ListStrings forecastServiceEnEcoEffREST(@QueryParam("timeSpan") String timeSpan, String manifest) {

        Long tspan = null;
        if (timeSpan != null) {
            tspan = Long.valueOf(timeSpan);
        }
        double[] results = ecoForecaster.forecastServiceEcoEfficiency(null, tspan, manifest);
        
        ListStrings ret = new ListStrings();
        ret.add(Double.toString(results[0]));
        ret.add(Double.toString(results[1]));
        return ret;
    }
    

    /**************************************************************************/
    /*                                                                        */
    /*                                VM                                      */
    /*                                                                        */
    /**************************************************************************/
    
    @GET
    @Path("/vm/{vmid}/assessecoefficiency")
    @Produces(MediaType.TEXT_PLAIN)
    public String assessVMEcoEfficiencyREST(@PathParam("vmid") String vmId, @QueryParam("type") String type) {
        if(type == null) {
            log.warn("ECO: Warning: eco-efficiency type wasn't specified. Using default: energy efficiency.");
            type = "energy";
        }
        return super.assessVMEcoEfficiency(vmId, type);
    }
    
    @GET
    @Path("/vm/{vmid}/forecastecoefficiency")
    @Produces(MediaType.TEXT_PLAIN)
    public String forecastVMEcoEfficiencyREST(@PathParam("vmid") String vmId, @QueryParam("type") String type, @QueryParam("timeSpan") String timeSpan) {
        Long tspan = null;
        if (timeSpan != null) {
            tspan = Long.valueOf(timeSpan);
        }
        if(type == null) {
            log.warn("ECO: Warning: eco-efficiency type wasn't specified. Using default: energy efficiency.");
            type = "energy";
        }
        return ecoForecaster.forecastVMEcoEfficiency(vmId, type, tspan);
    }

    /**************************************************************************/
    /*                                                                        */
    /*                         Infrastructure                                 */
    /*                                                                        */
    /**************************************************************************/

    @POST
    @Path("/infrastructure")
    public void startInfrastructureAssessmentREST(@QueryParam("timeout") String timeout) {
        Long tout = null;
        if (timeout != null) {
            tout = Long.valueOf(timeout);
        }

        proactiveManager.startInfrastructureAssessment(tout);
    }

    @DELETE
    @Path("/infrastructure")
    public void stopInfrastructureAssessmentREST() {
        proactiveManager.stopInfrastructureAssessment();
    }

    @GET
    @Path("/infrastructure/assessecoefficiency")
    @Produces(MediaType.TEXT_PLAIN)
    public String assessIPEcoEfficiencyREST(@QueryParam("type") String type) {
        if(type == null) {
            log.warn("ECO: Warning: eco-efficiency type wasn't specified. Using default: energy efficiency.");
            type = "energy";
        }
        return super.assessIPEcoEfficiency(type, false);
    }

    /* OLD, REMOVED @POST
    @Path("/infrastructure/forecastecoefficiency")
    @Produces(MediaType.TEXT_PLAIN)
    public String forecastIPEcoEfficiencyREST(@QueryParam("type") String type, @QueryParam("timeSpan") String timeSpan, ListStrings ovfs) {
        Long tspan = null;
        if (timeSpan != null) {
            tspan = Long.valueOf(timeSpan);
        }
        if(type == null) {
            log.info("ECO: Warning: eco-efficiency type wasn't specified.");
            type = "energy";
        }
        return ecoForecaster.forecastIPEcoEfficiency(ovfs, type, tspan);
    }*/
    
    @GET
    @Path("/infrastructure/forecastecoefficiency")
    @Produces(MediaType.TEXT_PLAIN)
    public String forecastIPEcoEfficiencyREST(@QueryParam("timeSpan") String timeSpan, @QueryParam("type") String type) {
        Long tspan = null;
        if (timeSpan != null) {
            tspan = Long.valueOf(timeSpan);
        }
        if(type == null) {
            log.warn("ECO: Warning: eco-efficiency type wasn't specified. Using default: energy efficiency.");
            type = "energy";
        }
        return ecoForecaster.forecastIPEcoEfficiency(type, tspan);
    }
    
    @POST
    @Path("/infrastructure/forecastecoefficiencyPlusService")
    @Produces(MediaType.TEXT_PLAIN)
    public String forecastIPEcoEfficiencyServiceDeploymentREST(@QueryParam("type") String type, @QueryParam("timeSpan") String timeSpan, String manifest) {
        Long tspan = null;
        if (timeSpan != null) {
            tspan = Long.valueOf(timeSpan);
        }
        if(type == null) {
            log.warn("ECO: Warning: eco-efficiency type wasn't specified. Using default: energy efficiency.");
            type = "energy";
        }
        return ecoForecaster.forecastIPEcoEfficiencyServiceDeployment(manifest, type, tspan);
    }
    
    @POST
    @Path("/infrastructure/forecastecoefficiency")
    @Produces(MediaType.TEXT_PLAIN)
    public String forecastIPEcoEfficiencyREST(@QueryParam("timeSpan") String timeSpan, ListStrings ovfs) {
        Long tspan = null;
        if (timeSpan != null) {
            tspan = Long.valueOf(timeSpan);
        }
        log.warn("Function forecastIPEcoEfficiencyREST(List<String> ovfs, Long timeSpan) is DEPRECATED. Calling forecastIPEcoEfficiency(String type, Long tspan) instead.");
        return ecoForecaster.forecastIPEcoEfficiency("energy", tspan);
    }
    
    @GET
    @Path("/infrastructure/forecastecoefficiencyVMCancellation")
    @Produces(MediaType.TEXT_PLAIN)
    public String forecastIPEcoEfficiencyVMCancellationREST(@QueryParam("vmId") String vmId, @QueryParam("type") String type, @QueryParam("timeSpan") String timeSpan) {
        Long tspan = null;
        if (timeSpan != null) {
            tspan = Long.valueOf(timeSpan);
        }
        if(type == null) {
            log.warn("ECO: Warning: eco-efficiency type wasn't specified. Using default: energy efficiency.");
            type = "energy";
        }
        return ecoForecaster.forecastIPEcoEfficiencyVMCancellation(vmId, type, tspan);
    }
    
    @POST
    @Path("/infrastructure/forecastecoefficiencyVMDeploymentKnownPlacement")
    @Produces(MediaType.TEXT_PLAIN)
    public String forecastIPEcoEfficiencyVMDeploymentKnownPlacementREST(ListStrings ovfPlusActiveNodes, @QueryParam("destNode") String destNode, @QueryParam("type") String type, @QueryParam("timeSpan") String timeSpan) {
        if (destNode == null) {
            log.error("destNode can not be null.");
            return "-1.0";
        }
        Long tspan = null;
        if (timeSpan != null) {
            tspan = Long.valueOf(timeSpan);
        }
        if(type == null) {
            log.warn("ECO: Warning: eco-efficiency type wasn't specified. Using default: energy efficiency.");
            type = "energy";
        }

        OVFWrapper ovfDom = null;
        List<String> activeNodes = null;
        try {
            String ovfDescriptor = ovfPlusActiveNodes.remove(0);
            ovfDom = new OVFWrapper(ovfDescriptor);
            if(ovfPlusActiveNodes.size() > 0) {
                activeNodes = ovfPlusActiveNodes;
            }
        } catch (Exception ex) {
            log.error("Error in parsing received OVF or activeNodes list.");
            log.error(ex.getMessage());
            return "-1.0";
        }
        return ecoForecaster.forecastIPEcoEfficiencyVMDeploymentKnownPlacement(ovfDom, destNode, activeNodes, type, tspan);
    }
    
    @POST
    @Path("/infrastructure/forecastecoefficiencyVMMigrationKnownPlacement")
    @Produces(MediaType.TEXT_PLAIN)
    public String forecastIPEcoEfficiencyVMMigrationKnownPlacementREST(ListStrings activeNodes, @QueryParam("vmId") String vmId, @QueryParam("destNode") String destNode, @QueryParam("type") String type, @QueryParam("timeSpan") String timeSpan) {
        if (vmId == null) {
            log.error("vmId can not be null.");
            return "-1.0";
        }
        if (destNode == null) {
            log.error("destNode can not be null.");
            return "-1.0";
        }
        Long tspan = null;
        if (timeSpan != null) {
            tspan = Long.valueOf(timeSpan);
        }
        if(type == null) {
            log.warn("ECO: Warning: eco-efficiency type wasn't specified. Using default: energy efficiency.");
            type = "energy";
        }

        return ecoForecaster.forecastIPEcoEfficiencyVMMigrationKnownPlacement(vmId, destNode, activeNodes, type, tspan);
    }
    
    @POST
    @Path("/infrastructure/forecastecoefficiencyVMDeploymentUnknownPlacement")
    @Produces(MediaType.TEXT_PLAIN)
    public String forecastIPEcoEfficiencyVMDeploymentUnknownPlacementREST(String ovfDescriptor, @QueryParam("type") String type, @QueryParam("timeSpan") String timeSpan) {
        if(ovfDescriptor == null) {
            log.error("ovfDescriptor can not be null.");
            return "-1.0";
        }
        Long tspan = null;
        if (timeSpan != null) {
            tspan = Long.valueOf(timeSpan);
        }
        if(type == null) {
            log.warn("ECO: Warning: eco-efficiency type wasn't specified. Using default: energy efficiency.");
            type = "energy";
        }

        OVFWrapper ovfDom = null;
        try {
            ovfDom = new OVFWrapper(ovfDescriptor);
        } catch (Exception ex) {
            log.error("Error in parsing received OVF.");
            log.error(ex.getMessage());
            return "-1.0";
        }
        return ecoForecaster.forecastIPEcoEfficiencyVMDeploymentUnknownPlacement(ovfDom, type, tspan);
    }
    
    @GET
    @Path("/infrastructure/forecastecoefficiencyVMMigrationUnknownPlacement")
    @Produces(MediaType.TEXT_PLAIN)
    public String forecastIPEcoEfficiencyVMMigrationUnknownPlacementREST(@QueryParam("vmId") String vmId, @QueryParam("type") String type, @QueryParam("timeSpan") String timeSpan) {
        if (vmId == null) {
            log.error("vmId can not be null.");
            return "-1.0";
        }
        Long tspan = null;
        if (timeSpan != null) {
            tspan = Long.valueOf(timeSpan);
        }
        if(type == null) {
            log.warn("ECO: Warning: eco-efficiency type wasn't specified. Using default: energy efficiency.");
            type = "energy";
        }

        return ecoForecaster.forecastIPEcoEfficiencyVMMigrationUnknownPlacement(vmId, type, tspan);
    }

    @GET
    @Path("/infrastructure/{nodeid}/assessecoefficiency")
    @Produces(MediaType.TEXT_PLAIN)
    public String assessNodeEcoEfficiencyREST(@PathParam("nodeid") String nodeId, @QueryParam("type") String type) {
        if(type == null) {
            log.warn("ECO: Warning: eco-efficiency type wasn't specified. Using default: energy efficiency.");
            type = "energy";
        }
        return super.assessNodeEcoEfficiency(nodeId, type, false);
    }

    @POST
    @Path("/infrastructure/multiple/assessecoefficiency")
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.APPLICATION_XML)
    public ListStrings assessMultipleNodeEcoEfficiencyREST(ListStrings nodeList, @QueryParam("type") String type) {
        ListStrings ret = new ListStrings();
        if(type == null) {
            log.warn("ECO: Warning: eco-efficiency type wasn't specified. Using default: energy efficiency.");
            type = "energy";
        }
        
        List<String> ecoAssessments = super.assessMultipleNodesEfficiency(nodeList, type, false);
        for (String ecoAssess : ecoAssessments) {
            ret.add(ecoAssess);
        }

        return ret;
    }

    @POST
    @Path("/infrastructure/{nodeid}/forecastecoefficiency")
    @Produces(MediaType.TEXT_PLAIN)
    public String forecastNodeEcoEfficiencyREST(@PathParam("nodeid") String nodeId, @QueryParam("type") String type, @QueryParam("timeSpan") String timeSpan, ListStrings ovfs) {
        if(type == null) {
            log.warn("ECO: Warning: eco-efficiency type wasn't specified. Using default: energy efficiency.");
            type = "energy";
        }
        
        Long tspan = null;
        if (timeSpan != null) {
            tspan = Long.valueOf(timeSpan);
        }
        return ecoForecaster.forecastNodeEcoEfficiency(nodeId,ovfs,type,tspan);
    }

    @POST
    @Path("/infrastructure/multiple/forecastecoefficiency")
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.APPLICATION_XML)
    public ListStrings forecastMultipleNodeEcoEfficiencyREST(ListStrings nodeList, @QueryParam("type") String type) {
        ListStrings ret = new ListStrings();
        if(type == null) {
            log.warn("ECO: Warning: eco-efficiency type wasn't specified. Using default: energy efficiency.");
            type = "energy";
        }
        
        List<String> ecoForecasts = ecoForecaster.forecastMultipleNodesEfficiency(nodeList, type);
        for (String ecoForecast : ecoForecasts) {
            ret.add(ecoForecast);
        }

        return ret;
    }
   
    /***************PROACTIVE THRESHOLD-SETTING METHODS************************/
    
    @POST
    @Path("/proactive/infrastructure/setth")
    public void setInfrastructureEcoThreshold(@QueryParam("enEffTH") String enEffTH, @QueryParam("ecoEffTH") String ecoEffTH) {
        
        Double energyEfficiencyTH = null;
        if(enEffTH != null) {
            energyEfficiencyTH = Double.valueOf(enEffTH);
        }
        
        Double ecologicalEfficiencyTH = null;
        if(ecoEffTH != null) {
            ecologicalEfficiencyTH = Double.valueOf(ecoEffTH);
        }
        
        proactiveManager.setInfrastructureEcoThreshold(energyEfficiencyTH, ecologicalEfficiencyTH);
    }
    
    @POST
    @Path("/proactive/node/{nodeid}/setth")
    public void setNodeEcoThreshold(@PathParam("nodeid") String nodeId, @QueryParam("enEffTH") String enEffTH, @QueryParam("ecoEffTH") String ecoEffTH) {
        
        if(nodeId != null) {
            Double energyEfficiencyTH = null;
            if(enEffTH != null) {
                energyEfficiencyTH = Double.valueOf(enEffTH);
            }

            Double ecologicalEfficiencyTH = null;
            if(ecoEffTH != null) {
                ecologicalEfficiencyTH = Double.valueOf(ecoEffTH);
            }

            proactiveManager.setNodeEcoThreshold(nodeId, energyEfficiencyTH, ecologicalEfficiencyTH);
        }
    }
    
    @POST
    @Path("/proactive/service/{serviceid}/setth")
    public void setServiceEcoThreshold(@PathParam("serviceid") String serviceId, @QueryParam("enEffTH") String enEffTH, @QueryParam("ecoEffTH") String ecoEffTH) {
        
        if(serviceId != null) {
            Double energyEfficiencyTH = null;
            if(enEffTH != null) {
                energyEfficiencyTH = Double.valueOf(enEffTH);
            }

            Double ecologicalEfficiencyTH = null;
            if(ecoEffTH != null) {
                ecologicalEfficiencyTH = Double.valueOf(ecoEffTH);
            }

            proactiveManager.setServiceEcoThreshold(serviceId, energyEfficiencyTH, ecologicalEfficiencyTH);
        }
    }
    
    @POST
    @Path("/proactive/vm/{vmid}/setth")
    public void setVMEcoThreshold(@PathParam("vmid") String vmId, @QueryParam("enEffTH") String enEffTH, @QueryParam("ecoEffTH") String ecoEffTH) {
        
        if(vmId != null) {
            Double energyEfficiencyTH = null;
            if(enEffTH != null) {
                energyEfficiencyTH = Double.valueOf(enEffTH);
            }

            Double ecologicalEfficiencyTH = null;
            if(ecoEffTH != null) {
                ecologicalEfficiencyTH = Double.valueOf(ecoEffTH);
            }

            proactiveManager.setVMEcoThreshold(vmId, energyEfficiencyTH, ecologicalEfficiencyTH);
        }
    }
    
    /*********************METRIC OBTENTION METHODS******************************/
    
    @GET
    @Path("/infrastructure/{nodeid}/maxeco")
    @Produces(MediaType.TEXT_PLAIN)
    public String getNodeMaxEco(@PathParam("nodeid") String nodeId, @QueryParam("type") String type) {
        if(type == null) {
            log.warn("ECO: Warning: eco-efficiency type wasn't specified. Using default: energy efficiency.");
            type = "energy";
        }
        return super.getNodeMaxEco(nodeId, type);
    }
    
    @GET
    @Path("/infrastructure/metrics/cpumeanperformance")
    @Produces(MediaType.TEXT_PLAIN)
    public String getCPUMeanPerformance() {
        return Double.toString(metrics.getCPUMeanPerformance(null));
    }
    
    @GET
    @Path("/infrastructure/metrics/{nodeid}/cpunumber")
    @Produces(MediaType.TEXT_PLAIN)
    public String getCPUNumber(@PathParam("nodeid") String nodeId) {
        return Double.toString(metrics.getCPUNumber(nodeId));
    }
    
    @GET
    @Path("/infrastructure/metrics/{nodeid}/cpuperformance")
    @Produces(MediaType.TEXT_PLAIN)
    public String getCPUPerformance(@PathParam("nodeid") String nodeId) {
        return Double.toString(metrics.getCPUPerformance(nodeId));
    }
    
    @GET
    @Path("/infrastructure/metrics/{nodeid}/performance")
    @Produces(MediaType.TEXT_PLAIN)
    public String getMaxPerformance(@PathParam("nodeid") String nodeId) {
        return Double.toString(metrics.getNodeBenchmarkResult(nodeId));
    }
    
    @GET
    @Path("/service/alldeploymentmessages")
    @Produces(MediaType.TEXT_PLAIN)
    public String getAllDeploymentMessages() {
        return ecoForecaster.getAllDeploymentMessages();
    }
    
    @GET
    @Path("/infrastructure/metrics/{nodeid}/usedcpus")
    @Produces(MediaType.TEXT_PLAIN)
    public String getNodeUsedCPUs(@PathParam("nodeid") String nodeId) {
        return Double.toString(metrics.getNumberOfUsedCPUs(nodeId));
    }
    
    @GET
    @Path("/infrastructure/metrics/{nodeid}/pidle")
    @Produces(MediaType.TEXT_PLAIN)
    public String getNodePidle(@PathParam("nodeid") String nodeId) {
        return Double.toString(energyEstimator.getPidle(nodeId));
    }
    
    @GET
    @Path("/infrastructure/metrics/{nodeid}/pmax")
    @Produces(MediaType.TEXT_PLAIN)
    public String getNodePMax(@PathParam("nodeid") String nodeId) {
        return Double.toString(energyEstimator.getPMax(nodeId));
    }
    
    @GET
    @Path("/infrastructure/metrics/{nodeid}/pincr")
    @Produces(MediaType.TEXT_PLAIN)
    public String getNodePIncr(@PathParam("nodeid") String nodeId) {
        return Double.toString(energyEstimator.getIncrementalPowerPerCPU(nodeId));
    }
}
