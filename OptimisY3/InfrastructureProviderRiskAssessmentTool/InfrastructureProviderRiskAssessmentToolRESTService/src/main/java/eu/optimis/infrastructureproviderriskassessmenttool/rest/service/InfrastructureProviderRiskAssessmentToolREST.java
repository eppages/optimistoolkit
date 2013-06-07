/*
 *  Copyright 2013 University of Leeds
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package eu.optimis.infrastructureproviderriskassessmenttool.rest.service;

import com.sun.jersey.spi.resource.Singleton;
import eu.optimis.infrastructureproviderriskassessmenttool.core.InfrastructureProviderRiskAssessmentServer;
import eu.optimis.infrastructureproviderriskassessmenttool.core.ReturnSPPoF;
import eu.optimis.infrastructureproviderriskassessmenttool.core.holisticriskassessment.HolisticRiskAssessment;
import eu.optimis.infrastructureproviderriskassessmenttool.core.riskassessor.*;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
//import javax.servlet.ServletException;
//import javax.servlet.ServletException;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriInfo;

/**
 *
 * @author scsmj
 */
@Path("/")
@Singleton
/**
 * RESTFul Webservice interface of the
 * InfrastructureProviderRiskAssessmentServer and HolisticRiskAssessment class.
 */
public class InfrastructureProviderRiskAssessmentToolREST {

      protected static org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(InfrastructureProviderRiskAssessmentServer.class);

    private InfrastructureProviderRiskAssessmentServer iprs = InfrastructureProviderRiskAssessmentServer.getInfrastructureProviderRiskAssessmentServerRiskAssessmentServer();
    private HolisticRiskAssessment holisticRiskAssessment = new HolisticRiskAssessment();
    private runRisk rr = null;
    @POST
    @Path("/infrastructure/startproactiveriskassessor")
    public void startProactiveRiskAssessorREST(RiskLevelThresholdsObject rlto) {
        String infrastructureID = rlto.getInfrastructureID();
        String infrastructureRiskLevelThreshold = rlto.getInfrastructureRiskLevelThreshold();
        HashMap<String, String> physicalHostsRiskLevelThresholds = rlto.getPhysicalHostsRiskLevelThresholds();
        HashMap<String, String> servicesRiskLevelThresholds = rlto.getServicesRiskLevelThresholds();
        HashMap<String, HashMap<String, String>> VMsRiskLevelThresholds = rlto.getVMsRiskLevelThresholds();
        holisticRiskAssessment.startProactiveRiskAssessor(infrastructureID, infrastructureRiskLevelThreshold, physicalHostsRiskLevelThresholds, servicesRiskLevelThresholds, VMsRiskLevelThresholds);
        rlto = null;
    }

    @DELETE
    @Path("/infrastructure")
    public void stopProactiveRiskAssessorREST() {
        holisticRiskAssessment.stopProactiveRiskAssessor();
    }

    @POST
    @Path("/infrastructure/prenegotiatespdeploymentphase")
    @Produces(MediaType.APPLICATION_XML)
    public SPPoFs preNegotiateSPDeploymentPhase(@QueryParam("SPName") String SPName, String serviceManifest) {

        SPPoFs temp1 = new SPPoFs();

        Map<String, String> riskMap = new HashMap<String, String>();
        riskMap.put(SPName, serviceManifest);
        ReturnSPPoF temp2 = iprs.preNegotiateSPDeploymentPhase(riskMap);

        temp1.setPoFSLA(temp2.getPoFSLA());
        temp1.setSPNames(temp2.getSPNames());
        return temp1;

    }

    @GET
    @Path("/infrastructure/calculatephyhostpof")
    @Produces(MediaType.TEXT_PLAIN)
    public String calculatePhyHostPoF(@QueryParam("physicalHostName") String phyiscalHostName, @QueryParam("timePeriod") String timePeriod) {

        return iprs.calculatePhyHostPoF(phyiscalHostName, Long.valueOf(timePeriod)) + "";
    }

    @POST
    @Path("/infrastructure/calculatephyhostspofs")
    @Consumes("application/x-www-form-urlencoded")
    @Produces(MediaType.APPLICATION_XML)
    public ListStrings calculatePhyHostsPoFs(@Context UriInfo ui) {
        MultivaluedMap<String, String> physicalHostNames = ui.getQueryParameters();
        HashMap<String, Long>  names = new HashMap<String, Long>();
        Iterator<String> it = physicalHostNames.keySet().iterator();
        while (it.hasNext()) {
            String key = (String) it.next();
            String time = physicalHostNames.get(key).get(0);
            names.put(key, Long.valueOf(time));
        }
       
        ListStrings temps1 = new ListStrings();

        List<Double> temps2 = iprs.calculatePhyHostPoFs(names);

        for (Double temp : temps2) {
            temps1.add(temp + "");
        }

        return temps1;
    }

    @GET
    @Path("/infrastructure/calculaterisklevelofphyhostfailure")
    @Produces(MediaType.TEXT_PLAIN)
    public String calculateRiskLevelOfPhyHostFailure(@QueryParam("physicalHostName") String physicalHostName, @QueryParam("timePeriod") String timePeriod) {
        return iprs.calculateRiskLevelOfPhyHostFailure(physicalHostName, Long.valueOf(timePeriod)) + "";
    }

    @POST
    @Path("/infrastructure/calculaterisklevelsofphyhostfailures")
    @Consumes("application/x-www-form-urlencoded")
    @Produces(MediaType.APPLICATION_XML)
    public ListStrings calculateRiskLevelsOfPhyHostFailures(@Context UriInfo ui) {

        MultivaluedMap<String, String> physicalHostNames = ui.getQueryParameters();
        HashMap<String, Long>  names = new HashMap<String, Long>();
        Iterator<String> it = physicalHostNames.keySet().iterator();
        while (it.hasNext()) {
            String key = (String) it.next();
            String time = physicalHostNames.get(key).get(0);
            names.put(key, Long.valueOf(time));
        }

        ListStrings temps1 = new ListStrings();

        List<Integer> temps2 = iprs.calculateRiskLevelsOfPhyHostFailures(names);

        for (Integer temp : temps2) {
            temps1.add(temp + "");
        }
        return temps1;
    }

    @GET
    @Path("/infrastructure/forecastVMDeploymentUnknown")
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.TEXT_PLAIN)
    public String forecastVMDeploymentUnknown(@QueryParam("replicationFactor") String replicationFactor, @QueryParam("vmID") String vmID, @QueryParam("destNode") String destNode) {
 double[] result = new double[5];
        try{
      result = forcastVMDepUnknown.calc(replicationFactor, vmID);
      } catch(Exception e){
          
      }
      String resStr = (result[0] + "," + result[1] + "," +result[2] + "," + result[3] + "," + result[4]);
        return resStr;
     }
    
//    @GET
//    @Path("/infrastructure/getIPCapacityRisk")
//    @Consumes(MediaType.TEXT_PLAIN)
//    @Produces(MediaType.TEXT_PLAIN)
//    public Double getIPCapacityRisk(@QueryParam("infrastructureID") String infrastructureID) {
//    double[] risk = new double[5];
//        try{
//      risk = getIPCapacityRisk.getRisks(infrastructureID);
//      } catch(Exception e){
//          
//      }
//        return risk[3];
//     }
   
    
    /* 
    @GET
    @Path("/infrastructure/forecastVMDeploymentUnknown")
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.TEXT_PLAIN)
    public String forecastVMDeploymentUnknown(@QueryParam("replicationFactor") String replicationFactor, @QueryParam("vmID") String vmID, @QueryParam("destNode") String destNode) {
 double[] result = new double[5];
        try{
      result = forcastVMDepUnknown.calc(replicationFactor, vmID);
      } catch(Exception e){
          
      }
      String resStr = (result[0] + "," + result[1] + "," +result[2] + "," + result[3] + "," + result[4]);
        return resStr;
     }
*/

    
    @GET
    @Path("/infrastructure/runRiskStart")
   // @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.TEXT_PLAIN)
    public void runRiskStart(@QueryParam("serviceID") String serviceID) {
        try{
            if (rr == null){
            rr = new runRisk();
            rr.start();
            }
            
        } catch(Exception e){
          log.info(" run risk failed ");
      }

    }
    @GET
    @Path("/infrastructure/runRiskStop")
   // @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.TEXT_PLAIN)
    public void runRiskStop(@QueryParam("serviceID") String serviceID) {
        try{
            if(rr !=null)
                rr = null;
            
        } catch(Exception e){
          log.info(" stop risk failed ");
      }

    }

 
    @GET
    @Path("/infrastructure/forecastVMMigrationUnknown")
  //  @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.TEXT_PLAIN)
    public String forecastVMMigrationUnknown(@QueryParam("replicationFactor") String replicationFactor, @QueryParam("vmID") String VMID, @QueryParam("destNode") String destNode) {
 double[] result = new double[5];
        try{
      result = forcastVMMigUnknown.calc(replicationFactor, VMID);
      } catch(Exception e){
          
      }
        
        String resStr = (result[0] + "," + result[1] + "," +result[2] + "," + result[3] + "," + result[4]);
        return resStr;
      }
    
    @GET
    @Path("/infrastructure/forecastVMDeploymentKnown")
 //   @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.TEXT_PLAIN)
    public String forecastVMDeploymentKnown(@QueryParam("replicationFactor") String replicationFactor, @QueryParam("vmID") String VMID, @QueryParam("destNode") String destNode) {
 double[] result = new double[5];
        try{
      result = forcastVMDepKnown.calc(replicationFactor, VMID, destNode);
      } catch(Exception e){
          
      }
        String resStr = (result[0] + "," + result[1] + "," +result[2] + "," + result[3] + "," + result[4]);
        log.info("resStr = " + resStr);
        return resStr;
      }
   
  
    
    @GET
    @Path("/infrastructure/forecastVMDeploymentCancelled")
  //  @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.TEXT_PLAIN)
    public String forecastVMDeploymentCancelled(@QueryParam("vmID") String VMID, @QueryParam("replicationFactor") String replicationFactor){
 int[] result = new int[5];
 try {
            // try{
                  result = forcastVMCan.calc(replicationFactor, VMID);
        } catch (ServletException ex) {
            Logger.getLogger(InfrastructureProviderRiskAssessmentToolREST.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(InfrastructureProviderRiskAssessmentToolREST.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(InfrastructureProviderRiskAssessmentToolREST.class.getName()).log(Level.SEVERE, null, ex);
        }
            
           
      
  //    } catch(Exception e){
      
          
    //  }
 
        String resStr = (result[0] + "," + result[1] + "," +result[2] + "," + result[3] + "," + result[4]);
        log.info("resStr = " + resStr);
        return resStr;
      }
 
    
    /*
    
    @POST
    @Path("/infrastructure/forecastVMMigrationKnown")
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.TEXT_PLAIN)
    public double[] forecastVMMigrationKnown(String replicationFactor, @QueryParam("VMID") String VMID, @QueryParam("destNode") String destNode) {
 double[] result = new double[5];
        try{
      result = forcastVMMigKnown.calc(replicationFactor, VMID, destNode);
      } catch(Exception e){
          
      }
        return result;
      }

    @POST
    @Path("/infrastructure/DMRisk")
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.TEXT_PLAIN)
    public String DMRisk(@QueryParam("applicationDetail") String applicationDetail, @QueryParam("replicationFactor") String replicationFactor) {

        String DMReturn = new DMProposal().risk();
        return DMReturn;
    }
    
  

    @POST
    @Path("/infrastructure/forecastVMMigrationUnknown")
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.TEXT_PLAIN)
    public double[] forecastVMMigrationUnknown(String replicationFactor, @QueryParam("VMID") String VMID, @QueryParam("destNode") String destNode) {
        //       String return = forecastVMDeployementUnknown.calc(applicationDetail, replicationFactor);
      //  int[] calc = forcastVMMigrationUnknown.calc(applicationDetail, replicationFactor);
  //forecastVMMigrationUnknown(applicationDetail, replicationFactor);
       double[] result = new double[5];
        try{
      result = forcastVMMigUnknown.calc(replicationFactor, VMID);
      } catch(Exception e){
          
      }
        return result;
     
     }

    @POST
    @Path("/infrastructure/forecastVMDeploymentCancelled")
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.TEXT_PLAIN)
//    public double[] forecastVMDeployementCancelled(@QueryParam("replicationFactor") String replicationFactor, @QueryParam("VMID") String VMID, @QueryParam("destNode") String destNode, @QueryParam("type") String type, @QueryParam("timeSpan") String timeSpan) {
public int [] forecastVMDeployementCancelled(@QueryParam("VMID") String VMID, String replicationFactor) {
       log.info("in vm cancelled");
        int[] result = new int[5];
        try{
      result = forcastVMCan.calc(replicationFactor, VMID);
      } catch(Exception e){
       log.info("error getting vm cancelled result");   
      }
         log.info("cancelled result got");  
        return result;
         }
   */
   
    @POST
    @Path("/infrastructure/startRisk")
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.TEXT_PLAIN)
    public String startRisk(@QueryParam("applicationDetail") String riskParam, @QueryParam("replicationFactor") String replicationFactor, @QueryParam("VMID") String vmID) {

        String[] riskID = new String[4];
        if (riskParam == null) {
            log.info("manually populating riskparams as input is null");
            riskParam = "7:7:7:7";
        }


        riskID[0] = riskParam.substring(0, 1);
        log.info("level0 = " + riskID[0]);
        riskID[1] = riskParam.substring(2, 3);
        log.info("level1 = " + riskID[1]);
        riskID[2] = riskParam.substring(4, 5);
        log.info("level2 = " + riskID[2]);
        riskID[3] = riskParam.substring(6, 7);
        log.info("level3 = " + riskID[3]);

        try {
            start.risk(riskID, vmID);
            //setThreashold(riskID, targetRiskMetrics);			
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }


        return "RiskStarted";
    }

}