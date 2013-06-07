package elasticityserver;
/**
* 
 * @author Ahmed Ali-Eldin (<a
 *         href="mailto:ahmeda@cs.umu.se">ahmeda@cs.umu.se</a>)
 *Copyright (C) 2012 Umeå University

* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.

* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU General Public License for more details.

* You should have received a copy of the GNU General Public License
* along with this program.  If not, see <http://www.gnu.org/licenses/>.

 */
import eu.optimis.elasticityengine.*;

import org.apache.log4j.Logger;
import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.sun.jersey.spi.resource.Singleton;
import com.thoughtworks.xstream.XStream;

@Singleton
@Path("/ElasticityEngine")
public class ElasticityServer {
	protected final static Logger log = Logger
			.getLogger(ElasticityServer.class);
	private final ElasticityEngineImpl elast=new ElasticityEngineImpl();
	    
//	    public ElasticityServer() {  
//	    	log.debug("Modified Elasticity REST Server Initialized..");
//	    	System.out.println(System.getProperty("user.dir"));
	    	
//	    }
	    
	    /**
	     * Interface to deploy the EE for a service
	     
	     */
	    @PUT
	    @Path("/{serviceid}")
	    public void startElasticity(@PathParam("serviceid") String serviceId, @FormParam("Manifest") String serviceManifest,
	    		@FormParam("Mode") boolean LowRiskMode, @FormParam("SPAdd") String spAddress){
	        log.debug("Starting EE for service "+ serviceId );
	        try { 
	        	XStream xstream = new XStream();
	            elast.startElasticity(serviceId, serviceManifest,LowRiskMode, spAddress);
	        } catch (Exception ex) {
	            log.error(ex.getMessage(), ex);
	            ex.printStackTrace();        
	            }
	        
	    }
	    
	    @DELETE
	    @Path("/{serviceid}")
	    public void stopElasticity(@PathParam("serviceid") String serviceID){
	    	log.debug("Stoping EE for service "+ serviceID );
	        try { 
	            elast.stopElasticity(serviceID);
	        } catch (Exception ex) {
	            log.error(ex.getMessage(), ex);
	            ex.printStackTrace();        
	            }
	    }
	    
	    @POST
	    @Path("/{serviceid}")
	    public void updateElasticityRules(@PathParam("serviceid") String serviceID, @FormParam("Manifest") String serviceManifest) {
	    	log.debug("Updating Manifest for service "+ serviceID );
	        try { 
	            elast.updateElasticityRules(serviceID, serviceManifest);
	        } catch (Exception ex) {
	            log.error(ex.getMessage(), ex);
	            ex.printStackTrace();        
	            }
	    }
	    
	    @GET
	    @Path("/{serviceid}")
	    public String getPrediction(@PathParam("serviceid") String serviceID, @FormParam("imageID") String imageID,@FormParam("time") int timeSpanInMinutes) {
	    	log.debug("Getting EE prediction for service "+ serviceID );
	    	String pred;
	        try { 
	            pred=Integer.toString(elast.getPrediction(serviceID, imageID, timeSpanInMinutes));
	        } catch (Exception ex) {
	        	pred="0";
	            log.error(ex.getMessage(), ex);
	            ex.printStackTrace();        
	            }
	        log.debug("Precition is "+ pred );
	        return pred;
	    }
	    
	    @GET
	    @Path("/Mode/{serviceid}")
	    public String setMode(@PathParam("serviceid") String serviceID, @QueryParam("Mode")boolean Proactive) {
	        log.debug("Setting Mode for "+ serviceID );
	        try { 
	            elast.setMode(serviceID, Proactive);
	        } catch (Exception ex) {
	            log.error(ex.getMessage(), ex);
	            ex.printStackTrace();        
	            }
	        return serviceID;
	    }
	    
	    @GET
	    @Path("/getHtml")
	    public String getHtml() 
	    {
	    	log.debug("getHtml is called..");
	    	System.out.println("getHtml is called..");
	    	return "Elasticity Server HTML.";
	    }
	    
	    
}
