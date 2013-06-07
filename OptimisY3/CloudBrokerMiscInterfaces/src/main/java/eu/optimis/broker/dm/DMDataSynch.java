package eu.optimis.broker.dm;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.Consumes;




//Sets the path to base URL + /dmsynch
@Path( "/dmsynch" )
public class DMDataSynch {
	
	private static HashMap serviceList = new HashMap();
	
	//Check if the Service exist and if exists Delete
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	@Path("/deleteIfServiceExist/{serviceid}")
	public String deleteIfServiceExist(@PathParam("serviceid") String serviceid) throws Exception {
	
		System.out.println("DMDATASynch: deleteIfServiceExist received serviceid: " + serviceid);
		
		// Get a set of the entries 
		Set set = this.serviceList.entrySet(); 
		// Get an iterator 
		Iterator i = set.iterator(); 
		
		System.out.println("DMDataSynch: serviceList :" + this.serviceList.size());
					
		// Search for the service for which Data percentage is requested
		while(i.hasNext()) { 
		    Map.Entry me = (Map.Entry)i.next();
		    System.out.println("Key : " + me.getKey() + "   value :" + me.getValue());
		    if(me.getKey().equals(serviceid)){
		    	System.out.print(me.getKey() + ": "); 
				System.out.println(me.getValue());
				System.out.println("DMDATASynch: deleteIfServiceExist:  remoove serviceid: " + me.getValue());
				this.serviceList.remove(me.getKey());
				return "-1";
		    }
		} 

		

		
		return "-1";
	}


	
	
	//SD updates that the data upload is completed
	@POST
	@Consumes(MediaType.TEXT_PLAIN)
	@Path("/dataUploadComplete")
	public Response dataUploadComplete(String serviceid)throws Exception {
		System.out.println("DMDataSynch : SD has completed the data upload to the broker"+ serviceid);
		
		this.serviceList.put(serviceid, new Double(0.0));
		
		Response res = Response.ok().build();
		return res;
		
	}

	
	//Check if the DM on the broker has completed the data upload to the IP
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	@Path("/isReady2CreateAgreeement/{serviceid}")
	public String isReady2CreateAgreeement(@PathParam("serviceid") String serviceid) throws Exception {

		System.out.println("DMDATASynch: isReady2CreateAgreement received serviceid: " + serviceid);
		
		// Get a set of the entries 
		Set set = this.serviceList.entrySet(); 
		// Get an iterator 
		Iterator i = set.iterator(); 
		
		System.out.println("DMDataSynch: serviceList :" + this.serviceList.size());
					
		// Search for the service for which Data percentage is requested
		while(i.hasNext()) { 
		    Map.Entry me = (Map.Entry)i.next();
		    System.out.println("Key : " + me.getKey() + "   value :" + me.getValue());
		    if(me.getKey().equals(serviceid)){
		    	System.out.print(me.getKey() + ": "); 
				System.out.println(me.getValue()); 
		    	return me.getValue().toString();
		    }
		} 
				
		return "-1";
	}
	

	//broker updates SD that the data upload to the IP is completed and 
	//the SD can create agreement
	@POST
	@Consumes(MediaType.TEXT_PLAIN)
	@Path("/Ready2CreateAgreeement/{serviceid}/{datapercent}")
	public Response  Ready2CreateAgreeement(@PathParam("serviceid") String serviceid, @PathParam("datapercent") String datapercent) throws Exception {
		System.out.println("DMDataSumch : Ready2CreateAgreeement : SD has completed the data upload to the broker"+ serviceid + " " + datapercent);
		// Get a set of the entries 
		Set set = serviceList.entrySet(); 
		// Get an iterator 
		Iterator i = set.iterator(); 
		
		// Search for the service for which Data percentage is requested
		
		while(i.hasNext()) { 
		    Map.Entry me = (Map.Entry)i.next(); 
		    if(me.getKey().equals(serviceid)){
		    	System.out.print(me.getKey() + ": "); 
				System.out.println(me.getValue()); 
				me.setValue(new Double(datapercent));
		    	return Response.ok().build();
		    }
		} 

		Response res = Response.ok().build();
		return res;
	}

	
	
	
	
}
