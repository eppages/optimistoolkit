/**

Copyright 2012 University of Stuttgart

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at 

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

**/

package eu.optimis.mi.aggregator.resources;


import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;


//import javax.ws.rs.FormParam;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.bind.JAXBElement;

/**
 * OPTIMIS basic Toolkit - Monitoring Infrastructure - Aggregator
 * Aggregator provides the functionalities to gather all monitoring data
 * from the different monitoring collectors and store them into the Monitoring DB using the correct identifiers
 * 
 * @author Gregory Katsaros (katsaros@hlrs.de)
 * 
 */


@Path("stopmonitoring")
public class stop {
	
	
	
   
	@GET
	@Produces(MediaType.TEXT_PLAIN)
    public String stopall() {
        return "Stop all monitoring activity";
    }
	
    
    @Path("{id}")
	@GET
	@Produces(MediaType.TEXT_PLAIN)
    public String stop(@PathParam("id") int id) {
        return "Stop monitoring for Information Provider: "+id;
    } 
    
    

}
