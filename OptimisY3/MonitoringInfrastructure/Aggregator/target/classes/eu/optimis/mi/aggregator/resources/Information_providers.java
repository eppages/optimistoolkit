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

import eu.optimis.mi.aggregator.bean.InformationCollectors;
import eu.optimis.mi.aggregator.queries.InfoCollectorQuery;
import eu.optimis.mi.dbutil.AggregatorDBUtil;

/**
 * OPTIMIS basic Toolkit - Monitoring Infrastructure - Aggregator Aggregator
 * provides the functionalities to gather all monitoring data from the different
 * monitoring collectors and store them into the Monitoring DB using the correct
 * identifiers
 * 
 * @author Gregory Katsaros (katsaros@hlrs.de)
 * 
 */

@Path("/informationproviders")
public class Information_providers {

	private static String location = "remote";

	@GET
	@Produces(MediaType.APPLICATION_XML)
	public InformationCollectors getProviders() {
		java.sql.Connection conn = AggregatorDBUtil.getConnection(location);
		return InfoCollectorQuery.getFullInfoCollectors(conn);
	}

	@Path("{id}")
	@GET
	@Produces(MediaType.APPLICATION_XML)
	public InformationCollectors getProvider(@PathParam("id") String id) {
		java.sql.Connection conn = AggregatorDBUtil.getConnection(location);
		return InfoCollectorQuery.getOneInfoCollector(conn, id);
	}

	@POST
	@Consumes(MediaType.APPLICATION_XML)
	@Produces(MediaType.TEXT_PLAIN)
	public String createResource(InformationCollectors ics) {
		// ResponseBuilder
		java.sql.Connection conn = AggregatorDBUtil.getConnection(location);
		boolean bo = InfoCollectorQuery.insertInfoCollectors(conn, ics);
		if (bo)
			return "New Information Provider resources have been created successfully";
		else
			return " some errors by adding.";
	}

	@Path("{id}")
	@DELETE
	@Produces(MediaType.TEXT_PLAIN)
	public String deleteProvider(@PathParam("id") String id) {
		java.sql.Connection conn = AggregatorDBUtil.getConnection(location);
		boolean bo = InfoCollectorQuery.delOneInfoCollector(conn, id);
		if (bo)
			return "New Information Provider resources have been created successfully";
		else
			return " some errors by adding.";
	}

}
